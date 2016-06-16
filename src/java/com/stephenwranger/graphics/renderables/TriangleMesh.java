package com.stephenwranger.graphics.renderables;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.bounds.BoundingBox;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.Quat4d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.intersection.Triangle3d;

public class TriangleMesh extends Renderable {
   private final Triangle3d[] triangles;
   private final BoundingVolume bounds;
   private final Color4f color;
   
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
      gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
      gl.glDisable(GL2.GL_LIGHTING);
      gl.glDisable(GL2.GL_CULL_FACE);

      gl.glBegin(GL2.GL_TRIANGLES);

      gl.glColor4f(this.color.r, this.color.g, this.color.b, this.color.a);
      
      for(final Triangle3d triangle : this.triangles) {
         for(final Tuple3d corner : triangle.getCorners()) {
            gl.glVertex3f((float)corner.x, (float)corner.y, (float)corner.z);
         }
      }
      
      gl.glEnd();
   }

   @Override
   public BoundingVolume getBoundingVolume() {
      return this.bounds;
   }

}
