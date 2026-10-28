package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.util.EnumChatFormatting;

public abstract class GuiResourcePackList extends GuiListExtended {
    public Minecraft mc;
    public List widgets;

    public GuiResourcePackList(Minecraft mc, int n, int n2, List list) {
        super(mc, n, n2, 32, n2 - 55 + 4, 36);
        this.mc = mc;
        this.widgets = list;
        this.centerListVertically = false;
        this.setHasListHeader(true, (int)(mc.fontRendererObj.FONT_HEIGHT * 1.5F));
    }

    @Override
    public void drawListHeader(int n, int n2, Tessellator tessellator) {
        String str = EnumChatFormatting.UNDERLINE + "" + EnumChatFormatting.BOLD + this.getListHeader();
        this.mc
            .fontRendererObj
            .drawString(str, n + this.width / 2 - this.mc.fontRendererObj.getStringWidth(str) / 2, Math.min(this.top + 3, n2), 16777215);
    }

    public abstract String getListHeader();

    public List getList() {
        return this.widgets;
    }

    @Override
    public int getSize() {
        return this.getList().size();
    }

    public ResourcePackListEntry getListEntry(int n) {
        return (ResourcePackListEntry)this.getList().get(n);
    }

    @Override
    public int getListWidth() {
        return this.width;
    }

    @Override
    public int getScrollBarX() {
        return this.right - 6;
    }
}
