package com.stephenwranger.graphics.utils.buffers;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.utils.MathUtils;
import com.stephenwranger.graphics.utils.Timings;
import com.stephenwranger.graphics.utils.textures.Texture2d;

/**
 * This buffer splits itself into a number of segments and keeps track of which segments are currently in use.
 *
 * @author rangers
 *
 */
public class SegmentedVertexBufferObject extends VertexBufferObject {
   private final Set<Integer>          usedIndices;
   private final Map<Integer, Integer> vertexCounts;
   private final int                   maxSegmentSize;
   private final int                   segmentsPerBuffer;
   private final Timings               timings = new Timings(100);

   public SegmentedVertexBufferObject(final int maxSegmentSize, final int segmentsPerBuffer, final int glPrimitiveType, final int usage, final BufferRegion... bufferRegions) {
      super(maxSegmentSize * segmentsPerBuffer, true, glPrimitiveType, usage, bufferRegions);

      this.maxSegmentSize = maxSegmentSize;
      this.segmentsPerBuffer = segmentsPerBuffer;
      this.usedIndices = new ConcurrentSkipListSet<>();
      this.vertexCounts = new HashMap<>();
   }

   public void clearIndex(final int bufferIndex) {
      final int index = bufferIndex % this.segmentsPerBuffer;
      this.usedIndices.remove(index);
      this.vertexCounts.remove(index);
   }

   /**
    * Returns the next available segment index or -1 if this buffer is full. If not full, the returned index will be
    * marked as used.
    *
    * @return the next avialable index or -1 if full
    */
   public int getNextAvailableIndex() {
      for (int i = 0; i < this.segmentsPerBuffer; i++) {
         if (!this.usedIndices.contains(i)) {
            this.usedIndices.add(i);
            return i;
         }
      }

      return -1;
   }

   public int getPointCount() {
      return (int) MathUtils.sum(this.vertexCounts.values());
   }

   public double getPointUsagePercentage() {
      return MathUtils.sum(this.vertexCounts.values()) / this.vertexCounts.size();
   }

   public int getSegmentCount() {
      return this.usedIndices.size();
   }

   public double getSegmentUsagePercentage() {
      return this.usedIndices.size() / (double) this.segmentsPerBuffer;
   }

   public boolean isEmpty() {
      return this.usedIndices.isEmpty();
   }

   @Override
   public void render(final GL2 gl) {
      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.vboId);

      for (final BufferRegion region : this.regions) {
         region.enable(gl);
      }

      for (final Entry<Integer, Integer> entry : this.vertexCounts.entrySet()) {
         final int index = entry.getKey() * this.maxSegmentSize;
         final int vertexCount = entry.getValue();
         gl.glDrawArrays(this.glPrimitiveType, index, vertexCount);
      }

      // disable arrays once we're done
      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

      for (final BufferRegion region : this.regions) {
         region.disable(gl);
      }
   }

   public void render(final GL2 gl, final Collection<SegmentObject> segments) {
      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.vboId);

      for (final BufferRegion region : this.regions) {
         region.enable(gl);
      }

      for (final SegmentObject segment : segments) {
         final int index = (segment.getBufferIndex() % this.segmentsPerBuffer) * this.maxSegmentSize;
         final int vertexCount = segment.getVertexCount();
         final Texture2d texture = segment.getTexture();

         if (texture != null) {
            texture.enable(gl);
         }

         gl.glDrawArrays(this.glPrimitiveType, index, vertexCount);

         if (texture != null) {
            texture.disable(gl);
         }
      }

      // disable arrays once we're done
      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

      for (final BufferRegion region : this.regions) {
         region.disable(gl);
      }
   }

   public void setSegmentObject(final GL2 gl, final Tuple3d origin, final SegmentObject segment) {
      final int bufferIndex = segment.getBufferIndex() % this.segmentsPerBuffer;

      if (bufferIndex > -1) {
         this.timings.start("map");
         final ByteBuffer buffer = this.mapBuffer(gl);
         this.timings.end("map");

         this.timings.start("position");
         buffer.position(this.maxSegmentSize * bufferIndex * 32);
         this.timings.end("position");

         this.timings.start("load");
         segment.loadBuffer(origin, buffer);
         this.timings.end("load");

         this.timings.start("counts");
         this.vertexCounts.put(bufferIndex, segment.getVertexCount());
         this.timings.end("counts");

         this.timings.start("unmap");
         this.unmapBuffer(gl);
         this.timings.end("unmap");
      }

      //      System.out.println("\n" + timings);
   }
}
