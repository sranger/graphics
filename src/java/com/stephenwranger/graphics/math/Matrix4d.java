package com.stephenwranger.graphics.math;

import com.stephenwranger.graphics.math.intersection.IntersectionUtils;

/**
 * A Column Major Matrix implementation. Reference: http://www.scratchapixel.com/lessons/3d-basic-rendering/perspective-and-orthographic-projection-matrix/building-basic-perspective-projection-matrix
 * @author rangers
 *
 */
public class Matrix4d {
   private final double[] m = new double[16];
   
   /**
    * Creates an Identity Matrix
    * <pre>
    * 1 0 0 0
    * 0 1 0 0
    * 0 0 1 0
    * 0 0 0 1
    * </pre>
    */
   public Matrix4d() {
      m[0] = 1.0;
      m[5] = 1.0;
      m[10] = 1.0;
      m[15] = 1.0;
   }
   
   /**
    * Creates a column major Matrix with the defined columns.
    * 
    * @param column0
    * @param column1
    * @param column2
    * @param column3
    */
   public Matrix4d(final Tuple4d column0, final Tuple4d column1, final Tuple4d column2, final Tuple4d column3) {
      set(column0, column1, column2, column3);
   }
   
   /**
    * Creates a column major Matrix from the given array.
    * 
    * @param array
    */
   public Matrix4d(final double[] array) {
      this.set(array);
   }
   
   public double get(final int row, final int column) {
      return this.m[column * 4 + row];
   }
   
   public double[] get() {
      return this.get(null);
   }
   
   public double[] get(final double[] array) {
      final double[] outArray = (array == null || array.length != 16) ? new double[16] : array;
      
      System.arraycopy(this.m, 0, outArray, 0, 16);
      
      return outArray;
   }
   
   /**
    * Sets column major matrix.
    * 
    * @param column0
    * @param column1
    * @param column2
    * @param column3
    */
   public void set(final Tuple4d column0, final Tuple4d column1, final Tuple4d column2, final Tuple4d column3) {
      set(column0.x,
         column0.y,
         column0.z,
         column0.w,
   
         column1.x,
         column1.y,
         column1.z,
         column1.w,
   
         column2.x,
         column2.y,
         column2.z,
         column2.w,
   
         column3.x,
         column3.y,
         column3.z,
         column3.w);
   }
   
   public void set(final Matrix4d other) {
      this.set(other.m);
   }
   
   public void set(final double[] array) {
      this.set(array[0], array[1], array[2], array[3],
               array[4], array[5], array[6], array[7],
               array[8], array[9], array[10], array[11],
               array[12], array[13], array[14], array[15]);
   }
   
   /**
    * Sets column major matrix (m[col][row]).
    * 
    * @param m00
    * @param m01
    * @param m02
    * @param m03
    * @param m10
    * @param m11
    * @param m12
    * @param m13
    * @param m20
    * @param m21
    * @param m22
    * @param m23
    * @param m30
    * @param m31
    * @param m32
    * @param m33
    */
   public void set(final double m00, final double m01, final double m02, final double m03,
         final double m10, final double m11, final double m12, final double m13,
         final double m20, final double m21, final double m22, final double m23,
         final double m30, final double m31, final double m32, final double m33) {
      m[0] = m00;
      m[1] = m01;
      m[2] = m02;
      m[3] = m03;

      m[4] = m10;
      m[5] = m11;
      m[6] = m12;
      m[7] = m13;

      m[8] = m20;
      m[9] = m21;
      m[10] = m22;
      m[11] = m23;

      m[12] = m30;
      m[13] = m31;
      m[14] = m32;
      m[15] = m33;
   }
   
   public Matrix4d multiply(final Matrix4d other) {
      final double n00 = this.m[0] * other.m[0] + this.m[1] * other.m[4] + this.m[2] * other.m[8] + this.m[3] * other.m[12];
      final double n01 = this.m[0] * other.m[1] + this.m[1] * other.m[5] + this.m[2] * other.m[9] + this.m[3] * other.m[13];
      final double n02 = this.m[0] * other.m[2] + this.m[1] * other.m[6] + this.m[2] * other.m[10] + this.m[3] * other.m[14];
      final double n03 = this.m[0] * other.m[3] + this.m[1] * other.m[7] + this.m[2] * other.m[11] + this.m[3] * other.m[15];

      final double n10 = this.m[4] * other.m[0] + this.m[5] * other.m[4] + this.m[6] * other.m[8] + this.m[7] * other.m[12];
      final double n11 = this.m[4] * other.m[1] + this.m[5] * other.m[5] + this.m[6] * other.m[9] + this.m[7] * other.m[13];
      final double n12 = this.m[4] * other.m[2] + this.m[5] * other.m[6] + this.m[6] * other.m[10] + this.m[7] * other.m[14];
      final double n13 = this.m[4] * other.m[3] + this.m[5] * other.m[7] + this.m[6] * other.m[11] + this.m[7] * other.m[15];

      final double n20 = this.m[8] * other.m[0] + this.m[9] * other.m[4] + this.m[10] * other.m[8] + this.m[11] * other.m[12];
      final double n21 = this.m[8] * other.m[1] + this.m[9] * other.m[5] + this.m[10] * other.m[9] + this.m[11] * other.m[13];
      final double n22 = this.m[8] * other.m[2] + this.m[9] * other.m[6] + this.m[10] * other.m[10] + this.m[11] * other.m[14];
      final double n23 = this.m[8] * other.m[3] + this.m[9] * other.m[7] + this.m[10] * other.m[11] + this.m[11] * other.m[15];

      final double n30 = this.m[12] * other.m[0] + this.m[13] * other.m[4] + this.m[14] * other.m[8] + this.m[15] * other.m[12];
      final double n31 = this.m[12] * other.m[1] + this.m[13] * other.m[5] + this.m[14] * other.m[9] + this.m[15] * other.m[13];
      final double n32 = this.m[12] * other.m[2] + this.m[13] * other.m[6] + this.m[14] * other.m[10] + this.m[15] * other.m[14];
      final double n33 = this.m[12] * other.m[3] + this.m[13] * other.m[7] + this.m[14] * other.m[11] + this.m[15] * other.m[15];

      set(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
      return this;
   }
   
   public Matrix4d multiply(final Matrix4d left, final Matrix4d right) {
      this.set(left);
      this.multiply(right);
      
      return this;
   }
   
   public double[] multiply(final double[] in) {
      final double[] result = new double[4];
      result[0] = m[0] * in[0] + m[4] * in[1] + m[8] * in[2] + m[12] * in[3];
      result[1] = m[1] * in[0] + m[5] * in[1] + m[9] * in[2] + m[13] * in[3];
      result[2] = m[2] * in[0] + m[6] * in[1] + m[10] * in[2] + m[14] * in[3];
      result[3] = m[3] * in[0] + m[7] * in[1] + m[11] * in[2] + m[15] * in[3];

      return result;
   }

   public Matrix4d translate(final Tuple3d translation) {
      this.m[12] = translation.x;
      this.m[13] = translation.y;
      this.m[14] = translation.z;
      
      return this;
   }
   
   public static void invert(final double[] in) {
      final Matrix4d inMatrix = new Matrix4d(in);
      Matrix4d.invert(inMatrix);
      
      System.arraycopy(inMatrix.m, 0, in, 0, 16);
   }
   
   public static void invert(final Matrix4d in, final Matrix4d out) {
      out.set(in);
      Matrix4d.invert(out);
   }
   
   public static void invert(final Matrix4d in) {
      double[] inv = new double[16];
      double det;
      int i;

      inv[0] =   in.m[5]*in.m[10]*in.m[15]   - in.m[5]*in.m[11]*in.m[14]   - in.m[9]*in.m[6]*in.m[15]
               + in.m[9]*in.m[7]*in.m[14]    + in.m[13]*in.m[6]*in.m[11]   - in.m[13]*in.m[7]*in.m[10];
      inv[4] =  -in.m[4]*in.m[10]*in.m[15]   + in.m[4]*in.m[11]*in.m[14]   + in.m[8]*in.m[6]*in.m[15]
               - in.m[8]*in.m[7]*in.m[14]    - in.m[12]*in.m[6]*in.m[11]   + in.m[12]*in.m[7]*in.m[10];
      inv[8] =   in.m[4]*in.m[9]*in.m[15]    - in.m[4]*in.m[11]*in.m[13]   - in.m[8]*in.m[5]*in.m[15]
               + in.m[8]*in.m[7]*in.m[13]    + in.m[12]*in.m[5]*in.m[11]   - in.m[12]*in.m[7]*in.m[9];
      inv[12] = -in.m[4]*in.m[9]*in.m[14]    + in.m[4]*in.m[10]*in.m[13]   + in.m[8]*in.m[5]*in.m[14]
               - in.m[8]*in.m[6]*in.m[13]    - in.m[12]*in.m[5]*in.m[10]   + in.m[12]*in.m[6]*in.m[9];
      inv[1] =  -in.m[1]*in.m[10]*in.m[15]   + in.m[1]*in.m[11]*in.m[14]   + in.m[9]*in.m[2]*in.m[15]
               - in.m[9]*in.m[3]*in.m[14]    - in.m[13]*in.m[2]*in.m[11]   + in.m[13]*in.m[3]*in.m[10];
      inv[5] =   in.m[0]*in.m[10]*in.m[15]   - in.m[0]*in.m[11]*in.m[14]   - in.m[8]*in.m[2]*in.m[15]
               + in.m[8]*in.m[3]*in.m[14]    + in.m[12]*in.m[2]*in.m[11]   - in.m[12]*in.m[3]*in.m[10];
      inv[9] =  -in.m[0]*in.m[9]*in.m[15]    + in.m[0]*in.m[11]*in.m[13]   + in.m[8]*in.m[1]*in.m[15]
               - in.m[8]*in.m[3]*in.m[13]    - in.m[12]*in.m[1]*in.m[11]   + in.m[12]*in.m[3]*in.m[9];
      inv[13] =  in.m[0]*in.m[9]*in.m[14]    - in.m[0]*in.m[10]*in.m[13]   - in.m[8]*in.m[1]*in.m[14]
               + in.m[8]*in.m[2]*in.m[13]    + in.m[12]*in.m[1]*in.m[10]   - in.m[12]*in.m[2]*in.m[9];
      inv[2] =   in.m[1]*in.m[6]*in.m[15]    - in.m[1]*in.m[7]*in.m[14]    - in.m[5]*in.m[2]*in.m[15]
               + in.m[5]*in.m[3]*in.m[14]    + in.m[13]*in.m[2]*in.m[7]    - in.m[13]*in.m[3]*in.m[6];
      inv[6] =  -in.m[0]*in.m[6]*in.m[15]    + in.m[0]*in.m[7]*in.m[14]    + in.m[4]*in.m[2]*in.m[15]
               - in.m[4]*in.m[3]*in.m[14]    - in.m[12]*in.m[2]*in.m[7]    + in.m[12]*in.m[3]*in.m[6];
      inv[10] =  in.m[0]*in.m[5]*in.m[15]    - in.m[0]*in.m[7]*in.m[13]    - in.m[4]*in.m[1]*in.m[15]
               + in.m[4]*in.m[3]*in.m[13]    + in.m[12]*in.m[1]*in.m[7]    - in.m[12]*in.m[3]*in.m[5];
      inv[14] = -in.m[0]*in.m[5]*in.m[14]    + in.m[0]*in.m[6]*in.m[13]    + in.m[4]*in.m[1]*in.m[14]
               - in.m[4]*in.m[2]*in.m[13]    - in.m[12]*in.m[1]*in.m[6]    + in.m[12]*in.m[2]*in.m[5];
      inv[3] =  -in.m[1]*in.m[6]*in.m[11]    + in.m[1]*in.m[7]*in.m[10]    + in.m[5]*in.m[2]*in.m[11]
               - in.m[5]*in.m[3]*in.m[10]    - in.m[9]*in.m[2]*in.m[7]     + in.m[9]*in.m[3]*in.m[6];
      inv[7] =   in.m[0]*in.m[6]*in.m[11]    - in.m[0]*in.m[7]*in.m[10]    - in.m[4]*in.m[2]*in.m[11]
               + in.m[4]*in.m[3]*in.m[10]    + in.m[8]*in.m[2]*in.m[7]     - in.m[8]*in.m[3]*in.m[6];
      inv[11] = -in.m[0]*in.m[5]*in.m[11]    + in.m[0]*in.m[7]*in.m[9]     + in.m[4]*in.m[1]*in.m[11]
               - in.m[4]*in.m[3]*in.m[9]     - in.m[8]*in.m[1]*in.m[7]     + in.m[8]*in.m[3]*in.m[5];
      inv[15] =  in.m[0]*in.m[5]*in.m[10]    - in.m[0]*in.m[6]*in.m[9]     - in.m[4]*in.m[1]*in.m[10]
               + in.m[4]*in.m[2]*in.m[9]     + in.m[8]*in.m[1]*in.m[6]     - in.m[8]*in.m[2]*in.m[5];
      
      det = in.m[0]*inv[0] + in.m[1]*inv[4] + in.m[2]*inv[8] + in.m[3]*inv[12];
      if (IntersectionUtils.isZero(det)) {
         throw new RuntimeException("Matrix is singular; it cannot be inverted.");
      }

      det = 1.0 / det;

      final double[] out = new double[16];
      for (i = 0; i < 16; i++) {
          out[i] = inv[i] * det;
      }
   }
}
