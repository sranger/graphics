package com.stephenwranger.graphics.math;

import java.nio.FloatBuffer;

public class Tuple4d {
   public double x;
   public double y;
   public double z;
   public double w;

   public Tuple4d() {
      this.x = 0;
      this.y = 0;
      this.z = 0;
      this.w = 0;
   }

   public Tuple4d(final double x, final double y, final double z, final double w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   public Tuple4d(final double[] xyzw) {
      this.x = xyzw[0];
      this.y = xyzw[1];
      this.z = xyzw[2];
      this.w = xyzw[3];
   }

   public Tuple4d(final Tuple4d position) {
      this.x = position.x;
      this.y = position.y;
      this.z = position.z;
      this.w = position.w;
   }

   public void set(final double x, final double y, final double z, final double w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   public void set(final Tuple4d tuple) {
      this.x = tuple.x;
      this.y = tuple.y;
      this.z = tuple.z;
      this.w = tuple.w;
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
      temp = Double.doubleToLongBits(w);
      result = prime * result + (int) (temp ^ (temp >>> 32));
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
      Tuple4d other = (Tuple4d) obj;
      if (Double.doubleToLongBits(w) != Double.doubleToLongBits(other.w))
         return false;
      if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
         return false;
      if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
         return false;
      if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
         return false;
      return true;
   }
}
