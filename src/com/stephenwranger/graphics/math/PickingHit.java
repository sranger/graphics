package com.stephenwranger.graphics.math;

import com.stephenwranger.graphics.renderables.Renderable;

public class PickingHit {
   private final Tuple3d    hitLocation;
   private final Renderable hitObject;

   public PickingHit(final Renderable hitObject, final Tuple3d hitLocation) {
      this.hitObject = hitObject;
      this.hitLocation = new Tuple3d(hitLocation);
   }

   public Renderable getHitObject() {
      return hitObject;
   }

   public Tuple3d getHitLocation() {
      return new Tuple3d(hitLocation);
   }
}
