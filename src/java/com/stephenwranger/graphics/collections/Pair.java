package com.stephenwranger.graphics.collections;

import java.util.Comparator;

public class Pair<T1, T2> implements Comparable<Pair<T1, T2>> {
   public final T1 left;
   public final T2 right;
   private Comparator<Pair<T1, T2>> comparator = new Comparator<Pair<T1,T2>>() {
      @SuppressWarnings("unchecked")
      @Override
      public int compare(Pair<T1, T2> o1, Pair<T1, T2> o2) {
         int value = 0;
         
         if(o1 != null && o2 != null) {
            if(o1.left instanceof Comparable) {
               value = ((Comparable<T1>)o1.left).compareTo(o2.left);
            }
            
            if(o1.right instanceof Comparable && value == 0) {
               value = ((Comparable<T2>)o1.right).compareTo(o2.right);
            }
         }

         return value;
      }
   };

   public Pair(final T1 left, final T2 right) {
      this.left = left;
      this.right = right;
   }

   public void setComparator(final Comparator<Pair<T1, T2>> comparator) {
      this.comparator = comparator;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.left == null) ? 0 : this.left.hashCode());
      result = prime * result + ((this.right == null) ? 0 : this.right.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (this.getClass() != obj.getClass()) {
         return false;
      }
      final Pair<?,?> other = (Pair<?,?>) obj;
      if (this.left == null) {
         if (other.left != null) {
            return false;
         }
      } else if (!this.left.equals(other.left)) {
         return false;
      }
      if (this.right == null) {
         if (other.right != null) {
            return false;
         }
      } else if (!this.right.equals(other.right)) {
         return false;
      }
      return true;
   }

   @Override
   public int compareTo(final Pair<T1, T2> o) {
      return this.comparator.compare(this, o);
   }

   @Override
   public String toString() {
      return "[Pair: " + this.left + ", " + this.right + "]";
   }

   public static <T1, T2> Pair<T1, T2> getInstance(final T1 left, final T2 right) {
      return new Pair<T1, T2>(left, right);
   }
}
