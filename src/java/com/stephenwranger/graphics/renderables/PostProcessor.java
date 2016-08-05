package com.stephenwranger.graphics.renderables;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.stephenwranger.graphics.Scene;

public interface PostProcessor {
   /**
    * Is called at the end of a draw pass.
    * 
    * @param gl current OpenGL context
    * @param glu GLU context
    * @param scene the current scene
    */
   public void process(final GL2 gl, final GLU glu, final Scene scene);
}
