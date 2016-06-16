package com.stephenwranger.graphics.math;

import javax.media.opengl.glu.GLU;

import com.stephenwranger.graphics.Scene;

public class CameraUtils {
   private static final GLU GLU_CONTEXT = new GLU();
   
   private CameraUtils() {
      // statics only
   }
   
   /**
    * http://www.opengl.org/sdk/docs/man2/xhtml/gluProject.xml
    * 
    * @param gl
    * @param glu
    * @param world
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
}
