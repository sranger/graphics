package com.stephenwranger.graphics.utils;

public class TimeUtils {
   public static final String    NANOSECONDS                 = "ns";
   public static final long      NANOSECONDS_TO_NANOSECONDS  = 1l;
   public static final String    MICROSECONDS                = "µs";
   public static final long      NANOSECONDS_TO_MICROSECONDS = 1000l;
   public static final String    MILLISECONDS                = "ms";
   public static final long      NANOSECONDS_TO_MILLISECONDS = 1000l * TimeUtils.NANOSECONDS_TO_MICROSECONDS;
   public static final String    SECONDS                     = "s";
   public static final long      NANOSECONDS_TO_SECONDS      = 1000l * TimeUtils.NANOSECONDS_TO_MILLISECONDS;
   public static final String    MINUTES                     = "m";
   public static final long      NANOSECONDS_TO_MINUTES      = 60l * TimeUtils.NANOSECONDS_TO_SECONDS;
   public static final String    HOURS                       = "h";
   public static final long      NANOSECONDS_TO_HOURS        = 60l * TimeUtils.NANOSECONDS_TO_MINUTES;
   private static final String[] UNIT_LABEL_ORDER            = { TimeUtils.NANOSECONDS, TimeUtils.MICROSECONDS, TimeUtils.MILLISECONDS, TimeUtils.SECONDS, TimeUtils.MINUTES, TimeUtils.HOURS };
   private static final long[]   UNIT_CONVERSION_ORDER       = { TimeUtils.NANOSECONDS_TO_NANOSECONDS, TimeUtils.NANOSECONDS_TO_MICROSECONDS,
      TimeUtils.NANOSECONDS_TO_MILLISECONDS, TimeUtils.NANOSECONDS_TO_SECONDS, TimeUtils.NANOSECONDS_TO_MINUTES, TimeUtils.NANOSECONDS_TO_HOURS };

   private TimeUtils() {
      // statics only
   }

   public static String formatNanoseconds(final long duration) {
      int left, right;

      for (int i = 2; i < TimeUtils.UNIT_CONVERSION_ORDER.length; i++) {
         if (duration < TimeUtils.UNIT_CONVERSION_ORDER[i]) {
            left = (int) Math.floor(duration / TimeUtils.UNIT_CONVERSION_ORDER[i - 1]);
            right = (int) ((duration % TimeUtils.UNIT_CONVERSION_ORDER[i - 1]) / TimeUtils.UNIT_CONVERSION_ORDER[i - 2]);

            return left + TimeUtils.UNIT_LABEL_ORDER[i - 1] + " " + right + TimeUtils.UNIT_LABEL_ORDER[i - 2];
         }
      }

      left = (int) Math.floor(duration / TimeUtils.NANOSECONDS_TO_HOURS);
      right = (int) ((duration % TimeUtils.NANOSECONDS_TO_HOURS) / TimeUtils.NANOSECONDS_TO_MINUTES);
      return left + TimeUtils.HOURS + " " + right + TimeUtils.MINUTES;
   }
}
