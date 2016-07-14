package com.stephenwranger.graphics.utils.shader;

import com.jogamp.opengl.GL2;

/**
 * Abstraction for the appropriate glUniformXX(...) function to call for each uniform type. This base class throws an
 * exception from each method. Subclasses for each uniform type should override only the method(s) (typically only
 * 1) that correspond to the legal Java type for the uniform type.
 */
public class ShaderUniformSetter {
   void setInt(final GL2 gl, final int uniform, final int arrayLength, final int[] values) {
      throw new IllegalArgumentException("Attempt to set int values for uniform: " + uniform + ".");
   };

   void setFloat(final GL2 gl, final int uniform, final int arrayLength, final float[] values) {
      throw new IllegalArgumentException("Attempt to set float values for uniform: " + uniform + ".");
   };

   void setFloatMatrix(final GL2 gl, final int uniform, final int arrayLength, final float[] values,
         final boolean transpose) {
      throw new IllegalArgumentException("Attempt to set float matrix values for uniform: " + uniform + ".");
   };

   void setDouble(final GL2 gl, final int uniform, final int arrayLength, final double[] values) {
      throw new IllegalArgumentException("Attempt to set double values for uniform: " + uniform + ".");
   };

   void setDoubleMatrix(final GL2 gl, final int uniform, final int arrayLength, final double[] values,
         final boolean transpose) {
      throw new IllegalArgumentException("Attempt to set double matrix values for uniform: " + uniform + ".");
   };

   static ShaderUniformSetter getSetter(final int componentType, final int cols, final int rows) {
      /* Vector or scalar type */
      if (rows > 4 || cols > 4) {
         return null;
      }
      ShaderUniformSetter[][] setters = null;
      switch (componentType) {
         case GL2.GL_FLOAT:
            setters = f;
            break;
         case GL2.GL_BOOL:
         case GL2.GL_INT:
            setters = i;
            break;
         case GL2.GL_UNSIGNED_INT:
            setters = ui;
            break;
      }
      if (setters == null) {
         return null;
      }
      return setters[cols - 1][rows - 1];
   }

   /*
    * Arrays of ShaderUniformSetters for each set of glUniform calls, outer array index is for the number of
    * scalar/vector/matrix columns, inner array is for number of rows in a matrix. Null elements are present where
    * there is no corresponding glUniform function. This layout was designed to simplify/speed-up getSetter(...).
    */
   private static final ShaderUniformSetter[][] i = new ShaderUniformSetter[4][4];

   private static final ShaderUniformSetter[][] ui = new ShaderUniformSetter[4][4];

   private static final ShaderUniformSetter[][] f = new ShaderUniformSetter[4][4];

   static {
      i[0][0] = new ShaderUniformSetter() {
         @Override
         public void setInt(final GL2 gl, final int uniform, final int arrayLength, final int[] values) {
            gl.glUniform1iv(uniform, arrayLength, values, 0);
         }
      };
      i[1][0] = new ShaderUniformSetter() {
         @Override
         public void setInt(final GL2 gl, final int uniform, final int arrayLength, final int[] values) {
            gl.glUniform2iv(uniform, arrayLength, values, 0);
         }
      };
      i[2][0] = new ShaderUniformSetter() {
         @Override
         public void setInt(final GL2 gl, final int uniform, final int arrayLength, final int[] values) {
            gl.glUniform3iv(uniform, arrayLength, values, 0);
         }
      };
      i[3][0] = new ShaderUniformSetter() {
         @Override
         public void setInt(final GL2 gl, final int uniform, final int arrayLength, final int[] values) {
            gl.glUniform4iv(uniform, arrayLength, values, 0);
         }
      };

      ui[0][0] = new ShaderUniformSetter() {
         @Override
         public void setInt(final GL2 gl, final int uniform, final int arrayLength, final int[] values) {
            gl.glUniform1uiv(uniform, arrayLength, values, 0);
         }
      };
      ui[1][0] = new ShaderUniformSetter() {
         @Override
         public void setInt(final GL2 gl, final int uniform, final int arrayLength, final int[] values) {
            gl.glUniform2uiv(uniform, arrayLength, values, 0);
         }
      };
      ui[2][0] = new ShaderUniformSetter() {
         @Override
         public void setInt(final GL2 gl, final int uniform, final int arrayLength, final int[] values) {
            gl.glUniform3uiv(uniform, arrayLength, values, 0);
         }
      };
      ui[3][0] = new ShaderUniformSetter() {
         @Override
         public void setInt(final GL2 gl, final int uniform, final int arrayLength, final int[] values) {
            gl.glUniform4uiv(uniform, arrayLength, values, 0);
         }
      };

      f[0][0] = new ShaderUniformSetter() {
         @Override
         public void setFloat(final GL2 gl, final int uniform, final int arrayLength, final float[] values) {
            gl.glUniform1fv(uniform, arrayLength, values, 0);
         }
      };
      f[1][0] = new ShaderUniformSetter() {
         @Override
         public void setFloat(final GL2 gl, final int uniform, final int arrayLength, final float[] values) {
            gl.glUniform2fv(uniform, arrayLength, values, 0);
         }
      };
      f[1][1] = new ShaderUniformSetter() {
         @Override
         public void setFloatMatrix(final GL2 gl, final int uniform, final int arrayLength, final float[] values,
               final boolean transpose) {
            gl.glUniformMatrix2fv(uniform, arrayLength, transpose, values, 0);
         }
      };
      f[1][2] = new ShaderUniformSetter() {
         @Override
         public void setFloatMatrix(final GL2 gl, final int uniform, final int arrayLength, final float[] values,
               final boolean transpose) {
            gl.glUniformMatrix2x3fv(uniform, arrayLength, transpose, values, 0);
         }
      };
      f[1][3] = new ShaderUniformSetter() {
         @Override
         public void setFloatMatrix(final GL2 gl, final int uniform, final int arrayLength, final float[] values,
               final boolean transpose) {
            gl.glUniformMatrix2x4fv(uniform, arrayLength, transpose, values, 0);
         }
      };
      f[2][0] = new ShaderUniformSetter() {
         @Override
         public void setFloat(final GL2 gl, final int uniform, final int arrayLength, final float[] values) {
            gl.glUniform3fv(uniform, arrayLength, values, 0);
         }
      };
      f[2][1] = new ShaderUniformSetter() {
         @Override
         public void setFloatMatrix(final GL2 gl, final int uniform, final int arrayLength, final float[] values,
               final boolean transpose) {
            gl.glUniformMatrix3x2fv(uniform, arrayLength, transpose, values, 0);
         }
      };
      f[2][2] = new ShaderUniformSetter() {
         @Override
         public void setFloatMatrix(final GL2 gl, final int uniform, final int arrayLength, final float[] values,
               final boolean transpose) {
            gl.glUniformMatrix3fv(uniform, arrayLength, transpose, values, 0);
         }
      };
      f[2][3] = new ShaderUniformSetter() {
         @Override
         public void setFloatMatrix(final GL2 gl, final int uniform, final int arrayLength, final float[] values,
               final boolean transpose) {
            gl.glUniformMatrix3x4fv(uniform, arrayLength, transpose, values, 0);
         }
      };
      f[3][0] = new ShaderUniformSetter() {
         @Override
         public void setFloat(final GL2 gl, final int uniform, final int arrayLength, final float[] values) {
            gl.glUniform4fv(uniform, arrayLength, values, 0);
         }
      };
      f[3][1] = new ShaderUniformSetter() {
         @Override
         public void setFloatMatrix(final GL2 gl, final int uniform, final int arrayLength, final float[] values,
               final boolean transpose) {
            gl.glUniformMatrix4x2fv(uniform, arrayLength, transpose, values, 0);
         }
      };
      f[3][2] = new ShaderUniformSetter() {
         @Override
         public void setFloatMatrix(final GL2 gl, final int uniform, final int arrayLength, final float[] values,
               final boolean transpose) {
            gl.glUniformMatrix4x3fv(uniform, arrayLength, transpose, values, 0);
         }
      };
      f[3][3] = new ShaderUniformSetter() {
         @Override
         public void setFloatMatrix(final GL2 gl, final int uniform, final int arrayLength, final float[] values,
               final boolean transpose) {
            gl.glUniformMatrix4fv(uniform, arrayLength, transpose, values, 0);
         }
      };

   }
}
