package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.FloatEvent4;
import com.cheatbreaker.client.mod.impl.NametagMod;
import com.cheatbreaker.client.mod.impl.PackTweaksMod;
import com.cheatbreaker.client.network.PlayerData;
import com.cheatbreaker.client.network.PlayerDataManager;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.PreviewPlayer;
import java.awt.Color;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.Config;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public abstract class Render {
    public static ResourceLocation shadowTextures = new ResourceLocation("textures/misc/shadow.png");
    public RenderManager renderManager;
    public RenderBlocks renderBlocks = new RenderBlocks();
    public float shadowSize;
    public ResourceLocation logoWhite1Png = new ResourceLocation("client/logo_white_1.png");
    public ResourceLocation logoWhite2Png = new ResourceLocation("client/logo_white_2.png");
    public float shadowOpaque = 1.0F;
    public boolean staticEntity = false;
    public Pattern pattern = Pattern.compile("(?i)§[0-689A-E]");

    public abstract void doRender(Entity entity, double d, double d2, double d3, float f, float f2);

    public abstract ResourceLocation getEntityTexture(Entity entity);

    public boolean isStaticEntity() {
        return this.staticEntity;
    }

    public void bindEntityTexture(Entity entity) {
        this.bindTexture(this.getEntityTexture(entity));
    }

    public void bindTexture(ResourceLocation resourceLocation) {
        this.renderManager.renderEngine.bindTexture(resourceLocation);
    }

    public void renderEntityOnFire(Entity entity, double d, double d2, double d3, float f) {
        GL11.glDisable(2896);
        IIcon fireIcon = Blocks.fire.getFireIcon(0);
        IIcon fireIcon2 = Blocks.fire.getFireIcon(1);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d, (float)d2, (float)d3);
        float f2 = entity.width * 1.4F;
        GL11.glScalef(f2, f2, f2);
        Tessellator instance = Tessellator.instance;
        float f3 = 0.5F;
        float f4 = 0.0F;
        float f5 = entity.height / f2;
        float f6 = (float)(entity.posY - entity.boundingBox.minY);
        GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.0F, -0.3F + (int)f5 * 0.02F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f7 = 0.0F;
        int n = 0;
        instance.startDrawingQuads();

        while (f5 > 0.0F) {
            IIcon icon = n % 2 == 0 ? fireIcon : fireIcon2;
            this.bindTexture(TextureMap.locationBlocksTexture);
            float minU = icon.getMinU();
            float minV = icon.getMinV();
            float maxU = icon.getMaxU();
            float maxV = icon.getMaxV();
            if (n / 2 % 2 == 0) {
                float f8 = maxU;
                maxU = minU;
                minU = f8;
            }

            instance.addVertexWithUV(f3 - f4, 0.0F - f6, f7, maxU, maxV);
            instance.addVertexWithUV(-f3 - f4, 0.0F - f6, f7, minU, maxV);
            instance.addVertexWithUV(-f3 - f4, 1.4F - f6, f7, minU, minV);
            instance.addVertexWithUV(f3 - f4, 1.4F - f6, f7, maxU, minV);
            f5 -= 0.45F;
            f6 -= 0.45F;
            f3 *= 0.9F;
            f7 += 0.03F;
            n++;
        }

        instance.draw();
        GL11.glPopMatrix();
        GL11.glEnable(2896);
    }

    public void renderShadow(Entity entity, double d, double d2, double d3, float f, float f2) {
        if (!Config.isShaders() || !Shaders.shouldSkipDefaultShadow) {
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            this.renderManager.renderEngine.bindTexture(shadowTextures);
            World worldFromRenderManager = this.getWorldFromRenderManager();
            GL11.glDepthMask(false);
            float shadowSize = this.shadowSize;
            if (entity instanceof EntityLiving) {
                EntityLiving entityLiving = (EntityLiving)entity;
                shadowSize *= entityLiving.getRenderSizeModifier();
                if (entityLiving.isChild()) {
                    shadowSize *= 0.5F;
                }
            }

            double d4 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * f2;
            double d5 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * f2 + entity.getShadowSize();
            double d6 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * f2;
            int n = MathHelper.floor_double(d4 - shadowSize);
            int n2 = MathHelper.floor_double(d4 + shadowSize);
            int n3 = MathHelper.floor_double(d5 - shadowSize);
            int n4 = MathHelper.floor_double(d5);
            int n5 = MathHelper.floor_double(d6 - shadowSize);
            int n6 = MathHelper.floor_double(d6 + shadowSize);
            double d7 = d - d4;
            double d8 = d2 - d5;
            double d9 = d3 - d6;
            Tessellator instance = Tessellator.instance;
            instance.startDrawingQuads();

            for (int i = n; i <= n2; i++) {
                for (int j = n3; j <= n4; j++) {
                    for (int k = n5; k <= n6; k++) {
                        Block block = worldFromRenderManager.getBlock(i, j - 1, k);
                        if (block.getMaterial() != Material.air && worldFromRenderManager.getBlockLightValue(i, j, k) > 3) {
                            this.handleBlock(
                                block, d, d2 + entity.getShadowSize(), d3, i, j, k, f, shadowSize, d7, d8 + entity.getShadowSize(), d9
                            );
                        }
                    }
                }
            }

            instance.draw();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(3042);
            GL11.glDepthMask(true);
        }
    }

    public World getWorldFromRenderManager() {
        return this.renderManager.worldObj;
    }

    public void handleBlock(
        Block block, double d, double d2, double d3, int n, int n2, int n3, float f, float f2, double d4, double d5, double d6
    ) {
        Tessellator instance = Tessellator.instance;
        if (block.renderAsNormalBlock()) {
            double d7 = (f - (d2 - (n2 + d5)) / 2.0) * 0.5 * this.getWorldFromRenderManager().getLightBrightness(n, n2, n3);
            if (d7 >= 0.0) {
                if (d7 > 1.0) {
                    d7 = 1.0;
                }

                instance.setColorRGBA_F(1.0F, 1.0F, 1.0F, (float)d7);
                double d8 = n + block.getBlockBoundsMinX() + d4;
                double d9 = n + block.getBlockBoundsMaxX() + d4;
                double d10 = n2 + block.getBlockBoundsMinY() + d5 + 0.015625;
                double d11 = n3 + block.getBlockBoundsMinZ() + d6;
                double d12 = n3 + block.getBlockBoundsMaxZ() + d6;
                float f3 = (float)((d - d8) / 2.0 / f2 + 0.5);
                float f4 = (float)((d - d9) / 2.0 / f2 + 0.5);
                float f5 = (float)((d3 - d11) / 2.0 / f2 + 0.5);
                float f6 = (float)((d3 - d12) / 2.0 / f2 + 0.5);
                instance.addVertexWithUV(d8, d10, d11, f3, f5);
                instance.addVertexWithUV(d8, d10, d12, f3, f6);
                instance.addVertexWithUV(d9, d10, d12, f4, f6);
                instance.addVertexWithUV(d9, d10, d11, f4, f5);
            }
        }
    }

    public static void renderOffsetAABB(AxisAlignedBB axisAlignedBB, double d, double d2, double d3) {
        GL11.glDisable(3553);
        Tessellator instance = Tessellator.instance;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        instance.startDrawingQuads();
        instance.setTranslation(d, d2, d3);
        instance.setNormal(0.0F, 0.0F, -1.0F);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.setNormal(0.0F, 0.0F, 1.0F);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.setNormal(0.0F, -1.0F, 0.0F);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.setNormal(0.0F, 1.0F, 0.0F);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.setNormal(-1.0F, 0.0F, 0.0F);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.setNormal(1.0F, 0.0F, 0.0F);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.setTranslation(0.0, 0.0, 0.0);
        instance.draw();
        GL11.glEnable(3553);
    }

    public static void renderAABB(AxisAlignedBB axisAlignedBB) {
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.draw();
    }

    public void setRenderManager(RenderManager renderManager) {
        this.renderManager = renderManager;
    }

    public void doRenderShadowAndFire(Entity entity, double d, double d2, double d3, float f, float f2) {
        if (this.shadowSize > 0.0F && !entity.isInvisible() && (Boolean)CheatBreaker.getInstance().getKeybindSettings().entityShadows.getValue()) {
            double distanceToCamera = this.renderManager.getDistanceToCamera(entity.posX, entity.posY, entity.posZ);
            float f3 = (float)((1.0 - distanceToCamera / 256.0) * this.shadowOpaque);
            if (f3 > 0.0F) {
                this.renderShadow(entity, d, d2, d3, f3, f2);
            }
        }

        PackTweaksMod packTweaksMod = CheatBreaker.getInstance().getModManager().packTweaksMod;
        boolean isEnabled = packTweaksMod.isEnabled();
        boolean flag = isEnabled ? (Boolean)packTweaksMod.fireOnEntities.getValue() : true;
        if (entity.canRenderOnFire() && flag) {
            this.renderEntityOnFire(entity, d, d2, d3, f2);
        }
    }

    public FontRenderer getFontRendererFromRenderManager() {
        return this.renderManager.getFontRenderer();
    }

    public void updateIcons(IIconRegister iconRegister) {
    }

    public void renderLivingLabel(Entity entity, String str, double d, double d2, double d3, int n) {
        double distanceSqToEntity = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
        if (distanceSqToEntity <= n * n) {
            if (CheatBreaker.getInstance().getNetHandler().getNametagOverrides().containsKey(entity.getUniqueID())) {
                List list = CheatBreaker.getInstance().getNetHandler().getNametagOverrides().get(entity.getUniqueID());
                int n2 = 0;

                for (String str2 : (Iterable<String>)list) {
                    this.render(-n2, entity, str2, d, d2, d3, n);
                    n2++;
                }
            } else {
                this.render(0.0, entity, str, d, d2, d3, n);
            }
        }
    }

    public String formatText(String str) {
        return str == null ? null : this.pattern.matcher(str).replaceAll("§r");
    }

    public void render(
        int n, int n2, int n3, int n4, FontRenderer fontRenderer, String str, PlayerData playerData, NametagMod nametagMod, boolean flag, boolean flag2, int n5
    ) {
        fontRenderer.drawString(
            str,
            -(fontRenderer.getStringWidth(str) / 2 - (flag2 ? n3 : n4)),
            n2,
            nametagMod.stripColorFormatting.getBooleanValue()
                ? new Color(
                        nametagMod.textColor.getColorValue() >> 16 & 0xFF,
                        nametagMod.textColor.getColorValue() >> 8 & 0xFF,
                        nametagMod.textColor.getColorValue() & 0xFF,
                        Math.min(n5, nametagMod.textColor.getColorValue() >> 24 & 0xFF)
                    )
                    .getRGB()
                : new Color(255, 255, 255, n5).getRGB(),
            nametagMod.isEnabled() && nametagMod.textShadow.getBooleanValue()
        );
        if (playerData != null) {
            if (flag) {
                for (int i = 0; i < playerData.getIntegerList().size(); i++) {
                    float f = (playerData.getIntegerList().get(i) >> 16 & 0xFF) / 255.0F;
                    float f2 = (playerData.getIntegerList().get(i) >> 8 & 0xFF) / 255.0F;
                    float f3 = (playerData.getIntegerList().get(i) & 0xFF) / 255.0F;
                    if (nametagMod.textShadow.getBooleanValue()) {
                        if (flag2) {
                            GL11.glColor4f(f / 4.0F, f2 / 4.0F, f3 / 4.0F, n5 / 255.0F);
                            RenderUtils.drawSquareTextureViaRenderEngine(
                                new ResourceLocation("client/icons/nametag/logo_circle_" + i + ".png"), 3.6F, -(n * 2) / 2.0F + 1.0F, 0.0F
                            );
                        } else {
                            GL11.glColor4f(f / 4.0F, f2 / 4.0F, f3 / 4.0F, n5 / 255.0F);
                            RenderUtils.drawTexture(
                                new ResourceLocation("client/icons/nametag/logo_white_" + i + ".png"), -(n * 2) / 2.0F + 1.0F, 1.0F, 13.0F, 7.0F
                            );
                        }
                    }

                    if (flag2) {
                        GL11.glColor4f(f, f2, f3, n5 / 255.0F);
                        RenderUtils.drawSquareTextureViaRenderEngine(
                            new ResourceLocation("client/icons/nametag/logo_circle_" + i + ".png"), 3.6F, -(n * 2) / 2.0F + 1.0F, 0.0F
                        );
                    } else {
                        GL11.glColor4f(f, f2, f3, n5 / 255.0F);
                        RenderUtils.drawTexture(new ResourceLocation("client/icons/nametag/logo_white_" + i + ".png"), -(n * 2) / 2.0F, 0.0F, 13.0F, 7.0F);
                    }
                }

                if (nametagMod.showSubIcons.getBooleanValue() && !playerData.getText2().equalsIgnoreCase("")) {
                    if (nametagMod.textShadow.getBooleanValue()) {
                        GL11.glColor4f(0.25F, 0.25F, 0.25F, n5 / 255.0F);
                        RenderUtils.drawTexture(
                            new ResourceLocation("client/icons/nametag/subicons/" + playerData.getText2() + ".png"),
                            -(n * 2) / 2.0F + 10.0F + 0.33333334F,
                            -1.6666666F,
                            4.3333335F,
                            4.3333335F
                        );
                    }

                    GL11.glColor4f(1.0F, 1.0F, 1.0F, n5 / 255.0F);
                    RenderUtils.drawTexture(
                        new ResourceLocation("client/icons/nametag/subicons/" + playerData.getText2() + ".png"), -(n * 2) / 2.0F + 10.0F, -2.0F, 4.3333335F, 4.3333335F
                    );
                }
            }
        }
    }

    public void render(double d, Entity entity, String str, double d2, double d3, double d4, int n) {
        if (!(entity instanceof PreviewPlayer)) {
            NametagMod nametagMod = CheatBreaker.getInstance().getModManager().nametagMod;
            PlayerDataManager playerDataManager = CheatBreaker.getInstance().getPlayerDataManager();
            PlayerData findPlayerData = playerDataManager.findPlayerData(entity.getUniqueID().toString());
            boolean isEnabled = nametagMod.isEnabled();
            boolean flag = findPlayerData != null
                && isEnabled
                && (Boolean)nametagMod.showCheatbreakerLogo.getValue()
                && (nametagMod.isNametagAllowed(str) || playerDataManager.containsPlayerName(entity.getUniqueID().toString(), false))
                && playerDataManager.containsPlayerName(str, true);
            boolean value = (Boolean)nametagMod.cheatbreakerLogoType.getValue();
            double distanceSqToEntity = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
            if (distanceSqToEntity <= n * n) {
                FontRenderer fontRendererFromRenderManager = this.getFontRendererFromRenderManager();
                float f = 1.6F;
                float f2 = 0.016666668F * f;
                GL11.glPushMatrix();
                GL11.glTranslatef((float)d2 + 0.0F, (float)(d3 + entity.height + 0.5 - d / 3.0), (float)d4);
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                GL11.glScalef(-f2, -f2, f2);
                GL11.glDisable(2896);
                GL11.glDepthMask(false);
                GL11.glDisable(2929);
                GL11.glEnable(3042);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                Tessellator instance = Tessellator.instance;
                byte b = 0;
                GL11.glDisable(3553);
                boolean flag2 = nametagMod.stripColorFormatting.getBooleanValue() && isEnabled;
                String str2 = flag2 ? this.formatText(str) : str;
                int n2 = fontRendererFromRenderManager.getStringWidth(str2) / 2;
                int n3 = flag ? (str2.contains(" ") ? 6 : 5) : 0;
                int n4 = flag ? (str2.contains(" ") ? 8 : 7) : 0;
                n2 = value ? n2 + n3 : n2 + n4;
                if (!isEnabled || (Boolean)nametagMod.showBackground.getValue()) {
                    instance.startDrawingQuads();
                    FloatEvent4 floatEvent4 = new FloatEvent4(0.0F, 0.0F, 0.0F, 0.25F);
                    CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent4);
                    instance.setColorRGBA_F(floatEvent4.getFloatValue1(), floatEvent4.getFloatValue2(), floatEvent4.getFloatValue3(), floatEvent4.getFloatValue4());
                    instance.addVertex(-n2 - 1, -1 + b, 0.0);
                    instance.addVertex(-n2 - 1, 8 + b, 0.0);
                    instance.addVertex(n2 + 1, 8 + b, 0.0);
                    instance.addVertex(n2 + 1, -1 + b, 0.0);
                    instance.draw();
                }

                GL11.glEnable(3553);
                this.render(n2, b, n3, n4, fontRendererFromRenderManager, str2, findPlayerData, nametagMod, flag, value, 32);
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
                this.render(n2, b, n3, n4, fontRendererFromRenderManager, str2, findPlayerData, nametagMod, flag, value, 255);
                GL11.glEnable(2896);
                GL11.glDisable(3042);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();
            }
        }
    }
}
