package com.stephenwranger.graphics.renderables;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.stephenwranger.graphics.collections.Pair;
import com.stephenwranger.graphics.math.Tuple2d;

public class Graph implements Renderable2d {
   private final List<Tuple2d>                points = new ArrayList<Tuple2d>();
   private final List<Tuple2d>                edges = new ArrayList<Tuple2d>();
   //   private final List<Pair<Tuple2d, Tuple2d>> edges  = new ArrayList<Pair<Tuple2d, Tuple2d>>();

   private final Tuple2d       min, max;

   public Graph(final List<Tuple2d> points, final List<Tuple2d> edges) {// final List<Pair<Tuple2d, Tuple2d>> edges) {
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
   }

   @Override
   public void paint(final Graphics2D graphics) {
      final double xDiff = this.max.x - this.min.x;
      final double yDiff = this.max.y - this.min.y;
      final double stepX = (xDiff > 20) ? xDiff / 10 : 1;
      final double stepY = (yDiff > 20) ? yDiff / 10 : 1;

      final Rectangle bounds = graphics.getClipBounds();
      int posX, posY;

      final int border = (int) Math.min(20, Math.max(bounds.width * 0.05, bounds.height * 0.05));

      for (double x = (int) Math.ceil(this.min.x); x <= Math.floor(this.max.x); x += stepX) {
         posX = (int) ((x - this.min.x) / xDiff * (bounds.width - border * 2.0) + border);
         graphics.drawLine(posX + bounds.x, bounds.y + border, posX + bounds.x, bounds.y + bounds.height - border);
      }

      for (double y = (int) Math.ceil(this.min.y); y <= Math.floor(this.max.y); y += stepY) {
         posY = (int) ((y - this.min.y) / yDiff * (bounds.height - border * 2.0) + border);
         graphics.drawLine(bounds.x + border, posY + bounds.y, bounds.x + bounds.width - border, posY + bounds.y);
      }

      for (final Tuple2d point : this.points) {
         posX = (int) ((point.x - this.min.x) / xDiff * (bounds.width - border * 2.0) + border);
         posY = (int) ((point.y - this.min.y) / yDiff * (bounds.height - border * 2.0) + border);
         graphics.fillOval(posX - 5, posY - 5, 10, 10);
      }

      int leftX, leftY, rightX, rightY;

      graphics.setStroke(new BasicStroke(3f));
      //      float colorStep = 1.0f / (edges.size() * 1.2f);
      //      float value = 0;
      //      int ctr = 0;

      Pair<Tuple2d, Tuple2d> edge;
      //      for (final Pair<Tuple2d, Tuple2d> edge : this.edges) {
      for(int i = 0; i < this.edges.size(); i++) {
         edge = Pair.getInstance(this.edges.get(i), this.edges.get((i+1)%this.edges.size()));
         //         value = Math.min(1f, colorStep * ctr);
         //         graphics.setColor(new Color(value, value, value));
         //         ctr++;

         leftX = (int) ((edge.left.x - this.min.x) / xDiff * (bounds.width - border * 2.0) + border);
         leftY = (int) ((edge.left.y - this.min.y) / yDiff * (bounds.height - border * 2.0) + border);
         rightX = (int) ((edge.right.x - this.min.x) / xDiff * (bounds.width - border * 2.0) + border);
         rightY = (int) ((edge.right.y - this.min.y) / yDiff * (bounds.height - border * 2.0) + border);

         graphics.drawLine(leftX + bounds.x, leftY + bounds.y, rightX + bounds.x, rightY + bounds.y);
      }
   }

}
