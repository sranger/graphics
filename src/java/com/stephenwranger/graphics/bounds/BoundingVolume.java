package com.stephenwranger.graphics.bounds;

import com.stephenwranger.graphics.math.Tuple3d;

public abstract class BoundingVolume {
   public enum Axis {
      X_AXIS(new Tuple3d(1,0,0)),
      Y_AXIS(new Tuple3d(0,1,0)),
      Z_AXIS(new Tuple3d(0,0,1));
      
      private final Tuple3d axis;
      
      private Axis(final Tuple3d axis) {
         this.axis = axis;
      }
      
      public Tuple3d getAxis() {
         return new Tuple3d(axis);
      }
   }
   
   public boolean intersects(final BoundingVolume bounds) {
      return BoundsUtils.intersectVolumes(this, bounds);
   }
   
   public boolean intersectsVector(final Tuple3d vector) {
      return BoundsUtils.intersectsVector(this, vector);
   }
   
   public abstract boolean contains(final Tuple3d xyz);
   
   public abstract Tuple3d getCenter();
   
   public abstract Tuple3d getDimensions();
   
   public abstract double getRadius();
   
   public abstract double getSpannedDistance(final Tuple3d directionVector);
   
   public abstract BoundingVolume offset(final Tuple3d offset);
}
