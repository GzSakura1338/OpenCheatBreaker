package com.cheatbreaker.client.emote;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class Bone {
    public float scaleX = 1.0F;
    public float locationZ;
    public float scaleY = 1.0F;
    public float scaleZ = 1.0F;
    public Matrix4f worldMatrix = new Matrix4f();
    public Matrix4f inverseRestMatrix = new Matrix4f();
    public Matrix4f localRestMatrix = new Matrix4f();
    public Matrix4f tempMatrix = new Matrix4f();
    public int index;
    public float rotationY;
    public String name;
    public String parentName;
    public Matrix4f restMatrix;
    public float rotationX;
    public Vector3f headPosition;
    public float rotationZ;
    public Vector3f tailPosition;
    public float length;
    public Bone parent;
    public float locationY;
    public float locationX;
    public List<LookAtConstraint> constraints;

    public void resetPose() {
        this.locationZ = 0.0F;
        this.locationY = 0.0F;
        this.locationX = 0.0F;
        this.rotationZ = 0.0F;
        this.rotationY = 0.0F;
        this.rotationX = 0.0F;
        this.scaleZ = 1.0F;
        this.scaleY = 1.0F;
        this.scaleX = 1.0F;
    }

    public void addConstraint(LookAtConstraint lookAtConstraint) {
        if (this.constraints == null) {
            this.constraints = new ArrayList<>();
        }

        this.constraints.add(lookAtConstraint);
    }

    public Matrix4f computeSkinMatrix() {
        Matrix4f computeWorldTransform = this.computeWorldTransform(new Matrix4f());
        this.worldMatrix.set(computeWorldTransform);
        this.applyConstraints();
        computeWorldTransform.set(this.worldMatrix);
        computeWorldTransform.mul(this.inverseRestMatrix);
        return computeWorldTransform;
    }

    public boolean hasConstraints() {
        return this.constraints != null;
    }

    public Bone(int n, String str, String str2, Vector3f vector3f, Matrix4f matrix4f) {
        this.index = n;
        this.name = str;
        this.parentName = str2;
        this.restMatrix = matrix4f;
        this.headPosition = new Vector3f(matrix4f.m03, matrix4f.m13, matrix4f.m23);
        this.tailPosition = vector3f;
        Vector3f vector3f2 = new Vector3f(this.tailPosition);
        vector3f2.sub(this.headPosition);
        this.length = vector3f2.length();
        this.inverseRestMatrix.set(matrix4f);
        this.inverseRestMatrix.invert();
        this.localRestMatrix.setIdentity();
    }

    public Matrix4f computeWorldTransform(Matrix4f matrix4f) {
        matrix4f.setIdentity();
        this.worldMatrix.set(this.localRestMatrix);
        this.buildLocalTransform();
        if (this.parent != null) {
            matrix4f = new Matrix4f(this.parent.worldMatrix);
        }

        matrix4f.mul(this.worldMatrix);
        return matrix4f;
    }

    public void buildLocalTransform() {
        this.tempMatrix.setIdentity();
        this.tempMatrix.m03 = this.locationX;
        this.tempMatrix.m13 = this.locationY;
        this.tempMatrix.m23 = this.locationZ;
        this.worldMatrix.mul(this.tempMatrix);
        this.tempMatrix.setIdentity();
        this.tempMatrix.m00 = this.scaleX;
        this.tempMatrix.m11 = this.scaleY;
        this.tempMatrix.m22 = this.scaleZ;
        this.worldMatrix.mul(this.tempMatrix);
        if (!this.hasConstraints()) {
            if (this.rotationZ != 0.0F) {
                this.tempMatrix.rotZ(this.rotationZ);
                this.worldMatrix.mul(this.tempMatrix);
            }

            if (this.rotationY != 0.0F) {
                this.tempMatrix.rotY(this.rotationY);
                this.worldMatrix.mul(this.tempMatrix);
            }

            if (this.rotationX != 0.0F) {
                this.tempMatrix.rotX(this.rotationX);
                this.worldMatrix.mul(this.tempMatrix);
            }
        }
    }

    public void applyConstraints() {
        if (this.hasConstraints()) {
            for (LookAtConstraint lookAtConstraint : this.constraints) {
                lookAtConstraint.apply(this);
            }
        }
    }
}
