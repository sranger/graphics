package com.stephenwranger.graphics.renderables;

import java.awt.Dimension;
import java.awt.Graphics2D;

public interface Renderable2d {
   public void paint(final Graphics2D graphics);

   public void setDimensions(final Dimension dimensions);
   public Dimension getDimensions();
}
