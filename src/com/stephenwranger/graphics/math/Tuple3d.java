package com.stephenwranger.graphics.math;

import java.nio.FloatBuffer;

public class Tuple3d extends Tuple2d {
   public double z;

   public Tuple3d() {
      super(0, 0);

      this.z = 0;
   }

   public Tuple3d(final double x, final double y, final double z) {
      super(x, y);

      this.z = z;
   }

   public Tuple3d(final double[] xyz) {
      super(xyz[0], xyz[1]);

      this.z = xyz[2];
   }

   public Tuple3d(final Tuple3d position) {
      super(position);

      this.z = position.z;
   }

   public void set(final double x, final double y, final double z) {
      super.set(x, y);

      this.z = z;
   }

   public void set(final Tuple3d tuple) {
      super.set(tuple);

      this.z = tuple.z;
   }

   public double distanceSquared(final Tuple3d other) {
      return (this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y) + (this.z - other.z) * (this.z - other.z);
   }

   public double distance(final Tuple3d other) {
      return Math.sqrt(this.distanceSquared(other));
   }

   @Override
   public float[] toFloatArray() {
      return new float[] { (float) this.x, (float) this.y, (float) this.z };
   }

   @Override
   public String toString() {
      return "(" + this.x + ", " + this.y + ", " + this.z + ")";
   }

   @Override
   public void putInto(final FloatBuffer buffer) {
      buffer.put((float) this.x).put((float) this.y).put((float) this.z);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      long temp;
      temp = Double.doubleToLongBits(this.z);
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
      final Tuple3d other = (Tuple3d) obj;
      if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
         return false;
      }
      return true;
   }
}
