package net.minecraft.client.renderer.tileentity;

import com.cheatbreaker.client.CheatBreaker;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderEnchantmentTable;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererDispatcher {
    public Map mapSpecialRenderers = new HashMap();
    public static TileEntityRendererDispatcher instance = new TileEntityRendererDispatcher();
    public FontRenderer textRenderer;
    public static double staticPlayerX;
    public static double staticPlayerY;
    public static double staticPlayerZ;
    public TextureManager renderEngine;
    public World worldObj;
    public EntityLivingBase entityLivingBase;
    public float cameraYaw;
    public float cameraPitch;
    public double cameraX;
    public double cameraY;
    public double cameraZ;

    public TileEntityRendererDispatcher() {
        this.mapSpecialRenderers.put(TileEntitySign.class, new TileEntitySignRenderer());
        this.mapSpecialRenderers.put(TileEntityMobSpawner.class, new TileEntityMobSpawnerRenderer());
        this.mapSpecialRenderers.put(TileEntityPiston.class, new TileEntityRendererPiston());
        this.mapSpecialRenderers.put(TileEntityChest.class, new TileEntityChestRenderer());
        this.mapSpecialRenderers.put(TileEntityEnderChest.class, new TileEntityEnderChestRenderer());
        this.mapSpecialRenderers.put(TileEntityEnchantmentTable.class, new RenderEnchantmentTable());
        this.mapSpecialRenderers.put(TileEntityEndPortal.class, new RenderEndPortal());
        this.mapSpecialRenderers.put(TileEntityBeacon.class, new TileEntityBeaconRenderer());
        this.mapSpecialRenderers.put(TileEntitySkull.class, new TileEntitySkullRenderer());

        for (TileEntitySpecialRenderer tileEntitySpecialRenderer : (Iterable<TileEntitySpecialRenderer>)this.mapSpecialRenderers.values()) {
            tileEntitySpecialRenderer.setDispatcher(this);
        }
    }

    public TileEntitySpecialRenderer getSpecialRendererByClass(Class clazz) {
        TileEntitySpecialRenderer tileEntitySpecialRenderer = (TileEntitySpecialRenderer)this.mapSpecialRenderers.get(clazz);
        if (tileEntitySpecialRenderer == null && clazz != TileEntity.class) {
            tileEntitySpecialRenderer = this.getSpecialRendererByClass(clazz.getSuperclass());
            this.mapSpecialRenderers.put(clazz, tileEntitySpecialRenderer);
        }

        return tileEntitySpecialRenderer;
    }

    public boolean hasSpecialRenderer(TileEntity tileEntity) {
        return this.getSpecialRenderer(tileEntity) != null;
    }

    public TileEntitySpecialRenderer getSpecialRenderer(TileEntity tileEntity) {
        return tileEntity == null ? null : this.getSpecialRendererByClass(tileEntity.getClass());
    }

    public void cacheActiveRenderInfo(World world, TextureManager textureManager, FontRenderer fontRenderer, EntityLivingBase entityLivingBase, float f) {
        if (this.worldObj != world) {
            this.setWorld(world);
        }

        this.renderEngine = textureManager;
        this.entityLivingBase = entityLivingBase;
        this.textRenderer = fontRenderer;
        this.cameraYaw = entityLivingBase.prevRotationYaw + (entityLivingBase.rotationYaw - entityLivingBase.prevRotationYaw) * f;
        this.cameraPitch = entityLivingBase.prevRotationPitch + (entityLivingBase.rotationPitch - entityLivingBase.prevRotationPitch) * f;
        this.cameraX = entityLivingBase.lastTickPosX + (entityLivingBase.posX - entityLivingBase.lastTickPosX) * f;
        this.cameraY = entityLivingBase.lastTickPosY + (entityLivingBase.posY - entityLivingBase.lastTickPosY) * f;
        this.cameraZ = entityLivingBase.lastTickPosZ + (entityLivingBase.posZ - entityLivingBase.lastTickPosZ) * f;
    }

    public void renderTileEntity(TileEntity tileEntity, float f) {
        if (CheatBreaker.getInstance().getKeybindSettings().shouldRenderTileEntity(tileEntity)) {
            if (tileEntity.getDistanceSq(this.cameraX, this.cameraY, this.cameraZ) < tileEntity.getMaxRenderDistanceSquared()) {
                int lightBrightnessForSkyBlocks = this.worldObj.getLightBrightnessForSkyBlocks(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 0);
                int n = lightBrightnessForSkyBlocks % 65536;
                int n2 = lightBrightnessForSkyBlocks / 65536;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.renderTileEntityAt(tileEntity, tileEntity.xCoord - staticPlayerX, tileEntity.yCoord - staticPlayerY, tileEntity.zCoord - staticPlayerZ, f);
            }
        }
    }

    public void renderTileEntityAt(TileEntity tileEntity, double d, double d2, double d3, float f) {
        TileEntitySpecialRenderer specialRenderer = this.getSpecialRenderer(tileEntity);
        if (specialRenderer != null) {
            try {
                specialRenderer.renderTileEntityAt(tileEntity, d, d2, d3, f);
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Rendering Block Entity");
                CrashReportCategory makeCategory = makeCrashReport.makeCategory("Block Entity Details");
                tileEntity.addInfoToCrashReport(makeCategory);
                throw new ReportedException(makeCrashReport);
            }
        }
    }

    public void setWorld(World world) {
        this.worldObj = world;

        for (TileEntitySpecialRenderer tileEntitySpecialRenderer : (Iterable<TileEntitySpecialRenderer>)this.mapSpecialRenderers.values()) {
            if (tileEntitySpecialRenderer != null) {
                tileEntitySpecialRenderer.onWorldChange(world);
            }
        }
    }

    public FontRenderer getFontRenderer() {
        return this.textRenderer;
    }
}
