package com.stephenwranger.graphics.bounds;

import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.math.intersection.Plane;
import com.stephenwranger.graphics.math.intersection.Triangle3d;
import com.stephenwranger.graphics.utils.TupleMath;

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
   private final double depth;
   private final Tuple3d center;

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
      
      this.depth = Plane.distanceToPlane(this.top.getNormal(), this.top.getCorners()[0], bottom[0].x, bottom[0].y, bottom[0].z);

      final Tuple3d topCenter = TupleMath.average(top);
      final Tuple3d bottomCenter = TupleMath.average(bottom);
      final Vector3d toCenter = Vector3d.getVector(topCenter, bottomCenter, true);
      toCenter.scale(this.depth / 2.0);
      
      this.center = new Tuple3d(topCenter);
      this.center.add(toCenter);
   }
   
   @Override
   public Tuple3d getCenter() {
      return new Tuple3d(this.center);
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
      
//      final double[] uvwDepth = this.getBarycentricCoordinate(point);
//      
//      final double uvw = uvwDepth[0] + uvwDepth[1] + uvwDepth[2];
//      final double depth = uvwDepth[3];
//      
//      return IntersectionUtils.isLessOrEqual(uvw, 1.0) && IntersectionUtils.isClampedInclusive(depth, 0.0, 1.0);
   }
   
   /**
    * Returns a double array containing barycentric coordinates and normalized depth from top face.
    * 
    * @param xyz
    * @return u,v,w,depth coordinates
    */
   public double[] getBarycentricCoordinate(final Tuple3d xyz) {
      final Tuple3d uvw = this.top.getBarycentricCoordinate(xyz);
      final double depth = this.top.distanceToPoint(xyz) / this.depth;
      
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

   @Override
   public BoundingVolume offset(final Tuple3d offset) {
      final Tuple3d[] triangleTop = this.top.getCorners();
      final Tuple3d[] top = new Tuple3d[3];
      top[0] = TupleMath.sub(triangleTop[1], offset);
      top[1] = TupleMath.sub(triangleTop[0], offset);
      top[2] = TupleMath.sub(triangleTop[2], offset);

      final Tuple3d[] triangleBottom = this.top.getCorners();
      final Tuple3d[] bottom = new Tuple3d[3];
      bottom[0] = TupleMath.sub(triangleBottom[0], offset);
      bottom[1] = TupleMath.sub(triangleBottom[1], offset);
      bottom[2] = TupleMath.sub(triangleBottom[2], offset);
      
      return new TrianglePrismVolume(top, bottom);
   }

   /**
    * Returns the volume in meters^3 of this volume using the formula:
    * <pre>
    *    h * (TopArea + BottomArea + sqrt(TopArea * BottomArea))
    *    -------------------------------------------------------
    *                           3.0
    * </pre>
    * @return
    */
   public double getVolume() {
      final double a = this.top.getArea();
      final double b = this.bottom.getArea();
      final double volume = (this.getDepth() * (a + b + Math.sqrt(a*b))) / 3.0;
      
      return volume;
   }
}
