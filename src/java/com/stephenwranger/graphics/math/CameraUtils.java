package com.stephenwranger.graphics.math;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.math.intersection.Plane;

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
      final double near   =  (1+m.get(2, 3))/m.get(2, 2);
      final double far    = -(1-m.get(2, 3))/m.get(2, 2);
      final double bottom =  (1-m.get(1, 3))/m.get(1, 1);
      final double top    = -(1+m.get(1, 3))/m.get(1, 1);
      final double left   = -(1+m.get(0, 3))/m.get(0, 0);
      final double right  =  (1-m.get(0, 3))/m.get(0, 0);

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
      final double near   = m.get(2, 3) / (m.get(2, 2) - 1);
      final double far    = m.get(2, 3) / (m.get(2, 2) + 1);
      final double bottom = near * (m.get(1, 2) - 1) / m.get(1, 1);
      final double top    = near * (m.get(1, 2) + 1) / m.get(1, 1);
      final double left   = near * (m.get(0, 2) - 1) / m.get(0, 0);
      final double right  = near * (m.get(0, 2) + 1) / m.get(0, 0);

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
    * @param matrix
    * @return
    */
   public static Plane[] getFrustumPlanes(final Matrix4d matrix) {
      final Plane[] planes = new Plane[6];
      // left
      final Vector3d leftNormal = new Vector3d();
      leftNormal.x = matrix.get(0, 3) + matrix.get(0, 0);
      leftNormal.y = matrix.get(1, 3) + matrix.get(1, 0);
      leftNormal.z = matrix.get(2, 3) + matrix.get(2, 0);
      final double leftD = matrix.get(3, 3) + matrix.get(3, 0);

      // right
      final Vector3d rightNormal = new Vector3d();
      rightNormal.x = matrix.get(0, 3) - matrix.get(0, 0);
      rightNormal.y = matrix.get(1, 3) - matrix.get(1, 0);
      rightNormal.z = matrix.get(2, 3) - matrix.get(2, 0);
      final double rightD = matrix.get(3, 3) - matrix.get(3, 0);

      // bottom
      final Vector3d bottomNormal = new Vector3d();
      bottomNormal.x = matrix.get(0, 3) + matrix.get(0, 1);
      bottomNormal.y = matrix.get(1, 3) + matrix.get(1, 1);
      bottomNormal.z = matrix.get(2, 3) + matrix.get(2, 1);
      final double bottomD = matrix.get(3, 3) + matrix.get(3, 1);

      // top
      final Vector3d topNormal = new Vector3d();
      topNormal.x = matrix.get(0, 3) - matrix.get(0, 1);
      topNormal.y = matrix.get(1, 3) - matrix.get(1, 1);
      topNormal.z = matrix.get(2, 3) - matrix.get(2, 1);
      final double topD = matrix.get(3, 3) - matrix.get(3, 1);

      // near
      final Vector3d nearNormal = new Vector3d();
      nearNormal.x = matrix.get(0, 2);
      nearNormal.y = matrix.get(1, 2);
      nearNormal.z = matrix.get(2, 2);
      final double nearD = matrix.get(3, 2);

      // far
      final Vector3d farNormal = new Vector3d();
      farNormal.x = matrix.get(0, 3) - matrix.get(0, 2);
      farNormal.y = matrix.get(1, 3) - matrix.get(1, 2);
      farNormal.z = matrix.get(2, 3) - matrix.get(2, 2);
      final double farD = matrix.get(3, 3) - matrix.get(3, 2);

      planes[CameraUtils.LEFT_PLANE] = new Plane(leftNormal, leftD);
      planes[CameraUtils.RIGHT_PLANE] = new Plane(rightNormal, rightD);
      planes[CameraUtils.BOTTOM_PLANE] = new Plane(bottomNormal, bottomD);
      planes[CameraUtils.TOP_PLANE] = new Plane(topNormal, topD);
      planes[CameraUtils.NEAR_PLANE] = new Plane(nearNormal, nearD);
      planes[CameraUtils.FAR_PLANE] = new Plane(farNormal, farD);

      // normalize
      for (int i = 0; i < 6; i++) {
         final Vector3d normal = planes[i].getNormal();
         final double length = normal.length();
         normal.x /= length;
         normal.y /= length;
         normal.z /= length;
         // d also has to be divided by the length of the normal
         planes[i] = new Plane(normal, planes[i].getDistance() / length);
      }

      return planes;
   }

   //   /**
   //    * Generates the six frustum planes using the projection matrix. The index constants can be accessed from this class
   //    * as static values.<br/>
   //    * <br/>
   //    * http://ruh.li/CameraViewFrustum.html<br/>
   //    * http://gamedevs.org/uploads/fast-extraction-viewing-frustum-planes-from-world-view-projection-matrix.pdf
   //    *
   //    * @param projection
   //    * @return
   //    */
   //   public static Plane[] getFrustumPlanes(final Matrix4d projection) {
   //      final Plane[] planes = new Plane[6];
   //      // left
   //      final Vector3d leftNormal = new Vector3d();
   //      leftNormal.x = projection.get(3, 0) + projection.get(0, 0);
   //      leftNormal.y = projection.get(3, 1) + projection.get(0, 1);
   //      leftNormal.z = projection.get(3, 2) + projection.get(0, 2);
   //      final double leftD = projection.get(3, 3) + projection.get(0, 3);
   //
   //      // right
   //      final Vector3d rightNormal = new Vector3d();
   //      rightNormal.x = projection.get(3, 0) - projection.get(0, 0);
   //      rightNormal.y = projection.get(3, 1) - projection.get(0, 1);
   //      rightNormal.z = projection.get(3, 2) - projection.get(0, 2);
   //      final double rightD = projection.get(3, 3) - projection.get(0, 3);
   //
   //      // bottom
   //      final Vector3d bottomNormal = new Vector3d();
   //      bottomNormal.x = projection.get(3, 0) + projection.get(1, 0);
   //      bottomNormal.y = projection.get(3, 1) + projection.get(1, 1);
   //      bottomNormal.z = projection.get(3, 2) + projection.get(1, 2);
   //      final double bottomD = projection.get(3, 3) + projection.get(1, 3);
   //
   //      // top
   //      final Vector3d topNormal = new Vector3d();
   //      topNormal.x = projection.get(3, 0) - projection.get(1, 0);
   //      topNormal.y = projection.get(3, 1) - projection.get(1, 1);
   //      topNormal.z = projection.get(3, 2) - projection.get(1, 2);
   //      final double topD = projection.get(3, 3) - projection.get(1, 3);
   //
   //      // near
   //      final Vector3d nearNormal = new Vector3d();
   //      nearNormal.x = projection.get(3, 0) + projection.get(2, 0);
   //      nearNormal.y = projection.get(3, 1) + projection.get(2, 1);
   //      nearNormal.z = projection.get(3, 2) + projection.get(2, 2);
   //      final double nearD = projection.get(3, 3) + projection.get(2, 3);
   //
   //      // far
   //      final Vector3d farNormal = new Vector3d();
   //      farNormal.x = projection.get(3, 0) - projection.get(2, 0);
   //      farNormal.y = projection.get(3, 1) - projection.get(2, 1);
   //      farNormal.z = projection.get(3, 2) - projection.get(2, 2);
   //      final double farD = projection.get(3, 3) - projection.get(2, 3);
   //
   //      planes[CameraUtils.LEFT_PLANE] = new Plane(leftNormal, leftD);
   //      planes[CameraUtils.RIGHT_PLANE] = new Plane(rightNormal, rightD);
   //      planes[CameraUtils.BOTTOM_PLANE] = new Plane(bottomNormal, bottomD);
   //      planes[CameraUtils.TOP_PLANE] = new Plane(topNormal, topD);
   //      planes[CameraUtils.NEAR_PLANE] = new Plane(nearNormal, nearD);
   //      planes[CameraUtils.FAR_PLANE] = new Plane(farNormal, farD);
   //
   //      // normalize
   //      for (int i = 0; i < 6; i++) {
   //         final Vector3d normal = planes[i].getNormal();
   //         final double length = normal.length();
   //         normal.x /= length;
   //         normal.y /= length;
   //         normal.z /= length;
   //         // d also has to be divided by the length of the normal
   //         planes[i] = new Plane(normal, planes[i].getDistance() / length);
   //      }
   //
   //      return planes;
   //   }

   // private static final JFrame errorFrame = new JFrame("Error Image");
   // private static final JLabel label = new JLabel();
   // private static boolean isVisible = false;
   //
   // static {
   // errorFrame.setLocation(3940, 100);
   // errorFrame.getContentPane().add(label);
   // }

   /**
    * Computes the frustum matrix.
    *
    * @param left
    * @param right
    * @param bottom
    * @param top
    * @param znear
    * @param zfar
    * @return
    */
   public static double[] glhFrustum2(final double left, final double right, final double bottom, final double top, final double znear, final double zfar) {
      final double temp = 2.0 * znear;
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

      // return new Matrix4d(matrix);
      return matrix;
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
      gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
      gl.glLoadIdentity();
      CameraUtils.GLU_CONTEXT.gluLookAt(eyePosition3D.x, eyePosition3D.y, eyePosition3D.z, center3D.x, center3D.y, center3D.z, upVector3D.x, upVector3D.y, upVector3D.z);

      final double[] modelview = new double[16];
      gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, modelview, 0);

      return modelview;

      // //------------------
      // final Vector3d forward = new Vector3d();
      // forward.subtract(center3D, eyePosition3D);
      // forward.normalize();
      // //------------------
      // //Side = forward x up
      // final Vector3d side = new Vector3d();
      // side.cross(forward, upVector3D);
      // side.normalize();
      // //------------------
      // //Recompute up as: up = side x forward
      // final Vector3d up = new Vector3d();
      // up.cross(side, forward);
      // up.normalize();
      // //------------------
      // final double[] matrix = new double[16];
      // matrix[0] = side.x;
      // matrix[4] = side.y;
      // matrix[8] = side.z;
      // matrix[12] = 0.0;
      // //------------------
      // matrix[1] = up.x;
      // matrix[5] = up.y;
      // matrix[9] = up.z;
      // matrix[13] = 0.0;
      // //------------------
      // matrix[2] = -forward.x;
      // matrix[6] = -forward.y;
      // matrix[10] = -forward.z;
      // matrix[14] = 0.0;
      // //------------------
      // matrix[3] = matrix[7] = matrix[11] = 0.0;
      // matrix[15] = 1.0;
      // //------------------
      // CameraUtils.glhTranslatef2(matrix, -eyePosition3D.x, -eyePosition3D.y, -eyePosition3D.z);
      // //------------------
      //// return new Matrix4d(matrix);
      // return matrix;
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
      gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
      gl.glLoadIdentity();
      CameraUtils.GLU_CONTEXT.gluPerspective(fovyInDegrees, aspectRatio, znear, zfar);

      final double[] projection = new double[16];
      gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projection, 0);

      return projection;

      // final double ymax = znear * Math.tan(fovyInDegrees * Math.PI / 360.0);
      // final double xmax = ymax * aspectRatio;
      //
      // return glhFrustum2(-xmax, xmax, -ymax, ymax, znear, zfar);
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
      final double[] modelview = scene.getModelViewMatrix();
      final double[] projection = scene.getProjectionMatrix();
      final int[] viewport = scene.getViewport();
      final double[] winPos = new double[3];

      if (CameraUtils.GLU_CONTEXT.gluProject(worldXyz.x, worldXyz.y, worldXyz.z, modelview, 0, projection, 0, viewport, 0, winPos, 0)) {
         return new Tuple3d(winPos);
      } else {
         return null;
      }

      // // Transformation vectors
      // double[] fTempo = new double[8];
      // // Modelview transform
      // fTempo[0] = modelview[0] * worldXyz.x + modelview[4] * worldXyz.y + modelview[8] * worldXyz.z + modelview[12];
      // // w is always 1
      // fTempo[1] = modelview[1] * worldXyz.x + modelview[5] * worldXyz.y + modelview[9] * worldXyz.z + modelview[13];
      // fTempo[2] = modelview[2] * worldXyz.x + modelview[6] * worldXyz.y + modelview[10] * worldXyz.z + modelview[14];
      // fTempo[3] = modelview[3] * worldXyz.x + modelview[7] * worldXyz.y + modelview[11] * worldXyz.z + modelview[15];
      // // Projection transform, the final row of projection matrix is always [0 0 -1 0]
      // // so we optimize for that.
      // fTempo[4] = projection[0] * fTempo[0] + projection[4] * fTempo[1] + projection[8] * fTempo[2]
      // + projection[12] * fTempo[3];
      // fTempo[5] = projection[1] * fTempo[0] + projection[5] * fTempo[1] + projection[9] * fTempo[2]
      // + projection[13] * fTempo[3];
      // fTempo[6] = projection[2] * fTempo[0] + projection[6] * fTempo[1] + projection[10] * fTempo[2]
      // + projection[14] * fTempo[3];
      // fTempo[7] = -fTempo[2];
      // // The result normalizes between -1 and 1
      // if (fTempo[7] == 0.0) // The w value
      // return null;
      // fTempo[7] = 1.0 / fTempo[7];
      // // Perspective division
      // fTempo[4] *= fTempo[7];
      // fTempo[5] *= fTempo[7];
      // fTempo[6] *= fTempo[7];
      // // Window coordinates
      // // Map x, y to range 0-1
      // final double windowX = (fTempo[4] * 0.5 + 0.5) * viewport[2] + viewport[0];
      // final double windowY = (fTempo[5] * 0.5 + 0.5) * viewport[3] + viewport[1];
      // // This is only correct when glDepthRange(0.0, 1.0)
      // final double windowZ = (1.0 + fTempo[6]) * 0.5; // Between 0 and 1
      // return new Tuple3d(windowX, windowY, windowZ);
   }

   // /**
   // * Generates the six frustum planes using the projection matrix. The index constants can be accessed from this
   // class
   // * as static values.<br/><br/>
   // * http://ruh.li/CameraViewFrustum.html
   // *
   // * @param projection
   // * @return
   // */
   // public static Plane[] getFrustumPlanes(final Matrix4d projection) {
   // final Plane[] planes = new Plane[6];
   // // left
   // final Vector3d leftNormal = new Vector3d();
   // leftNormal.x = projection.get(0, 3) + projection.get(0, 0);
   // leftNormal.y = projection.get(1, 3) + projection.get(1, 0);
   // leftNormal.z = projection.get(2, 3) + projection.get(2, 0);
   // final double leftD = projection.get(3, 3) + projection.get(3, 0);
   //
   // // right
   // final Vector3d rightNormal = new Vector3d();
   // rightNormal.x = projection.get(0, 3) - projection.get(0, 0);
   // rightNormal.y = projection.get(1, 3) - projection.get(1, 0);
   // rightNormal.z = projection.get(2, 3) - projection.get(2, 0);
   // final double rightD = projection.get(3, 3) - projection.get(3, 0);
   //
   // // bottom
   // final Vector3d bottomNormal = new Vector3d();
   // bottomNormal.x = projection.get(0, 3) + projection.get(0, 1);
   // bottomNormal.y = projection.get(1, 3) + projection.get(1, 1);
   // bottomNormal.z = projection.get(2, 3) + projection.get(2, 1);
   // final double bottomD = projection.get(3, 3) + projection.get(3, 1);
   //
   // // top
   // final Vector3d topNormal = new Vector3d();
   // topNormal.x = projection.get(0, 3) - projection.get(0, 1);
   // topNormal.y = projection.get(1, 3) - projection.get(1, 1);
   // topNormal.z = projection.get(2, 3) - projection.get(2, 1);
   // final double topD = projection.get(3, 3) - projection.get(3, 1);
   //
   // // near
   // final Vector3d nearNormal = new Vector3d();
   //// nearNormal.x = projection.get(0, 3) + projection.get(0, 2);
   //// nearNormal.y = projection.get(1, 3) + projection.get(1, 2);
   //// nearNormal.z = projection.get(2, 3) + projection.get(2, 2);
   // nearNormal.x = projection.get(0, 2);
   // nearNormal.y = projection.get(1, 2);
   // nearNormal.z = projection.get(2, 2);
   // final double nearD = projection.get(3, 2);
   //
   // // far
   // final Vector3d farNormal = new Vector3d();
   // farNormal.x = projection.get(0, 3) - projection.get(0, 2);
   // farNormal.y = projection.get(1, 3) - projection.get(1, 2);
   // farNormal.z = projection.get(2, 3) - projection.get(2, 2);
   // final double farD = projection.get(3, 3) - projection.get(3, 2);
   //
   // planes[LEFT_PLANE] = new Plane(leftNormal, leftD);
   // planes[RIGHT_PLANE] = new Plane(rightNormal, rightD);
   // planes[BOTTOM_PLANE] = new Plane(bottomNormal, bottomD);
   // planes[TOP_PLANE] = new Plane(topNormal, topD);
   // planes[NEAR_PLANE] = new Plane(nearNormal, nearD);
   // planes[FAR_PLANE] = new Plane(farNormal, farD);
   //
   // // normalize
   // for(int i = 0; i < 6; i++) {
   // final Vector3d normal = planes[i].getNormal();
   // final double length = normal.length();
   // normal.x /= length;
   // normal.y /= length;
   // normal.z /= length;
   // // d also has to be divided by the length of the normal
   // planes[i] = new Plane(normal, planes[i].getDistance() / length);
   // }
   //
   // return planes;
   // }

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

      // final double near = scene.getNear();
      // final double far = scene.getFar();
      // final double range = near / far;
      final double scaledDistance = screenXyz.z;// * range + near;

      if (CameraUtils.GLU_CONTEXT.gluUnProject(screenXyz.x, screenXyz.y, scaledDistance, modelview, 0, projection, 0, viewport, 0, worldPos, 0)) {
         return new Tuple3d(worldPos);
      } else {
         // System.err.println("cannot unproject: " + screenXyz);
         // System.err.println("mv: " + Arrays.toString(modelview));
         // System.err.println("p: " + Arrays.toString(projection));
         //
         // Matrix4d.invert(modelview);
         // Matrix4d.invert(projection);
         //
         // System.err.println("inv mv: " + Arrays.toString(modelview));
         // System.err.println("inv proj: " + Arrays.toString(projection));
         //
         //
         // if(!isVisible) {
         // isVisible = true;
         // final BufferedImage image = new BufferedImage(scene.getWidth(), scene.getHeight(),
         // BufferedImage.TYPE_INT_ARGB);
         //
         // for(int x = 0; x < scene.getWidth(); x++) {
         // for(int y = 0; y < scene.getHeight(); y++) {
         // final boolean valid = GLU_CONTEXT.gluUnProject(x, y, 0.5, modelview, 0, projection, 0, viewport, 0,
         // worldPos, 0);
         // final Color color = (valid) ? Color.white : Color.red;
         // image.setRGB(x, y, color.getRGB());
         // }
         // }
         //
         // label.setIcon(new ImageIcon(image));
         // SwingUtilities.invokeLater(() -> {
         // errorFrame.pack();
         // errorFrame.setVisible(true);
         // });
         // }

         return null;
      }

      // // Transformation matrices
      // double[] in = new double[4];
      // // Calculation for inverting a matrix, compute projection x modelview
      // // and store in A[16]
      // final Matrix4d proj = new Matrix4d(projection);
      // final Matrix4d mv = new Matrix4d(modelview);
      // proj.multiply(mv);
      // // Now compute the inverse of matrix A
      // if (Matrix4d.isSingular(proj)) {
      // return null;
      // }
      //
      // // Transformation of normalized coordinates between -1 and 1
      // in[0] = (screenXyz.x - (float) viewport[0]) / (float) viewport[2] * 2.0 - 1.0;
      // in[1] = (screenXyz.y - (float) viewport[1]) / (float) viewport[3] * 2.0 - 1.0;
      // in[2] = 2.0 * screenXyz.z - 1.0;
      // in[3] = 1.0;
      // // Objects coordinates
      // final Matrix4d matrix = new Matrix4d(proj);
      // matrix.invert();
      // final double[] out = matrix.multiply(in);
      // if (out[3] == 0.0)
      // return null;
      // out[3] = 1.0 / out[3];
      // final double[] objectCoordinate = new double[3];
      // objectCoordinate[0] = out[0] * out[3];
      // objectCoordinate[1] = out[1] * out[3];
      // objectCoordinate[2] = out[2] * out[3];
      // return new Tuple3d(objectCoordinate);
   }

   // private static void glhTranslatef2(final double[] matrix, final double x, final double y, final double z) {
   // matrix[12] = matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12];
   // matrix[13] = matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13];
   // matrix[14] = matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14];
   // matrix[15] = matrix[3] * x + matrix[7] * y + matrix[11]*z+matrix[15];
   // }
}
