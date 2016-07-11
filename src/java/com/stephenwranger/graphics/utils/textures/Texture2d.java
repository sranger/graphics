package com.stephenwranger.graphics.utils.textures;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;
import com.jogamp.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

public class Texture2d {
   private int texId = -1;
   private final int width, height;
   private final ByteBuffer pixelData;
   private final int format;
   private final int internal;
   
   public Texture2d(final InputStream rgbStream, final InputStream alphaStream) throws IOException {
      final BufferedImage rgb = ImageIO.read(rgbStream);
      final BufferedImage alpha = ImageIO.read(alphaStream);
      
      if(rgb.getWidth() != alpha.getWidth() || rgb.getHeight() != alpha.getHeight()) {
         throw new IndexOutOfBoundsException("RGB and Alpha images must be identical dimensions.");
      }
      
      this.width = rgb.getWidth();
      this.height = rgb.getHeight();
      this.format = GL2.GL_RGBA;
      this.internal = GL2.GL_RGBA8;

      final int[] rgbData = new int[width * height];
      final int[] alphaData = new int[width * height];

      PixelGrabber rgbGrabber = new PixelGrabber(rgb, 0, 0, width, height, rgbData, 0, width);
      PixelGrabber alphaGrabber = new PixelGrabber(alpha, 0, 0, width, height, alphaData, 0, width);
      try {
         rgbGrabber.grabPixels();
         alphaGrabber.grabPixels();
      } catch (final InterruptedException e) {
         throw new RuntimeException();
      }

      pixelData = GLBuffers.newDirectByteBuffer(rgbData.length * 4);

      // TODO: replace in shader with per-component alpha
      for (int row = height - 1; row >= 0; row--) {
         for (int col = 0; col < width; col++) {
            int packedRgb = rgbData[row * width + col];
            int packedAlpha = rgbData[row * width + col];

            double r = ((packedRgb >> 16) & 0xFF);// * (((packedAlpha >> 16) & 0xFF) / 255.0);
            double g = ((packedRgb >> 8) & 0xFF);// * (((packedAlpha >> 8) & 0xFF) / 255.0);
            double b = ((packedRgb >> 0) & 0xFF);// * (((packedAlpha >> 0) & 0xFF) / 255.0);
//            double a = ((packedRgb >> 24) & 0xFF) * (((packedAlpha >> 24) & 0xFF) / 255.0);
            double aMean = ((packedAlpha >> 0) & 0xFF + (packedAlpha >> 8) & 0xFF + (packedAlpha >> 16) & 0xFF) / 3.0;
//            double low = 30;
            
//            if(r <= low && g <= low && b <= low) {
//               pixelData.put((byte)0).put((byte)0).put((byte)0).put((byte)0);
//            } else {
               pixelData.put((byte)r).put((byte)g).put((byte)b).put((byte)aMean);
//            }
         }
      }

      pixelData.flip();
   }

   public Texture2d(final InputStream inputStream, final int format) throws IOException {
      final BufferedImage image = ImageIO.read(inputStream);
      this.width = image.getWidth();
      this.height = image.getHeight();
      this.format = format;
      this.internal = (format == GL2.GL_LUMINANCE)? GL2.GL_LUMINANCE : (format == GL2.GL_RGB) ? GL2.GL_RGB8 : GL2.GL_RGBA8;

      final int[] rgbData = new int[width * height];

      PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, image.getWidth(), image.getHeight(), rgbData, 0, image.getWidth());
      try {
         pixelGrabber.grabPixels();
      } catch (final InterruptedException e) {
         throw new RuntimeException();
      }

      final int bytes = format == GL2.GL_ALPHA ? 1 : format == GL2.GL_RGB ? 3 : 4;
      pixelData = GLBuffers.newDirectByteBuffer(rgbData.length * bytes);

      for (int row = height - 1; row >= 0; row--) {
         for (int col = 0; col < width; col++) {
            int packedPixel = rgbData[row * width + col];
            if(bytes >= 3) {
               pixelData.put((byte) ((packedPixel >> 16) & 0xFF));
               pixelData.put((byte) ((packedPixel >> 8) & 0xFF));
               pixelData.put((byte) ((packedPixel >> 0) & 0xFF));
            }
            if(bytes == 1 || bytes == 4) {
               pixelData.put((byte) ((packedPixel >> 24) & 0xFF));
            }
         }
      }

      pixelData.flip();
   }
   
   public BufferedImage getImage() {
      final BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
      final int bytes = format == GL2.GL_ALPHA ? 1 : format == GL2.GL_RGB ? 3 : 4;
      int color;
      
      
      for (int row = height - 1; row >= 0; row--) {
         for (int col = 0; col < width; col++) {
            color = 0;
            
            if(bytes >= 3) {
               color += (pixelData.get() << 16);
               color += (pixelData.get() << 8);
               color += (pixelData.get() << 0);
            }
            if(bytes == 1 || bytes == 4) {
               color += (pixelData.get() << 24);
            }
            
            image.setRGB(col, row, color);
         }
      }
      
      pixelData.rewind();
      
      return image;
   }

   public void enable(final GL2 gl, final int offset) {
      gl.glActiveTexture(GL2.GL_TEXTURE0 + offset);
      gl.glEnable(GL2.GL_TEXTURE_2D);
   
      if (texId == -1) {
         final int[] ids = new int[1];
         gl.glGenTextures(1, ids, 0);
         texId = ids[0];

         gl.glBindTexture(GL2.GL_TEXTURE_2D, texId);
         gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
         gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
         gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, internal, width, height, 0, format, GL2.GL_UNSIGNED_BYTE, pixelData);
      }

      gl.glBindTexture(GL2.GL_TEXTURE_2D, texId);
   }
   
   public void enable(final GL2 gl) {
      enable(gl, 0);
   }
   
   public void enableAlpha(final GL2 gl) {
      enable(gl, 1);
   }
   
   public void disable(final GL2 gl) {
      if(texId != -1) {
         gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
      }
   }
   
   public void clear(final GL2 gl) {
      if(texId != -1) {
         gl.glDeleteTextures(1, new int[] { this.texId }, 0);
         texId = -1;
      }
   }
   
   public int getId() {
      return texId;
   }
   
   public static Texture2d getTexture(final InputStream inputStream, final int format) {
      try {
         return new Texture2d(inputStream, format);
      } catch (IOException e) {
         return null;
      }
   }
   
   public static Texture2d getTexture(final InputStream rgbStream, final InputStream alphaStream) {
      try {
         return new Texture2d(rgbStream, alphaStream);
      } catch (IOException e) {
         return null;
      }
   }
}
