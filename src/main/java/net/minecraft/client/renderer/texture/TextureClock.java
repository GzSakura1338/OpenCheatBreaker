package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class TextureClock extends TextureAtlasSprite {
   public double currentAngle;
   public double angleDelta;

   public TextureClock(String iconName) {
      super(iconName);
   }

   @Override
   public void updateAnimation() {
      if (!this.framesTextureData.isEmpty()) {
         Minecraft minecraft = Minecraft.getMinecraft();
         double d = 0.0;
         if (minecraft.theWorld != null && minecraft.thePlayer != null) {
            d = minecraft.theWorld.getCelestialAngle(1.0F);
            if (!minecraft.theWorld.provider.isSurfaceWorld()) {
               d = Math.random();
            }
         }

         double d2 = d - this.currentAngle;

         while (d2 < -0.5) {
            d2++;
         }

         while (d2 >= 0.5) {
            d2--;
         }

         d2 = MathHelper.clamp_double(d2, -1.0, 1.0);
         this.angleDelta += d2 * 0.1;
         this.angleDelta *= 0.8;
         this.currentAngle = this.currentAngle + this.angleDelta;
         int n = (int)((this.currentAngle + 1.0) * this.framesTextureData.size()) % this.framesTextureData.size();

         while (n < 0) {
            n = (n + this.framesTextureData.size()) % this.framesTextureData.size();
         }

         if (n != this.frameCounter) {
            this.frameCounter = n;
            TextureUtil.uploadTextureMipmap(this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
         }
      }
   }
}
