package com.stephenwranger.graphics.renderables;

import com.stephenwranger.graphics.math.Tuple2d;

public class LineSegment {
   public static final double OUT_OF_BOUNDS = -Double.MAX_VALUE;
   public final Tuple2d       min, max;
   private final double       a, b;

   public LineSegment(final Tuple2d v1, final Tuple2d v2) {
      if (v1.x < v2.x) {
         this.min = v1;
         this.max = v2;
      } else if (v1.x == v2.x) {
         throw new UnsupportedOperationException("Line Segment cannot be vertical.");
      } else {
         this.min = v2;
         this.max = v1;
      }

      // y - y1 = m(x - x1)
      // y - y1 = mx - mx1
      // y = mx - mx1 + y1
      this.a = (this.max.y - this.min.y) / (this.max.x - this.min.x);
      this.b = this.min.y - this.a * this.min.x;
   }

   /**
    * Returns the y value for the given x value along this LineSegment or LineSegment.OUT_OF_BOUNDS if outside the segment's x-bounds.
    *
    * @param x
    *           the x value of the intersection
    * @return the y value of the intersection or LineSegment.OUT_OF_BOUNDS if x value is invalid.
    */
   public double getIntersection(final double x) {
      if (this.min.x > x || this.max.x < x) {
         return LineSegment.OUT_OF_BOUNDS;
      } else {
         return this.a * x + this.b;
      }
   }

   public Tuple2d intersect(final LineSegment segment) {
      if (this.a == segment.a) {
         return null;
      }

      // x0 = -(b1-b2)/(a1-a2)
      final double x = -(this.b - segment.b) / (this.a - segment.a);

      if (this.min.x <= x && this.max.x >= x && segment.min.x <= x && segment.max.x >= x) {
         return new Tuple2d(x, this.getIntersection(x));
      }

      return null;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(this.a);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(this.b);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
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
      return true;
   }

   public static void main(final String[] args) {
      final LineSegment s1 = new LineSegment(new Tuple2d(0, 1), new Tuple2d(1, 0));
      final LineSegment s2 = new LineSegment(new Tuple2d(0, 0), new Tuple2d(1, 1));

      System.out.println(s1.intersect(s2));
   }

   @Override
   public String toString() {
      return this.min + ", " + this.max + ": y = " + this.a + "x + " + this.b;
   }
}
