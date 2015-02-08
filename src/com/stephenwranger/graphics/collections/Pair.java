package com.stephenwranger.graphics.collections;

import java.util.Comparator;

public class Pair<T1, T2> implements Comparable<Pair<T1, T2>> {
   public final T1 left;
   public final T2 right;
   private Comparator<Pair<T1, T2>> comparator = new Comparator<Pair<T1,T2>>() {
      @SuppressWarnings("unchecked")
      @Override
      public int compare(Pair<T1, T2> o1, Pair<T1, T2> o2) {
         if(o1 != null && o2 != null) {
            if(o1.left instanceof Comparable) {
               return ((Comparable<T1>)o1.left).compareTo((T1)o2.left);
            } else if(o1.right instanceof Comparable) {
               return ((Comparable<T2>)o1.right).compareTo((T2)o2.right);
            }
         }

         return 0;
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
      result = prime * result + ((left == null) ? 0 : left.hashCode());
      result = prime * result + ((right == null) ? 0 : right.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Pair<?,?> other = (Pair<?,?>) obj;
      if (left == null) {
         if (other.left != null)
            return false;
      } else if (!left.equals(other.left))
         return false;
      if (right == null) {
         if (other.right != null)
            return false;
      } else if (!right.equals(other.right))
         return false;
      return true;
   }

   @Override
   public int compareTo(final Pair<T1, T2> o) {
      return comparator.compare(this, o);
   }
   
   public static <T1, T2> Pair<T1, T2> getInstance(final T1 left, final T2 right) {
      return new Pair<T1, T2>(left, right);
   }
}
