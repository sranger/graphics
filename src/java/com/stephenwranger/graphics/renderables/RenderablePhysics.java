package com.stephenwranger.graphics.renderables;

import com.stephenwranger.graphics.math.Quat4d;
import com.stephenwranger.graphics.math.Tuple3d;

public abstract class RenderablePhysics extends Renderable {
   private final double mass;
   private final double restitution;
   private double uS;
   private double uK;
   private final Tuple3d velocity = new Tuple3d(0, 0, 0);

   /**
    * Creates a {@link RenderablePhysics} with the given mass in kilograms.
    * 
    * @param mass
    *           mass of {@link RenderablePhysics} in kilograms
    */
   public RenderablePhysics(final double mass, final double restitution, final double muStatic, final double muKinetic) {
      super(new Tuple3d(), new Quat4d());
      this.mass = (mass == 0) ? Double.POSITIVE_INFINITY : mass;
      this.restitution = Math.min(1.0, Math.max(0.0, restitution));
      this.uS = muStatic;
      this.uK = muKinetic;
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


   public abstract void setCollidable(final boolean value);

   public abstract boolean isCollidable();
}
