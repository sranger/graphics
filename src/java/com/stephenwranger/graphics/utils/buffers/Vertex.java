package com.stephenwranger.graphics.utils.buffers;

import java.nio.ByteBuffer;

import com.stephenwranger.graphics.color.Color4f;
import com.stephenwranger.graphics.math.Tuple2d;
import com.stephenwranger.graphics.math.Tuple3d;
import com.stephenwranger.graphics.math.Vector3d;
import com.stephenwranger.graphics.utils.TupleMath;

public class Vertex {
   private final Tuple3d  baseVertex;
   private final Tuple3d  vertex;
   private final Vector3d normal;
   private final Tuple2d  texCoord;
   private final Color4f  color;

   public Vertex(final Tuple3d baseVertex, final Tuple3d vertex, final Vector3d normal, final Color4f color) {
      this(baseVertex, vertex, normal, null, color);
   }

   public Vertex(final Tuple3d baseVertex, final Tuple3d vertex, final Vector3d normal, final Tuple2d texCoord) {
      this(baseVertex, vertex, normal, texCoord, null);
   }

   public Vertex(final Tuple3d baseVertex, final Tuple3d vertex, final Vector3d normal, final Tuple2d texCoord, final Color4f color) {
      this.baseVertex = (baseVertex == null) ? null : new Tuple3d(baseVertex);
      this.vertex = (vertex == null) ? null : new Tuple3d(vertex);
      this.normal = (normal == null) ? null : new Vector3d(normal);
      this.texCoord = (texCoord == null) ? null : new Tuple2d(texCoord);
      this.color = (color == null) ? null : new Color4f(color);

      if (this.normal != null) {
         TupleMath.normalize(this.normal);
      }
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
      Vertex other = (Vertex) obj;
      if (this.color == null) {
         if (other.color != null) {
            return false;
         }
      } else if (!this.color.equals(other.color)) {
         return false;
      }
      if (this.normal == null) {
         if (other.normal != null) {
            return false;
         }
      } else if (!this.normal.equals(other.normal)) {
         return false;
      }
      if (this.texCoord == null) {
         if (other.texCoord != null) {
            return false;
         }
      } else if (!this.texCoord.equals(other.texCoord)) {
         return false;
      }
      if (this.vertex == null) {
         if (other.vertex != null) {
            return false;
         }
      } else if (!this.vertex.equals(other.vertex)) {
         return false;
      }
      return true;
   }

   public Tuple3d getBaseVertex() {
      return new Tuple3d(this.baseVertex);
   }

   public Color4f getColor() {
      return new Color4f(this.color);
   }

   public Vector3d getNormal() {
      return new Vector3d(this.normal);
   }

   public Tuple2d getTextureCoordinates() {
      return new Tuple2d(this.texCoord);
   }

   public Tuple3d getVertex() {
      return new Tuple3d(this.vertex);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + ((this.color == null) ? 0 : this.color.hashCode());
      result = (prime * result) + ((this.normal == null) ? 0 : this.normal.hashCode());
      result = (prime * result) + ((this.texCoord == null) ? 0 : this.texCoord.hashCode());
      result = (prime * result) + ((this.vertex == null) ? 0 : this.vertex.hashCode());
      return result;
   }

   public void vertexIntoBuffer(final Tuple3d origin, final ByteBuffer buffer) {
      if (this.vertex != null) {
         buffer.putFloat((float) (this.vertex.x - origin.x)).putFloat((float) (this.vertex.y - origin.y)).putFloat((float) (this.vertex.z - origin.z));
      }

      if (this.normal != null) {
         buffer.putFloat((float) this.normal.x).putFloat((float) this.normal.y).putFloat((float) this.normal.z);
      }

      if (this.texCoord != null) {
         buffer.putFloat((float) this.texCoord.x).putFloat((float) this.texCoord.y);
      }

      if (this.color != null) {
         buffer.putFloat(this.color.r).putFloat(this.color.g).putFloat(this.color.b).putFloat(this.color.a);
      }
   }
}
