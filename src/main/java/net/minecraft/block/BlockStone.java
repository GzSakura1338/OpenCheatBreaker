package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;

public class BlockStone extends Block {
   public static PropertyEnum<BlockStone.EnumType> VARIANT = PropertyEnum.create("variant", BlockStone.EnumType.class);

   public BlockStone() {
      super(Material.rock);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockStone.EnumType.STONE));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + "." + BlockStone.EnumType.STONE.getUnlocalizedName() + ".name");
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      return state.getValue(VARIANT).getColor();
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return state.getValue(VARIANT) == BlockStone.EnumType.STONE ? Item.getItemFromBlock(Blocks.cobblestone) : Item.getItemFromBlock(Blocks.stone);
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(VARIANT).getMetadata();
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      for (BlockStone.EnumType enumType : BlockStone.EnumType.values()) {
         list.add(new ItemStack(itemIn, 1, enumType.getMetadata()));
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockStone.EnumType.byMetadata(meta));
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
      STONE(0, MapColor.stoneColor, "stone"),
      GRANITE(1, MapColor.dirtColor, "granite"),
      GRANITE_SMOOTH(2, MapColor.dirtColor, "smooth_granite", "graniteSmooth"),
      DIORITE(3, MapColor.quartzColor, "diorite"),
      DIORITE_SMOOTH(4, MapColor.quartzColor, "smooth_diorite", "dioriteSmooth"),
      ANDESITE(5, MapColor.stoneColor, "andesite"),
      ANDESITE_SMOOTH(6, MapColor.stoneColor, "smooth_andesite", "andesiteSmooth");

      public int meta;
      public String name;
      public String unlocalizedName;
      public MapColor color;
      public static BlockStone.EnumType[] META_LOOKUP = new BlockStone.EnumType[values().length];

      EnumType(int n, MapColor mapColor, String str) {
         this(n, mapColor, str, str);
      }

      EnumType(int n, MapColor mapColor, String str, String str2) {
         this.meta = n;
         this.name = str;
         this.unlocalizedName = str2;
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

      public static BlockStone.EnumType byMetadata(int meta) {
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
         for (BlockStone.EnumType enumType : values()) {
            META_LOOKUP[enumType.getMetadata()] = enumType;
         }
      }
   }
}
