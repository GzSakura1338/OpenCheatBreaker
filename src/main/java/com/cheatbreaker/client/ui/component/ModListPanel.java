package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.font.CustomFontRenderer;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.render.RenderUtils;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class ModListPanel extends Panel {
   public List<ModListItem> modListItemList = new ArrayList<>();
   public List<ModListItem> modListItemList2 = new ArrayList<>();
   public boolean flag4 = false;
   public CustomFontRenderer customFontRenderer = CheatBreaker.getInstance().getFontManager().playBold16;
   public Button all = new Button(
      this.customFontRenderer,
      null,
      "All",
      this.x + 4,
      this.y + this.height - 20,
      this.x + this.width - 4,
      this.y + this.height - 6,
      this.theme2.getNormalHighlightColorValue(),
      1.0F,
      false,
      true
   );
   public Button hud = new Button(
      this.customFontRenderer,
      null,
      "HUD",
      this.x + 4,
      this.y + this.height - 20,
      this.x + this.width - 4,
      this.y + this.height - 6,
      this.theme2.getNormalHighlightColorValue(),
      1.0F,
      false,
      true
   );
   public Button combat = new Button(
      this.customFontRenderer,
      null,
      "Combat",
      this.x + 4,
      this.y + this.height - 20,
      this.x + this.width - 4,
      this.y + this.height - 6,
      this.theme2.getNormalHighlightColorValue(),
      1.0F,
      false,
      true
   );
   public Button server = new Button(
      this.customFontRenderer,
      null,
      "Server",
      this.x + 4,
      this.y + this.height - 20,
      this.x + this.width - 4,
      this.y + this.height - 6,
      this.theme2.getNormalHighlightColorValue(),
      1.0F,
      false,
      true
   );
   public Button misc = new Button(
      this.customFontRenderer,
      null,
      "Misc",
      this.x + 4,
      this.y + this.height - 20,
      this.x + this.width - 4,
      this.y + this.height - 6,
      this.theme2.getNormalHighlightColorValue(),
      1.0F,
      false,
      true
   );
   public List<Button> buttonList = ImmutableList.of(this.all, this.hud, this.combat, this.server, this.misc);
   public SettingsNavigationItem cheatbreakerSettings;
   public TextField searchMods;
   public Button button;
   public Button cogSearch64Png;
   public Button compact64Png;

   public boolean checkList(List<String> list, String str) {
      if (list == null) {
         return false;
      }

      for (String str2 : list) {
         if (str2.toLowerCase().contains(str.toLowerCase())) {
            return true;
         }
      }

      return false;
   }

   public boolean checkString(String str) {
      if (str != null && str.length() != 0) {
         List allSettings = CheatBreaker.getInstance().getKeybindSettings().allSettings;
         return allSettings.stream()
            .anyMatch(
               obj -> !CheatBreaker.getInstance().getKeybindSettings().bypassCustomizationLevel.getBooleanValue() && this.isHiddenByCustomizationLevel((Setting)obj)
                  ? false
                  : ((Setting)obj).getName().toLowerCase().contains(str.toLowerCase()) && !(obj instanceof SettingGroup)
            );
      } else {
         return false;
      }
   }

   @Override
   public void keyTyped(char c, int n) {
      if (this.searchMods.isFocused()) {
         this.searchMods.keyTyped(c, n);
         this.process(true);
      } else {
         if ((Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) && Keyboard.isKeyDown(33) && !this.searchMods.isFocused()) {
            this.searchMods.setFocused(true);
         }
      }
   }

   @Override
   public void render(int n, int n2, float f) {
      super.render(n, n2, f);
      this.searchMods.enabledColor = this.theme2.getPrimaryTextColorValue();
      this.searchMods.backgroundColor = this.theme2.getButtonBackgroundColorValue();
      boolean flag = CheatBreaker.getInstance().getKeybindSettings().modList.getValue().equals("Compact");
      int n3 = flag ? 20 : 112;
      byte b = 17;
      RenderUtils.drawRoundedRectFilledPrecise(this.x, this.y, this.x + this.width, this.y + this.height + 2, 8.0, this.theme2.getMenuBackgroundColorValue());
      this.process2(n, n2);
      int n4 = 0;
      int n5 = 0;
      byte b2 = 0;

      for (ModListItem modListItem : this.modListItemList) {
         modListItem.scrollOffsetY = this.intValue;
         modListItem.setBounds(this.x + 4 + n4 * 120, this.y + 4 + n5 * n3 + b, 116, flag ? 16 : 108);
         modListItem.render(n, n2, f);
         if (flag) {
            b2 = 18;
         } else {
            b2 = 112;
         }

         if (++n4 == 3) {
            n5++;
            b2 = 0;
            n4 = 0;
         }
      }

      this.intValue2 = 4 + n5 * n3 + b + b2;
      if (this.flag4) {
         this.cheatbreakerSettings.setBounds(this.x + 4, this.y + this.intValue2, this.width - 12, 18);
         this.cheatbreakerSettings.scrollOffsetY = this.intValue;
         this.cheatbreakerSettings.render(n, n2, f);
         this.intValue2 = this.intValue2 + this.cheatbreakerSettings.getHeight();
      }

      CustomFontRenderer playBold18 = CheatBreaker.getInstance().getFontManager().playBold18;
      this.compact64Png.setBounds(this.x + 4, this.y + 5, 12, 12);
      this.compact64Png.setSmallIcon(true);
      this.compact64Png.setSelected(flag);
      this.compact64Png.render(n, n2, f);
      this.compact64Png.scrollOffsetY = this.intValue;
      int n6 = 20;

      for (Button button : this.buttonList) {
         button.setBounds(this.x + n6, this.y + 5, playBold18.getStringWidth(button.label) + 8, 12);
         button.scrollOffsetY = this.intValue;
         button.render(n, n2, f);
         n6 += playBold18.getStringWidth(button.label) + 12;
      }

      this.button.setBounds(this.x + n6, this.y + 5, 12, 12);
      this.button.label = this.getText();
      this.button.scrollOffsetY = this.intValue;
      this.button.render(n, n2, f);
      n6 += 16;
      this.cogSearch64Png.setBounds(this.x + n6, this.y + 5, 12, 12);
      this.cogSearch64Png.setSmallIcon(true);
      this.cogSearch64Png.setSelected(CheatBreaker.getInstance().getKeybindSettings().searchForSettingsInMods.getBooleanValue());
      this.cogSearch64Png.scrollOffsetY = this.intValue;
      this.cogSearch64Png.render(n, n2, f);
      n6 += 16;
      this.searchMods.setBounds(this.x + n6, this.y + 5, this.width - n6 - 10, 12.0F);
      this.searchMods.scrollOffsetY = this.intValue;
      this.searchMods.renderAndTestHover(n, n2, true);
      this.process(n, n2);
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      super.mouseClicked(n, n2, n3);
      if (!this.searchMods.isMouseOverScaled(n, n2) && this.searchMods.isFocused()) {
         this.searchMods.setFocused(false);
      } else if (this.searchMods.isMouseOverScaled(n, n2) && !this.searchMods.isFocused()) {
         this.searchMods.setFocused(true);
      }

      if (this.cheatbreakerSettings.isMouseOver(n, n2) && this.flag4) {
         CheatBreaker.getInstance().getUiManager().run4();
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
      }

      if (this.cogSearch64Png.isMouseOver(n, n2)) {
         CheatBreaker.getInstance()
            .getKeybindSettings()
            .searchForSettingsInMods
            .changeValue(!CheatBreaker.getInstance().getKeybindSettings().searchForSettingsInMods.getBooleanValue());
         this.process(false);
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
      }

      if (this.button.isMouseOver(n, n2)) {
         this.process(false);
         this.run();
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
      }

      for (Button button : this.buttonList) {
         if (button.isMouseOver(n, n2)) {
            CheatBreaker.getInstance().getKeybindSettings().modCategory = this.findModCategory(button);
            this.process(false);
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));

            for (Button button2 : this.buttonList) {
               if (button2 != button) {
                  button2.setSelected(false);
                  button.setSelected(true);
               }
            }
         }
      }

      if (this.compact64Png.isMouseOver(n, n2)) {
         Setting modList = CheatBreaker.getInstance().getKeybindSettings().modList;
         String stringValue = modList.getStringValue();
         boolean equalsIgnoreCase = stringValue.equalsIgnoreCase("Compact");
         modList.changeValue(equalsIgnoreCase ? "Normal" : "Compact");
         CheatBreaker.getInstance().getUiManager().run2();
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
      }

      for (ModListItem modListItem : this.modListItemList) {
         if (modListItem.isMouseOver(n, n2)) {
            modListItem.mouseClicked(n, n2, n3);
         }
      }
   }

   public boolean checkAbstractMod(AbstractMod abstractMod, String str) {
      if (abstractMod.getSettings() != null && CheatBreaker.getInstance().getKeybindSettings().searchForSettingsInMods.getBooleanValue()) {
         for (Setting setting : abstractMod.getSettings()) {
            if (!CheatBreaker.getInstance().getKeybindSettings().bypassCustomizationLevel.getBooleanValue() && this.isHiddenByCustomizationLevel(setting)) {
               return false;
            }

            if (setting.getName().toLowerCase().contains(str.toLowerCase())) {
               return true;
            }

            if (setting.getAliases() != null) {
               for (String str2 : setting.getAliases()) {
                  if (str2.toLowerCase().contains(str.toLowerCase())) {
                     return true;
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void run() {
      Setting customizationLevel = CheatBreaker.getInstance().getKeybindSettings().customizationLevel;
      String toLowerCase = customizationLevel.getStringValue().toLowerCase();
      if (toLowerCase.equalsIgnoreCase("simple")) {
         customizationLevel.changeValue("Medium");
      } else if (toLowerCase.equalsIgnoreCase("medium")) {
         customizationLevel.changeValue("Advanced");
      } else {
         customizationLevel.changeValue("Simple");
      }
   }

   public void process(boolean flag) {
      this.modListItemList.clear();
      String text = this.searchMods.getText();
      if (CheatBreaker.getInstance().getKeybindSettings().modCategory == ModCategory.ALL) {
         this.modListItemList.addAll(this.findList(text));
      } else {
         this.modListItemList.addAll(this.findList(text, flag));
      }

      this.flag4 = this.checkString(text) && CheatBreaker.getInstance().getKeybindSettings().modCategory == ModCategory.ALL;
   }

   public String getText() {
      String toLowerCase = CheatBreaker.getInstance().getKeybindSettings().customizationLevel.getStringValue().toLowerCase();
      switch (toLowerCase) {
         case "medium":
            return "2";
         case "advanced":
            return "3";
         default:
            return "1";
      }
   }

   public List<ModListItem> findList(String str) {
      return str != null && str.length() != 0
         ? this.modListItemList2
            .stream()
            .filter(
               modListItem -> {
                  AbstractMod abstractMod = modListItem.abstractMod;
                  return abstractMod.getName().toLowerCase().contains(str.toLowerCase())
                     || this.checkList(abstractMod.getAuthors(), str)
                     || this.checkList(abstractMod.getKeywords(), str)
                     || this.checkList(abstractMod.getAliases(), str)
                     || this.checkAbstractMod(abstractMod, str);
               }
            )
            .collect(Collectors.toList())
         : this.modListItemList2;
   }

   @Override
   public void tick() {
      this.searchMods.tick();
   }

   public ModListPanel(List list, float f, int n, int n2, int n3, int n4) {
      super(f, n, n2, n3, n4);

      for (Object obj : list) {
         if (obj != CheatBreaker.getInstance().getModManager().notificationsMod) {
            ModListItem modListItem = new ModListItem(this, (AbstractMod)obj, f);
            this.modListItemList.add(modListItem);
            this.modListItemList2.add(modListItem);
         }
      }

      this.cheatbreakerSettings = new SettingsNavigationItem("CheatBreaker Settings", this.theme2.getNormalHighlightColorValue(), f);
      CustomFontRenderer playBold16 = CheatBreaker.getInstance().getFontManager().playBold16;
      this.searchMods = new TextField(
         CheatBreaker.getInstance().getFontManager().playRegular14,
         "Search Mods...",
         this.theme2.getButtonBackgroundColorValue(),
         this.theme2.getPrimaryTextColorValue()
      );
      this.searchMods.setMaxStringLength(256);

      for (Button button : this.buttonList) {
         button.scale = f;
         if (this.findModCategory(button) == CheatBreaker.getInstance().getKeybindSettings().modCategory) {
            button.setSelected(true);
         }
      }

      this.button = new Button(
         playBold16,
         null,
         this.getText(),
         this.x + 4,
         this.y + this.height - 20,
         this.x + this.width - 4,
         this.y + this.height - 6,
         this.theme2.getNormalHighlightColorValue(),
         f,
         false,
         true
      );
      this.cogSearch64Png = new Button(
         playBold16,
         null,
         "cog-search-64.png",
         this.x + 4,
         this.y + this.height - 20,
         this.x + this.width - 4,
         this.y + this.height - 6,
         this.theme2.getNormalHighlightColorValue(),
         f,
         false,
         true
      );
      this.cogSearch64Png.setTinyIcon(true);
      this.compact64Png = new Button(
         playBold16,
         null,
         "compact-64.png",
         this.x + 4,
         this.y + this.height - 20,
         this.x + this.width - 4,
         this.y + this.height - 6,
         this.theme2.getNormalHighlightColorValue(),
         f,
         false,
         true
      );
      this.compact64Png.setTinyIcon(true);
   }

   public List<ModListItem> findList(String str, boolean flag) {
      return str != null && (str.length() != 0 || !flag)
         ? this.modListItemList2
            .stream()
            .filter(
               modListItem -> {
                  AbstractMod abstractMod = modListItem.abstractMod;
                  return abstractMod.getCategories() == null
                     ? false
                     : (
                           abstractMod.getName().toLowerCase().contains(str.toLowerCase())
                              || this.checkList(abstractMod.getAuthors(), str)
                              || this.checkList(abstractMod.getKeywords(), str)
                              || this.checkList(abstractMod.getAliases(), str)
                              || this.checkAbstractMod(abstractMod, str)
                        )
                        && abstractMod.getCategories().contains(CheatBreaker.getInstance().getKeybindSettings().modCategory);
               }
            )
            .collect(Collectors.toList())
         : this.modListItemList2;
   }

   @Override
   public boolean hasSettings(AbstractMod abstractMod) {
      return false;
   }

   @Override
   public void selectMod(AbstractMod abstractMod) {
   }

   public ModCategory findModCategory(Button button) {
      switch (button.label.toLowerCase()) {
         case "hud":
            return ModCategory.HUD;
         case "combat":
            return ModCategory.COMBAT;
         case "server":
            return ModCategory.SERVER;
         case "misc":
            return ModCategory.MISC;
         default:
            return ModCategory.ALL;
      }
   }
}
