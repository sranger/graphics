package com.stephenwranger.graphics.math.intersection;

import java.util.List;

import com.stephenwranger.graphics.math.Tuple2d;

public interface LineIntersectable {
   public boolean contains(final LineSegment segment);

   public List<Tuple2d> getIntersection(final LineSegment segment);
}
