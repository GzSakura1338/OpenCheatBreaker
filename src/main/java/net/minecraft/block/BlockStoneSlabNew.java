package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public abstract class BlockStoneSlabNew extends BlockSlab {
   public static PropertyBool SEAMLESS = PropertyBool.create("seamless");
   public static PropertyEnum<BlockStoneSlabNew.EnumType> VARIANT = PropertyEnum.create("variant", BlockStoneSlabNew.EnumType.class);

   public BlockStoneSlabNew() {
      super(Material.rock);
      IBlockState baseState = this.blockState.getBaseState();
      if (this.isDouble()) {
         baseState = baseState.withProperty(SEAMLESS, false);
      } else {
         baseState = baseState.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
      }

      this.setDefaultState(baseState.withProperty(VARIANT, BlockStoneSlabNew.EnumType.RED_SANDSTONE));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + ".red_sandstone.name");
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(Blocks.stone_slab2);
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Item.getItemFromBlock(Blocks.stone_slab2);
   }

   @Override
   public String getUnlocalizedName(int meta) {
      return super.getUnlocalizedName() + "." + BlockStoneSlabNew.EnumType.byMetadata(meta).getUnlocalizedName();
   }

   @Override
   public IProperty<?> getVariantProperty() {
      return VARIANT;
   }

   @Override
   public Object getVariant(ItemStack stack) {
      return BlockStoneSlabNew.EnumType.byMetadata(stack.getMetadata() & 7);
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      if (itemIn != Item.getItemFromBlock(Blocks.double_stone_slab2)) {
         for (BlockStoneSlabNew.EnumType enumType : BlockStoneSlabNew.EnumType.values()) {
            list.add(new ItemStack(itemIn, 1, enumType.getMetadata()));
         }
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      IBlockState withProperty = this.getDefaultState().withProperty(VARIANT, BlockStoneSlabNew.EnumType.byMetadata(meta & 7));
      if (this.isDouble()) {
         withProperty = withProperty.withProperty(SEAMLESS, (meta & 8) != 0);
      } else {
         withProperty = withProperty.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
      }

      return withProperty;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      n |= state.getValue(VARIANT).getMetadata();
      if (this.isDouble()) {
         if (state.getValue(SEAMLESS)) {
            n |= 8;
         }
      } else if (state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
         n |= 8;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return this.isDouble() ? new BlockState(this, SEAMLESS, VARIANT) : new BlockState(this, HALF, VARIANT);
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      return state.getValue(VARIANT).getColor();
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(VARIANT).getMetadata();
   }

   public enum EnumType implements IStringSerializable {
      RED_SANDSTONE(0, "red_sandstone", BlockSand.EnumType.RED_SAND.getMapColor());

      public int meta;
      public String name;
      public MapColor color;
      public static BlockStoneSlabNew.EnumType[] META_LOOKUP = new BlockStoneSlabNew.EnumType[values().length];

      EnumType(int n, String str, MapColor mapColor) {
         this.meta = n;
         this.name = str;
         this.color = mapColor;
      }

      public int getMetadata() {
         return this.meta;
      }

      public MapColor getColor() {
         return this.color;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockStoneSlabNew.EnumType byMetadata(int meta) {
         if (meta < 0 || meta >= META_LOOKUP.length) {
            meta = 0;
         }

         return META_LOOKUP[meta];
      }

      @Override
      public String getName() {
         return this.name;
      }

      public String getUnlocalizedName() {
         return this.name;
      }

      static {
         for (BlockStoneSlabNew.EnumType enumType : values()) {
            META_LOOKUP[enumType.getMetadata()] = enumType;
         }
      }
   }
}
