package com.stephenwranger.graphics.math;

public class SphericalCoordinate {
   /** Azimuth coordinate in radians. */
   public double azimuth;
   /** Elevation coordinate in radians. */
   public double elevation;
   /** Range coordinate in meters. */
   public double range;
   
   public SphericalCoordinate(final double azimuthRads, final double elevationRads, final double rangeMeters) {
      this.set(azimuthRads, elevationRads, rangeMeters);
   }
   
   public SphericalCoordinate(final SphericalCoordinate coordinate) {
      this(coordinate.azimuth, coordinate.elevation, coordinate.range);
   }
   
   public void set(final SphericalCoordinate coordinate) {
      this.set(coordinate.azimuth, coordinate.elevation, coordinate.range);
   }
   
   public void set(final double azimuthRads, final double elevationRads, final double rangeMeters) {
      this.azimuth = azimuthRads;
      this.elevation = elevationRads;
      this.range = rangeMeters;
   }
   
   @Override
   public String toString() {
      return "(" + this.azimuth + ", " + this.elevation + ", " + this.range + ")";
   }
}
