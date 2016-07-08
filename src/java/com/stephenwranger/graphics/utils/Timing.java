package com.stephenwranger.graphics.utils;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Timing {
   private final Queue<Long> timings = new LinkedBlockingQueue<>();
   private final int historySize;
   
   private long min = Long.MAX_VALUE;
   private long max = Long.MIN_VALUE;
   private long startTime = Long.MIN_VALUE;
   
   Timing(final int historySize) {
      this.historySize = historySize;
   }
   
   public long getMinTiming() {
      return this.min;
   }
   
   public long getMaxTiming() {
      return this.max;
   }
   
   public double getAverageTiming() {
      return MathUtils.sum(timings) / (double) timings.size();
   }
   
   public void start() {
      this.startTime = System.nanoTime();
   }
   
   public void end() {
      final long endTime = System.nanoTime();
      
      if(this.startTime != Long.MIN_VALUE) {
         final long duration = endTime - this.startTime;
         this.startTime = Long.MIN_VALUE;

         this.min = Math.min(this.min, duration);
         this.max = Math.max(this.max, duration);
         this.timings.add(duration);
         
         if(this.timings.size() > this.historySize) {
            this.timings.remove();
            this.min = (Long) MathUtils.getMin(this.timings);
            this.max = (Long) MathUtils.getMax(this.timings);
         }
      }
   }
}
