package com.stephenwranger.graphics.math;

import java.nio.FloatBuffer;

public class Tuple4d extends Tuple3d {
   public double w;

   public Tuple4d(final double x, final double y, final double z, final double w) {
      super(x, y, z);

      this.w = w;
   }

   public Tuple4d(final Tuple4d position) {
      super(position);

      this.w = position.w;
   }

   public void set(final double x, final double y, final double z, final double w) {
      super.set(x, y, z);

      this.w = w;
   }

   public void set(final Tuple4d tuple) {
      super.set(tuple);

      this.w = tuple.w;
   }

   @Override
   public float[] toFloatArray() {
      return new float[] { (float) this.x, (float) this.y, (float) this.z, (float) this.w };
   }

   @Override
   public String toString() {
      return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
   }

   @Override
   public void putInto(final FloatBuffer buffer) {
      buffer.put((float) this.x).put((float) this.y).put((float) this.z).put((float) this.w);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      long temp;
      temp = Double.doubleToLongBits(this.w);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (this.getClass() != obj.getClass()) {
         return false;
      }
      final Tuple4d other = (Tuple4d) obj;
      if (Double.doubleToLongBits(this.w) != Double.doubleToLongBits(other.w)) {
         return false;
      }
      return true;
   }
}
