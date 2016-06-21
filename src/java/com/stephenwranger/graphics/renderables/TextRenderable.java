package com.stephenwranger.graphics.renderables;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.stephenwranger.graphics.Scene;

public class TextRenderable implements RenderableOrthographic {
   private final Font font;
   private Map<String, Point> text = new HashMap<>();
   
   public TextRenderable(final Font font) {
      this.font = font;
   }
   
   public synchronized void addText(final String text, final Point screenPosition) {
      this.text.put(text, screenPosition);
   }
   
   public synchronized void removeText(final String text, final Point screenPosition) {
      this.text.remove(text);
   }
   
   public synchronized void clearText() {
      this.text.clear();
   }

   @Override
   public synchronized void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, final Scene scene) {
      final TextRenderer textRenderer = new TextRenderer(this.font);
      textRenderer.setColor(Color.white);
      textRenderer.setUseVertexArrays(true);
      
      if(!this.text.isEmpty()) {
         textRenderer.beginRendering(scene.getWidth(), scene.getHeight(), true);
         
         for(final Entry<String, Point> entry : this.text.entrySet()) {
            final String text = entry.getKey();
            final Point screenPosition = entry.getValue();
            textRenderer.draw(text, screenPosition.x, screenPosition.y);
         }
         
         textRenderer.endRendering();
      }
      
      textRenderer.dispose();
   }
}
