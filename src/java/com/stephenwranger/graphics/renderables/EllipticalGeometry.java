package com.stephenwranger.graphics.renderables;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.bounds.BoundingSphere;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.PickingHit;
import com.stephenwranger.graphics.math.PickingRay;
import com.stephenwranger.graphics.math.Quat4d;
import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.utils.BiConsumerSupplier;
import com.stephenwranger.graphics.utils.MathUtils;
import com.stephenwranger.graphics.utils.TupleMath;
import com.stephenwranger.graphics.utils.buffers.BufferRegion;
import com.stephenwranger.graphics.utils.buffers.DataType;
import com.stephenwranger.graphics.utils.buffers.NormalRegion;
import com.stephenwranger.graphics.utils.buffers.TextureRegion;
import com.stephenwranger.graphics.utils.buffers.Vertex;
import com.stephenwranger.graphics.utils.buffers.VertexBufferObject;
import com.stephenwranger.graphics.utils.buffers.VertexRegion;
import com.stephenwranger.graphics.utils.textures.Texture2d;

public class EllipticalGeometry extends Renderable {
   private final BiConsumerSupplier<Double, Double, Double> altitudeSupplier;
   private final BoundingSphere bounds;
   
   // TODO: use shader
   private final Tuple3d[]    mainVertices        = new Tuple3d[12];
   private final int[][]      mainFaces           = new int[20][3];
   private final List<Vertex> vertices            = new LinkedList<Vertex>();
   private final VertexBufferObject vbo;
   private Texture2d          texture             = null;
   private Color4f            color               = Color4f.white();
   
   public EllipticalGeometry(final GL2 gl, final double boundedRadius, final int subdivisions, final BiConsumerSupplier<Double, Double, Double> altitudeSupplier) {
      super(new Tuple3d(), new Quat4d());
      
      this.altitudeSupplier = altitudeSupplier;
      this.bounds = new BoundingSphere(new Tuple3d(), boundedRadius);
      
      // icosahedron vertices
      final double t = (1.0 + Math.sqrt(5.0)) / 2.0;
    
      // original geometry
      this.mainVertices[0] = new Tuple3d(-1, t, 0);
      this.mainVertices[1] = new Tuple3d(1, t, 0);
      this.mainVertices[2] = new Tuple3d(-1, -t, 0);
      this.mainVertices[3] = new Tuple3d(1, -t, 0);
      
      this.mainVertices[4] = new Tuple3d(0, -1, t);
      this.mainVertices[5] = new Tuple3d(0, 1, t);
      this.mainVertices[6] = new Tuple3d(0, -1, -t);
      this.mainVertices[7] = new Tuple3d(0, 1, -t);
      
      this.mainVertices[8] = new Tuple3d(t, 0, -1);
      this.mainVertices[9] = new Tuple3d(t, 0, 1);
      this.mainVertices[10] = new Tuple3d(-t, 0, -1);
      this.mainVertices[11] = new Tuple3d(-t, 0, 1);
      
      // faces near v0
      this.mainFaces[0] = new int[] { 0, 11, 5 };
      this.mainFaces[1] = new int[] { 0, 5, 1 };
      this.mainFaces[2] = new int[] { 0, 1, 7 };
      this.mainFaces[3] = new int[] { 0, 7, 10 };
      this.mainFaces[4] = new int[] { 0, 10, 11 };
      
      // faces next to v0
      this.mainFaces[5] = new int[] { 1, 5, 9 };
      this.mainFaces[6] = new int[] { 5, 11, 4 };
      this.mainFaces[7] = new int[] { 11, 10, 2 };
      this.mainFaces[8] = new int[] { 10, 7, 6 };
      this.mainFaces[9] = new int[] { 7, 1, 8 };
      
      // faces near v3
      this.mainFaces[10] = new int[] { 3, 9, 4 };
      this.mainFaces[11] = new int[] { 3, 4, 2 };
      this.mainFaces[12] = new int[] { 3, 2, 6 };
      this.mainFaces[13] = new int[] { 3, 6, 8 };
      this.mainFaces[14] = new int[] { 3, 8, 9 };
      
      // faces next to v3
      this.mainFaces[15] = new int[] { 4, 9, 5 };
      this.mainFaces[16] = new int[] { 2, 4, 11 };
      this.mainFaces[17] = new int[] { 6, 2, 10 };
      this.mainFaces[18] = new int[] { 8, 6, 7 };
      this.mainFaces[19] = new int[] { 9, 8, 1 };
      
      for (int i = 0; i < this.mainFaces.length; i++) {
         this.split(new Tuple3d[] { this.mainVertices[this.mainFaces[i][0]], this.mainVertices[this.mainFaces[i][1]], this.mainVertices[this.mainFaces[i][2]] }, subdivisions);
      }
      
      correctSeam(this.vertices, true);
      
      this.vbo = new VertexBufferObject(this.vertices.size(), true, GL2.GL_TRIANGLES, new BufferRegion[] { new VertexRegion(3, DataType.FLOAT), new NormalRegion(DataType.FLOAT),
            new TextureRegion(2, DataType.FLOAT) });
      final ByteBuffer buffer = this.vbo.mapBuffer(gl);
      
      for (final Vertex vert : this.vertices) {
         vert.vertexIntoBuffer(buffer);
      }
      
      buffer.rewind();
      this.vbo.unmapBuffer(gl);
   }
   
   private void split(final Tuple3d[] triangle, final int repeat) {
      // replace triangle by 4 triangles
      final Tuple3d a = this.bisect(triangle[0], triangle[1]);
      final Tuple3d b = this.bisect(triangle[1], triangle[2]);
      final Tuple3d c = this.bisect(triangle[2], triangle[0]);
      
      final Tuple3d[][] newTriangles = new Tuple3d[4][3];
      
      newTriangles[0] = new Tuple3d[] { triangle[0], a, c };
      newTriangles[1] = new Tuple3d[] { triangle[1], b, a };
      newTriangles[2] = new Tuple3d[] { triangle[2], c, b };
      newTriangles[3] = new Tuple3d[] { a, b, c };
      
      if (repeat > 0) {
         this.split(newTriangles[0], repeat - 1);
         this.split(newTriangles[1], repeat - 1);
         this.split(newTriangles[2], repeat - 1);
         this.split(newTriangles[3], repeat - 1);
      } else {
         Tuple3d vertex;
         Tuple3d normal;
         Tuple2d texCoord;
         
         for (final Tuple3d[] t : newTriangles) {
            for (final Tuple3d v : t) {
               vertex = new Tuple3d(v);
               normal = new Tuple3d(v);
               texCoord = new Tuple2d(0.5 + Math.atan2(v.y, v.x) / (2.0 * Math.PI), (0.5 + Math.asin(v.z) / Math.PI));

               final double azimuth = texCoord.x * MathUtils.TWO_PI - Math.PI;
               final double elevation = texCoord.y * Math.PI - Math.PI / 2.0;
               TupleMath.scale(vertex, this.altitudeSupplier.getValue(azimuth, elevation)); // TODO: is this the correct az/el?
               
               this.vertices.add(new Vertex(vertex, normal, texCoord));
            }
         }
      }
   }
   
   // return index of point in the middle of p0 and p1
   private Tuple3d bisect(final Tuple3d p0, final Tuple3d p1) {
      TupleMath.normalize(p0);
      TupleMath.normalize(p1);
      // not in cache, calculate it
      final Tuple3d center = new Tuple3d((p0.x + p1.x) / 2.0, (p0.y + p1.y) / 2.0, (p0.z + p1.z) / 2.0);
      // normalize to make sure it's as close to the sphere's surface as possible
      TupleMath.normalize(center);
      return center;
   }
   
   public static void fixTextureCoordinates(final Tuple2d t0, final Tuple2d t1) {
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
   
   public static void correctSeam(final List<Vertex> vertices, final boolean counterClockwise) {
      Tuple2d v0, v1, v2;
      
      for (int i = 0; i < vertices.size() / 3; i++) {
         v0 = vertices.get(i * 3 + 0).getTextureCoordinates();
         v1 = vertices.get(i * 3 + 1).getTextureCoordinates();
         v2 = vertices.get(i * 3 + 2).getTextureCoordinates();
         
         fixTextureCoordinates(v0, v1);
         fixTextureCoordinates(v1, v2);
         fixTextureCoordinates(v2, v0);
      }
   }
   
   public void setColor(final Color4f color) {
      this.color.setColor(color);
   }
   
   public void setTexture(final Texture2d texture) {
      this.texture = texture;
   }

   @Override
   public void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, final Scene scene) {
      gl.glPushMatrix();
      
      gl.glEnable(GL2.GL_LIGHTING);
      gl.glEnable(GL2.GL_LIGHT0);
      
      if (this.texture == null) {
         gl.glEnable(GL2.GL_COLOR_MATERIAL);
         gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
      } else {
         gl.glDisable(GL2.GL_COLOR_MATERIAL);
         this.texture.enable(gl);
      }
      
      final float[] axis = new float[3];
      final float angle = this.rotation.toAngleAxis(axis);
      
      gl.glTranslatef((float) this.position.x, (float) this.position.y, (float) this.position.z);
      gl.glRotatef((float) Math.toDegrees(angle), axis[0], axis[1], axis[2]);
      
      this.vbo.render(gl);
      
      gl.glFlush();
      
      if (this.texture != null) {
         this.texture.disable(gl);
      }
      
      gl.glPopMatrix();
   }

   @Override
   public BoundingVolume getBoundingVolume() {
      return this.bounds;
   }
   
   @Override
   public PickingHit getIntersection(final PickingRay ray) {
      return ray.raySphereIntersection(this, this.getPosition(), this.bounds.getRadius(), 1.0);
   }
}
