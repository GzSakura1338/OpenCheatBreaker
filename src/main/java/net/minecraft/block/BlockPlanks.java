package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class BlockPlanks extends Block {
   public static PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class);

   public BlockPlanks() {
      super(Material.wood);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.OAK));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(VARIANT).getMetadata();
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      for (BlockPlanks.EnumType enumType : BlockPlanks.EnumType.values()) {
         list.add(new ItemStack(itemIn, 1, enumType.getMetadata()));
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockPlanks.EnumType.byMetadata(meta));
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      return state.getValue(VARIANT).getMapColor();
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
      OAK(0, "oak", MapColor.woodColor),
      SPRUCE(1, "spruce", MapColor.obsidianColor),
      BIRCH(2, "birch", MapColor.sandColor),
      JUNGLE(3, "jungle", MapColor.dirtColor),
      ACACIA(4, "acacia", MapColor.adobeColor),
      DARK_OAK(5, "dark_oak", "big_oak", MapColor.brownColor);

      public int meta;
      public String name;
      public String unlocalizedName;
      public MapColor mapColor;
      public static BlockPlanks.EnumType[] META_LOOKUP = new BlockPlanks.EnumType[values().length];

      EnumType(int n, String str, MapColor mapColor) {
         this(n, str, str, mapColor);
      }

      EnumType(int n, String str, String str2, MapColor mapColor) {
         this.meta = n;
         this.name = str;
         this.unlocalizedName = str2;
         this.mapColor = mapColor;
      }

      public int getMetadata() {
         return this.meta;
      }

      public MapColor getMapColor() {
         return this.mapColor;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockPlanks.EnumType byMetadata(int meta) {
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
         for (BlockPlanks.EnumType enumType : values()) {
            META_LOOKUP[enumType.getMetadata()] = enumType;
         }
      }
   }
}
