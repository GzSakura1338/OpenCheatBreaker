package net.minecraft.client.renderer;

import net.minecraft.client.renderer.texture.Stitcher.Holder;

public class StitcherException extends RuntimeException {
    public Holder holder;

    public StitcherException(Holder holder, String str) {
        super(str);
        this.holder = holder;
    }
}
