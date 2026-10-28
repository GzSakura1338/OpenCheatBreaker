package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.AddCollisionBoxesEvent;
import com.cheatbreaker.client.mod.AbstractNamedMod;
import com.cheatbreaker.client.mod.ModCategory;

public class NoclipMod extends AbstractNamedMod {
    public NoclipMod(String str) {
        super(str);
        this.setStaffMod(true);
        this.setCategories(ModCategory.MISC);
        this.addEventHandler(AddCollisionBoxesEvent.class, addCollisionBoxesEvent -> addCollisionBoxesEvent.setCancelled(true));
    }
}
