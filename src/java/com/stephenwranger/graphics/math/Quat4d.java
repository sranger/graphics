package com.stephenwranger.graphics.math;

import com.jogamp.opengl.math.Quaternion;

public class Quat4d extends Quaternion {
   // TODO: implement own quaternion class

   public Quat4d() {
      super(0,0,0,1);
   }

   public Quat4d(final Quat4d quaternion) {
      super(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
   }

   /**
    * Creates a Quaternion from the rotation around the given axis by the angle (in degrees).
    * 
    * @param axis
    *           the axis to rotate about
    * @param angle
    *           the angle to rotate (in degrees)
    */
   public Quat4d(final Tuple3d axis, final double angle) {
      super(axis.toFloatArray(), (float) Math.toRadians(angle));
   }

   @Override
   public String toString() {
      return "(" + x + ", " + y + ", " + z + ", " + w + ")";
   }

   public void mult(final Tuple3d offset) {
      final float[] vector = this.mult(offset.toFloatArray());
      offset.x = vector[0];
      offset.y = vector[1];
      offset.z = vector[2];
   }
   
   public void set(final double x, final double y, final double z, final double w) {
      this.x = (float) x;
      this.y = (float) y;
      this.z = (float) z;
      this.w = (float) w;
   }
}
