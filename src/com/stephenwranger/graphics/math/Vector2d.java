package com.stephenwranger.graphics.math;

public class Vector2d extends Tuple2d implements Vector {
   public Vector2d(final double x, final double y) {
      super(x,y);
   }
   
   @Override
   public double lengthSquared() {
      return x*x + y*y;
   }
   
   @Override
   public double length() {
      return Math.sqrt(this.lengthSquared());
   }

   @Override
   public Vector2d normalize() {
      final double length = this.length();
      this.x /= length;
      this.y /= length;
      
      return this;
   }

   public double dot(final Vector2d other) {
      return this.x * other.x + this.y * other.y;
   }
   
   public double angle(final Vector2d other) {
      return Math.acos(this.dot(other));
   }
   
   public double angleSigned(final Vector2d other) {
      double sign = (this.x >= other.x) ? 1 : -1;
      return this.angle(other) * sign;
   }
}
