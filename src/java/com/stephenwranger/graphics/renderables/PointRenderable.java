package com.stephenwranger.graphics.renderables;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.bounds.BoundsUtils;
import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.Quat4d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.utils.buffers.BufferRegion;
import com.stephenwranger.graphics.utils.buffers.ColorRegion;
import com.stephenwranger.graphics.utils.buffers.DataType;
import com.stephenwranger.graphics.utils.buffers.VertexBufferObject;
import com.stephenwranger.graphics.utils.buffers.VertexRegion;

public class PointRenderable extends Renderable {
   private final List<Tuple3d> points        = new ArrayList<>();
   private BoundingVolume      bounds        = null;
   private boolean             needsRefresh  = false;
   private VertexBufferObject  vbo           = null;
   private float               pointSize     = 1f;
   private Color4f             pointColor    = Color4f.white();
   private final Tuple3d       currentOrigin = new Tuple3d();

   public PointRenderable() {
      // will handle only global positioned points
      super(new Tuple3d(), new Quat4d());
   }

   @Override
   public BoundingVolume getBoundingVolume() {
      return this.bounds;
   }

   public Color4f getPointColor() {
      return new Color4f(this.pointColor);
   }

   public float getPointSize() {
      return this.pointSize;
   }

   @Override
   public synchronized void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, final Scene scene) {
      if (this.currentOrigin.distance(scene.getOrigin()) > 0) {
         this.needsRefresh = true;
         this.currentOrigin.set(scene.getOrigin());
      }

      if (this.needsRefresh) {
         this.needsRefresh = false;

         if (this.points.isEmpty()) {
            this.vbo = null;
         } else {
            final BufferRegion[] regions = new BufferRegion[] { new VertexRegion(3, DataType.FLOAT), new ColorRegion(4, DataType.FLOAT) };
            this.vbo = new VertexBufferObject(this.points.size(), true, GL.GL_POINTS, GL.GL_STATIC_DRAW, regions);
            final FloatBuffer buffer = this.vbo.mapBuffer(gl).asFloatBuffer();

            for (final Tuple3d point : this.points) {
               buffer.put((float) (point.x - this.currentOrigin.x));
               buffer.put((float) (point.y - this.currentOrigin.y));
               buffer.put((float) (point.z - this.currentOrigin.z));
               this.pointColor.putInto(buffer);
            }

            this.vbo.unmapBuffer(gl);
         }
      }

      if (this.vbo != null) {
         gl.glPushMatrix();
         gl.glPushAttrib(GL2.GL_LIGHTING_BIT | GL2.GL_POINT_BIT);
         gl.glDisable(GLLightingFunc.GL_LIGHTING);
         gl.glPointSize(this.pointSize);

         this.vbo.render(gl);

         gl.glPopAttrib();
         gl.glPopMatrix();
      }
   }

   public void setPointColor(final Color4f color) {
      this.pointColor = new Color4f(color);
   }

   public synchronized void setPoints(final Collection<Tuple3d> points) {
      this.points.clear();

      if (points != null) {
         this.points.addAll(points);
      }

      this.bounds = BoundsUtils.getBoundingBox(this.points);
      this.needsRefresh = true;
   }

   public void setPointSize(final float pointSize) {
      this.pointSize = pointSize;
   }
}
