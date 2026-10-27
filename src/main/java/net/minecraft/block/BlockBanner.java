package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.StatCollector;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBanner extends BlockContainer {
   public static PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
   public static PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 15);

   public BlockBanner() {
      super(Material.wood);
      float f = 0.25F;
      float f2 = 1.0F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f2, 0.5F + f);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal("item.banner.white.name");
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getSelectedBoundingBox(worldIn, pos);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean canSpawnInBlock() {
      return true;
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityBanner();
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.banner;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.banner;
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityBanner) {
         ItemStack itemStack = new ItemStack(Items.banner, 1, ((TileEntityBanner)tileEntity).getBaseColor());
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         tileEntity.writeToNBT(nbttagCompound);
         nbttagCompound.removeTag("x");
         nbttagCompound.removeTag("y");
         nbttagCompound.removeTag("z");
         nbttagCompound.removeTag("id");
         itemStack.setTagInfo("BlockEntityTag", nbttagCompound);
         spawnAsEntity(worldIn, pos, itemStack);
      } else {
         super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
      }
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return !this.hasInvalidNeighbor(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos);
   }

   @Override
   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
      if (te instanceof TileEntityBanner) {
         TileEntityBanner te2 = (TileEntityBanner)te;
         ItemStack itemStack = new ItemStack(Items.banner, 1, ((TileEntityBanner)te).getBaseColor());
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         TileEntityBanner.setBaseColorAndPatterns(nbttagCompound, te2.getBaseColor(), te2.getPatterns());
         itemStack.setTagInfo("BlockEntityTag", nbttagCompound);
         spawnAsEntity(worldIn, pos, itemStack);
      } else {
         super.harvestBlock(worldIn, player, pos, state, (TileEntity)null);
      }
   }

   public static class BlockBannerHanging extends BlockBanner {
      public BlockBannerHanging() {
         this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      }

      @Override
      public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
         EnumFacing value = worldIn.getBlockState(pos).getValue(FACING);
         float f = 0.0F;
         float f2 = 0.78125F;
         float f3 = 0.0F;
         float f4 = 1.0F;
         float f5 = 0.125F;
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
         switch (value) {
            case NORTH:
            default:
               this.setBlockBounds(f3, f, 1.0F - f5, f4, f2, 1.0F);
               break;
            case SOUTH:
               this.setBlockBounds(f3, f, 0.0F, f4, f2, f5);
               break;
            case WEST:
               this.setBlockBounds(1.0F - f5, f, f3, 1.0F, f2, f4);
               break;
            case EAST:
               this.setBlockBounds(0.0F, f, f3, f5, f2, f4);
         }
      }

      @Override
      public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
         EnumFacing value = state.getValue(FACING);
         if (!worldIn.getBlockState(pos.offset(value.getOpposite())).getBlock().getMaterial().isSolid()) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
         }

         super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
      }

      @Override
      public IBlockState getStateFromMeta(int meta) {
         EnumFacing front = EnumFacing.getFront(meta);
         if (front.getAxis() == Axis.Y) {
            front = EnumFacing.NORTH;
         }

         return this.getDefaultState().withProperty(FACING, front);
      }

      @Override
      public int getMetaFromState(IBlockState state) {
         return state.getValue(FACING).getIndex();
      }

      @Override
      public BlockState createBlockState() {
         return new BlockState(this, FACING);
      }
   }

   public static class BlockBannerStanding extends BlockBanner {
      public BlockBannerStanding() {
         this.setDefaultState(this.blockState.getBaseState().withProperty(ROTATION, 0));
      }

      @Override
      public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
         if (!worldIn.getBlockState(pos.down()).getBlock().getMaterial().isSolid()) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
         }

         super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
      }

      @Override
      public IBlockState getStateFromMeta(int meta) {
         return this.getDefaultState().withProperty(ROTATION, meta);
      }

      @Override
      public int getMetaFromState(IBlockState state) {
         return state.getValue(ROTATION);
      }

      @Override
      public BlockState createBlockState() {
         return new BlockState(this, ROTATION);
      }
   }
}
