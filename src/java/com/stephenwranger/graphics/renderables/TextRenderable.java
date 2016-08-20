package com.stephenwranger.graphics.renderables;

import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.Tuple2d;

public class TextRenderable implements RenderableOrthographic {
   private final TextRenderer       textRenderer;
   private final Font               font;
   private final Map<String, Point> text            = new HashMap<>();
   private Color4f                  backgroundColor = new Color4f(0, 0, 0, 0);
   private Color4f                  textColor       = new Color4f(0, 0, 0, 0);
   private float                    border          = 5f;

   public TextRenderable(final Font font) {
      this.font = font;
      this.textRenderer = new TextRenderer(this.font);
      this.textRenderer.setUseVertexArrays(true);
   }

   public synchronized void addText(final String text, final Point screenPosition) {
      this.text.put(text, screenPosition);
   }

   public synchronized void clearText() {
      this.text.clear();
   }

   public synchronized void removeText(final String text, final Point screenPosition) {
      this.text.remove(text);
   }

   @Override
   public synchronized void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, final Scene scene) {
      if (!this.text.isEmpty()) {
         if (this.backgroundColor.a > 0f) {
            gl.glPushMatrix();
            gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluOrtho2D(0, scene.getWidth(), 0, scene.getHeight());
            gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
            gl.glLoadIdentity();

            gl.glPushAttrib(GL2.GL_LIGHTING_BIT | GL2.GL_LINE_BIT);
            gl.glDisable(GLLightingFunc.GL_LIGHTING);

            final Tuple2d min = new Tuple2d(Double.MAX_VALUE, Double.MAX_VALUE);
            final Tuple2d max = new Tuple2d(-Double.MAX_VALUE, -Double.MAX_VALUE);

            for (final Entry<String, Point> entry : this.text.entrySet()) {
               final String s = entry.getKey();
               final Point p = entry.getValue();
               min.x = Math.min(min.x, p.getX());
               min.y = Math.min(min.y, p.getY());

               final Rectangle2D rect = this.font.getStringBounds(s, this.textRenderer.getFontRenderContext());
               max.x = Math.max(max.x, p.x + rect.getWidth());
               max.y = Math.max(max.y, p.y + rect.getHeight());
            }

            gl.glBegin(GL.GL_TRIANGLE_STRIP);
            gl.glColor4f(this.backgroundColor.r, this.backgroundColor.g, this.backgroundColor.b, this.backgroundColor.a);
            gl.glVertex2f((float) min.x - this.border, (float) max.y + this.border);
            gl.glVertex2f((float) min.x - this.border, (float) min.y - this.border);
            gl.glVertex2f((float) max.x + this.border, (float) max.y + this.border);
            gl.glVertex2f((float) max.x + this.border, (float) min.y - this.border);
            gl.glEnd();

            gl.glPopAttrib();
            gl.glPopMatrix();
         }

         this.textRenderer.beginRendering(scene.getWidth(), scene.getHeight(), true);
         this.textRenderer.setColor(this.textColor.r, this.textColor.g, this.textColor.b, this.textColor.a);

         for (final Entry<String, Point> entry : this.text.entrySet()) {
            final String text = entry.getKey();
            final Point screenPosition = entry.getValue();
            this.textRenderer.draw(text, screenPosition.x, screenPosition.y);
         }

         this.textRenderer.endRendering();
      }
   }

   public void setBackgroundColor(final Color4f color) {
      this.backgroundColor = color;
   }

   public void setBorder(final float border) {
      this.border = border;
   }

   public void setTextColor(final Color4f color) {
      this.textColor = color;
   }
}
