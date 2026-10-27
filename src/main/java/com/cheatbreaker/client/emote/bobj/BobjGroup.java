package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.Bone;
import java.util.ArrayList;
import java.util.List;

public class BobjGroup {
   public List<BobjChannel> channels = new ArrayList<>();
   public String boneName;

   public void apply(Bone bone, float f) {
      for (BobjChannel bobjChannel : this.channels) {
         bobjChannel.apply(bone, f);
      }
   }

   public BobjGroup(String str) {
      this.boneName = str;
   }

   public int getLength() {
      int n = 0;

      for (BobjChannel bobjChannel : this.channels) {
         int size = bobjChannel.keyframes.size();
         if (size > 0) {
            n = Math.max(n, bobjChannel.keyframes.get(size - 1).frame);
         }
      }

      return n;
   }

   public void applyBlended(Bone bone, float f, float f2) {
      for (BobjChannel bobjChannel : this.channels) {
         bobjChannel.applyBlended(bone, f, f2);
      }
   }
}
