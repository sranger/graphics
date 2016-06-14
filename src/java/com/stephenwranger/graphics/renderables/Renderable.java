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
import com.stephenwranger.graphics.utils.TupleMath;

public abstract class Renderable {
   protected final Tuple3d position = new Tuple3d();
   protected Quat4d rotation = new Quat4d();
   private final double mass;
   private final double restitution;
   private double uS;
   private double uK;
   private final Tuple3d velocity = new Tuple3d(0, 0, 0);

   /**
    * Creates a {@link Renderable} with the given mass in kilograms.
    * 
    * @param mass
    *           mass of {@link Renderable} in kilograms
    */
   public Renderable(final double mass, final double restitution, final double muStatic, final double muKinetic) {
      this.mass = (mass == 0) ? Double.POSITIVE_INFINITY : mass;
      this.restitution = Math.min(1.0, Math.max(0.0, restitution));
      this.uS = muStatic;
      this.uK = muKinetic;
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
      rotation = quaternion;
   }

   public Tuple3d getPosition() {
      return new Tuple3d(position);
   }

   public Quat4d getRotation() {
      return new Quat4d(rotation);
   }

   public void setFrictionStatic(final double uS) {
      this.uS = uS;
   }

   public void setFrictionKinetic(final double uK) {
      this.uK = uK;
   }

   /**
    * Returns mass in kg.
    * 
    * @return
    */
   public double getMass() {
      return mass;
   }

   public double getInverseMass() {
      return ((mass <= 0) || (mass == Double.POSITIVE_INFINITY)) ? 0 : 1.0 / mass;
   }

   /**
    * Returns the coefficient of restitution in the range [0,1].
    * 
    * @return
    */
   public double getCoefficientOfRestitution() {
      return restitution;
   }

   /**
    * The coefficient of static friction.
    * 
    * @return
    */
   public double getMuStatic() {
      return uS;
   }

   /**
    * The coefficient of kinetic friction.
    * 
    * @return
    */
   public double getMuKinetic() {
      return uK;
   }

   public Tuple3d getVelocity() {
      return new Tuple3d(velocity);
   }

   /**
    * Sets the velocity direction and magnitude.
    * 
    * @param velocity
    *           the direction of velocity and magnitude as length of direction vector
    */
   public void setVelocity(final Tuple3d velocity) {
      this.velocity.set(velocity);
   }

   public PickingHit getIntersection(final PickingRay ray) {
      return PickingRay.NO_HIT;
   }


   public abstract void setCollidable(final boolean value);

   public abstract boolean isCollidable();

   public abstract void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, final Scene scene);

   public abstract BoundingVolume getBoundingVolume();
}
