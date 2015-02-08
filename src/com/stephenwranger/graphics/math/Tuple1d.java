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
   
   @Override
   public String toString() {
      return "(" + this.x + ")";
   }
   
   public void putInto(final FloatBuffer buffer) {
      buffer.put((float) this.x);
   }
}
