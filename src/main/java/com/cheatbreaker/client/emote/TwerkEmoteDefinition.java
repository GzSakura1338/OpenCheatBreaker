package com.cheatbreaker.client.emote;

public class TwerkEmoteDefinition extends EmoteDefinition {
    @Override
    public boolean rendersWings() {
        return false;
    }

    @Override
    public boolean rendersCape() {
        return false;
    }

    public TwerkEmoteDefinition(String str, int n, boolean flag) {
        super(str, n, flag);
    }

    @Override
    public boolean stopsOnMovement() {
        return true;
    }
}
