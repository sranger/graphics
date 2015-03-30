package com.stephenwranger.graphics.math.intersection;

import java.util.List;

import com.stephenwranger.graphics.math.Tuple2d;

public class Triangle2D implements PointIntersectable, LineIntersectable {
   private final Tuple2d[] corners = new Tuple2d[3];

   public Triangle2D(final Tuple2d c1, final Tuple2d c2, final Tuple2d c3) {
      this.corners[0] = new Tuple2d(c1);
      this.corners[1] = new Tuple2d(c2);
      this.corners[2] = new Tuple2d(c3);
   }

   public Tuple2d[] getCorners() {
      return new Tuple2d[] { new Tuple2d(this.corners[0]), new Tuple2d(this.corners[1]), new Tuple2d(this.corners[2]) };
   }

   public LineSegment[] getLineSegments() {
      return new LineSegment[] { new LineSegment(this.corners[0], this.corners[1]), new LineSegment(this.corners[1], this.corners[2]),
            new LineSegment(this.corners[2], this.corners[0]) };
   }

   @Override
   public boolean contains(final Tuple2d point) {
      return IntersectionUtils.pointInTriangle(point, this);
   }

   @Override
   public boolean contains(final LineSegment segment) {
      if (this.contains(segment.min) || this.contains(segment.max)) {
         return true;
      }

      final List<Tuple2d> intersected = IntersectionUtils.lineIntersectTriangle(this, segment);

      return !intersected.isEmpty();
   }

   @Override
   public List<Tuple2d> getIntersection(final LineSegment segment) {
      return IntersectionUtils.lineIntersectTriangle(this, segment);
   }
}
