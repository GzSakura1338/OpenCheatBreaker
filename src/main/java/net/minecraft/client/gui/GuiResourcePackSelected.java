package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiResourcePackSelected extends GuiResourcePackList {
    public GuiResourcePackSelected(Minecraft mc, int n, int n2, List list) {
        super(mc, n, n2, list);
    }

    @Override
    public String getListHeader() {
        return I18n.format("resourcePack.selected.title");
    }
}
