package com.stephenwranger.graphics.math.intersection;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;

import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Vector2d;
import com.stephenwranger.graphics.renderables.Renderable2d;

public class LineSegment implements PointIntersectable, Renderable2d {
   public static final double OUT_OF_BOUNDS = -Double.MAX_VALUE;
   public final Tuple2d       min, max;
   public final double        a, b, x;

   private Dimension          dimensions    = new Dimension(0, 0);
   private int                scale         = 1;
   private float              lineWidth     = 2f;

   public LineSegment(final Tuple2d v1, final Tuple2d v2) {
      if (v1.x < v2.x) {
         this.min = new Tuple2d(v1);
         this.max = new Tuple2d(v2);
      } else if (v2.x < v1.x) {
         this.min = new Tuple2d(v2);
         this.max = new Tuple2d(v1);
      } else {
         this.min = new Tuple2d(((v1.y <= v2.y) ? v1 : v2));
         this.max = new Tuple2d(((v1.y <= v2.y) ? v2 : v1));
      }

      if (v1.x != v2.x) {
         // y - y1 = m(x - x1)
         // y - y1 = mx - mx1
         // y = mx - mx1 + y1
         this.a = (this.max.y - this.min.y) / (this.max.x - this.min.x);
         this.b = this.min.y - (this.a * this.min.x);
         this.x = Double.NaN;
      } else {
         this.a = Double.NaN;
         this.b = Double.NaN;
         this.x = this.min.x;
      }
   }

   @Override
   public boolean contains(final Tuple2d point) {
      if (Double.isNaN(this.x) && IntersectionUtils.isEqual(point.y, (this.a * point.x) + this.b)) {
         if (IntersectionUtils.isClampedInclusive(point.x, this.min.x, this.max.x) && IntersectionUtils.isClampedInclusive(point.y, this.min.y, this.max.y)) {
            return true;
         }
      } else if (!Double.isNaN(this.x)) {
         return IntersectionUtils.isEqual(this.x, point.x);
      }
      return false;
   }

   public double distance(final Tuple2d point) {
      if (IntersectionUtils.isZero(this.min.distanceSquared(this.max))) {
         return point.distance(this.min);
      }

      final double u = (((point.x - this.min.x) * (this.max.x - this.min.x)) + ((point.y - this.min.y) * (this.max.y - this.min.y))) / (this.max.distanceSquared(this.min));

      if (IntersectionUtils.isClampedInclusive(u, 0, 1)) {
         final double x = this.min.x + (u * (this.max.x - this.min.x));
         final double y = this.min.y + (u * (this.max.y - this.min.y));

         return point.distance(new Tuple2d(x, y));
      }

      return Double.MAX_VALUE;
   }

   public boolean doIntersect(final LineSegment other) {
      return LineSegment.doIntersect(this.min, this.max, other.min, other.max);
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (this.getClass() != obj.getClass()) {
         return false;
      }
      final LineSegment other = (LineSegment) obj;
      if (Double.doubleToLongBits(this.a) != Double.doubleToLongBits(other.a)) {
         return false;
      }
      if (Double.doubleToLongBits(this.b) != Double.doubleToLongBits(other.b)) {
         return false;
      }
      if (this.max == null) {
         if (other.max != null) {
            return false;
         }
      }
      if (this.min == null) {
         if (other.min != null) {
            return false;
         }
      }

      // make sure the endpoints aren't just swapped
      if (!(this.min.equals(other.min) && this.max.equals(other.max)) && !(this.min.equals(other.max) && this.max.equals(other.min))) {
         return false;
      }

      if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
         return false;
      }
      return true;
   }

   public double getAngle(final LineSegment other) {
      final Tuple2d shared = (this.min.equals(other.min) || this.min.equals(other.max)) ? this.min : this.max;
      final Vector2d a = new Vector2d();
      final Vector2d b = new Vector2d();

      if (this.max.equals(shared)) {
         a.set(this.min.x - shared.x, this.min.y - shared.y);
      } else {
         a.set(this.max.x - shared.x, this.max.y - shared.y);
      }

      if (other.max.equals(shared)) {
         b.set(other.min.x - shared.x, other.min.y - shared.y);
      } else {
         b.set(other.max.x - shared.x, other.max.y - shared.y);
      }

      a.normalize();
      b.normalize();

      return a.angle(b);
   }

   public Tuple2d getCommonVertex(final LineSegment other) {
      return (this.max.equals(other.max) || this.max.equals(other.min)) ? this.max : (this.min.equals(other.min) || this.min.equals(other.max)) ? this.min : null;
   }

   @Override
   public Dimension getDimensions() {
      return this.dimensions;
   }

   /**
    * Returns the y value for the given x value along this LineSegment or LineSegment.OUT_OF_BOUNDS if outside the
    * segment's x-bounds.
    *
    * @param x
    *           the x value of the intersection
    * @return the y value of the intersection or LineSegment.OUT_OF_BOUNDS if x value is invalid.
    */
   public double getIntersection(final double x) {
      if (IntersectionUtils.isEqual(x, this.min.x)) {
         return this.min.y;
      } else if (IntersectionUtils.isEqual(x, this.max.x)) {
         return this.max.y;
      } else if (IntersectionUtils.isLessThan(x, this.min.x) || IntersectionUtils.isGreaterThan(x, this.max.x)) {
         return LineSegment.OUT_OF_BOUNDS;
      } else {
         return (Double.isNaN(this.x)) ? (this.a * x) + this.b : Double.NaN;
      }
   }

   public Tuple2d getMidpoint() {
      final Vector2d v = new Vector2d();
      v.add(this.max, this.min);
      v.scale(0.5);

      return new Tuple2d(v.x, v.y);
   }

   public Tuple2d getPerpendicular(final Tuple2d origin, final double distance) {
      final Vector2d p = new Vector2d().subtract(this.max, this.min);
      final Vector2d n = new Vector2d(-p.y, p.x);
      n.normalize().scale(distance);
      final Tuple2d perpendicularPoint = new Tuple2d().add(origin, n);

      return perpendicularPoint;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(this.a);
      result = (prime * result) + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(this.b);
      result = (prime * result) + (int) (temp ^ (temp >>> 32));
      result = (prime * result) + ((this.max == null) ? 0 : this.max.hashCode());
      result = (prime * result) + ((this.min == null) ? 0 : this.min.hashCode());
      temp = Double.doubleToLongBits(this.x);
      result = (prime * result) + (int) (temp ^ (temp >>> 32));
      return result;
   }

   public Tuple2d intersect(final LineSegment segment) {
      // if slope of both are equal, no intersection or colinear (or both vertical, a == NaN)
      if (this.a == segment.a) {
         return null;
      } else if (this.min.equals(segment.min) || this.min.equals(segment.max)) {
         return this.min;
      } else if (this.max.equals(segment.min) || this.max.equals(segment.max)) {
         return this.max;
      }

      // if both aren't vertical
      if (Double.isNaN(this.x) && Double.isNaN(segment.x)) {
         // x0 = -(b1-b2)/(a1-a2)
         final double x = -(this.b - segment.b) / (this.a - segment.a);
         final boolean inThis = IntersectionUtils.isClampedInclusive(x, this.min.x, this.max.x);
         final boolean inThat = IntersectionUtils.isClampedInclusive(x, segment.min.x, segment.max.x);

         if (inThis && inThat) {
            final double intersection = this.getIntersection(x);

            return (intersection == LineSegment.OUT_OF_BOUNDS) ? null : new Tuple2d(x, intersection);
         }
      } else if (Double.isNaN(segment.x)) {
         // if other is vertical
         if (IntersectionUtils.isClampedInclusive(this.x, segment.min.x, segment.max.x)) {
            final double intersection = segment.getIntersection(this.x);

            if ((intersection == LineSegment.OUT_OF_BOUNDS) || !IntersectionUtils.isClampedInclusive(intersection, this.min.y, this.max.y)) {
               return null;
            } else {
               return new Tuple2d(this.x, intersection);
            }
         }
      } else if (Double.isNaN(this.x)) {
         // if this is vertical
         if (IntersectionUtils.isClampedExclusive(segment.x, this.min.x, this.max.x)) {
            final double intersection = this.getIntersection(segment.x);

            if ((intersection == LineSegment.OUT_OF_BOUNDS) || !IntersectionUtils.isClampedInclusive(intersection, segment.min.y, segment.max.y)) {
               return null;
            } else {
               return new Tuple2d(segment.x, intersection);
            }
         }
      }

      return null;
   }

   public double length() {
      return this.min.distance(this.max);
   }

   @Override
   public void paint(final Graphics2D graphics) {
      final int minX = (int) (this.min.x * this.scale);
      final int minY = this.dimensions.height - (int) (this.min.y * this.scale);
      final int maxX = (int) (this.max.x * this.scale);
      final int maxY = this.dimensions.height - (int) (this.max.y * this.scale);

      graphics.setStroke(new BasicStroke(this.lineWidth));
      graphics.drawLine(minX, minY, maxX, maxY);
      graphics.fillOval(minX - 5, minY - 5, 10, 10);
      graphics.fillOval(maxX - 5, maxY - 5, 10, 10);
   }

   @Override
   public void setDimensions(final Dimension dimensions) {
      this.dimensions = dimensions;
   }

   public void setLineWidth(final float lineWidth) {
      this.lineWidth = lineWidth;
   }

   public void setScale(final int scale) {
      this.scale = scale;
   }

   @Override
   public String toString() {
      return "[LineSegment: " + ((Double.isNaN(this.x)) ? this.min + ", " + this.max + ": y = " + this.a + "x + " + this.b : "x = " + this.x) + "]";
   }

   /**
    * The main function that returns true if line segment 'p1q1' and 'p2q2' intersect.
    *
    * http://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
    *
    * @param p1
    * @param q1
    * @param p2
    * @param q2
    * @return
    */
   public static boolean doIntersect(final Tuple2d p1, final Tuple2d q1, final Tuple2d p2, final Tuple2d q2) {
      final boolean p1q1 = p1.equals(q1);
      final boolean p2q2 = p2.equals(q2);

      if (p1q1 && p2q2) {
         return p1.equals(q1);
      } else if (p1q1) {
         final double d1 = p1.distance(p2);
         final double d2 = p1.distance(q2);
         final double distance = p2.distance(q2);
         return IntersectionUtils.isEqual(distance, d1 + d2);
      } else if (p2q2) {
         final double d1 = p2.distance(p1);
         final double d2 = p2.distance(q1);
         final double distance = p1.distance(q1);
         return IntersectionUtils.isEqual(distance, d1 + d2);
      }

      // Find the four orientations needed for general and
      // special cases
      int o1 = LineSegment.orientation(p1, q1, p2);
      int o2 = LineSegment.orientation(p1, q1, q2);
      int o3 = LineSegment.orientation(p2, q2, p1);
      int o4 = LineSegment.orientation(p2, q2, q1);

      // General case
      if ((o1 != o2) && (o3 != o4)) {
         return true;
      }

      // Special Cases
      // p1, q1 and p2 are colinear and p2 lies on segment p1q1
      if ((o1 == 0) && LineSegment.onSegment(p1, p2, q1)) {
         return true;
      }

      // p1, q1 and q2 are colinear and q2 lies on segment p1q1
      if ((o2 == 0) && LineSegment.onSegment(p1, q2, q1)) {
         return true;
      }

      // p2, q2 and p1 are colinear and p1 lies on segment p2q2
      if ((o3 == 0) && LineSegment.onSegment(p2, p1, q2)) {
         return true;
      }

      // p2, q2 and q1 are colinear and q1 lies on segment p2q2
      if ((o4 == 0) && LineSegment.onSegment(p2, q1, q2)) {
         return true;
      }

      return false; // Doesn't fall in any of the above cases
   }

   public static void main(final String[] args) {
      final Tuple2d p1 = new Tuple2d(-10, -10);
      final Tuple2d q1 = new Tuple2d(-10, -6);
      final Tuple2d p2 = new Tuple2d(-10, 5);
      final Tuple2d q2 = new Tuple2d(2, -1);

      //      System.out.println("p1q1p2: " + LineSegment.orientation(p1, q1, p2));
      //      System.out.println("p1q1q2: " + LineSegment.orientation(p1, q1, q2));
      //
      //      System.out.println("p2q2p1: " + LineSegment.orientation(p2, q2, p1));
      //      System.out.println("p2q2q1: " + LineSegment.orientation(p2, q2, q1));

      final LineSegment p1q1 = new LineSegment(p1, q1);
      final LineSegment p2q2 = new LineSegment(p2, q2);

      System.out.println(p1q1.intersect(p2q2));
   }

   // Given three colinear points p, q, r, the function checks if
   // point q lies on line segment 'pr'
   public static boolean onSegment(final Tuple2d p, final Tuple2d q, final Tuple2d r) {
      if ((q.x <= Math.max(p.x, r.x)) && (q.x >= Math.min(p.x, r.x)) && (q.y <= Math.max(p.y, r.y)) && (q.y >= Math.min(p.y, r.y))) {
         return true;
      }

      return false;
   }

   // To find orientation of ordered triplet (p, q, r).
   // The function returns following values
   // 0 --> p, q and r are colinear
   // 1 --> Clockwise
   // 2 --> Counterclockwise
   public static int orientation(final Tuple2d p, final Tuple2d q, final Tuple2d r) {
      // See http://www.geeksforgeeks.org/orientation-3-ordered-points/
      // for details of below formula.
      final double val = ((q.y - p.y) * (r.x - q.x)) - ((r.y - q.y) * (q.x - p.x));

      if (IntersectionUtils.isZero(val)) {
         return 0;  // colinear
      }

      return (val > 0) ? 1 : 2; // clock or counterclock wise
   }
}
