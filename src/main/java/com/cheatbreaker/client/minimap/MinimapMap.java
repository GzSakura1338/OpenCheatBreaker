package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.minimap.MapData.BiomeLabel;
import com.cheatbreaker.client.mod.ModForceState;
import com.cheatbreaker.client.mod.impl.ZansMinimapMod;
import com.cheatbreaker.client.util.FastTrig;
import com.cheatbreaker.client.util.MinecraftHelper;
import com.cheatbreaker.client.util.NetworkUtils;
import com.cheatbreaker.client.util.Translations;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D.Double;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class MinimapMap implements IMap, Runnable {
   public float[] lastLightBrightnessTable = new float[16];
   public int[] developerNameHashes = new int[]{
      "minecraftxteria".toLowerCase().hashCode(),
      "jacoboom100".toLowerCase().hashCode(),
      "Laserpigofdoom".toLowerCase().hashCode(),
      "DesignVenomz".toLowerCase().hashCode(),
      "ElectronTowel".toLowerCase().hashCode(),
      "Fighterbear12".toLowerCase().hashCode(),
      "KillmurCS".toLowerCase().hashCode()
   };
   public Object bufferLock = new Object();
   public String version = "v1.2.0";
   public FontRenderer fontRenderer;
   public float percentX;
   public MinimapSettings minimapSettings = null;
   public IRadar radar = null;
   public RadarContext radarContext = null;
   public IColorManager colorManager = null;
   public IWaypointManager waypointManager = null;
   public IDimensionManager dimensionManager = null;
   public Random random = new Random();
   public MinecraftServer minecraftServer;
   public int welcomeState = 1;
   public boolean fullscreenMap = false;
   public boolean calculating = false;
   public int zoom = 2;
   public int minimapCenterX = 37;
   public int minimapCenterY = 37;
   public boolean fullRender = true;
   public float percentY;
   public int lastX = 0;
   public int lastZ = 0;
   public int lastScale = 0;
   public boolean percentXOverOne = false;
   public boolean percentYOverOne = false;
   public boolean lastSquareMap = false;
   public int northRotate = 0;
   public Thread calculationThread = new Thread(this, "Voxelmap Map Calculation Thread");
   public boolean threading;
   public boolean needSkyColor;
   public Long lastWorldChangeTime;
   public boolean checkChatForPermissions;
   public ChatLine chatLine;
   public World world;
   public final int mapImageSize;
   public boolean waypointRendererRegistered;
   public int availableProcessors;
   public boolean multicore;
   public MapData[] mapData;
   public MapChunkCache[] chunkCache;
   public GlBufferedImage[] mapImages;
   public int scaledWidth;
   public boolean imageChanged;
   public DynamicTexture dynamicTexture;
   public boolean needLight;
   public float lastGamma;
   public float lastSunBrightness;
   public float lastLightning;
   public float lastNightVision;
   public int[] lastLightmapColors;
   public int scaledHeight;
   public boolean lastUnderground;
   public boolean lastAboveHorizon;
   public int lastBiomeId;
   public int lastNeutralColor;
   public GuiScreen guiScreen;
   public boolean showMinimap;
   public String statusMessage;
   public String[] welcomeLines;
   public int messageTimer;
   public int heightChangeCounter;
   public int tickCounter;
   public int lastY;
   public int lastImageX;
   public int lastImageZ;
   public boolean lastFullscreen;
   public float direction;
   public String worldName;
   public boolean pendingFullRender;
   public int heightChangeThreshold;
   public int heightChangeTicks;
   public int[] lightmapColors;
   public final boolean unusedFlag1;
   public boolean unusedFlag2;
   public boolean developer;
   public IVoxelMap voxelMap;
   public Minecraft mc;
   public GlBufferedImage roundMapImage;

   @Override
   public String getCurrentWorldName() {
      return this.worldName;
   }

   public void calculateMap(boolean flag) {
      this.zoom = this.minimapSettings.zoom;
      int blockX = MinecraftHelper.getBlockX();
      int blockZ = MinecraftHelper.getBlockZ();
      int playerY = MinecraftHelper.getPlayerY();
      int n = blockX - this.lastX;
      int n2 = blockZ - this.lastZ;
      int n3 = playerY - this.lastY;
      int pow = (int)Math.pow(2.0, this.zoom);
      boolean flag2 = false;
      boolean flag3 = false;
      boolean flag4 = false;
      boolean flag5 = false;
      int blockColor = this.colorManager.getBlockColor(0, 0, 0);
      if (this.lastNeutralColor != blockColor) {
         flag5 = true;
         this.lastNeutralColor = blockColor;
      }

      if (this.minimapSettings.dynamicLighting) {
         byte b = 0;
         if (this.minimapSettings.realTimeTorchFlicker) {
            b = 8;
         }

         for (int i = 0; i < 16; i++) {
            if (this.lastLightmapColors[i] != this.lightmapColors[i * 16 + b]) {
               flag4 = true;
               this.lastLightmapColors[i] = this.lightmapColors[i * 16 + b];
            }
         }
      }

      if (n3 != 0) {
         this.heightChangeCounter++;
      } else if (this.heightChangeCounter != 0) {
         this.heightChangeCounter++;
      }

      if (flag || Math.abs(n3) >= this.heightChangeThreshold || this.heightChangeCounter > this.heightChangeTicks) {
         this.lastY = playerY;
         flag3 = true;
         this.heightChangeCounter = 0;
      }

      if (n > 32 * pow || n < -32 * pow || n2 > 32 * pow || n2 < -32 * pow) {
         flag = true;
      }

      boolean flag6 = false;
      boolean flag7 = false;
      boolean flag8 = false;
      if (this.mc.thePlayer.dimension != -1) {
         flag7 = this.minimapSettings.caveModeAllowed
            && this.minimapSettings.caveMode
            && this.world
                  .getChunkFromBlockCoords(new BlockPos(this.lastX, 0, this.lastZ))
                  .getLightFor(EnumSkyBlock.SKY, new BlockPos(this.lastX, Math.max(Math.min(MinecraftHelper.getPlayerY(), 255), 0), this.lastZ))
               <= 0;
      } else {
         flag6 = true;
         flag8 = this.world.getTopSolidOrLiquidBlock(new BlockPos(this.lastX, 0, this.lastZ)).getY() < MinecraftHelper.getPlayerY();
      }

      if (this.lastUnderground != (flag7 || flag6 && (playerY <= 125 || !flag8 && this.minimapSettings.caveMode))) {
         this.lastUnderground = flag7 || flag6 && (playerY <= 125 || !flag8 && this.minimapSettings.caveMode);
         flag = true;
      }

      flag2 = flag3 && (flag6 || flag7);
      int n4 = -1;
      synchronized (this.bufferLock) {
         if (!flag) {
            this.mapImages[this.zoom].shiftZ(n2);
            this.mapImages[this.zoom].shiftX(n);
         }

         this.lastX = blockX;
         this.lastZ = blockZ;
      }

      blockX -= 16 * pow;
      blockZ -= 16 * pow;
      if (!flag) {
         this.mapData[this.zoom].shiftZ(n2);
         this.mapData[this.zoom].shiftX(n);

         for (int i = n2 > 0 ? 32 * pow - 1 : -n2 - 1; i >= (n2 > 0 ? 32 * pow - n2 : 0); i--) {
            for (int j = 0; j < 32 * pow; j++) {
               n4 = this.getPixelColor(true, true, true, true, flag6, flag8, flag7, this.world, pow, blockX, blockZ, j, i);
               this.mapImages[this.zoom].setRGB(j, i, n4);
            }
         }

         for (int i = 32 * pow - 1; i >= 0; i--) {
            for (int j = n > 0 ? 32 * pow - n : 0; j < (n > 0 ? 32 * pow : -n); j++) {
               n4 = this.getPixelColor(true, true, true, true, flag6, flag8, flag7, this.world, pow, blockX, blockZ, j, i);
               this.mapImages[this.zoom].setRGB(j, i, n4);
            }
         }
      }

      if (flag || this.minimapSettings.heightMap && flag3 || flag2 || this.minimapSettings.dynamicLighting && flag4 || flag5) {
         for (int i = 32 * pow - 1; i >= 0; i--) {
            for (int j = 0; j < 32 * pow; j++) {
               n4 = this.getPixelColor(flag, flag || flag2, flag, flag || flag4 || flag2, flag6, flag8, flag7, this.world, pow, blockX, blockZ, j, i);
               this.mapImages[this.zoom].setRGB(j, i, n4);
            }
         }
      }

      if ((flag || n != 0 || n2 != 0 || !this.lastFullscreen) && this.fullscreenMap && this.minimapSettings.biomeOverlay > 0) {
         this.mapData[this.zoom].segmentBiomes();
         this.mapData[this.zoom].computeBiomeLabels();
      }

      this.lastFullscreen = this.fullscreenMap;
      if (flag || n != 0 || n2 != 0 || flag3 || flag4 || flag5) {
         this.imageChanged = true;
      }
   }

   public String getIntegratedWorldName() {
      return this.mc.getIntegratedServer().getWorldName();
   }

   public DynamicTexture getLightmapTexture() {
      return this.mc.entityRenderer.getLightmapTexture();
   }

   public int getLight(int n, int n2, World world, int n3, int n4, int n5, boolean flag) {
      int n6 = 255;
      if (flag) {
         n6 = 0;
      } else if (n != this.colorManager.getAirColor() && n != 0 && this.minimapSettings.dynamicLighting) {
         Chunk chunkFromBlockCoords = world.getChunkFromBlockCoords(new BlockPos(n3, 0, n4));
         int lightFor = chunkFromBlockCoords.getLightFor(EnumSkyBlock.BLOCK, new BlockPos(n3, Math.max(Math.min(n5, 255), 0), n4));
         int lightFor2 = chunkFromBlockCoords.getLightFor(EnumSkyBlock.SKY, new BlockPos(n3, Math.max(Math.min(n5, 255), 0), n4));
         if (n2 == BlockIdRepository.lavaId && lightFor < 14) {
            lightFor = 14;
         }

         n6 = this.lightmapColors[lightFor + lightFor2 * 16];
      }

      return n6;
   }

   public int getPixelColor(
      boolean flag,
      boolean flag2,
      boolean flag3,
      boolean flag4,
      boolean flag5,
      boolean flag6,
      boolean flag7,
      World world,
      int n,
      int n2,
      int n3,
      int n4,
      int n5
   ) {
      int n6 = 0;
      int n7 = 0;
      if (flag) {
         if (world.getChunkFromBlockCoords(new BlockPos(n2 + n4, 0, n3 + n5)).isLoaded()) {
            n7 = world.getBiomeGenForCoords(new BlockPos(n2 + n4, 0, n3 + n5)).biomeID;
         } else {
            n7 = -1;
         }

         this.mapData[this.zoom].setBiomeId(n4, n5, n7);
      } else {
         n7 = this.mapData[this.zoom].getBiomeId(n4, n5);
      }

      if (this.minimapSettings.biomeOverlay == 1) {
         if (n7 >= 0) {
            n6 = BiomeGenBase.getBiomeGenArray()[n7].color | 0xFF000000;
         } else {
            n6 = 0;
         }

         if (this.minimapSettings.chunkGrid && ((n2 + n4) % 16 == 0 || (n3 + n5) % 16 == 0)) {
            n6 = this.colorManager.colorAdder(2097152000, n6);
         }

         return n6;
      } else {
         int n8 = 0;
         boolean flag8 = false;
         boolean flag9 = false;
         if (flag2) {
            n8 = this.getBlockHeight(flag5, flag6, flag7, world, n2 + n4, n3 + n5, MinecraftHelper.getPlayerY());
            this.mapData[this.zoom].setHeight(n4, n5, n8);
         } else {
            n8 = this.mapData[this.zoom].getHeight(n4, n5);
         }

         if (n8 == -1) {
            n8 = this.lastY + 1;
            flag9 = true;
         }

         int n9 = -1;
         int n10 = 0;
         if (flag2) {
            Block block = world.getBlockState(new BlockPos(n2 + n4, n8, n3 + n5)).getBlock();
            if (block.getMaterial() == Material.snow) {
               n9 = Block.blockRegistry.getIDForObject(block);
               n10 = getBlockMetadata(world, n2 + n4, n8, n3 + n5);
            } else {
               Block block2 = world.getBlockState(new BlockPos(n2 + n4, n8 - 1, n3 + n5)).getBlock();
               n9 = Block.blockRegistry.getIDForObject(block2);
               n10 = getBlockMetadata(world, n2 + n4, n8 - 1, n3 + n5);
            }

            if (this.minimapSettings.biomes && n9 != this.mapData[this.zoom].getBlockId(n4, n5)) {
               flag8 = true;
            }

            this.mapData[this.zoom].setBlockId(n4, n5, n9);
            this.mapData[this.zoom].setMetadata(n4, n5, n10);
         } else {
            n9 = this.mapData[this.zoom].getBlockId(n4, n5);
            n10 = this.mapData[this.zoom].getMetadata(n4, n5);
         }

         if (n9 == BlockIdRepository.lavaId) {
            flag9 = false;
         }

         if (this.minimapSettings.biomes) {
            n6 = this.colorManager.getBlockColor(n9, n10, n7);
         } else {
            n6 = this.colorManager.getBlockColorWithDefaultTint(n9, n10, n7);
         }

         if (n6 == -65025) {
            n6 = 0;
         }

         if (this.minimapSettings.biomes && n9 != -1) {
            int n11 = -1;
            if (!flag3 && !flag8) {
               n11 = this.mapData[this.zoom].getTint(n4, n5);
            } else {
               n11 = this.getBiomeTint(n9, n10, n2 + n4, n8 - 1, n3 + n5);
               this.mapData[this.zoom].setTint(n4, n5, n11);
            }

            if (n11 != -1) {
               n6 = this.colorManager.colorMultiplier(n6, n11);
            }
         }

         n6 = this.applyHeightShading(n6, flag5, flag6, flag7, world, n, n2, n3, n4, n5, n8, flag9, 1);
         int n11 = flag9 ? 0 : 255;
         if (flag4) {
            n11 = this.getLight(n6, n9, world, n2 + n4, n3 + n5, n8, flag9);
            this.mapData[this.zoom].setLight(n4, n5, n11);
         } else {
            n11 = this.mapData[this.zoom].getLight(n4, n5);
         }

         if (n11 == 0) {
            n6 = 0;
         } else if (n11 != 255) {
            n6 = this.colorManager.colorMultiplier(n6, n11);
         }

         if (this.minimapSettings.waterTransparency) {
            Material material = Block.getBlockById(n9).getMaterial();
            if (material == Material.water || material == Material.ice) {
               int n12;
               if (flag2) {
                  n12 = this.getSeafloorHeight(world, n2 + n4, n3 + n5, n8);
                  this.mapData[this.zoom].setOceanFloorHeight(n4, n5, n12);
               } else {
                  n12 = this.mapData[this.zoom].getOceanFloorHeight(n4, n5);
               }

               int n13 = 0;
               if (flag2) {
                  Block block = world.getBlockState(new BlockPos(n2 + n4, n12 - 1, n3 + n5)).getBlock();
                  n9 = Block.blockRegistry.getIDForObject(block);
                  n10 = getBlockMetadata(world, n2 + n4, n12 - 1, n3 + n5);
                  if (block.getMaterial() == Material.water) {
                     n9 = BlockIdRepository.airId;
                     n10 = 0;
                  }

                  if (this.minimapSettings.biomes && n9 != this.mapData[this.zoom].getOceanFloorBlockId(n4, n5)) {
                     flag8 = true;
                  }

                  this.mapData[this.zoom].setOceanFloorBlockId(n4, n5, n9);
                  this.mapData[this.zoom].setOceanFloorMetadata(n4, n5, n10);
               } else {
                  n9 = this.mapData[this.zoom].getOceanFloorBlockId(n4, n5);
                  n10 = this.mapData[this.zoom].getOceanFloorMetadata(n4, n5);
               }

               if (this.minimapSettings.biomes) {
                  n13 = this.colorManager.getBlockColor(n9, n10, n7);
               } else {
                  n13 = this.colorManager.getBlockColorWithDefaultTint(n9, n10, n7);
               }

               if (this.minimapSettings.biomes && n9 != -1) {
                  int n14 = -1;
                  if (!flag3 && !flag8) {
                     n14 = this.mapData[this.zoom].getOceanFloorTint(n4, n5);
                  } else {
                     n14 = this.getBiomeTint(n9, n10, n2 + n4, n12 - 1, n3 + n5);
                     this.mapData[this.zoom].setOceanFloorTint(n4, n5, n14);
                  }

                  if (n14 != -1) {
                     n13 = this.colorManager.colorMultiplier(n13, n14);
                  }
               }

               n13 = this.applyHeightShading(n13, flag5, flag6, flag7, world, n, n2, n3, n4, n5, n12, flag9, 0);
               int n14 = 255;
               if (!flag4) {
                  n14 = this.mapData[this.zoom].getOceanFloorLight(n4, n5);
               } else {
                  n14 = this.getLight(n13, n9, world, n2 + n4, n3 + n5, n12, flag9);
                  if (this.minimapSettings.dynamicLighting
                     && material == Material.ice
                     && (n12 == n8 - 1 || world.getBlockState(new BlockPos(n2 + n4, n12, n3 + n5)).getBlock().getMaterial() == Material.ice)) {
                     n14 = this.colorManager.colorMultiplier(n14, 5592405);
                  }

                  this.mapData[this.zoom].setOceanFloorLight(n4, n5, n14);
               }

               if (n14 == 0) {
                  n13 = 0;
               } else if (n14 != 255) {
                  n13 = this.colorManager.colorMultiplier(n13, n14);
               }

               n6 = this.colorManager.colorAdder(n6, n13);
            }
         }

         if (this.minimapSettings.blockTransparency) {
            int n12 = -1;
            if (flag2) {
               n12 = this.getTransparentHeight(flag5, flag6, flag7, world, n2 + n4, n3 + n5, n8);
               this.mapData[this.zoom].setTransparentHeight(n4, n5, n12);
            } else {
               n12 = this.mapData[this.zoom].getTransparentHeight(n4, n5);
            }

            if (!flag2) {
               n9 = this.mapData[this.zoom].getTransparentBlockId(n4, n5);
               n10 = this.mapData[this.zoom].getTransparentMetadata(n4, n5);
            } else {
               if (n12 != -1 && n12 > n8) {
                  Block block = world.getBlockState(new BlockPos(n2 + n4, n12 - 1, n3 + n5)).getBlock();
                  n9 = Block.blockRegistry.getIDForObject(block);
                  n10 = getBlockMetadata(world, n2 + n4, n12 - 1, n3 + n5);
               } else {
                  n9 = 0;
                  n10 = 0;
               }

               if (this.minimapSettings.biomes && n9 != this.mapData[this.zoom].getTransparentBlockId(n4, n5)) {
                  flag8 = true;
               }

               this.mapData[this.zoom].setTransparentBlockId(n4, n5, n9);
               this.mapData[this.zoom].setTransparentMetadata(n4, n5, n10);
            }

            if (n9 != 0) {
               int n13 = 0;
               if (this.minimapSettings.biomes) {
                  n13 = this.colorManager.getBlockColor(n9, n10, n7);
               } else {
                  n13 = this.colorManager.getBlockColorWithDefaultTint(n9, n10, n7);
               }

               if (this.minimapSettings.biomes) {
                  int n14 = -1;
                  if (!flag3 && !flag8) {
                     n14 = this.mapData[this.zoom].getTransparentTint(n4, n5);
                  } else {
                     n14 = this.getBiomeTint(n9, n10, n2 + n4, n8, n3 + n5);
                     this.mapData[this.zoom].setTransparentTint(n4, n5, n14);
                  }

                  if (n14 != -1) {
                     n13 = this.colorManager.colorMultiplier(n13, n14);
                  }
               }

               n13 = this.applyHeightShading(n13, flag5, flag6, flag7, world, n, n2, n3, n4, n5, n12, flag9, 2);
               int n14 = 255;
               if (flag4) {
                  n14 = this.getLight(n13, n9, world, n2 + n4, n3 + n5, n12, flag9);
                  this.mapData[this.zoom].setTransparentLight(n4, n5, n14);
               } else {
                  n14 = this.mapData[this.zoom].getTransparentLight(n4, n5);
               }

               if (n14 == 0) {
                  n13 = 0;
               } else if (n14 != 255) {
                  n13 = this.colorManager.colorMultiplier(n13, n14);
               }

               n6 = this.colorManager.colorAdder(n13, n6);
            }
         }

         if (this.minimapSettings.biomeOverlay == 2) {
            int n12 = 0;
            if (n7 >= 0) {
               n12 = BiomeGenBase.getBiomeGenArray()[n7].color;
            }

            int n13 = n12 >> 16 & 0xFF;
            int n14 = n12 >> 8 & 0xFF;
            int n15 = n12 >> 0 & 0xFF;
            n12 = 2130706432 | (n13 & 0xFF) << 16 | (n14 & 0xFF) << 8 | n15 & 0xFF;
            n6 = this.colorManager.colorAdder(n12, n6);
         }

         if (this.minimapSettings.chunkGrid && ((n2 + n4) % 16 == 0 || (n3 + n5) % 16 == 0)) {
            n6 = this.colorManager.colorAdder(2097152000, n6);
         }

         return n6;
      }
   }

   public int getSeafloorHeight(World world, int n, int n2, int n3) {
      int n4 = n3;

      for (Block block = world.getBlockState(new BlockPos(n, n4 - 1, n2)).getBlock();
         block.getLightOpacity() < 5 && block.getMaterial() != Material.leaves && n4 > 1;
         block = world.getBlockState(new BlockPos(n, n4 - 1, n2)).getBlock()
      ) {
         n4--;
      }

      return n4;
   }

   public void drawArrow(int n, int n2) {
      try {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.blendFunc(770, 771);
         GlStateManager.pushMatrix();
         GlUtils.bindTextureLocation(new ResourceLocation("voxelmap/images/mmarrow.png"));
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GlStateManager.translate(n, n2, 0.0F);
         GlStateManager.rotate(this.direction, 0.0F, 0.0F, 1.0F);
         GlStateManager.translate(-n, -n2, 0.0F);
         GlUtils.startDrawing();
         GlUtils.drawQuadSized(n, n2, 16);
         GlUtils.finishDrawing();
      } catch (Exception e) {
         this.statusMessage = "Error: minimap arrow not found!";
      } finally {
         GlStateManager.popMatrix();
      }
   }

   @Override
   public void onTickInGame(Minecraft mc) {
      this.northRotate = this.minimapSettings.oldNorth ? 90 : 0;
      if (this.mc == null) {
         this.mc = mc;
      }

      if (this.fontRenderer == null) {
         this.fontRenderer = this.mc.fontRendererObj;
      }

      if (GlUtils.textureManager == null) {
         GlUtils.textureManager = this.mc.getTextureManager();
      }

      if (this.dynamicTexture == null) {
         this.dynamicTexture = this.getLightmapTexture();
      }

      if (!this.waypointRendererRegistered) {
         RenderManager renderManager = RenderManager.renderManager;
         if (renderManager != null) {
            WaypointRenderer waypointRenderer = new WaypointRenderer(this.minimapSettings);
            renderManager.getEntityRenderMap().put(EntityWaypointContainer.class, waypointRenderer);
            this.waypointRendererRegistered = true;
         }
      }

      if (this.mc.currentScreen == null && this.minimapSettings.keyMinimapVoxelmapmenu.isPressed()) {
         this.welcomeState = 0;
         if (this.minimapSettings.welcomeMessage) {
            this.minimapSettings.welcomeMessage = false;
            this.minimapSettings.save();
         }

         this.mc.displayGuiScreen(new MinimapOptionsScreen(this.voxelMap));
      }

      if (this.mc.currentScreen == null && this.minimapSettings.keyMinimapWaypointhotkey.isPressed()) {
         this.welcomeState = 0;
         if (this.minimapSettings.welcomeMessage) {
            this.minimapSettings.welcomeMessage = false;
            this.minimapSettings.save();
         }

         float f;
         float f2;
         float f3;
         if (this.waypointManager.getWaypoints().size() == 0) {
            f2 = 0.0F;
            f3 = 1.0F;
            f = 0.0F;
         } else {
            f2 = this.random.nextFloat();
            f3 = this.random.nextFloat();
            f = this.random.nextFloat();
         }

         TreeSet treeSet = new TreeSet();
         treeSet.add(this.mc.thePlayer.dimension);
         Waypoint waypoint = new Waypoint(
            "",
            this.mc.thePlayer.dimension != -1 ? MinecraftHelper.getBlockX() : MinecraftHelper.getBlockX() * 8,
            this.mc.thePlayer.dimension != -1 ? MinecraftHelper.getBlockZ() : MinecraftHelper.getBlockZ() * 8,
            MinecraftHelper.getPlayerY() - 1,
            true,
            f2,
            f3,
            f,
            "",
            this.voxelMap.getWaypointManager().getCurrentSubworld(),
            treeSet
         );
         this.mc.displayGuiScreen(new WaypointEditScreen(this.voxelMap, null, waypoint));
      }

      if (this.mc.currentScreen == null && this.minimapSettings.keyMinimapTogglemobs.isPressed()) {
         if (this.minimapSettings.welcomeMessage) {
            this.minimapSettings.welcomeMessage = false;
            this.minimapSettings.save();
         }

         this.voxelMap.getRadarSettings().toggleOption(MinimapOption.SHOWRADAR, 0);
         this.minimapSettings.save();
      }

      if (this.mc.currentScreen == null && this.minimapSettings.keyMinimapZoom.isPressed()) {
         if (this.minimapSettings.welcomeMessage) {
            this.minimapSettings.welcomeMessage = false;
            this.minimapSettings.save();
         }

         this.cycleZoom();
      }

      if (this.mc.currentScreen == null && this.minimapSettings.keyMinimapTogglefullscreen.isPressed()) {
         if (this.minimapSettings.welcomeMessage) {
            this.minimapSettings.welcomeMessage = false;
            this.minimapSettings.save();
         }

         this.fullscreenMap = !this.fullscreenMap;
         if (this.zoom == 3) {
            this.statusMessage = Translations.translate("minimap.ui.zoomlevel") + " (0.5x)";
         } else if (this.zoom == 2) {
            this.statusMessage = Translations.translate("minimap.ui.zoomlevel") + " (1.0x)";
         } else if (this.zoom == 1) {
            this.statusMessage = Translations.translate("minimap.ui.zoomlevel") + " (2.0x)";
         } else {
            this.statusMessage = Translations.translate("minimap.ui.zoomlevel") + " (4.0x)";
         }
      }

      this.checkWorldChange();
      if (this.mc.currentScreen instanceof GuiGameOver && !(this.guiScreen instanceof GuiGameOver)) {
         this.waypointManager.handleDeath();
      }

      this.guiScreen = this.mc.currentScreen;
      this.waypointManager.updateEntityContainer();
      this.checkLightingChanges();
      if (this.threading) {
         if (!this.calculationThread.isAlive() && this.threading) {
            this.calculationThread = new Thread(this, "Map Calculation");
            this.calculationThread.setPriority(5);
            this.calculationThread.start();
         }

         if (!(this.mc.currentScreen instanceof GuiGameOver) && !(this.mc.currentScreen instanceof GuiMemoryErrorScreen)) {
            synchronized (this.calculationThread) {
               this.calculationThread.notify();
            }
         }
      } else if (!this.threading) {
         if (!this.minimapSettings.hide) {
            this.calculateMap(this.fullRender);
            if (!this.fullRender || ZansMinimapMod.modForceState != ModForceState.NEUTRAL && ZansMinimapMod.modForceState != ModForceState.ALLOWED) {
               boolean flag = !this.minimapSettings.worldDownloadCompatibility;
               this.chunkCache[this.zoom].moveTo(this.lastX, this.lastZ);
               this.chunkCache[this.zoom].update(flag);
            }
         }

         this.fullRender = false;
      }

      if (this.welcomeState == 1 && !this.minimapSettings.welcomeMessage) {
         this.welcomeState = 0;
      }

      this.showMinimap = (!mc.gameSettings.hideGUI || this.mc.currentScreen != null)
         && (this.minimapSettings.showInScreens || this.mc.currentScreen == null || this.mc.currentScreen instanceof GuiChat)
         && !Keyboard.isKeyDown(61);
      this.direction = MinecraftHelper.getPlayerYaw() + 180.0F + this.northRotate;

      while (this.direction >= 360.0F) {
         this.direction -= 360.0F;
      }

      while (this.direction < 0.0F) {
         this.direction += 360.0F;
      }

      if (!this.statusMessage.equals("") && this.messageTimer == 0) {
         this.messageTimer = 500;
      }

      if (this.messageTimer > 0) {
         this.messageTimer--;
      }

      if (this.messageTimer == 0 && !this.statusMessage.equals("")) {
         this.statusMessage = "";
      }

      if (this.showMinimap && (ZansMinimapMod.modForceState == ModForceState.NEUTRAL || ZansMinimapMod.modForceState == ModForceState.ALLOWED)) {
         this.drawMinimap(mc);
      }

      this.tickCounter = this.tickCounter > 5000 ? 0 : this.tickCounter + 1;
      if (this.tickCounter == 5000 && this.mc.thePlayer.dimension == 0) {
         this.waypointManager.updateDeathpoints();
      }
   }

   public void checkLightingChanges() {
      if (this.waypointRendererRegistered) {
         if (this.mc.gameSettings.gammaSetting != this.lastGamma) {
            this.needLight = true;
            this.lastGamma = this.mc.gameSettings.gammaSetting;
         }

         for (int i = 0; i < 16; i++) {
            if (this.world.provider.getLightBrightnessTable()[i] != this.lastLightBrightnessTable[i]) {
               this.needLight = true;
               this.lastLightBrightnessTable[i] = this.world.provider.getLightBrightnessTable()[i];
            }
         }

         float sunBrightness = this.world.getSunBrightness(1.0F);
         if (Math.abs(this.lastSunBrightness - sunBrightness) > 0.01 || sunBrightness == 1.0 && sunBrightness != this.lastSunBrightness || sunBrightness == 0.0 && sunBrightness != this.lastSunBrightness) {
            this.needLight = true;
            this.needSkyColor = true;
            this.lastSunBrightness = sunBrightness;
         }

         float f = 0.0F;
         if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
            int duration = this.mc.thePlayer.getActivePotionEffect(Potion.nightVision).getDuration();
            f = duration > 200 ? 1.0F : 0.7F + MathHelper.sin((duration - 1.0F) * (float) Math.PI * 0.2F) * 0.3F;
         }

         if (this.lastNightVision != f) {
            this.lastNightVision = f;
            this.needLight = true;
         }

         int lastLightningBolt = this.world.getLastLightningBolt();
         if (this.lastLightning != lastLightningBolt) {
            this.lastLightning = lastLightningBolt;
            this.needLight = true;
         }

         boolean flag = (this.tickCounter - 50) % (this.mc.thePlayer.dimension != -1 ? 500 : (this.lastLightBrightnessTable[0] == 0.0F ? 250 : 5000)) == 0;
         if (this.minimapSettings.dynamicLighting && (this.needLight || flag || this.minimapSettings.realTimeTorchFlicker)) {
            this.lightmapColors = (int[])this.dynamicTexture.getTextureData().clone();
            byte b = 0;
            if (this.minimapSettings.realTimeTorchFlicker) {
               b = 8;
            }

            for (int i = 0; i < 16; i++) {
               if (this.lightmapColors[i * 16 + b] != this.lastLightmapColors[i]) {
                  this.needLight = false;
               }
            }
         }

         boolean flag2 = this.mc.thePlayer.posY >= this.world.getHorizon();
         if (flag2 != this.lastAboveHorizon) {
            this.needSkyColor = true;
            this.lastAboveHorizon = flag2;
         }

         int biomeID = this.world.getBiomeGenForCoords(new BlockPos(MinecraftHelper.getBlockX(), 0, MinecraftHelper.getBlockZ())).biomeID;
         if (biomeID != this.lastBiomeId) {
            this.needSkyColor = true;
            this.lastBiomeId = biomeID;
         }

         if (this.needSkyColor || flag) {
            this.colorManager.setSkyColor(this.getSkyColor());
         }
      }
   }

   public int getBiomeTint(int n, int n2, int n3, int n4, int n5) {
      int n6 = -1;
      if (this.colorManager.isOptifineInstalled()) {
         try {
            Integer[] integers = this.colorManager.getBiomeColormaps().get(n + " " + n2);
            if (integers != null) {
               int n7 = 0;
               int n8 = 0;
               int n9 = 0;

               for (int i = -1; i <= 1; i++) {
                  for (int j = -1; j <= 1; j++) {
                     int n10 = integers[this.world.getBiomeGenForCoords(new BlockPos(n3 + j, 0, n5 + i)).biomeID];
                     n7 += (n10 & 0xFF0000) >> 16;
                     n8 += (n10 & 0xFF00) >> 8;
                     n9 += n10 & 0xFF;
                  }
               }

               n6 = 0xFF000000 | (n7 / 9 & 0xFF) << 16 | (n8 / 9 & 0xFF) << 8 | n9 / 9 & 0xFF;
            } else {
               n6 = this.getBlockColorMultiplier(n, n2, n3, n4, n5);
            }
         } catch (Exception e) {
            n6 = this.getBlockColorMultiplier(n, n2, n3, n4, n5);
         }
      } else {
         n6 = this.getBlockColorMultiplier(n, n2, n3, n4, n5);
      }

      return n6;
   }

   @Override
   public float getPercentX() {
      return this.percentX;
   }

   public void drawRoundMapFrame(int n, int n2) {
      try {
         GlUtils.bindTextureLocation(new ResourceLocation("voxelmap/images/roundmap.png"));
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GlUtils.startDrawing();
         GlUtils.drawQuad(n, n2);
         GlUtils.finishDrawing();
      } catch (Exception e) {
         this.statusMessage = "Error: minimap overlay not found!";
      }
   }

   public void drawCoordinates(int n, int n2) {
      int n3;
      if (n2 > this.scaledHeight - 37 - 32 - 4 - 15) {
         n3 = n2 - 32 - 4 - 9;
      } else {
         n3 = n2 + 32 + 4;
      }

      if (!this.minimapSettings.hide && !this.fullscreenMap) {
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.5F, 0.5F, 1.0F);
         String str = "";
         str = this.formatCoordinate(MinecraftHelper.getBlockX()) + ", " + this.formatCoordinate(MinecraftHelper.getBlockZ());
         int n4 = this.getStringWidth(str) / 2;
         this.write(str, n * 2 - n4, n3 * 2, 16777215);
         str = Integer.toString(MinecraftHelper.getPlayerY());
         n4 = this.getStringWidth(str) / 2;
         this.write(str, n * 2 - n4, n3 * 2 + 10, 16777215);
         if (this.messageTimer > 0) {
            n4 = this.getStringWidth(this.statusMessage) / 2;
            this.write(this.statusMessage, n * 2 - n4, n3 * 2 + 19, 16777215);
         }

         GlStateManager.popMatrix();
      } else {
         String str = "";
         str = "("
            + this.formatCoordinate(MinecraftHelper.getBlockX())
            + ", "
            + MinecraftHelper.getPlayerY()
            + ", "
            + this.formatCoordinate(MinecraftHelper.getBlockZ())
            + ") "
            + (int)this.direction
            + "'";
         int n4 = this.getStringWidth(str) / 2;
         this.write(str, this.scaledWidth / 2 - n4, 5, 16777215);
         if (this.messageTimer > 0) {
            n4 = this.getStringWidth(this.statusMessage) / 2;
            this.write(this.statusMessage, this.scaledWidth / 2 - n4, 15, 16777215);
         }
      }
   }

   public void write(String str, int n, int n2, int n3) {
      this.fontRenderer.drawStringWithShadow(str, n, n2, n3);
   }

   @Override
   public void setPermissions(boolean flag, boolean flag2) {
      this.minimapSettings.radarAllowed = flag;
      this.minimapSettings.caveModeAllowed = flag2;
   }

   public void setChunksModified(boolean flag) {
      Chunk chunkFromBlockCoords = this.mc.theWorld.getChunkFromBlockCoords(new BlockPos(this.lastX, 0, this.lastZ));
      int xPosition = chunkFromBlockCoords.xPosition;
      int zPosition = chunkFromBlockCoords.zPosition;
      int n = 0;

      for (boolean flag2 = true; flag2 && n < 25; n++) {
         flag2 = false;

         for (int i = xPosition - n; i <= xPosition + n; i++) {
            Chunk chunkFromChunkCoords = this.mc.theWorld.getChunkFromChunkCoords(i, zPosition - n);
            if (chunkFromChunkCoords.isLoaded()) {
               chunkFromChunkCoords.setModified(flag);
               flag2 = true;
            }

            chunkFromChunkCoords = this.mc.theWorld.getChunkFromChunkCoords(i, zPosition + n);
            if (chunkFromChunkCoords.isLoaded()) {
               chunkFromChunkCoords.setModified(flag);
               flag2 = true;
            }
         }

         for (int i = zPosition - n + 1; i <= zPosition + n - 1; i++) {
            Chunk chunkFromChunkCoords = this.mc.theWorld.getChunkFromChunkCoords(xPosition - n, i);
            if (chunkFromChunkCoords.isLoaded()) {
               chunkFromChunkCoords.setModified(flag);
               flag2 = true;
            }

            chunkFromChunkCoords = this.mc.theWorld.getChunkFromChunkCoords(xPosition + n, i);
            if (chunkFromChunkCoords.isLoaded()) {
               chunkFromChunkCoords.setModified(flag);
               flag2 = true;
            }
         }
      }
   }

   @Override
   public float getPercentY() {
      return this.percentY;
   }

   public void drawMapTexture(int n, int n2, int n3) {
      boolean flag = this.lastScale != n3 || this.minimapSettings.squareMap != this.lastSquareMap;
      this.lastScale = n3;
      this.lastSquareMap = this.minimapSettings.squareMap;
      if (GlUtils.stencilSupported) {
         GlStateManager.colorMask(false, false, false, true);
         GL11.glBindTexture(3553, 0);
         GlStateManager.blendFunc(0, 0);
         GlStateManager.color(0.0F, 0.0F, 255.0F);
         GL11.glBegin(7);
         GL11.glVertex2f(n - 47, n2 + 47);
         GL11.glVertex2f(n + 47, n2 + 47);
         GL11.glVertex2f(n + 47, n2 - 47);
         GL11.glVertex2f(n - 47, n2 - 47);
         GL11.glEnd();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.colorMask(true, true, true, true);
         GlStateManager.blendFunc(770, 771);
         GlUtils.bindTextureLocation(new ResourceLocation("voxelmap/" + (this.minimapSettings.squareMap ? "images/square.png" : "images/circle.png")));
         GlUtils.startDrawing();
         GlUtils.drawQuad(n, n2);
         GlUtils.finishDrawing();
         GlStateManager.colorMask(true, true, true, true);
         GlStateManager.blendFunc(772, 773);
         synchronized (this.bufferLock) {
            if (this.imageChanged) {
               this.imageChanged = false;
               this.mapImages[this.zoom].uploadTexture();
               this.lastImageX = this.lastX;
               this.lastImageZ = this.lastZ;
            }
         }

         float f = 2.0F / (float)Math.pow(2.0, this.zoom);
         this.percentX = (float)(MinecraftHelper.getPlayerX() - this.lastImageX);
         this.percentY = (float)(MinecraftHelper.getPlayerZ() - this.lastImageZ);
         this.percentX *= f;
         this.percentY *= f;
         if (this.zoom == 3) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 1.0F);
            GlUtils.bindTextureId(this.mapImages[this.zoom].textureId);
            GlStateManager.popMatrix();
         } else {
            GlUtils.bindTextureId(this.mapImages[this.zoom].textureId);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate(n, n2, 0.0F);
         GlStateManager.rotate(this.minimapSettings.squareMap ? this.northRotate : -this.direction + this.northRotate, 0.0F, 0.0F, 1.0F);
         GlStateManager.translate(-n, -n2, 0.0F);
         GlStateManager.translate(-this.percentX, -this.percentY, 0.0F);
         if (this.minimapSettings.filtering) {
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
         }
      } else if (GlUtils.framebufferSupported) {
         GL11.glBindTexture(3553, 0);
         GL11.glPushAttrib(22528);
         GlStateManager.viewport(0, 0, 256, 256);
         GlStateManager.matrixMode(5889);
         GlStateManager.pushMatrix();
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0, 256.0, 256.0, 0.0, 1000.0, 3000.0);
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -2000.0F);
         GlUtils.bindFramebuffer();
         GlStateManager.depthMask(false);
         GlStateManager.disableDepth();
         if (flag) {
            GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GlStateManager.clear(16384);
         }

         GlStateManager.blendFunc(770, 0);
         GlUtils.bindTextureLocation(new ResourceLocation("voxelmap/" + (this.minimapSettings.squareMap ? "images/square.png" : "images/circle.png")));
         GlUtils.startDrawing();
         GlUtils.drawVertexTex(0.0, 256.0, 1.0, 0.0, 0.0);
         GlUtils.drawVertexTex(256.0, 256.0, 1.0, 1.0, 0.0);
         GlUtils.drawVertexTex(256.0, 0.0, 1.0, 1.0, 1.0);
         GlUtils.drawVertexTex(0.0, 0.0, 1.0, 0.0, 1.0);
         GlUtils.finishDrawing();
         GL14.glBlendFuncSeparate(1, 0, 774, 0);
         synchronized (this.bufferLock) {
            if (this.imageChanged) {
               this.imageChanged = false;
               this.mapImages[this.zoom].uploadTexture();
               this.lastImageX = this.lastX;
               this.lastImageZ = this.lastZ;
            }
         }

         float f = 2.0F / (float)Math.pow(2.0, this.zoom);
         this.percentX = (float)(MinecraftHelper.getPlayerX() - this.lastImageX);
         this.percentY = (float)(MinecraftHelper.getPlayerZ() - this.lastImageZ);
         this.percentX *= f;
         this.percentY *= f;
         if (this.zoom == 3) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 1.0F);
            GlUtils.bindTextureId(this.mapImages[this.zoom].textureId);
            GlStateManager.popMatrix();
         } else {
            GlUtils.bindTextureId(this.mapImages[this.zoom].textureId);
         }

         if (this.minimapSettings.filtering) {
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
         }

         GlStateManager.translate(128.0F, 128.0F, 0.0F);
         if (this.minimapSettings.squareMap) {
            GlStateManager.rotate(-this.northRotate, 0.0F, 0.0F, 1.0F);
         } else {
            GlStateManager.rotate(this.direction - this.northRotate, 0.0F, 0.0F, 1.0F);
         }

         GlStateManager.translate(-128.0F, -128.0F, 0.0F);
         GlStateManager.translate(-this.percentX * 4.0F, this.percentY * 4.0F, 0.0F);
         GlUtils.startDrawing();
         GlUtils.drawVertexTex(0.0, 256.0, 1.0, 0.0, 0.0);
         GlUtils.drawVertexTex(256.0, 256.0, 1.0, 1.0, 0.0);
         GlUtils.drawVertexTex(256.0, 0.0, 1.0, 1.0, 1.0);
         GlUtils.drawVertexTex(0.0, 0.0, 1.0, 0.0, 1.0);
         GlUtils.finishDrawing();
         GlStateManager.depthMask(true);
         GlStateManager.enableDepth();
         GlUtils.unbindFramebuffer();
         GlStateManager.matrixMode(5889);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GL11.glPopAttrib();
         GlStateManager.pushMatrix();
         GlStateManager.blendFunc(770, 0);
         GlStateManager.enableAlpha();
         GlUtils.bindTextureId(GlUtils.framebufferTexture);
      } else {
         if (this.minimapSettings.squareMap) {
            if (this.minimapSettings.filtering && this.zoom == 0 && this.percentXOverOne != this.percentX > 1.0F) {
               this.percentXOverOne = this.percentX > 1.0F;
               this.imageChanged = true;
            }

            if (this.minimapSettings.filtering && this.zoom == 0 && this.percentYOverOne != this.percentY > 1.0F) {
               this.percentYOverOne = this.percentY > 1.0F;
               this.imageChanged = true;
            }
         }

         if (this.imageChanged) {
            this.imageChanged = false;
            if (this.minimapSettings.squareMap) {
               synchronized (this.bufferLock) {
                  this.mapImages[this.zoom].uploadTexture();
                  this.lastImageX = this.lastX;
                  this.lastImageZ = this.lastZ;
               }
            } else {
               int width = this.mapImages[this.zoom].getWidth();
               if (this.roundMapImage != null) {
                  this.roundMapImage.deleteTexture();
               }

               this.roundMapImage = new GlBufferedImage(width, width, 6);
               Double doubleValue = new Double(this.zoom * 10 / 6, this.zoom * 10 / 6, width - this.zoom * 2, width - this.zoom * 2);
               Graphics2D createGraphics = this.roundMapImage.createGraphics();
               createGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               createGraphics.setClip(doubleValue);
               createGraphics.setColor(new Color(0.1F, 0.0F, 0.0F, 0.1F));
               createGraphics.fillRect(0, 0, width, width);
               synchronized (this.bufferLock) {
                  createGraphics.drawImage(this.mapImages[this.zoom], 0, 0, null);
                  this.lastImageX = this.lastX;
                  this.lastImageZ = this.lastZ;
               }

               createGraphics.dispose();
               this.roundMapImage.uploadTexture();
            }
         }

         float f = 2.0F / (float)Math.pow(2.0, this.zoom);
         this.percentX = (float)(MinecraftHelper.getPlayerX() - this.lastImageX);
         this.percentY = (float)(MinecraftHelper.getPlayerZ() - this.lastImageZ);
         this.percentX *= f;
         this.percentY *= f;
         GlStateManager.blendFunc(770, 0);
         if (this.zoom == 3) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 1.0F);
            GlUtils.bindTextureId(this.minimapSettings.squareMap ? this.mapImages[this.zoom].textureId : this.roundMapImage.textureId);
            GlStateManager.popMatrix();
         } else {
            GlUtils.bindTextureId(this.minimapSettings.squareMap ? this.mapImages[this.zoom].textureId : this.roundMapImage.textureId);
         }

         if (this.minimapSettings.filtering) {
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate(n, n2, 0.0F);
         GlStateManager.rotate(this.minimapSettings.squareMap ? this.northRotate : -this.direction + this.northRotate, 0.0F, 0.0F, 1.0F);
         GlStateManager.translate(-n, -n2, 0.0F);
         GlStateManager.translate(-this.percentX, -this.percentY, 0.0F);
      }

      GlUtils.startDrawing();
      GlUtils.drawQuad(n, n2);
      GlUtils.finishDrawing();
      GlStateManager.popMatrix();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.minimapSettings.squareMap) {
         this.drawSquareMapFrame(n, n2);
      } else {
         this.drawRoundMapFrame(n, n2);
      }

      double playerX = MinecraftHelper.getPlayerX();
      double playerZ = MinecraftHelper.getPlayerZ();

      for (Waypoint waypoint : this.waypointManager.getWaypoints()) {
         if (waypoint.isActive()) {
            double d = playerX - waypoint.getX() - 0.5;
            double d2 = playerZ - waypoint.getZ() - 0.5;
            float toDegrees = (float)Math.toDegrees(Math.atan2(d, d2));
            double sqrt = Math.sqrt(d * d + d2 * d2);
            boolean flag2 = false;
            if (this.minimapSettings.squareMap) {
               flag2 = Math.abs(d) / (Math.pow(2.0, this.zoom) / 2.0) > 28.5 || Math.abs(d2) / (Math.pow(2.0, this.zoom) / 2.0) > 28.5;
               if (flag2) {
                  sqrt = sqrt / Math.max(Math.abs(d), Math.abs(d2)) * 30.0;
               } else {
                  sqrt /= Math.pow(2.0, this.zoom) / 2.0;
               }
            } else {
               toDegrees += this.direction;
               sqrt /= Math.pow(2.0, this.zoom) / 2.0;
               flag2 = sqrt >= 31.0;
               if (flag2) {
                  sqrt = 34.0;
               }
            }

            if (flag2) {
               try {
                  GlStateManager.pushMatrix();
                  GlStateManager.color(waypoint.red, waypoint.green, waypoint.blue);
                  if (n3 >= 3) {
                     GlUtils.bindTextureLocation(new ResourceLocation("voxelmap/images/marker" + waypoint.imageSuffix + ".png"));
                  } else {
                     GlUtils.bindTextureLocation(new ResourceLocation("voxelmap/images/marker" + waypoint.imageSuffix + "Small.png"));
                  }

                  GL11.glTexParameteri(3553, 10241, 9729);
                  GL11.glTexParameteri(3553, 10240, 9729);
                  GlStateManager.translate(n, n2, 0.0F);
                  GlStateManager.rotate(-toDegrees + this.northRotate, 0.0F, 0.0F, 1.0F);
                  GlStateManager.translate(-n, -n2, 0.0F);
                  GlStateManager.translate(0.0, -sqrt, 0.0);
                  GlUtils.startDrawing();
                  GlUtils.drawQuadSized(n, n2, 16);
                  GlUtils.finishDrawing();
               } catch (Exception e) {
                  this.statusMessage = "Error: marker overlay not found!";
               } finally {
                  GlStateManager.popMatrix();
               }
            } else {
               try {
                  GlStateManager.pushMatrix();
                  GlStateManager.color(waypoint.red, waypoint.green, waypoint.blue);
                  if (n3 >= 3) {
                     GlUtils.bindTextureLocation(new ResourceLocation("voxelmap/images/waypoint" + waypoint.imageSuffix + ".png"));
                  } else {
                     GlUtils.bindTextureLocation(new ResourceLocation("voxelmap/images/waypoint" + waypoint.imageSuffix + "Small.png"));
                  }

                  GL11.glTexParameteri(3553, 10241, 9729);
                  GL11.glTexParameteri(3553, 10240, 9729);
                  GlStateManager.rotate(-toDegrees + this.northRotate, 0.0F, 0.0F, 1.0F);
                  GlStateManager.translate(0.0, -sqrt, 0.0);
                  GlStateManager.rotate(-(-toDegrees + this.northRotate), 0.0F, 0.0F, 1.0F);
                  GlUtils.startDrawing();
                  GlUtils.drawQuadSized(n, n2, 16);
                  GlUtils.finishDrawing();
               } catch (Exception e) {
                  this.statusMessage = "Error: waypoint overlay not found!";
               } finally {
                  GlStateManager.popMatrix();
               }
            }
         }
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static int getBlockMetadata(World world, int n, int n2, int n3) {
      BlockPos blockPos = new BlockPos(n, n2, n3);
      return world.getBlockState(blockPos).getBlock().getMetaFromState(world.getBlockState(blockPos));
   }

   public String formatCoordinate(int n) {
      if (n < 0) {
         return "-" + Math.abs(n);
      } else {
         return n > 0 ? "+" + n : " " + n;
      }
   }

   public int getBlockColorMultiplier(int n, int n2, int n3, int n4, int n5) {
      int n6 = -1;
      if (n == BlockIdRepository.grassId
         || n == BlockIdRepository.leavesId
         || n == BlockIdRepository.leaves2Id
         || n == BlockIdRepository.tallgrassId
         || n == BlockIdRepository.vineId
         || n == BlockIdRepository.doublePlantId
         || n == BlockIdRepository.waterId
         || n == BlockIdRepository.flowingWaterId
         || this.colorManager.getBiomeTintedBlocks().contains(n)) {
         n6 = Block.getBlockById(n).colorMultiplier(this.world, new BlockPos(n3, n4, n5), 0) | 0xFF000000;
      }

      return n6;
   }

   public void drawQuad(double d, double d2, double d3, double d4) {
      GlUtils.startDrawing();
      GlUtils.drawVertex(d, d4, 0.0);
      GlUtils.drawVertex(d2, d4, 0.0);
      GlUtils.drawVertex(d2, d3, 0.0);
      GlUtils.drawVertex(d, d3, 0.0);
      GlUtils.finishDrawing();
   }

   @Override
   public void run() {
      if (this.mc != null) {
         while (true) {
            while (!this.threading) {
               synchronized (this.calculationThread) {
                  try {
                     this.calculationThread.wait(0L);
                  } catch (InterruptedException e) {
                  }
               }
            }

            for (this.calculating = true; this.mc.thePlayer != null && this.calculating; this.calculating = false) {
               if (!this.minimapSettings.hide) {
                  try {
                     this.calculateMap(this.fullRender);
                     if (!this.fullRender) {
                        boolean flag = !this.minimapSettings.worldDownloadCompatibility;
                        this.chunkCache[this.zoom].moveTo(this.lastX, this.lastZ);
                        this.chunkCache[this.zoom].update(flag);
                        if (flag != !this.minimapSettings.worldDownloadCompatibility) {
                           this.setChunksModified(true);
                        }
                     }
                  } catch (Exception e) {
                  }
               }

               this.fullRender = this.pendingFullRender;
               this.pendingFullRender = false;
            }

            synchronized (this.calculationThread) {
               try {
                  this.calculationThread.wait(0L);
               } catch (InterruptedException e) {
               }
            }
         }
      }
   }

   public MinimapMap(IVoxelMap voxelMap) {
      this.threading = this.multicore;
      this.needSkyColor = false;
      this.lastWorldChangeTime = 0L;
      this.checkChatForPermissions = false;
      this.chatLine = null;
      this.world = null;
      this.mapImageSize = 256;
      this.waypointRendererRegistered = false;
      this.availableProcessors = Runtime.getRuntime().availableProcessors();
      this.multicore = this.availableProcessors > 0;
      this.mapData = new MapData[4];
      this.chunkCache = new MapChunkCache[4];
      this.mapImages = new GlBufferedImage[4];
      this.imageChanged = true;
      this.dynamicTexture = null;
      this.needLight = true;
      this.lastGamma = 0.0F;
      this.lastSunBrightness = 0.0F;
      this.lastLightning = 0.0F;
      this.lastNightVision = 0.0F;
      this.lastLightmapColors = new int[]{
         -16777216,
         -16777216,
         -16777216,
         -16777216,
         -16777216,
         -16777216,
         -16777216,
         -16777216,
         -16777216,
         -16777216,
         -16777216,
         -16777216,
         -16777216,
         -16777216,
         -16777216,
         -16777216
      };
      this.lastUnderground = false;
      this.lastAboveHorizon = true;
      this.lastBiomeId = 0;
      this.lastNeutralColor = 0;
      this.guiScreen = null;
      this.showMinimap = true;
      this.statusMessage = "";
      this.welcomeLines = new String[8];
      this.messageTimer = 0;
      this.heightChangeCounter = 0;
      this.tickCounter = 0;
      this.lastY = 0;
      this.lastImageX = 0;
      this.lastImageZ = 0;
      this.lastFullscreen = false;
      this.direction = 0.0F;
      this.worldName = "";
      this.heightChangeThreshold = this.multicore ? 2 : 5;
      this.heightChangeTicks = this.multicore ? 300 : 3000;
      this.lightmapColors = new int[256];
      this.unusedFlag1 = false;
      this.unusedFlag2 = false;
      this.developer = false;
      this.voxelMap = voxelMap;
      this.mc = MinecraftHelper.getMc();
      this.minimapSettings = voxelMap.getMapOptions();
      this.radar = voxelMap.getRadar();
      this.colorManager = voxelMap.getColorManager();
      this.waypointManager = voxelMap.getWaypointManager();
      this.dimensionManager = voxelMap.getDimensionManager();
      this.radarContext = new RadarContext();

      try {
         NetworkUtils.loadInterfaces();
      } catch (SocketException e) {
         System.err.println("could not get network interface addresses");
         e.printStackTrace();
      }

      ArrayList list = new ArrayList();
      list.addAll(Arrays.asList(this.mc.gameSettings.keyBindings));
      list.addAll(Arrays.asList(this.minimapSettings.keyBindings));
      this.mc.gameSettings.keyBindings = (KeyBinding[])list.toArray(new KeyBinding[list.size()]);
      this.calculationThread.start();
      this.calculationThread.setPriority(5);
      this.mapData[0] = new MapData(32, 32);
      this.mapData[1] = new MapData(64, 64);
      this.mapData[2] = new MapData(128, 128);
      this.mapData[3] = new MapData(256, 256);
      this.chunkCache[0] = new MapChunkCache(3, 3, this);
      this.chunkCache[1] = new MapChunkCache(5, 5, this);
      this.chunkCache[2] = new MapChunkCache(9, 9, this);
      this.chunkCache[3] = new MapChunkCache(17, 17, this);
      this.mapImages[0] = new GlBufferedImage(32, 32, 6);
      this.mapImages[1] = new GlBufferedImage(64, 64, 6);
      this.mapImages[2] = new GlBufferedImage(128, 128, 6);
      this.mapImages[3] = new GlBufferedImage(256, 256, 6);
      this.roundMapImage = new GlBufferedImage(128, 128, 6);
      this.welcomeLines[0] = EnumChatFormatting.DARK_RED
         + "VoxelMap"
         + EnumChatFormatting.WHITE
         + "! "
         + this.version
         + " "
         + Translations.translate("minimap.ui.welcome1");
      this.welcomeLines[1] = Translations.translate("minimap.ui.welcome2");
      this.welcomeLines[2] = Translations.translate("minimap.ui.welcome3");
      this.welcomeLines[3] = Translations.translate("minimap.ui.welcome4");
      this.welcomeLines[4] = EnumChatFormatting.AQUA
         + MinimapSettings.getKeyName(this.minimapSettings.keyMinimapZoom.getKeyCode())
         + EnumChatFormatting.WHITE
         + ": "
         + Translations.translate("minimap.ui.welcome5a")
         + ", "
         + EnumChatFormatting.AQUA
         + ": "
         + MinimapSettings.getKeyName(this.minimapSettings.keyMinimapVoxelmapmenu.getKeyCode())
         + EnumChatFormatting.WHITE
         + ": "
         + Translations.translate("minimap.ui.welcome5b");
      this.welcomeLines[5] = EnumChatFormatting.AQUA
         + MinimapSettings.getKeyName(this.minimapSettings.keyMinimapTogglefullscreen.getKeyCode())
         + EnumChatFormatting.WHITE
         + ": "
         + Translations.translate("minimap.ui.welcome6");
      this.welcomeLines[6] = EnumChatFormatting.AQUA
         + MinimapSettings.getKeyName(this.minimapSettings.keyMinimapWaypointhotkey.getKeyCode())
         + EnumChatFormatting.WHITE
         + ": "
         + Translations.translate("minimap.ui.welcome7");
      this.welcomeLines[7] = EnumChatFormatting.WHITE
         + MinimapSettings.getKeyName(this.minimapSettings.keyMinimapZoom.getKeyCode())
         + EnumChatFormatting.GRAY
         + ": "
         + Translations.translate("minimap.ui.welcome8");
      if (GlUtils.framebufferSupported) {
         GlUtils.setupFramebuffer();
      }

      RenderManager renderManager = RenderManager.renderManager;
      if (renderManager != null) {
         WaypointRenderer waypointRenderer = new WaypointRenderer(this.minimapSettings);
         renderManager.getEntityRenderMap().put(EntityWaypointContainer.class, waypointRenderer);
         this.waypointRendererRegistered = true;
      }
   }

   public void drawDirections(int n, int n2) {
      float f;
      float f2;
      if (this.minimapSettings.squareMap) {
         f = -90.0F;
         f2 = 67.0F;
      } else {
         f = -this.direction - 90.0F;
         f2 = 64.0F;
      }

      GlStateManager.pushMatrix();
      GlStateManager.scale(0.5F, 0.5F, 1.0F);
      GlStateManager.translate(f2 * Math.sin(Math.toRadians(-(f - 90.0))), f2 * FastTrig.cos(Math.toRadians(-(f - 90.0))), 0.0);
      this.write("N", n * 2 - 2, n2 * 2 - 4, 16777215);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scale(0.5F, 0.5F, 1.0F);
      GlStateManager.translate(f2 * Math.sin(Math.toRadians(-f)), f2 * FastTrig.cos(Math.toRadians(-f)), 0.0);
      this.write("E", n * 2 - 2, n2 * 2 - 4, 16777215);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scale(0.5F, 0.5F, 1.0F);
      GlStateManager.translate(f2 * Math.sin(Math.toRadians(-(f + 90.0))), f2 * FastTrig.cos(Math.toRadians(-(f + 90.0))), 0.0);
      this.write("S", n * 2 - 2, n2 * 2 - 4, 16777215);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scale(0.5F, 0.5F, 1.0F);
      GlStateManager.translate(f2 * Math.sin(Math.toRadians(-(f + 180.0))), f2 * FastTrig.cos(Math.toRadians(-(f + 180.0))), 0.0);
      this.write("W", n * 2 - 2, n2 * 2 - 4, 16777215);
      GlStateManager.popMatrix();
   }

   @Override
   public void forceFullRender(boolean flag) {
      this.fullRender = flag;
   }

   public void drawWelcomeScreen(int n, int n2) {
      GlStateManager.blendFunc(770, 771);
      int n3 = 0;
      byte b = 2;
      String str = this.welcomeLines[0];

      int n4;
      for (n4 = 1; n4 < this.welcomeLines.length - 1; n4++) {
         if (this.getStringWidth(this.welcomeLines[n4]) > n3) {
            n3 = this.getStringWidth(this.welcomeLines[n4]);
         }
      }

      int stringWidth = this.getStringWidth(str);
      int n5 = (int)((n + 5) / 2.0);
      int n6 = (int)((n2 + 5) / 2.0);
      String str2 = this.welcomeLines[this.welcomeLines.length - 1];
      int stringWidth2 = this.getStringWidth(str2);
      GlStateManager.disableTexture2D();
      GlStateManager.color(0.0F, 0.0F, 0.0F, 0.7F);
      double d = n5 - stringWidth / 2.0 - b;
      double d2 = n5 + stringWidth / 2.0 + b;
      double d3 = n6 - (n4 - 1) / 2.0 * 10.0 - b - 20.0;
      double d4 = n6 - (n4 - 1) / 2.0 * 10.0 + b - 10.0;
      this.drawQuad(d, d2, d3, d4);
      d = n5 - n3 / 2.0 - b;
      d2 = n5 + n3 / 2.0 + b;
      d3 = n6 - (n4 - 1) / 2.0 * 10.0 - b;
      d4 = n6 + (n4 - 1) / 2.0 * 10.0 + b;
      this.drawQuad(d, d2, d3, d4);
      d = n5 - stringWidth2 / 2.0 - b;
      d2 = n5 + stringWidth2 / 2.0 + b;
      d3 = n6 + (n4 - 1) / 2.0 * 10.0 - b + 10.0;
      d4 = n6 + (n4 - 1) / 2.0 * 10.0 + b + 20.0;
      this.drawQuad(d, d2, d3, d4);
      GlStateManager.enableTexture2D();
      this.write(str, n5 - stringWidth / 2, n6 - (n4 - 1) * 10 / 2 - 19, 16777215);

      for (int i = 1; i < n4; i++) {
         this.write(this.welcomeLines[i], n5 - n3 / 2, n6 - (n4 - 1) * 10 / 2 + i * 10 - 9, 16777215);
      }

      this.write(str2, n5 - stringWidth2 / 2, (n2 + 5) / 2 + (n4 - 1) * 10 / 2 + 11, 16777215);
   }

   public void closeCurrentScreen() {
      this.mc.currentScreen = null;
   }

   public Object getCurrentScreen() {
      return this.mc.currentScreen;
   }

   public int getStringWidth(String str) {
      return this.fontRenderer.getStringWidth(str);
   }

   public String getServerName() {
      try {
         ServerData currentServerData = this.mc.getCurrentServerData();
         if (currentServerData != null) {
            boolean flag = false;
            if (currentServerData.populationInfo == null && currentServerData.serverMOTD == null) {
               try {
                  String serverIP = currentServerData.serverIP;
                  int lastIndexOf = serverIP.lastIndexOf(":");
                  if (lastIndexOf != -1) {
                     serverIP = serverIP.substring(0, lastIndexOf);
                  }

                  InetAddress byName = Inet4Address.getByName(serverIP);
                  flag = NetworkUtils.isLanAddress(byName);
               } catch (Exception e) {
                  System.err.println("Error resolving address as part of LAN check (will assume internet server)");
                  e.printStackTrace();
               }
            }

            if (flag) {
               System.out.println("LAN server detected!");
               return currentServerData.serverName;
            }

            return currentServerData.serverIP;
         }
      } catch (Exception e) {
         System.err.println("error getting ServerData");
         e.printStackTrace();
      }

      return "";
   }

   public void drawFullscreenMap(int n, int n2) {
      synchronized (this.bufferLock) {
         if (this.imageChanged) {
            this.imageChanged = false;
            this.mapImages[this.zoom].uploadTexture();
            this.lastImageX = this.lastX;
            this.lastImageZ = this.lastZ;
         }
      }

      GlUtils.bindTextureId(this.mapImages[this.zoom].textureId);
      if (this.minimapSettings.filtering) {
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate(n / 2.0F, n2 / 2.0F, 0.0F);
      GlStateManager.rotate(this.northRotate, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(-(n / 2.0F), -(n2 / 2.0F), 0.0F);
      GlUtils.startDrawing();
      int n3 = n / 2 - 128;
      int n4 = n2 / 2 - 128;
      GlUtils.drawVertexIntTex(n3, n4 + 256, 67.0, 0.0, 1.0);
      GlUtils.drawVertexIntTex(n3 + 256, n4 + 256, 67.0, 1.0, 1.0);
      GlUtils.drawVertexIntTex(n3 + 256, n4, 67.0, 1.0, 0.0);
      GlUtils.drawVertexIntTex(n3, n4, 67.0, 0.0, 0.0);
      GlUtils.finishDrawing();
      GlStateManager.popMatrix();
      if (this.minimapSettings.biomeOverlay > 0) {
         int pow = (int)Math.pow(2.0, 3 - this.zoom);
         int pow2 = (int)Math.pow(2.0, this.zoom);
         pow2 *= pow2;
         ArrayList biomeLabels = this.mapData[this.zoom].getBiomeLabels();
         GlStateManager.disableDepth();

         for (int i = 0; i < biomeLabels.size(); i++) {
            BiomeLabel biomeLabel = (BiomeLabel)biomeLabels.get(i);
            if (biomeLabel.cellCount > pow2) {
               String biomeName = BiomeGenBase.getBiomeGenArray()[biomeLabel.biomeId].biomeName;
               int stringWidth = this.getStringWidth(biomeName);
               int n5 = biomeLabel.x * pow;
               int n6 = biomeLabel.z * pow;
               if (this.minimapSettings.oldNorth) {
                  this.write(biomeName, n3 + 256 - n6 - stringWidth / 2, n4 + n5 - 3, 16777215);
               } else {
                  this.write(biomeName, n3 + n5 - stringWidth / 2, n4 + n6 - 3, 16777215);
               }
            }
         }

         GlStateManager.enableDepth();
      }
   }

   public void cycleZoom() {
      if (this.welcomeState != 0) {
         this.welcomeState = 0;
         if (this.getCurrentScreen() != null) {
            this.closeCurrentScreen();
         }
      } else {
         if (this.minimapSettings.zoom == 0) {
            this.minimapSettings.zoom = 3;
            this.statusMessage = Translations.translate("minimap.ui.zoomlevel") + " (0.5x)";
         } else if (this.minimapSettings.zoom == 3) {
            this.minimapSettings.zoom = 2;
            this.statusMessage = Translations.translate("minimap.ui.zoomlevel") + " (1.0x)";
         } else if (this.minimapSettings.zoom == 2) {
            this.minimapSettings.zoom = 1;
            this.statusMessage = Translations.translate("minimap.ui.zoomlevel") + " (2.0x)";
         } else {
            this.minimapSettings.zoom = 0;
            this.statusMessage = Translations.translate("minimap.ui.zoomlevel") + " (4.0x)";
         }

         this.minimapSettings.save();
         this.mapImages[this.minimapSettings.zoom].clear();
         this.pendingFullRender = true;
         this.fullRender = true;
      }
   }

   public void checkChatForPermissions() {
      if (System.currentTimeMillis() - this.lastWorldChangeTime < 5000L) {
         GuiNewChat chatGUI = this.mc.ingameGUI.getChatGUI();
         if (chatGUI == null) {
            System.out.println("failed to get guiNewChat");
         } else {
            List chatLines = chatGUI.chatLines;
            boolean flag = false;
            boolean flag2 = false;

            for (int i = 0; i < chatLines.size(); i++) {
               ChatLine chatLine = (ChatLine)chatLines.get(i);
               if (chatLine.equals(this.chatLine)) {
                  break;
               }

               String formattedText = chatLine.getChatComponent().getFormattedText();
               formattedText = formattedText.replaceAll("ï¿½r", "");
               if (formattedText.contains("ï¿½3 ï¿½6 ï¿½3 ï¿½6 ï¿½3 ï¿½6 ï¿½e")) {
                  flag = true;
                  this.statusMessage = "Server disabled radar";
               }

               if (formattedText.contains("ï¿½3 ï¿½6 ï¿½3 ï¿½6 ï¿½3 ï¿½6 ï¿½d")) {
                  flag2 = true;
                  this.statusMessage = "Server disabled cavemapping";
               }
            }

            this.minimapSettings.radarAllowed = this.minimapSettings.radarAllowed && !flag;
            this.minimapSettings.caveModeAllowed = this.minimapSettings.caveModeAllowed && !flag2;
            this.chatLine = chatLines.size() > 0 ? (ChatLine)chatLines.get(0) : null;
         }
      } else {
         this.checkChatForPermissions = false;
      }
   }

   public int getBlockHeight(boolean flag, boolean flag2, boolean flag3, World world, int n, int n2, int n3) {
      int n4 = world.getTopSolidOrLiquidBlock(new BlockPos(n, 0, n2)).getY();
      if ((flag || flag3) && n4 >= n3 && (!flag || n3 <= 125 || this.minimapSettings.caveMode && !flag2)) {
         int lastY = this.lastY;
         Block block = world.getBlockState(new BlockPos(n, lastY, n2)).getBlock();
         if (block.getLightOpacity() == 0 && block.getMaterial() != Material.lava) {
            while (lastY > 0) {
               block = world.getBlockState(new BlockPos(n, --lastY, n2)).getBlock();
               if (block.getLightOpacity() > 0 || block.getMaterial() == Material.lava) {
                  return lastY + 1;
               }
            }

            return lastY;
         } else {
            while (lastY <= n3 + 10) {
               if (lastY < (flag && n3 < 126 ? 127 : 255)) {
                  block = world.getBlockState(new BlockPos(n, ++lastY, n2)).getBlock();
                  if (block.getLightOpacity() == 0 && block.getMaterial() != Material.lava) {
                     return lastY;
                  }
               }
            }

            return -1;
         }
      } else {
         int n5 = world.getPrecipitationHeight(new BlockPos(n, 0, n2)).getY();
         if (n5 != n4) {
            Block block = world.getBlockState(new BlockPos(n, n5 - 1, n2)).getBlock();
            if (block.getMaterial() == Material.lava) {
               n4 = n5;
            }
         }

         for (int i = (n4 >> 4) * 16 + 15; i < 256; i += 16) {
            Block block = world.getBlockState(new BlockPos(n, i, n2)).getBlock();
            if (block.getLightOpacity() > 0) {
               n4 = i + 1;
            }
         }

         return n4;
      }
   }

   public int getSkyColor() {
      this.needSkyColor = false;
      boolean flag = this.mc.thePlayer.posY >= this.world.getHorizon();
      float[] floats = new float[16];
      FloatBuffer createFloatBuffer = BufferUtils.createFloatBuffer(16);
      GL11.glGetFloat(3106, createFloatBuffer);
      createFloatBuffer.get(floats);
      double d = floats[0];
      double d2 = floats[1];
      double d3 = floats[2];
      int n = -16777216 + (int)(d * 255.0) * 65536 + (int)(d2 * 255.0) * 256 + (int)(d3 * 255.0);
      if (this.mc.theWorld.provider.isSurfaceWorld() && this.mc.gameSettings.getOptionFloatValue(Options.RENDER_DISTANCE) >= 4.0F) {
         double d4;
         double d5;
         double d6;
         if (!flag) {
            d6 = 0.0;
            d5 = 0.0;
            d4 = 0.0;
         } else {
            Vec3 skyColor = this.world.getSkyColor(this.mc.thePlayer, 0.0F);
            d4 = skyColor.xCoord;
            d5 = skyColor.yCoord;
            d6 = skyColor.zCoord;
            if (this.world.provider.isSkyColored()) {
               d4 = d4 * 0.2F + 0.04F;
               d5 = d5 * 0.2F + 0.04F;
               d6 = d6 * 0.6F + 0.1F;
            }
         }

         boolean doesXZShowFog = this.world.provider.doesXZShowFog(MinecraftHelper.getBlockX(), MinecraftHelper.getBlockZ());
         float f = this.mc.gameSettings.getOptionFloatValue(Options.RENDER_DISTANCE) * 16.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;
         if (doesXZShowFog) {
            f2 = f * 0.05F;
            f3 = Math.min(f, 192.0F) * 0.5F;
         } else {
            f3 = f * 0.8F;
         }

         float max = Math.max(0.0F, Math.min(1.0F, (f3 - (MinecraftHelper.getPlayerY() - (float)this.mc.theWorld.getHorizon())) / (f3 - f2)));
         int n2 = (int)(max * 255.0F) * 16777216 + (int)(d4 * 255.0) * 65536 + (int)(d5 * 255.0) * 256 + (int)(d6 * 255.0);
         return this.colorManager.colorAdder(n2, n);
      } else {
         return n;
      }
   }

   public void recalculateRegion(int n, int n2, int n3, int n4) {
      boolean flag = false;
      boolean flag2 = false;
      boolean flag3 = false;
      if (this.mc.thePlayer.dimension != -1) {
         flag2 = this.minimapSettings.caveModeAllowed
            && this.minimapSettings.caveMode
            && this.world
                  .getChunkFromBlockCoords(new BlockPos(this.lastX, 0, this.lastZ))
                  .getLightFor(EnumSkyBlock.SKY, new BlockPos(this.lastX, Math.max(Math.min(MinecraftHelper.getPlayerY(), 255), 0), this.lastZ))
               <= 0;
      } else {
         flag = true;
         flag3 = this.world.getTopSolidOrLiquidBlock(new BlockPos(this.lastX, 0, this.lastZ)).getY() < MinecraftHelper.getPlayerY();
      }

      int lastX = this.lastX;
      int lastZ = this.lastZ;
      int pow = (int)Math.pow(2.0, this.zoom);
      lastX -= 16 * pow;
      lastZ -= 16 * pow;
      n = n - lastX - 1;
      n3 = n3 - lastX + 1;
      n2 = n2 - lastZ - 1;
      n4 = n4 - lastZ + 1;
      n = Math.max(0, n);
      n3 = Math.min(32 * pow - 1, n3);
      n2 = Math.max(0, n2);
      n4 = Math.min(32 * pow - 1, n4);
      int n5 = 0;

      for (int i = n4; i >= n2; i--) {
         for (int j = n; j <= n3; j++) {
            n5 = this.getPixelColor(true, true, true, true, flag, flag3, flag2, this.world, pow, lastX, lastZ, j, i);
            this.mapImages[this.zoom].setRGB(j, i, n5);
         }
      }

      this.imageChanged = true;
   }

   public void checkWorldChange() {
      boolean flag = false;
      MinecraftServer server = MinecraftServer.getServer();
      if (server != null && server != this.minecraftServer && server.getCommandManager() instanceof ServerCommandManager) {
         this.minecraftServer = server;
         ServerCommandManager commandManager = (ServerCommandManager)server.getCommandManager();
         commandManager.registerCommand(new ZansTeleportCommand(this.waypointManager));
      }

      if (this.checkChatForPermissions) {
         this.checkChatForPermissions();
      }

      if (MinecraftHelper.getWorld() != null && !MinecraftHelper.getWorld().equals(this.world)) {
         String str;
         if (this.mc.isIntegratedServerRunning()) {
            str = this.getIntegratedWorldName();
         } else {
            str = this.getServerName();
            if (str != null) {
               str = str.toLowerCase();
            }
         }

         if (!this.worldName.equals(str) && str != null && !str.equals("")) {
            this.dynamicTexture = this.getLightmapTexture();
            flag = true;
            this.worldName = str;
            this.waypointManager.onWorldChange();
            this.minimapSettings.radarAllowed = this.radar != null;
            this.minimapSettings.caveModeAllowed = this.radar != null;
            if (!this.mc.isIntegratedServerRunning()) {
               this.lastWorldChangeTime = System.currentTimeMillis();
               this.checkChatForPermissions = true;
            }

            this.dimensionManager.populateDimensions();
            this.developer = false;
            if (this.mc.thePlayer != null) {
               int n = this.mc.thePlayer.getName().toLowerCase().hashCode();

               for (int i = 0; i < this.developerNameHashes.length; i++) {
                  if (n == this.developerNameHashes[i]) {
                     this.developer = true;
                  }
               }
            }
         }

         flag = true;
         this.world = MinecraftHelper.getWorld();
         this.waypointManager.setDimensionFilter(this.mc.thePlayer.dimension);
         this.dimensionManager.ensureDimension(this.world.provider.getDimensionId());
      }

      if (this.colorManager.getResourcePackChanged()) {
         flag = true;
      }

      if (this.minimapSettings.consumeChanged()) {
         flag = true;
      }

      if (flag) {
         this.fullRender = true;
      }

      this.getClass();
      this.unusedFlag2 = false;
   }

   @Override
   public void chunkCalc(Chunk chunk) {
      this.voxelMap.getChangeNotifier().chunkChanged(chunk);
      this.recalculateRegion(chunk.xPosition * 16, chunk.zPosition * 16, chunk.xPosition * 16 + 15, chunk.zPosition * 16 + 15);
   }

   public int getTransparentHeight(boolean flag, boolean flag2, boolean flag3, World world, int n, int n2, int n3) {
      int n4 = -1;
      if (!flag3 && !flag || flag && n3 > 125 && (!this.minimapSettings.caveMode || flag2)) {
         int n5 = world.getPrecipitationHeight(new BlockPos(n, 0, n2)).getY();
         if (n5 <= n3) {
            n4 = n3 + 1;
         } else {
            n4 = n5;
         }
      } else {
         n4 = n3 + 1;
      }

      Material material = world.getBlockState(new BlockPos(n, n4 - 1, n2)).getBlock().getMaterial();
      if (material == Material.snow || material == Material.air) {
         n4 = -1;
      }

      return n4;
   }

   public int applyHeightShading(
      int n, boolean flag, boolean flag2, boolean flag3, World world, int n2, int n3, int n4, int n5, int n6, int n7, boolean flag4, int n8
   ) {
      if (n != this.colorManager.getAirColor() && n != 0) {
         int n9 = 0;
         if ((this.minimapSettings.heightMap || this.minimapSettings.slopeMap) && !flag4) {
            int n10 = 0;
            double d = 0.0;
            if (!this.minimapSettings.slopeMap) {
               if (this.minimapSettings.heightMap) {
                  n10 = n7 - this.lastY;
                  d = Math.log10(Math.abs(n10) / 8.0 + 1.0) / 1.8;
                  if (n10 < 0) {
                     d = 0.0 - d;
                  }
               }
            } else {
               if (n5 > 0 && n6 < 32 * n2 - 1) {
                  if (n8 == 0) {
                     n9 = this.mapData[this.zoom].getOceanFloorHeight(n5 - 1, n6 + 1);
                  }

                  if (n8 == 1) {
                     n9 = this.mapData[this.zoom].getHeight(n5 - 1, n6 + 1);
                  }

                  if (n8 == 2) {
                     n9 = this.mapData[this.zoom].getTransparentHeight(n5 - 1, n6 + 1);
                     if (n9 == -1) {
                        Block blockById = Block.getBlockById(this.mapData[this.zoom].getTransparentBlockId(n5, n6));
                        if (blockById instanceof BlockGlass || blockById instanceof BlockStainedGlass) {
                           n9 = this.mapData[this.zoom].getHeight(n5 - 1, n6 + 1);
                        }
                     }
                  }
               } else {
                  if (n8 == 0) {
                     int blockHeight = this.getBlockHeight(flag, flag2, flag3, world, n3 + n5 - 1, n4 + n6 + 1, this.lastY);
                     n9 = this.getSeafloorHeight(world, n3 + n5 - 1, n4 + n6 + 1, blockHeight);
                  }

                  if (n8 == 1) {
                     n9 = this.getBlockHeight(flag, flag2, flag3, world, n3 + n5 - 1, n4 + n6 + 1, this.lastY);
                  }

                  if (n8 == 2) {
                     int blockHeight = this.getBlockHeight(flag, flag2, flag3, world, n3 + n5 - 1, n4 + n6 + 1, this.lastY);
                     n9 = this.getTransparentHeight(flag, flag2, flag3, world, n3 + n5 - 1, n4 + n6 + 1, blockHeight);
                     if (n9 == -1) {
                        Block block = world.getBlockState(new BlockPos(n3 + n5, n7 - 1, n4 + n6)).getBlock();
                        if (block instanceof BlockGlass || block instanceof BlockStainedGlass) {
                           n9 = blockHeight;
                        }
                     }
                  }
               }

               if (n9 == -1) {
                  n9 = n7;
               }

               n10 = n9 - n7;
               if (n10 != 0) {
                  d = n10 < 0 ? -1.0 : (n10 > 0 ? 1.0 : 0.0);
                  d /= 8.0;
               }

               if (this.minimapSettings.heightMap) {
                  n10 = n7 - this.lastY;
                  double d2 = Math.log10(Math.abs(n10) / 8.0 + 1.0) / 3.0;
                  d = n10 > 0 ? d + d2 : d - d2;
               }
            }

            int n11 = n >> 24 & 0xFF;
            int n12 = n >> 16 & 0xFF;
            int n13 = n >> 8 & 0xFF;
            int n14 = n >> 0 & 0xFF;
            if (d > 0.0) {
               n12 += (int)(d * (255 - n12));
               n13 += (int)(d * (255 - n13));
               n14 += (int)(d * (255 - n14));
            } else if (d < 0.0) {
               d = Math.abs(d);
               n12 -= (int)(d * n12);
               n13 -= (int)(d * n13);
               n14 -= (int)(d * n14);
            }

            n = n11 * 16777216 + n12 * 65536 + n13 * 256 + n14;
         }
      }

      return n;
   }

   public void drawSquareMapFrame(int n, int n2) {
      try {
         GlUtils.bindTextureId(this.colorManager.getMapImageTextureId());
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10242, 10496);
         GL11.glTexParameteri(3553, 10243, 10496);
         GlUtils.startDrawing();
         GlUtils.drawQuad(n, n2);
         GlUtils.finishDrawing();
      } catch (Exception e) {
         this.statusMessage = "error: minimap overlay not found!";
      }
   }

   @Override
   public void drawMinimap(Minecraft mc) {
      int n = 1;

      while (this.mc.displayWidth / (n + 1) >= 320 && this.mc.displayHeight / (n + 1) >= 240) {
         n++;
      }

      n += this.fullscreenMap ? 0 : this.minimapSettings.mapSize;
      if (n == 0) {
         n = 1;
      }

      double d = this.mc.displayWidth / n;
      double d2 = this.mc.displayHeight / n;
      this.scaledWidth = MathHelper.ceiling_double_int(d);
      this.scaledHeight = MathHelper.ceiling_double_int(d2);
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0, d, d2, 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
      if (this.minimapSettings.mapCorner != 0 && this.minimapSettings.mapCorner != 3) {
         this.minimapCenterX = this.scaledWidth - 37;
      } else {
         this.minimapCenterX = 37;
      }

      if (this.minimapSettings.mapCorner != 0 && this.minimapSettings.mapCorner != 1) {
         this.minimapCenterY = this.scaledHeight - 37;
      } else {
         this.minimapCenterY = 37;
      }

      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 0);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (!this.minimapSettings.hide && (ZansMinimapMod.modForceState == ModForceState.NEUTRAL || ZansMinimapMod.modForceState == ModForceState.ALLOWED)) {
         GlStateManager.enableDepth();
         if (this.fullscreenMap) {
            this.drawFullscreenMap(this.scaledWidth, this.scaledHeight);
         } else {
            this.drawMapTexture(this.minimapCenterX, this.minimapCenterY, n);
         }

         GlStateManager.disableDepth();
         if (this.radar != null && this.minimapSettings.radarAllowed && !this.fullscreenMap) {
            this.radarContext.set(n, this.minimapCenterX, this.minimapCenterY, this.zoom);
            this.radar.onTickInGame(mc, this.radarContext);
         }

         if (!this.fullscreenMap) {
            this.drawDirections(this.minimapCenterX, this.minimapCenterY);
         }

         if ((this.minimapSettings.squareMap || this.fullscreenMap) && !this.minimapSettings.hide) {
            if (this.fullscreenMap) {
               this.drawArrow(this.scaledWidth / 2, this.scaledHeight / 2);
            } else {
               this.drawArrow(this.minimapCenterX, this.minimapCenterY);
            }
         }

         if (this.developer) {
            GlUtils.bindTextureLocation(new ResourceLocation("voxelmap/lang/i18n.txt"));
            GlUtils.startDrawing();
            GlUtils.drawQuad(this.minimapCenterX, this.minimapCenterY);
            GlUtils.finishDrawing();
         }
      }

      if (this.minimapSettings.showCoordinates) {
         this.drawCoordinates(this.minimapCenterX, this.minimapCenterY);
      }

      if (this.welcomeState > 0) {
      }

      GlStateManager.depthMask(true);
      GlStateManager.enableDepth();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.matrixMode(5889);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
   }
}
