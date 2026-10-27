package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityItemDoubleEvent;
import com.cheatbreaker.client.event.IBakedModelFloatEvent1;
import com.cheatbreaker.client.event.IBakedModelFloatEvent2;
import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderEntityItem extends Render<EntityItem> {
   public Random random = new Random();
   public RenderItem itemRenderer;

   public RenderEntityItem(RenderManager renderManagerIn, RenderItem renderItem) {
      super(renderManagerIn);
      this.itemRenderer = renderItem;
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   public int computeInt(EntityItem itemIn, double d, double d2, double d3, float f, IBakedModel bakedModel) {
      ItemStack entityItem = itemIn.getEntityItem();
      Item item = entityItem.getItem();
      if (item == null) {
         return 0;
      }

      boolean isGui3d = bakedModel.isGui3d();
      int computeInt = this.computeInt(entityItem);
      float f2 = 0.25F;
      float f3 = MathHelper.sin((itemIn.getAge() + f) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F;
      float f4 = bakedModel.getItemCameraTransforms().getTransform(TransformType.GROUND).scale.y;
      GlStateManager.translate((float)d, (float)d2 + f3 + 0.25F * f4, (float)d3);
      if (isGui3d || this.renderManager.options != null) {
         float f5 = ((itemIn.getAge() + f) / 20.0F + itemIn.hoverStart) * (180.0F / (float)Math.PI);
         IBakedModelFloatEvent1 bakedModelFloatEvent1 = new IBakedModelFloatEvent1(f5, bakedModel);
         CheatBreaker.getInstance().getEventBus().handleEvent(bakedModelFloatEvent1);
         f5 = bakedModelFloatEvent1.getFloatValue();
         if (!bakedModelFloatEvent1.isFlag()) {
            GlStateManager.rotate(f5, 0.0F, 1.0F, 0.0F);
         }

         IBakedModelFloatEvent2 bakedModelFloatEvent2 = new IBakedModelFloatEvent2(f5, bakedModel);
         CheatBreaker.getInstance().getEventBus().handleEvent(bakedModelFloatEvent2);
      }

      if (!isGui3d) {
         float f5 = -0.0F * (computeInt - 1) * 0.5F;
         float f6 = -0.0F * (computeInt - 1) * 0.5F;
         float f7 = -0.046875F * (computeInt - 1) * 0.5F;
         GlStateManager.translate(f5, f6, f7);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      return computeInt;
   }

   public int computeInt(ItemStack stack) {
      byte b = 1;
      if (stack.stackSize > 48) {
         b = 5;
      } else if (stack.stackSize > 32) {
         b = 4;
      } else if (stack.stackSize > 16) {
         b = 3;
      } else if (stack.stackSize > 1) {
         b = 2;
      }

      return b;
   }

   public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
      EntityItemDoubleEvent entityItemDoubleEvent = new EntityItemDoubleEvent(entity, x, y, z);
      CheatBreaker.getInstance().getEventBus().handleEvent(entityItemDoubleEvent);
      if (!entityItemDoubleEvent.isFlag()) {
         ItemStack entityItem = entity.getEntityItem();
         this.random.setSeed(187L);
         boolean flag = false;
         if (this.bindEntityTexture(entity)) {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
            flag = true;
         }

         GlStateManager.enableRescaleNormal();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.pushMatrix();
         IBakedModel itemModel = this.itemRenderer.getItemModelMesher().getItemModel(entityItem);
         int computeInt = this.computeInt(entity, x, y, z, partialTicks, itemModel);

         for (int i = 0; i < computeInt; i++) {
            if (itemModel.isGui3d()) {
               GlStateManager.pushMatrix();
               if (i > 0) {
                  float f = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                  float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                  float f3 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                  GlStateManager.translate(f, f2, f3);
               }

               GlStateManager.scale(0.5F, 0.5F, 0.5F);
               itemModel.getItemCameraTransforms().applyTransform(TransformType.GROUND);
               this.itemRenderer.render(entityItem, itemModel, "dropped", TransformType.GROUND);
               GlStateManager.popMatrix();
            } else {
               GlStateManager.pushMatrix();
               itemModel.getItemCameraTransforms().applyTransform(TransformType.GROUND);
               this.itemRenderer.render(entityItem, itemModel, "dropped", TransformType.GROUND);
               GlStateManager.popMatrix();
               float f = itemModel.getItemCameraTransforms().ground.scale.x;
               float f2 = itemModel.getItemCameraTransforms().ground.scale.y;
               float f3 = itemModel.getItemCameraTransforms().ground.scale.z;
               GlStateManager.translate(0.0F * f, 0.0F * f2, 0.046875F * f3);
            }
         }

         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableBlend();
         this.bindEntityTexture(entity);
         if (flag) {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
         }

         super.doRender(entity, x, y, z, entityYaw, partialTicks);
      }
   }

   public ResourceLocation getEntityTexture(EntityItem entity) {
      return TextureMap.locationBlocksTexture;
   }
}
