package com.cheatbreaker.client.ui.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository.Entry;

public class ResourcePackSync {
    public Minecraft mc = Minecraft.getMinecraft();

    public ResourcePackSync() {
        HashMap map = new HashMap();

        for (String str : this.mc.gameSettings.resourcePacks) {
            map.put(str, null);
        }

        for (Entry entry : this.mc.getResourcePackRepository().getRepositoryEntries()) {
            map.put(entry.getResourcePackName(), entry);
        }

        for (Entry entry : ResourcePackScanner.getEnabledEntries()) {
            map.put(entry.getResourcePackName(), entry);
        }

        map.values().removeIf(Objects::isNull);
        this.mc.getResourcePackRepository().setSelectedResourcePacks(new ArrayList(map.values()));
        Runtime.getRuntime().addShutdownHook(new Thread(PackExecutor::shutdown));
    }
}
