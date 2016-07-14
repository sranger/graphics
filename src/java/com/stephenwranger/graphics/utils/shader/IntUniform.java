package com.stephenwranger.graphics.utils.shader;

import com.jogamp.opengl.GL2;

public class IntUniform extends ShaderUniform {

   IntUniform(final ShaderProgram program, final String name, final int location, final ShaderParameterType type, final int arraySize) {
      super(program, name, location, type, arraySize);
   }

   /**
    * Binds a specified uniform variable in the shader program to the specified integer value(s). The shader
    * program must be active for this call to function. This variant supports specification of values for arrays such
    * as <code>ivec4 v[2];</code> as well as single value (int) and vector (ivecX) types.
    * <p>
    * This version also works for unsigned integer types.
    *
    * @param gl
    *           The current OpenGL context.
    * @param values
    *           The integer value(s) to bind to the uniform. This can be either 1, 2, 3, or 4 values, times the
    *           number of array elements.
    * @throws IllegalArgumentException
    *            If the uniform does not have an integer type, or an incorrect number of values were provided.
    */
   public void set(final GL2 gl, final int... values) {
      validateSize(0, values.length);
      setter.setInt(gl, location, arrayLength, values);
   }

   /**
    * Binds a specified uniform variable in the shader program to the specified integer value(s). The shader
    * program must be active for this call to function. This variant supports specification of values for arrays such
    * as <code>ivec4 v[2];</code> as well as single value (int) and vector (ivecX) types.
    * <p>
    * This version also works for unsigned integer types.
    *
    * @param gl
    *           The current OpenGL context.
    * @param startIndex
    *           The array index of the array-typed uniform to set. Elements are written starting from this index, but
    *           may still span multiple indices.
    * @param values
    *           The integer value(s) to bind to the uniform. This can be either 1, 2, 3, or 4 values, times the
    *           number of array elements.
    * @throws IllegalArgumentException
    *            If the uniform does not have an integer type, or an incorrect number of values were provided.
    */
   public void setArrayElement(final GL2 gl, final int startIndex, final int... values) {
      validateSize(startIndex, values.length);
      setter.setInt(gl, location + startIndex, arrayLength, values);
   }
}
