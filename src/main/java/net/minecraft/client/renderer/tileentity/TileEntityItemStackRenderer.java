package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;

public class TileEntityItemStackRenderer {
   public static TileEntityItemStackRenderer instance = new TileEntityItemStackRenderer();
   public TileEntityChest chest = new TileEntityChest(0);
   public TileEntityChest trappedChest = new TileEntityChest(1);
   public TileEntityEnderChest enderChest = new TileEntityEnderChest();
   public TileEntityBanner banner = new TileEntityBanner();
   public TileEntitySkull skull = new TileEntitySkull();

   public void renderByItem(ItemStack itemStackIn) {
      if (itemStackIn.getItem() == Items.banner) {
         this.banner.setItemValues(itemStackIn);
         TileEntityRendererDispatcher.instance.renderTileEntityAt(this.banner, 0.0, 0.0, 0.0, 0.0F);
      } else if (itemStackIn.getItem() == Items.skull) {
         GameProfile gameProfile = null;
         if (itemStackIn.hasTagCompound()) {
            NBTTagCompound tagCompound = itemStackIn.getTagCompound();
            if (tagCompound.hasKey("SkullOwner", 10)) {
               gameProfile = NBTUtil.readGameProfileFromNBT(tagCompound.getCompoundTag("SkullOwner"));
            } else if (tagCompound.hasKey("SkullOwner", 8) && tagCompound.getString("SkullOwner").length() > 0) {
               GameProfile gameProfile2 = new GameProfile((UUID)null, tagCompound.getString("SkullOwner"));
               gameProfile = TileEntitySkull.updateGameprofile(gameProfile2);
               tagCompound.removeTag("SkullOwner");
               tagCompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameProfile));
            }
         }

         if (TileEntitySkullRenderer.instance != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, 0.0F, -0.5F);
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            GlStateManager.disableCull();
            TileEntitySkullRenderer.instance.renderSkull(0.0F, 0.0F, 0.0F, EnumFacing.UP, 0.0F, itemStackIn.getMetadata(), gameProfile, -1);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
         }
      } else {
         Block blockFromItem = Block.getBlockFromItem(itemStackIn.getItem());
         if (blockFromItem == Blocks.ender_chest) {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.enderChest, 0.0, 0.0, 0.0, 0.0F);
         } else if (blockFromItem == Blocks.trapped_chest) {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.trappedChest, 0.0, 0.0, 0.0, 0.0F);
         } else {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.chest, 0.0, 0.0, 0.0, 0.0F);
         }
      }
   }
}
