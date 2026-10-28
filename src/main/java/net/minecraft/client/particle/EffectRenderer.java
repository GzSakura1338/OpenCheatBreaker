package net.minecraft.client.particle;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.impl.ParticlesMod;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class EffectRenderer {
    public static ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");
    public List[] fxLayers = new List[4];
    public Random rand = new Random();
    public World worldObj;
    public TextureManager renderer;

    public EffectRenderer(World world, TextureManager textureManager) throws java.io.IOException {
        if (world != null) {
            this.worldObj = world;
        }

        this.renderer = textureManager;

        for (int i = 0; i < 4; i++) {
            this.fxLayers[i] = new ArrayList();
            Minecraft.getMinecraft().initializationScreen.advance();
        }
    }

    public void addEffect(EntityFX entityFX) {
        int fXLayer = entityFX.getFXLayer();
        if (this.fxLayers[fXLayer].size() >= 4000) {
            this.fxLayers[fXLayer].remove(0);
        }

        this.fxLayers[fXLayer].add(entityFX);
    }

    public void updateEffects() {
        for (int i = 0; i < 4; i++) {
            final int n = i;

            for (int j = 0; j < this.fxLayers[n].size(); j++) {
                final EntityFX entityFX = (EntityFX)this.fxLayers[n].get(j);

                try {
                    entityFX.onUpdate();
                } catch (Throwable e) {
                    CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Ticking Particle");
                    CrashReportCategory makeCategory = makeCrashReport.makeCategory("Particle being ticked");
                    makeCategory.addCrashSectionCallable("Particle", new Callable() {
                        public String call() {
                            return entityFX.toString();
                        }
                    });
                    makeCategory.addCrashSectionCallable(
                        "Particle Type",
                        new Callable() {
                            public String call() {
                                return n == 0
                                    ? "MISC_TEXTURE"
                                    : (n == 1 ? "TERRAIN_TEXTURE" : (n == 2 ? "ITEM_TEXTURE" : (n == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + n)));
                            }
                        }
                    );
                    throw new ReportedException(makeCrashReport);
                }

                if (entityFX.isDead) {
                    this.fxLayers[n].remove(j--);
                }
            }
        }
    }

    public void renderParticles(Entity entity, float f) {
        float rotationX = ActiveRenderInfo.rotationX;
        float rotationZ = ActiveRenderInfo.rotationZ;
        float rotationYZ = ActiveRenderInfo.rotationYZ;
        float rotationXY = ActiveRenderInfo.rotationXY;
        float rotationXZ = ActiveRenderInfo.rotationXZ;
        EntityFX.interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * f;
        EntityFX.interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * f;
        EntityFX.interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * f;

        for (int i = 0; i < 3; i++) {
            final int n = i;
            if (!this.fxLayers[n].isEmpty()) {
                switch (n) {
                    case 0:
                    default:
                        this.renderer.bindTexture(particleTextures);
                        break;
                    case 1:
                        this.renderer.bindTexture(TextureMap.locationBlocksTexture);
                        break;
                    case 2:
                        this.renderer.bindTexture(TextureMap.locationItemsTexture);
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDepthMask(false);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glAlphaFunc(516, 0.003921569F);
                Tessellator instance = Tessellator.instance;
                instance.startDrawingQuads();

                for (int j = 0; j < this.fxLayers[n].size(); j++) {
                    final EntityFX entityFX = (EntityFX)this.fxLayers[n].get(j);
                    instance.setBrightness(entityFX.getBrightnessForRender(f));

                    try {
                        if (CheatBreaker.getInstance().getKeybindSettings().shouldRenderEntity(entityFX)) {
                            entityFX.renderParticle(instance, f, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY);
                        }
                    } catch (Throwable e) {
                        CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Rendering Particle");
                        CrashReportCategory makeCategory = makeCrashReport.makeCategory("Particle being rendered");
                        makeCategory.addCrashSectionCallable("Particle", new Callable() {
                            public String call() {
                                return entityFX.toString();
                            }
                        });
                        makeCategory.addCrashSectionCallable(
                            "Particle Type",
                            new Callable() {
                                public String call() {
                                    return n == 0
                                        ? "MISC_TEXTURE"
                                        : (n == 1 ? "TERRAIN_TEXTURE" : (n == 2 ? "ITEM_TEXTURE" : (n == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + n)));
                                }
                            }
                        );
                        throw new ReportedException(makeCrashReport);
                    }
                }

                instance.draw();
                GL11.glDisable(3042);
                GL11.glDepthMask(true);
                GL11.glAlphaFunc(516, 0.1F);
            }
        }
    }

    public void renderLitParticles(Entity entity, float f) {
        float f2 = (float) (Math.PI / 180.0);
        float cos = MathHelper.cos(entity.rotationYaw * (float) (Math.PI / 180.0));
        float sin = MathHelper.sin(entity.rotationYaw * (float) (Math.PI / 180.0));
        float f3 = -sin * MathHelper.sin(entity.rotationPitch * (float) (Math.PI / 180.0));
        float f4 = cos * MathHelper.sin(entity.rotationPitch * (float) (Math.PI / 180.0));
        float cos2 = MathHelper.cos(entity.rotationPitch * (float) (Math.PI / 180.0));
        byte b = 3;
        List list = this.fxLayers[b];
        if (!list.isEmpty()) {
            Tessellator instance = Tessellator.instance;

            for (int i = 0; i < list.size(); i++) {
                EntityFX entityFX = (EntityFX)list.get(i);
                instance.setBrightness(entityFX.getBrightnessForRender(f));
                entityFX.renderParticle(instance, f, cos, cos2, sin, f3, f4);
            }
        }
    }

    public void clearEffects(World world) {
        this.worldObj = world;

        for (int i = 0; i < 4; i++) {
            this.fxLayers[i].clear();
        }
    }

    public void addBlockDestroyEffects(int n, int n2, int n3, Block block, int n4) {
        ParticlesMod particlesMod = CheatBreaker.getInstance().getModManager().particlesMod;
        if (!particlesMod.isEnabled() || particlesMod.showBlockBreakingParticles.getBooleanValue()) {
            if (block.getMaterial() != Material.air) {
                byte b = 4;

                for (int i = 0; i < b; i++) {
                    for (int j = 0; j < b; j++) {
                        for (int k = 0; k < b; k++) {
                            double d = n + (i + 0.5) / b;
                            double d2 = n2 + (j + 0.5) / b;
                            double d3 = n3 + (k + 0.5) / b;
                            this.addEffect(
                                new EntityDiggingFX(this.worldObj, d, d2, d3, d - n - 0.5, d2 - n2 - 0.5, d3 - n3 - 0.5, block, n4)
                                    .applyColourMultiplier(n, n2, n3)
                            );
                        }
                    }
                }
            }
        }
    }

    public void addBlockHitEffects(int n, int n2, int n3, int n4) {
        ParticlesMod particlesMod = CheatBreaker.getInstance().getModManager().particlesMod;
        if (!particlesMod.isEnabled() || particlesMod.showBlockBreakingParticles.getBooleanValue()) {
            Block block = this.worldObj.getBlock(n, n2, n3);
            if (block.getMaterial() != Material.air) {
                float f = 0.1F;
                double d = n
                    + this.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - f * 2.0F)
                    + f
                    + block.getBlockBoundsMinX();
                double d2 = n2
                    + this.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - f * 2.0F)
                    + f
                    + block.getBlockBoundsMinY();
                double d3 = n3
                    + this.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - f * 2.0F)
                    + f
                    + block.getBlockBoundsMinZ();
                if (n4 == 0) {
                    d2 = n2 + block.getBlockBoundsMinY() - f;
                }

                if (n4 == 1) {
                    d2 = n2 + block.getBlockBoundsMaxY() + f;
                }

                if (n4 == 2) {
                    d3 = n3 + block.getBlockBoundsMinZ() - f;
                }

                if (n4 == 3) {
                    d3 = n3 + block.getBlockBoundsMaxZ() + f;
                }

                if (n4 == 4) {
                    d = n + block.getBlockBoundsMinX() - f;
                }

                if (n4 == 5) {
                    d = n + block.getBlockBoundsMaxX() + f;
                }

                this.addEffect(
                    new EntityDiggingFX(this.worldObj, d, d2, d3, 0.0, 0.0, 0.0, block, this.worldObj.getBlockMetadata(n, n2, n3))
                        .applyColourMultiplier(n, n2, n3)
                        .multiplyVelocity(0.2F)
                        .multipleParticleScaleBy(0.6F)
                );
            }
        }
    }

    public String getStatistics() {
        return "" + (this.fxLayers[0].size() + this.fxLayers[1].size() + this.fxLayers[2].size());
    }
}
