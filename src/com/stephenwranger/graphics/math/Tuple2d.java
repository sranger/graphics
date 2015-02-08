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
}
