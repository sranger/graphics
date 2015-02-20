package com.stephenwranger.graphics.math;

public class Vector3d extends Tuple3d implements Vector {
   public Vector3d(double x, double y, double z) {
      super(x,y,z);
   }

   @Override
   public double lengthSquared() {
      return x*x + y*y + z*z;
   }
   
   @Override
   public double length() {
      return Math.sqrt(this.lengthSquared());
   }

   @Override
   public Vector3d normalize() {
      final double length = this.length();
      this.x /= length;
      this.y /= length;
      this.z /= length;
      
      return this;
   }

   public Vector3d cross(final Vector3d other) {
      double x = this.y * other.z - this.z * other.y;
      double y = this.z * other.x - this.x * other.z;
      double z = this.x * other.y - this.y * other.x;

      return new Vector3d(x,y,z);
   }

   public double dot(final Vector3d other) {
      return this.x * other.x + this.y * other.y + this.z * other.z;
   }
   
   public double angle(final Vector3d other) {
      return Math.acos(this.dot(other));
   }
   
   public double angleSigned(final Vector3d other) {
      final double angle = this.angle(other);
      final double sign = (this.dot(other)) >= 0 ? 1 : -1;
       
      return sign * angle;
   }
}
