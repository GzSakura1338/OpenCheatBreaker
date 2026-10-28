package net.minecraft.entity.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityMagmaCube extends EntitySlime {
    public EntityMagmaCube(World world) {
        super(world);
        this.isImmuneToFire = true;
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F);
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL
            && this.worldObj.checkNoEntityCollision(this.boundingBox)
            && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()
            && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

    @Override
    public int getTotalArmorValue() {
        return this.getSlimeSize() * 3;
    }

    @Override
    public int getBrightnessForRender(float f) {
        return 15728880;
    }

    @Override
    public float getBrightness(float f) {
        return 1.0F;
    }

    @Override
    public String getSlimeParticle() {
        return "flame";
    }

    @Override
    public EntitySlime createInstance() {
        return new EntityMagmaCube(this.worldObj);
    }

    @Override
    public Item getDropItem() {
        return Items.magma_cream;
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        Item dropItem = this.getDropItem();
        if (dropItem != null && this.getSlimeSize() > 1) {
            int n2 = this.rand.nextInt(4) - 2;
            if (n > 0) {
                n2 += this.rand.nextInt(n + 1);
            }

            for (int i = 0; i < n2; i++) {
                this.dropItem(dropItem, 1);
            }
        }
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    public int getJumpDelay() {
        return super.getJumpDelay() * 4;
    }

    @Override
    public void alterSquishAmount() {
        this.squishAmount *= 0.9F;
    }

    @Override
    public void jump() {
        this.motionY = 0.42F + this.getSlimeSize() * 0.1F;
        this.isAirBorne = true;
    }

    @Override
    public void fall(float distance) {
    }

    @Override
    public boolean canDamagePlayer() {
        return true;
    }

    @Override
    public int getAttackStrength() {
        return super.getAttackStrength() + 2;
    }

    @Override
    public String getJumpSound() {
        return this.getSlimeSize() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
    }

    @Override
    public boolean handleLavaMovement() {
        return false;
    }

    @Override
    public boolean makesSoundOnLand() {
        return true;
    }
}
