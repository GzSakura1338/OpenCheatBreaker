package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Map;
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
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntitySheep extends EntityAnimal {
   public InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container() {
      @Override
      public boolean canInteractWith(EntityPlayer playerIn) {
         return false;
      }
   }, 2, 1);
   public static Map<EnumDyeColor, float[]> DYE_TO_RGB = Maps.newEnumMap(EnumDyeColor.class);
   public int sheepTimer;
   public EntityAIEatGrass entityAIEatGrass = new EntityAIEatGrass(this);

   public static float[] getDyeRgb(EnumDyeColor dyeColor) {
      return DYE_TO_RGB.get(dyeColor);
   }

   public EntitySheep(World worldIn) {
      super(worldIn);
      this.setSize(0.9F, 1.3F);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
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
   public void updateAITasks() {
      this.sheepTimer = this.entityAIEatGrass.getEatingGrassTimer();
      super.updateAITasks();
   }

   @Override
   public void onLivingUpdate() {
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
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      if (!this.getSheared()) {
         this.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, this.getFleeceColor().getMetadata()), 0.0F);
      }

      int n = this.rand.nextInt(2) + 1 + this.rand.nextInt(1 + lootingModifier);

      for (int i = 0; i < n; i++) {
         if (this.isBurning()) {
            this.dropItem(Items.cooked_mutton, 1);
         } else {
            this.dropItem(Items.mutton, 1);
         }
      }
   }

   @Override
   public Item getDropItem() {
      return Item.getItemFromBlock(Blocks.wool);
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 10) {
         this.sheepTimer = 40;
      } else {
         super.handleStatusUpdate(id);
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
   public boolean interact(EntityPlayer player) {
      ItemStack currentItem = player.inventory.getCurrentItem();
      if (currentItem != null && currentItem.getItem() == Items.shears && !this.getSheared() && !this.isChild()) {
         if (!this.worldObj.isRemote) {
            this.setSheared(true);
            int n = 1 + this.rand.nextInt(3);

            for (int i = 0; i < n; i++) {
               EntityItem entityDropItem = this.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, this.getFleeceColor().getMetadata()), 1.0F);
               entityDropItem.motionY = entityDropItem.motionY + this.rand.nextFloat() * 0.05F;
               entityDropItem.motionX = entityDropItem.motionX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
               entityDropItem.motionZ = entityDropItem.motionZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
            }
         }

         currentItem.damageItem(1, player);
         this.playSound("mob.sheep.shear", 1.0F, 1.0F);
      }

      return super.interact(player);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setBoolean("Sheared", this.getSheared());
      tagCompound.setByte("Color", (byte)this.getFleeceColor().getMetadata());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setSheared(tagCompund.getBoolean("Sheared"));
      this.setFleeceColor(EnumDyeColor.byMetadata(tagCompund.getByte("Color")));
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
   public void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound("mob.sheep.step", 0.15F, 1.0F);
   }

   public EnumDyeColor getFleeceColor() {
      return EnumDyeColor.byMetadata(this.dataWatcher.getWatchableObjectByte(16) & 15);
   }

   public void setFleeceColor(EnumDyeColor color) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
      this.dataWatcher.updateObject(16, (byte)(watchableObjectByte & 240 | color.getMetadata() & 15));
   }

   public boolean getSheared() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 16) != 0;
   }

   public void setSheared(boolean sheared) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
      if (sheared) {
         this.dataWatcher.updateObject(16, (byte)(watchableObjectByte | 16));
      } else {
         this.dataWatcher.updateObject(16, (byte)(watchableObjectByte & -17));
      }
   }

   public static EnumDyeColor getRandomSheepColor(Random random) {
      int nextInt = random.nextInt(100);
      return nextInt < 5
         ? EnumDyeColor.BLACK
         : (
            nextInt < 10
               ? EnumDyeColor.GRAY
               : (nextInt < 15 ? EnumDyeColor.SILVER : (nextInt < 18 ? EnumDyeColor.BROWN : (random.nextInt(500) == 0 ? EnumDyeColor.PINK : EnumDyeColor.WHITE)))
         );
   }

   public EntitySheep createChild(EntityAgeable ageable) {
      EntitySheep ageable2 = (EntitySheep)ageable;
      EntitySheep entitySheep = new EntitySheep(this.worldObj);
      entitySheep.setFleeceColor(this.getDyeColorMixFromParents(this, ageable2));
      return entitySheep;
   }

   @Override
   public void eatGrassBonus() {
      this.setSheared(false);
      if (this.isChild()) {
         this.addGrowth(60);
      }
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      this.setFleeceColor(getRandomSheepColor(this.worldObj.rand));
      return livingdata;
   }

   public EnumDyeColor getDyeColorMixFromParents(EntityAnimal father, EntityAnimal mother) {
      int dyeDamage = ((EntitySheep)father).getFleeceColor().getDyeDamage();
      int dyeDamage2 = ((EntitySheep)mother).getFleeceColor().getDyeDamage();
      this.inventoryCrafting.getStackInSlot(0).setItemDamage(dyeDamage);
      this.inventoryCrafting.getStackInSlot(1).setItemDamage(dyeDamage2);
      ItemStack findMatchingRecipe = CraftingManager.getInstance().findMatchingRecipe(this.inventoryCrafting, ((EntitySheep)father).worldObj);
      int n;
      if (findMatchingRecipe != null && findMatchingRecipe.getItem() == Items.dye) {
         n = findMatchingRecipe.getMetadata();
      } else {
         n = this.worldObj.rand.nextBoolean() ? dyeDamage : dyeDamage2;
      }

      return EnumDyeColor.byDyeDamage(n);
   }

   @Override
   public float getEyeHeight() {
      return 0.95F * this.height;
   }

   static {
      DYE_TO_RGB.put(EnumDyeColor.WHITE, new float[]{1.0F, 1.0F, 1.0F});
      DYE_TO_RGB.put(EnumDyeColor.ORANGE, new float[]{0.85F, 0.5F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.MAGENTA, new float[]{0.7F, 0.3F, 0.85F});
      DYE_TO_RGB.put(EnumDyeColor.LIGHT_BLUE, new float[]{0.4F, 0.6F, 0.85F});
      DYE_TO_RGB.put(EnumDyeColor.YELLOW, new float[]{0.9F, 0.9F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.LIME, new float[]{0.5F, 0.8F, 0.1F});
      DYE_TO_RGB.put(EnumDyeColor.PINK, new float[]{0.95F, 0.5F, 0.65F});
      DYE_TO_RGB.put(EnumDyeColor.GRAY, new float[]{0.3F, 0.3F, 0.3F});
      DYE_TO_RGB.put(EnumDyeColor.SILVER, new float[]{0.6F, 0.6F, 0.6F});
      DYE_TO_RGB.put(EnumDyeColor.CYAN, new float[]{0.3F, 0.5F, 0.6F});
      DYE_TO_RGB.put(EnumDyeColor.PURPLE, new float[]{0.5F, 0.25F, 0.7F});
      DYE_TO_RGB.put(EnumDyeColor.BLUE, new float[]{0.2F, 0.3F, 0.7F});
      DYE_TO_RGB.put(EnumDyeColor.BROWN, new float[]{0.4F, 0.3F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.GREEN, new float[]{0.4F, 0.5F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.RED, new float[]{0.6F, 0.2F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.BLACK, new float[]{0.1F, 0.1F, 0.1F});
   }
}
