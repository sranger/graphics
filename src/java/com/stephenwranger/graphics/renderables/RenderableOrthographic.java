package com.stephenwranger.graphics.renderables;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

import com.stephenwranger.graphics.Scene;

public interface RenderableOrthographic {
   public void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, final Scene scene);
}
