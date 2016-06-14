package com.stephenwranger.graphics.math.intersection;

import com.stephenwranger.graphics.math.Tuple2d;

public interface PointIntersectable {
   public boolean contains(final Tuple2d point);
}
