package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.Bone;
import java.util.Comparator;

public class BoneIndexComparator implements Comparator<Bone> {
   public BobjArmature armature;

   public BoneIndexComparator(BobjArmature bobjArmature) {
      this.armature = bobjArmature;
   }

   public int compare(Bone bone, Bone bone2) {
      return bone.index - bone2.index;
   }
}
