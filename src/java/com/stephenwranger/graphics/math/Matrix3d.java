package com.stephenwranger.graphics.math;

/**
 * A Column Major Matrix implementation. Reference: http://www.scratchapixel.com/lessons/3d-basic-rendering/perspective-and-orthographic-projection-matrix/building-basic-perspective-projection-matrix
 * @author rangers
 *
 */
public class Matrix3d {
   private final double[] m = new double[9];
   
   /**
    * Creates an Identity Matrix
    * <pre>
    * 1 0 0
    * 0 1 0
    * 0 0 1
    * </pre>
    */
   public Matrix3d() {
      m[0] = 1.0;
      m[3] = 1.0;
      m[6] = 1.0;
   }
   
   /**
    * Creates a column major Matrix with the defined columns.
    * 
    * @param column0
    * @param column1
    * @param column2
    */
   public Matrix3d(final Tuple3d column0, final Tuple3d column1, final Tuple3d column2) {
      set(column0, column1, column2);
   }
   
   /**
    * Creates a column major Matrix from the given array.
    * 
    * @param array
    */
   public Matrix3d(final double[] array) {
      this.set(array);
   }
   
   public double[] get() {
      return this.get(null);
   }
   
   public double[] get(final double[] array) {
      final double[] outArray = (array == null || array.length != 9) ? new double[9] : array;
      
      System.arraycopy(this.m, 0, outArray, 0, 9);
      
      return outArray;
   }
   
   /**
    * Sets column major matrix.
    * 
    * @param column0
    * @param column1
    * @param column2
    */
   public void set(final Tuple3d column0, final Tuple3d column1, final Tuple3d column2) {
      set(column0.x,
         column0.y,
         column0.z,
   
         column1.x,
         column1.y,
         column1.z,
   
         column2.x,
         column2.y,
         column2.z);
   }
   
   public void set(final Matrix3d other) {
      this.set(other.m);
   }
   
   public void set(final double[] array) {
      this.set(array[0], array[1], array[2], 
               array[3], array[4], array[5], 
               array[6], array[7], array[8]);
   }
   
   /**
    * Sets column major matrix (m[col][row]).
    * 
    * @param m00
    * @param m01
    * @param m02
    * @param m10
    * @param m11
    * @param m12
    * @param m20
    * @param m21
    * @param m22
    */
   public void set(final double m00, final double m01, final double m02,
                   final double m10, final double m11, final double m12,
                   final double m20, final double m21, final double m22) {
      m[0] = m00;
      m[1] = m01;
      m[2] = m02;

      m[3] = m10;
      m[4] = m11;
      m[5] = m12;

      m[6] = m20;
      m[7] = m21;
      m[8] = m22;
   }
   
   public void rotateVector(final Vector3d vector) {
      final double x = (this.m[0] * vector.x) + (this.m[1] * vector.y) + (this.m[2] * vector.z);
      final double y = (this.m[3] * vector.x) + (this.m[4] * vector.y) + (this.m[5] * vector.z);
      final double z = (this.m[6] * vector.x) + (this.m[7] * vector.y) + (this.m[8] * vector.z);
      
      vector.set(x, y, z);
   }
   
   public Matrix3d multiply(final Matrix3d left, final Matrix3d right) {
      this.set(left);
      this.multiply(right);
      
      return this;
   }
   
   public Matrix3d multiply(final Matrix3d other) {
      final double n00 = this.m[0] * other.m[0] + this.m[1] * other.m[3] + this.m[2] * other.m[6];
      final double n01 = this.m[0] * other.m[1] + this.m[1] * other.m[4] + this.m[2] * other.m[7];
      final double n02 = this.m[0] * other.m[2] + this.m[1] * other.m[5] + this.m[2] * other.m[8];

      final double n10 = this.m[3] * other.m[0] + this.m[4] * other.m[3] + this.m[5] * other.m[6];
      final double n11 = this.m[3] * other.m[1] + this.m[4] * other.m[4] + this.m[5] * other.m[7];
      final double n12 = this.m[3] * other.m[2] + this.m[4] * other.m[5] + this.m[5] * other.m[8];

      final double n20 = this.m[6] * other.m[0] + this.m[7] * other.m[3] + this.m[8] * other.m[6];
      final double n21 = this.m[6] * other.m[1] + this.m[7] * other.m[4] + this.m[8] * other.m[7];
      final double n22 = this.m[6] * other.m[2] + this.m[7] * other.m[5] + this.m[8] * other.m[8];
      
      set(n00, n01, n02, n10, n11, n12, n20, n21, n22);
      return this;
   }
   
   public double[] multiply(final double[] in) {
      final double[] result = new double[4];
      result[0] = m[0] * in[0] + m[3] * in[1] + m[6];
      result[1] = m[1] * in[0] + m[4] * in[1] + m[7];
      result[2] = m[2] * in[0] + m[5] * in[1] + m[8];

      return result;
   }
   
   public static void invert(final double[] in) {
      final Matrix3d inMatrix = new Matrix3d(in);
      Matrix3d.invert(inMatrix);
      
      System.arraycopy(inMatrix.m, 0, in, 0, 9);
   }
   
   public static void invert(final Matrix3d in, final Matrix3d out) {
      out.set(in);
      Matrix3d.invert(out);
   }
   
   public static void invert(final Matrix3d in) {
      double invDet = in.determinant();

      if (invDet == 0) {
         throw new RuntimeException("Matrix is singular; it cannot be inverted.");
      }

      invDet = 1 / invDet; // This is the true inverse of the determinant.

      final double n00 = invDet * (in.m[4] * in.m[8] - in.m[7] * in.m[5]);
      final double n01 = invDet * (in.m[7] * in.m[2] - in.m[1] * in.m[8]);
      final double n02 = invDet * (in.m[1] * in.m[5] - in.m[4] * in.m[2]);
      final double n10 = invDet * (in.m[5] * in.m[6] - in.m[3] * in.m[8]);
      final double n11 = invDet * (in.m[0] * in.m[8] - in.m[6] * in.m[2]);
      final double n12 = invDet * (in.m[3] * in.m[2] - in.m[0] * in.m[5]);
      final double n20 = invDet * (in.m[3] * in.m[7] - in.m[6] * in.m[4]);
      final double n21 = invDet * (in.m[6] * in.m[1] - in.m[0] * in.m[7]);
      final double n22 = invDet * (in.m[0] * in.m[4] - in.m[1] * in.m[3]);

      in.set(n00, n01, n02, n10, n11, n12, n20, n21, n22);
   }
   
   public double determinant() {
      // Matrix2d d = new Matrix2d();

      final double det1 = this.m[4] * this.m[8] - this.m[5] * this.m[7];
      final double det2 = this.m[3] * this.m[8] - this.m[5] * this.m[6];
      final double det3 = this.m[3] * this.m[7] - this.m[4] * this.m[6];

      return ((this.m[0] * det1) - (this.m[1] * det2) + (this.m[2] * det3));

   }
}
