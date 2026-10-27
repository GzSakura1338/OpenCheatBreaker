package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecart.EnumMinecartType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemMinecart extends Item {
   public static IBehaviorDispenseItem dispenserMinecartBehavior = new BehaviorDefaultDispenseItem() {
      public BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();

      @Override
      public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         EnumFacing facing = BlockDispenser.getFacing(source.getBlockMetadata());
         World world = source.getWorld();
         double d = source.getX() + facing.getFrontOffsetX() * 1.125;
         double d2 = Math.floor(source.getY()) + facing.getFrontOffsetY();
         double d3 = source.getZ() + facing.getFrontOffsetZ() * 1.125;
         BlockPos offset = source.getBlockPos().offset(facing);
         IBlockState blockState = world.getBlockState(offset);
         EnumRailDirection enumRailDirection = blockState.getBlock() instanceof BlockRailBase
            ? blockState.getValue(((BlockRailBase)blockState.getBlock()).getShapeProperty())
            : EnumRailDirection.NORTH_SOUTH;
         double d4;
         if (BlockRailBase.isRailBlock(blockState)) {
            if (enumRailDirection.isAscending()) {
               d4 = 0.6;
            } else {
               d4 = 0.1;
            }
         } else {
            if (blockState.getBlock().getMaterial() != Material.air || !BlockRailBase.isRailBlock(world.getBlockState(offset.down()))) {
               return this.behaviourDefaultDispenseItem.dispense(source, stack);
            }

            IBlockState blockState2 = world.getBlockState(offset.down());
            EnumRailDirection enumRailDirection2 = blockState2.getBlock() instanceof BlockRailBase
               ? blockState2.getValue(((BlockRailBase)blockState2.getBlock()).getShapeProperty())
               : EnumRailDirection.NORTH_SOUTH;
            if (facing != EnumFacing.DOWN && enumRailDirection2.isAscending()) {
               d4 = -0.4;
            } else {
               d4 = -0.9;
            }
         }

         EntityMinecart minecart = EntityMinecart.getMinecart(world, d, d2 + d4, d3, ((ItemMinecart)stack.getItem()).minecartType);
         if (stack.hasDisplayName()) {
            minecart.setCustomNameTag(stack.getDisplayName());
         }

         world.spawnEntityInWorld(minecart);
         stack.splitStack(1);
         return stack;
      }

      @Override
      public void playDispenseSound(IBlockSource source) {
         source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
      }
   };
   public EnumMinecartType minecartType;

   public ItemMinecart(EnumMinecartType type) {
      this.maxStackSize = 1;
      this.minecartType = type;
      this.setCreativeTab(CreativeTabs.tabTransport);
      BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserMinecartBehavior);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      IBlockState blockState = worldIn.getBlockState(pos);
      if (BlockRailBase.isRailBlock(blockState)) {
         if (!worldIn.isRemote) {
            EnumRailDirection enumRailDirection = blockState.getBlock() instanceof BlockRailBase
               ? blockState.getValue(((BlockRailBase)blockState.getBlock()).getShapeProperty())
               : EnumRailDirection.NORTH_SOUTH;
            double d = 0.0;
            if (enumRailDirection.isAscending()) {
               d = 0.5;
            }

            EntityMinecart minecart = EntityMinecart.getMinecart(worldIn, pos.getX() + 0.5, pos.getY() + 0.0625 + d, pos.getZ() + 0.5, this.minecartType);
            if (stack.hasDisplayName()) {
               minecart.setCustomNameTag(stack.getDisplayName());
            }

            worldIn.spawnEntityInWorld(minecart);
         }

         stack.stackSize--;
         return true;
      } else {
         return false;
      }
   }
}
