package com.stephenwranger.graphics.utils.buffers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidParameterException;

import com.jogamp.opengl.GL2;

public class VertexBufferObject {
   public final int vertexCount;
   private int bytesPerVertex;
   private final boolean interleaved;
   private final int glType;
   private final BufferRegion[] regions;
   private int vboId = -1;
   
   private ByteBuffer buffer;

   public VertexBufferObject(final int vertexCount, final boolean interleaved, final int glType, final BufferRegion... regions) {
      if (regions == null || regions.length == 0) {
         throw new NullPointerException("Regions cannot be null or of zero length.");
      }

      if (vertexCount <= 0) {
         throw new InvalidParameterException("Vertex Count must be greater than zero.");
      }

      this.vertexCount = vertexCount;
      this.interleaved = interleaved;
      this.glType = glType;
      this.regions = regions.clone();

      bytesPerVertex = 0;

      for (final BufferRegion region : regions) {
         bytesPerVertex += (region.componentCount * region.dataType.bytesPerComponent);
      }
      
      int offset = 0;
      
      for(final BufferRegion region : regions) {
         if(this.interleaved) {
            region.setLocation(bytesPerVertex, offset);
            offset += (region.componentCount * region.dataType.bytesPerComponent);
         } else {
            region.setLocation(0, offset);
            offset += (bytesPerVertex * vertexCount);
         }
      }
   }

   public void render(final GL2 gl) {
      gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, vboId );
      
      for(final BufferRegion region : regions) {
         region.enable(gl);
      }
      
      gl.glDrawArrays( glType, 0, vertexCount );
       
      // disable arrays once we're done
      gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, 0 );

      for(final BufferRegion region : regions) {
         region.disable(gl);
      }
   }
   
   public ByteBuffer mapBuffer(final GL2 gl) {
      if(vboId < 0) {
         initializeVbo(gl);
      }
      
      gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboId);
      buffer = gl.glMapBuffer(GL2.GL_ARRAY_BUFFER, GL2.GL_WRITE_ONLY).order(ByteOrder.nativeOrder());
      
      return buffer;
   }
   
   public void unmapBuffer(final GL2 gl) {
      if(vboId >= 0) {
         gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
         buffer = null;
      }
   }

   private void initializeVbo(final GL2 gl) {
      final int[] ids = new int[1];
      
      // Generate And Bind The Vertex Buffer
      gl.glGenBuffers(1, ids, 0); // Get A Valid Name
      vboId = ids[0];
      
      // bind current id to memory
      gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboId);
      
      // Load The Data
      gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * bytesPerVertex, null, GL2.GL_STATIC_DRAW);
   }
}
