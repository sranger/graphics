package com.stephenwranger.graphics.utils.shader;

public class ShaderAttribute extends ShaderParameter {

   /**
    * Map of attribute array indices to binding locations.
    */
   private final int[] elementLocations;

   /**
    * Defines an attribute with a non-array type.
    * 
    * @param program
    *           The program that defines the attribute.
    * @param name
    *           The attribute name (the base name for array typed attributes, i.e. excluding the '[0]' array qualifier).
    * @param location
    *           The attribute location.
    * @param type
    *           The data type of the attribute.
    */
   ShaderAttribute(final ShaderProgram program, final String name, final int location, final ShaderParameterType type) {
      super(program, name, location, type, 1);
      this.elementLocations = new int[] { location };
   }

   /**
    * Defines an attribute with an array type.
    * 
    * @param program
    *           The program that defines the attribute.
    * @param name
    *           The attribute name (the base name for array typed attributes, i.e. excluding the '[0]' array qualifier).
    * @param type
    *           The data type of the attribute.
    * @param locations
    *           An array of locations (must have length &gt;= 1) for the corresponding index into the attribute array.
    */
   ShaderAttribute(final ShaderProgram program, final String name, final ShaderParameterType type, final int[] locations) {
      super(program, name, getFirstElementLocation(locations), type, locations.length);
      this.elementLocations = locations;
   }
   
   public int[] getArrayElementLocations() {
      return this.elementLocations.clone();
   }
   
   private static int getFirstElementLocation(int[] locations) {
      for (int loc : locations) {
         if (loc >= 0) {
            return loc;
         }
      }
      return -1;
   }
}
