package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntityHugeExplodeFX extends EntityFX {
    public int timeSinceStart;
    public final int maximumTime = 8;

    public EntityHugeExplodeFX(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f2, float f3, float f4, float f5, float f6) {
    }

    @Override
    public void onUpdate() {
        for (int i = 0; i < 6; i++) {
            double d = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
            double d2 = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
            double d3 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
            this.worldObj.spawnParticle("largeexplode", d, d2, d3, this.timeSinceStart / 8.0F, 0.0, 0.0);
        }

        this.timeSinceStart++;
        if (this.timeSinceStart == 8) {
            this.setDead();
        }
    }

    @Override
    public int getFXLayer() {
        return 1;
    }
}
