package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntitySplashFX extends EntityRainFX {
    public EntitySplashFX(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3);
        this.particleGravity = 0.04F;
        this.nextTextureIndexX();
        if (d5 == 0.0 && (d4 != 0.0 || d6 != 0.0)) {
            this.motionX = d4;
            this.motionY = d5 + 0.1;
            this.motionZ = d6;
        }
    }
}
