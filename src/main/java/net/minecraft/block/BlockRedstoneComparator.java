package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider {
   public static PropertyBool POWERED = PropertyBool.create("powered");
   public static PropertyEnum<BlockRedstoneComparator.Mode> MODE = PropertyEnum.create("mode", BlockRedstoneComparator.Mode.class);

   public BlockRedstoneComparator(boolean powered) {
      super(powered);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(POWERED, false)
            .withProperty(MODE, BlockRedstoneComparator.Mode.COMPARE)
      );
      this.isBlockContainer = true;
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal("item.comparator.name");
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.comparator;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.comparator;
   }

   @Override
   public int getDelay(IBlockState state) {
      return 2;
   }

   @Override
   public IBlockState getPoweredState(IBlockState unpoweredState) {
      Boolean value = unpoweredState.getValue(POWERED);
      BlockRedstoneComparator.Mode value2 = unpoweredState.getValue(MODE);
      EnumFacing value3 = unpoweredState.getValue(FACING);
      return Blocks.powered_comparator.getDefaultState().withProperty(FACING, value3).withProperty(POWERED, value).withProperty(MODE, value2);
   }

   @Override
   public IBlockState getUnpoweredState(IBlockState poweredState) {
      Boolean value = poweredState.getValue(POWERED);
      BlockRedstoneComparator.Mode value2 = poweredState.getValue(MODE);
      EnumFacing value3 = poweredState.getValue(FACING);
      return Blocks.unpowered_comparator.getDefaultState().withProperty(FACING, value3).withProperty(POWERED, value).withProperty(MODE, value2);
   }

   @Override
   public boolean isPowered(IBlockState state) {
      return this.isRepeaterPowered || state.getValue(POWERED);
   }

   @Override
   public int getActiveSignal(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      return tileEntity instanceof TileEntityComparator ? ((TileEntityComparator)tileEntity).getOutputSignal() : 0;
   }

   public int calculateOutput(World worldIn, BlockPos pos, IBlockState state) {
      return state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT
         ? Math.max(this.calculateInputStrength(worldIn, pos, state) - this.getPowerOnSides(worldIn, pos, state), 0)
         : this.calculateInputStrength(worldIn, pos, state);
   }

   @Override
   public boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state) {
      int calculateInputStrength = this.calculateInputStrength(worldIn, pos, state);
      if (calculateInputStrength >= 15) {
         return true;
      }

      if (calculateInputStrength == 0) {
         return false;
      }

      int powerOnSides = this.getPowerOnSides(worldIn, pos, state);
      return powerOnSides == 0 ? true : calculateInputStrength >= powerOnSides;
   }

   @Override
   public int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
      int calculateInputStrength = super.calculateInputStrength(worldIn, pos, state);
      EnumFacing value = state.getValue(FACING);
      BlockPos offset = pos.offset(value);
      Block block = worldIn.getBlockState(offset).getBlock();
      if (block.hasComparatorInputOverride()) {
         calculateInputStrength = block.getComparatorInputOverride(worldIn, offset);
      } else if (calculateInputStrength < 15 && block.isNormalCube()) {
         offset = offset.offset(value);
         block = worldIn.getBlockState(offset).getBlock();
         if (block.hasComparatorInputOverride()) {
            calculateInputStrength = block.getComparatorInputOverride(worldIn, offset);
         } else if (block.getMaterial() == Material.air) {
            EntityItemFrame findItemFrame = this.findItemFrame(worldIn, value, offset);
            if (findItemFrame != null) {
               calculateInputStrength = findItemFrame.getComparatorPower();
            }
         }
      }

      return calculateInputStrength;
   }

   public EntityItemFrame findItemFrame(World worldIn, final EnumFacing facing, BlockPos pos) {
      List entitiesWithinAABB = worldIn.getEntitiesWithinAABB(
         EntityItemFrame.class,
         new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
         new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               return entity != null && entity.getHorizontalFacing() == facing;
            }
         }
      );
      return entitiesWithinAABB.size() == 1 ? (EntityItemFrame)entitiesWithinAABB.get(0) : null;
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!playerIn.capabilities.allowEdit) {
         return false;
      }

      state = state.cycleProperty(MODE);
      worldIn.playSoundEffect(
         pos.getX() + 0.5,
         pos.getY() + 0.5,
         pos.getZ() + 0.5,
         "random.click",
         0.3F,
         state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT ? 0.55F : 0.5F
      );
      worldIn.setBlockState(pos, state, 2);
      this.onStateChange(worldIn, pos, state);
      return true;
   }

   @Override
   public void updateState(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isBlockTickPending(pos, this)) {
         int calculateOutput = this.calculateOutput(worldIn, pos, state);
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         int n = tileEntity instanceof TileEntityComparator ? ((TileEntityComparator)tileEntity).getOutputSignal() : 0;
         if (calculateOutput != n || this.isPowered(state) != this.shouldBePowered(worldIn, pos, state)) {
            if (this.isFacingTowardsRepeater(worldIn, pos, state)) {
               worldIn.updateBlockTick(pos, this, 2, -1);
            } else {
               worldIn.updateBlockTick(pos, this, 2, 0);
            }
         }
      }
   }

   public void onStateChange(World worldIn, BlockPos pos, IBlockState state) {
      int calculateOutput = this.calculateOutput(worldIn, pos, state);
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      int n = 0;
      if (tileEntity instanceof TileEntityComparator) {
         TileEntityComparator tileEntityComparator = (TileEntityComparator)tileEntity;
         n = tileEntityComparator.getOutputSignal();
         tileEntityComparator.setOutputSignal(calculateOutput);
      }

      if (n != calculateOutput || state.getValue(MODE) == BlockRedstoneComparator.Mode.COMPARE) {
         boolean shouldBePowered = this.shouldBePowered(worldIn, pos, state);
         boolean isPowered = this.isPowered(state);
         if (isPowered && !shouldBePowered) {
            worldIn.setBlockState(pos, state.withProperty(POWERED, false), 2);
         } else if (!isPowered && shouldBePowered) {
            worldIn.setBlockState(pos, state.withProperty(POWERED, true), 2);
         }

         this.notifyNeighbors(worldIn, pos, state);
      }
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (this.isRepeaterPowered) {
         worldIn.setBlockState(pos, this.getUnpoweredState(state).withProperty(POWERED, true), 4);
      }

      this.onStateChange(worldIn, pos, state);
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      super.onBlockAdded(worldIn, pos, state);
      worldIn.setTileEntity(pos, this.createNewTileEntity(worldIn, 0));
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      worldIn.removeTileEntity(pos);
      this.notifyNeighbors(worldIn, pos, state);
   }

   @Override
   public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam) {
      super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      return tileEntity == null ? false : tileEntity.receiveClientEvent(eventID, eventParam);
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityComparator();
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(FACING, EnumFacing.getHorizontal(meta))
         .withProperty(POWERED, (meta & 8) > 0)
         .withProperty(MODE, (meta & 4) > 0 ? BlockRedstoneComparator.Mode.SUBTRACT : BlockRedstoneComparator.Mode.COMPARE);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      n |= state.getValue(FACING).getHorizontalIndex();
      if (state.getValue(POWERED)) {
         n |= 8;
      }

      if (state.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT) {
         n |= 4;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, MODE, POWERED);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState()
         .withProperty(FACING, placer.getHorizontalFacing().getOpposite())
         .withProperty(POWERED, false)
         .withProperty(MODE, BlockRedstoneComparator.Mode.COMPARE);
   }

   public enum Mode implements IStringSerializable {
      COMPARE("compare"),
      SUBTRACT("subtract");

      public String name;

      Mode(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }

      @Override
      public String getName() {
         return this.name;
      }
   }
}
