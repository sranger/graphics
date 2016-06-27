package com.stephenwranger.graphics.math;

import com.stephenwranger.graphics.utils.MathUtils;

public class SphericalCoordinate {
   public static final Vector3d AZIMUTH_ROTATION_AXIS = new Vector3d(0,1,0);
   public static final Vector3d ELEVATION_ROTATION_AXIS = new Vector3d(1,0,0);
   
   /** Azimuth coordinate in radians. */
   public double azimuth;
   /** Elevation coordinate in radians. */
   public double elevation;
   /** Range coordinate in meters. */
   private double range;
   
   public SphericalCoordinate(final double azimuthRads, final double elevationRads, final double rangeMeters) {
      this.update(azimuthRads, elevationRads, rangeMeters);
   }
   
   public SphericalCoordinate(final SphericalCoordinate coordinate) {
      this.update(coordinate.azimuth, coordinate.elevation, coordinate.range);
   }
   
   public void update(final double azimuthRads, final double elevationRads, final double stepRange) {
      if(elevationRads != 0) {
         this.azimuth += azimuthRads;
         // make sure it stays between [-PI, PI]
         if (this.azimuth < -Math.PI) {
            this.azimuth += MathUtils.TWO_PI;
         } else if (this.azimuth > Math.PI) {
            this.azimuth -= MathUtils.TWO_PI;
         }
      }
   
      if(azimuthRads != 0) {
         this.elevation += elevationRads;
         
         // make sure it stays above the horizon and below the zenith
         this.elevation = MathUtils.clamp(Math.toRadians(0), Math.toRadians(90), this.elevation);
      }
      
      // make sure it stays above the anchor
      this.range = Math.max(0.0, this.range + stepRange);
   }
   
   public Quat4d getOrientation() {
      return SphericalCoordinate.getOrientation(this.azimuth, this.elevation);
   }
   
   public double getAzimuth() {
      return this.azimuth;
   }
   
   public double getElevation() {
      return this.elevation;
   }
   
   public double getRange() {
      return this.range;
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
