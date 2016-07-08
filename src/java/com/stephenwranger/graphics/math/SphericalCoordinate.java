package com.stephenwranger.graphics.math;

import com.stephenwranger.graphics.utils.MathUtils;

public class SphericalCoordinate {
   public static final Vector3d AZIMUTH_ROTATION_AXIS = new Vector3d(0,1,0);
   public static final Vector3d ELEVATION_ROTATION_AXIS = new Vector3d(1,0,0);
   
   /** Azimuth coordinate in radians. */
   private double azimuth;
   /** Elevation coordinate in radians. */
   private double elevation;
   /** Range coordinate in meters. */
   private double range;
   
   public SphericalCoordinate(final double azimuthRads, final double elevationRads, final double rangeMeters) {
      this.update(azimuthRads, elevationRads, rangeMeters);
   }
   
   public SphericalCoordinate(final SphericalCoordinate coordinate) {
      this.update(coordinate.azimuth, coordinate.elevation, coordinate.range);
   }
   
   public void update(final double stepAzimuthRads, final double stepElevationRads, final double stepRange) {
      if(stepElevationRads != 0) {
         this.azimuth += stepAzimuthRads;
         // make sure it stays between [-PI, PI]
         if (this.azimuth < -Math.PI) {
            this.azimuth += MathUtils.TWO_PI;
         } else if (this.azimuth > Math.PI) {
            this.azimuth -= MathUtils.TWO_PI;
         }
      }
   
      if(stepAzimuthRads != 0) {
         this.elevation += stepElevationRads;
         
         // make sure it stays above the horizon and below the zenith
         this.elevation = MathUtils.clamp(Math.toRadians(0), Math.toRadians(90), this.elevation);
      }
      
      // make sure it stays above the anchor
      this.range = Math.max(10.0, this.range + stepRange);
   }
   
   public Quat4d getOrientation() {
      return SphericalCoordinate.getOrientation(this.azimuth, this.elevation);
   }
   
   public double getAzimuth() {
      return this.azimuth;
   }

   public void setAzimuth(final double newAzimuthRads) {
      this.update(newAzimuthRads - this.azimuth, 0, 0);      
   }
   
   public double getElevation() {
      return this.elevation;
   }

   public void setElevation(final double newElevationRads) {
      this.update(0, newElevationRads - this.elevation, 0);      
   }
   
   public double getRange() {
      return this.range;
   }

   public void setRange(final double newRangeMeters) {
      this.update(0, 0, newRangeMeters - this.range);   
   }
   
   @Override
   public String toString() {
      return "(" + this.azimuth + ", " + this.elevation + ", " + this.range + ")";
   }
   
   public static Quat4d getOrientation(final double azimuthRads, final double elevationRads) {
      final Quat4d orientation = new Quat4d();
      orientation.mult(new Quat4d(AZIMUTH_ROTATION_AXIS, Math.toDegrees(azimuthRads)));
      orientation.mult(new Quat4d(ELEVATION_ROTATION_AXIS, Math.toDegrees(elevationRads)));
      
      return orientation;
   }
}
