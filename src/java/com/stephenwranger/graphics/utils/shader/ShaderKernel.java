package com.stephenwranger.graphics.utils.shader;

import java.io.InputStream;

import com.stephenwranger.graphics.utils.FileUtils;

public class ShaderKernel {
   private final String source;
   private final ShaderStage stage;
   
   public ShaderKernel(final String source, final ShaderStage stage) {
      this.source = source;
      this.stage = stage;
   }
   
   public ShaderKernel(final InputStream sourceStream, final ShaderStage stage) {
      this.source = FileUtils.getStreamContentsString(sourceStream);
      this.stage = stage;
   }
}
