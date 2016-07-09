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

      gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
      gl.glDisable(GLLightingFunc.GL_LIGHTING);
      gl.glBegin(GL.GL_LINES);
      gl.glLineWidth(3f);

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

   //   /**
   //    * Computes corners of frustum from the given Scene and sets the currently rendering lines.<br/>
   //    * <br/. http://gamedev.stackexchange.com/a/55248
   //    *
   //    * @param gl
   //    * @param scene
   //    */
   //   private void updateLines(final GL2 gl, final Scene scene) {
   //      final Tuple3d cameraPosition = scene.getCameraPosition();
   //      final Vector3d viewVector = scene.getViewVector();
   //      final Vector3d upVector = scene.getUpVector();
   //      final Vector3d rightVector = scene.getRightVector();
   //      final double ar = scene.getSurfaceWidth() / scene.getSurfaceHeight();
   //      final double near = scene.getNear();
   //      final double far = scene.getFar();
   //
   //      final double hNear = 2.0 * Math.tan(scene.getFOV() / 2.0) * near;
   //      final double wNear = hNear * ar;
   //      final double hFar = 2.0 * Math.tan(scene.getFOV() / 2.0) * far;
   //      final double wFar = hFar * ar;
   //
   //      final Tuple3d cNear = new Tuple3d(viewVector);
   //      TupleMath.scale(cNear, near);
   //      cNear.add(cameraPosition);
   //
   //      final Tuple3d cFar = new Tuple3d(viewVector);
   //      TupleMath.scale(cFar, far);
   //      cFar.add(cameraPosition);
   //
   //      final Tuple3d ntl = FrustumRenderable.computeCorner(upVector, rightVector, cNear, wNear, hNear, true, false);
   //      final Tuple3d ntr = FrustumRenderable.computeCorner(upVector, rightVector, cNear, wNear, hNear, true, true);
   //      final Tuple3d nbr = FrustumRenderable.computeCorner(upVector, rightVector, cNear, wNear, hNear, false, true);
   //      final Tuple3d nbl = FrustumRenderable.computeCorner(upVector, rightVector, cNear, wNear, hNear, false, false);
   //
   //      final Tuple3d ftl = FrustumRenderable.computeCorner(upVector, rightVector, cFar, wFar, hFar, true, false);
   //      final Tuple3d ftr = FrustumRenderable.computeCorner(upVector, rightVector, cFar, wFar, hFar, true, true);
   //      final Tuple3d fbr = FrustumRenderable.computeCorner(upVector, rightVector, cFar, wFar, hFar, false, true);
   //      final Tuple3d fbl = FrustumRenderable.computeCorner(upVector, rightVector, cFar, wFar, hFar, false, false);
   //
   //      final Vector3d depthOffset = new Vector3d(viewVector);
   //      depthOffset.scale((far - near) * 0.1);
   //
   //      ntl.add(depthOffset);
   //      ntr.add(depthOffset);
   //      nbr.add(depthOffset);
   //      nbl.add(depthOffset);
   //
   //      ftl.subtract(depthOffset);
   //      ftr.subtract(depthOffset);
   //      fbr.subtract(depthOffset);
   //      fbl.subtract(depthOffset);
   //
   //      this.lines[0] = new Line(ntl, ntr);
   //      this.lines[1] = new Line(ntr, nbr);
   //      this.lines[2] = new Line(nbr, nbl);
   //      this.lines[3] = new Line(nbl, ntl);
   //
   //      this.lines[4] = new Line(ftl, ftr);
   //      this.lines[5] = new Line(ftr, fbr);
   //      this.lines[6] = new Line(fbr, fbl);
   //      this.lines[7] = new Line(fbl, ftl);
   //
   //      this.lines[8] = new Line(ntl, ftl);
   //      this.lines[9] = new Line(ntr, ftr);
   //      this.lines[10] = new Line(nbr, fbr);
   //      this.lines[11] = new Line(nbl, fbl);
   //   }
   //
   //   private static Tuple3d computeCorner(final Vector3d up, final Vector3d right, final Tuple3d center, final double width, final double height, final boolean isWidthPositive, final boolean isHeightPositive) {
   //      final Tuple3d point = new Tuple3d(center);
   //      final Vector3d upScaled = new Vector3d(up);
   //      upScaled.scale(height);
   //      final Vector3d rightScaled = new Vector3d(right);
   //      rightScaled.scale(height);
   //
   //      if (isHeightPositive) {
   //         point.add(upScaled);
   //      } else {
   //         point.subtract(upScaled);
   //      }
   //
   //      if (isWidthPositive) {
   //         point.add(rightScaled);
   //      } else {
   //         point.subtract(rightScaled);
   //      }
   //
   //      return point;
   //   }
}
