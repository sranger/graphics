package com.stephenwranger.graphics.utils.buffers;

import javax.media.opengl.GL2;

public enum DataType {
   BYTE(1,GL2.GL_BYTE),
   SHORT(2, GL2.GL_SHORT),
   INT(4, GL2.GL_INT),
   FLOAT(4, GL2.GL_FLOAT),
   DOUBLE(8, GL2.GL_DOUBLE);
   
   public final int bytesPerComponent;
   public final int glType;
   
   DataType(final int bytesPerComponent, final int glType) {
      this.bytesPerComponent = bytesPerComponent;
      this.glType = glType;
   }
}
