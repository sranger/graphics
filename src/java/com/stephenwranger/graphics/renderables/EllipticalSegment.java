package com.stephenwranger.graphics.renderables;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.bounds.BoundingBox;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.PickingHit;
import com.stephenwranger.graphics.math.PickingRay;
import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.math.intersection.Ellipsoid;
import com.stephenwranger.graphics.math.intersection.IntersectionUtils;
import com.stephenwranger.graphics.utils.BiConsumerSupplier;
import com.stephenwranger.graphics.utils.TupleMath;
import com.stephenwranger.graphics.utils.buffers.SegmentObject;
import com.stephenwranger.graphics.utils.textures.Texture2d;

public class EllipticalSegment implements SegmentObject {
   private static int[][]                faces          = new int[][] { { 0, 1, 2 }, { 0, 2, 3 }, { 0, 3, 4 }, { 0, 4, 5 }, { 0, 5, 6 }, { 0, 6, 7 }, { 0, 7, 8 }, { 0, 8, 1 } };

   private final GeodesicVertex[]        vertices;
   private final Tuple3d[]               cartesianVertices;
   private final Tuple3d[]               geodesicVertices;
   private final List<EllipticalSegment> splitSegments  = new ArrayList<>();
   private final BoundingVolume          bounds;
   private final int                     depth;

   private Texture2d[]                   customTextures = null;
   private Tuple2d[][]                   texCoords      = null;
   private int                           poolIndex      = -1;
   private int                           bufferIndex    = -1;

   private boolean                       hasChildren    = false;

   public EllipticalSegment(final GeodesicVertex[] vertices, final int depth) {
      this.vertices = vertices;
      this.depth = depth;

      this.cartesianVertices = new Tuple3d[vertices.length];
      this.geodesicVertices = new Tuple3d[vertices.length];

      for (int i = 0; i < vertices.length; i++) {
         this.cartesianVertices[i] = this.vertices[i].getVertex();
         this.geodesicVertices[i] = this.vertices[i].getGeodesicVertex();
      }

      final Tuple3d min = TupleMath.getMin(this.cartesianVertices);
      final Tuple3d max = TupleMath.getMax(this.cartesianVertices);
      this.bounds = new BoundingBox(min, max);
   }

   public void clearTextures(final GL2 gl) {
      if (this.customTextures != null) {
         for (final Texture2d texture : this.customTextures) {
            texture.clear(gl);
         }
      }
   }

   public BoundingVolume getBoundingVolume() {
      return this.bounds;
   }

   @Override
   public int getBufferIndex() {
      return this.bufferIndex;
   }

   public Tuple3d[] getCartesianVertices() {
      return this.cartesianVertices.clone();
   }

   public List<EllipticalSegment> getChildSegments(final Ellipsoid ellipsoid, final BiConsumerSupplier<Double, Double, Double> altitudeSupplier, final Consumer<EllipticalSegment> setTextureFunction, final boolean threaded) {
      synchronized (this.splitSegments) {
         if (!this.hasChildren) {
            if (threaded) {
               this.hasChildren = true;
               new Thread() {
                  @Override
                  public void run() {
                     //                     try {
                     //                        Thread.sleep(10);
                     //                     } catch (final InterruptedException e) {
                     //                        e.printStackTrace();
                     //                     }

                     final List<EllipticalSegment> children = EllipticalSegment.getChildSegments(ellipsoid, EllipticalSegment.this, altitudeSupplier, setTextureFunction);
                     EllipticalSegment.this.splitSegments.addAll(children);
                  }
               }.start();
            } else {
               this.hasChildren = true;
               final List<EllipticalSegment> children = EllipticalSegment.getChildSegments(ellipsoid, EllipticalSegment.this, altitudeSupplier, setTextureFunction);
               this.splitSegments.addAll(children);
            }
         }
      }

      return Collections.unmodifiableList(this.splitSegments);
   }

   public int getDepth() {
      return this.depth;
   }

   public Tuple3d[] getGeodesicVertices() {
      return this.geodesicVertices.clone();
   }

   public PickingHit getIntersection(final Renderable parent, final PickingRay ray) {
      final Tuple3d origin = ray.getOrigin();
      final Vector3d direction = ray.getDirection();
      Tuple3d hit = null;
      double distance = Double.MAX_VALUE;

      for (int[] face : EllipticalSegment.faces) {
         final Tuple3d v0 = this.vertices[face[0]].getVertex();
         final Tuple3d v1 = this.vertices[face[1]].getVertex();
         final Tuple3d v2 = this.vertices[face[2]].getVertex();
         final Tuple3d tempHit = IntersectionUtils.rayTriangleIntersection(v0, v1, v2, origin, direction);

         if ((tempHit != null)) {
            final double tempDistance = origin.distance(tempHit);

            if (tempDistance < distance) {
               hit = tempHit;
               distance = tempDistance;
            }
         }
      }

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

   public int getTextureCount() {
      return (this.customTextures == null) ? 0 : this.customTextures.length;
   }

   @Override
   public int getVertexCount() {
      return this.vertices.length;
   }

   public GeodesicVertex[] getVertices() {
      return this.vertices.clone();
   }

   public boolean isSplit() {
      return !this.splitSegments.isEmpty();
   }

   @Override
   public void loadBuffer(final Tuple3d origin, final ByteBuffer buffer) {
      for (int[] face : EllipticalSegment.faces) {
         final GeodesicVertex v0 = this.vertices[face[0]];
         final GeodesicVertex v1 = this.vertices[face[1]];
         final GeodesicVertex v2 = this.vertices[face[2]];
         v0.vertexIntoBuffer(origin, buffer);
         v1.vertexIntoBuffer(origin, buffer);
         v2.vertexIntoBuffer(origin, buffer);
      }
   }

   /**
    * Used for testing multi-texture rendering.
    * TODO: implement this using vbo and shader
    *
    * @param gl
    * @param glu
    * @param scene
    */
   public void render(final GL2 gl, final GLU glu, final Scene scene) {
      final Tuple3d origin = scene.getOrigin();

      for (int i = 0; i < Math.max(1, ((this.customTextures == null) ? 1 : this.customTextures.length)); i++) {
         final Texture2d texture = ((this.customTextures == null) || (this.customTextures.length < i)) ? null : this.customTextures[i];
         final Tuple2d[] texCoord = ((this.texCoords == null) || (this.texCoords.length < i)) ? null : this.texCoords[i];

         if (texture == null) {
            gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
            gl.glColorMaterial(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE);
         } else {
            gl.glDisable(GLLightingFunc.GL_COLOR_MATERIAL);
            texture.enable(gl);
         }

         gl.glBegin(GL.GL_TRIANGLE_FAN);

         for (int j = 0; j <= this.vertices.length; j++) {
            final int index = (j == this.vertices.length) ? 1 : j;
            final GeodesicVertex vertex = this.vertices[index];
            final Tuple3d xyz = vertex.getVertex();
            final Vector3d normal = vertex.getNormal();
            final Color4f color = vertex.getColor();

            gl.glColor4f(color.r, color.g, color.b, color.a);

            if ((texCoord == null) || (texCoord.length < index)) {
               final Tuple2d tc = vertex.getTextureCoordinates();
               gl.glTexCoord2f((float) tc.x, (float) tc.y);
            } else {
               gl.glTexCoord2f((float) texCoord[index].x, (float) texCoord[index].y);
            }

            gl.glNormal3f((float) normal.x, (float) normal.y, (float) normal.z);
            gl.glVertex3f((float) (xyz.x - origin.x), (float) (xyz.y - origin.y), (float) (xyz.z - origin.z));
         }

         gl.glEnd();

         if (texture != null) {
            texture.disable(gl);
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
   }

   //   public static EllipticalSegment createSegment(final Tuple3d v0, final Tuple3d v1, final Tuple3d v2, final int depth, final Ellipsoid ellipsoid, final BiConsumerSupplier<Double, Double, Double> altitudeSupplier,
   //         final Consumer<EllipticalSegment> setTextureFunction) {
   //      final Tuple3d[] corners = new Tuple3d[] { v0, v1, v2 };
   //      final GeodesicVertex[] vertices = new GeodesicVertex[3];
   //
   //      for (int i = 0; i < corners.length; i++) {
   //         final Tuple3d v = corners[i];
   //         TupleMath.normalize(v);
   //
   //         final Tuple3d vertex = new Tuple3d(v);
   //         final Tuple3d normal = new Tuple3d(v);
   //         final Tuple2d texCoord = new Tuple2d(0.5 + (Math.atan2(v.y, v.x) / (2.0 * Math.PI)), (0.5 + (Math.asin(v.z) / Math.PI)));
   //         final double length = Math.sqrt((texCoord.x * texCoord.x) + (texCoord.y * texCoord.y));
   //
   //         final PickingRay ray = new PickingRay(new Tuple3d(0, 0, 0), new Vector3d(v));
   //         final double[] hit = ellipsoid.getIntersection(ray);
   //         double longitude, latitude;
   //
   //         if (hit.length > 0) {
   //            final Tuple3d lonLatAlt = ellipsoid.intersectionToLonLat(ray, hit[0]);
   //            longitude = lonLatAlt.x;
   //            latitude = lonLatAlt.y;
   //         } else {
   //            longitude = Math.toDegrees(((texCoord.x / length) * MathUtils.TWO_PI) - Math.PI);
   //            latitude = Math.toDegrees((texCoord.y / length) * Math.PI) - (Math.PI / 2.0);
   //         }
   //
   //         final double altitude = altitudeSupplier.getValue(longitude, latitude);
   //
   //         TupleMath.scale(vertex, altitude); // TODO: is this the correct lon/lat?
   //
   //         vertices[i] = new GeodesicVertex(v, vertex, normal, texCoord, null, new Tuple3d(longitude, latitude, altitude));
   //      }
   //
   //      EllipticalSegment.fixTextureCoordinates(vertices[0].getTextureCoordinates(), vertices[1].getTextureCoordinates());
   //      EllipticalSegment.fixTextureCoordinates(vertices[1].getTextureCoordinates(), vertices[2].getTextureCoordinates());
   //      EllipticalSegment.fixTextureCoordinates(vertices[2].getTextureCoordinates(), vertices[0].getTextureCoordinates());
   //
   //      final EllipticalSegment segment = new EllipticalSegment(vertices[0], vertices[1], vertices[2], depth);
   //      setTextureFunction.accept(segment);
   //
   //      return segment;
   //   }
   //
   //   // return index of point in the middle of p0 and p1
   //   private static Tuple3d bisect(final Vertex v0, final Vertex v1) {
   //      final Tuple3d p0 = new Tuple3d(v0.getBaseVertex());
   //      final Tuple3d p1 = new Tuple3d(v1.getBaseVertex());
   //      TupleMath.normalize(p0);
   //      TupleMath.normalize(p1);
   //      // not in cache, calculate it
   //      final Tuple3d center = new Tuple3d((p0.x + p1.x) / 2.0, (p0.y + p1.y) / 2.0, (p0.z + p1.z) / 2.0);
   //      // normalize to make sure it's as close to the sphere's surface as possible
   //      TupleMath.normalize(center);
   //      return center;
   //   }
   //
   //   private static void fixTextureCoordinates(final Tuple2d t0, final Tuple2d t1) {
   //      final double tt = 0.75;
   //      final double nn = 1.0 - tt;
   //
   //      if (Math.abs(t0.x - t1.x) > tt) {
   //         if (t0.x < nn) {
   //            t0.x += 1.f;
   //         }
   //
   //         if (t1.x < nn) {
   //            t1.x += 1.f;
   //         }
   //      }
   //   }

   private static GeodesicVertex bisect(final Ellipsoid ellipsoid, final GeodesicVertex v0, final GeodesicVertex v1, final BiConsumerSupplier<Double, Double, Double> altitudeSupplier) {
      final Tuple3d lonLatAlt0 = v0.getGeodesicVertex();
      final Tuple3d lonLatAlt1 = v1.getGeodesicVertex();
      final double midLon = (lonLatAlt0.x + lonLatAlt1.x) / 2.0;
      final double midLat = (lonLatAlt0.y + lonLatAlt1.y) / 2.0;
      final double midAlt = altitudeSupplier.getValue(midLon, midLat);
      final Tuple3d lla = new Tuple3d(midLon, midLat, midAlt);
      final Tuple3d xyz = ellipsoid.toXYZ(lla);
      final Vector3d normal = new Vector3d(xyz);
      normal.normalize();
      final Tuple3d base = new Tuple3d(normal);
      final Tuple2d texCoord = new Tuple2d((midLon + 180) / 360.0, (midLat + 90) / 180.0);

      return new GeodesicVertex(base, xyz, normal, texCoord, Color4f.white(), lla);
   }

   private static EllipticalSegment createSegment(final Ellipsoid ellipsoid, final GeodesicVertex NW, final GeodesicVertex NE, final GeodesicVertex SE, final GeodesicVertex SW, final int depth,
         final BiConsumerSupplier<Double, Double, Double> altitudeSupplier) {
      final GeodesicVertex N = EllipticalSegment.bisect(ellipsoid, NW, NE, altitudeSupplier);
      final GeodesicVertex E = EllipticalSegment.bisect(ellipsoid, NE, SE, altitudeSupplier);
      final GeodesicVertex S = EllipticalSegment.bisect(ellipsoid, SE, SW, altitudeSupplier);
      final GeodesicVertex W = EllipticalSegment.bisect(ellipsoid, SW, NW, altitudeSupplier);
      final GeodesicVertex C = EllipticalSegment.bisect(ellipsoid, NW, SE, altitudeSupplier);

      final GeodesicVertex[] vertices = new GeodesicVertex[9];
      vertices[EllipticalGeometry.CENTER] = C;
      vertices[EllipticalGeometry.NORTHWEST] = NW;
      vertices[EllipticalGeometry.NORTH] = N;
      vertices[EllipticalGeometry.NORTHEAST] = NE;
      vertices[EllipticalGeometry.EAST] = E;
      vertices[EllipticalGeometry.SOUTHEAST] = SE;
      vertices[EllipticalGeometry.SOUTH] = S;
      vertices[EllipticalGeometry.SOUTHWEST] = SW;
      vertices[EllipticalGeometry.WEST] = W;

      return new EllipticalSegment(vertices, depth);
   }

   private static List<EllipticalSegment> getChildSegments(final Ellipsoid ellipsoid, final EllipticalSegment segment, final BiConsumerSupplier<Double, Double, Double> altitudeSupplier,
         final Consumer<EllipticalSegment> setTextureFunction) {
      final List<EllipticalSegment> newSegments = new ArrayList<>();
      final GeodesicVertex C = segment.vertices[EllipticalGeometry.CENTER];
      final GeodesicVertex NW = segment.vertices[EllipticalGeometry.NORTHWEST];
      final GeodesicVertex N = segment.vertices[EllipticalGeometry.NORTH];
      final GeodesicVertex NE = segment.vertices[EllipticalGeometry.NORTHEAST];
      final GeodesicVertex E = segment.vertices[EllipticalGeometry.EAST];
      final GeodesicVertex SE = segment.vertices[EllipticalGeometry.SOUTHEAST];
      final GeodesicVertex S = segment.vertices[EllipticalGeometry.SOUTH];
      final GeodesicVertex SW = segment.vertices[EllipticalGeometry.SOUTHWEST];
      final GeodesicVertex W = segment.vertices[EllipticalGeometry.WEST];

      final EllipticalSegment segmentNW = EllipticalSegment.createSegment(ellipsoid, NW, N, C, W, segment.depth + 1, altitudeSupplier);
      final EllipticalSegment segmentNE = EllipticalSegment.createSegment(ellipsoid, N, NE, E, C, segment.depth + 1, altitudeSupplier);
      final EllipticalSegment segmentSE = EllipticalSegment.createSegment(ellipsoid, C, E, SE, S, segment.depth + 1, altitudeSupplier);
      final EllipticalSegment segmentSW = EllipticalSegment.createSegment(ellipsoid, W, C, S, SW, segment.depth + 1, altitudeSupplier);

      new Thread(() -> {
         setTextureFunction.accept(segmentNW);
         setTextureFunction.accept(segmentNE);
         setTextureFunction.accept(segmentSE);
         setTextureFunction.accept(segmentSW);
      }).start();

      newSegments.add(segmentNW);
      newSegments.add(segmentNE);
      newSegments.add(segmentSE);
      newSegments.add(segmentSW);

      return newSegments;
   }
}
