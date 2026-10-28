package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityLookHelper {
    public EntityLiving entity;
    public float deltaLookYaw;
    public float deltaLookPitch;
    public boolean isLooking;
    public double posX;
    public double posY;
    public double posZ;

    public EntityLookHelper(EntityLiving entityLiving) {
        this.entity = entityLiving;
    }

    public void setLookPositionWithEntity(Entity entity, float f, float f2) {
        this.posX = entity.posX;
        if (entity instanceof EntityLivingBase) {
            this.posY = entity.posY + entity.getEyeHeight();
        } else {
            this.posY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0;
        }

        this.posZ = entity.posZ;
        this.deltaLookYaw = f;
        this.deltaLookPitch = f2;
        this.isLooking = true;
    }

    public void setLookPosition(double d, double d2, double d3, float f, float f2) {
        this.posX = d;
        this.posY = d2;
        this.posZ = d3;
        this.deltaLookYaw = f;
        this.deltaLookPitch = f2;
        this.isLooking = true;
    }

    public void onUpdateLook() {
        this.entity.rotationPitch = 0.0F;
        if (this.isLooking) {
            this.isLooking = false;
            double d = this.posX - this.entity.posX;
            double d2 = this.posY - (this.entity.posY + this.entity.getEyeHeight());
            double d3 = this.posZ - this.entity.posZ;
            double d4 = MathHelper.sqrt_double(d * d + d3 * d3);
            float f = (float)(Math.atan2(d3, d) * 180.0 / Math.PI) - 90.0F;
            float f2 = (float)(-(Math.atan2(d2, d4) * 180.0 / Math.PI));
            this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, f2, this.deltaLookPitch);
            this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, f, this.deltaLookYaw);
        } else {
            this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, this.entity.renderYawOffset, 10.0F);
        }

        float f = MathHelper.wrapAngleTo180_float(this.entity.rotationYawHead - this.entity.renderYawOffset);
        if (!this.entity.getNavigator().noPath()) {
            if (f < -75.0F) {
                this.entity.rotationYawHead = this.entity.renderYawOffset - 75.0F;
            }

            if (f > 75.0F) {
                this.entity.rotationYawHead = this.entity.renderYawOffset + 75.0F;
            }
        }
    }

    public float updateRotation(float f, float f2, float f3) {
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
