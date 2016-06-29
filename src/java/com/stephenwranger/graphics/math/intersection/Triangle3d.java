package com.stephenwranger.graphics.math.intersection;

import java.util.Arrays;
import java.util.HashSet;

import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector2d;
import com.stephenwranger.graphics.math.Vector3d;

public class Triangle3d {
   private final Tuple3d[] corners = new Tuple3d[3];
   private final Vector3d normal;
   private final double d;

   public Triangle3d(final Tuple3d c1, final Tuple3d c2, final Tuple3d c3) {
      this.corners[0] = new Tuple3d(c1);
      this.corners[1] = new Tuple3d(c2);
      this.corners[2] = new Tuple3d(c3);
      
      this.normal = IntersectionUtils.calculateSurfaceNormal(this.corners);
      this.d = -(this.normal.x * c1.x + this.normal.y * c1.y + this.normal.z * c1.z);
   }
   
   public Vector3d getNormal() {
      return new Vector3d(this.normal);
   }
   
   public double distance(final Tuple3d point) {
      // D = abs(aX + bY + cZ + d) / sqrt(a^2 + b^2 + c^2)
      // D = abs(aX + bY + cZ + d) / 1.0  # sqrt part is length of normal which we normalized to length = 1
      return this.normal.x * point.x + this.normal.y * point.y + this.normal.z * point.z + this.d;
   }
   
   public boolean fixCounterClockwise(final Tuple3d internalPoint) {
      if(!isInside(internalPoint)) {
         final Tuple3d temp = this.corners[0];
         this.corners[0] = this.corners[2];
         this.corners[2] = temp;
         this.normal.set(IntersectionUtils.calculateSurfaceNormal(this.corners));
         return true;
      }
      
      return false;
   }
   
   /**
    * Will split this triangle into four equal triangles where the return array will contain the following:
    * 
    * <pre>
    * return { t0, t1, t2, t3 }
    * 
    *          v0
    *         /  \
    *        / t0 \
    *      v01----v02
    *      /  \t3/  \
    *     / t1 \/ t2 \
    *    v1----v12----v2
    * </pre>
    * 
    * @return
    */
   public Triangle3d[] split() {
      final Triangle3d[] newTriangles = new Triangle3d[4];
      final Tuple3d v0 = this.corners[0];
      final Tuple3d v1 = this.corners[1];
      final Tuple3d v2 = this.corners[2];

      final Vector3d v01 = new Vector3d();
      final Vector3d v02 = new Vector3d();
      final Vector3d v12 = new Vector3d();
      
      v01.subtract(v1, v0);
      v01.scale(0.5);
      v01.add(v0);
      
      v02.subtract(v2, v0);
      v02.scale(0.5);
      v02.add(v0);
      
      v12.subtract(v2, v1);
      v12.scale(0.5);
      v12.add(v1);

      newTriangles[0] = new Triangle3d(v0, v01, v02);
      newTriangles[1] = new Triangle3d(v01, v1, v12);
      newTriangles[2] = new Triangle3d(v02, v12, v2);
      newTriangles[3] = new Triangle3d(v01, v12, v02);
      
      return newTriangles;
   }
   
   /**
    * Returns true if the point given is on the same side as the face normal.
    * 
    * <pre>
    * http://stackoverflow.com/questions/8877872/determining-if-a-point-is-inside-a-polyhedron
    * http://stackoverflow.com/a/33836085/1451705
    * </pre>
    * 
    * @param point
    * @return
    */
   public boolean isInside(final Tuple3d point) {
      final double distance = this.distance(point);

      return IntersectionUtils.isGreaterOrEqual(distance, 0.0);
   }

   public Tuple3d[] getCorners() {
      return new Tuple3d[] { new Tuple3d(this.corners[0]), new Tuple3d(this.corners[1]), new Tuple3d(this.corners[2]) };
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      return prime * (this.corners[0].hashCode() + this.corners[1].hashCode() + this.corners[2].hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (this.getClass() != obj.getClass()) {
         return false;
      }
      final Triangle3d other = (Triangle3d) obj;
      if (!new HashSet<Tuple3d>( Arrays.asList( this.corners )).equals( new HashSet<Tuple3d>( Arrays.asList( other.corners ) ))) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "[Triangle3d: " + this.corners[0] + ", " + this.corners[1] + ", " + this.corners[2] + "]";
   }
   
   /**
    * Returns a non-normalized {@link Vector3d} along the U-axis (in barycentric coordinates).
    * 
    * @return
    */
   public Vector3d getVectorU() {
      final Vector3d vector = new Vector3d(this.corners[1]);
      vector.subtract(this.corners[0]);
      
      return vector;
   }

   /**
    * Returns a scaled {@link Vector3d} along the V-axis (in barycentric coordinates).
    * 
    * @return
    */
   public Vector3d getVectorV() {
      final Vector3d vector = new Vector3d(this.corners[2]);
      vector.subtract(this.corners[0]);
      
      return vector;
   }

   /**
    * Returns the Tuple3d used as the origin for barycentric coordinate calculations.
    * 
    * @return
    */
   public Tuple3d getBarycentricOrigin() {
      return new Tuple3d(this.corners[0]);
   }

   /**
    * Projects the point onto the closest axis-aligned plane and computes the barycentric coordinates for the given 
    * {@link Tuple3d} in this {@link Triangle3d}. The projected plane is chosen by ignoring the axis coordinate in this 
    * triangle's normal with the largest absolute magnitude.<br/><br/>
    * 
    * http://stackoverflow.com/a/5145505/1451705
    * 
    * @param point
    * @return
    */
   public Tuple3d getBarycentricCoordinate(final Tuple3d point) {
      final Vector3d normal = new Vector3d(this.normal);
      final Tuple3d[] corners = this.getCorners();
      final Tuple2d a = new Tuple2d();
      final Tuple2d b = new Tuple2d();
      final Tuple2d c = new Tuple2d();
      final Tuple2d p = new Tuple2d();
      final double x = Math.abs(normal.x);
      final double y = Math.abs(normal.y);
      final double z = Math.abs(normal.z);
      
      if(x > y && x > z) {
         a.set(corners[0].y, corners[0].z);
         b.set(corners[1].y, corners[1].z);
         c.set(corners[2].y, corners[2].z);
         p.set(point.y, point.z);
      } else if(y > x && y > z) {
         a.set(corners[0].x, corners[0].z);
         b.set(corners[1].x, corners[1].z);
         c.set(corners[2].x, corners[2].z);
         p.set(point.x, point.z);
      } else {
         a.set(corners[0].x, corners[0].y);
         b.set(corners[1].x, corners[1].y);
         c.set(corners[2].x, corners[2].y);
         p.set(point.x, point.y);
      }
      
      final Vector2d v0 = new Vector2d(b);
      v0.subtract(a);
      final Vector2d v1 = new Vector2d(c);
      v1.subtract(a);
      final Vector2d v2 = new Vector2d(p);
      v2.subtract(a);

      final double dot00 = v0.dot(v0);
      final double dot01 = v0.dot(v1);
      final double dot02 = v0.dot(v2);
      final double dot11 = v1.dot(v1);
      final double dot12 = v1.dot(v2);

      final double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);
      final double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
      final double v = (dot00 * dot12 - dot01 * dot02) * invDenom;
      final double w = 1.0 - u - v;
      
      return new Tuple3d(u, v, w);
   }
}
