package com.stephenwranger.graphics.math.intersection;

import java.util.Arrays;
import java.util.HashSet;

import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector2d;

public class Triangle3d {
   private final Tuple3d[] corners = new Tuple3d[3];

   public Triangle3d(final Tuple3d c1, final Tuple3d c2, final Tuple3d c3) {
      this.corners[0] = new Tuple3d(c1);
      this.corners[1] = new Tuple3d(c2);
      this.corners[2] = new Tuple3d(c3);
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
      if (!new HashSet<Tuple2d>( Arrays.asList( this.corners )).equals( new HashSet<Tuple2d>( Arrays.asList( other.corners ) ))) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "[Triangle3d: " + this.corners[0] + ", " + this.corners[1] + ", " + this.corners[2] + "]";
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
