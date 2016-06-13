package com.stephenwranger.graphics.bounds;

import com.stephenwranger.graphics.math.Tuple3d;

public class BoundingSphere extends BoundingVolume {
   private final Tuple3d center;
   private final double radius;
   
   public BoundingSphere(final Tuple3d center, final double radius) {
      this.center = new Tuple3d(center);
      this.radius = radius;
   }
   
   @Override
   public boolean contains(final Tuple3d xyz) {
      return xyz.distance(center) <= radius;
   }

   @Override
   public Tuple3d getCenter() {
      return new Tuple3d(center);
   }

   @Override
   public Tuple3d getDimensions() {
      return new Tuple3d(radius * 2.0, radius * 2.0, radius * 2.0);
   }

   @Override
   public double getRadius() {
      return radius;
   }

   @Override
   public double getSpannedDistance(Tuple3d directionVector) {
      return radius * 2.0;
   }

}
