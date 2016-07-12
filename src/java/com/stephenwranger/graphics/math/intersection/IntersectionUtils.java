package com.stephenwranger.graphics.math.intersection;

import java.util.ArrayList;
import java.util.List;

import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.utils.MathUtils;
import com.stephenwranger.graphics.utils.TupleMath;

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

   public static Vector3d calculateSurfaceNormal(final Tuple3d... corners) {
      final Vector3d u = new Vector3d();
      u.subtract(corners[1], corners[0]);
      final Vector3d v = new Vector3d();
      v.subtract(corners[2], corners[0]);

      final Vector3d normal = new Vector3d();
      normal.cross(u, v);

      normal.normalize();

      return normal;
   }

   /**
    * Returns the unique intersection point between the three given planes or null if the planes do not intersect at a
    * single point.<br/>
    * <br/>
    * http://geomalgorithms.com/a05-_intersect-1.html
    *
    * @param p1
    * @param p2
    * @param p3
    * @return
    */
   public static Tuple3d intersectingPlanes(final Plane p1, final Plane p2, final Plane p3) {
      final Vector3d n1 = p1.getNormal();
      final Vector3d n2 = p2.getNormal();
      final Vector3d n3 = p3.getNormal();

      final double denom = TupleMath.dot(n1, TupleMath.cross(n2, n3));

      // if the denominator is zero, there is not unique intersection
      if (IntersectionUtils.isZero(denom)) {
         return null;
      }

      final double d1 = p1.getDistance();
      final double d2 = p2.getDistance();
      final double d3 = p3.getDistance();

      final Vector3d cross12 = new Vector3d();
      cross12.cross(n1, n2);
      final Vector3d cross23 = new Vector3d();
      cross23.cross(n2, n3);
      final Vector3d cross31 = new Vector3d();
      cross31.cross(n3, n1);

      final Vector3d negD1cross23 = new Vector3d(cross23);
      negD1cross23.scale(-d1);
      final Vector3d negD2cross31 = new Vector3d(cross31);
      negD2cross31.scale(-d2);
      final Vector3d negD3cross12 = new Vector3d(cross12);
      negD3cross12.scale(-d3);

      final Tuple3d point = new Tuple3d();
      point.add(negD1cross23);
      point.add(negD2cross31);
      point.add(negD3cross12);

      TupleMath.scale(point, 1.0 / denom);

      return point;
   }

   public static boolean isClampedExclusive(final double value, final double min, final double max) {
      return IntersectionUtils.isGreaterThan(value, min) && IntersectionUtils.isLessThan(value, max);
   }

   public static boolean isClampedInclusive(final double value, final double min, final double max) {
      return (IntersectionUtils.isGreaterThan(value, min) || IntersectionUtils.isEqual(value, min)) && (IntersectionUtils.isLessThan(value, max) || IntersectionUtils.isEqual(value, max));
   }

   public static boolean isEqual(final double value1, final double value2) {
      return IntersectionUtils.isZero(value1 - value2);
   }

   public static boolean isGreaterOrEqual(final double value, final double min) {
      return IntersectionUtils.isGreaterThan(value, min) || IntersectionUtils.isEqual(value, min);
   }

   public static boolean isGreaterThan(final double value, final double min) {
      return (value > (min - MathUtils.EPSILON)) && !IntersectionUtils.isZero(value - min);
   }

   public static boolean isLessOrEqual(final double value, final double max) {
      return IntersectionUtils.isLessThan(value, max) || IntersectionUtils.isEqual(value, max);
   }

   public static boolean isLessThan(final double value, final double max) {
      return value < (max + MathUtils.EPSILON);
   }

   public static boolean isZero(final double value) {
      return Math.abs(value) <= MathUtils.EPSILON;
   }

   public static List<Tuple2d> lineIntersectPolygon(final Tuple2d[] corners, final LineSegment segment) {
      final List<Tuple2d> intersected = new ArrayList<>();
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

   public static List<Tuple2d> lineIntersectTriangle(final Triangle2d triangle, final LineSegment segment) {
      final LineSegment[] segments = triangle.getLineSegments();
      final List<Tuple2d> intersected = new ArrayList<>();
      final Tuple2d temp = new Tuple2d();

      for (final LineSegment edge : segments) {
         if (IntersectionUtils.lineSegmentsIntersect(segment, edge, temp)) {
            intersected.add(new Tuple2d(temp));
         }
      }

      return intersected;
   }

   public static boolean lineSegmentsIntersect(final LineSegment s1, final LineSegment s2, final Tuple2d result) {
      final Tuple2d output = s1.intersect(s2);

      if (output != null) {
         result.set(output);
      }

      return output != null;
   }

   public static boolean overlap(final double firstMinY, final double firstMaxY, final double secondMinY, final double secondMaxY) {
      if (((firstMinY >= secondMinY) && (firstMinY <= secondMaxY)) || ((firstMaxY >= secondMinY) && (firstMaxY <= secondMaxY))) {
         return true;
      }

      if (((secondMinY >= firstMinY) && (secondMinY <= firstMaxY)) || ((secondMaxY >= firstMinY) && (secondMaxY <= firstMaxY))) {
         return true;
      }

      return false;
   }

   public static boolean pointInTriangle(final Tuple2d point, final Triangle2d triangle) {
      final Tuple3d barycentric = triangle.getBarycentricCoordinate(point);

      return IntersectionUtils.isGreaterOrEqual(barycentric.x, 0) && IntersectionUtils.isGreaterOrEqual(barycentric.y, 0) && IntersectionUtils.isLessOrEqual(barycentric.x + barycentric.y, 1);
   }
   
   /**
    * https://www.cs.princeton.edu/courses/archive/fall00/cs426/lectures/raycast/sld017.htm
    * 
    * @param plane
    * @param origin
    * @param direction
    * @return
    */
   public static Tuple3d rayPlaneIntersection(final Plane plane, final Tuple3d origin, final Vector3d direction) {
      final Vector3d p0 = new Vector3d(origin);
      final Vector3d normal = plane.getNormal();
      final Vector3d v = new Vector3d(direction);
      final double d = plane.getDistance();
      
      final double t = -(p0.dot(normal) + d) / (v.dot(normal));
      
      return TupleMath.add(p0, v.scale(t));
   }

   /**
    * https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm
    *
    * @param v1
    * @param v2
    * @param v3
    * @param origin
    * @param direction
    * @return
    */
   public static Tuple3d rayTriangleIntersection(final Tuple3d v1, final Tuple3d v2, final Tuple3d v3, final Tuple3d origin, final Vector3d direction) {
      //Find vectors for two edges sharing V1
      final Vector3d e1 = new Vector3d();
      e1.subtract(v2, v1);

      final Vector3d e2 = new Vector3d();
      e2.subtract(v3, v1);

      //Begin calculating determinant - also used to calculate u parameter
      final Vector3d P = new Vector3d();
      P.cross(direction, e2);

      //if determinant is near zero, ray lies in plane of triangle or ray is parallel to plane of triangle
      final double det = e1.dot(P);

      //NOT CULLING
      if ((det > -MathUtils.EPSILON) && (det < MathUtils.EPSILON)) {
         return null;
      }

      final double inv_det = 1.f / det;

      //calculate distance from V1 to ray origin
      final Vector3d T = new Vector3d();
      T.subtract(origin, v1);

      //Calculate u parameter and test bound
      final double u = T.dot(P) * inv_det;

      //The intersection lies outside of the triangle
      if ((u < 0.f) || (u > 1.f)) {
         return null;
      }

      //Prepare to test v parameter
      final Vector3d Q = new Vector3d();
      Q.cross(T, e1);

      //Calculate V parameter and test bound
      final double v = direction.dot(Q) * inv_det;

      //The intersection lies outside of the triangle
      if ((v < 0.f) || ((u + v) > 1.f)) {
         return null;
      }

      final double t = e2.dot(Q) * inv_det;

      if (t > MathUtils.EPSILON) { //ray intersection
         final Vector3d scaledDirection = new Vector3d(direction);
         scaledDirection.scale(t);

         final Tuple3d hitLocation = new Tuple3d(origin);
         hitLocation.add(scaledDirection);

         return hitLocation;
      }

      // No hit, no win
      return null;
   }
}
