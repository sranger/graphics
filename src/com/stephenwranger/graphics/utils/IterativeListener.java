package com.stephenwranger.graphics.utils;

import java.util.List;

public interface IterativeListener {
   public void step(final Iterative source, final String message, final List<Object> payload);
}
