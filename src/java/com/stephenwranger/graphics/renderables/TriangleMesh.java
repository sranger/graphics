package com.stephenwranger.graphics.renderables;

import java.nio.FloatBuffer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.bounds.BoundingBox;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.Quat4d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.math.intersection.Triangle3d;
import com.stephenwranger.graphics.utils.TupleMath;
import com.stephenwranger.graphics.utils.buffers.ColorRegion;
import com.stephenwranger.graphics.utils.buffers.DataType;
import com.stephenwranger.graphics.utils.buffers.NormalRegion;
import com.stephenwranger.graphics.utils.buffers.VertexBufferObject;
import com.stephenwranger.graphics.utils.buffers.VertexRegion;

public class TriangleMesh extends Renderable {
   private VertexBufferObject vbo = null;
   private final Triangle3d[] triangles;
   private final BoundingVolume bounds;
   private final Color4f color;

   private boolean isWireframe = false;
   private boolean isDrawNormals = false;
   
   public TriangleMesh(final Triangle3d[] triangles, final Color4f color) {
      super(new Tuple3d(), new Quat4d());
      
      // TODO: make a VBO
      this.triangles = triangles;
      this.color = color;
      final Tuple3d min = new Tuple3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
      final Tuple3d max = new Tuple3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
      
      for(final Triangle3d triangle : this.triangles) {
         for(final Tuple3d corner : triangle.getCorners()) {
            min.x = Math.min(min.x, corner.x);
            min.y = Math.min(min.y, corner.y);
            min.z = Math.min(min.z, corner.z);

            max.x = Math.max(max.x, corner.x);
            max.y = Math.max(max.y, corner.y);
            max.z = Math.max(max.z, corner.z);
         }
      }
      
      this.bounds = new BoundingBox(min, max);
   }

   @Override
   public void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, final Scene scene) {
      if(this.vbo == null) {
         this.vbo = new VertexBufferObject(triangles.length * 3, true, GL2.GL_TRIANGLES, new VertexRegion(3, DataType.FLOAT), new NormalRegion(DataType.FLOAT), new ColorRegion(4, DataType.FLOAT));
         final FloatBuffer buffer = this.vbo.mapBuffer(gl).asFloatBuffer();
         
         for(final Triangle3d triangle : this.triangles) {
            final Vector3d normal = triangle.getNormal();
            
            for(final Tuple3d corner : triangle.getCorners()) {
               buffer.put((float) corner.x).put((float) corner.y).put((float) corner.z);
               buffer.put((float) normal.x).put((float) normal.y).put((float) normal.z);
               buffer.put((float) this.color.r).put((float) this.color.g).put((float) this.color.b).put((float) this.color.a);
            }
         }
         
         this.vbo.unmapBuffer(gl);
      }
      
      gl.glPushAttrib(GL2.GL_POLYGON_BIT | GL2.GL_LIGHTING_BIT);
      gl.glPolygonMode(GL2.GL_FRONT, (isWireframe) ? GL2.GL_LINE : GL2.GL_FILL);
      gl.glDisable(GL2.GL_LIGHTING);
      
      this.vbo.render(gl);
      
      if(this.isDrawNormals) {
         gl.glLineWidth(4f);
         gl.glBegin(GL2.GL_LINES);

         final Color4f brighter = new Color4f(this.color);
         brighter.r = Math.min(1.0f, brighter.r + 0.3f);
         brighter.g = Math.min(1.0f, brighter.g + 0.3f);
         brighter.b = Math.min(1.0f, brighter.b + 0.3f);
         gl.glColor4f(brighter.r, brighter.g, brighter.b, brighter.a);
         
         for(final Triangle3d triangle : this.triangles) {
            final Tuple3d[] corners = triangle.getCorners();
            final Vector3d normal = triangle.getNormal();
            normal.scale(this.bounds.getSpannedDistance(normal) / 8.0);
            final Tuple3d center = TupleMath.average(corners);

            gl.glVertex3f((float) center.x, (float) center.y, (float) center.z);
            gl.glVertex3f((float) (center.x + normal.x), (float) (center.y + normal.y), (float) (center.z + normal.z));
         }
         
         gl.glEnd();
      }
      
      gl.glPopAttrib();
   }

   @Override
   public BoundingVolume getBoundingVolume() {
      return this.bounds;
   }
   
   public void setWireframe(final boolean isWireframe) {
      this.isWireframe = isWireframe;
   }
   
   public boolean isWireframe() {
      return this.isWireframe;
   }
   
   public void setDrawNormals(final boolean isDrawNormals) {
      this.isDrawNormals = isDrawNormals;
   }
   
   public boolean isDrawNormals() {
      return this.isDrawNormals;
   }
}
