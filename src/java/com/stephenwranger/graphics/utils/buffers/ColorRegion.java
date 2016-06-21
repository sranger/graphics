package com.stephenwranger.graphics.utils.buffers;

import com.jogamp.opengl.GL2;

public class ColorRegion extends BufferRegion {

   public ColorRegion(final int componentCount, final DataType type) {
      super(componentCount, type);
   }

   @Override
   public void enable(final GL2 gl) {
      gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
      gl.glColorPointer(componentCount, this.dataType.glType, stride, offset);
   }

   @Override
   public void disable(final GL2 gl) {
      gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
   }
}