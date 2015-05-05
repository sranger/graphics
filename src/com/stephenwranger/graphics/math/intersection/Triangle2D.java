package com.stephenwranger.graphics.math.intersection;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.stephenwranger.graphics.collections.Pair;
import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.renderables.Circle;

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

   public Circle getCircumscribedCircle() {
      final LineSegment aEdge = new LineSegment(this.corners[0], this.corners[1]);
      final LineSegment bEdge = new LineSegment(this.corners[1], this.corners[2]);
      final LineSegment cEdge = new LineSegment(this.corners[2], this.corners[0]);

      final double a = aEdge.length();
      final double b = bEdge.length();
      final double c = cEdge.length();

      final double radius = (a * b * c) / (Math.sqrt((a + b + c) * (b + c - a) * (c + a - b) * (a + b - c)));

      final Tuple2d aMid = aEdge.getMidpoint();
      final Tuple2d aPerp1 = aEdge.getPerpendicular(aMid, radius);
      final Tuple2d aPerp2 = aEdge.getPerpendicular(aMid, -radius);
      final LineSegment aPerp = new LineSegment(aPerp1, aPerp2);

      final Tuple2d bMid = bEdge.getMidpoint();
      final Tuple2d bPerp1 = bEdge.getPerpendicular(bMid, radius);
      final Tuple2d bPerp2 = bEdge.getPerpendicular(bMid, -radius);
      final LineSegment bPerp = new LineSegment(bPerp1, bPerp2);

      final Tuple2d center = aPerp.intersect(bPerp);

      return new Circle(center, radius);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      return prime * (corners[0].hashCode() + corners[1].hashCode() + corners[2].hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Triangle2D other = (Triangle2D) obj;
      if (!new HashSet<Tuple2d>( Arrays.asList( this.corners )).equals( new HashSet<Tuple2d>( Arrays.asList( other.corners ) )))
         return false;
      return true;
   }

   public Pair<LineSegment, LineSegment> getOppositeEdges(final LineSegment edge) {
      final List<LineSegment> edges = Arrays.asList(this.getLineSegments());
      final int index = edges.indexOf(edge);
      
      return Pair.getInstance(edges.get((index+1) % 3), edges.get((index+2) % 3));
   }

   public LineSegment getCommonEdge(final Triangle2D other) {
      final List<LineSegment> edges = Arrays.asList(other.getLineSegments());
      
      for(final LineSegment edge : this.getLineSegments()) {
         if(edges.contains(edge)) {
            return edge;
         }
      }
      
      return null;
   }
}
