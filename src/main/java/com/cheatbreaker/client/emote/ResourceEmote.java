package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.animation.LinearAnimation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.util.ResourceLocation;

public class ResourceEmote extends AbstractEmote {
    public String emoteKey;
    public boolean looping;

    @Override
    public void afterModelRender(AbstractClientPlayer abstractClientPlayer, float f) {
    }

    public String getEmoteKey() {
        return this.emoteKey;
    }

    public boolean isLooping() {
        return this.looping;
    }

    public static long getDurationMillis(String str) {
        EmoteDefinition resolve = EmoteRegistry.resolve(str);
        int n = resolve != null ? resolve.durationTicks : 100;
        return Math.max(n, 1) * 50L;
    }

    @Override
    public ResourceLocation getClientEmotes() {
        return new ResourceLocation("client/emotes/" + this.emoteKey + ".png");
    }

    @Override
    public void applyPose(AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f) {
    }

    public ResourceEmote(String str) {
        super(formatEmoteName(str), new LinearAnimation(getDurationMillis(str)));
        this.emoteKey = str;
        EmoteDefinition resolve = EmoteRegistry.resolve(str);
        this.looping = resolve != null && resolve.looping;
    }

    @Override
    public boolean isFinished() {
        return !this.looping && super.isFinished();
    }

    public static String formatEmoteName(String str) {
        String[] split = str.split("_");
        StringBuilder sb = new StringBuilder();

        for (String str2 : split) {
            if (!str2.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }

                sb.append(Character.toUpperCase(str2.charAt(0))).append(str2.substring(1));
            }
        }

        return sb.toString();
    }

    @Override
    public void restorePerspective(AbstractClientPlayer abstractClientPlayer) {
        super.restorePerspective(abstractClientPlayer);
        EmotePlayHelper.playEmoteByName(null, abstractClientPlayer);
    }
}
