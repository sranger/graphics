package com.stephenwranger.graphics.utils.shader;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.GLBuffers;

/**
 * This class stores information about a shader. It may be used to
 * generate a shader, or be used to store information from an already
 * existing shader.
 * The reason this class really exists is that enums offer compile time checking
 * where if the user just used the int value from the GL2 class, then validation
 * is significantly more difficult and could make the code much more difficult
 * to read.
 */
public enum ShaderParameterType {

   BYTE(GL2.GL_BYTE, 1, GL2.GL_BYTE),
   UNSIGNED_BYTE(GL2.GL_UNSIGNED_BYTE, 1, GL2.GL_UNSIGNED_BYTE),
   SHORT(GL2.GL_SHORT, 1, GL2.GL_SHORT),
   UNSIGNED_SHORT(GL2.GL_UNSIGNED_SHORT, 1, GL2.GL_UNSIGNED_SHORT),

   UNKNOWN(GL2.GL_FALSE, 0, GL2.GL_FALSE),

   BOOL(GL2.GL_BOOL, 1, GL2.GL_BOOL),
   BOOL_VEC2(GL2.GL_BOOL_VEC2, 2, GL2.GL_BOOL),
   BOOL_VEC3(GL2.GL_BOOL_VEC3, 3, GL2.GL_BOOL),
   BOOL_VEC4(GL2.GL_BOOL_VEC4, 4, GL2.GL_BOOL),

   FLOAT(GL2.GL_FLOAT, 1, GL2.GL_FLOAT),
   FLOAT_VEC2(GL2.GL_FLOAT_VEC2, 2, GL2.GL_FLOAT), // a two-component
   // floating-point vector
   FLOAT_VEC3(GL2.GL_FLOAT_VEC3, 3, GL2.GL_FLOAT), // a three-component floating-point vector
   FLOAT_VEC4(GL2.GL_FLOAT_VEC4, 4, GL2.GL_FLOAT), // a four-component floating-point vector

   DOUBLE(GL2.GL_DOUBLE, 1, GL2.GL_DOUBLE),

   INT(GL2.GL_INT, 1, GL2.GL_INT),
   INT_VEC2(GL2.GL_INT_VEC2, 2, GL2.GL_INT), // a two-component signed integer vector
   INT_VEC3(GL2.GL_INT_VEC3, 3, GL2.GL_INT), // a three-component signed integer vector
   INT_VEC4(GL2.GL_INT_VEC4, 4, GL2.GL_INT), // a four-component signed integer vector

   UNSIGNED_INT(GL2.GL_UNSIGNED_INT, 1, GL2.GL_UNSIGNED_INT),
   UNSIGNED_INT_VEC2(GL2.GL_UNSIGNED_INT_VEC2, 2, GL2.GL_UNSIGNED_INT), // a two-component unsigned
                                                                        // integer vector
   UNSIGNED_INT_VEC3(GL2.GL_UNSIGNED_INT_VEC3, 3, GL2.GL_UNSIGNED_INT), // a three-component unsigned
                                                                        // integer vector
   UNSIGNED_INT_VEC4(GL2.GL_UNSIGNED_INT_VEC4, 4, GL2.GL_UNSIGNED_INT), // a four-component unsigned
                                                                        // integer vector

   FLOAT_MAT2(GL2.GL_FLOAT_MAT2, 2, 2, GL2.GL_FLOAT),
   FLOAT_MAT2x3(GL2.GL_FLOAT_MAT2x3, 2, 3, GL2.GL_FLOAT),
   FLOAT_MAT2x4(GL2.GL_FLOAT_MAT2x4, 2, 4, GL2.GL_FLOAT),
   FLOAT_MAT3(GL2.GL_FLOAT_MAT3, 3, 3, GL2.GL_FLOAT),
   FLOAT_MAT3x2(GL2.GL_FLOAT_MAT3x2, 3, 2, GL2.GL_FLOAT),
   FLOAT_MAT3x4(GL2.GL_FLOAT_MAT3x4, 3, 4, GL2.GL_FLOAT),
   FLOAT_MAT4(GL2.GL_FLOAT_MAT4, 4, 4, GL2.GL_FLOAT),
   FLOAT_MAT4x2(GL2.GL_FLOAT_MAT4x2, 4, 2, GL2.GL_FLOAT),
   FLOAT_MAT4x3(GL2.GL_FLOAT_MAT4x3, 4, 3, GL2.GL_FLOAT),


   SAMPLER_1D(GL2.GL_SAMPLER_1D, 1, GL2.GL_INT),
   SAMPLER_2D(GL2.GL_SAMPLER_2D, 1, GL2.GL_INT),
   SAMPLER_3D(GL2.GL_SAMPLER_3D, 1, GL2.GL_INT),

   SAMPLER_1D_SHADOW(GL2.GL_SAMPLER_1D_SHADOW, 1, GL2.GL_INT),
   SAMPLER_2D_SHADOW(GL2.GL_SAMPLER_2D_SHADOW, 1, GL2.GL_INT),

   SAMPLER_1D_ARRAY(GL2.GL_SAMPLER_1D_ARRAY, 1, GL2.GL_INT),
   SAMPLER_2D_ARRAY(GL2.GL_SAMPLER_2D_ARRAY, 1, GL2.GL_INT),

   SAMPLER_1D_ARRAY_SHADOW(GL2.GL_SAMPLER_1D_ARRAY_SHADOW, 1, GL2.GL_INT),
   SAMPLER_2D_ARRAY_SHADOW(GL2.GL_SAMPLER_2D_ARRAY_SHADOW, 1, GL2.GL_INT),

   SAMPLER_2D_MULTISAMPLE(GL2.GL_SAMPLER_2D_MULTISAMPLE, 1, GL2.GL_INT),
   SAMPLER_2D_MULTISAMPLE_ARRAY(GL2.GL_SAMPLER_2D_MULTISAMPLE_ARRAY, 1, GL2.GL_INT),

   SAMPLER_CUBE_SHADOW(GL2.GL_SAMPLER_CUBE_SHADOW, 1, GL2.GL_INT),

   SAMPLER_BUFFER(GL2.GL_SAMPLER_BUFFER, 1, GL2.GL_INT),

   SAMPLER_2D_RECT(GL2.GL_SAMPLER_2D_RECT, 1, GL2.GL_INT),
   SAMPLER_2D_RECT_SHADOW(GL2.GL_SAMPLER_2D_RECT_SHADOW, 1, GL2.GL_INT),

   INT_SAMPLER_1D(GL2.GL_INT_SAMPLER_1D, 1, GL2.GL_INT),
   INT_SAMPLER_2D(GL2.GL_INT_SAMPLER_2D, 1, GL2.GL_INT),
   INT_SAMPLER_3D(GL2.GL_INT_SAMPLER_3D, 1, GL2.GL_INT),

   INT_SAMPLER_1D_ARRAY(GL2.GL_INT_SAMPLER_1D_ARRAY, 1, GL2.GL_INT),
   INT_SAMPLER_2D_ARRAY(GL2.GL_INT_SAMPLER_2D_ARRAY, 1, GL2.GL_INT),

   INT_SAMPLER_2D_MULTISAMPLE(GL2.GL_INT_SAMPLER_2D_MULTISAMPLE, 1, GL2.GL_INT),
   INT_SAMPLER_2D_MULTISAMPLE_ARRAY(GL2.GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, 1, GL2.GL_INT),

   INT_SAMPLER_BUFFER(GL2.GL_INT_SAMPLER_BUFFER, 1, GL2.GL_INT),

   INT_SAMPLER_2D_RECT(GL2.GL_INT_SAMPLER_2D_RECT, 1, GL2.GL_INT),

   UNSIGNED_INT_SAMPLER_1D(GL2.GL_UNSIGNED_INT_SAMPLER_1D, 1, GL2.GL_INT),
   UNSIGNED_INT_SAMPLER_2D(GL2.GL_UNSIGNED_INT_SAMPLER_2D, 1, GL2.GL_INT),
   UNSIGNED_INT_SAMPLER_3D(GL2.GL_UNSIGNED_INT_SAMPLER_3D, 1, GL2.GL_INT),

   UNSIGNED_INT_SAMPLER_1D_ARRAY(GL2.GL_UNSIGNED_INT_SAMPLER_1D_ARRAY, 1, GL2.GL_INT),
   UNSIGNED_INT_SAMPLER_2D_ARRAY(GL2.GL_UNSIGNED_INT_SAMPLER_2D_ARRAY, 1, GL2.GL_INT),

   UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE(GL2.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE, 1, GL2.GL_INT),
   UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY(GL2.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, 1, GL2.GL_INT),

   UNSIGNED_INT_SAMPLER_BUFFER(GL2.GL_UNSIGNED_INT_SAMPLER_BUFFER, 1, GL2.GL_INT),

   UNSIGNED_INT_SAMPLER_2D_RECT(GL2.GL_UNSIGNED_INT_SAMPLER_2D_RECT, 1, GL2.GL_INT),

   ;
   /** The actual OpenGL integer that maps the enum */
   public final int glType;

   /** The number of arguments for the length, ie vec2x4 is 8 */
   public final int componentCount;

   /** The size of the field in OpenGL units (this should be bytes) */
   public final int sizeBytes;

   /**
    * The number of columns in the data type (for matrix types), or the number of components for scalar or vector types.
    */
   public final int cols;

   /**
    * The number of rows in the data type. This is always 1 for scalar and vector types.
    */
   public final int rows;

   /**
    * This is the primitive type, so if it's an array of floats, then this
    * is a float (this is a GL contstant such as GL.GL_FLOAT).
    */
   public final int componentType;


   /**
    * Defines an enum constant with the specified OpenGL type constant and size information. Use this constructor for
    * scalar and vector data types.
    * 
    * @param parameterDataType
    *           The OpenGL data type (GL_FLOAT or GL_FLOAT_MAT4x2 for example).
    * @param cols
    *           The number of components in the parameter (e.g. 1 for scalar values, 4 for vec4, etc).
    * @param componentDataType
    *           The base component type (e.g. GL_FLOAT for GL_FLOAT, GL_FLOAT_VEC3, etc).
    */
   ShaderParameterType(final int parameterDataType, final int cols, final int componentDataType) {
      this(parameterDataType, cols, 1, componentDataType);
   }

   /**
    * Defines an enum constant with the specified OpenGL type constant and size information. Use this constructor for
    * matrix types.
    * 
    * @param parameterDataType
    *           The OpenGL data type (GL_FLOAT or GL_FLOAT_MAT4x2 for example).
    * @param cols
    *           The number of columns in a matrix, or the number of components in a vector type (1 for float, 3 for
    *           vec3, 2 for mat2x3, etc).
    * @param rows
    *           The number of rows in a matrix, or 1 for a vector type (1 for float, 1 for
    *           vec3, 3 for mat2x3, etc).
    * @param componentDataType
    *           The base component type (e.g. GL_FLOAT for GL_FLOAT, GL_FLOAT_VEC3, etc).
    */
   ShaderParameterType(final int parameterDataType, final int cols, final int rows, final int componentDataType) {
      this.glType = parameterDataType;
      this.componentCount = cols * rows;
      this.componentType = componentDataType;
      this.sizeBytes = GLBuffers.sizeOfGLType(componentDataType) * componentCount;
      this.cols = cols;
      this.rows = rows;
   }

   /**
    * Generate the TYPE from the OpenGL int value.
    * 
    * @param glInt
    *           the OpenGL data type constant (e.g. GL_FLOAT, GL_FLOAT_VEC3, etc).
    * @return Enum constant for the specified type, or {@link #UNKNOWN} if the type does not match any other constant.
    */
   public static ShaderParameterType from(final int glInt) {
      for (ShaderParameterType type : values()) {
         if (type.glType == glInt)
            return type;
      }
      return UNKNOWN;
   }

   /**
    * An easy mapping table for determining types indexed by [width-1][height-1]
    */
   private static ShaderParameterType[][] FLOAT_TABLE = new ShaderParameterType[][] {
         { FLOAT, FLOAT_VEC2, FLOAT_VEC3, FLOAT_VEC4 }, // 1x1, 1x2, 1x3, 1x4
         { null, FLOAT_MAT2, FLOAT_MAT3x2, FLOAT_MAT3x4 }, // 2x1, 2x2, 2x3, 2x4
         { null, FLOAT_MAT3x2, FLOAT_MAT3, FLOAT_MAT3x4 }, // 3x1, 3x2, 3x3, 3x4
         { null, FLOAT_MAT4x2, FLOAT_MAT4x3, FLOAT_MAT4 } // 4x1, 4x2, 4x3, 4x4
   };

   /*
    * While the int type doesn't have matrix versions, this makes it easier
    * to add when they finaly do
    */
   private static ShaderParameterType[][] INT_TABLE = new ShaderParameterType[][] { { INT, INT_VEC2, INT_VEC3, INT_VEC4 }, // 1x1,
                                                                                                                           // 1x2,
                                                                                                                           // 1x3,
                                                                                                                           // 1x4
   };

   /**
    * Given the width, height, and Class type, which OpenGL construct matches.
    * This does assume that all the values sent are of the same type, it does not
    * check it.
    * 
    * @param width
    *           the width of the data, 1-4 for arrays and matrices
    * @param height
    *           the height of the data, 1 for arrays, 2-4 for matrices
    * @param value
    *           the actual values, used in this just for their types
    * @return the appropriate type, or null if no match is found
    * @deprecated This function does not work for a few reasons (e.g. how to resolve scalar integer value vs. bool vs.
    *             sampler), and should not be used as is.
    */
   @Deprecated
   public static ShaderParameterType from(final int width, final int height, final Number... value) {
      final Class<? extends Number> type = value[0].getClass();

      // Floats have both vector and matrix versions
      if (type == Float.class) {
         try {
            return FLOAT_TABLE[height - 1][width - 1];
         } catch (final IndexOutOfBoundsException iobe) {
            return null;
         }
      }

      // the others only have vector versions
      if (type == Integer.class) {
         try {
            return INT_TABLE[height - 1][width - 1];
         } catch (final IndexOutOfBoundsException iobe) {
            return null;
         }
      }

      // TODO : Handle types that are not supported

      return null;
   }

   /**
    * Returns whether the length of the data passed is the correct amount
    * for the type specified
    * 
    * @param type
    *           the parameter type which defines the size
    * @param value
    *           the number of parameters passed to check
    * @return
    * @deprecated This is trivially handled via information in the type, but does not handle arrays of scalar, float, or
    *             matrix types. Uniform already does validation and has the array component count, Attribute soon will.
    */
   @Deprecated
   public static boolean validateNumArguments(final ShaderParameterType type, final Number... value) {
      return (type.componentCount == value.length);
   }

}
