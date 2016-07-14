package com.stephenwranger.graphics.utils.shader;

import com.jogamp.opengl.GL2;

public enum ShaderStage {
   VERTEX(GL2.GL_VERTEX_SHADER), FRAGMENT(GL2.GL_FRAGMENT_SHADER);
   
   public final int type;
   
   private ShaderStage(final int glType) {
      this.type = glType;
   }
}
