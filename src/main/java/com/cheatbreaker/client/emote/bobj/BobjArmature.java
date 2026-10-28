package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.Bone;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Matrix4f;

public class BobjArmature {
    public String defaultActionName = "";
    public Matrix4f[] boneMatrices;
    public Map<String, Bone> bonesByName = new HashMap<>();
    public List<Bone> bones = new ArrayList<>();
    public List<Bone> constrainedBones = new ArrayList<>();
    public boolean initialized;
    public String name;

    public void initialize() {
        if (!this.initialized) {
            ArrayList list = new ArrayList();

            for (Bone bone : this.bonesByName.values()) {
                if (bone.hasConstraints()) {
                    list.add(bone);
                }

                if (!bone.parentName.isEmpty()) {
                    bone.parent = this.bonesByName.get(bone.parentName);
                    bone.localRestMatrix.set(bone.parent.restMatrix);
                    bone.localRestMatrix.invert();
                    bone.localRestMatrix.mul(bone.restMatrix);
                } else {
                    bone.localRestMatrix.set(bone.restMatrix);
                }
            }

            if (!list.isEmpty()) {
                this.constrainedBones = list;
            }

            Collections.sort(this.bones, new BoneIndexComparator(this));
            this.boneMatrices = new Matrix4f[this.bones.size()];
            this.initialized = true;
        }
    }

    public void updateBoneMatrices() {
        for (Bone bone : this.bones) {
            this.boneMatrices[bone.index] = bone.computeSkinMatrix();
        }
    }

    public void addBone(Bone bone) {
        this.bonesByName.put(bone.name, bone);
        this.bones.add(bone);
    }

    public BobjArmature(String str) {
        this.name = str;
    }
}
