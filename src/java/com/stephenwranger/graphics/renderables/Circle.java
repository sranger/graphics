package com.stephenwranger.graphics.renderables;

import java.awt.Dimension;
import java.awt.Graphics2D;

import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.intersection.IntersectionUtils;
import com.stephenwranger.graphics.math.intersection.PointIntersectable;

public class Circle implements Renderable2d, PointIntersectable {
   private Dimension     dimensions = new Dimension(1, 1);
   private final Tuple2d   center;
   private final double    radius;

   public Circle(final Tuple2d center, final double radius) {
      this.center = new Tuple2d(center);
      this.radius = radius;
   }

   @Override
   public boolean contains(final Tuple2d point) {
      return IntersectionUtils.isLessOrEqual(point.distance(this.center), this.radius);
   }

   @Override
   public void paint(final Graphics2D graphics) {

   }

   @Override
   public void setDimensions(final Dimension dimensions) {
      this.dimensions = dimensions;
   }

   @Override
   public Dimension getDimensions() {
      return this.dimensions;
   }

   public Tuple2d getCenter() {
      return new Tuple2d(this.center);
   }

   public double getRadius() {
      return this.radius;
   }
}
