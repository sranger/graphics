package com.stephenwranger.graphics.utils.buffers;

import javax.media.opengl.GL2;

public class VertexRegion extends BufferRegion {

   public VertexRegion(final int componentCount, final DataType type) {
      super(componentCount, type);
   }

   @Override
   public void enable(final GL2 gl) {
      gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
      gl.glVertexPointer(componentCount, this.dataType.glType, stride, offset);
   }

   @Override
   public void disable(final GL2 gl) {
      gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
   }
}
