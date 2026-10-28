package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.Animation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractEmote {
    public Animation animation;
    public List<Animation> animationList;
    public String displayName;
    public ResourceLocation clientEmotes;

    public ResourceLocation getClientEmotes() {
        return this.clientEmotes;
    }

    public void delayAnimations(long l) {
        if (this.animationList == null) {
            this.animationList = this.collectAnimations();
        }

        for (Animation animation : this.animationList) {
            animation.delayStart(l);
        }
    }

    public boolean isFinished() {
        return this.animation.isFinished();
    }

    public AbstractEmote(String str, String str2, Animation animation) {
        this.animation = animation;
        animation.start();
        this.displayName = str;
        this.clientEmotes = new ResourceLocation("client/emotes/" + str2.toLowerCase().replace("-", "").replace(" ", "") + ".png");
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public List<Animation> getAnimationList() {
        return this.animationList;
    }

    public void restorePerspective(AbstractClientPlayer abstractClientPlayer) {
        if (abstractClientPlayer != null) {
            if (abstractClientPlayer.getUniqueID().equals(Minecraft.getMinecraft().getSession().getUUID()) && CheatBreaker.getInstance().getEmoteManager().isChangedPerspective()) {
                Minecraft.getMinecraft().gameSettings.thirdPersonView = 0;
                CheatBreaker.getInstance().getEmoteManager().setChangedPerspective(false);
                CheatBreaker.getInstance().getEmoteManager().setKeepPerspective(false);
            }
        }
    }

    public abstract void afterModelRender(AbstractClientPlayer abstractClientPlayer, float f);

    public Animation getAnimation() {
        return this.animation;
    }

    public abstract void applyPose(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f);

    public AbstractEmote(String str, Animation animation) {
        this(str, str, animation);
    }

    public List<Animation> collectAnimations() {
        ArrayList list = new ArrayList();

        for (Class clazz = this.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && Animation.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        Animation animation = (Animation)field.get(this);
                        if (animation != null) {
                            list.add(animation);
                        }
                    } catch (IllegalAccessException e) {
                    }
                }
            }
        }

        return list;
    }
}
