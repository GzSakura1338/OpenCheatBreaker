package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockSilverfish extends Block {
   public static PropertyEnum<BlockSilverfish.EnumType> VARIANT = PropertyEnum.create("variant", BlockSilverfish.EnumType.class);

   public BlockSilverfish() {
      super(Material.clay);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockSilverfish.EnumType.STONE));
      this.setHardness(0.0F);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public int quantityDropped(Random random) {
      return 0;
   }

   public static boolean canContainSilverfish(IBlockState blockState) {
      Block block = blockState.getBlock();
      return blockState == Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE)
         || block == Blocks.cobblestone
         || block == Blocks.stonebrick;
   }

   @Override
   public ItemStack createStackedBlock(IBlockState state) {
      switch (state.getValue(VARIANT)) {
         case COBBLESTONE:
            return new ItemStack(Blocks.cobblestone);
         case STONEBRICK:
            return new ItemStack(Blocks.stonebrick);
         case MOSSY_STONEBRICK:
            return new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.EnumType.MOSSY.getMetadata());
         case CRACKED_STONEBRICK:
            return new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.EnumType.CRACKED.getMetadata());
         case CHISELED_STONEBRICK:
            return new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.EnumType.CHISELED.getMetadata());
         default:
            return new ItemStack(Blocks.stone);
      }
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops")) {
         EntitySilverfish entitySilverfish = new EntitySilverfish(worldIn);
         entitySilverfish.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
         worldIn.spawnEntityInWorld(entitySilverfish);
         entitySilverfish.spawnExplosionParticle();
      }
   }

   @Override
   public int getDamageValue(World worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      return blockState.getBlock().getMetaFromState(blockState);
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      for (BlockSilverfish.EnumType enumType : BlockSilverfish.EnumType.values()) {
         list.add(new ItemStack(itemIn, 1, enumType.getMetadata()));
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockSilverfish.EnumType.byMetadata(meta));
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
      STONE(0, "stone") {
         @Override
         public IBlockState getModelBlock() {
            return Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE);
         }
      },
      COBBLESTONE(1, "cobblestone", "cobble") {
         @Override
         public IBlockState getModelBlock() {
            return Blocks.cobblestone.getDefaultState();
         }
      },
      STONEBRICK(2, "stone_brick", "brick") {
         @Override
         public IBlockState getModelBlock() {
            return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT);
         }
      },
      MOSSY_STONEBRICK(3, "mossy_brick", "mossybrick") {
         @Override
         public IBlockState getModelBlock() {
            return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);
         }
      },
      CRACKED_STONEBRICK(4, "cracked_brick", "crackedbrick") {
         @Override
         public IBlockState getModelBlock() {
            return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
         }
      },
      CHISELED_STONEBRICK(5, "chiseled_brick", "chiseledbrick") {
         @Override
         public IBlockState getModelBlock() {
            return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
         }
      };

      public int meta;
      public String name;
      public String unlocalizedName;
      public static BlockSilverfish.EnumType[] META_LOOKUP = new BlockSilverfish.EnumType[values().length];

      EnumType(int meta, String name) {
         this(meta, name, name);
      }

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

      public static BlockSilverfish.EnumType byMetadata(int meta) {
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

      public abstract IBlockState getModelBlock();

      public static BlockSilverfish.EnumType forModelBlock(IBlockState model) {
         for (BlockSilverfish.EnumType enumType : values()) {
            if (model == enumType.getModelBlock()) {
               return enumType;
            }
         }

         return STONE;
      }

      static {
         for (BlockSilverfish.EnumType enumType : values()) {
            META_LOOKUP[enumType.getMetadata()] = enumType;
         }
      }
   }
}
