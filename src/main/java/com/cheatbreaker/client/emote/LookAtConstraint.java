package com.cheatbreaker.client.emote;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class LookAtConstraint {
   public int mode = 0;
   public Vector4f targetPosition = new Vector4f();
   public Vector4f tempVector = new Vector4f();
   public Matrix4f matrix4f = new Matrix4f();
   public Bone bone;
   public boolean useTail;

   public void apply(Bone bone) {
      if (this.mode != 0 && this.bone != null) {
         this.targetPosition.set(0.0F, 0.0F, 0.0F, 1.0F);
         this.bone.worldMatrix.transform(this.targetPosition);
         this.tempVector.set(0.0F, 0.0F, 0.0F, 1.0F);
         bone.worldMatrix.transform(this.tempVector);
         this.tempVector.sub(this.targetPosition);
         float f = this.tempVector.length();
         this.matrix4f.set(bone.worldMatrix);
         this.matrix4f.invert();
         this.tempVector.set(this.targetPosition);
         this.matrix4f.transform(this.tempVector);
         Vector3f vector3f = new Vector3f(this.tempVector.x, this.tempVector.y, this.tempVector.z);
         vector3f.normalize();
         this.tempVector.set(0.0F, 0.0F, 1.0F, 1.0F);
         this.bone.worldMatrix.transform(this.tempVector);
         Vector3f vector3f2 = new Vector3f(0.0F, 1.0F, 0.0F);
         vector3f2.normalize();
         vector3f2.cross(vector3f, vector3f2);
         vector3f2.normalize();
         Vector3f vector3f3 = new Vector3f(0.0F, 0.0F, 0.0F);
         vector3f3.cross(vector3f2, vector3f);
         vector3f3.normalize();
         this.matrix4f.setIdentity();
         this.matrix4f.m00 = vector3f2.x;
         this.matrix4f.m10 = vector3f2.y;
         this.matrix4f.m20 = vector3f2.z;
         this.matrix4f.m01 = vector3f.x;
         this.matrix4f.m11 = vector3f.y;
         this.matrix4f.m21 = vector3f.z;
         this.matrix4f.m02 = vector3f3.x;
         this.matrix4f.m12 = vector3f3.y;
         this.matrix4f.m22 = vector3f3.z;
         if (this.useTail) {
            this.tempVector.set(0.0F, f - bone.length, 0.0F, 1.0F);
            this.matrix4f.transform(this.tempVector);
            this.matrix4f.m03 = this.tempVector.x;
            this.matrix4f.m13 = this.tempVector.y;
            this.matrix4f.m23 = this.tempVector.z;
         }

         Matrix4f matrix4f = new Matrix4f();
         bone.worldMatrix.set(bone.localRestMatrix);
         bone.buildLocalTransform();
         bone.worldMatrix.mul(this.matrix4f);
         if (bone.parent != null) {
            matrix4f = new Matrix4f(bone.parent.worldMatrix);
         }

         matrix4f.mul(bone.worldMatrix);
         bone.worldMatrix.set(matrix4f);
      }
   }

   public LookAtConstraint(Bone bone, int n, boolean flag) {
      this.bone = bone;
      this.mode = n;
      this.useTail = flag;
   }
}
