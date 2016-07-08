package com.stephenwranger.graphics.utils.buffers;

import java.nio.ByteBuffer;

public class BufferUtils {
   private BufferUtils() {
      // static only
   }
   
   public static ByteBuffer newByteBuffer(final int numElements) {
      try {

         return com.jogamp.opengl.util.GLBuffers.newDirectByteBuffer(numElements);
      } catch (final OutOfMemoryError oome) {
         System.gc();
         Thread.yield();
      }
      return com.jogamp.opengl.util.GLBuffers.newDirectByteBuffer(numElements);
   }
}
