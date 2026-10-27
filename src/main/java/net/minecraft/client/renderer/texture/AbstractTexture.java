package net.minecraft.client.renderer.texture;

import net.minecraft.client.renderer.GlStateManager;
import net.optifine.shaders.MultiTexID;
import net.optifine.shaders.ShadersTex;
import org.lwjgl.opengl.GL11;

public abstract class AbstractTexture implements ITextureObject {
   public int glTextureId = -1;
   public boolean blur;
   public boolean mipmap;
   public boolean blurLast;
   public boolean mipmapLast;
   public MultiTexID multiTex;

   public void setBlurMipmapDirect(boolean flag, boolean flag2) {
      this.blur = flag;
      this.mipmap = flag2;
      int n = -1;
      short s = -1;
      if (flag) {
         n = flag2 ? 9987 : 9729;
         s = 9729;
      } else {
         n = flag2 ? 9986 : 9728;
         s = 9728;
      }

      GlStateManager.bindTexture(this.getGlTextureId());
      GL11.glTexParameteri(3553, 10241, n);
      GL11.glTexParameteri(3553, 10240, s);
   }

   @Override
   public void setBlurMipmap(boolean flag, boolean flag2) {
      this.blurLast = this.blur;
      this.mipmapLast = this.mipmap;
      this.setBlurMipmapDirect(flag, flag2);
   }

   @Override
   public void restoreLastBlurMipmap() {
      this.setBlurMipmapDirect(this.blurLast, this.mipmapLast);
   }

   @Override
   public int getGlTextureId() {
      if (this.glTextureId == -1) {
         this.glTextureId = TextureUtil.glGenTextures();
      }

      return this.glTextureId;
   }

   public void deleteGlTexture() {
      ShadersTex.deleteTextures(this, this.glTextureId);
      if (this.glTextureId != -1) {
         TextureUtil.deleteTexture(this.glTextureId);
         this.glTextureId = -1;
      }
   }

   @Override
   public MultiTexID getMultiTexID() {
      return ShadersTex.getMultiTexID(this);
   }
}
