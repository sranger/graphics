package com.stephenwranger.graphics.math;

import java.util.Arrays;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.math.intersection.Plane;
import com.stephenwranger.graphics.utils.MathUtils;
import com.stephenwranger.graphics.utils.TupleMath;

/**
 * http://www.scratchapixel.com/lessons/3d-basic-rendering/perspective-and-orthographic-projection-matrix/building-basic-perspective-projection-matrix<br/>
 * https://www.opengl.org/wiki/GluProject_and_gluUnProject_code
 *
 * @author rangers
 *
 */
public class CameraUtils {
   public static final int  LEFT_PLANE   = 0;
   public static final int  RIGHT_PLANE  = 1;
   public static final int  BOTTOM_PLANE = 2;
   public static final int  TOP_PLANE    = 3;
   public static final int  NEAR_PLANE   = 4;
   public static final int  FAR_PLANE    = 5;
   private static final GLU GLU_CONTEXT  = new GLU();

   private CameraUtils() {
      // statics only
   }

   /**
    * http://stackoverflow.com/a/12926655/1451705
    */
   public static double[] getFrustumOrthographic(final Matrix4d m) {
      final double near = (1 + m.get(2, 3)) / m.get(2, 2);
      final double far = -(1 - m.get(2, 3)) / m.get(2, 2);
      final double bottom = (1 - m.get(1, 3)) / m.get(1, 1);
      final double top = -(1 + m.get(1, 3)) / m.get(1, 1);
      final double left = -(1 + m.get(0, 3)) / m.get(0, 0);
      final double right = (1 - m.get(0, 3)) / m.get(0, 0);

      final double[] planes = new double[6];
      planes[CameraUtils.LEFT_PLANE] = left;
      planes[CameraUtils.RIGHT_PLANE] = right;
      planes[CameraUtils.BOTTOM_PLANE] = bottom;
      planes[CameraUtils.TOP_PLANE] = top;
      planes[CameraUtils.NEAR_PLANE] = near;
      planes[CameraUtils.FAR_PLANE] = far;

      return planes;
   }

   /**
    * http://stackoverflow.com/a/12926655/1451705
    */
   public static double[] getFrustumPerspective(final Matrix4d m) {
      final double near = m.get(2, 3) / (m.get(2, 2) - 1);
      final double far = m.get(2, 3) / (m.get(2, 2) + 1);
      final double bottom = (near * (m.get(1, 2) - 1)) / m.get(1, 1);
      final double top = (near * (m.get(1, 2) + 1)) / m.get(1, 1);
      final double left = (near * (m.get(0, 2) - 1)) / m.get(0, 0);
      final double right = (near * (m.get(0, 2) + 1)) / m.get(0, 0);

      final double[] planes = new double[6];
      planes[CameraUtils.LEFT_PLANE] = left;
      planes[CameraUtils.RIGHT_PLANE] = right;
      planes[CameraUtils.BOTTOM_PLANE] = bottom;
      planes[CameraUtils.TOP_PLANE] = top;
      planes[CameraUtils.NEAR_PLANE] = near;
      planes[CameraUtils.FAR_PLANE] = far;

      return planes;
   }

   /**
    * Generates the six frustum planes using the projection matrix. The index constants can be accessed from this class
    * as static values.<br/>
    * <br/>
    * http://ruh.li/CameraViewFrustum.html<br/>
    * http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf
    *
    * @param modelview
    * @param projection
    * @return
    */
   public static Plane[] getFrustumPlanes(final Tuple3d origin, final Matrix4d mvpMatrix) {
      final double[] clip = mvpMatrix.get();
      double t;

      /*
       * Translate the planes by the localeOrigin. The new distance of the plane from the origin is the dot product of
       * the plane normal and the translation vector (ax + by + cz + d = aTx + bTy + cTz). Subtract this value from the
       * d component of the plane equation to get the new plane equation in the form ax + by + cz + d = 0.
       *
       * planeD -= planeNormal.x * origin.x + planeNormal.y * origin.y + planeNormal.z * origin.z;
       */

      /* Extract the numbers for the RIGHT plane */
      final Vector3d rightNormal = new Vector3d();
      rightNormal.x = clip[3] - clip[0];
      rightNormal.y = clip[7] - clip[4];
      rightNormal.z = clip[11] - clip[8];
      double rightD = clip[15] - clip[12];

      /* Normalize the result */
      t = (float) Math.sqrt((rightNormal.x * rightNormal.x) + (rightNormal.y * rightNormal.y) + (rightNormal.z * rightNormal.z));
      rightNormal.x /= t;
      rightNormal.y /= t;
      rightNormal.z /= t;
      rightD /= t;
      rightD -= (rightNormal.x * origin.x) + (rightNormal.y * origin.y) + (rightNormal.z * origin.z);
      final Plane rightPlane = new Plane(rightNormal, rightD);

      /* Extract the numbers for the LEFT Plane */
      final Vector3d leftNormal = new Vector3d();
      leftNormal.x = clip[3] + clip[0];
      leftNormal.y = clip[7] + clip[4];
      leftNormal.z = clip[11] + clip[8];
      double leftD = clip[15] + clip[12];

      /* Normalize the result */
      t = (float) Math.sqrt((leftNormal.x * leftNormal.x) + (leftNormal.y * leftNormal.y) + (leftNormal.z * leftNormal.z));
      leftNormal.x /= t;
      leftNormal.y /= t;
      leftNormal.z /= t;
      leftD /= t;
      leftD -= (leftNormal.x * origin.x) + (leftNormal.y * origin.y) + (leftNormal.z * origin.z);
      final Plane leftPlane = new Plane(leftNormal, leftD);

      /* Extract the BOTTOM Plane */
      final Vector3d bottomNormal = new Vector3d();
      bottomNormal.x = clip[3] + clip[1];
      bottomNormal.y = clip[7] + clip[5];
      bottomNormal.z = clip[11] + clip[9];
      double bottomD = clip[15] + clip[13];

      /* Normalize the result */
      t = (float) Math.sqrt((bottomNormal.x * bottomNormal.x) + (bottomNormal.y * bottomNormal.y) + (bottomNormal.z * bottomNormal.z));
      bottomNormal.x /= t;
      bottomNormal.y /= t;
      bottomNormal.z /= t;
      bottomD /= t;
      bottomD -= (bottomNormal.x * origin.x) + (bottomNormal.y * origin.y) + (bottomNormal.z * origin.z);
      final Plane bottomPlane = new Plane(bottomNormal, bottomD);

      /* Extract the TOP Plane */
      final Vector3d topNormal = new Vector3d();
      topNormal.x = clip[3] - clip[1];
      topNormal.y = clip[7] - clip[5];
      topNormal.z = clip[11] - clip[9];
      double topD = clip[15] - clip[13];

      /* Normalize the result */
      t = (float) Math.sqrt((topNormal.x * topNormal.x) + (topNormal.y * topNormal.y) + (topNormal.z * topNormal.z));
      topNormal.x /= t;
      topNormal.y /= t;
      topNormal.z /= t;
      topD /= t;
      topD -= (topNormal.x * origin.x) + (topNormal.y * origin.y) + (topNormal.z * origin.z);
      final Plane topPlane = new Plane(topNormal, topD);

      /* Extract the NEAR Plane */
      final Vector3d nearNormal = new Vector3d();
      nearNormal.x = clip[3] - clip[2];
      nearNormal.y = clip[7] - clip[6];
      nearNormal.z = clip[11] - clip[10];
      double nearD = clip[15] - clip[14];

      /* Normalize the result */
      t = (float) Math.sqrt((nearNormal.x * nearNormal.x) + (nearNormal.y * nearNormal.y) + (nearNormal.z * nearNormal.z));
      nearNormal.x /= t;
      nearNormal.y /= t;
      nearNormal.z /= t;
      nearD /= t;
      nearD -= (nearNormal.x * origin.x) + (nearNormal.y * origin.y) + (nearNormal.z * origin.z);
      final Plane nearPlane = new Plane(nearNormal, nearD);

      /* Extract the FAR Plane */
      final Vector3d farNormal = new Vector3d();
      farNormal.x = clip[3] + clip[2];
      farNormal.y = clip[7] + clip[6];
      farNormal.z = clip[11] + clip[10];
      double farD = clip[15] + clip[14];

      /* Normalize the result */
      t = (float) Math.sqrt((farNormal.x * farNormal.x) + (farNormal.y * farNormal.y) + (farNormal.z * farNormal.z));
      farNormal.x /= t;
      farNormal.y /= t;
      farNormal.z /= t;
      farD /= t;
      farD -= (farNormal.x * origin.x) + (farNormal.y * origin.y) + (farNormal.z * origin.z);
      final Plane farPlane = new Plane(farNormal, farD);

      final Plane[] planes = new Plane[6];
      planes[CameraUtils.RIGHT_PLANE] = rightPlane;
      planes[CameraUtils.LEFT_PLANE] = leftPlane;
      planes[CameraUtils.BOTTOM_PLANE] = bottomPlane;
      planes[CameraUtils.TOP_PLANE] = topPlane;
      planes[CameraUtils.NEAR_PLANE] = nearPlane;
      planes[CameraUtils.FAR_PLANE] = farPlane;

      //      System.out.println("rightD: " + rightD);
      //      System.out.println("leftD: " + leftD);
      //      System.out.println("bottomD: " + bottomD);
      //      System.out.println("topD: " + topD);
      //      System.out.println("nearD: " + nearD);
      //      System.out.println("farD: " + farD);
      //      System.out.println("near.dot(far): " + nearNormal.dot(farNormal));

      return planes;
   }

   /**
    * Computes the ModelView matrix; reference: https://www.opengl.org/wiki/GluLookAt_code.
    *
    * @param eyePosition3D
    * @param center3D
    * @param upVector3D
    * @return
    */
   public static double[] gluLookAt(final GL2 gl, final Tuple3d eyePosition3D, final Tuple3d center3D, final Vector3d upVector3D) {
      final Vector3d forward = Vector3d.getVector(eyePosition3D, center3D, true);
      final Vector3d side = new Vector3d();

      side.cross(forward, upVector3D);
      side.normalize();

      /*
       * Normalizes up without sqrt...
       */
      upVector3D.cross(side, forward);

      /*
       * glTranslate(-eyex, -eyey, -eyez)
       */
      final double eyex = -(eyePosition3D.x);
      final double eyey = -(eyePosition3D.y);
      final double eyez = -(eyePosition3D.z);
      final double[] mv = new double[16];
      mv[0] = side.x;
      mv[4] = side.y;
      mv[8] = side.z;
      mv[12] = (side.x * eyex) + (side.y * eyey) + (side.z * eyez);

      mv[1] = upVector3D.x;
      mv[5] = upVector3D.y;
      mv[9] = upVector3D.z;
      mv[13] = (upVector3D.x * eyex) + (upVector3D.y * eyey) + (upVector3D.z * eyez);

      mv[2] = -forward.x;
      mv[6] = -forward.y;
      mv[10] = -forward.z;
      mv[14] = -((forward.x * eyex) + (forward.y * eyey) + (forward.z * eyez));

      mv[3] = 0;
      mv[7] = 0;
      mv[11] = 0;
      mv[15] = 1;

      //      final double[] gluVersion = CameraUtils.gluLookAtGLU(gl, eyePosition3D, center3D, upVector3D);
      //      if (!Arrays.equals(gluVersion, mv)) {
      //         System.out.println("not equal");
      //         System.out.println("\tglu: " + Arrays.toString(gluVersion));
      //         System.out.println("\tme:  " + Arrays.toString(mv));
      //      }

      gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
      gl.glLoadIdentity();
      gl.glLoadMatrixd(mv, 0);

      return mv;
   }

   /**
    * Computes the ModelView matrix; reference: https://www.opengl.org/wiki/GluLookAt_code.
    *
    * @param eyePosition3D
    * @param center3D
    * @param upVector3D
    * @return
    */
   public static double[] gluLookAtGLU(final GL2 gl, final Tuple3d eyePosition3D, final Tuple3d center3D, final Vector3d upVector3D) {
      gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
      gl.glLoadIdentity();
      CameraUtils.GLU_CONTEXT.gluLookAt(eyePosition3D.x, eyePosition3D.y, eyePosition3D.z, center3D.x, center3D.y, center3D.z, upVector3D.x, upVector3D.y, upVector3D.z);

      final double[] modelview = new double[16];
      gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, modelview, 0);

      return modelview;
   }

   /**
    * Computes Projection Matrix; reference: https://www.opengl.org/wiki/GluPerspective_code.
    *
    * @param fovyInDegrees
    * @param aspectRatio
    * @param znear
    * @param zfar
    * @return
    */
   public static double[] gluPerspective(final GL2 gl, final double fovyInDegrees, final double aspectRatio, final double znear, final double zfar) {
      final double ymax = znear * Math.tan(Math.toRadians(fovyInDegrees / 2.0));
      final double xmax = ymax * aspectRatio;
      final double temp = 2.0 * znear;
      final double left = -xmax;
      final double right = xmax;
      final double bottom = -ymax;
      final double top = ymax;
      final double temp2 = right - left;
      final double temp3 = top - bottom;
      final double temp4 = zfar - znear;
      final double[] matrix = new double[16];

      matrix[0] = temp / temp2;
      matrix[1] = 0.0;
      matrix[2] = 0.0;
      matrix[3] = 0.0;
      matrix[4] = 0.0;
      matrix[5] = temp / temp3;
      matrix[6] = 0.0;
      matrix[7] = 0.0;
      matrix[8] = (right + left) / temp2;
      matrix[9] = (top + bottom) / temp3;
      matrix[10] = (-zfar - znear) / temp4;
      matrix[11] = -1.0;
      matrix[12] = 0.0;
      matrix[13] = 0.0;
      matrix[14] = (-temp * zfar) / temp4;
      matrix[15] = 0.0;

      //      final double[] gluVersion = CameraUtils.gluPerspectiveGLU(gl, fovyInDegrees, aspectRatio, znear, zfar);
      //      if (!Arrays.equals(gluVersion, matrix)) {
      //         System.out.println("not equal");
      //         System.out.println("\tglu: " + Arrays.toString(gluVersion));
      //         System.out.println("\tme:  " + Arrays.toString(matrix));
      //      }

      gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
      gl.glLoadIdentity();
      gl.glLoadMatrixd(matrix, 0);

      return matrix;
   }

   /**
    * Computes Projection Matrix; reference: https://www.opengl.org/wiki/GluPerspective_code.
    *
    * @param fovyInDegrees
    * @param aspectRatio
    * @param znear
    * @param zfar
    * @return
    */
   public static double[] gluPerspectiveGLU(final GL2 gl, final double fovyInDegrees, final double aspectRatio, final double znear, final double zfar) {
      gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
      gl.glLoadIdentity();
      CameraUtils.GLU_CONTEXT.gluPerspective(fovyInDegrees, aspectRatio, znear, zfar);

      final double[] projection = new double[16];
      gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projection, 0);

      return projection;
   }

   /**
    * http://www.opengl.org/sdk/docs/man2/xhtml/gluProject.xml
    *
    * @param scene
    * @param worldXyz
    *           coordinates to convert x,y,z cartesian world coordinate
    * @return screen coordinates as x,y in screen space and z being normalized depth
    */
   public static Tuple3d gluProject(final Scene scene, final Tuple3d worldXyz) {
      final Tuple3d xyz = TupleMath.sub(worldXyz, scene.getOrigin());
      final double[] modelview = scene.getModelViewMatrix();
      final double[] projection = scene.getProjectionMatrix();
      final int[] viewport = scene.getViewport();
      final double[] winPos = new double[3];

      if (CameraUtils.GLU_CONTEXT.gluProject(xyz.x, xyz.y, xyz.z, modelview, 0, projection, 0, viewport, 0, winPos, 0)) {
         return new Tuple3d(winPos);
      } else {
         //         new RuntimeException("invalid gluProject\n\tworld = " + worldXyz + "\n\tmv = " + Arrays.toString(modelview) + "\n\tproj = " + Arrays.toString(projection) + "\n\tviewport: " + Arrays.toString(viewport)).printStackTrace();
         return null;
      }
   }

   /**
    * http://www.opengl.org/sdk/docs/man2/xhtml/gluUnProject.xml
    *
    * @param scene
    * @param screenXyz
    *           screen coordinates as x,y in screen space and z being normalized depth
    * @return x,y,z cartesian world coordinates
    */
   public static Tuple3d gluUnProject(final Scene scene, final Tuple3d screenXyz) {
      final double[] modelview = scene.getModelViewMatrix();
      final double[] projection = scene.getProjectionMatrix();
      final int[] viewport = scene.getViewport();
      final double[] worldPos = new double[] { 0, 0, 0, 1 };

      final double x = MathUtils.clamp(viewport[0] + 1, viewport[2] - 1, screenXyz.x);
      final double y = MathUtils.clamp(viewport[1] + 1, viewport[3] - 1, screenXyz.y);
      final double z = MathUtils.clamp(0.0001, 0.9999, screenXyz.z);
      
//      final Matrix4d mv = new Matrix4d(modelview);
//      final Matrix4d proj = new Matrix4d(projection);
//
//      System.out.println("\nmodelview: " + Matrix4d.isSingular(mv));
//      mv.print("%.3f");
//      System.out.println("proj: " + Matrix4d.isSingular(proj));
//      proj.print("%.3f");
//      System.out.println("viewport: " + Arrays.toString(viewport));
//      System.out.println("screen xyz: " + screenXyz);

      if (CameraUtils.GLU_CONTEXT.gluUnProject(x, y, z, modelview, 0, projection, 0, viewport, 0, worldPos, 0)) {
         final Tuple3d value = new Tuple3d(worldPos);
         value.add(scene.getOrigin());
         return value;
      } else {
         //         new RuntimeException("invalid gluUnProject\n\tscreen = " + screenXyz + "\n\tmv = " + Arrays.toString(modelview) + "\n\tproj = " + Arrays.toString(projection) + "\n\tviewport: " + Arrays.toString(viewport)).printStackTrace();
         System.err.println("gluUnProject invalid");
         return null;
      }
   }
}
