package com.stephenwranger.graphics.renderables;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import com.stephenwranger.graphics.Scene;

public interface RenderableOrthographic {
   public void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, final Scene scene);
}
