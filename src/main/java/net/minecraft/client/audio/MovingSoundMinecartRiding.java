package net.minecraft.client.audio;

import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class MovingSoundMinecartRiding extends MovingSound {
    public EntityPlayer player;
    public EntityMinecart minecart;

    public MovingSoundMinecartRiding(EntityPlayer entityPlayer, EntityMinecart entityMinecart) {
        super(new ResourceLocation("minecraft:minecart.inside"));
        this.player = entityPlayer;
        this.minecart = entityMinecart;
        this.attenuationType = AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
    }

    @Override
    public void update() {
        if (!this.minecart.isDead && this.player.isRiding() && this.player.ridingEntity == this.minecart) {
            float f = MathHelper.sqrt_double(this.minecart.motionX * this.minecart.motionX + this.minecart.motionZ * this.minecart.motionZ);
            if (f >= 0.01) {
                this.volume = 0.0F + MathHelper.clamp_float(f, 0.0F, 1.0F) * 0.75F;
            } else {
                this.volume = 0.0F;
            }
        } else {
            this.donePlaying = true;
        }
    }
}
