package com.stephenwranger.graphics.renderables;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.bounds.BoundingBox;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.utils.TupleMath;

public class RectangularSolid extends Renderable {
   private static final double[][] CUBE_VERTICES = new double[][] { { -0.5, -0.5, -0.5 }, // LEFT BOTTOM FAR
         { -0.5, 0.5, -0.5 }, // LEFT TOP FAR
         { 0.5, -0.5, -0.5 }, // RIGHT BOTTOM FAR
         { 0.5, 0.5, -0.5 }, // RIGHT TOP FAR
         { -0.5, -0.5, 0.5 }, // LEFT BOTTOM NEAR
         { -0.5, 0.5, 0.5 }, // LEFT TOP NEAR
         { 0.5, -0.5, 0.5 }, // RIGHT BOTTOM NEAR
         { 0.5, 0.5, 0.5 } }; // RIGHT TOP NEAR
   private static final int[][] CUBE_INDICES = new int[][] { { 0, 1, 3, 2 }, // FRONT
         { 6, 7, 5, 4 }, // BACK
         { 4, 5, 1, 0 }, // LEFT
         { 2, 3, 7, 6 }, // RIGHT
         { 1, 5, 7, 3 }, // TOP
         { 4, 0, 2, 6 } }; // BOTTOM

   private final double width, height, depth;
   private final Color4f[] edgeColor;
   private final Color4f[] colors;
   private boolean isCollidable = true;


   /**
    * Creates a new {@link RectangularSolid} with the given width dimension and color(s) for its sides.
    * 
    * @param width
    *           the width of the {@link RectangularSolid}
    * @param height
    *           the height of the {@link RectangularSolid}
    * @param depth
    *           the depth of the {@link RectangularSolid}
    * @param edgeColor
    *           the color to draw the edges of the {@link RectangularSolid} or null for no edge rendering
    * @param colors
    *           the color of the {@link RectangularSolid} (will rap if not enough for unique sides).
    *           if null, default color is white. Will be filled in the following order:
    * 
    *           <pre>
    *          FRONT
    *          BACK
    *          LEFT
    *          RIGHT
    *          TOP
    *          BOTTOM
    * </pre>
    */
   public RectangularSolid(final double mass, final double restitution, final double muStatic, final double muKinetic, final double width, final double height, final double depth,
         final Color4f edgeColor, final Color4f... colors) {
      super(mass, restitution, muStatic, muKinetic);

      if (width <= 0 || height <= 0 || depth <= 0) {
         throw new UnsupportedOperationException("Dimensions of RectangularSolid cannot be <= 0");
      }

      this.width = width;
      this.height = height;
      this.depth = depth;
      this.edgeColor = (edgeColor == null) ? null : new Color4f[] { new Color4f(edgeColor) };
      this.colors = (colors == null || colors.length == 0) ? new Color4f[] {} : colors;
   }

   @Override
   public void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, Scene scene) {
      gl.glPushMatrix();
      gl.glDisable(GL2.GL_LIGHTING);
      gl.glEnable(GL2.GL_COLOR_MATERIAL);
      gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);

      final float[] axisAngle = rotation.toAxis();

      gl.glTranslatef((float) position.x, (float) position.y, (float) position.z);
      gl.glRotatef((float) Math.toDegrees(axisAngle[0]), axisAngle[1], axisAngle[2], axisAngle[3]);

      gl.glEnable(GL2.GL_DEPTH_TEST);

      if(colors != null && colors.length > 0) {
         gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
         gl.glPolygonOffset(1f, 1f);
         renderGeometry(gl, GL2.GL_QUADS, CUBE_INDICES, colors);
         gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
      }

      if (edgeColor != null) {
         gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
         renderGeometry(gl, GL2.GL_QUADS, CUBE_INDICES, edgeColor);
         gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
      }

      gl.glFlush();
      gl.glPopMatrix();
   }

   private void renderGeometry(final GL2 gl, final int glType, final int[][] indices, final Color4f[] colors) {
      gl.glBegin(glType);

      final Tuple3d[] verts = new Tuple3d[4];
      Tuple3d normal;
      Color4f color;
      int ctr = 0;

      for (final int[] side : indices) {
         for (int i = 0; i < 4; i++) {
            verts[i] = new Tuple3d(CUBE_VERTICES[side[i]][0], CUBE_VERTICES[side[i]][1], CUBE_VERTICES[side[i]][2]);
         }

         normal = TupleMath.cross(TupleMath.sub(verts[1], verts[0]), TupleMath.sub(verts[2], verts[0]));
         TupleMath.normalize(normal);

         for (final Tuple3d vert : verts) {
            color = colors[ctr % colors.length];
            gl.glColor4f(color.r, color.g, color.b, color.a);
//            gl.glNormal3f((float) normal.x, (float) normal.y, (float) normal.z);
            gl.glVertex3f((float) (vert.x * width), (float) (vert.y * height), (float) (vert.z * depth));
         }

         ctr++;
      }

      gl.glEnd();
   }

   @Override
   public BoundingVolume getBoundingVolume() {
      return (isCollidable) ? new BoundingBox(position, width, height, depth) : null;
   }

   @Override
   public void setCollidable(final boolean value) {
      isCollidable = value;
   }

   @Override
   public boolean isCollidable() {
      return isCollidable;
   }
}
