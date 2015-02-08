package com.stephenwranger.graphics.utils;

import java.util.List;

import com.stephenwranger.graphics.bounds.BoundingBox;
import com.stephenwranger.graphics.math.Tuple3d;

// TODO: use apache SI/NonSI
public class MathUtils {
   public static final double  YEARS_TO_SECONDS            = 3.15569e7;
   public static final double  DAYS_TO_SECONDS             = 86400;
   
   public static final double  SECONDS_TO_NANOSECONDS      = 1e9;
   public static final double  SECONDS_TO_MILLISECONDS     = 1000;
   public static final double  SECONDS_TO_MINUTES          = 0.0166667;
   public static final double  SECONDS_TO_HOURS            = 0.000277778;
   public static final double  SECONDS_TO_DAYS             = 1.1574e-5;
   public static final double  SECONDS_TO_YEARS            = 3.1689e-8;
   
   public static final double  MILLISECONDS_TO_NANOSECONDS = 1e6;
   public static final double  MILLISECONDS_TO_SECONDS     = 0.001;
   public static final double  MILLISECONDS_TO_MINUTES     = 1.6667e-5;
   public static final double  MILLISECONDS_TO_HOURS       = 2.7778e-7;
   public static final double  MILLISECONDS_TO_DAYS        = 1.15741e-8;
   public static final double  MILLISECONDS_TO_YEARS       = 3.1689e-11;
   
   public static final double  NANOSECONDS_TO_SECONDS      = 1e-9;
   public static final double  NANOSECONDS_TO_MILLISECONDS = 1e-6;
   
   public static final double  GRAVITATIONAL_CONSTANT      = 6.67384e-11;          // m3 kg-1 s-2;
   public static final double  METERS_TO_CENTIMETERS       = 100;
   
   public static final double  TWO_PI                      = Math.PI * 2.0;
   
   public static final double  ASTRONOMICAL_UNIT_METERS    = 149597870700.0;
   
   public static final Tuple3d X_AXIS                      = new Tuple3d(1, 0, 0);
   public static final Tuple3d Y_AXIS                      = new Tuple3d(0, -1, 0);
   public static final Tuple3d Z_AXIS                      = new Tuple3d(0, 0, 1);
   
   private MathUtils() {
      // static only
   }
   
   public static double clamp(final double min, final double max, final double value) {
      return Math.min(max, Math.max(min, value));
   }
   
   public static BoundingBox getKeyframeBounds(final List<Keyframe> keyframes, final double oversize) {
      double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
      double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
      
      for (final Keyframe frame : keyframes) {
         minX = Math.min(minX, frame.position.x);
         minY = Math.min(minY, frame.position.y);
         minZ = Math.min(minZ, frame.position.z);
         maxX = Math.max(maxX, frame.position.x);
         maxY = Math.max(maxY, frame.position.y);
         maxZ = Math.max(maxZ, frame.position.z);
      }
      
      return new BoundingBox(minX - (maxX - minX) * (oversize - 1.0), minY - (maxY - minY) * (oversize - 1.0), minZ - (maxZ - minZ) * (oversize - 1.0), maxX + (maxX - minX) * (oversize - 1.0), maxY
            + (maxY - minY) * (oversize - 1.0), maxZ + (maxZ - minZ) * (oversize - 1.0));
   }
   
   public static Double getDouble(final String value, final Double defaultValue) {
      return value == null || value.length() == 0 ? defaultValue : Double.valueOf(value);
   }
}
