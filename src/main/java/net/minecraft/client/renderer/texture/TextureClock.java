package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.optifine.Config;
import shadersmod.client.ShadersTex;

public class TextureClock extends TextureAtlasSprite {
    public double doubleValue;
    public double doubleValue2;

    public TextureClock(String str) {
        super(str);
    }

    @Override
    public void updateAnimation() {
        if (!this.framesTextureData.isEmpty()) {
            Minecraft minecraft = Minecraft.getMinecraft();
            double d = 0.0;
            if (minecraft.theWorld != null && minecraft.thePlayer != null) {
                float celestialAngle = minecraft.theWorld.getCelestialAngle(1.0F);
                d = celestialAngle;
                if (!minecraft.theWorld.provider.isSurfaceWorld()) {
                    d = Math.random();
                }
            }

            double d2 = d - this.doubleValue;

            while (d2 < -0.5) {
                d2++;
            }

            while (d2 >= 0.5) {
                d2--;
            }

            if (d2 < -1.0) {
                d2 = -1.0;
            }

            if (d2 > 1.0) {
                d2 = 1.0;
            }

            this.doubleValue2 += d2 * 0.1;
            this.doubleValue2 *= 0.8;
            this.doubleValue = this.doubleValue + this.doubleValue2;
            int n = (int)((this.doubleValue + 1.0) * this.framesTextureData.size()) % this.framesTextureData.size();

            while (n < 0) {
                n = (n + this.framesTextureData.size()) % this.framesTextureData.size();
            }

            if (n != this.frameCounter) {
                this.frameCounter = n;
                if (Config.isShaders()) {
                    ShadersTex.uploadTexSub(
                        (int[][])this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false
                    );
                } else {
                    TextureUtil.uploadTextureMipmap(
                        (int[][])this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false
                    );
                }
            }
        }
    }
}
