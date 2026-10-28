package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFireworkStarterFX extends EntityFX {
    public int fireworkAge;
    public EffectRenderer theEffectRenderer;
    public NBTTagList fireworkExplosions;
    public boolean twinkle;

    public EntityFireworkStarterFX(
        World world, double d, double d2, double d3, double d4, double d5, double d6, EffectRenderer effectRenderer, NBTTagCompound nbttagCompound
    ) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.motionX = d4;
        this.motionY = d5;
        this.motionZ = d6;
        this.theEffectRenderer = effectRenderer;
        this.particleMaxAge = 8;
        if (nbttagCompound != null) {
            this.fireworkExplosions = nbttagCompound.getTagList("Explosions", 10);
            if (this.fireworkExplosions.tagCount() == 0) {
                this.fireworkExplosions = null;
            } else {
                this.particleMaxAge = this.fireworkExplosions.tagCount() * 2 - 1;

                for (int i = 0; i < this.fireworkExplosions.tagCount(); i++) {
                    NBTTagCompound compoundTagAt = this.fireworkExplosions.getCompoundTagAt(i);
                    if (compoundTagAt.getBoolean("Flicker")) {
                        this.twinkle = true;
                        this.particleMaxAge += 15;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f2, float f3, float f4, float f5, float f6) {
    }

    @Override
    public void onUpdate() {
        if (this.fireworkAge == 0 && this.fireworkExplosions != null) {
            boolean check = this.check();
            boolean flag = false;
            if (this.fireworkExplosions.tagCount() >= 3) {
                flag = true;
            } else {
                for (int i = 0; i < this.fireworkExplosions.tagCount(); i++) {
                    NBTTagCompound compoundTagAt = this.fireworkExplosions.getCompoundTagAt(i);
                    if (compoundTagAt.getByte("Type") == 1) {
                        flag = true;
                        break;
                    }
                }
            }

            String str = "fireworks." + (flag ? "largeBlast" : "blast") + (check ? "_far" : "");
            this.worldObj.playSound(this.posX, this.posY, this.posZ, str, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);
        }

        if (this.fireworkAge % 2 == 0 && this.fireworkExplosions != null && this.fireworkAge / 2 < this.fireworkExplosions.tagCount()) {
            int n = this.fireworkAge / 2;
            NBTTagCompound compoundTagAt = this.fireworkExplosions.getCompoundTagAt(n);
            byte byte2 = compoundTagAt.getByte("Type");
            boolean boolean2 = compoundTagAt.getBoolean("Trail");
            boolean boolean3 = compoundTagAt.getBoolean("Flicker");
            int[] intArray = compoundTagAt.getIntArray("Colors");
            int[] intArray2 = compoundTagAt.getIntArray("FadeColors");
            if (byte2 == 1) {
                this.createBall(0.5, 4, intArray, intArray2, boolean2, boolean3);
            } else if (byte2 == 2) {
                this.createShaped(
                    0.5,
                    new double[][]{
                        {0.0, 1.0},
                        {0.3455, 0.309},
                        {0.9511, 0.309},
                        {0.3795918367346939, -0.12653061224489795},
                        {0.6122448979591837, -0.8040816326530612},
                        {0.0, -0.35918367346938773}
                    },
                    intArray,
                    intArray2,
                    boolean2,
                    boolean3,
                    false
                );
            } else if (byte2 == 3) {
                this.createShaped(
                    0.5,
                    new double[][]{
                        {0.0, 0.2},
                        {0.2, 0.2},
                        {0.2, 0.6},
                        {0.6, 0.6},
                        {0.6, 0.2},
                        {0.2, 0.2},
                        {0.2, 0.0},
                        {0.4, 0.0},
                        {0.4, -0.6},
                        {0.2, -0.6},
                        {0.2, -0.4},
                        {0.0, -0.4}
                    },
                    intArray,
                    intArray2,
                    boolean2,
                    boolean3,
                    true
                );
            } else if (byte2 == 4) {
                this.createBurst(intArray, intArray2, boolean2, boolean3);
            } else {
                this.createBall(0.25, 2, intArray, intArray2, boolean2, boolean3);
            }

            int n2 = intArray[0];
            float f = ((n2 & 0xFF0000) >> 16) / 255.0F;
            float f2 = ((n2 & 0xFF00) >> 8) / 255.0F;
            float f3 = ((n2 & 0xFF) >> 0) / 255.0F;
            EntityFireworkOverlayFX entityFireworkOverlayFX = new EntityFireworkOverlayFX(this.worldObj, this.posX, this.posY, this.posZ);
            entityFireworkOverlayFX.setRBGColorF(f, f2, f3);
            this.theEffectRenderer.addEffect(entityFireworkOverlayFX);
        }

        this.fireworkAge++;
        if (this.fireworkAge > this.particleMaxAge) {
            if (this.twinkle) {
                boolean check = this.check();
                String str = "fireworks." + (check ? "twinkle_far" : "twinkle");
                this.worldObj.playSound(this.posX, this.posY, this.posZ, str, 20.0F, 0.9F + this.rand.nextFloat() * 0.15F, true);
            }

            this.setDead();
        }
    }

    public boolean check() {
        Minecraft minecraft = Minecraft.getMinecraft();
        return minecraft == null || minecraft.renderViewEntity == null || minecraft.renderViewEntity.getDistanceSq(this.posX, this.posY, this.posZ) >= 256.0;
    }

    public void createParticle(
        double d, double d2, double d3, double d4, double d5, double d6, int[] ints, int[] ints2, boolean flag, boolean flag2
    ) {
        EntityFireworkSparkFX entityFireworkSparkFX = new EntityFireworkSparkFX(this.worldObj, d, d2, d3, d4, d5, d6, this.theEffectRenderer);
        entityFireworkSparkFX.setTrail(flag);
        entityFireworkSparkFX.setTwinkle(flag2);
        int nextInt = this.rand.nextInt(ints.length);
        entityFireworkSparkFX.setColour(ints[nextInt]);
        if (ints2 != null && ints2.length > 0) {
            entityFireworkSparkFX.setFadeColour(ints2[this.rand.nextInt(ints2.length)]);
        }

        this.theEffectRenderer.addEffect(entityFireworkSparkFX);
    }

    public void createBall(double d, int n, int[] ints, int[] ints2, boolean flag, boolean flag2) {
        double posX = this.posX;
        double posY = this.posY;
        double posZ = this.posZ;

        for (int i = -n; i <= n; i++) {
            for (int j = -n; j <= n; j++) {
                for (int k = -n; k <= n; k++) {
                    double d2 = j + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
                    double d3 = i + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
                    double d4 = k + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
                    double d5 = MathHelper.sqrt_double(d2 * d2 + d3 * d3 + d4 * d4) / d + this.rand.nextGaussian() * 0.05;
                    this.createParticle(posX, posY, posZ, d2 / d5, d3 / d5, d4 / d5, ints, ints2, flag, flag2);
                    if (i != -n && i != n && j != -n && j != n) {
                        k += n * 2 - 1;
                    }
                }
            }
        }
    }

    public void createShaped(double d, double[][] doubleses, int[] ints, int[] ints2, boolean flag, boolean flag2, boolean flag3) {
        double d2 = doubleses[0][0];
        double d3 = doubleses[0][1];
        this.createParticle(this.posX, this.posY, this.posZ, d2 * d, d3 * d, 0.0, ints, ints2, flag, flag2);
        float f = this.rand.nextFloat() * (float) Math.PI;
        double d4 = flag3 ? 0.034 : 0.34;

        for (int i = 0; i < 3; i++) {
            double d5 = f + i * (float) Math.PI * d4;
            double d6 = d2;
            double d7 = d3;

            for (int j = 1; j < doubleses.length; j++) {
                double d8 = doubleses[j][0];
                double d9 = doubleses[j][1];

                for (double d10 = 0.25; d10 <= 1.0; d10 += 0.25) {
                    double d11 = (d6 + (d8 - d6) * d10) * d;
                    double d12 = (d7 + (d9 - d7) * d10) * d;
                    double d13 = d11 * Math.sin(d5);
                    d11 *= Math.cos(d5);

                    for (double d14 = -1.0; d14 <= 1.0; d14 += 2.0) {
                        this.createParticle(this.posX, this.posY, this.posZ, d11 * d14, d12, d13 * d14, ints, ints2, flag, flag2);
                    }
                }

                d6 = d8;
                d7 = d9;
            }
        }
    }

    public void createBurst(int[] ints, int[] ints2, boolean flag, boolean flag2) {
        double d = this.rand.nextGaussian() * 0.05;
        double d2 = this.rand.nextGaussian() * 0.05;

        for (int i = 0; i < 70; i++) {
            double d3 = this.motionX * 0.5 + this.rand.nextGaussian() * 0.15 + d;
            double d4 = this.motionZ * 0.5 + this.rand.nextGaussian() * 0.15 + d2;
            double d5 = this.motionY * 0.5 + this.rand.nextDouble() * 0.5;
            this.createParticle(this.posX, this.posY, this.posZ, d3, d5, d4, ints, ints2, flag, flag2);
        }
    }

    @Override
    public int getFXLayer() {
        return 0;
    }
}
