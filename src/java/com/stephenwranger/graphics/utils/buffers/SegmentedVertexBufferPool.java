package com.stephenwranger.graphics.utils.buffers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jogamp.opengl.GL2;
import com.stephenwranger.graphics.math.Tuple3d;

/**
 * This class handles a set of {@link VertexBufferObject} such that each is split into a number of segments up to the
 * given max segment size number of vertices (size of each vertex is defined by the buffer regions) and each new buffer
 * in the pool is initialized to hold a number of segments equal to the given segments per buffer value. There will be a
 * number of buffers initialized in order to handle objects smaller than the max segment size without wasting too much
 * memory; the max size will be divided by two until the smallest segmented pool is less than 100 vertices.
 *
 * <pre>
 *    maxSegmentSize = 5000
 *    segmentsPerBuffer = 100
 *
 *    buffer[5000] = 5000 x 100 x bufferRegions.byteSize
 *    buffer[2500] = 2500 x 100 x bufferRegions.byteSize
 *    buffer[1250] = 1250 x 100 x bufferRegions.byteSize
 *    buffer[ 625] = 625  x 100 x bufferRegions.byteSize
 *    buffer[ 312] = 312  x 100 x bufferRegions.byteSize
 *    buffer[ 156] = 156  x 100 x bufferRegions.byteSize
 *    buffer[  78] = 78   x 100 x bufferRegions.byteSize
 * </pre>
 *
 * Once all segments are filled, a new buffer will be appended to that specific pool. As segments are cleared, they will
 * be reused. If the last buffer in a pool is empty, it will be deleted; however, one will always be available. When
 * adding a new SegmentObject to the pool, its location in the pool will automatically be set. If it already exists in
 * the pool, the call will be ignored.
 *
 * @author rangers
 *
 */
public class SegmentedVertexBufferPool {
   private final Map<Integer, List<SegmentedVertexBufferObject>> buffers = new HashMap<>();
   private final int                                             maxSegmentSize;
   private final int                                             segmentsPerBuffer;
   private final int                                             glPrimitiveType;
   private final int                                             usage;
   private final BufferRegion[]                                  bufferRegions;

   public SegmentedVertexBufferPool(final int maxSegmentSize, final int segmentsPerBuffer, final int glPrimitiveType, final int usage, final BufferRegion... bufferRegions) {
      this.maxSegmentSize = maxSegmentSize;
      this.segmentsPerBuffer = segmentsPerBuffer;
      this.glPrimitiveType = glPrimitiveType;
      this.usage = usage;
      this.bufferRegions = bufferRegions.clone();

      System.out.println("\nmax segment size:    " + this.maxSegmentSize);
      System.out.println("segments per buffer: " + this.segmentsPerBuffer);

      // so we can divide at start of loop; if we don't, we can't get the last pool to be less than 100
      int segmentSize = this.maxSegmentSize * 2;

      do {
         segmentSize /= 2;
         this.addBuffer(segmentSize);
      } while (segmentSize > 100);
   }

   public void clearSegmentObject(final GL2 gl, final SegmentObject segment) {
      if (segment != null) {
         final int poolIndex = segment.getSegmentPoolIndex();
         final int bufferIndex = segment.getBufferIndex();

         if ((poolIndex != -1) && (bufferIndex != -1)) {
            final SegmentedVertexBufferObject buffer = this.getBuffer(poolIndex, bufferIndex);
            final int segmentBufferIndex = bufferIndex % this.segmentsPerBuffer;
            buffer.clearIndex(segmentBufferIndex);

            this.clearEmptyBuffers(gl);
         }

         segment.setSegmentLocation(-1, -1);
      }
   }

   /**
    * Will render the given set of {@link SegmentObject} after sorting them into a set of objects per pool-buffer in
    * order to render all segments in each buffer at a time to limit the number of bind/unbind calls.
    *
    * @param gl
    *           the current OpenGL context
    * @param segments
    *           the segments to render
    */
   public void render(final GL2 gl, final Collection<? extends SegmentObject> segments) {
      final Map<Integer, Map<Integer, List<SegmentObject>>> segmentsPerPool = new HashMap<>();

      for (final SegmentObject segment : segments) {
         Map<Integer, List<SegmentObject>> map = segmentsPerPool.get(segment.getSegmentPoolIndex());

         if (map == null) {
            map = new HashMap<>();
            segmentsPerPool.put(segment.getSegmentPoolIndex(), map);
         }

         final int subPoolIndex = (int) Math.floor(segment.getBufferIndex() / this.segmentsPerBuffer);
         List<SegmentObject> list = map.get(subPoolIndex);

         if (list == null) {
            list = new ArrayList<>();
            map.put(subPoolIndex, list);
         }

         list.add(segment);
      }

      this.render(gl, segmentsPerPool);
   }

   /**
    * Will render the given set of {@link SegmentObject} that have been pre-sorted into per pool-buffer collections. The
    * base map should contain a pair between the pool index and the segments in that pool. The inner map should contain
    * pairs between the subPool index and the segments in that subPool. SegmentObject.getPoolIndex() should be used for
    * the outer map. Math.floor(SegmentObject.getBufferIndex / pool.segmentsPerBuffer) should be used as the inner
    * index. <br/>
    * <br/>
    * Note: Math.floor() should not be necessary as each value is an integer and will automatically compute the value by
    * truncating any floating point portion of the quotient but it is clearer when reading the code.
    *
    * @param gl
    * @param segmentsPerPool
    */
   public void render(final GL2 gl, final Map<Integer, Map<Integer, List<SegmentObject>>> segmentsPerPool) {
      for (final Entry<Integer, Map<Integer, List<SegmentObject>>> entry : segmentsPerPool.entrySet()) {
         final int poolKey = entry.getKey();
         final Map<Integer, List<SegmentObject>> map = entry.getValue();
         final List<SegmentedVertexBufferObject> pools = this.buffers.get(poolKey);
         
         if(pools == null) {
            System.err.println("pool key not found: " + poolKey);
            return;
         }

         for (final Entry<Integer, List<SegmentObject>> mapEntry : map.entrySet()) {
            final int subPoolIndex = mapEntry.getKey();
            final List<SegmentObject> list = mapEntry.getValue();
            final SegmentedVertexBufferObject pool = pools.get(subPoolIndex);
            pool.render(gl, list);
         }
      }
   }

   public void setSegmentObject(final GL2 gl, final Tuple3d origin, final SegmentObject segment) {
      if (segment != null) {
         int poolIndex = segment.getSegmentPoolIndex();
         int bufferIndex = segment.getBufferIndex();

         if ((poolIndex == -1) && (bufferIndex == -1)) {
            final int vertexCount = segment.getVertexCount();
            int maxPoolIndex = Integer.MAX_VALUE;

            for (final int key : this.buffers.keySet()) {
               if ((vertexCount <= key) && (key < maxPoolIndex)) {
                  maxPoolIndex = key;
               }
            }

            if (maxPoolIndex != Integer.MAX_VALUE) {
               poolIndex = maxPoolIndex;
               bufferIndex = this.getNextAvailableBufferIndex(maxPoolIndex);
               segment.setSegmentLocation(poolIndex, bufferIndex);
            }
         }

//         System.out.println("pool index: " + poolIndex + ", buffer index: " + bufferIndex + ", point count: " + segment.getVertexCount());
         final SegmentedVertexBufferObject buffer = this.getBuffer(poolIndex, bufferIndex);
         buffer.setSegmentObject(gl, origin, segment);
      }
   }

   /**
    * Adds a new {@link SegmentedVertexBufferObject} to the pool at the given pool index and returns the resulting
    * buffer.
    *
    * @param poolIndex
    * @return
    */
   private SegmentedVertexBufferObject addBuffer(final int poolIndex) {
      List<SegmentedVertexBufferObject> buffers = this.buffers.get(poolIndex);

      if (buffers == null) {
         buffers = new ArrayList<>();
         this.buffers.put(poolIndex, buffers);
      }

      final SegmentedVertexBufferObject buffer = new SegmentedVertexBufferObject(poolIndex, this.segmentsPerBuffer, this.glPrimitiveType, this.usage, this.bufferRegions);
      buffers.add(buffer);
      // System.out.println("buffer added: " + poolIndex + ", " + buffers.size());
      // for(int i = 0; i < buffers.size(); i++) {
      // final SegmentedVertexBufferObject vbo = buffers.get(i);
      // System.out.println("\tbuffer " + i + ": " + vbo.getSegmentCount() + " (" + vbo.getSegmentUsagePercentage() +
      // "), " + vbo.getPointCount() + " (" + vbo.getPointUsagePercentage() + ")");
      // }
      return buffer;
   }

   private synchronized void clearEmptyBuffers(final GL2 gl) {
      for (final int poolIndex : this.buffers.keySet()) {
         final List<SegmentedVertexBufferObject> pool = this.buffers.get(poolIndex);

         if (pool != null) {
            final List<SegmentedVertexBufferObject> toRemove = new ArrayList<>();

            // make sure we don't delete the last buffer so stop at i == 1
            for (int i = pool.size() - 1; i >= 1; i--) {
               final SegmentedVertexBufferObject buffer = pool.get(i);

               if (buffer.isEmpty()) {
                  toRemove.add(buffer);
               } else {
                  // stop when we reach the first non-empty buffer as we can only remove off the far end
                  break;
               }
            }

            pool.removeAll(toRemove);
         }
      }
   }

   private SegmentedVertexBufferObject getBuffer(final int poolIndex, final int bufferIndex) {
      final List<SegmentedVertexBufferObject> buffers = this.buffers.get(poolIndex);
      // the index of the vertex buffer in the pool
      final int segmentBufferIndex = (int) Math.floor(bufferIndex / this.segmentsPerBuffer);

      // System.out.println("\npool: " + poolIndex + ", buffer index: " + bufferIndex + ", " + segmentBufferIndex + " of
      // " + (buffers == null ? null : buffers.size()));
      // System.out.println("segment count: " + (buffers == null ? -1 :
      // buffers.get(segmentBufferIndex).getSegmentCount()));
      return buffers.get(segmentBufferIndex);
   }

   private int getNextAvailableBufferIndex(final int poolIndex) {
      List<SegmentedVertexBufferObject> buffers = this.buffers.get(poolIndex);

      if (buffers == null) {
         System.err.println(poolIndex + " not initialized");
         this.addBuffer(poolIndex);
         buffers = this.buffers.get(poolIndex);
      }

      int segmentBufferIndex = -1;
      int bufferIndex = -1;

      for (final SegmentedVertexBufferObject vbo : buffers) {
         final int tempIndex = vbo.getNextAvailableIndex();

         if (tempIndex > -1) {
            segmentBufferIndex = buffers.indexOf(vbo);
            bufferIndex = tempIndex;
            break;
         }
      }

      if (bufferIndex == -1) {
         final SegmentedVertexBufferObject buffer = this.addBuffer(poolIndex);
         segmentBufferIndex = buffers.indexOf(buffer);
         bufferIndex = buffer.getNextAvailableIndex();
      }

      return bufferIndex + (segmentBufferIndex * this.segmentsPerBuffer);
   }
}
