package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBanner.EnumBannerPattern;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBanner extends ItemBlock {
   public ItemBanner() {
      super(Blocks.standing_banner);
      this.maxStackSize = 16;
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (side == EnumFacing.DOWN) {
         return false;
      }

      if (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid()) {
         return false;
      }

      pos = pos.offset(side);
      if (!playerIn.canPlayerEdit(pos, side, stack)) {
         return false;
      }

      if (!Blocks.standing_banner.canPlaceBlockAt(worldIn, pos)) {
         return false;
      }

      if (worldIn.isRemote) {
         return true;
      }

      if (side == EnumFacing.UP) {
         int n = MathHelper.floor_double((playerIn.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5) & 15;
         worldIn.setBlockState(pos, Blocks.standing_banner.getDefaultState().withProperty(BlockStandingSign.ROTATION, n), 3);
      } else {
         worldIn.setBlockState(pos, Blocks.wall_banner.getDefaultState().withProperty(BlockWallSign.FACING, side), 3);
      }

      stack.stackSize--;
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityBanner) {
         ((TileEntityBanner)tileEntity).setItemValues(stack);
      }

      return true;
   }

   @Override
   public String getItemStackDisplayName(ItemStack stack) {
      String str = "item.banner.";
      EnumDyeColor baseColor = this.getBaseColor(stack);
      str = str + baseColor.getUnlocalizedName() + ".name";
      return StatCollector.translateToLocal(str);
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      NBTTagCompound subCompound = stack.getSubCompound("BlockEntityTag", false);
      if (subCompound != null && subCompound.hasKey("Patterns")) {
         NBTTagList tagList = subCompound.getTagList("Patterns", 10);

         for (int i = 0; i < tagList.tagCount() && i < 6; i++) {
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
            EnumDyeColor byDyeDamage = EnumDyeColor.byDyeDamage(compoundTagAt.getInteger("Color"));
            EnumBannerPattern patternByID = EnumBannerPattern.getPatternByID(compoundTagAt.getString("Pattern"));
            if (patternByID != null) {
               tooltip.add(StatCollector.translateToLocal("item.banner." + patternByID.getPatternName() + "." + byDyeDamage.getUnlocalizedName()));
            }
         }
      }
   }

   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      if (renderPass == 0) {
         return 16777215;
      }

      EnumDyeColor baseColor = this.getBaseColor(stack);
      return baseColor.getMapColor().colorValue;
   }

   @Override
   public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
      for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         TileEntityBanner.setBaseColorAndPatterns(nbttagCompound, enumDyeColor.getDyeDamage(), (NBTTagList)null);
         NBTTagCompound nbttagCompound2 = new NBTTagCompound();
         nbttagCompound2.setTag("BlockEntityTag", nbttagCompound);
         ItemStack itemStack = new ItemStack(itemIn, 1, enumDyeColor.getDyeDamage());
         itemStack.setTagCompound(nbttagCompound2);
         subItems.add(itemStack);
      }
   }

   @Override
   public CreativeTabs getCreativeTab() {
      return CreativeTabs.tabDecorations;
   }

   public EnumDyeColor getBaseColor(ItemStack stack) {
      NBTTagCompound subCompound = stack.getSubCompound("BlockEntityTag", false);
      EnumDyeColor enumDyeColor = null;
      if (subCompound != null && subCompound.hasKey("Base")) {
         enumDyeColor = EnumDyeColor.byDyeDamage(subCompound.getInteger("Base"));
      } else {
         enumDyeColor = EnumDyeColor.byDyeDamage(stack.getMetadata());
      }

      return enumDyeColor;
   }
}
