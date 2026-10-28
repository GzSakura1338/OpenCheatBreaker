package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.RandomMobs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import shadersmod.client.ShadersTex;

public class TextureManager implements IResourceManagerReloadListener, ITickable {
    public static Logger logger = LogManager.getLogger();
    public Map mapTextureObjects = Maps.newHashMap();
    public Map mapResourceLocations = Maps.newHashMap();
    public List listTickables = Lists.newArrayList();
    public Map mapTextureCounters = Maps.newHashMap();
    public IResourceManager theResourceManager;

    public TextureManager(IResourceManager resourceManager) {
        this.theResourceManager = resourceManager;
    }

    public void bindTexture(ResourceLocation resource) {
        if (Config.isRandomMobs()) {
            resource = RandomMobs.getTextureLocation(resource);
        }

        ITextureObject textureObject = (ITextureObject)this.mapTextureObjects.get(resource);
        if (textureObject == null) {
            textureObject = new SimpleTexture(resource);
            this.loadTexture(resource, textureObject);
        }

        if (Config.isShaders()) {
            ShadersTex.bindTexture(textureObject);
        } else {
            TextureUtil.bindTexture(textureObject.getGlTextureId());
        }
    }

    public ResourceLocation getResourceLocation(int n) {
        return (ResourceLocation)this.mapResourceLocations.get(n);
    }

    public boolean loadTextureMap(ResourceLocation resourceLocation, TextureMap textureMap) {
        if (this.loadTickableTexture(resourceLocation, textureMap)) {
            this.mapResourceLocations.put(textureMap.getTextureType(), resourceLocation);
            return true;
        } else {
            return false;
        }
    }

    public boolean loadTickableTexture(ResourceLocation resourceLocation, ITickableTextureObject tickableTextureObject) {
        if (this.loadTexture(resourceLocation, tickableTextureObject)) {
            this.listTickables.add(tickableTextureObject);
            return true;
        } else {
            return false;
        }
    }

    public boolean loadTexture(ResourceLocation resourceLocation, ITextureObject textureObject) {
        boolean flag = true;
        ITextureObject textureObject2 = textureObject;

        try {
            textureObject.loadTexture(this.theResourceManager);
        } catch (IOException e) {
            logger.warn("Failed to load texture: " + resourceLocation, e);
            textureObject2 = TextureUtil.missingTexture;
            this.mapTextureObjects.put(resourceLocation, textureObject2);
            flag = false;
        } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Registering texture");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Resource location being registered");
            makeCategory.addCrashSection("Resource location", resourceLocation);
            makeCategory.addCrashSectionCallable("Texture object class", () -> textureObject.getClass().getName());
            throw new ReportedException(makeCrashReport);
        }

        this.mapTextureObjects.put(resourceLocation, textureObject2);
        return flag;
    }

    public ITextureObject getTexture(ResourceLocation resourceLocation) {
        return (ITextureObject)this.mapTextureObjects.get(resourceLocation);
    }

    public ResourceLocation getDynamicTextureLocation(String str, DynamicTexture dynamicTexture) {
        if (str.equals("logo")) {
            dynamicTexture = Config.getMojangLogoTexture(dynamicTexture);
        }

        Integer integer = (Integer)this.mapTextureCounters.get(str);
        if (integer == null) {
            integer = 1;
        } else {
            integer = integer + 1;
        }

        this.mapTextureCounters.put(str, integer);
        ResourceLocation resourceLocation = new ResourceLocation(String.format("dynamic/%s_%d", str, integer));
        this.loadTexture(resourceLocation, dynamicTexture);
        return resourceLocation;
    }

    @Override
    public void tick() {
        for (ITickable tickable : (Iterable<ITickable>)this.listTickables) {
            tickable.tick();
        }
    }

    public void deleteTexture(ResourceLocation resourceLocation) {
        ITextureObject texture = this.getTexture(resourceLocation);
        if (texture != null) {
            this.mapTextureObjects.remove(resourceLocation);
            TextureUtil.deleteTexture(texture.getGlTextureId());
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        Config.dbg("*** Reloading textures ***");
        Config.log("Resource packs: " + Config.getResourcePackNames());
        Iterator iterator = this.mapTextureObjects.keySet().iterator();

        while (iterator.hasNext()) {
            ResourceLocation resourceLocation = (ResourceLocation)iterator.next();
            String resourcePath = resourceLocation.getResourcePath();
            if (resourcePath.startsWith("mcpatcher/") || resourcePath.startsWith("optifine/")) {
                ITextureObject textureObject = (ITextureObject)this.mapTextureObjects.get(resourceLocation);
                int glTextureId = textureObject.getGlTextureId();
                if (glTextureId > 0) {
                    GL11.glDeleteTextures(glTextureId);
                }

                iterator.remove();
            }
        }

        for (Entry entry : (Iterable<Entry>)this.mapTextureObjects.entrySet()) {
            this.loadTexture((ResourceLocation)entry.getKey(), (ITextureObject)entry.getValue());
        }
    }
}
