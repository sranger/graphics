package com.stephenwranger.graphics.utils;

import java.util.Collections;
import java.util.List;

import com.stephenwranger.graphics.math.Quat4d;
import com.stephenwranger.graphics.math.Tuple3d;

public class Keyframe implements Comparable<Keyframe> {
   public final double time;
   public final Tuple3d position;
   public final Tuple3d axis;
   public final double angle;

   public Keyframe(final double time, final Tuple3d position, final Tuple3d axis, final double angle) {
      this.time = time;
      this.position = new Tuple3d(position);
      this.axis = new Tuple3d(axis);
      this.angle = angle;
   }

   @Override
   public int compareTo(final Keyframe kf) {
      return Double.valueOf(this.time).compareTo(Double.valueOf(kf.time));
   }

   /**
    * Interpolates between the given {@link Keyframe} and this with the t value as the current time.
    * 
    * @param next
    *           the next keyframe
    * @param t
    *           current time value
    * @return if t <= this.time, returns this, if t >= next.time, returns next, otherwise, returns keyframe at time t
    *         interpolated between this and next
    */
   public Keyframe interpolate(final Keyframe next, final double t) {
      if (t <= this.time) {
         return this;
      } else if (t >= next.time) {
         return next;
      }

      final double delta = (t - this.time) / (next.time - this.time);
      final float[] temp = new float[3];
      final Quat4d q1 = new Quat4d();
      q1.setFromAngleAxis((float) Math.toRadians(angle), axis.toFloatArray(), temp);
      q1.normalize();

      final Quat4d q2 = new Quat4d();
      q2.setFromAngleAxis((float) Math.toRadians(next.angle), next.axis.toFloatArray(), temp  );
      q2.normalize();

      final Quat4d slerpd = new Quat4d();
      slerpd.setSlerp(q1, q2, (float) (delta));
      slerpd.normalize();

      final Tuple3d offset = TupleMath.sub(next.position, this.position);
      TupleMath.scale(offset, delta);
      offset.set(TupleMath.add(this.position, offset));

      final float[] slerpdAxis = new float[3];
      final float slerpdAngle = slerpd.toAngleAxis(slerpdAxis);
      final Tuple3d axis = new Tuple3d((double) slerpdAxis[0], (double) slerpdAxis[1], (double) slerpdAxis[2]);
      final double angle = Math.toDegrees(slerpdAngle);

      return new Keyframe(t, offset, axis, angle);
   }

   public static Keyframe getPrevious(final List<Keyframe> keyframes, final double time) {
      Collections.sort(keyframes);

      if (keyframes == null || keyframes.isEmpty()) {
         return null;
      } else if (keyframes.size() == 1) {
         return keyframes.get(0);
      } else if (time <= keyframes.get(0).time) {
         return keyframes.get(0);
      } else if (time >= keyframes.get(keyframes.size() - 1).time) {
         return keyframes.get(keyframes.size() - 1);
      }

      for (int i = 1; i < keyframes.size(); i++) {
         if (keyframes.get(i).time >= time) {
            return keyframes.get(i - 1);
         }
      }

      // shouldn't happen
      return keyframes.get(keyframes.size() - 1);
   }

   public static Keyframe getNext(final List<Keyframe> keyframes, final double time) {
      Collections.sort(keyframes);

      if (keyframes == null || keyframes.isEmpty()) {
         return null;
      } else if (keyframes.size() == 1) {
         return keyframes.get(0);
      } else if (time <= keyframes.get(0).time) {
         return keyframes.get(0);
      } else if (time >= keyframes.get(keyframes.size() - 1).time) {
         return keyframes.get(keyframes.size() - 1);
      }

      for (int i = 0; i < keyframes.size(); i++) {
         if (keyframes.get(i).time >= time) {
            return keyframes.get(i);
         }
      }

      // shouldn't happen
      return keyframes.get(keyframes.size() - 1);
   }
}
