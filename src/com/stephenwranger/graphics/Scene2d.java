package com.stephenwranger.graphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import com.stephenwranger.graphics.renderables.Renderable2d;

public class Scene2d extends JComponent {
   private static final long serialVersionUID = -3368095526329010787L;
   final List<Renderable2d> renderables = new ArrayList<Renderable2d>();

   public Scene2d(final int width, final int height) {
      super();

      this.setPreferredSize(new Dimension(width, height));
   }

   public void addRenderable2d(final Renderable2d renderable) {
      this.renderables.add(renderable);
   }

   @Override
   public void paint(final Graphics g) {
      final Graphics2D g2d = (Graphics2D) g;

      for (final Renderable2d renderable : this.renderables) {
         renderable.paint(g2d);
      }
   }
}
