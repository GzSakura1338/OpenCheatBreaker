package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemLead;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFence extends Block {
   public static PropertyBool NORTH = PropertyBool.create("north");
   public static PropertyBool EAST = PropertyBool.create("east");
   public static PropertyBool SOUTH = PropertyBool.create("south");
   public static PropertyBool WEST = PropertyBool.create("west");

   public BlockFence(Material materialIn) {
      this(materialIn, materialIn.getMaterialMapColor());
   }

   public BlockFence(Material material, MapColor mapColor) {
      super(material, mapColor);
      this.setDefaultState(
         this.blockState.getBaseState().withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false)
      );
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
      boolean canConnectTo = this.canConnectTo(worldIn, pos.north());
      boolean canConnectTo2 = this.canConnectTo(worldIn, pos.south());
      boolean canConnectTo3 = this.canConnectTo(worldIn, pos.west());
      boolean canConnectTo4 = this.canConnectTo(worldIn, pos.east());
      float f = 0.375F;
      float f2 = 0.625F;
      float f3 = 0.375F;
      float f4 = 0.625F;
      if (canConnectTo) {
         f3 = 0.0F;
      }

      if (canConnectTo2) {
         f4 = 1.0F;
      }

      if (canConnectTo || canConnectTo2) {
         this.setBlockBounds(f, 0.0F, f3, f2, 1.5F, f4);
         super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      }

      f3 = 0.375F;
      f4 = 0.625F;
      if (canConnectTo3) {
         f = 0.0F;
      }

      if (canConnectTo4) {
         f2 = 1.0F;
      }

      if (canConnectTo3 || canConnectTo4 || !canConnectTo && !canConnectTo2) {
         this.setBlockBounds(f, 0.0F, f3, f2, 1.5F, f4);
         super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      }

      if (canConnectTo) {
         f3 = 0.0F;
      }

      if (canConnectTo2) {
         f4 = 1.0F;
      }

      this.setBlockBounds(f, 0.0F, f3, f2, 1.0F, f4);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      boolean canConnectTo = this.canConnectTo(worldIn, pos.north());
      boolean canConnectTo2 = this.canConnectTo(worldIn, pos.south());
      boolean canConnectTo3 = this.canConnectTo(worldIn, pos.west());
      boolean canConnectTo4 = this.canConnectTo(worldIn, pos.east());
      float f = 0.375F;
      float f2 = 0.625F;
      float f3 = 0.375F;
      float f4 = 0.625F;
      if (canConnectTo) {
         f3 = 0.0F;
      }

      if (canConnectTo2) {
         f4 = 1.0F;
      }

      if (canConnectTo3) {
         f = 0.0F;
      }

      if (canConnectTo4) {
         f2 = 1.0F;
      }

      this.setBlockBounds(f, 0.0F, f3, f2, 1.0F, f4);
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
   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return false;
   }

   public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos).getBlock();
      return block == Blocks.barrier
         ? false
         : (
            (!(block instanceof BlockFence) || block.blockMaterial != this.blockMaterial) && !(block instanceof BlockFenceGate)
               ? (block.blockMaterial.isOpaque() && block.isFullCube() ? block.blockMaterial != Material.gourd : false)
               : true
         );
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return true;
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      return worldIn.isRemote ? true : ItemLead.attachToFence(playerIn, worldIn, pos);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return 0;
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(NORTH, this.canConnectTo(worldIn, pos.north()))
         .withProperty(EAST, this.canConnectTo(worldIn, pos.east()))
         .withProperty(SOUTH, this.canConnectTo(worldIn, pos.south()))
         .withProperty(WEST, this.canConnectTo(worldIn, pos.west()));
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, NORTH, EAST, WEST, SOUTH);
   }
}
