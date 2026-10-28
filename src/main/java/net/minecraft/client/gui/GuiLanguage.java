package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;

public class GuiLanguage extends GuiScreen {
    public GuiScreen parent;
    public GuiLanguage.List list;
    public GameSettings game_settings_3;
    public LanguageManager languageManager;
    public GuiOptionButton forceUnicodeButton;
    public GuiOptionButton doneButton;

    public GuiLanguage(GuiScreen guiScreen, GameSettings gameSettings, LanguageManager languageManager) {
        this.parent = guiScreen;
        this.game_settings_3 = gameSettings;
        this.languageManager = languageManager;
    }

    @Override
    public void initGui() {
        boolean flag = false;
        if (this.forceUnicodeButton != null) {
        }

        this.buttonList
            .add(
                this.forceUnicodeButton = new GuiOptionButton(
                    100, this.width / 2 - 155, this.height - 38, Options.FORCE_UNICODE_FONT, this.game_settings_3.getKeyBinding(Options.FORCE_UNICODE_FONT)
                )
            );
        this.buttonList.add(this.doneButton = new GuiOptionButton(6, this.width / 2 - 155 + 160, this.height - 38, I18n.format("gui.done")));
        this.list = new GuiLanguage.List();
        this.list.registerScrollButtons(7, 8);
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.enabled) {
            switch (button.id) {
                case 5:
                    break;
                case 6:
                    this.mc.displayGuiScreen(this.parent);
                    break;
                case 100:
                    if (button instanceof GuiOptionButton) {
                        this.game_settings_3.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
                        button.displayString = this.game_settings_3.getKeyBinding(Options.FORCE_UNICODE_FONT);
                        ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
                        int scaledWidth = scaledResolution.getScaledWidth();
                        int scaledHeight = scaledResolution.getScaledHeight();
                        this.setWorldAndResolution(this.mc, scaledWidth, scaledHeight);
                    }
                    break;
                default:
                    this.list.actionPerformed(button);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, I18n.format("options.language"), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.fontRendererObj, "(" + I18n.format("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public class List extends GuiSlot {
        public java.util.List list = Lists.newArrayList();
        public Map map = Maps.newHashMap();

        public List() {
            super(GuiLanguage.this.mc, GuiLanguage.this.width, GuiLanguage.this.height, 32, GuiLanguage.this.height - 65 + 4, 18);

            for (Language language : (Iterable<Language>)GuiLanguage.this.languageManager.getLanguages()) {
                this.map.put(language.getLanguageCode(), language);
                this.list.add(language.getLanguageCode());
            }
        }

        @Override
        public int getSize() {
            return this.list.size();
        }

        @Override
        public void elementClicked(int n, boolean flag, int n2, int n3) {
            Language language = (Language)this.map.get(this.list.get(n));
            GuiLanguage.this.languageManager.setCurrentLanguage(language);
            GuiLanguage.this.game_settings_3.language = language.getLanguageCode();
            GuiLanguage.this.mc.refreshResources();
            GuiLanguage.this.fontRendererObj
                .setUnicodeFlag(GuiLanguage.this.languageManager.isCurrentLocaleUnicode() || GuiLanguage.this.game_settings_3.forceUnicodeFont);
            GuiLanguage.this.fontRendererObj.setBidiFlag(GuiLanguage.this.languageManager.isCurrentLanguageBidirectional());
            GuiLanguage.this.doneButton.displayString = I18n.format("gui.done");
            GuiLanguage.this.forceUnicodeButton.displayString = GuiLanguage.this.game_settings_3.getKeyBinding(Options.FORCE_UNICODE_FONT);
            GuiLanguage.this.game_settings_3.saveOptions();
        }

        @Override
        public boolean isSelected(int n) {
            return this.list.get(n).equals(GuiLanguage.this.languageManager.getCurrentLanguage().getLanguageCode());
        }

        @Override
        public int getContentHeight() {
            return this.getSize() * 18;
        }

        @Override
        public void drawBackground() {
            GuiLanguage.this.drawDefaultBackground();
        }

        @Override
        public void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6) {
            GuiLanguage.this.fontRendererObj.setBidiFlag(true);
            GuiLanguage.this.drawCenteredString(GuiLanguage.this.fontRendererObj, this.map.get(this.list.get(n)).toString(), this.width / 2, n3 + 1, 16777215);
            GuiLanguage.this.fontRendererObj.setBidiFlag(GuiLanguage.this.languageManager.getCurrentLanguage().isBidirectional());
        }
    }
}
