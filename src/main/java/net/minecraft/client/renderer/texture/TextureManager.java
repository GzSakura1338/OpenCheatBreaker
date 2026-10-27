package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.HashSet;
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
import net.optifine.CustomGuis;
import net.optifine.EmissiveTextures;
import net.optifine.RandomEntities;
import net.optifine.shaders.ShadersTex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureManager implements IResourceManagerReloadListener, ITickable {
   public static Logger logger = LogManager.getLogger();
   public Map<ResourceLocation, ITextureObject> mapTextureObjects = Maps.newHashMap();
   public List<ITickable> listTickables = Lists.newArrayList();
   public Map<String, Integer> mapTextureCounters = Maps.newHashMap();
   public IResourceManager theResourceManager;
   public ITextureObject boundTexture;
   public ResourceLocation boundTextureLocation;

   public TextureManager(IResourceManager resourceManager) {
      this.theResourceManager = resourceManager;
   }

   public void bindTexture(ResourceLocation resource) {
      if (Config.isRandomEntities()) {
         resource = RandomEntities.getTextureLocation(resource);
      }

      if (Config.isCustomGuis()) {
         resource = CustomGuis.getTextureLocation(resource);
      }

      ITextureObject textureObject = this.mapTextureObjects.get(resource);
      if (EmissiveTextures.isActive()) {
         textureObject = EmissiveTextures.getEmissiveTexture(textureObject, this.mapTextureObjects);
      }

      if (textureObject == null) {
         textureObject = new SimpleTexture(resource);
         this.loadTexture(resource, textureObject);
      }

      if (Config.isShaders()) {
         ShadersTex.bindTexture(textureObject);
      } else {
         TextureUtil.bindTexture(textureObject.getGlTextureId());
      }

      this.boundTexture = textureObject;
      this.boundTextureLocation = resource;
   }

   public boolean loadTickableTexture(ResourceLocation textureLocation, ITickableTextureObject textureObj) {
      if (this.loadTexture(textureLocation, textureObj)) {
         this.listTickables.add(textureObj);
         return true;
      } else {
         return false;
      }
   }

   public boolean loadTexture(ResourceLocation textureLocation, ITextureObject textureObj) {
      try {
         textureObj.loadTexture(this.theResourceManager);
      } catch (IOException e) {
         logger.warn("Failed to load texture: " + textureLocation, e);
         ITextureObject missingTexture = TextureUtil.missingTexture;
         this.mapTextureObjects.put(textureLocation, missingTexture);
         return false;
      } catch (Throwable e) {
         ITextureObject textureObj2 = textureObj;
         CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Registering texture");
         CrashReportCategory makeCategory = makeCrashReport.makeCategory("Resource location being registered");
         makeCategory.addCrashSection("Resource location", textureLocation);
         makeCategory.addCrashSectionCallable("Texture object class", () -> textureObj2.getClass().getName());
         throw new ReportedException(makeCrashReport);
      }

      this.mapTextureObjects.put(textureLocation, textureObj);
      return true;
   }

   public ITextureObject getTexture(ResourceLocation textureLocation) {
      return this.mapTextureObjects.get(textureLocation);
   }

   public ResourceLocation getDynamicTextureLocation(String name, DynamicTexture texture) {
      if (name.equals("logo")) {
         texture = Config.getMojangLogoTexture(texture);
      }

      Integer integer = this.mapTextureCounters.get(name);
      if (integer == null) {
         integer = 1;
      } else {
         integer = integer + 1;
      }

      this.mapTextureCounters.put(name, integer);
      ResourceLocation resourceLocation = new ResourceLocation(String.format("dynamic/%s_%d", name, integer));
      this.loadTexture(resourceLocation, texture);
      return resourceLocation;
   }

   @Override
   public void tick() {
      for (ITickable tickable : this.listTickables) {
         tickable.tick();
      }
   }

   public void deleteTexture(ResourceLocation textureLocation) {
      ITextureObject texture = this.getTexture(textureLocation);
      if (texture != null) {
         this.mapTextureObjects.remove(textureLocation);
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
         if (resourcePath.startsWith("mcpatcher/") || resourcePath.startsWith("optifine/") || EmissiveTextures.isEmissive(resourceLocation)) {
            ITextureObject textureObject = this.mapTextureObjects.get(resourceLocation);
            if (textureObject instanceof AbstractTexture) {
               AbstractTexture abstractTexture = (AbstractTexture)textureObject;
               abstractTexture.deleteGlTexture();
            }

            iterator.remove();
         }
      }

      EmissiveTextures.update();

      for (Object obj : new HashSet<>(this.mapTextureObjects.entrySet())) {
         Entry entry = (Entry)obj;
         this.loadTexture((ResourceLocation)entry.getKey(), (ITextureObject)entry.getValue());
      }
   }

   public void reloadBannerTextures() {
      for (Object obj : new HashSet<>(this.mapTextureObjects.entrySet())) {
         Entry entry = (Entry)obj;
         ResourceLocation key = (ResourceLocation)entry.getKey();
         ITextureObject value = (ITextureObject)entry.getValue();
         if (value instanceof LayeredColorMaskTexture) {
            this.loadTexture(key, value);
         }
      }
   }

   public ITextureObject getBoundTexture() {
      return this.boundTexture;
   }

   public ResourceLocation getBoundTextureLocation() {
      return this.boundTextureLocation;
   }
}
