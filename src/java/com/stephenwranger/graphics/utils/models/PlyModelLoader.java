package com.stephenwranger.graphics.utils.models;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.intersection.Triangle2d;
import com.stephenwranger.graphics.math.intersection.Triangle3d;
import com.stephenwranger.graphics.utils.MathUtils;

public class PlyModelLoader {
   private int        numVertices, numFaces;
   private double[][] vertices;
   private double[][] normals;
   private int[][]    indices;

   public PlyModelLoader() {

   }

   public void loadModel(final File file, final List<Triangle3d> outputTriangles, final List<Tuple3d> outputVertices) {
      try {
         final BufferedReader reader = new BufferedReader(new FileReader(file));
         String temp = null;
         boolean body = false;
         this.numVertices = 0;
         this.numFaces = 0;
         boolean hasNormals = false;
         boolean isASCII = true;
         int propertyCount = 0;
         int xpos = -1, ypos = -1, zpos = -1, nxpos = -1, nypos = -1, nzpos = -1;

         while (!body && ((temp = reader.readLine()) != null)) {
            if (temp.startsWith("element vertex")) {
               this.numVertices = Integer.parseInt(temp.split(" ")[2]);
               this.vertices = new double[this.numVertices][3];
               this.normals = new double[this.numVertices][3];
            } else if (temp.startsWith("element face")) {
               this.numFaces = Integer.parseInt(temp.split(" ")[2]);
               this.indices = new int[this.numFaces][3];
            } else if (temp.startsWith("property")) {
               if (temp.endsWith(" nx")) {
                  nxpos = propertyCount;
               } else if (temp.endsWith(" ny")) {
                  nypos = propertyCount;
               } else if (temp.endsWith(" nz")) {
                  nzpos = propertyCount;
               } else if (temp.endsWith(" x")) {
                  xpos = propertyCount;
               } else if (temp.endsWith(" y")) {
                  ypos = propertyCount;
               } else if (temp.endsWith(" z")) {
                  zpos = propertyCount;
               }

               if (!temp.startsWith("property list")) {
                  propertyCount++;
               }
            } else if (temp.startsWith("format binary")) {
               isASCII = false;
            } else if (temp.equals("end_header")) {
               body = true;
            }
         }

         hasNormals = (nxpos != -1) && (nypos != -1) && (nzpos != -1);

         if (isASCII) {
            this.readASCII(reader, new int[] { xpos, ypos, zpos }, new int[] { nxpos, nypos, nzpos }, hasNormals);
         } else {
            this.readBinary(file, propertyCount, new int[] { xpos, ypos, zpos }, new int[] { nxpos, nypos, nzpos }, hasNormals);
         }

         this.computeNormals(hasNormals);
      } catch (final Exception e) {
         e.printStackTrace();
         System.exit(1);
      }

      if (outputVertices != null) {
         for (final double[] vertex : this.vertices) {
            outputVertices.add(new Tuple3d(vertex));
         }
      }

      if (outputTriangles != null) {
         Tuple3d v0, v1, v2;

         for (final int[] face : this.indices) {
            v0 = new Tuple3d(this.vertices[face[0]]);
            v1 = new Tuple3d(this.vertices[face[1]]);
            v2 = new Tuple3d(this.vertices[face[2]]);
            outputTriangles.add(new Triangle3d(v0, v1, v2));
         }
      }
   }

   private void computeNormals(final boolean hasNormals) {
      if (!hasNormals) {
         double[] normal = new double[3];

         for (final int[] face : this.indices) {
            normal = MathUtils.computeNormal(this.vertices, face);

            for (final int i : face) {
               this.normals[i][0] += normal[0];
               this.normals[i][1] += normal[1];
               this.normals[i][2] += normal[2];
            }
         }

         final double[] zero = new double[] { 0, 0, 0 };
         double length = 0;

         // average and normalize
         for (int i = 0; i < this.normals.length; i++) {
            length = MathUtils.getDistance(zero, this.normals[i]);

            this.normals[i][0] /= length;
            this.normals[i][1] /= length;
            this.normals[i][2] /= length;
         }
      }
   }

   private void readASCII(final BufferedReader reader, final int[] pos, final int[] normal, final boolean hasNormals) throws IOException {
      String[] split;
      String temp;

      for (int i = 0; i < this.numVertices; i++) {
         split = reader.readLine().split(" ");

         this.vertices[i][0] = (pos[0] < 0) ? Double.NaN : Double.parseDouble(split[pos[0]]);
         this.vertices[i][1] = (pos[1] < 0) ? Double.NaN : Double.parseDouble(split[pos[1]]);
         this.vertices[i][2] = (pos[2] < 0) ? Double.NaN : Double.parseDouble(split[pos[2]]);

         this.normals[i][0] = hasNormals ? Double.parseDouble(split[normal[0]]) : 0;
         this.normals[i][1] = hasNormals ? Double.parseDouble(split[normal[1]]) : 0;
         this.normals[i][2] = hasNormals ? Double.parseDouble(split[normal[2]]) : 0;
      }

      int ctr = 0;

      while ((temp = reader.readLine()) != null) {
         split = temp.split(" ");
         this.indices[ctr][0] = Integer.parseInt(split[1]);
         this.indices[ctr][1] = Integer.parseInt(split[2]);
         this.indices[ctr][2] = Integer.parseInt(split[3]);
         ctr++;
      }
   }

   private void readBinary(final File modelLocation, final int propertyCount, final int[] pos, final int[] normal, final boolean hasNormals) throws IOException {
      try (final DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(modelLocation)))) {
         StringBuilder builder = new StringBuilder();
         String temp = "";
         byte c = 0;

         while (!temp.startsWith("end_header")) {
            while ((c = (byte) dis.read()) != '\n') {
               builder.append((char) c);
            }

            temp = builder.toString();
            builder = new StringBuilder();
         }

         final double[] properties = new double[propertyCount];

         for (int i = 0; i < this.numVertices; i++) {
            for (int j = 0; j < propertyCount; j++) {
               properties[j] = dis.readFloat();
            }

            this.vertices[i][0] = properties[pos[0]];
            this.vertices[i][1] = properties[pos[1]];
            this.vertices[i][2] = properties[pos[2]];

            this.normals[i][0] = hasNormals ? properties[normal[0]] : 0;
            this.normals[i][1] = hasNormals ? properties[normal[1]] : 0;
            this.normals[i][2] = hasNormals ? properties[normal[2]] : 0;
         }

         for (int i = 0; i < this.numFaces; i++) {
            dis.read(); // only supporting triangles
            this.indices[i][0] = dis.readInt();
            this.indices[i][1] = dis.readInt();
            this.indices[i][2] = dis.readInt();
         }
      }
   }

   public static void writePlyModel2d(final File outputFile, final Collection<Triangle2d> triangles) throws IOException {
      final Set<Tuple2d> verticesSet = new HashSet<Tuple2d>();

      for (final Triangle2d triangle : triangles) {
         verticesSet.addAll(Arrays.asList(triangle.getCorners()));
      }

      PlyModelLoader.writePlyModel2d(outputFile, verticesSet, triangles);
   }

   public static void writePlyModel2d(final File outputFile, final Set<Tuple2d> verticesSet, final Collection<Triangle2d> triangles) throws IOException {
      final List<Tuple2d> vertices = new ArrayList<Tuple2d>(verticesSet);
      final List<int[]> faces = new ArrayList<int[]>();
      Tuple2d[] corners;

      if (triangles != null && !triangles.isEmpty()) {
         for (final Triangle2d triangle : triangles) {
            corners = triangle.getCorners();
            faces.add(new int[] { vertices.indexOf(corners[0]), vertices.indexOf(corners[1]), vertices.indexOf(corners[2]) });
         }
      }

      try (final BufferedWriter fout = new BufferedWriter(new FileWriter(outputFile))) {
         // header
         fout.write("ply\n");
         fout.write("format ascii 1.0\n");
         fout.write("element vertex " + vertices.size() + "\n");
         fout.write("property double x\n");
         fout.write("property double y\n");
         fout.write("element face " + faces.size() + "\n");
         fout.write("property list uchar int vertex_indices\n");
         fout.write("end_header\n");

         for (final Tuple2d vertex : vertices) {
            fout.write(vertex.x + " " + vertex.y + "\n");
         }

         for (final int[] face : faces) {
            fout.write("3 " + face[0] + " " + face[1] + " " + face[2] + "\n");
         }
      }
   }

   public static void writePlyModel3d(final File outputFile, final Collection<Triangle3d> triangles) throws IOException {
      final Set<Tuple3d> verticesSet = new HashSet<Tuple3d>();

      for (final Triangle3d triangle : triangles) {
         verticesSet.addAll(Arrays.asList(triangle.getCorners()));
      }

      PlyModelLoader.writePlyModel3d(outputFile, verticesSet, triangles);
   }

   public static void writePlyModel3d(final File outputFile, final Set<Tuple3d> verticesSet, final Collection<Triangle3d> triangles) throws IOException {
      final List<Tuple3d> vertices = new ArrayList<Tuple3d>(verticesSet);
      final List<int[]> faces = new ArrayList<int[]>();
      Tuple3d[] corners;

      if (triangles != null && !triangles.isEmpty()) {
         for (final Triangle3d triangle : triangles) {
            corners = triangle.getCorners();
            faces.add(new int[] { vertices.indexOf(corners[0]), vertices.indexOf(corners[1]), vertices.indexOf(corners[2]) });
         }
      }

      try (final BufferedWriter fout = new BufferedWriter(new FileWriter(outputFile))) {
         // header
         fout.write("ply\n");
         fout.write("format ascii 1.0\n");
         fout.write("element vertex " + vertices.size() + "\n");
         fout.write("property double x\n");
         fout.write("property double y\n");
         fout.write("property double z\n");
         fout.write("element face " + faces.size() + "\n");
         fout.write("property list uchar int vertex_indices\n");
         fout.write("end_header\n");

         for (final Tuple3d vertex : vertices) {
            fout.write(vertex.x + " " + vertex.y + " " + vertex.z + "\n");
         }

         for (final int[] face : faces) {
            fout.write("3 " + face[0] + " " + face[1] + " " + face[2] + "\n");
         }
      }
   }
}
