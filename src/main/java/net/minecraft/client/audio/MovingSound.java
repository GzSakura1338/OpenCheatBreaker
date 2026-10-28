package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public abstract class MovingSound extends PositionedSound implements ITickableSound {
    public boolean donePlaying = false;

    public MovingSound(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean isDonePlaying() {
        return this.donePlaying;
    }
}
