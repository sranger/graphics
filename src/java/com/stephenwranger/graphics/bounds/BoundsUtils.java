package com.stephenwranger.graphics.bounds;

import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.intersection.Plane;
import com.stephenwranger.graphics.renderables.RenderablePhysics;
import com.stephenwranger.graphics.utils.TupleMath;

public class BoundsUtils {
   public enum FrustumResult {
      IN, OUT, INTERSECT
   }
   
   private BoundsUtils() {
      // statics only
   }
   
//   /**
//    * Checks whether the given {@link BoundingVolume} is inside, outside, or intersects with the given frustum planes.<br/><br/>
//    * 
//    * C++ Optimized algorithm from:
//    * http://www.gamedev.net/page/resources/_/technical/general-programming/useless-snippet-2-aabbfrustum-test-r3342
//    * 
//    * @param frustumPlanes
//    * @param bv
//    * @return
//    */
//   public static FrustumResult testFrustum(final Plane[] frustumPlanes, final BoundingVolume bv) {
//      final Vector3d[] absNormals = new Vector3d[6];
//      
//      for(int i = 0; i < 6; i++) {
//         absNormals[i] = frustumPlanes[i].getNormal();
//         absNormals[i].x = Math.abs(absNormals[i].x);
//         absNormals[i].y = Math.abs(absNormals[i].y);
//         absNormals[i].z = Math.abs(absNormals[i].z);
//      }
//
//      final Tuple3d aabbCenter = bv.getCenter();
//      final Tuple3d aabbSize = bv.getDimensions();
//
//      FrustumResult result = FrustumResult.IN; // Assume that the aabb will be inside the frustum
//      
//      for(int iPlane = 0;iPlane < 6;++iPlane) {
//         final Vector3d frustumPlaneNormal = frustumPlanes[iPlane].getNormal();
//         final double distance = frustumPlanes[iPlane].getDistance();
//
//         final double d = aabbCenter.x * frustumPlaneNormal.x + 
//              aabbCenter.y * frustumPlaneNormal.y + 
//              aabbCenter.z * frustumPlaneNormal.z;
//
//         final double r = aabbSize.x * absNormals[iPlane].x + 
//              aabbSize.y * absNormals[iPlane].y + 
//              aabbSize.z * absNormals[iPlane].z;
//
//         final double d_p_r = d + r + distance;
//         
//         if(IntersectionUtils.isLessThan(d_p_r, 0.0)) {
//            result = FrustumResult.OUT;
//            break;
//         }
//
//         final double d_m_r = d - r + distance;
//         
//         if(IntersectionUtils.isLessThan(d_m_r, 0.0)) {
//            result = FrustumResult.INTERSECT;
//         }
//      }
//
//      return result;
//   }
   
   public static FrustumResult testFrustum(final Plane[] frustumPlanes, final BoundingVolume bounds) {
      if(bounds instanceof BoundingBox) {
         return BoundsUtils.testAABBInFrustum(frustumPlanes, (BoundingBox) bounds);
      } else if(bounds instanceof BoundingSphere) {
         return BoundsUtils.testSphereInFrustum(frustumPlanes, (BoundingSphere) bounds);
      } else {
         final BoundingBox box = new BoundingBox(bounds);
         return BoundsUtils.testAABBInFrustum(frustumPlanes, box);
      }
   }

   /**
    * http://www.flipcode.com/archives/Frustum_Culling.shtml
    * 
    * @param frustumPlanes
    * @param bounds
    * @return
    */
   public static FrustumResult testSphereInFrustum(final Plane[] frustumPlanes, final BoundingSphere bounds) {
      // various distances
      double fDistance;

      // calculate our distances to each of the planes
      for (int i = 0; i < 6; ++i) {
         // find the distance to this plane
         fDistance = frustumPlanes[i].distanceToPoint(bounds.getCenter()) + frustumPlanes[i].getDistance();

         // if this distance is < -sphere.radius, we are outside
         if (fDistance < -bounds.getRadius()) {
            return FrustumResult.OUT;
         }

         // else if the distance is between +- radius, then we intersect
         if ((float) Math.abs(fDistance) < bounds.getRadius()) {
            return FrustumResult.INTERSECT;
         }
      }

      // otherwise we are fully in view
      return FrustumResult.IN;
   }
   
   /**
    * http://www.flipcode.com/archives/Frustum_Culling.shtml
    * 
    * @param frustumPlanes
    * @param bounds
    * @return
    */
   public static FrustumResult testAABBInFrustum(final Plane[] frustumPlanes, final BoundingBox bounds) {
      final Tuple3d[] vCorner = bounds.getCorners();
      int iTotalIn = 0;

      // test all 8 corners against the 6 sides 
      // if all points are behind 1 specific plane, we are out
      // if we are in with all points, then we are fully in
      for(int p = 0; p < 6; ++p) {
         int iInCount = 8;
         int iPtIn = 1;

         for(int i = 0; i < 8; ++i) {

            // test this point against the planes
            if(frustumPlanes[p].isInside(vCorner[i])) {
               iPtIn = 0;
               --iInCount;
            }
         }

         // were all the points outside of plane p?
         if(iInCount == 0) {
            return FrustumResult.OUT;
         }

         // check if they were all on the right side of the plane
         iTotalIn += iPtIn;
      }

      // so if iTotalIn is 6, then all are inside the view
      if(iTotalIn == 6) {
         return FrustumResult.IN;
      }

      // we must be partly in then otherwise
      return FrustumResult.INTERSECT;
   }
   
   /**
    * http://ruh.li/CameraViewFrustum.html
    * 
    * @param frustumPlanes
    * @param xyz
    * @return
    */
   public static boolean testPointInFrustum(final Plane[] frustumPlanes, final Tuple3d xyz) {
      for (int i = 0; i < 6; i++) {
         if (!frustumPlanes[i].isInside(xyz)) {
            return false;
         }
      }
      return true;
   }

   public static boolean intersectsVector(final BoundingVolume bv, final Tuple3d vector) {
      return (bv instanceof BoundingSphere) ? BoundsUtils.intersectSphereVector((BoundingSphere) bv, vector) : BoundsUtils.intersectBoxVector((BoundingBox) bv, vector);
   }

   /**
    * Returns true if the given BoundingSphere intersects the given Vector.<br/>
    * <br/>
    * 
    * http://wiki.cgsociety.org/index.php/Ray_Sphere_Intersection
    * 
    * @param sphere
    *           BoundingSphere to check
    * @param vector
    *           Vector to intersect with (in World coordinate space)
    * @return
    */
   public static boolean intersectSphereVector(final BoundingSphere sphere, final Tuple3d vector) {
      final Tuple3d center = sphere.getCenter();
      final double radius = sphere.getRadius();

      // convert ray into object space
      final Tuple3d origin = new Tuple3d(-center.x, -center.y, -center.z);
      final Tuple3d direction = new Tuple3d(vector);
      TupleMath.normalize(direction);

      // Compute A, B and C coefficients
      final double a = TupleMath.dot(direction, direction);
      final double b = 2.0 * TupleMath.dot(direction, origin);
      final double c = TupleMath.dot(origin, origin) - (radius * radius);

      // Find discriminant
      final double disc = b * b - 4 * a * c;

      // if discriminant is negative there are no real roots, so return
      // false as vector misses sphere
      if (disc < 0)
         return false;

      // compute q as described above
      final double distSqrt = Math.sqrt(disc);
      final double q;
      if (b < 0)
         q = (-b - distSqrt) / 2.0;
      else
         q = (-b + distSqrt) / 2.0;

      // compute t0 and t1
      double t0 = q / a;
      double t1 = c / q;

      // make sure t0 is smaller than t1
      if (t0 > t1) {
         // if t0 is bigger than t1 swap them around
         final double temp = t0;
         t0 = t1;
         t1 = temp;
      }

      // if t1 is less than zero, the object is in the vector's negative direction
      // and consequently the vector misses the sphere
      if (t1 < 0)
         return false;

      // if t0 is less than zero, the intersection point is at t1
      if (t0 < 0) {
         // t = t1;
         return true;
      }
      // else the intersection point is at t0
      else {
         // t = t0;
         return true;
      }
   }

   /**
    * Returns true if the given BoundingBox is intersected by the given Vector.<br/>
    * <br/>
    * 
    * http://gamedev.stackexchange.com/questions/18436/most-efficient-aabb-vs-ray-collision-algorithms
    * 
    * @param box
    *           the BoundingBox to intersect
    * @param vector
    *           the Vector to intersect with (in World coordinate space)
    * @return
    */
   public static boolean intersectBoxVector(final BoundingBox box, final Tuple3d vector) {
      final Tuple3d center = box.getCenter();
      final Tuple3d min = box.getMin();
      final Tuple3d max = box.getMax();

      // convert ray into object space
      final Tuple3d origin = new Tuple3d(-center.x, -center.y, -center.z);
      final Tuple3d direction = new Tuple3d(vector);
      TupleMath.normalize(direction);
      
      // direction is unit direction vector of ray
      final Tuple3d dirFrac = new Tuple3d();
      dirFrac.x = 1.0f / direction.x;
      dirFrac.y = 1.0f / direction.y;
      dirFrac.z = 1.0f / direction.z;
      
      final double t1 = (min.x - origin.x) * dirFrac.x;
      final double t2 = (max.x - origin.x) * dirFrac.x;
      final double t3 = (min.y - origin.y) * dirFrac.y;
      final double t4 = (max.y - origin.y) * dirFrac.y;
      final double t5 = (min.z - origin.z) * dirFrac.z;
      final double t6 = (max.z - origin.z) * dirFrac.z;

      final double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
      final double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

      // if tmax < 0, ray (line) is intersecting AABB, but whole AABB is behing us
      if (tmax < 0) {
//         t = tmax;
         return false;
      }

      // if tmin > tmax, ray doesn't intersect AABB
      if (tmin > tmax) {
//         t = tmax;
         return false;
      }

//      t = tmin;
      return true;
   }

   public static boolean intersectSpheres(final BoundingSphere b0, final BoundingSphere b1) {
      final double distance = TupleMath.distance(b0.getCenter(), b1.getCenter());
      final double r0 = b0.getRadius();
      final double r1 = b1.getRadius();

      return (distance <= r0 + r1);
   }

   public static boolean intersectBoxes(final BoundingBox b0, final BoundingBox b1) {
      // TODO: one completely inside other?
      final Tuple3d c0 = b0.getCenter();
      final Tuple3d dim0 = b0.getDimensions();
      final Tuple3d c1 = b1.getCenter();
      final Tuple3d dim1 = b1.getDimensions();

      if (Math.abs(c0.x - c1.x) > dim0.x + dim1.x) {
         if (Math.abs(c0.y - c1.y) > dim0.y + dim1.y) {
            if (Math.abs(c0.z - c1.z) > dim0.z + dim1.z) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean intersectSphereBox(final BoundingSphere sphere, final BoundingBox box) {
      // do sphere-box check
      final double radius = sphere.getRadius();
      final Tuple3d sphereToBox = TupleMath.sub(sphere.getCenter(), box.getCenter());
      final Tuple3d boxPoint = new Tuple3d();
      final Tuple3d boxDims = box.getDimensions();

      // check x
      if (sphereToBox.x < -boxDims.x / 2.0) {
         boxPoint.x = -boxDims.x / 2.0;
      } else if (sphereToBox.x > boxDims.x / 2.0) {
         boxPoint.x = boxDims.x / 2.0;
      } else {
         boxPoint.x = sphereToBox.x;
      }

      // check y
      if (sphereToBox.y < -boxDims.y / 2.0) {
         boxPoint.y = -boxDims.y / 2.0;
      } else if (sphereToBox.y > boxDims.y / 2.0) {
         boxPoint.y = boxDims.y / 2.0;
      } else {
         boxPoint.y = sphereToBox.y;
      }

      // check z
      if (sphereToBox.z < -boxDims.z / 2.0) {
         boxPoint.z = -boxDims.z / 2.0;
      } else if (sphereToBox.z > boxDims.z / 2.0) {
         boxPoint.z = boxDims.z / 2.0;
      } else {
         boxPoint.z = sphereToBox.z;
      }

      final double distanceSquared = TupleMath.distanceSquared(sphereToBox, boxPoint);
      final boolean intersects = (distanceSquared <= radius * radius);

      return intersects;
   }

   public static boolean intersectVolumes(final BoundingVolume v0, final BoundingVolume v1) {
      if(v0 == null || v1 == null) {
         return false;
      }
      
      final boolean v0Sphere = v0 instanceof BoundingSphere;
      final boolean v1Sphere = v1 instanceof BoundingSphere;

      if (v0Sphere && v1Sphere) {
         return BoundsUtils.intersectSpheres((BoundingSphere) v0, (BoundingSphere) v1);
      } else if (!v0Sphere && !v1Sphere) {
         return BoundsUtils.intersectBoxes((BoundingBox) v0, (BoundingBox) v1);
      } else {
         final BoundingSphere sphere = (v0Sphere) ? (BoundingSphere) v0 : (BoundingSphere) v1;
         final BoundingBox box = (v0Sphere) ? (BoundingBox) v1 : (BoundingBox) v0;
         return BoundsUtils.intersectSphereBox(sphere, box);
      }
   }

   public static boolean intersect(final RenderablePhysics obj0, final RenderablePhysics obj1) {
      return BoundsUtils.intersectVolumes(obj0.getBoundingVolume(), obj1.getBoundingVolume());
   }
}
