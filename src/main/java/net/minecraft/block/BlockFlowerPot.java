package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFlowerPot extends BlockContainer {
   public static PropertyInteger LEGACY_DATA = PropertyInteger.create("legacy_data", 0, 15);
   public static PropertyEnum<BlockFlowerPot.EnumFlowerType> CONTENTS = PropertyEnum.create("contents", BlockFlowerPot.EnumFlowerType.class);

   public BlockFlowerPot() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(CONTENTS, BlockFlowerPot.EnumFlowerType.EMPTY).withProperty(LEGACY_DATA, 0));
      this.setBlockBoundsForItemRender();
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal("item.flowerPot.name");
   }

   @Override
   public void setBlockBoundsForItemRender() {
      float f = 0.375F;
      float f2 = f / 2.0F;
      this.setBlockBounds(0.5F - f2, 0.0F, 0.5F - f2, 0.5F + f2, f, 0.5F + f2);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityFlowerPot) {
         Item flowerPotItem = ((TileEntityFlowerPot)tileEntity).getFlowerPotItem();
         if (flowerPotItem instanceof ItemBlock) {
            return Block.getBlockFromItem(flowerPotItem).colorMultiplier(worldIn, pos, renderPass);
         }
      }

      return 16777215;
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack currentItem = playerIn.inventory.getCurrentItem();
      if (currentItem != null && currentItem.getItem() instanceof ItemBlock) {
         TileEntityFlowerPot tileEntity = this.getTileEntity(worldIn, pos);
         if (tileEntity == null) {
            return false;
         }

         if (tileEntity.getFlowerPotItem() != null) {
            return false;
         }

         Block blockFromItem = Block.getBlockFromItem(currentItem.getItem());
         if (!this.canNotContain(blockFromItem, currentItem.getMetadata())) {
            return false;
         }

         tileEntity.setFlowerPotData(currentItem.getItem(), currentItem.getMetadata());
         tileEntity.markDirty();
         worldIn.markBlockForUpdate(pos);
         playerIn.triggerAchievement(StatList.FLOWER_POTTED);
         if (!playerIn.capabilities.isCreativeMode && --currentItem.stackSize <= 0) {
            playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, (ItemStack)null);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean canNotContain(Block blockIn, int meta) {
      return blockIn != Blocks.yellow_flower
            && blockIn != Blocks.red_flower
            && blockIn != Blocks.cactus
            && blockIn != Blocks.brown_mushroom
            && blockIn != Blocks.red_mushroom
            && blockIn != Blocks.sapling
            && blockIn != Blocks.deadbush
         ? blockIn == Blocks.tallgrass && meta == BlockTallGrass.EnumType.FERN.getMeta()
         : true;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      TileEntityFlowerPot tileEntity = this.getTileEntity(worldIn, pos);
      return tileEntity != null && tileEntity.getFlowerPotItem() != null ? tileEntity.getFlowerPotItem() : Items.flower_pot;
   }

   @Override
   public int getDamageValue(World worldIn, BlockPos pos) {
      TileEntityFlowerPot tileEntity = this.getTileEntity(worldIn, pos);
      return tileEntity != null && tileEntity.getFlowerPotItem() != null ? tileEntity.getFlowerPotData() : 0;
   }

   @Override
   public boolean isFlowerPot() {
      return true;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return super.canPlaceBlockAt(worldIn, pos) && World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down())) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntityFlowerPot tileEntity = this.getTileEntity(worldIn, pos);
      if (tileEntity != null && tileEntity.getFlowerPotItem() != null) {
         spawnAsEntity(worldIn, pos, new ItemStack(tileEntity.getFlowerPotItem(), 1, tileEntity.getFlowerPotData()));
      }

      super.breakBlock(worldIn, pos, state);
   }

   @Override
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      super.onBlockHarvested(worldIn, pos, state, player);
      if (player.capabilities.isCreativeMode) {
         TileEntityFlowerPot tileEntity = this.getTileEntity(worldIn, pos);
         if (tileEntity != null) {
            tileEntity.setFlowerPotData((Item)null, 0);
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.flower_pot;
   }

   public TileEntityFlowerPot getTileEntity(World worldIn, BlockPos pos) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      return tileEntity instanceof TileEntityFlowerPot ? (TileEntityFlowerPot)tileEntity : null;
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      Block block = null;
      int n = 0;
      switch (meta) {
         case 1:
            block = Blocks.red_flower;
            n = BlockFlower.EnumFlowerType.POPPY.getMeta();
            break;
         case 2:
            block = Blocks.yellow_flower;
            break;
         case 3:
            block = Blocks.sapling;
            n = BlockPlanks.EnumType.OAK.getMetadata();
            break;
         case 4:
            block = Blocks.sapling;
            n = BlockPlanks.EnumType.SPRUCE.getMetadata();
            break;
         case 5:
            block = Blocks.sapling;
            n = BlockPlanks.EnumType.BIRCH.getMetadata();
            break;
         case 6:
            block = Blocks.sapling;
            n = BlockPlanks.EnumType.JUNGLE.getMetadata();
            break;
         case 7:
            block = Blocks.red_mushroom;
            break;
         case 8:
            block = Blocks.brown_mushroom;
            break;
         case 9:
            block = Blocks.cactus;
            break;
         case 10:
            block = Blocks.deadbush;
            break;
         case 11:
            block = Blocks.tallgrass;
            n = BlockTallGrass.EnumType.FERN.getMeta();
            break;
         case 12:
            block = Blocks.sapling;
            n = BlockPlanks.EnumType.ACACIA.getMetadata();
            break;
         case 13:
            block = Blocks.sapling;
            n = BlockPlanks.EnumType.DARK_OAK.getMetadata();
      }

      return new TileEntityFlowerPot(Item.getItemFromBlock(block), n);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, CONTENTS, LEGACY_DATA);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(LEGACY_DATA);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      BlockFlowerPot.EnumFlowerType enumFlowerType = BlockFlowerPot.EnumFlowerType.EMPTY;
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityFlowerPot) {
         TileEntityFlowerPot tileEntityFlowerPot = (TileEntityFlowerPot)tileEntity;
         Item flowerPotItem = tileEntityFlowerPot.getFlowerPotItem();
         if (flowerPotItem instanceof ItemBlock) {
            int flowerPotData = tileEntityFlowerPot.getFlowerPotData();
            Block blockFromItem = Block.getBlockFromItem(flowerPotItem);
            if (blockFromItem == Blocks.sapling) {
               switch (BlockPlanks.EnumType.byMetadata(flowerPotData)) {
                  case OAK:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.OAK_SAPLING;
                     break;
                  case SPRUCE:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.SPRUCE_SAPLING;
                     break;
                  case BIRCH:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.BIRCH_SAPLING;
                     break;
                  case JUNGLE:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.JUNGLE_SAPLING;
                     break;
                  case ACACIA:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.ACACIA_SAPLING;
                     break;
                  case DARK_OAK:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.DARK_OAK_SAPLING;
                     break;
                  default:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.EMPTY;
               }
            } else if (blockFromItem == Blocks.tallgrass) {
               switch (flowerPotData) {
                  case 0:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.DEAD_BUSH;
                     break;
                  case 2:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.FERN;
                     break;
                  default:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.EMPTY;
               }
            } else if (blockFromItem == Blocks.yellow_flower) {
               enumFlowerType = BlockFlowerPot.EnumFlowerType.DANDELION;
            } else if (blockFromItem == Blocks.red_flower) {
               switch (BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.RED, flowerPotData)) {
                  case POPPY:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.POPPY;
                     break;
                  case BLUE_ORCHID:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.BLUE_ORCHID;
                     break;
                  case ALLIUM:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.ALLIUM;
                     break;
                  case HOUSTONIA:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.HOUSTONIA;
                     break;
                  case RED_TULIP:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.RED_TULIP;
                     break;
                  case ORANGE_TULIP:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.ORANGE_TULIP;
                     break;
                  case WHITE_TULIP:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.WHITE_TULIP;
                     break;
                  case PINK_TULIP:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.PINK_TULIP;
                     break;
                  case OXEYE_DAISY:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.OXEYE_DAISY;
                     break;
                  default:
                     enumFlowerType = BlockFlowerPot.EnumFlowerType.EMPTY;
               }
            } else if (blockFromItem == Blocks.red_mushroom) {
               enumFlowerType = BlockFlowerPot.EnumFlowerType.MUSHROOM_RED;
            } else if (blockFromItem == Blocks.brown_mushroom) {
               enumFlowerType = BlockFlowerPot.EnumFlowerType.MUSHROOM_BROWN;
            } else if (blockFromItem == Blocks.deadbush) {
               enumFlowerType = BlockFlowerPot.EnumFlowerType.DEAD_BUSH;
            } else if (blockFromItem == Blocks.cactus) {
               enumFlowerType = BlockFlowerPot.EnumFlowerType.CACTUS;
            }
         }
      }

      return state.withProperty(CONTENTS, enumFlowerType);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public enum EnumFlowerType implements IStringSerializable {
      EMPTY("empty"),
      POPPY("rose"),
      BLUE_ORCHID("blue_orchid"),
      ALLIUM("allium"),
      HOUSTONIA("houstonia"),
      RED_TULIP("red_tulip"),
      ORANGE_TULIP("orange_tulip"),
      WHITE_TULIP("white_tulip"),
      PINK_TULIP("pink_tulip"),
      OXEYE_DAISY("oxeye_daisy"),
      DANDELION("dandelion"),
      OAK_SAPLING("oak_sapling"),
      SPRUCE_SAPLING("spruce_sapling"),
      BIRCH_SAPLING("birch_sapling"),
      JUNGLE_SAPLING("jungle_sapling"),
      ACACIA_SAPLING("acacia_sapling"),
      DARK_OAK_SAPLING("dark_oak_sapling"),
      MUSHROOM_RED("mushroom_red"),
      MUSHROOM_BROWN("mushroom_brown"),
      DEAD_BUSH("dead_bush"),
      FERN("fern"),
      CACTUS("cactus");

      public String name;

      EnumFlowerType(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }

      @Override
      public String getName() {
         return this.name;
      }
   }
}
