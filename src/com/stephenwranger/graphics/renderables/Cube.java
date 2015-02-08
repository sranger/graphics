package com.stephenwranger.graphics.renderables;

import com.stephenwranger.graphics.color.Color4f;

public class Cube extends RectangularSolid {

   /**
    * Creates a new {@link Cube} with the given width dimension and color(s) for its sides.
    * 
    * @param width
    *           the width of the {@link Cube}
    * @param colors
    *           the color of the {@link Cube} (will rap if not enough for unique sides).
    *           if null, default color is white. Will be filled in the following order: 
    * <pre>
    *          FRONT
    *          BACK
    *          LEFT
    *          RIGHT
    *          TOP
    *          BOTTOM
    * </pre>
    */
   public Cube(final double mass, final double restitution, final double muStatic, final double muKinetic, final double width, final Color4f... colors) {      
      super(mass, restitution, muStatic, muKinetic, width, width, width, null, colors);
   }

   public static Cube getDefaultColors(final float width) {
      return new Cube(0, 1, 0, 0, 20f, Color4f.red(), Color4f.cyan(), Color4f.green(), Color4f.magenta(), Color4f.blue(), Color4f.yellow());
   }
}
