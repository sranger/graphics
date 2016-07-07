package com.stephenwranger.graphics.bounds;

import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.intersection.IntersectionUtils;
import com.stephenwranger.graphics.math.intersection.Triangle3d;

/**
 * The {@link TrianglePrismVolume} acts as an axis aligned {@link BoundingBox} except for the contains method which 
 * checks first the super class' bounds and then checks the triangle prism geometry directly.
 * 
 * @author rangers
 *
 */
public class TrianglePrismVolume extends BoundingBox {
   private final Triangle3d top;
   private final Triangle3d bottom;
   private final Triangle3d[] faces = new Triangle3d[8];

   /**
    * Creates a new {@link TrianglePrismVolume} with the given vertices in top as one triangular face and the vertices
    * in bottom as the other. It is assumed that top[n] is connected to bottom[n] and top is wound counter-clockwise as
    * looking from behind the face.
    * 
    * @param top
    * @param bottom
    */
   public TrianglePrismVolume(final Tuple3d[] top, final Tuple3d[] bottom) {
      super(getMin(top, bottom), getMax(top, bottom));

      this.top = new Triangle3d(top[1], top[0], top[2]);
      this.bottom = new Triangle3d(bottom[0], bottom[1], bottom[2]);
      this.faces[0] = new Triangle3d(top[2], top[0], bottom[0]);
      this.faces[1] = new Triangle3d(top[2], bottom[0], bottom[2]);
      
      this.faces[2] = new Triangle3d(top[1], top[2], bottom[2]);
      this.faces[3] = new Triangle3d(bottom[1], top[1], bottom[2]);
      
      this.faces[4] = new Triangle3d(top[0], top[1], bottom[1]);
      this.faces[5] = new Triangle3d(bottom[0], top[0], bottom[1]);

      // added so loop can be used for contains check
      this.faces[6] = this.top;
      this.faces[7] = this.bottom;
      
//      final Tuple3d center = TupleMath.average(TupleMath.average(top), TupleMath.average(bottom));
//      final boolean contains = this.contains(center);
//      
//      if(!contains) {
//         System.out.println(center.x + "," + center.y + "," + center.z + ",0");
//         int ctr = 1;
//         
//         for(final Triangle3d face : this.faces) {
//            final Tuple3d[] corners = face.getCorners();
//            
//            for(final Tuple3d corner : corners) {
//               System.out.println(corner.x + "," + corner.y + "," + corner.z + "," + ctr);
//            }
//            
//            ctr++;
//         }
//      }
   }
   
   public Triangle3d[] getFaces() {
      return this.faces;
   }
   
   public Triangle3d getTopFace() {
      return this.top;
   }
   
   public Triangle3d getBottomFace() {
      return this.bottom;
   }
   
   public double getDepth() {
      return this.top.distanceToPoint(this.bottom.getBarycentricOrigin());
   }
   
//   @Override
//   public boolean contains(final Tuple3d point) {
//      // check the easier contains computation as an early termination step
//      if(super.contains(point)) {
//         final Tuple3d topBarycentric = this.top.getBarycentricCoordinate(point);
//         
//         // check if its barycentric coordinates fall on the top face
//         if(IntersectionUtils.isLessOrEqual(topBarycentric.x + topBarycentric.y + topBarycentric.z, 1.0)) {
//            final Tuple3d bottomBarycentric = this.bottom.getBarycentricCoordinate(point);
//
//            // check if its barycentric coordinates fall on the bottom face
//            if(IntersectionUtils.isLessOrEqual(bottomBarycentric.x + bottomBarycentric.y + bottomBarycentric.z, 1.0)) {
//               final double totalDistance = this.top.distance(this.bottom.getBarycentricOrigin());
//               final double toTop = this.top.distance(point);
//               final double toBottom = this.bottom.distance(point);
//               
//               // make sure it's between the top and bottom faces
//               return IntersectionUtils.isEqual(totalDistance, toTop + toBottom);
//            }
//         }
//      }
//      
//      return false;
//   }
   
   @Override
   public boolean contains(final Tuple3d point) {
      int ctr = 0;

      // check the easier contains computation as an early termination step
      if(super.contains(point)) {
         for(int i = 0; i < this.faces.length; i++) {
            final Triangle3d face = this.faces[i];
            
            if(face.isInside(point)) {
               ctr++;
            }
         }
      }
      
      return ctr == this.faces.length;
   }
   
   /**
    * Returns a double array containing barycentric coordinates and depth from top face.
    * 
    * @param xyz
    * @return u,v,w,depth coordinates
    */
   public double[] getBarycentricCoordinate(final Tuple3d xyz) {
      final Tuple3d uvw = this.top.getBarycentricCoordinate(xyz);
      final double depth = this.top.distanceToPoint(xyz);
      
      return new double[] { uvw.x, uvw.y, uvw.z, depth };
   }
   
   private static Tuple3d getMin(final Tuple3d[] top, final Tuple3d[] bottom) {
      final Tuple3d min = new Tuple3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
      
      for(final Tuple3d corner : top) {
         min.x = Math.min(min.x, corner.x);
         min.y = Math.min(min.y, corner.y);
         min.z = Math.min(min.z, corner.z);
      }
      
      for(final Tuple3d corner : bottom) {
         min.x = Math.min(min.x, corner.x);
         min.y = Math.min(min.y, corner.y);
         min.z = Math.min(min.z, corner.z);
      }
      
      return min;
   }
   
   private static Tuple3d getMax(final Tuple3d[] top, final Tuple3d[] bottom) {
      final Tuple3d max = new Tuple3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
      
      for(final Tuple3d corner : top) {
         max.x = Math.max(max.x, corner.x);
         max.y = Math.max(max.y, corner.y);
         max.z = Math.max(max.z, corner.z);
      }
      
      for(final Tuple3d corner : bottom) {
         max.x = Math.max(max.x, corner.x);
         max.y = Math.max(max.y, corner.y);
         max.z = Math.max(max.z, corner.z);
      }
      
      return max;
   }
}
