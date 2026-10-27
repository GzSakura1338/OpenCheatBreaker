package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StringUtils;

public class LayerCustomHead implements LayerRenderer<EntityLivingBase> {
   public ModelRenderer modelPart;

   public LayerCustomHead(ModelRenderer modelRenderer) {
      this.modelPart = modelRenderer;
   }

   @Override
   public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      ItemStack currentArmor = entitylivingbaseIn.getCurrentArmor(3);
      if (currentArmor != null && currentArmor.getItem() != null) {
         Item item = currentArmor.getItem();
         Minecraft minecraft = Minecraft.getMinecraft();
         GlStateManager.pushMatrix();
         if (entitylivingbaseIn.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         boolean flag = entitylivingbaseIn instanceof EntityVillager
            || entitylivingbaseIn instanceof EntityZombie && ((EntityZombie)entitylivingbaseIn).isVillager();
         if (!flag && entitylivingbaseIn.isChild()) {
            float f6 = 2.0F;
            float f7 = 1.4F;
            GlStateManager.scale(f7 / f6, f7 / f6, f7 / f6);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
         }

         this.modelPart.postRender(0.0625F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         if (item instanceof ItemBlock) {
            float f6 = 0.625F;
            GlStateManager.translate(0.0F, -0.25F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(f6, -f6, -f6);
            if (flag) {
               GlStateManager.translate(0.0F, 0.1875F, 0.0F);
            }

            minecraft.getItemRenderer().renderItem(entitylivingbaseIn, currentArmor, TransformType.HEAD);
         } else if (item == Items.skull) {
            float f6 = 1.1875F;
            GlStateManager.scale(f6, -f6, -f6);
            if (flag) {
               GlStateManager.translate(0.0F, 0.0625F, 0.0F);
            }

            GameProfile gameProfile = null;
            if (currentArmor.hasTagCompound()) {
               NBTTagCompound tagCompound = currentArmor.getTagCompound();
               if (tagCompound.hasKey("SkullOwner", 10)) {
                  gameProfile = NBTUtil.readGameProfileFromNBT(tagCompound.getCompoundTag("SkullOwner"));
               } else if (tagCompound.hasKey("SkullOwner", 8)) {
                  String string = tagCompound.getString("SkullOwner");
                  if (!StringUtils.isNullOrEmpty(string)) {
                     gameProfile = TileEntitySkull.updateGameprofile(new GameProfile((UUID)null, string));
                     tagCompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameProfile));
                  }
               }
            }

            TileEntitySkullRenderer.instance.renderSkull(-0.5F, 0.0F, -0.5F, EnumFacing.UP, 180.0F, currentArmor.getMetadata(), gameProfile, -1);
         }

         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
