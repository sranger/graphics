package com.stephenwranger.graphics.math;

import java.nio.FloatBuffer;

public class Tuple2d {
   public double x;
   public double y;

   public Tuple2d() {
      this.x = 0;
      this.y = 0;
   }

   public Tuple2d(final double x, final double y) {
      this.x = x;
      this.y = y;
   }

   public Tuple2d(final Tuple2d position) {
      this.x = position.x;
      this.y = position.y;
   }

   public void set(final double x, final double y) {
      this.x = x;
      this.y = y;
   }

   public void set(final Tuple2d tuple) {
      this.x = tuple.x;
      this.y = tuple.y;
   }

   public double distanceSquared(final Tuple2d other) {
      return (this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y);
   }

   public double distance(final Tuple2d other) {
      return Math.sqrt(this.distanceSquared(other));
   }

   public Tuple2d add(final Tuple2d other) {
      this.x += other.x;
      this.y += other.y;

      return this;
   }

   public Tuple2d add(final Tuple2d o1, final Tuple2d o2) {
      this.x = o1.x + o2.x;
      this.y = o1.y + o2.y;

      return this;
   }

   public Tuple2d subtract(final Tuple2d other) {
      this.x -= other.x;
      this.y -= other.y;

      return this;
   }

   public Tuple2d subtract(final Tuple2d o1, final Tuple2d o2) {
      this.x = o1.x - o2.x;
      this.y = o1.y - o2.y;

      return this;
   }

   public float[] toFloatArray() {
      return new float[] { (float) this.x, (float) this.y };
   }

   @Override
   public String toString() {
      return "(" + this.x + ", " + this.y + ")";
   }

   public void putInto(final FloatBuffer buffer) {
      buffer.put((float) this.x).put((float) this.y);
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
      Tuple2d other = (Tuple2d) obj;
      if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
         return false;
      if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
         return false;
      return true;
   }
}
