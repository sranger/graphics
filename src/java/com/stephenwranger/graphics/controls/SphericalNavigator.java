package com.stephenwranger.graphics.controls;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.math.CameraUtils;
import com.stephenwranger.graphics.math.PickingHit;
import com.stephenwranger.graphics.math.PickingRay;
import com.stephenwranger.graphics.math.Quat4d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.utils.TupleMath;

public class SphericalNavigator implements MouseListener, MouseMotionListener, MouseWheelListener {
   private static final Tuple3d ORIGIN = new Tuple3d(0,0,0);
   private final Scene scene;
   private final double radius;

   public SphericalNavigator(final Scene scene, final double radius) {
      this.scene = scene;
      this.radius = radius;

      scene.addMouseListener(this);
      scene.addMouseMotionListener(this);
      scene.addMouseWheelListener(this);
   }

   public void removeListeners() {
      this.scene.removeMouseListener(this);
      this.scene.removeMouseMotionListener(this);
      this.scene.removeMouseWheelListener(this);
   }

   @Override
   public void mouseWheelMoved(final MouseWheelEvent e) {
      final double count = -e.getPreciseWheelRotation();
      final Tuple3d camPos = this.scene.getCameraPosition();
      final Tuple3d lookAt = this.scene.getLookAt();
      final Tuple3d dir = TupleMath.sub(lookAt, camPos);
      final double distance = TupleMath.length(dir);
      TupleMath.normalize(dir);

      final double moveDistance = distance * 0.1 * count;
      TupleMath.scale(dir, moveDistance);
      camPos.set(TupleMath.add(camPos, dir));

      this.scene.setCameraPosition(camPos);
   }
   
   private Tuple3d previousIntersection = null;

   @Override
   public void mouseDragged(final MouseEvent e) {
      if(this.previousIntersection != null) {
         final Tuple3d newHit = this.getIntersection(new Tuple3d(e.getX(), this.scene.getHeight() - e.getY(), 0.01));
         
         if(newHit != null) {
            if(SwingUtilities.isLeftMouseButton(e)) {
               final Tuple3d camera = this.scene.getCameraPosition();
               
               final Vector3d toOld = new Vector3d();
               toOld.subtract(this.previousIntersection, camera);
               toOld.normalize();
               
               final Vector3d toNew = new Vector3d();
               toNew.subtract(newHit, camera);
               toNew.normalize();
               
               final Vector3d right = new Vector3d();
               right.cross(toNew, toOld);
               right.normalize();
   
               final Tuple3d lookAt = this.scene.getLookAt();
               final Vector3d up = this.scene.getUp();
               
               final double angle = Math.toDegrees(toNew.angle(toOld));
               
               final Quat4d rotation = new Quat4d(right, angle);
               rotation.mult(camera);
               rotation.mult(up);
               
               this.scene.setCameraPosition(camera);
               this.scene.setLookAt(lookAt, up);
            } else if(SwingUtilities.isRightMouseButton(e)) {
               
            }
            
            this.previousIntersection = newHit;
         } else {
            this.previousIntersection = null;
         }
      }
   }

   @Override
   public void mouseMoved(final MouseEvent e) {
      // nothing atm

   }

   @Override
   public void mouseClicked(final MouseEvent e) {
      if(e.getClickCount() == 2) {
         this.scene.setLookAtTarget(e.getX(), e.getY(), e.isControlDown());
      }
   }

   @Override
   public void mousePressed(final MouseEvent e) {
      this.previousIntersection = this.getIntersection(new Tuple3d(e.getX(), this.scene.getHeight() - e.getY(), 0.01));
   }

   @Override
   public void mouseReleased(final MouseEvent e) {
      this.previousIntersection = null;
   }

   @Override
   public void mouseEntered(final MouseEvent e) {
      // nothing atm

   }

   @Override
   public void mouseExited(final MouseEvent e) {
      // nothing atm

   }
   
   private Tuple3d getIntersection(final Tuple3d screenCoordinates) {
      final Tuple3d camera = this.scene.getCameraPosition();
      final Tuple3d lookAt = CameraUtils.getWorldCoordinates(this.scene, screenCoordinates);
      final Vector3d view = new Vector3d();
      view.subtract(lookAt, camera);
      view.normalize();
      final PickingRay ray = new PickingRay(camera, view);
      final PickingHit hit = ray.raySphereIntersection(null, ORIGIN, this.radius, 1.0);
      
      return (hit == PickingRay.NO_HIT) ? null : hit.getHitLocation();
   }
}
