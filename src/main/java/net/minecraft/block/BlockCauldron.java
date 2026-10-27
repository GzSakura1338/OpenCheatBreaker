package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCauldron extends Block {
   public static PropertyInteger LEVEL = PropertyInteger.create("level", 0, 3);

   public BlockCauldron() {
      super(Material.iron, MapColor.stoneColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
   }

   @Override
   public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      float f = 0.125F;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      this.setBlockBoundsForItemRender();
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      int value = state.getValue(LEVEL);
      float f = pos.getY() + (6.0F + 3 * value) / 16.0F;
      if (!worldIn.isRemote && entityIn.isBurning() && value > 0 && entityIn.getEntityBoundingBox().minY <= f) {
         entityIn.extinguish();
         this.setWaterLevel(worldIn, pos, state, value - 1);
      }
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      ItemStack currentItem = playerIn.inventory.getCurrentItem();
      if (currentItem == null) {
         return true;
      }

      int value = state.getValue(LEVEL);
      Item item = currentItem.getItem();
      if (item == Items.water_bucket) {
         if (value < 3) {
            if (!playerIn.capabilities.isCreativeMode) {
               playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, new ItemStack(Items.bucket));
            }

            playerIn.triggerAchievement(StatList.CAULDRONS_FILLED);
            this.setWaterLevel(worldIn, pos, state, 3);
         }

         return true;
      } else if (item == Items.glass_bottle) {
         if (value > 0) {
            if (!playerIn.capabilities.isCreativeMode) {
               ItemStack itemStack = new ItemStack(Items.potionitem, 1, 0);
               if (!playerIn.inventory.addItemStackToInventory(itemStack)) {
                  worldIn.spawnEntityInWorld(new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, itemStack));
               } else if (playerIn instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)playerIn).sendContainerToPlayer(playerIn.inventoryContainer);
               }

               playerIn.triggerAchievement(StatList.CAULDRONS_USED);
               currentItem.stackSize--;
               if (currentItem.stackSize <= 0) {
                  playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, (ItemStack)null);
               }
            }

            this.setWaterLevel(worldIn, pos, state, value - 1);
         }

         return true;
      } else {
         if (value > 0 && item instanceof ItemArmor) {
            ItemArmor itemArmor = (ItemArmor)item;
            if (itemArmor.getArmorMaterial() == ArmorMaterial.LEATHER && itemArmor.hasColor(currentItem)) {
               itemArmor.removeColor(currentItem);
               this.setWaterLevel(worldIn, pos, state, value - 1);
               playerIn.triggerAchievement(StatList.ARMOR_CLEANED);
               return true;
            }
         }

         if (value > 0 && item instanceof ItemBanner && TileEntityBanner.getPatterns(currentItem) > 0) {
            ItemStack itemStack = currentItem.copy();
            itemStack.stackSize = 1;
            TileEntityBanner.removeBannerData(itemStack);
            if (currentItem.stackSize <= 1 && !playerIn.capabilities.isCreativeMode) {
               playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, itemStack);
            } else {
               if (!playerIn.inventory.addItemStackToInventory(itemStack)) {
                  worldIn.spawnEntityInWorld(new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, itemStack));
               } else if (playerIn instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)playerIn).sendContainerToPlayer(playerIn.inventoryContainer);
               }

               playerIn.triggerAchievement(StatList.BANNER_CLEANED);
               if (!playerIn.capabilities.isCreativeMode) {
                  currentItem.stackSize--;
               }
            }

            if (!playerIn.capabilities.isCreativeMode) {
               this.setWaterLevel(worldIn, pos, state, value - 1);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public void setWaterLevel(World worldIn, BlockPos pos, IBlockState state, int level) {
      worldIn.setBlockState(pos, state.withProperty(LEVEL, MathHelper.clamp_int(level, 0, 3)), 2);
      worldIn.updateComparatorOutputLevel(pos, this);
   }

   @Override
   public void fillWithRain(World worldIn, BlockPos pos) {
      if (worldIn.rand.nextInt(20) == 1) {
         IBlockState blockState = worldIn.getBlockState(pos);
         if (blockState.getValue(LEVEL) < 3) {
            worldIn.setBlockState(pos, blockState.cycleProperty(LEVEL), 2);
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.cauldron;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.cauldron;
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   public int getComparatorInputOverride(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).getValue(LEVEL);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(LEVEL, meta);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(LEVEL);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, LEVEL);
   }
}
