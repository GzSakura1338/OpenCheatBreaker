package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.MathHelper;

public class EntityMoveHelper {
    public EntityLiving entity;
    public double posX;
    public double posY;
    public double posZ;
    public double speed;
    public boolean update;

    public EntityMoveHelper(EntityLiving entityLiving) {
        this.entity = entityLiving;
        this.posX = entityLiving.posX;
        this.posY = entityLiving.posY;
        this.posZ = entityLiving.posZ;
    }

    public boolean isUpdating() {
        return this.update;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setMoveTo(double d, double d2, double d3, double d4) {
        this.posX = d;
        this.posY = d2;
        this.posZ = d3;
        this.speed = d4;
        this.update = true;
    }

    public void onUpdateMoveHelper() {
        this.entity.setMoveForward(0.0F);
        if (this.update) {
            this.update = false;
            int n = MathHelper.floor_double(this.entity.boundingBox.minY + 0.5);
            double d = this.posX - this.entity.posX;
            double d2 = this.posZ - this.entity.posZ;
            double d3 = this.posY - n;
            double d4 = d * d + d3 * d3 + d2 * d2;
            if (d4 >= 2.5000003E-7F) {
                float f = (float)(Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0F;
                this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f, 30.0F);
                this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));
                if (d3 > 0.0 && d * d + d2 * d2 < 1.0) {
                    this.entity.getJumpHelper().setJumping();
                }
            }
        }
    }

    public float limitAngle(float f, float f2, float f3) {
        float f4 = MathHelper.wrapAngleTo180_float(f2 - f);
        if (f4 > f3) {
            f4 = f3;
        }

        if (f4 < -f3) {
            f4 = -f3;
        }

        return f + f4;
    }
}
