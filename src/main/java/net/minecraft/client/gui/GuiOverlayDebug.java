package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3i;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.optifine.Config;
import net.optifine.SmartAnimations;
import net.optifine.TextureAnimations;
import net.optifine.reflect.Reflector;
import net.optifine.util.MemoryMonitor;
import net.optifine.util.NativeMemory;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class GuiOverlayDebug
extends Gui {
    public String debugOF = null;
    public List<String> debugInfoLeft = null;
    public List<String> debugInfoRight = null;
    public long updateInfoLeftTimeMs = 0L;
    public long updateInfoRightTimeMs = 0L;
    public Minecraft mc;
    public FontRenderer fontRenderer;

    public GuiOverlayDebug(Minecraft mc) {
        this.mc = mc;
        this.fontRenderer = mc.fontRendererObj;
    }

    public void renderDebugInfo(ScaledResolution scaledResolutionIn) {
        this.mc.mcProfiler.startSection("debug");
        GlStateManager.pushMatrix();
        this.renderDebugInfoLeft();
        this.renderDebugInfoRight(scaledResolutionIn);
        GlStateManager.popMatrix();
        if (this.mc.gameSettings.showLagometer) {
            this.renderLagometer();
        }
        this.mc.mcProfiler.endSection();
    }

    public boolean isReducedDebug() {
        return this.mc.thePlayer.hasReducedDebug() || this.mc.gameSettings.reducedDebugInfo;
    }

    public void renderDebugInfoLeft() {
        List<String> list = this.debugInfoLeft;
        if (list == null || System.currentTimeMillis() > this.updateInfoLeftTimeMs) {
            this.debugInfoLeft = list = this.call();
            this.updateInfoLeftTimeMs = System.currentTimeMillis() + 100L;
        }
        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);
            if (Strings.isNullOrEmpty(string)) continue;
            int n = this.fontRenderer.FONT_HEIGHT;
            int n2 = this.fontRenderer.getStringWidth(string);
            int n3 = 2;
            int n4 = 2 + n * i;
            GuiOverlayDebug.drawRect(1, n4 - 1, 2 + n2 + 1, n4 + n - 1, -1873784752);
            this.fontRenderer.drawString(string, 2.0f, n4, 0xE0E0E0);
        }
    }

    public void renderDebugInfoRight(ScaledResolution scaledRes) {
        List<String> list = this.debugInfoRight;
        if (list == null || System.currentTimeMillis() > this.updateInfoRightTimeMs) {
            this.debugInfoRight = list = this.getDebugInfoRight();
            this.updateInfoRightTimeMs = System.currentTimeMillis() + 100L;
        }
        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);
            if (Strings.isNullOrEmpty(string)) continue;
            int n = this.fontRenderer.FONT_HEIGHT;
            int n2 = this.fontRenderer.getStringWidth(string);
            int n3 = scaledRes.getScaledWidth() - 2 - n2;
            int n4 = 2 + n * i;
            GuiOverlayDebug.drawRect(n3 - 1, n4 - 1, n3 + n2 + 1, n4 + n - 1, -1873784752);
            this.fontRenderer.drawString(string, n3, n4, 0xE0E0E0);
        }
    }

    public List<String> call() {
        Object object;
        Object abstractStringBuilder;
        BlockPos blockPos = new BlockPos(this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ);
        if (this.mc.debug != this.debugOF) {
            abstractStringBuilder = new StringBuffer(this.mc.debug);
            int n = Config.getFpsMin();
            int n2 = this.mc.debug.indexOf(" fps ");
            if (n2 >= 0) {
                ((StringBuffer)abstractStringBuilder).insert(n2, "/" + n);
            }
            if (Config.isSmoothFps()) {
                ((StringBuffer)abstractStringBuilder).append(" sf");
            }
            if (Config.isFastRender()) {
                ((StringBuffer)abstractStringBuilder).append(" fr");
            }
            if (Config.isAnisotropicFiltering()) {
                ((StringBuffer)abstractStringBuilder).append(" af");
            }
            if (Config.isAntialiasing()) {
                ((StringBuffer)abstractStringBuilder).append(" aa");
            }
            if (Config.isRenderRegions()) {
                ((StringBuffer)abstractStringBuilder).append(" reg");
            }
            if (Config.isShaders()) {
                ((StringBuffer)abstractStringBuilder).append(" sh");
            }
            this.debugOF = this.mc.debug = ((StringBuffer)abstractStringBuilder).toString();
        }
        abstractStringBuilder = new StringBuilder();
        TextureMap textureMap = Config.getTextureMap();
        ((StringBuilder)abstractStringBuilder).append(", A: ");
        if (SmartAnimations.isActive()) {
            ((StringBuilder)abstractStringBuilder).append(textureMap.getCountAnimationsActive() + TextureAnimations.getCountAnimationsActive());
            ((StringBuilder)abstractStringBuilder).append("/");
        }
        ((StringBuilder)abstractStringBuilder).append(textureMap.getCountAnimations() + TextureAnimations.getCountAnimations());
        String string = ((StringBuilder)abstractStringBuilder).toString();
        if (this.isReducedDebug()) {
            return Lists.newArrayList("Minecraft 1.8.9 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities() + string, this.mc.theWorld.getProviderName(), "", String.format("Chunk-relative: %d %d %d", blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF));
        }
        Entity entity = this.mc.getRenderViewEntity();
        EnumFacing enumFacing = entity.getHorizontalFacing();
        String string2 = "Invalid";
        switch (enumFacing) {
            case NORTH: {
                string2 = "Towards negative Z";
                break;
            }
            case SOUTH: {
                string2 = "Towards positive Z";
                break;
            }
            case WEST: {
                string2 = "Towards negative X";
                break;
            }
            case EAST: {
                string2 = "Towards positive X";
            }
        }
        ArrayList<String> arrayList = Lists.newArrayList("Minecraft 1.8.9 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities() + string, this.mc.theWorld.getProviderName(), "", String.format("XYZ: %.3f / %.5f / %.3f", this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ), String.format("Block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()), String.format("Chunk: %d %d %d in %d %d %d", blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF, blockPos.getX() >> 4, blockPos.getY() >> 4, blockPos.getZ() >> 4), String.format("Facing: %s (%s) (%.1f / %.1f)", enumFacing, string2, Float.valueOf(MathHelper.wrapAngleTo180_float(entity.rotationYaw)), Float.valueOf(MathHelper.wrapAngleTo180_float(entity.rotationPitch))));
        if (this.mc.theWorld != null && this.mc.theWorld.isBlockLoaded(blockPos)) {
            DifficultyInstance difficultyInstance;
            EntityPlayerMP entityPlayerMP;
            object = this.mc.theWorld.getChunkFromBlockCoords(blockPos);
            arrayList.add("Biome: " + ((Chunk)object).getBiome((BlockPos)blockPos, (WorldChunkManager)this.mc.theWorld.getWorldChunkManager()).biomeName);
            arrayList.add("Light: " + ((Chunk)object).getLightSubtracted(blockPos, 0) + " (" + ((Chunk)object).getLightFor(EnumSkyBlock.SKY, blockPos) + " sky, " + ((Chunk)object).getLightFor(EnumSkyBlock.BLOCK, blockPos) + " block)");
            DifficultyInstance difficultyInstance2 = this.mc.theWorld.getDifficultyForLocation(blockPos);
            if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null && (entityPlayerMP = this.mc.getIntegratedServer().getConfigurationManager().getPlayerByUUID(this.mc.thePlayer.getUniqueID())) != null && (difficultyInstance = this.mc.getIntegratedServer().getDifficultyAsync(entityPlayerMP.worldObj, new BlockPos(entityPlayerMP))) != null) {
                difficultyInstance2 = difficultyInstance;
            }
            arrayList.add(String.format("Local Difficulty: %.2f (Day %d)", Float.valueOf(difficultyInstance2.getAdditionalDifficulty()), this.mc.theWorld.getWorldTime() / 24000L));
        }
        if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive()) {
            arrayList.add("Shader: " + this.mc.entityRenderer.getShaderGroup().getShaderGroupName());
        }
        if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null) {
            object = this.mc.objectMouseOver.getBlockPos();
            arrayList.add(String.format("Looking at: %d %d %d", ((Vec3i)object).getX(), ((Vec3i)object).getY(), ((Vec3i)object).getZ()));
        }
        return arrayList;
    }

    public List<String> getDebugInfoRight() {
        Object object;
        long l = Runtime.getRuntime().maxMemory();
        long l2 = Runtime.getRuntime().totalMemory();
        long l3 = Runtime.getRuntime().freeMemory();
        long l4 = l2 - l3;
        ArrayList<String> arrayList = Lists.newArrayList(String.format("Java: %s %dbit", System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", l4 * 100L / l, GuiOverlayDebug.bytesToMb(l4), GuiOverlayDebug.bytesToMb(l)), String.format("Allocated: % 2d%% %03dMB", l2 * 100L / l, GuiOverlayDebug.bytesToMb(l2)), "", String.format("CPU: %s", OpenGlHelper.getCpu()), "", String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GL11.glGetString((int)7936)), GL11.glGetString((int)7937), GL11.glGetString((int)7938));
        long l5 = NativeMemory.getBufferAllocated();
        long l6 = NativeMemory.getBufferMaximum();
        String string = "Native: " + GuiOverlayDebug.bytesToMb(l5) + "/" + GuiOverlayDebug.bytesToMb(l6) + "MB";
        arrayList.add(4, string);
        arrayList.add(5, "GC: " + MemoryMonitor.getAllocationRateMb() + "MB/s");
        arrayList.add(6, "");
        if (Reflector.FMLCommonHandler_getBrandings.exists()) {
            object = Reflector.call(Reflector.FMLCommonHandler_instance, new Object[0]);
            arrayList.add("");
            arrayList.addAll((Collection)Reflector.call(object, Reflector.FMLCommonHandler_getBrandings, false));
        }
        if (this.isReducedDebug()) {
            return arrayList;
        }
        if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null) {
            object = this.mc.objectMouseOver.getBlockPos();
            IBlockState iBlockState = this.mc.theWorld.getBlockState((BlockPos)object);
            if (this.mc.theWorld.getWorldType() != WorldType.DEBUG_WORLD) {
                iBlockState = iBlockState.getBlock().getActualState(iBlockState, this.mc.theWorld, (BlockPos)object);
            }
            arrayList.add("");
            arrayList.add(String.valueOf(Block.blockRegistry.getNameForObject(iBlockState.getBlock())));
            for (Map.Entry entry : iBlockState.getProperties().entrySet()) {
                String string2 = ((Comparable)entry.getValue()).toString();
                if (entry.getValue() == Boolean.TRUE) {
                    string2 = (Object)((Object)EnumChatFormatting.GREEN) + string2;
                } else if (entry.getValue() == Boolean.FALSE) {
                    string2 = (Object)((Object)EnumChatFormatting.RED) + string2;
                }
                arrayList.add(((IProperty)entry.getKey()).getName() + ": " + string2);
            }
        }
        return arrayList;
    }

    public void renderLagometer() {
    }

    public int getFrameColor(int n, int n2, int n3, int n4) {
        return n < n3 ? this.blendColors(-16711936, -256, (float)n / (float)n3) : this.blendColors(-256, -65536, (float)(n - n3) / (float)(n4 - n3));
    }

    public int blendColors(int n, int n2, float f) {
        int n3 = n >> 24 & 0xFF;
        int n4 = n >> 16 & 0xFF;
        int n5 = n >> 8 & 0xFF;
        int n6 = n & 0xFF;
        int n7 = n2 >> 24 & 0xFF;
        int n8 = n2 >> 16 & 0xFF;
        int n9 = n2 >> 8 & 0xFF;
        int n10 = n2 & 0xFF;
        int n11 = MathHelper.clamp_int((int)((float)n3 + (float)(n7 - n3) * f), 0, 255);
        int n12 = MathHelper.clamp_int((int)((float)n4 + (float)(n8 - n4) * f), 0, 255);
        int n13 = MathHelper.clamp_int((int)((float)n5 + (float)(n9 - n5) * f), 0, 255);
        int n14 = MathHelper.clamp_int((int)((float)n6 + (float)(n10 - n6) * f), 0, 255);
        return n11 << 24 | n12 << 16 | n13 << 8 | n14;
    }

    public static long bytesToMb(long bytes) {
        return bytes / 1024L / 1024L;
    }
}
