package com.stephenwranger.graphics.math.intersection;

import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;

public class Plane {

   protected final Vector3d normal;
   protected final double d;

   public Plane(final Tuple3d c1, final Tuple3d c2, final Tuple3d c3) {
      this.normal = IntersectionUtils.calculateSurfaceNormal(c1, c2, c3);
      this.d = -(this.normal.x * c1.x + this.normal.y * c1.y + this.normal.z * c1.z);
   }
   
   public Plane(final Vector3d normal, final double distance) {
      this.normal = new Vector3d(normal);
      this.d = distance;
      
   }
   
   public Vector3d getNormal() {
      return new Vector3d(this.normal);
   }
   
   public double getDistance() {
      return this.d;
   }
   
   public double distanceToPoint(final Tuple3d point) {
      // D = abs(aX + bY + cZ + d) / sqrt(a^2 + b^2 + c^2)
      // D = abs(aX + bY + cZ + d) / 1.0  # sqrt part is length of normal which we normalized to length = 1
      return this.normal.x * point.x + this.normal.y * point.y + this.normal.z * point.z + this.d;
   }
   
   public static final double distanceToPlane(final Vector3d planeNormal, final Tuple3d planePoint, final double x, final double y, final double z) {
      return ((planeNormal.x * (x - planePoint.x)) + (planeNormal.y * (y - planePoint.y)) + (planeNormal.z * (z - planePoint.z)));
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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(d);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + ((normal == null) ? 0 : normal.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Plane other = (Plane) obj;
      if (Double.doubleToLongBits(d) != Double.doubleToLongBits(other.d))
         return false;
      if (normal == null) {
         if (other.normal != null)
            return false;
      } else if (!normal.equals(other.normal))
         return false;
      return true;
   }
}
