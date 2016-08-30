package com.stephenwranger.graphics.math.intersection;

import java.util.Arrays;
import java.util.HashSet;

import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector2d;
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.renderables.Line;
import com.stephenwranger.graphics.utils.MathUtils;

public class Triangle3d extends Plane {
   protected final Tuple3d[] corners = new Tuple3d[3];
   protected final Line sLine;
   protected final Line tLine;
   protected final Line uLine;

   protected final double sRange;
   protected final double tRange;
   protected final double uRange;
   
   public Triangle3d(final Tuple3d c1, final Tuple3d c2, final Tuple3d c3) {
      super(c1, c2, c3);
      
      this.corners[0] = new Tuple3d(c1);
      this.corners[1] = new Tuple3d(c2);
      this.corners[2] = new Tuple3d(c3);

      this.sLine = new Line(this.corners[0], this.corners[1]);
      this.tLine = new Line(this.corners[1], this.corners[2]);
      this.uLine = new Line(this.corners[2], this.corners[0]);

      this.sRange = this.sLine.distanceToPoint(this.corners[2]);
      this.tRange = this.tLine.distanceToPoint(this.corners[0]);
      this.uRange = this.uLine.distanceToPoint(this.corners[1]);
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
    * Returns the {@link Triangle3d} parallel to the two given {@link Triangle3d} objects and equidistant to both.
    * 
    * @param top
    * @param bottom
    * @return
    */
   public static Triangle3d getMidFace(final Triangle3d top, final Triangle3d bottom) {
      final Vector3d m0 = new Vector3d(top.corners[0]);
      final Vector3d m1 = new Vector3d(top.corners[1]);
      final Vector3d m2 = new Vector3d(top.corners[2]);

      // add the bottom face corners to the top
      m0.add(bottom.corners[0]);
      m1.add(bottom.corners[1]);
      m2.add(bottom.corners[2]);
      
      // multiply by 0.5 to get the "average" between the top and bottom points
      m0.scale(0.5);
      m1.scale(0.5);
      m2.scale(0.5);
      
      return new Triangle3d(m0, m1, m2);
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
   public Tuple3d getBarycentricCoordinate(final Tuple3d original) {
      // first, get point on plane closest to original point
      final double distance = this.distanceToPoint(original);
      final Tuple3d point = new Tuple3d(original);
      final Vector3d scaledNormal = new Vector3d(this.normal);
      scaledNormal.normalize();
      scaledNormal.scale(distance);
      point.subtract(scaledNormal);
      
//      // project point onto triangle along major axis plane by ignoring the smallest magnitude component of the normal
//      final Vector3d normal = new Vector3d(this.normal);
//      final Tuple3d[] corners = this.getCorners();
//      final Tuple2d a = new Tuple2d();
//      final Tuple2d b = new Tuple2d();
//      final Tuple2d c = new Tuple2d();
//      final Tuple2d p = new Tuple2d();
//      final double x = Math.abs(normal.x);
//      final double y = Math.abs(normal.y);
//      final double z = Math.abs(normal.z);
//      
//      if(x > y && x > z) {
//         a.set(corners[0].y, corners[0].z);
//         b.set(corners[1].y, corners[1].z);
//         c.set(corners[2].y, corners[2].z);
//         p.set(point.y, point.z);
//      } else if(y > x && y > z) {
//         a.set(corners[0].x, corners[0].z);
//         b.set(corners[1].x, corners[1].z);
//         c.set(corners[2].x, corners[2].z);
//         p.set(point.x, point.z);
//      } else {
//         a.set(corners[0].x, corners[0].y);
//         b.set(corners[1].x, corners[1].y);
//         c.set(corners[2].x, corners[2].y);
//         p.set(point.x, point.y);
//      }
//      
//      final Vector2d v0 = new Vector2d(b);
//      v0.subtract(a);
//      final Vector2d v1 = new Vector2d(c);
//      v1.subtract(a);
//      final Vector2d v2 = new Vector2d(p);
//      v2.subtract(a);
//
//      final double dot00 = v0.dot(v0);
//      final double dot01 = v0.dot(v1);
//      final double dot02 = v0.dot(v2);
//      final double dot11 = v1.dot(v1);
//      final double dot12 = v1.dot(v2);
//
//      final double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);
//      final double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
//      final double v = (dot00 * dot12 - dot01 * dot02) * invDenom;
//      final double w = 1.0 - u - v;
//      
//      return new Tuple3d(u, v, w);
      
      // calculate vectors from point f to vertices p1, p2 and p3:
      final Tuple3d f = new Tuple3d(point);
      final Tuple3d p1 = new Tuple3d(this.corners[0]);
      final Tuple3d p2 = new Tuple3d(this.corners[1]);
      final Tuple3d p3 = new Tuple3d(this.corners[2]);
      final Vector3d f1 = new Vector3d(p1.x-f.x, p1.y-f.y, p1.z-f.z);
      final Vector3d f2 = new Vector3d(p2.x-f.x, p2.y-f.y, p2.z-f.z);
      final Vector3d f3 = new Vector3d(p3.x-f.x, p3.y-f.y, p3.z-f.z);
      
      // calculate the areas (parameters order is essential in this case):
      final Vector3d p1p2 = new Vector3d(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
      final Vector3d p1p3 = new Vector3d(p1.x - p3.x, p1.y - p3.y, p1.z - p3.z);
      final Vector3d va = new Vector3d().cross(p1p2, p1p3);
      final Vector3d va1 = new Vector3d().cross(f2, f3);
      final Vector3d va2 = new Vector3d().cross(f3, f1);
      final Vector3d va3 = new Vector3d().cross(f1, f2);
      final double a = va.length();

      final double a1 = va1.length() / a * Math.sin(va.dot(va1));
      final double a2 = va2.length() / a * Math.sin(va.dot(va2));
      final double a3 = va3.length() / a * Math.sin(va.dot(va3));
      
//      var va: Vector3 = Vector3.Cross(p1-p2, p1-p3); // main triangle cross product
//      var va1: Vector3 = Vector3.Cross(f2, f3); // p1's triangle cross product
//      var va2: Vector3 = Vector3.Cross(f3, f1); // p2's triangle cross product
//      var va3: Vector3 = Vector3.Cross(f1, f2); // p3's triangle cross product
//      var a: float = va.magnitude; // main triangle area
//      
//      // calculate barycentric coordinates with sign:
//      var a1: float = va1.magnitude/a * Mathf.Sign(Vector3.Dot(va, va1));
//      var a2: float = va2.magnitude/a * Mathf.Sign(Vector3.Dot(va, va2));
//      var a3: float = va3.magnitude/a * Mathf.Sign(Vector3.Dot(va, va3));
      
      // find the uv corresponding to point f (uv1/uv2/uv3 are associated to p1/p2/p3):
      final Vector2d uv1 = new Vector2d(0,0);
      final Vector2d uv2 = new Vector2d(1,0);
      final Vector2d uv3 = new Vector2d(0,1);
      
//      var uv: Vector2 = uv1 * a1 + uv2 * a2 + uv3 * a3;
      uv1.scale(a1);
      uv2.scale(a2);
      uv3.scale(a3);
      
      final Tuple2d uv = new Tuple2d();
      uv.add(uv1);
      uv.add(uv2);
      uv.add(uv3);
      
      return new Tuple3d(uv.x, uv.y, 1.0 - uv.x - uv.y);
   }
   
   /**
    * Projects the given {@link Tuple3d} onto this {@link Triangle3d} and computes the triangular coordinates 
    * as a distance ratio from each face. The projected plane is chosen by ignoring the axis coordinate in this 
    * triangle's normal with the largest absolute magnitude.
    * 
    * @param original the original point
    * @return the triangular coordinates of the given point
    */
   public Tuple3d getTriangularCoordinates(final Tuple3d original) {
      // first, get point on plane closest to original point
      final double distance = this.distanceToPoint(original);
      final Tuple3d point = new Tuple3d(original);
      final Vector3d scaledNormal = new Vector3d(this.normal);
      scaledNormal.normalize();
      scaledNormal.scale(distance);
      point.subtract(scaledNormal);

      final double s = MathUtils.clamp(0, sRange, sLine.distanceToPoint(point));
      final double t = MathUtils.clamp(0, tRange, tLine.distanceToPoint(point));
      final double u = MathUtils.clamp(0, uRange, uLine.distanceToPoint(point));
      Tuple3d coords = null;
      
      // TODO: this is out of bounds at times
//      if(IntersectionUtils.isClampedInclusive(s, 0.0, this.sRange) && IntersectionUtils.isClampedInclusive(t, 0.0, this.tRange) && IntersectionUtils.isClampedInclusive(u, 0.0, this.uRange)) {
         coords = new Tuple3d(s, t, u);
//      } else {
//         System.err.println(String.format("not valid: %.2f, %.2f, %.2f", s, t, u));
//         System.err.println(String.format("range: %.2f, %.2f, %.2f", this.sRange, this.tRange, this.uRange));
//         System.err.println(String.format("to plane: %.2f (should be 0)", this.distanceToPoint(point)));
//         final Tuple3d bary = this.getBarycentricCoordinate(original);
//         System.err.println(String.format("bary: %.2f, %.2f, %.2f (>= 0), total = %.2f (== 1.0)", bary.x, bary.y, bary.z, (bary.x + bary.y + bary.z)));
//      }
      
      return coords;
   }
   
   /**
    * Returns the range [0,{x,y,z}] for triangular coordinates.
    * @return
    */
   public Tuple3d getTriangularCoordinatesRange() {
      return new Tuple3d(this.sRange, this.tRange, this.uRange);
   }

   /**
    * Returns the area of this {@link Triangle3d} in meters^2 using the following formula:
    * <pre>
    *    AB X AC
    *    -------
    *      2.0
    * </pre>
    * @return
    */
   public double getArea() {
      final Vector3d ab = Vector3d.getVector(this.corners[0], this.corners[1], false);
      final Vector3d ac = Vector3d.getVector(this.corners[0], this.corners[2], false);
      final Vector3d cross = new Vector3d();
      cross.cross(ab, ac);
      final double area = cross.length() / 2.0;
      
      return area;
   }
}
