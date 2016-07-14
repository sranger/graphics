package com.stephenwranger.graphics.utils.shader;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.GLBuffers;
import com.stephenwranger.graphics.utils.FileUtils;

public class ShaderKernel {
   private final String name;
   private final String source;
   private final ShaderStage stage;
   
   private String shaderInfoLog;
   private int id;
   
   public ShaderKernel(final String name, final String source, final ShaderStage stage) {
      this.name = name;
      this.source = source;
      this.stage = stage;
   }
   
   public ShaderKernel(final String name, final InputStream sourceStream, final ShaderStage stage) {
      this.name = name;
      this.source = FileUtils.getStreamContentsString(sourceStream);
      this.stage = stage;
   }

   /**
    * Compiles this shader program.
    *
    * @param gl
    *           the current gl context.
    * @throws IllegalStateException
    *            if shader compilation fails (only checked if verify is true).
    */
   public synchronized void compile(final GL2 gl) throws IllegalStateException {
      if (this.id <= 0) {
         this.id = gl.glCreateShader(this.stage.type);
         if (this.id <= 0) {
            throw new IllegalStateException("No ID assigned for shader kernel " + this.name);
         }

         final String source = this.source;
         gl.glShaderSource(this.id, 1, new String[] { source }, (int[]) null, 0);
         gl.glCompileShader(this.id);

         shaderInfoLog = getShaderInfoLog(gl, this.id);

         final int[] compileStatus = new int[1];
         gl.glGetShaderiv(this.id, GL2.GL_COMPILE_STATUS, compileStatus, 0);

         if (compileStatus[0] == GL.GL_FALSE) {
            System.err.println("Failed to compile ShaderKernel: " + this.name + ", ID: " + this.id + ((shaderInfoLog == null) ? "" : ", info-log: \n" + shaderInfoLog));
            deleteShader(gl);
            throw new IllegalStateException("Shader compilation failed for shader " + this.name + ", InfoLog: \n" + shaderInfoLog);
         }
      }
   }

   /**
    * Attaches a kernel to the specified shader program id. This implementation automatically compiles the kernel if it
    * has not already been compiled, does the attachment, then deletes the kernel (which remains attached to the
    * program).
    *
    * @param gl
    *           GL instance whose GLContext is current on the calling thread.
    * @param programId
    *           The ID of the shader program to attach to (must be > 0).
    */
   protected void attachKernel(final GL2 gl, final int programId) {
      compile(gl);
      gl.glAttachShader(programId, this.id);
      deleteShader(gl);
   }

   private static String getShaderInfoLog(final GL2 gl, final int id) {
      final IntBuffer iVal = GLBuffers.newDirectIntBuffer(1);
      gl.glGetShaderiv(id, GL2.GL_INFO_LOG_LENGTH, iVal);

      final int length = iVal.get();

      if (length > 1) {
         final ByteBuffer infoLog = GLBuffers.newDirectByteBuffer(length);

         iVal.flip();
         gl.glGetShaderInfoLog(id, length, iVal, infoLog);

         final byte[] infoBytes = new byte[length];
         infoLog.get(infoBytes);
         return new String(infoBytes);
      }
      return null;
   }

   /**
    * Accessor for the Shader InfoLog from the most recent attempt to compile this shader.
    *
    * @return a String with the contents of the info log, or null if there were no messages in the log, or the kernel
    *         has never been compiled.
    */
   public String getInfoLog() {
      return shaderInfoLog;
   }

   /**
    * Deletes this program from the OpenGL context.
    *
    * @param gl
    */
   public synchronized void deleteShader(final GL2 gl) {
      if (this.id > 0) {
         gl.glDeleteShader(this.id);
      }
      this.id = -1;
   }
}
