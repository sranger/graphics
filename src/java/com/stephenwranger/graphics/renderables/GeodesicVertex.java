package com.stephenwranger.graphics.renderables;

import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.utils.buffers.Vertex;

public class GeodesicVertex extends Vertex {
   private final Tuple3d lonLatAlt;

   public GeodesicVertex(final Tuple3d baseVertex, final Tuple3d vertex, final Vector3d normal, final Tuple2d texCoord, final Color4f color, final Tuple3d lonLatAlt) {
      super(baseVertex, vertex, normal, texCoord, color);

      this.lonLatAlt = new Tuple3d(lonLatAlt);
   }

   public Tuple3d getGeodesicVertex() {
      return new Tuple3d(this.lonLatAlt);
   }
}
