package net.minecraft.entity.monster;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class EntitySlime extends EntityLiving implements IMob {
    public float squishAmount;
    public float squishFactor;
    public float prevSquishFactor;
    public int slimeJumpDelay;

    public EntitySlime(World world) {
        super(world);
        int n = 1 << this.rand.nextInt(3);
        this.yOffset = 0.0F;
        this.slimeJumpDelay = this.rand.nextInt(20) + 10;
        this.setSlimeSize(n);
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)1));
    }

    public void setSlimeSize(int n) {
        this.dataWatcher.updateObject(16, new Byte((byte)n));
        this.setSize(0.6F * n, 0.6F * n);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(n * n);
        this.setHealth(this.getMaxHealth());
        this.experienceValue = n;
    }

    public int getSlimeSize() {
        return this.dataWatcher.getWatchableObjectByte(16);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("Size", this.getSlimeSize() - 1);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        int integer = tagCompund.getInteger("Size");
        if (integer < 0) {
            integer = 0;
        }

        this.setSlimeSize(integer + 1);
    }

    public String getSlimeParticle() {
        return "slime";
    }

    public String getJumpSound() {
        return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL && this.getSlimeSize() > 0) {
            this.isDead = true;
        }

        this.squishFactor = this.squishFactor + (this.squishAmount - this.squishFactor) * 0.5F;
        this.prevSquishFactor = this.squishFactor;
        boolean onGround = this.onGround;
        super.onUpdate();
        if (this.onGround && !onGround) {
            int slimeSize = this.getSlimeSize();

            for (int i = 0; i < slimeSize * 8; i++) {
                float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                float f2 = this.rand.nextFloat() * 0.5F + 0.5F;
                float f3 = MathHelper.sin(f) * slimeSize * 0.5F * f2;
                float f4 = MathHelper.cos(f) * slimeSize * 0.5F * f2;
                this.worldObj.spawnParticle(this.getSlimeParticle(), this.posX + f3, this.boundingBox.minY, this.posZ + f4, 0.0, 0.0, 0.0);
            }

            if (this.makesSoundOnLand()) {
                this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            }

            this.squishAmount = -0.5F;
        } else if (!this.onGround && onGround) {
            this.squishAmount = 1.0F;
        }

        this.alterSquishAmount();
        if (this.worldObj.isRemote) {
            int slimeSize = this.getSlimeSize();
            this.setSize(0.6F * slimeSize, 0.6F * slimeSize);
        }
    }

    @Override
    public void updateEntityActionState() {
        this.despawnEntity();
        EntityPlayer closestVulnerablePlayerToEntity = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0);
        if (closestVulnerablePlayerToEntity != null) {
            this.faceEntity(closestVulnerablePlayerToEntity, 10.0F, 20.0F);
        }

        if (this.onGround && this.slimeJumpDelay-- <= 0) {
            this.slimeJumpDelay = this.getJumpDelay();
            if (closestVulnerablePlayerToEntity != null) {
                this.slimeJumpDelay /= 3;
            }

            this.isJumping = true;
            if (this.makesSoundOnJump()) {
                this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
            }

            this.moveStrafing = 1.0F - this.rand.nextFloat() * 2.0F;
            this.moveForward = 1 * this.getSlimeSize();
        } else {
            this.isJumping = false;
            if (this.onGround) {
                this.moveStrafing = this.moveForward = 0.0F;
            }
        }
    }

    public void alterSquishAmount() {
        this.squishAmount *= 0.6F;
    }

    public int getJumpDelay() {
        return this.rand.nextInt(20) + 10;
    }

    public EntitySlime createInstance() {
        return new EntitySlime(this.worldObj);
    }

    @Override
    public void setDead() {
        int slimeSize = this.getSlimeSize();
        if (!this.worldObj.isRemote && slimeSize > 1 && this.getHealth() <= 0.0F) {
            int n = 2 + this.rand.nextInt(3);

            for (int i = 0; i < n; i++) {
                float f = (i % 2 - 0.5F) * slimeSize / 4.0F;
                float f2 = (i / 2 - 0.5F) * slimeSize / 4.0F;
                EntitySlime createInstance = this.createInstance();
                createInstance.setSlimeSize(slimeSize / 2);
                createInstance.setLocationAndAngles(this.posX + f, this.posY + 0.5, this.posZ + f2, this.rand.nextFloat() * 360.0F, 0.0F);
                this.worldObj.spawnEntityInWorld(createInstance);
            }
        }

        super.setDead();
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn) {
        if (this.canDamagePlayer()) {
            int slimeSize = this.getSlimeSize();
            if (this.canEntityBeSeen(entityIn)
                && this.getDistanceSqToEntity(entityIn) < 0.6 * slimeSize * 0.6 * slimeSize
                && entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackStrength())) {
                this.playSound("mob.attack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }
        }
    }

    public boolean canDamagePlayer() {
        return this.getSlimeSize() > 1;
    }

    public int getAttackStrength() {
        return this.getSlimeSize();
    }

    @Override
    public String getHurtSound() {
        return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
    }

    @Override
    public String getDeathSound() {
        return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
    }

    @Override
    public Item getDropItem() {
        return this.getSlimeSize() == 1 ? Items.slime_ball : Item.getItemById(0);
    }

    @Override
    public boolean getCanSpawnHere() {
        Chunk chunkFromBlockCoords = this.worldObj.getChunkFromBlockCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
        if (this.worldObj.getWorldInfo().getTerrainType() == WorldType.FLAT && this.rand.nextInt(4) != 1) {
            return false;
        }

        if (this.getSlimeSize() == 1 || this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL) {
            BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
            if (biomeGenForCoords == BiomeGenBase.swampland
                && this.posY > 50.0
                && this.posY < 70.0
                && this.rand.nextFloat() < 0.5F
                && this.rand.nextFloat() < this.worldObj.getCurrentMoonPhaseFactor()
                && this.worldObj.getBlockLightValue(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                    <= this.rand.nextInt(8)) {
                return super.getCanSpawnHere();
            }

            if (this.rand.nextInt(10) == 0 && chunkFromBlockCoords.getRandomWithSeed(987234911L).nextInt(10) == 0 && this.posY < 40.0) {
                return super.getCanSpawnHere();
            }
        }

        return false;
    }

    @Override
    public float getSoundVolume() {
        return 0.4F * this.getSlimeSize();
    }

    @Override
    public int getVerticalFaceSpeed() {
        return 0;
    }

    public boolean makesSoundOnJump() {
        return this.getSlimeSize() > 0;
    }

    public boolean makesSoundOnLand() {
        return this.getSlimeSize() > 2;
    }
}
