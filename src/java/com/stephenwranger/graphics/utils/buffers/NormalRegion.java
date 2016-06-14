package com.stephenwranger.graphics.utils.buffers;

import javax.media.opengl.GL2;

public class NormalRegion extends BufferRegion {

   public NormalRegion(final DataType type) {
      super(3, type);
   }

   @Override
   public void enable(final GL2 gl) {
      gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
      gl.glNormalPointer(this.dataType.glType, stride, offset);
   }

   @Override
   public void disable(final GL2 gl) {
      gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
   }
}