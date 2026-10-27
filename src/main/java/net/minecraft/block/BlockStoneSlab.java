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
import net.minecraft.world.World;

public abstract class BlockStoneSlab extends BlockSlab {
   public static PropertyBool SEAMLESS = PropertyBool.create("seamless");
   public static PropertyEnum<BlockStoneSlab.EnumType> VARIANT = PropertyEnum.create("variant", BlockStoneSlab.EnumType.class);

   public BlockStoneSlab() {
      super(Material.rock);
      IBlockState baseState = this.blockState.getBaseState();
      if (this.isDouble()) {
         baseState = baseState.withProperty(SEAMLESS, false);
      } else {
         baseState = baseState.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
      }

      this.setDefaultState(baseState.withProperty(VARIANT, BlockStoneSlab.EnumType.STONE));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(Blocks.stone_slab);
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Item.getItemFromBlock(Blocks.stone_slab);
   }

   @Override
   public String getUnlocalizedName(int meta) {
      return super.getUnlocalizedName() + "." + BlockStoneSlab.EnumType.byMetadata(meta).getUnlocalizedName();
   }

   @Override
   public IProperty<?> getVariantProperty() {
      return VARIANT;
   }

   @Override
   public Object getVariant(ItemStack stack) {
      return BlockStoneSlab.EnumType.byMetadata(stack.getMetadata() & 7);
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      if (itemIn != Item.getItemFromBlock(Blocks.double_stone_slab)) {
         for (BlockStoneSlab.EnumType enumType : BlockStoneSlab.EnumType.values()) {
            if (enumType != BlockStoneSlab.EnumType.WOOD) {
               list.add(new ItemStack(itemIn, 1, enumType.getMetadata()));
            }
         }
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      IBlockState withProperty = this.getDefaultState().withProperty(VARIANT, BlockStoneSlab.EnumType.byMetadata(meta & 7));
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
   public int damageDropped(IBlockState state) {
      return state.getValue(VARIANT).getMetadata();
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      return state.getValue(VARIANT).getMaterialColor();
   }

   public enum EnumType implements IStringSerializable {
      STONE(0, MapColor.stoneColor, "stone"),
      SAND(1, MapColor.sandColor, "sandstone", "sand"),
      WOOD(2, MapColor.woodColor, "wood_old", "wood"),
      COBBLESTONE(3, MapColor.stoneColor, "cobblestone", "cobble"),
      BRICK(4, MapColor.redColor, "brick"),
      SMOOTHBRICK(5, MapColor.stoneColor, "stone_brick", "smoothStoneBrick"),
      NETHERBRICK(6, MapColor.netherrackColor, "nether_brick", "netherBrick"),
      QUARTZ(7, MapColor.quartzColor, "quartz");

      public int meta;
      public MapColor color;
      public String name;
      public String unlocalizedName;
      public static BlockStoneSlab.EnumType[] META_LOOKUP = new BlockStoneSlab.EnumType[values().length];

      EnumType(int n, MapColor mapColor, String str) {
         this(n, mapColor, str, str);
      }

      EnumType(int n, MapColor mapColor, String str, String str2) {
         this.meta = n;
         this.color = mapColor;
         this.name = str;
         this.unlocalizedName = str2;
      }

      public int getMetadata() {
         return this.meta;
      }

      public MapColor getMaterialColor() {
         return this.color;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockStoneSlab.EnumType byMetadata(int meta) {
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
         return this.unlocalizedName;
      }

      static {
         for (BlockStoneSlab.EnumType enumType : values()) {
            META_LOOKUP[enumType.getMetadata()] = enumType;
         }
      }
   }
}
