package com.stephenwranger.graphics.renderables;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.math.PickingHit;
import com.stephenwranger.graphics.math.PickingRay;
import com.stephenwranger.graphics.math.Quat4d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.utils.TupleMath;

public abstract class Renderable {
   protected final Tuple3d position;
   protected Quat4d rotation;

   public Renderable(final Tuple3d position, final Quat4d rotation) {
      this.position = new Tuple3d(position);
      this.rotation = new Quat4d(rotation);
   }

   public void setPosition(final Tuple3d position) {
      this.position.set(position);
   }

   public void addPosition(final Tuple3d translation) {
      position.set(TupleMath.add(position, translation));
   }

   public void subPosition(final Tuple3d translation) {
      position.set(TupleMath.sub(position, translation));
   }

   public void setRotation(final Quat4d quaternion) {
      rotation = new Quat4d(quaternion);
   }

   public Tuple3d getPosition() {
      return new Tuple3d(position);
   }

   public Quat4d getRotation() {
      return new Quat4d(rotation);
   }

   public PickingHit getIntersection(final PickingRay ray) {
      return PickingRay.NO_HIT;
   }
   
   public double[] getNearFar(final Scene scene) {
      final BoundingVolume bounds = this.getBoundingVolume();
      final double[] nearFar = new double[2];
      
      if(bounds == null) {
         nearFar[0] = Double.NaN;
         nearFar[1] = Double.NaN;
      } else {
         final Tuple3d cameraPosition = scene.getCameraPosition();
         final Tuple3d origin = this.getPosition();
         final Vector3d lookAt = new Vector3d();
         lookAt.subtract(origin, cameraPosition);
         lookAt.normalize();
         final double distance = origin.distance(cameraPosition);
         final double range = this.getBoundingVolume().getSpannedDistance(lookAt);
         nearFar[0] = distance - range;
         nearFar[1] = distance + range;
      }
      
      return nearFar;
   }

   public abstract void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, final Scene scene);

   public abstract BoundingVolume getBoundingVolume();
}
