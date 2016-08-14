package com.stephenwranger.graphics.math.intersection;

import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.utils.TupleMath;

public class Plane {

   protected final Vector3d normal;
   protected final double   d;

   private final Tuple3d    projectionOrigin;
   private final Vector3d   projectionX;
   private final Vector3d   projectionY;

   public Plane(final Tuple3d c1, final Tuple3d c2, final Tuple3d c3) {
      this(c1, IntersectionUtils.calculateSurfaceNormal(c1, c2, c3));
   }

   public Plane(final Tuple3d point, final Vector3d normal) {
      this(normal, Plane.computeD(point, normal));
   }

   public Plane(final Vector3d normal, final double d) {
      this.normal = new Vector3d(normal);
      this.normal.normalize();
      this.d = d;

      this.projectionOrigin = this.getClosestPoint(new Tuple3d());
      final Vector3d xAxis = new Vector3d(1, 0, 0);

      // if the normal is the x-axis, choose a different axis
      if (IntersectionUtils.isEqual(1.0, this.normal.dot(xAxis))) {
         this.projectionX = new Vector3d(0, 1, 0);
      } else {
         // if not, project the x-axis onto the normal to get our projection x-axis
         this.projectionX = new Vector3d(this.getClosestPoint(xAxis));
         this.projectionX.normalize();
      }

      // cross the normal and our projection x-axis to get a new y-axis
      this.projectionY = new Vector3d();
      this.projectionY.cross(this.normal, this.projectionX);
      this.projectionY.normalize();
   }

   /**
    * Returns the signed distance of the given point to this plane; a negative value
    * indicates the point is behind the plane (opposite direction of plane normal).
    *
    * @param point
    * @return
    */
   public double distanceToPoint(final Tuple3d point) {
      //      // D = abs(aX + bY + cZ + d) / sqrt(a^2 + b^2 + c^2)
      //      // D = abs(aX + bY + cZ + d) / 1.0  # sqrt part is length of normal which we normalized to length = 1
      return (this.normal.x * point.x) + (this.normal.y * point.y) + (this.normal.z * point.z) + this.d;
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (this.getClass() != obj.getClass()) {
         return false;
      }
      Plane other = (Plane) obj;
      if (Double.doubleToLongBits(this.d) != Double.doubleToLongBits(other.d)) {
         return false;
      }
      if (this.normal == null) {
         if (other.normal != null) {
            return false;
         }
      } else if (!this.normal.equals(other.normal)) {
         return false;
      }
      return true;
   }

   /**
    * Returns the closest 3D point on this plane from the given point.<br/>
    * <br/>
    *
    * http://stackoverflow.com/a/23472188/1451705
    *
    * @param point
    * @return
    */
   public Tuple3d getClosestPoint(final Tuple3d point) {
      // A' = A - (A . n) * n
      final Vector3d aprime = new Vector3d(point);
      final double adotn = TupleMath.dot(point, this.normal);
      final Vector3d scaledNormal = new Vector3d(this.normal);
      scaledNormal.scale(adotn);
      aprime.subtract(scaledNormal);

      return aprime;
   }

   public double getDistance() {
      return this.d;
   }

   public Vector3d getNormal() {
      return new Vector3d(this.normal);
   }

   /**
    * Returns the 2D projected onto this plane.<br/>
    * <br/>
    *
    * http://stackoverflow.com/a/23472188/1451705
    *
    * @param point
    * @return
    */
   public Tuple2d getProjectedPoint(final Tuple3d point) {
      return new Tuple2d(TupleMath.dot(point, this.projectionX), TupleMath.dot(point, this.projectionY));
   }

   public Vector3d getProjectedVector(final Vector3d normal, final Tuple3d point) {
      final Tuple3d closestPoint = this.getClosestPoint(point);
      return Vector3d.getVector(this.projectionOrigin, closestPoint, true);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(this.d);
      result = (prime * result) + (int) (temp ^ (temp >>> 32));
      result = (prime * result) + ((this.normal == null) ? 0 : this.normal.hashCode());
      return result;
   }

   /**
    * Returns true if the point given is on the same side as the face normal.
    *
    * <pre>
    * http://stackoverflow.com/questions/8877872/determining-if-a-point-is-inside-a-polyhedron
    * http://stackoverflow.com/a/33836085/1451705
    * </pre>
    *
    * @param point
    * @return
    */
   public boolean isInside(final Tuple3d point) {
      final double distance = this.distanceToPoint(point);

      return IntersectionUtils.isGreaterOrEqual(distance, 0.0);
   }

   @Override
   public String toString() {
      return "[Plane: " + this.normal + ", " + this.d + "]";
   }

   public static final double distanceToPlane(final Vector3d planeNormal, final Tuple3d planePoint, final double x, final double y, final double z) {
      return ((planeNormal.x * (x - planePoint.x)) + (planeNormal.y * (y - planePoint.y)) + (planeNormal.z * (z - planePoint.z)));
   }

   public static void main(final String[] args) {
      // X-Z plane
      final Plane plane = new Plane(new Tuple3d(0, 0, 0), new Vector3d(1, 0, 0));
      System.out.println("a:                                           " + plane.normal.x);
      System.out.println("b:                                           " + plane.normal.y);
      System.out.println("c:                                           " + plane.normal.z);
      System.out.println("d:                                           " + plane.d);

      System.out.println("n . projectionX =                            " + plane.normal.dot(plane.projectionX));
      System.out.println("n . projectionY =                            " + plane.normal.dot(plane.projectionY));
      System.out.println("projectionX . projectionY =                  " + plane.projectionX.dot(plane.projectionY));

      System.out.println("Y-Z to 0,2,0 (expected = 0.0): " + plane.distanceToPoint(new Tuple3d(0, 2, 0)));
      System.out.println("Y-Z to 2,0,0 (expected = 2.0): " + plane.distanceToPoint(new Tuple3d(2, 0, 0)));
      System.out.println("2,1,0 projected onto Y-Z (expected = 0,1,0): " + plane.getClosestPoint(new Tuple3d(2, 1, 0)));
      System.out.println("1,1,1 projected onto Y-Z (expected = 0,1,1): " + plane.getClosestPoint(new Tuple3d(1, 1, 1)));
      System.out.println("0,12,1 projected onto Y-Z (expected = 0,12,1): " + plane.getClosestPoint(new Tuple3d(0, 12, 1)));
      System.out.println("0,12,1 projected onto Y-Z (expected = 12,1): " + plane.getProjectedPoint(new Tuple3d(0, 12, 1)));
      System.out.println("0,12,0 projected onto Y-Z (expected = 0.99,0.11): " + plane.getProjectedVector(plane.normal, new Tuple3d(0, 9, 1)));
      System.out.println("");
   }

   private static double computeD(final Tuple3d point, final Vector3d normal) {
      normal.normalize();

      return -((normal.x * point.x) + (normal.y * point.y) + (normal.z * point.z));
   }
}
