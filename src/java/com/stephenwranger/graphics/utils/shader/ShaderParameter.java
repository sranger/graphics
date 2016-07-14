package com.stephenwranger.graphics.utils.shader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class for common data for different types of shader parameters (uniforms, attributes).
 */
public class ShaderParameter {

   private static final Pattern ARRAY_INDEX_PATTERN = Pattern.compile(".+\\[(\\d+)\\]$");
   /**
    * The shader program that defines this parameter.
    */
   public final ShaderProgram program;
   /**
    * The location (index) of the parameter within the shader program.
    */
   public final int location;
   /**
    * The name of the parameter, note that members of structures will be represented by individual uniforms with a '.'
    * separated name.
    */
   public final String name;
   /**
    * The OpenGL data type for the parameter.
    *
    * @see <a href="http://www.opengl.org/sdk/docs/man4/xhtml/glGetActiveUniform.xml">glGetActiveUniform</a>
    */
   public final ShaderParameterType type;
   /**
    * The largest used value of the array index (plus one) for this parameter. If this uniform is not defined as an
    * array, this will contain 1. Otherwise, the greatest index (plus one) actually determined to be used (at compile/link
    * time) is represented here.
    */
   public final int arrayLength;

   /**
    * The size in bytes of the parameter, including all array elements.
    */
   public final int sizeBytes;

   /**
    *
    * @param program
    *           The program defining the parameter.
    * @param name
    *           The name of the parameter.
    * @param location
    *           The index/location of the parameter within the program.
    * @param type
    *           The parameter data type.
    * @param arraySize
    *           The number of array elements (the largest index actually used in the program).
    */
   protected ShaderParameter(final ShaderProgram program, final String name, final int location, final ShaderParameterType type, final int arraySize) {
      this.program = program;
      this.name = name == null ? "unknown" : name;
      this.location = location;
      this.type = type;
      this.arrayLength = arraySize;
      this.sizeBytes = arrayLength * type.sizeBytes;
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + " [name=" + name + ", location=" + location + ", type=" + type
            + ", arrayLength=" + arrayLength + ", program=" + program.getId() + "]";
   }

   /**
    * Determines the array index qualifier from a specified variable name. This captures the integer value contained in
    * the final array qualifier in a string, for example 'varName[3]' would yield 3, varName[1].var[2] would yield 2. If
    * there is no array qualifier, then -1 is returned.
    * 
    * @param name
    *           A string to extract the array index from.
    * @return The integer value of the final array index qualifier, or -1 if the string does not end with an array
    *         qualifier.
    */
   static int getArrayIndexFromName(final String name) {
      final Matcher matcher = ARRAY_INDEX_PATTERN.matcher(name);
      if (matcher.matches()) {
         return Integer.parseInt(matcher.group(1));
      }
      return -1;
   }
}
