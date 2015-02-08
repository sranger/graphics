package com.stephenwranger.graphics.utils;

import java.util.HashMap;
import java.util.Map;

import com.stephenwranger.graphics.math.Quat4d;
import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Tuple4d;

public class TupleMath {
   private static final Map<Double, Double[]> COS_SIN_MAP = new HashMap<Double, Double[]>();
   private static final Tuple3d BASE_DIRECTION = new Tuple3d(0, 0, 1);

   private TupleMath() {
      // nothing; statics only
   }

   public static Tuple3d add(final Tuple3d t1, final Tuple3d t2) {
      return new Tuple3d(t1.x + t2.x, t1.y + t2.y, t1.z + t2.z);
   }

   public static Tuple3d sub(final Tuple3d t1, final Tuple3d t2) {
      return new Tuple3d(t1.x - t2.x, t1.y - t2.y, t1.z - t2.z);
   }

   public static void scale(final Tuple3d t, final double scale) {
      t.x *= scale;
      t.y *= scale;
      t.z *= scale;
   }

   public static double length(final Tuple2d t) {
      return Math.sqrt(TupleMath.lengthSquared(t));
   }

   public static double lengthSquared(final Tuple2d t) {
      return (t.x * t.x + t.y * t.y);
   }

   public static double length(final Tuple3d t) {
      return Math.sqrt(TupleMath.lengthSquared(t));
   }

   public static double lengthSquared(final Tuple3d t) {
      return (t.x * t.x + t.y * t.y + t.z * t.z);
   }

   public static double length(final Tuple4d t) {
      return Math.sqrt(TupleMath.lengthSquared(t));
   }

   public static double lengthSquared(final Tuple4d t) {
      return (t.x * t.x + t.y * t.y + t.z * t.z + t.w * t.w);
   }

   public static void normalize(final Tuple2d vector) {
      final double length = length(vector);

      if (length != 0) {
         vector.x /= length;
         vector.y /= length;
      }
   }

   public static void normalize(final Tuple3d vector) {
      final double length = length(vector);

      if (length != 0) {
         vector.x /= length;
         vector.y /= length;
         vector.z /= length;
      }
   }

   public static void normalize(final Tuple4d vector) {
      final double length = length(vector);

      if (length != 0) {
         vector.x /= length;
         vector.y /= length;
         vector.z /= length;
         vector.w /= length;
      }
   }

   public static double dot(final Tuple3d v0, final Tuple3d v1) {
      // final double v0Length = TupleMath.length(v0);
      // final double v1Length = TupleMath.length(v1);
      // final double angle = TupleMath.angle(v0, v1);
      //
      // return v0Length * v1Length * Math.cos(angle);

      return v0.x * v1.x + v0.y * v1.y + v0.z * v1.z;
   }

   public static Tuple3d cross(final Tuple3d v0, final Tuple3d v1) {
      final Tuple3d cross = new Tuple3d();

      cross.x = (v0.y * v1.z) - (v0.z * v1.y);
      cross.y = (v0.z * v1.x) - (v0.x * v1.z);
      cross.z = (v0.x * v1.y) - (v0.y * v1.x);

      return cross;
   }

   /**
    * Returns the angle between the two given vectors in radians in the range [0,PI] or NaN if invalid.
    * 
    * @param v0
    * @param v1
    * @return
    */
   public static double angle(final Tuple3d Vn, final Tuple3d Va, final Tuple3d Vb) {
      final double sina = TupleMath.length(TupleMath.cross(Va, Vb)) / (TupleMath.length(Va) * TupleMath.length(Vb));
      final double cosa = TupleMath.dot(Va, Vb) / (TupleMath.length(Va) * TupleMath.length(Vb));

      double angle = Math.atan2(sina, cosa);

      final double sign = TupleMath.dot(Vn, TupleMath.cross(Va, Vb));

      if (sign < 0) {
         angle = -angle;
      }

      return angle;
   }

   public static double innerProduct(final Tuple3d v0, final Tuple3d v1) {
      double c = 0;

      c += (v0.x * v1.x);
      c += (v0.y * v1.y);
      c += (v0.z * v1.z);

      return c;
   }

   public static double distanceSquared(final Tuple3d v0, final Tuple3d v1) {
      return (v1.x - v0.x) * (v1.x - v0.x) + (v1.y - v0.y) * (v1.y - v0.y) + (v1.z - v0.z) * (v1.z - v0.z);
   }

   public static double distance(final Tuple3d v0, final Tuple3d v1) {
      return Math.sqrt(TupleMath.distanceSquared(v0, v1));
   }

   /**
    * Returns a random, normalized vector along the given direction within a cone defined by the given angle (in
    * degrees; half angle radius from direction vector). <br/>
    * <br/>
    * http://math.stackexchange.com/questions/44689/how-to-find-a-random-axis-or-unit-vector-in-3d
    * 
    * @param direction
    * @param angle
    * @return
    */
   public static Tuple3d getRandomVector(final Tuple3d direction, final double angle) {
      final double phi = Math.random() * MathUtils.TWO_PI;
      final double cosTheta = Math.cos(Math.toRadians(angle));
      final double z = Math.random() * (1.0 - cosTheta) + cosTheta;
      final double sqrtOneMinusZsquared = Math.sqrt(1.0 - (z * z));
      Double[] cosSin;

      if (COS_SIN_MAP.containsKey(phi)) {
         cosSin = COS_SIN_MAP.get(phi);
      } else {
         cosSin = new Double[] { Math.cos(phi), Math.sin(phi) };
         COS_SIN_MAP.put(phi, cosSin);
      }

      double x = sqrtOneMinusZsquared * cosSin[0];
      double y = sqrtOneMinusZsquared * cosSin[1];

      final Tuple3d randomVector = new Tuple3d(x, y, z);
      TupleMath.normalize(randomVector);

      final Tuple3d axis = TupleMath.cross(BASE_DIRECTION, direction);
      final double rotationAngle = Math.acos(TupleMath.dot(direction, BASE_DIRECTION));
      final Quat4d rotation = new Quat4d(axis, Math.toDegrees(rotationAngle));
      rotation.mult(randomVector);

      return randomVector;
   }
}
