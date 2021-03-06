package com.stephenwranger.graphics.utils.buffers;

import java.nio.ByteBuffer;

import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.utils.textures.Texture2d;

public interface SegmentObject {
   /**
    * Returns the buffer index currently set or -1 if no index has been set.
    *
    * @return the buffer index
    */
   public int getBufferIndex();

   /**
    * Returns the segment pool index currently set or -1 if no index has been set.
    *
    * @return the segment pool index
    */
   public int getSegmentPoolIndex();

   /**
    * Returns the custom {@link Texture2d} that should be enabled when this segment is rendered.
    * 
    * @return
    */
   public Texture2d getTexture();

   /**
    * Returns the number of vertices stored in this object's buffer.
    *
    * @return the vertex count
    */
   public int getVertexCount();

   /**
    * Puts the data to be stored into the given ByteBuffer; the number of points needs to be less than or equal to the
    * max segment size defined when the segmented buffer pool was created.
    *
    * @return
    */
   public void loadBuffer(final Tuple3d origin, final ByteBuffer buffer);

   /**
    * Will store the segmented buffer pool key as the pool index and the buffer index where it has
    * been stored.
    *
    * @param poolIndex
    *           the segmented buffer key
    * @param bufferIndex
    *           the buffer index
    */
   public void setSegmentLocation(final int poolIndex, final int bufferIndex);
}
