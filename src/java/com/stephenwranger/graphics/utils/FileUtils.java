package com.stephenwranger.graphics.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
   private FileUtils() {
      // statics only
   }
   
   public static String getStreamContentsString(final InputStream inputStream) {
      final StringBuilder sb = new StringBuilder();
      
      try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
         String line = null;
         
         while((line = reader.readLine()) != null) {
            if(sb.length() != 0) {
               sb.append("\n");
            }
            sb.append(line);
         }
      } catch (final IOException e) {
         e.printStackTrace();
      }
      
      return sb.toString();
   }
   
   public static byte[] getStreamContentsBuffer(final InputStream inputStream) {
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      
      try(final BufferedInputStream bis = new BufferedInputStream(inputStream)) {
         final byte[] buffer = new byte[2000];
         int count = -1;
         
         while((count = bis.read(buffer)) != -1) {
            baos.write(buffer, 0, count);
         }
      } catch (final IOException e) {
         e.printStackTrace();
      }
      
      return baos.toByteArray();
   }
}
