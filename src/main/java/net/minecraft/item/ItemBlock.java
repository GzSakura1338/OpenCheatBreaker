package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemBlock extends Item {
   public Block block;

   public ItemBlock(Block block) {
      this.block = block;
   }

   public ItemBlock setUnlocalizedName(String unlocalizedName) {
      super.setUnlocalizedName(unlocalizedName);
      return this;
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      IBlockState blockState = worldIn.getBlockState(pos);
      Block block = blockState.getBlock();
      if (!block.isReplaceable(worldIn, pos)) {
         pos = pos.offset(side);
      }

      if (stack.stackSize == 0) {
         return false;
      }

      if (!playerIn.canPlayerEdit(pos, side, stack)) {
         return false;
      }

      if (worldIn.canBlockBePlaced(this.block, pos, false, side, (Entity)null, stack)) {
         int metadata = this.getMetadata(stack.getMetadata());
         IBlockState onBlockPlaced = this.block.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, metadata, playerIn);
         if (worldIn.setBlockState(pos, onBlockPlaced, 3)) {
            onBlockPlaced = worldIn.getBlockState(pos);
            if (onBlockPlaced.getBlock() == this.block) {
               setTileEntityNBT(worldIn, playerIn, pos, stack);
               this.block.onBlockPlacedBy(worldIn, pos, onBlockPlaced, playerIn, stack);
            }

            worldIn.playSoundEffect(
               pos.getX() + 0.5F,
               pos.getY() + 0.5F,
               pos.getZ() + 0.5F,
               this.block.stepSound.getPlaceSound(),
               (this.block.stepSound.getVolume() + 1.0F) / 2.0F,
               this.block.stepSound.getFrequency() * 0.8F
            );
            stack.stackSize--;
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean setTileEntityNBT(World worldIn, EntityPlayer pos, BlockPos stack, ItemStack itemStack) {
      MinecraftServer server = MinecraftServer.getServer();
      if (server == null) {
         return false;
      }

      if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("BlockEntityTag", 10)) {
         TileEntity tileEntity = worldIn.getTileEntity(stack);
         if (tileEntity != null) {
            if (!worldIn.isRemote && tileEntity.alwaysFalse() && !server.getConfigurationManager().canSendCommands(pos.getGameProfile())) {
               return false;
            }

            NBTTagCompound nbttagCompound = new NBTTagCompound();
            NBTTagCompound nbttagCompound2 = (NBTTagCompound)nbttagCompound.copy();
            tileEntity.writeToNBT(nbttagCompound);
            NBTTagCompound tag = (NBTTagCompound)itemStack.getTagCompound().getTag("BlockEntityTag");
            nbttagCompound.merge(tag);
            nbttagCompound.setInteger("x", stack.getX());
            nbttagCompound.setInteger("y", stack.getY());
            nbttagCompound.setInteger("z", stack.getZ());
            if (!nbttagCompound.equals(nbttagCompound2)) {
               tileEntity.readFromNBT(nbttagCompound);
               tileEntity.markDirty();
               return true;
            }
         }
      }

      return false;
   }

   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
      Block block = worldIn.getBlockState(pos).getBlock();
      if (block == Blocks.snow_layer) {
         side = EnumFacing.UP;
      } else if (!block.isReplaceable(worldIn, pos)) {
         pos = pos.offset(side);
      }

      return worldIn.canBlockBePlaced(this.block, pos, false, side, (Entity)null, stack);
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      return this.block.getUnlocalizedName();
   }

   @Override
   public String getUnlocalizedName() {
      return this.block.getUnlocalizedName();
   }

   @Override
   public CreativeTabs getCreativeTab() {
      return this.block.getCreativeTabToDisplayOn();
   }

   @Override
   public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
      this.block.getSubBlocks(itemIn, tab, subItems);
   }

   public Block getBlock() {
      return this.block;
   }
}
