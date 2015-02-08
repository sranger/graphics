package com.stephenwranger.graphics.bounds;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.stephenwranger.graphics.math.Tuple3d;

public class BoundsTest {

   @Test
   public void testSphereSphereTangent() {
      final BoundingSphere sphere0 = new BoundingSphere(new Tuple3d(0,0,0), 1.0);
      final BoundingSphere sphere1 = new BoundingSphere(new Tuple3d(0,1,0), 1.0);
      final boolean intersects = BoundsUtils.intersectVolumes(sphere0, sphere1);
      
      assertTrue(intersects);
   }

   @Test
   public void testSphereSphereIntersect() {
      final BoundingSphere sphere0 = new BoundingSphere(new Tuple3d(0,0,0), 1.0);
      final BoundingSphere sphere1 = new BoundingSphere(new Tuple3d(0,1,0), 2.0);
      final boolean intersects = BoundsUtils.intersectVolumes(sphere0, sphere1);
      
      assertTrue(intersects);
   }

   @Test
   public void testSphereSphereInside() {
      final BoundingSphere sphere0 = new BoundingSphere(new Tuple3d(0,0,0), 1.0);
      final BoundingSphere sphere1 = new BoundingSphere(new Tuple3d(0,0,0), 0.5);
      final boolean intersects = BoundsUtils.intersectVolumes(sphere0, sphere1);
      
      assertTrue(intersects);
   }

   @Test
   public void testSphereSphereFail() {
      final BoundingSphere sphere0 = new BoundingSphere(new Tuple3d(-5,0,0), 2.0);
      final BoundingSphere sphere1 = new BoundingSphere(new Tuple3d(5,0,0), 2.0);
      final boolean intersects = BoundsUtils.intersectVolumes(sphere0, sphere1);
      
      assertFalse(intersects);
   }

   @Test
   public void testBoxBoxEdge() {
      final BoundingBox b0 = new BoundingBox(new Tuple3d(-2,-2,-2), new Tuple3d(0,2,2));
      final BoundingBox b1 = new BoundingBox(new Tuple3d(0,0,0), new Tuple3d(2,2,2));
      final boolean intersects = BoundsUtils.intersectVolumes(b0, b1);
      
      assertTrue(intersects);
   }

   @Test
   public void testBoxBoxIntersect() {
      final BoundingBox b0 = new BoundingBox(new Tuple3d(-2,-2,-2), new Tuple3d(2,2,2));
      final BoundingBox b1 = new BoundingBox(new Tuple3d(-1,0,0), new Tuple3d(2,2,2));
      final boolean intersects = BoundsUtils.intersectVolumes(b0, b1);
      
      assertTrue(intersects);
   }

   @Test
   public void testBoxBoxInside() {
      final BoundingBox b0 = new BoundingBox(new Tuple3d(-2,-2,-2), new Tuple3d(2,2,2));
      final BoundingBox b1 = new BoundingBox(new Tuple3d(-1,-1,-1), new Tuple3d(1,1,1));
      final boolean intersects = BoundsUtils.intersectVolumes(b0, b1);
      
      assertTrue(intersects);
   }

   @Test
   public void testBoxBoxFail() {
      final BoundingBox b0 = new BoundingBox(new Tuple3d(-2,-2,-2), new Tuple3d(-1,-1,-1));
      final BoundingBox b1 = new BoundingBox(new Tuple3d(1,1,1), new Tuple3d(2,2,2));
      final boolean intersects = BoundsUtils.intersectVolumes(b0, b1);
      
      assertFalse(intersects);
   }
   
   @Test
   public void testSphereBoxEdge() {
      final BoundingSphere sphere = new BoundingSphere(new Tuple3d(0,0,0), 1.0);
      final BoundingBox box = new BoundingBox(new Tuple3d(1,-1,-1), new Tuple3d(2,1,1));
      final boolean intersects = BoundsUtils.intersectVolumes(sphere, box);
      
      assertTrue(intersects);
   }
   
   @Test
   public void testSphereBoxIntersect() {
      final BoundingSphere sphere = new BoundingSphere(new Tuple3d(0,0,0), 2.0);
      final BoundingBox box = new BoundingBox(new Tuple3d(0,0,0), new Tuple3d(5,5,5));
      final boolean intersects = BoundsUtils.intersectVolumes(sphere, box);
      
      assertTrue(intersects);
   }
   
   @Test
   public void testSphereBoxInside() {
      final BoundingSphere sphere = new BoundingSphere(new Tuple3d(0,0,0), 1.0);
      final BoundingBox box = new BoundingBox(new Tuple3d(-5,-5,-5), new Tuple3d(5,5,5));
      final boolean intersects = BoundsUtils.intersectVolumes(sphere, box);
      
      assertTrue(intersects);
   }
   
   @Test
   public void testSphereBoxFail() {
      final BoundingSphere sphere = new BoundingSphere(new Tuple3d(0,0,0), 1.0);
      final BoundingBox box = new BoundingBox(new Tuple3d(3,3,3), new Tuple3d(5,5,5));
      final boolean intersects = BoundsUtils.intersectVolumes(sphere, box);
      
      assertFalse(intersects);
   }
}
