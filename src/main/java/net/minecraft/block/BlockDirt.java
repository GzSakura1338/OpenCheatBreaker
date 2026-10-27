package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDirt extends Block {
   public static PropertyEnum<BlockDirt.DirtType> VARIANT = PropertyEnum.create("variant", BlockDirt.DirtType.class);
   public static PropertyBool SNOWY = PropertyBool.create("snowy");

   public BlockDirt() {
      super(Material.ground);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockDirt.DirtType.DIRT).withProperty(SNOWY, false));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      return state.getValue(VARIANT).getColor();
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      if (state.getValue(VARIANT) == BlockDirt.DirtType.PODZOL) {
         Block block = worldIn.getBlockState(pos.up()).getBlock();
         state = state.withProperty(SNOWY, block == Blocks.snow || block == Blocks.snow_layer);
      }

      return state;
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      list.add(new ItemStack(this, 1, BlockDirt.DirtType.DIRT.getMetadata()));
      list.add(new ItemStack(this, 1, BlockDirt.DirtType.COARSE_DIRT.getMetadata()));
      list.add(new ItemStack(this, 1, BlockDirt.DirtType.PODZOL.getMetadata()));
   }

   @Override
   public int getDamageValue(World worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      return blockState.getBlock() != this ? 0 : blockState.getValue(VARIANT).getMetadata();
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockDirt.DirtType.byMetadata(meta));
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(VARIANT).getMetadata();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, VARIANT, SNOWY);
   }

   @Override
   public int damageDropped(IBlockState state) {
      BlockDirt.DirtType value = state.getValue(VARIANT);
      if (value == BlockDirt.DirtType.PODZOL) {
         value = BlockDirt.DirtType.DIRT;
      }

      return value.getMetadata();
   }

   public enum DirtType implements IStringSerializable {
      DIRT(0, "dirt", "default", MapColor.dirtColor),
      COARSE_DIRT(1, "coarse_dirt", "coarse", MapColor.dirtColor),
      PODZOL(2, "podzol", MapColor.obsidianColor);

      public int metadata;
      public String name;
      public String unlocalizedName;
      public MapColor color;
      public static BlockDirt.DirtType[] METADATA_LOOKUP = new BlockDirt.DirtType[values().length];

      DirtType(int n, String str, MapColor mapColor) {
         this(n, str, str, mapColor);
      }

      DirtType(int n, String str, String str2, MapColor mapColor) {
         this.metadata = n;
         this.name = str;
         this.unlocalizedName = str2;
         this.color = mapColor;
      }

      public int getMetadata() {
         return this.metadata;
      }

      public String getUnlocalizedName() {
         return this.unlocalizedName;
      }

      public MapColor getColor() {
         return this.color;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockDirt.DirtType byMetadata(int metadata) {
         if (metadata < 0 || metadata >= METADATA_LOOKUP.length) {
            metadata = 0;
         }

         return METADATA_LOOKUP[metadata];
      }

      @Override
      public String getName() {
         return this.name;
      }

      static {
         for (BlockDirt.DirtType dirtType : values()) {
            METADATA_LOOKUP[dirtType.getMetadata()] = dirtType;
         }
      }
   }
}
