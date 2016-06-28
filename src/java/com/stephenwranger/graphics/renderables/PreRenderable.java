package com.stephenwranger.graphics.renderables;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.glu.GLU;
import com.stephenwranger.graphics.Scene;

public interface PreRenderable {
   public void preRender(final GL2 gl, final GLU glu, final GLDrawable drawable, final Scene scene);
}
