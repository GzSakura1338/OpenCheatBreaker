package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.ScaledResolutionEvent7;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.render.GlStateCache;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.voice.VoiceChatPlayer;
import com.cheatbreaker.client.voice.VoicePushToTalkHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class VoiceChatUIMod extends AbstractHudMod {
    public static final float CONSTANT_110_0 = 110.0F;
    public static final float CONSTANT_20_0 = 20.0F;
    public static List<VoiceChatEntry> voiceChatEntryList = Arrays.asList(
        new VoiceChatEntry("Notch", false, true), new VoiceChatEntry("dinnerbone", false, true)
    );
    public static final float CONSTANT_18_0 = 18.0F;

    public void drawEntry(String str, float f, boolean flag, boolean flag2) {
        if (flag) {
            RenderUtils.drawBorderedGradientRect(0.0F, f, 110.0F, f + 18.0F, -11493284, -10176146, -11164318);
        } else {
            RenderUtils.drawBorderedGradientRect(0.0F, f, 110.0F, f + 18.0F, -1356454362, -1355664846, -1356191190);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (flag2) {
            ResourceLocation headTexture = this.cheatBreaker.getFontManager().getHeadTexture(str);
            RenderUtils.drawSquareTextureViaRenderEngine(headTexture, 7.0F, 2.0F, f + 2.0F);
        } else {
            RenderUtils.drawRoundedRectFilledPrecise(2.0, f + 2.0F, 16.0, f + 16.0F, 2.0, 1442840575);
            this.cheatBreaker.getFontManager().playRegular16.drawCenteredString("?", 9.0F, f + 4.0F, -1);
        }

        this.cheatBreaker.getFontManager().playRegular16.drawString(str, 22.0F, f + 4.0F, -1);
    }

    public static String uuidKey(String str) {
        return "u:" + str.replace("-", "").toLowerCase(Locale.ROOT);
    }

    public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
        if (this.shouldRender()) {
            List list = this.getList();
            int n = list.isEmpty() ? voiceChatEntryList.size() : list.size();
            this.setHudSize(110.0F, getHeight(n));
            if (!list.isEmpty()) {
                GlStateCache.glPushMatrix();
                this.applyHudTransformForSize(scaledResolutionEvent3.getScaledResolution());
                float f = 0.0F;

                for (VoiceChatEntry voiceChatEntry : (Iterable<VoiceChatEntry>)list) {
                    this.drawEntry(voiceChatEntry.name, f, voiceChatEntry.speaking, voiceChatEntry.showHead);
                    f += 20.0F;
                }

                GlStateCache.glPopMatrix();
            }
        }
    }

    public void onScaledResolutionEvent7(ScaledResolutionEvent7 scaledResolutionEvent7) {
        if (this.getList().isEmpty()) {
            this.setHudSize(110.0F, getHeight(voiceChatEntryList.size()));
            GlStateCache.glPushMatrix();
            this.applyHudTransformForSize(scaledResolutionEvent7.getScaledResolution());
            float f = 0.0F;

            for (VoiceChatEntry voiceChatEntry : voiceChatEntryList) {
                this.drawEntry(voiceChatEntry.name, f, voiceChatEntry.speaking, voiceChatEntry.showHead);
                f += 20.0F;
            }

            GlStateCache.glPopMatrix();
        }
    }

    public List<VoiceChatEntry> getList() {
        VoicePushToTalkHandler voicePushToTalkHandler = this.cheatBreaker.getModManager().voicePushToTalkHandler;
        boolean flag = this.cheatBreaker.getNetHandler().isVoiceEnabled() && this.cheatBreaker.getNetHandler().getVoiceChannels() != null;
        ArrayList list = new ArrayList();
        HashSet set = new HashSet();
        String username = this.mc.getSession().getUsername();
        String uuidKey2 = uuidKey(this.mc.thePlayer != null ? this.mc.thePlayer.getUniqueID().toString() : this.mc.getSession().getPlayerID());
        boolean flag2 = flag && voicePushToTalkHandler.isPushToTalkActive();

        if (flag2) {
            list.add(new VoiceChatEntry(username, true, true));
            set.add(nameKey(username));
            set.add(uuidKey2);
        }

        if (flag) {
            for (VoiceChatPlayer voiceChatPlayer : voicePushToTalkHandler.unmodifiableMap().keySet()) {
                String nameKey2 = nameKey(voiceChatPlayer.getName());
                String uuidKey3 = uuidKey(voiceChatPlayer.getUuid().toString());
                if (!set.contains(nameKey2) && !set.contains(uuidKey3)) {
                    set.add(nameKey2);
                    set.add(uuidKey3);
                    list.add(new VoiceChatEntry(voiceChatPlayer.getName(), false, true));
                }
            }
        }

        return list;
    }

    public static String nameKey(String str) {
        return "n:" + str.toLowerCase(Locale.ROOT);
    }
    public VoiceChatUIMod() throws java.io.IOException {
        super("Voice Chat UI");
        this.setAnchorPointAndDefault(AnchorPoint.RIGHT_TOP);
        this.setTranslationAndDefault(-8.0F, 8.0F);
        this.setEnabled(false);
        this.setApplyDefaultColorOnEnable(false);
        this.setUseDefaultTranslation(true);
        this.setDescription("Shows who is currently speaking in voice chat.");
        this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
        this.addEventHandler(ScaledResolutionEvent7.class, this::onScaledResolutionEvent7);
        this.setEnabledByDefault(true);
    }

    public static float getHeight(int n) {
        return Math.max(n, 1) * 20.0F - 2.0F;
    }
}
