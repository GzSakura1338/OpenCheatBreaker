package net.minecraft.client.audio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
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
import paulscode.sound.Source;
import paulscode.sound.codecs.CodecJOrbis;
import com.cheatbreaker.client.audio.JavaSoundMixer;
import paulscode.sound.libraries.LibraryJavaSound;

public class SoundManager {
    public static Marker LOG_MARKER = MarkerManager.getMarker("SOUNDS");
    public static Logger logger = LogManager.getLogger();
    public int playTime = 0;
    public Map playingSounds = HashBiMap.create();
    public SoundManager.SoundSystemStarterThread sndSystem;
    public boolean loaded;
    public Map invPlayingSounds = ((BiMap)this.playingSounds).inverse();
    public Map playingSoundPoolEntries = Maps.newHashMap();
    public Multimap categorySounds = HashMultimap.create();
    public List tickableSounds = Lists.newArrayList();
    public Map delayedSounds = Maps.newHashMap();
    public Map playingSoundsStopTime = Maps.newHashMap();
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
                        SoundManager.this.sndSystem = SoundManager.this.new SoundSystemStarterThread(null);
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

    public float getSoundCategoryVolume(SoundCategory soundCategory) {
        return soundCategory != null && soundCategory != SoundCategory.MASTER ? this.options.getSoundLevel(soundCategory) : 1.0F;
    }

    public void setSoundCategoryVolume(SoundCategory soundCategory, float f) {
        if (this.loaded) {
            if (soundCategory == SoundCategory.MASTER) {
                this.sndSystem.setMasterVolume(f);
            } else {
                for (String str : (Iterable<String>)this.categorySounds.get(soundCategory)) {
                    ISound sound = (ISound)this.playingSounds.get(str);
                    float normalizedVolume = this.getNormalizedVolume(sound, (SoundPoolEntry)this.playingSoundPoolEntries.get(sound), soundCategory);
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
            for (String str : (Iterable<String>)this.playingSounds.keySet()) {
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

        for (ITickableSound tickableSound : (Iterable<ITickableSound>)this.tickableSounds) {
            tickableSound.update();
            if (tickableSound.isDonePlaying()) {
                this.stopSound(tickableSound);
            } else {
                String str = (String)this.invPlayingSounds.get(tickableSound);
                this.sndSystem
                    .setVolume(
                        str,
                        this.getNormalizedVolume(
                            tickableSound, (SoundPoolEntry)this.playingSoundPoolEntries.get(tickableSound), this.sndHandler.getSound(tickableSound.getSoundLocation()).getSoundCategory()
                        )
                    );
                this.sndSystem.setPitch(str, this.getNormalizedPitch(tickableSound, (SoundPoolEntry)this.playingSoundPoolEntries.get(tickableSound)));
                this.sndSystem.setPosition(str, tickableSound.getXPosF(), tickableSound.getYPosF(), tickableSound.getZPosF());
            }
        }

        Iterator iterator = this.playingSounds.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry)iterator.next();
            String key = (String)entry.getKey();
            ISound value = (ISound)entry.getValue();
            if (!this.sndSystem.playing(key)) {
                int n = (Integer)this.playingSoundsStopTime.get(key);
                if (n <= this.playTime) {
                    int repeatDelay = value.getRepeatDelay();
                    if (value.canRepeat() && repeatDelay > 0) {
                        this.delayedSounds.put(value, this.playTime + repeatDelay);
                    }

                    iterator.remove();
                    logger.debug(LOG_MARKER, "Removed channel {} because it's not playing anymore", key);
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

        String str = (String)this.invPlayingSounds.get(sound);
        return str != null
            && (this.sndSystem.playing(str) || this.playingSoundsStopTime.containsKey(str) && (Integer)this.playingSoundsStopTime.get(str) <= this.playTime);
    }

    public void stopSound(ISound sound) {
        if (this.loaded) {
            String str = (String)this.invPlayingSounds.get(sound);
            if (str != null) {
                this.sndSystem.stop(str);
            }
        }
    }

    public void playSound(ISound sound) {
        if (this.loaded) {
            if (this.sndSystem.getMasterVolume() <= 0.0F) {
                logger.debug(LOG_MARKER, "Skipped playing soundEvent: {}, master volume was zero", sound.getSoundLocation());
            } else {
                SoundEventAccessorComposite sound2 = this.sndHandler.getSound(sound.getSoundLocation());
                if (sound2 == null) {
                    logger.warn(LOG_MARKER, "Unable to play unknown soundEvent: {}", sound.getSoundLocation());
                } else {
                    SoundPoolEntry sound3 = sound2.getSound();
                    if (sound3 == SoundHandler.missing_sound) {
                        logger.warn(LOG_MARKER, "Unable to play empty soundEvent: {}", sound2.getSoundEventLocation());
                    } else {
                        float volume = sound.getVolume();
                        float f = 16.0F;
                        if (volume > 1.0F) {
                            f *= volume;
                        }

                        SoundCategory soundCategory = sound2.getSoundCategory();
                        float normalizedVolume = this.getNormalizedVolume(sound, sound3, soundCategory);
                        double normalizedPitch = this.getNormalizedPitch(sound, sound3);
                        ResourceLocation soundPoolEntryLocation = sound3.getSoundPoolEntryLocation();
                        if (normalizedVolume == 0.0F) {
                            logger.debug(LOG_MARKER, "Skipped playing sound {}, volume was zero.", soundPoolEntryLocation);
                        } else {
                            boolean flag = sound.canRepeat() && sound.getRepeatDelay() == 0;
                            String str = UUID.randomUUID().toString();
                            if (sound3.isStreamingSound()) {
                                this.sndSystem
                                    .newStreamingSource(
                                        false,
                                        str,
                                        getURLForSoundResource(soundPoolEntryLocation),
                                        soundPoolEntryLocation.toString(),
                                        flag,
                                        sound.getXPosF(),
                                        sound.getYPosF(),
                                        sound.getZPosF(),
                                        sound.getAttenuationType().getTypeInt(),
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
                                        sound.getXPosF(),
                                        sound.getYPosF(),
                                        sound.getZPosF(),
                                        sound.getAttenuationType().getTypeInt(),
                                        f
                                    );
                            }

                            logger.debug(
                                LOG_MARKER, "Playing sound {} for event {} as channel {}", sound3.getSoundPoolEntryLocation(), sound2.getSoundEventLocation(), str
                            );
                            this.sndSystem.setPitch(str, (float)normalizedPitch);
                            this.sndSystem.setVolume(str, normalizedVolume);
                            this.sndSystem.play(str);
                            this.playingSoundsStopTime.put(str, this.playTime + 20);
                            this.playingSounds.put(str, sound);
                            this.playingSoundPoolEntries.put(sound, sound3);
                            if (soundCategory != SoundCategory.MASTER) {
                                this.categorySounds.put(soundCategory, str);
                            }

                            if (sound instanceof ITickableSound) {
                                this.tickableSounds.add(sound);
                            }
                        }
                    }
                }
            }
        }
    }

    public float getNormalizedPitch(ISound sound, SoundPoolEntry soundPoolEntry) {
        return (float)MathHelper.clamp_double(sound.getPitch() * soundPoolEntry.getPitch(), 0.5, 2.0);
    }

    public float getNormalizedVolume(ISound sound, SoundPoolEntry soundPoolEntry, SoundCategory soundCategory) {
        return (float)MathHelper.clamp_double(sound.getVolume() * soundPoolEntry.getVolume() * this.getSoundCategoryVolume(soundCategory), 0.0, 1.0);
    }

    public void pauseAllSounds() {
        for (String str : (Iterable<String>)this.playingSounds.keySet()) {
            logger.debug(LOG_MARKER, "Pausing channel {}", str);
            this.sndSystem.pause(str);
        }
    }

    public void resumeAllSounds() {
        for (String str : (Iterable<String>)this.playingSounds.keySet()) {
            logger.debug(LOG_MARKER, "Resuming channel {}", str);
            this.sndSystem.play(str);
        }
    }

    public void playDelayedSound(ISound sound, int n) {
        this.delayedSounds.put(sound, this.playTime + n);
    }

    public static URL getURLForSoundResource(final ResourceLocation resourceLocation) {
        String format = String.format("%s:%s:%s", "mcsounddomain", resourceLocation.getResourceDomain(), resourceLocation.getResourcePath());
        URLStreamHandler urlstreamHandler = new URLStreamHandler() {
            @Override
            public URLConnection openConnection(URL url) {
                return new URLConnection(url) {
                    @Override
                    public void connect() {
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
                    }
                };
            }
        };

        try {
            return new URL(null, format, urlstreamHandler);
        } catch (MalformedURLException e) {
            throw new Error("TODO: Sanely handle url exception! :D");
        }
    }

    public void setListener(EntityPlayer entityPlayer, float f) {
        if (this.loaded && entityPlayer != null) {
            float f2 = entityPlayer.prevRotationPitch + (entityPlayer.rotationPitch - entityPlayer.prevRotationPitch) * f;
            float f3 = entityPlayer.prevRotationYaw + (entityPlayer.rotationYaw - entityPlayer.prevRotationYaw) * f;
            double d = entityPlayer.prevPosX + (entityPlayer.posX - entityPlayer.prevPosX) * f;
            double d2 = entityPlayer.prevPosY + (entityPlayer.posY - entityPlayer.prevPosY) * f;
            double d3 = entityPlayer.prevPosZ + (entityPlayer.posZ - entityPlayer.prevPosZ) * f;
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

    public void playClientSound(String str, float f) {
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

    public class SoundSystemStarterThread extends SoundSystem {
        public SoundSystemStarterThread() {
        }

        public boolean playing(String str) {
            Object obj = SoundSystemConfig.THREAD_SYNC;
            synchronized (SoundSystemConfig.THREAD_SYNC) {
                if (this.soundLibrary == null) {
                    return false;
                }

                Source source = (Source)this.soundLibrary.getSources().get(str);
                return source != null && (source.playing() || source.paused() || source.preLoad);
            }
        }

        public SoundSystemStarterThread(Object obj) {
            this();
        }
    }
}
