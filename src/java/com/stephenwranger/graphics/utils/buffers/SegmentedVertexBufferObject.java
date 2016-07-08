package com.stephenwranger.graphics.utils.buffers;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import com.jogamp.opengl.GL2;
import com.stephenwranger.graphics.utils.Timings;

/**
 * This buffer splits itself into a number of segments and keeps track of which segments are currently in use.
 * 
 * @author rangers
 *
 */
public class SegmentedVertexBufferObject extends VertexBufferObject {
   private final Set<Integer> usedIndices;
   private final Map<Integer, Integer> vertexCounts;
   private final int maxSegmentSize;
   private final int segmentsPerBuffer;
   private final Timings timings = new Timings(100);
   
   public SegmentedVertexBufferObject(final int maxSegmentSize, final int segmentsPerBuffer, final int glPrimitiveType, final int usage, final BufferRegion...bufferRegions) {
      super(maxSegmentSize * segmentsPerBuffer, true, glPrimitiveType, usage, bufferRegions);
      
      this.maxSegmentSize = maxSegmentSize;
      this.segmentsPerBuffer = segmentsPerBuffer;
      this.usedIndices = new ConcurrentSkipListSet<>();
      this.vertexCounts = new HashMap<Integer, Integer>();
   }
   
   public void setSegmentObject(final GL2 gl, final SegmentObject segment) {
      final int bufferIndex = segment.getBufferIndex() % this.segmentsPerBuffer;
      
      if(bufferIndex > -1) {
         timings.start("map");
         final ByteBuffer buffer = this.mapBuffer(gl);
         timings.end("map");

         timings.start("position");
         buffer.position(this.maxSegmentSize * bufferIndex);
         timings.end("position");
         
         timings.start("load");
         segment.loadBuffer(buffer);
         timings.end("load");

         timings.start("counts");
         this.vertexCounts.put(bufferIndex, segment.getVertexCount());
         timings.end("counts");

         timings.start("unmap");
         this.unmapBuffer(gl);
         timings.end("unmap");
      }
      
//      System.out.println("\n" + timings);
   }
   
   /**
    * Returns the next available segment index or -1 if this buffer is full. If not full, the returned index will be 
    * marked as used.
    * 
    * @return the next avialable index or -1 if full
    */
   public int getNextAvailableIndex() {
      for(int i = 0; i < this.segmentsPerBuffer; i++) {
         if(!this.usedIndices.contains(i)) {
            this.usedIndices.add(i);
            return i;
         }
      }
      
      return -1;
   }
   
   public void clearIndex(final int bufferIndex) {
      this.usedIndices.remove(bufferIndex);
      this.vertexCounts.remove(bufferIndex);
   }
   
   @Override
   public void render(final GL2 gl) {
      gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, vboId );
      
      for(final BufferRegion region : regions) {
         region.enable(gl);
      }
      
      for(final Entry<Integer, Integer> entry : this.vertexCounts.entrySet()) {
         final int index = entry.getKey() * this.maxSegmentSize;
         final int vertexCount = entry.getValue();
         gl.glDrawArrays( glPrimitiveType, index, vertexCount );
      }
       
      // disable arrays once we're done
      gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, 0 );

      for(final BufferRegion region : regions) {
         region.disable(gl);
      }
   }
   
   public void render(final GL2 gl, final Collection<SegmentObject> segments) {
      gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, vboId );
      
      for(final BufferRegion region : regions) {
         region.enable(gl);
      }
      
      for(final SegmentObject segment : segments) {
         final int index = segment.getBufferIndex() % this.segmentsPerBuffer * this.maxSegmentSize;
         final int vertexCount = segment.getVertexCount();
         gl.glDrawArrays( glPrimitiveType, index, vertexCount );
      }
       
      // disable arrays once we're done
      gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, 0 );

      for(final BufferRegion region : regions) {
         region.disable(gl);
      }
   }
}
