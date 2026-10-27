package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockHugeMushroom extends Block {
   public static PropertyEnum<BlockHugeMushroom.EnumType> VARIANT = PropertyEnum.create("variant", BlockHugeMushroom.EnumType.class);
   public Block smallBlock;

   public BlockHugeMushroom(Material material, MapColor mapColor, Block block) {
      super(material, mapColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockHugeMushroom.EnumType.ALL_OUTSIDE));
      this.smallBlock = block;
   }

   @Override
   public int quantityDropped(Random random) {
      return Math.max(0, random.nextInt(10) - 7);
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      switch (state.getValue(VARIANT)) {
         case ALL_STEM:
            return MapColor.clothColor;
         case ALL_INSIDE:
            return MapColor.sandColor;
         case STEM:
            return MapColor.sandColor;
         default:
            return super.getMapColor(state);
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(this.smallBlock);
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Item.getItemFromBlock(this.smallBlock);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState();
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockHugeMushroom.EnumType.byMetadata(meta));
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
      NORTH_WEST(1, "north_west"),
      NORTH(2, "north"),
      NORTH_EAST(3, "north_east"),
      WEST(4, "west"),
      CENTER(5, "center"),
      EAST(6, "east"),
      SOUTH_WEST(7, "south_west"),
      SOUTH(8, "south"),
      SOUTH_EAST(9, "south_east"),
      STEM(10, "stem"),
      ALL_INSIDE(0, "all_inside"),
      ALL_OUTSIDE(14, "all_outside"),
      ALL_STEM(15, "all_stem");

      public int meta;
      public String name;
      public static BlockHugeMushroom.EnumType[] META_LOOKUP = new BlockHugeMushroom.EnumType[16];

      EnumType(int meta, String name) {
         this.meta = meta;
         this.name = name;
      }

      public int getMetadata() {
         return this.meta;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockHugeMushroom.EnumType byMetadata(int meta) {
         if (meta < 0 || meta >= META_LOOKUP.length) {
            meta = 0;
         }

         BlockHugeMushroom.EnumType enumType = META_LOOKUP[meta];
         return enumType == null ? META_LOOKUP[0] : enumType;
      }

      @Override
      public String getName() {
         return this.name;
      }

      static {
         for (BlockHugeMushroom.EnumType enumType : values()) {
            META_LOOKUP[enumType.getMetadata()] = enumType;
         }
      }
   }
}
