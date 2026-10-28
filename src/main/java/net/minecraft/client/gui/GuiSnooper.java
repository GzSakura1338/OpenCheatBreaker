package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;

public class GuiSnooper extends GuiScreen {
    public java.util.List entryNames = new ArrayList();
    public java.util.List entryValues = new ArrayList();
    public GuiScreen parent;
    public GameSettings game_settings_2;
    public String title;
    public String[] description;
    public GuiSnooper.List list;
    public GuiButton enableButton;

    public GuiSnooper(GuiScreen guiScreen, GameSettings gameSettings) {
        this.parent = guiScreen;
        this.game_settings_2 = gameSettings;
    }

    @Override
    public void initGui() {
        this.title = I18n.format("options.snooper.title");
        String format = I18n.format("options.snooper.desc");
        ArrayList list = new ArrayList();

        for (String str : (Iterable<String>)this.fontRendererObj.listFormattedStringToWidth(format, this.width - 30)) {
            list.add(str);
        }

        this.description = (String[])list.toArray(new String[0]);
        this.entryNames.clear();
        this.entryValues.clear();
        this.buttonList
            .add(
                this.enableButton = new GuiButton(1, this.width / 2 - 152, this.height - 30, 150, 20, this.game_settings_2.getKeyBinding(Options.SNOOPER_ENABLED))
            );
        this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height - 30, 150, 20, I18n.format("gui.done")));
        boolean flag = this.mc.getIntegratedServer() != null && this.mc.getIntegratedServer().getPlayerUsageSnooper() != null;

        for (Entry entry : (Iterable<Entry>)new TreeMap(this.mc.getPlayerUsageSnooper().getCurrentStats()).entrySet()) {
            this.entryNames.add((flag ? "C " : "") + entry.getKey());
            this.entryValues.add(this.fontRendererObj.trimStringToWidth((String)entry.getValue(), this.width - 220));
        }

        if (flag) {
            for (Entry entry : (Iterable<Entry>)new TreeMap(this.mc.getIntegratedServer().getPlayerUsageSnooper().getCurrentStats()).entrySet()) {
                this.entryNames.add("S " + entry.getKey());
                this.entryValues.add(this.fontRendererObj.trimStringToWidth((String)entry.getValue(), this.width - 220));
            }
        }

        this.list = new GuiSnooper.List();
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.enabled) {
            if (button.id == 2) {
                this.game_settings_2.saveOptions();
                this.game_settings_2.saveOptions();
                this.mc.displayGuiScreen(this.parent);
            }

            if (button.id == 1) {
                this.game_settings_2.setOptionValue(Options.SNOOPER_ENABLED, 1);
                this.enableButton.displayString = this.game_settings_2.getKeyBinding(Options.SNOOPER_ENABLED);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.drawDefaultBackground();
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 8, 16777215);
        int n = 22;

        for (String str : this.description) {
            this.drawCenteredString(this.fontRendererObj, str, this.width / 2, n, 8421504);
            n += this.fontRendererObj.FONT_HEIGHT;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public class List extends GuiSlot {
        public List() {
            super(
                GuiSnooper.this.mc, GuiSnooper.this.width, GuiSnooper.this.height, 80, GuiSnooper.this.height - 40, GuiSnooper.this.fontRendererObj.FONT_HEIGHT + 1
            );
        }

        @Override
        public int getSize() {
            return GuiSnooper.this.entryNames.size();
        }

        @Override
        public void elementClicked(int n, boolean flag, int n2, int n3) {
        }

        @Override
        public boolean isSelected(int n) {
            return false;
        }

        @Override
        public void drawBackground() {
        }

        @Override
        public void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6) {
            GuiSnooper.this.fontRendererObj.drawString((String)GuiSnooper.this.entryNames.get(n), 10, n3, 16777215);
            GuiSnooper.this.fontRendererObj.drawString((String)GuiSnooper.this.entryValues.get(n), 230, n3, 16777215);
        }

        @Override
        public int getScrollBarX() {
            return this.width - 10;
        }
    }
}
