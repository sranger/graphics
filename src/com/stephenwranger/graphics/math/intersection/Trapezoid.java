package com.stephenwranger.graphics.math.intersection;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.renderables.Renderable2d;

public class Trapezoid implements PointIntersectable, LineIntersectable, Renderable2d {
   private final Tuple2d[] corners = new Tuple2d[4];
   private final Triangle2D t1, t2;

   // for Renderable2d
   private Dimension        dimensions = new Dimension(0, 0);
   private int              scale      = 1;
   private float            lineWidth  = 2f;

   public final double      minX, minY, maxX, maxY, width, height;

   /**
    * Creates a new Trapezoid from its four corners (must be in CW/CCW order); left and right sides must be vertical.
    *
    * @param c0
    * @param c1
    * @param c2
    * @param c3
    */
   public Trapezoid(final Tuple2d c0, final Tuple2d c1, final Tuple2d c2, final Tuple2d c3) {
      this.corners[0] = new Tuple2d(c0);
      this.corners[1] = new Tuple2d(c1);
      this.corners[2] = new Tuple2d(c2);
      this.corners[3] = new Tuple2d(c3);

      this.t1 = new Triangle2D(c0, c1, c2);
      this.t2 = new Triangle2D(c0, c2, c3);

      this.minX = Math.min(c0.x, Math.min(c1.x, Math.min(c2.x, c3.x)));
      this.minY = Math.min(c0.y, Math.min(c1.y, Math.min(c2.y, c3.y)));
      this.maxX = Math.max(c0.x, Math.max(c1.x, Math.max(c2.x, c3.x)));
      this.maxY = Math.max(c0.y, Math.max(c1.y, Math.max(c2.y, c3.y)));
      this.width = this.maxX - this.minX;
      this.height = this.maxY - this.minY;
   }

   /**
    * Splits this Trapezoid along the intersection with the given LineSegment. If the segment completely bisects, the resulting above and below Trapezoids will
    * be returned as new Trapezoid[] { above, below }; If only one endpoint is within the trapezoid, the resulting trapezoids will be returns left-to-right and
    * top-to-bottom.
    *
    * <pre>
    * ---------- ----------
    * |   |    | |   |    |
    * | A |    | |   | B  |
    * |---| C  | | A |----|
    * | B |    | |   | C  |
    * ---------- ----------
    *
    * return new Trapezoid[] { A, B, C };
    * </pre>
    *
    * @param segment
    * @return
    */
   public Trapezoid[] split(final LineSegment segment) {
      final List<Tuple2d> intersected = IntersectionUtils.lineIntersectPolygon(this.corners, segment);

      if (intersected.size() == 2) {
         final Tuple2d min = (intersected.get(0).x <= intersected.get(1).x) ? intersected.get(0) : intersected.get(1);
         final Tuple2d max = (intersected.get(0).x >= intersected.get(1).x) ? intersected.get(0) : intersected.get(1);
         LineSegment leftSegment = null, rightSegment = null, temp;

         for (int i = 0; i < this.corners.length; i++) {
            temp = new LineSegment(this.corners[i], this.corners[(i + 1) % this.corners.length]);

            if (temp.contains(min)) {
               leftSegment = temp;
            } else if (temp.contains(max)) {
               rightSegment = temp;
            }
         }

         if (leftSegment != null && rightSegment != null) {
            return new Trapezoid[] { new Trapezoid(min, leftSegment.max, rightSegment.max, max), new Trapezoid(min, leftSegment.min, rightSegment.min, max) };
         }
      } else if (intersected.size() == 1) {
         final Tuple2d inside = (this.contains(segment.min)) ? segment.min : segment.max;
         final boolean isMin = (inside == segment.min);
         final Tuple2d split = intersected.get(0);
         final Tuple2d insideDown = new Tuple2d(inside.x, inside.y - this.height);
         final Tuple2d insideUp = new Tuple2d(inside.x, inside.y + this.height);

         final LineSegment top = this.getTop();
         final LineSegment bottom = this.getBottom();

         final Tuple2d topIntersection = top.intersect(new LineSegment(inside, insideUp));
         final Tuple2d bottomIntersection = bottom.intersect(new LineSegment(inside, insideDown));

         final Trapezoid x = new Trapezoid(topIntersection, bottomIntersection, (isMin) ? bottom.min : bottom.max, (isMin) ? top.min : top.max);
         final Trapezoid y = new Trapezoid(inside, topIntersection, (isMin) ? top.max : top.min, split);
         final Trapezoid z = new Trapezoid(inside, bottomIntersection, (isMin) ? bottom.max : bottom.min, split);

         return (isMin) ? new Trapezoid[] { x, y, z } : new Trapezoid[] { y, z, x };
      } else {
         throw new IndexOutOfBoundsException("Illegal number of intersections found. " + intersected.size());
      }

      return null;
   }

   public LineSegment getLeft() {
      return new LineSegment(this.getCorner(false, false), this.getCorner(false, true));
   }

   public LineSegment getRight() {
      return new LineSegment(this.getCorner(true, true), this.getCorner(true, false));
   }

   public LineSegment getTop() {
      return new LineSegment(this.getCorner(false, true), this.getCorner(true, true));
   }

   public LineSegment getBottom() {
      return new LineSegment(this.getCorner(false, false), this.getCorner(true, false));
   }

   public Tuple2d getCorner(final boolean isMaxX, final boolean isMaxY) {
      Tuple2d corner = null;

      for (final Tuple2d c : this.corners) {
         if (corner == null) {
            corner = c;
         } else {
            if (isMaxX && isMaxY) {
               // top-right
               if (IntersectionUtils.isGreaterThan(c.x, corner.x)) {
                  corner = c;
               } else if (IntersectionUtils.isEqual(c.x, corner.x) && IntersectionUtils.isGreaterThan(c.y, corner.y)) {
                  corner = c;
               }
            } else if (isMaxX && !isMaxY) {
               // bottom-right
               if (IntersectionUtils.isGreaterThan(c.x, corner.x)) {
                  corner = c;
               } else if (IntersectionUtils.isEqual(c.x, corner.x) && IntersectionUtils.isLessThan(c.y, corner.y)) {
                  corner = c;
               }
            } else if (!isMaxX && isMaxY) {
               // top-left
               if (IntersectionUtils.isLessThan(c.x, corner.x)) {
                  corner = c;
               } else if (IntersectionUtils.isEqual(c.x, corner.x) && IntersectionUtils.isGreaterThan(c.y, corner.y)) {
                  corner = c;
               }
            } else {
               // bottom right
               if (IntersectionUtils.isLessThan(c.x, corner.x)) {
                  corner = c;
               } else if (IntersectionUtils.isEqual(c.x, corner.x) && IntersectionUtils.isLessThan(c.y, corner.y)) {
                  corner = c;
               }
            }
         }
      }

      return corner;
   }

   @Override
   public boolean contains(final Tuple2d point) {
      return this.t1.contains(point) || this.t2.contains(point) && IntersectionUtils.isLessOrEqual(point.x, this.getRight().x);
   }

   @Override
   public boolean contains(final LineSegment segment) {
      return this.contains(segment.min) || this.contains(segment.max) || this.t1.contains(segment) || this.t2.contains(segment);
   }

   @Override
   public List<Tuple2d> getIntersection(final LineSegment segment) {
      return IntersectionUtils.lineIntersectPolygon(this.corners, segment);
   }

   @Override
   public String toString() {
      return "[Trapezoid: " + this.getLeft().min + ", " + this.getLeft().max + ", " + this.getRight().max + ", " + this.getRight().min + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(this.corners);
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
      final Trapezoid other = (Trapezoid) obj;
      if (!Arrays.equals(this.corners, other.corners)) {
         return false;
      }
      return true;
   }

   public void setScale(final int scale) {
      this.scale = scale;
   }

   public void setLineWidth(final float lineWidth) {
      this.lineWidth = lineWidth;
   }

   @Override
   public void paint(final Graphics2D graphics) {
      final LineSegment left = this.getLeft();
      final LineSegment right = this.getRight();
      final int border = 0;

      final int[] x = new int[] { (int) left.min.x * this.scale + border, (int) left.max.x * this.scale + border, (int) right.max.x * this.scale - border,
            (int) right.min.x * this.scale - border };
      final int[] y = new int[] { 1000 - (int) left.min.y * this.scale - border, 1000 - (int) left.max.y * this.scale + border,
            1000 - (int) right.max.y * this.scale + border, 1000 - (int) right.min.y * this.scale - border };

      graphics.setStroke(new BasicStroke(this.lineWidth));
      // graphics.setColor(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
      graphics.drawPolygon(x, y, 4);
   }

   @Override
   public void setDimensions(Dimension dimensions) {
      this.dimensions = dimensions;
   }

   @Override
   public Dimension getDimensions() {
      return this.dimensions;
   }
}
