package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class SkinManager {
   public static ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
   public TextureManager textureManager;
   public File skinCacheDir;
   public MinecraftSessionService sessionService;
   public LoadingCache<GameProfile, Map<Type, MinecraftProfileTexture>> skinCacheLoader;

   public SkinManager(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService) {
      this.textureManager = textureManagerInstance;
      this.skinCacheDir = skinCacheDirectory;
      this.sessionService = sessionService;
      this.skinCacheLoader = CacheBuilder.newBuilder()
         .expireAfterAccess(15L, TimeUnit.SECONDS)
         .build(new CacheLoader<GameProfile, Map<Type, MinecraftProfileTexture>>() {
            public Map<Type, MinecraftProfileTexture> load(GameProfile gameProfile) throws Exception {
               return Minecraft.getMinecraft().getSessionService().getTextures(gameProfile, false);
            }
         });
   }

   public ResourceLocation loadSkin(MinecraftProfileTexture profileTexture, Type type) {
      return this.loadSkin(profileTexture, type, (SkinManager.SkinAvailableCallback)null);
   }

   public ResourceLocation loadSkin(
      final MinecraftProfileTexture profileTexture, final Type type, final SkinManager.SkinAvailableCallback skinAvailableCallback
   ) {
      final ResourceLocation resourceLocation = new ResourceLocation("skins/" + profileTexture.getHash());
      ITextureObject texture = this.textureManager.getTexture(resourceLocation);
      if (texture != null) {
         if (skinAvailableCallback != null) {
            skinAvailableCallback.skinAvailable(type, resourceLocation, profileTexture);
         }
      } else {
         File file = new File(this.skinCacheDir, profileTexture.getHash().length() > 2 ? profileTexture.getHash().substring(0, 2) : "xx");
         File file2 = new File(file, profileTexture.getHash());
         final ImageBufferDownload imageBufferDownload = type == Type.SKIN ? new ImageBufferDownload() : null;
         ThreadDownloadImageData threadDownloadImageData = new ThreadDownloadImageData(
            file2, profileTexture.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer() {
               @Override
               public BufferedImage parseUserSkin(BufferedImage image) {
                  if (imageBufferDownload != null) {
                     image = imageBufferDownload.parseUserSkin(image);
                  }

                  return image;
               }

               @Override
               public void skinAvailable() {
                  if (imageBufferDownload != null) {
                     imageBufferDownload.skinAvailable();
                  }

                  if (skinAvailableCallback != null) {
                     skinAvailableCallback.skinAvailable(type, resourceLocation, profileTexture);
                  }
               }
            }
         );
         this.textureManager.loadTexture(resourceLocation, threadDownloadImageData);
      }

      return resourceLocation;
   }

   public void loadProfileTextures(final GameProfile profile, final SkinManager.SkinAvailableCallback skinAvailableCallback, final boolean requireSecure) {
      THREAD_POOL.submit(new Runnable() {
         @Override
         public void run() {
            final HashMap newHashMap = Maps.newHashMap();

            try {
               newHashMap.putAll(SkinManager.this.sessionService.getTextures(profile, requireSecure));
            } catch (InsecureTextureException e) {
            }

            if (newHashMap.isEmpty() && profile.getId().equals(Minecraft.getMinecraft().getSession().getProfile().getId())) {
               profile.getProperties().clear();
               profile.getProperties().putAll(Minecraft.getMinecraft().getProfileProperties());
               newHashMap.putAll(SkinManager.this.sessionService.getTextures(profile, false));
            }

            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
               @Override
               public void run() {
                  if (newHashMap.containsKey(Type.SKIN)) {
                     SkinManager.this.loadSkin((MinecraftProfileTexture)newHashMap.get(Type.SKIN), Type.SKIN, skinAvailableCallback);
                  }

                  if (newHashMap.containsKey(Type.CAPE)) {
                     SkinManager.this.loadSkin((MinecraftProfileTexture)newHashMap.get(Type.CAPE), Type.CAPE, skinAvailableCallback);
                  }
               }
            });
         }
      });
   }

   public Map<Type, MinecraftProfileTexture> loadSkinFromCache(GameProfile profile) {
      return (Map<Type, MinecraftProfileTexture>)this.skinCacheLoader.getUnchecked(profile);
   }

   public interface SkinAvailableCallback {
      void skinAvailable(Type type, ResourceLocation resourceLocation, MinecraftProfileTexture minecraftProfileTexture);
   }
}
