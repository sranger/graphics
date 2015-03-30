package com.stephenwranger.graphics.renderables;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.intersection.LineSegment;

public class Graph implements Renderable2d {
   private final List<Tuple2d>     points = new ArrayList<Tuple2d>();
   private final List<LineSegment> edges  = new ArrayList<LineSegment>();

   private Dimension               dimensions;
   private final Tuple2d           min, max;

   public Graph(final List<Tuple2d> points, final List<LineSegment> edges) {
      this.points.addAll(points);
      this.edges.addAll(edges);

      this.min = new Tuple2d(points.get(0));
      this.max = new Tuple2d(points.get(0));

      Tuple2d point;
      for (int i = 1; i < points.size(); i++) {
         point = points.get(i);
         this.min.x = Math.min(this.min.x, point.x);
         this.max.x = Math.max(this.max.x, point.x);
         this.min.y = Math.min(this.min.y, point.y);
         this.max.y = Math.max(this.max.y, point.y);
      }

      LineSegment edge;

      for (int i = 0; i < edges.size(); i++) {
         edge = this.edges.get(i);
         this.min.x = Math.min(this.min.x, Math.min(edge.min.x, edge.max.x));
         this.max.x = Math.max(this.max.x, Math.max(edge.min.x, edge.max.x));

         this.min.y = Math.min(this.min.y, Math.min(edge.min.y, edge.max.y));
         this.max.y = Math.max(this.max.y, Math.max(edge.min.y, edge.max.y));
      }
   }

   @Override
   public void setDimensions(final Dimension dimensions) {
      this.dimensions = dimensions;
   }

   @Override
   public Dimension getDimensions() {
      return this.dimensions;
   }

   @Override
   public void paint(final Graphics2D graphics) {
      final double xDiff = this.max.x - this.min.x;
      final double yDiff = this.max.y - this.min.y;
      final double stepX = (xDiff > 20) ? xDiff / 30 : 1;
      final double stepY = (yDiff > 20) ? yDiff / 30 : 1;

      final Rectangle bounds = new Rectangle(this.dimensions);
      int posX, posY;

      final int border = (int) Math.min(20, Math.max(bounds.width * 0.05, bounds.height * 0.05));

      for (double x = (int) Math.ceil(this.min.x); x <= Math.floor(this.max.x); x += stepX) {
         posX = (int) ((x - this.min.x) / xDiff * (bounds.width - border * 2.0) + border);
         graphics.drawLine(posX + bounds.x, bounds.y + border, posX + bounds.x, bounds.y + bounds.height - border);
      }

      for (double y = (int) Math.ceil(this.min.y); y <= Math.floor(this.max.y); y += stepY) {
         posY = (int) (bounds.height - (((y - this.min.y) / yDiff * (bounds.height - border * 2.0) + border)));
         graphics.drawLine(bounds.x + border, posY + bounds.y, bounds.x + bounds.width - border, posY + bounds.y);
      }

      int leftX, leftY, rightX, rightY;

      graphics.setStroke(new BasicStroke(3f));

      LineSegment edge;

      for (int i = 0; i < this.edges.size(); i++) {
         edge = this.edges.get(i);

         leftX = (int) ((edge.min.x - this.min.x) / xDiff * (bounds.width - border * 2.0) + border);
         leftY = (int) (bounds.height - ((edge.min.y - this.min.y) / yDiff * (bounds.height - border * 2.0) + border));
         rightX = (int) ((edge.max.x - this.min.x) / xDiff * (bounds.width - border * 2.0) + border);
         rightY = (int) (bounds.height - ((edge.max.y - this.min.y) / yDiff * (bounds.height - border * 2.0) + border));

         graphics.drawLine(leftX + bounds.x, leftY + bounds.y, rightX + bounds.x, rightY + bounds.y);
      }

      graphics.setColor(Color.red);

      for (final Tuple2d point : this.points) {
         posX = (int) ((point.x - this.min.x) / xDiff * (bounds.width - border * 2.0) + border);
         posY = (int) (bounds.height - ((point.y - this.min.y) / yDiff * (bounds.height - border * 2.0) + border));
         graphics.fillOval(posX - 5, posY - 5, 10, 10);
      }
   }

   public static List<LineSegment> toSegments(final List<Tuple2d> polygonPoints) {
      final List<LineSegment> segments = new ArrayList<LineSegment>();

      for (int i = 0; i < polygonPoints.size(); i++) {
         segments.add(new LineSegment(polygonPoints.get(i), polygonPoints.get((i + 1) % polygonPoints.size())));
      }

      return segments;
   }
}
