package com.stephenwranger.graphics.renderables;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.math.CameraUtils;
import com.stephenwranger.graphics.math.Quat4d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.intersection.IntersectionUtils;
import com.stephenwranger.graphics.math.intersection.Plane;

public class FrustumRenderable extends Renderable {
   private final Tuple3d[][] lines    = new Tuple3d[12][];
   private boolean           isPaused = false;

   public FrustumRenderable() {
      super(new Tuple3d(), new Quat4d());
   }

   @Override
   public BoundingVolume getBoundingVolume() {
      // nothing as we don't want this changing the frustum calculation
      return null;
   }

   public boolean isPaused() {
      return this.isPaused;
   }

   @Override
   public void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, final Scene scene) {
      if (!this.isPaused) {
         this.updateLines(gl, scene);
      }

      gl.glPushAttrib(GL2.GL_LIGHTING_BIT | GL2.GL_COLOR_BUFFER_BIT | GL2.GL_LINE_BIT);
      gl.glDisable(GLLightingFunc.GL_LIGHTING);
      gl.glLineWidth(10f);
      
      gl.glBegin(GL.GL_LINES);
      gl.glColor4f(1f, 1f, 1f, 1f);

      // these do not need to offset by the scene's origin as they were created using the mvp matrix directly
      for (final Tuple3d[] line : this.lines) {
         if (line != null) {
            gl.glVertex3f((float) line[0].x, (float) line[0].y, (float) line[0].z);
            gl.glVertex3f((float) line[1].x, (float) line[1].y, (float) line[1].z);
         }
      }

      gl.glEnd();
      gl.glPopAttrib();
   }

   public void setPaused(final boolean isPaused) {
      this.isPaused = isPaused;
   }

   /**
    * Computes corners of frustum from the given Scene's frustum planes and sets the currently rendering lines.<br/>
    *
    * @param gl
    * @param scene
    */
   private void updateLines(final GL2 gl, final Scene scene) {
      final Plane[] frustum = scene.getFrustumPlanes();
      final Plane n = frustum[CameraUtils.NEAR_PLANE];
      final Plane f = frustum[CameraUtils.FAR_PLANE];
      final Plane t = frustum[CameraUtils.TOP_PLANE];
      final Plane b = frustum[CameraUtils.BOTTOM_PLANE];
      final Plane l = frustum[CameraUtils.LEFT_PLANE];
      final Plane r = frustum[CameraUtils.RIGHT_PLANE];

      final Tuple3d ntl = IntersectionUtils.intersectingPlanes(n, t, l);
      final Tuple3d ntr = IntersectionUtils.intersectingPlanes(n, t, r);
      final Tuple3d nbr = IntersectionUtils.intersectingPlanes(n, b, r);
      final Tuple3d nbl = IntersectionUtils.intersectingPlanes(n, b, l);

      final Tuple3d ftl = IntersectionUtils.intersectingPlanes(f, t, l);
      final Tuple3d ftr = IntersectionUtils.intersectingPlanes(f, t, r);
      final Tuple3d fbr = IntersectionUtils.intersectingPlanes(f, b, r);
      final Tuple3d fbl = IntersectionUtils.intersectingPlanes(f, b, l);

      if ((ntl != null) && (ntr != null) && (nbr != null) && (nbl != null) && (ftl != null) && (ftr != null) && (fbr != null) && (fbl != null)) {
         this.lines[0] = new Tuple3d[] { ntl, ntr };
         this.lines[1] = new Tuple3d[] { ntr, nbr };
         this.lines[2] = new Tuple3d[] { nbr, nbl };
         this.lines[3] = new Tuple3d[] { nbl, ntl };

         this.lines[4] = new Tuple3d[] { ftl, ftr };
         this.lines[5] = new Tuple3d[] { ftr, fbr };
         this.lines[6] = new Tuple3d[] { fbr, fbl };
         this.lines[7] = new Tuple3d[] { fbl, ftl };

         this.lines[8] = new Tuple3d[] { ntl, ftl };
         this.lines[9] = new Tuple3d[] { ntr, ftr };
         this.lines[10] = new Tuple3d[] { nbr, fbr };
         this.lines[11] = new Tuple3d[] { nbl, fbl };
      } else {
         for (int i = 0; i < this.lines.length; i++) {
            this.lines[i] = null;
         }

         System.err.println("frustum planes do not intersect");
         System.err.println("\tntl: " + ntl);
         System.err.println("\tntr: " + ntr);
         System.err.println("\tnbr: " + nbr);
         System.err.println("\tnbl: " + nbl);
         System.err.println("\tftl: " + ftl);
         System.err.println("\tftr: " + ftr);
         System.err.println("\tfbr: " + fbr);
         System.err.println("\tfbl: " + fbl);
      }
   }
}
