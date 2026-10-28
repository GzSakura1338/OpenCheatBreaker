package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.emote.AbstractEmote;
import com.cheatbreaker.client.emote.EmoteManager;
import com.cheatbreaker.client.emote.EmoteWheelEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.lwjgl.input.Keyboard;

public class QuickEmoteWheelScreen extends EmoteWheelScreen {
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void mouseClickedScaled(float f, float f2, int n) {
    }

    public static List<EmoteWheelEntry> getList() {
        EmoteManager emoteManager = CheatBreaker.getInstance().getEmoteManager();
        List ownedEmotes = emoteManager.getOwnedEmotes();
        ArrayList listValue = CheatBreaker.getInstance().getKeybindSettings().emoteWheelQuickActions.getListValue();
        ArrayList list = new ArrayList();
        HashSet set = new HashSet();

        for (int i = 0; i < 8; i++) {
            Integer integer = i < listValue.size() ? (Integer)listValue.get(i) : null;
            EmoteWheelEntry emoteWheelEntry = null;
            if (integer != null && integer >= 0 && ownedEmotes.contains(integer)) {
                AbstractEmote createEmote = emoteManager.createEmote(integer);
                if (createEmote != null) {
                    emoteWheelEntry = new EmoteWheelEntry(createEmote, createEmote.getDisplayName(), createEmote.getClientEmotes());
                    set.add(integer);
                }
            }

            list.add(emoteWheelEntry);
        }

        ownedEmotes.stream().filter(obj -> !set.contains(obj)).map(obj -> {
            AbstractEmote createEmote = emoteManager.createEmote((int)obj);
            return createEmote == null ? null : new EmoteWheelEntry(createEmote, createEmote.getDisplayName(), createEmote.getClientEmotes());
        }).filter(Objects::nonNull).sorted(Comparator.comparing(EmoteWheelEntry::getName, String.CASE_INSENSITIVE_ORDER)).forEach(list::add);
        return list;
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        super.drawScreen(n, n2, f);
    }

    public QuickEmoteWheelScreen(int n) {
        super(n, getList());
        this.consumer = emoteWheelEntry -> {
            if (emoteWheelEntry != null && !Keyboard.isKeyDown(this.holdKey)) {
                CheatBreaker.getInstance().getEmoteManager().playLocalEmote(emoteWheelEntry.getAbstractEmote());
            }
        };
    }

    @Override
    public void mouseReleasedScaled(float f, float f2, int n) {
    }
}
