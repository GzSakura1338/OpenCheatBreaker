package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.client.audio.SoundList.SoundEntry;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundHandler implements IResourceManagerReloadListener, IUpdatePlayerListBox {
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

    public SoundHandler(IResourceManager resourceManager, GameSettings gameSettings) {
        this.mcResourceManager = resourceManager;
        this.sndManager = new SoundManager(this, gameSettings);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.sndManager.reloadSoundSystem();
        this.sndRegistry.clearMap();

        for (String str : (Iterable<String>)resourceManager.getResourceDomains()) {
            try {
                for (IResource resource : (Iterable<IResource>)resourceManager.getAllResources(new ResourceLocation(str, "sounds.json"))) {
                    try {
                        Map fromJson = (Map)GSON.fromJson(new InputStreamReader(resource.getInputStream()), TYPE);

                        for (Entry entry : (Iterable<Entry>)fromJson.entrySet()) {
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

    public void loadSoundResource(ResourceLocation resourceLocation, SoundList soundList) {
        SoundEventAccessorComposite soundEventAccessorComposite;
        if (this.sndRegistry.containsKey(resourceLocation) && !soundList.canReplaceExisting()) {
            soundEventAccessorComposite = (SoundEventAccessorComposite)this.sndRegistry.getObject(resourceLocation);
        } else {
            logger.debug("Registered/replaced new sound event location {}", resourceLocation);
            soundEventAccessorComposite = new SoundEventAccessorComposite(resourceLocation, 1.0, 1.0, soundList.getSoundCategory());
            this.sndRegistry.registerSound(soundEventAccessorComposite);
        }

        for (final SoundEntry soundEntry : (Iterable<SoundEntry>)soundList.getSoundList()) {
            String soundEntryName = soundEntry.getSoundEntryName();
            ResourceLocation resourceLocation2 = new ResourceLocation(soundEntryName);
            final String str = soundEntryName.contains(":") ? resourceLocation2.getResourceDomain() : resourceLocation.getResourceDomain();
            ISoundEventAccessor soundEventAccessor;
            switch (soundEntry.getSoundEntryType()) {
                case FILE:
                    ResourceLocation resourceLocation3 = new ResourceLocation(str, "sounds/" + resourceLocation2.getResourcePath() + ".ogg");

                    try {
                        this.mcResourceManager.getResource(resourceLocation3);
                    } catch (FileNotFoundException e) {
                        logger.warn("File {} does not exist, cannot add it to event {}", resourceLocation3, resourceLocation);
                        continue;
                    } catch (IOException e) {
                        logger.warn("Could not load sound file " + resourceLocation3 + ", cannot add it to event " + resourceLocation, e);
                        continue;
                    }

                    soundEventAccessor = new SoundEventAccessor(
                        new SoundPoolEntry(resourceLocation3, soundEntry.getSoundEntryPitch(), soundEntry.getSoundEntryVolume(), soundEntry.isStreaming()), soundEntry.getSoundEntryWeight()
                    );
                    break;
                case SOUND_EVENT:
                    soundEventAccessor = new ISoundEventAccessor() {
                        public ResourceLocation resourceLocation = new ResourceLocation(str, soundEntry.getSoundEntryName());

                        @Override
                        public int getWeight() {
                            SoundEventAccessorComposite object = (SoundEventAccessorComposite)SoundHandler.this.sndRegistry.getObject(this.resourceLocation);
                            return object == null ? 0 : object.getWeight();
                        }

                        public SoundPoolEntry getSound() {
                            SoundEventAccessorComposite object = (SoundEventAccessorComposite)SoundHandler.this.sndRegistry.getObject(this.resourceLocation);
                            return object == null ? SoundHandler.missing_sound : object.getSound();
                        }
                    };
                    break;
                default:
                    throw new IllegalStateException("IN YOU FACE");
            }

            soundEventAccessorComposite.addSoundToEventPool(soundEventAccessor);
        }
    }

    public SoundEventAccessorComposite getSound(ResourceLocation resourceLocation) {
        return (SoundEventAccessorComposite)this.sndRegistry.getObject(resourceLocation);
    }

    public void playSound(ISound sound) {
        this.sndManager.playSound(sound);
    }

    public void playDelayedSound(ISound sound, int n) {
        this.sndManager.playDelayedSound(sound, n);
    }

    public void setListener(EntityPlayer entityPlayer, float f) {
        this.sndManager.setListener(entityPlayer, f);
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

    public void setSoundLevel(SoundCategory soundCategory, float f) {
        if (soundCategory == SoundCategory.MASTER && f <= 0.0F) {
            this.stopSounds();
        }

        this.sndManager.setSoundCategoryVolume(soundCategory, f);
    }

    public void stopSound(ISound sound) {
        this.sndManager.stopSound(sound);
    }

    public SoundEventAccessorComposite getRandomSoundFromCategories(SoundCategory... soundCategories) {
        ArrayList newArrayList = Lists.newArrayList();

        for (ResourceLocation resourceLocation : (Iterable<ResourceLocation>)this.sndRegistry.getKeys()) {
            SoundEventAccessorComposite object = (SoundEventAccessorComposite)this.sndRegistry.getObject(resourceLocation);
            if (ArrayUtils.contains(soundCategories, object.getSoundCategory())) {
                newArrayList.add(object);
            }
        }

        return newArrayList.isEmpty() ? null : (SoundEventAccessorComposite)newArrayList.get(new Random().nextInt(newArrayList.size()));
    }

    public boolean isSoundPlaying(ISound sound) {
        return this.sndManager.isSoundPlaying(sound);
    }
}
