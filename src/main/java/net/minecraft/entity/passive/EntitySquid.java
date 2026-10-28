package net.minecraft.entity.passive;

import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySquid extends EntityWaterMob {
    public float squidPitch;
    public float prevSquidPitch;
    public float squidYaw;
    public float prevSquidYaw;
    public float squidRotation;
    public float prevSquidRotation;
    public float tentacleAngle;
    public float lastTentacleAngle;
    public float randomMotionSpeed;
    public float rotationVelocity;
    public float turningSpeed;
    public float randomMotionVecX;
    public float randomMotionVecY;
    public float randomMotionVecZ;

    public EntitySquid(World world) {
        super(world);
        this.setSize(0.95F, 0.95F);
        this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
    }

    @Override
    public String getLivingSound() {
        return null;
    }

    @Override
    public String getHurtSound() {
        return null;
    }

    @Override
    public String getDeathSound() {
        return null;
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public Item getDropItem() {
        return Item.getItemById(0);
    }

    @Override
    public boolean canTriggerWalking() {
        return false;
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        int n2 = this.rand.nextInt(3 + n) + 1;

        for (int i = 0; i < n2; i++) {
            this.entityDropItem(new ItemStack(Items.dye, 1, 0), 0.0F);
        }
    }

    @Override
    public boolean isInWater() {
        return this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0, -0.6F, 0.0), Material.water, this);
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        super.onLivingUpdate();
        this.prevSquidPitch = this.squidPitch;
        this.prevSquidYaw = this.squidYaw;
        this.prevSquidRotation = this.squidRotation;
        this.lastTentacleAngle = this.tentacleAngle;
        this.squidRotation = this.squidRotation + this.rotationVelocity;
        if (this.squidRotation > (float) (Math.PI * 2)) {
            this.squidRotation -= (float) (Math.PI * 2);
            if (this.rand.nextInt(10) == 0) {
                this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
            }
        }

        if (this.isInWater()) {
            if (this.squidRotation < (float) Math.PI) {
                float f = this.squidRotation / (float) Math.PI;
                this.tentacleAngle = MathHelper.sin(f * f * (float) Math.PI) * (float) Math.PI * 0.25F;
                if (f > 0.75) {
                    this.randomMotionSpeed = 1.0F;
                    this.turningSpeed = 1.0F;
                } else {
                    this.turningSpeed *= 0.8F;
                }
            } else {
                this.tentacleAngle = 0.0F;
                this.randomMotionSpeed *= 0.9F;
                this.turningSpeed *= 0.99F;
            }

            if (!this.worldObj.isRemote) {
                this.motionX = this.randomMotionVecX * this.randomMotionSpeed;
                this.motionY = this.randomMotionVecY * this.randomMotionSpeed;
                this.motionZ = this.randomMotionVecZ * this.randomMotionSpeed;
            }

            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.renderYawOffset = this.renderYawOffset
                + (-((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float) Math.PI - this.renderYawOffset) * 0.1F;
            this.rotationYaw = this.renderYawOffset;
            this.squidYaw = this.squidYaw + (float) Math.PI * this.turningSpeed * 1.5F;
            this.squidPitch = this.squidPitch + (-((float)Math.atan2(f, this.motionY)) * 180.0F / (float) Math.PI - this.squidPitch) * 0.1F;
        } else {
            this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.squidRotation)) * (float) Math.PI * 0.25F;
            if (!this.worldObj.isRemote) {
                this.motionX = 0.0;
                this.motionY -= 0.08;
                this.motionY *= 0.98F;
                this.motionZ = 0.0;
            }

            this.squidPitch = (float)(this.squidPitch + (-90.0F - this.squidPitch) * 0.02);
        }
    }

    @Override
    public void moveEntityWithHeading(float f, float f2) {
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
    }

    @Override
    public void updateEntityActionState() {
        this.entityAge++;
        if (this.entityAge > 100) {
            this.randomMotionVecX = this.randomMotionVecY = this.randomMotionVecZ = 0.0F;
        } else if (this.rand.nextInt(50) == 0 || !this.inWater || this.randomMotionVecX == 0.0F && this.randomMotionVecY == 0.0F && this.randomMotionVecZ == 0.0F
            )
         {
            float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            this.randomMotionVecX = MathHelper.cos(f) * 0.2F;
            this.randomMotionVecY = -0.1F + this.rand.nextFloat() * 0.2F;
            this.randomMotionVecZ = MathHelper.sin(f) * 0.2F;
        }

        this.despawnEntity();
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.posY > 45.0 && this.posY < 63.0 && super.getCanSpawnHere();
    }
}
