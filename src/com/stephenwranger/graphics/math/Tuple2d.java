package com.stephenwranger.graphics.math;

import java.nio.FloatBuffer;

public class Tuple2d extends Tuple1d {
   public double y;

   public Tuple2d() {
      super(0);

      this.y = 0;
   }

   public Tuple2d(final double x, final double y) {
      super(x);

      this.y = y;
   }

   public Tuple2d(final Tuple2d position) {
      super(position);

      this.y = position.y;
   }

   public void set(final double x, final double y) {
      super.set(x);

      this.y = y;
   }

   public void set(final Tuple2d tuple) {
      super.set(tuple);

      this.y = tuple.y;
   }

   public double distanceSquared(final Tuple2d other) {
      return (this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y);
   }

   public double distance(final Tuple2d other) {
      return Math.sqrt(this.distanceSquared(other));
   }

   @Override
   public float[] toFloatArray() {
      return new float[] { (float) this.x, (float) this.y };
   }

   @Override
   public String toString() {
      return "(" + this.x + ", " + this.y + ")";
   }

   @Override
   public void putInto(final FloatBuffer buffer) {
      buffer.put((float) this.x).put((float) this.y);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      long temp;
      temp = Double.doubleToLongBits(this.y);
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
      final Tuple2d other = (Tuple2d) obj;
      if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
         return false;
      }
      return true;
   }
}
