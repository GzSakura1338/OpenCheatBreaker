package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.SimpleEvent14;
import com.cheatbreaker.client.event.SimpleEvent33;
import com.cheatbreaker.client.minimap.VoxelMap;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.ModForceState;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.ui.AnchorPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class ZansMinimapMod extends AbstractMod {
    public static ModForceState modForceState = ModForceState.NEUTRAL;
    public VoxelMap voxelMap;

    public ZansMinimapMod() throws java.io.IOException {
        super("Zans Minimap");
        this.setEnabledByDefault(false);
        this.hudOnly = false;
        this.setAnchorPointAndDefault(AnchorPoint.RIGHT_TOP);
        this.voxelMap = new VoxelMap(true, true);

        String str;
        try {
            str = Keyboard.getKeyName(this.cheatBreaker.getModManager().zansMinimapMod.getVoxelMap().getMapOptions().keyMinimapVoxelmapmenu.getKeyCode());
        } catch (NullPointerException e) {
            str = "M";
        }

        new SettingGroup(this, "PRESS '" + str + "' INGAME FOR OPTIONS.").changeValue(true);
        this.setIcon(new ResourceLocation("client/icons/mods/zans.png"), 42, 42);
        this.setDescription("Adds a minimap on the top right and allows you to manage waypoints.");
        this.setAuthors("Zan");
        this.setCategories(ModCategory.HUD);
        this.setCanToggle(false);
        this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
        this.subscribe(SimpleEvent14.class, this::onSimpleEvent14);
        this.subscribe(SimpleEvent33.class, this::onSimpleEvent33);
    }

    public VoxelMap getVoxelMap() {
        return this.voxelMap;
    }

    @Override
    public void onEnable() throws java.io.IOException {
        super.onEnable();
        if (modForceState == ModForceState.FORCED_OFF) {
            CheatBreaker.getInstance()
                .getModManager()
                .notificationsMod
                .addNotification("Error", "&4Minimap &fis not allowed on this server. Some functions may not work.", 4000L);
        }
    }

    public void onSimpleEvent33(SimpleEvent33 simpleEvent33) {
        this.voxelMap.getMapOptions().load();
    }

    public void onSimpleEvent14(SimpleEvent14 simpleEvent14) {
        this.voxelMap.getMapOptions().save();
    }

    public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) { try {
        float f = 1.0F / CheatBreaker.getInstance().getUiManager().getFloatValue();
        switch (this.voxelMap.getMapOptions().mapCorner) {
            case 0:
                if (this.getAnchorPoint() != AnchorPoint.LEFT_TOP) {
                    this.setAnchorPoint(AnchorPoint.LEFT_TOP);
                }
                break;
            case 1:
                if (this.getAnchorPoint() != AnchorPoint.RIGHT_TOP) {
                    this.setAnchorPoint(AnchorPoint.RIGHT_TOP);
                }
                break;
            case 2:
                if (this.getAnchorPoint() != AnchorPoint.RIGHT_BOTTOM) {
                    this.setAnchorPoint(AnchorPoint.RIGHT_BOTTOM);
                }
                break;
            case 3:
                if (this.getAnchorPoint() != AnchorPoint.LEFT_BOTTOM) {
                    this.setAnchorPoint(AnchorPoint.LEFT_BOTTOM);
                }
        }

        switch (this.voxelMap.getMapOptions().mapSize) {
            case -1:
                this.setTranslation((int)(-5.0F * f), (int)(5.0F * f));
                this.setHudSize((int)(100.0F * f), (int)(100.0F * f));
                break;
            case 0:
                this.setTranslation((int)(-5.0F * f), (int)(5.0F * f));
                this.setHudSize((int)(135.0F * f), (int)(135.0F * f));
                break;
            case 1:
                this.setTranslation((int)(-5.0F * f), (int)(5.0F * f));
                this.setHudSize((int)(175.0F * f), (int)(175.0F * f));
        }

        this.voxelMap.onTickInGame(Minecraft.getMinecraft());
    } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } }
}
