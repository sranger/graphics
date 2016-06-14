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
         this.b = this.min.y - this.a * this.min.x;
         this.x = Double.NaN;
      } else {
         this.a = Double.NaN;
         this.b = Double.NaN;
         this.x = this.min.x;
      }
   }

   public Tuple2d getCommonVertex(final LineSegment other) {
      return (this.max.equals(other.max) || this.max.equals(other.min)) ? this.max : (this.min.equals(other.min) || this.min.equals(other.max)) ? this.min : null;
   }

   public double getAngle(final LineSegment other) {
      final Tuple2d shared = (this.min.equals(other.min) || this.min.equals(other.max)) ? this.min : this.max;
      final Vector2d a = new Vector2d();
      final Vector2d b = new Vector2d();

      if(this.max.equals(shared)) {
         a.set(this.min.x - shared.x, this.min.y - shared.y);
      } else {
         a.set(this.max.x - shared.x, this.max.y - shared.y);
      }

      if(other.max.equals(shared)) {
         b.set(other.min.x - shared.x, other.min.y - shared.y);
      } else {
         b.set(other.max.x - shared.x, other.max.y - shared.y);
      }

      a.normalize();
      b.normalize();

      return a.angle(b);
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
         return (Double.isNaN(this.x)) ? this.a * x + this.b : Double.NaN;
      }
   }

   public Tuple2d intersect(final LineSegment segment) {
      if (this.a == segment.a) {
         return null;
      }

      if (Double.isNaN(this.x) && Double.isNaN(segment.x)) {
         // x0 = -(b1-b2)/(a1-a2)
         final double x = -(this.b - segment.b) / (this.a - segment.a);

         if (this.min.x <= x && this.max.x >= x && segment.min.x <= x && segment.max.x >= x) {
            return new Tuple2d(x, this.getIntersection(x));
         }
      } else if (Double.isNaN(segment.x)) {
         if (segment.min.x <= this.x && segment.max.x >= this.x) {
            return new Tuple2d(this.x, segment.getIntersection(this.x));
         }
      } else if (Double.isNaN(this.x)) {
         if (this.min.x <= segment.x && this.max.x >= segment.x) {
            return new Tuple2d(segment.x, this.getIntersection(segment.x));
         }
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
      result = prime * result + ((this.max == null) ? 0 : this.max.hashCode());
      result = prime * result + ((this.min == null) ? 0 : this.min.hashCode());
      temp = Double.doubleToLongBits(this.x);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
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
      if(!(this.min.equals(other.min) && this.max.equals(other.max)) && !(this.min.equals(other.max) && this.max.equals(other.min))) {
         return false;
      }

      if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
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
      return "[LineSegment: " + ((Double.isNaN(this.x)) ? this.min + ", " + this.max + ": y = " + this.a + "x + " + this.b : "x = " + this.x) + "]";
   }

   @Override
   public boolean contains(final Tuple2d point) {
      if (Double.isNaN(this.x) && IntersectionUtils.isEqual(point.y, this.a * point.x + this.b)) {
         if (IntersectionUtils.isClampedInclusive(point.x, this.min.x, this.max.x) && IntersectionUtils.isClampedInclusive(point.y, this.min.y, this.max.y)) {
            return true;
         }
      } else if (!Double.isNaN(this.x)) {
         return IntersectionUtils.isEqual(this.x, point.x);
      }
      return false;
   }

   public double length() {
      return this.min.distance(this.max);
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

   public void setScale(final int scale) {
      this.scale = scale;
   }

   public void setLineWidth(final float lineWidth) {
      this.lineWidth = lineWidth;
   }

   @Override
   public void setDimensions(final Dimension dimensions) {
      this.dimensions = dimensions;
   }

   @Override
   public Dimension getDimensions() {
      return this.dimensions;
   }
}
