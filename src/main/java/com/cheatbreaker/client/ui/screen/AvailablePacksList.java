package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.util.PackTextUtils;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;

public class AvailablePacksList extends PackList {
    public List<PackFolder> packFolderList;
    public boolean showFolders;
    public static ResourceLocation folderPng = new ResourceLocation("client/icons/folder.png");

    public void handleEntry(Entry entry) {
        if (this.resourcePacksScreen.addSelected(entry)) {
            this.entryList.remove(entry);
        }
    }

    @Override
    public int getSize() {
        return this.showFolders ? this.entryList.size() + this.packFolderList.size() : this.entryList.size();
    }

    @Override
    public void drawSlot(int n, int n2, int n3, int n4, int n5, int n6, boolean flag) {
        if (flag) {
            Gui.drawRect(this.left, n3 - 1, n2 + 1, n3 + n4 + 3, -2134851392);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (this.showFolders) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            if (0 <= n && n < this.packFolderList.size()) {
                this.drawPackFolder(this.packFolderList.get(n), n3);
            } else if (this.packFolderList.size() <= n && n < this.getSize()) {
                super.drawSlot(n - this.packFolderList.size(), n2, n3, n4, n5, n6, flag);
            }
        } else if (0 <= n && n < this.getSize()) {
            super.drawSlot(n, n2, n3, n4, n5, n6, flag);
        }
    }

    @Override
    public void elementClicked(int n, boolean flag) {
        if (this.showFolders) {
            if (0 <= n && n < this.packFolderList.size()) {
                this.openPackFolder(this.packFolderList.get(n), n == 0);
            } else if (this.packFolderList.size() <= n && n < this.getSize()) {
                this.handleEntry(this.entryList.get(n - this.packFolderList.size()));
            }
        } else if (0 <= n && n < this.getSize()) {
            this.handleEntry(this.entryList.get(n));
        }
    }

    public AvailablePacksList(ResourcePacksScreen resourcePacksScreen, int n, int n2, int n3, int n4, int n5, List<Entry> list, List<PackFolder> list2, boolean flag) {
        super(resourcePacksScreen, n, n2, n3, n4, n5, I18n.format("resourcePack.available.title"), list);
        this.packFolderList = list2;
        this.showFolders = flag;
    }

    public void openPackFolder(PackFolder packFolder, boolean flag) {
        if (flag) {
            if (packFolder.getName().equals("Back to Main Folder")) {
                this.resourcePacksScreen.openFolder(null);
            } else if (packFolder.getPackFolder() != null) {
                this.resourcePacksScreen.openFolder(packFolder.getPackFolder());
            } else {
                this.resourcePacksScreen.openFolder(packFolder);
            }
        } else {
            this.resourcePacksScreen.openFolder(packFolder);
        }
    }

    public void drawPackFolder(PackFolder packFolder, int n) {
        boolean value = (Boolean)CheatBreaker.getInstance().getKeybindSettings().showPackFolderIcons.getValue();
        if (value) {
            this.mc2.getTextureManager().bindTexture(folderPng);
            GlStateManager.enableBlend();
            Gui.drawScaledCustomSizeModalRect(this.left + 2, n + 3, 0.0F, 0.0F, 256, 256, 32, 32, 256.0F, 256.0F);
            GlStateManager.disableBlend();
        }

        this.mc2
            .fontRendererObj
            .drawString(PackTextUtils.trimPackName(packFolder.getName(), this.width - 46), this.left + (value ? 36.0F : 2.0F), n + 2.0F, -1, true);
        int n2 = packFolder.getPackFolderList().size() - 1;
        if (n2 != -1 && (Boolean)CheatBreaker.getInstance().getKeybindSettings().showPackFolderInformation.getValue()) {
            float f = n + 13.0F;
            if (n2 != 0) {
                String trimPackName = PackTextUtils.trimPackName(n2 + (n2 == 1 ? " Subfolder" : " Subfolders"), this.width - 46);
                this.mc2.fontRendererObj.drawString(trimPackName, this.left + (value ? 36.0F : 2.0F), f, -5592406, true);
                f += 10.0F;
            }

            int n3;
            if ((n3 = packFolder.getEntryList().size()) != 0) {
                String trimPackName = PackTextUtils.trimPackName(n3 + (n3 == 1 ? " Pack" : " Packs"), this.width - 46);
                this.mc2.fontRendererObj.drawString(trimPackName, this.left + (value ? 36.0F : 2.0F), f, -5592406, true);
            }
        }
    }
}
