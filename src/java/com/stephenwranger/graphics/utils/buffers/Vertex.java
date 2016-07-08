package com.stephenwranger.graphics.utils.buffers;

import java.nio.ByteBuffer;

import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.utils.TupleMath;

public class Vertex {
   private final Tuple3d vertex;
   private final Tuple3d normal;
   private final Tuple2d texCoord;
   private final Color4f color;
   
   public Vertex(final Tuple3d vertex, final Tuple3d normal, final Tuple2d texCoord) {
      this(vertex, normal, texCoord, null);
   }
   
   public Vertex(final Tuple3d vertex, final Tuple3d normal, final Color4f color) {
      this(vertex, normal, null, color);
   }
   
   public Vertex(final Tuple3d vertex, final Tuple3d normal, final Tuple2d texCoord, final Color4f color) {
      this.vertex = (vertex == null) ? null : new Tuple3d(vertex);
      this.normal = (normal == null) ? null : new Tuple3d(normal);
      this.texCoord = (texCoord == null) ? null : new Tuple2d(texCoord);
      this.color = (color == null) ? null : new Color4f(color);
      
      if(this.normal != null) {
         TupleMath.normalize(this.normal);
      }
   }
   
   public void vertexIntoBuffer(final Tuple3d origin, final ByteBuffer buffer) {
      if(vertex != null)
         buffer.putFloat((float) (vertex.x - origin.x)).putFloat((float) (vertex.y - origin.y)).putFloat((float) (vertex.z - origin.z));
      
      if(normal != null)
         buffer.putFloat((float) normal.x).putFloat((float) normal.y).putFloat((float) normal.z);
      
      if(texCoord != null)
         buffer.putFloat((float) texCoord.x).putFloat((float) texCoord.y);
      
      if(color != null)
         buffer.putFloat(color.r).putFloat(color.g).putFloat(color.b).putFloat(color.a);
   }

   public Tuple3d getVertex() {
      return vertex;
   }
   
   public Tuple3d getNormal() {
      return normal;
   }
   
   public Tuple2d getTextureCoordinates() {
      return texCoord;
   }
   
   public Color4f getColor() {
      return color;
   }
}
