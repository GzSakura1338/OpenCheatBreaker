package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.animation.Keyframe;
import com.cheatbreaker.client.emote.Bone;
import java.util.ArrayList;
import java.util.List;

public class BobjChannel {
   public List<Keyframe> keyframes = new ArrayList<>();
   public String property;
   public int axis;

   public Keyframe getKeyframe(float f, boolean flag) {
      int size = this.keyframes.size();
      if (size == 0) {
         return null;
      }

      if (size == 1) {
         return this.keyframes.get(0);
      }

      Keyframe keyframe = null;

      for (int i = 0; i < size; i++) {
         keyframe = this.keyframes.get(i);
         if (keyframe.frame > f && i != 0) {
            return flag ? keyframe : this.keyframes.get(i - 1);
         }
      }

      return keyframe;
   }

   public BobjChannel(String str, int n) {
      this.property = str;
      this.axis = n;
   }

   public float getValueAt(float f) {
      int size = this.keyframes.size();
      if (size <= 0) {
         return 0.0F;
      }

      if (size == 1) {
         return this.keyframes.get(0).value;
      }

      Keyframe keyframe = this.keyframes.get(0);
      if (keyframe.frame > f) {
         return keyframe.value;
      }

      for (int i = 0; i < size; i++) {
         keyframe = this.keyframes.get(i);
         if (keyframe.frame > f && i != 0) {
            Keyframe keyframe2 = this.keyframes.get(i - 1);
            float f2 = (f - keyframe2.frame) / (keyframe.frame - keyframe2.frame);
            return keyframe2.interpolate(f2, keyframe);
         }
      }

      return keyframe.value;
   }

   public void apply(Bone bone, float f) {
      if (this.property.equals("location")) {
         if (this.axis == 0) {
            bone.locationX = this.getValueAt(f);
         } else if (this.axis == 1) {
            bone.locationY = this.getValueAt(f);
         } else if (this.axis == 2) {
            bone.locationZ = this.getValueAt(f);
         }
      } else if (this.property.equals("rotation")) {
         if (this.axis == 0) {
            bone.rotationX = this.getValueAt(f);
         } else if (this.axis == 1) {
            bone.rotationY = this.getValueAt(f);
         } else if (this.axis == 2) {
            bone.rotationZ = this.getValueAt(f);
         }
      } else if (this.property.equals("scale")) {
         if (this.axis == 0) {
            bone.scaleX = this.getValueAt(f);
         } else if (this.axis == 1) {
            bone.scaleY = this.getValueAt(f);
         } else if (this.axis == 2) {
            bone.scaleZ = this.getValueAt(f);
         }
      }
   }

   public void applyBlended(Bone bone, float f, float f2) {
      float valueAt = this.getValueAt(f);
      if (this.property.equals("location")) {
         if (this.axis == 0) {
            bone.locationX = valueAt + (bone.locationX - valueAt) * f2;
         } else if (this.axis == 1) {
            bone.locationY = valueAt + (bone.locationY - valueAt) * f2;
         } else if (this.axis == 2) {
            bone.locationZ = valueAt + (bone.locationZ - valueAt) * f2;
         }
      } else if (this.property.equals("rotation")) {
         if (this.axis == 0) {
            bone.rotationX = valueAt + (bone.rotationX - valueAt) * f2;
         } else if (this.axis == 1) {
            bone.rotationY = valueAt + (bone.rotationY - valueAt) * f2;
         } else if (this.axis == 2) {
            bone.rotationZ = valueAt + (bone.rotationZ - valueAt) * f2;
         }
      } else if (this.property.equals("scale")) {
         if (this.axis == 0) {
            bone.scaleX = valueAt + (bone.scaleX - valueAt) * f2;
         } else if (this.axis == 1) {
            bone.scaleY = valueAt + (bone.scaleY - valueAt) * f2;
         } else if (this.axis == 2) {
            bone.scaleZ = valueAt + (bone.scaleZ - valueAt) * f2;
         }
      }
   }
}
