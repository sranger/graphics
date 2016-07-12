package com.stephenwranger.graphics.renderables;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.bounds.BoundingSphere;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.CameraUtils;
import com.stephenwranger.graphics.math.PickingHit;
import com.stephenwranger.graphics.math.PickingRay;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.intersection.Ellipsoid;
import com.stephenwranger.graphics.utils.TupleMath;
import com.stephenwranger.graphics.utils.textures.Texture2d;

/**
 * http://blog.andreaskahler.com/2009/06/creating-icosphere-mesh-in-code.html
 * http://acko.net/blog/making-worlds-1-of-spheres-and-cubes/
 *
 * @author rangers
 *
 */
public class Sphere extends RenderablePhysics {
   private Texture2d          texture             = null;
   public final double        radius;
   private final Color4f      color               = Color4f.white();
   private boolean            isCollidable        = true;

   private EllipticalGeometry geometry            = null;

   private boolean            enableScreenScaling = false;
   private int                minScreenSize       = 10;
   private double             scale               = 1.0;

   public Sphere(final double mass, final double restitution, final double muStatic, final double muKinetic, final double radius) {
      this(mass, restitution, muStatic, muKinetic, radius, 1);
   }

   public Sphere(final double mass, final double restitution, final double muStatic, final double muKinetic, final double radius, final int subdivisions) {
      super(mass, restitution, muStatic, muKinetic);

      this.radius = radius;
   }

   public Sphere(final Tuple3d origin, final double radius) {
      this(0, 0, 0, 0, radius, 1);

      super.setPosition(origin);
   }

   public Sphere(final Tuple3d origin, final double radius, final int subdivisions) {
      this(0, 0, 0, 0, radius, subdivisions);

      super.setPosition(origin);
   }

   @Override
   public BoundingVolume getBoundingVolume() {
      return this.isCollidable ? new BoundingSphere(this.position, this.radius) : null;
   }

   @Override
   public PickingHit getIntersection(final PickingRay ray) {
      return ray.raySphereIntersection(this);
   }

   public double getScale() {
      return this.scale;
   }

   @Override
   public boolean isCollidable() {
      return this.isCollidable;
   }

   @Override
   public void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, final Scene scene) {
      if (this.geometry == null) {
         this.initializeVbo(gl);
      }

      gl.glPushMatrix();

      gl.glEnable(GLLightingFunc.GL_LIGHTING);
      gl.glEnable(GLLightingFunc.GL_LIGHT0);

      if (this.texture == null) {
         gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
         gl.glColorMaterial(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE);
      } else {
         gl.glDisable(GLLightingFunc.GL_COLOR_MATERIAL);
         this.texture.enable(gl);
      }

      this.scale = 1.0;

      if (this.enableScreenScaling) {
         final Tuple3d posScreen = CameraUtils.gluProject(scene, this.position);
         final Tuple3d posSide = TupleMath.add(posScreen, new Tuple3d(this.minScreenSize / 2.0, 0, 0));
         final Tuple3d worldSide = CameraUtils.gluUnProject(scene, posSide);
         final double distance = TupleMath.distance(this.position, worldSide);

         if (distance > this.radius) {
            this.scale = distance / this.radius;
         }
      }

      final float[] axis = new float[3];
      final float angle = this.rotation.toAngleAxis(axis);

      gl.glTranslatef((float) this.position.x, (float) this.position.y, (float) this.position.z);
      gl.glRotatef((float) Math.toDegrees(angle), axis[0], axis[1], axis[2]);
      gl.glScalef((float) this.scale, 1f, (float) this.scale);

      this.geometry.render(gl, glu, glDrawable, scene);

      gl.glFlush();

      if (this.texture != null) {
         this.texture.disable(gl);
      }

      gl.glPopMatrix();
   }

   @Override
   public void setCollidable(final boolean value) {
      this.isCollidable = value;
   }

   public void setColor(final Color4f color) {
      this.color.setColor(color);
   }

   /**
    * If screen scaling is enabled, the set value is not guaranteed to persist.
    *
    * @param scale
    */
   public void setScale(final double scale) {
      this.scale = scale;
   }

   public void setScreenScaling(final boolean enableScreenScaling, final int minScreenSize) {
      this.enableScreenScaling = enableScreenScaling;
      this.minScreenSize = minScreenSize;
   }

   public void setTexture(final Texture2d texture) {
      this.texture = texture;
   }

   private void initializeVbo(final GL2 gl) {
      final double     firstEccentricitySquared  = 1.0; // 2f − f^2 == 2.0 * 1.0 - (1.0 * 1.0) == 2.0 - 1.0 == 1.0
      final double     secondEccentricitySquared = Double.NaN; //f(2 − f)/(1 − f)^2;
      this.geometry = new EllipticalGeometry(gl, new Ellipsoid(new Tuple3d(), this.radius, 1.0, firstEccentricitySquared, secondEccentricitySquared), this.radius, 1, (azimuth, elevation) -> {
         return this.radius;
      }, (segment) -> {});
   }
}
