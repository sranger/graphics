package com.stephenwranger.graphics.math;

public class Vector3d extends Tuple3d implements Vector {
   public Vector3d(double x, double y, double z) {
      super(x,y,z);
   }

   public Vector3d() {
      this(0, 0, 0);
   }
   
   public Vector3d(final Tuple3d tuple) {
      super(tuple);
   }

   @Override
   public double lengthSquared() {
      return this.x*this.x + this.y*this.y + this.z*this.z;
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

   public Vector3d cross(final Vector3d v1, final Vector3d v2) {
      final double x = v1.y * v2.z - v1.z * v2.y;
      final double y = v1.z * v2.x - v1.x * v2.z;
      final double z = v1.x * v2.y - v1.y * v2.x;

      this.set(x, y, z);
      return this;
   }

   public Vector3d cross(final Vector3d other) {
      final double x = this.y * other.z - this.z * other.y;
      final double y = this.z * other.x - this.x * other.z;
      final double z = this.x * other.y - this.y * other.x;

      return new Vector3d(x,y,z);
   }

   public double dot(final Vector3d other) {
      return this.x * other.x + this.y * other.y + this.z * other.z;
   }

   /**
    * Returns angle between two vectors in radians.
    * 
    * @param other
    * @return
    */
   public double angleRadians(final Vector3d other) {
      return Math.acos(this.dot(other));
   }

   /**
    * Returns angle between two vectors in degrees.
    * 
    * @param other
    * @return
    */
   public double angleDegrees(final Vector3d other) {
      return Math.toDegrees(this.angleRadians(other));
   }

   public double angleSigned(final Vector3d other) {
      final double angle = this.angleRadians(other);
      final double sign = (this.dot(other)) >= 0 ? 1 : -1;

      return sign * angle;
   }

   public void get(double[] retVal) {
      retVal[0] = this.x;
      retVal[1] = this.y;
      retVal[2] = this.z;
   }
   
   public Vector3d scale(final double scalar) {
      this.x *= scalar;
      this.y *= scalar;
      this.z *= scalar;
      
      return this;
   }
}
