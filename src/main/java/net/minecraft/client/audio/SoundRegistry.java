package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.RegistrySimple;

public class SoundRegistry extends RegistrySimple {
    public Map sounds;

    @Override
    public Map createUnderlyingMap() {
        this.sounds = Maps.newHashMap();
        return this.sounds;
    }

    public void registerSound(SoundEventAccessorComposite soundEventAccessorComposite) {
        this.putObject(soundEventAccessorComposite.getSoundEventLocation(), soundEventAccessorComposite);
    }

    public void clearMap() {
        this.sounds.clear();
    }
}
