package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BlockBed extends BlockDirectional {
   public static PropertyEnum<BlockBed.EnumPartType> PART = PropertyEnum.create("part", BlockBed.EnumPartType.class);
   public static PropertyBool OCCUPIED = PropertyBool.create("occupied");

   public BlockBed() {
      super(Material.cloth);
      this.setDefaultState(this.blockState.getBaseState().withProperty(PART, BlockBed.EnumPartType.FOOT).withProperty(OCCUPIED, false));
      this.setBedBounds();
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      if (state.getValue(PART) != BlockBed.EnumPartType.HEAD) {
         pos = pos.offset(state.getValue(FACING));
         state = worldIn.getBlockState(pos);
         if (state.getBlock() != this) {
            return true;
         }
      }

      if (worldIn.provider.canRespawnHere() && worldIn.getBiomeGenForCoords(pos) != BiomeGenBase.hell) {
         if (state.getValue(OCCUPIED)) {
            EntityPlayer playerInBed = this.getPlayerInBed(worldIn, pos);
            if (playerInBed != null) {
               playerIn.addChatComponentMessage(new ChatComponentTranslation("tile.bed.occupied"));
               return true;
            }

            state = state.withProperty(OCCUPIED, false);
            worldIn.setBlockState(pos, state, 4);
         }

         EnumStatus trySleep = playerIn.trySleep(pos);
         if (trySleep == EnumStatus.OK) {
            state = state.withProperty(OCCUPIED, true);
            worldIn.setBlockState(pos, state, 4);
            return true;
         }

         if (trySleep == EnumStatus.NOT_POSSIBLE_NOW) {
            playerIn.addChatComponentMessage(new ChatComponentTranslation("tile.bed.noSleep"));
         } else if (trySleep == EnumStatus.NOT_SAFE) {
            playerIn.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe"));
         }

         return true;
      } else {
         worldIn.setBlockToAir(pos);
         BlockPos offset = pos.offset(state.getValue(FACING).getOpposite());
         if (worldIn.getBlockState(offset).getBlock() == this) {
            worldIn.setBlockToAir(offset);
         }

         worldIn.newExplosion((Entity)null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5.0F, true, true);
         return true;
      }
   }

   public EntityPlayer getPlayerInBed(World worldIn, BlockPos pos) {
      for (EntityPlayer entityPlayer : worldIn.playerEntities) {
         if (entityPlayer.isPlayerSleeping() && entityPlayer.playerLocation.equals(pos)) {
            return entityPlayer;
         }
      }

      return null;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      this.setBedBounds();
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      EnumFacing value = state.getValue(FACING);
      if (state.getValue(PART) == BlockBed.EnumPartType.HEAD) {
         if (worldIn.getBlockState(pos.offset(value.getOpposite())).getBlock() != this) {
            worldIn.setBlockToAir(pos);
         }
      } else if (worldIn.getBlockState(pos.offset(value)).getBlock() != this) {
         worldIn.setBlockToAir(pos);
         if (!worldIn.isRemote) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return state.getValue(PART) == BlockBed.EnumPartType.HEAD ? null : Items.bed;
   }

   public void setBedBounds() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
   }

   public static BlockPos getSafeExitLocation(World worldIn, BlockPos pos, int tries) {
      EnumFacing value = worldIn.getBlockState(pos).getValue(FACING);
      int n = pos.getX();
      int n2 = pos.getY();
      int n3 = pos.getZ();

      for (int i = 0; i <= 1; i++) {
         int n4 = n - value.getFrontOffsetX() * i - 1;
         int n5 = n3 - value.getFrontOffsetZ() * i - 1;
         int n6 = n4 + 2;
         int n7 = n5 + 2;

         for (int j = n4; j <= n6; j++) {
            for (int k = n5; k <= n7; k++) {
               BlockPos blockPos = new BlockPos(j, n2, k);
               if (hasRoomForPlayer(worldIn, blockPos)) {
                  if (tries <= 0) {
                     return blockPos;
                  }

                  tries--;
               }
            }
         }
      }

      return null;
   }

   public static boolean hasRoomForPlayer(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down())
         && !worldIn.getBlockState(pos).getBlock().getMaterial().isSolid()
         && !worldIn.getBlockState(pos.up()).getBlock().getMaterial().isSolid();
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      if (state.getValue(PART) == BlockBed.EnumPartType.FOOT) {
         super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
      }
   }

   @Override
   public int getMobilityFlag() {
      return 1;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.bed;
   }

   @Override
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (player.capabilities.isCreativeMode && state.getValue(PART) == BlockBed.EnumPartType.HEAD) {
         BlockPos offset = pos.offset(state.getValue(FACING).getOpposite());
         if (worldIn.getBlockState(offset).getBlock() == this) {
            worldIn.setBlockToAir(offset);
         }
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      EnumFacing horizontal = EnumFacing.getHorizontal(meta);
      return (meta & 8) > 0
         ? this.getDefaultState().withProperty(PART, BlockBed.EnumPartType.HEAD).withProperty(FACING, horizontal).withProperty(OCCUPIED, (meta & 4) > 0)
         : this.getDefaultState().withProperty(PART, BlockBed.EnumPartType.FOOT).withProperty(FACING, horizontal);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      if (state.getValue(PART) == BlockBed.EnumPartType.FOOT) {
         IBlockState blockState = worldIn.getBlockState(pos.offset(state.getValue(FACING)));
         if (blockState.getBlock() == this) {
            state = state.withProperty(OCCUPIED, blockState.getValue(OCCUPIED));
         }
      }

      return state;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      n |= state.getValue(FACING).getHorizontalIndex();
      if (state.getValue(PART) == BlockBed.EnumPartType.HEAD) {
         n |= 8;
         if (state.getValue(OCCUPIED)) {
            n |= 4;
         }
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, PART, OCCUPIED);
   }

   public enum EnumPartType implements IStringSerializable {
      HEAD("head"),
      FOOT("foot");

      public String name;

      EnumPartType(String name) {
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
