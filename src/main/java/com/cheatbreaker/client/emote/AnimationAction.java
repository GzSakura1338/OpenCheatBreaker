package com.cheatbreaker.client.emote;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

public class AnimationAction {
    public String name = "";
    public boolean clamp = true;
    public boolean reset = true;
    public float speed = 1.0F;
    public float fade = 5.0F;
    public int tick = 0;

    public void fromNbt(NBTBase nbtbase) {
        if (nbtbase instanceof NBTTagCompound) {
            NBTTagCompound nbttagCompound = (NBTTagCompound)nbtbase;
            if (nbttagCompound.hasKey("Name")) {
                this.name = nbttagCompound.getString("Name");
            }

            if (nbttagCompound.hasKey("Clamp")) {
                this.clamp = nbttagCompound.getBoolean("Clamp");
            }

            if (nbttagCompound.hasKey("Reset")) {
                this.reset = nbttagCompound.getBoolean("Reset");
            }

            if (nbttagCompound.hasKey("Speed")) {
                this.speed = nbttagCompound.getFloat("Speed");
            }

            if (nbttagCompound.hasKey("Fade")) {
                this.fade = nbttagCompound.getInteger("Fade");
            }

            if (nbttagCompound.hasKey("Tick")) {
                this.tick = nbttagCompound.getInteger("Tick");
            }
        } else if (nbtbase instanceof NBTTagString) {
            this.name = ((NBTTagString)nbtbase).getString();
        }
    }

    public NBTBase toNbt() {
        if (!this.name.isEmpty() && this.isDefault()) {
            return new NBTTagString(this.name);
        }

        NBTTagCompound nbttagCompound = new NBTTagCompound();
        if (!this.name.isEmpty()) {
            nbttagCompound.setString("Name", this.name);
        }

        if (!this.clamp) {
            nbttagCompound.setBoolean("Clamp", this.clamp);
        }

        if (!this.reset) {
            nbttagCompound.setBoolean("Reset", this.reset);
        }

        if (this.speed != 1.0F) {
            nbttagCompound.setFloat("Speed", this.speed);
        }

        if (this.fade != 5.0F) {
            nbttagCompound.setInteger("Fade", (int)this.fade);
        }

        if (this.tick != 0) {
            nbttagCompound.setInteger("Tick", this.tick);
        }

        return nbttagCompound;
    }

    @Override
    public Object clone() {
        return this.copy();
    }

    public AnimationAction copy() {
        AnimationAction animationAction = new AnimationAction(this.name);
        animationAction.clamp = this.clamp;
        animationAction.reset = this.reset;
        animationAction.speed = this.speed;
        animationAction.fade = this.fade;
        animationAction.tick = this.tick;
        return animationAction;
    }

    public AnimationAction() {
    }

    public boolean isDefault() {
        return this.clamp && this.reset && this.speed == 1.0F && this.fade == 5.0F && this.tick == 0;
    }

    public AnimationAction(String str) {
        this.name = str;
    }
}
