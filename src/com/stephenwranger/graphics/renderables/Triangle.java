package com.stephenwranger.graphics.renderables;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.bounds.BoundingBox;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.Tuple3d;

public class Triangle extends Renderable {
   
   private final double baseWidth;
   private final double height;
   private final Color4f color = Color4f.white();
   private final Tuple3d[] vert;
   
   public Triangle(final double baseWidth, final double height) {
      this(0,0,0,0, baseWidth, height);
   }

   public Triangle(final double mass, final double restitution, final double muStatic, final double muKinetic, final double baseWidth, final double height) {
      super(mass, restitution, muStatic, muKinetic);
      
      this.baseWidth = baseWidth;
      this.height = height;
      
      vert = new Tuple3d[3];
      vert[0] = new Tuple3d(-baseWidth * 0.5, -height * 0.5, 0);
      vert[1] = new Tuple3d(baseWidth * 0.5, -height * 0.5, 0);
      vert[2] = new Tuple3d(0, height * 0.5, 0);
   }

   @Override
   public void setCollidable(final boolean value) {
      // TODO: ?
   }

   @Override
   public boolean isCollidable() {
      return false;
   }

   @Override
   public void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, Scene scene) {
      gl.glBegin(GL2.GL_TRIANGLES);
      
      gl.glColor4f(color.r, color.g, color.b, color.a);
      gl.glVertex3f((float)vert[0].x, (float)vert[0].y, (float)vert[0].z);
      gl.glVertex3f((float)vert[1].x, (float)vert[1].y, (float)vert[1].z);
      gl.glVertex3f((float)vert[2].x, (float)vert[2].y, (float)vert[2].z);
      
      gl.glEnd();
   }

   @Override
   public BoundingVolume getBoundingVolume() {
      return new BoundingBox(getPosition(), baseWidth, height, 0);
   }

}
