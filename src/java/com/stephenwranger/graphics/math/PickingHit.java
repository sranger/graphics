package com.stephenwranger.graphics.math;

import com.stephenwranger.graphics.renderables.Renderable;

public class PickingHit {
   private final Tuple3d    hitLocation;
   private final Renderable hitObject;
   private final double     distance;

   public PickingHit(final Renderable hitObject, final Tuple3d hitLocation, final double distance) {
      this.hitObject = hitObject;
      this.hitLocation = new Tuple3d(hitLocation);
      this.distance = distance;
   }

   public double getDistance() {
      return this.distance;
   }

   public Tuple3d getHitLocation() {
      return new Tuple3d(this.hitLocation);
   }

   public Renderable getHitObject() {
      return this.hitObject;
   }
}
