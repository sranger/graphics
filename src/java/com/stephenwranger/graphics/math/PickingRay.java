package com.stephenwranger.graphics.math;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.stephenwranger.graphics.renderables.Renderable;
import com.stephenwranger.graphics.renderables.Sphere;
import com.stephenwranger.graphics.utils.TupleMath;

public class PickingRay {
   public static final PickingHit    NO_HIT             = null;

   private final Comparator<PickingHit> distanceComparator = new Comparator<PickingHit>() {
      @Override
      public int compare(final PickingHit o1, final PickingHit o2) {
         final double d1 = TupleMath.distanceSquared(origin, o1.getHitLocation());
         final double d2 = TupleMath.distanceSquared(origin, o2.getHitLocation());

         return Double.valueOf(d1).compareTo(d2);
      }
   };

   private final Tuple3d origin;
   private final Tuple3d direction;

   public PickingRay(final Tuple3d origin, final Tuple3d direction) {
      this.origin = new Tuple3d(origin);
      this.direction = new Tuple3d(direction);
      TupleMath.normalize(this.direction);
   }

   /**
    * http://www.lighthouse3d.com/tutorials/maths/ray-sphere-intersection/
    * 
    * @param sphere
    * @return
    */
   public PickingHit raySphereIntersection(final Renderable renderable, final Tuple3d origin, final double sphereRadius, final double sphereScale) {
      final Tuple3d spherePosition = origin;
      final Tuple3d rayOriginToSphereOrigin = TupleMath.sub(spherePosition, this.origin);

      if (TupleMath.dot(rayOriginToSphereOrigin, direction) < 0) {
         // behind origin; not caring
      } else {
         final Tuple3d projectedCenter = projectToRay(spherePosition);
         final double distanceSquared = TupleMath.distanceSquared(spherePosition, projectedCenter);
         final double radius = sphereRadius * sphereScale;
         final double radius2 = radius * radius;


         if (distanceSquared <= (radius2)) {
            final double pcMinusCLength = TupleMath.distance(projectedCenter, spherePosition);
            final double dist = Math.sqrt(radius2 - (pcMinusCLength * pcMinusCLength));
            double di1;

            if (TupleMath.length(rayOriginToSphereOrigin) > radius) {
               // origin outside sphere
               di1 = TupleMath.distance(projectedCenter, origin) - dist;
            } else {
               // origin inside sphere
               di1 = TupleMath.distance(projectedCenter, origin) + dist;
            }

            final Tuple3d scaledDirection = new Tuple3d(direction);
            TupleMath.scale(scaledDirection, di1);

            return new PickingHit(renderable, TupleMath.add(origin, scaledDirection));
         }
      }

      return PickingRay.NO_HIT;
   }

   /**
    * http://www.lighthouse3d.com/tutorials/maths/ray-sphere-intersection/
    * 
    * @param sphere
    * @return
    */
   public PickingHit raySphereIntersection(final Sphere sphere) {
      return this.raySphereIntersection(sphere, sphere.getPosition(), sphere.radius, sphere.getScale());
   }

   /**
    * http://www.lighthouse3d.com/tutorials/maths/line-and-rays/
    * 
    * @param position
    * @return
    */
   public Tuple3d projectToRay(final Tuple3d position) {
      final Tuple3d u = TupleMath.sub(position, origin);
      final Tuple3d v = new Tuple3d(direction);

      final double uDotV = TupleMath.dot(u, v);
      TupleMath.scale(v, uDotV);

      return TupleMath.add(origin, v);
   }

   public void sort(final List<PickingHit> hits) {
      Collections.sort(hits, this.distanceComparator);
   }
}
