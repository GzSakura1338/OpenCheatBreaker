package net.minecraft.client.resources;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class ResourcePackListEntry implements IGuiListEntry {
    public static ResourceLocation RESOURCE_PACKS_TEX = new ResourceLocation("textures/gui/resource_packs.png");
    public GuiScreenResourcePacks screen;
    public Minecraft client;

    public ResourcePackListEntry(GuiScreenResourcePacks guiScreenResourcePacks) {
        this.screen = guiScreenResourcePacks;
        this.client = Minecraft.getMinecraft();
    }

    @Override
    public void drawEntry(int n, int n2, int n3, int n4, int n5, Tessellator tessellator, int n6, int n7, boolean flag) {
        this.run();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(n2, n3, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        if ((this.client.gameSettings.touchscreen || flag) && this.alwaysTrue()) {
            this.client.getTextureManager().bindTexture(RESOURCE_PACKS_TEX);
            Gui.drawRect(n2, n3, n2 + 32, n3 + 32, -1601138544);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int n8 = n6 - n2;
            int n9 = n7 - n3;
            if (this.hasResourcePackEntry()) {
                if (n8 < 32) {
                    Gui.drawModalRectWithCustomSizedTexture(n2, n3, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(n2, n3, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            } else {
                if (this.hasResourcePackEntry2()) {
                    if (n8 < 16) {
                        Gui.drawModalRectWithCustomSizedTexture(n2, n3, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(n2, n3, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.canSortUp()) {
                    if (n8 < 32 && n8 > 16 && n9 < 16) {
                        Gui.drawModalRectWithCustomSizedTexture(n2, n3, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(n2, n3, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.check()) {
                    if (n8 < 32 && n8 > 16 && n9 > 16) {
                        Gui.drawModalRectWithCustomSizedTexture(n2, n3, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(n2, n3, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }
            }
        }

        String resourcePackName = this.getResourcePackName();
        int stringWidth = this.client.fontRendererObj.getStringWidth(resourcePackName);
        if (stringWidth > 157) {
            resourcePackName = this.client.fontRendererObj.trimStringToWidth(resourcePackName, 157 - this.client.fontRendererObj.getStringWidth("...")) + "...";
        }

        this.client.fontRendererObj.drawShadowedString(resourcePackName, n2 + 32 + 2, n3 + 1, 16777215);
        List listFormattedStringToWidth = this.client.fontRendererObj.listFormattedStringToWidth(this.createStringBuilder(), 157);

        for (int i = 0; i < 2 && i < listFormattedStringToWidth.size(); i++) {
            this.client.fontRendererObj.drawShadowedString((String)listFormattedStringToWidth.get(i), n2 + 32 + 2, n3 + 12 + 10 * i, 8421504);
        }
    }

    public abstract String createStringBuilder();

    public abstract String getResourcePackName();

    public abstract void run();

    public boolean alwaysTrue() {
        return true;
    }

    public boolean hasResourcePackEntry() {
        return !this.screen.hasResourcePackEntry(this);
    }

    public boolean hasResourcePackEntry2() {
        return this.screen.hasResourcePackEntry(this);
    }

    public boolean canSortUp() {
        List containingList = this.screen.getContainingList(this);
        int indexOf = containingList.indexOf(this);
        return indexOf > 0 && ((ResourcePackListEntry)containingList.get(indexOf - 1)).alwaysTrue();
    }

    public boolean check() {
        List containingList = this.screen.getContainingList(this);
        int indexOf = containingList.indexOf(this);
        return indexOf >= 0 && indexOf < containingList.size() - 1 && ((ResourcePackListEntry)containingList.get(indexOf + 1)).alwaysTrue();
    }

    @Override
    public boolean mousePressed(int n, int n2, int n3, int n4, int n5, int n6) {
        if (this.alwaysTrue() && n5 <= 32) {
            if (this.hasResourcePackEntry()) {
                this.screen.getContainingList(this).remove(this);
                this.screen.getSelectedPacks().add(0, this);
                return true;
            }

            if (n5 < 16 && this.hasResourcePackEntry2()) {
                this.screen.getContainingList(this).remove(this);
                this.screen.getAvailablePacks().add(0, this);
                return true;
            }

            if (n5 > 16 && n6 < 16 && this.canSortUp()) {
                List containingList = this.screen.getContainingList(this);
                int indexOf = containingList.indexOf(this);
                containingList.remove(this);
                containingList.add(indexOf - 1, this);
                return true;
            }

            if (n5 > 16 && n6 > 16 && this.check()) {
                List containingList = this.screen.getContainingList(this);
                int indexOf = containingList.indexOf(this);
                containingList.remove(this);
                containingList.add(indexOf + 1, this);
                return true;
            }
        }

        return false;
    }

    @Override
    public void mouseReleased(int n, int n2, int n3, int n4, int n5, int n6) {
    }
}
