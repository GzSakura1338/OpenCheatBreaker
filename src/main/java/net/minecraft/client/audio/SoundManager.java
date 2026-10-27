package net.minecraft.client.audio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.Source;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryJavaSound;
import com.cheatbreaker.client.audio.JavaSoundMixer;

public class SoundManager {
   public static Marker LOG_MARKER = MarkerManager.getMarker("SOUNDS");
   public static Logger logger = LogManager.getLogger();
   public int playTime = 0;
   public Map<String, ISound> playingSounds = HashBiMap.create();
   public SoundManager.SoundSystemStarterThread sndSystem;
   public boolean loaded;
   public Map<ISound, String> invPlayingSounds = ((BiMap)this.playingSounds).inverse();
   public Map<ISound, SoundPoolEntry> playingSoundPoolEntries = Maps.newHashMap();
   public Multimap<SoundCategory, String> categorySounds = HashMultimap.create();
   public List<ITickableSound> tickableSounds = Lists.newArrayList();
   public Map<ISound, Integer> delayedSounds = Maps.newHashMap();
   public Map<String, Integer> playingSoundsStopTime = Maps.newHashMap();
   public SoundHandler sndHandler;
   public GameSettings options;

   public SoundManager(SoundHandler soundHandler, GameSettings gameSettings) {
      this.sndHandler = soundHandler;
      this.options = gameSettings;

      try {
         SoundSystemConfig.addLibrary(LibraryJavaSound.class);
         JavaSoundMixer.select();
         SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
      } catch (SoundSystemException e) {
         logger.error(LOG_MARKER, "Error linking with the LibraryJavaSound plug-in", e);
      }
   }

   public void reloadSoundSystem() {
      this.unloadSoundSystem();
      this.loadSoundSystem();
   }

   public synchronized void loadSoundSystem() {
      if (!this.loaded) {
         try {
            new Thread(new Runnable() {
               @Override
               public void run() {
                  SoundSystemConfig.setLogger(new SoundSystemLogger() {
                     public void message(String str, int n) {
                        if (!str.isEmpty()) {
                           SoundManager.logger.info(str);
                        }
                     }

                     public void importantMessage(String str, int n) {
                        if (!str.isEmpty()) {
                           SoundManager.logger.warn(str);
                        }
                     }

                     public void errorMessage(String str, String str2, int n) {
                        if (!str2.isEmpty()) {
                           SoundManager.logger.error("Error in class '" + str + "'");
                           SoundManager.logger.error(str2);
                        }
                     }
                  });
                  SoundManager.this.sndSystem = SoundManager.this.new SoundSystemStarterThread();
                  SoundManager.this.loaded = true;
                  SoundManager.this.sndSystem.setMasterVolume(SoundManager.this.options.getSoundLevel(SoundCategory.MASTER));
                  SoundManager.logger.info(SoundManager.LOG_MARKER, "Sound engine started");
               }
            }, "Sound Library Loader").start();
         } catch (RuntimeException e) {
            logger.error(LOG_MARKER, "Error starting SoundSystem. Turning off sounds & music", e);
            this.options.setSoundLevel(SoundCategory.MASTER, 0.0F);
            this.options.saveOptions();
         }
      }
   }

   public float getSoundCategoryVolume(SoundCategory category) {
      return category != null && category != SoundCategory.MASTER ? this.options.getSoundLevel(category) : 1.0F;
   }

   public void setSoundCategoryVolume(SoundCategory category, float volume) {
      if (this.loaded) {
         if (category == SoundCategory.MASTER) {
            this.sndSystem.setMasterVolume(volume);
         } else {
            for (String str : this.categorySounds.get(category)) {
               ISound sound = this.playingSounds.get(str);
               float normalizedVolume = this.getNormalizedVolume(sound, this.playingSoundPoolEntries.get(sound), category);
               if (normalizedVolume <= 0.0F) {
                  this.stopSound(sound);
               } else {
                  this.sndSystem.setVolume(str, normalizedVolume);
               }
            }
         }
      }
   }

   public void unloadSoundSystem() {
      if (this.loaded) {
         this.stopAllSounds();
         this.sndSystem.cleanup();
         this.loaded = false;
      }
   }

   public void stopAllSounds() {
      if (this.loaded) {
         for (String str : this.playingSounds.keySet()) {
            this.sndSystem.stop(str);
         }

         this.playingSounds.clear();
         this.delayedSounds.clear();
         this.tickableSounds.clear();
         this.categorySounds.clear();
         this.playingSoundPoolEntries.clear();
         this.playingSoundsStopTime.clear();
      }
   }

   public void updateAllSounds() {
      this.playTime++;

      for (ITickableSound tickableSound : this.tickableSounds) {
         tickableSound.update();
         if (tickableSound.isDonePlaying()) {
            this.stopSound(tickableSound);
         } else {
            String str = this.invPlayingSounds.get(tickableSound);
            this.sndSystem
               .setVolume(
                  str,
                  this.getNormalizedVolume(tickableSound, this.playingSoundPoolEntries.get(tickableSound), this.sndHandler.getSound(tickableSound.getSoundLocation()).getSoundCategory())
               );
            this.sndSystem.setPitch(str, this.getNormalizedPitch(tickableSound, this.playingSoundPoolEntries.get(tickableSound)));
            this.sndSystem.setPosition(str, tickableSound.getXPosF(), tickableSound.getYPosF(), tickableSound.getZPosF());
         }
      }

      Iterator iterator = this.playingSounds.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         String key = (String)entry.getKey();
         ISound value = (ISound)entry.getValue();
         if (!this.sndSystem.playing(key)) {
            int n = this.playingSoundsStopTime.get(key);
            if (n <= this.playTime) {
               int repeatDelay = value.getRepeatDelay();
               if (value.canRepeat() && repeatDelay > 0) {
                  this.delayedSounds.put(value, this.playTime + repeatDelay);
               }

               iterator.remove();
               logger.debug(LOG_MARKER, "Removed channel {} because it's not playing anymore", new Object[]{key});
               this.sndSystem.removeSource(key);
               this.playingSoundsStopTime.remove(key);
               this.playingSoundPoolEntries.remove(value);

               try {
                  this.categorySounds.remove(this.sndHandler.getSound(value.getSoundLocation()).getSoundCategory(), key);
               } catch (RuntimeException e) {
               }

               if (value instanceof ITickableSound) {
                  this.tickableSounds.remove(value);
               }
            }
         }
      }

      Iterator iterator2 = this.delayedSounds.entrySet().iterator();

      while (iterator2.hasNext()) {
         Entry entry = (Entry)iterator2.next();
         if (this.playTime >= (Integer)entry.getValue()) {
            ISound key = (ISound)entry.getKey();
            if (key instanceof ITickableSound) {
               ((ITickableSound)key).update();
            }

            this.playSound(key);
            iterator2.remove();
         }
      }
   }

   public boolean isSoundPlaying(ISound sound) {
      if (!this.loaded) {
         return false;
      }

      String str = this.invPlayingSounds.get(sound);
      return str == null
         ? false
         : this.sndSystem.playing(str) || this.playingSoundsStopTime.containsKey(str) && this.playingSoundsStopTime.get(str) <= this.playTime;
   }

   public void stopSound(ISound sound) {
      if (this.loaded) {
         String str = this.invPlayingSounds.get(sound);
         if (str != null) {
            this.sndSystem.stop(str);
         }
      }
   }

   public void playSound(ISound p_sound) {
      if (this.loaded) {
         if (this.sndSystem.getMasterVolume() <= 0.0F) {
            logger.debug(LOG_MARKER, "Skipped playing soundEvent: {}, master volume was zero", new Object[]{p_sound.getSoundLocation()});
         } else {
            SoundEventAccessorComposite sound = this.sndHandler.getSound(p_sound.getSoundLocation());
            if (sound == null && !p_sound.getSoundLocation().toString().equals("none")) {
               logger.warn(LOG_MARKER, "Unable to play unknown soundEvent: {}", p_sound.getSoundLocation());
            } else {
               SoundPoolEntry cloneEntry = sound.cloneEntry();
               if (cloneEntry == SoundHandler.missing_sound) {
                  logger.warn(LOG_MARKER, "Unable to play empty soundEvent: {}", new Object[]{sound.getSoundEventLocation()});
               } else {
                  float volume = p_sound.getVolume();
                  float f = 16.0F;
                  if (volume > 1.0F) {
                     f *= volume;
                  }

                  SoundCategory soundCategory = sound.getSoundCategory();
                  float normalizedVolume = this.getNormalizedVolume(p_sound, cloneEntry, soundCategory);
                  double normalizedPitch = this.getNormalizedPitch(p_sound, cloneEntry);
                  ResourceLocation soundPoolEntryLocation = cloneEntry.getSoundPoolEntryLocation();
                  if (normalizedVolume == 0.0F) {
                     logger.debug(LOG_MARKER, "Skipped playing sound {}, volume was zero.", new Object[]{soundPoolEntryLocation});
                  } else {
                     boolean flag = p_sound.canRepeat() && p_sound.getRepeatDelay() == 0;
                     String str = MathHelper.getRandomUuid(ThreadLocalRandom.current()).toString();
                     if (cloneEntry.isStreamingSound()) {
                        this.sndSystem
                           .newStreamingSource(
                              false,
                              str,
                              getURLForSoundResource(soundPoolEntryLocation),
                              soundPoolEntryLocation.toString(),
                              flag,
                              p_sound.getXPosF(),
                              p_sound.getYPosF(),
                              p_sound.getZPosF(),
                              p_sound.getAttenuationType().getTypeInt(),
                              f
                           );
                     } else {
                        this.sndSystem
                           .newSource(
                              false,
                              str,
                              getURLForSoundResource(soundPoolEntryLocation),
                              soundPoolEntryLocation.toString(),
                              flag,
                              p_sound.getXPosF(),
                              p_sound.getYPosF(),
                              p_sound.getZPosF(),
                              p_sound.getAttenuationType().getTypeInt(),
                              f
                           );
                     }

                     logger.debug(
                        LOG_MARKER,
                        "Playing sound {} for event {} as channel {}",
                        new Object[]{cloneEntry.getSoundPoolEntryLocation(), sound.getSoundEventLocation(), str}
                     );
                     this.sndSystem.setPitch(str, (float)normalizedPitch);
                     this.sndSystem.setVolume(str, normalizedVolume);
                     this.sndSystem.play(str);
                     this.playingSoundsStopTime.put(str, this.playTime + 20);
                     this.playingSounds.put(str, p_sound);
                     this.playingSoundPoolEntries.put(p_sound, cloneEntry);
                     if (soundCategory != SoundCategory.MASTER) {
                        this.categorySounds.put(soundCategory, str);
                     }

                     if (p_sound instanceof ITickableSound) {
                        this.tickableSounds.add((ITickableSound)p_sound);
                     }
                  }
               }
            }
         }
      }
   }

   public float getNormalizedPitch(ISound sound, SoundPoolEntry entry) {
      return (float)MathHelper.clamp_double(sound.getPitch() * entry.getPitch(), 0.5, 2.0);
   }

   public float getNormalizedVolume(ISound sound, SoundPoolEntry entry, SoundCategory category) {
      return (float)MathHelper.clamp_double(sound.getVolume() * entry.getVolume(), 0.0, 1.0) * this.getSoundCategoryVolume(category);
   }

   public void pauseAllSounds() {
      for (String str : this.playingSounds.keySet()) {
         logger.debug(LOG_MARKER, "Pausing channel {}", new Object[]{str});
         this.sndSystem.pause(str);
      }
   }

   public void resumeAllSounds() {
      for (String str : this.playingSounds.keySet()) {
         logger.debug(LOG_MARKER, "Resuming channel {}", new Object[]{str});
         this.sndSystem.play(str);
      }
   }

   public void playDelayedSound(ISound sound, int delay) {
      this.delayedSounds.put(sound, this.playTime + delay);
   }

   public static URL getURLForSoundResource(final ResourceLocation resourceLocation) {
      String format = String.format("%s:%s:%s", "mcsounddomain", resourceLocation.getResourceDomain(), resourceLocation.getResourcePath());
      URLStreamHandler urlstreamHandler = new URLStreamHandler() {
         @Override
         public URLConnection openConnection(URL url) {
            return new URLConnection(url) {
               @Override
               public void connect() throws IOException {
               }

               @Override
               public InputStream getInputStream() throws IOException {
                  return Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
               }
            };
         }
      };

      try {
         return new URL((URL)null, format, urlstreamHandler);
      } catch (MalformedURLException e) {
         throw new Error("TODO: Sanely handle url exception! :D");
      }
   }

   public void setListener(EntityPlayer player, float f) {
      if (this.loaded && player != null) {
         float f2 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
         float f3 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
         double d = player.prevPosX + (player.posX - player.prevPosX) * f;
         double d2 = player.prevPosY + (player.posY - player.prevPosY) * f + player.getEyeHeight();
         double d3 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
         float cos = MathHelper.cos((f3 + 90.0F) * (float) (Math.PI / 180.0));
         float sin = MathHelper.sin((f3 + 90.0F) * (float) (Math.PI / 180.0));
         float cos2 = MathHelper.cos(-f2 * (float) (Math.PI / 180.0));
         float sin2 = MathHelper.sin(-f2 * (float) (Math.PI / 180.0));
         float cos3 = MathHelper.cos((-f2 + 90.0F) * (float) (Math.PI / 180.0));
         float sin3 = MathHelper.sin((-f2 + 90.0F) * (float) (Math.PI / 180.0));
         float f4 = cos * cos2;
         float f5 = sin * cos2;
         float f6 = cos * cos3;
         float f7 = sin * cos3;
         this.sndSystem.setListenerPosition((float)d, (float)d2, (float)d3);
         this.sndSystem.setListenerOrientation(f4, sin2, f5, f6, sin3, f7);
      }
   }

   public void handleString(String str, float f) {
      if (this.loaded) {
         ResourceLocation resourceLocation = new ResourceLocation("client/sound/" + str + ".ogg");
         String str2 = UUID.randomUUID().toString();
         this.sndSystem
            .newStreamingSource(false, str2, getURLForSoundResource(resourceLocation), resourceLocation.toString(), false, 0.0F, 0.0F, 0.0F, 0, SoundSystemConfig.getDefaultRolloff());
         this.sndSystem.setPitch(str2, 1.0F);
         this.sndSystem.setVolume(str2, 1.0F * f);
         this.sndSystem.play(str2);
      }
   }

   class SoundSystemStarterThread extends SoundSystem {
      public SoundSystemStarterThread() {
      }

      public boolean playing(String str) {
         synchronized (SoundSystemConfig.THREAD_SYNC) {
            if (this.soundLibrary == null) {
               return false;
            }

            Source source = (Source)this.soundLibrary.getSources().get(str);
            return source == null ? false : source.playing() || source.paused() || source.preLoad;
         }
      }
   }
}
