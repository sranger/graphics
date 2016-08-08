package com.stephenwranger.graphics;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.CameraUtils;
import com.stephenwranger.graphics.math.Matrix4d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.math.intersection.Plane;
import com.stephenwranger.graphics.renderables.PostProcessor;
import com.stephenwranger.graphics.renderables.PreRenderable;
import com.stephenwranger.graphics.renderables.Renderable;
import com.stephenwranger.graphics.renderables.RenderableOrthographic;
import com.stephenwranger.graphics.utils.AnimationListener;
import com.stephenwranger.graphics.utils.MathUtils;
import com.stephenwranger.graphics.utils.TupleMath;

public class Scene extends GLCanvas implements GLEventListener {
   private static final long                 serialVersionUID        = -5725872347284851012L;

   private final double[]                    projection              = new double[16];
   private final double[]                    modelview               = new double[16];
   private final int[]                       viewport                = new int[4];

   private final Set<AnimationListener>      listeners               = new HashSet<>();
   private final Set<Animation>              animations              = new HashSet<>();
   private final Set<PreRenderable>          preRenderables          = new HashSet<>();
   private final Set<Renderable>             renderables             = new HashSet<>();
   private final Set<RenderableOrthographic> renderablesOrthographic = new HashSet<>();
   private final Set<PostProcessor>          postProcessors          = new HashSet<>();
   private final FPSAnimator                 animator;
   private final GLU                         glu                     = new GLU();
   private long                              current, delta;
   private final Tuple3d                     cameraPosition          = new Tuple3d(0, 0, 10);
   private final Tuple3d                     lookAt                  = new Vector3d(0, 0, 0);
   private final Vector3d                    up                      = new Vector3d(0, 1, 0);
   private BoundingVolume                    sceneBounds             = null;
   private volatile double                   animationSpeed          = 1.0;
   private volatile boolean                  updateStep              = true;

   private double                            near;
   private double                            far;
   private double                            fov                     = 45.0;
   private Tuple3d                           screenLookAt            = null;
   private boolean                           enableFollowTarget      = false;
   private final Renderable                  followTarget            = null;
   private Plane[]                           frustumPlanes           = null;
   private final Tuple3d                     origin                  = new Tuple3d(0, 0, 0);
   private boolean                           originEnabled           = false;

   private final Color4f specular = new Color4f(1,1,1,1);
   private final Color4f ambient = new Color4f(0,0,0,1);
   private final Color4f diffuse = new Color4f(1,1,1,1);
   private float constantAttenuation = 1f;
   private float linearAttenuation = 0f;
   private float quadraticAttenuation = 0f;

   public Scene(final Dimension preferredSize) {
      this(preferredSize, 60);
   }

   public Scene(final Dimension preferredSize, final int framerate) {
      super(new GLCapabilities(GLProfile.get(GLProfile.GL2)));

      this.setIgnoreRepaint(true);
      this.setPreferredSize(preferredSize);
      this.animator = new FPSAnimator(this, framerate);

      this.addGLEventListener(this);
   }

   public synchronized void addAnimation(final Animation animation) {
      this.animations.add(animation);
   }

   public synchronized void addAnimationListener(final AnimationListener listener) {
      this.listeners.add(listener);
   }

   public synchronized void addPostProcessor(final PostProcessor postProcessor) {
      this.postProcessors.add(postProcessor);
   }

   public synchronized void addPreRenderable(final PreRenderable renderable) {
      this.preRenderables.add(renderable);
   }

   public synchronized void addRenderable(final Renderable renderable) {
      renderable.setScene(this);
      this.renderables.add(renderable);
   }

   public synchronized void addRenderableOrthographic(final RenderableOrthographic renderableOrthographic) {
      this.renderablesOrthographic.add(renderableOrthographic);
   }

   @Override
   public synchronized void display(final GLAutoDrawable glDrawable) {
      final GL2 gl = (GL2) glDrawable.getGL();
      gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

      gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
      gl.glEnable(GL.GL_BLEND);
      gl.glEnable(GL.GL_CULL_FACE);
      gl.glEnable(GL.GL_DEPTH_TEST);

      final long temp = this.current;
      this.current = System.nanoTime();
      // delta = (long) ((1.0 / 10.0) * MathUtils.SECONDS_TO_NANOSECONDS); // 60 fps
      this.delta = (long) ((this.current - temp) * this.animationSpeed * MathUtils.NANOSECONDS_TO_MILLISECONDS);

      // update animation before moving screen lookAt
      if (this.updateStep) {
         this.notifyListeners(this.delta);

         for (final Animation animation : this.animations) {
            animation.step(gl, this.delta);
         }
      } else {
         this.updateStep = true;
      }

      if (this.originEnabled && (this.origin.distance(this.lookAt) > 1e6)) {
         this.origin.set(this.lookAt); // update to lookAt

         // System.out.println("origin: " + this.origin + ", " + TupleMath.length(this.origin));
      }

      this.setMatrices(gl, glDrawable);
      
      final Vector3d toLight = new Vector3d();
      toLight.subtract(this.cameraPosition, this.origin);

      gl.glEnable(GL2.GL_LIGHTING);
      gl.glEnable(GL2.GL_LIGHT0);
      gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[] { (float) toLight.x, (float) toLight.y, (float) toLight.z, 0 }, 0);

      gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specular.toArray(), 0);
      gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient.toArray(), 0);
      gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse.toArray(), 0);

      gl.glLightf(GL2.GL_LIGHT0, GL2.GL_CONSTANT_ATTENUATION, constantAttenuation);
      gl.glLightf(GL2.GL_LIGHT0, GL2.GL_LINEAR_ATTENUATION, linearAttenuation);
      gl.glLightf(GL2.GL_LIGHT0, GL2.GL_QUADRATIC_ATTENUATION, quadraticAttenuation);

      // // now that all elements are in the proper location, move camera to match
      //      if (this.screenLookAt != null) {
      //         this.followTarget = null;
      //         final Tuple3d worldTarget = CameraUtils.gluUnProject(this, this.screenLookAt);
      //         final Vector3d direction = new Vector3d(TupleMath.sub(worldTarget, this.cameraPosition));
      //         direction.normalize();
      //
      //         // TODO: update camPos and lookAt with intersection
      //         final PickingRay ray = new PickingRay(this.cameraPosition, direction);
      //         final List<PickingHit> hits = new ArrayList<>();
      //         PickingHit hit;
      //
      //         for (final Animation animation : this.animations) {
      //            hit = animation.getIntersection(ray);
      //
      //            if (hit != PickingRay.NO_HIT) {
      //               hits.add(hit);
      //            }
      //         }
      //
      //         for (final Renderable renderable : this.renderables) {
      //            hit = renderable.getIntersection(ray);
      //
      //            if (hit != PickingRay.NO_HIT) {
      //               hits.add(hit);
      //            }
      //         }
      //
      //         ray.sort(hits);
      //
      //         final PickingHit closestHit = hits.isEmpty() ? null : hits.get(0);
      //
      //         if (closestHit != null) {
      //            final Tuple3d dir = TupleMath.sub(this.cameraPosition, this.lookAt);
      //            this.lookAt.set(closestHit.getHitLocation());
      //            this.cameraPosition.set(TupleMath.add(dir, this.lookAt));
      //
      //            final double[] mv = CameraUtils.gluLookAt(gl, this.cameraPosition, this.lookAt, this.up);
      //            System.arraycopy(mv, 0, this.modelview, 0, mv.length);
      //
      //            if (this.enableFollowTarget) {
      //               this.followTarget = closestHit.getHitObject();
      //            }
      //         }
      //
      //         this.screenLookAt = null;
      //      }

      if (this.followTarget != null) {
         final Tuple3d dir = TupleMath.sub(this.cameraPosition, this.lookAt);
         this.lookAt.set(this.followTarget.getPosition());
         this.cameraPosition.set(TupleMath.add(dir, this.lookAt));

         final double[] mv = CameraUtils.gluLookAt(gl, this.cameraPosition, this.lookAt, this.up);
         System.arraycopy(mv, 0, this.modelview, 0, mv.length);
      }

      // System.out.println("near/far: " + ((int)(this.near * 100.0) / 100.0) + ", " + ((int)(this.far * 100.0) /
      // 100.0));

      for (final PreRenderable renderable : this.preRenderables) {
         renderable.preRender(gl, this.glu, glDrawable, this);
      }

      // setup is done; rendering time
      for (final Animation animation : this.animations) {
         animation.render(gl, this.glu, glDrawable, this);
      }

      // this is for mostly static objects or those that don't need a custom animation
      for (final Renderable renderable : this.renderables) {
         renderable.render(gl, this.glu, glDrawable, this);
      }

      // this is for screen-space renderables
      for (final RenderableOrthographic renderableOrthographic : this.renderablesOrthographic) {
         renderableOrthographic.render(gl, this.glu, glDrawable, this);
      }

      // this is for post processor functions
      for (final PostProcessor postProcessor : this.postProcessors) {
         postProcessor.process(gl, this.glu, this);
      }
   }

   @Override
   public synchronized void dispose(final GLAutoDrawable glDrawable) {
      this.stop();
   }

   public synchronized Tuple3d getCameraPosition() {
      return new Tuple3d(this.cameraPosition);
   }

   public synchronized double getFar() {
      return this.far;
   }

   public double getFOV() {
      return this.fov;
   }

   public double[] getFrustumPerspective() {
      final Matrix4d mvMatrix = new Matrix4d(this.modelview);
      final Matrix4d pMatrix = new Matrix4d(this.projection);
      final Matrix4d mvpMatrix = new Matrix4d();
      mvpMatrix.multiply(mvMatrix, pMatrix);
      return CameraUtils.getFrustumPerspective(mvpMatrix);
   }

   public Plane[] getFrustumPlanes() {
      return this.frustumPlanes;
   }

   public synchronized Tuple3d getLookAt() {
      return new Tuple3d(this.lookAt);
   }

   public synchronized double[] getModelViewMatrix() {
      return this.modelview;
   }

   public synchronized double getNear() {
      return this.near;
   }

   public synchronized Tuple3d getOrigin() {
      return new Tuple3d(this.origin);
   }

   public synchronized double[] getProjectionMatrix() {
      return this.projection;
   }

   public synchronized Vector3d getRightVector() {
      final Vector3d up = this.getUpVector();
      final Vector3d view = this.getViewVector();
      final Vector3d right = new Vector3d();
      right.cross(view, up);
      right.normalize();

      return right;
   }

   public synchronized Vector3d getUpVector() {
      return new Vector3d(this.up);
   }

   public synchronized int[] getViewport() {
      return this.viewport;
   }

   public synchronized Vector3d getViewVector() {
      final Vector3d view = new Vector3d();
      view.subtract(this.lookAt, this.cameraPosition);
      view.normalize();

      return view;
   }

   @Override
   public synchronized void init(final GLAutoDrawable glDrawable) {
      final GL2 gl = (GL2) glDrawable.getGL();
      gl.glShadeModel(GLLightingFunc.GL_SMOOTH); // Enable Smooth Shading
      gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // Black Background
      gl.glClearDepth(1.0f); // Depth Buffer Setup
      gl.glEnable(GL.GL_DEPTH_TEST); // Enables Depth Testing
      gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Testing To Do
      gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST); // Really Nice Perspective Calculations
   }

   public synchronized boolean isOriginEnabled() {
      return this.originEnabled;
   }

   public synchronized void removeAnimation(final Animation animation) {
      this.animations.remove(animation);
   }

   public synchronized void removeAnimationListener(final AnimationListener listener) {
      this.listeners.remove(listener);
   }

   public synchronized void removePreRenderable(final PreRenderable renderable) {
      this.preRenderables.remove(renderable);
   }

   public synchronized void removeRenderable(final Renderable renderable) {
      this.renderables.remove(renderable);
      renderable.setScene(null);
   }

   public synchronized void removeRenderableOrthographic(final RenderableOrthographic renderableOrthographic) {
      this.renderablesOrthographic.remove(renderableOrthographic);
   }

   @Override
   public synchronized void reshape(final GLAutoDrawable glDrawable, final int x, final int y, final int width, int height) {
      this.viewport[0] = x;
      this.viewport[1] = y;
      this.viewport[2] = width;
      this.viewport[3] = height;

      final GL2 gl = glDrawable.getGL().getGL2();
      gl.glViewport(this.viewport[0], this.viewport[1], this.viewport[2], this.viewport[3]);
      gl.glGetIntegerv(GL.GL_VIEWPORT, this.viewport, 0);

      if (height <= 0) {
         height = 1;
      }

      this.near = Double.MAX_VALUE;
      this.far = -Double.MAX_VALUE;
      double[] nearFar;

      for (final Animation animation : this.animations) {
         nearFar = animation.getNearFar(this);

         if (nearFar != null) {
            this.near = Math.min(this.near, nearFar[0]);
            this.far = Math.max(this.far, nearFar[1]);
         }
      }

      for (final Renderable renderable : this.renderables) {
         nearFar = renderable.getNearFar(this);

         if ((nearFar != null) && Double.isFinite(nearFar[0]) && Double.isFinite(nearFar[1])) {
            this.near = Math.min(this.near, nearFar[0]);
            this.far = Math.max(this.far, nearFar[1]);
         }
      }

      if (this.near >= this.far) {
         if (this.sceneBounds != null) {
            final Tuple3d direction = TupleMath.sub(this.cameraPosition, this.sceneBounds.getCenter());
            final double directionDistance = TupleMath.length(direction);
            TupleMath.normalize(direction);
            final double distance = this.sceneBounds.getSpannedDistance(null);
            this.far = distance + ((directionDistance - (distance / 2.0)) * 5.0);
            this.near = this.far / 3000.0;
         } else {
            this.far = TupleMath.length(TupleMath.sub(this.cameraPosition, this.lookAt)) * 5.0;
            this.near = this.far / 3000.0;
         }
      }

      if (this.near < 0.01) {
         this.near = 0.01;
      }

      if (this.far < (this.near * 3000.0)) {
         this.far = this.near * 3000.0;
      }

      //      if (this.near < (this.far / 3000.0)) {
      //         this.near = this.far / 3000.0;
      //      }

      final double[] proj = CameraUtils.gluPerspective(gl, this.fov, this.viewport[2] / (double) this.viewport[3], this.near, this.far);
      System.arraycopy(proj, 0, this.projection, 0, 16);

      //      System.out.println("near/far: " + String.format("%.2f", near) + " / " + String.format("%.2f", far));
   }

   public synchronized void setAnimationSpeed(final double doubleValue) {
      this.animationSpeed = Math.max(0, doubleValue);
   }

   public synchronized void setBounds(final BoundingVolume boundingVolume) {
      this.sceneBounds = boundingVolume;
   }

   public synchronized void setCameraPosition(final Tuple3d cameraPosition, final Tuple3d lookAt, final Vector3d up) {
      this.lookAt.set(lookAt);
      this.up.set(up);
      this.cameraPosition.set(cameraPosition);
   }

   public synchronized void setFov(final double fov) {
      this.fov = fov;
   }

   public synchronized void setLookAtTarget(final int x, final int y, final boolean follow) {
      this.screenLookAt = new Tuple3d(x, this.getHeight() - y, 1.0);
      this.enableFollowTarget = follow;
   }

   public synchronized void setMatrices(final GL2 gl, final GLAutoDrawable glDrawable) {
      this.reshape(glDrawable, this.viewport[0], this.viewport[1], this.viewport[2], this.viewport[3]);

      final double[] mv = CameraUtils.gluLookAt(gl, TupleMath.sub(this.cameraPosition, this.origin), TupleMath.sub(this.lookAt, this.origin), this.up);
      System.arraycopy(mv, 0, this.modelview, 0, 16);

      final Matrix4d mvMatrix = new Matrix4d(this.modelview);
      final Matrix4d pMatrix = new Matrix4d(this.projection);
      final Matrix4d mvpMatrix = new Matrix4d();
      mvpMatrix.multiply(mvMatrix, pMatrix);

      this.frustumPlanes = CameraUtils.getFrustumPlanes(mvpMatrix);
   }

   public synchronized void setOriginEnabled(final boolean isOriginEnabled) {
      this.originEnabled = isOriginEnabled;
   }

   public void start() {
      try {
         this.requestFocus();

         this.current = System.nanoTime();
         this.animator.start();
      } catch (final Exception e) {
         e.printStackTrace();
      }
   }

   public void stop() {
      try {
         this.animator.stop();
      } catch (final Exception e) {
         e.printStackTrace();
      }
   }

   public synchronized void update(final boolean updateStep) {
      this.updateStep = updateStep;
      this.repaint();
   }

   private synchronized void notifyListeners(final long frameTime) {
      for (final AnimationListener listener : this.listeners) {
         listener.animationStep(frameTime);
      }
   }
}
