package com.stephenwranger.graphics.math.intersection;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.stephenwranger.graphics.collections.Pair;
import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector2d;
import com.stephenwranger.graphics.renderables.Circle;

public class Triangle2d implements PointIntersectable, LineIntersectable {
   private final Tuple2d[] corners = new Tuple2d[3];

   public Triangle2d(final Tuple2d c1, final Tuple2d c2, final Tuple2d c3) {
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
      final Triangle2d other = (Triangle2d) obj;
      if (!new HashSet<Tuple2d>( Arrays.asList( this.corners )).equals( new HashSet<Tuple2d>( Arrays.asList( other.corners ) ))) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "[Triangle2D: " + this.corners[0] + ", " + this.corners[1] + ", " + this.corners[2] + "]";
   }

   public Pair<LineSegment, LineSegment> getOppositeEdges(final LineSegment edge) {
      final List<LineSegment> edges = Arrays.asList(this.getLineSegments());
      final int index = edges.indexOf(edge);

      return Pair.getInstance(edges.get((index+1) % 3), edges.get((index+2) % 3));
   }

   public LineSegment getCommonEdge(final Triangle2d other) {
      final List<LineSegment> edges = Arrays.asList(other.getLineSegments());

      for(final LineSegment edge : this.getLineSegments()) {
         if(edges.contains(edge)) {
            return edge;
         }
      }

      return null;
   }

   public Tuple3d getBarycentricCoordinate(final Tuple2d point) {
      final Tuple2d[] corners = this.getCorners();
      final Tuple2d a = corners[0];
      final Tuple2d b = corners[1];
      final Tuple2d c = corners[2];

      final Vector2d v0 = new Vector2d().subtract(c, a);
      final Vector2d v1 = new Vector2d().subtract(b, a);
      final Vector2d v2 = new Vector2d().subtract(point, a);

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
