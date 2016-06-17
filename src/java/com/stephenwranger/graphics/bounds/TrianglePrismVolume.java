package com.stephenwranger.graphics.bounds;

import com.stephenwranger.graphics.math.Tuple3d;
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

      this.top = new Triangle3d(top[0], top[1], top[2]);
      this.bottom = new Triangle3d(bottom[2], bottom[1], bottom[0]);
      this.faces[0] = new Triangle3d(top[0], top[2], bottom[0]);
      this.faces[1] = new Triangle3d(bottom[0], top[2], bottom[2]);
      
      this.faces[2] = new Triangle3d(top[2], top[1], bottom[2]);
      this.faces[3] = new Triangle3d(top[1], bottom[1], bottom[2]);
      
      this.faces[4] = new Triangle3d(top[1], top[0], bottom[1]);
      this.faces[5] = new Triangle3d(top[0], bottom[0], bottom[1]);

      // added so loop can be used for contains check
      this.faces[6] = this.top;
      this.faces[7] = this.bottom;
   }
   
   public Triangle3d[] getFaces() {
      return this.faces;
   }
   
   @Override
   public boolean contains(final Tuple3d point) {
      int ctr = 0;

      // check the easier contains computation as an early termination step
      if(super.contains(point)) {
//         System.out.println("inside aabb");
         for(int i = 0; i < this.faces.length; i++) {
            final Triangle3d face = this.faces[i];
            System.out.println("\tface #" + i + " isBehind? " + face.isBehind(point));
            if(face.isBehind(point)) {
               ctr++;
            }
         }
      }
      
      return ctr == this.faces.length;
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
