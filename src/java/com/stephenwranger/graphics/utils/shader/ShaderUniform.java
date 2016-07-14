package com.stephenwranger.graphics.utils.shader;

import com.jogamp.opengl.GL2;

public class ShaderUniform extends ShaderParameter {
   protected final ShaderUniformSetter setter;

   protected ShaderUniform(final ShaderProgram program, final String name, final int location, final ShaderParameterType type, final int arraySize) {
      super(program, name, location, type, arraySize);
      
      this.setter = ShaderUniformSetter.getSetter(type.componentType, type.cols, type.rows);
   }

   protected final void validateSize(final int startIndex, final int valueCount) {
      if (valueCount != (arrayLength - startIndex) * type.componentCount) {
         throw new IllegalArgumentException("Attempt to set " + valueCount + " values for uniform " + name
               + " that requires " + ((arrayLength - startIndex) * type.componentCount) + "(" + type.sizeBytes
               + " values * (" + arrayLength + " - " + startIndex + ")" + " array elements).");
      }
   }

   static ShaderUniform createUniform(final ShaderProgram program, final String name, final int location, final ShaderParameterType type, final int arraySize) {
      switch (type.componentType) {
         case GL2.GL_FLOAT:
            if (type.rows > 1) {
               return new FloatMatrixUniform(program, name, location, type, arraySize);
            }
            return new FloatUniform(program, name, location, type, arraySize);
         case GL2.GL_BOOL:
         case GL2.GL_INT:
         case GL2.GL_UNSIGNED_INT:
            return new IntUniform(program, name, location, type, arraySize);
         case GL2.GL_DOUBLE:
            if (type.rows > 1) {
               return new DoubleMatrixUniform(program, name, location, type, arraySize);
            }
            return new DoubleUniform(program, name, location, type, arraySize);
      }
      /* Type not recognized, return the most generic Uniform */
      System.err.println("Unrecognized uniform data type: " + type + " while creating uniform: " + name + ", for program: " + program.getName() + ", creating generic Uniform.");
      return new ShaderUniform(program, name, location, ShaderParameterType.UNKNOWN, arraySize);
   }
}
