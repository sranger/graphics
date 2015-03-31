package com.stephenwranger.graphics.math.intersection;

import java.util.ArrayList;
import java.util.List;

import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Vector2d;

public class IntersectionUtils {
   private IntersectionUtils() {
      // statics only
   }

   public static boolean pointInTriangle(final Tuple2d point, final Triangle2D triangle) {
      final Tuple2d[] corners = triangle.getCorners();
      final Tuple2d a = corners[0];
      final Tuple2d b = corners[1];
      final Tuple2d c = corners[2];

      final Vector2d v0 = new Vector2d().subtract(c, a);
      final Vector2d v1 = new Vector2d().subtract(b, a);
      final Vector2d v2 = new Vector2d().subtract(point, a);

      final double dot00 = v0.dot(v0);
      final double dot01 = v0.dot(v1);
      final double dot02 = v0.dot(v2);
      final double dot11 = v1.dot(v1);
      final double dot12 = v1.dot(v2);

      final double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);
      final double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
      final double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

      return (u >= 0) && (v >= 0) && (u + v <= 1);
   }

   public static List<Tuple2d> lineIntersectTriangle(final Triangle2D triangle, final LineSegment segment) {
      final LineSegment[] segments = triangle.getLineSegments();
      final List<Tuple2d> intersected = new ArrayList<Tuple2d>();
      final Tuple2d temp = new Tuple2d();

      for (final LineSegment edge : segments) {
         if (IntersectionUtils.lineSegmentsIntersect(segment, edge, temp)) {
            intersected.add(new Tuple2d(temp));
         }
      }

      return intersected;
   }

   public static List<Tuple2d> lineIntersectPolygon(final Tuple2d[] corners, final LineSegment segment) {
      final List<Tuple2d> intersected = new ArrayList<Tuple2d>();
      final Tuple2d result = new Tuple2d();
      LineSegment temp;

      for (int i = 0; i < corners.length; i++) {
         temp = new LineSegment(corners[i], corners[(i + 1) % corners.length]);

         if (IntersectionUtils.lineSegmentsIntersect(segment, temp, result) && !intersected.contains(result)) {
            intersected.add(new Tuple2d(result));
         }
      }

      return intersected;
   }

   public static boolean isZero(final double value) {
      return Math.abs(value) <= 1e-4;
   }

   public static boolean isEqual(final double value1, final double value2) {
      return IntersectionUtils.isZero(value1 - value2);
   }

   public static boolean isLessThan(final double value, final double max) {
      return (value < max) && !IntersectionUtils.isZero(value - max);
   }

   public static boolean isLessOrEqual(final double value, final double max) {
      return IntersectionUtils.isLessThan(value, max) || IntersectionUtils.isEqual(value, max);
   }

   public static boolean isGreaterThan(final double value, final double min) {
      return value > min && !IntersectionUtils.isZero(value - min);
   }

   public static boolean isGreaterOrEqual(final double value, final double min) {
      return IntersectionUtils.isGreaterThan(value, min) || IntersectionUtils.isEqual(value, min);
   }

   public static boolean isClampedExclusive(final double value, final double min, final double max) {
      return IntersectionUtils.isGreaterThan(value, min) && IntersectionUtils.isLessThan(value, max);
   }

   public static boolean isClampedInclusive(final double value, final double min, final double max) {
      return (IntersectionUtils.isGreaterThan(value, min) || IntersectionUtils.isEqual(value, min))
            && (IntersectionUtils.isLessThan(value, max) || IntersectionUtils.isEqual(value, max));
   }

   public static boolean lineSegmentsIntersect(final LineSegment s1, final LineSegment s2, final Tuple2d result) {
      final Tuple2d output = s1.intersect(s2);

      if (output != null) {
         result.set(output);
      }

      return output != null;
   }

   public static boolean overlap(final double firstMinY, final double firstMaxY, final double secondMinY, final double secondMaxY) {
      if ((firstMinY >= secondMinY && firstMinY <= secondMaxY) || (firstMaxY >= secondMinY && firstMaxY <= secondMaxY)) {
         return true;
      }

      if ((secondMinY >= firstMinY && secondMinY <= firstMaxY) || (secondMaxY >= firstMinY && secondMaxY <= firstMaxY)) {
         return true;
      }

      return false;
   }
}
