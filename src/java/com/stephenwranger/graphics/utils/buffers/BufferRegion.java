package com.stephenwranger.graphics.utils.buffers;

import com.jogamp.opengl.GL2;


public abstract class BufferRegion {
   public final int componentCount;
   public final DataType dataType;
   protected int stride = 0;
   protected int offset = 0;

   public BufferRegion(final int componentCount, final DataType type) {
      this.componentCount = componentCount;
      this.dataType = type;
   }
   
   public void setLocation(final int stride, final int offset) {
      this.stride = stride;
      this.offset = offset;
   }

   public abstract void enable(final GL2 gl);
   
   public abstract void disable(final GL2 gl);
}
