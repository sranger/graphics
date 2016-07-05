package com.stephenwranger.graphics.utils.buffers;

import com.jogamp.opengl.GL2;

public class AttributeRegion extends BufferRegion {
   private final int attributeLocation;
   
   public AttributeRegion(final int attributeLocation, final int componentCount, final DataType type) {
      super(componentCount, type);
      
      this.attributeLocation = attributeLocation;
   }

   @Override
   public void enable(final GL2 gl) {
      gl.glEnableVertexAttribArray(this.attributeLocation);
      gl.glVertexAttribPointer(this.attributeLocation, this.componentCount, this.dataType.glType, false, stride, offset);
   }

   @Override
   public void disable(final GL2 gl) {
      gl.glDisableVertexAttribArray(this.attributeLocation);
   }

}
