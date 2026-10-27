package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockPattern.PatternHelper;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSkull extends BlockContainer {
   public static PropertyDirection FACING = PropertyDirection.create("facing");
   public static PropertyBool NODROP = PropertyBool.create("nodrop");
   public static Predicate<BlockWorldState> IS_WITHER_SKELETON = new Predicate<BlockWorldState>() {
      public boolean apply(BlockWorldState blockWorldState) {
         return blockWorldState.getBlockState() != null
            && blockWorldState.getBlockState().getBlock() == Blocks.skull
            && blockWorldState.getTileEntity() instanceof TileEntitySkull
            && ((TileEntitySkull)blockWorldState.getTileEntity()).getSkullType() == 1;
      }
   };
   public BlockPattern witherBasePattern;
   public BlockPattern witherPattern;

   public BlockSkull() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(NODROP, false));
      this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal("tile.skull.skeleton.name");
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
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      switch (worldIn.getBlockState(pos).getValue(FACING)) {
         case UP:
         default:
            this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
            break;
         case NORTH:
            this.setBlockBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
            break;
         case SOUTH:
            this.setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
            break;
         case WEST:
            this.setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
            break;
         case EAST:
            this.setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getCollisionBoundingBox(worldIn, pos, state);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(NODROP, false);
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntitySkull();
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.skull;
   }

   @Override
   public int getDamageValue(World worldIn, BlockPos pos) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      return tileEntity instanceof TileEntitySkull ? ((TileEntitySkull)tileEntity).getSkullType() : super.getDamageValue(worldIn, pos);
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
   }

   @Override
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (player.capabilities.isCreativeMode) {
         state = state.withProperty(NODROP, true);
         worldIn.setBlockState(pos, state, 4);
      }

      super.onBlockHarvested(worldIn, pos, state, player);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         if (!state.getValue(NODROP)) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntitySkull) {
               TileEntitySkull tileEntitySkull = (TileEntitySkull)tileEntity;
               ItemStack itemStack = new ItemStack(Items.skull, 1, this.getDamageValue(worldIn, pos));
               if (tileEntitySkull.getSkullType() == 3 && tileEntitySkull.getPlayerProfile() != null) {
                  itemStack.setTagCompound(new NBTTagCompound());
                  NBTTagCompound nbttagCompound = new NBTTagCompound();
                  NBTUtil.writeGameProfile(nbttagCompound, tileEntitySkull.getPlayerProfile());
                  itemStack.getTagCompound().setTag("SkullOwner", nbttagCompound);
               }

               spawnAsEntity(worldIn, pos, itemStack);
            }
         }

         super.breakBlock(worldIn, pos, state);
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.skull;
   }

   public boolean canDispenserPlace(World worldIn, BlockPos pos, ItemStack stack) {
      return stack.getMetadata() == 1 && pos.getY() >= 2 && worldIn.getDifficulty() != EnumDifficulty.PEACEFUL && !worldIn.isRemote
         ? this.getWitherBasePattern().match(worldIn, pos) != null
         : false;
   }

   public void checkWitherSpawn(World worldIn, BlockPos pos, TileEntitySkull te) {
      if (te.getSkullType() == 1 && pos.getY() >= 2 && worldIn.getDifficulty() != EnumDifficulty.PEACEFUL && !worldIn.isRemote) {
         BlockPattern witherPattern = this.getWitherPattern();
         PatternHelper match = witherPattern.match(worldIn, pos);
         if (match != null) {
            for (int i = 0; i < 3; i++) {
               BlockWorldState translateOffset = match.translateOffset(i, 0, 0);
               worldIn.setBlockState(translateOffset.getPos(), translateOffset.getBlockState().withProperty(NODROP, true), 2);
            }

            for (int i = 0; i < witherPattern.getPalmLength(); i++) {
               for (int j = 0; j < witherPattern.getThumbLength(); j++) {
                  BlockWorldState translateOffset = match.translateOffset(i, j, 0);
                  worldIn.setBlockState(translateOffset.getPos(), Blocks.air.getDefaultState(), 2);
               }
            }

            BlockPos pos2 = match.translateOffset(1, 0, 0).getPos();
            EntityWither entityWither = new EntityWither(worldIn);
            BlockPos pos3 = match.translateOffset(1, 2, 0).getPos();
            entityWither.setLocationAndAngles(
               pos3.getX() + 0.5, pos3.getY() + 0.55, pos3.getZ() + 0.5, match.getFinger().getAxis() == Axis.X ? 0.0F : 90.0F, 0.0F
            );
            entityWither.renderYawOffset = match.getFinger().getAxis() == Axis.X ? 0.0F : 90.0F;
            entityWither.onSummoned();

            for (EntityPlayer entityPlayer : worldIn.getEntitiesWithinAABB(EntityPlayer.class, entityWither.getEntityBoundingBox().expand(50.0, 50.0, 50.0))) {
               entityPlayer.triggerAchievement(AchievementList.spawnWither);
            }

            worldIn.spawnEntityInWorld(entityWither);

            for (int i = 0; i < 120; i++) {
               worldIn.spawnParticle(
                  EnumParticleTypes.SNOWBALL,
                  pos2.getX() + worldIn.rand.nextDouble(),
                  pos2.getY() - 2 + worldIn.rand.nextDouble() * 3.9,
                  pos2.getZ() + worldIn.rand.nextDouble(),
                  0.0,
                  0.0,
                  0.0
               );
            }

            for (int i = 0; i < witherPattern.getPalmLength(); i++) {
               for (int j = 0; j < witherPattern.getThumbLength(); j++) {
                  BlockWorldState translateOffset = match.translateOffset(i, j, 0);
                  worldIn.notifyNeighborsRespectDebug(translateOffset.getPos(), Blocks.air);
               }
            }
         }
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7)).withProperty(NODROP, (meta & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      n |= state.getValue(FACING).getIndex();
      if (state.getValue(NODROP)) {
         n |= 8;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, NODROP);
   }

   public BlockPattern getWitherBasePattern() {
      if (this.witherBasePattern == null) {
         this.witherBasePattern = FactoryBlockPattern.start()
            .aisle("   ", "###", "~#~")
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand)))
            .where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air)))
            .build();
      }

      return this.witherBasePattern;
   }

   public BlockPattern getWitherPattern() {
      if (this.witherPattern == null) {
         this.witherPattern = FactoryBlockPattern.start()
            .aisle("^^^", "###", "~#~")
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand)))
            .where('^', IS_WITHER_SKELETON)
            .where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air)))
            .build();
      }

      return this.witherPattern;
   }
}
