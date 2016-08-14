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
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.math.intersection.Ellipsoid;
import com.stephenwranger.graphics.math.intersection.Plane;
import com.stephenwranger.graphics.utils.BiConsumerSupplier;
import com.stephenwranger.graphics.utils.TupleMath;
import com.stephenwranger.graphics.utils.textures.Texture2d;

public class EllipticalGeometry extends Renderable {
   public static final int                                  CENTER             = 0;
   public static final int                                  NORTHWEST          = 1;
   public static final int                                  NORTH              = 2;
   public static final int                                  NORTHEAST          = 3;
   public static final int                                  EAST               = 4;
   public static final int                                  SOUTHEAST          = 5;
   public static final int                                  SOUTH              = 6;
   public static final int                                  SOUTHWEST          = 7;
   public static final int                                  WEST               = 8;

   private static final double                              SCREEN_EDGE_FACTOR = 500;
   private static final double                              SCREEN_AREA_FACTOR = EllipticalGeometry.SCREEN_EDGE_FACTOR * EllipticalGeometry.SCREEN_EDGE_FACTOR;
   private static final int                                 MAX_DEPTH          = Integer.MAX_VALUE;
   private static final double                              MAX_OFFSET         = 30.0;

   //@formatter:off
   private static final double[] LONGITUDE_OFFSETS = new double[] {
         EllipticalGeometry.MAX_OFFSET / 2.0,   // center
         0.0,                                   // NW
         EllipticalGeometry.MAX_OFFSET / 2.0,   // N
         EllipticalGeometry.MAX_OFFSET,         // NE
         EllipticalGeometry.MAX_OFFSET,         // E
         EllipticalGeometry.MAX_OFFSET,         // SE
         EllipticalGeometry.MAX_OFFSET / 2.0,   // S
         0.0,                                   // SW
         0.0                                    // W
   };
   private static final double[] LATITUDE_OFFSETS = new double[] {
         EllipticalGeometry.MAX_OFFSET / 2.0,   // center
         0.0,                                   // NW
         0.0,                                   // N
         0.0,                                   // NE
         EllipticalGeometry.MAX_OFFSET / 2.0,   // E
         EllipticalGeometry.MAX_OFFSET,         // SE
         EllipticalGeometry.MAX_OFFSET,         // S
         EllipticalGeometry.MAX_OFFSET,         // SW
         EllipticalGeometry.MAX_OFFSET / 2.0    // W
   };
   //@formatter:on

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

      for (double lon = -180; lon < 180; lon += EllipticalGeometry.MAX_OFFSET) {
         for (double lat = -90; lat < 90; lat += EllipticalGeometry.MAX_OFFSET) {
            final GeodesicVertex[] vertices = new GeodesicVertex[EllipticalGeometry.LONGITUDE_OFFSETS.length];

            for (int i = 0; i < EllipticalGeometry.LONGITUDE_OFFSETS.length; i++) {
               final double lonWithOffset = lon + EllipticalGeometry.LONGITUDE_OFFSETS[i];
               final double latWithOffset = lat + EllipticalGeometry.LATITUDE_OFFSETS[i];
               final double alt = altitudeSupplier.getValue(lonWithOffset, latWithOffset);

               final Tuple3d lonLatAlt = new Tuple3d(lonWithOffset, latWithOffset, alt);
               final Tuple3d xyz = this.ellipsoid.toXYZ(lonLatAlt);
               final Vector3d normal = new Vector3d(xyz);
               normal.normalize();
               final Tuple3d base = new Tuple3d(normal);
               final Tuple2d texCoords = new Tuple2d((lonWithOffset + 180) / 360.0, (latWithOffset + 90) / 180.0);
               vertices[i] = new GeodesicVertex(base, xyz, normal, texCoords, Color4f.white(), lonLatAlt);
            }

            final EllipticalSegment segment = new EllipticalSegment(vertices, 0);
            setTextureFunction.accept(segment);
            this.segments.add(segment);
         }
      }

      if (subdivisions > 0) {
         // pre-segment so initial rendering isn't chunky
         for (final EllipticalSegment segment : this.segments) {
            this.split(segment, 0, subdivisions);
         }
      }
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
         result = BoundsUtils.testFrustum(frustumPlanes, new BoundingSphere(bounds.getCenter(), bounds.getSpannedDistance(null)).offset(origin));
         ignoreFrustum = (result == FrustumResult.IN);
      }

      if (result != FrustumResult.OUT) {
         if (depth < EllipticalGeometry.MAX_DEPTH) {
            final GeodesicVertex[] vertices = segment.getVertices();
            final Tuple2d[] screenSpace = new Tuple2d[vertices.length];

            for (int i = 0; i < vertices.length; i++) {
               screenSpace[i] = CameraUtils.gluProject(scene, TupleMath.sub(vertices[i].getVertex(), origin)).xy();
            }

            final Tuple2d max = TupleMath.getMax(screenSpace);
            final Tuple2d min = TupleMath.getMin(screenSpace);
            final double base = max.x - min.x;
            final double height = max.y - min.y;
            final double area = base * height;

            final boolean isArea = area >= (EllipticalGeometry.SCREEN_AREA_FACTOR * this.loadFactor);
            final boolean isEdges = ((base >= EllipticalGeometry.SCREEN_EDGE_FACTOR) && (height >= (EllipticalGeometry.SCREEN_EDGE_FACTOR / 2.0)))
                  || ((height >= EllipticalGeometry.SCREEN_EDGE_FACTOR) && (base >= (EllipticalGeometry.SCREEN_EDGE_FACTOR / 2.0)));

            if (isArea || isEdges) {
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

   private void split(final EllipticalSegment segment, final int depth, final int maxDepth) {
      final List<EllipticalSegment> children = segment.getChildSegments(this.ellipsoid, this.altitudeSupplier, this.setTextureFunction, false);

      if (depth < maxDepth) {
         for (final EllipticalSegment child : children) {
            this.split(child, depth + 1, maxDepth);
         }
      }
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
