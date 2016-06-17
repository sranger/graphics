package com.stephenwranger.graphics.math.intersection;

import java.util.ArrayList;
import java.util.List;

import com.stephenwranger.graphics.math.PickingRay;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.utils.MathUtils;

public class Ellipsoid {
   public static final double COS_67P5 = 0.38268343236508977; // cosine of 67.5 degrees
   public static final double AD_C = 1.0026000; // Toms region 1 constant
   public static final double MIN_LAT = -MathUtils.HALF_PI;
   public static final double MAX_LAT = MathUtils.HALF_PI;
   public static final double MIN_LON = -Math.PI;
   public static final double MAX_LON = Math.PI;
   
   private final Tuple3d center;
   private final double semiMajorAxis;
   private final double flattening;
   private final double invFlattening;
   private final double firstEccentricitySquared;
   private final double secondEccentricitySquared;
   
   public Ellipsoid(final Tuple3d center, final double semiMajorAxis, final double flattening, final double firstEccentricitySquared, final double secondEccentricitySquared) {
      this.center = new Tuple3d(center);
      this.semiMajorAxis = semiMajorAxis;
      this.flattening = flattening;
      this.invFlattening = 1.0 - this.flattening;
      this.firstEccentricitySquared = firstEccentricitySquared;
      this.secondEccentricitySquared = secondEccentricitySquared;
   }
   
   public List<Tuple3d> getIntersectionGeodetic(final PickingRay ray) {
      final List<Tuple3d> cartesianIntersections = this.getIntersection(ray);
      final List<Tuple3d> geodeticIntersections = new ArrayList<>();

      for(final Tuple3d cartesian : cartesianIntersections) {
         cartesian.subtract(center);
         geodeticIntersections.add(toLonLatAlt(cartesian));
      }
      
      return geodeticIntersections;
   }

   /**
    * Intersection test with this {@link Ellipsoid}. Returns zero, one, or two intersections; zero means no intersection,
    * one means ray is tangent, two means ray passes through ellipsoid.
    * 
    * @param ray
    * @return
    */
   public List<Tuple3d> getIntersection(final PickingRay ray) {
      final double[] lineParmsAtIntersect = new double[2];
      final double a, b, c, discrminant;
      int numSoln = 0;
      lineParmsAtIntersect[0] = 0.0;
      lineParmsAtIntersect[1] = 0.0;

      final Tuple3d origin = ray.getOrigin();
      final Vector3d direction = ray.getDirection();
      final double diffX = origin.x - center.x;
      final double diffY = origin.y - center.y;
      final double diffZ = origin.z - center.z;

      a = direction.x * direction.x + direction.y * direction.y + this.invFlattening * direction.z * direction.z;

      b = 2.0 * (diffX * direction.x + diffY * direction.y + this.invFlattening * diffZ * direction.z);

      c = diffX * diffX + diffY * diffY + this.invFlattening * diffZ * diffZ - this.semiMajorAxis * this.semiMajorAxis;

      if (Math.abs(a) > 0.0) {
         discrminant = b * b - 4.0 * a * c;

         if (IntersectionUtils.isGreaterThan(discrminant, 0.0)) {
            final double dRoot = Math.sqrt(discrminant);
            lineParmsAtIntersect[numSoln] = (-b + dRoot) / (2.0 * a);
            numSoln++;
            lineParmsAtIntersect[numSoln] = (-b - dRoot) / (2.0 * a);
            numSoln++;
         } else if (discrminant >= 0.0) {
            // 0 <= discrm <= nearZero, line is tangent to ellipsoid
            lineParmsAtIntersect[numSoln] = -b / (2.0 * a);
            numSoln++;
         } else if ((IntersectionUtils.isLessOrEqual(Math.abs(b), 0.0)) && (-1 == (MathUtils.getSign(a) * MathUtils.getSign(c)))) {
            lineParmsAtIntersect[numSoln] = Math.sqrt(-c / a);
            numSoln++;
            lineParmsAtIntersect[numSoln] = -Math.sqrt(-c / a);
            numSoln++;
         }
      } else if (IntersectionUtils.isGreaterThan(Math.abs(b), 0.0)) {
         // a = 0 ==> t = -c / b, and also not quadratic, therefore tangent to ellipsoid
         lineParmsAtIntersect[0] = -c / b;
         numSoln++;
      }
      
      final List<Tuple3d> intersections = new ArrayList<>();
      
      for(int i = 0; i < numSoln; i++) {
         final double dist = lineParmsAtIntersect[i];
         final Vector3d dir = new Vector3d(direction);
         dir.scale(dist);
         final Tuple3d intersection = new Tuple3d(origin);
         intersection.add(dir);
         intersections.add(intersection);
      }

      return intersections;
   }

   /**
    * Converts the given cartesian coordinates into lon,lat,alt using the current {@link Ellipsoid} parameters.
    * 
    * @param cartesian XYZ coordinates
    * 
    * @return {@link Tuple3d} containing lon in degrees, lat in degrees, and alt in meters
    * 
    *         The method used here is derived from 'An Improved Algorithm for
    *         Geocentric to Geodetic Coordinate Conversion', by Ralph Toms, Feb 1996
    */
   public Tuple3d toLonLatAlt(final Tuple3d cartesian) {
      final Tuple3d cartesianOffset = new Tuple3d(cartesian);
      cartesianOffset.subtract(this.center);
      
      /* Note: Variable names follow the notation used in Toms, Feb 1996 */
      boolean atPole = false; // indicates whether location is in polar region
      final double b = this.semiMajorAxis * this.invFlattening; // Semi-minor axis of ellipsoid, in meters
      double lat = 0, lon = 0, alt = 0;

      if (cartesianOffset.x != 0) {
         lon = Math.atan2(cartesianOffset.y, cartesianOffset.x);
      } else {
         if (cartesianOffset.y > 0) {
            lon = MathUtils.HALF_PI;
         } else if (cartesianOffset.y < 0) {
            lon = -MathUtils.HALF_PI;
         } else {
            atPole = true;
            lon = 0;
            if (cartesianOffset.z > 0) { /* north pole */
               lat = MAX_LAT;
            } else if (cartesianOffset.z < 0) { /* south pole */
               lat = MIN_LAT;
            } else { /* center of earth */
               lat = MAX_LAT;
               alt = -b;
               return new Tuple3d(Math.toDegrees(lon), Math.toDegrees(lat), alt);
            }
         }
      }
      final double w2 = cartesianOffset.x * cartesianOffset.x + cartesianOffset.y * cartesianOffset.y; // square of distance from Z axis
      final double w = Math.sqrt(w2); // distance from Z axis
      final double t0 = cartesianOffset.z * AD_C; // initial estimate of vertical component
      final double s0 = Math.sqrt(t0 * t0 + w2); // initial estimate of horizontal component
      final double sinB0 = t0 / s0; // sin(B0), B0 is estimate of Bowring aux variable
      final double cosB0 = w / s0; // cos(B0)
      final double sin3B0 = sinB0 * sinB0 * sinB0; // cube of sin(B0)
      final double t1 = cartesianOffset.z + b * this.secondEccentricitySquared * sin3B0; // corrected estimate of vertical component
      final double sum = w - this.semiMajorAxis * this.firstEccentricitySquared * cosB0 * cosB0 * cosB0; // numerator of cos(phi1)
      final double s1 = Math.sqrt(t1 * t1 + sum * sum); // corrected estimate of horizontal component
      final double sinP1 = t1 / s1; // sin(phi1), phi1 is estimated latitude
      final double cosP1 = sum / s1; // cos(phi1)
      final double Rn = this.semiMajorAxis / Math.sqrt(1.0 - this.firstEccentricitySquared * sinP1 * sinP1); // earth radius at loc
      
      if (cosP1 >= COS_67P5) {
         alt = w / cosP1 - Rn;
      } else if (cosP1 <= -COS_67P5) {
         alt = w / -cosP1 - Rn;
      } else {
         alt = cartesianOffset.z / sinP1 + Rn * (this.firstEccentricitySquared - 1.0);
      }
      if (!atPole) {
         lat = Math.atan(sinP1 / cosP1);
      }
      
      return new Tuple3d(Math.toDegrees(lon), Math.toDegrees(lat), alt);
   }
}