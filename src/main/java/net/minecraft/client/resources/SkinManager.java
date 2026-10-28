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
    public static ResourceLocation DEFAULT_SKIN = new ResourceLocation("textures/entity/steve.png");
    public static ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
    public TextureManager textureManager;
    public File skinCacheDir;
    public MinecraftSessionService sessionService;
    public LoadingCache skinCacheLoader;

    public SkinManager(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService) {
        this.textureManager = textureManagerInstance;
        this.skinCacheDir = skinCacheDirectory;
        this.sessionService = sessionService;
        this.skinCacheLoader = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader() {
            public Map load(GameProfile gameProfile) {
                return Minecraft.getMinecraft().getSessionService().getTextures(gameProfile, false);
            }

            public Object load(Object obj) {
                return this.load((GameProfile)obj);
            }
        });
    }

    public ResourceLocation loadSkin(MinecraftProfileTexture minecraftProfileTexture, Type type) {
        return this.loadSkin(minecraftProfileTexture, type, null);
    }

    public ResourceLocation loadSkin(final MinecraftProfileTexture minecraftProfileTexture, final Type type, final SkinManager.SkinAvailableCallback skinAvailableCallback) {
        final ResourceLocation resourceLocation = new ResourceLocation("skins/" + minecraftProfileTexture.getHash());
        ITextureObject texture = this.textureManager.getTexture(resourceLocation);
        if (texture != null) {
            if (skinAvailableCallback != null) {
                skinAvailableCallback.handleType(type, resourceLocation, minecraftProfileTexture);
            }
        } else {
            File file = new File(this.skinCacheDir, minecraftProfileTexture.getHash().substring(0, 2));
            File file2 = new File(file, minecraftProfileTexture.getHash());
            final ImageBufferDownload imageBufferDownload = type == Type.SKIN ? new ImageBufferDownload() : null;
            ThreadDownloadImageData threadDownloadImageData = new ThreadDownloadImageData(file2, minecraftProfileTexture.getUrl(), DEFAULT_SKIN, new IImageBuffer() {
                @Override
                public BufferedImage parseUserSkin(BufferedImage bufferedImage) {
                    if (imageBufferDownload != null) {
                        bufferedImage = imageBufferDownload.parseUserSkin(bufferedImage);
                    }

                    return bufferedImage;
                }

                @Override
                public void run() {
                    if (imageBufferDownload != null) {
                        imageBufferDownload.run();
                    }

                    if (skinAvailableCallback != null) {
                        skinAvailableCallback.handleType(type, resourceLocation, minecraftProfileTexture);
                    }
                }
            });
            this.textureManager.loadTexture(resourceLocation, threadDownloadImageData);
        }

        return resourceLocation;
    }

    public void handleGameProfile(final GameProfile gameProfile, final SkinManager.SkinAvailableCallback skinAvailableCallback, final boolean flag) {
        THREAD_POOL.submit(new Runnable() {
            @Override
            public void run() {
                final HashMap newHashMap = Maps.newHashMap();

                try {
                    newHashMap.putAll(SkinManager.this.sessionService.getTextures(gameProfile, flag));
                } catch (InsecureTextureException e) {
                }

                if (newHashMap.isEmpty() && gameProfile.getId().equals(Minecraft.getMinecraft().getSession().getProfile().getId())) {
                    newHashMap.putAll(SkinManager.this.sessionService.getTextures(SkinManager.this.sessionService.fillProfileProperties(gameProfile, false), false));
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

    public Map loadSkinFromCache(GameProfile gameProfile) {
        return (Map)this.skinCacheLoader.getUnchecked(gameProfile);
    }

    public interface SkinAvailableCallback {
        void handleType(Type type, ResourceLocation resourceLocation, MinecraftProfileTexture minecraftProfileTexture);
    }
}
