package com.stephenwranger.graphics.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Timings {
   private final Map<String, Timing> timings = new HashMap<>();
   private final int                 historySize;

   public Timings(final int historySize) {
      this.historySize = historySize;
   }

   public void end(final String key) {
      final Timing timing = this.timings.get(key);

      if (timing == null) {
         throw new RuntimeException("Cannot end a timing without first starting it. Key not found: " + key);
      }

      timing.end();
   }

   public double getAverage(final String key) {
      final Timing timing = this.timings.get(key);

      if (timing == null) {
         throw new RuntimeException("Cannot access a timing ; key not found: " + key);
      }

      return timing.getAverageTiming();
   }

   public long getMax(final String key) {
      final Timing timing = this.timings.get(key);

      if (timing == null) {
         throw new RuntimeException("Cannot access a timing ; key not found: " + key);
      }

      return timing.getMaxTiming();
   }

   public long getMin(final String key) {
      final Timing timing = this.timings.get(key);

      if (timing == null) {
         throw new RuntimeException("Cannot access a timing ; key not found: " + key);
      }

      return timing.getMinTiming();
   }

   public void start(final String key) {
      Timing timing = this.timings.get(key);

      if (timing == null) {
         timing = new Timing(this.historySize);
         this.timings.put(key, timing);
      }

      timing.start();
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();

      for (final Entry<String, Timing> entry : this.timings.entrySet()) {
         final Timing timing = entry.getValue();
         sb.append(entry.getKey()).append("\n");
         sb.append("\tavg: ").append(TimeUtils.formatNanoseconds((long) timing.getAverageTiming())).append("\n");
         sb.append("\tmin: ").append(TimeUtils.formatNanoseconds(timing.getMinTiming())).append("\n");
         sb.append("\tmax: ").append(TimeUtils.formatNanoseconds(timing.getMaxTiming())).append("\n");
      }

      return sb.toString();
   }
}
