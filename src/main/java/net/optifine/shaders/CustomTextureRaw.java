package net.optifine.shaders;

import java.nio.ByteBuffer;
import net.optifine.texture.InternalFormat;
import net.optifine.texture.PixelFormat;
import net.optifine.texture.PixelType;
import net.optifine.texture.TextureType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class CustomTextureRaw implements ICustomTexture {
   public TextureType type;
   public int textureUnit;
   public int textureId;

   public CustomTextureRaw(
      TextureType textureType,
      InternalFormat internalFormat,
      int n,
      int n2,
      int n3,
      PixelFormat pixelFormat,
      PixelType pixelType,
      ByteBuffer byteBuffer,
      int n4,
      boolean flag,
      boolean flag2
   ) {
      this.type = textureType;
      this.textureUnit = n4;
      this.textureId = GL11.glGenTextures();
      GL11.glBindTexture(this.getTarget(), this.textureId);
      int n5 = flag2 ? 33071 : 10497;
      int n6 = flag ? 9729 : 9728;
      switch (textureType) {
         case TEXTURE_1D:
            GL11.glTexImage1D(3552, 0, internalFormat.getId(), n, 0, pixelFormat.getId(), pixelType.getId(), byteBuffer);
            GL11.glTexParameteri(3552, 10242, n5);
            GL11.glTexParameteri(3552, 10240, n6);
            GL11.glTexParameteri(3552, 10241, n6);
            break;
         case TEXTURE_2D:
            GL11.glTexImage2D(3553, 0, internalFormat.getId(), n, n2, 0, pixelFormat.getId(), pixelType.getId(), byteBuffer);
            GL11.glTexParameteri(3553, 10242, n5);
            GL11.glTexParameteri(3553, 10243, n5);
            GL11.glTexParameteri(3553, 10240, n6);
            GL11.glTexParameteri(3553, 10241, n6);
            break;
         case TEXTURE_3D:
            GL12.glTexImage3D(32879, 0, internalFormat.getId(), n, n2, n3, 0, pixelFormat.getId(), pixelType.getId(), byteBuffer);
            GL11.glTexParameteri(32879, 10242, n5);
            GL11.glTexParameteri(32879, 10243, n5);
            GL11.glTexParameteri(32879, 32882, n5);
            GL11.glTexParameteri(32879, 10240, n6);
            GL11.glTexParameteri(32879, 10241, n6);
            break;
         case TEXTURE_RECTANGLE:
            GL11.glTexImage2D(34037, 0, internalFormat.getId(), n, n2, 0, pixelFormat.getId(), pixelType.getId(), byteBuffer);
            GL11.glTexParameteri(34037, 10242, n5);
            GL11.glTexParameteri(34037, 10243, n5);
            GL11.glTexParameteri(34037, 10240, n6);
            GL11.glTexParameteri(34037, 10241, n6);
      }

      GL11.glBindTexture(this.getTarget(), 0);
   }

   @Override
   public int getTarget() {
      return this.type.getId();
   }

   @Override
   public int getTextureId() {
      return this.textureId;
   }

   @Override
   public int getTextureUnit() {
      return this.textureUnit;
   }

   @Override
   public void deleteTexture() {
      if (this.textureId > 0) {
         GL11.glDeleteTextures(this.textureId);
         this.textureId = 0;
      }
   }
}
