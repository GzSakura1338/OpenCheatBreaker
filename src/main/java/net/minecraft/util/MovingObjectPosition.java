package net.minecraft.util;

import net.minecraft.entity.Entity;

public class MovingObjectPosition {
    public MovingObjectPosition.MovingObjectType typeOfHit;
    public int blockX;
    public int blockY;
    public int blockZ;
    public int sideHit;
    public Vec3 hitVec;
    public Entity entityHit;

    public MovingObjectPosition(int n, int n2, int n3, int n4, Vec3 vec3) {
        this(n, n2, n3, n4, vec3, true);
    }

    public MovingObjectPosition(int n, int n2, int n3, int n4, Vec3 vec3, boolean flag) {
        this.typeOfHit = flag ? MovingObjectPosition.MovingObjectType.BLOCK : MovingObjectPosition.MovingObjectType.MISS;
        this.blockX = n;
        this.blockY = n2;
        this.blockZ = n3;
        this.sideHit = n4;
        this.hitVec = Vec3.createVectorHelper(vec3.xCoord, vec3.yCoord, vec3.zCoord);
    }

    public MovingObjectPosition(Entity entity) {
        this(entity, Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ));
    }

    public MovingObjectPosition(Entity entity, Vec3 vec3) {
        this.typeOfHit = MovingObjectPosition.MovingObjectType.ENTITY;
        this.entityHit = entity;
        this.hitVec = vec3;
    }

    @Override
    public String toString() {
        return "HitResult{type="
            + this.typeOfHit
            + ", x="
            + this.blockX
            + ", y="
            + this.blockY
            + ", z="
            + this.blockZ
            + ", f="
            + this.sideHit
            + ", pos="
            + this.hitVec
            + ", entity="
            + this.entityHit
            + '}';
    }

    public enum MovingObjectType {
        MISS("MISS", 0),
        BLOCK("BLOCK", 1),
        ENTITY("ENTITY", 2);

        public static MovingObjectPosition.MovingObjectType[] movingObjectTypeArray = new MovingObjectPosition.MovingObjectType[]{MISS, BLOCK, ENTITY};

        MovingObjectType(String str, int n) {
        }
    }
}
