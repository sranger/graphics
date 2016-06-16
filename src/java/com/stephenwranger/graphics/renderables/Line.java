package com.stephenwranger.graphics.renderables;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.bounds.BoundingBox;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.Tuple3d;

public class Line extends RenderablePhysics {
   private final Tuple3d p0, p1;
   private final BoundingBox bounds;
   private final Color4f color = new Color4f(1,1,1,1);
   
   public Line(final Tuple3d p0, final Tuple3d p1) {
      super(0,0,0,0);

      this.p0 = new Tuple3d(p0);
      this.p1 = new Tuple3d(p1);
      
      this.bounds = new BoundingBox(Math.min(p0.x, p1.x), Math.min(p0.y, p1.y), Math.min(p0.z, p1.z),
                                    Math.max(p0.x, p1.x), Math.max(p0.y, p1.y), Math.max(p0.z, p1.z));
   }

   @Override
   public void setCollidable(final boolean value) {
   }

   @Override
   public boolean isCollidable() {
      return false;
   }

   @Override
   public void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, Scene scene) {
      gl.glPushMatrix();
      gl.glDisable(GL2.GL_LIGHTING);
      gl.glEnable(GL2.GL_COLOR_MATERIAL);
      gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE);
      gl.glLineWidth(4f);
      
      gl.glBegin(GL2.GL_LINES);

      gl.glColor4f(color.r, color.g, color.b, color.a);
      gl.glVertex3f((float)p0.x, (float)p0.y, (float)p0.z);
      gl.glVertex3f((float)p1.x, (float)p1.y, (float)p1.z);
      
      gl.glEnd();
      gl.glFlush();
      gl.glPopMatrix();
   }

   @Override
   public BoundingVolume getBoundingVolume() {
      return bounds;
   }

   public void setColor(final Color4f color) {
      this.color.setColor(color);
   }
}
