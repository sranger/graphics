package com.stephenwranger.graphics.renderables;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.bounds.BoundingBox;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.Tuple3d;

public class Line extends RenderablePhysics {
   private final Tuple3d     p0, p1;
   private final BoundingBox bounds;
   private final Color4f     color = new Color4f(1, 1, 1, 1);

   public Line(final Tuple3d p0, final Tuple3d p1) {
      super(0, 0, 0, 0);

      this.p0 = new Tuple3d(p0);
      this.p1 = new Tuple3d(p1);

      this.bounds = new BoundingBox(Math.min(p0.x, p1.x), Math.min(p0.y, p1.y), Math.min(p0.z, p1.z), Math.max(p0.x, p1.x), Math.max(p0.y, p1.y), Math.max(p0.z, p1.z));
   }

   @Override
   public BoundingVolume getBoundingVolume() {
      return this.bounds;
   }

   @Override
   public boolean isCollidable() {
      return false;
   }

   @Override
   public void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, final Scene scene) {
      gl.glPushMatrix();
      gl.glDisable(GLLightingFunc.GL_LIGHTING);
      gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
      gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_DIFFUSE);
      gl.glLineWidth(4f);

      gl.glBegin(GL.GL_LINES);

      final Tuple3d origin = scene.getOrigin();

      gl.glColor4f(this.color.r, this.color.g, this.color.b, this.color.a);
      gl.glVertex3f((float) (this.p0.x - origin.x), (float) (this.p0.y - origin.y), (float) (this.p0.z - origin.z));
      gl.glVertex3f((float) (this.p1.x - origin.x), (float) (this.p1.y - origin.y), (float) (this.p1.z - origin.z));

      gl.glEnd();
      gl.glFlush();
      gl.glPopMatrix();
   }

   @Override
   public void setCollidable(final boolean value) {
   }

   public void setColor(final Color4f color) {
      this.color.setColor(color);
   }
}
