package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.emote.bobj.BobjArmature;
import com.cheatbreaker.client.emote.bobj.PlayerMeshRenderer;
import java.util.Random;
import net.minecraft.entity.EntityLivingBase;

public class EmoteDefinition {
    public Random random = new Random();
    public String name;
    public int durationTicks;
    public boolean looping;

    public void onStop(PlayerMeshRenderer playerMeshRenderer) {
    }

    public boolean stopsOnMovement() {
        return false;
    }

    public boolean rendersCape() {
        return true;
    }

    public void onTick(EntityLivingBase entityLivingBase, BobjArmature bobjArmature, PlayerMeshRenderer playerMeshRenderer, int n, float f) {
    }

    public float randomOffset(float f) {
        return this.random.nextFloat() * f - f / 2.0F;
    }

    public EmoteDefinition createVariant(String str) {
        return this;
    }

    public EmoteDefinition createInstance() {
        return this;
    }

    public void onStart(PlayerMeshRenderer playerMeshRenderer) {
    }

    public int getMaxRepeats() {
        return -1;
    }

    public String getDisplayName() {
        return this.name;
    }

    public boolean hasRepeatLimit() {
        return this.getMaxRepeats() > 0;
    }

    public EmoteDefinition(String str, int n, boolean flag) {
        this.name = str;
        this.durationTicks = n;
        this.looping = flag;
    }

    public boolean rendersWings() {
        return true;
    }
}
