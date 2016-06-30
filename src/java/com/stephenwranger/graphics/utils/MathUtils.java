package com.stephenwranger.graphics.utils;

import java.util.List;

import com.stephenwranger.graphics.bounds.BoundingBox;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;

// TODO: use apache SI/NonSI
public class MathUtils {
   public static final double  YEARS_TO_SECONDS            = 3.15569e7;
   public static final double  DAYS_TO_SECONDS             = 86400;

   public static final double  SECONDS_TO_NANOSECONDS      = 1e9;
   public static final double  SECONDS_TO_MILLISECONDS     = 1000;
   public static final double  SECONDS_TO_MINUTES          = 0.0166667;
   public static final double  SECONDS_TO_HOURS            = 0.000277778;
   public static final double  SECONDS_TO_DAYS             = 1.1574e-5;
   public static final double  SECONDS_TO_YEARS            = 3.1689e-8;

   public static final double  MILLISECONDS_TO_NANOSECONDS = 1e6;
   public static final double  MILLISECONDS_TO_SECONDS     = 0.001;
   public static final double  MILLISECONDS_TO_MINUTES     = 1.6667e-5;
   public static final double  MILLISECONDS_TO_HOURS       = 2.7778e-7;
   public static final double  MILLISECONDS_TO_DAYS        = 1.15741e-8;
   public static final double  MILLISECONDS_TO_YEARS       = 3.1689e-11;

   public static final double  NANOSECONDS_TO_SECONDS      = 1e-9;
   public static final double  NANOSECONDS_TO_MILLISECONDS = 1e-6;

   public static final double  GRAVITATIONAL_CONSTANT      = 6.67384e-11;          // m3 kg-1 s-2;
   public static final double  METERS_TO_CENTIMETERS       = 100;

   public static final double  TWO_PI                      = Math.PI * 2.0;
   public static final double  HALF_PI                     = Math.PI / 2.0;

   public static final double  ASTRONOMICAL_UNIT_METERS    = 149597870700.0;

   public static final Tuple3d X_AXIS                      = new Tuple3d(1, 0, 0);
   public static final Tuple3d Y_AXIS                      = new Tuple3d(0, -1, 0);
   public static final Tuple3d Z_AXIS                      = new Tuple3d(0, 0, 1);

   public static final double  EPSILON                     = 1e-4;
   public static final float   EPSILON_F                   = 1e-4f;

   private MathUtils() {
      // static only
   }

   public static double clamp(final double min, final double max, final double value) {
      return Math.min(max, Math.max(min, value));
   }

   public static BoundingBox getKeyframeBounds(final List<Keyframe> keyframes, final double oversize) {
      double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
      double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;

      for (final Keyframe frame : keyframes) {
         minX = Math.min(minX, frame.position.x);
         minY = Math.min(minY, frame.position.y);
         minZ = Math.min(minZ, frame.position.z);
         maxX = Math.max(maxX, frame.position.x);
         maxY = Math.max(maxY, frame.position.y);
         maxZ = Math.max(maxZ, frame.position.z);
      }

      return new BoundingBox(minX - (maxX - minX) * (oversize - 1.0), minY - (maxY - minY) * (oversize - 1.0), minZ - (maxZ - minZ) * (oversize - 1.0), maxX + (maxX - minX) * (oversize - 1.0), maxY
            + (maxY - minY) * (oversize - 1.0), maxZ + (maxZ - minZ) * (oversize - 1.0));
   }

   public static Double getDouble(final String value, final Double defaultValue) {
      return value == null || value.length() == 0 ? defaultValue : Double.valueOf(value);
   }
   
   public static int getSign(final double value) {
      return (value < 0.0) ? -1 : 1;
   }

   /**
    * Computes the normal from the given vertices.
    *
    * @param vertices
    *           A array matrix of size 3x3 with three x,y,z triplets in counter-clockwise order
    * @return The normal of the given triangle
    */
   public static double[] computeNormal(final double[][] vertices, final int[] indices) {
      final Vector3d e1 = new Vector3d(vertices[indices[1]][0] - vertices[indices[0]][0], vertices[indices[1]][1] - vertices[indices[0]][1],
            vertices[indices[1]][2] - vertices[indices[0]][2]);
      final Vector3d e2 = new Vector3d(vertices[indices[2]][0] - vertices[indices[0]][0], vertices[indices[2]][1] - vertices[indices[0]][1],
            vertices[indices[2]][2] - vertices[indices[0]][2]);
      final Vector3d normal = new Vector3d();
      normal.cross(e1, e2);
      normal.normalize();

      final double[] retVal = new double[3];
      normal.get(retVal);

      return retVal;
   }

   /**
    * Returns the distance between the two given vertices.
    *
    * @param p1
    *           Vertex 1
    * @param p2
    *           Vertex 2
    * @return The distance between p1 and p2
    */
   public static double getDistance(final double[] p1, final double[] p2) {
      return Math.sqrt(MathUtils.getDistanceSquared(p1, p2));
   }

   /**
    * Returns the squared distance between the two given vertices.
    *
    * @param p1
    *           Vertex 1
    * @param p2
    *           Vertex 2
    * @return The distance between p1 and p2
    */
   public static double getDistanceSquared(final double[] p1, final double[] p2) {
      return (p1[0] - p2[0]) * (p1[0] - p2[0]) + (p1[1] - p2[1]) * (p1[1] - p2[1]) + (p1[2] - p2[2]) * (p1[2] - p2[2]);
   }

   /**
    * Returns the distance between the two given vertices.
    *
    * @param p1
    *           Vertex 1
    * @param p2
    *           Vertex 2
    * @return The distance between p1 and p2
    */
   public static double getDistance(final Vector3d p1, final Vector3d p2) {
      return Math.sqrt(MathUtils.getDistanceSquared(p1, p2));
   }

   /**
    * Returns the squared distance between the two given vertices.
    *
    * @param p1
    *           Vertex 1
    * @param p2
    *           Vertex 2
    * @return The distance between p1 and p2
    */
   public static double getDistanceSquared(final Vector3d p1, final Vector3d p2) {
      return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y) + (p1.z - p2.z) * (p1.z - p2.z);
   }

   /**
    * Returns the distance between the two given vertices.
    *
    * @param p1
    *           Vertex 1
    * @param p2
    *           Vertex 2
    * @return The distance between p1 and p2
    */
   public static double getDistance(final Vector3d p1, final double[] p2) {
      return Math.sqrt(MathUtils.getDistanceSquared(p1, p2));
   }

   /**
    * Returns the squared distance between the two given vertices.
    *
    * @param p1
    *           Vertex 1
    * @param p2
    *           Vertex 2
    * @return The distance between p1 and p2
    */
   public static double getDistanceSquared(final Vector3d p1, final double[] p2) {
      return (p1.x - p2[0]) * (p1.x - p2[0]) + (p1.y - p2[1]) * (p1.y - p2[1]) + (p1.z - p2[2]) * (p1.z - p2[2]);
   }
   
   /**
    * Returns true if any component of the given {@link Tuple3d} is NaN.
    * 
    * @param tuple
    * @return
    */
   public static boolean isNaN(final Tuple3d tuple) {
      return Double.isNaN(tuple.x) || Double.isNaN(tuple.y) || Double.isNaN(tuple.z);
   }
   
   /**
    * Returns true if any component of the given {@link Tuple3d} is Infinite.
    * 
    * @param tuple
    * @return
    */
   public static boolean isInfinite(final Tuple3d tuple) {
      return Double.isInfinite(tuple.x) || Double.isInfinite(tuple.y) || Double.isInfinite(tuple.z);
   }
   
   /**
    * Return true if all components of the given {@link Tuple3d} are finite.
    * 
    * @param tuple
    * @return
    */
   public static boolean isFinite(final Tuple3d tuple) {
      return Double.isFinite(tuple.x) && Double.isFinite(tuple.y) && Double.isFinite(tuple.z);
   }
}
