package com.stephenwranger.graphics.utils.shader;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.GLBuffers;

public class ShaderProgram {
   private final ShaderKernel[] kernels;
   private final Map<String, ShaderUniform> uniforms = new HashMap<>();
   private final Map<String, ShaderAttribute> attributes = new HashMap<>();
   private final Map<String, Integer> attributeLocations = new HashMap<>();
   private final String name;
   
   private String linkLog = null;
   private String programInfoLog = null;
   private int id = -1;
   
   public ShaderProgram(final String name, final Map<String, Integer> requestedAttributeLocations, final ShaderKernel... kernels) {
      this.name = name;
      this.attributeLocations.putAll(requestedAttributeLocations);
      this.kernels = new ShaderKernel[kernels.length];
      
      System.arraycopy(kernels, 0, this.kernels, 0, kernels.length);
   }
   
   public int getId() {
      return this.id;
   }
   
   public String getName() {
      return this.name;
   }
   
   public void enable(final GL2 gl) {
      if(this.id <= 0) {
         this.buildProgram(gl);
      }
      
      gl.glUseProgram(this.id);
   }
   
   public void disable(final GL2 gl) {
      gl.glUseProgram(0);
   }

   /**
    * Reads the complete list of uniforms for the program, and returns a map of uniform names to Uniform objects. The
    * names are reported by the OpenGL driver (see glGetActiveUniformName), except that names of array valued uniforms
    * are stripped of the trailing '[0]' appended by at least some drivers to derive the base name.
    * <p>
    * Prerequisite: the program must be allocated and linked (this.id > 0).
    * 
    * @param gl
    *           GL instance whose context is current.
    * @return A map of uniform names to Uniforms, may be empty but never null.
    */
   private void readUniformMetadata(final GL2 gl) {
      assert id > 0 : "Program must be allocated and linked prior to calling readUniformMetadata.";

      this.uniforms.clear();
      
      final int[] iBuff = new int[1];

      /* Get number of uniforms */
      gl.glGetProgramiv(this.id, GL2.GL_ACTIVE_UNIFORMS, iBuff, 0);
      final int activeUniforms = iBuff[0];

      /* Allocate buffer to store uniform names from OpenGL */
      gl.glGetProgramiv(this.id, GL2.GL_ACTIVE_UNIFORM_MAX_LENGTH, iBuff, 0);

      // If the driver returns that there are names, but the maximum size is 0,
      // then we need an OK default to use.  This actually happened on Version XXX
      // of AMD driver for an existing Alienware laptop
      // Driver Packaging Version : 13.251.3.5-140317a-169669C-Dell
      // OpenGL Version : 6.14.10.12618
      final int maxNameSize = iBuff[0] > 0 ? iBuff[0] : 1024; //maxNameSize = iBuff[0];

      if (iBuff[0] == 0) {
        System.err.println("ShaderProgram:readUniformMetadata, OpenGL driver returned that uniforms exist, but have no length.  Overriding the 0 length information with 1024.");
      }
     
      final byte[] nameBuffer = new byte[maxNameSize];

      /* Loop and get metadata for each */
      final int[] sizeBuffer = new int[1];
      final int[] typeBuffer = new int[1];
      final int[] nameLenBuffer = new int[1];
      for (int i = 0; i < activeUniforms; ++i) {
         gl.glGetActiveUniform(this.id, i, maxNameSize, nameLenBuffer, 0, sizeBuffer, 0, typeBuffer, 0, nameBuffer, 0);
         /* Create Uniform object */
         /* Populate internal map */
         String name = new String(Arrays.copyOf(nameBuffer, nameLenBuffer[0]));
         final int location = gl.glGetUniformLocation(this.id, name);
         int arrayIdx = ShaderParameter.getArrayIndexFromName(name);
         if (arrayIdx >= 0) {
            /* Remove array qualifier, it is not necessary, and can be confusing */
            if (arrayIdx == 0)
               name = name.substring(0, name.length() - 3);
            else if (arrayIdx > 0)
               /*
                * We assume that uniforms are always reported by the base index only ('name[0]'), or base name ('name')
                * only.
                */
               assert false : "Incorrect assumption regarding uniform naming and layouts.";
         }

         this.uniforms.put(name, ShaderUniform.createUniform(this, name, location, ShaderParameterType.from(typeBuffer[0]), sizeBuffer[0]));
      }
   }

   /**
    * Reads the complete list of attributes for the program, and returns a map of attribute names to Attribute objects.
    * The
    * names are reported by the OpenGL driver (see glGetActiveAttrib), except that names of array valued attributes
    * are stripped of the trailing '[0]' appended by at least some drivers to derive the base name.
    * <p>
    * Prerequisite: the program must be allocated and linked (this.id > 0).
    * 
    * @param gl
    *           GL instance whose context is current.
    * @return A map of attribute names to Attributes, may be empty but never null.
    */
   private void readAttributeMetadata(final GL2 gl) {
      assert this.id > 0 : "Program must be allocated and linked prior to calling readAttributeMetadata.";

      this.attributes.clear();
      final int[] iBuff = new int[1];

      /* Get number of uniforms */
      gl.glGetProgramiv(this.id, GL2.GL_ACTIVE_ATTRIBUTES, iBuff, 0);
      final int activeAttributes = iBuff[0];

      /* Allocate buffer to store uniform names from OpenGL */
      // TODO: returns 0 on GeForce 590 and Quadro 6000 under linux and windows
      gl.glGetProgramiv(this.id, GL2.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH, iBuff, 0);
      
      // TODO: to fix nvidia driver issue
      final int maxNameSize = (iBuff[0] == 0 && activeAttributes > 0) ? 2000 : iBuff[0];
      final byte[] nameBuffer = new byte[maxNameSize];

      /* Loop and get metadata for each */
      final int[] sizeBuffer = new int[1];
      final int[] typeBuffer = new int[1];
      final int[] nameLenBuffer = new int[1];
      for (int i = 0; i < activeAttributes; ++i) {
         try {
            gl.glGetActiveAttrib(this.id, i, maxNameSize, nameLenBuffer, 0, sizeBuffer, 0, typeBuffer, 0, nameBuffer, 0);
            /* Create Uniform object */
            /* Populate internal map */
            String name = new String(Arrays.copyOf(nameBuffer, nameLenBuffer[0]));
            int location = gl.glGetAttribLocation(this.id, name);
            final int arrayIdx = ShaderParameter.getArrayIndexFromName(name);
            if (arrayIdx >= 0) {
               sizeBuffer[0] += arrayIdx;
   
               /* Remove array qualifier from name */
               name = name.substring(0, name.lastIndexOf('['));
               /* Locations for each array element */
               final int[] elementLocations;
               /* If attribute was previously defined, copy any array element locations into the new array */
               final ShaderAttribute previousAttrib = this.attributes.get(name);
               if (previousAttrib != null) {
                  final int[] oldLocations = previousAttrib.getArrayElementLocations();
                  elementLocations = new int[Math.max(sizeBuffer[0], oldLocations.length)];
                  Arrays.fill(elementLocations, -1);
                  System.arraycopy(oldLocations, 0, elementLocations, 0, oldLocations.length);
               } else {
                  elementLocations = new int[sizeBuffer[0]];
                  Arrays.fill(elementLocations, -1);
               }
   
               elementLocations[arrayIdx] = location;
               this.attributes.put(name, new ShaderAttribute(this, name, ShaderParameterType.from(typeBuffer[0]), elementLocations));
            } else {
               this.attributes.put(name, new ShaderAttribute(this, name, location, ShaderParameterType.from(typeBuffer[0])));
            }
         } catch(final GLException e) {
            e.printStackTrace();
         }
      }
   }

   private void deleteProgram(final GL2 gl) {
      this.uniforms.clear();
      this.attributes.clear();

      if (this.id > 0) {
         gl.glDeleteProgram(this.id);
      }
      
      this.id = -1;
   }

   /**
    * Compiles and links the shader program. If the program has already been compiled and linked successfully, this
    * deletes the existing program and re-compiles/links. This is automatically called the first time the program is
    * activated, but we make this function available for cases where compilation and validation should be done
    * separately (e.g. in an initialization routine) to reduce the possibility of unexpected errors during rendering.
    * <p>
    * This method does not change the active program.
    * <p>
    * If compilation or linking fails, the program is deleted before an IllegalStateException is thrown. Upon
    * non-exceptional completion, this program is allocated and linked. Additionally, the list of uniforms is populated.
    *
    * @param gl
    *           The current OpenGL context.
    * @throws IllegalStateException
    *            if the shader fails to compile or link.
    */
   public void buildProgram(final GL2 gl) {
      /* Don't compile/link/validate externally defined program (with no kernels, but valid this.id) */
      if (kernels.length > 0 || this.id <= 0) {
         if (this.id > 0) {
            /* Could detach each shader, but deleting the program is safer and only requires a single GL call. */
            deleteProgram(gl);
         }
         
         this.id = gl.glCreateProgram();
         
         if (this.id <= 0) {
            throw new IllegalStateException("Shader program ID not assigned");
         }

         for (final ShaderKernel kernel : kernels) {
            /* Attach each kernel */
            kernel.attachKernel(gl, this.id);
         }
         /* Attribute location bindings must be set up prior to linking */
         if (this.attributeLocations.isEmpty()) {
            for (final Map.Entry<String, Integer> attribLoc : this.attributeLocations.entrySet()) {
               gl.glBindAttribLocation(this.id, attribLoc.getValue(), attribLoc.getKey());
            }
         }
         /* Link and check status */
         gl.glLinkProgram(this.id);
         final int[] linkStatus = new int[1];
         gl.glGetProgramiv(this.id, GL2.GL_LINK_STATUS, linkStatus, 0);

         /* Check the info-log, this may contain warnings even if linking was successful */
         this.linkLog = getProgramInfoLog(gl, this.id);
         if (linkStatus[0] == GL2.GL_FALSE) {
            deleteProgram(gl);
            System.err.println("Failed to link ShaderProgram: " + this.name + ", ID: " + this.id + ((linkLog == null) ? "" : ", info-log: \n" + linkLog));
            throw new IllegalStateException("Shader Program compilation failed, InfoLog: \n" + linkLog);
         }
      }

      /* Introspection of uniform and attribute metadata */
      readUniformMetadata(gl);
      readAttributeMetadata(gl);
   }

   /**
    * Validates the program to determine whether it will be able to execute given the current state. This updates the
    * program info log with any output produced by the GLSL validator.
    *
    * @param gl
    *           The gl to invoke on.
    * @throws IllegalStateException
    *            If validation fails, or the program has not been compiled or linked.
    * @return The validation message.
    */
   public String validateProgram(final GL2 gl) throws IllegalStateException {
      if (this.id <= 0) {
         throw new IllegalStateException("Shader program ID not assigned");
      }

      gl.glValidateProgram(this.id);
      final int[] validateStatus = new int[1];
      gl.glGetProgramiv(this.id, GL2.GL_VALIDATE_STATUS, validateStatus, 0);

      this.programInfoLog = getProgramInfoLog(gl, this.id);

      if (validateStatus[0] == GL2.GL_FALSE) {
         throw new IllegalStateException("Shader Program validation failed, InfoLog: \n" + programInfoLog);
      }
      if (programInfoLog == null || programInfoLog.isEmpty()) {
         return null;
      }
      return programInfoLog;
   }

   /**
    * Accessor for the content of the info log. This may be called immediately following a call to
    * {@link #activate(GL2)} to get the contents of the program info
    * log which may contain more information on program warnings and errors. Note that the returned value will only be
    * valid if called immediately following
    * the {@link #activate(GL2)} call, as the InfoLog may overwritten.
    *
    * @param gl
    *           A GL instance whose context is current.
    * @param this.id
    *           The id of a program (must be > 0).
    *
    * @return A string containing the contents of the Program InfoLog from immediately following the most recent attempt
    *         to build the program, or <code>null</code> if the infolog was empty, or the program has not been
    *         (attempted to be) built.
    */
   private static String getProgramInfoLog(final GL2 gl, final int id) {
      final IntBuffer iVal = GLBuffers.newDirectIntBuffer(1);
      gl.glGetProgramiv(id, GL2.GL_INFO_LOG_LENGTH, iVal);

      final int length = iVal.get();

      if (length > 1) {
         final ByteBuffer infoLog = GLBuffers.newDirectByteBuffer(length);
         iVal.flip();
         gl.glGetProgramInfoLog(id, length, iVal, infoLog);

         final byte[] infoBytes = new byte[length];
         infoLog.get(infoBytes);
         return new String(infoBytes);
      }
      return null;
   }
}
