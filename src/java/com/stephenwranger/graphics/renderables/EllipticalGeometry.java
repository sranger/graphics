package com.stephenwranger.graphics.renderables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.stephenwranger.graphics.Scene;
import com.stephenwranger.graphics.bounds.BoundingSphere;
import com.stephenwranger.graphics.bounds.BoundingVolume;
import com.stephenwranger.graphics.bounds.BoundsUtils;
import com.stephenwranger.graphics.bounds.BoundsUtils.FrustumResult;
import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.CameraUtils;
import com.stephenwranger.graphics.math.PickingHit;
import com.stephenwranger.graphics.math.PickingRay;
import com.stephenwranger.graphics.math.Quat4d;
import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.intersection.Ellipsoid;
import com.stephenwranger.graphics.math.intersection.Plane;
import com.stephenwranger.graphics.utils.BiConsumerSupplier;
import com.stephenwranger.graphics.utils.MathUtils;
import com.stephenwranger.graphics.utils.TupleMath;
import com.stephenwranger.graphics.utils.textures.Texture2d;

public class EllipticalGeometry extends Renderable {
   private static final double                              SCREEN_EDGE_FACTOR = 500;
   private static final double                              SCREEN_AREA_FACTOR = 0.5 * EllipticalGeometry.SCREEN_EDGE_FACTOR * EllipticalGeometry.SCREEN_EDGE_FACTOR;
   private static final int                                 MAX_DEPTH          = Integer.MAX_VALUE;

   private final Ellipsoid                                  ellipsoid;
   private final BiConsumerSupplier<Double, Double, Double> altitudeSupplier;
   private final Consumer<EllipticalSegment>                setTextureFunction;
   private final BoundingSphere                             bounds;
   private final Tuple3d[]                                  mainVertices       = new Tuple3d[12];
   private final int[][]                                    mainFaces          = new int[20][3];
   private final List<EllipticalSegment>                    segments           = new LinkedList<>();
   private final Set<EllipticalSegment>                     renderedSegments   = new HashSet<>();
   private final Tuple3d                                    origin             = new Tuple3d(0, 0, 0);
   private final Color4f                                    color              = Color4f.white();
   //   private final SegmentedVertexBufferPool                  vbo;

   private Texture2d                                        texture            = null;
   private double                                           loadFactor         = 0.75;
   private boolean                                          isLightingEnabled  = true;

   public EllipticalGeometry(final GL2 gl, final Ellipsoid ellipsoid, final double boundedRadius, final int subdivisions, final BiConsumerSupplier<Double, Double, Double> altitudeSupplier,
         final Consumer<EllipticalSegment> setTextureFunction) {
      super(new Tuple3d(), new Quat4d());

      this.ellipsoid = ellipsoid;
      this.altitudeSupplier = altitudeSupplier;
      this.setTextureFunction = setTextureFunction;
      this.bounds = new BoundingSphere(new Tuple3d(), boundedRadius);

      // icosahedron vertices
      final double t = (1.0 + Math.sqrt(5.0)) / 2.0;

      // original geometry
      this.mainVertices[0] = new Tuple3d(-1, t, 0);
      this.mainVertices[1] = new Tuple3d(1, t, 0);
      this.mainVertices[2] = new Tuple3d(-1, -t, 0);
      this.mainVertices[3] = new Tuple3d(1, -t, 0);

      this.mainVertices[4] = new Tuple3d(0, -1, t);
      this.mainVertices[5] = new Tuple3d(0, 1, t);
      this.mainVertices[6] = new Tuple3d(0, -1, -t);
      this.mainVertices[7] = new Tuple3d(0, 1, -t);

      this.mainVertices[8] = new Tuple3d(t, 0, -1);
      this.mainVertices[9] = new Tuple3d(t, 0, 1);
      this.mainVertices[10] = new Tuple3d(-t, 0, -1);
      this.mainVertices[11] = new Tuple3d(-t, 0, 1);

      // faces near v0
      this.mainFaces[0] = new int[] { 0, 11, 5 };
      this.mainFaces[1] = new int[] { 0, 5, 1 };
      this.mainFaces[2] = new int[] { 0, 1, 7 };
      this.mainFaces[3] = new int[] { 0, 7, 10 };
      this.mainFaces[4] = new int[] { 0, 10, 11 };

      // faces next to v0
      this.mainFaces[5] = new int[] { 1, 5, 9 };
      this.mainFaces[6] = new int[] { 5, 11, 4 };
      this.mainFaces[7] = new int[] { 11, 10, 2 };
      this.mainFaces[8] = new int[] { 10, 7, 6 };
      this.mainFaces[9] = new int[] { 7, 1, 8 };

      // faces near v3
      this.mainFaces[10] = new int[] { 3, 9, 4 };
      this.mainFaces[11] = new int[] { 3, 4, 2 };
      this.mainFaces[12] = new int[] { 3, 2, 6 };
      this.mainFaces[13] = new int[] { 3, 6, 8 };
      this.mainFaces[14] = new int[] { 3, 8, 9 };

      // faces next to v3
      this.mainFaces[15] = new int[] { 4, 9, 5 };
      this.mainFaces[16] = new int[] { 2, 4, 11 };
      this.mainFaces[17] = new int[] { 6, 2, 10 };
      this.mainFaces[18] = new int[] { 8, 6, 7 };
      this.mainFaces[19] = new int[] { 9, 8, 1 };

      for (int i = 0; i < this.mainFaces.length; i++) {
         final Tuple3d v0 = new Tuple3d(this.mainVertices[this.mainFaces[i][0]]);
         final Tuple3d v1 = new Tuple3d(this.mainVertices[this.mainFaces[i][1]]);
         final Tuple3d v2 = new Tuple3d(this.mainVertices[this.mainFaces[i][2]]);

         final EllipticalSegment segment = EllipticalSegment.createSegment(v0, v1, v2, 0, this.ellipsoid, this.altitudeSupplier, this.setTextureFunction);
         final List<EllipticalSegment> children = segment.getChildSegments(this.ellipsoid, this.altitudeSupplier, this.setTextureFunction, false);

         for (final EllipticalSegment child : children) {
            this.segments.addAll(child.getChildSegments(this.ellipsoid, this.altitudeSupplier, this.setTextureFunction, false));
         }
      }

      //      final BufferRegion[] bufferRegions = new BufferRegion[] { new VertexRegion(3, DataType.FLOAT), new NormalRegion(DataType.FLOAT), new TextureRegion(2, DataType.FLOAT) };
      //      this.vbo = new SegmentedVertexBufferPool(3, 1000, GL.GL_TRIANGLES, GL.GL_DYNAMIC_DRAW, bufferRegions);
   }

   @Override
   public BoundingVolume getBoundingVolume() {
      return this.bounds;
   }

   @Override
   public PickingHit getIntersection(final PickingRay ray) {
      PickingHit currentHit = PickingRay.NO_HIT;

      for (final EllipticalSegment segment : this.renderedSegments) {
         final PickingHit hit = segment.getIntersection(this, ray);

         if ((currentHit == PickingRay.NO_HIT) || ((hit != null) && (hit.getDistance() < currentHit.getDistance()))) {
            currentHit = hit;
         }
      }

      return currentHit;
   }

   public double getLoadFactor() {
      return this.loadFactor;
   }

   public boolean isLightingEnabled() {
      return this.isLightingEnabled;
   }

   @Override
   public void render(final GL2 gl, final GLU glu, final GLAutoDrawable glDrawable, final Scene scene) {
      gl.glPushMatrix();
      gl.glPushAttrib(GL2.GL_LIGHTING_BIT | GL2.GL_LINE_BIT);

      if (this.isLightingEnabled) {
         gl.glEnable(GLLightingFunc.GL_LIGHTING);
         gl.glEnable(GLLightingFunc.GL_LIGHT0);
      } else {
         gl.glDisable(GLLightingFunc.GL_LIGHTING);
      }

      if (this.texture == null) {
         gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
         gl.glColorMaterial(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE);
      } else {
         gl.glDisable(GLLightingFunc.GL_COLOR_MATERIAL);
         this.texture.enable(gl);
      }

      final Tuple3d origin = scene.getOrigin();

      // TODO split/add/remove via LOD
      //      boolean originChanged = false;

      if (this.origin.distance(origin) > 1) {
         this.origin.set(origin);
         //         originChanged = true;
      }

      final List<EllipticalSegment> previous = new ArrayList<>();
      previous.addAll(this.renderedSegments);
      this.renderedSegments.clear();

      for (final EllipticalSegment segment : this.segments) {
         this.renderedSegments.addAll(this.getSegmentsToRender(gl, scene, segment, false, 0));
      }

      //      for (final EllipticalSegment segment : this.renderedSegments) {
      //         this.loadVertices(gl, segment, originChanged);
      //      }

      for (final EllipticalSegment segment : previous) {
         if (!this.renderedSegments.contains(segment)) {
            //            this.vbo.clearSegmentObject(gl, segment);
            segment.clearTextures(gl);
         }
      }

      gl.glLineWidth(3f);
      //      this.vbo.render(gl, this.renderedSegments);

      for (final EllipticalSegment segment : this.renderedSegments) {
         segment.render(gl, glu, scene);
      }

      gl.glFlush();

      if (this.texture != null) {
         this.texture.disable(gl);
      }

      gl.glPopAttrib();
      gl.glPopMatrix();
   }

   public void setColor(final Color4f color) {
      this.color.setColor(color);
   }

   public void setLightingEnabled(final boolean isLightingEnabled) {
      this.isLightingEnabled = isLightingEnabled;
   }

   public void setLoadFactor(final double loadFactor) {
      this.loadFactor = loadFactor;
   }

   public void setTexture(final Texture2d texture) {
      this.texture = texture;
   }

   private List<EllipticalSegment> getSegmentsToRender(final GL2 gl, final Scene scene, final EllipticalSegment segment, boolean ignoreFrustum, final int depth) {
      final List<EllipticalSegment> toRender = new ArrayList<>();
      final BoundingVolume bounds = segment.getBoundingVolume();
      final Tuple3d origin = scene.getOrigin();
      FrustumResult result = null;

      if (ignoreFrustum) {
         result = FrustumResult.IN;
      } else {
         final Plane[] frustumPlanes = scene.getFrustumPlanes();
         result = BoundsUtils.testFrustum(frustumPlanes, bounds.offset(origin));
         ignoreFrustum = (result == FrustumResult.IN);
      }

      if (result != FrustumResult.OUT) {
         if (depth < EllipticalGeometry.MAX_DEPTH) {
            final GeodesicVertex[] vertices = segment.getVertices();
            final Tuple2d v0ScreenSpace = CameraUtils.gluProject(scene, TupleMath.sub(vertices[0].getVertex(), origin)).xy();
            final Tuple2d v1ScreenSpace = CameraUtils.gluProject(scene, TupleMath.sub(vertices[1].getVertex(), origin)).xy();
            final Tuple2d v2ScreenSpace = CameraUtils.gluProject(scene, TupleMath.sub(vertices[2].getVertex(), origin)).xy();
            final double base = MathUtils.getMax(v0ScreenSpace.x, v1ScreenSpace.x, v2ScreenSpace.x) - MathUtils.getMin(v0ScreenSpace.x, v1ScreenSpace.x, v2ScreenSpace.x);
            final double height = MathUtils.getMax(v0ScreenSpace.y, v1ScreenSpace.y, v2ScreenSpace.y) - MathUtils.getMin(v0ScreenSpace.y, v1ScreenSpace.y, v2ScreenSpace.y);
            final double area = 0.5 * base * height;

            if ((area >= (EllipticalGeometry.SCREEN_AREA_FACTOR * this.loadFactor)) || (base >= (EllipticalGeometry.SCREEN_EDGE_FACTOR * this.loadFactor)) || (height >= (EllipticalGeometry.SCREEN_EDGE_FACTOR * this.loadFactor))) {
               final List<EllipticalSegment> children = segment.getChildSegments(this.ellipsoid, this.altitudeSupplier, this.setTextureFunction, true);

               if ((children == null) || children.isEmpty() || !EllipticalGeometry.hasTextures(children)) {
                  toRender.add(segment);
               } else {
                  for (final EllipticalSegment child : children) {
                     toRender.addAll(this.getSegmentsToRender(gl, scene, child, ignoreFrustum, depth + 1));
                  }
               }
            } else {
               toRender.add(segment);
            }
         } else {
            toRender.add(segment);
         }
      }

      return toRender;

   }

   private static boolean hasTextures(final List<EllipticalSegment> segments) {
      for (final EllipticalSegment segment : segments) {
         if (segment.getTextureCount() == 0) {
            return false;
         }
      }

      return true;
   }

   //   private void loadVertices(final GL2 gl, final EllipticalSegment segment, final boolean originChanged) {
   //      if (originChanged || (segment.getSegmentPoolIndex() == -1) || (segment.getBufferIndex() == -1)) {
   //         this.vbo.setSegmentObject(gl, this.origin, segment);
   //      }
   //   }
}
