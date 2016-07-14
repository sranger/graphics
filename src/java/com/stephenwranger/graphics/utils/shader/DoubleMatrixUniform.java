package com.stephenwranger.graphics.utils.shader;

import com.jogamp.opengl.GL2;

public class DoubleMatrixUniform extends ShaderUniform {

   DoubleMatrixUniform(final ShaderProgram program, final String name, final int location, final ShaderParameterType type, final int arraySize) {
      super(program, name, location, type, arraySize);
   }

   /**
    * Binds a Matrix to this Shader Program with the given uniform name. The shader
    * program must be active for this call to function.
    *
    * @param gl
    *           a current OpenGL 3 context
    * @param transpose
    *           true to transpose the matrix before sending down to OpenGL.
    * @param values
    *           A float array containing the matrix values (must have at least rows * cols elements).
    */
   public void set(final GL2 gl, final boolean transpose, final double... values) {
      validateSize(0, values.length);
      setter.setDoubleMatrix(gl, location, arrayLength, values, transpose);
   }

   /**
    * Binds a Matrix to this Shader Program with the given uniform name. The shader
    * program must be active for this call to function.
    *
    * @param gl
    *           a current OpenGL 3 context
    * @param transpose
    *           true to transpose the matrix before sending down to OpenGL.
    * @param startIndex
    *           The array index of the array-typed uniform to set. Elements are written starting from this index, but
    *           may still span multiple indices.
    * @param values
    *           A float array containing the matrix values (must have at least rows * cols elements).
    */
   public void setArrayElement(final GL2 gl, final boolean transpose, final int startIndex, final double... values) {
      validateSize(startIndex, values.length);
      setter.setDoubleMatrix(gl, location + startIndex, arrayLength, values, transpose);
   }
}
