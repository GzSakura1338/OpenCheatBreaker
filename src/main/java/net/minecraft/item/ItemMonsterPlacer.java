package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemMonsterPlacer extends Item {
   public ItemMonsterPlacer() {
      this.setHasSubtypes(true);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public String getItemStackDisplayName(ItemStack stack) {
      String trim = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
      String stringFromID = EntityList.getStringFromID(stack.getMetadata());
      if (stringFromID != null) {
         trim = trim + " " + StatCollector.translateToLocal("entity." + stringFromID + ".name");
      }

      return trim;
   }

   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      EntityEggInfo entityEggInfo = EntityList.entityEggs.get(stack.getMetadata());
      return entityEggInfo != null ? (renderPass == 0 ? entityEggInfo.primaryColor : entityEggInfo.secondaryColor) : 16777215;
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
         return false;
      }

      IBlockState blockState = worldIn.getBlockState(pos);
      if (blockState.getBlock() == Blocks.mob_spawner) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof TileEntityMobSpawner) {
            MobSpawnerBaseLogic spawnerBaseLogic = ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic();
            spawnerBaseLogic.setEntityName(EntityList.getStringFromID(stack.getMetadata()));
            tileEntity.markDirty();
            worldIn.markBlockForUpdate(pos);
            if (!playerIn.capabilities.isCreativeMode) {
               stack.stackSize--;
            }

            return true;
         }
      }

      pos = pos.offset(side);
      double d = 0.0;
      if (side == EnumFacing.UP && blockState instanceof BlockFence) {
         d = 0.5;
      }

      Entity spawnCreature2 = spawnCreature(worldIn, stack.getMetadata(), pos.getX() + 0.5, pos.getY() + d, pos.getZ() + 0.5);
      if (spawnCreature2 != null) {
         if (spawnCreature2 instanceof EntityLivingBase && stack.hasDisplayName()) {
            spawnCreature2.setCustomNameTag(stack.getDisplayName());
         }

         if (!playerIn.capabilities.isCreativeMode) {
            stack.stackSize--;
         }
      }

      return true;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      if (worldIn.isRemote) {
         return itemStackIn;
      }

      MovingObjectPosition movingObjectPositionFromPlayer = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, true);
      if (movingObjectPositionFromPlayer == null) {
         return itemStackIn;
      }

      if (movingObjectPositionFromPlayer.typeOfHit == MovingObjectType.BLOCK) {
         BlockPos blockPos = movingObjectPositionFromPlayer.getBlockPos();
         if (!worldIn.isBlockModifiable(playerIn, blockPos)) {
            return itemStackIn;
         }

         if (!playerIn.canPlayerEdit(blockPos, movingObjectPositionFromPlayer.sideHit, itemStackIn)) {
            return itemStackIn;
         }

         if (worldIn.getBlockState(blockPos).getBlock() instanceof BlockLiquid) {
            Entity spawnCreature2 = spawnCreature(worldIn, itemStackIn.getMetadata(), blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
            if (spawnCreature2 != null) {
               if (spawnCreature2 instanceof EntityLivingBase && itemStackIn.hasDisplayName()) {
                  ((EntityLiving)spawnCreature2).setCustomNameTag(itemStackIn.getDisplayName());
               }

               if (!playerIn.capabilities.isCreativeMode) {
                  itemStackIn.stackSize--;
               }

               playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
            }
         }
      }

      return itemStackIn;
   }

   public static Entity spawnCreature(World worldIn, int entityID, double x, double y, double z) {
      if (!EntityList.entityEggs.containsKey(entityID)) {
         return null;
      }

      Entity entity = null;

      for (int i = 0; i < 1; i++) {
         entity = EntityList.createEntityByID(entityID, worldIn);
         if (entity instanceof EntityLivingBase) {
            EntityLiving entityLiving = (EntityLiving)entity;
            entity.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(worldIn.rand.nextFloat() * 360.0F), 0.0F);
            entityLiving.rotationYawHead = entityLiving.rotationYaw;
            entityLiving.renderYawOffset = entityLiving.rotationYaw;
            entityLiving.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityLiving)), (IEntityLivingData)null);
            worldIn.spawnEntityInWorld(entity);
            entityLiving.playLivingSound();
         }
      }

      return entity;
   }

   @Override
   public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
      for (EntityEggInfo entityEggInfo : EntityList.entityEggs.values()) {
         subItems.add(new ItemStack(itemIn, 1, entityEggInfo.spawnedID));
      }
   }
}
