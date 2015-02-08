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
}
