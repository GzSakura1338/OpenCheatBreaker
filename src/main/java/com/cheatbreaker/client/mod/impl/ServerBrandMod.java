package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SetClientBrandEvent;
import com.cheatbreaker.client.mod.AbstractInternalMod;
import com.cheatbreaker.client.mod.ModCategory;

public class ServerBrandMod extends AbstractInternalMod {
    public String brand = "";

    public ServerBrandMod() {
        super("Server Brand");
        this.setDescription("Displays the brand of the current connected server.");
        this.setCategories(ModCategory.HUD, ModCategory.SERVER);
        this.setLabel("[CheatBreakerSpigot]");
        this.addEventHandler(SetClientBrandEvent.class, setClientBrandEvent -> this.brand = setClientBrandEvent.getBrand());
    }

    public String getBrand() {
        return this.brand;
    }

    @Override
    public void onEnable() throws java.io.IOException {
        super.onEnable();
        if (this.mc.theWorld != null) {
            this.brand = this.mc.thePlayer.getClientBrand();
        }
    }

    public String constantText() {
        return "Brand";
    }

    public String getText() {
        return CheatBreaker.clientName;
    }
}
