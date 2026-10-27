package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundHandler implements ITickable, IResourceManagerReloadListener {
   public static Logger logger = LogManager.getLogger();
   public static Gson GSON = new GsonBuilder().registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
   public static ParameterizedType TYPE = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class, SoundList.class};
      }

      @Override
      public Type getRawType() {
         return Map.class;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }
   };
   public static SoundPoolEntry missing_sound = new SoundPoolEntry(new ResourceLocation("meta:missing_sound"), 0.0, 0.0, false);
   public SoundRegistry sndRegistry = new SoundRegistry();
   public IResourceManager mcResourceManager;
   public SoundManager sndManager;

   public SoundHandler(IResourceManager manager, GameSettings gameSettingsIn) {
      this.mcResourceManager = manager;
      this.sndManager = new SoundManager(this, gameSettingsIn);
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.sndManager.reloadSoundSystem();
      this.sndRegistry.clearMap();

      for (String str : resourceManager.getResourceDomains()) {
         try {
            for (IResource resource : resourceManager.getAllResources(new ResourceLocation(str, "sounds.json"))) {
               try {
                  Map soundMap = this.getSoundMap(resource.getInputStream());

                  for (Entry entry : (Iterable<Entry>)soundMap.entrySet()) {
                     this.loadSoundResource(new ResourceLocation(str, (String)entry.getKey()), (SoundList)entry.getValue());
                  }
               } catch (RuntimeException e) {
                  logger.warn("Invalid sounds.json", e);
               }
            }
         } catch (IOException e) {
         }
      }
   }

   public Map<String, SoundList> getSoundMap(InputStream stream) {
      Map map;
      try {
         map = (Map)GSON.fromJson(new InputStreamReader(stream), TYPE);
      } finally {
         IOUtils.closeQuietly(stream);
      }

      return map;
   }

   public void loadSoundResource(ResourceLocation location, SoundList sounds) {
      boolean flag = !this.sndRegistry.containsKey(location);
      SoundEventAccessorComposite soundEventAccessorComposite;
      if (!flag && !sounds.canReplaceExisting()) {
         soundEventAccessorComposite = this.sndRegistry.getObject(location);
      } else {
         if (!flag) {
            logger.debug("Replaced sound event location {}", new Object[]{location});
         }

         soundEventAccessorComposite = new SoundEventAccessorComposite(location, 1.0, 1.0, sounds.getSoundCategory());
         this.sndRegistry.registerSound(soundEventAccessorComposite);
      }

      for (final SoundList.SoundEntry soundEntry : sounds.getSoundList()) {
         String soundEntryName = soundEntry.getSoundEntryName();
         ResourceLocation resourceLocation = new ResourceLocation(soundEntryName);
         final String str = soundEntryName.contains(":") ? resourceLocation.getResourceDomain() : location.getResourceDomain();
         ISoundEventAccessor soundEventAccessor;
         switch (soundEntry.getSoundEntryType()) {
            case FILE:
               ResourceLocation resourceLocation2 = new ResourceLocation(str, "sounds/" + resourceLocation.getResourcePath() + ".ogg");
               InputStream inputStream = null;

               try {
                  inputStream = this.mcResourceManager.getResource(resourceLocation2).getInputStream();
               } catch (FileNotFoundException e) {
                  logger.warn("File {} does not exist, cannot add it to event {}", new Object[]{resourceLocation2, location});
                  continue;
               } catch (IOException e) {
                  logger.warn("Could not load sound file " + resourceLocation2 + ", cannot add it to event " + location, e);
                  continue;
               } finally {
                  IOUtils.closeQuietly(inputStream);
               }

               soundEventAccessor = new SoundEventAccessor(
                  new SoundPoolEntry(resourceLocation2, soundEntry.getSoundEntryPitch(), soundEntry.getSoundEntryVolume(), soundEntry.isStreaming()), soundEntry.getSoundEntryWeight()
               );
               break;
            case SOUND_EVENT:
               soundEventAccessor = new ISoundEventAccessor<SoundPoolEntry>() {
                  public ResourceLocation resourceLocation = new ResourceLocation(str, soundEntry.getSoundEntryName());

                  @Override
                  public int getWeight() {
                     SoundEventAccessorComposite object = SoundHandler.this.sndRegistry.getObject(this.resourceLocation);
                     return object == null ? 0 : object.getWeight();
                  }

                  public SoundPoolEntry cloneEntry() {
                     SoundEventAccessorComposite object = SoundHandler.this.sndRegistry.getObject(this.resourceLocation);
                     return object == null ? SoundHandler.missing_sound : object.cloneEntry();
                  }
               };
               break;
            default:
               throw new IllegalStateException("IN YOU FACE");
         }

         soundEventAccessorComposite.addSoundToEventPool(soundEventAccessor);
      }
   }

   public SoundEventAccessorComposite getSound(ResourceLocation location) {
      return this.sndRegistry.getObject(location);
   }

   public void playSound(ISound sound) {
      this.sndManager.playSound(sound);
   }

   public void playDelayedSound(ISound sound, int delay) {
      this.sndManager.playDelayedSound(sound, delay);
   }

   public void setListener(EntityPlayer player, float f) {
      this.sndManager.setListener(player, f);
   }

   public void pauseSounds() {
      this.sndManager.pauseAllSounds();
   }

   public void stopSounds() {
      this.sndManager.stopAllSounds();
   }

   public void unloadSounds() {
      this.sndManager.unloadSoundSystem();
   }

   @Override
   public void update() {
      this.sndManager.updateAllSounds();
   }

   public void resumeSounds() {
      this.sndManager.resumeAllSounds();
   }

   public void setSoundLevel(SoundCategory category, float volume) {
      if (category == SoundCategory.MASTER && volume <= 0.0F) {
         this.stopSounds();
      }

      this.sndManager.setSoundCategoryVolume(category, volume);
   }

   public void stopSound(ISound sound) {
      this.sndManager.stopSound(sound);
   }

   public SoundEventAccessorComposite getRandomSoundFromCategories(SoundCategory... categories) {
      ArrayList newArrayList = Lists.newArrayList();

      for (ResourceLocation resourceLocation : this.sndRegistry.getKeys()) {
         SoundEventAccessorComposite object = this.sndRegistry.getObject(resourceLocation);
         if (ArrayUtils.contains(categories, object.getSoundCategory())) {
            newArrayList.add(object);
         }
      }

      return newArrayList.isEmpty() ? null : (SoundEventAccessorComposite)newArrayList.get(new Random().nextInt(newArrayList.size()));
   }

   public boolean isSoundPlaying(ISound sound) {
      return this.sndManager.isSoundPlaying(sound);
   }
}
