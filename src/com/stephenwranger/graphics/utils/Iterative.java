package com.stephenwranger.graphics.utils;

public interface Iterative {
   public void continueIteration();

   public void addIterativeListener(final IterativeListener listener);

   public void removeIterativeListener(final IterativeListener listener);
}
