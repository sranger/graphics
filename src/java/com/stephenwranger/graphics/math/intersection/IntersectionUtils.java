package com.stephenwranger.graphics.math.intersection;

import java.util.ArrayList;
import java.util.List;

import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.utils.MathUtils;

/**
 * http://www.realtimerendering.com/intersections.html
 * 
 * @author rangers
 *
 */
public class IntersectionUtils {
   private IntersectionUtils() {
      // statics only
   }

   public static boolean pointInTriangle(final Tuple2d point, final Triangle2d triangle) {
      final Tuple3d barycentric = triangle.getBarycentricCoordinate(point);

      return IntersectionUtils.isGreaterOrEqual(barycentric.x, 0) && IntersectionUtils.isGreaterOrEqual(barycentric.y, 0)
            && IntersectionUtils.isLessOrEqual(barycentric.x + barycentric.y, 1);
   }

   public static List<Tuple2d> lineIntersectTriangle(final Triangle2d triangle, final LineSegment segment) {
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
      return Math.abs(value) <= MathUtils.EPSILON;
   }

   public static boolean isEqual(final double value1, final double value2) {
      return IntersectionUtils.isZero(value1 - value2);
   }

   public static boolean isLessThan(final double value, final double max) {
      return value < max + MathUtils.EPSILON;
   }

   public static boolean isLessOrEqual(final double value, final double max) {
      return IntersectionUtils.isLessThan(value, max) || IntersectionUtils.isEqual(value, max);
   }

   public static boolean isGreaterThan(final double value, final double min) {
      return value > min - MathUtils.EPSILON && !IntersectionUtils.isZero(value - min);
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

   public static Vector3d calculateSurfaceNormal(final Tuple3d[] corners) {
      final Vector3d u = new Vector3d();
      u.subtract(corners[1], corners[0]);
      final Vector3d v = new Vector3d();
      v.subtract(corners[2], corners[0]);
      
      final Vector3d normal = new Vector3d();
      normal.cross(u, v);
      
      normal.normalize();
      
      return normal;
   }
}
