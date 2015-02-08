package com.stephenwranger.graphics.math;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

public class CameraUtils {
   
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
   public static Tuple3d getScreenCoordinates(final GL2 gl, final GLU glu, final Tuple3d world) {
      final double[] proj = new double[16];
      final double[] modelview = new double[16];
      final int[] viewport = new int[4];
      final double[] winPos = new double[3];
      
      gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, proj, 0);
      gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
      gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
      
      /*
       * double objX, double objY, double objZ, double[] model, int model_offset, double[] proj, int proj_offset, int[] view, int view_offset, double[] winPos,
       * int winPos_offset
       */
      glu.gluProject(world.x, world.y, world.z, modelview, 0, proj, 0, viewport, 0, winPos, 0);
      
      return new Tuple3d(winPos[0], winPos[1], winPos[2]);
   }
   
   /**
    * http://www.opengl.org/sdk/docs/man2/xhtml/gluUnProject.xml
    * 
    * @param gl
    * @param glu
    * @param screen
    *           screen coordinates as x,y in screen space and z being normalized depth
    * @return x,y,z cartesian world coordinates
    */
   public static Tuple3d getWorldCoordinates(final GL2 gl, final GLU glu, final Tuple3d screen) {
      final double[] proj = new double[16];
      final double[] modelview = new double[16];
      final int[] viewport = new int[4];
      final double[] worldPos = new double[3];
      
      gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, proj, 0);
      gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
      gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
      
      glu.gluUnProject(screen.x, screen.y, screen.z, modelview, 0, proj, 0, viewport, 0, worldPos, 0);
      
      return new Tuple3d(worldPos[0], worldPos[1], worldPos[2]);
   }
}
