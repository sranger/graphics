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
   
   public void rotateVector(final Tuple3d vector) {
      final double x = this.getX();
      final double y = this.getY();
      final double z = this.getZ();
      final double w = this.getW();
      
      final double conjResultX = vector.x * w + 0 * -x + vector.y * -z - vector.z * -y;
      final double conjResultY = vector.y * w + 0 * -y + vector.z * -x - vector.x * -z;
      final double conjResultZ = vector.z * w + 0 * -z + vector.x * -y - vector.y * -x;
      final double conjResultW = 0 * w - vector.x * -x - vector.y * -y - vector.z * -z;

      vector.x = x * conjResultW + w * conjResultX + y * conjResultZ - z * conjResultY;
      vector.y = y * conjResultW + w * conjResultY + z * conjResultX - x * conjResultZ;
      vector.z = z * conjResultW + w * conjResultZ + x * conjResultY - y * conjResultX;
   }
   
   public double normal() {
      final double tx = this.getX();
      final double ty = this.getY();
      final double tz = this.getZ();
      final double tw = this.getW();
      
      return (tx * tx + ty * ty + tz * tz + tw * tw);
   } // end normal
   
   public void circularInterpolate(final Quat4d from, final Quat4d to, final double alpha) {
      // algorithm from Hoggar
      final double fx = from.getX();
      final double fy = from.getY();
      final double fz = from.getZ();
      final double fw = from.getW();
      final double tx = to.getX();
      final double ty = to.getY();
      final double tz = to.getZ();
      final double tw = to.getW();
      
      
      double x1 = 0;
      double y1 = 0;
      double z1 = 0;
      double w1 = 0;

      final double norm = Math.sqrt(to.normal());
      normalize();

      if (norm != 0) {
         // zero-div may occur.
         x1 = tx / norm;
         y1 = ty / norm;
         z1 = tz / norm;
         w1 = tw / norm;
      } // end if

      // dp = dotProduct (aka cosine)
      final double dp = fx * x1 + fy * y1 + fz * z1 + fw * w1;

      // same quaternion (avoid domain error)
      if (1.0 > Math.abs(dp)) {
         final double theta = Math.acos(dp);
         final double sin_t = Math.sin(theta);

         // same quaternion (avoid zero-div)
         if (sin_t != 0.0) {
            final double s = Math.sin((1.0 - alpha) * theta) / sin_t;
            final double result = Math.sin(alpha * theta) / sin_t;

            // set values
            final double x = (float) (s * fx + result * x1);
            final double y = (float) (s * fy + result * y1);
            final double z = (float) (s * fz + result * z1);
            final double w = (float) (s * fw + result * w1);
            
            this.set(x,y,z,w);
         }
      }
   }
   
   public void set(final double x, final double y, final double z, final double w) {
      super.set((float) x, (float) y, (float) z, (float) w);
   }
}
