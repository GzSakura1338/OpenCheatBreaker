package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public interface ISound {
    ResourceLocation getSoundLocation();

    boolean canRepeat();

    int getRepeatDelay();

    float getVolume();

    float getPitch();

    float getXPosF();

    float getYPosF();

    float getZPosF();

    ISound.AttenuationType getAttenuationType();

    enum AttenuationType {
        NONE("NONE", 0, 0),
        LINEAR("LINEAR", 1, 2);

        public int intValue;

        public int getTypeInt() {
            return this.intValue;
        }

        AttenuationType(String str, int n, int n2) {
            this.intValue = n2;
        }
    }
}
