package com.stephenwranger.graphics.utils;


public class ModelKeyframe {
   private final int frameIndex;
   private final double frameTimeSeconds;
   private final double frameTime;
   private final double[] values;
   
   public ModelKeyframe(final int frameIndex, final double frameTimeSeconds, final double[] values) {
      this.frameIndex = frameIndex;
      this.frameTimeSeconds = frameTimeSeconds;
      this.values = values.clone();
      
      this.frameTime = (this.frameIndex * this.frameTimeSeconds) * MathUtils.SECONDS_TO_MILLISECONDS;
   }
   
   public int getFrameIndex() {
      return frameIndex;
   }
   
   public double getFrameTime() {
      return frameTime;
   }
   
   public double[] getValues() {
      return values.clone();
   }
}
