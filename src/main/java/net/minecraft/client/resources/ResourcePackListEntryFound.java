package net.minecraft.client.resources;

import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.ResourcePackRepository.Entry;

public class ResourcePackListEntryFound extends ResourcePackListEntry {
    public Entry entry;

    public ResourcePackListEntryFound(GuiScreenResourcePacks guiScreenResourcePacks, Entry entry) {
        super(guiScreenResourcePacks);
        this.entry = entry;
    }

    @Override
    public void run() {
        this.entry.bindTexturePackIcon(this.client.getTextureManager());
    }

    @Override
    public String createStringBuilder() {
        return this.entry.getTexturePackDescription();
    }

    @Override
    public String getResourcePackName() {
        return this.entry.getResourcePackName();
    }

    public Entry getEntry() {
        return this.entry;
    }
}
