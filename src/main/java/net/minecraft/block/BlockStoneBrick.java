package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class BlockStoneBrick extends Block {
   public static PropertyEnum<BlockStoneBrick.EnumType> VARIANT = PropertyEnum.create("variant", BlockStoneBrick.EnumType.class);
   public static int DEFAULT_META = BlockStoneBrick.EnumType.DEFAULT.getMetadata();
   public static int MOSSY_META = BlockStoneBrick.EnumType.MOSSY.getMetadata();
   public static int CRACKED_META = BlockStoneBrick.EnumType.CRACKED.getMetadata();
   public static int CHISELED_META = BlockStoneBrick.EnumType.CHISELED.getMetadata();

   public BlockStoneBrick() {
      super(Material.rock);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockStoneBrick.EnumType.DEFAULT));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(VARIANT).getMetadata();
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      for (BlockStoneBrick.EnumType enumType : BlockStoneBrick.EnumType.values()) {
         list.add(new ItemStack(itemIn, 1, enumType.getMetadata()));
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockStoneBrick.EnumType.byMetadata(meta));
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(VARIANT).getMetadata();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, VARIANT);
   }

   public enum EnumType implements IStringSerializable {
      DEFAULT(0, "stonebrick", "default"),
      MOSSY(1, "mossy_stonebrick", "mossy"),
      CRACKED(2, "cracked_stonebrick", "cracked"),
      CHISELED(3, "chiseled_stonebrick", "chiseled");

      public int meta;
      public String name;
      public String unlocalizedName;
      public static BlockStoneBrick.EnumType[] META_LOOKUP = new BlockStoneBrick.EnumType[values().length];

      EnumType(int meta, String name, String unlocalizedName) {
         this.meta = meta;
         this.name = name;
         this.unlocalizedName = unlocalizedName;
      }

      public int getMetadata() {
         return this.meta;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockStoneBrick.EnumType byMetadata(int meta) {
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
         for (BlockStoneBrick.EnumType enumType : values()) {
            META_LOOKUP[enumType.getMetadata()] = enumType;
         }
      }
   }
}
