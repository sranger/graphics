package com.stephenwranger.graphics.utils.buffers;

import java.nio.ByteBuffer;

public interface SegmentObject {
   /**
    * Will store the segmented buffer pool key as the pool index and the buffer index where it has
    * been stored.
    * 
    * @param poolIndex the segmented buffer key
    * @param bufferIndex the buffer index
    */
   public void setSegmentLocation(final int poolIndex, final int bufferIndex);
   
   /**
    * Returns the segment pool index currently set or -1 if no index has been set.
    * 
    * @return the segment pool index
    */
   public int getSegmentPoolIndex();
   
   /**
    * Returns the buffer index currently set or -1 if no index has been set.
    * 
    * @return the buffer index
    */
   public int getBufferIndex();
   
   /**
    * Puts the data to be stored into the given ByteBuffer; the number of points needs to be less than or equal to the
    * max segment size defined when the segmented buffer pool was created.
    * 
    * @return
    */
   public void loadBuffer(final ByteBuffer buffer);
   
   /**
    * Returns the number of vertices stored in this object's buffer.
    * 
    * @return the vertex count
    */
   public int getVertexCount();
}
