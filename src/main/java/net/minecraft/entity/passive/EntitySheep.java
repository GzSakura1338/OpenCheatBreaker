package net.minecraft.entity.passive;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySheep extends EntityAnimal {
    public InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container() {
        @Override
        public boolean canInteractWith(EntityPlayer player) {
            return false;
        }
    }, 2, 1);
    public static float[][] fleeceColorTable = new float[][]{
        {1.0F, 1.0F, 1.0F},
        {0.85F, 0.5F, 0.2F},
        {0.7F, 0.3F, 0.85F},
        {0.4F, 0.6F, 0.85F},
        {0.9F, 0.9F, 0.2F},
        {0.5F, 0.8F, 0.1F},
        {0.95F, 0.5F, 0.65F},
        {0.3F, 0.3F, 0.3F},
        {0.6F, 0.6F, 0.6F},
        {0.3F, 0.5F, 0.6F},
        {0.5F, 0.25F, 0.7F},
        {0.2F, 0.3F, 0.7F},
        {0.4F, 0.3F, 0.2F},
        {0.4F, 0.5F, 0.2F},
        {0.6F, 0.2F, 0.2F},
        {0.1F, 0.1F, 0.1F}
    };
    public int sheepTimer;
    public EntityAIEatGrass entityAIEatGrass = new EntityAIEatGrass(this);

    public EntitySheep(World world) {
        super(world);
        this.setSize(0.9F, 1.3F);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0));
        this.tasks.addTask(3, new EntityAITempt(this, 1.1, Items.wheat, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1));
        this.tasks.addTask(5, this.entityAIEatGrass);
        this.tasks.addTask(6, new EntityAIWander(this, 1.0));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.inventoryCrafting.setInventorySlotContents(0, new ItemStack(Items.dye, 1, 0));
        this.inventoryCrafting.setInventorySlotContents(1, new ItemStack(Items.dye, 1, 0));
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public void updateAITasks() {
        this.sheepTimer = this.entityAIEatGrass.getEatingGrassTimer();
        super.updateAITasks();
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        if (this.worldObj.isRemote) {
            this.sheepTimer = Math.max(0, this.sheepTimer - 1);
        }

        super.onLivingUpdate();
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23F);
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)0));
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        if (!this.getSheared()) {
            this.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, this.getFleeceColor()), 0.0F);
        }
    }

    @Override
    public Item getDropItem() {
        return Item.getItemFromBlock(Blocks.wool);
    }

    @Override
    public void handleHealthUpdate(byte b) {
        if (b == 10) {
            this.sheepTimer = 40;
        } else {
            super.handleHealthUpdate(b);
        }
    }

    public float getHeadRotationPointY(float f) {
        return this.sheepTimer <= 0
            ? 0.0F
            : (
                this.sheepTimer >= 4 && this.sheepTimer <= 36
                    ? 1.0F
                    : (this.sheepTimer < 4 ? (this.sheepTimer - f) / 4.0F : -(this.sheepTimer - 40 - f) / 4.0F)
            );
    }

    public float getHeadRotationAngleX(float f) {
        if (this.sheepTimer > 4 && this.sheepTimer <= 36) {
            float f2 = (this.sheepTimer - 4 - f) / 32.0F;
            return (float) (Math.PI / 5) + 0.2199115F * MathHelper.sin(f2 * 28.7F);
        } else {
            return this.sheepTimer > 0 ? (float) (Math.PI / 5) : this.rotationPitch / (180.0F / (float)Math.PI);
        }
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer) throws java.io.IOException {
        ItemStack currentItem = entityPlayer.inventory.getCurrentItem();
        if (currentItem != null && currentItem.getItem() == Items.shears && !this.getSheared() && !this.isChild()) {
            if (!this.worldObj.isRemote) {
                this.setSheared(true);
                int n = 1 + this.rand.nextInt(3);

                for (int i = 0; i < n; i++) {
                    EntityItem entityDropItem = this.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, this.getFleeceColor()), 1.0F);
                    entityDropItem.motionY = entityDropItem.motionY + this.rand.nextFloat() * 0.05F;
                    entityDropItem.motionX = entityDropItem.motionX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                    entityDropItem.motionZ = entityDropItem.motionZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                }
            }

            currentItem.damageItem(1, entityPlayer);
            this.playSound("mob.sheep.shear", 1.0F, 1.0F);
        }

        return super.interact(entityPlayer);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("Sheared", this.getSheared());
        tagCompound.setByte("Color", (byte)this.getFleeceColor());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.setSheared(tagCompund.getBoolean("Sheared"));
        this.setFleeceColor(tagCompund.getByte("Color"));
    }

    @Override
    public String getLivingSound() {
        return "mob.sheep.say";
    }

    @Override
    public String getHurtSound() {
        return "mob.sheep.say";
    }

    @Override
    public String getDeathSound() {
        return "mob.sheep.say";
    }

    @Override
    public void playStepSound(int x, int y, int z, Block blockIn) {
        this.playSound("mob.sheep.step", 0.15F, 1.0F);
    }

    public int getFleeceColor() {
        return this.dataWatcher.getWatchableObjectByte(16) & 15;
    }

    public void setFleeceColor(int n) {
        byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
        this.dataWatcher.updateObject(16, (byte)(watchableObjectByte & 240 | n & 15));
    }

    public boolean getSheared() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 16) != 0;
    }

    public void setSheared(boolean flag) {
        byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
        if (flag) {
            this.dataWatcher.updateObject(16, (byte)(watchableObjectByte | 16));
        } else {
            this.dataWatcher.updateObject(16, (byte)(watchableObjectByte & -17));
        }
    }

    public static int getRandomFleeceColor(Random random) {
        int nextInt = random.nextInt(100);
        return nextInt < 5 ? 15 : (nextInt < 10 ? 7 : (nextInt < 15 ? 8 : (nextInt < 18 ? 12 : (random.nextInt(500) == 0 ? 6 : 0))));
    }

    public EntitySheep createChild(EntityAgeable entityAgeable) {
        EntitySheep entitySheep = (EntitySheep)entityAgeable;
        EntitySheep entitySheep2 = new EntitySheep(this.worldObj);
        int dyeBasedOnParents = this.getDyeBasedOnParents(this, entitySheep);
        entitySheep2.setFleeceColor(15 - dyeBasedOnParents);
        return entitySheep2;
    }

    @Override
    public void eatGrassBonus() {
        this.setSheared(false);
        if (this.isChild()) {
            this.addGrowth(60);
        }
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityLivingData) {
        entityLivingData = super.onSpawnWithEgg(entityLivingData);
        this.setFleeceColor(getRandomFleeceColor(this.worldObj.rand));
        return entityLivingData;
    }

    public int getDyeBasedOnParents(EntityAnimal entityAnimal, EntityAnimal entityAnimal2) {
        int dyeFromFleeceColor = this.getDyeFromFleeceColor(entityAnimal);
        int dyeFromFleeceColor2 = this.getDyeFromFleeceColor(entityAnimal2);
        this.inventoryCrafting.getStackInSlot(0).setMetadata(dyeFromFleeceColor);
        this.inventoryCrafting.getStackInSlot(1).setMetadata(dyeFromFleeceColor2);
        ItemStack findMatchingRecipe = CraftingManager.getInstance().findMatchingRecipe(this.inventoryCrafting, entityAnimal.worldObj);
        int n;
        if (findMatchingRecipe != null && findMatchingRecipe.getItem() == Items.dye) {
            n = findMatchingRecipe.getMetadata();
        } else {
            n = this.worldObj.rand.nextBoolean() ? dyeFromFleeceColor : dyeFromFleeceColor2;
        }

        return n;
    }

    public int getDyeFromFleeceColor(EntityAnimal entityAnimal) {
        return 15 - ((EntitySheep)entityAnimal).getFleeceColor();
    }
}
