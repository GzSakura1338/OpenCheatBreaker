package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.BlockIntEvent;
import com.cheatbreaker.client.event.BlockLightValueEvent;
import com.cheatbreaker.client.event.BlockRenderPassEvent;
import com.cheatbreaker.client.event.TessellatorColorOpaqueEvent;
import com.cheatbreaker.client.mod.AbstractNamedMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;

public class OpacityMod extends AbstractNamedMod {
    public List<Integer> visibleBlockIds = new ArrayList<>();
    public Setting opacity;

    public boolean shouldRenderBlock(int n, boolean flag) {
        return !this.isEnabled() ? flag : this.visibleBlockIds.contains(n);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.mc.renderGlobal.loadRenderers();
    }

    public void onBlockIntEvent(BlockIntEvent blockIntEvent) {
        this.mc.renderGlobal.renderBlocksRg.renderAllFaces = this.shouldRenderBlock(
            Block.getIdFromBlock(blockIntEvent.getBlock()), this.mc.renderGlobal.renderBlocksRg.renderAllFaces
        );
    }

    @Override
    public void onEnable() throws java.io.IOException {
        super.onEnable();
        this.mc.renderGlobal.loadRenderers();
    }

    public OpacityMod(String str) {
        super(str);
        this.setStaffMod(true);
        this.setCategories(ModCategory.HUD);
        Collections.addAll(this.visibleBlockIds, 14, 15, 16, 15, 56, 129, 52);
        this.opacity = new Setting(this, "Opacity").changeValue(45).setRange(15, 255).setUnit(" alpha");
        this.addEventHandler(BlockIntEvent.class, this::onBlockIntEvent);
        this.addEventHandler(BlockLightValueEvent.class, this::onBlockLightValueEvent);
        this.addEventHandler(BlockRenderPassEvent.class, this::onBlockRenderPassEvent);
        this.addEventHandler(TessellatorColorOpaqueEvent.class, tessellatorColorOpaqueEvent -> tessellatorColorOpaqueEvent.setAlpha((Integer)this.opacity.getValue()));
    }

    public void onBlockLightValueEvent(BlockLightValueEvent blockLightValueEvent) {
        if (!this.shouldRenderBlock(blockLightValueEvent.getBlockId(), true)) {
            blockLightValueEvent.setLightValue(15);
        }
    }

    public List<Integer> getVisibleBlockIds() {
        return this.visibleBlockIds;
    }

    public void onBlockRenderPassEvent(BlockRenderPassEvent blockRenderPassEvent) {
        if (!this.shouldRenderBlock(blockRenderPassEvent.getBlockId(), true)) {
            blockRenderPassEvent.setCancelled(true);
        }
    }
}
