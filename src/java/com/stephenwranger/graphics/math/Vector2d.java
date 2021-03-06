package com.stephenwranger.graphics.math;

public class Vector2d extends Tuple2d implements Vector {
   public Vector2d() {
      super(0, 0);
   }
   
   public Vector2d(final Tuple2d tuple) {
      super(tuple.x, tuple.y);
   }

   public Vector2d(final double x, final double y) {
      super(x,y);
   }

   @Override
   public double lengthSquared() {
      return this.x*this.x + this.y*this.y;
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

   public Vector2d scale(final double scale) {
      this.x *= scale;
      this.y *= scale;

      return this;
   }

   public double dot(final Vector2d other) {
      return this.x * other.x + this.y * other.y;
   }

   public double angle(final Vector2d other) {
      return Math.acos(this.dot(other));
   }

   public double angleSigned(final Vector2d other) {
      final double sign = (this.x >= other.x) ? 1 : -1;
      return this.angle(other) * sign;
   }

   @Override
   public Vector2d add(final Tuple2d other) {
      this.x += other.x;
      this.y += other.y;

      return this;
   }

   @Override
   public Vector2d add(final Tuple2d o1, final Tuple2d o2) {
      this.x = o1.x + o2.x;
      this.y = o1.y + o2.y;

      return this;
   }

   @Override
   public Vector2d subtract(final Tuple2d other) {
      this.x -= other.x;
      this.y -= other.y;

      return this;
   }

   @Override
   public Vector2d subtract(final Tuple2d o1, final Tuple2d o2) {
      this.x = o1.x - o2.x;
      this.y = o1.y - o2.y;

      return this;
   }
}
