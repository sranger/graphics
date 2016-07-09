package com.stephenwranger.graphics.color;

import java.nio.FloatBuffer;

public class Color4f {
   public float r, g, b, a;

   public Color4f() {
      this(1, 1, 1, 1);
   }

   public Color4f(final Color4f color) {
      this(color.r, color.g, color.b, color.a);
   }

   public Color4f(final float r, final float g, final float b, final float a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
   }

   public Color4f(final float[] rgba) {
      this(rgba[0], rgba[1], rgba[2], rgba[3]);
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (this.getClass() != obj.getClass()) {
         return false;
      }
      Color4f other = (Color4f) obj;
      if (Float.floatToIntBits(this.a) != Float.floatToIntBits(other.a)) {
         return false;
      }
      if (Float.floatToIntBits(this.b) != Float.floatToIntBits(other.b)) {
         return false;
      }
      if (Float.floatToIntBits(this.g) != Float.floatToIntBits(other.g)) {
         return false;
      }
      if (Float.floatToIntBits(this.r) != Float.floatToIntBits(other.r)) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + Float.floatToIntBits(this.a);
      result = (prime * result) + Float.floatToIntBits(this.b);
      result = (prime * result) + Float.floatToIntBits(this.g);
      result = (prime * result) + Float.floatToIntBits(this.r);
      return result;
   }

   public void putInto(final FloatBuffer buffer) {
      buffer.put(this.r).put(this.g).put(this.b).put(this.a);
   }

   public void setColor(final Color4f color) {
      this.r = color.r;
      this.g = color.g;
      this.b = color.b;
      this.a = color.a;
   }

   @Override
   public String toString() {
      return "Color4f[" + this.r + ", " + this.g + ", " + this.b + ", " + this.a + "]";
   }

   public static Color4f black() {
      return new Color4f(0, 0, 0, 1);
   }

   public static Color4f blue() {
      return new Color4f(0, 0, 1, 1);
   }

   public static Color4f cyan() {
      return new Color4f(0, 1, 1, 1);
   }

   public static Color4f green() {
      return new Color4f(0, 1, 0, 1);
   }

   public static Color4f magenta() {
      return new Color4f(1, 0, 1, 1);
   }

   public static Color4f maroon() {
      return new Color4f(0.5f, 0f, 0f, 1f);
   }

   public static Color4f orange() {
      return new Color4f(1f, 0.5f, 0f, 1f);
   }

   public static Color4f purple() {
      return new Color4f(0.5f, 0f, 0.5f, 1f);
   }

   public static Color4f red() {
      return new Color4f(1, 0, 0, 1);
   }

   public static Color4f white() {
      return new Color4f(1, 1, 1, 1);
   }

   public static Color4f yellow() {
      return new Color4f(1, 1, 0, 1);
   }
}
