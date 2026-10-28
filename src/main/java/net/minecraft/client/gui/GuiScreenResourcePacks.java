package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;

public class GuiScreenResourcePacks extends GuiScreen {
    public static Logger logger = LogManager.getLogger();
    public GuiScreen parent;
    public List availablePackList;
    public List selectedPackList;
    public GuiResourcePackAvailable availablePacks;
    public GuiResourcePackSelected selectedPacks;

    public GuiScreenResourcePacks(GuiScreen guiScreen) {
        this.parent = guiScreen;
    }

    @Override
    public void initGui() throws IOException {
        this.buttonList.add(new GuiOptionButton(2, this.width / 2 - 154, this.height - 48, I18n.format("resourcePack.openFolder")));
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 4, this.height - 48, I18n.format("gui.done")));
        this.availablePackList = new ArrayList();
        this.selectedPackList = new ArrayList();
        ResourcePackRepository resourcePackRepository = this.mc.getResourcePackRepository();
        resourcePackRepository.updateRepositoryEntriesAll();
        ArrayList newArrayList = Lists.newArrayList(resourcePackRepository.getRepositoryEntriesAll());
        newArrayList.removeAll(resourcePackRepository.getRepositoryEntries());

        for (Entry entry : (Iterable<Entry>)newArrayList) {
            this.availablePackList.add(new ResourcePackListEntryFound(this, entry));
        }

        for (Entry entry : Lists.reverse(resourcePackRepository.getRepositoryEntries())) {
            this.selectedPackList.add(new ResourcePackListEntryFound(this, entry));
        }

        this.selectedPackList.add(new ResourcePackListEntryDefault(this));
        this.availablePacks = new GuiResourcePackAvailable(this.mc, 200, this.height, this.availablePackList);
        this.availablePacks.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
        this.availablePacks.registerScrollButtons(7, 8);
        this.selectedPacks = new GuiResourcePackSelected(this.mc, 200, this.height, this.selectedPackList);
        this.selectedPacks.setSlotXBoundsFromLeft(this.width / 2 + 4);
        this.selectedPacks.registerScrollButtons(7, 8);
    }

    public boolean hasResourcePackEntry(ResourcePackListEntry resourcePackListEntry) {
        return this.selectedPackList.contains(resourcePackListEntry);
    }

    public List getContainingList(ResourcePackListEntry resourcePackListEntry) {
        return this.hasResourcePackEntry(resourcePackListEntry) ? this.selectedPackList : this.availablePackList;
    }

    public List getAvailablePacks() {
        return this.availablePackList;
    }

    public List getSelectedPacks() {
        return this.selectedPackList;
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 2) {
                File dirResourcepacks = this.mc.getResourcePackRepository().getDirResourcepacks();
                String absolutePath = dirResourcepacks.getAbsolutePath();
                if (Util.getOSType() == EnumOS.OSX) {
                    try {
                        logger.info(absolutePath);
                        Runtime.getRuntime().exec(new String[]{"/usr/bin/open", absolutePath});
                        return;
                    } catch (IOException e) {
                        logger.error("Couldn't open file", e);
                    }
                } else if (Util.getOSType() == EnumOS.WINDOWS) {
                    String format = String.format("cmd.exe /C start \"Open file\" \"%s\"", absolutePath);

                    try {
                        Runtime.getRuntime().exec(format);
                        return;
                    } catch (IOException e) {
                        logger.error("Couldn't open file", e);
                    }
                }

                boolean flag = false;

                try {
                    Class forName = Class.forName("java.awt.Desktop");
                    Object invoke = forName.getMethod("getDesktop").invoke(null);
                    forName.getMethod("browse", URI.class).invoke(invoke, dirResourcepacks.toURI());
                } catch (Throwable e) {
                    logger.error("Couldn't open link", e);
                    flag = true;
                }

                if (flag) {
                    logger.info("Opening via system class!");
                    Sys.openURL("file://" + absolutePath);
                }
            } else if (button.id == 1) {
                ArrayList newArrayList = Lists.newArrayList();

                for (ResourcePackListEntry resourcePackListEntry : (Iterable<ResourcePackListEntry>)this.selectedPackList) {
                    if (resourcePackListEntry instanceof ResourcePackListEntryFound) {
                        newArrayList.add(((ResourcePackListEntryFound)resourcePackListEntry).getEntry());
                    }
                }

                Collections.reverse(newArrayList);
                this.mc.getResourcePackRepository().setSelectedResourcePacks(newArrayList);
                this.mc.gameSettings.resourcePacks.clear();

                for (Entry entry : (Iterable<Entry>)newArrayList) {
                    this.mc.gameSettings.resourcePacks.add(entry.getResourcePackName());
                }

                this.mc.gameSettings.saveOptions();
                this.mc.refreshResources();
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.availablePacks.mouseClicked(mouseX, mouseY, mouseButton);
        this.selectedPacks.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws IOException {
        this.drawBackground(0);
        this.availablePacks.drawScreen(mouseX, mouseY, partialTicks);
        this.selectedPacks.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.title"), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
