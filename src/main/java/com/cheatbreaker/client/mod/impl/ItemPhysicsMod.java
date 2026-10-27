package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.EntityItemDoubleEvent;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ItemPhysicsMod extends AbstractMod {
   public long longValue;
   public double doubleValue;
   public Random random = new Random();
   public Setting rotationSpeed;

   public ResourceLocation getResourceLocation2() {
      return TextureMap.locationBlocksTexture;
   }

   public int computeInt(ItemStack itemStack) {
      if (itemStack.stackSize > 48) {
         return 5;
      } else if (itemStack.stackSize > 32) {
         return 4;
      } else if (itemStack.stackSize > 16) {
         return 3;
      } else {
         return itemStack.stackSize > 1 ? 2 : 1;
      }
   }

   public void onEntityItemDoubleEvent(EntityItemDoubleEvent entityItemDoubleEvent) {
      entityItemDoubleEvent.setFlag(true);
      this.doubleValue = (System.nanoTime() - this.longValue) / 2500000.0 * (this.rotationSpeed.getDoubleValue() / 1000.0);
      if (!this.mc.inGameHasFocus) {
         this.doubleValue = 0.0;
      }

      EntityItem entityItem;
      ItemStack itemStack;
      int n = (itemStack = (entityItem = entityItemDoubleEvent.getEntityItem()).getEntityItem()) != null && itemStack.getItem() != null
         ? Item.getIdFromItem(itemStack.getItem()) + itemStack.getMetadata()
         : 187;
      this.random.setSeed(n);
      this.mc.getTextureManager().bindTexture(this.getResourceLocation2());
      this.mc.getTextureManager().getTexture(this.getResourceLocation2()).setBlurMipmap(false, false);
      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.pushMatrix();
      IBakedModel itemModel = this.mc.getRenderItem().getItemModelMesher().getItemModel(itemStack);
      boolean isGui3d = itemModel.isGui3d();
      boolean isGui3d2 = itemModel.isGui3d();
      int computeInt = this.computeInt(itemStack);
      GlStateManager.translate((float)entityItemDoubleEvent.getX(), (float)entityItemDoubleEvent.getY(), (float)entityItemDoubleEvent.getZ());
      if (itemModel.isGui3d()) {
         GlStateManager.scale(0.5F, 0.5F, 0.5F);
      }

      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(entityItem.rotationYaw, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(0.0, 0.0, isGui3d2 ? -0.08 : -0.04);
      if (isGui3d2 || this.mc.getRenderManager().options != null) {
         if (isGui3d2) {
            if (!entityItem.onGround) {
               double d = this.doubleValue * 2.0;
               entityItem.rotationPitch = (float)(entityItem.rotationPitch + d);
            }
         } else if (!Double.isNaN(entityItem.posX) && !Double.isNaN(entityItem.posY) && !Double.isNaN(entityItem.posZ) && entityItem.worldObj != null) {
            if (entityItem.onGround) {
               entityItem.rotationPitch = 0.0F;
            } else {
               double d = this.doubleValue * 2.0;
               entityItem.rotationPitch = (float)(entityItem.rotationPitch + d);
            }
         }

         GlStateManager.rotate(entityItem.rotationPitch, 1.0F, 0.0F, 0.0F);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

      for (int i = 0; i < computeInt; i++) {
         GlStateManager.pushMatrix();
         if (isGui3d) {
            if (i > 0) {
               float f = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f3 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               GlStateManager.translate(f, f2, f3);
            }

            this.mc.getRenderItem().render(itemStack, itemModel, "dropped", TransformType.GROUND);
            GlStateManager.popMatrix();
         } else {
            this.mc.getRenderItem().render(itemStack, itemModel, "dropped", TransformType.GROUND);
            GlStateManager.popMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.05375F);
         }
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      this.mc.getTextureManager().bindTexture(this.getResourceLocation2());
      this.mc.getTextureManager().getTexture(this.getResourceLocation2()).restoreLastBlurMipmap();
   }

   public ItemPhysicsMod() {
      super("Item Physics");
      this.setLabel("Item Physics", 1.0F);
      this.setCategories(ModCategory.MISC);
      this.rotationSpeed = new Setting(this, "Rotation Speed").changeValue(50.0).setRange(10.0, 100.0);
      this.addEventHandler(ClientTickEvent.class, clientTickEvent -> this.longValue = System.nanoTime());
      this.addEventHandler(EntityItemDoubleEvent.class, this::onEntityItemDoubleEvent);
   }
}
