package com.stephenwranger.graphics.math;

import java.nio.FloatBuffer;

public class Tuple1d {
   public double x;

   public Tuple1d() {
      this.x = 0;
   }

   public Tuple1d(final double x) {
      this.x = x;
   }

   public Tuple1d(final Tuple1d position) {
      this.x = position.x;
   }

   public void set(final double x) {
      this.x = x;
   }

   public void set(final Tuple1d tuple) {
      this.x = tuple.x;
   }

   public float[] toFloatArray() {
      return new float[] { (float) this.x };
   }

   public double distance(final Tuple1d other) {
      return Math.abs(this.x - other.x);
   }

   @Override
   public String toString() {
      return "(" + this.x + ")";
   }

   public void putInto(final FloatBuffer buffer) {
      buffer.put((float) this.x);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(this.x);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (this.getClass() != obj.getClass()) {
         return false;
      }
      final Tuple1d other = (Tuple1d) obj;
      if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
         return false;
      }
      return true;
   }
}
