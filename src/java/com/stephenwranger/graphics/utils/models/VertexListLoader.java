package com.stephenwranger.graphics.utils.models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;

public class VertexListLoader {
   private VertexListLoader() {
      // static only
   }

   public static void loadVertexList2d(final File file, final List<Tuple2d> output) throws FileNotFoundException, IOException {
      try (final BufferedReader fin = new BufferedReader(new FileReader(file))) {
         String line;
         String[] split;

         while ((line = fin.readLine()) != null) {
            split = line.split(" ");
            output.add(new Tuple2d(Double.parseDouble(split[0]), Double.parseDouble(split[1])));
         }
      }
   }

   public static void loadVertexList3d(final File file, final List<Tuple3d> output) throws FileNotFoundException, IOException {
      try (final BufferedReader fin = new BufferedReader(new FileReader(file))) {
         String line;
         String[] split;

         while ((line = fin.readLine()) != null) {
            split = line.split(" ");
            output.add(new Tuple3d(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])));
         }
      }
   }

   public static void writeVertexList2d(final File output, final Collection<Tuple2d> vertices) throws IOException {
      try (final BufferedWriter fout = new BufferedWriter(new FileWriter(output))) {
         for (final Tuple2d vertex : vertices) {
            fout.write(vertex.x + " " + vertex.y + "\n");
         }
      }
   }

   public static void writeVertexList3d(final File output, final Collection<Tuple3d> vertices) throws IOException {
      try (final BufferedWriter fout = new BufferedWriter(new FileWriter(output))) {
         for (final Tuple3d vertex : vertices) {
            fout.write(vertex.x + " " + vertex.y + " " + vertex.z + "\n");
         }
      }
   }
}
