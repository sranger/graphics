package com.stephenwranger.graphics.bounds;

import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.intersection.Triangle3d;
import com.stephenwranger.graphics.utils.TupleMath;

public class BoundingBox extends BoundingVolume {
   private final Tuple3d min, max;
   private final double boundingSphereRadius;
   
   public BoundingBox(final Tuple3d min, final Tuple3d max) {
      this(min.x, min.y, min.z, max.x, max.y, max.z);
   }
   
   public BoundingBox(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ) {
      this.min = new Tuple3d(Math.min(minX, maxX), Math.min(minY, maxY), Math.min(minZ, maxZ));
      this.max = new Tuple3d(Math.max(minX, maxX), Math.max(minY, maxY), Math.max(minZ, maxZ));
      
      this.boundingSphereRadius = TupleMath.distance(min, max);
   }
   
   public BoundingBox(final Tuple3d center, final double xDimension, final double yDimension, final double zDimension) {
      this(center.x - xDimension / 2.0, center.y - yDimension / 2.0, center.z - zDimension / 2.0, center.x + xDimension / 2.0, center.y + yDimension / 2.0, center.z + zDimension / 2.0);
   }
   
   public BoundingBox(final BoundingVolume bv) {
      this(bv.getCenter(), bv.getSpannedDistance(Axis.X_AXIS.getAxis()), bv.getSpannedDistance(Axis.Y_AXIS.getAxis()), bv.getSpannedDistance(Axis.Z_AXIS.getAxis()));
   }
   
   public Triangle3d[] getFaces() {
      final Triangle3d[] faces = new Triangle3d[12];
      final Tuple3d lll = new Tuple3d(this.min);
      final Tuple3d llh = new Tuple3d(this.min.x, this.min.y, this.max.z);
      final Tuple3d lhl = new Tuple3d(this.min.x, this.max.y, this.min.z);
      final Tuple3d lhh = new Tuple3d(this.min.x, this.max.y, this.max.z);
      final Tuple3d hll = new Tuple3d(this.max.x, this.min.y, this.min.z);
      final Tuple3d hlh = new Tuple3d(this.max.x, this.min.y, this.max.z);
      final Tuple3d hhl = new Tuple3d(this.max.x, this.max.y, this.min.z);
      final Tuple3d hhh = new Tuple3d(this.max);

      faces[0] = new Triangle3d(lll, lhl, lhh);
      faces[1] = new Triangle3d(lll, lhh, llh);
      faces[2] = new Triangle3d(lll, lhl, hhl);
      faces[3] = new Triangle3d(lll, hhl, hll);
      faces[4] = new Triangle3d(hll, hhl, hhh);
      faces[5] = new Triangle3d(hll, hhh, hlh);
      faces[6] = new Triangle3d(hlh, hhh, lhh);
      faces[7] = new Triangle3d(hlh, lhh, llh);
      faces[8] = new Triangle3d(lhl, lhh, hhh);
      faces[9] = new Triangle3d(lhl, hhh, hhl);
      faces[10] = new Triangle3d(lll, hll, hlh);
      faces[11] = new Triangle3d(lll, hlh, llh);
      
      return faces;
   }
   
   @Override
   public boolean contains(final Tuple3d xyz) {
      final boolean inX = xyz.x >= this.min.x && xyz.x <= this.max.x;
      final boolean inY = xyz.y >= this.min.y && xyz.y <= this.max.y;
      final boolean inZ = xyz.z >= this.min.z && xyz.z <= this.max.z;
      
      return inX && inY && inZ;
   }
   
   public Tuple3d[] getCorners() {
      final Tuple3d lll = new Tuple3d(this.min);
      final Tuple3d llh = new Tuple3d(this.min.x, this.min.y, this.max.z);
      final Tuple3d lhl = new Tuple3d(this.min.x, this.max.y, this.min.z);
      final Tuple3d lhh = new Tuple3d(this.min.x, this.max.y, this.max.z);
      final Tuple3d hll = new Tuple3d(this.max.x, this.min.y, this.min.z);
      final Tuple3d hlh = new Tuple3d(this.max.x, this.min.y, this.max.z);
      final Tuple3d hhl = new Tuple3d(this.max.x, this.max.y, this.min.z);
      final Tuple3d hhh = new Tuple3d(this.max);
      
      return new Tuple3d[] { lll, llh, lhl, lhh, hll, hlh, hhl, hhh };
   }
   
   public Tuple3d getMin() {
      return new Tuple3d(min);
   }
   
   public Tuple3d getMax() {
      return new Tuple3d(max);
   }

   @Override
   public Tuple3d getCenter() {
      return new Tuple3d((max.x - min.x) / 2.0 + min.x, (max.y - min.y) / 2.0 + min.y, (max.z - min.z) / 2.0 + min.z);
   }

   @Override
   public Tuple3d getDimensions() {
      return new Tuple3d(max.x - min.x, max.y - min.y, max.z - min.z);
   }

   @Override
   public double getRadius() {
      return boundingSphereRadius;
   }

   @Override
   public double getSpannedDistance(Tuple3d directionVector) {
      if (directionVector == null) {
         return boundingSphereRadius * 2.0;
      }
      
      final Tuple3d dims = getDimensions();
      
      return Math.abs(dims.x * directionVector.x) + Math.abs(dims.y * directionVector.y) + Math.abs(dims.z * directionVector.z);
   }
   
   @Override
   public String toString() {
      return "min: " + min + ", max: " + max;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(boundingSphereRadius);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + ((max == null) ? 0 : max.hashCode());
      result = prime * result + ((min == null) ? 0 : min.hashCode());
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
      BoundingBox other = (BoundingBox) obj;
      if (Double.doubleToLongBits(boundingSphereRadius) != Double.doubleToLongBits(other.boundingSphereRadius))
         return false;
      if (max == null) {
         if (other.max != null)
            return false;
      } else if (!max.equals(other.max))
         return false;
      if (min == null) {
         if (other.min != null)
            return false;
      } else if (!min.equals(other.min))
         return false;
      return true;
   }

   @Override
   public BoundingVolume offset(final Tuple3d offset) {
      return new BoundingBox(TupleMath.sub(this.min, offset), TupleMath.sub(this.max, offset));
   }

   /**
    * Returns the surface area of the X-Y plane side.
    * @return
    */
   public double getXYSideArea() {
      final double x = max.x - min.x;
      final double y = max.y - min.y;
      return x * y;
   }

   /**
    * Returns the surface area of the X-Z plane side.
    * @return
    */
   public double getXZSideArea() {
      final double x = max.x - min.x;
      final double z = max.z - min.z;
      return x * z;
   }

   /**
    * Returns the surface area of the Y-Z plane side.
    * @return
    */
   public double getYZSideArea() {
      final double y = max.y - min.y;
      final double z = max.z - min.z;
      return y * z;
   }
   
   public double getVolume() {
      final double x = max.x - min.x;
      final double y = max.y - min.y;
      final double z = max.z - min.z;
      
      return x * y * z;
   }
}
