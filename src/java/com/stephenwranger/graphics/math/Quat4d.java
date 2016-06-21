package com.stephenwranger.graphics.math;

import com.jogamp.opengl.math.Quaternion;

public class Quat4d extends Quaternion {
   // TODO: implement own quaternion class

   public Quat4d() {
      super(0,0,0,1);
   }

   public Quat4d(final Quat4d quaternion) {
      super(quaternion.getX(), quaternion.getY(), quaternion.getZ(), quaternion.getW());
   }

   /**
    * Creates a Quaternion from the rotation around the given axis by the angle (in degrees).<br/><br/>
    * 
    * http://www.euclideanspace.com/maths/geometry/rotations/conversions/angleToQuaternion/
    * 
    * @param axis
    *           the axis to rotate about
    * @param angle
    *           the angle to rotate (in degrees)
    */
   public Quat4d(final Tuple3d axis, final double angle) {
      final double angleRads = Math.toRadians(angle);
      final double qx = axis.x * Math.sin(angleRads / 2.0);
      final double qy = axis.y * Math.sin(angleRads / 2.0);
      final double qz = axis.z * Math.sin(angleRads / 2.0);
      final double qw = Math.cos(angleRads / 2.0);
      
      this.set(qx, qy, qz, qw);
   }

   @Override
   public String toString() {
      return "(" + this.getX() + ", " + this.getY() + ", " + this.getZ() + ", " + this.getW() + ")";
   }

   public void mult(final Tuple3d offset) {
      final float[] vector = new float[3];
      this.rotateVector(vector, 0, offset.toFloatArray(), 0);
      offset.x = vector[0];
      offset.y = vector[1];
      offset.z = vector[2];
   }
   
   public void set(final double x, final double y, final double z, final double w) {
      super.set((float) x, (float) y, (float) z, (float) w);
   }
}
