package net.minecraft.client.renderer.texture;

import com.cheatbreaker.client.util.SneakyThrow;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.optifine.BetterGrass;
import net.optifine.Config;
import net.optifine.ConnectedTextures;
import net.optifine.CustomItems;
import net.optifine.EmissiveTextures;
import net.optifine.SmartAnimations;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.ShadersTex;
import net.optifine.util.CounterInt;
import net.optifine.util.TextureUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureMap extends AbstractTexture implements ITickableTextureObject {
   public static boolean ENABLE_SKIP = Boolean.parseBoolean(System.getProperty("fml.skipFirstTextureLoad", "true"));
   public static Logger logger = LogManager.getLogger();
   public static ResourceLocation LOCATION_MISSING_TEXTURE = new ResourceLocation("missingno");
   public static ResourceLocation locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png");
   public boolean skipFirst = false;
   public TextureAtlasSprite[] iconGrid = null;
   public int iconGridSize = -1;
   public int iconGridCountX = -1;
   public int iconGridCountY = -1;
   public int mipmapLevels;
   public double iconGridSizeU = -1.0;
   public double iconGridSizeV = -1.0;
   public CounterInt counterIndexInMap = new CounterInt(0);
   public int atlasWidth = 0;
   public int atlasHeight = 0;
   public List<TextureAtlasSprite> listAnimatedSprites = Lists.newArrayList();
   public Map<String, TextureAtlasSprite> mapRegisteredSprites = Maps.newHashMap();
   public Map<String, TextureAtlasSprite> mapUploadedSprites = Maps.newHashMap();
   public TextureAtlasSprite missingImage = new TextureAtlasSprite("missingno");
   public String basePath;
   public IIconCreator iconCreator;
   public int countAnimationsActive;
   public int frameCountAnimations;

   public TextureMap(String str) {
      this(str, (IIconCreator)null);
   }

   public TextureMap(String str, boolean flag) {
      this(str, (IIconCreator)null, flag);
   }

   public TextureMap(String str, IIconCreator iconCreatorIn) {
      this(str, iconCreatorIn, false);
   }

   public TextureMap(String str, IIconCreator iconCreator, boolean flag) {
      this.basePath = str;
      this.iconCreator = iconCreator;
      this.skipFirst = flag && ENABLE_SKIP;
   }

   public void initMissingImage() {
      int minSpriteSize = this.getMinSpriteSize();
      int[] missingImageData = this.getMissingImageData(minSpriteSize);
      this.missingImage.setIconWidth(minSpriteSize);
      this.missingImage.setIconHeight(minSpriteSize);
      int[][] intses = new int[this.mipmapLevels + 1][];
      intses[0] = missingImageData;
      this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][][]{intses}));
      this.missingImage.setIndexInMap(this.counterIndexInMap.nextValue());
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      if (this.iconCreator != null) {
         this.loadSprites(resourceManager, this.iconCreator);
      }
   }

   public void loadSprites(IResourceManager resourceManager, IIconCreator iconCreator) {
      this.mapRegisteredSprites.clear();
      this.counterIndexInMap.reset();
      iconCreator.registerSprites(this);
      if (this.mipmapLevels >= 4) {
         this.mipmapLevels = this.detectMaxMipmapLevel(this.mapRegisteredSprites, resourceManager);
         Config.log("Mipmap levels: " + this.mipmapLevels);
      }

      this.initMissingImage();
      this.deleteGlTexture();
      this.loadTextureAtlas(resourceManager);
   }

   public void loadTextureAtlas(IResourceManager resourceManager) {
      Config.dbg("Multitexture: " + Config.isMultiTexture());
      if (Config.isMultiTexture()) {
         for (TextureAtlasSprite textureAtlasSprite : this.mapUploadedSprites.values()) {
            textureAtlasSprite.deleteSpriteTexture();
         }
      }

      ConnectedTextures.updateIcons(this);
      CustomItems.updateIcons(this);
      BetterGrass.updateIcons(this);
      int gLMaximumTextureSize = TextureUtils.getGLMaximumTextureSize();
      Stitcher stitcher = new Stitcher(gLMaximumTextureSize, gLMaximumTextureSize, true, 0, this.mipmapLevels);
      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
      int n = Integer.MAX_VALUE;
      Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPre, this);
      int minSpriteSize = this.getMinSpriteSize();
      this.iconGridSize = minSpriteSize;
      int n2 = 1 << this.mipmapLevels;
      int n3 = 0;
      int n4 = 0;

      for (Entry entry : this.mapRegisteredSprites.entrySet()) {
         if (!this.skipFirst) {
            TextureAtlasSprite value = (TextureAtlasSprite)entry.getValue();
            ResourceLocation resourceLocation = new ResourceLocation(value.getIconName());
            ResourceLocation completeResourceLocation = this.completeResourceLocation(resourceLocation, 0);
            value.updateIndexInMap(this.counterIndexInMap);
            if (value.hasCustomLoader(resourceManager, resourceLocation)) {
               if (!value.load(resourceManager, resourceLocation)) {
                  n = Math.min(n, Math.min(value.getIconWidth(), value.getIconHeight()));
                  stitcher.addSprite(value);
                  Config.detail("Custom loader (skipped): " + value);
                  n4++;
               }

               Config.detail("Custom loader: " + value);
               n3++;
            } else {
               try {
                  IResource resource = resourceManager.getResource(completeResourceLocation);
                  BufferedImage[] bufferedImages = new BufferedImage[1 + this.mipmapLevels];
                  bufferedImages[0] = TextureUtil.readBufferedImage(resource.getInputStream());
                  int width = bufferedImages[0].getWidth();
                  int height = bufferedImages[0].getHeight();
                  if (width < 1 || height < 1) {
                     Config.warn("Invalid sprite size: " + value);
                     continue;
                  }

                  if (width < minSpriteSize || this.mipmapLevels > 0) {
                     int n5 = this.mipmapLevels > 0 ? TextureUtils.scaleToGrid(width, minSpriteSize) : TextureUtils.scaleToMin(width, minSpriteSize);
                     if (n5 != width) {
                        if (!TextureUtils.isPowerOfTwo(width)) {
                           Config.log("Scaled non power of 2: " + value.getIconName() + ", " + width + " -> " + n5);
                        } else {
                           Config.log("Scaled too small texture: " + value.getIconName() + ", " + width + " -> " + n5);
                        }

                        int n6 = height * n5 / width;
                        bufferedImages[0] = TextureUtils.scaleImage(bufferedImages[0], n5);
                     }
                  }

                  TextureMetadataSection metadata = resource.getMetadata("texture");
                  if (metadata != null) {
                     List listMipmaps = metadata.getListMipmaps();
                     if (!listMipmaps.isEmpty()) {
                        int width2 = bufferedImages[0].getWidth();
                        int height2 = bufferedImages[0].getHeight();
                        if (MathHelper.roundUpToPowerOfTwo(width2) != width2 || MathHelper.roundUpToPowerOfTwo(height2) != height2) {
                           throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                        }
                     }

                     for (int n5 : (Iterable<Integer>)listMipmaps) {
                        if (n5 > 0 && n5 < bufferedImages.length - 1 && bufferedImages[n5] == null) {
                           ResourceLocation completeResourceLocation2 = this.completeResourceLocation(resourceLocation, n5);

                           try {
                              bufferedImages[n5] = TextureUtil.readBufferedImage(resourceManager.getResource(completeResourceLocation2).getInputStream());
                           } catch (IOException e) {
                              logger.error("Unable to load miplevel {} from: {}", new Object[]{n5, completeResourceLocation2, e});
                           }
                        }
                     }
                  }

                  AnimationMetadataSection metadata2 = resource.getMetadata("animation");
                  value.loadSprite(bufferedImages, metadata2);
               } catch (RuntimeException e) {
                  logger.error("Unable to parse metadata from " + completeResourceLocation, e);
                  ReflectorForge.FMLClientHandler_trackBrokenTexture(completeResourceLocation, e.getMessage());
                  continue;
               } catch (IOException e) {
                  logger.error("Using missing texture, unable to load " + completeResourceLocation + ", " + e.getClass().getName());
                  ReflectorForge.FMLClientHandler_trackMissingTexture(completeResourceLocation);
                  continue;
               }

               n = Math.min(n, Math.min(value.getIconWidth(), value.getIconHeight()));
               int min = Math.min(Integer.lowestOneBit(value.getIconWidth()), Integer.lowestOneBit(value.getIconHeight()));
               if (min < n2) {
                  logger.warn(
                     "Texture {} with size {}x{} limits mip level from {} to {}",
                     new Object[]{
                        completeResourceLocation, value.getIconWidth(), value.getIconHeight(), MathHelper.calculateLogBaseTwo(n2), MathHelper.calculateLogBaseTwo(min)
                     }
                  );
                  n2 = min;
               }

               stitcher.addSprite(value);
            }
            continue;
         }
         break;
      }

      if (n3 > 0) {
         Config.dbg("Custom loader sprites: " + n3);
      }

      if (n4 > 0) {
         Config.dbg("Custom loader sprites (skipped): " + n4);
      }

      int min = Math.min(n, n2);
      int calculateLogBaseTwo = MathHelper.calculateLogBaseTwo(min);
      if (calculateLogBaseTwo < 0) {
         calculateLogBaseTwo = 0;
      }

      if (calculateLogBaseTwo < this.mipmapLevels) {
         logger.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[]{this.basePath, this.mipmapLevels, calculateLogBaseTwo, min});
         this.mipmapLevels = calculateLogBaseTwo;
      }

      for (final TextureAtlasSprite textureAtlasSprite : this.mapRegisteredSprites.values()) {
         if (this.skipFirst) {
            break;
         }

         try {
            textureAtlasSprite.generateMipmaps(this.mipmapLevels);
         } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Applying mipmap");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Sprite being mipmapped");
            makeCategory.addCrashSectionCallable("Sprite name", new Callable<String>() {
               public String call() throws Exception {
                  return textureAtlasSprite.getIconName();
               }
            });
            makeCategory.addCrashSectionCallable("Sprite size", new Callable<String>() {
               public String call() throws Exception {
                  return textureAtlasSprite.getIconWidth() + " x " + textureAtlasSprite.getIconHeight();
               }
            });
            makeCategory.addCrashSectionCallable("Sprite frames", new Callable<String>() {
               public String call() throws Exception {
                  return textureAtlasSprite.getFrameCount() + " frames";
               }
            });
            makeCategory.addCrashSection("Mipmap levels", this.mipmapLevels);
            throw new ReportedException(makeCrashReport);
         }
      }

      this.missingImage.generateMipmaps(this.mipmapLevels);
      stitcher.addSprite(this.missingImage);
      this.skipFirst = false;

      try {
         stitcher.doStitch();
      } catch (StitcherException e) {
         throw SneakyThrow.sneakyThrow(e);
      }

      logger.info("Created: {}x{} {}-atlas", new Object[]{stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), this.basePath});
      if (Config.isShaders()) {
         ShadersTex.allocateTextureMap(this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), stitcher, this);
      } else {
         TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight());
      }

      HashMap newHashMap = Maps.newHashMap(this.mapRegisteredSprites);

      for (TextureAtlasSprite textureAtlasSprite : stitcher.getStichSlots()) {
         String iconName = textureAtlasSprite.getIconName();
         newHashMap.remove(iconName);
         this.mapUploadedSprites.put(iconName, textureAtlasSprite);

         try {
            if (Config.isShaders()) {
               ShadersTex.uploadTexSubForLoadAtlas(
                  this,
                  textureAtlasSprite.getIconName(),
                  textureAtlasSprite.getFrameTextureData(0),
                  textureAtlasSprite.getIconWidth(),
                  textureAtlasSprite.getIconHeight(),
                  textureAtlasSprite.getOriginX(),
                  textureAtlasSprite.getOriginY(),
                  false,
                  false
               );
            } else {
               TextureUtil.uploadTextureMipmap(
                  textureAtlasSprite.getFrameTextureData(0), textureAtlasSprite.getIconWidth(), textureAtlasSprite.getIconHeight(), textureAtlasSprite.getOriginX(), textureAtlasSprite.getOriginY(), false, false
               );
            }
         } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Stitching texture atlas");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Texture being stitched together");
            makeCategory.addCrashSection("Atlas path", this.basePath);
            makeCategory.addCrashSection("Sprite", textureAtlasSprite);
            throw new ReportedException(makeCrashReport);
         }

         if (textureAtlasSprite.hasAnimationMetadata()) {
            textureAtlasSprite.setAnimationIndex(this.listAnimatedSprites.size());
            this.listAnimatedSprites.add(textureAtlasSprite);
         }
      }

      for (TextureAtlasSprite textureAtlasSprite : (Iterable<TextureAtlasSprite>)newHashMap.values()) {
         textureAtlasSprite.copyFrom(this.missingImage);
      }

      Config.log("Animated sprites: " + this.listAnimatedSprites.size());
      if (Config.isMultiTexture()) {
         int currentWidth = stitcher.getCurrentWidth();
         int currentHeight = stitcher.getCurrentHeight();

         for (TextureAtlasSprite textureAtlasSprite : stitcher.getStichSlots()) {
            textureAtlasSprite.sheetWidth = currentWidth;
            textureAtlasSprite.sheetHeight = currentHeight;
            textureAtlasSprite.mipmapLevels = this.mipmapLevels;
            TextureAtlasSprite spriteSingle = textureAtlasSprite.spriteSingle;
            if (spriteSingle != null) {
               if (spriteSingle.getIconWidth() <= 0) {
                  spriteSingle.setIconWidth(textureAtlasSprite.getIconWidth());
                  spriteSingle.setIconHeight(textureAtlasSprite.getIconHeight());
                  spriteSingle.initSprite(textureAtlasSprite.getIconWidth(), textureAtlasSprite.getIconHeight(), 0, 0, false);
                  spriteSingle.clearFramesTextureData();
                  List framesTextureData = textureAtlasSprite.getFramesTextureData();
                  spriteSingle.setFramesTextureData(framesTextureData);
                  spriteSingle.setAnimationMetadata(textureAtlasSprite.getAnimationMetadata());
               }

               spriteSingle.sheetWidth = currentWidth;
               spriteSingle.sheetHeight = currentHeight;
               spriteSingle.mipmapLevels = this.mipmapLevels;
               spriteSingle.setAnimationIndex(textureAtlasSprite.getAnimationIndex());
               textureAtlasSprite.bindSpriteTexture();
               boolean flag = false;
               boolean flag2 = true;

               try {
                  TextureUtil.uploadTextureMipmap(
                     spriteSingle.getFrameTextureData(0), spriteSingle.getIconWidth(), spriteSingle.getIconHeight(), spriteSingle.getOriginX(), spriteSingle.getOriginY(), flag, flag2
                  );
               } catch (Exception e) {
                  Config.dbg("Error uploading sprite single: " + spriteSingle + ", parent: " + textureAtlasSprite);
                  e.printStackTrace();
               }
            }
         }

         Config.getMinecraft().getTextureManager().bindTexture(locationBlocksTexture);
      }

      Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPost, this);
      this.updateIconGrid(stitcher.getCurrentWidth(), stitcher.getCurrentHeight());
      if (Config.equals(System.getProperty("saveTextureMap"), "true")) {
         Config.dbg("Exporting texture map: " + this.basePath);
         TextureUtils.saveGlTexture(
            "debug/" + this.basePath.replaceAll("/", "_"), this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight()
         );
      }
   }

   public ResourceLocation completeResourceLocation(ResourceLocation resourceLocation) {
      return this.completeResourceLocation(resourceLocation, 0);
   }

   public ResourceLocation completeResourceLocation(ResourceLocation location, int n) {
      return this.isAbsoluteLocation(location)
         ? new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".png")
         : (
            n == 0
               ? new ResourceLocation(location.getResourceDomain(), String.format("%s/%s%s", this.basePath, location.getResourcePath(), ".png"))
               : new ResourceLocation(
                  location.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", this.basePath, location.getResourcePath(), n, ".png")
               )
         );
   }

   public TextureAtlasSprite getAtlasSprite(String iconName) {
      TextureAtlasSprite textureAtlasSprite = this.mapUploadedSprites.get(iconName);
      if (textureAtlasSprite == null) {
         textureAtlasSprite = this.missingImage;
      }

      return textureAtlasSprite;
   }

   public void updateAnimations() {
      boolean flag = false;
      boolean flag2 = false;
      TextureUtil.bindTexture(this.getGlTextureId());
      int n = 0;

      for (TextureAtlasSprite textureAtlasSprite : this.listAnimatedSprites) {
         if (this.isTerrainAnimationActive(textureAtlasSprite)) {
            textureAtlasSprite.updateAnimation();
            if (textureAtlasSprite.isAnimationActive()) {
               n++;
            }

            if (textureAtlasSprite.spriteNormal != null) {
               flag = true;
            }

            if (textureAtlasSprite.spriteSpecular != null) {
               flag2 = true;
            }
         }
      }

      if (Config.isMultiTexture()) {
         for (TextureAtlasSprite textureAtlasSprite : this.listAnimatedSprites) {
            if (this.isTerrainAnimationActive(textureAtlasSprite)) {
               TextureAtlasSprite spriteSingle = textureAtlasSprite.spriteSingle;
               if (spriteSingle != null) {
                  if (textureAtlasSprite == TextureUtils.iconClock || textureAtlasSprite == TextureUtils.iconCompass) {
                     spriteSingle.frameCounter = textureAtlasSprite.frameCounter;
                  }

                  textureAtlasSprite.bindSpriteTexture();
                  spriteSingle.updateAnimation();
                  if (spriteSingle.isAnimationActive()) {
                     n++;
                  }
               }
            }
         }

         TextureUtil.bindTexture(this.getGlTextureId());
      }

      if (Config.isShaders()) {
         if (flag) {
            TextureUtil.bindTexture(this.getMultiTexID().norm);

            for (TextureAtlasSprite textureAtlasSprite : this.listAnimatedSprites) {
               if (textureAtlasSprite.spriteNormal != null && this.isTerrainAnimationActive(textureAtlasSprite)) {
                  if (textureAtlasSprite == TextureUtils.iconClock || textureAtlasSprite == TextureUtils.iconCompass) {
                     textureAtlasSprite.spriteNormal.frameCounter = textureAtlasSprite.frameCounter;
                  }

                  textureAtlasSprite.spriteNormal.updateAnimation();
                  if (textureAtlasSprite.spriteNormal.isAnimationActive()) {
                     n++;
                  }
               }
            }
         }

         if (flag2) {
            TextureUtil.bindTexture(this.getMultiTexID().spec);

            for (TextureAtlasSprite textureAtlasSprite : this.listAnimatedSprites) {
               if (textureAtlasSprite.spriteSpecular != null && this.isTerrainAnimationActive(textureAtlasSprite)) {
                  if (textureAtlasSprite == TextureUtils.iconClock || textureAtlasSprite == TextureUtils.iconCompass) {
                     textureAtlasSprite.spriteNormal.frameCounter = textureAtlasSprite.frameCounter;
                  }

                  textureAtlasSprite.spriteSpecular.updateAnimation();
                  if (textureAtlasSprite.spriteSpecular.isAnimationActive()) {
                     n++;
                  }
               }
            }
         }

         if (flag || flag2) {
            TextureUtil.bindTexture(this.getGlTextureId());
         }
      }

      int frameCount = Config.getMinecraft().entityRenderer.frameCount;
      if (frameCount != this.frameCountAnimations) {
         this.countAnimationsActive = n;
         this.frameCountAnimations = frameCount;
      }

      if (SmartAnimations.isActive()) {
         SmartAnimations.resetSpritesRendered();
      }
   }

   public TextureAtlasSprite registerSprite(ResourceLocation location) {
      if (location == null) {
         throw new IllegalArgumentException("Location cannot be null!");
      }

      TextureAtlasSprite textureAtlasSprite = this.mapRegisteredSprites.get(location.toString());
      if (textureAtlasSprite == null) {
         textureAtlasSprite = TextureAtlasSprite.makeAtlasSprite(location);
         this.mapRegisteredSprites.put(location.toString(), textureAtlasSprite);
         textureAtlasSprite.updateIndexInMap(this.counterIndexInMap);
         if (Config.isEmissiveTextures()) {
            this.checkEmissive(location, textureAtlasSprite);
         }
      }

      return textureAtlasSprite;
   }

   @Override
   public void tick() {
      this.updateAnimations();
   }

   public void setMipmapLevels(int mipmapLevelsIn) {
      this.mipmapLevels = mipmapLevelsIn;
   }

   public TextureAtlasSprite getMissingSprite() {
      return this.missingImage;
   }

   public TextureAtlasSprite getTextureExtry(String str) {
      return this.mapRegisteredSprites.get(str);
   }

   public boolean setTextureEntry(String str, TextureAtlasSprite textureAtlasSprite) {
      if (!this.mapRegisteredSprites.containsKey(str)) {
         this.mapRegisteredSprites.put(str, textureAtlasSprite);
         textureAtlasSprite.updateIndexInMap(this.counterIndexInMap);
         return true;
      } else {
         return false;
      }
   }

   public boolean setTextureEntry(TextureAtlasSprite textureAtlasSprite) {
      return this.setTextureEntry(textureAtlasSprite.getIconName(), textureAtlasSprite);
   }

   public String getBasePath() {
      return this.basePath;
   }

   public int getMipmapLevels() {
      return this.mipmapLevels;
   }

   public boolean isAbsoluteLocation(ResourceLocation resourceLocation) {
      String resourcePath = resourceLocation.getResourcePath();
      return this.isAbsoluteLocationPath(resourcePath);
   }

   public boolean isAbsoluteLocationPath(String str) {
      String toLowerCase = str.toLowerCase();
      return toLowerCase.startsWith("mcpatcher/") || toLowerCase.startsWith("optifine/");
   }

   public TextureAtlasSprite getSpriteSafe(String str) {
      ResourceLocation resourceLocation = new ResourceLocation(str);
      return this.mapRegisteredSprites.get(resourceLocation.toString());
   }

   public TextureAtlasSprite getRegisteredSprite(ResourceLocation resourceLocation) {
      return this.mapRegisteredSprites.get(resourceLocation.toString());
   }

   public boolean isTerrainAnimationActive(TextureAtlasSprite textureAtlasSprite) {
      return textureAtlasSprite == TextureUtils.iconWaterStill || textureAtlasSprite == TextureUtils.iconWaterFlow
         ? Config.isAnimatedTerrain()
         : (
            textureAtlasSprite == TextureUtils.iconLavaStill || textureAtlasSprite == TextureUtils.iconLavaFlow
               ? Config.check9()
               : (
                  textureAtlasSprite == TextureUtils.iconFireLayer0 || textureAtlasSprite == TextureUtils.iconFireLayer1
                     ? Config.check3()
                     : (
                        textureAtlasSprite == TextureUtils.iconPortal
                           ? Config.isAnimatedPortal()
                           : (textureAtlasSprite != TextureUtils.iconClock && textureAtlasSprite != TextureUtils.iconCompass ? Config.check6() : true)
                     )
               )
         );
   }

   public int getCountRegisteredSprites() {
      return this.counterIndexInMap.getValue();
   }

   public int detectMaxMipmapLevel(Map map, IResourceManager resourceManager) {
      int detectMinimumSpriteSize = this.detectMinimumSpriteSize(map, resourceManager, 20);
      if (detectMinimumSpriteSize < 16) {
         detectMinimumSpriteSize = 16;
      }

      detectMinimumSpriteSize = MathHelper.roundUpToPowerOfTwo(detectMinimumSpriteSize);
      if (detectMinimumSpriteSize > 16) {
         Config.log("Sprite size: " + detectMinimumSpriteSize);
      }

      int calculateLogBaseTwo = MathHelper.calculateLogBaseTwo(detectMinimumSpriteSize);
      if (calculateLogBaseTwo < 4) {
         calculateLogBaseTwo = 4;
      }

      return calculateLogBaseTwo;
   }

   public int detectMinimumSpriteSize(Map map, IResourceManager resourceManager, int n) {
      HashMap map2 = new HashMap();

      for (Object obj : map.entrySet()) {
         Entry entry = (Entry)obj;
         TextureAtlasSprite value = (TextureAtlasSprite)entry.getValue();
         ResourceLocation resourceLocation = new ResourceLocation(value.getIconName());
         ResourceLocation completeResourceLocation = this.completeResourceLocation(resourceLocation);
         if (!value.hasCustomLoader(resourceManager, resourceLocation)) {
            try {
               IResource resource = resourceManager.getResource(completeResourceLocation);
               if (resource != null) {
                  InputStream inputStream = resource.getInputStream();
                  if (inputStream != null) {
                     Dimension imageSize = TextureUtils.getImageSize(inputStream, "png");
                     inputStream.close();
                     if (imageSize != null) {
                        int width = imageSize.width;
                        int roundUpToPowerOfTwo = MathHelper.roundUpToPowerOfTwo(width);
                        if (!map2.containsKey(roundUpToPowerOfTwo)) {
                           map2.put(roundUpToPowerOfTwo, 1);
                        } else {
                           int n2 = (Integer)map2.get(roundUpToPowerOfTwo);
                           map2.put(roundUpToPowerOfTwo, n2 + 1);
                        }
                     }
                  }
               }
            } catch (Exception e) {
            }
         }
      }

      int n2 = 0;
      Set keySet = map2.keySet();
      TreeSet treeSet = new TreeSet(keySet);

      for (int n3 : (Iterable<Integer>)treeSet) {
         int n4 = (Integer)map2.get(n3);
         n2 += n4;
      }

      int n3 = 16;
      int n4 = 0;
      int n5 = n2 * n / 100;

      for (int n6 : (Iterable<Integer>)treeSet) {
         int n7 = (Integer)map2.get(n6);
         n4 += n7;
         if (n6 > n3) {
            n3 = n6;
         }

         if (n4 > n5) {
            return n3;
         }
      }

      return n3;
   }

   public int getMinSpriteSize() {
      int n = 1 << this.mipmapLevels;
      if (n < 8) {
         n = 8;
      }

      return n;
   }

   public int[] getMissingImageData(int n) {
      BufferedImage bufferedImage = new BufferedImage(16, 16, 2);
      bufferedImage.setRGB(0, 0, 16, 16, TextureUtil.missingTextureData, 0, 16);
      BufferedImage scaleImage = TextureUtils.scaleImage(bufferedImage, n);
      int[] ints = new int[n * n];
      scaleImage.getRGB(0, 0, n, n, ints, 0, n);
      return ints;
   }

   public boolean isTextureBound() {
      int boundTexture = GlStateManager.getBoundTexture();
      int glTextureId = this.getGlTextureId();
      return boundTexture == glTextureId;
   }

   public void updateIconGrid(int n, int n2) {
      this.iconGridCountX = -1;
      this.iconGridCountY = -1;
      this.iconGrid = null;
      if (this.iconGridSize > 0) {
         this.iconGridCountX = n / this.iconGridSize;
         this.iconGridCountY = n2 / this.iconGridSize;
         this.iconGrid = new TextureAtlasSprite[this.iconGridCountX * this.iconGridCountY];
         this.iconGridSizeU = 1.0 / this.iconGridCountX;
         this.iconGridSizeV = 1.0 / this.iconGridCountY;

         for (TextureAtlasSprite textureAtlasSprite : this.mapUploadedSprites.values()) {
            double d = 0.5 / n;
            double d2 = 0.5 / n2;
            double d3 = Math.min(textureAtlasSprite.getMinU(), textureAtlasSprite.getMaxU()) + d;
            double d4 = Math.min(textureAtlasSprite.getMinV(), textureAtlasSprite.getMaxV()) + d2;
            double d5 = Math.max(textureAtlasSprite.getMinU(), textureAtlasSprite.getMaxU()) - d;
            double d6 = Math.max(textureAtlasSprite.getMinV(), textureAtlasSprite.getMaxV()) - d2;
            int n3 = (int)(d3 / this.iconGridSizeU);
            int n4 = (int)(d4 / this.iconGridSizeV);
            int n5 = (int)(d5 / this.iconGridSizeU);
            int n6 = (int)(d6 / this.iconGridSizeV);

            for (int i = n3; i <= n5; i++) {
               if (i >= 0 && i < this.iconGridCountX) {
                  for (int j = n4; j <= n6; j++) {
                     if (j >= 0 && j < this.iconGridCountX) {
                        int n7 = j * this.iconGridCountX + i;
                        this.iconGrid[n7] = textureAtlasSprite;
                     } else {
                        Config.warn("Invalid grid V: " + j + ", icon: " + textureAtlasSprite.getIconName());
                     }
                  }
               } else {
                  Config.warn("Invalid grid U: " + i + ", icon: " + textureAtlasSprite.getIconName());
               }
            }
         }
      }
   }

   public TextureAtlasSprite getIconByUV(double d, double d2) {
      if (this.iconGrid == null) {
         return null;
      }

      int n = (int)(d / this.iconGridSizeU);
      int n2 = (int)(d2 / this.iconGridSizeV);
      int n3 = n2 * this.iconGridCountX + n;
      return n3 >= 0 && n3 <= this.iconGrid.length ? this.iconGrid[n3] : null;
   }

   public void checkEmissive(ResourceLocation resourceLocation, TextureAtlasSprite textureAtlasSprite) {
      String suffixEmissive = EmissiveTextures.getSuffixEmissive();
      if (suffixEmissive != null && !resourceLocation.getResourcePath().endsWith(suffixEmissive)) {
         ResourceLocation resourceLocation2 = new ResourceLocation(resourceLocation.getResourceDomain(), resourceLocation.getResourcePath() + suffixEmissive);
         ResourceLocation completeResourceLocation = this.completeResourceLocation(resourceLocation2);
         if (Config.hasResource(completeResourceLocation)) {
            TextureAtlasSprite registerSprite = this.registerSprite(resourceLocation2);
            registerSprite.isEmissive = true;
            textureAtlasSprite.spriteEmissive = registerSprite;
         }
      }
   }

   public int getCountAnimations() {
      return this.listAnimatedSprites.size();
   }

   public int getCountAnimationsActive() {
      return this.countAnimationsActive;
   }
}
