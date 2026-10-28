package net.minecraft.client.renderer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.BlockIntEvent;
import com.cheatbreaker.client.mod.impl.PackTweaksMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererChestHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.optifine.Config;
import net.optifine.ConnectedTextures;
import net.optifine.CustomColorizer;
import net.optifine.NaturalProperties;
import net.optifine.NaturalTextures;
import net.optifine.Reflector;
import net.optifine.TextureUtils;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public class RenderBlocks {
    public boolean useInventoryTint = true;
    public IIcon overrideBlockTexture;
    public boolean renderFromInside = false;
    public float aoLightValueOpaque = 0.2F;
    public boolean renderAllFaces;
    public static boolean fancyGrass = true;
    public static boolean cfgGrassFix = true;
    public boolean betterSnowEnabled = true;
    public float lightLevel05 = 0.5F;
    public double renderMinX;
    public double renderMaxX;
    public double renderMinY;
    public double renderMaxY;
    public double renderMinZ;
    public double renderMaxZ;
    public boolean lockBlockBounds;
    public boolean partialRenderBounds;
    public float lightLevel06 = 0.6F;
    public int uvRotateEast;
    public int uvRotateWest;
    public int uvRotateSouth;
    public int uvRotateNorth;
    public int uvRotateTop;
    public int uvRotateBottom;
    public boolean enableAO;
    public float aoLightValueScratchXYZNNN;
    public float aoLightValueScratchXYNN;
    public float aoLightValueScratchXYZNNP;
    public float aoLightValueScratchYZNN;
    public float aoLightValueScratchYZNP;
    public float aoLightValueScratchXYZPNN;
    public float aoLightValueScratchXYPN;
    public float aoLightValueScratchXYZPNP;
    public float aoLightValueScratchXYZNPN;
    public float aoLightValueScratchXYNP;
    public float aoLightValueScratchXYZNPP;
    public float aoLightValueScratchYZPN;
    public float aoLightValueScratchXYZPPN;
    public float aoLightValueScratchXYPP;
    public float aoLightValueScratchYZPP;
    public float aoLightValueScratchXYZPPP;
    public float aoLightValueScratchXZNN;
    public float aoLightValueScratchXZPN;
    public float aoLightValueScratchXZNP;
    public float aoLightValueScratchXZPP;
    public int aoBrightnessXYZNNN;
    public int aoBrightnessXYNN;
    public int aoBrightnessXYZNNP;
    public int aoBrightnessYZNN;
    public int aoBrightnessYZNP;
    public int aoBrightnessXYZPNN;
    public int aoBrightnessXYPN;
    public int aoBrightnessXYZPNP;
    public int aoBrightnessXYZNPN;
    public int aoBrightnessXYNP;
    public int aoBrightnessXYZNPP;
    public int aoBrightnessYZPN;
    public int aoBrightnessXYZPPN;
    public int aoBrightnessXYPP;
    public int aoBrightnessYZPP;
    public int aoBrightnessXYZPPP;
    public int aoBrightnessXZNN;
    public int aoBrightnessXZPN;
    public int aoBrightnessXZNP;
    public int aoBrightnessXZPP;
    public int brightnessTopLeft;
    public int brightnessBottomLeft;
    public int brightnessBottomRight;
    public int brightnessTopRight;
    public float colorRedTopLeft;
    public float colorRedBottomLeft;
    public float colorRedBottomRight;
    public float colorRedTopRight;
    public float colorGreenTopLeft;
    public float colorGreenBottomLeft;
    public float colorGreenBottomRight;
    public float colorGreenTopRight;
    public float colorBlueTopLeft;
    public float colorBlueBottomLeft;
    public float colorBlueBottomRight;
    public float colorBlueTopRight;
    public boolean aoLightValuesCalculated;
    public float lightLevel08 = 0.8F;
    public IBlockAccess blockAccess;
    public boolean flag;
    public boolean flipTexture;
    public Minecraft minecraftRB;
    public static RenderBlocks instance;

    public RenderBlocks(IBlockAccess blockAccess) {
        this.blockAccess = blockAccess;
        this.flag = false;
        this.flipTexture = false;
        this.minecraftRB = Minecraft.getMinecraft();
        this.aoLightValueOpaque = 1.0F - Config.getAmbientOcclusionLevel() * 0.8F;
        if (Config.isShaders()) {
            this.lightLevel08 = Shaders.blockLightLevel08;
            this.lightLevel06 = Shaders.blockLightLevel06;
            this.lightLevel05 = Shaders.blockLightLevel05;
        }
    }

    public RenderBlocks() {
        this.minecraftRB = Minecraft.getMinecraft();
    }

    public void setOverrideBlockTexture(IIcon icon) {
        this.overrideBlockTexture = icon;
    }

    public void clearOverrideBlockTexture() {
        this.overrideBlockTexture = null;
    }

    public boolean hasOverrideBlockTexture() {
        return this.overrideBlockTexture != null;
    }

    public void setRenderFromInside(boolean flag) {
        this.renderFromInside = flag;
    }

    public void setRenderAllFaces(boolean flag) {
        this.renderAllFaces = flag;
    }

    public void setRenderBounds(double d, double d2, double d3, double d4, double d5, double d6) {
        if (!this.lockBlockBounds) {
            this.renderMinX = d;
            this.renderMaxX = d4;
            this.renderMinY = d2;
            this.renderMaxY = d5;
            this.renderMinZ = d3;
            this.renderMaxZ = d6;
            this.partialRenderBounds = this.minecraftRB.gameSettings.ambientOcclusion >= 2
                && (
                    this.renderMinX > 0.0
                        || this.renderMaxX < 1.0
                        || this.renderMinY > 0.0
                        || this.renderMaxY < 1.0
                        || this.renderMinZ > 0.0
                        || this.renderMaxZ < 1.0
                );
        }
    }

    public void setRenderBoundsFromBlock(Block block) {
        if (!this.lockBlockBounds) {
            this.renderMinX = block.getBlockBoundsMinX();
            this.renderMaxX = block.getBlockBoundsMaxX();
            this.renderMinY = block.getBlockBoundsMinY();
            this.renderMaxY = block.getBlockBoundsMaxY();
            this.renderMinZ = block.getBlockBoundsMinZ();
            this.renderMaxZ = block.getBlockBoundsMaxZ();
            this.partialRenderBounds = this.minecraftRB.gameSettings.ambientOcclusion >= 2
                && (
                    this.renderMinX > 0.0
                        || this.renderMaxX < 1.0
                        || this.renderMinY > 0.0
                        || this.renderMaxY < 1.0
                        || this.renderMinZ > 0.0
                        || this.renderMaxZ < 1.0
                );
        }
    }

    public void overrideBlockBounds(double d, double d2, double d3, double d4, double d5, double d6) {
        this.renderMinX = d;
        this.renderMaxX = d4;
        this.renderMinY = d2;
        this.renderMaxY = d5;
        this.renderMinZ = d3;
        this.renderMaxZ = d6;
        this.lockBlockBounds = true;
        this.partialRenderBounds = this.minecraftRB.gameSettings.ambientOcclusion >= 2
            && (this.renderMinX > 0.0 || this.renderMaxX < 1.0 || this.renderMinY > 0.0 || this.renderMaxY < 1.0 || this.renderMinZ > 0.0 || this.renderMaxZ < 1.0);
    }

    public void unlockBlockBounds() {
        this.lockBlockBounds = false;
    }

    public void renderBlockUsingTexture(Block block, int n, int n2, int n3, IIcon icon) {
        this.setOverrideBlockTexture(icon);
        this.renderBlockByRenderType(block, n, n2, n3);
        this.clearOverrideBlockTexture();
    }

    public void renderBlockAllFaces(Block block, int n, int n2, int n3) {
        this.renderAllFaces = true;
        this.renderBlockByRenderType(block, n, n2, n3);
        this.renderAllFaces = false;
    }

    public boolean renderBlockByRenderType(Block block, int n, int n2, int n3) {
        int renderType = block.getRenderType();
        if (renderType == -1) {
            return false;
        }

        try {
            if (Config.isShaders()) {
                Shaders.pushEntity(this, block, n, n2, n3);
            }

            BlockIntEvent blockIntEvent = new BlockIntEvent(block, n, n2, n3);
            CheatBreaker.getInstance().getEventBus().handleEvent(blockIntEvent);
            if (blockIntEvent.isCancelled()) {
                return false;
            }

            block.setBlockBoundsBasedOnState(this.blockAccess, n, n2, n3);
            if (Config.isBetterSnow() && block == Blocks.standing_sign && this.hasSnowNeighbours(n, n2, n3)) {
                this.renderSnow(n, n2, n3, Blocks.snow_layer.getBlockBoundsMaxY());
            }

            this.setRenderBoundsFromBlock(block);
            switch (renderType) {
                case 0:
                    return this.renderStandardBlock(block, n, n2, n3);
                case 1:
                    return this.renderCrossedSquares(block, n, n2, n3);
                case 2:
                    return this.renderBlockTorch(block, n, n2, n3);
                case 3:
                    return this.renderBlockFire((BlockFire)block, n, n2, n3);
                case 4:
                    return this.renderBlockLiquid(block, n, n2, n3);
                case 5:
                    return this.renderBlockRedstoneWire(block, n, n2, n3);
                case 6:
                    return this.renderBlockCrops(block, n, n2, n3);
                case 7:
                    return this.renderBlockDoor(block, n, n2, n3);
                case 8:
                    return this.renderBlockLadder(block, n, n2, n3);
                case 9:
                    return this.renderBlockMinecartTrack((BlockRailBase)block, n, n2, n3);
                case 10:
                    return this.renderBlockStairs((BlockStairs)block, n, n2, n3);
                case 11:
                    return this.renderBlockFence((BlockFence)block, n, n2, n3);
                case 12:
                    return this.renderBlockLever(block, n, n2, n3);
                case 13:
                    return this.renderBlockCactus(block, n, n2, n3);
                case 14:
                    return this.renderBlockBed(block, n, n2, n3);
                case 15:
                    return this.renderBlockRepeater((BlockRedstoneRepeater)block, n, n2, n3);
                case 16:
                    return this.renderPistonBase(block, n, n2, n3, false);
                case 17:
                    return this.renderPistonExtension(block, n, n2, n3, true);
                case 18:
                    return this.renderBlockPane((BlockPane)block, n, n2, n3);
                case 19:
                    return this.renderBlockStem(block, n, n2, n3);
                case 20:
                    return this.renderBlockVine(block, n, n2, n3);
                case 21:
                    return this.renderBlockFenceGate((BlockFenceGate)block, n, n2, n3);
                case 22:
                default:
                    if (Reflector.ModLoader.exists()) {
                        return Reflector.callBoolean(Reflector.ModLoader_renderWorldBlock, this, this.blockAccess, n, n2, n3, block, renderType);
                    } else {
                        if (Reflector.FMLRenderAccessLibrary.exists()) {
                            return Reflector.callBoolean(Reflector.FMLRenderAccessLibrary_renderWorldBlock, this, this.blockAccess, n, n2, n3, block, renderType);
                        }

                        return false;
                    }
                case 23:
                    return this.renderBlockLilyPad(block, n, n2, n3);
                case 24:
                    return this.renderBlockCauldron((BlockCauldron)block, n, n2, n3);
                case 25:
                    return this.renderBlockBrewingStand((BlockBrewingStand)block, n, n2, n3);
                case 26:
                    return this.renderBlockEndPortalFrame((BlockEndPortalFrame)block, n, n2, n3);
                case 27:
                    return this.renderBlockDragonEgg((BlockDragonEgg)block, n, n2, n3);
                case 28:
                    return this.renderBlockCocoa((BlockCocoa)block, n, n2, n3);
                case 29:
                    return this.renderBlockTripWireSource(block, n, n2, n3);
                case 30:
                    return this.renderBlockTripWire(block, n, n2, n3);
                case 31:
                    return this.renderBlockLog(block, n, n2, n3);
                case 32:
                    return this.renderBlockWall((BlockWall)block, n, n2, n3);
                case 33:
                    return this.renderBlockFlowerpot((BlockFlowerPot)block, n, n2, n3);
                case 34:
                    return this.renderBlockBeacon((BlockBeacon)block, n, n2, n3);
                case 35:
                    return this.renderBlockAnvil((BlockAnvil)block, n, n2, n3);
                case 36:
                    return this.renderBlockRedstoneDiode((BlockRedstoneDiode)block, n, n2, n3);
                case 37:
                    return this.renderBlockRedstoneComparator((BlockRedstoneComparator)block, n, n2, n3);
                case 38:
                    return this.renderBlockHopper((BlockHopper)block, n, n2, n3);
                case 39:
                    return this.renderBlockQuartz(block, n, n2, n3);
                case 40:
                    return this.renderBlockDoublePlant((BlockDoublePlant)block, n, n2, n3);
                case 41:
                    return this.renderBlockStainedGlassPane(block, n, n2, n3);
            }
        } finally {
            if (Config.isShaders()) {
                Shaders.popEntity();
            }
        }
    }

    public boolean renderBlockEndPortalFrame(BlockEndPortalFrame blockEndPortalFrame, int n, int n2, int n3) {
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        int n4 = blockMetadata & 3;
        if (n4 == 0) {
            this.uvRotateTop = 3;
        } else if (n4 == 3) {
            this.uvRotateTop = 1;
        } else if (n4 == 1) {
            this.uvRotateTop = 2;
        }

        if (!BlockEndPortalFrame.isEnderEyeInserted(blockMetadata)) {
            this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.8125, 1.0);
            this.renderStandardBlock(blockEndPortalFrame, n, n2, n3);
            this.uvRotateTop = 0;
            return true;
        } else {
            this.renderAllFaces = true;
            this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.8125, 1.0);
            this.renderStandardBlock(blockEndPortalFrame, n, n2, n3);
            this.setOverrideBlockTexture(blockEndPortalFrame.getIconEndPortalFrameEye());
            this.setRenderBounds(0.25, 0.8125, 0.25, 0.75, 1.0, 0.75);
            this.renderStandardBlock(blockEndPortalFrame, n, n2, n3);
            this.renderAllFaces = false;
            this.clearOverrideBlockTexture();
            this.uvRotateTop = 0;
            return true;
        }
    }

    public boolean renderBlockBed(Block block, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        int direction = BlockBed.getDirection(blockMetadata);
        boolean isBlockHeadOfBed = BlockBed.isBlockHeadOfBed(blockMetadata);
        if (Reflector.ForgeBlock_getBedDirection.exists()) {
            direction = Reflector.callInt(block, Reflector.ForgeBlock_getBedDirection, this.blockAccess, n, n2, n3);
        }

        if (Reflector.ForgeBlock_isBedFoot.exists()) {
            isBlockHeadOfBed = Reflector.callBoolean(block, Reflector.ForgeBlock_isBedFoot, this.blockAccess, n, n2, n3);
        }

        float f = 0.5F;
        float f2 = 1.0F;
        float f3 = 0.8F;
        float f4 = 0.6F;
        if (Config.isShaders()) {
            f = this.lightLevel05;
            f3 = this.lightLevel08;
            f4 = this.lightLevel06;
        }

        int mixedBrightnessForBlock = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
        instance.setBrightness(mixedBrightnessForBlock);
        instance.setColorOpaque_F(f, f, f);
        IIcon blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 0);
        if (this.overrideBlockTexture != null) {
            blockIcon = this.overrideBlockTexture;
        }

        double minU = blockIcon.getMinU();
        double maxU = blockIcon.getMaxU();
        double minV = blockIcon.getMinV();
        double maxV = blockIcon.getMaxV();
        double d = n + this.renderMinX;
        double d2 = n + this.renderMaxX;
        double d3 = n2 + this.renderMinY + 0.1875;
        double d4 = n3 + this.renderMinZ;
        double d5 = n3 + this.renderMaxZ;
        instance.addVertexWithUV(d, d3, d5, minU, maxV);
        instance.addVertexWithUV(d, d3, d4, minU, minV);
        instance.addVertexWithUV(d2, d3, d4, maxU, minV);
        instance.addVertexWithUV(d2, d3, d5, maxU, maxV);
        instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3));
        instance.setColorOpaque_F(f2, f2, f2);
        blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 1);
        if (this.overrideBlockTexture != null) {
            blockIcon = this.overrideBlockTexture;
        }

        minU = blockIcon.getMinU();
        maxU = blockIcon.getMaxU();
        minV = blockIcon.getMinV();
        maxV = blockIcon.getMaxV();
        d = minU;
        d2 = maxU;
        d3 = minV;
        d4 = minV;
        d5 = minU;
        double d6 = maxU;
        double d7 = maxV;
        double d8 = maxV;
        if (direction == 0) {
            d2 = minU;
            d3 = maxV;
            d5 = maxU;
            d8 = minV;
        } else if (direction == 2) {
            d = maxU;
            d4 = maxV;
            d6 = minU;
            d7 = minV;
        } else if (direction == 3) {
            d = maxU;
            d4 = maxV;
            d6 = minU;
            d7 = minV;
            d2 = minU;
            d3 = maxV;
            d5 = maxU;
            d8 = minV;
        }

        double d9 = n + this.renderMinX;
        double d10 = n + this.renderMaxX;
        double d11 = n2 + this.renderMaxY;
        double d12 = n3 + this.renderMinZ;
        double d13 = n3 + this.renderMaxZ;
        instance.addVertexWithUV(d10, d11, d13, d5, d7);
        instance.addVertexWithUV(d10, d11, d12, d, d3);
        instance.addVertexWithUV(d9, d11, d12, d2, d4);
        instance.addVertexWithUV(d9, d11, d13, d6, d8);
        int n4 = Direction.directionToFacing[direction];
        if (isBlockHeadOfBed) {
            n4 = Direction.directionToFacing[Direction.rotateOpposite[direction]];
        }

        byte b = 4;
        switch (direction) {
            case 0:
                b = 5;
                break;
            case 1:
                b = 3;
            case 2:
            default:
                break;
            case 3:
                b = 2;
        }

        if (n4 != 2 && (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2, n3 - 1, 2))) {
            instance.setBrightness(this.renderMinZ > 0.0 ? mixedBrightnessForBlock : block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 - 1));
            instance.setColorOpaque_F(f3, f3, f3);
            this.flipTexture = b == 2;
            this.renderFaceZNeg(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 2));
        }

        if (n4 != 3 && (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2, n3 + 1, 3))) {
            instance.setBrightness(this.renderMaxZ < 1.0 ? mixedBrightnessForBlock : block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 + 1));
            instance.setColorOpaque_F(f3, f3, f3);
            this.flipTexture = b == 3;
            this.renderFaceZPos(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 3));
        }

        if (n4 != 4 && (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n - 1, n2, n3, 4))) {
            instance.setBrightness(this.renderMinZ > 0.0 ? mixedBrightnessForBlock : block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3));
            instance.setColorOpaque_F(f4, f4, f4);
            this.flipTexture = b == 4;
            this.renderFaceXNeg(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 4));
        }

        if (n4 != 5 && (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n + 1, n2, n3, 5))) {
            instance.setBrightness(this.renderMaxZ < 1.0 ? mixedBrightnessForBlock : block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3));
            instance.setColorOpaque_F(f4, f4, f4);
            this.flipTexture = b == 5;
            this.renderFaceXPos(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 5));
        }

        this.flipTexture = false;
        return true;
    }

    public boolean renderBlockBrewingStand(BlockBrewingStand blockBrewingStand, int n, int n2, int n3) {
        this.setRenderBounds(0.4375, 0.0, 0.4375, 0.5625, 0.875, 0.5625);
        this.renderStandardBlock(blockBrewingStand, n, n2, n3);
        this.setOverrideBlockTexture(blockBrewingStand.getIconBrewingStandBase());
        this.renderAllFaces = true;
        this.setRenderBounds(0.5625, 0.0, 0.3125, 0.9375, 0.125, 0.6875);
        this.renderStandardBlock(blockBrewingStand, n, n2, n3);
        this.setRenderBounds(0.125, 0.0, 0.0625, 0.5, 0.125, 0.4375);
        this.renderStandardBlock(blockBrewingStand, n, n2, n3);
        this.setRenderBounds(0.125, 0.0, 0.5625, 0.5, 0.125, 0.9375);
        this.renderStandardBlock(blockBrewingStand, n, n2, n3);
        this.renderAllFaces = false;
        this.clearOverrideBlockTexture();
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(blockBrewingStand.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        int colorMultiplier = blockBrewingStand.colorMultiplier(this.blockAccess, n, n2, n3);
        float f = (colorMultiplier >> 16 & 0xFF) / 255.0F;
        float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
        float f3 = (colorMultiplier & 0xFF) / 255.0F;
        if (EntityRenderer.anaglyphEnable) {
            float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
            f = f4;
            f2 = f5;
            f3 = f6;
        }

        instance.setColorOpaque_F(f, f2, f3);
        IIcon blockIconFromSideAndMetadata = this.getBlockIconFromSideAndMetadata(blockBrewingStand, 0, 0);
        if (this.hasOverrideBlockTexture()) {
            blockIconFromSideAndMetadata = this.overrideBlockTexture;
        }

        if (Config.isConnectedTextures() && this.overrideBlockTexture == null) {
            blockIconFromSideAndMetadata = ConnectedTextures.getConnectedTexture(this.blockAccess, blockBrewingStand, n, n2, n3, -1, blockIconFromSideAndMetadata);
        }

        double minV = blockIconFromSideAndMetadata.getMinV();
        double maxV = blockIconFromSideAndMetadata.getMaxV();
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);

        for (int i = 0; i < 3; i++) {
            double d = i * Math.PI * 2.0 / 3.0 + (Math.PI / 2);
            double interpolatedU = blockIconFromSideAndMetadata.getInterpolatedU(8.0);
            double maxU = blockIconFromSideAndMetadata.getMaxU();
            if ((blockMetadata & 1 << i) != 0) {
                maxU = blockIconFromSideAndMetadata.getMinU();
            }

            double d2 = n + 0.5;
            double d3 = n + 0.5 + Math.sin(d) * 8.0 / 16.0;
            double d4 = n3 + 0.5;
            double d5 = n3 + 0.5 + Math.cos(d) * 8.0 / 16.0;
            instance.addVertexWithUV(d2, n2 + 1, d4, interpolatedU, minV);
            instance.addVertexWithUV(d2, n2 + 0, d4, interpolatedU, maxV);
            instance.addVertexWithUV(d3, n2 + 0, d5, maxU, maxV);
            instance.addVertexWithUV(d3, n2 + 1, d5, maxU, minV);
            instance.addVertexWithUV(d3, n2 + 1, d5, maxU, minV);
            instance.addVertexWithUV(d3, n2 + 0, d5, maxU, maxV);
            instance.addVertexWithUV(d2, n2 + 0, d4, interpolatedU, maxV);
            instance.addVertexWithUV(d2, n2 + 1, d4, interpolatedU, minV);
        }

        blockBrewingStand.setBlockBoundsForItemRender();
        return true;
    }

    public boolean renderBlockCauldron(BlockCauldron blockCauldron, int n, int n2, int n3) {
        this.renderStandardBlock(blockCauldron, n, n2, n3);
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(blockCauldron.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        int colorMultiplier = blockCauldron.colorMultiplier(this.blockAccess, n, n2, n3);
        float f = (colorMultiplier >> 16 & 0xFF) / 255.0F;
        float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
        float f3 = (colorMultiplier & 0xFF) / 255.0F;
        if (EntityRenderer.anaglyphEnable) {
            float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
            f = f4;
            f2 = f5;
            f3 = f6;
        }

        instance.setColorOpaque_F(f, f2, f3);
        IIcon blockTextureFromSide = blockCauldron.getBlockTextureFromSide(2);
        float f4 = 0.125F;
        this.renderFaceXPos(blockCauldron, n - 1.0F + f4, n2, n3, blockTextureFromSide);
        this.renderFaceXNeg(blockCauldron, n + 1.0F - f4, n2, n3, blockTextureFromSide);
        this.renderFaceZPos(blockCauldron, n, n2, n3 - 1.0F + f4, blockTextureFromSide);
        this.renderFaceZNeg(blockCauldron, n, n2, n3 + 1.0F - f4, blockTextureFromSide);
        IIcon cauldronIcon = BlockCauldron.getCauldronIcon("inner");
        this.renderFaceYPos(blockCauldron, n, n2 - 1.0F + 0.25F, n3, cauldronIcon);
        this.renderFaceYNeg(blockCauldron, n, n2 + 1.0F - 0.75F, n3, cauldronIcon);
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        if (blockMetadata > 0) {
            IIcon liquidIcon = BlockLiquid.getLiquidIcon("water_still");
            int fluidColor = CustomColorizer.getFluidColor(Blocks.water, this.blockAccess, n, n2, n3);
            float f5 = (fluidColor >> 16 & 0xFF) / 255.0F;
            float f6 = (fluidColor >> 8 & 0xFF) / 255.0F;
            float f7 = (fluidColor & 0xFF) / 255.0F;
            instance.setColorOpaque_F(f5, f6, f7);
            this.renderFaceYPos(blockCauldron, n, n2 - 1.0F + BlockCauldron.getRenderLiquidLevel(blockMetadata), n3, liquidIcon);
        }

        return true;
    }

    public boolean renderBlockFlowerpot(BlockFlowerPot blockFlowerPot, int n, int n2, int n3) {
        if (Config.isShaders()) {
            Shaders.pushEntity(this, blockFlowerPot, n, n2, n3);
        }

        this.renderStandardBlock(blockFlowerPot, n, n2, n3);
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(blockFlowerPot.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        int colorMultiplier = blockFlowerPot.colorMultiplier(this.blockAccess, n, n2, n3);
        IIcon blockIconFromSide = this.getBlockIconFromSide(blockFlowerPot, 0);
        float f = (colorMultiplier >> 16 & 0xFF) / 255.0F;
        float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
        float f3 = (colorMultiplier & 0xFF) / 255.0F;
        if (EntityRenderer.anaglyphEnable) {
            float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
            f = f4;
            f2 = f5;
            f3 = f6;
        }

        instance.setColorOpaque_F(f, f2, f3);
        float f4 = 0.1865F;
        this.renderFaceXPos(blockFlowerPot, n - 0.5F + f4, n2, n3, blockIconFromSide);
        this.renderFaceXNeg(blockFlowerPot, n + 0.5F - f4, n2, n3, blockIconFromSide);
        this.renderFaceZPos(blockFlowerPot, n, n2, n3 - 0.5F + f4, blockIconFromSide);
        this.renderFaceZNeg(blockFlowerPot, n, n2, n3 + 0.5F - f4, blockIconFromSide);
        this.renderFaceYPos(blockFlowerPot, n, n2 - 0.5F + f4 + 0.1875F, n3, this.getBlockIcon(Blocks.dirt));
        TileEntity tileEntity = this.blockAccess.getTileEntity(n, n2, n3);
        if (tileEntity != null && tileEntity instanceof TileEntityFlowerPot) {
            Item flowerPotItem = ((TileEntityFlowerPot)tileEntity).getFlowerPotItem();
            int flowerPotData = ((TileEntityFlowerPot)tileEntity).getFlowerPotData();
            if (flowerPotItem instanceof ItemBlock) {
                Block blockFromItem = Block.getBlockFromItem(flowerPotItem);
                if (Config.isShaders()) {
                    Shaders.pushEntity(blockFromItem);
                }

                int renderType = blockFromItem.getRenderType();
                float f5 = 0.0F;
                float f6 = 4.0F;
                float f7 = 0.0F;
                instance.addTranslation(f5 / 16.0F, f6 / 16.0F, f7 / 16.0F);
                colorMultiplier = blockFromItem.colorMultiplier(this.blockAccess, n, n2, n3);
                if (colorMultiplier != 16777215) {
                    f = (colorMultiplier >> 16 & 0xFF) / 255.0F;
                    f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
                    f3 = (colorMultiplier & 0xFF) / 255.0F;
                    instance.setColorOpaque_F(f, f2, f3);
                }

                if (renderType == 1) {
                    this.drawCrossedSquares(this.getBlockIconFromSideAndMetadata(blockFromItem, 0, flowerPotData), n, n2, n3, 0.75F);
                } else if (renderType == 13) {
                    this.renderAllFaces = true;
                    float f8 = 0.125F;
                    this.setRenderBounds(0.5F - f8, 0.0, 0.5F - f8, 0.5F + f8, 0.25, 0.5F + f8);
                    this.renderStandardBlock(blockFromItem, n, n2, n3);
                    this.setRenderBounds(0.5F - f8, 0.25, 0.5F - f8, 0.5F + f8, 0.5, 0.5F + f8);
                    this.renderStandardBlock(blockFromItem, n, n2, n3);
                    this.setRenderBounds(0.5F - f8, 0.5, 0.5F - f8, 0.5F + f8, 0.75, 0.5F + f8);
                    this.renderStandardBlock(blockFromItem, n, n2, n3);
                    this.renderAllFaces = false;
                    this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
                }

                instance.addTranslation(-f5 / 16.0F, -f6 / 16.0F, -f7 / 16.0F);
                if (Config.isShaders()) {
                    Shaders.popEntity();
                }
            }
        }

        if (Config.isBetterSnow() && this.hasSnowNeighbours(n, n2, n3)) {
            this.renderSnow(n, n2, n3, Blocks.snow_layer.getBlockBoundsMaxY());
        }

        if (Config.isShaders()) {
            Shaders.popEntity();
        }

        return true;
    }

    public boolean renderBlockAnvil(BlockAnvil blockAnvil, int n, int n2, int n3) {
        return this.renderBlockAnvilMetadata(blockAnvil, n, n2, n3, this.blockAccess.getBlockMetadata(n, n2, n3));
    }

    public boolean renderBlockAnvilMetadata(BlockAnvil blockAnvil, int n, int n2, int n3, int n4) {
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(blockAnvil.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        int colorMultiplier = blockAnvil.colorMultiplier(this.blockAccess, n, n2, n3);
        float f = (colorMultiplier >> 16 & 0xFF) / 255.0F;
        float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
        float f3 = (colorMultiplier & 0xFF) / 255.0F;
        if (EntityRenderer.anaglyphEnable) {
            float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
            f = f4;
            f2 = f5;
            f3 = f6;
        }

        instance.setColorOpaque_F(f, f2, f3);
        return this.renderBlockAnvilOrient(blockAnvil, n, n2, n3, n4, false);
    }

    public boolean renderBlockAnvilOrient(BlockAnvil blockAnvil, int n, int n2, int n3, int n4, boolean flag) {
        int n5 = flag ? 0 : n4 & 3;
        boolean flag2 = false;
        float f = 0.0F;
        switch (n5) {
            case 0:
                this.uvRotateSouth = 2;
                this.uvRotateNorth = 1;
                this.uvRotateTop = 3;
                this.uvRotateBottom = 3;
                break;
            case 1:
                this.uvRotateEast = 1;
                this.uvRotateWest = 2;
                this.uvRotateTop = 2;
                this.uvRotateBottom = 1;
                flag2 = true;
                break;
            case 2:
                this.uvRotateSouth = 1;
                this.uvRotateNorth = 2;
                break;
            case 3:
                this.uvRotateEast = 2;
                this.uvRotateWest = 1;
                this.uvRotateTop = 1;
                this.uvRotateBottom = 2;
                flag2 = true;
        }

        f = this.renderBlockAnvilRotate(blockAnvil, n, n2, n3, 0, f, 0.75F, 0.25F, 0.75F, flag2, flag, n4);
        f = this.renderBlockAnvilRotate(blockAnvil, n, n2, n3, 1, f, 0.5F, 0.0625F, 0.625F, flag2, flag, n4);
        f = this.renderBlockAnvilRotate(blockAnvil, n, n2, n3, 2, f, 0.25F, 0.3125F, 0.5F, flag2, flag, n4);
        this.renderBlockAnvilRotate(blockAnvil, n, n2, n3, 3, f, 0.625F, 0.375F, 1.0F, flag2, flag, n4);
        this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        this.uvRotateEast = 0;
        this.uvRotateWest = 0;
        this.uvRotateSouth = 0;
        this.uvRotateNorth = 0;
        this.uvRotateTop = 0;
        this.uvRotateBottom = 0;
        return true;
    }

    public float renderBlockAnvilRotate(
        BlockAnvil blockAnvil, int n, int n2, int n3, int n4, float f, float f2, float f3, float f4, boolean flag, boolean flag2, int n5
    ) {
        if (flag) {
            float f5 = f2;
            f2 = f4;
            f4 = f5;
        }

        f2 /= 2.0F;
        f4 /= 2.0F;
        blockAnvil.anvilRenderSide = n4;
        this.setRenderBounds(0.5F - f2, f, 0.5F - f4, 0.5F + f2, f + f3, 0.5F + f4);
        if (flag2) {
            Tessellator instance = Tessellator.instance;
            instance.startDrawingQuads();
            instance.setNormal(0.0F, -1.0F, 0.0F);
            this.renderFaceYNeg(blockAnvil, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(blockAnvil, 0, n5));
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(0.0F, 1.0F, 0.0F);
            this.renderFaceYPos(blockAnvil, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(blockAnvil, 1, n5));
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(0.0F, 0.0F, -1.0F);
            this.renderFaceZNeg(blockAnvil, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(blockAnvil, 2, n5));
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(0.0F, 0.0F, 1.0F);
            this.renderFaceZPos(blockAnvil, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(blockAnvil, 3, n5));
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(-1.0F, 0.0F, 0.0F);
            this.renderFaceXNeg(blockAnvil, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(blockAnvil, 4, n5));
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(1.0F, 0.0F, 0.0F);
            this.renderFaceXPos(blockAnvil, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(blockAnvil, 5, n5));
            instance.draw();
        } else {
            this.renderStandardBlock(blockAnvil, n, n2, n3);
        }

        return f + f3;
    }

    public boolean renderBlockTorch(Block block, int n, int n2, int n3) {
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        double d = 0.4F;
        double d2 = 0.5 - d;
        double d3 = 0.2F;
        if (blockMetadata == 1) {
            this.renderTorchAtAngle(block, n - d2, n2 + d3, n3, -d, 0.0, 0);
        } else if (blockMetadata == 2) {
            this.renderTorchAtAngle(block, n + d2, n2 + d3, n3, d, 0.0, 0);
        } else if (blockMetadata == 3) {
            this.renderTorchAtAngle(block, n, n2 + d3, n3 - d2, 0.0, -d, 0);
        } else if (blockMetadata == 4) {
            this.renderTorchAtAngle(block, n, n2 + d3, n3 + d2, 0.0, d, 0);
        } else {
            this.renderTorchAtAngle(block, n, n2, n3, 0.0, 0.0, 0);
            if (block != Blocks.torch && Config.isBetterSnow() && this.hasSnowNeighbours(n, n2, n3)) {
                this.renderSnow(n, n2, n3, Blocks.snow_layer.getBlockBoundsMaxY());
            }
        }

        return true;
    }

    public boolean renderBlockRepeater(BlockRedstoneRepeater blockRedstoneRepeater, int n, int n2, int n3) {
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        int n4 = blockMetadata & 3;
        int n5 = (blockMetadata & 12) >> 2;
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(blockRedstoneRepeater.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        double d = -0.1875;
        boolean isLocked = blockRedstoneRepeater.isLocked(this.blockAccess, n, n2, n3, blockMetadata);
        double d2 = 0.0;
        double d3 = 0.0;
        double d4 = 0.0;
        double d5 = 0.0;
        switch (n4) {
            case 0:
                d5 = -0.3125;
                d3 = BlockRedstoneRepeater.repeaterTorchOffset[n5];
                break;
            case 1:
                d4 = 0.3125;
                d2 = -BlockRedstoneRepeater.repeaterTorchOffset[n5];
                break;
            case 2:
                d5 = 0.3125;
                d3 = -BlockRedstoneRepeater.repeaterTorchOffset[n5];
                break;
            case 3:
                d4 = -0.3125;
                d2 = BlockRedstoneRepeater.repeaterTorchOffset[n5];
        }

        if (!isLocked) {
            this.renderTorchAtAngle(blockRedstoneRepeater, n + d2, n2 + d, n3 + d3, 0.0, 0.0, 0);
        } else {
            IIcon blockIcon = this.getBlockIcon(Blocks.bedrock);
            this.setOverrideBlockTexture(blockIcon);
            float f = 2.0F;
            float f2 = 14.0F;
            float f3 = 7.0F;
            float f4 = 9.0F;
            switch (n4) {
                case 1:
                case 3:
                    f = 7.0F;
                    f2 = 9.0F;
                    f3 = 2.0F;
                    f4 = 14.0F;
                case 0:
                case 2:
                default:
                    this.setRenderBounds(
                        f / 16.0F + (float)d2, 0.125, f3 / 16.0F + (float)d3, f2 / 16.0F + (float)d2, 0.25, f4 / 16.0F + (float)d3
                    );
                    double interpolatedU = blockIcon.getInterpolatedU(f);
                    double interpolatedV = blockIcon.getInterpolatedV(f3);
                    double interpolatedU2 = blockIcon.getInterpolatedU(f2);
                    double interpolatedV2 = blockIcon.getInterpolatedV(f4);
                    instance.addVertexWithUV(n + f / 16.0F + d2, n2 + 0.25F, n3 + f3 / 16.0F + d3, interpolatedU, interpolatedV);
                    instance.addVertexWithUV(n + f / 16.0F + d2, n2 + 0.25F, n3 + f4 / 16.0F + d3, interpolatedU, interpolatedV2);
                    instance.addVertexWithUV(n + f2 / 16.0F + d2, n2 + 0.25F, n3 + f4 / 16.0F + d3, interpolatedU2, interpolatedV2);
                    instance.addVertexWithUV(n + f2 / 16.0F + d2, n2 + 0.25F, n3 + f3 / 16.0F + d3, interpolatedU2, interpolatedV);
                    this.renderStandardBlock(blockRedstoneRepeater, n, n2, n3);
                    this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
                    this.clearOverrideBlockTexture();
            }
        }

        instance.setBrightness(blockRedstoneRepeater.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        this.renderTorchAtAngle(blockRedstoneRepeater, n + d4, n2 + d, n3 + d5, 0.0, 0.0, 0);
        this.renderBlockRedstoneDiode(blockRedstoneRepeater, n, n2, n3);
        return true;
    }

    public boolean renderBlockRedstoneComparator(BlockRedstoneComparator blockRedstoneComparator, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(blockRedstoneComparator.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        int n4 = blockMetadata & 3;
        double d = 0.0;
        double d2 = -0.1875;
        double d3 = 0.0;
        double d4 = 0.0;
        double d5 = 0.0;
        IIcon icon;
        if (blockRedstoneComparator.check2(blockMetadata)) {
            icon = Blocks.redstone_torch.getBlockTextureFromSide(0);
        } else {
            d2 -= 0.1875;
            icon = Blocks.unlit_redstone_torch.getBlockTextureFromSide(0);
        }

        switch (n4) {
            case 0:
                d3 = -0.3125;
                d5 = 1.0;
                break;
            case 1:
                d = 0.3125;
                d4 = -1.0;
                break;
            case 2:
                d3 = 0.3125;
                d5 = -1.0;
                break;
            case 3:
                d = -0.3125;
                d4 = 1.0;
        }

        this.renderTorchAtAngle(blockRedstoneComparator, n + 0.25 * d4 + 0.1875 * d5, n2 - 0.1875F, n3 + 0.25 * d5 + 0.1875 * d4, 0.0, 0.0, blockMetadata);
        this.renderTorchAtAngle(blockRedstoneComparator, n + 0.25 * d4 + -0.1875 * d5, n2 - 0.1875F, n3 + 0.25 * d5 + -0.1875 * d4, 0.0, 0.0, blockMetadata);
        this.setOverrideBlockTexture(icon);
        this.renderTorchAtAngle(blockRedstoneComparator, n + d, n2 + d2, n3 + d3, 0.0, 0.0, blockMetadata);
        this.clearOverrideBlockTexture();
        this.renderBlockRedstoneDiodeMetadata(blockRedstoneComparator, n, n2, n3, n4);
        return true;
    }

    public boolean renderBlockRedstoneDiode(BlockRedstoneDiode blockRedstoneDiode, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        this.renderBlockRedstoneDiodeMetadata(blockRedstoneDiode, n, n2, n3, this.blockAccess.getBlockMetadata(n, n2, n3) & 3);
        return true;
    }

    public void renderBlockRedstoneDiodeMetadata(BlockRedstoneDiode blockRedstoneDiode, int n, int n2, int n3, int n4) {
        this.renderStandardBlock(blockRedstoneDiode, n, n2, n3);
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(blockRedstoneDiode.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        IIcon blockIconFromSideAndMetadata = this.getBlockIconFromSideAndMetadata(blockRedstoneDiode, 1, blockMetadata);
        double minU = blockIconFromSideAndMetadata.getMinU();
        double maxU = blockIconFromSideAndMetadata.getMaxU();
        double minV = blockIconFromSideAndMetadata.getMinV();
        double maxV = blockIconFromSideAndMetadata.getMaxV();
        double d = 0.125;
        double d2 = n + 1;
        double d3 = n + 1;
        double d4 = n + 0;
        double d5 = n + 0;
        double d6 = n3 + 0;
        double d7 = n3 + 1;
        double d8 = n3 + 1;
        double d9 = n3 + 0;
        double d10 = n2 + d;
        if (n4 == 2) {
            d2 = d3 = n + 0;
            d4 = d5 = n + 1;
            d6 = d9 = n3 + 1;
            d7 = d8 = n3 + 0;
        } else if (n4 == 3) {
            d2 = d5 = n + 0;
            d3 = d4 = n + 1;
            d6 = d7 = n3 + 0;
            d8 = d9 = n3 + 1;
        } else if (n4 == 1) {
            d2 = d5 = n + 1;
            d3 = d4 = n + 0;
            d6 = d7 = n3 + 1;
            d8 = d9 = n3 + 0;
        }

        instance.addVertexWithUV(d5, d10, d9, minU, minV);
        instance.addVertexWithUV(d4, d10, d8, minU, maxV);
        instance.addVertexWithUV(d3, d10, d7, maxU, maxV);
        instance.addVertexWithUV(d2, d10, d6, maxU, minV);
    }

    public void renderPistonBaseAllFaces(Block block, int n, int n2, int n3) {
        this.renderAllFaces = true;
        this.renderPistonBase(block, n, n2, n3, true);
        this.renderAllFaces = false;
    }

    public boolean renderPistonBase(Block block, int n, int n2, int n3, boolean flag) {
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        boolean flag2 = flag || (blockMetadata & 8) != 0;
        int pistonOrientation = BlockPistonBase.getPistonOrientation(blockMetadata);
        float f = 0.25F;
        if (flag2) {
            switch (pistonOrientation) {
                case 0:
                    this.uvRotateEast = 3;
                    this.uvRotateWest = 3;
                    this.uvRotateSouth = 3;
                    this.uvRotateNorth = 3;
                    this.setRenderBounds(0.0, 0.25, 0.0, 1.0, 1.0, 1.0);
                    break;
                case 1:
                    this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
                    break;
                case 2:
                    this.uvRotateSouth = 1;
                    this.uvRotateNorth = 2;
                    this.setRenderBounds(0.0, 0.0, 0.25, 1.0, 1.0, 1.0);
                    break;
                case 3:
                    this.uvRotateSouth = 2;
                    this.uvRotateNorth = 1;
                    this.uvRotateTop = 3;
                    this.uvRotateBottom = 3;
                    this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.75);
                    break;
                case 4:
                    this.uvRotateEast = 1;
                    this.uvRotateWest = 2;
                    this.uvRotateTop = 2;
                    this.uvRotateBottom = 1;
                    this.setRenderBounds(0.25, 0.0, 0.0, 1.0, 1.0, 1.0);
                    break;
                case 5:
                    this.uvRotateEast = 2;
                    this.uvRotateWest = 1;
                    this.uvRotateTop = 1;
                    this.uvRotateBottom = 2;
                    this.setRenderBounds(0.0, 0.0, 0.0, 0.75, 1.0, 1.0);
            }

            ((BlockPistonBase)block)
                .process(
                    (float)this.renderMinX, (float)this.renderMinY, (float)this.renderMinZ, (float)this.renderMaxX, (float)this.renderMaxY, (float)this.renderMaxZ
                );
            this.renderStandardBlock(block, n, n2, n3);
            this.uvRotateEast = 0;
            this.uvRotateWest = 0;
            this.uvRotateSouth = 0;
            this.uvRotateNorth = 0;
            this.uvRotateTop = 0;
            this.uvRotateBottom = 0;
            this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
            ((BlockPistonBase)block)
                .process(
                    (float)this.renderMinX, (float)this.renderMinY, (float)this.renderMinZ, (float)this.renderMaxX, (float)this.renderMaxY, (float)this.renderMaxZ
                );
        } else {
            switch (pistonOrientation) {
                case 0:
                    this.uvRotateEast = 3;
                    this.uvRotateWest = 3;
                    this.uvRotateSouth = 3;
                    this.uvRotateNorth = 3;
                case 1:
                default:
                    break;
                case 2:
                    this.uvRotateSouth = 1;
                    this.uvRotateNorth = 2;
                    break;
                case 3:
                    this.uvRotateSouth = 2;
                    this.uvRotateNorth = 1;
                    this.uvRotateTop = 3;
                    this.uvRotateBottom = 3;
                    break;
                case 4:
                    this.uvRotateEast = 1;
                    this.uvRotateWest = 2;
                    this.uvRotateTop = 2;
                    this.uvRotateBottom = 1;
                    break;
                case 5:
                    this.uvRotateEast = 2;
                    this.uvRotateWest = 1;
                    this.uvRotateTop = 1;
                    this.uvRotateBottom = 2;
            }

            this.renderStandardBlock(block, n, n2, n3);
            this.uvRotateEast = 0;
            this.uvRotateWest = 0;
            this.uvRotateSouth = 0;
            this.uvRotateNorth = 0;
            this.uvRotateTop = 0;
            this.uvRotateBottom = 0;
        }

        return true;
    }

    public void renderPistonRodUD(double d, double d2, double d3, double d4, double d5, double d6, float f, double d7) {
        IIcon pistonBaseIcon = BlockPistonBase.getPistonBaseIcon("piston_side");
        if (this.hasOverrideBlockTexture()) {
            pistonBaseIcon = this.overrideBlockTexture;
        }

        Tessellator instance = Tessellator.instance;
        double minU = pistonBaseIcon.getMinU();
        double minV = pistonBaseIcon.getMinV();
        double interpolatedU = pistonBaseIcon.getInterpolatedU(d7);
        double interpolatedV = pistonBaseIcon.getInterpolatedV(4.0);
        instance.setColorOpaque_F(f, f, f);
        instance.addVertexWithUV(d, d4, d5, interpolatedU, minV);
        instance.addVertexWithUV(d, d3, d5, minU, minV);
        instance.addVertexWithUV(d2, d3, d6, minU, interpolatedV);
        instance.addVertexWithUV(d2, d4, d6, interpolatedU, interpolatedV);
    }

    public void renderPistonRodSN(double d, double d2, double d3, double d4, double d5, double d6, float f, double d7) {
        IIcon pistonBaseIcon = BlockPistonBase.getPistonBaseIcon("piston_side");
        if (this.hasOverrideBlockTexture()) {
            pistonBaseIcon = this.overrideBlockTexture;
        }

        Tessellator instance = Tessellator.instance;
        double minU = pistonBaseIcon.getMinU();
        double minV = pistonBaseIcon.getMinV();
        double interpolatedU = pistonBaseIcon.getInterpolatedU(d7);
        double interpolatedV = pistonBaseIcon.getInterpolatedV(4.0);
        instance.setColorOpaque_F(f, f, f);
        instance.addVertexWithUV(d, d3, d6, interpolatedU, minV);
        instance.addVertexWithUV(d, d3, d5, minU, minV);
        instance.addVertexWithUV(d2, d4, d5, minU, interpolatedV);
        instance.addVertexWithUV(d2, d4, d6, interpolatedU, interpolatedV);
    }

    public void renderPistonRodEW(double d, double d2, double d3, double d4, double d5, double d6, float f, double d7) {
        IIcon pistonBaseIcon = BlockPistonBase.getPistonBaseIcon("piston_side");
        if (this.hasOverrideBlockTexture()) {
            pistonBaseIcon = this.overrideBlockTexture;
        }

        Tessellator instance = Tessellator.instance;
        double minU = pistonBaseIcon.getMinU();
        double minV = pistonBaseIcon.getMinV();
        double interpolatedU = pistonBaseIcon.getInterpolatedU(d7);
        double interpolatedV = pistonBaseIcon.getInterpolatedV(4.0);
        instance.setColorOpaque_F(f, f, f);
        instance.addVertexWithUV(d2, d3, d5, interpolatedU, minV);
        instance.addVertexWithUV(d, d3, d5, minU, minV);
        instance.addVertexWithUV(d, d4, d6, minU, interpolatedV);
        instance.addVertexWithUV(d2, d4, d6, interpolatedU, interpolatedV);
    }

    public void renderPistonExtensionAllFaces(Block block, int n, int n2, int n3, boolean flag) {
        this.renderAllFaces = true;
        this.renderPistonExtension(block, n, n2, n3, flag);
        this.renderAllFaces = false;
    }

    public boolean renderPistonExtension(Block block, int n, int n2, int n3, boolean flag) {
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        int directionMeta = BlockPistonExtension.getDirectionMeta(blockMetadata);
        float f = 0.25F;
        float f2 = 0.375F;
        float f3 = 0.625F;
        float f4 = flag ? 1.0F : 0.5F;
        double d = flag ? 16.0 : 8.0;
        switch (directionMeta) {
            case 0:
                this.uvRotateEast = 3;
                this.uvRotateWest = 3;
                this.uvRotateSouth = 3;
                this.uvRotateNorth = 3;
                this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.25, 1.0);
                this.renderStandardBlock(block, n, n2, n3);
                this.renderPistonRodUD(n + 0.375F, n + 0.625F, n2 + 0.25F, n2 + 0.25F + f4, n3 + 0.625F, n3 + 0.625F, this.lightLevel08, d);
                this.renderPistonRodUD(n + 0.625F, n + 0.375F, n2 + 0.25F, n2 + 0.25F + f4, n3 + 0.375F, n3 + 0.375F, this.lightLevel08, d);
                this.renderPistonRodUD(n + 0.375F, n + 0.375F, n2 + 0.25F, n2 + 0.25F + f4, n3 + 0.375F, n3 + 0.625F, this.lightLevel06, d);
                this.renderPistonRodUD(n + 0.625F, n + 0.625F, n2 + 0.25F, n2 + 0.25F + f4, n3 + 0.625F, n3 + 0.375F, this.lightLevel06, d);
                break;
            case 1:
                this.setRenderBounds(0.0, 0.75, 0.0, 1.0, 1.0, 1.0);
                this.renderStandardBlock(block, n, n2, n3);
                this.renderPistonRodUD(
                    n + 0.375F, n + 0.625F, n2 - 0.25F + 1.0F - f4, n2 - 0.25F + 1.0F, n3 + 0.625F, n3 + 0.625F, this.lightLevel08, d
                );
                this.renderPistonRodUD(
                    n + 0.625F, n + 0.375F, n2 - 0.25F + 1.0F - f4, n2 - 0.25F + 1.0F, n3 + 0.375F, n3 + 0.375F, this.lightLevel08, d
                );
                this.renderPistonRodUD(
                    n + 0.375F, n + 0.375F, n2 - 0.25F + 1.0F - f4, n2 - 0.25F + 1.0F, n3 + 0.375F, n3 + 0.625F, this.lightLevel06, d
                );
                this.renderPistonRodUD(
                    n + 0.625F, n + 0.625F, n2 - 0.25F + 1.0F - f4, n2 - 0.25F + 1.0F, n3 + 0.625F, n3 + 0.375F, this.lightLevel06, d
                );
                break;
            case 2:
                this.uvRotateSouth = 1;
                this.uvRotateNorth = 2;
                this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.25);
                this.renderStandardBlock(block, n, n2, n3);
                this.renderPistonRodSN(n + 0.375F, n + 0.375F, n2 + 0.625F, n2 + 0.375F, n3 + 0.25F, n3 + 0.25F + f4, this.lightLevel06, d);
                this.renderPistonRodSN(n + 0.625F, n + 0.625F, n2 + 0.375F, n2 + 0.625F, n3 + 0.25F, n3 + 0.25F + f4, this.lightLevel06, d);
                this.renderPistonRodSN(n + 0.375F, n + 0.625F, n2 + 0.375F, n2 + 0.375F, n3 + 0.25F, n3 + 0.25F + f4, this.lightLevel05, d);
                this.renderPistonRodSN(n + 0.625F, n + 0.375F, n2 + 0.625F, n2 + 0.625F, n3 + 0.25F, n3 + 0.25F + f4, 1.0F, d);
                break;
            case 3:
                this.uvRotateSouth = 2;
                this.uvRotateNorth = 1;
                this.uvRotateTop = 3;
                this.uvRotateBottom = 3;
                this.setRenderBounds(0.0, 0.0, 0.75, 1.0, 1.0, 1.0);
                this.renderStandardBlock(block, n, n2, n3);
                this.renderPistonRodSN(
                    n + 0.375F, n + 0.375F, n2 + 0.625F, n2 + 0.375F, n3 - 0.25F + 1.0F - f4, n3 - 0.25F + 1.0F, this.lightLevel06, d
                );
                this.renderPistonRodSN(
                    n + 0.625F, n + 0.625F, n2 + 0.375F, n2 + 0.625F, n3 - 0.25F + 1.0F - f4, n3 - 0.25F + 1.0F, this.lightLevel06, d
                );
                this.renderPistonRodSN(
                    n + 0.375F, n + 0.625F, n2 + 0.375F, n2 + 0.375F, n3 - 0.25F + 1.0F - f4, n3 - 0.25F + 1.0F, this.lightLevel05, d
                );
                this.renderPistonRodSN(n + 0.625F, n + 0.375F, n2 + 0.625F, n2 + 0.625F, n3 - 0.25F + 1.0F - f4, n3 - 0.25F + 1.0F, 1.0F, d);
                break;
            case 4:
                this.uvRotateEast = 1;
                this.uvRotateWest = 2;
                this.uvRotateTop = 2;
                this.uvRotateBottom = 1;
                this.setRenderBounds(0.0, 0.0, 0.0, 0.25, 1.0, 1.0);
                this.renderStandardBlock(block, n, n2, n3);
                this.renderPistonRodEW(n + 0.25F, n + 0.25F + f4, n2 + 0.375F, n2 + 0.375F, n3 + 0.625F, n3 + 0.375F, this.lightLevel05, d);
                this.renderPistonRodEW(n + 0.25F, n + 0.25F + f4, n2 + 0.625F, n2 + 0.625F, n3 + 0.375F, n3 + 0.625F, 1.0F, d);
                this.renderPistonRodEW(n + 0.25F, n + 0.25F + f4, n2 + 0.375F, n2 + 0.625F, n3 + 0.375F, n3 + 0.375F, this.lightLevel06, d);
                this.renderPistonRodEW(n + 0.25F, n + 0.25F + f4, n2 + 0.625F, n2 + 0.375F, n3 + 0.625F, n3 + 0.625F, this.lightLevel06, d);
                break;
            case 5:
                this.uvRotateEast = 2;
                this.uvRotateWest = 1;
                this.uvRotateTop = 1;
                this.uvRotateBottom = 2;
                this.setRenderBounds(0.75, 0.0, 0.0, 1.0, 1.0, 1.0);
                this.renderStandardBlock(block, n, n2, n3);
                this.renderPistonRodEW(
                    n - 0.25F + 1.0F - f4, n - 0.25F + 1.0F, n2 + 0.375F, n2 + 0.375F, n3 + 0.625F, n3 + 0.375F, this.lightLevel05, d
                );
                this.renderPistonRodEW(n - 0.25F + 1.0F - f4, n - 0.25F + 1.0F, n2 + 0.625F, n2 + 0.625F, n3 + 0.375F, n3 + 0.625F, 1.0F, d);
                this.renderPistonRodEW(
                    n - 0.25F + 1.0F - f4, n - 0.25F + 1.0F, n2 + 0.375F, n2 + 0.625F, n3 + 0.375F, n3 + 0.375F, this.lightLevel06, d
                );
                this.renderPistonRodEW(
                    n - 0.25F + 1.0F - f4, n - 0.25F + 1.0F, n2 + 0.625F, n2 + 0.375F, n3 + 0.625F, n3 + 0.625F, this.lightLevel06, d
                );
        }

        this.uvRotateEast = 0;
        this.uvRotateWest = 0;
        this.uvRotateSouth = 0;
        this.uvRotateNorth = 0;
        this.uvRotateTop = 0;
        this.uvRotateBottom = 0;
        this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        return true;
    }

    public boolean renderBlockLever(Block block, int n, int n2, int n3) {
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        int n4 = blockMetadata & 7;
        boolean flag = (blockMetadata & 8) > 0;
        Tessellator instance = Tessellator.instance;
        boolean hasOverrideBlockTexture = this.hasOverrideBlockTexture();
        if (!hasOverrideBlockTexture) {
            this.setOverrideBlockTexture(this.getBlockIcon(Blocks.cobblestone));
        }

        float f = 0.25F;
        float f2 = 0.1875F;
        float f3 = 0.1875F;
        if (n4 == 5) {
            this.setRenderBounds(0.5F - f2, 0.0, 0.5F - f, 0.5F + f2, f3, 0.5F + f);
        } else if (n4 == 6) {
            this.setRenderBounds(0.5F - f, 0.0, 0.5F - f2, 0.5F + f, f3, 0.5F + f2);
        } else if (n4 == 4) {
            this.setRenderBounds(0.5F - f2, 0.5F - f, 1.0F - f3, 0.5F + f2, 0.5F + f, 1.0);
        } else if (n4 == 3) {
            this.setRenderBounds(0.5F - f2, 0.5F - f, 0.0, 0.5F + f2, 0.5F + f, f3);
        } else if (n4 == 2) {
            this.setRenderBounds(1.0F - f3, 0.5F - f, 0.5F - f2, 1.0, 0.5F + f, 0.5F + f2);
        } else if (n4 == 1) {
            this.setRenderBounds(0.0, 0.5F - f, 0.5F - f2, f3, 0.5F + f, 0.5F + f2);
        } else if (n4 == 0) {
            this.setRenderBounds(0.5F - f, 1.0F - f3, 0.5F - f2, 0.5F + f, 1.0, 0.5F + f2);
        } else if (n4 == 7) {
            this.setRenderBounds(0.5F - f2, 1.0F - f3, 0.5F - f, 0.5F + f2, 1.0, 0.5F + f);
        }

        this.renderStandardBlock(block, n, n2, n3);
        if (!hasOverrideBlockTexture) {
            this.clearOverrideBlockTexture();
        }

        instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        IIcon blockIconFromSide = this.getBlockIconFromSide(block, 0);
        if (this.hasOverrideBlockTexture()) {
            blockIconFromSide = this.overrideBlockTexture;
        }

        double minU = blockIconFromSide.getMinU();
        double minV = blockIconFromSide.getMinV();
        double maxU = blockIconFromSide.getMaxU();
        double maxV = blockIconFromSide.getMaxV();
        Vec3[] vec3s = new Vec3[8];
        float f4 = 0.0625F;
        float f5 = 0.0625F;
        float f6 = 0.625F;
        vec3s[0] = Vec3.createVectorHelper(-f4, 0.0, -f5);
        vec3s[1] = Vec3.createVectorHelper(f4, 0.0, -f5);
        vec3s[2] = Vec3.createVectorHelper(f4, 0.0, f5);
        vec3s[3] = Vec3.createVectorHelper(-f4, 0.0, f5);
        vec3s[4] = Vec3.createVectorHelper(-f4, f6, -f5);
        vec3s[5] = Vec3.createVectorHelper(f4, f6, -f5);
        vec3s[6] = Vec3.createVectorHelper(f4, f6, f5);
        vec3s[7] = Vec3.createVectorHelper(-f4, f6, f5);

        for (int i = 0; i < 8; i++) {
            if (flag) {
                vec3s[i].zCoord -= 0.0625;
                vec3s[i].rotateAroundX((float) Math.PI * 2.0F / 9.0F);
            } else {
                vec3s[i].zCoord += 0.0625;
                vec3s[i].rotateAroundX((float) -Math.PI * 2.0F / 9.0F);
            }

            if (n4 == 0 || n4 == 7) {
                vec3s[i].rotateAroundZ((float) Math.PI);
            }

            if (n4 == 6 || n4 == 0) {
                vec3s[i].rotateAroundY((float) (Math.PI / 2));
            }

            if (n4 > 0 && n4 < 5) {
                vec3s[i].yCoord -= 0.375;
                vec3s[i].rotateAroundX((float) (Math.PI / 2));
                if (n4 == 4) {
                    vec3s[i].rotateAroundY(0.0F);
                }

                if (n4 == 3) {
                    vec3s[i].rotateAroundY((float) Math.PI);
                }

                if (n4 == 2) {
                    vec3s[i].rotateAroundY((float) (Math.PI / 2));
                }

                if (n4 == 1) {
                    vec3s[i].rotateAroundY((float) (-Math.PI / 2));
                }

                vec3s[i].xCoord += n + 0.5;
                vec3s[i].yCoord += n2 + 0.5F;
                vec3s[i].zCoord += n3 + 0.5;
            } else if (n4 != 0 && n4 != 7) {
                vec3s[i].xCoord += n + 0.5;
                vec3s[i].yCoord += n2 + 0.125F;
                vec3s[i].zCoord += n3 + 0.5;
            } else {
                vec3s[i].xCoord += n + 0.5;
                vec3s[i].yCoord += n2 + 0.875F;
                vec3s[i].zCoord += n3 + 0.5;
            }
        }

        Vec3 vec3 = null;
        Vec3 vec32 = null;
        Vec3 vec33 = null;
        Vec3 vec34 = null;

        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                minU = blockIconFromSide.getInterpolatedU(7.0);
                minV = blockIconFromSide.getInterpolatedV(6.0);
                maxU = blockIconFromSide.getInterpolatedU(9.0);
                maxV = blockIconFromSide.getInterpolatedV(8.0);
            } else if (i == 2) {
                minU = blockIconFromSide.getInterpolatedU(7.0);
                minV = blockIconFromSide.getInterpolatedV(6.0);
                maxU = blockIconFromSide.getInterpolatedU(9.0);
                maxV = blockIconFromSide.getMaxV();
            }

            if (i == 0) {
                vec3 = vec3s[0];
                vec32 = vec3s[1];
                vec33 = vec3s[2];
                vec34 = vec3s[3];
            } else if (i == 1) {
                vec3 = vec3s[7];
                vec32 = vec3s[6];
                vec33 = vec3s[5];
                vec34 = vec3s[4];
            } else if (i == 2) {
                vec3 = vec3s[1];
                vec32 = vec3s[0];
                vec33 = vec3s[4];
                vec34 = vec3s[5];
            } else if (i == 3) {
                vec3 = vec3s[2];
                vec32 = vec3s[1];
                vec33 = vec3s[5];
                vec34 = vec3s[6];
            } else if (i == 4) {
                vec3 = vec3s[3];
                vec32 = vec3s[2];
                vec33 = vec3s[6];
                vec34 = vec3s[7];
            } else if (i == 5) {
                vec3 = vec3s[0];
                vec32 = vec3s[3];
                vec33 = vec3s[7];
                vec34 = vec3s[4];
            }

            instance.addVertexWithUV(vec3.xCoord, vec3.yCoord, vec3.zCoord, minU, maxV);
            instance.addVertexWithUV(vec32.xCoord, vec32.yCoord, vec32.zCoord, maxU, maxV);
            instance.addVertexWithUV(vec33.xCoord, vec33.yCoord, vec33.zCoord, maxU, minV);
            instance.addVertexWithUV(vec34.xCoord, vec34.yCoord, vec34.zCoord, minU, minV);
        }

        if (Config.isBetterSnow() && this.hasSnowNeighbours(n, n2, n3)) {
            this.renderSnow(n, n2, n3, Blocks.snow_layer.getBlockBoundsMaxY());
        }

        return true;
    }

    public boolean renderBlockTripWireSource(Block block, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        int n4 = blockMetadata & 3;
        boolean flag = (blockMetadata & 4) == 4;
        boolean flag2 = (blockMetadata & 8) == 8;
        boolean flag3 = !World.doesBlockHaveSolidTopSurface(this.blockAccess, n, n2 - 1, n3);
        boolean hasOverrideBlockTexture = this.hasOverrideBlockTexture();
        if (!hasOverrideBlockTexture) {
            this.setOverrideBlockTexture(this.getBlockIcon(Blocks.planks));
        }

        float f = 0.25F;
        float f2 = 0.125F;
        float f3 = 0.125F;
        float f4 = 0.3F - f;
        float f5 = 0.3F + f;
        if (n4 == 2) {
            this.setRenderBounds(0.5F - f2, f4, 1.0F - f3, 0.5F + f2, f5, 1.0);
        } else if (n4 == 0) {
            this.setRenderBounds(0.5F - f2, f4, 0.0, 0.5F + f2, f5, f3);
        } else if (n4 == 1) {
            this.setRenderBounds(1.0F - f3, f4, 0.5F - f2, 1.0, f5, 0.5F + f2);
        } else if (n4 == 3) {
            this.setRenderBounds(0.0, f4, 0.5F - f2, f3, f5, 0.5F + f2);
        }

        this.renderStandardBlock(block, n, n2, n3);
        if (!hasOverrideBlockTexture) {
            this.clearOverrideBlockTexture();
        }

        instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        IIcon blockIconFromSide = this.getBlockIconFromSide(block, 0);
        if (this.hasOverrideBlockTexture()) {
            blockIconFromSide = this.overrideBlockTexture;
        }

        double minU = blockIconFromSide.getMinU();
        double minV = blockIconFromSide.getMinV();
        double maxU = blockIconFromSide.getMaxU();
        double maxV = blockIconFromSide.getMaxV();
        Vec3[] vec3s = new Vec3[8];
        float f6 = 0.046875F;
        float f7 = 0.046875F;
        float f8 = 0.3125F;
        vec3s[0] = Vec3.createVectorHelper(-f6, 0.0, -f7);
        vec3s[1] = Vec3.createVectorHelper(f6, 0.0, -f7);
        vec3s[2] = Vec3.createVectorHelper(f6, 0.0, f7);
        vec3s[3] = Vec3.createVectorHelper(-f6, 0.0, f7);
        vec3s[4] = Vec3.createVectorHelper(-f6, f8, -f7);
        vec3s[5] = Vec3.createVectorHelper(f6, f8, -f7);
        vec3s[6] = Vec3.createVectorHelper(f6, f8, f7);
        vec3s[7] = Vec3.createVectorHelper(-f6, f8, f7);

        for (int i = 0; i < 8; i++) {
            vec3s[i].zCoord += 0.0625;
            if (flag2) {
                vec3s[i].rotateAroundX((float) (Math.PI / 6));
                vec3s[i].yCoord -= 0.4375;
            } else if (flag) {
                vec3s[i].rotateAroundX(0.08726647F);
                vec3s[i].yCoord -= 0.4375;
            } else {
                vec3s[i].rotateAroundX((float) -Math.PI * 2.0F / 9.0F);
                vec3s[i].yCoord -= 0.375;
            }

            vec3s[i].rotateAroundX((float) (Math.PI / 2));
            if (n4 == 2) {
                vec3s[i].rotateAroundY(0.0F);
            }

            if (n4 == 0) {
                vec3s[i].rotateAroundY((float) Math.PI);
            }

            if (n4 == 1) {
                vec3s[i].rotateAroundY((float) (Math.PI / 2));
            }

            if (n4 == 3) {
                vec3s[i].rotateAroundY((float) (-Math.PI / 2));
            }

            vec3s[i].xCoord += n + 0.5;
            vec3s[i].yCoord += n2 + 0.3125F;
            vec3s[i].zCoord += n3 + 0.5;
        }

        Vec3 vec3 = null;
        Vec3 vec32 = null;
        Vec3 vec33 = null;
        Vec3 vec34 = null;
        byte b = 7;
        byte b2 = 9;
        byte b3 = 9;
        byte b4 = 16;

        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                vec3 = vec3s[0];
                vec32 = vec3s[1];
                vec33 = vec3s[2];
                vec34 = vec3s[3];
                minU = blockIconFromSide.getInterpolatedU(b);
                minV = blockIconFromSide.getInterpolatedV(b3);
                maxU = blockIconFromSide.getInterpolatedU(b2);
                maxV = blockIconFromSide.getInterpolatedV(b3 + 2);
            } else if (i == 1) {
                vec3 = vec3s[7];
                vec32 = vec3s[6];
                vec33 = vec3s[5];
                vec34 = vec3s[4];
            } else if (i == 2) {
                vec3 = vec3s[1];
                vec32 = vec3s[0];
                vec33 = vec3s[4];
                vec34 = vec3s[5];
                minU = blockIconFromSide.getInterpolatedU(b);
                minV = blockIconFromSide.getInterpolatedV(b3);
                maxU = blockIconFromSide.getInterpolatedU(b2);
                maxV = blockIconFromSide.getInterpolatedV(b4);
            } else if (i == 3) {
                vec3 = vec3s[2];
                vec32 = vec3s[1];
                vec33 = vec3s[5];
                vec34 = vec3s[6];
            } else if (i == 4) {
                vec3 = vec3s[3];
                vec32 = vec3s[2];
                vec33 = vec3s[6];
                vec34 = vec3s[7];
            } else if (i == 5) {
                vec3 = vec3s[0];
                vec32 = vec3s[3];
                vec33 = vec3s[7];
                vec34 = vec3s[4];
            }

            instance.addVertexWithUV(vec3.xCoord, vec3.yCoord, vec3.zCoord, minU, maxV);
            instance.addVertexWithUV(vec32.xCoord, vec32.yCoord, vec32.zCoord, maxU, maxV);
            instance.addVertexWithUV(vec33.xCoord, vec33.yCoord, vec33.zCoord, maxU, minV);
            instance.addVertexWithUV(vec34.xCoord, vec34.yCoord, vec34.zCoord, minU, minV);
        }

        float f9 = 0.09375F;
        float f10 = 0.09375F;
        float f11 = 0.03125F;
        vec3s[0] = Vec3.createVectorHelper(-f9, 0.0, -f10);
        vec3s[1] = Vec3.createVectorHelper(f9, 0.0, -f10);
        vec3s[2] = Vec3.createVectorHelper(f9, 0.0, f10);
        vec3s[3] = Vec3.createVectorHelper(-f9, 0.0, f10);
        vec3s[4] = Vec3.createVectorHelper(-f9, f11, -f10);
        vec3s[5] = Vec3.createVectorHelper(f9, f11, -f10);
        vec3s[6] = Vec3.createVectorHelper(f9, f11, f10);
        vec3s[7] = Vec3.createVectorHelper(-f9, f11, f10);

        for (int i = 0; i < 8; i++) {
            vec3s[i].zCoord += 0.21875;
            if (flag2) {
                vec3s[i].yCoord -= 0.09375;
                vec3s[i].zCoord -= 0.1625;
                vec3s[i].rotateAroundX(0.0F);
            } else if (flag) {
                vec3s[i].yCoord += 0.015625;
                vec3s[i].zCoord -= 0.171875;
                vec3s[i].rotateAroundX(0.17453294F);
            } else {
                vec3s[i].rotateAroundX(0.87266463F);
            }

            if (n4 == 2) {
                vec3s[i].rotateAroundY(0.0F);
            }

            if (n4 == 0) {
                vec3s[i].rotateAroundY((float) Math.PI);
            }

            if (n4 == 1) {
                vec3s[i].rotateAroundY((float) (Math.PI / 2));
            }

            if (n4 == 3) {
                vec3s[i].rotateAroundY((float) (-Math.PI / 2));
            }

            vec3s[i].xCoord += n + 0.5;
            vec3s[i].yCoord += n2 + 0.3125F;
            vec3s[i].zCoord += n3 + 0.5;
        }

        byte b5 = 5;
        byte b6 = 11;
        byte b7 = 3;
        byte b8 = 9;

        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                vec3 = vec3s[0];
                vec32 = vec3s[1];
                vec33 = vec3s[2];
                vec34 = vec3s[3];
                minU = blockIconFromSide.getInterpolatedU(b5);
                minV = blockIconFromSide.getInterpolatedV(b7);
                maxU = blockIconFromSide.getInterpolatedU(b6);
                maxV = blockIconFromSide.getInterpolatedV(b8);
            } else if (i == 1) {
                vec3 = vec3s[7];
                vec32 = vec3s[6];
                vec33 = vec3s[5];
                vec34 = vec3s[4];
            } else if (i == 2) {
                vec3 = vec3s[1];
                vec32 = vec3s[0];
                vec33 = vec3s[4];
                vec34 = vec3s[5];
                minU = blockIconFromSide.getInterpolatedU(b5);
                minV = blockIconFromSide.getInterpolatedV(b7);
                maxU = blockIconFromSide.getInterpolatedU(b6);
                maxV = blockIconFromSide.getInterpolatedV(b7 + 2);
            } else if (i == 3) {
                vec3 = vec3s[2];
                vec32 = vec3s[1];
                vec33 = vec3s[5];
                vec34 = vec3s[6];
            } else if (i == 4) {
                vec3 = vec3s[3];
                vec32 = vec3s[2];
                vec33 = vec3s[6];
                vec34 = vec3s[7];
            } else if (i == 5) {
                vec3 = vec3s[0];
                vec32 = vec3s[3];
                vec33 = vec3s[7];
                vec34 = vec3s[4];
            }

            instance.addVertexWithUV(vec3.xCoord, vec3.yCoord, vec3.zCoord, minU, maxV);
            instance.addVertexWithUV(vec32.xCoord, vec32.yCoord, vec32.zCoord, maxU, maxV);
            instance.addVertexWithUV(vec33.xCoord, vec33.yCoord, vec33.zCoord, maxU, minV);
            instance.addVertexWithUV(vec34.xCoord, vec34.yCoord, vec34.zCoord, minU, minV);
        }

        if (flag) {
            double yCoord = vec3s[0].yCoord;
            float f12 = 0.03125F;
            float f13 = 0.5F - f12 / 2.0F;
            float f14 = f13 + f12;
            double minU2 = blockIconFromSide.getMinU();
            double interpolatedV = blockIconFromSide.getInterpolatedV(flag ? 2.0 : 0.0);
            double maxU2 = blockIconFromSide.getMaxU();
            double interpolatedV2 = blockIconFromSide.getInterpolatedV(flag ? 4.0 : 2.0);
            double d = (flag3 ? 3.5F : 1.5F) / 16.0;
            instance.setColorOpaque_F(0.75F, 0.75F, 0.75F);
            if (n4 == 2) {
                instance.addVertexWithUV(n + f13, n2 + d, n3 + 0.25, minU2, interpolatedV);
                instance.addVertexWithUV(n + f14, n2 + d, n3 + 0.25, minU2, interpolatedV2);
                instance.addVertexWithUV(n + f14, n2 + d, n3, maxU2, interpolatedV2);
                instance.addVertexWithUV(n + f13, n2 + d, n3, maxU2, interpolatedV);
                instance.addVertexWithUV(n + f13, yCoord, n3 + 0.5, minU2, interpolatedV);
                instance.addVertexWithUV(n + f14, yCoord, n3 + 0.5, minU2, interpolatedV2);
                instance.addVertexWithUV(n + f14, n2 + d, n3 + 0.25, maxU2, interpolatedV2);
                instance.addVertexWithUV(n + f13, n2 + d, n3 + 0.25, maxU2, interpolatedV);
            } else if (n4 == 0) {
                instance.addVertexWithUV(n + f13, n2 + d, n3 + 0.75, minU2, interpolatedV);
                instance.addVertexWithUV(n + f14, n2 + d, n3 + 0.75, minU2, interpolatedV2);
                instance.addVertexWithUV(n + f14, yCoord, n3 + 0.5, maxU2, interpolatedV2);
                instance.addVertexWithUV(n + f13, yCoord, n3 + 0.5, maxU2, interpolatedV);
                instance.addVertexWithUV(n + f13, n2 + d, n3 + 1, minU2, interpolatedV);
                instance.addVertexWithUV(n + f14, n2 + d, n3 + 1, minU2, interpolatedV2);
                instance.addVertexWithUV(n + f14, n2 + d, n3 + 0.75, maxU2, interpolatedV2);
                instance.addVertexWithUV(n + f13, n2 + d, n3 + 0.75, maxU2, interpolatedV);
            } else if (n4 == 1) {
                instance.addVertexWithUV(n, n2 + d, n3 + f14, minU2, interpolatedV2);
                instance.addVertexWithUV(n + 0.25, n2 + d, n3 + f14, maxU2, interpolatedV2);
                instance.addVertexWithUV(n + 0.25, n2 + d, n3 + f13, maxU2, interpolatedV);
                instance.addVertexWithUV(n, n2 + d, n3 + f13, minU2, interpolatedV);
                instance.addVertexWithUV(n + 0.25, n2 + d, n3 + f14, minU2, interpolatedV2);
                instance.addVertexWithUV(n + 0.5, yCoord, n3 + f14, maxU2, interpolatedV2);
                instance.addVertexWithUV(n + 0.5, yCoord, n3 + f13, maxU2, interpolatedV);
                instance.addVertexWithUV(n + 0.25, n2 + d, n3 + f13, minU2, interpolatedV);
            } else {
                instance.addVertexWithUV(n + 0.5, yCoord, n3 + f14, minU2, interpolatedV2);
                instance.addVertexWithUV(n + 0.75, n2 + d, n3 + f14, maxU2, interpolatedV2);
                instance.addVertexWithUV(n + 0.75, n2 + d, n3 + f13, maxU2, interpolatedV);
                instance.addVertexWithUV(n + 0.5, yCoord, n3 + f13, minU2, interpolatedV);
                instance.addVertexWithUV(n + 0.75, n2 + d, n3 + f14, minU2, interpolatedV2);
                instance.addVertexWithUV(n + 1, n2 + d, n3 + f14, maxU2, interpolatedV2);
                instance.addVertexWithUV(n + 1, n2 + d, n3 + f13, maxU2, interpolatedV);
                instance.addVertexWithUV(n + 0.75, n2 + d, n3 + f13, minU2, interpolatedV);
            }
        }

        return true;
    }

    public boolean renderBlockTripWire(Block block, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        IIcon blockIconFromSide = this.getBlockIconFromSide(block, 0);
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        boolean flag = (blockMetadata & 4) == 4;
        boolean flag2 = (blockMetadata & 2) == 2;
        if (this.hasOverrideBlockTexture()) {
            blockIconFromSide = this.overrideBlockTexture;
        }

        PackTweaksMod packTweaksMod = CheatBreaker.getInstance().getModManager().packTweaksMod;
        if ((Boolean)packTweaksMod.colorPlacedString.getValue() && packTweaksMod.isEnabled()) {
            blockIconFromSide = Block.iIcon;
            float f = (packTweaksMod.placedStringColor.getColorValue() >> 24 & 0xFF) / 255.0F;
            float f2 = (packTweaksMod.placedStringColor.getColorValue() >> 16 & 0xFF) / 255.0F;
            float f3 = (packTweaksMod.placedStringColor.getColorValue() >> 8 & 0xFF) / 255.0F;
            float f4 = (packTweaksMod.placedStringColor.getColorValue() & 0xFF) / 255.0F;
            instance.setColorRGBA_F(f2, f3, f4, f);
        } else {
            instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
            instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        }

        double minU = blockIconFromSide.getMinU();
        double interpolatedV = blockIconFromSide.getInterpolatedV(flag ? 2.0 : 0.0);
        double maxU = blockIconFromSide.getMaxU();
        double interpolatedV2 = blockIconFromSide.getInterpolatedV(flag ? 4.0 : 2.0);
        double d = (flag2 ? 3.5F : 1.5F) / 16.0;
        boolean checkIBlockAccess = BlockTripWire.checkIBlockAccess(this.blockAccess, n, n2, n3, blockMetadata, 1);
        boolean checkIBlockAccess2 = BlockTripWire.checkIBlockAccess(this.blockAccess, n, n2, n3, blockMetadata, 3);
        boolean checkIBlockAccess3 = BlockTripWire.checkIBlockAccess(this.blockAccess, n, n2, n3, blockMetadata, 2);
        boolean checkIBlockAccess4 = BlockTripWire.checkIBlockAccess(this.blockAccess, n, n2, n3, blockMetadata, 0);
        float f = 0.03125F;
        float f2 = 0.5F - f / 2.0F;
        float f3 = f2 + f;
        if (!checkIBlockAccess3 && !checkIBlockAccess2 && !checkIBlockAccess4 && !checkIBlockAccess) {
            checkIBlockAccess3 = true;
            checkIBlockAccess4 = true;
        }

        if (checkIBlockAccess3) {
            instance.addVertexWithUV(n + f2, n2 + d, n3 + 0.25, minU, interpolatedV);
            instance.addVertexWithUV(n + f3, n2 + d, n3 + 0.25, minU, interpolatedV2);
            instance.addVertexWithUV(n + f3, n2 + d, n3, maxU, interpolatedV2);
            instance.addVertexWithUV(n + f2, n2 + d, n3, maxU, interpolatedV);
            instance.addVertexWithUV(n + f2, n2 + d, n3, maxU, interpolatedV);
            instance.addVertexWithUV(n + f3, n2 + d, n3, maxU, interpolatedV2);
            instance.addVertexWithUV(n + f3, n2 + d, n3 + 0.25, minU, interpolatedV2);
            instance.addVertexWithUV(n + f2, n2 + d, n3 + 0.25, minU, interpolatedV);
        }

        if (checkIBlockAccess3 || checkIBlockAccess4 && !checkIBlockAccess2 && !checkIBlockAccess) {
            instance.addVertexWithUV(n + f2, n2 + d, n3 + 0.5, minU, interpolatedV);
            instance.addVertexWithUV(n + f3, n2 + d, n3 + 0.5, minU, interpolatedV2);
            instance.addVertexWithUV(n + f3, n2 + d, n3 + 0.25, maxU, interpolatedV2);
            instance.addVertexWithUV(n + f2, n2 + d, n3 + 0.25, maxU, interpolatedV);
            instance.addVertexWithUV(n + f2, n2 + d, n3 + 0.25, maxU, interpolatedV);
            instance.addVertexWithUV(n + f3, n2 + d, n3 + 0.25, maxU, interpolatedV2);
            instance.addVertexWithUV(n + f3, n2 + d, n3 + 0.5, minU, interpolatedV2);
            instance.addVertexWithUV(n + f2, n2 + d, n3 + 0.5, minU, interpolatedV);
        }

        if (checkIBlockAccess4 || checkIBlockAccess3 && !checkIBlockAccess2 && !checkIBlockAccess) {
            instance.addVertexWithUV(n + f2, n2 + d, n3 + 0.75, minU, interpolatedV);
            instance.addVertexWithUV(n + f3, n2 + d, n3 + 0.75, minU, interpolatedV2);
            instance.addVertexWithUV(n + f3, n2 + d, n3 + 0.5, maxU, interpolatedV2);
            instance.addVertexWithUV(n + f2, n2 + d, n3 + 0.5, maxU, interpolatedV);
            instance.addVertexWithUV(n + f2, n2 + d, n3 + 0.5, maxU, interpolatedV);
            instance.addVertexWithUV(n + f3, n2 + d, n3 + 0.5, maxU, interpolatedV2);
            instance.addVertexWithUV(n + f3, n2 + d, n3 + 0.75, minU, interpolatedV2);
            instance.addVertexWithUV(n + f2, n2 + d, n3 + 0.75, minU, interpolatedV);
        }

        if (checkIBlockAccess4) {
            instance.addVertexWithUV(n + f2, n2 + d, n3 + 1, minU, interpolatedV);
            instance.addVertexWithUV(n + f3, n2 + d, n3 + 1, minU, interpolatedV2);
            instance.addVertexWithUV(n + f3, n2 + d, n3 + 0.75, maxU, interpolatedV2);
            instance.addVertexWithUV(n + f2, n2 + d, n3 + 0.75, maxU, interpolatedV);
            instance.addVertexWithUV(n + f2, n2 + d, n3 + 0.75, maxU, interpolatedV);
            instance.addVertexWithUV(n + f3, n2 + d, n3 + 0.75, maxU, interpolatedV2);
            instance.addVertexWithUV(n + f3, n2 + d, n3 + 1, minU, interpolatedV2);
            instance.addVertexWithUV(n + f2, n2 + d, n3 + 1, minU, interpolatedV);
        }

        if (checkIBlockAccess) {
            instance.addVertexWithUV(n, n2 + d, n3 + f3, minU, interpolatedV2);
            instance.addVertexWithUV(n + 0.25, n2 + d, n3 + f3, maxU, interpolatedV2);
            instance.addVertexWithUV(n + 0.25, n2 + d, n3 + f2, maxU, interpolatedV);
            instance.addVertexWithUV(n, n2 + d, n3 + f2, minU, interpolatedV);
            instance.addVertexWithUV(n, n2 + d, n3 + f2, minU, interpolatedV);
            instance.addVertexWithUV(n + 0.25, n2 + d, n3 + f2, maxU, interpolatedV);
            instance.addVertexWithUV(n + 0.25, n2 + d, n3 + f3, maxU, interpolatedV2);
            instance.addVertexWithUV(n, n2 + d, n3 + f3, minU, interpolatedV2);
        }

        if (checkIBlockAccess || checkIBlockAccess2 && !checkIBlockAccess3 && !checkIBlockAccess4) {
            instance.addVertexWithUV(n + 0.25, n2 + d, n3 + f3, minU, interpolatedV2);
            instance.addVertexWithUV(n + 0.5, n2 + d, n3 + f3, maxU, interpolatedV2);
            instance.addVertexWithUV(n + 0.5, n2 + d, n3 + f2, maxU, interpolatedV);
            instance.addVertexWithUV(n + 0.25, n2 + d, n3 + f2, minU, interpolatedV);
            instance.addVertexWithUV(n + 0.25, n2 + d, n3 + f2, minU, interpolatedV);
            instance.addVertexWithUV(n + 0.5, n2 + d, n3 + f2, maxU, interpolatedV);
            instance.addVertexWithUV(n + 0.5, n2 + d, n3 + f3, maxU, interpolatedV2);
            instance.addVertexWithUV(n + 0.25, n2 + d, n3 + f3, minU, interpolatedV2);
        }

        if (checkIBlockAccess2 || checkIBlockAccess && !checkIBlockAccess3 && !checkIBlockAccess4) {
            instance.addVertexWithUV(n + 0.5, n2 + d, n3 + f3, minU, interpolatedV2);
            instance.addVertexWithUV(n + 0.75, n2 + d, n3 + f3, maxU, interpolatedV2);
            instance.addVertexWithUV(n + 0.75, n2 + d, n3 + f2, maxU, interpolatedV);
            instance.addVertexWithUV(n + 0.5, n2 + d, n3 + f2, minU, interpolatedV);
            instance.addVertexWithUV(n + 0.5, n2 + d, n3 + f2, minU, interpolatedV);
            instance.addVertexWithUV(n + 0.75, n2 + d, n3 + f2, maxU, interpolatedV);
            instance.addVertexWithUV(n + 0.75, n2 + d, n3 + f3, maxU, interpolatedV2);
            instance.addVertexWithUV(n + 0.5, n2 + d, n3 + f3, minU, interpolatedV2);
        }

        if (checkIBlockAccess2) {
            instance.addVertexWithUV(n + 0.75, n2 + d, n3 + f3, minU, interpolatedV2);
            instance.addVertexWithUV(n + 1, n2 + d, n3 + f3, maxU, interpolatedV2);
            instance.addVertexWithUV(n + 1, n2 + d, n3 + f2, maxU, interpolatedV);
            instance.addVertexWithUV(n + 0.75, n2 + d, n3 + f2, minU, interpolatedV);
            instance.addVertexWithUV(n + 0.75, n2 + d, n3 + f2, minU, interpolatedV);
            instance.addVertexWithUV(n + 1, n2 + d, n3 + f2, maxU, interpolatedV);
            instance.addVertexWithUV(n + 1, n2 + d, n3 + f3, maxU, interpolatedV2);
            instance.addVertexWithUV(n + 0.75, n2 + d, n3 + f3, minU, interpolatedV2);
        }

        return true;
    }

    public boolean renderBlockFire(BlockFire blockFire, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        IIcon fireIcon = blockFire.getFireIcon(0);
        IIcon fireIcon2 = blockFire.getFireIcon(1);
        IIcon icon = fireIcon;
        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
        }

        instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        instance.setBrightness(blockFire.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        double minU = icon.getMinU();
        double minV = icon.getMinV();
        double maxU = icon.getMaxU();
        double maxV = icon.getMaxV();
        float f = 1.4F;
        if (!World.doesBlockHaveSolidTopSurface(this.blockAccess, n, n2 - 1, n3) && !Blocks.fire.canBlockCatchFire(this.blockAccess, n, n2 - 1, n3)
            )
         {
            float f2 = 0.2F;
            float f3 = 0.0625F;
            if ((n + n2 + n3 & 1) == 1) {
                minU = fireIcon2.getMinU();
                minV = fireIcon2.getMinV();
                maxU = fireIcon2.getMaxU();
                maxV = fireIcon2.getMaxV();
            }

            if ((n / 2 + n2 / 2 + n3 / 2 & 1) == 1) {
                double d = maxU;
                maxU = minU;
                minU = d;
            }

            if (Blocks.fire.canBlockCatchFire(this.blockAccess, n - 1, n2, n3)) {
                instance.addVertexWithUV(n + f2, n2 + f + f3, n3 + 1, maxU, minV);
                instance.addVertexWithUV(n + 0, n2 + 0 + f3, n3 + 1, maxU, maxV);
                instance.addVertexWithUV(n + 0, n2 + 0 + f3, n3 + 0, minU, maxV);
                instance.addVertexWithUV(n + f2, n2 + f + f3, n3 + 0, minU, minV);
                instance.addVertexWithUV(n + f2, n2 + f + f3, n3 + 0, minU, minV);
                instance.addVertexWithUV(n + 0, n2 + 0 + f3, n3 + 0, minU, maxV);
                instance.addVertexWithUV(n + 0, n2 + 0 + f3, n3 + 1, maxU, maxV);
                instance.addVertexWithUV(n + f2, n2 + f + f3, n3 + 1, maxU, minV);
            }

            if (Blocks.fire.canBlockCatchFire(this.blockAccess, n + 1, n2, n3)) {
                instance.addVertexWithUV(n + 1 - f2, n2 + f + f3, n3 + 0, minU, minV);
                instance.addVertexWithUV(n + 1 - 0, n2 + 0 + f3, n3 + 0, minU, maxV);
                instance.addVertexWithUV(n + 1 - 0, n2 + 0 + f3, n3 + 1, maxU, maxV);
                instance.addVertexWithUV(n + 1 - f2, n2 + f + f3, n3 + 1, maxU, minV);
                instance.addVertexWithUV(n + 1 - f2, n2 + f + f3, n3 + 1, maxU, minV);
                instance.addVertexWithUV(n + 1 - 0, n2 + 0 + f3, n3 + 1, maxU, maxV);
                instance.addVertexWithUV(n + 1 - 0, n2 + 0 + f3, n3 + 0, minU, maxV);
                instance.addVertexWithUV(n + 1 - f2, n2 + f + f3, n3 + 0, minU, minV);
            }

            if (Blocks.fire.canBlockCatchFire(this.blockAccess, n, n2, n3 - 1)) {
                instance.addVertexWithUV(n + 0, n2 + f + f3, n3 + f2, maxU, minV);
                instance.addVertexWithUV(n + 0, n2 + 0 + f3, n3 + 0, maxU, maxV);
                instance.addVertexWithUV(n + 1, n2 + 0 + f3, n3 + 0, minU, maxV);
                instance.addVertexWithUV(n + 1, n2 + f + f3, n3 + f2, minU, minV);
                instance.addVertexWithUV(n + 1, n2 + f + f3, n3 + f2, minU, minV);
                instance.addVertexWithUV(n + 1, n2 + 0 + f3, n3 + 0, minU, maxV);
                instance.addVertexWithUV(n + 0, n2 + 0 + f3, n3 + 0, maxU, maxV);
                instance.addVertexWithUV(n + 0, n2 + f + f3, n3 + f2, maxU, minV);
            }

            if (Blocks.fire.canBlockCatchFire(this.blockAccess, n, n2, n3 + 1)) {
                instance.addVertexWithUV(n + 1, n2 + f + f3, n3 + 1 - f2, minU, minV);
                instance.addVertexWithUV(n + 1, n2 + 0 + f3, n3 + 1 - 0, minU, maxV);
                instance.addVertexWithUV(n + 0, n2 + 0 + f3, n3 + 1 - 0, maxU, maxV);
                instance.addVertexWithUV(n + 0, n2 + f + f3, n3 + 1 - f2, maxU, minV);
                instance.addVertexWithUV(n + 0, n2 + f + f3, n3 + 1 - f2, maxU, minV);
                instance.addVertexWithUV(n + 0, n2 + 0 + f3, n3 + 1 - 0, maxU, maxV);
                instance.addVertexWithUV(n + 1, n2 + 0 + f3, n3 + 1 - 0, minU, maxV);
                instance.addVertexWithUV(n + 1, n2 + f + f3, n3 + 1 - f2, minU, minV);
            }

            if (Blocks.fire.canBlockCatchFire(this.blockAccess, n, n2 + 1, n3)) {
                double d = n + 0.5 + 0.5;
                double d2 = n + 0.5 - 0.5;
                double d3 = n3 + 0.5 + 0.5;
                double d4 = n3 + 0.5 - 0.5;
                double d5 = n + 0.5 - 0.5;
                double d6 = n + 0.5 + 0.5;
                double d7 = n3 + 0.5 - 0.5;
                double d8 = n3 + 0.5 + 0.5;
                minU = fireIcon.getMinU();
                minV = fireIcon.getMinV();
                maxU = fireIcon.getMaxU();
                maxV = fireIcon.getMaxV();
                n2++;
                f = -0.2F;
                if ((n + n2 + n3 & 1) == 0) {
                    instance.addVertexWithUV(d5, n2 + f, n3 + 0, maxU, minV);
                    instance.addVertexWithUV(d, n2 + 0, n3 + 0, maxU, maxV);
                    instance.addVertexWithUV(d, n2 + 0, n3 + 1, minU, maxV);
                    instance.addVertexWithUV(d5, n2 + f, n3 + 1, minU, minV);
                    minU = fireIcon2.getMinU();
                    minV = fireIcon2.getMinV();
                    maxU = fireIcon2.getMaxU();
                    maxV = fireIcon2.getMaxV();
                    instance.addVertexWithUV(d6, n2 + f, n3 + 1, maxU, minV);
                    instance.addVertexWithUV(d2, n2 + 0, n3 + 1, maxU, maxV);
                    instance.addVertexWithUV(d2, n2 + 0, n3 + 0, minU, maxV);
                    instance.addVertexWithUV(d6, n2 + f, n3 + 0, minU, minV);
                } else {
                    instance.addVertexWithUV(n + 0, n2 + f, d8, maxU, minV);
                    instance.addVertexWithUV(n + 0, n2 + 0, d4, maxU, maxV);
                    instance.addVertexWithUV(n + 1, n2 + 0, d4, minU, maxV);
                    instance.addVertexWithUV(n + 1, n2 + f, d8, minU, minV);
                    minU = fireIcon2.getMinU();
                    minV = fireIcon2.getMinV();
                    maxU = fireIcon2.getMaxU();
                    maxV = fireIcon2.getMaxV();
                    instance.addVertexWithUV(n + 1, n2 + f, d7, maxU, minV);
                    instance.addVertexWithUV(n + 1, n2 + 0, d3, maxU, maxV);
                    instance.addVertexWithUV(n + 0, n2 + 0, d3, minU, maxV);
                    instance.addVertexWithUV(n + 0, n2 + f, d7, minU, minV);
                }
            }
        } else {
            double d = n + 0.5 + 0.2;
            double d2 = n + 0.5 - 0.2;
            double d3 = n3 + 0.5 + 0.2;
            double d4 = n3 + 0.5 - 0.2;
            double d5 = n + 0.5 - 0.3;
            double d6 = n + 0.5 + 0.3;
            double d7 = n3 + 0.5 - 0.3;
            double d8 = n3 + 0.5 + 0.3;
            instance.addVertexWithUV(d5, n2 + f, n3 + 1, maxU, minV);
            instance.addVertexWithUV(d, n2 + 0, n3 + 1, maxU, maxV);
            instance.addVertexWithUV(d, n2 + 0, n3 + 0, minU, maxV);
            instance.addVertexWithUV(d5, n2 + f, n3 + 0, minU, minV);
            instance.addVertexWithUV(d6, n2 + f, n3 + 0, maxU, minV);
            instance.addVertexWithUV(d2, n2 + 0, n3 + 0, maxU, maxV);
            instance.addVertexWithUV(d2, n2 + 0, n3 + 1, minU, maxV);
            instance.addVertexWithUV(d6, n2 + f, n3 + 1, minU, minV);
            minU = fireIcon2.getMinU();
            minV = fireIcon2.getMinV();
            maxU = fireIcon2.getMaxU();
            maxV = fireIcon2.getMaxV();
            instance.addVertexWithUV(n + 1, n2 + f, d8, maxU, minV);
            instance.addVertexWithUV(n + 1, n2 + 0, d4, maxU, maxV);
            instance.addVertexWithUV(n + 0, n2 + 0, d4, minU, maxV);
            instance.addVertexWithUV(n + 0, n2 + f, d8, minU, minV);
            instance.addVertexWithUV(n + 0, n2 + f, d7, maxU, minV);
            instance.addVertexWithUV(n + 0, n2 + 0, d3, maxU, maxV);
            instance.addVertexWithUV(n + 1, n2 + 0, d3, minU, maxV);
            instance.addVertexWithUV(n + 1, n2 + f, d7, minU, minV);
            d = n + 0.5 - 0.5;
            d2 = n + 0.5 + 0.5;
            d3 = n3 + 0.5 - 0.5;
            d4 = n3 + 0.5 + 0.5;
            d5 = n + 0.5 - 0.4;
            d6 = n + 0.5 + 0.4;
            d7 = n3 + 0.5 - 0.4;
            d8 = n3 + 0.5 + 0.4;
            instance.addVertexWithUV(d5, n2 + f, n3 + 0, minU, minV);
            instance.addVertexWithUV(d, n2 + 0, n3 + 0, minU, maxV);
            instance.addVertexWithUV(d, n2 + 0, n3 + 1, maxU, maxV);
            instance.addVertexWithUV(d5, n2 + f, n3 + 1, maxU, minV);
            instance.addVertexWithUV(d6, n2 + f, n3 + 1, minU, minV);
            instance.addVertexWithUV(d2, n2 + 0, n3 + 1, minU, maxV);
            instance.addVertexWithUV(d2, n2 + 0, n3 + 0, maxU, maxV);
            instance.addVertexWithUV(d6, n2 + f, n3 + 0, maxU, minV);
            minU = fireIcon.getMinU();
            minV = fireIcon.getMinV();
            maxU = fireIcon.getMaxU();
            maxV = fireIcon.getMaxV();
            instance.addVertexWithUV(n + 0, n2 + f, d8, minU, minV);
            instance.addVertexWithUV(n + 0, n2 + 0, d4, minU, maxV);
            instance.addVertexWithUV(n + 1, n2 + 0, d4, maxU, maxV);
            instance.addVertexWithUV(n + 1, n2 + f, d8, maxU, minV);
            instance.addVertexWithUV(n + 1, n2 + f, d7, minU, minV);
            instance.addVertexWithUV(n + 1, n2 + 0, d3, minU, maxV);
            instance.addVertexWithUV(n + 0, n2 + 0, d3, maxU, maxV);
            instance.addVertexWithUV(n + 0, n2 + f, d7, maxU, minV);
        }

        return true;
    }

    public boolean renderBlockRedstoneWire(Block block, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        IIcon redstoneWireIcon = BlockRedstoneWire.getRedstoneWireIcon("cross");
        IIcon redstoneWireIcon2 = BlockRedstoneWire.getRedstoneWireIcon("line");
        IIcon redstoneWireIcon3 = BlockRedstoneWire.getRedstoneWireIcon("cross_overlay");
        IIcon redstoneWireIcon4 = BlockRedstoneWire.getRedstoneWireIcon("line_overlay");
        instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        float f = blockMetadata / 15.0F;
        float f2 = f * 0.6F + 0.4F;
        if (blockMetadata == 0) {
            f2 = 0.3F;
        }

        float f3 = f * f * 0.7F - 0.5F;
        float f4 = f * f * 0.6F - 0.7F;
        if (f3 < 0.0F) {
            f3 = 0.0F;
        }

        if (f4 < 0.0F) {
            f4 = 0.0F;
        }

        int redstoneColor = CustomColorizer.getRedstoneColor(blockMetadata);
        if (redstoneColor != -1) {
            int n4 = redstoneColor >> 16 & 0xFF;
            int n5 = redstoneColor >> 8 & 0xFF;
            int n6 = redstoneColor & 0xFF;
            f2 = n4 / 255.0F;
            f3 = n5 / 255.0F;
            f4 = n6 / 255.0F;
        }

        instance.setColorOpaque_F(f2, f3, f4);
        double d = 0.015625;
        double d2 = 0.015625;
        boolean flag = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, n - 1, n2, n3, 1)
            || !this.blockAccess.getBlock(n - 1, n2, n3).isBlockNormalCube()
                && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, n - 1, n2 - 1, n3, -1);
        boolean flag2 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, n + 1, n2, n3, 3)
            || !this.blockAccess.getBlock(n + 1, n2, n3).isBlockNormalCube()
                && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, n + 1, n2 - 1, n3, -1);
        boolean flag3 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, n, n2, n3 - 1, 2)
            || !this.blockAccess.getBlock(n, n2, n3 - 1).isBlockNormalCube()
                && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, n, n2 - 1, n3 - 1, -1);
        boolean flag4 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, n, n2, n3 + 1, 0)
            || !this.blockAccess.getBlock(n, n2, n3 + 1).isBlockNormalCube()
                && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, n, n2 - 1, n3 + 1, -1);
        if (!this.blockAccess.getBlock(n, n2 + 1, n3).isBlockNormalCube()) {
            if (this.blockAccess.getBlock(n - 1, n2, n3).isBlockNormalCube()
                && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, n - 1, n2 + 1, n3, -1)) {
                flag = true;
            }

            if (this.blockAccess.getBlock(n + 1, n2, n3).isBlockNormalCube()
                && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, n + 1, n2 + 1, n3, -1)) {
                flag2 = true;
            }

            if (this.blockAccess.getBlock(n, n2, n3 - 1).isBlockNormalCube()
                && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, n, n2 + 1, n3 - 1, -1)) {
                flag3 = true;
            }

            if (this.blockAccess.getBlock(n, n2, n3 + 1).isBlockNormalCube()
                && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, n, n2 + 1, n3 + 1, -1)) {
                flag4 = true;
            }
        }

        float f5 = n + 0;
        float f6 = n + 1;
        float f7 = n3 + 0;
        float f8 = n3 + 1;
        int n4 = 0;
        if ((flag || flag2) && !flag3 && !flag4) {
            n4 = 1;
        }

        if ((flag3 || flag4) && !flag2 && !flag) {
            n4 = 2;
        }

        if (n4 == 0) {
            int n5 = 0;
            int n6 = 0;
            int n7 = 16;
            int n8 = 16;
            boolean flag5 = true;
            if (!flag) {
                f5 += 0.3125F;
            }

            if (!flag) {
                n5 += 5;
            }

            if (!flag2) {
                f6 -= 0.3125F;
            }

            if (!flag2) {
                n7 -= 5;
            }

            if (!flag3) {
                f7 += 0.3125F;
            }

            if (!flag3) {
                n6 += 5;
            }

            if (!flag4) {
                f8 -= 0.3125F;
            }

            if (!flag4) {
                n8 -= 5;
            }

            instance.addVertexWithUV(f6, n2 + 0.015625, f8, redstoneWireIcon.getInterpolatedU(n7), redstoneWireIcon.getInterpolatedV(n8));
            instance.addVertexWithUV(f6, n2 + 0.015625, f7, redstoneWireIcon.getInterpolatedU(n7), redstoneWireIcon.getInterpolatedV(n6));
            instance.addVertexWithUV(f5, n2 + 0.015625, f7, redstoneWireIcon.getInterpolatedU(n5), redstoneWireIcon.getInterpolatedV(n6));
            instance.addVertexWithUV(f5, n2 + 0.015625, f8, redstoneWireIcon.getInterpolatedU(n5), redstoneWireIcon.getInterpolatedV(n8));
            instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            instance.addVertexWithUV(f6, n2 + 0.015625, f8, redstoneWireIcon3.getInterpolatedU(n7), redstoneWireIcon3.getInterpolatedV(n8));
            instance.addVertexWithUV(f6, n2 + 0.015625, f7, redstoneWireIcon3.getInterpolatedU(n7), redstoneWireIcon3.getInterpolatedV(n6));
            instance.addVertexWithUV(f5, n2 + 0.015625, f7, redstoneWireIcon3.getInterpolatedU(n5), redstoneWireIcon3.getInterpolatedV(n6));
            instance.addVertexWithUV(f5, n2 + 0.015625, f8, redstoneWireIcon3.getInterpolatedU(n5), redstoneWireIcon3.getInterpolatedV(n8));
        } else if (n4 == 1) {
            instance.addVertexWithUV(f6, n2 + 0.015625, f8, redstoneWireIcon2.getMaxU(), redstoneWireIcon2.getMaxV());
            instance.addVertexWithUV(f6, n2 + 0.015625, f7, redstoneWireIcon2.getMaxU(), redstoneWireIcon2.getMinV());
            instance.addVertexWithUV(f5, n2 + 0.015625, f7, redstoneWireIcon2.getMinU(), redstoneWireIcon2.getMinV());
            instance.addVertexWithUV(f5, n2 + 0.015625, f8, redstoneWireIcon2.getMinU(), redstoneWireIcon2.getMaxV());
            instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            instance.addVertexWithUV(f6, n2 + 0.015625, f8, redstoneWireIcon4.getMaxU(), redstoneWireIcon4.getMaxV());
            instance.addVertexWithUV(f6, n2 + 0.015625, f7, redstoneWireIcon4.getMaxU(), redstoneWireIcon4.getMinV());
            instance.addVertexWithUV(f5, n2 + 0.015625, f7, redstoneWireIcon4.getMinU(), redstoneWireIcon4.getMinV());
            instance.addVertexWithUV(f5, n2 + 0.015625, f8, redstoneWireIcon4.getMinU(), redstoneWireIcon4.getMaxV());
        } else {
            instance.addVertexWithUV(f6, n2 + 0.015625, f8, redstoneWireIcon2.getMaxU(), redstoneWireIcon2.getMaxV());
            instance.addVertexWithUV(f6, n2 + 0.015625, f7, redstoneWireIcon2.getMinU(), redstoneWireIcon2.getMaxV());
            instance.addVertexWithUV(f5, n2 + 0.015625, f7, redstoneWireIcon2.getMinU(), redstoneWireIcon2.getMinV());
            instance.addVertexWithUV(f5, n2 + 0.015625, f8, redstoneWireIcon2.getMaxU(), redstoneWireIcon2.getMinV());
            instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            instance.addVertexWithUV(f6, n2 + 0.015625, f8, redstoneWireIcon4.getMaxU(), redstoneWireIcon4.getMaxV());
            instance.addVertexWithUV(f6, n2 + 0.015625, f7, redstoneWireIcon4.getMinU(), redstoneWireIcon4.getMaxV());
            instance.addVertexWithUV(f5, n2 + 0.015625, f7, redstoneWireIcon4.getMinU(), redstoneWireIcon4.getMinV());
            instance.addVertexWithUV(f5, n2 + 0.015625, f8, redstoneWireIcon4.getMaxU(), redstoneWireIcon4.getMinV());
        }

        if (!this.blockAccess.getBlock(n, n2 + 1, n3).isBlockNormalCube()) {
            float f9 = 0.021875F;
            if (this.blockAccess.getBlock(n - 1, n2, n3).isBlockNormalCube() && this.blockAccess.getBlock(n - 1, n2 + 1, n3) == Blocks.redstone_wire
                )
             {
                instance.setColorOpaque_F(f2, f3, f4);
                instance.addVertexWithUV(n + 0.015625, n2 + 1 + 0.021875F, n3 + 1, redstoneWireIcon2.getMaxU(), redstoneWireIcon2.getMinV());
                instance.addVertexWithUV(n + 0.015625, n2 + 0, n3 + 1, redstoneWireIcon2.getMinU(), redstoneWireIcon2.getMinV());
                instance.addVertexWithUV(n + 0.015625, n2 + 0, n3 + 0, redstoneWireIcon2.getMinU(), redstoneWireIcon2.getMaxV());
                instance.addVertexWithUV(n + 0.015625, n2 + 1 + 0.021875F, n3 + 0, redstoneWireIcon2.getMaxU(), redstoneWireIcon2.getMaxV());
                instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                instance.addVertexWithUV(n + 0.015625, n2 + 1 + 0.021875F, n3 + 1, redstoneWireIcon4.getMaxU(), redstoneWireIcon4.getMinV());
                instance.addVertexWithUV(n + 0.015625, n2 + 0, n3 + 1, redstoneWireIcon4.getMinU(), redstoneWireIcon4.getMinV());
                instance.addVertexWithUV(n + 0.015625, n2 + 0, n3 + 0, redstoneWireIcon4.getMinU(), redstoneWireIcon4.getMaxV());
                instance.addVertexWithUV(n + 0.015625, n2 + 1 + 0.021875F, n3 + 0, redstoneWireIcon4.getMaxU(), redstoneWireIcon4.getMaxV());
            }

            if (this.blockAccess.getBlock(n + 1, n2, n3).isBlockNormalCube() && this.blockAccess.getBlock(n + 1, n2 + 1, n3) == Blocks.redstone_wire
                )
             {
                instance.setColorOpaque_F(f2, f3, f4);
                instance.addVertexWithUV(n + 1 - 0.015625, n2 + 0, n3 + 1, redstoneWireIcon2.getMinU(), redstoneWireIcon2.getMaxV());
                instance.addVertexWithUV(n + 1 - 0.015625, n2 + 1 + 0.021875F, n3 + 1, redstoneWireIcon2.getMaxU(), redstoneWireIcon2.getMaxV());
                instance.addVertexWithUV(n + 1 - 0.015625, n2 + 1 + 0.021875F, n3 + 0, redstoneWireIcon2.getMaxU(), redstoneWireIcon2.getMinV());
                instance.addVertexWithUV(n + 1 - 0.015625, n2 + 0, n3 + 0, redstoneWireIcon2.getMinU(), redstoneWireIcon2.getMinV());
                instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                instance.addVertexWithUV(n + 1 - 0.015625, n2 + 0, n3 + 1, redstoneWireIcon4.getMinU(), redstoneWireIcon4.getMaxV());
                instance.addVertexWithUV(n + 1 - 0.015625, n2 + 1 + 0.021875F, n3 + 1, redstoneWireIcon4.getMaxU(), redstoneWireIcon4.getMaxV());
                instance.addVertexWithUV(n + 1 - 0.015625, n2 + 1 + 0.021875F, n3 + 0, redstoneWireIcon4.getMaxU(), redstoneWireIcon4.getMinV());
                instance.addVertexWithUV(n + 1 - 0.015625, n2 + 0, n3 + 0, redstoneWireIcon4.getMinU(), redstoneWireIcon4.getMinV());
            }

            if (this.blockAccess.getBlock(n, n2, n3 - 1).isBlockNormalCube() && this.blockAccess.getBlock(n, n2 + 1, n3 - 1) == Blocks.redstone_wire
                )
             {
                instance.setColorOpaque_F(f2, f3, f4);
                instance.addVertexWithUV(n + 1, n2 + 0, n3 + 0.015625, redstoneWireIcon2.getMinU(), redstoneWireIcon2.getMaxV());
                instance.addVertexWithUV(n + 1, n2 + 1 + 0.021875F, n3 + 0.015625, redstoneWireIcon2.getMaxU(), redstoneWireIcon2.getMaxV());
                instance.addVertexWithUV(n + 0, n2 + 1 + 0.021875F, n3 + 0.015625, redstoneWireIcon2.getMaxU(), redstoneWireIcon2.getMinV());
                instance.addVertexWithUV(n + 0, n2 + 0, n3 + 0.015625, redstoneWireIcon2.getMinU(), redstoneWireIcon2.getMinV());
                instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                instance.addVertexWithUV(n + 1, n2 + 0, n3 + 0.015625, redstoneWireIcon4.getMinU(), redstoneWireIcon4.getMaxV());
                instance.addVertexWithUV(n + 1, n2 + 1 + 0.021875F, n3 + 0.015625, redstoneWireIcon4.getMaxU(), redstoneWireIcon4.getMaxV());
                instance.addVertexWithUV(n + 0, n2 + 1 + 0.021875F, n3 + 0.015625, redstoneWireIcon4.getMaxU(), redstoneWireIcon4.getMinV());
                instance.addVertexWithUV(n + 0, n2 + 0, n3 + 0.015625, redstoneWireIcon4.getMinU(), redstoneWireIcon4.getMinV());
            }

            if (this.blockAccess.getBlock(n, n2, n3 + 1).isBlockNormalCube() && this.blockAccess.getBlock(n, n2 + 1, n3 + 1) == Blocks.redstone_wire
                )
             {
                instance.setColorOpaque_F(f2, f3, f4);
                instance.addVertexWithUV(n + 1, n2 + 1 + 0.021875F, n3 + 1 - 0.015625, redstoneWireIcon2.getMaxU(), redstoneWireIcon2.getMinV());
                instance.addVertexWithUV(n + 1, n2 + 0, n3 + 1 - 0.015625, redstoneWireIcon2.getMinU(), redstoneWireIcon2.getMinV());
                instance.addVertexWithUV(n + 0, n2 + 0, n3 + 1 - 0.015625, redstoneWireIcon2.getMinU(), redstoneWireIcon2.getMaxV());
                instance.addVertexWithUV(n + 0, n2 + 1 + 0.021875F, n3 + 1 - 0.015625, redstoneWireIcon2.getMaxU(), redstoneWireIcon2.getMaxV());
                instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
                instance.addVertexWithUV(n + 1, n2 + 1 + 0.021875F, n3 + 1 - 0.015625, redstoneWireIcon4.getMaxU(), redstoneWireIcon4.getMinV());
                instance.addVertexWithUV(n + 1, n2 + 0, n3 + 1 - 0.015625, redstoneWireIcon4.getMinU(), redstoneWireIcon4.getMinV());
                instance.addVertexWithUV(n + 0, n2 + 0, n3 + 1 - 0.015625, redstoneWireIcon4.getMinU(), redstoneWireIcon4.getMaxV());
                instance.addVertexWithUV(n + 0, n2 + 1 + 0.021875F, n3 + 1 - 0.015625, redstoneWireIcon4.getMaxU(), redstoneWireIcon4.getMaxV());
            }
        }

        if (Config.isBetterSnow() && this.hasSnowNeighbours(n, n2, n3)) {
            this.renderSnow(n, n2, n3, 0.01);
        }

        return true;
    }

    public boolean renderBlockMinecartTrack(BlockRailBase blockRailBase, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        IIcon blockIconFromSideAndMetadata = this.getBlockIconFromSideAndMetadata(blockRailBase, 0, blockMetadata);
        if (this.hasOverrideBlockTexture()) {
            blockIconFromSideAndMetadata = this.overrideBlockTexture;
        }

        if (Config.isConnectedTextures() && this.overrideBlockTexture == null) {
            blockIconFromSideAndMetadata = ConnectedTextures.getConnectedTexture(this.blockAccess, blockRailBase, n, n2, n3, 1, blockIconFromSideAndMetadata);
        }

        if (blockRailBase.isPowered()) {
            blockMetadata &= 7;
        }

        instance.setBrightness(blockRailBase.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        double minU = blockIconFromSideAndMetadata.getMinU();
        double minV = blockIconFromSideAndMetadata.getMinV();
        double maxU = blockIconFromSideAndMetadata.getMaxU();
        double maxV = blockIconFromSideAndMetadata.getMaxV();
        double d = 0.0625;
        double d2 = n + 1;
        double d3 = n + 1;
        double d4 = n + 0;
        double d5 = n + 0;
        double d6 = n3 + 0;
        double d7 = n3 + 1;
        double d8 = n3 + 1;
        double d9 = n3 + 0;
        double d10 = n2 + d;
        double d11 = n2 + d;
        double d12 = n2 + d;
        double d13 = n2 + d;
        if (blockMetadata == 1 || blockMetadata == 2 || blockMetadata == 3 || blockMetadata == 7) {
            d2 = d5 = n + 1;
            d3 = d4 = n + 0;
            d6 = d7 = n3 + 1;
            d8 = d9 = n3 + 0;
        } else if (blockMetadata == 8) {
            d2 = d3 = n + 0;
            d4 = d5 = n + 1;
            d6 = d9 = n3 + 1;
            d7 = d8 = n3 + 0;
        } else if (blockMetadata == 9) {
            d2 = d5 = n + 0;
            d3 = d4 = n + 1;
            d6 = d7 = n3 + 0;
            d8 = d9 = n3 + 1;
        }

        if (blockMetadata == 2 || blockMetadata == 4) {
            d10++;
            d13++;
        } else if (blockMetadata == 3 || blockMetadata == 5) {
            d11++;
            d12++;
        }

        instance.addVertexWithUV(d2, d10, d6, maxU, minV);
        instance.addVertexWithUV(d3, d11, d7, maxU, maxV);
        instance.addVertexWithUV(d4, d12, d8, minU, maxV);
        instance.addVertexWithUV(d5, d13, d9, minU, minV);
        instance.addVertexWithUV(d5, d13, d9, minU, minV);
        instance.addVertexWithUV(d4, d12, d8, minU, maxV);
        instance.addVertexWithUV(d3, d11, d7, maxU, maxV);
        instance.addVertexWithUV(d2, d10, d6, maxU, minV);
        if (Config.isBetterSnow() && this.hasSnowNeighbours(n, n2, n3)) {
            this.renderSnow(n, n2, n3, 0.05);
        }

        return true;
    }

    public boolean renderBlockLadder(Block block, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        IIcon blockIconFromSide = this.getBlockIconFromSide(block, 0);
        if (this.hasOverrideBlockTexture()) {
            blockIconFromSide = this.overrideBlockTexture;
        }

        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        if (Config.isConnectedTextures() && this.overrideBlockTexture == null) {
            blockIconFromSide = ConnectedTextures.getConnectedTexture(this.blockAccess, block, n, n2, n3, blockMetadata, blockIconFromSide);
        }

        instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        double minU = blockIconFromSide.getMinU();
        double minV = blockIconFromSide.getMinV();
        double maxU = blockIconFromSide.getMaxU();
        double maxV = blockIconFromSide.getMaxV();
        double d = 0.0;
        double d2 = 0.05F;
        if (blockMetadata == 5) {
            instance.addVertexWithUV(n + d2, n2 + 1 + d, n3 + 1 + d, minU, minV);
            instance.addVertexWithUV(n + d2, n2 + 0 - d, n3 + 1 + d, minU, maxV);
            instance.addVertexWithUV(n + d2, n2 + 0 - d, n3 + 0 - d, maxU, maxV);
            instance.addVertexWithUV(n + d2, n2 + 1 + d, n3 + 0 - d, maxU, minV);
        }

        if (blockMetadata == 4) {
            instance.addVertexWithUV(n + 1 - d2, n2 + 0 - d, n3 + 1 + d, maxU, maxV);
            instance.addVertexWithUV(n + 1 - d2, n2 + 1 + d, n3 + 1 + d, maxU, minV);
            instance.addVertexWithUV(n + 1 - d2, n2 + 1 + d, n3 + 0 - d, minU, minV);
            instance.addVertexWithUV(n + 1 - d2, n2 + 0 - d, n3 + 0 - d, minU, maxV);
        }

        if (blockMetadata == 3) {
            instance.addVertexWithUV(n + 1 + d, n2 + 0 - d, n3 + d2, maxU, maxV);
            instance.addVertexWithUV(n + 1 + d, n2 + 1 + d, n3 + d2, maxU, minV);
            instance.addVertexWithUV(n + 0 - d, n2 + 1 + d, n3 + d2, minU, minV);
            instance.addVertexWithUV(n + 0 - d, n2 + 0 - d, n3 + d2, minU, maxV);
        }

        if (blockMetadata == 2) {
            instance.addVertexWithUV(n + 1 + d, n2 + 1 + d, n3 + 1 - d2, minU, minV);
            instance.addVertexWithUV(n + 1 + d, n2 + 0 - d, n3 + 1 - d2, minU, maxV);
            instance.addVertexWithUV(n + 0 - d, n2 + 0 - d, n3 + 1 - d2, maxU, maxV);
            instance.addVertexWithUV(n + 0 - d, n2 + 1 + d, n3 + 1 - d2, maxU, minV);
        }

        return true;
    }

    public boolean renderBlockVine(Block block, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        IIcon blockIconFromSide = this.getBlockIconFromSide(block, 0);
        if (this.hasOverrideBlockTexture()) {
            blockIconFromSide = this.overrideBlockTexture;
        }

        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        if (Config.isConnectedTextures() && this.overrideBlockTexture == null) {
            byte b = 0;
            if ((blockMetadata & 1) != 0) {
                b = 2;
            } else if ((blockMetadata & 2) != 0) {
                b = 5;
            } else if ((blockMetadata & 4) != 0) {
                b = 3;
            } else if ((blockMetadata & 8) != 0) {
                b = 4;
            }

            blockIconFromSide = ConnectedTextures.getConnectedTexture(this.blockAccess, block, n, n2, n3, b, blockIconFromSide);
        }

        instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        int colorMultiplier = CustomColorizer.getColorMultiplier(block, this.blockAccess, n, n2, n3);
        float f = (colorMultiplier >> 16 & 0xFF) / 255.0F;
        float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
        float f3 = (colorMultiplier & 0xFF) / 255.0F;
        instance.setColorOpaque_F(f, f2, f3);
        double minU = blockIconFromSide.getMinU();
        double minV = blockIconFromSide.getMinV();
        double maxU = blockIconFromSide.getMaxU();
        double maxV = blockIconFromSide.getMaxV();
        double d = 0.05F;
        if ((blockMetadata & 2) != 0) {
            instance.addVertexWithUV(n + d, n2 + 1, n3 + 1, minU, minV);
            instance.addVertexWithUV(n + d, n2 + 0, n3 + 1, minU, maxV);
            instance.addVertexWithUV(n + d, n2 + 0, n3 + 0, maxU, maxV);
            instance.addVertexWithUV(n + d, n2 + 1, n3 + 0, maxU, minV);
            instance.addVertexWithUV(n + d, n2 + 1, n3 + 0, maxU, minV);
            instance.addVertexWithUV(n + d, n2 + 0, n3 + 0, maxU, maxV);
            instance.addVertexWithUV(n + d, n2 + 0, n3 + 1, minU, maxV);
            instance.addVertexWithUV(n + d, n2 + 1, n3 + 1, minU, minV);
        }

        if ((blockMetadata & 8) != 0) {
            instance.addVertexWithUV(n + 1 - d, n2 + 0, n3 + 1, maxU, maxV);
            instance.addVertexWithUV(n + 1 - d, n2 + 1, n3 + 1, maxU, minV);
            instance.addVertexWithUV(n + 1 - d, n2 + 1, n3 + 0, minU, minV);
            instance.addVertexWithUV(n + 1 - d, n2 + 0, n3 + 0, minU, maxV);
            instance.addVertexWithUV(n + 1 - d, n2 + 0, n3 + 0, minU, maxV);
            instance.addVertexWithUV(n + 1 - d, n2 + 1, n3 + 0, minU, minV);
            instance.addVertexWithUV(n + 1 - d, n2 + 1, n3 + 1, maxU, minV);
            instance.addVertexWithUV(n + 1 - d, n2 + 0, n3 + 1, maxU, maxV);
        }

        if ((blockMetadata & 4) != 0) {
            instance.addVertexWithUV(n + 1, n2 + 0, n3 + d, maxU, maxV);
            instance.addVertexWithUV(n + 1, n2 + 1, n3 + d, maxU, minV);
            instance.addVertexWithUV(n + 0, n2 + 1, n3 + d, minU, minV);
            instance.addVertexWithUV(n + 0, n2 + 0, n3 + d, minU, maxV);
            instance.addVertexWithUV(n + 0, n2 + 0, n3 + d, minU, maxV);
            instance.addVertexWithUV(n + 0, n2 + 1, n3 + d, minU, minV);
            instance.addVertexWithUV(n + 1, n2 + 1, n3 + d, maxU, minV);
            instance.addVertexWithUV(n + 1, n2 + 0, n3 + d, maxU, maxV);
        }

        if ((blockMetadata & 1) != 0) {
            instance.addVertexWithUV(n + 1, n2 + 1, n3 + 1 - d, minU, minV);
            instance.addVertexWithUV(n + 1, n2 + 0, n3 + 1 - d, minU, maxV);
            instance.addVertexWithUV(n + 0, n2 + 0, n3 + 1 - d, maxU, maxV);
            instance.addVertexWithUV(n + 0, n2 + 1, n3 + 1 - d, maxU, minV);
            instance.addVertexWithUV(n + 0, n2 + 1, n3 + 1 - d, maxU, minV);
            instance.addVertexWithUV(n + 0, n2 + 0, n3 + 1 - d, maxU, maxV);
            instance.addVertexWithUV(n + 1, n2 + 0, n3 + 1 - d, minU, maxV);
            instance.addVertexWithUV(n + 1, n2 + 1, n3 + 1 - d, minU, minV);
        }

        if (this.blockAccess.getBlock(n, n2 + 1, n3).isBlockNormalCube()) {
            instance.addVertexWithUV(n + 1, n2 + 1 - d, n3 + 0, minU, minV);
            instance.addVertexWithUV(n + 1, n2 + 1 - d, n3 + 1, minU, maxV);
            instance.addVertexWithUV(n + 0, n2 + 1 - d, n3 + 1, maxU, maxV);
            instance.addVertexWithUV(n + 0, n2 + 1 - d, n3 + 0, maxU, minV);
        }

        return true;
    }

    public boolean renderBlockStainedGlassPane(Block block, int n, int n2, int n3) {
        int height = this.blockAccess.getHeight();
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        int colorMultiplier = block.colorMultiplier(this.blockAccess, n, n2, n3);
        float f = (colorMultiplier >> 16 & 0xFF) / 255.0F;
        float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
        float f3 = (colorMultiplier & 0xFF) / 255.0F;
        if (EntityRenderer.anaglyphEnable) {
            float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
            f = f4;
            f2 = f5;
            f3 = f6;
        }

        instance.setColorOpaque_F(f, f2, f3);
        boolean flag = block instanceof BlockStainedGlassPane;
        int n4 = 0;
        IIcon icon;
        IIcon icon2;
        if (this.hasOverrideBlockTexture()) {
            icon2 = this.overrideBlockTexture;
            icon = this.overrideBlockTexture;
        } else {
            n4 = this.blockAccess.getBlockMetadata(n, n2, n3);
            icon2 = this.getBlockIconFromSideAndMetadata(block, 0, n4);
            icon = flag ? ((BlockStainedGlassPane)block).findIIcon(n4) : ((BlockPane)block).getIIcon();
        }

        IIcon icon3 = icon2;
        boolean flag2 = true;
        boolean flag3 = true;
        if (Config.isConnectedTextures() && this.overrideBlockTexture == null) {
            IIcon connectedTexture = ConnectedTextures.getConnectedTexture(this.blockAccess, block, n, n2, n3, 4, icon2);
            IIcon connectedTexture2 = ConnectedTextures.getConnectedTexture(this.blockAccess, block, n, n2, n3, 3, icon2);
            if (connectedTexture != icon2 || connectedTexture2 != icon2) {
                BlockPane blockPane = (BlockPane)block;
                flag2 = this.blockAccess.getBlock(n, n2 + 1, n3) != block || this.blockAccess.getBlockMetadata(n, n2 + 1, n3) != n4;
                flag3 = this.blockAccess.getBlock(n, n2 - 1, n3) != block || this.blockAccess.getBlockMetadata(n, n2 - 1, n3) != n4;
            }

            icon2 = connectedTexture;
            icon3 = connectedTexture2;
        }

        double minU = icon2.getMinU();
        double interpolatedU = icon2.getInterpolatedU(7.0);
        double interpolatedU2 = icon2.getInterpolatedU(9.0);
        double maxU = icon2.getMaxU();
        double minV = icon2.getMinV();
        double maxV = icon2.getMaxV();
        double minU2 = icon3.getMinU();
        double interpolatedU3 = icon3.getInterpolatedU(7.0);
        double interpolatedU4 = icon3.getInterpolatedU(9.0);
        double maxU2 = icon3.getMaxU();
        double minV2 = icon3.getMinV();
        double maxV2 = icon3.getMaxV();
        double interpolatedU5 = icon.getInterpolatedU(7.0);
        double interpolatedU6 = icon.getInterpolatedU(9.0);
        double minV3 = icon.getMinV();
        double maxV3 = icon.getMaxV();
        double interpolatedV = icon.getInterpolatedV(7.0);
        double interpolatedV2 = icon.getInterpolatedV(9.0);
        double d = n;
        double d2 = n + 1;
        double d3 = n3;
        double d4 = n3 + 1;
        double d5 = n + 0.5 - 0.0625;
        double d6 = n + 0.5 + 0.0625;
        double d7 = n3 + 0.5 - 0.0625;
        double d8 = n3 + 0.5 + 0.0625;
        boolean flag4 = flag
            ? ((BlockStainedGlassPane)block).canPaneConnectToBlock(this.blockAccess.getBlock(n, n2, n3 - 1))
            : ((BlockPane)block).canPaneConnectToBlock(this.blockAccess.getBlock(n, n2, n3 - 1));
        boolean flag5 = flag
            ? ((BlockStainedGlassPane)block).canPaneConnectToBlock(this.blockAccess.getBlock(n, n2, n3 + 1))
            : ((BlockPane)block).canPaneConnectToBlock(this.blockAccess.getBlock(n, n2, n3 + 1));
        boolean flag6 = flag
            ? ((BlockStainedGlassPane)block).canPaneConnectToBlock(this.blockAccess.getBlock(n - 1, n2, n3))
            : ((BlockPane)block).canPaneConnectToBlock(this.blockAccess.getBlock(n - 1, n2, n3));
        boolean flag7 = flag
            ? ((BlockStainedGlassPane)block).canPaneConnectToBlock(this.blockAccess.getBlock(n + 1, n2, n3))
            : ((BlockPane)block).canPaneConnectToBlock(this.blockAccess.getBlock(n + 1, n2, n3));
        double d9 = 0.001;
        double d10 = 0.999;
        double d11 = 0.001;
        boolean flag8 = !flag4 && !flag5 && !flag6 && !flag7;
        double d12 = n2 + 0.999;
        double d13 = n2 + 0.001;
        if (!flag2) {
            d12 = n2 + 1;
        }

        if (!flag3) {
            d13 = n2;
        }

        if (!flag6 && !flag8) {
            if (!flag4 && !flag5) {
                instance.addVertexWithUV(d5, d12, d7, interpolatedU, minV);
                instance.addVertexWithUV(d5, d13, d7, interpolatedU, maxV);
                instance.addVertexWithUV(d5, d13, d8, interpolatedU2, maxV);
                instance.addVertexWithUV(d5, d12, d8, interpolatedU2, minV);
            }
        } else if (flag6 && flag7) {
            if (!flag4) {
                instance.addVertexWithUV(d2, d12, d7, maxU2, minV2);
                instance.addVertexWithUV(d2, d13, d7, maxU2, maxV2);
                instance.addVertexWithUV(d, d13, d7, minU2, maxV2);
                instance.addVertexWithUV(d, d12, d7, minU2, minV2);
            } else {
                instance.addVertexWithUV(d5, d12, d7, interpolatedU3, minV2);
                instance.addVertexWithUV(d5, d13, d7, interpolatedU3, maxV2);
                instance.addVertexWithUV(d, d13, d7, minU2, maxV2);
                instance.addVertexWithUV(d, d12, d7, minU2, minV2);
                instance.addVertexWithUV(d2, d12, d7, maxU2, minV2);
                instance.addVertexWithUV(d2, d13, d7, maxU2, maxV2);
                instance.addVertexWithUV(d6, d13, d7, interpolatedU4, maxV2);
                instance.addVertexWithUV(d6, d12, d7, interpolatedU4, minV2);
            }

            if (!flag5) {
                instance.addVertexWithUV(d, d12, d8, minU2, minV2);
                instance.addVertexWithUV(d, d13, d8, minU2, maxV2);
                instance.addVertexWithUV(d2, d13, d8, maxU2, maxV2);
                instance.addVertexWithUV(d2, d12, d8, maxU2, minV2);
            } else {
                instance.addVertexWithUV(d, d12, d8, minU2, minV2);
                instance.addVertexWithUV(d, d13, d8, minU2, maxV2);
                instance.addVertexWithUV(d5, d13, d8, interpolatedU3, maxV2);
                instance.addVertexWithUV(d5, d12, d8, interpolatedU3, minV2);
                instance.addVertexWithUV(d6, d12, d8, interpolatedU4, minV2);
                instance.addVertexWithUV(d6, d13, d8, interpolatedU4, maxV2);
                instance.addVertexWithUV(d2, d13, d8, maxU2, maxV2);
                instance.addVertexWithUV(d2, d12, d8, maxU2, minV2);
            }

            if (flag2) {
                instance.addVertexWithUV(d, d12, d8, interpolatedU6, minV3);
                instance.addVertexWithUV(d2, d12, d8, interpolatedU6, maxV3);
                instance.addVertexWithUV(d2, d12, d7, interpolatedU5, maxV3);
                instance.addVertexWithUV(d, d12, d7, interpolatedU5, minV3);
            }

            if (flag3) {
                instance.addVertexWithUV(d2, d13, d8, interpolatedU5, maxV3);
                instance.addVertexWithUV(d, d13, d8, interpolatedU5, minV3);
                instance.addVertexWithUV(d, d13, d7, interpolatedU6, minV3);
                instance.addVertexWithUV(d2, d13, d7, interpolatedU6, maxV3);
            }
        } else {
            if (!flag4 && !flag8) {
                instance.addVertexWithUV(d6, d12, d7, interpolatedU4, minV2);
                instance.addVertexWithUV(d6, d13, d7, interpolatedU4, maxV2);
                instance.addVertexWithUV(d, d13, d7, minU2, maxV2);
                instance.addVertexWithUV(d, d12, d7, minU2, minV2);
            } else {
                instance.addVertexWithUV(d5, d12, d7, interpolatedU3, minV2);
                instance.addVertexWithUV(d5, d13, d7, interpolatedU3, maxV2);
                instance.addVertexWithUV(d, d13, d7, minU2, maxV2);
                instance.addVertexWithUV(d, d12, d7, minU2, minV2);
            }

            if (!flag5 && !flag8) {
                instance.addVertexWithUV(d, d12, d8, minU2, minV2);
                instance.addVertexWithUV(d, d13, d8, minU2, maxV2);
                instance.addVertexWithUV(d6, d13, d8, interpolatedU4, maxV2);
                instance.addVertexWithUV(d6, d12, d8, interpolatedU4, minV2);
            } else {
                instance.addVertexWithUV(d, d12, d8, minU2, minV2);
                instance.addVertexWithUV(d, d13, d8, minU2, maxV2);
                instance.addVertexWithUV(d5, d13, d8, interpolatedU3, maxV2);
                instance.addVertexWithUV(d5, d12, d8, interpolatedU3, minV2);
            }

            if (flag2) {
                instance.addVertexWithUV(d, d12, d8, interpolatedU6, minV3);
                instance.addVertexWithUV(d5, d12, d8, interpolatedU6, interpolatedV);
                instance.addVertexWithUV(d5, d12, d7, interpolatedU5, interpolatedV);
                instance.addVertexWithUV(d, d12, d7, interpolatedU5, minV3);
            }

            if (flag3) {
                instance.addVertexWithUV(d5, d13, d8, interpolatedU5, interpolatedV);
                instance.addVertexWithUV(d, d13, d8, interpolatedU5, minV3);
                instance.addVertexWithUV(d, d13, d7, interpolatedU6, minV3);
                instance.addVertexWithUV(d5, d13, d7, interpolatedU6, interpolatedV);
            }
        }

        if ((flag7 || flag8) && !flag6) {
            if (!flag5 && !flag8) {
                instance.addVertexWithUV(d5, d12, d8, interpolatedU3, minV2);
                instance.addVertexWithUV(d5, d13, d8, interpolatedU3, maxV2);
                instance.addVertexWithUV(d2, d13, d8, maxU2, maxV2);
                instance.addVertexWithUV(d2, d12, d8, maxU2, minV2);
            } else {
                instance.addVertexWithUV(d6, d12, d8, interpolatedU4, minV2);
                instance.addVertexWithUV(d6, d13, d8, interpolatedU4, maxV2);
                instance.addVertexWithUV(d2, d13, d8, maxU2, maxV2);
                instance.addVertexWithUV(d2, d12, d8, maxU2, minV2);
            }

            if (!flag4 && !flag8) {
                instance.addVertexWithUV(d2, d12, d7, maxU2, minV2);
                instance.addVertexWithUV(d2, d13, d7, maxU2, maxV2);
                instance.addVertexWithUV(d5, d13, d7, interpolatedU3, maxV2);
                instance.addVertexWithUV(d5, d12, d7, interpolatedU3, minV2);
            } else {
                instance.addVertexWithUV(d2, d12, d7, maxU2, minV2);
                instance.addVertexWithUV(d2, d13, d7, maxU2, maxV2);
                instance.addVertexWithUV(d6, d13, d7, interpolatedU4, maxV2);
                instance.addVertexWithUV(d6, d12, d7, interpolatedU4, minV2);
            }

            if (flag2) {
                instance.addVertexWithUV(d6, d12, d8, interpolatedU6, interpolatedV2);
                instance.addVertexWithUV(d2, d12, d8, interpolatedU6, minV3);
                instance.addVertexWithUV(d2, d12, d7, interpolatedU5, minV3);
                instance.addVertexWithUV(d6, d12, d7, interpolatedU5, interpolatedV2);
            }

            if (flag3) {
                instance.addVertexWithUV(d2, d13, d8, interpolatedU5, maxV3);
                instance.addVertexWithUV(d6, d13, d8, interpolatedU5, interpolatedV2);
                instance.addVertexWithUV(d6, d13, d7, interpolatedU6, interpolatedV2);
                instance.addVertexWithUV(d2, d13, d7, interpolatedU6, maxV3);
            }
        } else if (!flag7 && !flag4 && !flag5) {
            instance.addVertexWithUV(d6, d12, d8, interpolatedU, minV);
            instance.addVertexWithUV(d6, d13, d8, interpolatedU, maxV);
            instance.addVertexWithUV(d6, d13, d7, interpolatedU2, maxV);
            instance.addVertexWithUV(d6, d12, d7, interpolatedU2, minV);
        }

        if (!flag4 && !flag8) {
            if (!flag7 && !flag6) {
                instance.addVertexWithUV(d6, d12, d7, interpolatedU4, minV2);
                instance.addVertexWithUV(d6, d13, d7, interpolatedU4, maxV2);
                instance.addVertexWithUV(d5, d13, d7, interpolatedU3, maxV2);
                instance.addVertexWithUV(d5, d12, d7, interpolatedU3, minV2);
            }
        } else if (flag4 && flag5) {
            if (!flag6) {
                instance.addVertexWithUV(d5, d12, d3, minU, minV);
                instance.addVertexWithUV(d5, d13, d3, minU, maxV);
                instance.addVertexWithUV(d5, d13, d4, maxU, maxV);
                instance.addVertexWithUV(d5, d12, d4, maxU, minV);
            } else {
                instance.addVertexWithUV(d5, d12, d3, minU, minV);
                instance.addVertexWithUV(d5, d13, d3, minU, maxV);
                instance.addVertexWithUV(d5, d13, d7, interpolatedU, maxV);
                instance.addVertexWithUV(d5, d12, d7, interpolatedU, minV);
                instance.addVertexWithUV(d5, d12, d8, interpolatedU2, minV);
                instance.addVertexWithUV(d5, d13, d8, interpolatedU2, maxV);
                instance.addVertexWithUV(d5, d13, d4, maxU, maxV);
                instance.addVertexWithUV(d5, d12, d4, maxU, minV);
            }

            if (!flag7) {
                instance.addVertexWithUV(d6, d12, d4, maxU, minV);
                instance.addVertexWithUV(d6, d13, d4, maxU, maxV);
                instance.addVertexWithUV(d6, d13, d3, minU, maxV);
                instance.addVertexWithUV(d6, d12, d3, minU, minV);
            } else {
                instance.addVertexWithUV(d6, d12, d7, interpolatedU, minV);
                instance.addVertexWithUV(d6, d13, d7, interpolatedU, maxV);
                instance.addVertexWithUV(d6, d13, d3, minU, maxV);
                instance.addVertexWithUV(d6, d12, d3, minU, minV);
                instance.addVertexWithUV(d6, d12, d4, maxU, minV);
                instance.addVertexWithUV(d6, d13, d4, maxU, maxV);
                instance.addVertexWithUV(d6, d13, d8, interpolatedU2, maxV);
                instance.addVertexWithUV(d6, d12, d8, interpolatedU2, minV);
            }

            if (flag2) {
                instance.addVertexWithUV(d6, d12, d3, interpolatedU6, minV3);
                instance.addVertexWithUV(d5, d12, d3, interpolatedU5, minV3);
                instance.addVertexWithUV(d5, d12, d4, interpolatedU5, maxV3);
                instance.addVertexWithUV(d6, d12, d4, interpolatedU6, maxV3);
            }

            if (flag3) {
                instance.addVertexWithUV(d5, d13, d3, interpolatedU5, minV3);
                instance.addVertexWithUV(d6, d13, d3, interpolatedU6, minV3);
                instance.addVertexWithUV(d6, d13, d4, interpolatedU6, maxV3);
                instance.addVertexWithUV(d5, d13, d4, interpolatedU5, maxV3);
            }
        } else {
            if (!flag6 && !flag8) {
                instance.addVertexWithUV(d5, d12, d3, minU, minV);
                instance.addVertexWithUV(d5, d13, d3, minU, maxV);
                instance.addVertexWithUV(d5, d13, d8, interpolatedU2, maxV);
                instance.addVertexWithUV(d5, d12, d8, interpolatedU2, minV);
            } else {
                instance.addVertexWithUV(d5, d12, d3, minU, minV);
                instance.addVertexWithUV(d5, d13, d3, minU, maxV);
                instance.addVertexWithUV(d5, d13, d7, interpolatedU, maxV);
                instance.addVertexWithUV(d5, d12, d7, interpolatedU, minV);
            }

            if (!flag7 && !flag8) {
                instance.addVertexWithUV(d6, d12, d8, interpolatedU2, minV);
                instance.addVertexWithUV(d6, d13, d8, interpolatedU2, maxV);
                instance.addVertexWithUV(d6, d13, d3, minU, maxV);
                instance.addVertexWithUV(d6, d12, d3, minU, minV);
            } else {
                instance.addVertexWithUV(d6, d12, d7, interpolatedU, minV);
                instance.addVertexWithUV(d6, d13, d7, interpolatedU, maxV);
                instance.addVertexWithUV(d6, d13, d3, minU, maxV);
                instance.addVertexWithUV(d6, d12, d3, minU, minV);
            }

            if (flag2) {
                instance.addVertexWithUV(d6, d12, d3, interpolatedU6, minV3);
                instance.addVertexWithUV(d5, d12, d3, interpolatedU5, minV3);
                instance.addVertexWithUV(d5, d12, d7, interpolatedU5, interpolatedV);
                instance.addVertexWithUV(d6, d12, d7, interpolatedU6, interpolatedV);
            }

            if (flag3) {
                instance.addVertexWithUV(d5, d13, d3, interpolatedU5, minV3);
                instance.addVertexWithUV(d6, d13, d3, interpolatedU6, minV3);
                instance.addVertexWithUV(d6, d13, d7, interpolatedU6, interpolatedV);
                instance.addVertexWithUV(d5, d13, d7, interpolatedU5, interpolatedV);
            }
        }

        if ((flag5 || flag8) && !flag4) {
            if (!flag6 && !flag8) {
                instance.addVertexWithUV(d5, d12, d7, interpolatedU, minV);
                instance.addVertexWithUV(d5, d13, d7, interpolatedU, maxV);
                instance.addVertexWithUV(d5, d13, d4, maxU, maxV);
                instance.addVertexWithUV(d5, d12, d4, maxU, minV);
            } else {
                instance.addVertexWithUV(d5, d12, d8, interpolatedU2, minV);
                instance.addVertexWithUV(d5, d13, d8, interpolatedU2, maxV);
                instance.addVertexWithUV(d5, d13, d4, maxU, maxV);
                instance.addVertexWithUV(d5, d12, d4, maxU, minV);
            }

            if (!flag7 && !flag8) {
                instance.addVertexWithUV(d6, d12, d4, maxU, minV);
                instance.addVertexWithUV(d6, d13, d4, maxU, maxV);
                instance.addVertexWithUV(d6, d13, d7, interpolatedU, maxV);
                instance.addVertexWithUV(d6, d12, d7, interpolatedU, minV);
            } else {
                instance.addVertexWithUV(d6, d12, d4, maxU, minV);
                instance.addVertexWithUV(d6, d13, d4, maxU, maxV);
                instance.addVertexWithUV(d6, d13, d8, interpolatedU2, maxV);
                instance.addVertexWithUV(d6, d12, d8, interpolatedU2, minV);
            }

            if (flag2) {
                instance.addVertexWithUV(d6, d12, d8, interpolatedU6, interpolatedV2);
                instance.addVertexWithUV(d5, d12, d8, interpolatedU5, interpolatedV2);
                instance.addVertexWithUV(d5, d12, d4, interpolatedU5, maxV3);
                instance.addVertexWithUV(d6, d12, d4, interpolatedU6, maxV3);
            }

            if (flag3) {
                instance.addVertexWithUV(d5, d13, d8, interpolatedU5, interpolatedV2);
                instance.addVertexWithUV(d6, d13, d8, interpolatedU6, interpolatedV2);
                instance.addVertexWithUV(d6, d13, d4, interpolatedU6, maxV3);
                instance.addVertexWithUV(d5, d13, d4, interpolatedU5, maxV3);
            }
        } else if (!flag5 && !flag7 && !flag6) {
            instance.addVertexWithUV(d5, d12, d8, interpolatedU3, minV2);
            instance.addVertexWithUV(d5, d13, d8, interpolatedU3, maxV2);
            instance.addVertexWithUV(d6, d13, d8, interpolatedU4, maxV2);
            instance.addVertexWithUV(d6, d12, d8, interpolatedU4, minV2);
        }

        if (flag2) {
            instance.addVertexWithUV(d6, d12, d7, interpolatedU6, interpolatedV);
            instance.addVertexWithUV(d5, d12, d7, interpolatedU5, interpolatedV);
            instance.addVertexWithUV(d5, d12, d8, interpolatedU5, interpolatedV2);
            instance.addVertexWithUV(d6, d12, d8, interpolatedU6, interpolatedV2);
        }

        if (flag3) {
            instance.addVertexWithUV(d5, d13, d7, interpolatedU5, interpolatedV);
            instance.addVertexWithUV(d6, d13, d7, interpolatedU6, interpolatedV);
            instance.addVertexWithUV(d6, d13, d8, interpolatedU6, interpolatedV2);
            instance.addVertexWithUV(d5, d13, d8, interpolatedU5, interpolatedV2);
        }

        if (flag8) {
            instance.addVertexWithUV(d, d12, d7, interpolatedU, minV);
            instance.addVertexWithUV(d, d13, d7, interpolatedU, maxV);
            instance.addVertexWithUV(d, d13, d8, interpolatedU2, maxV);
            instance.addVertexWithUV(d, d12, d8, interpolatedU2, minV);
            instance.addVertexWithUV(d2, d12, d8, interpolatedU, minV);
            instance.addVertexWithUV(d2, d13, d8, interpolatedU, maxV);
            instance.addVertexWithUV(d2, d13, d7, interpolatedU2, maxV);
            instance.addVertexWithUV(d2, d12, d7, interpolatedU2, minV);
            instance.addVertexWithUV(d6, d12, d3, interpolatedU4, minV2);
            instance.addVertexWithUV(d6, d13, d3, interpolatedU4, maxV2);
            instance.addVertexWithUV(d5, d13, d3, interpolatedU3, maxV2);
            instance.addVertexWithUV(d5, d12, d3, interpolatedU3, minV2);
            instance.addVertexWithUV(d5, d12, d4, interpolatedU3, minV2);
            instance.addVertexWithUV(d5, d13, d4, interpolatedU3, maxV2);
            instance.addVertexWithUV(d6, d13, d4, interpolatedU4, maxV2);
            instance.addVertexWithUV(d6, d12, d4, interpolatedU4, minV2);
        }

        return true;
    }

    public boolean renderBlockPane(BlockPane blockPane, int n, int n2, int n3) {
        int height = this.blockAccess.getHeight();
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(blockPane.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        int colorMultiplier = blockPane.colorMultiplier(this.blockAccess, n, n2, n3);
        float f = (colorMultiplier >> 16 & 0xFF) / 255.0F;
        float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
        float f3 = (colorMultiplier & 0xFF) / 255.0F;
        if (EntityRenderer.anaglyphEnable) {
            float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
            f = f4;
            f2 = f5;
            f3 = f6;
        }

        instance.setColorOpaque_F(f, f2, f3);
        IIcon icon;
        IIcon icon2;
        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
            icon2 = this.overrideBlockTexture;
        } else {
            int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
            icon = this.getBlockIconFromSideAndMetadata(blockPane, 0, blockMetadata);
            icon2 = blockPane.getIIcon();
        }

        IIcon icon3 = icon;
        IIcon icon4 = icon;
        IIcon icon5 = icon;
        if (Config.isConnectedTextures() && this.overrideBlockTexture == null) {
            icon = ConnectedTextures.getConnectedTexture(this.blockAccess, blockPane, n, n2, n3, 2, icon);
            icon3 = ConnectedTextures.getConnectedTexture(this.blockAccess, blockPane, n, n2, n3, 3, icon3);
            icon4 = ConnectedTextures.getConnectedTexture(this.blockAccess, blockPane, n, n2, n3, 4, icon4);
            icon5 = ConnectedTextures.getConnectedTexture(this.blockAccess, blockPane, n, n2, n3, 5, icon5);
        }

        double minU = icon.getMinU();
        double interpolatedU = icon.getInterpolatedU(8.0);
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();
        double minU2 = icon3.getMinU();
        double interpolatedU2 = icon3.getInterpolatedU(8.0);
        double maxU2 = icon3.getMaxU();
        double minV2 = icon3.getMinV();
        double maxV2 = icon3.getMaxV();
        double minU3 = icon4.getMinU();
        double interpolatedU3 = icon4.getInterpolatedU(8.0);
        double maxU3 = icon4.getMaxU();
        double minV3 = icon4.getMinV();
        double maxV3 = icon4.getMaxV();
        double minU4 = icon5.getMinU();
        double interpolatedU4 = icon5.getInterpolatedU(8.0);
        double maxU4 = icon5.getMaxU();
        double minV4 = icon5.getMinV();
        double maxV4 = icon5.getMaxV();
        double interpolatedU5 = icon2.getInterpolatedU(7.0);
        double interpolatedU6 = icon2.getInterpolatedU(9.0);
        double minV5 = icon2.getMinV();
        double interpolatedV = icon2.getInterpolatedV(8.0);
        double maxV5 = icon2.getMaxV();
        double d = n;
        double d2 = n + 0.5;
        double d3 = n + 1;
        double d4 = n3;
        double d5 = n3 + 0.5;
        double d6 = n3 + 1;
        double d7 = n + 0.5 - 0.0625;
        double d8 = n + 0.5 + 0.0625;
        double d9 = n3 + 0.5 - 0.0625;
        double d10 = n3 + 0.5 + 0.0625;
        boolean canPaneConnectToBlock = blockPane.canPaneConnectToBlock(this.blockAccess.getBlock(n, n2, n3 - 1));
        boolean canPaneConnectToBlock2 = blockPane.canPaneConnectToBlock(this.blockAccess.getBlock(n, n2, n3 + 1));
        boolean canPaneConnectToBlock3 = blockPane.canPaneConnectToBlock(this.blockAccess.getBlock(n - 1, n2, n3));
        boolean canPaneConnectToBlock4 = blockPane.canPaneConnectToBlock(this.blockAccess.getBlock(n + 1, n2, n3));
        boolean shouldSideBeRendered = blockPane.shouldSideBeRendered(this.blockAccess, n, n2 + 1, n3, 1);
        boolean shouldSideBeRendered2 = blockPane.shouldSideBeRendered(this.blockAccess, n, n2 - 1, n3, 0);
        double d11 = 0.01;
        double d12 = 0.005;
        if ((!canPaneConnectToBlock3 || !canPaneConnectToBlock4) && (canPaneConnectToBlock3 || canPaneConnectToBlock4 || canPaneConnectToBlock || canPaneConnectToBlock2)) {
            if (canPaneConnectToBlock3 && !canPaneConnectToBlock4) {
                instance.addVertexWithUV(d, n2 + 1, d5, minU, minV);
                instance.addVertexWithUV(d, n2 + 0, d5, minU, maxV);
                instance.addVertexWithUV(d2, n2 + 0, d5, interpolatedU, maxV);
                instance.addVertexWithUV(d2, n2 + 1, d5, interpolatedU, minV);
                instance.addVertexWithUV(d2, n2 + 1, d5, interpolatedU2, minV2);
                instance.addVertexWithUV(d2, n2 + 0, d5, interpolatedU2, maxV2);
                instance.addVertexWithUV(d, n2 + 0, d5, maxU2, maxV2);
                instance.addVertexWithUV(d, n2 + 1, d5, maxU2, minV2);
                if (!canPaneConnectToBlock2 && !canPaneConnectToBlock) {
                    instance.addVertexWithUV(d2, n2 + 1, d10, interpolatedU5, minV5);
                    instance.addVertexWithUV(d2, n2 + 0, d10, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d2, n2 + 0, d9, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d2, n2 + 1, d9, interpolatedU6, minV5);
                    instance.addVertexWithUV(d2, n2 + 1, d9, interpolatedU5, minV5);
                    instance.addVertexWithUV(d2, n2 + 0, d9, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d2, n2 + 0, d10, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d2, n2 + 1, d10, interpolatedU6, minV5);
                }

                if (shouldSideBeRendered || n2 < height - 1 && this.blockAccess.isAirBlock(n - 1, n2 + 1, n3)) {
                    instance.addVertexWithUV(d, n2 + 1 + 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d10, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d9, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d, n2 + 1 + 0.01, d9, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d, n2 + 1 + 0.01, d10, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d, n2 + 1 + 0.01, d9, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d9, interpolatedU5, interpolatedV);
                }

                if (shouldSideBeRendered2 || n2 > 1 && this.blockAccess.isAirBlock(n - 1, n2 - 1, n3)) {
                    instance.addVertexWithUV(d, n2 - 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d2, n2 - 0.01, d10, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d2, n2 - 0.01, d9, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d, n2 - 0.01, d9, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d2, n2 - 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d, n2 - 0.01, d10, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d, n2 - 0.01, d9, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d2, n2 - 0.01, d9, interpolatedU5, interpolatedV);
                }
            } else if (!canPaneConnectToBlock3 && canPaneConnectToBlock4) {
                instance.addVertexWithUV(d2, n2 + 1, d5, interpolatedU, minV);
                instance.addVertexWithUV(d2, n2 + 0, d5, interpolatedU, maxV);
                instance.addVertexWithUV(d3, n2 + 0, d5, maxU, maxV);
                instance.addVertexWithUV(d3, n2 + 1, d5, maxU, minV);
                instance.addVertexWithUV(d3, n2 + 1, d5, minU2, minV2);
                instance.addVertexWithUV(d3, n2 + 0, d5, minU2, maxV2);
                instance.addVertexWithUV(d2, n2 + 0, d5, interpolatedU2, maxV2);
                instance.addVertexWithUV(d2, n2 + 1, d5, interpolatedU2, minV2);
                if (!canPaneConnectToBlock2 && !canPaneConnectToBlock) {
                    instance.addVertexWithUV(d2, n2 + 1, d9, interpolatedU5, minV5);
                    instance.addVertexWithUV(d2, n2 + 0, d9, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d2, n2 + 0, d10, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d2, n2 + 1, d10, interpolatedU6, minV5);
                    instance.addVertexWithUV(d2, n2 + 1, d10, interpolatedU5, minV5);
                    instance.addVertexWithUV(d2, n2 + 0, d10, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d2, n2 + 0, d9, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d2, n2 + 1, d9, interpolatedU6, minV5);
                }

                if (shouldSideBeRendered || n2 < height - 1 && this.blockAccess.isAirBlock(n + 1, n2 + 1, n3)) {
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d10, interpolatedU6, minV5);
                    instance.addVertexWithUV(d3, n2 + 1 + 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d3, n2 + 1 + 0.01, d9, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d9, interpolatedU5, minV5);
                    instance.addVertexWithUV(d3, n2 + 1 + 0.01, d10, interpolatedU6, minV5);
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d9, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d3, n2 + 1 + 0.01, d9, interpolatedU5, minV5);
                }

                if (shouldSideBeRendered2 || n2 > 1 && this.blockAccess.isAirBlock(n + 1, n2 - 1, n3)) {
                    instance.addVertexWithUV(d2, n2 - 0.01, d10, interpolatedU6, minV5);
                    instance.addVertexWithUV(d3, n2 - 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d3, n2 - 0.01, d9, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d2, n2 - 0.01, d9, interpolatedU5, minV5);
                    instance.addVertexWithUV(d3, n2 - 0.01, d10, interpolatedU6, minV5);
                    instance.addVertexWithUV(d2, n2 - 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d2, n2 - 0.01, d9, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d3, n2 - 0.01, d9, interpolatedU5, minV5);
                }
            }
        } else {
            instance.addVertexWithUV(d, n2 + 1, d5, minU, minV);
            instance.addVertexWithUV(d, n2 + 0, d5, minU, maxV);
            instance.addVertexWithUV(d3, n2 + 0, d5, maxU, maxV);
            instance.addVertexWithUV(d3, n2 + 1, d5, maxU, minV);
            instance.addVertexWithUV(d3, n2 + 1, d5, minU2, minV2);
            instance.addVertexWithUV(d3, n2 + 0, d5, minU2, maxV2);
            instance.addVertexWithUV(d, n2 + 0, d5, maxU2, maxV2);
            instance.addVertexWithUV(d, n2 + 1, d5, maxU2, minV2);
            if (shouldSideBeRendered) {
                instance.addVertexWithUV(d, n2 + 1 + 0.01, d10, interpolatedU6, maxV5);
                instance.addVertexWithUV(d3, n2 + 1 + 0.01, d10, interpolatedU6, minV5);
                instance.addVertexWithUV(d3, n2 + 1 + 0.01, d9, interpolatedU5, minV5);
                instance.addVertexWithUV(d, n2 + 1 + 0.01, d9, interpolatedU5, maxV5);
                instance.addVertexWithUV(d3, n2 + 1 + 0.01, d10, interpolatedU6, maxV5);
                instance.addVertexWithUV(d, n2 + 1 + 0.01, d10, interpolatedU6, minV5);
                instance.addVertexWithUV(d, n2 + 1 + 0.01, d9, interpolatedU5, minV5);
                instance.addVertexWithUV(d3, n2 + 1 + 0.01, d9, interpolatedU5, maxV5);
            } else {
                if (n2 < height - 1 && this.blockAccess.isAirBlock(n - 1, n2 + 1, n3)) {
                    instance.addVertexWithUV(d, n2 + 1 + 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d10, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d9, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d, n2 + 1 + 0.01, d9, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d, n2 + 1 + 0.01, d10, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d, n2 + 1 + 0.01, d9, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d9, interpolatedU5, interpolatedV);
                }

                if (n2 < height - 1 && this.blockAccess.isAirBlock(n + 1, n2 + 1, n3)) {
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d10, interpolatedU6, minV5);
                    instance.addVertexWithUV(d3, n2 + 1 + 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d3, n2 + 1 + 0.01, d9, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d9, interpolatedU5, minV5);
                    instance.addVertexWithUV(d3, n2 + 1 + 0.01, d10, interpolatedU6, minV5);
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d2, n2 + 1 + 0.01, d9, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d3, n2 + 1 + 0.01, d9, interpolatedU5, minV5);
                }
            }

            if (shouldSideBeRendered2) {
                instance.addVertexWithUV(d, n2 - 0.01, d10, interpolatedU6, maxV5);
                instance.addVertexWithUV(d3, n2 - 0.01, d10, interpolatedU6, minV5);
                instance.addVertexWithUV(d3, n2 - 0.01, d9, interpolatedU5, minV5);
                instance.addVertexWithUV(d, n2 - 0.01, d9, interpolatedU5, maxV5);
                instance.addVertexWithUV(d3, n2 - 0.01, d10, interpolatedU6, maxV5);
                instance.addVertexWithUV(d, n2 - 0.01, d10, interpolatedU6, minV5);
                instance.addVertexWithUV(d, n2 - 0.01, d9, interpolatedU5, minV5);
                instance.addVertexWithUV(d3, n2 - 0.01, d9, interpolatedU5, maxV5);
            } else {
                if (n2 > 1 && this.blockAccess.isAirBlock(n - 1, n2 - 1, n3)) {
                    instance.addVertexWithUV(d, n2 - 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d2, n2 - 0.01, d10, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d2, n2 - 0.01, d9, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d, n2 - 0.01, d9, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d2, n2 - 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d, n2 - 0.01, d10, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d, n2 - 0.01, d9, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d2, n2 - 0.01, d9, interpolatedU5, interpolatedV);
                }

                if (n2 > 1 && this.blockAccess.isAirBlock(n + 1, n2 - 1, n3)) {
                    instance.addVertexWithUV(d2, n2 - 0.01, d10, interpolatedU6, minV5);
                    instance.addVertexWithUV(d3, n2 - 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d3, n2 - 0.01, d9, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d2, n2 - 0.01, d9, interpolatedU5, minV5);
                    instance.addVertexWithUV(d3, n2 - 0.01, d10, interpolatedU6, minV5);
                    instance.addVertexWithUV(d2, n2 - 0.01, d10, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d2, n2 - 0.01, d9, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d3, n2 - 0.01, d9, interpolatedU5, minV5);
                }
            }
        }

        if ((!canPaneConnectToBlock || !canPaneConnectToBlock2) && (canPaneConnectToBlock3 || canPaneConnectToBlock4 || canPaneConnectToBlock || canPaneConnectToBlock2)) {
            if (canPaneConnectToBlock && !canPaneConnectToBlock2) {
                instance.addVertexWithUV(d2, n2 + 1, d4, minU3, minV3);
                instance.addVertexWithUV(d2, n2 + 0, d4, minU3, maxV3);
                instance.addVertexWithUV(d2, n2 + 0, d5, interpolatedU3, maxV3);
                instance.addVertexWithUV(d2, n2 + 1, d5, interpolatedU3, minV3);
                instance.addVertexWithUV(d2, n2 + 1, d5, interpolatedU4, minV4);
                instance.addVertexWithUV(d2, n2 + 0, d5, interpolatedU4, maxV4);
                instance.addVertexWithUV(d2, n2 + 0, d4, maxU4, maxV4);
                instance.addVertexWithUV(d2, n2 + 1, d4, maxU4, minV4);
                if (!canPaneConnectToBlock4 && !canPaneConnectToBlock3) {
                    instance.addVertexWithUV(d7, n2 + 1, d5, interpolatedU5, minV5);
                    instance.addVertexWithUV(d7, n2 + 0, d5, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d8, n2 + 0, d5, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d8, n2 + 1, d5, interpolatedU6, minV5);
                    instance.addVertexWithUV(d8, n2 + 1, d5, interpolatedU5, minV5);
                    instance.addVertexWithUV(d8, n2 + 0, d5, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d7, n2 + 0, d5, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d7, n2 + 1, d5, interpolatedU6, minV5);
                }

                if (shouldSideBeRendered || n2 < height - 1 && this.blockAccess.isAirBlock(n, n2 + 1, n3 - 1)) {
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d4, interpolatedU6, minV5);
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d5, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d5, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d4, interpolatedU5, minV5);
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d5, interpolatedU6, minV5);
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d4, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d4, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d5, interpolatedU5, minV5);
                }

                if (shouldSideBeRendered2 || n2 > 1 && this.blockAccess.isAirBlock(n, n2 - 1, n3 - 1)) {
                    instance.addVertexWithUV(d7, n2 - 0.005, d4, interpolatedU6, minV5);
                    instance.addVertexWithUV(d7, n2 - 0.005, d5, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d8, n2 - 0.005, d5, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d8, n2 - 0.005, d4, interpolatedU5, minV5);
                    instance.addVertexWithUV(d7, n2 - 0.005, d5, interpolatedU6, minV5);
                    instance.addVertexWithUV(d7, n2 - 0.005, d4, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d8, n2 - 0.005, d4, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d8, n2 - 0.005, d5, interpolatedU5, minV5);
                }
            } else if (!canPaneConnectToBlock && canPaneConnectToBlock2) {
                instance.addVertexWithUV(d2, n2 + 1, d5, interpolatedU3, minV3);
                instance.addVertexWithUV(d2, n2 + 0, d5, interpolatedU3, maxV3);
                instance.addVertexWithUV(d2, n2 + 0, d6, maxU3, maxV3);
                instance.addVertexWithUV(d2, n2 + 1, d6, maxU3, minV3);
                instance.addVertexWithUV(d2, n2 + 1, d6, minU4, minV4);
                instance.addVertexWithUV(d2, n2 + 0, d6, minU4, maxV4);
                instance.addVertexWithUV(d2, n2 + 0, d5, interpolatedU4, maxV4);
                instance.addVertexWithUV(d2, n2 + 1, d5, interpolatedU4, minV4);
                if (!canPaneConnectToBlock4 && !canPaneConnectToBlock3) {
                    instance.addVertexWithUV(d8, n2 + 1, d5, interpolatedU5, minV5);
                    instance.addVertexWithUV(d8, n2 + 0, d5, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d7, n2 + 0, d5, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d7, n2 + 1, d5, interpolatedU6, minV5);
                    instance.addVertexWithUV(d7, n2 + 1, d5, interpolatedU5, minV5);
                    instance.addVertexWithUV(d7, n2 + 0, d5, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d8, n2 + 0, d5, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d8, n2 + 1, d5, interpolatedU6, minV5);
                }

                if (shouldSideBeRendered || n2 < height - 1 && this.blockAccess.isAirBlock(n, n2 + 1, n3 + 1)) {
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d5, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d6, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d6, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d5, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d6, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d5, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d5, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d6, interpolatedU6, interpolatedV);
                }

                if (shouldSideBeRendered2 || n2 > 1 && this.blockAccess.isAirBlock(n, n2 - 1, n3 + 1)) {
                    instance.addVertexWithUV(d7, n2 - 0.005, d5, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d7, n2 - 0.005, d6, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d8, n2 - 0.005, d6, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d8, n2 - 0.005, d5, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d7, n2 - 0.005, d6, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d7, n2 - 0.005, d5, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d8, n2 - 0.005, d5, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d8, n2 - 0.005, d6, interpolatedU6, interpolatedV);
                }
            }
        } else {
            instance.addVertexWithUV(d2, n2 + 1, d6, minU4, minV4);
            instance.addVertexWithUV(d2, n2 + 0, d6, minU4, maxV4);
            instance.addVertexWithUV(d2, n2 + 0, d4, maxU4, maxV4);
            instance.addVertexWithUV(d2, n2 + 1, d4, maxU4, minV4);
            instance.addVertexWithUV(d2, n2 + 1, d4, minU3, minV3);
            instance.addVertexWithUV(d2, n2 + 0, d4, minU3, maxV3);
            instance.addVertexWithUV(d2, n2 + 0, d6, maxU3, maxV3);
            instance.addVertexWithUV(d2, n2 + 1, d6, maxU3, minV3);
            if (shouldSideBeRendered) {
                instance.addVertexWithUV(d8, n2 + 1 + 0.005, d6, interpolatedU6, maxV5);
                instance.addVertexWithUV(d8, n2 + 1 + 0.005, d4, interpolatedU6, minV5);
                instance.addVertexWithUV(d7, n2 + 1 + 0.005, d4, interpolatedU5, minV5);
                instance.addVertexWithUV(d7, n2 + 1 + 0.005, d6, interpolatedU5, maxV5);
                instance.addVertexWithUV(d8, n2 + 1 + 0.005, d4, interpolatedU6, maxV5);
                instance.addVertexWithUV(d8, n2 + 1 + 0.005, d6, interpolatedU6, minV5);
                instance.addVertexWithUV(d7, n2 + 1 + 0.005, d6, interpolatedU5, minV5);
                instance.addVertexWithUV(d7, n2 + 1 + 0.005, d4, interpolatedU5, maxV5);
            } else {
                if (n2 < height - 1 && this.blockAccess.isAirBlock(n, n2 + 1, n3 - 1)) {
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d4, interpolatedU6, minV5);
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d5, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d5, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d4, interpolatedU5, minV5);
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d5, interpolatedU6, minV5);
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d4, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d4, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d5, interpolatedU5, minV5);
                }

                if (n2 < height - 1 && this.blockAccess.isAirBlock(n, n2 + 1, n3 + 1)) {
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d5, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d6, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d6, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d5, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d6, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d7, n2 + 1 + 0.005, d5, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d5, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d8, n2 + 1 + 0.005, d6, interpolatedU6, interpolatedV);
                }
            }

            if (shouldSideBeRendered2) {
                instance.addVertexWithUV(d8, n2 - 0.005, d6, interpolatedU6, maxV5);
                instance.addVertexWithUV(d8, n2 - 0.005, d4, interpolatedU6, minV5);
                instance.addVertexWithUV(d7, n2 - 0.005, d4, interpolatedU5, minV5);
                instance.addVertexWithUV(d7, n2 - 0.005, d6, interpolatedU5, maxV5);
                instance.addVertexWithUV(d8, n2 - 0.005, d4, interpolatedU6, maxV5);
                instance.addVertexWithUV(d8, n2 - 0.005, d6, interpolatedU6, minV5);
                instance.addVertexWithUV(d7, n2 - 0.005, d6, interpolatedU5, minV5);
                instance.addVertexWithUV(d7, n2 - 0.005, d4, interpolatedU5, maxV5);
            } else {
                if (n2 > 1 && this.blockAccess.isAirBlock(n, n2 - 1, n3 - 1)) {
                    instance.addVertexWithUV(d7, n2 - 0.005, d4, interpolatedU6, minV5);
                    instance.addVertexWithUV(d7, n2 - 0.005, d5, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d8, n2 - 0.005, d5, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d8, n2 - 0.005, d4, interpolatedU5, minV5);
                    instance.addVertexWithUV(d7, n2 - 0.005, d5, interpolatedU6, minV5);
                    instance.addVertexWithUV(d7, n2 - 0.005, d4, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d8, n2 - 0.005, d4, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d8, n2 - 0.005, d5, interpolatedU5, minV5);
                }

                if (n2 > 1 && this.blockAccess.isAirBlock(n, n2 - 1, n3 + 1)) {
                    instance.addVertexWithUV(d7, n2 - 0.005, d5, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d7, n2 - 0.005, d6, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d8, n2 - 0.005, d6, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d8, n2 - 0.005, d5, interpolatedU6, interpolatedV);
                    instance.addVertexWithUV(d7, n2 - 0.005, d6, interpolatedU5, interpolatedV);
                    instance.addVertexWithUV(d7, n2 - 0.005, d5, interpolatedU5, maxV5);
                    instance.addVertexWithUV(d8, n2 - 0.005, d5, interpolatedU6, maxV5);
                    instance.addVertexWithUV(d8, n2 - 0.005, d6, interpolatedU6, interpolatedV);
                }
            }
        }

        if (Config.isBetterSnow() && this.hasSnowNeighbours(n, n2, n3)) {
            this.renderSnow(n, n2, n3, Blocks.snow_layer.getBlockBoundsMaxY());
        }

        return true;
    }

    public boolean renderCrossedSquares(Block block, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        int colorMultiplier = CustomColorizer.getColorMultiplier(block, this.blockAccess, n, n2, n3);
        float f = (colorMultiplier >> 16 & 0xFF) / 255.0F;
        float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
        float f3 = (colorMultiplier & 0xFF) / 255.0F;
        if (EntityRenderer.anaglyphEnable) {
            float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
            f = f4;
            f2 = f5;
            f3 = f6;
        }

        instance.setColorOpaque_F(f, f2, f3);
        double d = n;
        double d2 = n2;
        double d3 = n3;
        if (block == Blocks.tallgrass) {
            long l = n * 3129871 ^ n3 * 116129781L ^ n2;
            l = l * l * 42317861L + l * 11L;
            d += ((float)(l >> 16 & 15L) / 15.0F - 0.5) * 0.5;
            d2 += ((float)(l >> 20 & 15L) / 15.0F - 1.0) * 0.2;
            d3 += ((float)(l >> 24 & 15L) / 15.0F - 0.5) * 0.5;
        } else if (block == Blocks.red_flower || block == Blocks.yellow_flower) {
            long l = n * 3129871 ^ n3 * 116129781L ^ n2;
            l = l * l * 42317861L + l * 11L;
            d += ((float)(l >> 16 & 15L) / 15.0F - 0.5) * 0.3;
            d3 += ((float)(l >> 24 & 15L) / 15.0F - 0.5) * 0.3;
        }

        IIcon blockIconFromSideAndMetadata = this.getBlockIconFromSideAndMetadata(block, 0, this.blockAccess.getBlockMetadata(n, n2, n3));
        if (Config.isConnectedTextures() && this.overrideBlockTexture == null) {
            blockIconFromSideAndMetadata = ConnectedTextures.getConnectedTexture(this.blockAccess, block, n, n2, n3, 2, blockIconFromSideAndMetadata);
        }

        this.drawCrossedSquares(blockIconFromSideAndMetadata, d, d2, d3, 1.0F);
        if (Config.isBetterSnow() && this.hasSnowNeighbours(n, n2, n3)) {
            this.renderSnow(n, n2, n3, Blocks.snow_layer.getBlockBoundsMaxY());
        }

        return true;
    }

    public boolean renderBlockDoublePlant(BlockDoublePlant blockDoublePlant, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(blockDoublePlant.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        int colorMultiplier = CustomColorizer.getColorMultiplier(blockDoublePlant, this.blockAccess, n, n2, n3);
        float f = (colorMultiplier >> 16 & 0xFF) / 255.0F;
        float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
        float f3 = (colorMultiplier & 0xFF) / 255.0F;
        if (EntityRenderer.anaglyphEnable) {
            float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
            f = f4;
            f2 = f5;
            f3 = f6;
        }

        instance.setColorOpaque_F(f, f2, f3);
        long l = n * 3129871 ^ n3 * 116129781L;
        l = l * l * 42317861L + l * 11L;
        double d = n;
        double d2 = n2;
        double d3 = n3;
        d += ((float)(l >> 16 & 15L) / 15.0F - 0.5) * 0.3;
        d3 += ((float)(l >> 24 & 15L) / 15.0F - 0.5) * 0.3;
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        boolean flag = false;
        boolean check = BlockDoublePlant.check(blockMetadata);
        int n4;
        if (check) {
            if (this.blockAccess.getBlock(n, n2 - 1, n3) != blockDoublePlant) {
                return false;
            }

            n4 = BlockDoublePlant.computeInt(this.blockAccess.getBlockMetadata(n, n2 - 1, n3));
        } else {
            n4 = BlockDoublePlant.computeInt(blockMetadata);
        }

        IIcon findIIcon = blockDoublePlant.findIIcon(check, n4);
        this.drawCrossedSquares(findIIcon, d, d2, d3, 1.0F);
        if (check && n4 == 0) {
            IIcon icon = blockDoublePlant.sunflowerIcons[0];
            double d4 = Math.cos(l * 0.8) * Math.PI * 0.1;
            double cos = Math.cos(d4);
            double sin = Math.sin(d4);
            double minU = icon.getMinU();
            double minV = icon.getMinV();
            double maxU = icon.getMaxU();
            double maxV = icon.getMaxV();
            double d5 = 0.3;
            double d6 = -0.05;
            double d7 = 0.5 + 0.3 * cos - 0.5 * sin;
            double d8 = 0.5 + 0.5 * cos + 0.3 * sin;
            double d9 = 0.5 + 0.3 * cos + 0.5 * sin;
            double d10 = 0.5 + -0.5 * cos + 0.3 * sin;
            double d11 = 0.5 + -0.05 * cos + 0.5 * sin;
            double d12 = 0.5 + -0.5 * cos + -0.05 * sin;
            double d13 = 0.5 + -0.05 * cos - 0.5 * sin;
            double d14 = 0.5 + 0.5 * cos + -0.05 * sin;
            instance.addVertexWithUV(d + d11, d2 + 1.0, d3 + d12, minU, maxV);
            instance.addVertexWithUV(d + d13, d2 + 1.0, d3 + d14, maxU, maxV);
            instance.addVertexWithUV(d + d7, d2 + 0.0, d3 + d8, maxU, minV);
            instance.addVertexWithUV(d + d9, d2 + 0.0, d3 + d10, minU, minV);
            IIcon icon2 = blockDoublePlant.sunflowerIcons[1];
            minU = icon2.getMinU();
            minV = icon2.getMinV();
            maxU = icon2.getMaxU();
            maxV = icon2.getMaxV();
            instance.addVertexWithUV(d + d13, d2 + 1.0, d3 + d14, minU, maxV);
            instance.addVertexWithUV(d + d11, d2 + 1.0, d3 + d12, maxU, maxV);
            instance.addVertexWithUV(d + d9, d2 + 0.0, d3 + d10, maxU, minV);
            instance.addVertexWithUV(d + d7, d2 + 0.0, d3 + d8, minU, minV);
        }

        if (Config.isBetterSnow() && this.hasSnowNeighbours(n, n2, n3)) {
            this.renderSnow(n, n2, n3, Blocks.snow_layer.getBlockBoundsMaxY());
        }

        return true;
    }

    public boolean renderBlockStem(Block block, int n, int n2, int n3) {
        BlockStem blockStem = (BlockStem)block;
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(blockStem.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        int stemColorMultiplier = CustomColorizer.getStemColorMultiplier(blockStem, this.blockAccess, n, n2, n3);
        float f = (stemColorMultiplier >> 16 & 0xFF) / 255.0F;
        float f2 = (stemColorMultiplier >> 8 & 0xFF) / 255.0F;
        float f3 = (stemColorMultiplier & 0xFF) / 255.0F;
        if (EntityRenderer.anaglyphEnable) {
            float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
            f = f4;
            f2 = f5;
            f3 = f6;
        }

        instance.setColorOpaque_F(f, f2, f3);
        blockStem.setBlockBoundsBasedOnState(this.blockAccess, n, n2, n3);
        int state = blockStem.getState(this.blockAccess, n, n2, n3);
        if (state < 0) {
            this.renderBlockStemSmall(blockStem, this.blockAccess.getBlockMetadata(n, n2, n3), this.renderMaxY, n, n2 - 0.0625F, n3);
        } else {
            this.renderBlockStemSmall(blockStem, this.blockAccess.getBlockMetadata(n, n2, n3), 0.5, n, n2 - 0.0625F, n3);
            this.renderBlockStemBig(blockStem, this.blockAccess.getBlockMetadata(n, n2, n3), state, this.renderMaxY, n, n2 - 0.0625F, n3);
        }

        return true;
    }

    public boolean renderBlockCrops(Block block, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        this.renderBlockCropsImpl(block, this.blockAccess.getBlockMetadata(n, n2, n3), n, n2 - 0.0625F, n3);
        return true;
    }

    public void renderTorchAtAngle(Block block, double d, double d2, double d3, double d4, double d5, int n) {
        Tessellator instance = Tessellator.instance;
        IIcon blockIconFromSideAndMetadata = this.getBlockIconFromSideAndMetadata(block, 0, n);
        if (this.hasOverrideBlockTexture()) {
            blockIconFromSideAndMetadata = this.overrideBlockTexture;
        }

        double minU = blockIconFromSideAndMetadata.getMinU();
        double minV = blockIconFromSideAndMetadata.getMinV();
        double maxU = blockIconFromSideAndMetadata.getMaxU();
        double maxV = blockIconFromSideAndMetadata.getMaxV();
        double interpolatedU = blockIconFromSideAndMetadata.getInterpolatedU(7.0);
        double interpolatedV = blockIconFromSideAndMetadata.getInterpolatedV(6.0);
        double interpolatedU2 = blockIconFromSideAndMetadata.getInterpolatedU(9.0);
        double interpolatedV2 = blockIconFromSideAndMetadata.getInterpolatedV(8.0);
        double interpolatedU3 = blockIconFromSideAndMetadata.getInterpolatedU(7.0);
        double interpolatedV3 = blockIconFromSideAndMetadata.getInterpolatedV(13.0);
        double interpolatedU4 = blockIconFromSideAndMetadata.getInterpolatedU(9.0);
        double interpolatedV4 = blockIconFromSideAndMetadata.getInterpolatedV(15.0);
        d += 0.5;
        d3 += 0.5;
        double d6 = d - 0.5;
        double d7 = d + 0.5;
        double d8 = d3 - 0.5;
        double d9 = d3 + 0.5;
        double d10 = 0.0625;
        double d11 = 0.625;
        instance.addVertexWithUV(d + d4 * (1.0 - d11) - d10, d2 + d11, d3 + d5 * (1.0 - d11) - d10, interpolatedU, interpolatedV);
        instance.addVertexWithUV(d + d4 * (1.0 - d11) - d10, d2 + d11, d3 + d5 * (1.0 - d11) + d10, interpolatedU, interpolatedV2);
        instance.addVertexWithUV(d + d4 * (1.0 - d11) + d10, d2 + d11, d3 + d5 * (1.0 - d11) + d10, interpolatedU2, interpolatedV2);
        instance.addVertexWithUV(d + d4 * (1.0 - d11) + d10, d2 + d11, d3 + d5 * (1.0 - d11) - d10, interpolatedU2, interpolatedV);
        instance.addVertexWithUV(d + d10 + d4, d2, d3 - d10 + d5, interpolatedU4, interpolatedV3);
        instance.addVertexWithUV(d + d10 + d4, d2, d3 + d10 + d5, interpolatedU4, interpolatedV4);
        instance.addVertexWithUV(d - d10 + d4, d2, d3 + d10 + d5, interpolatedU3, interpolatedV4);
        instance.addVertexWithUV(d - d10 + d4, d2, d3 - d10 + d5, interpolatedU3, interpolatedV3);
        instance.addVertexWithUV(d - d10, d2 + 1.0, d8, minU, minV);
        instance.addVertexWithUV(d - d10 + d4, d2 + 0.0, d8 + d5, minU, maxV);
        instance.addVertexWithUV(d - d10 + d4, d2 + 0.0, d9 + d5, maxU, maxV);
        instance.addVertexWithUV(d - d10, d2 + 1.0, d9, maxU, minV);
        instance.addVertexWithUV(d + d10, d2 + 1.0, d9, minU, minV);
        instance.addVertexWithUV(d + d4 + d10, d2 + 0.0, d9 + d5, minU, maxV);
        instance.addVertexWithUV(d + d4 + d10, d2 + 0.0, d8 + d5, maxU, maxV);
        instance.addVertexWithUV(d + d10, d2 + 1.0, d8, maxU, minV);
        instance.addVertexWithUV(d6, d2 + 1.0, d3 + d10, minU, minV);
        instance.addVertexWithUV(d6 + d4, d2 + 0.0, d3 + d10 + d5, minU, maxV);
        instance.addVertexWithUV(d7 + d4, d2 + 0.0, d3 + d10 + d5, maxU, maxV);
        instance.addVertexWithUV(d7, d2 + 1.0, d3 + d10, maxU, minV);
        instance.addVertexWithUV(d7, d2 + 1.0, d3 - d10, minU, minV);
        instance.addVertexWithUV(d7 + d4, d2 + 0.0, d3 - d10 + d5, minU, maxV);
        instance.addVertexWithUV(d6 + d4, d2 + 0.0, d3 - d10 + d5, maxU, maxV);
        instance.addVertexWithUV(d6, d2 + 1.0, d3 - d10, maxU, minV);
    }

    public void drawCrossedSquares(IIcon icon, double d, double d2, double d3, float f) {
        Tessellator instance = Tessellator.instance;
        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
        }

        double minU = icon.getMinU();
        double minV = icon.getMinV();
        double maxU = icon.getMaxU();
        double maxV = icon.getMaxV();
        double d4 = 0.45 * f;
        double d5 = d + 0.5 - d4;
        double d6 = d + 0.5 + d4;
        double d7 = d3 + 0.5 - d4;
        double d8 = d3 + 0.5 + d4;
        instance.addVertexWithUV(d5, d2 + f, d7, minU, minV);
        instance.addVertexWithUV(d5, d2 + 0.0, d7, minU, maxV);
        instance.addVertexWithUV(d6, d2 + 0.0, d8, maxU, maxV);
        instance.addVertexWithUV(d6, d2 + f, d8, maxU, minV);
        instance.addVertexWithUV(d6, d2 + f, d8, minU, minV);
        instance.addVertexWithUV(d6, d2 + 0.0, d8, minU, maxV);
        instance.addVertexWithUV(d5, d2 + 0.0, d7, maxU, maxV);
        instance.addVertexWithUV(d5, d2 + f, d7, maxU, minV);
        instance.addVertexWithUV(d5, d2 + f, d8, minU, minV);
        instance.addVertexWithUV(d5, d2 + 0.0, d8, minU, maxV);
        instance.addVertexWithUV(d6, d2 + 0.0, d7, maxU, maxV);
        instance.addVertexWithUV(d6, d2 + f, d7, maxU, minV);
        instance.addVertexWithUV(d6, d2 + f, d7, minU, minV);
        instance.addVertexWithUV(d6, d2 + 0.0, d7, minU, maxV);
        instance.addVertexWithUV(d5, d2 + 0.0, d8, maxU, maxV);
        instance.addVertexWithUV(d5, d2 + f, d8, maxU, minV);
    }

    public void renderBlockStemSmall(Block block, int n, double d, double d2, double d3, double d4) {
        Tessellator instance = Tessellator.instance;
        IIcon blockIconFromSideAndMetadata = this.getBlockIconFromSideAndMetadata(block, 0, n);
        if (this.hasOverrideBlockTexture()) {
            blockIconFromSideAndMetadata = this.overrideBlockTexture;
        }

        double minU = blockIconFromSideAndMetadata.getMinU();
        double minV = blockIconFromSideAndMetadata.getMinV();
        double maxU = blockIconFromSideAndMetadata.getMaxU();
        double interpolatedV = blockIconFromSideAndMetadata.getInterpolatedV(d * 16.0);
        double d5 = d2 + 0.5 - 0.45F;
        double d6 = d2 + 0.5 + 0.45F;
        double d7 = d4 + 0.5 - 0.45F;
        double d8 = d4 + 0.5 + 0.45F;
        instance.addVertexWithUV(d5, d3 + d, d7, minU, minV);
        instance.addVertexWithUV(d5, d3 + 0.0, d7, minU, interpolatedV);
        instance.addVertexWithUV(d6, d3 + 0.0, d8, maxU, interpolatedV);
        instance.addVertexWithUV(d6, d3 + d, d8, maxU, minV);
        instance.addVertexWithUV(d6, d3 + d, d8, maxU, minV);
        instance.addVertexWithUV(d6, d3 + 0.0, d8, maxU, interpolatedV);
        instance.addVertexWithUV(d5, d3 + 0.0, d7, minU, interpolatedV);
        instance.addVertexWithUV(d5, d3 + d, d7, minU, minV);
        instance.addVertexWithUV(d5, d3 + d, d8, minU, minV);
        instance.addVertexWithUV(d5, d3 + 0.0, d8, minU, interpolatedV);
        instance.addVertexWithUV(d6, d3 + 0.0, d7, maxU, interpolatedV);
        instance.addVertexWithUV(d6, d3 + d, d7, maxU, minV);
        instance.addVertexWithUV(d6, d3 + d, d7, maxU, minV);
        instance.addVertexWithUV(d6, d3 + 0.0, d7, maxU, interpolatedV);
        instance.addVertexWithUV(d5, d3 + 0.0, d8, minU, interpolatedV);
        instance.addVertexWithUV(d5, d3 + d, d8, minU, minV);
    }

    public boolean renderBlockLilyPad(Block block, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        IIcon blockIconFromSide = this.getBlockIconFromSide(block, 1);
        if (this.hasOverrideBlockTexture()) {
            blockIconFromSide = this.overrideBlockTexture;
        }

        if (Config.isConnectedTextures() && this.overrideBlockTexture == null) {
            blockIconFromSide = ConnectedTextures.getConnectedTexture(this.blockAccess, block, n, n2, n3, 1, blockIconFromSide);
        }

        float f = 0.015625F;
        double minU = blockIconFromSide.getMinU();
        double minV = blockIconFromSide.getMinV();
        double maxU = blockIconFromSide.getMaxU();
        double maxV = blockIconFromSide.getMaxV();
        long l = n * 3129871 ^ n3 * 116129781L ^ n2;
        l = l * l * 42317861L + l * 11L;
        int n4 = (int)(l >> 16 & 3L);
        instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        float f2 = n + 0.5F;
        float f3 = n3 + 0.5F;
        float f4 = (n4 & 1) * 0.5F * (1 - n4 / 2 % 2 * 2);
        float f5 = (n4 + 1 & 1) * 0.5F * (1 - (n4 + 1) / 2 % 2 * 2);
        int lilypadColor = CustomColorizer.getLilypadColor();
        instance.setColorOpaque_I(lilypadColor);
        instance.addVertexWithUV(f2 + f4 - f5, n2 + f, f3 + f4 + f5, minU, minV);
        instance.addVertexWithUV(f2 + f4 + f5, n2 + f, f3 - f4 + f5, maxU, minV);
        instance.addVertexWithUV(f2 - f4 + f5, n2 + f, f3 - f4 - f5, maxU, maxV);
        instance.addVertexWithUV(f2 - f4 - f5, n2 + f, f3 + f4 - f5, minU, maxV);
        instance.setColorOpaque_I((lilypadColor & 16711422) >> 1);
        instance.addVertexWithUV(f2 - f4 - f5, n2 + f, f3 + f4 - f5, minU, maxV);
        instance.addVertexWithUV(f2 - f4 + f5, n2 + f, f3 - f4 - f5, maxU, maxV);
        instance.addVertexWithUV(f2 + f4 + f5, n2 + f, f3 - f4 + f5, maxU, minV);
        instance.addVertexWithUV(f2 + f4 - f5, n2 + f, f3 + f4 + f5, minU, minV);
        return true;
    }

    public void renderBlockStemBig(BlockStem blockStem, int n, int n2, double d, double d2, double d3, double d4) {
        Tessellator instance = Tessellator.instance;
        IIcon stemIcon = blockStem.getStemIcon();
        if (this.hasOverrideBlockTexture()) {
            stemIcon = this.overrideBlockTexture;
        }

        double minU = stemIcon.getMinU();
        double minV = stemIcon.getMinV();
        double maxU = stemIcon.getMaxU();
        double maxV = stemIcon.getMaxV();
        double d5 = d2 + 0.5 - 0.5;
        double d6 = d2 + 0.5 + 0.5;
        double d7 = d4 + 0.5 - 0.5;
        double d8 = d4 + 0.5 + 0.5;
        double d9 = d2 + 0.5;
        double d10 = d4 + 0.5;
        if ((n2 + 1) / 2 % 2 == 1) {
            double d11 = maxU;
            maxU = minU;
            minU = d11;
        }

        if (n2 < 2) {
            instance.addVertexWithUV(d5, d3 + d, d10, minU, minV);
            instance.addVertexWithUV(d5, d3 + 0.0, d10, minU, maxV);
            instance.addVertexWithUV(d6, d3 + 0.0, d10, maxU, maxV);
            instance.addVertexWithUV(d6, d3 + d, d10, maxU, minV);
            instance.addVertexWithUV(d6, d3 + d, d10, maxU, minV);
            instance.addVertexWithUV(d6, d3 + 0.0, d10, maxU, maxV);
            instance.addVertexWithUV(d5, d3 + 0.0, d10, minU, maxV);
            instance.addVertexWithUV(d5, d3 + d, d10, minU, minV);
        } else {
            instance.addVertexWithUV(d9, d3 + d, d8, minU, minV);
            instance.addVertexWithUV(d9, d3 + 0.0, d8, minU, maxV);
            instance.addVertexWithUV(d9, d3 + 0.0, d7, maxU, maxV);
            instance.addVertexWithUV(d9, d3 + d, d7, maxU, minV);
            instance.addVertexWithUV(d9, d3 + d, d7, maxU, minV);
            instance.addVertexWithUV(d9, d3 + 0.0, d7, maxU, maxV);
            instance.addVertexWithUV(d9, d3 + 0.0, d8, minU, maxV);
            instance.addVertexWithUV(d9, d3 + d, d8, minU, minV);
        }
    }

    public void renderBlockCropsImpl(Block block, int n, double d, double d2, double d3) {
        Tessellator instance = Tessellator.instance;
        IIcon blockIconFromSideAndMetadata = this.getBlockIconFromSideAndMetadata(block, 0, n);
        if (this.hasOverrideBlockTexture()) {
            blockIconFromSideAndMetadata = this.overrideBlockTexture;
        }

        double minU = blockIconFromSideAndMetadata.getMinU();
        double minV = blockIconFromSideAndMetadata.getMinV();
        double maxU = blockIconFromSideAndMetadata.getMaxU();
        double maxV = blockIconFromSideAndMetadata.getMaxV();
        double d4 = d + 0.5 - 0.25;
        double d5 = d + 0.5 + 0.25;
        double d6 = d3 + 0.5 - 0.5;
        double d7 = d3 + 0.5 + 0.5;
        instance.addVertexWithUV(d4, d2 + 1.0, d6, minU, minV);
        instance.addVertexWithUV(d4, d2 + 0.0, d6, minU, maxV);
        instance.addVertexWithUV(d4, d2 + 0.0, d7, maxU, maxV);
        instance.addVertexWithUV(d4, d2 + 1.0, d7, maxU, minV);
        instance.addVertexWithUV(d4, d2 + 1.0, d7, minU, minV);
        instance.addVertexWithUV(d4, d2 + 0.0, d7, minU, maxV);
        instance.addVertexWithUV(d4, d2 + 0.0, d6, maxU, maxV);
        instance.addVertexWithUV(d4, d2 + 1.0, d6, maxU, minV);
        instance.addVertexWithUV(d5, d2 + 1.0, d7, minU, minV);
        instance.addVertexWithUV(d5, d2 + 0.0, d7, minU, maxV);
        instance.addVertexWithUV(d5, d2 + 0.0, d6, maxU, maxV);
        instance.addVertexWithUV(d5, d2 + 1.0, d6, maxU, minV);
        instance.addVertexWithUV(d5, d2 + 1.0, d6, minU, minV);
        instance.addVertexWithUV(d5, d2 + 0.0, d6, minU, maxV);
        instance.addVertexWithUV(d5, d2 + 0.0, d7, maxU, maxV);
        instance.addVertexWithUV(d5, d2 + 1.0, d7, maxU, minV);
        d4 = d + 0.5 - 0.5;
        d5 = d + 0.5 + 0.5;
        d6 = d3 + 0.5 - 0.25;
        d7 = d3 + 0.5 + 0.25;
        instance.addVertexWithUV(d4, d2 + 1.0, d6, minU, minV);
        instance.addVertexWithUV(d4, d2 + 0.0, d6, minU, maxV);
        instance.addVertexWithUV(d5, d2 + 0.0, d6, maxU, maxV);
        instance.addVertexWithUV(d5, d2 + 1.0, d6, maxU, minV);
        instance.addVertexWithUV(d5, d2 + 1.0, d6, minU, minV);
        instance.addVertexWithUV(d5, d2 + 0.0, d6, minU, maxV);
        instance.addVertexWithUV(d4, d2 + 0.0, d6, maxU, maxV);
        instance.addVertexWithUV(d4, d2 + 1.0, d6, maxU, minV);
        instance.addVertexWithUV(d5, d2 + 1.0, d7, minU, minV);
        instance.addVertexWithUV(d5, d2 + 0.0, d7, minU, maxV);
        instance.addVertexWithUV(d4, d2 + 0.0, d7, maxU, maxV);
        instance.addVertexWithUV(d4, d2 + 1.0, d7, maxU, minV);
        instance.addVertexWithUV(d4, d2 + 1.0, d7, minU, minV);
        instance.addVertexWithUV(d4, d2 + 0.0, d7, minU, maxV);
        instance.addVertexWithUV(d5, d2 + 0.0, d7, maxU, maxV);
        instance.addVertexWithUV(d5, d2 + 1.0, d7, maxU, minV);
    }

    public boolean renderBlockLiquid(Block block, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        int fluidColor = CustomColorizer.getFluidColor(block, this.blockAccess, n, n2, n3);
        float f = (fluidColor >> 16 & 0xFF) / 255.0F;
        float f2 = (fluidColor >> 8 & 0xFF) / 255.0F;
        float f3 = (fluidColor & 0xFF) / 255.0F;
        boolean shouldSideBeRendered = block.shouldSideBeRendered(this.blockAccess, n, n2 + 1, n3, 1);
        boolean shouldSideBeRendered2 = block.shouldSideBeRendered(this.blockAccess, n, n2 - 1, n3, 0);
        boolean[] flags = new boolean[]{
            block.shouldSideBeRendered(this.blockAccess, n, n2, n3 - 1, 2),
            block.shouldSideBeRendered(this.blockAccess, n, n2, n3 + 1, 3),
            block.shouldSideBeRendered(this.blockAccess, n - 1, n2, n3, 4),
            block.shouldSideBeRendered(this.blockAccess, n + 1, n2, n3, 5)
        };
        if (!shouldSideBeRendered && !shouldSideBeRendered2 && !flags[0] && !flags[1] && !flags[2] && !flags[3]) {
            return false;
        }

        boolean flag = false;
        float f4 = 0.5F;
        float f5 = 1.0F;
        float f6 = 0.8F;
        float f7 = 0.6F;
        if (Config.isShaders()) {
            f4 = this.lightLevel05;
            f6 = this.lightLevel08;
            f7 = this.lightLevel06;
        }

        double d = 0.0;
        double d2 = 1.0;
        Material material = block.getMaterial();
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        double liquidHeight = this.getLiquidHeight(n, n2, n3, material);
        double liquidHeight2 = this.getLiquidHeight(n, n2, n3 + 1, material);
        double liquidHeight3 = this.getLiquidHeight(n + 1, n2, n3 + 1, material);
        double liquidHeight4 = this.getLiquidHeight(n + 1, n2, n3, material);
        double d3 = 0.001F;
        if (this.renderAllFaces || shouldSideBeRendered) {
            flag = true;
            IIcon blockIconFromSideAndMetadata = this.getBlockIconFromSideAndMetadata(block, 1, blockMetadata);
            float flowDirection = (float)BlockLiquid.getFlowDirection(this.blockAccess, n, n2, n3, material);
            if (flowDirection > -999.0F) {
                blockIconFromSideAndMetadata = this.getBlockIconFromSideAndMetadata(block, 2, blockMetadata);
            }

            liquidHeight -= d3;
            liquidHeight2 -= d3;
            liquidHeight3 -= d3;
            liquidHeight4 -= d3;
            double d4;
            double d5;
            double d6;
            double d7;
            double d8;
            double d9;
            double d10;
            double d11;
            if (flowDirection < -999.0F) {
                d10 = blockIconFromSideAndMetadata.getInterpolatedU(0.0);
                d7 = blockIconFromSideAndMetadata.getInterpolatedV(0.0);
                d4 = d10;
                d8 = blockIconFromSideAndMetadata.getInterpolatedV(16.0);
                d5 = blockIconFromSideAndMetadata.getInterpolatedU(16.0);
                d9 = d8;
                d6 = d5;
                d11 = d7;
            } else {
                float f8 = MathHelper.sin(flowDirection) * 0.25F;
                float f9 = MathHelper.cos(flowDirection) * 0.25F;
                float f10 = 8.0F;
                d10 = blockIconFromSideAndMetadata.getInterpolatedU(8.0F + (-f9 - f8) * 16.0F);
                d7 = blockIconFromSideAndMetadata.getInterpolatedV(8.0F + (-f9 + f8) * 16.0F);
                d4 = blockIconFromSideAndMetadata.getInterpolatedU(8.0F + (-f9 + f8) * 16.0F);
                d8 = blockIconFromSideAndMetadata.getInterpolatedV(8.0F + (f9 + f8) * 16.0F);
                d5 = blockIconFromSideAndMetadata.getInterpolatedU(8.0F + (f9 + f8) * 16.0F);
                d9 = blockIconFromSideAndMetadata.getInterpolatedV(8.0F + (f9 - f8) * 16.0F);
                d6 = blockIconFromSideAndMetadata.getInterpolatedU(8.0F + (f9 - f8) * 16.0F);
                d11 = blockIconFromSideAndMetadata.getInterpolatedV(8.0F + (-f9 - f8) * 16.0F);
            }

            instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
            instance.setColorOpaque_F(f5 * f, f5 * f2, f5 * f3);
            double d12 = 3.90625E-5;
            instance.addVertexWithUV(n + 0, n2 + liquidHeight, n3 + 0, d10 + d12, d7 + d12);
            instance.addVertexWithUV(n + 0, n2 + liquidHeight2, n3 + 1, d4 + d12, d8 - d12);
            instance.addVertexWithUV(n + 1, n2 + liquidHeight3, n3 + 1, d5 - d12, d9 - d12);
            instance.addVertexWithUV(n + 1, n2 + liquidHeight4, n3 + 0, d6 - d12, d11 + d12);
            instance.addVertexWithUV(n + 0, n2 + liquidHeight, n3 + 0, d10 + d12, d7 + d12);
            instance.addVertexWithUV(n + 1, n2 + liquidHeight4, n3 + 0, d6 - d12, d11 + d12);
            instance.addVertexWithUV(n + 1, n2 + liquidHeight3, n3 + 1, d5 - d12, d9 - d12);
            instance.addVertexWithUV(n + 0, n2 + liquidHeight2, n3 + 1, d4 + d12, d8 - d12);
        }

        if (this.renderAllFaces || shouldSideBeRendered2) {
            instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3));
            instance.setColorOpaque_F(f4 * f, f4 * f2, f4 * f3);
            this.renderFaceYNeg(block, n, n2 + d3, n3, this.getBlockIconFromSide(block, 0));
            flag = true;
        }

        for (int i = 0; i < 4; i++) {
            int n4 = n;
            int n5 = n3;
            if (i == 0) {
                n5 = n3 - 1;
            }

            if (i == 1) {
                n5++;
            }

            if (i == 2) {
                n4 = n - 1;
            }

            if (i == 3) {
                n4++;
            }

            IIcon blockIconFromSideAndMetadata = this.getBlockIconFromSideAndMetadata(block, i + 2, blockMetadata);
            if (this.renderAllFaces || flags[i]) {
                double d4;
                double d5;
                double d6;
                double d7;
                double d8;
                double d9;
                if (i == 0) {
                    d4 = liquidHeight;
                    d5 = liquidHeight4;
                    d6 = n;
                    d8 = n + 1;
                    d7 = n3 + d3;
                    d9 = n3 + d3;
                } else if (i == 1) {
                    d4 = liquidHeight3;
                    d5 = liquidHeight2;
                    d6 = n + 1;
                    d8 = n;
                    d7 = n3 + 1 - d3;
                    d9 = n3 + 1 - d3;
                } else if (i == 2) {
                    d4 = liquidHeight2;
                    d5 = liquidHeight;
                    d6 = n + d3;
                    d8 = n + d3;
                    d7 = n3 + 1;
                    d9 = n3;
                } else {
                    d4 = liquidHeight4;
                    d5 = liquidHeight3;
                    d6 = n + 1 - d3;
                    d8 = n + 1 - d3;
                    d7 = n3;
                    d9 = n3 + 1;
                }

                flag = true;
                float interpolatedU = blockIconFromSideAndMetadata.getInterpolatedU(0.0);
                float interpolatedU2 = blockIconFromSideAndMetadata.getInterpolatedU(8.0);
                float interpolatedV = blockIconFromSideAndMetadata.getInterpolatedV((1.0 - d4) * 16.0 * 0.5);
                float interpolatedV2 = blockIconFromSideAndMetadata.getInterpolatedV((1.0 - d5) * 16.0 * 0.5);
                float interpolatedV3 = blockIconFromSideAndMetadata.getInterpolatedV(8.0);
                instance.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, n4, n2, n5));
                float f8 = 1.0F;
                f8 *= i < 2 ? f6 : f7;
                instance.setColorOpaque_F(f5 * f8 * f, f5 * f8 * f2, f5 * f8 * f3);
                instance.addVertexWithUV(d6, n2 + d4, d7, interpolatedU, interpolatedV);
                instance.addVertexWithUV(d8, n2 + d5, d9, interpolatedU2, interpolatedV2);
                instance.addVertexWithUV(d8, n2 + 0, d9, interpolatedU2, interpolatedV3);
                instance.addVertexWithUV(d6, n2 + 0, d7, interpolatedU, interpolatedV3);
                instance.addVertexWithUV(d6, n2 + 0, d7, interpolatedU, interpolatedV3);
                instance.addVertexWithUV(d8, n2 + 0, d9, interpolatedU2, interpolatedV3);
                instance.addVertexWithUV(d8, n2 + d5, d9, interpolatedU2, interpolatedV2);
                instance.addVertexWithUV(d6, n2 + d4, d7, interpolatedU, interpolatedV);
            }
        }

        this.renderMinY = d;
        this.renderMaxY = d2;
        return flag;
    }

    public float getLiquidHeight(int n, int n2, int n3, Material material) {
        int n4 = 0;
        float f = 0.0F;

        for (int i = 0; i < 4; i++) {
            int n5 = n - (i & 1);
            int n6 = n3 - (i >> 1 & 1);
            if (this.blockAccess.getBlock(n5, n2 + 1, n6).getMaterial() == material) {
                return 1.0F;
            }

            Material material2 = this.blockAccess.getBlock(n5, n2, n6).getMaterial();
            if (material2 == material) {
                int blockMetadata = this.blockAccess.getBlockMetadata(n5, n2, n6);
                if (blockMetadata >= 8 || blockMetadata == 0) {
                    f += BlockLiquid.getLiquidHeightPercent(blockMetadata) * 10.0F;
                    n4 += 10;
                }

                f += BlockLiquid.getLiquidHeightPercent(blockMetadata);
                n4++;
            } else if (!material2.isSolid()) {
                f++;
                n4++;
            }
        }

        return 1.0F - f / n4;
    }

    public void renderBlockSandFalling(Block block, World world, int n, int n2, int n3, int n4) {
        float f = 0.5F;
        float f2 = 1.0F;
        float f3 = 0.8F;
        float f4 = 0.6F;
        if (Config.isShaders()) {
            f = this.lightLevel05;
            f3 = this.lightLevel08;
            f4 = this.lightLevel06;
        }

        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.setBrightness(block.getMixedBrightnessForBlock(world, n, n2, n3));
        instance.setColorOpaque_F(f, f, f);
        this.renderFaceYNeg(block, -0.5, -0.5, -0.5, this.getBlockIconFromSideAndMetadata(block, 0, n4));
        instance.setColorOpaque_F(f2, f2, f2);
        this.renderFaceYPos(block, -0.5, -0.5, -0.5, this.getBlockIconFromSideAndMetadata(block, 1, n4));
        instance.setColorOpaque_F(f3, f3, f3);
        this.renderFaceZNeg(block, -0.5, -0.5, -0.5, this.getBlockIconFromSideAndMetadata(block, 2, n4));
        instance.setColorOpaque_F(f3, f3, f3);
        this.renderFaceZPos(block, -0.5, -0.5, -0.5, this.getBlockIconFromSideAndMetadata(block, 3, n4));
        instance.setColorOpaque_F(f4, f4, f4);
        this.renderFaceXNeg(block, -0.5, -0.5, -0.5, this.getBlockIconFromSideAndMetadata(block, 4, n4));
        instance.setColorOpaque_F(f4, f4, f4);
        this.renderFaceXPos(block, -0.5, -0.5, -0.5, this.getBlockIconFromSideAndMetadata(block, 5, n4));
        instance.draw();
    }

    public boolean renderStandardBlock(Block blockType, int blockX, int blockY, int blockZ) {
        int colorMultiplier = CustomColorizer.getColorMultiplier(blockType, this.blockAccess, blockX, blockY, blockZ);
        float f = (colorMultiplier >> 16 & 0xFF) / 255.0F;
        float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
        float f3 = (colorMultiplier & 0xFF) / 255.0F;
        if (EntityRenderer.anaglyphEnable) {
            float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
            f = f4;
            f2 = f5;
            f3 = f6;
        }

        return Minecraft.isAmbientOcclusionEnabled() && blockType.getLightValue() == 0
            ? (
                this.partialRenderBounds
                    ? this.renderStandardBlockWithAmbientOcclusionPartial(blockType, blockX, blockY, blockZ, f, f2, f3)
                    : this.renderStandardBlockWithAmbientOcclusion(blockType, blockX, blockY, blockZ, f, f2, f3)
            )
            : this.renderStandardBlockWithColorMultiplier(blockType, blockX, blockY, blockZ, f, f2, f3);
    }

    public boolean renderBlockLog(Block block, int n, int n2, int n3) {
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        int n4 = blockMetadata & 12;
        if (n4 == 4) {
            this.uvRotateEast = 2;
            this.uvRotateWest = 1;
            this.uvRotateTop = 1;
            this.uvRotateBottom = 2;
        } else if (n4 == 8) {
            this.uvRotateSouth = 2;
            this.uvRotateNorth = 1;
            this.uvRotateTop = 3;
            this.uvRotateBottom = 3;
        }

        boolean renderStandardBlock = this.renderStandardBlock(block, n, n2, n3);
        this.uvRotateSouth = 0;
        this.uvRotateEast = 0;
        this.uvRotateWest = 0;
        this.uvRotateNorth = 0;
        this.uvRotateTop = 0;
        this.uvRotateBottom = 0;
        return renderStandardBlock;
    }

    public boolean renderBlockQuartz(Block block, int n, int n2, int n3) {
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        if (blockMetadata == 3) {
            this.uvRotateEast = 2;
            this.uvRotateWest = 1;
            this.uvRotateTop = 1;
            this.uvRotateBottom = 2;
        } else if (blockMetadata == 4) {
            this.uvRotateSouth = 2;
            this.uvRotateNorth = 1;
            this.uvRotateTop = 3;
            this.uvRotateBottom = 3;
        }

        boolean renderStandardBlock = this.renderStandardBlock(block, n, n2, n3);
        this.uvRotateSouth = 0;
        this.uvRotateEast = 0;
        this.uvRotateWest = 0;
        this.uvRotateNorth = 0;
        this.uvRotateTop = 0;
        this.uvRotateBottom = 0;
        return renderStandardBlock;
    }

    public boolean renderStandardBlockWithAmbientOcclusion(Block block, int n, int n2, int n3, float f, float f2, float f3) {
        this.enableAO = true;
        boolean defaultTexture = Tessellator.instance.defaultTexture;
        boolean flag = Config.isBetterGrass() && defaultTexture;
        boolean flag2 = block == Blocks.glass;
        boolean flag3 = false;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        float f7 = 0.0F;
        boolean flag4 = true;
        int n4 = -1;
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(983055);
        if (block == Blocks.grass) {
            flag4 = false;
        } else if (this.hasOverrideBlockTexture()) {
            flag4 = false;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2 - 1, n3, 0)) {
            if (this.renderMinY <= 0.0) {
                n2--;
            }

            this.aoBrightnessXYNN = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3);
            this.aoBrightnessYZNN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 - 1);
            this.aoBrightnessYZNP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 + 1);
            this.aoBrightnessXYPN = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3);
            this.aoLightValueScratchXYNN = this.getAmbientOcclusionLightValue(n - 1, n2, n3);
            this.aoLightValueScratchYZNN = this.getAmbientOcclusionLightValue(n, n2, n3 - 1);
            this.aoLightValueScratchYZNP = this.getAmbientOcclusionLightValue(n, n2, n3 + 1);
            this.aoLightValueScratchXYPN = this.getAmbientOcclusionLightValue(n + 1, n2, n3);
            boolean isTranslucent = this.blockAccess.getBlock(n + 1, n2 - 1, n3).isTranslucent();
            boolean isTranslucent2 = this.blockAccess.getBlock(n - 1, n2 - 1, n3).isTranslucent();
            boolean isTranslucent3 = this.blockAccess.getBlock(n, n2 - 1, n3 + 1).isTranslucent();
            boolean isTranslucent4 = this.blockAccess.getBlock(n, n2 - 1, n3 - 1).isTranslucent();
            if (!isTranslucent4 && !isTranslucent2) {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXYNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXYNN;
            } else {
                this.aoLightValueScratchXYZNNN = this.getAmbientOcclusionLightValue(n - 1, n2, n3 - 1);
                this.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3 - 1);
            }

            if (!isTranslucent3 && !isTranslucent2) {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXYNN;
                this.aoBrightnessXYZNNP = this.aoBrightnessXYNN;
            } else {
                this.aoLightValueScratchXYZNNP = this.getAmbientOcclusionLightValue(n - 1, n2, n3 + 1);
                this.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3 + 1);
            }

            if (!isTranslucent4 && !isTranslucent) {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXYPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXYPN;
            } else {
                this.aoLightValueScratchXYZPNN = this.getAmbientOcclusionLightValue(n + 1, n2, n3 - 1);
                this.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3 - 1);
            }

            if (!isTranslucent3 && !isTranslucent) {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXYPN;
                this.aoBrightnessXYZPNP = this.aoBrightnessXYPN;
            } else {
                this.aoLightValueScratchXYZPNP = this.getAmbientOcclusionLightValue(n + 1, n2, n3 + 1);
                this.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3 + 1);
            }

            if (this.renderMinY <= 0.0) {
                n2++;
            }

            if (n4 < 0) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
            }

            int n5 = n4;
            if (this.renderMinY <= 0.0 || !this.blockAccess.getBlock(n, n2 - 1, n3).isOpaqueCube()) {
                n5 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3);
            }

            float ambientOcclusionLightValue = this.getAmbientOcclusionLightValue(n, n2 - 1, n3);
            f4 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXYNN + this.aoLightValueScratchYZNP + ambientOcclusionLightValue) / 4.0F;
            f7 = (this.aoLightValueScratchYZNP + ambientOcclusionLightValue + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXYPN) / 4.0F;
            f6 = (ambientOcclusionLightValue + this.aoLightValueScratchYZNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNN) / 4.0F;
            f5 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNN + ambientOcclusionLightValue + this.aoLightValueScratchYZNN) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXYNN, this.aoBrightnessYZNP, n5);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXYPN, n5);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYPN, this.aoBrightnessXYZPNN, n5);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNN, this.aoBrightnessYZNN, n5);
            if (flag2) {
                f5 = ambientOcclusionLightValue;
                f6 = ambientOcclusionLightValue;
                f7 = ambientOcclusionLightValue;
                f4 = ambientOcclusionLightValue;
                this.brightnessTopLeft = this.brightnessTopRight = this.brightnessBottomRight = this.brightnessBottomLeft = n5;
            }

            if (flag4) {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = f * this.lightLevel05;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = f2 * this.lightLevel05;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = f3 * this.lightLevel05;
            } else {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = this.lightLevel05;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = this.lightLevel05;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = this.lightLevel05;
            }

            this.colorRedTopLeft *= f4;
            this.colorGreenTopLeft *= f4;
            this.colorBlueTopLeft *= f4;
            this.colorRedBottomLeft *= f5;
            this.colorGreenBottomLeft *= f5;
            this.colorBlueBottomLeft *= f5;
            this.colorRedBottomRight *= f6;
            this.colorGreenBottomRight *= f6;
            this.colorBlueBottomRight *= f6;
            this.colorRedTopRight *= f7;
            this.colorGreenTopRight *= f7;
            this.colorBlueTopRight *= f7;
            this.renderFaceYNeg(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 0));
            flag3 = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2 + 1, n3, 1)) {
            if (this.renderMaxY >= 1.0) {
                n2++;
            }

            this.aoBrightnessXYNP = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3);
            this.aoBrightnessXYPP = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3);
            this.aoBrightnessYZPN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 - 1);
            this.aoBrightnessYZPP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 + 1);
            this.aoLightValueScratchXYNP = this.getAmbientOcclusionLightValue(n - 1, n2, n3);
            this.aoLightValueScratchXYPP = this.getAmbientOcclusionLightValue(n + 1, n2, n3);
            this.aoLightValueScratchYZPN = this.getAmbientOcclusionLightValue(n, n2, n3 - 1);
            this.aoLightValueScratchYZPP = this.getAmbientOcclusionLightValue(n, n2, n3 + 1);
            boolean isTranslucent = this.blockAccess.getBlock(n + 1, n2 + 1, n3).isTranslucent();
            boolean isTranslucent2 = this.blockAccess.getBlock(n - 1, n2 + 1, n3).isTranslucent();
            boolean isTranslucent3 = this.blockAccess.getBlock(n, n2 + 1, n3 + 1).isTranslucent();
            boolean isTranslucent4 = this.blockAccess.getBlock(n, n2 + 1, n3 - 1).isTranslucent();
            if (!isTranslucent4 && !isTranslucent2) {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXYNP;
                this.aoBrightnessXYZNPN = this.aoBrightnessXYNP;
            } else {
                this.aoLightValueScratchXYZNPN = this.getAmbientOcclusionLightValue(n - 1, n2, n3 - 1);
                this.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3 - 1);
            }

            if (!isTranslucent4 && !isTranslucent) {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXYPP;
                this.aoBrightnessXYZPPN = this.aoBrightnessXYPP;
            } else {
                this.aoLightValueScratchXYZPPN = this.getAmbientOcclusionLightValue(n + 1, n2, n3 - 1);
                this.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3 - 1);
            }

            if (!isTranslucent3 && !isTranslucent2) {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXYNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXYNP;
            } else {
                this.aoLightValueScratchXYZNPP = this.getAmbientOcclusionLightValue(n - 1, n2, n3 + 1);
                this.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3 + 1);
            }

            if (!isTranslucent3 && !isTranslucent) {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXYPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXYPP;
            } else {
                this.aoLightValueScratchXYZPPP = this.getAmbientOcclusionLightValue(n + 1, n2, n3 + 1);
                this.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3 + 1);
            }

            if (this.renderMaxY >= 1.0) {
                n2--;
            }

            if (n4 < 0) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
            }

            int n5 = n4;
            if (this.renderMaxY >= 1.0 || !this.blockAccess.getBlock(n, n2 + 1, n3).isOpaqueCube()) {
                n5 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3);
            }

            float ambientOcclusionLightValue = this.getAmbientOcclusionLightValue(n, n2 + 1, n3);
            f7 = (this.aoLightValueScratchXYZNPP + this.aoLightValueScratchXYNP + this.aoLightValueScratchYZPP + ambientOcclusionLightValue) / 4.0F;
            f4 = (this.aoLightValueScratchYZPP + ambientOcclusionLightValue + this.aoLightValueScratchXYZPPP + this.aoLightValueScratchXYPP) / 4.0F;
            f5 = (ambientOcclusionLightValue + this.aoLightValueScratchYZPN + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPN) / 4.0F;
            f6 = (this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPN + ambientOcclusionLightValue + this.aoLightValueScratchYZPN) / 4.0F;
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNPP, this.aoBrightnessXYNP, this.aoBrightnessYZPP, n5);
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXYZPPP, this.aoBrightnessXYPP, n5);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXYPP, this.aoBrightnessXYZPPN, n5);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYNP, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, n5);
            if (flag2) {
                f5 = ambientOcclusionLightValue;
                f6 = ambientOcclusionLightValue;
                f7 = ambientOcclusionLightValue;
                f4 = ambientOcclusionLightValue;
                this.brightnessTopLeft = this.brightnessTopRight = this.brightnessBottomRight = this.brightnessBottomLeft = n5;
            }

            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = f;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = f2;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = f3;
            this.colorRedTopLeft *= f4;
            this.colorGreenTopLeft *= f4;
            this.colorBlueTopLeft *= f4;
            this.colorRedBottomLeft *= f5;
            this.colorGreenBottomLeft *= f5;
            this.colorBlueBottomLeft *= f5;
            this.colorRedBottomRight *= f6;
            this.colorGreenBottomRight *= f6;
            this.colorBlueBottomRight *= f6;
            this.colorRedTopRight *= f7;
            this.colorGreenTopRight *= f7;
            this.colorBlueTopRight *= f7;
            this.renderFaceYPos(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 1));
            flag3 = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2, n3 - 1, 2)) {
            if (this.renderMinZ <= 0.0) {
                n3--;
            }

            this.aoLightValueScratchXZNN = this.getAmbientOcclusionLightValue(n - 1, n2, n3);
            this.aoLightValueScratchYZNN = this.getAmbientOcclusionLightValue(n, n2 - 1, n3);
            this.aoLightValueScratchYZPN = this.getAmbientOcclusionLightValue(n, n2 + 1, n3);
            this.aoLightValueScratchXZPN = this.getAmbientOcclusionLightValue(n + 1, n2, n3);
            this.aoBrightnessXZNN = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3);
            this.aoBrightnessYZNN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3);
            this.aoBrightnessYZPN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3);
            this.aoBrightnessXZPN = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3);
            boolean isTranslucent = this.blockAccess.getBlock(n + 1, n2, n3 - 1).isTranslucent();
            boolean isTranslucent2 = this.blockAccess.getBlock(n - 1, n2, n3 - 1).isTranslucent();
            boolean isTranslucent3 = this.blockAccess.getBlock(n, n2 + 1, n3 - 1).isTranslucent();
            boolean isTranslucent4 = this.blockAccess.getBlock(n, n2 - 1, n3 - 1).isTranslucent();
            if (!isTranslucent2 && !isTranslucent4) {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
            } else {
                this.aoLightValueScratchXYZNNN = this.getAmbientOcclusionLightValue(n - 1, n2 - 1, n3);
                this.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2 - 1, n3);
            }

            if (!isTranslucent2 && !isTranslucent3) {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
            } else {
                this.aoLightValueScratchXYZNPN = this.getAmbientOcclusionLightValue(n - 1, n2 + 1, n3);
                this.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2 + 1, n3);
            }

            if (!isTranslucent && !isTranslucent4) {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
            } else {
                this.aoLightValueScratchXYZPNN = this.getAmbientOcclusionLightValue(n + 1, n2 - 1, n3);
                this.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2 - 1, n3);
            }

            if (!isTranslucent && !isTranslucent3) {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
            } else {
                this.aoLightValueScratchXYZPPN = this.getAmbientOcclusionLightValue(n + 1, n2 + 1, n3);
                this.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2 + 1, n3);
            }

            if (this.renderMinZ <= 0.0) {
                n3++;
            }

            if (n4 < 0) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
            }

            int n5 = n4;
            if (this.renderMinZ <= 0.0 || !this.blockAccess.getBlock(n, n2, n3 - 1).isOpaqueCube()) {
                n5 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 - 1);
            }

            float ambientOcclusionLightValue = this.getAmbientOcclusionLightValue(n, n2, n3 - 1);
            f4 = (this.aoLightValueScratchXZNN + this.aoLightValueScratchXYZNPN + ambientOcclusionLightValue + this.aoLightValueScratchYZPN) / 4.0F;
            f5 = (ambientOcclusionLightValue + this.aoLightValueScratchYZPN + this.aoLightValueScratchXZPN + this.aoLightValueScratchXYZPPN) / 4.0F;
            f6 = (this.aoLightValueScratchYZNN + ambientOcclusionLightValue + this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXZPN) / 4.0F;
            f7 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXZNN + this.aoLightValueScratchYZNN + ambientOcclusionLightValue) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, n5);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, n5);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYZPNN, this.aoBrightnessXZPN, n5);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXZNN, this.aoBrightnessYZNN, n5);
            if (flag2) {
                f5 = ambientOcclusionLightValue;
                f6 = ambientOcclusionLightValue;
                f7 = ambientOcclusionLightValue;
                f4 = ambientOcclusionLightValue;
                this.brightnessTopLeft = this.brightnessTopRight = this.brightnessBottomRight = this.brightnessBottomLeft = n5;
            }

            if (flag4) {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = f * this.lightLevel08;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = f2 * this.lightLevel08;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = f3 * this.lightLevel08;
            } else {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = this.lightLevel08;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = this.lightLevel08;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = this.lightLevel08;
            }

            this.colorRedTopLeft *= f4;
            this.colorGreenTopLeft *= f4;
            this.colorBlueTopLeft *= f4;
            this.colorRedBottomLeft *= f5;
            this.colorGreenBottomLeft *= f5;
            this.colorBlueBottomLeft *= f5;
            this.colorRedBottomRight *= f6;
            this.colorGreenBottomRight *= f6;
            this.colorBlueBottomRight *= f6;
            this.colorRedTopRight *= f7;
            this.colorGreenTopRight *= f7;
            this.colorBlueTopRight *= f7;
            IIcon blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 2);
            if (flag) {
                blockIcon = this.fixAoSideGrassTexture(blockIcon, n, n2, n3, 2, f, f2, f3);
            }

            this.renderFaceZNeg(block, n, n2, n3, blockIcon);
            if (defaultTexture && fancyGrass && blockIcon == TextureUtils.iconGrassSide && !this.hasOverrideBlockTexture()) {
                this.colorRedTopLeft *= f;
                this.colorRedBottomLeft *= f;
                this.colorRedBottomRight *= f;
                this.colorRedTopRight *= f;
                this.colorGreenTopLeft *= f2;
                this.colorGreenBottomLeft *= f2;
                this.colorGreenBottomRight *= f2;
                this.colorGreenTopRight *= f2;
                this.colorBlueTopLeft *= f3;
                this.colorBlueBottomLeft *= f3;
                this.colorBlueBottomRight *= f3;
                this.colorBlueTopRight *= f3;
                this.renderFaceZNeg(block, n, n2, n3, BlockGrass.getIconSideOverlay());
            }

            flag3 = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2, n3 + 1, 3)) {
            if (this.renderMaxZ >= 1.0) {
                n3++;
            }

            this.aoLightValueScratchXZNP = this.getAmbientOcclusionLightValue(n - 1, n2, n3);
            this.aoLightValueScratchXZPP = this.getAmbientOcclusionLightValue(n + 1, n2, n3);
            this.aoLightValueScratchYZNP = this.getAmbientOcclusionLightValue(n, n2 - 1, n3);
            this.aoLightValueScratchYZPP = this.getAmbientOcclusionLightValue(n, n2 + 1, n3);
            this.aoBrightnessXZNP = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3);
            this.aoBrightnessXZPP = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3);
            this.aoBrightnessYZNP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3);
            this.aoBrightnessYZPP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3);
            boolean isTranslucent = this.blockAccess.getBlock(n + 1, n2, n3 + 1).isTranslucent();
            boolean isTranslucent2 = this.blockAccess.getBlock(n - 1, n2, n3 + 1).isTranslucent();
            boolean isTranslucent3 = this.blockAccess.getBlock(n, n2 + 1, n3 + 1).isTranslucent();
            boolean isTranslucent4 = this.blockAccess.getBlock(n, n2 - 1, n3 + 1).isTranslucent();
            if (!isTranslucent2 && !isTranslucent4) {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
            } else {
                this.aoLightValueScratchXYZNNP = this.getAmbientOcclusionLightValue(n - 1, n2 - 1, n3);
                this.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2 - 1, n3);
            }

            if (!isTranslucent2 && !isTranslucent3) {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
            } else {
                this.aoLightValueScratchXYZNPP = this.getAmbientOcclusionLightValue(n - 1, n2 + 1, n3);
                this.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2 + 1, n3);
            }

            if (!isTranslucent && !isTranslucent4) {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
            } else {
                this.aoLightValueScratchXYZPNP = this.getAmbientOcclusionLightValue(n + 1, n2 - 1, n3);
                this.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2 - 1, n3);
            }

            if (!isTranslucent && !isTranslucent3) {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
            } else {
                this.aoLightValueScratchXYZPPP = this.getAmbientOcclusionLightValue(n + 1, n2 + 1, n3);
                this.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2 + 1, n3);
            }

            if (this.renderMaxZ >= 1.0) {
                n3--;
            }

            if (n4 < 0) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
            }

            int n5 = n4;
            if (this.renderMaxZ >= 1.0 || !this.blockAccess.getBlock(n, n2, n3 + 1).isOpaqueCube()) {
                n5 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 + 1);
            }

            float ambientOcclusionLightValue = this.getAmbientOcclusionLightValue(n, n2, n3 + 1);
            f4 = (this.aoLightValueScratchXZNP + this.aoLightValueScratchXYZNPP + ambientOcclusionLightValue + this.aoLightValueScratchYZPP) / 4.0F;
            f7 = (ambientOcclusionLightValue + this.aoLightValueScratchYZPP + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYZPPP) / 4.0F;
            f6 = (this.aoLightValueScratchYZNP + ambientOcclusionLightValue + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXZPP) / 4.0F;
            f5 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXZNP + this.aoLightValueScratchYZNP + ambientOcclusionLightValue) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYZNPP, this.aoBrightnessYZPP, n5);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXZPP, this.aoBrightnessXYZPPP, n5);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, n5);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, this.aoBrightnessYZNP, n5);
            if (flag2) {
                f5 = ambientOcclusionLightValue;
                f6 = ambientOcclusionLightValue;
                f7 = ambientOcclusionLightValue;
                f4 = ambientOcclusionLightValue;
                this.brightnessTopLeft = this.brightnessTopRight = this.brightnessBottomRight = this.brightnessBottomLeft = n5;
            }

            if (flag4) {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = f * this.lightLevel08;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = f2 * this.lightLevel08;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = f3 * this.lightLevel08;
            } else {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = this.lightLevel08;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = this.lightLevel08;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = this.lightLevel08;
            }

            this.colorRedTopLeft *= f4;
            this.colorGreenTopLeft *= f4;
            this.colorBlueTopLeft *= f4;
            this.colorRedBottomLeft *= f5;
            this.colorGreenBottomLeft *= f5;
            this.colorBlueBottomLeft *= f5;
            this.colorRedBottomRight *= f6;
            this.colorGreenBottomRight *= f6;
            this.colorBlueBottomRight *= f6;
            this.colorRedTopRight *= f7;
            this.colorGreenTopRight *= f7;
            this.colorBlueTopRight *= f7;
            IIcon blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 3);
            if (flag) {
                blockIcon = this.fixAoSideGrassTexture(blockIcon, n, n2, n3, 3, f, f2, f3);
            }

            this.renderFaceZPos(block, n, n2, n3, blockIcon);
            if (defaultTexture && fancyGrass && blockIcon == TextureUtils.iconGrassSide && !this.hasOverrideBlockTexture()) {
                this.colorRedTopLeft *= f;
                this.colorRedBottomLeft *= f;
                this.colorRedBottomRight *= f;
                this.colorRedTopRight *= f;
                this.colorGreenTopLeft *= f2;
                this.colorGreenBottomLeft *= f2;
                this.colorGreenBottomRight *= f2;
                this.colorGreenTopRight *= f2;
                this.colorBlueTopLeft *= f3;
                this.colorBlueBottomLeft *= f3;
                this.colorBlueBottomRight *= f3;
                this.colorBlueTopRight *= f3;
                this.renderFaceZPos(block, n, n2, n3, BlockGrass.getIconSideOverlay());
            }

            flag3 = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n - 1, n2, n3, 4)) {
            if (this.renderMinX <= 0.0) {
                n--;
            }

            this.aoLightValueScratchXYNN = this.getAmbientOcclusionLightValue(n, n2 - 1, n3);
            this.aoLightValueScratchXZNN = this.getAmbientOcclusionLightValue(n, n2, n3 - 1);
            this.aoLightValueScratchXZNP = this.getAmbientOcclusionLightValue(n, n2, n3 + 1);
            this.aoLightValueScratchXYNP = this.getAmbientOcclusionLightValue(n, n2 + 1, n3);
            this.aoBrightnessXYNN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3);
            this.aoBrightnessXZNN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 - 1);
            this.aoBrightnessXZNP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 + 1);
            this.aoBrightnessXYNP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3);
            boolean isTranslucent = this.blockAccess.getBlock(n - 1, n2 + 1, n3).isTranslucent();
            boolean isTranslucent2 = this.blockAccess.getBlock(n - 1, n2 - 1, n3).isTranslucent();
            boolean isTranslucent3 = this.blockAccess.getBlock(n - 1, n2, n3 - 1).isTranslucent();
            boolean isTranslucent4 = this.blockAccess.getBlock(n - 1, n2, n3 + 1).isTranslucent();
            if (!isTranslucent3 && !isTranslucent2) {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
            } else {
                this.aoLightValueScratchXYZNNN = this.getAmbientOcclusionLightValue(n, n2 - 1, n3 - 1);
                this.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3 - 1);
            }

            if (!isTranslucent4 && !isTranslucent2) {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
            } else {
                this.aoLightValueScratchXYZNNP = this.getAmbientOcclusionLightValue(n, n2 - 1, n3 + 1);
                this.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3 + 1);
            }

            if (!isTranslucent3 && !isTranslucent) {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
            } else {
                this.aoLightValueScratchXYZNPN = this.getAmbientOcclusionLightValue(n, n2 + 1, n3 - 1);
                this.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3 - 1);
            }

            if (!isTranslucent4 && !isTranslucent) {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
            } else {
                this.aoLightValueScratchXYZNPP = this.getAmbientOcclusionLightValue(n, n2 + 1, n3 + 1);
                this.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3 + 1);
            }

            if (this.renderMinX <= 0.0) {
                n++;
            }

            if (n4 < 0) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
            }

            int n5 = n4;
            if (this.renderMinX <= 0.0 || !this.blockAccess.getBlock(n - 1, n2, n3).isOpaqueCube()) {
                n5 = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3);
            }

            float ambientOcclusionLightValue = this.getAmbientOcclusionLightValue(n - 1, n2, n3);
            f7 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNP + ambientOcclusionLightValue + this.aoLightValueScratchXZNP) / 4.0F;
            f4 = (ambientOcclusionLightValue + this.aoLightValueScratchXZNP + this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPP) / 4.0F;
            f5 = (this.aoLightValueScratchXZNN + ambientOcclusionLightValue + this.aoLightValueScratchXYZNPN + this.aoLightValueScratchXYNP) / 4.0F;
            f6 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXYNN + this.aoLightValueScratchXZNN + ambientOcclusionLightValue) / 4.0F;
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, n5);
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYNP, this.aoBrightnessXYZNPP, n5);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessXYNP, n5);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXYNN, this.aoBrightnessXZNN, n5);
            if (flag2) {
                f5 = ambientOcclusionLightValue;
                f6 = ambientOcclusionLightValue;
                f7 = ambientOcclusionLightValue;
                f4 = ambientOcclusionLightValue;
                this.brightnessTopLeft = this.brightnessTopRight = this.brightnessBottomRight = this.brightnessBottomLeft = n5;
            }

            if (flag4) {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = f * this.lightLevel06;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = f2 * this.lightLevel06;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = f3 * this.lightLevel06;
            } else {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = this.lightLevel06;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = this.lightLevel06;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = this.lightLevel06;
            }

            this.colorRedTopLeft *= f4;
            this.colorGreenTopLeft *= f4;
            this.colorBlueTopLeft *= f4;
            this.colorRedBottomLeft *= f5;
            this.colorGreenBottomLeft *= f5;
            this.colorBlueBottomLeft *= f5;
            this.colorRedBottomRight *= f6;
            this.colorGreenBottomRight *= f6;
            this.colorBlueBottomRight *= f6;
            this.colorRedTopRight *= f7;
            this.colorGreenTopRight *= f7;
            this.colorBlueTopRight *= f7;
            IIcon blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 4);
            if (flag) {
                blockIcon = this.fixAoSideGrassTexture(blockIcon, n, n2, n3, 4, f, f2, f3);
            }

            this.renderFaceXNeg(block, n, n2, n3, blockIcon);
            if (defaultTexture && fancyGrass && blockIcon == TextureUtils.iconGrassSide && !this.hasOverrideBlockTexture()) {
                this.colorRedTopLeft *= f;
                this.colorRedBottomLeft *= f;
                this.colorRedBottomRight *= f;
                this.colorRedTopRight *= f;
                this.colorGreenTopLeft *= f2;
                this.colorGreenBottomLeft *= f2;
                this.colorGreenBottomRight *= f2;
                this.colorGreenTopRight *= f2;
                this.colorBlueTopLeft *= f3;
                this.colorBlueBottomLeft *= f3;
                this.colorBlueBottomRight *= f3;
                this.colorBlueTopRight *= f3;
                this.renderFaceXNeg(block, n, n2, n3, BlockGrass.getIconSideOverlay());
            }

            flag3 = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n + 1, n2, n3, 5)) {
            if (this.renderMaxX >= 1.0) {
                n++;
            }

            this.aoLightValueScratchXYPN = this.getAmbientOcclusionLightValue(n, n2 - 1, n3);
            this.aoLightValueScratchXZPN = this.getAmbientOcclusionLightValue(n, n2, n3 - 1);
            this.aoLightValueScratchXZPP = this.getAmbientOcclusionLightValue(n, n2, n3 + 1);
            this.aoLightValueScratchXYPP = this.getAmbientOcclusionLightValue(n, n2 + 1, n3);
            this.aoBrightnessXYPN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3);
            this.aoBrightnessXZPN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 - 1);
            this.aoBrightnessXZPP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 + 1);
            this.aoBrightnessXYPP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3);
            boolean isTranslucent = this.blockAccess.getBlock(n + 1, n2 + 1, n3).isTranslucent();
            boolean isTranslucent2 = this.blockAccess.getBlock(n + 1, n2 - 1, n3).isTranslucent();
            boolean isTranslucent3 = this.blockAccess.getBlock(n + 1, n2, n3 + 1).isTranslucent();
            boolean isTranslucent4 = this.blockAccess.getBlock(n + 1, n2, n3 - 1).isTranslucent();
            if (!isTranslucent2 && !isTranslucent4) {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
            } else {
                this.aoLightValueScratchXYZPNN = this.getAmbientOcclusionLightValue(n, n2 - 1, n3 - 1);
                this.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3 - 1);
            }

            if (!isTranslucent2 && !isTranslucent3) {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
            } else {
                this.aoLightValueScratchXYZPNP = this.getAmbientOcclusionLightValue(n, n2 - 1, n3 + 1);
                this.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3 + 1);
            }

            if (!isTranslucent && !isTranslucent4) {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
            } else {
                this.aoLightValueScratchXYZPPN = this.getAmbientOcclusionLightValue(n, n2 + 1, n3 - 1);
                this.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3 - 1);
            }

            if (!isTranslucent && !isTranslucent3) {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
            } else {
                this.aoLightValueScratchXYZPPP = this.getAmbientOcclusionLightValue(n, n2 + 1, n3 + 1);
                this.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3 + 1);
            }

            if (this.renderMaxX >= 1.0) {
                n--;
            }

            if (n4 < 0) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
            }

            int n5 = n4;
            if (this.renderMaxX >= 1.0 || !this.blockAccess.getBlock(n + 1, n2, n3).isOpaqueCube()) {
                n5 = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3);
            }

            float ambientOcclusionLightValue = this.getAmbientOcclusionLightValue(n + 1, n2, n3);
            f4 = (this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNP + ambientOcclusionLightValue + this.aoLightValueScratchXZPP) / 4.0F;
            f5 = (this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXZPN + ambientOcclusionLightValue) / 4.0F;
            f6 = (this.aoLightValueScratchXZPN + ambientOcclusionLightValue + this.aoLightValueScratchXYZPPN + this.aoLightValueScratchXYPP) / 4.0F;
            f7 = (ambientOcclusionLightValue + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPP) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYPN, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, n5);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXZPP, this.aoBrightnessXYPP, this.aoBrightnessXYZPPP, n5);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, this.aoBrightnessXYPP, n5);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZPNN, this.aoBrightnessXYPN, this.aoBrightnessXZPN, n5);
            if (flag2) {
                f5 = ambientOcclusionLightValue;
                f6 = ambientOcclusionLightValue;
                f7 = ambientOcclusionLightValue;
                f4 = ambientOcclusionLightValue;
                this.brightnessTopLeft = this.brightnessTopRight = this.brightnessBottomRight = this.brightnessBottomLeft = n5;
            }

            if (flag4) {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = f * this.lightLevel06;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = f2 * this.lightLevel06;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = f3 * this.lightLevel06;
            } else {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = this.lightLevel06;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = this.lightLevel06;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = this.lightLevel06;
            }

            this.colorRedTopLeft *= f4;
            this.colorGreenTopLeft *= f4;
            this.colorBlueTopLeft *= f4;
            this.colorRedBottomLeft *= f5;
            this.colorGreenBottomLeft *= f5;
            this.colorBlueBottomLeft *= f5;
            this.colorRedBottomRight *= f6;
            this.colorGreenBottomRight *= f6;
            this.colorBlueBottomRight *= f6;
            this.colorRedTopRight *= f7;
            this.colorGreenTopRight *= f7;
            this.colorBlueTopRight *= f7;
            IIcon blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 5);
            if (flag) {
                blockIcon = this.fixAoSideGrassTexture(blockIcon, n, n2, n3, 5, f, f2, f3);
            }

            this.renderFaceXPos(block, n, n2, n3, blockIcon);
            if (defaultTexture && fancyGrass && blockIcon == TextureUtils.iconGrassSide && !this.hasOverrideBlockTexture()) {
                this.colorRedTopLeft *= f;
                this.colorRedBottomLeft *= f;
                this.colorRedBottomRight *= f;
                this.colorRedTopRight *= f;
                this.colorGreenTopLeft *= f2;
                this.colorGreenBottomLeft *= f2;
                this.colorGreenBottomRight *= f2;
                this.colorGreenTopRight *= f2;
                this.colorBlueTopLeft *= f3;
                this.colorBlueBottomLeft *= f3;
                this.colorBlueBottomRight *= f3;
                this.colorBlueTopRight *= f3;
                this.renderFaceXPos(block, n, n2, n3, BlockGrass.getIconSideOverlay());
            }

            flag3 = true;
        }

        this.enableAO = false;
        return flag3;
    }

    public boolean renderStandardBlockWithAmbientOcclusionPartial(Block block, int n, int n2, int n3, float f, float f2, float f3) {
        this.enableAO = true;
        boolean flag = false;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        float f7 = 0.0F;
        boolean flag2 = true;
        int mixedBrightnessForBlock = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(983055);
        if (block == Blocks.grass) {
            flag2 = false;
        } else if (this.hasOverrideBlockTexture()) {
            flag2 = false;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2 - 1, n3, 0)) {
            if (this.renderMinY <= 0.0) {
                n2--;
            }

            this.aoBrightnessXYNN = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3);
            this.aoBrightnessYZNN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 - 1);
            this.aoBrightnessYZNP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 + 1);
            this.aoBrightnessXYPN = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3);
            this.aoLightValueScratchXYNN = this.getAmbientOcclusionLightValue(n - 1, n2, n3);
            this.aoLightValueScratchYZNN = this.getAmbientOcclusionLightValue(n, n2, n3 - 1);
            this.aoLightValueScratchYZNP = this.getAmbientOcclusionLightValue(n, n2, n3 + 1);
            this.aoLightValueScratchXYPN = this.getAmbientOcclusionLightValue(n + 1, n2, n3);
            boolean isTranslucent = this.blockAccess.getBlock(n + 1, n2 - 1, n3).isTranslucent();
            boolean isTranslucent2 = this.blockAccess.getBlock(n - 1, n2 - 1, n3).isTranslucent();
            boolean isTranslucent3 = this.blockAccess.getBlock(n, n2 - 1, n3 + 1).isTranslucent();
            boolean isTranslucent4 = this.blockAccess.getBlock(n, n2 - 1, n3 - 1).isTranslucent();
            if (!isTranslucent4 && !isTranslucent2) {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXYNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXYNN;
            } else {
                this.aoLightValueScratchXYZNNN = this.getAmbientOcclusionLightValue(n - 1, n2, n3 - 1);
                this.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3 - 1);
            }

            if (!isTranslucent3 && !isTranslucent2) {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXYNN;
                this.aoBrightnessXYZNNP = this.aoBrightnessXYNN;
            } else {
                this.aoLightValueScratchXYZNNP = this.getAmbientOcclusionLightValue(n - 1, n2, n3 + 1);
                this.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3 + 1);
            }

            if (!isTranslucent4 && !isTranslucent) {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXYPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXYPN;
            } else {
                this.aoLightValueScratchXYZPNN = this.getAmbientOcclusionLightValue(n + 1, n2, n3 - 1);
                this.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3 - 1);
            }

            if (!isTranslucent3 && !isTranslucent) {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXYPN;
                this.aoBrightnessXYZPNP = this.aoBrightnessXYPN;
            } else {
                this.aoLightValueScratchXYZPNP = this.getAmbientOcclusionLightValue(n + 1, n2, n3 + 1);
                this.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3 + 1);
            }

            if (this.renderMinY <= 0.0) {
                n2++;
            }

            int n4 = mixedBrightnessForBlock;
            if (this.renderMinY <= 0.0 || !this.blockAccess.getBlock(n, n2 - 1, n3).isOpaqueCube()) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3);
            }

            float ambientOcclusionLightValue = this.getAmbientOcclusionLightValue(n, n2 - 1, n3);
            f4 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXYNN + this.aoLightValueScratchYZNP + ambientOcclusionLightValue) / 4.0F;
            f7 = (this.aoLightValueScratchYZNP + ambientOcclusionLightValue + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXYPN) / 4.0F;
            f6 = (ambientOcclusionLightValue + this.aoLightValueScratchYZNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNN) / 4.0F;
            f5 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNN + ambientOcclusionLightValue + this.aoLightValueScratchYZNN) / 4.0F;
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXYNN, this.aoBrightnessYZNP, n4);
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXYPN, n4);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYPN, this.aoBrightnessXYZPNN, n4);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNN, this.aoBrightnessYZNN, n4);
            if (flag2) {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = f * this.lightLevel05;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = f2 * this.lightLevel05;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = f3 * this.lightLevel05;
            } else {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = this.lightLevel05;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = this.lightLevel05;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = this.lightLevel05;
            }

            this.colorRedTopLeft *= f4;
            this.colorGreenTopLeft *= f4;
            this.colorBlueTopLeft *= f4;
            this.colorRedBottomLeft *= f5;
            this.colorGreenBottomLeft *= f5;
            this.colorBlueBottomLeft *= f5;
            this.colorRedBottomRight *= f6;
            this.colorGreenBottomRight *= f6;
            this.colorBlueBottomRight *= f6;
            this.colorRedTopRight *= f7;
            this.colorGreenTopRight *= f7;
            this.colorBlueTopRight *= f7;
            this.renderFaceYNeg(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 0));
            flag = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2 + 1, n3, 1)) {
            if (this.renderMaxY >= 1.0) {
                n2++;
            }

            this.aoBrightnessXYNP = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3);
            this.aoBrightnessXYPP = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3);
            this.aoBrightnessYZPN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 - 1);
            this.aoBrightnessYZPP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 + 1);
            this.aoLightValueScratchXYNP = this.getAmbientOcclusionLightValue(n - 1, n2, n3);
            this.aoLightValueScratchXYPP = this.getAmbientOcclusionLightValue(n + 1, n2, n3);
            this.aoLightValueScratchYZPN = this.getAmbientOcclusionLightValue(n, n2, n3 - 1);
            this.aoLightValueScratchYZPP = this.getAmbientOcclusionLightValue(n, n2, n3 + 1);
            boolean isTranslucent = this.blockAccess.getBlock(n + 1, n2 + 1, n3).isTranslucent();
            boolean isTranslucent2 = this.blockAccess.getBlock(n - 1, n2 + 1, n3).isTranslucent();
            boolean isTranslucent3 = this.blockAccess.getBlock(n, n2 + 1, n3 + 1).isTranslucent();
            boolean isTranslucent4 = this.blockAccess.getBlock(n, n2 + 1, n3 - 1).isTranslucent();
            if (!isTranslucent4 && !isTranslucent2) {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXYNP;
                this.aoBrightnessXYZNPN = this.aoBrightnessXYNP;
            } else {
                this.aoLightValueScratchXYZNPN = this.getAmbientOcclusionLightValue(n - 1, n2, n3 - 1);
                this.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3 - 1);
            }

            if (!isTranslucent4 && !isTranslucent) {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXYPP;
                this.aoBrightnessXYZPPN = this.aoBrightnessXYPP;
            } else {
                this.aoLightValueScratchXYZPPN = this.getAmbientOcclusionLightValue(n + 1, n2, n3 - 1);
                this.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3 - 1);
            }

            if (!isTranslucent3 && !isTranslucent2) {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXYNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXYNP;
            } else {
                this.aoLightValueScratchXYZNPP = this.getAmbientOcclusionLightValue(n - 1, n2, n3 + 1);
                this.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3 + 1);
            }

            if (!isTranslucent3 && !isTranslucent) {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXYPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXYPP;
            } else {
                this.aoLightValueScratchXYZPPP = this.getAmbientOcclusionLightValue(n + 1, n2, n3 + 1);
                this.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3 + 1);
            }

            if (this.renderMaxY >= 1.0) {
                n2--;
            }

            int n4 = mixedBrightnessForBlock;
            if (this.renderMaxY >= 1.0 || !this.blockAccess.getBlock(n, n2 + 1, n3).isOpaqueCube()) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3);
            }

            float ambientOcclusionLightValue = this.getAmbientOcclusionLightValue(n, n2 + 1, n3);
            f7 = (this.aoLightValueScratchXYZNPP + this.aoLightValueScratchXYNP + this.aoLightValueScratchYZPP + ambientOcclusionLightValue) / 4.0F;
            f4 = (this.aoLightValueScratchYZPP + ambientOcclusionLightValue + this.aoLightValueScratchXYZPPP + this.aoLightValueScratchXYPP) / 4.0F;
            f5 = (ambientOcclusionLightValue + this.aoLightValueScratchYZPN + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPN) / 4.0F;
            f6 = (this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPN + ambientOcclusionLightValue + this.aoLightValueScratchYZPN) / 4.0F;
            this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNPP, this.aoBrightnessXYNP, this.aoBrightnessYZPP, n4);
            this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXYZPPP, this.aoBrightnessXYPP, n4);
            this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXYPP, this.aoBrightnessXYZPPN, n4);
            this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYNP, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, n4);
            this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = f;
            this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = f2;
            this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = f3;
            this.colorRedTopLeft *= f4;
            this.colorGreenTopLeft *= f4;
            this.colorBlueTopLeft *= f4;
            this.colorRedBottomLeft *= f5;
            this.colorGreenBottomLeft *= f5;
            this.colorBlueBottomLeft *= f5;
            this.colorRedBottomRight *= f6;
            this.colorGreenBottomRight *= f6;
            this.colorBlueBottomRight *= f6;
            this.colorRedTopRight *= f7;
            this.colorGreenTopRight *= f7;
            this.colorBlueTopRight *= f7;
            this.renderFaceYPos(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 1));
            flag = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2, n3 - 1, 2)) {
            if (this.renderMinZ <= 0.0) {
                n3--;
            }

            this.aoLightValueScratchXZNN = this.getAmbientOcclusionLightValue(n - 1, n2, n3);
            this.aoLightValueScratchYZNN = this.getAmbientOcclusionLightValue(n, n2 - 1, n3);
            this.aoLightValueScratchYZPN = this.getAmbientOcclusionLightValue(n, n2 + 1, n3);
            this.aoLightValueScratchXZPN = this.getAmbientOcclusionLightValue(n + 1, n2, n3);
            this.aoBrightnessXZNN = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3);
            this.aoBrightnessYZNN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3);
            this.aoBrightnessYZPN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3);
            this.aoBrightnessXZPN = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3);
            boolean isTranslucent = this.blockAccess.getBlock(n + 1, n2, n3 - 1).isTranslucent();
            boolean isTranslucent2 = this.blockAccess.getBlock(n - 1, n2, n3 - 1).isTranslucent();
            boolean isTranslucent3 = this.blockAccess.getBlock(n, n2 + 1, n3 - 1).isTranslucent();
            boolean isTranslucent4 = this.blockAccess.getBlock(n, n2 - 1, n3 - 1).isTranslucent();
            if (!isTranslucent2 && !isTranslucent4) {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
            } else {
                this.aoLightValueScratchXYZNNN = this.getAmbientOcclusionLightValue(n - 1, n2 - 1, n3);
                this.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2 - 1, n3);
            }

            if (!isTranslucent2 && !isTranslucent3) {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
            } else {
                this.aoLightValueScratchXYZNPN = this.getAmbientOcclusionLightValue(n - 1, n2 + 1, n3);
                this.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2 + 1, n3);
            }

            if (!isTranslucent && !isTranslucent4) {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
            } else {
                this.aoLightValueScratchXYZPNN = this.getAmbientOcclusionLightValue(n + 1, n2 - 1, n3);
                this.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2 - 1, n3);
            }

            if (!isTranslucent && !isTranslucent3) {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
            } else {
                this.aoLightValueScratchXYZPPN = this.getAmbientOcclusionLightValue(n + 1, n2 + 1, n3);
                this.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2 + 1, n3);
            }

            if (this.renderMinZ <= 0.0) {
                n3++;
            }

            int n4 = mixedBrightnessForBlock;
            if (this.renderMinZ <= 0.0 || !this.blockAccess.getBlock(n, n2, n3 - 1).isOpaqueCube()) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 - 1);
            }

            float ambientOcclusionLightValue = this.getAmbientOcclusionLightValue(n, n2, n3 - 1);
            float f8 = (this.aoLightValueScratchXZNN + this.aoLightValueScratchXYZNPN + ambientOcclusionLightValue + this.aoLightValueScratchYZPN) / 4.0F;
            float f9 = (ambientOcclusionLightValue + this.aoLightValueScratchYZPN + this.aoLightValueScratchXZPN + this.aoLightValueScratchXYZPPN) / 4.0F;
            float f10 = (this.aoLightValueScratchYZNN + ambientOcclusionLightValue + this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXZPN) / 4.0F;
            float f11 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXZNN + this.aoLightValueScratchYZNN + ambientOcclusionLightValue) / 4.0F;
            f4 = (float)(
                f8 * this.renderMaxY * (1.0 - this.renderMinX)
                    + f9 * this.renderMaxY * this.renderMinX
                    + f10 * (1.0 - this.renderMaxY) * this.renderMinX
                    + f11 * (1.0 - this.renderMaxY) * (1.0 - this.renderMinX)
            );
            f5 = (float)(
                f8 * this.renderMaxY * (1.0 - this.renderMaxX)
                    + f9 * this.renderMaxY * this.renderMaxX
                    + f10 * (1.0 - this.renderMaxY) * this.renderMaxX
                    + f11 * (1.0 - this.renderMaxY) * (1.0 - this.renderMaxX)
            );
            f6 = (float)(
                f8 * this.renderMinY * (1.0 - this.renderMaxX)
                    + f9 * this.renderMinY * this.renderMaxX
                    + f10 * (1.0 - this.renderMinY) * this.renderMaxX
                    + f11 * (1.0 - this.renderMinY) * (1.0 - this.renderMaxX)
            );
            f7 = (float)(
                f8 * this.renderMinY * (1.0 - this.renderMinX)
                    + f9 * this.renderMinY * this.renderMinX
                    + f10 * (1.0 - this.renderMinY) * this.renderMinX
                    + f11 * (1.0 - this.renderMinY) * (1.0 - this.renderMinX)
            );
            int aoBrightness = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, n4);
            int aoBrightness2 = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, n4);
            int aoBrightness3 = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYZPNN, this.aoBrightnessXZPN, n4);
            int aoBrightness4 = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXZNN, this.aoBrightnessYZNN, n4);
            this.brightnessTopLeft = this.mixAoBrightness(
                aoBrightness,
                aoBrightness2,
                aoBrightness3,
                aoBrightness4,
                this.renderMaxY * (1.0 - this.renderMinX),
                this.renderMaxY * this.renderMinX,
                (1.0 - this.renderMaxY) * this.renderMinX,
                (1.0 - this.renderMaxY) * (1.0 - this.renderMinX)
            );
            this.brightnessBottomLeft = this.mixAoBrightness(
                aoBrightness,
                aoBrightness2,
                aoBrightness3,
                aoBrightness4,
                this.renderMaxY * (1.0 - this.renderMaxX),
                this.renderMaxY * this.renderMaxX,
                (1.0 - this.renderMaxY) * this.renderMaxX,
                (1.0 - this.renderMaxY) * (1.0 - this.renderMaxX)
            );
            this.brightnessBottomRight = this.mixAoBrightness(
                aoBrightness,
                aoBrightness2,
                aoBrightness3,
                aoBrightness4,
                this.renderMinY * (1.0 - this.renderMaxX),
                this.renderMinY * this.renderMaxX,
                (1.0 - this.renderMinY) * this.renderMaxX,
                (1.0 - this.renderMinY) * (1.0 - this.renderMaxX)
            );
            this.brightnessTopRight = this.mixAoBrightness(
                aoBrightness,
                aoBrightness2,
                aoBrightness3,
                aoBrightness4,
                this.renderMinY * (1.0 - this.renderMinX),
                this.renderMinY * this.renderMinX,
                (1.0 - this.renderMinY) * this.renderMinX,
                (1.0 - this.renderMinY) * (1.0 - this.renderMinX)
            );
            if (flag2) {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = f * this.lightLevel08;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = f2 * this.lightLevel08;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = f3 * this.lightLevel08;
            } else {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = this.lightLevel08;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = this.lightLevel08;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = this.lightLevel08;
            }

            this.colorRedTopLeft *= f4;
            this.colorGreenTopLeft *= f4;
            this.colorBlueTopLeft *= f4;
            this.colorRedBottomLeft *= f5;
            this.colorGreenBottomLeft *= f5;
            this.colorBlueBottomLeft *= f5;
            this.colorRedBottomRight *= f6;
            this.colorGreenBottomRight *= f6;
            this.colorBlueBottomRight *= f6;
            this.colorRedTopRight *= f7;
            this.colorGreenTopRight *= f7;
            this.colorBlueTopRight *= f7;
            IIcon blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 2);
            this.renderFaceZNeg(block, n, n2, n3, blockIcon);
            if (fancyGrass && blockIcon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture()) {
                this.colorRedTopLeft *= f;
                this.colorRedBottomLeft *= f;
                this.colorRedBottomRight *= f;
                this.colorRedTopRight *= f;
                this.colorGreenTopLeft *= f2;
                this.colorGreenBottomLeft *= f2;
                this.colorGreenBottomRight *= f2;
                this.colorGreenTopRight *= f2;
                this.colorBlueTopLeft *= f3;
                this.colorBlueBottomLeft *= f3;
                this.colorBlueBottomRight *= f3;
                this.colorBlueTopRight *= f3;
                this.renderFaceZNeg(block, n, n2, n3, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2, n3 + 1, 3)) {
            if (this.renderMaxZ >= 1.0) {
                n3++;
            }

            this.aoLightValueScratchXZNP = this.getAmbientOcclusionLightValue(n - 1, n2, n3);
            this.aoLightValueScratchXZPP = this.getAmbientOcclusionLightValue(n + 1, n2, n3);
            this.aoLightValueScratchYZNP = this.getAmbientOcclusionLightValue(n, n2 - 1, n3);
            this.aoLightValueScratchYZPP = this.getAmbientOcclusionLightValue(n, n2 + 1, n3);
            this.aoBrightnessXZNP = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3);
            this.aoBrightnessXZPP = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3);
            this.aoBrightnessYZNP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3);
            this.aoBrightnessYZPP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3);
            boolean isTranslucent = this.blockAccess.getBlock(n + 1, n2, n3 + 1).isTranslucent();
            boolean isTranslucent2 = this.blockAccess.getBlock(n - 1, n2, n3 + 1).isTranslucent();
            boolean isTranslucent3 = this.blockAccess.getBlock(n, n2 + 1, n3 + 1).isTranslucent();
            boolean isTranslucent4 = this.blockAccess.getBlock(n, n2 - 1, n3 + 1).isTranslucent();
            if (!isTranslucent2 && !isTranslucent4) {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
            } else {
                this.aoLightValueScratchXYZNNP = this.getAmbientOcclusionLightValue(n - 1, n2 - 1, n3);
                this.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2 - 1, n3);
            }

            if (!isTranslucent2 && !isTranslucent3) {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
            } else {
                this.aoLightValueScratchXYZNPP = this.getAmbientOcclusionLightValue(n - 1, n2 + 1, n3);
                this.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2 + 1, n3);
            }

            if (!isTranslucent && !isTranslucent4) {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
            } else {
                this.aoLightValueScratchXYZPNP = this.getAmbientOcclusionLightValue(n + 1, n2 - 1, n3);
                this.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2 - 1, n3);
            }

            if (!isTranslucent && !isTranslucent3) {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
            } else {
                this.aoLightValueScratchXYZPPP = this.getAmbientOcclusionLightValue(n + 1, n2 + 1, n3);
                this.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2 + 1, n3);
            }

            if (this.renderMaxZ >= 1.0) {
                n3--;
            }

            int n4 = mixedBrightnessForBlock;
            if (this.renderMaxZ >= 1.0 || !this.blockAccess.getBlock(n, n2, n3 + 1).isOpaqueCube()) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 + 1);
            }

            float ambientOcclusionLightValue = this.getAmbientOcclusionLightValue(n, n2, n3 + 1);
            float f8 = (this.aoLightValueScratchXZNP + this.aoLightValueScratchXYZNPP + ambientOcclusionLightValue + this.aoLightValueScratchYZPP) / 4.0F;
            float f9 = (ambientOcclusionLightValue + this.aoLightValueScratchYZPP + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYZPPP) / 4.0F;
            float f10 = (this.aoLightValueScratchYZNP + ambientOcclusionLightValue + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXZPP) / 4.0F;
            float f11 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXZNP + this.aoLightValueScratchYZNP + ambientOcclusionLightValue) / 4.0F;
            f4 = (float)(
                f8 * this.renderMaxY * (1.0 - this.renderMinX)
                    + f9 * this.renderMaxY * this.renderMinX
                    + f10 * (1.0 - this.renderMaxY) * this.renderMinX
                    + f11 * (1.0 - this.renderMaxY) * (1.0 - this.renderMinX)
            );
            f5 = (float)(
                f8 * this.renderMinY * (1.0 - this.renderMinX)
                    + f9 * this.renderMinY * this.renderMinX
                    + f10 * (1.0 - this.renderMinY) * this.renderMinX
                    + f11 * (1.0 - this.renderMinY) * (1.0 - this.renderMinX)
            );
            f6 = (float)(
                f8 * this.renderMinY * (1.0 - this.renderMaxX)
                    + f9 * this.renderMinY * this.renderMaxX
                    + f10 * (1.0 - this.renderMinY) * this.renderMaxX
                    + f11 * (1.0 - this.renderMinY) * (1.0 - this.renderMaxX)
            );
            f7 = (float)(
                f8 * this.renderMaxY * (1.0 - this.renderMaxX)
                    + f9 * this.renderMaxY * this.renderMaxX
                    + f10 * (1.0 - this.renderMaxY) * this.renderMaxX
                    + f11 * (1.0 - this.renderMaxY) * (1.0 - this.renderMaxX)
            );
            int aoBrightness = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYZNPP, this.aoBrightnessYZPP, n4);
            int aoBrightness2 = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXZPP, this.aoBrightnessXYZPPP, n4);
            int aoBrightness3 = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, n4);
            int aoBrightness4 = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, this.aoBrightnessYZNP, n4);
            this.brightnessTopLeft = this.mixAoBrightness(
                aoBrightness,
                aoBrightness4,
                aoBrightness3,
                aoBrightness2,
                this.renderMaxY * (1.0 - this.renderMinX),
                (1.0 - this.renderMaxY) * (1.0 - this.renderMinX),
                (1.0 - this.renderMaxY) * this.renderMinX,
                this.renderMaxY * this.renderMinX
            );
            this.brightnessBottomLeft = this.mixAoBrightness(
                aoBrightness,
                aoBrightness4,
                aoBrightness3,
                aoBrightness2,
                this.renderMinY * (1.0 - this.renderMinX),
                (1.0 - this.renderMinY) * (1.0 - this.renderMinX),
                (1.0 - this.renderMinY) * this.renderMinX,
                this.renderMinY * this.renderMinX
            );
            this.brightnessBottomRight = this.mixAoBrightness(
                aoBrightness,
                aoBrightness4,
                aoBrightness3,
                aoBrightness2,
                this.renderMinY * (1.0 - this.renderMaxX),
                (1.0 - this.renderMinY) * (1.0 - this.renderMaxX),
                (1.0 - this.renderMinY) * this.renderMaxX,
                this.renderMinY * this.renderMaxX
            );
            this.brightnessTopRight = this.mixAoBrightness(
                aoBrightness,
                aoBrightness4,
                aoBrightness3,
                aoBrightness2,
                this.renderMaxY * (1.0 - this.renderMaxX),
                (1.0 - this.renderMaxY) * (1.0 - this.renderMaxX),
                (1.0 - this.renderMaxY) * this.renderMaxX,
                this.renderMaxY * this.renderMaxX
            );
            if (flag2) {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = f * this.lightLevel08;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = f2 * this.lightLevel08;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = f3 * this.lightLevel08;
            } else {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = this.lightLevel08;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = this.lightLevel08;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = this.lightLevel08;
            }

            this.colorRedTopLeft *= f4;
            this.colorGreenTopLeft *= f4;
            this.colorBlueTopLeft *= f4;
            this.colorRedBottomLeft *= f5;
            this.colorGreenBottomLeft *= f5;
            this.colorBlueBottomLeft *= f5;
            this.colorRedBottomRight *= f6;
            this.colorGreenBottomRight *= f6;
            this.colorBlueBottomRight *= f6;
            this.colorRedTopRight *= f7;
            this.colorGreenTopRight *= f7;
            this.colorBlueTopRight *= f7;
            IIcon blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 3);
            this.renderFaceZPos(block, n, n2, n3, blockIcon);
            if (fancyGrass && blockIcon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture()) {
                this.colorRedTopLeft *= f;
                this.colorRedBottomLeft *= f;
                this.colorRedBottomRight *= f;
                this.colorRedTopRight *= f;
                this.colorGreenTopLeft *= f2;
                this.colorGreenBottomLeft *= f2;
                this.colorGreenBottomRight *= f2;
                this.colorGreenTopRight *= f2;
                this.colorBlueTopLeft *= f3;
                this.colorBlueBottomLeft *= f3;
                this.colorBlueBottomRight *= f3;
                this.colorBlueTopRight *= f3;
                this.renderFaceZPos(block, n, n2, n3, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n - 1, n2, n3, 4)) {
            if (this.renderMinX <= 0.0) {
                n--;
            }

            this.aoLightValueScratchXYNN = this.getAmbientOcclusionLightValue(n, n2 - 1, n3);
            this.aoLightValueScratchXZNN = this.getAmbientOcclusionLightValue(n, n2, n3 - 1);
            this.aoLightValueScratchXZNP = this.getAmbientOcclusionLightValue(n, n2, n3 + 1);
            this.aoLightValueScratchXYNP = this.getAmbientOcclusionLightValue(n, n2 + 1, n3);
            this.aoBrightnessXYNN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3);
            this.aoBrightnessXZNN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 - 1);
            this.aoBrightnessXZNP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 + 1);
            this.aoBrightnessXYNP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3);
            boolean isTranslucent = this.blockAccess.getBlock(n - 1, n2 + 1, n3).isTranslucent();
            boolean isTranslucent2 = this.blockAccess.getBlock(n - 1, n2 - 1, n3).isTranslucent();
            boolean isTranslucent3 = this.blockAccess.getBlock(n - 1, n2, n3 - 1).isTranslucent();
            boolean isTranslucent4 = this.blockAccess.getBlock(n - 1, n2, n3 + 1).isTranslucent();
            if (!isTranslucent3 && !isTranslucent2) {
                this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
            } else {
                this.aoLightValueScratchXYZNNN = this.getAmbientOcclusionLightValue(n, n2 - 1, n3 - 1);
                this.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3 - 1);
            }

            if (!isTranslucent4 && !isTranslucent2) {
                this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
            } else {
                this.aoLightValueScratchXYZNNP = this.getAmbientOcclusionLightValue(n, n2 - 1, n3 + 1);
                this.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3 + 1);
            }

            if (!isTranslucent3 && !isTranslucent) {
                this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
                this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
            } else {
                this.aoLightValueScratchXYZNPN = this.getAmbientOcclusionLightValue(n, n2 + 1, n3 - 1);
                this.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3 - 1);
            }

            if (!isTranslucent4 && !isTranslucent) {
                this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
                this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
            } else {
                this.aoLightValueScratchXYZNPP = this.getAmbientOcclusionLightValue(n, n2 + 1, n3 + 1);
                this.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3 + 1);
            }

            if (this.renderMinX <= 0.0) {
                n++;
            }

            int n4 = mixedBrightnessForBlock;
            if (this.renderMinX <= 0.0 || !this.blockAccess.getBlock(n - 1, n2, n3).isOpaqueCube()) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3);
            }

            float ambientOcclusionLightValue = this.getAmbientOcclusionLightValue(n - 1, n2, n3);
            float f8 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNP + ambientOcclusionLightValue + this.aoLightValueScratchXZNP) / 4.0F;
            float f9 = (ambientOcclusionLightValue + this.aoLightValueScratchXZNP + this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPP) / 4.0F;
            float f10 = (this.aoLightValueScratchXZNN + ambientOcclusionLightValue + this.aoLightValueScratchXYZNPN + this.aoLightValueScratchXYNP) / 4.0F;
            float f11 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXYNN + this.aoLightValueScratchXZNN + ambientOcclusionLightValue) / 4.0F;
            f4 = (float)(
                f9 * this.renderMaxY * this.renderMaxZ
                    + f10 * this.renderMaxY * (1.0 - this.renderMaxZ)
                    + f11 * (1.0 - this.renderMaxY) * (1.0 - this.renderMaxZ)
                    + f8 * (1.0 - this.renderMaxY) * this.renderMaxZ
            );
            f5 = (float)(
                f9 * this.renderMaxY * this.renderMinZ
                    + f10 * this.renderMaxY * (1.0 - this.renderMinZ)
                    + f11 * (1.0 - this.renderMaxY) * (1.0 - this.renderMinZ)
                    + f8 * (1.0 - this.renderMaxY) * this.renderMinZ
            );
            f6 = (float)(
                f9 * this.renderMinY * this.renderMinZ
                    + f10 * this.renderMinY * (1.0 - this.renderMinZ)
                    + f11 * (1.0 - this.renderMinY) * (1.0 - this.renderMinZ)
                    + f8 * (1.0 - this.renderMinY) * this.renderMinZ
            );
            f7 = (float)(
                f9 * this.renderMinY * this.renderMaxZ
                    + f10 * this.renderMinY * (1.0 - this.renderMaxZ)
                    + f11 * (1.0 - this.renderMinY) * (1.0 - this.renderMaxZ)
                    + f8 * (1.0 - this.renderMinY) * this.renderMaxZ
            );
            int aoBrightness = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, n4);
            int aoBrightness2 = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYNP, this.aoBrightnessXYZNPP, n4);
            int aoBrightness3 = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessXYNP, n4);
            int aoBrightness4 = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXYNN, this.aoBrightnessXZNN, n4);
            this.brightnessTopLeft = this.mixAoBrightness(
                aoBrightness2,
                aoBrightness3,
                aoBrightness4,
                aoBrightness,
                this.renderMaxY * this.renderMaxZ,
                this.renderMaxY * (1.0 - this.renderMaxZ),
                (1.0 - this.renderMaxY) * (1.0 - this.renderMaxZ),
                (1.0 - this.renderMaxY) * this.renderMaxZ
            );
            this.brightnessBottomLeft = this.mixAoBrightness(
                aoBrightness2,
                aoBrightness3,
                aoBrightness4,
                aoBrightness,
                this.renderMaxY * this.renderMinZ,
                this.renderMaxY * (1.0 - this.renderMinZ),
                (1.0 - this.renderMaxY) * (1.0 - this.renderMinZ),
                (1.0 - this.renderMaxY) * this.renderMinZ
            );
            this.brightnessBottomRight = this.mixAoBrightness(
                aoBrightness2,
                aoBrightness3,
                aoBrightness4,
                aoBrightness,
                this.renderMinY * this.renderMinZ,
                this.renderMinY * (1.0 - this.renderMinZ),
                (1.0 - this.renderMinY) * (1.0 - this.renderMinZ),
                (1.0 - this.renderMinY) * this.renderMinZ
            );
            this.brightnessTopRight = this.mixAoBrightness(
                aoBrightness2,
                aoBrightness3,
                aoBrightness4,
                aoBrightness,
                this.renderMinY * this.renderMaxZ,
                this.renderMinY * (1.0 - this.renderMaxZ),
                (1.0 - this.renderMinY) * (1.0 - this.renderMaxZ),
                (1.0 - this.renderMinY) * this.renderMaxZ
            );
            if (flag2) {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = f * this.lightLevel06;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = f2 * this.lightLevel06;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = f3 * this.lightLevel06;
            } else {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = this.lightLevel06;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = this.lightLevel06;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = this.lightLevel06;
            }

            this.colorRedTopLeft *= f4;
            this.colorGreenTopLeft *= f4;
            this.colorBlueTopLeft *= f4;
            this.colorRedBottomLeft *= f5;
            this.colorGreenBottomLeft *= f5;
            this.colorBlueBottomLeft *= f5;
            this.colorRedBottomRight *= f6;
            this.colorGreenBottomRight *= f6;
            this.colorBlueBottomRight *= f6;
            this.colorRedTopRight *= f7;
            this.colorGreenTopRight *= f7;
            this.colorBlueTopRight *= f7;
            IIcon blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 4);
            this.renderFaceXNeg(block, n, n2, n3, blockIcon);
            if (fancyGrass && blockIcon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture()) {
                this.colorRedTopLeft *= f;
                this.colorRedBottomLeft *= f;
                this.colorRedBottomRight *= f;
                this.colorRedTopRight *= f;
                this.colorGreenTopLeft *= f2;
                this.colorGreenBottomLeft *= f2;
                this.colorGreenBottomRight *= f2;
                this.colorGreenTopRight *= f2;
                this.colorBlueTopLeft *= f3;
                this.colorBlueBottomLeft *= f3;
                this.colorBlueBottomRight *= f3;
                this.colorBlueTopRight *= f3;
                this.renderFaceXNeg(block, n, n2, n3, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n + 1, n2, n3, 5)) {
            if (this.renderMaxX >= 1.0) {
                n++;
            }

            this.aoLightValueScratchXYPN = this.getAmbientOcclusionLightValue(n, n2 - 1, n3);
            this.aoLightValueScratchXZPN = this.getAmbientOcclusionLightValue(n, n2, n3 - 1);
            this.aoLightValueScratchXZPP = this.getAmbientOcclusionLightValue(n, n2, n3 + 1);
            this.aoLightValueScratchXYPP = this.getAmbientOcclusionLightValue(n, n2 + 1, n3);
            this.aoBrightnessXYPN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3);
            this.aoBrightnessXZPN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 - 1);
            this.aoBrightnessXZPP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 + 1);
            this.aoBrightnessXYPP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3);
            boolean isTranslucent = this.blockAccess.getBlock(n + 1, n2 + 1, n3).isTranslucent();
            boolean isTranslucent2 = this.blockAccess.getBlock(n + 1, n2 - 1, n3).isTranslucent();
            boolean isTranslucent3 = this.blockAccess.getBlock(n + 1, n2, n3 + 1).isTranslucent();
            boolean isTranslucent4 = this.blockAccess.getBlock(n + 1, n2, n3 - 1).isTranslucent();
            if (!isTranslucent2 && !isTranslucent4) {
                this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
            } else {
                this.aoLightValueScratchXYZPNN = this.getAmbientOcclusionLightValue(n, n2 - 1, n3 - 1);
                this.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3 - 1);
            }

            if (!isTranslucent2 && !isTranslucent3) {
                this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
            } else {
                this.aoLightValueScratchXYZPNP = this.getAmbientOcclusionLightValue(n, n2 - 1, n3 + 1);
                this.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3 + 1);
            }

            if (!isTranslucent && !isTranslucent4) {
                this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
                this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
            } else {
                this.aoLightValueScratchXYZPPN = this.getAmbientOcclusionLightValue(n, n2 + 1, n3 - 1);
                this.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3 - 1);
            }

            if (!isTranslucent && !isTranslucent3) {
                this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
                this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
            } else {
                this.aoLightValueScratchXYZPPP = this.getAmbientOcclusionLightValue(n, n2 + 1, n3 + 1);
                this.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3 + 1);
            }

            if (this.renderMaxX >= 1.0) {
                n--;
            }

            int n4 = mixedBrightnessForBlock;
            if (this.renderMaxX >= 1.0 || !this.blockAccess.getBlock(n + 1, n2, n3).isOpaqueCube()) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3);
            }

            float ambientOcclusionLightValue = this.getAmbientOcclusionLightValue(n + 1, n2, n3);
            float f8 = (this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNP + ambientOcclusionLightValue + this.aoLightValueScratchXZPP) / 4.0F;
            float f9 = (this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXZPN + ambientOcclusionLightValue) / 4.0F;
            float f10 = (this.aoLightValueScratchXZPN + ambientOcclusionLightValue + this.aoLightValueScratchXYZPPN + this.aoLightValueScratchXYPP) / 4.0F;
            float f11 = (ambientOcclusionLightValue + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPP) / 4.0F;
            f4 = (float)(
                f8 * (1.0 - this.renderMinY) * this.renderMaxZ
                    + f9 * (1.0 - this.renderMinY) * (1.0 - this.renderMaxZ)
                    + f10 * this.renderMinY * (1.0 - this.renderMaxZ)
                    + f11 * this.renderMinY * this.renderMaxZ
            );
            f5 = (float)(
                f8 * (1.0 - this.renderMinY) * this.renderMinZ
                    + f9 * (1.0 - this.renderMinY) * (1.0 - this.renderMinZ)
                    + f10 * this.renderMinY * (1.0 - this.renderMinZ)
                    + f11 * this.renderMinY * this.renderMinZ
            );
            f6 = (float)(
                f8 * (1.0 - this.renderMaxY) * this.renderMinZ
                    + f9 * (1.0 - this.renderMaxY) * (1.0 - this.renderMinZ)
                    + f10 * this.renderMaxY * (1.0 - this.renderMinZ)
                    + f11 * this.renderMaxY * this.renderMinZ
            );
            f7 = (float)(
                f8 * (1.0 - this.renderMaxY) * this.renderMaxZ
                    + f9 * (1.0 - this.renderMaxY) * (1.0 - this.renderMaxZ)
                    + f10 * this.renderMaxY * (1.0 - this.renderMaxZ)
                    + f11 * this.renderMaxY * this.renderMaxZ
            );
            int aoBrightness = this.getAoBrightness(this.aoBrightnessXYPN, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, n4);
            int aoBrightness2 = this.getAoBrightness(this.aoBrightnessXZPP, this.aoBrightnessXYPP, this.aoBrightnessXYZPPP, n4);
            int aoBrightness3 = this.getAoBrightness(this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, this.aoBrightnessXYPP, n4);
            int aoBrightness4 = this.getAoBrightness(this.aoBrightnessXYZPNN, this.aoBrightnessXYPN, this.aoBrightnessXZPN, n4);
            this.brightnessTopLeft = this.mixAoBrightness(
                aoBrightness,
                aoBrightness4,
                aoBrightness3,
                aoBrightness2,
                (1.0 - this.renderMinY) * this.renderMaxZ,
                (1.0 - this.renderMinY) * (1.0 - this.renderMaxZ),
                this.renderMinY * (1.0 - this.renderMaxZ),
                this.renderMinY * this.renderMaxZ
            );
            this.brightnessBottomLeft = this.mixAoBrightness(
                aoBrightness,
                aoBrightness4,
                aoBrightness3,
                aoBrightness2,
                (1.0 - this.renderMinY) * this.renderMinZ,
                (1.0 - this.renderMinY) * (1.0 - this.renderMinZ),
                this.renderMinY * (1.0 - this.renderMinZ),
                this.renderMinY * this.renderMinZ
            );
            this.brightnessBottomRight = this.mixAoBrightness(
                aoBrightness,
                aoBrightness4,
                aoBrightness3,
                aoBrightness2,
                (1.0 - this.renderMaxY) * this.renderMinZ,
                (1.0 - this.renderMaxY) * (1.0 - this.renderMinZ),
                this.renderMaxY * (1.0 - this.renderMinZ),
                this.renderMaxY * this.renderMinZ
            );
            this.brightnessTopRight = this.mixAoBrightness(
                aoBrightness,
                aoBrightness4,
                aoBrightness3,
                aoBrightness2,
                (1.0 - this.renderMaxY) * this.renderMaxZ,
                (1.0 - this.renderMaxY) * (1.0 - this.renderMaxZ),
                this.renderMaxY * (1.0 - this.renderMaxZ),
                this.renderMaxY * this.renderMaxZ
            );
            if (flag2) {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = f * this.lightLevel06;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = f2 * this.lightLevel06;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = f3 * this.lightLevel06;
            } else {
                this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = this.lightLevel06;
                this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = this.lightLevel06;
                this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = this.lightLevel06;
            }

            this.colorRedTopLeft *= f4;
            this.colorGreenTopLeft *= f4;
            this.colorBlueTopLeft *= f4;
            this.colorRedBottomLeft *= f5;
            this.colorGreenBottomLeft *= f5;
            this.colorBlueBottomLeft *= f5;
            this.colorRedBottomRight *= f6;
            this.colorGreenBottomRight *= f6;
            this.colorBlueBottomRight *= f6;
            this.colorRedTopRight *= f7;
            this.colorGreenTopRight *= f7;
            this.colorBlueTopRight *= f7;
            IIcon blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 5);
            this.renderFaceXPos(block, n, n2, n3, blockIcon);
            if (fancyGrass && blockIcon.getIconName().equals("grass_side") && !this.hasOverrideBlockTexture()) {
                this.colorRedTopLeft *= f;
                this.colorRedBottomLeft *= f;
                this.colorRedBottomRight *= f;
                this.colorRedTopRight *= f;
                this.colorGreenTopLeft *= f2;
                this.colorGreenBottomLeft *= f2;
                this.colorGreenBottomRight *= f2;
                this.colorGreenTopRight *= f2;
                this.colorBlueTopLeft *= f3;
                this.colorBlueBottomLeft *= f3;
                this.colorBlueBottomRight *= f3;
                this.colorBlueTopRight *= f3;
                this.renderFaceXPos(block, n, n2, n3, BlockGrass.getIconSideOverlay());
            }

            flag = true;
        }

        this.enableAO = false;
        return flag;
    }

    public int getAoBrightness(int n, int n2, int n3, int n4) {
        if (n == 0) {
            n = n4;
        }

        if (n2 == 0) {
            n2 = n4;
        }

        if (n3 == 0) {
            n3 = n4;
        }

        return n + n2 + n3 + n4 >> 2 & 16711935;
    }

    public int mixAoBrightness(int n, int n2, int n3, int n4, double d, double d2, double d3, double d4) {
        int n5 = (int)((n >> 16 & 0xFF) * d + (n2 >> 16 & 0xFF) * d2 + (n3 >> 16 & 0xFF) * d3 + (n4 >> 16 & 0xFF) * d4) & 0xFF;
        int n6 = (int)((n & 0xFF) * d + (n2 & 0xFF) * d2 + (n3 & 0xFF) * d3 + (n4 & 0xFF) * d4) & 0xFF;
        return n5 << 16 | n6;
    }

    public boolean renderStandardBlockWithColorMultiplier(Block block, int n, int n2, int n3, float f, float f2, float f3) {
        this.enableAO = false;
        boolean defaultTexture = Tessellator.instance.defaultTexture;
        boolean flag = Config.isBetterGrass() && defaultTexture;
        Tessellator instance = Tessellator.instance;
        boolean flag2 = false;
        int n4 = -1;
        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2 - 1, n3, 0)) {
            if (n4 < 0) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
            }

            float lightLevel05 = this.lightLevel05;
            float f4 = lightLevel05;
            float f5 = lightLevel05;
            float f6 = lightLevel05;
            if (block != Blocks.grass) {
                f4 = lightLevel05 * f;
                f5 = lightLevel05 * f2;
                f6 = lightLevel05 * f3;
            }

            instance.setBrightness(this.renderMinY > 0.0 ? n4 : block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3));
            instance.setColorOpaque_F(f4, f5, f6);
            this.renderFaceYNeg(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 0));
            flag2 = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2 + 1, n3, 1)) {
            if (n4 < 0) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
            }

            float f4 = 1.0F;
            float f5 = f4 * f;
            float f6 = f4 * f2;
            float f7 = f4 * f3;
            instance.setBrightness(this.renderMaxY < 1.0 ? n4 : block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3));
            instance.setColorOpaque_F(f5, f6, f7);
            this.renderFaceYPos(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 1));
            flag2 = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2, n3 - 1, 2)) {
            if (n4 < 0) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
            }

            float lightLevel08 = this.lightLevel08;
            float f4 = lightLevel08;
            float f5 = lightLevel08;
            float f6 = lightLevel08;
            if (block != Blocks.grass) {
                f4 = lightLevel08 * f;
                f5 = lightLevel08 * f2;
                f6 = lightLevel08 * f3;
            }

            instance.setBrightness(this.renderMinZ > 0.0 ? n4 : block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 - 1));
            instance.setColorOpaque_F(f4, f5, f6);
            IIcon blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 2);
            if (flag) {
                if (blockIcon == TextureUtils.iconGrassSide || blockIcon == TextureUtils.iconMyceliumSide) {
                    blockIcon = Config.getSideGrassTexture(this.blockAccess, n, n2, n3, 2, blockIcon);
                    if (blockIcon == TextureUtils.iconGrassTop) {
                        instance.setColorOpaque_F(f4 * f, f5 * f2, f6 * f3);
                    }
                }

                if (blockIcon == TextureUtils.iconGrassSideSnowed) {
                    blockIcon = Config.getSideSnowGrassTexture(this.blockAccess, n, n2, n3, 2);
                }
            }

            this.renderFaceZNeg(block, n, n2, n3, blockIcon);
            if (defaultTexture && fancyGrass && blockIcon == TextureUtils.iconGrassSide && !this.hasOverrideBlockTexture()) {
                instance.setColorOpaque_F(f4 * f, f5 * f2, f6 * f3);
                this.renderFaceZNeg(block, n, n2, n3, BlockGrass.getIconSideOverlay());
            }

            flag2 = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2, n3 + 1, 3)) {
            if (n4 < 0) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
            }

            float lightLevel08 = this.lightLevel08;
            float f4 = lightLevel08;
            float f5 = lightLevel08;
            float f6 = lightLevel08;
            if (block != Blocks.grass) {
                f4 = lightLevel08 * f;
                f5 = lightLevel08 * f2;
                f6 = lightLevel08 * f3;
            }

            instance.setBrightness(this.renderMaxZ < 1.0 ? n4 : block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 + 1));
            instance.setColorOpaque_F(f4, f5, f6);
            IIcon blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 3);
            if (flag) {
                if (blockIcon == TextureUtils.iconGrassSide || blockIcon == TextureUtils.iconMyceliumSide) {
                    blockIcon = Config.getSideGrassTexture(this.blockAccess, n, n2, n3, 3, blockIcon);
                    if (blockIcon == TextureUtils.iconGrassTop) {
                        instance.setColorOpaque_F(f4 * f, f5 * f2, f6 * f3);
                    }
                }

                if (blockIcon == TextureUtils.iconGrassSideSnowed) {
                    blockIcon = Config.getSideSnowGrassTexture(this.blockAccess, n, n2, n3, 3);
                }
            }

            this.renderFaceZPos(block, n, n2, n3, blockIcon);
            if (defaultTexture && fancyGrass && blockIcon == TextureUtils.iconGrassSide && !this.hasOverrideBlockTexture()) {
                instance.setColorOpaque_F(f4 * f, f5 * f2, f6 * f3);
                this.renderFaceZPos(block, n, n2, n3, BlockGrass.getIconSideOverlay());
            }

            flag2 = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n - 1, n2, n3, 4)) {
            if (n4 < 0) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
            }

            float lightLevel06 = this.lightLevel06;
            float f4 = lightLevel06;
            float f5 = lightLevel06;
            float f6 = lightLevel06;
            if (block != Blocks.grass) {
                f4 = lightLevel06 * f;
                f5 = lightLevel06 * f2;
                f6 = lightLevel06 * f3;
            }

            instance.setBrightness(this.renderMinX > 0.0 ? n4 : block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3));
            instance.setColorOpaque_F(f4, f5, f6);
            IIcon blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 4);
            if (flag) {
                if (blockIcon == TextureUtils.iconGrassSide || blockIcon == TextureUtils.iconMyceliumSide) {
                    blockIcon = Config.getSideGrassTexture(this.blockAccess, n, n2, n3, 4, blockIcon);
                    if (blockIcon == TextureUtils.iconGrassTop) {
                        instance.setColorOpaque_F(f4 * f, f5 * f2, f6 * f3);
                    }
                }

                if (blockIcon == TextureUtils.iconGrassSideSnowed) {
                    blockIcon = Config.getSideSnowGrassTexture(this.blockAccess, n, n2, n3, 4);
                }
            }

            this.renderFaceXNeg(block, n, n2, n3, blockIcon);
            if (defaultTexture && fancyGrass && blockIcon == TextureUtils.iconGrassSide && !this.hasOverrideBlockTexture()) {
                instance.setColorOpaque_F(f4 * f, f5 * f2, f6 * f3);
                this.renderFaceXNeg(block, n, n2, n3, BlockGrass.getIconSideOverlay());
            }

            flag2 = true;
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n + 1, n2, n3, 5)) {
            if (n4 < 0) {
                n4 = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
            }

            float lightLevel06 = this.lightLevel06;
            float f4 = lightLevel06;
            float f5 = lightLevel06;
            float f6 = lightLevel06;
            if (block != Blocks.grass) {
                f4 = lightLevel06 * f;
                f5 = lightLevel06 * f2;
                f6 = lightLevel06 * f3;
            }

            instance.setBrightness(this.renderMaxX < 1.0 ? n4 : block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3));
            instance.setColorOpaque_F(f4, f5, f6);
            IIcon blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 5);
            if (flag) {
                if (blockIcon == TextureUtils.iconGrassSide || blockIcon == TextureUtils.iconMyceliumSide) {
                    blockIcon = Config.getSideGrassTexture(this.blockAccess, n, n2, n3, 5, blockIcon);
                    if (blockIcon == TextureUtils.iconGrassTop) {
                        instance.setColorOpaque_F(f4 * f, f5 * f2, f6 * f3);
                    }
                }

                if (blockIcon == TextureUtils.iconGrassSideSnowed) {
                    blockIcon = Config.getSideSnowGrassTexture(this.blockAccess, n, n2, n3, 5);
                }
            }

            this.renderFaceXPos(block, n, n2, n3, blockIcon);
            if (defaultTexture && fancyGrass && blockIcon == TextureUtils.iconGrassSide && !this.hasOverrideBlockTexture()) {
                instance.setColorOpaque_F(f4 * f, f5 * f2, f6 * f3);
                this.renderFaceXPos(block, n, n2, n3, BlockGrass.getIconSideOverlay());
            }

            flag2 = true;
        }

        return flag2;
    }

    public boolean renderBlockCocoa(BlockCocoa blockCocoa, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(blockCocoa.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        int direction = BlockDirectional.getDirection(blockMetadata);
        int computeInt = BlockCocoa.computeInt(blockMetadata);
        IIcon cocoaIcon = blockCocoa.getCocoaIcon(computeInt);
        int n4 = 4 + computeInt * 2;
        int n5 = 5 + computeInt * 2;
        double d = 15.0 - n4;
        double d2 = 15.0;
        double d3 = 4.0;
        double d4 = 4.0 + n5;
        double interpolatedU = cocoaIcon.getInterpolatedU(d);
        double interpolatedU2 = cocoaIcon.getInterpolatedU(d2);
        double interpolatedV = cocoaIcon.getInterpolatedV(d3);
        double interpolatedV2 = cocoaIcon.getInterpolatedV(d4);
        double d5 = 0.0;
        double d6 = 0.0;
        switch (direction) {
            case 0:
                d5 = 8.0 - n4 / 2;
                d6 = 15.0 - n4;
                break;
            case 1:
                d5 = 1.0;
                d6 = 8.0 - n4 / 2;
                break;
            case 2:
                d5 = 8.0 - n4 / 2;
                d6 = 1.0;
                break;
            case 3:
                d5 = 15.0 - n4;
                d6 = 8.0 - n4 / 2;
        }

        double d7 = n + d5 / 16.0;
        double d8 = n + (d5 + n4) / 16.0;
        double d9 = n2 + (12.0 - n5) / 16.0;
        double d10 = n2 + 0.75;
        double d11 = n3 + d6 / 16.0;
        double d12 = n3 + (d6 + n4) / 16.0;
        instance.addVertexWithUV(d7, d9, d11, interpolatedU, interpolatedV2);
        instance.addVertexWithUV(d7, d9, d12, interpolatedU2, interpolatedV2);
        instance.addVertexWithUV(d7, d10, d12, interpolatedU2, interpolatedV);
        instance.addVertexWithUV(d7, d10, d11, interpolatedU, interpolatedV);
        instance.addVertexWithUV(d8, d9, d12, interpolatedU, interpolatedV2);
        instance.addVertexWithUV(d8, d9, d11, interpolatedU2, interpolatedV2);
        instance.addVertexWithUV(d8, d10, d11, interpolatedU2, interpolatedV);
        instance.addVertexWithUV(d8, d10, d12, interpolatedU, interpolatedV);
        instance.addVertexWithUV(d8, d9, d11, interpolatedU, interpolatedV2);
        instance.addVertexWithUV(d7, d9, d11, interpolatedU2, interpolatedV2);
        instance.addVertexWithUV(d7, d10, d11, interpolatedU2, interpolatedV);
        instance.addVertexWithUV(d8, d10, d11, interpolatedU, interpolatedV);
        instance.addVertexWithUV(d7, d9, d12, interpolatedU, interpolatedV2);
        instance.addVertexWithUV(d8, d9, d12, interpolatedU2, interpolatedV2);
        instance.addVertexWithUV(d8, d10, d12, interpolatedU2, interpolatedV);
        instance.addVertexWithUV(d7, d10, d12, interpolatedU, interpolatedV);
        int n6 = n4;
        if (computeInt >= 2) {
            n6 = n4 - 1;
        }

        interpolatedU = cocoaIcon.getMinU();
        interpolatedU2 = cocoaIcon.getInterpolatedU(n6);
        interpolatedV = cocoaIcon.getMinV();
        interpolatedV2 = cocoaIcon.getInterpolatedV(n6);
        instance.addVertexWithUV(d7, d10, d12, interpolatedU, interpolatedV2);
        instance.addVertexWithUV(d8, d10, d12, interpolatedU2, interpolatedV2);
        instance.addVertexWithUV(d8, d10, d11, interpolatedU2, interpolatedV);
        instance.addVertexWithUV(d7, d10, d11, interpolatedU, interpolatedV);
        instance.addVertexWithUV(d7, d9, d11, interpolatedU, interpolatedV);
        instance.addVertexWithUV(d8, d9, d11, interpolatedU2, interpolatedV);
        instance.addVertexWithUV(d8, d9, d12, interpolatedU2, interpolatedV2);
        instance.addVertexWithUV(d7, d9, d12, interpolatedU, interpolatedV2);
        interpolatedU = cocoaIcon.getInterpolatedU(12.0);
        interpolatedU2 = cocoaIcon.getMaxU();
        interpolatedV = cocoaIcon.getMinV();
        interpolatedV2 = cocoaIcon.getInterpolatedV(4.0);
        d5 = 8.0;
        d6 = 0.0;
        switch (direction) {
            case 0:
                d5 = 8.0;
                d6 = 12.0;
                double d13 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d13;
                break;
            case 1:
                d5 = 0.0;
                d6 = 8.0;
                break;
            case 2:
                d5 = 8.0;
                d6 = 0.0;
                break;
            case 3:
                d5 = 12.0;
                d6 = 8.0;
                double d14 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d14;
        }

        d7 = n + d5 / 16.0;
        d8 = n + (d5 + 4.0) / 16.0;
        d9 = n2 + 0.75;
        d10 = n2 + 1.0;
        d11 = n3 + d6 / 16.0;
        d12 = n3 + (d6 + 4.0) / 16.0;
        if (direction == 2 || direction == 0) {
            instance.addVertexWithUV(d7, d9, d11, interpolatedU2, interpolatedV2);
            instance.addVertexWithUV(d7, d9, d12, interpolatedU, interpolatedV2);
            instance.addVertexWithUV(d7, d10, d12, interpolatedU, interpolatedV);
            instance.addVertexWithUV(d7, d10, d11, interpolatedU2, interpolatedV);
            instance.addVertexWithUV(d7, d9, d12, interpolatedU, interpolatedV2);
            instance.addVertexWithUV(d7, d9, d11, interpolatedU2, interpolatedV2);
            instance.addVertexWithUV(d7, d10, d11, interpolatedU2, interpolatedV);
            instance.addVertexWithUV(d7, d10, d12, interpolatedU, interpolatedV);
        } else if (direction == 1 || direction == 3) {
            instance.addVertexWithUV(d8, d9, d11, interpolatedU, interpolatedV2);
            instance.addVertexWithUV(d7, d9, d11, interpolatedU2, interpolatedV2);
            instance.addVertexWithUV(d7, d10, d11, interpolatedU2, interpolatedV);
            instance.addVertexWithUV(d8, d10, d11, interpolatedU, interpolatedV);
            instance.addVertexWithUV(d7, d9, d11, interpolatedU2, interpolatedV2);
            instance.addVertexWithUV(d8, d9, d11, interpolatedU, interpolatedV2);
            instance.addVertexWithUV(d8, d10, d11, interpolatedU, interpolatedV);
            instance.addVertexWithUV(d7, d10, d11, interpolatedU2, interpolatedV);
        }

        return true;
    }

    public boolean renderBlockBeacon(BlockBeacon blockBeacon, int n, int n2, int n3) {
        float f = 0.1875F;
        this.setOverrideBlockTexture(this.getBlockIcon(Blocks.glass));
        this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        this.renderStandardBlock(blockBeacon, n, n2, n3);
        this.renderAllFaces = true;
        this.setOverrideBlockTexture(this.getBlockIcon(Blocks.obsidian));
        this.setRenderBounds(0.125, 0.00625F, 0.125, 0.875, f, 0.875);
        this.renderStandardBlock(blockBeacon, n, n2, n3);
        IIcon blockIcon = this.getBlockIcon(Blocks.beacon);
        if (Config.isConnectedTextures()) {
            blockIcon = ConnectedTextures.getConnectedTexture(this.blockAccess, blockBeacon, n, n2, n3, -1, blockIcon);
        }

        this.setOverrideBlockTexture(blockIcon);
        this.setRenderBounds(0.1875, f, 0.1875, 0.8125, 0.875, 0.8125);
        this.renderStandardBlock(blockBeacon, n, n2, n3);
        this.renderAllFaces = false;
        this.clearOverrideBlockTexture();
        return true;
    }

    public boolean renderBlockCactus(Block block, int n, int n2, int n3) {
        int colorMultiplier = block.colorMultiplier(this.blockAccess, n, n2, n3);
        float f = (colorMultiplier >> 16 & 0xFF) / 255.0F;
        float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
        float f3 = (colorMultiplier & 0xFF) / 255.0F;
        if (EntityRenderer.anaglyphEnable) {
            float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
            f = f4;
            f2 = f5;
            f3 = f6;
        }

        return this.renderBlockCactusImpl(block, n, n2, n3, f, f2, f3);
    }

    public boolean renderBlockCactusImpl(Block block, int n, int n2, int n3, float f, float f2, float f3) {
        Tessellator instance = Tessellator.instance;
        boolean flag = false;
        float f4 = 0.5F;
        float f5 = 1.0F;
        float f6 = 0.8F;
        float f7 = 0.6F;
        if (Config.isShaders()) {
            f4 = this.lightLevel05;
            f6 = this.lightLevel08;
            f7 = this.lightLevel06;
        }

        float f8 = f4 * f;
        float f9 = f5 * f;
        float f10 = f6 * f;
        float f11 = f7 * f;
        float f12 = f4 * f2;
        float f13 = f5 * f2;
        float f14 = f6 * f2;
        float f15 = f7 * f2;
        float f16 = f4 * f3;
        float f17 = f5 * f3;
        float f18 = f6 * f3;
        float f19 = f7 * f3;
        float f20 = 0.0625F;
        int mixedBrightnessForBlock = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2 - 1, n3, 0)) {
            instance.setBrightness(this.renderMinY > 0.0 ? mixedBrightnessForBlock : block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3));
            instance.setColorOpaque_F(f8, f12, f16);
            this.renderFaceYNeg(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 0));
        }

        if (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, n, n2 + 1, n3, 1)) {
            instance.setBrightness(this.renderMaxY < 1.0 ? mixedBrightnessForBlock : block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3));
            instance.setColorOpaque_F(f9, f13, f17);
            this.renderFaceYPos(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 1));
        }

        instance.setBrightness(mixedBrightnessForBlock);
        instance.setColorOpaque_F(f10, f14, f18);
        instance.addTranslation(0.0F, 0.0F, f20);
        this.renderFaceZNeg(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 2));
        instance.addTranslation(0.0F, 0.0F, -f20);
        instance.addTranslation(0.0F, 0.0F, -f20);
        this.renderFaceZPos(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 3));
        instance.addTranslation(0.0F, 0.0F, f20);
        instance.setColorOpaque_F(f11, f15, f19);
        instance.addTranslation(f20, 0.0F, 0.0F);
        this.renderFaceXNeg(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 4));
        instance.addTranslation(-f20, 0.0F, 0.0F);
        instance.addTranslation(-f20, 0.0F, 0.0F);
        this.renderFaceXPos(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 5));
        instance.addTranslation(f20, 0.0F, 0.0F);
        return true;
    }

    public boolean renderBlockFence(BlockFence blockFence, int n, int n2, int n3) {
        boolean flag = false;
        float f = 0.375F;
        float f2 = 0.625F;
        this.setRenderBounds(f, 0.0, f, f2, 1.0, f2);
        this.renderStandardBlock(blockFence, n, n2, n3);
        flag = true;
        boolean flag2 = false;
        boolean flag3 = false;
        if (blockFence.canConnectFenceTo(this.blockAccess, n - 1, n2, n3) || blockFence.canConnectFenceTo(this.blockAccess, n + 1, n2, n3)) {
            flag2 = true;
        }

        if (blockFence.canConnectFenceTo(this.blockAccess, n, n2, n3 - 1) || blockFence.canConnectFenceTo(this.blockAccess, n, n2, n3 + 1)) {
            flag3 = true;
        }

        boolean canConnectFenceTo = blockFence.canConnectFenceTo(this.blockAccess, n - 1, n2, n3);
        boolean canConnectFenceTo2 = blockFence.canConnectFenceTo(this.blockAccess, n + 1, n2, n3);
        boolean canConnectFenceTo3 = blockFence.canConnectFenceTo(this.blockAccess, n, n2, n3 - 1);
        boolean canConnectFenceTo4 = blockFence.canConnectFenceTo(this.blockAccess, n, n2, n3 + 1);
        if (!flag2 && !flag3) {
            flag2 = true;
        }

        f = 0.4375F;
        f2 = 0.5625F;
        float f3 = 0.75F;
        float f4 = 0.9375F;
        float f5 = canConnectFenceTo ? 0.0F : f;
        float f6 = canConnectFenceTo2 ? 1.0F : f2;
        float f7 = canConnectFenceTo3 ? 0.0F : f;
        float f8 = canConnectFenceTo4 ? 1.0F : f2;
        this.flag = true;
        if (flag2) {
            this.setRenderBounds(f5, f3, f, f6, f4, f2);
            this.renderStandardBlock(blockFence, n, n2, n3);
            flag = true;
        }

        if (flag3) {
            this.setRenderBounds(f, f3, f7, f2, f4, f8);
            this.renderStandardBlock(blockFence, n, n2, n3);
            flag = true;
        }

        f3 = 0.375F;
        f4 = 0.5625F;
        if (flag2) {
            this.setRenderBounds(f5, f3, f, f6, f4, f2);
            this.renderStandardBlock(blockFence, n, n2, n3);
            flag = true;
        }

        if (flag3) {
            this.setRenderBounds(f, f3, f7, f2, f4, f8);
            this.renderStandardBlock(blockFence, n, n2, n3);
            flag = true;
        }

        this.flag = false;
        blockFence.setBlockBoundsBasedOnState(this.blockAccess, n, n2, n3);
        if (Config.isBetterSnow() && this.hasSnowNeighbours(n, n2, n3)) {
            this.renderSnow(n, n2, n3, Blocks.snow_layer.getBlockBoundsMaxY());
        }

        return flag;
    }

    public boolean renderBlockWall(BlockWall blockWall, int n, int n2, int n3) {
        boolean canConnectWallTo = blockWall.canConnectWallTo(this.blockAccess, n - 1, n2, n3);
        boolean canConnectWallTo2 = blockWall.canConnectWallTo(this.blockAccess, n + 1, n2, n3);
        boolean canConnectWallTo3 = blockWall.canConnectWallTo(this.blockAccess, n, n2, n3 - 1);
        boolean canConnectWallTo4 = blockWall.canConnectWallTo(this.blockAccess, n, n2, n3 + 1);
        boolean flag = canConnectWallTo3 && canConnectWallTo4 && !canConnectWallTo && !canConnectWallTo2;
        boolean flag2 = !canConnectWallTo3 && !canConnectWallTo4 && canConnectWallTo && canConnectWallTo2;
        boolean isAirBlock = this.blockAccess.isAirBlock(n, n2 + 1, n3);
        if ((flag || flag2) && isAirBlock) {
            if (flag) {
                this.setRenderBounds(0.3125, 0.0, 0.0, 0.6875, 0.8125, 1.0);
                this.renderStandardBlock(blockWall, n, n2, n3);
            } else {
                this.setRenderBounds(0.0, 0.0, 0.3125, 1.0, 0.8125, 0.6875);
                this.renderStandardBlock(blockWall, n, n2, n3);
            }
        } else {
            this.setRenderBounds(0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
            this.renderStandardBlock(blockWall, n, n2, n3);
            if (canConnectWallTo) {
                this.setRenderBounds(0.0, 0.0, 0.3125, 0.25, 0.8125, 0.6875);
                this.renderStandardBlock(blockWall, n, n2, n3);
            }

            if (canConnectWallTo2) {
                this.setRenderBounds(0.75, 0.0, 0.3125, 1.0, 0.8125, 0.6875);
                this.renderStandardBlock(blockWall, n, n2, n3);
            }

            if (canConnectWallTo3) {
                this.setRenderBounds(0.3125, 0.0, 0.0, 0.6875, 0.8125, 0.25);
                this.renderStandardBlock(blockWall, n, n2, n3);
            }

            if (canConnectWallTo4) {
                this.setRenderBounds(0.3125, 0.0, 0.75, 0.6875, 0.8125, 1.0);
                this.renderStandardBlock(blockWall, n, n2, n3);
            }
        }

        blockWall.setBlockBoundsBasedOnState(this.blockAccess, n, n2, n3);
        if (Config.isBetterSnow() && this.hasSnowNeighbours(n, n2, n3)) {
            this.renderSnow(n, n2, n3, Blocks.snow_layer.getBlockBoundsMaxY());
        }

        return true;
    }

    public boolean renderBlockDragonEgg(BlockDragonEgg blockDragonEgg, int n, int n2, int n3) {
        boolean flag = false;
        int n4 = 0;

        for (int i = 0; i < 8; i++) {
            int n5 = 0;
            int n6 = 1;
            if (i == 0) {
                n5 = 2;
            }

            if (i == 1) {
                n5 = 3;
            }

            if (i == 2) {
                n5 = 4;
            }

            if (i == 3) {
                n5 = 5;
                n6 = 2;
            }

            if (i == 4) {
                n5 = 6;
                n6 = 3;
            }

            if (i == 5) {
                n5 = 7;
                n6 = 5;
            }

            if (i == 6) {
                n5 = 6;
                n6 = 2;
            }

            if (i == 7) {
                n5 = 3;
            }

            float f = n5 / 16.0F;
            float f2 = 1.0F - n4 / 16.0F;
            float f3 = 1.0F - (n4 + n6) / 16.0F;
            n4 += n6;
            this.setRenderBounds(0.5F - f, f3, 0.5F - f, 0.5F + f, f2, 0.5F + f);
            this.renderStandardBlock(blockDragonEgg, n, n2, n3);
        }

        flag = true;
        this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        return flag;
    }

    public boolean renderBlockFenceGate(BlockFenceGate blockFenceGate, int n, int n2, int n3) {
        boolean flag = true;
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        boolean isFenceGateOpen = BlockFenceGate.isFenceGateOpen(blockMetadata);
        int direction = BlockDirectional.getDirection(blockMetadata);
        float f = 0.375F;
        float f2 = 0.5625F;
        float f3 = 0.75F;
        float f4 = 0.9375F;
        float f5 = 0.3125F;
        float f6 = 1.0F;
        if ((direction == 2 || direction == 0)
                && this.blockAccess.getBlock(n - 1, n2, n3) == Blocks.cobblestone_wall
                && this.blockAccess.getBlock(n + 1, n2, n3) == Blocks.cobblestone_wall
            || (direction == 3 || direction == 1)
                && this.blockAccess.getBlock(n, n2, n3 - 1) == Blocks.cobblestone_wall
                && this.blockAccess.getBlock(n, n2, n3 + 1) == Blocks.cobblestone_wall) {
            f -= 0.1875F;
            f2 -= 0.1875F;
            f3 -= 0.1875F;
            f4 -= 0.1875F;
            f5 -= 0.1875F;
            f6 -= 0.1875F;
        }

        this.renderAllFaces = true;
        if (direction != 3 && direction != 1) {
            float f7 = 0.0F;
            float f8 = 0.125F;
            float f9 = 0.4375F;
            float f10 = 0.5625F;
            this.setRenderBounds(f7, f5, f9, f8, f6, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
            f7 = 0.875F;
            f8 = 1.0F;
            this.setRenderBounds(f7, f5, f9, f8, f6, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
        } else {
            this.uvRotateTop = 1;
            float f7 = 0.4375F;
            float f8 = 0.5625F;
            float f9 = 0.0F;
            float f10 = 0.125F;
            this.setRenderBounds(f7, f5, f9, f8, f6, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
            f9 = 0.875F;
            f10 = 1.0F;
            this.setRenderBounds(f7, f5, f9, f8, f6, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
            this.uvRotateTop = 0;
        }

        if (isFenceGateOpen) {
            if (direction == 2 || direction == 0) {
                this.uvRotateTop = 1;
            }

            if (direction == 3) {
                float f7 = 0.0F;
                float f8 = 0.125F;
                float f9 = 0.875F;
                float f10 = 1.0F;
                float f11 = 0.5625F;
                float f12 = 0.8125F;
                float f13 = 0.9375F;
                this.setRenderBounds(0.8125, f, 0.0, 0.9375, f4, 0.125);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.8125, f, 0.875, 0.9375, f4, 1.0);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.5625, f, 0.0, 0.8125, f2, 0.125);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.5625, f, 0.875, 0.8125, f2, 1.0);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.5625, f3, 0.0, 0.8125, f4, 0.125);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.5625, f3, 0.875, 0.8125, f4, 1.0);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
            } else if (direction == 1) {
                float f7 = 0.0F;
                float f8 = 0.125F;
                float f9 = 0.875F;
                float f10 = 1.0F;
                float f11 = 0.0625F;
                float f12 = 0.1875F;
                float f13 = 0.4375F;
                this.setRenderBounds(0.0625, f, 0.0, 0.1875, f4, 0.125);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.0625, f, 0.875, 0.1875, f4, 1.0);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.1875, f, 0.0, 0.4375, f2, 0.125);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.1875, f, 0.875, 0.4375, f2, 1.0);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.1875, f3, 0.0, 0.4375, f4, 0.125);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.1875, f3, 0.875, 0.4375, f4, 1.0);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
            } else if (direction == 0) {
                float f7 = 0.0F;
                float f8 = 0.125F;
                float f9 = 0.875F;
                float f10 = 1.0F;
                float f11 = 0.5625F;
                float f12 = 0.8125F;
                float f13 = 0.9375F;
                this.setRenderBounds(0.0, f, 0.8125, 0.125, f4, 0.9375);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.875, f, 0.8125, 1.0, f4, 0.9375);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.0, f, 0.5625, 0.125, f2, 0.8125);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.875, f, 0.5625, 1.0, f2, 0.8125);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.0, f3, 0.5625, 0.125, f4, 0.8125);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.875, f3, 0.5625, 1.0, f4, 0.8125);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
            } else if (direction == 2) {
                float f7 = 0.0F;
                float f8 = 0.125F;
                float f9 = 0.875F;
                float f10 = 1.0F;
                float f11 = 0.0625F;
                float f12 = 0.1875F;
                float f13 = 0.4375F;
                this.setRenderBounds(0.0, f, 0.0625, 0.125, f4, 0.1875);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.875, f, 0.0625, 1.0, f4, 0.1875);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.0, f, 0.1875, 0.125, f2, 0.4375);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.875, f, 0.1875, 1.0, f2, 0.4375);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.0, f3, 0.1875, 0.125, f4, 0.4375);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
                this.setRenderBounds(0.875, f3, 0.1875, 1.0, f4, 0.4375);
                this.renderStandardBlock(blockFenceGate, n, n2, n3);
            }
        } else if (direction != 3 && direction != 1) {
            float f7 = 0.375F;
            float f8 = 0.5F;
            float f9 = 0.4375F;
            float f10 = 0.5625F;
            this.setRenderBounds(f7, f, f9, f8, f4, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
            f7 = 0.5F;
            f8 = 0.625F;
            this.setRenderBounds(f7, f, f9, f8, f4, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
            f7 = 0.625F;
            f8 = 0.875F;
            this.setRenderBounds(f7, f, f9, f8, f2, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
            this.setRenderBounds(f7, f3, f9, f8, f4, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
            f7 = 0.125F;
            f8 = 0.375F;
            this.setRenderBounds(f7, f, f9, f8, f2, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
            this.setRenderBounds(f7, f3, f9, f8, f4, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
        } else {
            this.uvRotateTop = 1;
            float f7 = 0.4375F;
            float f8 = 0.5625F;
            float f9 = 0.375F;
            float f10 = 0.5F;
            this.setRenderBounds(f7, f, f9, f8, f4, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
            f9 = 0.5F;
            f10 = 0.625F;
            this.setRenderBounds(f7, f, f9, f8, f4, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
            f9 = 0.625F;
            f10 = 0.875F;
            this.setRenderBounds(f7, f, f9, f8, f2, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
            this.setRenderBounds(f7, f3, f9, f8, f4, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
            f9 = 0.125F;
            f10 = 0.375F;
            this.setRenderBounds(f7, f, f9, f8, f2, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
            this.setRenderBounds(f7, f3, f9, f8, f4, f10);
            this.renderStandardBlock(blockFenceGate, n, n2, n3);
        }

        if (Config.isBetterSnow() && this.hasSnowNeighbours(n, n2, n3)) {
            this.renderSnow(n, n2, n3, Blocks.snow_layer.getBlockBoundsMaxY());
        }

        this.renderAllFaces = false;
        this.uvRotateTop = 0;
        this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        return flag;
    }

    public boolean renderBlockHopper(BlockHopper blockHopper, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        instance.setBrightness(blockHopper.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
        int colorMultiplier = blockHopper.colorMultiplier(this.blockAccess, n, n2, n3);
        float f = (colorMultiplier >> 16 & 0xFF) / 255.0F;
        float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
        float f3 = (colorMultiplier & 0xFF) / 255.0F;
        if (EntityRenderer.anaglyphEnable) {
            float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
            f = f4;
            f2 = f5;
            f3 = f6;
        }

        instance.setColorOpaque_F(f, f2, f3);
        return this.renderBlockHopperMetadata(blockHopper, n, n2, n3, this.blockAccess.getBlockMetadata(n, n2, n3), false);
    }

    public boolean renderBlockHopperMetadata(BlockHopper blockHopper, int n, int n2, int n3, int n4, boolean flag) {
        Tessellator instance = Tessellator.instance;
        int directionFromMetadata = BlockHopper.getDirectionFromMetadata(n4);
        double d = 0.625;
        this.setRenderBounds(0.0, d, 0.0, 1.0, 1.0, 1.0);
        if (flag) {
            instance.startDrawingQuads();
            instance.setNormal(0.0F, -1.0F, 0.0F);
            this.renderFaceYNeg(blockHopper, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(blockHopper, 0, n4));
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(0.0F, 1.0F, 0.0F);
            this.renderFaceYPos(blockHopper, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(blockHopper, 1, n4));
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(0.0F, 0.0F, -1.0F);
            this.renderFaceZNeg(blockHopper, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(blockHopper, 2, n4));
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(0.0F, 0.0F, 1.0F);
            this.renderFaceZPos(blockHopper, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(blockHopper, 3, n4));
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(-1.0F, 0.0F, 0.0F);
            this.renderFaceXNeg(blockHopper, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(blockHopper, 4, n4));
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(1.0F, 0.0F, 0.0F);
            this.renderFaceXPos(blockHopper, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(blockHopper, 5, n4));
            instance.draw();
        } else {
            this.renderStandardBlock(blockHopper, n, n2, n3);
        }

        if (!flag) {
            instance.setBrightness(blockHopper.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3));
            int colorMultiplier = blockHopper.colorMultiplier(this.blockAccess, n, n2, n3);
            float f = (colorMultiplier >> 16 & 0xFF) / 255.0F;
            float f2 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
            float f3 = (colorMultiplier & 0xFF) / 255.0F;
            if (EntityRenderer.anaglyphEnable) {
                float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
                float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
                float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
                f = f4;
                f2 = f5;
                f3 = f6;
            }

            instance.setColorOpaque_F(f, f2, f3);
        }

        IIcon hopperIcon = BlockHopper.getHopperIcon("hopper_outside");
        IIcon hopperIcon2 = BlockHopper.getHopperIcon("hopper_inside");
        float f = 0.125F;
        if (flag) {
            instance.startDrawingQuads();
            instance.setNormal(1.0F, 0.0F, 0.0F);
            this.renderFaceXPos(blockHopper, -1.0F + f, 0.0, 0.0, hopperIcon);
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(-1.0F, 0.0F, 0.0F);
            this.renderFaceXNeg(blockHopper, 1.0F - f, 0.0, 0.0, hopperIcon);
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(0.0F, 0.0F, 1.0F);
            this.renderFaceZPos(blockHopper, 0.0, 0.0, -1.0F + f, hopperIcon);
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(0.0F, 0.0F, -1.0F);
            this.renderFaceZNeg(blockHopper, 0.0, 0.0, 1.0F - f, hopperIcon);
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(0.0F, 1.0F, 0.0F);
            this.renderFaceYPos(blockHopper, 0.0, -1.0 + d, 0.0, hopperIcon2);
            instance.draw();
        } else {
            this.renderFaceXPos(blockHopper, n - 1.0F + f, n2, n3, hopperIcon);
            this.renderFaceXNeg(blockHopper, n + 1.0F - f, n2, n3, hopperIcon);
            this.renderFaceZPos(blockHopper, n, n2, n3 - 1.0F + f, hopperIcon);
            this.renderFaceZNeg(blockHopper, n, n2, n3 + 1.0F - f, hopperIcon);
            this.renderFaceYPos(blockHopper, n, n2 - 1.0F + d, n3, hopperIcon2);
        }

        this.setOverrideBlockTexture(hopperIcon);
        double d2 = 0.25;
        double d3 = 0.25;
        this.setRenderBounds(d2, d3, d2, 1.0 - d2, d - 0.002, 1.0 - d2);
        if (flag) {
            instance.startDrawingQuads();
            instance.setNormal(1.0F, 0.0F, 0.0F);
            this.renderFaceXPos(blockHopper, 0.0, 0.0, 0.0, hopperIcon);
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(-1.0F, 0.0F, 0.0F);
            this.renderFaceXNeg(blockHopper, 0.0, 0.0, 0.0, hopperIcon);
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(0.0F, 0.0F, 1.0F);
            this.renderFaceZPos(blockHopper, 0.0, 0.0, 0.0, hopperIcon);
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(0.0F, 0.0F, -1.0F);
            this.renderFaceZNeg(blockHopper, 0.0, 0.0, 0.0, hopperIcon);
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(0.0F, 1.0F, 0.0F);
            this.renderFaceYPos(blockHopper, 0.0, 0.0, 0.0, hopperIcon);
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(0.0F, -1.0F, 0.0F);
            this.renderFaceYNeg(blockHopper, 0.0, 0.0, 0.0, hopperIcon);
            instance.draw();
        } else {
            this.renderStandardBlock(blockHopper, n, n2, n3);
        }

        if (!flag) {
            double d4 = 0.375;
            double d5 = 0.25;
            this.setOverrideBlockTexture(hopperIcon);
            if (directionFromMetadata == 0) {
                this.setRenderBounds(d4, 0.0, d4, 1.0 - d4, 0.25, 1.0 - d4);
                this.renderStandardBlock(blockHopper, n, n2, n3);
            }

            if (directionFromMetadata == 2) {
                this.setRenderBounds(d4, d3, 0.0, 1.0 - d4, d3 + d5, d2);
                this.renderStandardBlock(blockHopper, n, n2, n3);
            }

            if (directionFromMetadata == 3) {
                this.setRenderBounds(d4, d3, 1.0 - d2, 1.0 - d4, d3 + d5, 1.0);
                this.renderStandardBlock(blockHopper, n, n2, n3);
            }

            if (directionFromMetadata == 4) {
                this.setRenderBounds(0.0, d3, d4, d2, d3 + d5, 1.0 - d4);
                this.renderStandardBlock(blockHopper, n, n2, n3);
            }

            if (directionFromMetadata == 5) {
                this.setRenderBounds(1.0 - d2, d3, d4, 1.0, d3 + d5, 1.0 - d4);
                this.renderStandardBlock(blockHopper, n, n2, n3);
            }
        }

        this.clearOverrideBlockTexture();
        return true;
    }

    public boolean renderBlockStairs(BlockStairs blockStairs, int n, int n2, int n3) {
        blockStairs.setBaseBounds(this.blockAccess, n, n2, n3);
        this.setRenderBoundsFromBlock(blockStairs);
        this.renderStandardBlock(blockStairs, n, n2, n3);
        this.flag = true;
        boolean checkIBlockAccess = blockStairs.checkIBlockAccess(this.blockAccess, n, n2, n3);
        this.setRenderBoundsFromBlock(blockStairs);
        this.renderStandardBlock(blockStairs, n, n2, n3);
        if (checkIBlockAccess && blockStairs.checkIBlockAccess2(this.blockAccess, n, n2, n3)) {
            this.setRenderBoundsFromBlock(blockStairs);
            this.renderStandardBlock(blockStairs, n, n2, n3);
        }

        this.flag = false;
        return true;
    }

    public boolean renderBlockDoor(Block block, int n, int n2, int n3) {
        Tessellator instance = Tessellator.instance;
        int blockMetadata = this.blockAccess.getBlockMetadata(n, n2, n3);
        if ((blockMetadata & 8) != 0) {
            if (this.blockAccess.getBlock(n, n2 - 1, n3) != block) {
                return false;
            }
        } else if (this.blockAccess.getBlock(n, n2 + 1, n3) != block) {
            return false;
        }

        boolean flag = false;
        float f = 0.5F;
        float f2 = 1.0F;
        float f3 = 0.8F;
        float f4 = 0.6F;
        if (Config.isShaders()) {
            f = this.lightLevel05;
            f3 = this.lightLevel08;
            f4 = this.lightLevel06;
        }

        int mixedBrightnessForBlock = block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3);
        instance.setBrightness(this.renderMinY > 0.0 ? mixedBrightnessForBlock : block.getMixedBrightnessForBlock(this.blockAccess, n, n2 - 1, n3));
        instance.setColorOpaque_F(f, f, f);
        this.renderFaceYNeg(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 0));
        flag = true;
        instance.setBrightness(this.renderMaxY < 1.0 ? mixedBrightnessForBlock : block.getMixedBrightnessForBlock(this.blockAccess, n, n2 + 1, n3));
        instance.setColorOpaque_F(f2, f2, f2);
        this.renderFaceYPos(block, n, n2, n3, this.getBlockIcon(block, this.blockAccess, n, n2, n3, 1));
        flag = true;
        instance.setBrightness(this.renderMinZ > 0.0 ? mixedBrightnessForBlock : block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 - 1));
        instance.setColorOpaque_F(f3, f3, f3);
        IIcon blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 2);
        this.renderFaceZNeg(block, n, n2, n3, blockIcon);
        flag = true;
        this.flipTexture = false;
        instance.setBrightness(this.renderMaxZ < 1.0 ? mixedBrightnessForBlock : block.getMixedBrightnessForBlock(this.blockAccess, n, n2, n3 + 1));
        instance.setColorOpaque_F(f3, f3, f3);
        blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 3);
        this.renderFaceZPos(block, n, n2, n3, blockIcon);
        flag = true;
        this.flipTexture = false;
        instance.setBrightness(this.renderMinX > 0.0 ? mixedBrightnessForBlock : block.getMixedBrightnessForBlock(this.blockAccess, n - 1, n2, n3));
        instance.setColorOpaque_F(f4, f4, f4);
        blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 4);
        this.renderFaceXNeg(block, n, n2, n3, blockIcon);
        flag = true;
        this.flipTexture = false;
        instance.setBrightness(this.renderMaxX < 1.0 ? mixedBrightnessForBlock : block.getMixedBrightnessForBlock(this.blockAccess, n + 1, n2, n3));
        instance.setColorOpaque_F(f4, f4, f4);
        blockIcon = this.getBlockIcon(block, this.blockAccess, n, n2, n3, 5);
        this.renderFaceXPos(block, n, n2, n3, blockIcon);
        flag = true;
        this.flipTexture = false;
        return flag;
    }

    public void renderFaceYNeg(Block block, double d, double d2, double d3, IIcon icon) {
        Tessellator instance = Tessellator.instance;
        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
        }

        if (Config.isConnectedTextures() && this.overrideBlockTexture == null) {
            icon = ConnectedTextures.getConnectedTexture(this.blockAccess, block, (int)d, (int)d2, (int)d3, 0, icon);
        }

        boolean flag = false;
        if (Config.isNaturalTextures() && this.overrideBlockTexture == null && this.uvRotateBottom == 0) {
            NaturalProperties naturalProperties = NaturalTextures.getNaturalProperties(icon);
            if (naturalProperties != null) {
                int random = Config.getRandom((int)d, (int)d2, (int)d3, 0);
                if (naturalProperties.rotation > 1) {
                    this.uvRotateBottom = random & 3;
                }

                if (naturalProperties.rotation == 2) {
                    this.uvRotateBottom = this.uvRotateBottom / 2 * 3;
                }

                if (naturalProperties.flip) {
                    this.flipTexture = (random & 4) != 0;
                }

                flag = true;
            }
        }

        double interpolatedU = icon.getInterpolatedU(this.renderMinX * 16.0);
        double interpolatedU2 = icon.getInterpolatedU(this.renderMaxX * 16.0);
        double interpolatedV = icon.getInterpolatedV(this.renderMinZ * 16.0);
        double interpolatedV2 = icon.getInterpolatedV(this.renderMaxZ * 16.0);
        if (this.renderMinX < 0.0 || this.renderMaxX > 1.0) {
            interpolatedU = icon.getMinU();
            interpolatedU2 = icon.getMaxU();
        }

        if (this.renderMinZ < 0.0 || this.renderMaxZ > 1.0) {
            interpolatedV = icon.getMinV();
            interpolatedV2 = icon.getMaxV();
        }

        if (this.flipTexture) {
            double d4 = interpolatedU;
            interpolatedU = interpolatedU2;
            interpolatedU2 = d4;
        }

        double d4 = interpolatedU2;
        double d5 = interpolatedU;
        double d6 = interpolatedV;
        double d7 = interpolatedV2;
        if (this.uvRotateBottom == 2) {
            interpolatedU = icon.getInterpolatedU(this.renderMinZ * 16.0);
            interpolatedV = icon.getInterpolatedV(16.0 - this.renderMaxX * 16.0);
            interpolatedU2 = icon.getInterpolatedU(this.renderMaxZ * 16.0);
            interpolatedV2 = icon.getInterpolatedV(16.0 - this.renderMinX * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d6 = interpolatedV;
            d7 = interpolatedV2;
            d4 = interpolatedU;
            d5 = interpolatedU2;
            interpolatedV = interpolatedV2;
            interpolatedV2 = d6;
        } else if (this.uvRotateBottom == 1) {
            interpolatedU = icon.getInterpolatedU(16.0 - this.renderMaxZ * 16.0);
            interpolatedV = icon.getInterpolatedV(this.renderMinX * 16.0);
            interpolatedU2 = icon.getInterpolatedU(16.0 - this.renderMinZ * 16.0);
            interpolatedV2 = icon.getInterpolatedV(this.renderMaxX * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d4 = interpolatedU2;
            d5 = interpolatedU;
            interpolatedU = interpolatedU2;
            interpolatedU2 = d5;
            d6 = interpolatedV2;
            d7 = interpolatedV;
        } else if (this.uvRotateBottom == 3) {
            interpolatedU = icon.getInterpolatedU(16.0 - this.renderMinX * 16.0);
            interpolatedU2 = icon.getInterpolatedU(16.0 - this.renderMaxX * 16.0);
            interpolatedV = icon.getInterpolatedV(16.0 - this.renderMinZ * 16.0);
            interpolatedV2 = icon.getInterpolatedV(16.0 - this.renderMaxZ * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d4 = interpolatedU2;
            d5 = interpolatedU;
            d6 = interpolatedV;
            d7 = interpolatedV2;
        }

        if (flag) {
            this.uvRotateBottom = 0;
            this.flipTexture = false;
        }

        double d8 = d + this.renderMinX;
        double d9 = d + this.renderMaxX;
        double d10 = d2 + this.renderMinY;
        double d11 = d3 + this.renderMinZ;
        double d12 = d3 + this.renderMaxZ;
        if (this.renderFromInside) {
            d8 = d + this.renderMaxX;
            d9 = d + this.renderMinX;
        }

        if (this.enableAO) {
            instance.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            instance.setBrightness(this.brightnessTopLeft);
            instance.addVertexWithUV(d8, d10, d12, d5, d7);
            instance.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            instance.setBrightness(this.brightnessBottomLeft);
            instance.addVertexWithUV(d8, d10, d11, interpolatedU, interpolatedV);
            instance.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            instance.setBrightness(this.brightnessBottomRight);
            instance.addVertexWithUV(d9, d10, d11, d4, d6);
            instance.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            instance.setBrightness(this.brightnessTopRight);
            instance.addVertexWithUV(d9, d10, d12, interpolatedU2, interpolatedV2);
        } else {
            instance.addVertexWithUV(d8, d10, d12, d5, d7);
            instance.addVertexWithUV(d8, d10, d11, interpolatedU, interpolatedV);
            instance.addVertexWithUV(d9, d10, d11, d4, d6);
            instance.addVertexWithUV(d9, d10, d12, interpolatedU2, interpolatedV2);
        }
    }

    public void renderFaceYPos(Block block, double d, double d2, double d3, IIcon icon) {
        Tessellator instance = Tessellator.instance;
        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
        }

        if (Config.isConnectedTextures() && this.overrideBlockTexture == null) {
            icon = ConnectedTextures.getConnectedTexture(this.blockAccess, block, (int)d, (int)d2, (int)d3, 1, icon);
        }

        boolean flag = false;
        if (Config.isNaturalTextures() && this.overrideBlockTexture == null && this.uvRotateTop == 0) {
            NaturalProperties naturalProperties = NaturalTextures.getNaturalProperties(icon);
            if (naturalProperties != null) {
                int random = Config.getRandom((int)d, (int)d2, (int)d3, 1);
                if (naturalProperties.rotation > 1) {
                    this.uvRotateTop = random & 3;
                }

                if (naturalProperties.rotation == 2) {
                    this.uvRotateTop = this.uvRotateTop / 2 * 3;
                }

                if (naturalProperties.flip) {
                    this.flipTexture = (random & 4) != 0;
                }

                flag = true;
            }
        }

        double interpolatedU = icon.getInterpolatedU(this.renderMinX * 16.0);
        double interpolatedU2 = icon.getInterpolatedU(this.renderMaxX * 16.0);
        double interpolatedV = icon.getInterpolatedV(this.renderMinZ * 16.0);
        double interpolatedV2 = icon.getInterpolatedV(this.renderMaxZ * 16.0);
        if (this.flipTexture) {
            double d4 = interpolatedU;
            interpolatedU = interpolatedU2;
            interpolatedU2 = d4;
        }

        if (this.renderMinX < 0.0 || this.renderMaxX > 1.0) {
            interpolatedU = icon.getMinU();
            interpolatedU2 = icon.getMaxU();
        }

        if (this.renderMinZ < 0.0 || this.renderMaxZ > 1.0) {
            interpolatedV = icon.getMinV();
            interpolatedV2 = icon.getMaxV();
        }

        double d4 = interpolatedU2;
        double d5 = interpolatedU;
        double d6 = interpolatedV;
        double d7 = interpolatedV2;
        if (this.uvRotateTop == 1) {
            interpolatedU = icon.getInterpolatedU(this.renderMinZ * 16.0);
            interpolatedV = icon.getInterpolatedV(16.0 - this.renderMaxX * 16.0);
            interpolatedU2 = icon.getInterpolatedU(this.renderMaxZ * 16.0);
            interpolatedV2 = icon.getInterpolatedV(16.0 - this.renderMinX * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d6 = interpolatedV;
            d7 = interpolatedV2;
            d4 = interpolatedU;
            d5 = interpolatedU2;
            interpolatedV = interpolatedV2;
            interpolatedV2 = d6;
        } else if (this.uvRotateTop == 2) {
            interpolatedU = icon.getInterpolatedU(16.0 - this.renderMaxZ * 16.0);
            interpolatedV = icon.getInterpolatedV(this.renderMinX * 16.0);
            interpolatedU2 = icon.getInterpolatedU(16.0 - this.renderMinZ * 16.0);
            interpolatedV2 = icon.getInterpolatedV(this.renderMaxX * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d4 = interpolatedU2;
            d5 = interpolatedU;
            interpolatedU = interpolatedU2;
            interpolatedU2 = d5;
            d6 = interpolatedV2;
            d7 = interpolatedV;
        } else if (this.uvRotateTop == 3) {
            interpolatedU = icon.getInterpolatedU(16.0 - this.renderMinX * 16.0);
            interpolatedU2 = icon.getInterpolatedU(16.0 - this.renderMaxX * 16.0);
            interpolatedV = icon.getInterpolatedV(16.0 - this.renderMinZ * 16.0);
            interpolatedV2 = icon.getInterpolatedV(16.0 - this.renderMaxZ * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d4 = interpolatedU2;
            d5 = interpolatedU;
            d6 = interpolatedV;
            d7 = interpolatedV2;
        }

        if (flag) {
            this.uvRotateTop = 0;
            this.flipTexture = false;
        }

        double d8 = d + this.renderMinX;
        double d9 = d + this.renderMaxX;
        double d10 = d2 + this.renderMaxY;
        double d11 = d3 + this.renderMinZ;
        double d12 = d3 + this.renderMaxZ;
        if (this.renderFromInside) {
            d8 = d + this.renderMaxX;
            d9 = d + this.renderMinX;
        }

        if (this.enableAO) {
            instance.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            instance.setBrightness(this.brightnessTopLeft);
            instance.addVertexWithUV(d9, d10, d12, interpolatedU2, interpolatedV2);
            instance.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            instance.setBrightness(this.brightnessBottomLeft);
            instance.addVertexWithUV(d9, d10, d11, d4, d6);
            instance.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            instance.setBrightness(this.brightnessBottomRight);
            instance.addVertexWithUV(d8, d10, d11, interpolatedU, interpolatedV);
            instance.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            instance.setBrightness(this.brightnessTopRight);
            instance.addVertexWithUV(d8, d10, d12, d5, d7);
        } else {
            instance.addVertexWithUV(d9, d10, d12, interpolatedU2, interpolatedV2);
            instance.addVertexWithUV(d9, d10, d11, d4, d6);
            instance.addVertexWithUV(d8, d10, d11, interpolatedU, interpolatedV);
            instance.addVertexWithUV(d8, d10, d12, d5, d7);
        }
    }

    public void renderFaceZNeg(Block block, double d, double d2, double d3, IIcon icon) {
        Tessellator instance = Tessellator.instance;
        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
        }

        if (Config.isConnectedTextures() && this.overrideBlockTexture == null) {
            icon = ConnectedTextures.getConnectedTexture(this.blockAccess, block, (int)d, (int)d2, (int)d3, 2, icon);
        }

        boolean flag = false;
        if (Config.isNaturalTextures() && this.overrideBlockTexture == null && this.uvRotateEast == 0) {
            NaturalProperties naturalProperties = NaturalTextures.getNaturalProperties(icon);
            if (naturalProperties != null) {
                int random = Config.getRandom((int)d, (int)d2, (int)d3, 2);
                if (naturalProperties.rotation > 1) {
                    this.uvRotateEast = random & 3;
                }

                if (naturalProperties.rotation == 2) {
                    this.uvRotateEast = this.uvRotateEast / 2 * 3;
                }

                if (naturalProperties.flip) {
                    this.flipTexture = (random & 4) != 0;
                }

                flag = true;
            }
        }

        double interpolatedU = icon.getInterpolatedU(this.renderMinX * 16.0);
        double interpolatedU2 = icon.getInterpolatedU(this.renderMaxX * 16.0);
        if (this.flag) {
            interpolatedU2 = icon.getInterpolatedU((1.0 - this.renderMinX) * 16.0);
            interpolatedU = icon.getInterpolatedU((1.0 - this.renderMaxX) * 16.0);
        }

        double interpolatedV = icon.getInterpolatedV(16.0 - this.renderMaxY * 16.0);
        double interpolatedV2 = icon.getInterpolatedV(16.0 - this.renderMinY * 16.0);
        if (this.flipTexture) {
            double d4 = interpolatedU;
            interpolatedU = interpolatedU2;
            interpolatedU2 = d4;
        }

        if (this.renderMinX < 0.0 || this.renderMaxX > 1.0) {
            interpolatedU = icon.getMinU();
            interpolatedU2 = icon.getMaxU();
        }

        if (this.renderMinY < 0.0 || this.renderMaxY > 1.0) {
            interpolatedV = icon.getMinV();
            interpolatedV2 = icon.getMaxV();
        }

        double d4 = interpolatedU2;
        double d5 = interpolatedU;
        double d6 = interpolatedV;
        double d7 = interpolatedV2;
        if (this.uvRotateEast == 2) {
            interpolatedU = icon.getInterpolatedU(this.renderMinY * 16.0);
            interpolatedU2 = icon.getInterpolatedU(this.renderMaxY * 16.0);
            interpolatedV = icon.getInterpolatedV(16.0 - this.renderMinX * 16.0);
            interpolatedV2 = icon.getInterpolatedV(16.0 - this.renderMaxX * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d6 = interpolatedV;
            d7 = interpolatedV2;
            d4 = interpolatedU;
            d5 = interpolatedU2;
            interpolatedV = interpolatedV2;
            interpolatedV2 = d6;
        } else if (this.uvRotateEast == 1) {
            interpolatedU = icon.getInterpolatedU(16.0 - this.renderMaxY * 16.0);
            interpolatedU2 = icon.getInterpolatedU(16.0 - this.renderMinY * 16.0);
            interpolatedV = icon.getInterpolatedV(this.renderMaxX * 16.0);
            interpolatedV2 = icon.getInterpolatedV(this.renderMinX * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d4 = interpolatedU2;
            d5 = interpolatedU;
            interpolatedU = interpolatedU2;
            interpolatedU2 = d5;
            d6 = interpolatedV2;
            d7 = interpolatedV;
        } else if (this.uvRotateEast == 3) {
            interpolatedU = icon.getInterpolatedU(16.0 - this.renderMinX * 16.0);
            interpolatedU2 = icon.getInterpolatedU(16.0 - this.renderMaxX * 16.0);
            interpolatedV = icon.getInterpolatedV(this.renderMaxY * 16.0);
            interpolatedV2 = icon.getInterpolatedV(this.renderMinY * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d4 = interpolatedU2;
            d5 = interpolatedU;
            d6 = interpolatedV;
            d7 = interpolatedV2;
        }

        if (flag) {
            this.uvRotateEast = 0;
            this.flipTexture = false;
        }

        double d8 = d + this.renderMinX;
        double d9 = d + this.renderMaxX;
        double d10 = d2 + this.renderMinY;
        double d11 = d2 + this.renderMaxY;
        double d12 = d3 + this.renderMinZ;
        if (this.renderFromInside) {
            d8 = d + this.renderMaxX;
            d9 = d + this.renderMinX;
        }

        if (this.enableAO) {
            instance.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            instance.setBrightness(this.brightnessTopLeft);
            instance.addVertexWithUV(d8, d11, d12, d4, d6);
            instance.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            instance.setBrightness(this.brightnessBottomLeft);
            instance.addVertexWithUV(d9, d11, d12, interpolatedU, interpolatedV);
            instance.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            instance.setBrightness(this.brightnessBottomRight);
            instance.addVertexWithUV(d9, d10, d12, d5, d7);
            instance.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            instance.setBrightness(this.brightnessTopRight);
            instance.addVertexWithUV(d8, d10, d12, interpolatedU2, interpolatedV2);
        } else {
            instance.addVertexWithUV(d8, d11, d12, d4, d6);
            instance.addVertexWithUV(d9, d11, d12, interpolatedU, interpolatedV);
            instance.addVertexWithUV(d9, d10, d12, d5, d7);
            instance.addVertexWithUV(d8, d10, d12, interpolatedU2, interpolatedV2);
        }
    }

    public void renderFaceZPos(Block block, double d, double d2, double d3, IIcon icon) {
        Tessellator instance = Tessellator.instance;
        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
        }

        if (Config.isConnectedTextures() && this.overrideBlockTexture == null) {
            icon = ConnectedTextures.getConnectedTexture(this.blockAccess, block, (int)d, (int)d2, (int)d3, 3, icon);
        }

        boolean flag = false;
        if (Config.isNaturalTextures() && this.overrideBlockTexture == null && this.uvRotateWest == 0) {
            NaturalProperties naturalProperties = NaturalTextures.getNaturalProperties(icon);
            if (naturalProperties != null) {
                int random = Config.getRandom((int)d, (int)d2, (int)d3, 3);
                if (naturalProperties.rotation > 1) {
                    this.uvRotateWest = random & 3;
                }

                if (naturalProperties.rotation == 2) {
                    this.uvRotateWest = this.uvRotateWest / 2 * 3;
                }

                if (naturalProperties.flip) {
                    this.flipTexture = (random & 4) != 0;
                }

                flag = true;
            }
        }

        double interpolatedU = icon.getInterpolatedU(this.renderMinX * 16.0);
        double interpolatedU2 = icon.getInterpolatedU(this.renderMaxX * 16.0);
        double interpolatedV = icon.getInterpolatedV(16.0 - this.renderMaxY * 16.0);
        double interpolatedV2 = icon.getInterpolatedV(16.0 - this.renderMinY * 16.0);
        if (this.flipTexture) {
            double d4 = interpolatedU;
            interpolatedU = interpolatedU2;
            interpolatedU2 = d4;
        }

        if (this.renderMinX < 0.0 || this.renderMaxX > 1.0) {
            interpolatedU = icon.getMinU();
            interpolatedU2 = icon.getMaxU();
        }

        if (this.renderMinY < 0.0 || this.renderMaxY > 1.0) {
            interpolatedV = icon.getMinV();
            interpolatedV2 = icon.getMaxV();
        }

        double d4 = interpolatedU2;
        double d5 = interpolatedU;
        double d6 = interpolatedV;
        double d7 = interpolatedV2;
        if (this.uvRotateWest == 1) {
            interpolatedU = icon.getInterpolatedU(this.renderMinY * 16.0);
            interpolatedV2 = icon.getInterpolatedV(16.0 - this.renderMinX * 16.0);
            interpolatedU2 = icon.getInterpolatedU(this.renderMaxY * 16.0);
            interpolatedV = icon.getInterpolatedV(16.0 - this.renderMaxX * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d6 = interpolatedV;
            d7 = interpolatedV2;
            d4 = interpolatedU;
            d5 = interpolatedU2;
            interpolatedV = interpolatedV2;
            interpolatedV2 = d6;
        } else if (this.uvRotateWest == 2) {
            interpolatedU = icon.getInterpolatedU(16.0 - this.renderMaxY * 16.0);
            interpolatedV = icon.getInterpolatedV(this.renderMinX * 16.0);
            interpolatedU2 = icon.getInterpolatedU(16.0 - this.renderMinY * 16.0);
            interpolatedV2 = icon.getInterpolatedV(this.renderMaxX * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d4 = interpolatedU2;
            d5 = interpolatedU;
            interpolatedU = interpolatedU2;
            interpolatedU2 = d5;
            d6 = interpolatedV2;
            d7 = interpolatedV;
        } else if (this.uvRotateWest == 3) {
            interpolatedU = icon.getInterpolatedU(16.0 - this.renderMinX * 16.0);
            interpolatedU2 = icon.getInterpolatedU(16.0 - this.renderMaxX * 16.0);
            interpolatedV = icon.getInterpolatedV(this.renderMaxY * 16.0);
            interpolatedV2 = icon.getInterpolatedV(this.renderMinY * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d4 = interpolatedU2;
            d5 = interpolatedU;
            d6 = interpolatedV;
            d7 = interpolatedV2;
        }

        if (flag) {
            this.uvRotateWest = 0;
            this.flipTexture = false;
        }

        double d8 = d + this.renderMinX;
        double d9 = d + this.renderMaxX;
        double d10 = d2 + this.renderMinY;
        double d11 = d2 + this.renderMaxY;
        double d12 = d3 + this.renderMaxZ;
        if (this.renderFromInside) {
            d8 = d + this.renderMaxX;
            d9 = d + this.renderMinX;
        }

        if (this.enableAO) {
            instance.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            instance.setBrightness(this.brightnessTopLeft);
            instance.addVertexWithUV(d8, d11, d12, interpolatedU, interpolatedV);
            instance.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            instance.setBrightness(this.brightnessBottomLeft);
            instance.addVertexWithUV(d8, d10, d12, d5, d7);
            instance.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            instance.setBrightness(this.brightnessBottomRight);
            instance.addVertexWithUV(d9, d10, d12, interpolatedU2, interpolatedV2);
            instance.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            instance.setBrightness(this.brightnessTopRight);
            instance.addVertexWithUV(d9, d11, d12, d4, d6);
        } else {
            instance.addVertexWithUV(d8, d11, d12, interpolatedU, interpolatedV);
            instance.addVertexWithUV(d8, d10, d12, d5, d7);
            instance.addVertexWithUV(d9, d10, d12, interpolatedU2, interpolatedV2);
            instance.addVertexWithUV(d9, d11, d12, d4, d6);
        }
    }

    public void renderFaceXNeg(Block block, double d, double d2, double d3, IIcon icon) {
        Tessellator instance = Tessellator.instance;
        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
        }

        if (Config.isConnectedTextures() && this.overrideBlockTexture == null) {
            icon = ConnectedTextures.getConnectedTexture(this.blockAccess, block, (int)d, (int)d2, (int)d3, 4, icon);
        }

        boolean flag = false;
        if (Config.isNaturalTextures() && this.overrideBlockTexture == null && this.uvRotateNorth == 0) {
            NaturalProperties naturalProperties = NaturalTextures.getNaturalProperties(icon);
            if (naturalProperties != null) {
                int random = Config.getRandom((int)d, (int)d2, (int)d3, 4);
                if (naturalProperties.rotation > 1) {
                    this.uvRotateNorth = random & 3;
                }

                if (naturalProperties.rotation == 2) {
                    this.uvRotateNorth = this.uvRotateNorth / 2 * 3;
                }

                if (naturalProperties.flip) {
                    this.flipTexture = (random & 4) != 0;
                }

                flag = true;
            }
        }

        double interpolatedU = icon.getInterpolatedU(this.renderMinZ * 16.0);
        double interpolatedU2 = icon.getInterpolatedU(this.renderMaxZ * 16.0);
        double interpolatedV = icon.getInterpolatedV(16.0 - this.renderMaxY * 16.0);
        double interpolatedV2 = icon.getInterpolatedV(16.0 - this.renderMinY * 16.0);
        if (this.flipTexture) {
            double d4 = interpolatedU;
            interpolatedU = interpolatedU2;
            interpolatedU2 = d4;
        }

        if (this.renderMinZ < 0.0 || this.renderMaxZ > 1.0) {
            interpolatedU = icon.getMinU();
            interpolatedU2 = icon.getMaxU();
        }

        if (this.renderMinY < 0.0 || this.renderMaxY > 1.0) {
            interpolatedV = icon.getMinV();
            interpolatedV2 = icon.getMaxV();
        }

        double d4 = interpolatedU2;
        double d5 = interpolatedU;
        double d6 = interpolatedV;
        double d7 = interpolatedV2;
        if (this.uvRotateNorth == 1) {
            interpolatedU = icon.getInterpolatedU(this.renderMinY * 16.0);
            interpolatedV = icon.getInterpolatedV(16.0 - this.renderMaxZ * 16.0);
            interpolatedU2 = icon.getInterpolatedU(this.renderMaxY * 16.0);
            interpolatedV2 = icon.getInterpolatedV(16.0 - this.renderMinZ * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d6 = interpolatedV;
            d7 = interpolatedV2;
            d4 = interpolatedU;
            d5 = interpolatedU2;
            interpolatedV = interpolatedV2;
            interpolatedV2 = d6;
        } else if (this.uvRotateNorth == 2) {
            interpolatedU = icon.getInterpolatedU(16.0 - this.renderMaxY * 16.0);
            interpolatedV = icon.getInterpolatedV(this.renderMinZ * 16.0);
            interpolatedU2 = icon.getInterpolatedU(16.0 - this.renderMinY * 16.0);
            interpolatedV2 = icon.getInterpolatedV(this.renderMaxZ * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d4 = interpolatedU2;
            d5 = interpolatedU;
            interpolatedU = interpolatedU2;
            interpolatedU2 = d5;
            d6 = interpolatedV2;
            d7 = interpolatedV;
        } else if (this.uvRotateNorth == 3) {
            interpolatedU = icon.getInterpolatedU(16.0 - this.renderMinZ * 16.0);
            interpolatedU2 = icon.getInterpolatedU(16.0 - this.renderMaxZ * 16.0);
            interpolatedV = icon.getInterpolatedV(this.renderMaxY * 16.0);
            interpolatedV2 = icon.getInterpolatedV(this.renderMinY * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d4 = interpolatedU2;
            d5 = interpolatedU;
            d6 = interpolatedV;
            d7 = interpolatedV2;
        }

        if (flag) {
            this.uvRotateNorth = 0;
            this.flipTexture = false;
        }

        double d8 = d + this.renderMinX;
        double d9 = d2 + this.renderMinY;
        double d10 = d2 + this.renderMaxY;
        double d11 = d3 + this.renderMinZ;
        double d12 = d3 + this.renderMaxZ;
        if (this.renderFromInside) {
            d11 = d3 + this.renderMaxZ;
            d12 = d3 + this.renderMinZ;
        }

        if (this.enableAO) {
            instance.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            instance.setBrightness(this.brightnessTopLeft);
            instance.addVertexWithUV(d8, d10, d12, d4, d6);
            instance.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            instance.setBrightness(this.brightnessBottomLeft);
            instance.addVertexWithUV(d8, d10, d11, interpolatedU, interpolatedV);
            instance.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            instance.setBrightness(this.brightnessBottomRight);
            instance.addVertexWithUV(d8, d9, d11, d5, d7);
            instance.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            instance.setBrightness(this.brightnessTopRight);
            instance.addVertexWithUV(d8, d9, d12, interpolatedU2, interpolatedV2);
        } else {
            instance.addVertexWithUV(d8, d10, d12, d4, d6);
            instance.addVertexWithUV(d8, d10, d11, interpolatedU, interpolatedV);
            instance.addVertexWithUV(d8, d9, d11, d5, d7);
            instance.addVertexWithUV(d8, d9, d12, interpolatedU2, interpolatedV2);
        }
    }

    public void renderFaceXPos(Block block, double d, double d2, double d3, IIcon icon) {
        Tessellator instance = Tessellator.instance;
        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
        }

        if (Config.isConnectedTextures() && this.overrideBlockTexture == null) {
            icon = ConnectedTextures.getConnectedTexture(this.blockAccess, block, (int)d, (int)d2, (int)d3, 5, icon);
        }

        boolean flag = false;
        if (Config.isNaturalTextures() && this.overrideBlockTexture == null && this.uvRotateSouth == 0) {
            NaturalProperties naturalProperties = NaturalTextures.getNaturalProperties(icon);
            if (naturalProperties != null) {
                int random = Config.getRandom((int)d, (int)d2, (int)d3, 5);
                if (naturalProperties.rotation > 1) {
                    this.uvRotateSouth = random & 3;
                }

                if (naturalProperties.rotation == 2) {
                    this.uvRotateSouth = this.uvRotateSouth / 2 * 3;
                }

                if (naturalProperties.flip) {
                    this.flipTexture = (random & 4) != 0;
                }

                flag = true;
            }
        }

        double interpolatedU = icon.getInterpolatedU(this.renderMinZ * 16.0);
        double interpolatedU2 = icon.getInterpolatedU(this.renderMaxZ * 16.0);
        if (this.flag) {
            interpolatedU2 = icon.getInterpolatedU((1.0 - this.renderMinZ) * 16.0);
            interpolatedU = icon.getInterpolatedU((1.0 - this.renderMaxZ) * 16.0);
        }

        double interpolatedV = icon.getInterpolatedV(16.0 - this.renderMaxY * 16.0);
        double interpolatedV2 = icon.getInterpolatedV(16.0 - this.renderMinY * 16.0);
        if (this.flipTexture) {
            double d4 = interpolatedU;
            interpolatedU = interpolatedU2;
            interpolatedU2 = d4;
        }

        if (this.renderMinZ < 0.0 || this.renderMaxZ > 1.0) {
            interpolatedU = icon.getMinU();
            interpolatedU2 = icon.getMaxU();
        }

        if (this.renderMinY < 0.0 || this.renderMaxY > 1.0) {
            interpolatedV = icon.getMinV();
            interpolatedV2 = icon.getMaxV();
        }

        double d4 = interpolatedU2;
        double d5 = interpolatedU;
        double d6 = interpolatedV;
        double d7 = interpolatedV2;
        if (this.uvRotateSouth == 2) {
            interpolatedU = icon.getInterpolatedU(this.renderMinY * 16.0);
            interpolatedV = icon.getInterpolatedV(16.0 - this.renderMinZ * 16.0);
            interpolatedU2 = icon.getInterpolatedU(this.renderMaxY * 16.0);
            interpolatedV2 = icon.getInterpolatedV(16.0 - this.renderMaxZ * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d6 = interpolatedV;
            d7 = interpolatedV2;
            d4 = interpolatedU;
            d5 = interpolatedU2;
            interpolatedV = interpolatedV2;
            interpolatedV2 = d6;
        } else if (this.uvRotateSouth == 1) {
            interpolatedU = icon.getInterpolatedU(16.0 - this.renderMaxY * 16.0);
            interpolatedV = icon.getInterpolatedV(this.renderMaxZ * 16.0);
            interpolatedU2 = icon.getInterpolatedU(16.0 - this.renderMinY * 16.0);
            interpolatedV2 = icon.getInterpolatedV(this.renderMinZ * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d4 = interpolatedU2;
            d5 = interpolatedU;
            interpolatedU = interpolatedU2;
            interpolatedU2 = d5;
            d6 = interpolatedV2;
            d7 = interpolatedV;
        } else if (this.uvRotateSouth == 3) {
            interpolatedU = icon.getInterpolatedU(16.0 - this.renderMinZ * 16.0);
            interpolatedU2 = icon.getInterpolatedU(16.0 - this.renderMaxZ * 16.0);
            interpolatedV = icon.getInterpolatedV(this.renderMaxY * 16.0);
            interpolatedV2 = icon.getInterpolatedV(this.renderMinY * 16.0);
            if (this.flipTexture) {
                double d8 = interpolatedU;
                interpolatedU = interpolatedU2;
                interpolatedU2 = d8;
            }

            d4 = interpolatedU2;
            d5 = interpolatedU;
            d6 = interpolatedV;
            d7 = interpolatedV2;
        }

        if (flag) {
            this.uvRotateSouth = 0;
            this.flipTexture = false;
        }

        double d8 = d + this.renderMaxX;
        double d9 = d2 + this.renderMinY;
        double d10 = d2 + this.renderMaxY;
        double d11 = d3 + this.renderMinZ;
        double d12 = d3 + this.renderMaxZ;
        if (this.renderFromInside) {
            d11 = d3 + this.renderMaxZ;
            d12 = d3 + this.renderMinZ;
        }

        if (this.enableAO) {
            instance.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            instance.setBrightness(this.brightnessTopLeft);
            instance.addVertexWithUV(d8, d9, d12, d5, d7);
            instance.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            instance.setBrightness(this.brightnessBottomLeft);
            instance.addVertexWithUV(d8, d9, d11, interpolatedU2, interpolatedV2);
            instance.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            instance.setBrightness(this.brightnessBottomRight);
            instance.addVertexWithUV(d8, d10, d11, d4, d6);
            instance.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            instance.setBrightness(this.brightnessTopRight);
            instance.addVertexWithUV(d8, d10, d12, interpolatedU, interpolatedV);
        } else {
            instance.addVertexWithUV(d8, d9, d12, d5, d7);
            instance.addVertexWithUV(d8, d9, d11, interpolatedU2, interpolatedV2);
            instance.addVertexWithUV(d8, d10, d11, d4, d6);
            instance.addVertexWithUV(d8, d10, d12, interpolatedU, interpolatedV);
        }
    }

    public void renderBlockAsItem(Block block, int n, float f) {
        Tessellator instance = Tessellator.instance;
        boolean flag = block == Blocks.grass;
        if (block == Blocks.dispenser || block == Blocks.dropper || block == Blocks.furnace) {
            n = 3;
        }

        if (this.useInventoryTint) {
            int renderColor = block.getRenderColor(n);
            if (flag) {
                renderColor = 16777215;
            }

            float f2 = (renderColor >> 16 & 0xFF) / 255.0F;
            float f3 = (renderColor >> 8 & 0xFF) / 255.0F;
            float f4 = (renderColor & 0xFF) / 255.0F;
            GL11.glColor4f(f2 * f, f3 * f, f4 * f, 1.0F);
        }

        int renderType = block.getRenderType();
        this.setRenderBoundsFromBlock(block);
        if (renderType != 0 && renderType != 31 && renderType != 39 && renderType != 16 && renderType != 26) {
            if (renderType == 1) {
                instance.startDrawingQuads();
                instance.setNormal(0.0F, -1.0F, 0.0F);
                IIcon blockIconFromSideAndMetadata = this.getBlockIconFromSideAndMetadata(block, 0, n);
                this.drawCrossedSquares(blockIconFromSideAndMetadata, -0.5, -0.5, -0.5, 1.0F);
                instance.draw();
            } else if (renderType == 19) {
                instance.startDrawingQuads();
                instance.setNormal(0.0F, -1.0F, 0.0F);
                block.setBlockBoundsForItemRender();
                this.renderBlockStemSmall(block, n, this.renderMaxY, -0.5, -0.5, -0.5);
                instance.draw();
            } else if (renderType == 23) {
                instance.startDrawingQuads();
                instance.setNormal(0.0F, -1.0F, 0.0F);
                block.setBlockBoundsForItemRender();
                instance.draw();
            } else if (renderType == 13) {
                block.setBlockBoundsForItemRender();
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                float f2 = 0.0625F;
                instance.startDrawingQuads();
                instance.setNormal(0.0F, -1.0F, 0.0F);
                this.renderFaceYNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 0));
                instance.draw();
                instance.startDrawingQuads();
                instance.setNormal(0.0F, 1.0F, 0.0F);
                this.renderFaceYPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 1));
                instance.draw();
                instance.startDrawingQuads();
                instance.setNormal(0.0F, 0.0F, -1.0F);
                instance.addTranslation(0.0F, 0.0F, f2);
                this.renderFaceZNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 2));
                instance.addTranslation(0.0F, 0.0F, -f2);
                instance.draw();
                instance.startDrawingQuads();
                instance.setNormal(0.0F, 0.0F, 1.0F);
                instance.addTranslation(0.0F, 0.0F, -f2);
                this.renderFaceZPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 3));
                instance.addTranslation(0.0F, 0.0F, f2);
                instance.draw();
                instance.startDrawingQuads();
                instance.setNormal(-1.0F, 0.0F, 0.0F);
                instance.addTranslation(f2, 0.0F, 0.0F);
                this.renderFaceXNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 4));
                instance.addTranslation(-f2, 0.0F, 0.0F);
                instance.draw();
                instance.startDrawingQuads();
                instance.setNormal(1.0F, 0.0F, 0.0F);
                instance.addTranslation(-f2, 0.0F, 0.0F);
                this.renderFaceXPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 5));
                instance.addTranslation(f2, 0.0F, 0.0F);
                instance.draw();
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            } else if (renderType == 22) {
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                TileEntityRendererChestHelper.instance.renderChest(block, n, f);
                GL11.glEnable(32826);
            } else if (renderType == 6) {
                instance.startDrawingQuads();
                instance.setNormal(0.0F, -1.0F, 0.0F);
                this.renderBlockCropsImpl(block, n, -0.5, -0.5, -0.5);
                instance.draw();
            } else if (renderType == 2) {
                instance.startDrawingQuads();
                instance.setNormal(0.0F, -1.0F, 0.0F);
                this.renderTorchAtAngle(block, -0.5, -0.5, -0.5, 0.0, 0.0, 0);
                instance.draw();
            } else if (renderType == 10) {
                for (int i = 0; i < 2; i++) {
                    if (i == 0) {
                        this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.5);
                    }

                    if (i == 1) {
                        this.setRenderBounds(0.0, 0.0, 0.5, 1.0, 0.5, 1.0);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, -1.0F, 0.0F);
                    this.renderFaceYNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 0));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 1.0F, 0.0F);
                    this.renderFaceYPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 1));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 0.0F, -1.0F);
                    this.renderFaceZNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 2));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 0.0F, 1.0F);
                    this.renderFaceZPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 3));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(-1.0F, 0.0F, 0.0F);
                    this.renderFaceXNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 4));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(1.0F, 0.0F, 0.0F);
                    this.renderFaceXPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 5));
                    instance.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }
            } else if (renderType == 27) {
                int n2 = 0;
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                instance.startDrawingQuads();

                for (int i = 0; i < 8; i++) {
                    int n3 = 0;
                    int n4 = 1;
                    if (i == 0) {
                        n3 = 2;
                    }

                    if (i == 1) {
                        n3 = 3;
                    }

                    if (i == 2) {
                        n3 = 4;
                    }

                    if (i == 3) {
                        n3 = 5;
                        n4 = 2;
                    }

                    if (i == 4) {
                        n3 = 6;
                        n4 = 3;
                    }

                    if (i == 5) {
                        n3 = 7;
                        n4 = 5;
                    }

                    if (i == 6) {
                        n3 = 6;
                        n4 = 2;
                    }

                    if (i == 7) {
                        n3 = 3;
                    }

                    float f2 = n3 / 16.0F;
                    float f3 = 1.0F - n2 / 16.0F;
                    float f4 = 1.0F - (n2 + n4) / 16.0F;
                    n2 += n4;
                    this.setRenderBounds(0.5F - f2, f4, 0.5F - f2, 0.5F + f2, f3, 0.5F + f2);
                    instance.setNormal(0.0F, -1.0F, 0.0F);
                    this.renderFaceYNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 0));
                    instance.setNormal(0.0F, 1.0F, 0.0F);
                    this.renderFaceYPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 1));
                    instance.setNormal(0.0F, 0.0F, -1.0F);
                    this.renderFaceZNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 2));
                    instance.setNormal(0.0F, 0.0F, 1.0F);
                    this.renderFaceZPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 3));
                    instance.setNormal(-1.0F, 0.0F, 0.0F);
                    this.renderFaceXNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 4));
                    instance.setNormal(1.0F, 0.0F, 0.0F);
                    this.renderFaceXPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 5));
                }

                instance.draw();
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
            } else if (renderType == 11) {
                for (int i = 0; i < 4; i++) {
                    float f2 = 0.125F;
                    if (i == 0) {
                        this.setRenderBounds(0.5F - f2, 0.0, 0.0, 0.5F + f2, 1.0, f2 * 2.0F);
                    }

                    if (i == 1) {
                        this.setRenderBounds(0.5F - f2, 0.0, 1.0F - f2 * 2.0F, 0.5F + f2, 1.0, 1.0);
                    }

                    f2 = 0.0625F;
                    if (i == 2) {
                        this.setRenderBounds(0.5F - f2, 1.0F - f2 * 3.0F, -f2 * 2.0F, 0.5F + f2, 1.0F - f2, 1.0F + f2 * 2.0F);
                    }

                    if (i == 3) {
                        this.setRenderBounds(0.5F - f2, 0.5F - f2 * 3.0F, -f2 * 2.0F, 0.5F + f2, 0.5F - f2, 1.0F + f2 * 2.0F);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, -1.0F, 0.0F);
                    this.renderFaceYNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 0));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 1.0F, 0.0F);
                    this.renderFaceYPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 1));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 0.0F, -1.0F);
                    this.renderFaceZNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 2));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 0.0F, 1.0F);
                    this.renderFaceZPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 3));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(-1.0F, 0.0F, 0.0F);
                    this.renderFaceXNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 4));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(1.0F, 0.0F, 0.0F);
                    this.renderFaceXPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 5));
                    instance.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
            } else if (renderType == 21) {
                for (int i = 0; i < 3; i++) {
                    float f2 = 0.0625F;
                    if (i == 0) {
                        this.setRenderBounds(0.5F - f2, 0.3F, 0.0, 0.5F + f2, 1.0, f2 * 2.0F);
                    }

                    if (i == 1) {
                        this.setRenderBounds(0.5F - f2, 0.3F, 1.0F - f2 * 2.0F, 0.5F + f2, 1.0, 1.0);
                    }

                    f2 = 0.0625F;
                    if (i == 2) {
                        this.setRenderBounds(0.5F - f2, 0.5, 0.0, 0.5F + f2, 1.0F - f2, 1.0);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, -1.0F, 0.0F);
                    this.renderFaceYNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 0));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 1.0F, 0.0F);
                    this.renderFaceYPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 1));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 0.0F, -1.0F);
                    this.renderFaceZNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 2));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 0.0F, 1.0F);
                    this.renderFaceZPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 3));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(-1.0F, 0.0F, 0.0F);
                    this.renderFaceXNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 4));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(1.0F, 0.0F, 0.0F);
                    this.renderFaceXPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSide(block, 5));
                    instance.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }
            } else if (renderType == 32) {
                for (int i = 0; i < 2; i++) {
                    if (i == 0) {
                        this.setRenderBounds(0.0, 0.0, 0.3125, 1.0, 0.8125, 0.6875);
                    }

                    if (i == 1) {
                        this.setRenderBounds(0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, -1.0F, 0.0F);
                    this.renderFaceYNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 0, n));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 1.0F, 0.0F);
                    this.renderFaceYPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 1, n));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 0.0F, -1.0F);
                    this.renderFaceZNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 2, n));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 0.0F, 1.0F);
                    this.renderFaceZPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 3, n));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(-1.0F, 0.0F, 0.0F);
                    this.renderFaceXNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 4, n));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(1.0F, 0.0F, 0.0F);
                    this.renderFaceXPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 5, n));
                    instance.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
            } else if (renderType == 35) {
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                this.renderBlockAnvilOrient((BlockAnvil)block, 0, 0, 0, n << 2, true);
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            } else if (renderType == 34) {
                for (int i = 0; i < 3; i++) {
                    if (i == 0) {
                        this.setRenderBounds(0.125, 0.0, 0.125, 0.875, 0.1875, 0.875);
                        this.setOverrideBlockTexture(this.getBlockIcon(Blocks.obsidian));
                    } else if (i == 1) {
                        this.setRenderBounds(0.1875, 0.1875, 0.1875, 0.8125, 0.875, 0.8125);
                        this.setOverrideBlockTexture(this.getBlockIcon(Blocks.beacon));
                    } else if (i == 2) {
                        this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
                        this.setOverrideBlockTexture(this.getBlockIcon(Blocks.glass));
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, -1.0F, 0.0F);
                    this.renderFaceYNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 0, n));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 1.0F, 0.0F);
                    this.renderFaceYPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 1, n));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 0.0F, -1.0F);
                    this.renderFaceZNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 2, n));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(0.0F, 0.0F, 1.0F);
                    this.renderFaceZPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 3, n));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(-1.0F, 0.0F, 0.0F);
                    this.renderFaceXNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 4, n));
                    instance.draw();
                    instance.startDrawingQuads();
                    instance.setNormal(1.0F, 0.0F, 0.0F);
                    this.renderFaceXPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 5, n));
                    instance.draw();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                this.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
                this.clearOverrideBlockTexture();
            } else if (renderType == 38) {
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                this.renderBlockHopperMetadata((BlockHopper)block, 0, 0, 0, 0, true);
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            } else if (Reflector.ModLoader.exists()) {
                Reflector.callVoid(Reflector.ModLoader_renderInvBlock, this, block, n, renderType);
            } else if (Reflector.FMLRenderAccessLibrary.exists()) {
                Reflector.callVoid(Reflector.FMLRenderAccessLibrary_renderInventoryBlock, this, block, n, renderType);
            }
        } else {
            if (renderType == 16) {
                n = 1;
            }

            block.setBlockBoundsForItemRender();
            this.setRenderBoundsFromBlock(block);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            instance.startDrawingQuads();
            instance.setNormal(0.0F, -1.0F, 0.0F);
            this.renderFaceYNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 0, n));
            instance.draw();
            if (flag && this.useInventoryTint) {
                int renderColor = block.getRenderColor(n);
                float f2 = (renderColor >> 16 & 0xFF) / 255.0F;
                float f3 = (renderColor >> 8 & 0xFF) / 255.0F;
                float f4 = (renderColor & 0xFF) / 255.0F;
                GL11.glColor4f(f2 * f, f3 * f, f4 * f, 1.0F);
            }

            instance.startDrawingQuads();
            instance.setNormal(0.0F, 1.0F, 0.0F);
            this.renderFaceYPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 1, n));
            instance.draw();
            if (flag && this.useInventoryTint) {
                GL11.glColor4f(f, f, f, 1.0F);
            }

            instance.startDrawingQuads();
            instance.setNormal(0.0F, 0.0F, -1.0F);
            this.renderFaceZNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 2, n));
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(0.0F, 0.0F, 1.0F);
            this.renderFaceZPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 3, n));
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(-1.0F, 0.0F, 0.0F);
            this.renderFaceXNeg(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 4, n));
            instance.draw();
            instance.startDrawingQuads();
            instance.setNormal(1.0F, 0.0F, 0.0F);
            this.renderFaceXPos(block, 0.0, 0.0, 0.0, this.getBlockIconFromSideAndMetadata(block, 5, n));
            instance.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }
    }

    public static boolean renderItemIn3d(int n) {
        switch (n) {
            case -1:
                return false;
            case 0:
            case 10:
            case 11:
            case 13:
            case 16:
            case 21:
            case 22:
            case 26:
            case 27:
            case 31:
            case 32:
            case 34:
            case 35:
            case 39:
                return true;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 12:
            case 14:
            case 15:
            case 17:
            case 18:
            case 19:
            case 20:
            case 23:
            case 24:
            case 25:
            case 28:
            case 29:
            case 30:
            case 33:
            case 36:
            case 37:
            case 38:
            default:
                return Reflector.ModLoader.exists()
                    ? Reflector.callBoolean(Reflector.ModLoader_renderBlockIsItemFull3D, n)
                    : (Reflector.FMLRenderAccessLibrary.exists() ? Reflector.callBoolean(Reflector.FMLRenderAccessLibrary_renderItemAsFull3DBlock, n) : false);
        }
    }

    public IIcon getBlockIcon(Block block, IBlockAccess blockAccess, int n, int n2, int n3, int n4) {
        return this.getIconSafe(block.getIcon(blockAccess, n, n2, n3, n4));
    }

    public IIcon getBlockIconFromSideAndMetadata(Block block, int n, int n2) {
        return this.getIconSafe(block.getIcon(n, n2));
    }

    public IIcon getBlockIconFromSide(Block block, int n) {
        return this.getIconSafe(block.getBlockTextureFromSide(n));
    }

    public IIcon getBlockIcon(Block block) {
        return this.getIconSafe(block.getBlockTextureFromSide(1));
    }

    public IIcon getIconSafe(IIcon icon) {
        if (icon == null) {
            icon = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno");
        }

        return icon;
    }

    public float getAmbientOcclusionLightValue(int n, int n2, int n3) {
        Block block = this.blockAccess.getBlock(n, n2, n3);
        return block.isBlockNormalCube() ? this.aoLightValueOpaque : 1.0F;
    }

    public IIcon fixAoSideGrassTexture(IIcon icon, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (icon == TextureUtils.iconGrassSide || icon == TextureUtils.iconMyceliumSide) {
            icon = Config.getSideGrassTexture(this.blockAccess, n, n2, n3, n4, icon);
            if (icon == TextureUtils.iconGrassTop) {
                this.colorRedTopLeft *= f;
                this.colorRedBottomLeft *= f;
                this.colorRedBottomRight *= f;
                this.colorRedTopRight *= f;
                this.colorGreenTopLeft *= f2;
                this.colorGreenBottomLeft *= f2;
                this.colorGreenBottomRight *= f2;
                this.colorGreenTopRight *= f2;
                this.colorBlueTopLeft *= f3;
                this.colorBlueBottomLeft *= f3;
                this.colorBlueBottomRight *= f3;
                this.colorBlueTopRight *= f3;
            }
        }

        if (icon == TextureUtils.iconGrassSideSnowed) {
            icon = Config.getSideSnowGrassTexture(this.blockAccess, n, n2, n3, n4);
        }

        return icon;
    }

    public boolean hasSnowNeighbours(int n, int n2, int n3) {
        Block block = Blocks.snow_layer;
        return this.blockAccess.getBlock(n - 1, n2, n3) != block
                && this.blockAccess.getBlock(n + 1, n2, n3) != block
                && this.blockAccess.getBlock(n, n2, n3 - 1) != block
                && this.blockAccess.getBlock(n, n2, n3 + 1) != block
            ? false
            : this.blockAccess.getBlock(n, n2 - 1, n3).isOpaqueCube();
    }

    public void renderSnow(int n, int n2, int n3, double d) {
        if (this.betterSnowEnabled) {
            this.setRenderBoundsFromBlock(Blocks.snow_layer);
            this.renderMaxY = d;
            this.renderStandardBlock(Blocks.snow_layer, n, n2, n3);
        }
    }

    public static RenderBlocks getInstance() {
        if (instance == null) {
            instance = new RenderBlocks();
        }

        return instance;
    }
}
