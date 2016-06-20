package com.stephenwranger.graphics.math;

import javax.media.opengl.glu.GLU;

import com.stephenwranger.graphics.Scene;

/**
 * http://www.scratchapixel.com/lessons/3d-basic-rendering/perspective-and-orthographic-projection-matrix/building-basic-perspective-projection-matrix
 * 
 * @author rangers
 *
 */
public class CameraUtils {
   private static final GLU GLU_CONTEXT = new GLU();
   
   private CameraUtils() {
      // statics only
   }
   
   /**
    * http://www.opengl.org/sdk/docs/man2/xhtml/gluProject.xml
    * 
    * @param scene 
    * @param world coordinates to convert
    *           x,y,z cartesian world coordinate
    * @return screen coordinates as x,y in screen space and z being normalized depth
    */
   public static Tuple3d getScreenCoordinates(final Scene scene, final Tuple3d world) {
      final double[] proj = scene.getProjectionMatrix();
      final double[] modelview = scene.getModelViewMatrix();
      final int[] viewport = scene.getViewport();
      final double[] winPos = new double[3];
      
      /*
       * double objX, double objY, double objZ, double[] model, int model_offset, double[] proj, int proj_offset, int[] view, int view_offset, double[] winPos,
       * int winPos_offset
       */
      GLU_CONTEXT.gluProject(world.x, world.y, world.z, modelview, 0, proj, 0, viewport, 0, winPos, 0);
      
      return new Tuple3d(winPos[0], winPos[1], winPos[2]);
   }
   
   /**
    * http://www.opengl.org/sdk/docs/man2/xhtml/gluUnProject.xml
    * 
    * @param gl
    * @param GLU_CONTEXT
    * @param screen
    *           screen coordinates as x,y in screen space and z being normalized depth
    * @return x,y,z cartesian world coordinates
    */
   public static Tuple3d getWorldCoordinates(final Scene scene, final Tuple3d screen) {
      final double[] proj = scene.getProjectionMatrix();
      final double[] modelview = scene.getModelViewMatrix();
      final int[] viewport = scene.getViewport();
      final double[] worldPos = new double[3];
      
      GLU_CONTEXT.gluUnProject(screen.x, screen.y, screen.z, modelview, 0, proj, 0, viewport, 0, worldPos, 0);
      
      return new Tuple3d(worldPos[0], worldPos[1], worldPos[2]);
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
   public static Matrix4d gluPerspective(final double fovyInDegrees, final double aspectRatio, final double znear, final double zfar) {
      final double ymax = znear * Math.tan(fovyInDegrees * Math.PI / 360.0);
      final double xmax = ymax * aspectRatio;
      
      return glhFrustum2(-xmax, xmax, -ymax, ymax, znear, zfar);
   }
   
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
   public static Matrix4d glhFrustum2(final double left, final double right, final double bottom, final double top, final double znear, final double zfar) {
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
      
      return new Matrix4d(matrix);
   }
   
   /**
    * Computes the ModelView matrix; reference: https://www.opengl.org/wiki/GluLookAt_code.
    * 
    * @param eyePosition3D
    * @param center3D
    * @param upVector3D
    * @return
    */
   public static Matrix4d gluLookAt(final Tuple3d eyePosition3D, final Tuple3d center3D, final Vector3d upVector3D) {
      //------------------
      final Vector3d forward = new Vector3d();
      forward.subtract(center3D, eyePosition3D);
      forward.normalize();
      //------------------
      //Side = forward x up
      final Vector3d side = new Vector3d();
      side.cross(forward, upVector3D);
      side.normalize();
      //------------------
      //Recompute up as: up = side x forward
      final Vector3d up = new Vector3d();
      up.cross(side, forward);
      //------------------
      final double[] matrix = new double[16];
      matrix[0] = side.x;
      matrix[4] = side.y;
      matrix[8] = side.z;
      matrix[12] = 0.0;
      //------------------
      matrix[1] = up.x;
      matrix[5] = up.y;
      matrix[9] = up.z;
      matrix[13] = 0.0;
      //------------------
      matrix[2] = -forward.x;
      matrix[6] = -forward.y;
      matrix[10] = -forward.z;
      matrix[14] = 0.0;
      //------------------
      matrix[3] = matrix[7] = matrix[11] = 0.0;
      matrix[15] = 1.0;
      //------------------
      CameraUtils.glhTranslatef2(matrix, -eyePosition3D.x, -eyePosition3D.y, -eyePosition3D.z);
      //------------------
      return new Matrix4d(matrix);
   }
   
   private static void glhTranslatef2(final double[] matrix, final double x, final double y, final double z) {
      matrix[12]=matrix[0]*x+matrix[4]*y+matrix[8]*z+matrix[12];
      matrix[13]=matrix[1]*x+matrix[5]*y+matrix[9]*z+matrix[13];
      matrix[14]=matrix[2]*x+matrix[6]*y+matrix[10]*z+matrix[14];
      matrix[15]=matrix[3]*x+matrix[7]*y+matrix[11]*z+matrix[15];
   }
}
