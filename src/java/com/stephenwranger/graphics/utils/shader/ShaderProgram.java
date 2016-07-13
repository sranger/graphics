package com.stephenwranger.graphics.utils.shader;

import com.jogamp.opengl.GL2;

public class ShaderProgram {
   private final ShaderKernel[] kernels;
   private int id = -1;
   
   public ShaderProgram(final ShaderKernel... kernels) {
      this.kernels = new ShaderKernel[kernels.length];
      
      System.arraycopy(kernels, 0, this.kernels, 0, kernels.length);
   }
   
   public void enable(final GL2 gl) {
      if(this.id == -1) {
         initialize(gl);
      }
   }
   
   public void disable(final GL2 gl) {
      
   }
   
   private void initialize(final GL2 gl) {
      
   }
}
