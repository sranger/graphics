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

   public Graph(final List<Tuple2d> points, final List<Tuple2d> edges) {//final List<Pair<Tuple2d, Tuple2d>> edges) {
      this.points.addAll(points);
      this.edges.addAll(edges);
   }

   @Override
   public void paint(final Graphics2D graphics) {
      double minX = Double.MAX_VALUE;
      double maxX = -Double.MAX_VALUE;
      double minY = Double.MAX_VALUE;
      double maxY = -Double.MAX_VALUE;

      for (final Tuple2d p : this.points) {
         minX = Math.min(minX, p.x);
         maxX = Math.max(maxX, p.x);
         minY = Math.min(minY, p.y);
         maxY = Math.max(maxY, p.y);
      }

      final double xDiff = maxX - minX;
      final double yDiff = maxY - minY;
      final double stepX = xDiff / 10.0;
      final double stepY = yDiff / 10.0;

      final Rectangle bounds = graphics.getClipBounds();
      int posX, posY;

      final int border = (int) Math.min(20, Math.max(bounds.width * 0.05, bounds.height * 0.05));

      for (double x = (int) Math.ceil(minX); x <= Math.floor(maxX); x += stepX) {
         posX = (int) ((x - minX) / xDiff * (bounds.width - border * 2.0) + border);
         graphics.drawLine(posX + bounds.x, bounds.y + border, posX + bounds.x, bounds.y + bounds.height - border);
      }

      for (double y = (int) Math.ceil(minY); y <= Math.floor(maxY); y += stepY) {
         posY = (int) ((y - minY) / yDiff * (bounds.height - border * 2.0) + border);
         graphics.drawLine(bounds.x + border, posY + bounds.y, bounds.x + bounds.width - border, posY + bounds.y);
      }

      for (final Tuple2d point : this.points) {
         posX = (int) ((point.x - minX) / xDiff * (bounds.width - border * 2.0) + border);
         posY = (int) ((point.y - minY) / yDiff * (bounds.height - border * 2.0) + border);
         graphics.fillOval(posX - 5, posY - 5, 10, 10);
      }

      int leftX, leftY, rightX, rightY;

      graphics.setStroke(new BasicStroke(3f));
//      float colorStep = 1.0f / (edges.size() * 1.2f);
//      float value = 0;
//      int ctr = 0;

      Pair<Tuple2d, Tuple2d> edge;
//      for (final Pair<Tuple2d, Tuple2d> edge : this.edges) {
      for(int i = 0; i < edges.size(); i++) {
         edge = Pair.getInstance(edges.get(i), edges.get((i+1)%edges.size()));
//         value = Math.min(1f, colorStep * ctr);
//         graphics.setColor(new Color(value, value, value));
//         ctr++;

         leftX = (int) ((edge.left.x - minX) / xDiff * (bounds.width - border * 2.0) + border);
         leftY = (int) ((edge.left.y - minY) / yDiff * (bounds.height - border * 2.0) + border);
         rightX = (int) ((edge.right.x - minX) / xDiff * (bounds.width - border * 2.0) + border);
         rightY = (int) ((edge.right.y - minY) / yDiff * (bounds.height - border * 2.0) + border);

         graphics.drawLine(leftX + bounds.x, leftY + bounds.y, rightX + bounds.x, rightY + bounds.y);
      }
   }

}
