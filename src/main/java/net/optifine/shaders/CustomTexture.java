package net.optifine.shaders;

import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;

public class CustomTexture implements ICustomTexture {
   public int textureUnit = -1;
   public String path = null;
   public ITextureObject texture = null;

   public CustomTexture(int n, String str, ITextureObject textureObject) {
      this.textureUnit = n;
      this.path = str;
      this.texture = textureObject;
   }

   @Override
   public int getTextureUnit() {
      return this.textureUnit;
   }

   public String getPath() {
      return this.path;
   }

   public ITextureObject getTexture() {
      return this.texture;
   }

   @Override
   public int getTextureId() {
      return this.texture.getGlTextureId();
   }

   @Override
   public void deleteTexture() {
      TextureUtil.deleteTexture(this.texture.getGlTextureId());
   }

   @Override
   public int getTarget() {
      return 3553;
   }

   @Override
   public String toString() {
      return "textureUnit: " + this.textureUnit + ", path: " + this.path + ", glTextureId: " + this.getTextureId();
   }
}
