package net.minecraft.client.model;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.entity.model.anim.ModelUpdater;
import net.optifine.model.ModelSprite;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public class ModelRenderer {
   public List spriteList = new ArrayList();
   public boolean mirrorV = false;
   public int textureOffsetX;
   public int textureOffsetY;
   public float rotationPointX;
   public float rotationPointY;
   public float rotationPointZ;
   public float rotateAngleX;
   public float rotateAngleY;
   public float rotateAngleZ;
   public boolean compiled;
   public int displayList;
   public boolean mirror;
   public float scaleX = 1.0F;
   public boolean isHidden;
   public float scaleY = 1.0F;
   public List<ModelRenderer> childModels;
   public float scaleZ = 1.0F;
   public ResourceLocation textureLocation = null;
   public float offsetX;
   public float offsetY;
   public float offsetZ;
   public String text = null;
   public RenderGlobal renderGlobal = Config.getRenderGlobal();
   public float textureWidth = 64.0F;
   public float textureHeight = 32.0F;
   public boolean showModel = true;
   public int countResetDisplayList;
   public List<ModelBox> cubeList = Lists.newArrayList();
   public ModelBase baseModel;
   public ModelUpdater modelUpdater;
   public String boxName;

   public ModelRenderer(ModelBase model, String boxNameIn) {
      this.baseModel = model;
      model.boxList.add(this);
      this.boxName = boxNameIn;
      this.setTextureSize(model.textureWidth, model.textureHeight);
   }

   public ModelRenderer(ModelBase model) {
      this(model, null);
   }

   public ModelRenderer(ModelBase model, int texOffX, int texOffY) {
      this(model);
      this.setTextureOffset(texOffX, texOffY);
   }

   public void addChild(ModelRenderer renderer) {
      if (this.childModels == null) {
         this.childModels = Lists.newArrayList();
      }

      this.childModels.add(renderer);
   }

   public ModelRenderer setTextureOffset(int x, int y) {
      this.textureOffsetX = x;
      this.textureOffsetY = y;
      return this;
   }

   public ModelRenderer addBox(String partName, float offX, float offY, float offZ, int width, int height, int depth) {
      partName = this.boxName + "." + partName;
      TextureOffset textureOffset = this.baseModel.getTextureOffset(partName);
      this.setTextureOffset(textureOffset.textureOffsetX, textureOffset.textureOffsetY);
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F).setBoxName(partName));
      return this;
   }

   public ModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F));
      return this;
   }

   public ModelRenderer addBox(float f, float f2, float f3, int n, int n2, int n3, boolean flag) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, f, f2, f3, n, n2, n3, 0.0F, flag));
      return this;
   }

   public void addBox(float f, float f2, float f3, int width, int height, int depth, float scaleFactor) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, f, f2, f3, width, height, depth, scaleFactor));
   }

   public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn) {
      this.rotationPointX = rotationPointXIn;
      this.rotationPointY = rotationPointYIn;
      this.rotationPointZ = rotationPointZIn;
   }

   public void render(float f) {
      if (!this.isHidden && this.showModel) {
         this.checkResetDisplayList();
         if (!this.compiled) {
            this.compileDisplayList(f);
         }

         int n = 0;
         if (this.textureLocation != null && !this.renderGlobal.renderOverlayDamaged) {
            if (this.renderGlobal.renderOverlayEyes) {
               return;
            }

            n = GlStateManager.getBoundTexture();
            Config.getTextureManager().bindTexture(this.textureLocation);
         }

         if (this.modelUpdater != null) {
            this.modelUpdater.update();
         }

         boolean flag = this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F;
         GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);
         if (this.rotateAngleX != 0.0F || this.rotateAngleY != 0.0F || this.rotateAngleZ != 0.0F) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
            if (this.rotateAngleZ != 0.0F) {
               GlStateManager.rotate(this.rotateAngleZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            if (this.rotateAngleY != 0.0F) {
               GlStateManager.rotate(this.rotateAngleY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
               GlStateManager.rotate(this.rotateAngleX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }

            if (flag) {
               GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
            }

            GlStateManager.callList(this.displayList);
            if (this.childModels != null) {
               for (int i = 0; i < this.childModels.size(); i++) {
                  this.childModels.get(i).render(f);
               }
            }

            GlStateManager.popMatrix();
         } else if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
            if (flag) {
               GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
            }

            GlStateManager.callList(this.displayList);
            if (this.childModels != null) {
               for (ModelRenderer modelRenderer : this.childModels) {
                  modelRenderer.render(f);
               }
            }

            if (flag) {
               GlStateManager.scale(1.0F / this.scaleX, 1.0F / this.scaleY, 1.0F / this.scaleZ);
            }
         } else {
            GlStateManager.translate(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
            if (flag) {
               GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
            }

            GlStateManager.callList(this.displayList);
            if (this.childModels != null) {
               for (int i = 0; i < this.childModels.size(); i++) {
                  this.childModels.get(i).render(f);
               }
            }

            if (flag) {
               GlStateManager.scale(1.0F / this.scaleX, 1.0F / this.scaleY, 1.0F / this.scaleZ);
            }

            GlStateManager.translate(-this.rotationPointX * f, -this.rotationPointY * f, -this.rotationPointZ * f);
         }

         GlStateManager.translate(-this.offsetX, -this.offsetY, -this.offsetZ);
         if (n != 0) {
            GlStateManager.bindTexture(n);
         }
      }
   }

   public void renderWithRotation(float f) {
      if (!this.isHidden && this.showModel) {
         this.checkResetDisplayList();
         if (!this.compiled) {
            this.compileDisplayList(f);
         }

         int n = 0;
         if (this.textureLocation != null && !this.renderGlobal.renderOverlayDamaged) {
            if (this.renderGlobal.renderOverlayEyes) {
               return;
            }

            n = GlStateManager.getBoundTexture();
            Config.getTextureManager().bindTexture(this.textureLocation);
         }

         if (this.modelUpdater != null) {
            this.modelUpdater.update();
         }

         boolean flag = this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translate(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
         if (this.rotateAngleY != 0.0F) {
            GlStateManager.rotate(this.rotateAngleY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         }

         if (this.rotateAngleX != 0.0F) {
            GlStateManager.rotate(this.rotateAngleX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         }

         if (this.rotateAngleZ != 0.0F) {
            GlStateManager.rotate(this.rotateAngleZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
         }

         if (flag) {
            GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
         }

         GlStateManager.callList(this.displayList);
         if (this.childModels != null) {
            for (int i = 0; i < this.childModels.size(); i++) {
               this.childModels.get(i).render(f);
            }
         }

         GlStateManager.popMatrix();
         if (n != 0) {
            GlStateManager.bindTexture(n);
         }
      }
   }

   public void postRender(float scale) {
      if (!this.isHidden && this.showModel) {
         this.checkResetDisplayList();
         if (!this.compiled) {
            this.compileDisplayList(scale);
         }

         if (this.rotateAngleX != 0.0F || this.rotateAngleY != 0.0F || this.rotateAngleZ != 0.0F) {
            GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            if (this.rotateAngleZ != 0.0F) {
               GlStateManager.rotate(this.rotateAngleZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            if (this.rotateAngleY != 0.0F) {
               GlStateManager.rotate(this.rotateAngleY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
               GlStateManager.rotate(this.rotateAngleX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }
         } else if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
            GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
         }
      }
   }

   public void compileDisplayList(float scale) {
      if (this.displayList == 0) {
         this.displayList = GLAllocation.generateDisplayLists(1);
      }

      GL11.glNewList(this.displayList, 4864);
      WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

      for (int i = 0; i < this.cubeList.size(); i++) {
         this.cubeList.get(i).render(worldRenderer, scale);
      }

      for (int i = 0; i < this.spriteList.size(); i++) {
         ModelSprite modelSprite = (ModelSprite)this.spriteList.get(i);
         modelSprite.render(Tessellator.getInstance(), scale);
      }

      GL11.glEndList();
      this.compiled = true;
   }

   public ModelRenderer setTextureSize(int textureWidthIn, int textureHeightIn) {
      this.textureWidth = textureWidthIn;
      this.textureHeight = textureHeightIn;
      return this;
   }

   public void addSprite(float f, float f2, float f3, int n, int n2, int n3, float f4) {
      this.spriteList.add(new ModelSprite(this, this.textureOffsetX, this.textureOffsetY, f, f2, f3, n, n2, n3, f4));
   }

   public boolean getCompiled() {
      return this.compiled;
   }

   public int getDisplayList() {
      return this.displayList;
   }

   public void checkResetDisplayList() {
      if (this.countResetDisplayList != Shaders.countResetDisplayLists) {
         this.compiled = false;
         this.countResetDisplayList = Shaders.countResetDisplayLists;
      }
   }

   public ResourceLocation getTextureLocation() {
      return this.textureLocation;
   }

   public void setTextureLocation(ResourceLocation resourceLocation) {
      this.textureLocation = resourceLocation;
   }

   public String getId() {
      return this.text;
   }

   public void setId(String str) {
      this.text = str;
   }

   public void addBox(int[][] intses, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
      this.cubeList.add(new ModelBox(this, intses, f, f2, f3, f4, f5, f6, f7, this.mirror));
   }

   public ModelRenderer getChild(String str) {
      if (str == null) {
         return null;
      }

      if (this.childModels != null) {
         for (int i = 0; i < this.childModels.size(); i++) {
            ModelRenderer modelRenderer = this.childModels.get(i);
            if (str.equals(modelRenderer.getId())) {
               return modelRenderer;
            }
         }
      }

      return null;
   }

   public ModelRenderer getChildDeep(String str) {
      if (str == null) {
         return null;
      }

      ModelRenderer child = this.getChild(str);
      if (child != null) {
         return child;
      }

      if (this.childModels != null) {
         for (int i = 0; i < this.childModels.size(); i++) {
            ModelRenderer modelRenderer = this.childModels.get(i);
            ModelRenderer childDeep = modelRenderer.getChildDeep(str);
            if (childDeep != null) {
               return childDeep;
            }
         }
      }

      return null;
   }

   public void setModelUpdater(ModelUpdater modelUpdater) {
      this.modelUpdater = modelUpdater;
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append(
         "id: "
            + this.text
            + ", boxes: "
            + (this.cubeList != null ? this.cubeList.size() : null)
            + ", submodels: "
            + (this.childModels != null ? this.childModels.size() : null)
      );
      return sb.toString();
   }
}
