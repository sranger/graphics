package com.stephenwranger.graphics.bounds;

import com.stephenwranger.graphics.math.Tuple3d;
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
}
