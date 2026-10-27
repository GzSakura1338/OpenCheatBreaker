package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.FloatEvent4;
import com.cheatbreaker.client.event.IntEvent1;
import com.cheatbreaker.client.event.IntEvent4;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class NametagMod extends AbstractMod {
   public String[] stringArray = new String[]{
      " ", "[", "]", "!", "=", "-", "@", "#", "$", "%", "^", "&", "*", "(", ")", "~", "`", "|", ";", ":", "'", "\"", ",", "<", ">", ".", "?", "/"
   };
   public ResourceLocation logoWhitePng = new ResourceLocation("client/logo_white.png");
   public Setting generalOptions = new SettingGroup(this, "General Options").changeValue(true);
   public Setting hideNametagKeybind = new Setting(this, "Hide Nametag Keybind").changeValue(0).setMouseBind(false).withParents(this.generalOptions);
   public Setting showBackground = new Setting(this, "Show Background").changeValue(true).withParents(this.generalOptions);
   public Setting textShadow = new Setting(this, "Text Shadow").changeValue(false).withParents(this.generalOptions);
   public Setting showCheatbreakerLogo = new Setting(this, "Show CheatBreaker Logo").changeValue(true).withParents(this.generalOptions);
   public Setting cheatbreakerLogoType = new Setting(this, "CheatBreaker Logo Type")
      .changeValue(false)
      .withHierarchyParents(this.showCheatbreakerLogo)
      .setToggleTexts("Text", "Circle");
   public Setting showSubIcons = new Setting(this, "Show Sub Icons").changeValue(false).withHierarchyParents(this.showCheatbreakerLogo);
   public Setting shownOwnNametag = new Setting(this, "Shown own Nametag").changeValue(false).withParents(this.generalOptions);
   public Setting showNametagsWhenGuiIsHidden = new Setting(this, "Show Nametags when GUI is hidden").changeValue(false).withParents(this.generalOptions);
   public Setting colorOptions = new SettingGroup(this, "Color Options").changeValue(true);
   public Setting stripColorFormatting = new Setting(this, "Strip Color Formatting").changeValue(false).withParents(this.colorOptions);
   public Setting textColor = new Setting(this, "Text Color")
      .changeValue(-1)
      .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
      .withHierarchyParents(this.stripColorFormatting);
   public static final float CONSTANT_0_003921569 = 0.003921569F;
   public boolean flag;
   public Setting backgroundColor = new Setting(this, "Background Color")
      .changeValue(1073741824)
      .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
      .withParents(this.colorOptions, this.showBackground);

   public void onIntEvent4(IntEvent4 intEvent4) {
      if (intEvent4.getIntValue() == this.hideNametagKeybind.getIntValue() && !this.hideNametagKeybind.hasMouseBind()) {
         this.flag = !this.flag;
      }
   }

   public void onIntEvent1(IntEvent1 intEvent1) {
      if (intEvent1.getEventButton() == this.hideNametagKeybind.getIntValue() && this.hideNametagKeybind.hasMouseBind()) {
         this.flag = !this.flag;
      }
   }

   public boolean contains(String str) {
      for (String str2 : this.stringArray) {
         if (str.contains(str2)) {
            return false;
         }
      }

      return true;
   }

   public NametagMod() {
      super("Nametag");
      this.setDescription("Displays the username above players.");
      this.setCategories(ModCategory.HUD);
      this.setIcon(new ResourceLocation("client/logo_white.png"), 13, 7);
      this.setIconOffsetY(-3);
      this.addEventHandler(IntEvent4.class, this::onIntEvent4);
      this.addEventHandler(IntEvent1.class, this::onIntEvent1);
      this.addEventHandler(FloatEvent4.class, this::onFloatEvent4);
      this.setEnabledByDefault(true);
   }

   public void onFloatEvent4(FloatEvent4 floatEvent4) {
      int colorValue = this.backgroundColor.getColorValue();
      float f = (colorValue >> 24 & 0xFF) * 0.003921569F;
      float f2 = (colorValue >> 16 & 0xFF) * 0.003921569F;
      float f3 = (colorValue >> 8 & 0xFF) * 0.003921569F;
      float f4 = (colorValue & 0xFF) * 0.003921569F;
      if (this.isRestricted()) {
         f = Math.min(f, floatEvent4.getFloatValue4());
      }

      floatEvent4.setFloatValue4(f);
      floatEvent4.setFloatValue1(f2);
      floatEvent4.setFloatValue2(f3);
      floatEvent4.setFloatValue3(f4);
   }

   @Override
   public void renderPreview(int n, int n2, int n3, int n4) {
      String str = this.mc.fontRendererObj.getStringWidth(this.mc.getSession().getUsername()) > 94 ? "Steve" : this.mc.getSession().getUsername();
      int n5 = this.mc.fontRendererObj.getStringWidth(str) / 2;
      Gui.drawRect(n + n3 / 2.0F - n5 - 8.0F, n2 + n4 / 2.0F - 33.5F, n + n3 / 2.0F + n5 + 8.0F, n2 + n4 / 2.0F - 24.5F, 1862270976);
      this.mc.fontRendererObj.drawString(str, n + n3 / 2.0F - n5 + 8.0F, n2 + n4 / 2.0F - 32.5F, -1, false);
      this.setIconOffsetX(-n5);
   }
}
