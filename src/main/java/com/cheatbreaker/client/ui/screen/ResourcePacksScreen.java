package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.util.FileOpener;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Keyboard;

public class ResourcePacksScreen extends GuiScreen {
    public GuiScreen guiScreen;
    public GuiButton redString;
    public GuiButton resourcepackOpenfolder;
    public ResourcePackRepository resourcePackRepository;
    public static Comparator<Entry> ENTRY_COMPARATOR = (entry, entry2) -> StringUtils.stripControlCodes(entry.getResourcePackName())
        .trim()
        .compareToIgnoreCase(StringUtils.stripControlCodes(entry2.getResourcePackName()).trim());
    public List<Entry> availableEntries;
    public GuiButton background;
    public List<Entry> searchResults;
    public PackFolder packFolder;
    public boolean needsRefresh;
    public List<Entry> selectedEntries;
    public GuiButton guiDone;
    public SelectedPacksList selectedPacksList;
    public GuiTextField guiTextField;
    public AvailablePacksList availablePacksList;
    public static Comparator<PackFolder> FOLDER_COMPARATOR = (packFolder, packFolder2) -> StringUtils.stripControlCodes(packFolder.getName())
        .trim()
        .compareToIgnoreCase(StringUtils.stripControlCodes(packFolder2.getName()).trim());
    public GuiButton clearGlass;
    public List<Entry> initialSelected;
    public ResourcePackWatcher resourcePackWatcher;
    public List<PackFolder> folders;

    @Override
    public void drawScreen(int n, int n2, float f) throws IOException {
        if (this.needsRefresh) {
            this.needsRefresh = false;
        }

        if (this.mc.theWorld != null && (Boolean)CheatBreaker.getInstance().getKeybindSettings().transparentBackground.getValue()) {
            this.drawDefaultBackground();
        } else {
            this.drawBackground(0);
        }

        this.availablePacksList.drawScreen(n, n2);
        this.selectedPacksList.drawScreen(n, n2);
        floatValue = 0.0F;
        boolean booleanValue = CheatBreaker.getInstance().getKeybindSettings().widePackMenu.getBooleanValue();
        if (this.availablePacksList.entryList.isEmpty()) {
            String str = !this.guiTextField.getText().isEmpty() && this.searchResults.isEmpty() ? "No resource packs found." : "";
            if (this.needsRefresh) {
                str = "Discovering resource packs...";
            }

            this.drawCenteredString(
                this.fontRendererObj, EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + str, booleanValue ? this.width / 4 : this.width / 2 - 100, 60, 16777215
            );
        }

        if (this.selectedPacksList.entryList.isEmpty()) {
            String str = "Select resource packs.";
            this.drawCenteredString(
                this.fontRendererObj,
                EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + str,
                booleanValue ? this.width / 2 + this.width / 4 : this.width / 2 + 100,
                60,
                16777215
            );
        }

        super.drawCenteredString(
            this.mc.fontRendererObj,
            this.selectedPacksList.title,
            this.selectedPacksList.left + this.selectedPacksList.width / 2,
            this.selectedPacksList.top - 14,
            -1
        );
        super.drawCenteredString(
            this.mc.fontRendererObj,
            this.availablePacksList.title,
            this.availablePacksList.left + this.availablePacksList.width / 2,
            this.availablePacksList.top - 14,
            -1
        );
        this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
        super.drawScreen(n, n2, f);
        this.guiTextField.drawTextBox();
        if (this.guiTextField.getVisible() && !this.guiTextField.isFocused() && this.guiTextField.getText().isEmpty()) {
            super.drawString(
                this.fontRendererObj,
                EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + "Search...",
                (boolean)CheatBreaker.getInstance().getKeybindSettings().widePackMenu.getValue() ? 14 : this.width / 2 - 200,
                this.height - 65,
                16777215
            );
        }
    }

    public void sortFolders(String str) {
        if (str.equals("A-Z")) {
            this.folders.sort(FOLDER_COMPARATOR);
        } else if (str.equals("Z-A")) {
            this.folders.sort(FOLDER_COMPARATOR.reversed());
        }
    }

    public void search(String str) {
        if (str.isEmpty()) {
            this.openFolder(null);
        } else {
            this.searchResults.clear();

            for (Entry entry : this.availableEntries) {
                if (StringUtils.stripControlCodes(entry.getResourcePackName()).toLowerCase().contains(str)
                    || StringUtils.stripControlCodes(entry.getTexturePackDescription()).toLowerCase().contains(str)) {
                    this.addSearchResult(entry);
                }
            }

            for (PackFolder packFolder : this.folders) {
                this.searchFolder(packFolder, str);
            }

            if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().widePackMenu.getValue()) {
                this.availablePacksList = new AvailablePacksList(this, 10, 32, (this.width - 30) / 2, this.height - 80 - 26, 36, this.searchResults, null, false);
            } else {
                this.availablePacksList = new AvailablePacksList(this, this.width / 2 - 204, 32, 200, this.height - 80 - 26, 36, this.searchResults, null, false);
            }

            this.sortEntries(this.searchResults);
        }
    }

    public ResourcePacksScreen(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
        this.resourcePackRepository = guiScreen.mc.getResourcePackRepository();
        this.availableEntries = new ArrayList<>();
        this.searchResults = new ArrayList<>();
        this.selectedEntries = new ArrayList<>(Lists.reverse(this.resourcePackRepository.getRepositoryEntries()));
        this.initialSelected = Collections.unmodifiableList(new ArrayList<>(this.selectedEntries));
        this.folders = new ArrayList<>();
        PackExecutor.execute(() -> {
            this.resourcePackRepository.updateRepositoryEntriesAll();
            this.refreshPacks();
            this.resourcePackWatcher = new ResourcePackWatcher(this.resourcePackRepository.getDirResourcepacks().toPath());
            this.needsRefresh = true;
        });
    }

    public List<PackFolder> getFolders() {
        return Collections.unmodifiableList(this.folders);
    }

    public void addAvailable(Entry entry) {
        if (!this.availableEntries.contains(entry) && this.packFolder == null) {
            this.availableEntries.add(entry);
            this.sortEntries(this.availableEntries);
        }
    }

    public void sortEntries(List<Entry> list) {
        String value = (String)CheatBreaker.getInstance().getKeybindSettings().sortMethod.getValue();
        if (value.equals("A-Z")) {
            list.sort(ENTRY_COMPARATOR);
        } else if (value.equals("Z-A")) {
            list.sort(ENTRY_COMPARATOR.reversed());
        }
    }

    @Override
    public void actionPerformed(GuiButton guiButton) throws IOException {
        if (guiButton.id == this.resourcepackOpenfolder.id) {
            try {
                FileOpener.openFile(this.resourcePackRepository.getDirResourcepacks());
            } catch (IOException e) {
                CheatBreaker.getInstance().getLogger().error("Failed to open file", e);
            }
        } else if (guiButton.id == this.guiDone.id) {
            if (!this.initialSelected.equals(this.selectedPacksList.unmodifiableList())) {
                ArrayList list = new ArrayList(Lists.reverse(this.selectedPacksList.unmodifiableList()));
                this.resourcePackRepository.setSelectedResourcePacks(list);
                this.mc.gameSettings.resourcePacks.clear();

                for (Entry entry : (Iterable<Entry>)list) {
                    this.mc.gameSettings.resourcePacks.add(entry.getResourcePackName());
                }

                this.mc.gameSettings.saveOptions();
                this.mc.refreshResources();
            }

            this.mc.displayGuiScreen(this.guiScreen);
        } else if (CheatBreaker.getInstance().getModManager().packTweaksMod.isEnabled() && guiButton.id == this.clearGlass.id) {
            Setting clearGlass = CheatBreaker.getInstance().getModManager().packTweaksMod.clearGlass;

            for (int i = 0; i < clearGlass.getStringValues().length; i++) {
                if (clearGlass.getStringValues()[i].toLowerCase().equalsIgnoreCase(clearGlass.getStringValue())) {
                    if (i + 1 >= clearGlass.getStringValues().length) {
                        clearGlass.changeValue(clearGlass.getStringValues()[0]);
                    } else {
                        clearGlass.changeValue(clearGlass.getStringValues()[i + 1]);
                        clearGlass.changeValue(clearGlass.getStringValues()[i + 1]);
                    }
                    break;
                }
            }

            this.clearGlass.displayString = "Clear Glass: " + clearGlass.getValue();
            this.mc.renderGlobal.loadRenderers();
        } else if (CheatBreaker.getInstance().getModManager().packTweaksMod.isEnabled() && guiButton.id == this.redString.id) {
            boolean booleanValue = CheatBreaker.getInstance().getModManager().packTweaksMod.colorPlacedString.getBooleanValue();
            this.redString.displayString = "Red String: " + (!booleanValue ? "ON" : "OFF");
            CheatBreaker.getInstance().getModManager().packTweaksMod.colorPlacedString.changeValue(!booleanValue);
            this.mc.renderGlobal.loadRenderers();
        } else if (guiButton.id == this.background.id) {
            boolean booleanValue = CheatBreaker.getInstance().getKeybindSettings().transparentBackground.getBooleanValue();
            this.background.displayString = "Background: " + (!booleanValue ? "TRANSPARENT" : "NORMAL");
            CheatBreaker.getInstance().getKeybindSettings().transparentBackground.changeValue(!booleanValue);
            this.mc.renderGlobal.loadRenderers();
        }
    }

    @Override
    public void keyTyped(char c, int n) throws IOException {
        super.keyTyped(c, n);
        if (this.guiTextField.isFocused() && this.guiTextField.textboxKeyTyped(c, n)) {
            this.search(this.guiTextField.getText().toLowerCase());
        } else {
            if ((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && Keyboard.isKeyDown(33) && !this.guiTextField.isFocused()) {
                this.guiTextField.setFocused(true);
            }
        }
    }

    public boolean isSearching() {
        return !this.guiTextField.getText().isEmpty();
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws IOException {
        super.mouseClicked(n, n2, n3);
        if (this.guiTextField.getVisible()) {
            this.guiTextField.mouseClicked(n, n2, n3);
        }
    }

    @Override
    public void initGui() {
        int n = (this.width - 30) / 2;
        int n2 = this.height - 80 - 46;
        boolean booleanValue = CheatBreaker.getInstance().getKeybindSettings().widePackMenu.getBooleanValue();
        int n3 = booleanValue ? this.width - n - 12 : this.width / 2 + 4;
        int n4 = booleanValue ? n + 2 : 200;
        if (booleanValue) {
            int n5 = n + 2;
        } else {
            short s = 200;
        }

        this.openFolder(this.packFolder);
        this.selectedPacksList = new SelectedPacksList(
            this,
            n3,
            32,
            n4,
            n2 + ((!booleanValue || this.width / 2 <= 338) && CheatBreaker.getInstance().getModManager().packTweaksMod.isEnabled() ? 0 : 20),
            36,
            this.selectedEntries
        );
        this.resourcepackOpenfolder = new GuiButton(0, this.width / 2 - 154, this.height - 48, 150, 20, I18n.format("resourcePack.openFolder"));
        this.buttonList.add(this.resourcepackOpenfolder);
        this.guiDone = new GuiButton(2, n3, this.height - 48, 200, 20, I18n.format("gui.done"));
        this.buttonList.add(this.guiDone);
        int n5 = this.selectedPacksList.left
            + this.selectedPacksList.width / 2
            - this.fontRendererObj.getStringWidth(this.selectedPacksList.title) / 2
            - (this.availablePacksList.left + this.availablePacksList.width / 2 + this.fontRendererObj.getStringWidth(this.availablePacksList.title) / 2)
            - 50;
        if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().widePackMenu.getValue()) {
            this.guiTextField = new GuiTextField(this.fontRendererObj, 10, this.height - 70, (this.width - 30) / 2, 18);
        } else {
            this.guiTextField = new GuiTextField(this.fontRendererObj, this.width / 2 - 204, this.height - 70, 200, 18);
        }

        this.guiTextField.setVisible(this.packFolder == null && (Boolean)CheatBreaker.getInstance().getKeybindSettings().showSearchBar.getValue());
        if (CheatBreaker.getInstance().getModManager().packTweaksMod.isEnabled()) {
            this.clearGlass = new GuiOptionButton(
                100,
                this.width / 2 + 4,
                this.height - 71,
                114,
                20,
                "Clear Glass: " + CheatBreaker.getInstance().getModManager().packTweaksMod.clearGlass.getValue()
            );
            this.buttonList.add(this.clearGlass);
            this.redString = new GuiOptionButton(
                101,
                this.width / 2 + 118,
                this.height - 71,
                86,
                20,
                "Red String: " + ((boolean)CheatBreaker.getInstance().getModManager().packTweaksMod.colorPlacedString.getValue() ? "ON" : "OFF")
            );
            this.buttonList.add(this.redString);
            this.background = new GuiOptionButton(
                102,
                this.width / 2 + (booleanValue && this.width / 2 > 338 ? 204 : 4),
                this.height - (booleanValue && this.width / 2 > 338 ? 71 : 91),
                booleanValue && this.width / 2 > 338 ? 138 : 200,
                20,
                "Background: " + ((boolean)CheatBreaker.getInstance().getKeybindSettings().transparentBackground.getValue() ? "TRANSPARENT" : "NORMAL")
            );
        } else {
            this.background = new GuiOptionButton(
                102,
                n3,
                this.height - 71,
                booleanValue && this.width / 2 > 338 ? (this.width - 24) / 2 : 200,
                20,
                "Background: " + ((boolean)CheatBreaker.getInstance().getKeybindSettings().transparentBackground.getValue() ? "TRANSPARENT" : "NORMAL")
            );
        }

        this.buttonList.add(this.background);
    }

    public boolean addSelected(Entry entry) {
        if (!this.selectedEntries.contains(entry)) {
            this.selectedEntries.add(0, entry);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.availablePacksList.handleMouseInput();
        this.selectedPacksList.handleMouseInput();
    }

    @Override
    public void updateScreen() {
        if (this.resourcePackWatcher != null && this.resourcePackWatcher.pollChanges()) {
            PackExecutor.execute(this::refreshPacks);
            this.openFolder(null);
        }

        this.guiTextField.updateCursorCounter();
    }

    public void refreshPacks() {
        ArrayList list = new ArrayList();

        for (File file : ResourcePackScanner.listFiles(this.resourcePackRepository.getDirResourcepacks(), ResourcePackScanner::checkFile)) {
            list.add(new PackFolder(file, this.selectedEntries));
        }

        Minecraft.getMinecraft().addScheduledTask(() -> {
            this.folders.clear();
            this.folders.addAll(list);
            this.sortFolders((String)CheatBreaker.getInstance().getKeybindSettings().sortMethod.getValue());
        });
    }

    public void searchFolder(PackFolder packFolder, String str) {
        for (Entry entry : packFolder.getEntryList()) {
            if (StringUtils.stripControlCodes(entry.getResourcePackName().toLowerCase()).contains(str)
                || StringUtils.stripControlCodes(entry.getTexturePackDescription()).toLowerCase().contains(str)) {
                this.addSearchResult(entry);
            }
        }

        for (PackFolder packFolder2 : packFolder.getPackFolderList()) {
            this.searchFolder(packFolder2, str);
        }
    }

    public void addSearchResult(Entry entry) {
        boolean flag = false;

        for (Entry entry2 : this.searchResults) {
            if (entry2.getResourcePackName().equals(entry.getResourcePackName())) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            this.searchResults.add(entry);
            this.sortEntries(this.searchResults);
        }
    }

    public void openFolder(PackFolder packFolder) {
        this.packFolder = packFolder;
        int n = (boolean)CheatBreaker.getInstance().getKeybindSettings().showSearchBar.getValue() ? 26 : 4;
        if (packFolder == null) {
            if (this.guiTextField != null) {
                this.guiTextField.setVisible((Boolean)CheatBreaker.getInstance().getKeybindSettings().showSearchBar.getValue());
            }

            this.availableEntries = new ArrayList<>(this.resourcePackRepository.getRepositoryEntriesAll());
            this.availableEntries.removeAll(this.selectedEntries);
            if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().widePackMenu.getValue()) {
                this.availablePacksList = new AvailablePacksList(
                    this, 10, 32, (this.width - 30) / 2, this.height - 80 - n, 36, this.availableEntries, this.folders, true
                );
            } else {
                this.availablePacksList = new AvailablePacksList(
                    this, this.width / 2 - 204, 32, 200, this.height - 80 - n, 36, this.availableEntries, this.folders, true
                );
            }
        } else {
            this.availableEntries = packFolder.getEntryList();
            if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().widePackMenu.getValue()) {
                this.availablePacksList = new AvailablePacksList(
                    this, 10, 32, (this.width - 30) / 2, this.height - 80 - n, 36, this.availableEntries, packFolder.getPackFolderList(), true
                );
            } else {
                this.availablePacksList = new AvailablePacksList(
                    this, this.width / 2 - 204, 32, 200, this.height - 80 - n, 36, this.availableEntries, packFolder.getPackFolderList(), true
                );
            }
        }

        this.sortEntries(this.availableEntries);
    }
}
