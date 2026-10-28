package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityCow extends EntityAnimal {
    public EntityCow(World world) {
        super(world);
        this.setSize(0.9F, 1.3F);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 2.0));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0));
        this.tasks.addTask(3, new EntityAITempt(this, 1.25, Items.wheat, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.25));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F);
    }

    @Override
    public String getLivingSound() {
        return "mob.cow.say";
    }

    @Override
    public String getHurtSound() {
        return "mob.cow.hurt";
    }

    @Override
    public String getDeathSound() {
        return "mob.cow.hurt";
    }

    @Override
    public void playStepSound(int x, int y, int z, Block blockIn) {
        this.playSound("mob.cow.step", 0.15F, 1.0F);
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public Item getDropItem() {
        return Items.leather;
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        int n2 = this.rand.nextInt(3) + this.rand.nextInt(1 + n);

        for (int i = 0; i < n2; i++) {
            this.dropItem(Items.leather, 1);
        }

        n2 = this.rand.nextInt(3) + 1 + this.rand.nextInt(1 + n);

        for (int i = 0; i < n2; i++) {
            if (this.isBurning()) {
                this.dropItem(Items.cooked_beef, 1);
            } else {
                this.dropItem(Items.beef, 1);
            }
        }
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer) throws java.io.IOException {
        ItemStack currentItem = entityPlayer.inventory.getCurrentItem();
        if (currentItem != null && currentItem.getItem() == Items.bucket && !entityPlayer.capabilities.isCreativeMode) {
            if (currentItem.stackSize-- == 1) {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.milk_bucket));
            } else if (!entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.milk_bucket))) {
                entityPlayer.dropPlayerItemWithRandomChoice(new ItemStack(Items.milk_bucket, 1, 0), false);
            }

            return true;
        } else {
            return super.interact(entityPlayer);
        }
    }

    public EntityCow createChild(EntityAgeable entityAgeable) {
        return new EntityCow(this.worldObj);
    }
}
