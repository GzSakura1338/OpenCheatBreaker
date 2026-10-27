package net.optifine.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class CustomTextureLocation implements ICustomTexture {
   public int textureUnit = -1;
   public int variant = 0;
   public ResourceLocation location;
   public ITextureObject texture;
   public static final int VARIANT_BASE = 0;
   public static final int VARIANT_NORMAL = 1;
   public static final int VARIANT_SPECULAR = 2;

   public CustomTextureLocation(int n, ResourceLocation resourceLocation, int n2) {
      this.textureUnit = n;
      this.location = resourceLocation;
      this.variant = n2;
   }

   public ITextureObject getTexture() {
      if (this.texture == null) {
         TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
         this.texture = textureManager.getTexture(this.location);
         if (this.texture == null) {
            this.texture = new SimpleTexture(this.location);
            textureManager.loadTexture(this.location, this.texture);
            this.texture = textureManager.getTexture(this.location);
         }
      }

      return this.texture;
   }

   @Override
   public int getTextureId() {
      ITextureObject texture = this.getTexture();
      if (this.variant != 0 && texture instanceof AbstractTexture) {
         AbstractTexture abstractTexture = (AbstractTexture)texture;
         MultiTexID multiTex = abstractTexture.multiTex;
         if (multiTex != null) {
            if (this.variant == 1) {
               return multiTex.norm;
            }

            if (this.variant == 2) {
               return multiTex.spec;
            }
         }
      }

      return texture.getGlTextureId();
   }

   @Override
   public int getTextureUnit() {
      return this.textureUnit;
   }

   @Override
   public void deleteTexture() {
   }

   @Override
   public int getTarget() {
      return 3553;
   }

   @Override
   public String toString() {
      return "textureUnit: "
         + this.textureUnit
         + ", location: "
         + this.location
         + ", glTextureId: "
         + (this.texture != null ? this.texture.getGlTextureId() : "");
   }
}
