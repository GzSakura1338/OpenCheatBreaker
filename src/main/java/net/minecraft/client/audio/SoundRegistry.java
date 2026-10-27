package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.RegistrySimple;
import net.minecraft.util.ResourceLocation;

public class SoundRegistry extends RegistrySimple<ResourceLocation, SoundEventAccessorComposite> {
   public Map<ResourceLocation, SoundEventAccessorComposite> soundRegistry;

   @Override
   public Map<ResourceLocation, SoundEventAccessorComposite> createUnderlyingMap() {
      this.soundRegistry = Maps.newHashMap();
      return this.soundRegistry;
   }

   public void registerSound(SoundEventAccessorComposite soundEventAccessorComposite) {
      this.putObject(soundEventAccessorComposite.getSoundEventLocation(), soundEventAccessorComposite);
   }

   public void clearMap() {
      this.soundRegistry.clear();
   }
}
