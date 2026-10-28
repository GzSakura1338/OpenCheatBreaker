package com.cheatbreaker.client.mod;

import com.cheatbreaker.client.util.DeveloperFlags;

public class AbstractInternalMod extends AbstractMod {
    @Override
    public void onEnable() throws java.io.IOException {
        if (DeveloperFlags.showInternalMods) {
            super.onEnable();
        }
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && DeveloperFlags.showInternalMods;
    }

    public AbstractInternalMod(String str) {
        super(str);
    }
}
