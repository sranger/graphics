package com.stephenwranger.graphics.renderables;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.bounds.BoundingBox;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.math.PickingHit;
import com.stephenwranger.graphics.math.PickingRay;
import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.math.intersection.Ellipsoid;
import com.stephenwranger.graphics.math.intersection.IntersectionUtils;
import com.stephenwranger.graphics.utils.BiConsumerSupplier;
import com.stephenwranger.graphics.utils.MathUtils;
import com.stephenwranger.graphics.utils.TupleMath;
import com.stephenwranger.graphics.utils.buffers.SegmentObject;
import com.stephenwranger.graphics.utils.buffers.Vertex;
import com.stephenwranger.graphics.utils.textures.Texture2d;

public class EllipticalSegment implements SegmentObject {
   private final Vertex                  v0;
   private final Vertex                  v1;
   private final Vertex                  v2;
   private final List<EllipticalSegment> splitSegments = new ArrayList<>();
   private final BoundingVolume          bounds;
   private final int                     depth;

   private Texture2d[]                   customTextures = null;
   private Tuple2d[][]                   texCoords = null;
   private int                           poolIndex     = -1;
   private int                           bufferIndex   = -1;

   public EllipticalSegment(final Vertex v0, final Vertex v1, final Vertex v2, final int depth) {
      this.v0 = v0;
      this.v1 = v1;
      this.v2 = v2;
      this.depth = depth;

      final Tuple3d min = TupleMath.getMin(this.v0.getVertex(), this.v1.getVertex(), this.v2.getVertex());
      final Tuple3d max = TupleMath.getMax(this.v0.getVertex(), this.v1.getVertex(), this.v2.getVertex());
      this.bounds = new BoundingBox(min, max);
   }
   
   public Vertex[] getVertices() {
      return new Vertex[] { v0, v1, v2 };
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (this.getClass() != obj.getClass()) {
         return false;
      }
      EllipticalSegment other = (EllipticalSegment) obj;
      if (this.v0 == null) {
         if (other.v0 != null) {
            return false;
         }
      } else if (!this.v0.equals(other.v0)) {
         return false;
      }
      if (this.v1 == null) {
         if (other.v1 != null) {
            return false;
         }
      } else if (!this.v1.equals(other.v1)) {
         return false;
      }
      if (this.v2 == null) {
         if (other.v2 != null) {
            return false;
         }
      } else if (!this.v2.equals(other.v2)) {
         return false;
      }
      return true;
   }

   public BoundingVolume getBoundingVolume() {
      return this.bounds;
   }

   @Override
   public int getBufferIndex() {
      return this.bufferIndex;
   }

   public List<EllipticalSegment> getChildSegments(final Ellipsoid ellipsoid, final BiConsumerSupplier<Double, Double, Double> altitudeSupplier, final Consumer<EllipticalSegment> setTextureFunction) {
      if (this.splitSegments.isEmpty()) {
         final List<EllipticalSegment> children = EllipticalSegment.getChildSegments(ellipsoid, this, altitudeSupplier, setTextureFunction);
         this.splitSegments.addAll(children);
      }

      return Collections.unmodifiableList(this.splitSegments);
   }

   public PickingHit getIntersection(final Renderable parent, final PickingRay ray) {
      final Tuple3d hit = IntersectionUtils.rayTriangleIntersection(this.v0.getVertex(), this.v1.getVertex(), this.v2.getVertex(), ray.getOrigin(), ray.getDirection());

      return (hit == null) ? PickingRay.NO_HIT : new PickingHit(parent, hit, ray.getOrigin().distance(hit));
   }

   @Override
   public int getSegmentPoolIndex() {
      return this.poolIndex;
   }

   @Override
   public Texture2d getTexture() {
      return (this.customTextures == null) ? null : this.customTextures[0];
   }

   @Override
   public int getVertexCount() {
      return 3;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + ((this.v0 == null) ? 0 : this.v0.hashCode());
      result = (prime * result) + ((this.v1 == null) ? 0 : this.v1.hashCode());
      result = (prime * result) + ((this.v2 == null) ? 0 : this.v2.hashCode());
      return result;
   }

   public boolean isSplit() {
      return !this.splitSegments.isEmpty();
   }

   public int getDepth() {
      return this.depth;
   }
   
   @Override
   public void loadBuffer(final Tuple3d origin, final ByteBuffer buffer) {
      this.v0.vertexIntoBuffer(origin, buffer);
      this.v1.vertexIntoBuffer(origin, buffer);
      this.v2.vertexIntoBuffer(origin, buffer);
   }
   
   /**
    * Used for testing multi-texture rendering.
    * TODO: implement this using vbo and shader
    * @param gl
    * @param glu
    * @param scene
    */
   public void render(final GL2 gl, final GLU glu, final Scene scene) {
      if(this.customTextures != null && this.texCoords != null && this.customTextures.length == this.texCoords.length) {
         final Tuple3d origin = scene.getOrigin();
   
         for(int i = 0; i < this.customTextures.length; i++) {
            final Texture2d texture = this.customTextures[i];
            final Tuple2d[] texCoord = this.texCoords[i];
            texture.enable(gl);

            gl.glBegin(GL2.GL_TRIANGLES);
            gl.glTexCoord2f((float) texCoord[0].x, (float) texCoord[0].y);
            gl.glVertex3f((float) (this.v0.getVertex().x - origin.x), (float) (this.v0.getVertex().y - origin.y), (float) (this.v0.getVertex().z - origin.z));
            gl.glTexCoord2f((float) texCoord[1].x, (float) texCoord[1].y);
            gl.glVertex3f((float) (this.v1.getVertex().x - origin.x), (float) (this.v1.getVertex().y - origin.y), (float) (this.v1.getVertex().z - origin.z));
            gl.glTexCoord2f((float) texCoord[2].x, (float) texCoord[2].y);
            gl.glVertex3f((float) (this.v2.getVertex().x - origin.x), (float) (this.v2.getVertex().y - origin.y), (float) (this.v2.getVertex().z - origin.z));
            gl.glEnd();
         }
      }
   }

   @Override
   public void setSegmentLocation(final int poolIndex, final int bufferIndex) {
      this.poolIndex = poolIndex;
      this.bufferIndex = bufferIndex;
   }

   public void setTexture(final Texture2d[] textures, final Tuple2d[][] texCoords) {
      this.customTextures = textures;
      this.texCoords = texCoords;

      if ((texCoords != null) && (texCoords.length > 0 && texCoords[0].length == 3)) {
         this.v0.setTextureCoordinates(texCoords[0][0]);
         this.v1.setTextureCoordinates(texCoords[0][1]);
         this.v2.setTextureCoordinates(texCoords[0][2]);
      }
   }
   
   public void clearTextures(final GL2 gl) {
      if(this.customTextures != null) {
         for(final Texture2d texture : this.customTextures) {
            texture.clear(gl);
         }
      }
   }

   public static EllipticalSegment createSegment(final Tuple3d v0, final Tuple3d v1, final Tuple3d v2, final int depth, final Ellipsoid ellipsoid, final BiConsumerSupplier<Double, Double, Double> altitudeSupplier, final Consumer<EllipticalSegment> setTextureFunction) {
      final Tuple3d[] corners = new Tuple3d[] { v0, v1, v2 };
      final Vertex[] vertices = new Vertex[3];

      for (int i = 0; i < corners.length; i++) {
         final Tuple3d v = corners[i];
         TupleMath.normalize(v);

         final Tuple3d vertex = new Tuple3d(v);
         final Tuple3d normal = new Tuple3d(v);
         final Tuple2d texCoord = new Tuple2d(0.5 + (Math.atan2(v.y, v.x) / (2.0 * Math.PI)), (0.5 + (Math.asin(v.z) / Math.PI)));
         final double length = Math.sqrt((texCoord.x * texCoord.x) + (texCoord.y * texCoord.y));

         final PickingRay ray = new PickingRay(new Tuple3d(0,0,0), new Vector3d(v));
         final double[] hit = ellipsoid.getIntersection(ray);
         double longitude, latitude;
         
         if(hit.length > 0) {
            final Tuple3d lonLatAlt = ellipsoid.intersectionToLonLat(ray, hit[0]);
            longitude = lonLatAlt.x;
            latitude = lonLatAlt.y;
         } else {
            longitude = Math.toDegrees(((texCoord.x / length) * MathUtils.TWO_PI) - Math.PI);
            latitude = Math.toDegrees((texCoord.y / length) * Math.PI) - (Math.PI / 2.0);
         }
         
         final double altitude = altitudeSupplier.getValue(longitude, latitude);

         TupleMath.scale(vertex, altitude); // TODO: is this the correct lon/lat?

         vertices[i] = new Vertex(v, vertex, normal, texCoord);
      }

      EllipticalSegment.fixTextureCoordinates(vertices[0].getTextureCoordinates(), vertices[1].getTextureCoordinates());
      EllipticalSegment.fixTextureCoordinates(vertices[1].getTextureCoordinates(), vertices[2].getTextureCoordinates());
      EllipticalSegment.fixTextureCoordinates(vertices[2].getTextureCoordinates(), vertices[0].getTextureCoordinates());

      final EllipticalSegment segment = new EllipticalSegment(vertices[0], vertices[1], vertices[2], depth);
      setTextureFunction.accept(segment);

      return segment;
   }

   // return index of point in the middle of p0 and p1
   private static Tuple3d bisect(final Vertex v0, final Vertex v1) {
      final Tuple3d p0 = new Tuple3d(v0.getBaseVertex());
      final Tuple3d p1 = new Tuple3d(v1.getBaseVertex());
      TupleMath.normalize(p0);
      TupleMath.normalize(p1);
      // not in cache, calculate it
      final Tuple3d center = new Tuple3d((p0.x + p1.x) / 2.0, (p0.y + p1.y) / 2.0, (p0.z + p1.z) / 2.0);
      // normalize to make sure it's as close to the sphere's surface as possible
      TupleMath.normalize(center);
      return center;
   }

   private static void fixTextureCoordinates(final Tuple2d t0, final Tuple2d t1) {
      final double tt = 0.75;
      final double nn = 1.0 - tt;

      if (Math.abs(t0.x - t1.x) > tt) {
         if (t0.x < nn) {
            t0.x += 1.f;
         }

         if (t1.x < nn) {
            t1.x += 1.f;
         }
      }
   }

   private static List<EllipticalSegment> getChildSegments(final Ellipsoid ellipsoid, final EllipticalSegment segment, final BiConsumerSupplier<Double, Double, Double> altitudeSupplier, final Consumer<EllipticalSegment> setTextureFunction) {
      // replace triangle by 4 triangles
      final Tuple3d a = EllipticalSegment.bisect(segment.v0, segment.v1);
      final Tuple3d b = EllipticalSegment.bisect(segment.v1, segment.v2);
      final Tuple3d c = EllipticalSegment.bisect(segment.v2, segment.v0);

      final Tuple3d[][] newTriangles = new Tuple3d[4][3];

      newTriangles[0] = new Tuple3d[] { new Tuple3d(segment.v0.getBaseVertex()), a, c };
      newTriangles[1] = new Tuple3d[] { new Tuple3d(segment.v1.getBaseVertex()), b, a };
      newTriangles[2] = new Tuple3d[] { new Tuple3d(segment.v2.getBaseVertex()), c, b };
      newTriangles[3] = new Tuple3d[] { a, b, c };

      final List<EllipticalSegment> newSegments = new ArrayList<>();

      for (final Tuple3d[] t : newTriangles) {
         newSegments.add(EllipticalSegment.createSegment(t[0], t[1], t[2], segment.depth + 1, ellipsoid, altitudeSupplier, setTextureFunction));
      }

      return newSegments;
   }
}
