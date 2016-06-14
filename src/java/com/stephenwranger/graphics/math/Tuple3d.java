package com.stephenwranger.graphics.math;

import java.nio.FloatBuffer;

public class Tuple3d {
   public double x;
   public double y;
   public double z;

   public Tuple3d() {
      this.x = 0;
      this.y = 0;
      this.z = 0;
   }

   public Tuple3d(final double x, final double y, final double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Tuple3d(final double[] xyz) {
      this.x = xyz[0];
      this.y = xyz[1];
      this.z = xyz[2];
   }

   public Tuple3d(final Tuple3d position) {
      this.x = position.x;
      this.y = position.y;
      this.z = position.z;
   }

   public void set(final double x, final double y, final double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void set(final Tuple3d tuple) {
      this.x = tuple.x;
      this.y = tuple.y;
      this.z = tuple.z;
   }

   public Tuple3d add(final Tuple3d other) {
      this.x += other.x;
      this.y += other.y;
      this.z += other.z;

      return this;
   }

   public Tuple3d add(final Tuple3d o1, final Tuple3d o2) {
      this.x = o1.x + o2.x;
      this.y = o1.y + o2.y;
      this.z = o1.z + o2.z;

      return this;
   }

   public Tuple3d subtract(final Tuple3d other) {
      this.x -= other.x;
      this.y -= other.y;
      this.z -= other.z;

      return this;
   }

   public Tuple3d subtract(final Tuple3d o1, final Tuple3d o2) {
      this.x = o1.x - o2.x;
      this.y = o1.y - o2.y;
      this.z = o1.z - o2.z;

      return this;
   }

   public double distanceSquared(final Tuple3d other) {
      return (this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y) + (this.z - other.z) * (this.z - other.z);
   }

   public double distance(final Tuple3d other) {
      return Math.sqrt(this.distanceSquared(other));
   }

   public float[] toFloatArray() {
      return new float[] { (float) this.x, (float) this.y, (float) this.z };
   }

   @Override
   public String toString() {
      return "(" + this.x + ", " + this.y + ", " + this.z + ")";
   }

   public void putInto(final FloatBuffer buffer) {
      buffer.put((float) this.x).put((float) this.y).put((float) this.z);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(x);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(y);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(z);
      result = prime * result + (int) (temp ^ (temp >>> 32));
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
      Tuple3d other = (Tuple3d) obj;
      if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
         return false;
      if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
         return false;
      if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
         return false;
      return true;
   }
}
