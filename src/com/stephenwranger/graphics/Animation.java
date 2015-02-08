package com.stephenwranger.graphics;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import com.stephenwranger.graphics.math.PickingHit;
import com.stephenwranger.graphics.math.PickingRay;

public interface Animation {
   /**
    * Notifies this {@link Animation} to update geometry using the given delta in milliseconds.
    * 
    * @param delta
    *           time since last frame in milliseconds
    */
   public void step(final GL2 gl, final long delta);

   public void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, final Scene scene);

   /**
    * Returns the location on a Renderable hit or PickingRay.NO_HIT if no intersection occurs.
    * 
    * @param ray
    * @return world space coordinates of the hit location or PickingRay.NO_HIT if no hit occurs
    */
   public PickingHit getIntersection(final PickingRay ray);

   /**
    * Returns an array containing the optimized near and far clipping plane values or null if no optimum values are
    * natively supported by the animation. If null, the Scene will either use the scene bounds (manually set) or the
    * distance of the camera to the lookAt position.
    * 
    * @param scene
    * @return
    */
   public double[] getNearFar(final Scene scene);
}
