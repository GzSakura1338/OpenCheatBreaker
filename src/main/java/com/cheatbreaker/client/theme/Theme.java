package com.cheatbreaker.client.theme;

import com.cheatbreaker.client.mod.setting.FormatSetting;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import java.util.ArrayList;
import java.util.List;

public class Theme {
   public int intValue = 0;
   public String themeNameValue;
   public boolean flag;
   public Setting textColor4;
   public boolean flag2;
   public int primaryBackgroundColorValue;
   public Setting selectionOutlineColor;
   public int menuBackgroundColorValue;
   public int enabledModColorValue;
   public Setting trackHoverColor;
   public Setting scrollbarColors;
   public int disabledModColorValue;
   public int modNameTextColorValue;
   public Setting selectedButtonColor;
   public int buttonBackgroundColorValue;
   public Setting unselectedOverlayColor;
   public Setting normalTextColors;
   public Setting trackColor;
   public float hoverBorderThicknessValue;
   public float buttonCornerRadiusValue;
   public Setting selectedOutlineColor;
   public int selectedButtonColorValue;
   public int disabledButtonColorValue;
   public Setting buttonBackgroundColor;
   public Setting labelHighlightColor;
   public Setting thumbOutlineColor;
   public int modsHighlightColorValue;
   public Setting textColor2;
   public Setting primaryTextColor;
   public Setting snapLineColor;
   public int normalHighlightColorValue;
   public Setting thumbColor;
   public Setting normalHighlightColor;
   public Setting primaryBackgroundColor;
   public Setting thumbColor2;
   public Setting unselecHoveringOvColor;
   public int labelHighlightColorValue;
   public int enableModHighlightColorValue;
   public int disableModHighlightColorValue;
   public Setting textColor22;
   public Setting disableModHighlightColor;
   public Setting disabledModColor;
   public FormatSetting themeName;
   public int selectedOutlineColorValue;
   public Setting hoverBorderThickness;
   public Setting thumbHoverColor;
   public int unselectedOutlineColorValue;
   public Setting disabledButtonColor;
   public int unselectedOverlayColorValue;
   public Setting enableModHighlightColor;
   public Setting selectionColor;
   public int selectedOverlayColorValue;
   public Setting unselecHoveringOlColor;
   public Setting textColor3;
   public int selecHoveringOlColorValue;
   public Setting modsHighlightColor;
   public Setting modNameTextColor;
   public Setting textColor5;
   public int unselecHoveringOlColorValue;
   public int unselecHoveringOvColorValue;
   public int selecHoveringOvColorValue;
   public int snapLineColorValue;
   public Setting setting;
   public Setting buttonCornerRadius;
   public int neighboringModOverlayColorValue;
   public int hudBorderColorValue;
   public int selectionOutlineColorValue;
   public Setting selecHoveringOlColor;
   public int selectionColorValue;
   public int trackColorValue;
   public Setting hudEditorColors;
   public Setting selecHoveringOvColor;
   public int thumbColorValue;
   public int thumbOutlineColorValue;
   public int thumbColorValue2;
   public int thumbHoverColorValue;
   public Setting enabledModColor;
   public int trackColorValue2;
   public int trackHoverColorValue;
   public Setting hudBorderColor;
   public Setting dullTextColors;
   public int primaryTextColorValue;
   public Setting textColor42;
   public int textColor2Value;
   public Setting buttonOptions;
   public int textColor3Value;
   public Setting modPreviewColors;
   public Setting unselectedOutlineColor;
   public Setting textColor32;
   public int textColor4Value;
   public Setting menuBackgroundColor;
   public Setting trackColor2;
   public Setting primaryTextColor2;
   public int textColor5Value;
   public int intValue2;
   public Setting selectedOverlayColor;
   public Setting neighboringModOverlayColor;
   public Setting sliderOptionColors;
   public int primaryTextColorValue2;
   public int textColor2Value2;
   public int textColor3Value2;
   public int textColor4Value2;
   public Setting backgroundColors;
   public List<Setting> settingList;

   public void setTextColor4Value(int n) {
      this.textColor4Value = n;
   }

   public int getPrimaryBackgroundColorValue() {
      return this.primaryBackgroundColorValue;
   }

   public void setPrimaryTextColor2(Setting setting) {
      this.primaryTextColor2 = setting;
   }

   public void setDisabledModColorValue(int n) {
      this.disabledModColorValue = n;
   }

   public Setting getTrackColor() {
      return this.trackColor;
   }

   public int getNormalHighlightColorValue() {
      return this.normalHighlightColorValue;
   }

   public void setModNameTextColor(Setting setting) {
      this.modNameTextColor = setting;
   }

   public void setThemeNameValue(String str) {
      this.themeNameValue = str;
   }

   public void setPrimaryTextColorValue(int n) {
      this.primaryTextColorValue = n;
   }

   public void setTrackColor(Setting setting) {
      this.trackColor = setting;
   }

   public int getSelectedButtonColorValue() {
      return this.selectedButtonColorValue;
   }

   public Setting getMenuBackgroundColor() {
      return this.menuBackgroundColor;
   }

   public void setMenuBackgroundColor(Setting setting) {
      this.menuBackgroundColor = setting;
   }

   public void setThemeName(FormatSetting formatSetting) {
      this.themeName = formatSetting;
   }

   public int getThumbHoverColorValue() {
      return this.thumbHoverColorValue;
   }

   public Setting getModNameTextColor() {
      return this.modNameTextColor;
   }

   public int getDisabledButtonColorValue() {
      return this.disabledButtonColorValue;
   }

   public void setTextColor42(Setting setting) {
      this.textColor42 = setting;
   }

   public void run() {
      this.settingList = new ArrayList<>();
      this.themeName = (FormatSetting)new FormatSetting(this.settingList, "Theme Name")
         .changeValue(this.themeNameValue)
         .setOnUpdate(obj -> this.themeNameValue = (String)obj);
      this.backgroundColors = new SettingGroup(this.settingList, "Background Colors").changeValue(false);
      this.primaryBackgroundColor = new Setting(this.settingList, "Primary Background Color")
         .changeValue(this.primaryBackgroundColorValue)
         .withHierarchyParents(this.backgroundColors)
         .setOnUpdate(obj -> this.primaryBackgroundColorValue = (Integer)obj);
      this.menuBackgroundColor = new Setting(this.settingList, "Menu Background Color")
         .setOnUpdate(obj -> this.menuBackgroundColorValue = (Integer)obj)
         .withHierarchyParents(this.backgroundColors)
         .changeValue(this.menuBackgroundColorValue);
      this.modPreviewColors = new SettingGroup(this.settingList, "Mod Preview Colors").changeValue(false);
      this.enabledModColor = new Setting(this.settingList, "Enabled Mod Color")
         .withHierarchyParents(this.modPreviewColors)
         .setOnUpdate(obj -> this.enabledModColorValue = (Integer)obj)
         .changeValue(this.enabledModColorValue);
      this.disabledModColor = new Setting(this.settingList, "Disabled Mod Color")
         .withHierarchyParents(this.modPreviewColors)
         .setOnUpdate(obj -> this.disabledModColorValue = (Integer)obj)
         .changeValue(this.disabledModColorValue);
      this.modNameTextColor = new Setting(this.settingList, "Mod Name Text Color")
         .withHierarchyParents(this.modPreviewColors)
         .setOnUpdate(obj -> this.modNameTextColorValue = (Integer)obj)
         .changeValue(this.modNameTextColorValue);
      this.hudEditorColors = new SettingGroup(this.settingList, "HUD Editor Colors").changeValue(false);
      this.selectedOutlineColor = new Setting(this.settingList, "Selected Outline Color")
         .withHierarchyParents(this.hudEditorColors)
         .setOnUpdate(obj -> this.selectedOutlineColorValue = (Integer)obj)
         .changeValue(this.selectedOutlineColorValue);
      this.unselectedOutlineColor = new Setting(this.settingList, "Unselected Outline Color")
         .withHierarchyParents(this.hudEditorColors)
         .setOnUpdate(obj -> this.unselectedOutlineColorValue = (Integer)obj)
         .changeValue(this.unselectedOutlineColorValue);
      this.selectedOverlayColor = new Setting(this.settingList, "Selected Overlay Color")
         .withHierarchyParents(this.hudEditorColors)
         .setOnUpdate(obj -> this.selectedOverlayColorValue = (Integer)obj)
         .changeValue(this.selectedOverlayColorValue);
      this.unselectedOverlayColor = new Setting(this.settingList, "Unselected Overlay Color")
         .withHierarchyParents(this.hudEditorColors)
         .setOnUpdate(obj -> this.unselectedOverlayColorValue = (Integer)obj)
         .changeValue(this.unselectedOverlayColorValue);
      this.selecHoveringOlColor = new Setting(this.settingList, "Selec. Hovering OL Color")
         .withHierarchyParents(this.hudEditorColors)
         .setOnUpdate(obj -> this.selecHoveringOlColorValue = (Integer)obj)
         .changeValue(this.selecHoveringOlColorValue);
      this.unselecHoveringOlColor = new Setting(this.settingList, "Unselec. Hovering OL Color")
         .withHierarchyParents(this.hudEditorColors)
         .setOnUpdate(obj -> this.unselecHoveringOlColorValue = (Integer)obj)
         .changeValue(this.unselecHoveringOlColorValue);
      this.selecHoveringOvColor = new Setting(this.settingList, "Selec. Hovering OV Color")
         .withHierarchyParents(this.hudEditorColors)
         .setOnUpdate(obj -> this.selecHoveringOvColorValue = (Integer)obj)
         .changeValue(this.selecHoveringOvColorValue);
      this.unselecHoveringOvColor = new Setting(this.settingList, "Unselec. Hovering OV Color")
         .withHierarchyParents(this.hudEditorColors)
         .setOnUpdate(obj -> this.unselecHoveringOvColorValue = (Integer)obj)
         .changeValue(this.unselecHoveringOvColorValue);
      this.neighboringModOverlayColor = new Setting(this.settingList, "Neighboring Mod Overlay Color")
         .withHierarchyParents(this.hudEditorColors)
         .setOnUpdate(obj -> this.neighboringModOverlayColorValue = (Integer)obj)
         .changeValue(this.neighboringModOverlayColorValue);
      this.snapLineColor = new Setting(this.settingList, "Snap Line Color")
         .withHierarchyParents(this.hudEditorColors)
         .setOnUpdate(obj -> this.snapLineColorValue = (Integer)obj)
         .changeValue(this.snapLineColorValue);
      this.hudBorderColor = new Setting(this.settingList, "HUD Border Color")
         .withHierarchyParents(this.hudEditorColors)
         .setOnUpdate(obj -> this.hudBorderColorValue = (Integer)obj)
         .changeValue(this.hudBorderColorValue);
      this.selectionColor = new Setting(this.settingList, "Selection Color")
         .withHierarchyParents(this.hudEditorColors)
         .setOnUpdate(obj -> this.selectionColorValue = (Integer)obj)
         .changeValue(this.selectionColorValue);
      this.selectionOutlineColor = new Setting(this.settingList, "Selection Outline Color")
         .withHierarchyParents(this.hudEditorColors)
         .setOnUpdate(obj -> this.selectionOutlineColorValue = (Integer)obj)
         .changeValue(this.selectionOutlineColorValue);
      this.buttonOptions = new SettingGroup(this.settingList, "Button Options").changeValue(false);
      this.hoverBorderThickness = new Setting(this.settingList, "Hover Border Thickness")
         .withHierarchyParents(this.buttonOptions)
         .setOnUpdate(obj -> this.hoverBorderThicknessValue = (Float)obj)
         .setUnit("px")
         .changeValue(2.0F)
         .setRange(0.0F, 5.0F);
      this.buttonCornerRadius = new Setting(this.settingList, "Button Corner Radius")
         .withHierarchyParents(this.buttonOptions)
         .setOnUpdate(obj -> this.buttonCornerRadiusValue = (Float)obj)
         .setUnit("px")
         .changeValue(0.0F)
         .setRange(0.0F, 8.0F);
      this.buttonBackgroundColor = new Setting(this.settingList, "Button Background Color")
         .withHierarchyParents(this.buttonOptions)
         .setOnUpdate(obj -> this.buttonBackgroundColorValue = (Integer)obj)
         .changeValue(this.buttonBackgroundColorValue);
      this.selectedButtonColor = new Setting(this.settingList, "Selected Button Color")
         .withHierarchyParents(this.buttonOptions)
         .setOnUpdate(obj -> this.selectedButtonColorValue = (Integer)obj)
         .changeValue(this.selectedButtonColorValue);
      this.disabledButtonColor = new Setting(this.settingList, "Disabled Button Color")
         .withHierarchyParents(this.buttonOptions)
         .setOnUpdate(obj -> this.disabledButtonColorValue = (Integer)obj)
         .changeValue(this.disabledButtonColorValue);
      this.normalHighlightColor = new Setting(this.settingList, "Normal Highlight Color")
         .withHierarchyParents(this.buttonOptions)
         .setOnUpdate(obj -> this.normalHighlightColorValue = (Integer)obj)
         .changeValue(this.normalHighlightColorValue);
      this.labelHighlightColor = new Setting(this.settingList, "Label Highlight Color")
         .withHierarchyParents(this.buttonOptions)
         .setOnUpdate(obj -> this.labelHighlightColorValue = (Integer)obj)
         .changeValue(this.labelHighlightColorValue);
      this.modsHighlightColor = new Setting(this.settingList, "Mods Highlight Color")
         .withHierarchyParents(this.buttonOptions)
         .setOnUpdate(obj -> this.modsHighlightColorValue = (Integer)obj)
         .changeValue(this.modsHighlightColorValue);
      this.enableModHighlightColor = new Setting(this.settingList, "Enable Mod Highlight Color")
         .withHierarchyParents(this.buttonOptions)
         .setOnUpdate(obj -> this.enableModHighlightColorValue = (Integer)obj)
         .changeValue(this.enableModHighlightColorValue);
      this.disableModHighlightColor = new Setting(this.settingList, "Disable Mod Highlight Color")
         .withHierarchyParents(this.buttonOptions)
         .setOnUpdate(obj -> this.disableModHighlightColorValue = (Integer)obj)
         .changeValue(this.disableModHighlightColorValue);
      this.sliderOptionColors = new SettingGroup(this.settingList, "Slider Option Colors").changeValue(false);
      this.trackColor = new Setting(this.settingList, "Track Color")
         .withHierarchyParents(this.sliderOptionColors)
         .setOnUpdate(obj -> this.trackColorValue = (Integer)obj)
         .changeValue(this.trackColorValue);
      this.thumbColor = new Setting(this.settingList, "Thumb Color")
         .withHierarchyParents(this.sliderOptionColors)
         .setOnUpdate(obj -> this.thumbColorValue = (Integer)obj)
         .changeValue(this.thumbColorValue);
      this.thumbOutlineColor = new Setting(this.settingList, "Thumb Outline Color")
         .withHierarchyParents(this.sliderOptionColors)
         .setOnUpdate(obj -> this.thumbOutlineColorValue = (Integer)obj)
         .changeValue(this.thumbOutlineColorValue);
      this.scrollbarColors = new SettingGroup(this.settingList, "Scrollbar Colors").changeValue(false);
      this.thumbColor2 = new Setting(this.settingList, "Thumb Color")
         .withHierarchyParents(this.scrollbarColors)
         .setOnUpdate(obj -> this.thumbColorValue2 = (Integer)obj)
         .changeValue(this.thumbColorValue2);
      this.thumbHoverColor = new Setting(this.settingList, "Thumb Hover Color")
         .withHierarchyParents(this.scrollbarColors)
         .setOnUpdate(obj -> this.thumbHoverColorValue = (Integer)obj)
         .changeValue(this.thumbHoverColorValue);
      this.trackColor2 = new Setting(this.settingList, "Track Color")
         .withHierarchyParents(this.scrollbarColors)
         .setOnUpdate(obj -> this.trackColorValue2 = (Integer)obj)
         .changeValue(this.trackColorValue2);
      this.trackHoverColor = new Setting(this.settingList, "Track Hover Color")
         .withHierarchyParents(this.scrollbarColors)
         .setOnUpdate(obj -> this.trackHoverColorValue = (Integer)obj)
         .changeValue(this.trackHoverColorValue);
      this.normalTextColors = new SettingGroup(this.settingList, "Normal Text Colors").changeValue(false);
      this.primaryTextColor2 = new Setting(this.settingList, "Primary Text Color")
         .withHierarchyParents(this.normalTextColors)
         .setOnUpdate(obj -> this.primaryTextColorValue = (Integer)obj)
         .changeValue(this.primaryTextColorValue);
      this.textColor22 = new Setting(this.settingList, "Text Color 2")
         .withHierarchyParents(this.normalTextColors)
         .setOnUpdate(obj -> this.textColor2Value = (Integer)obj)
         .changeValue(this.textColor2Value);
      this.textColor32 = new Setting(this.settingList, "Text Color 3")
         .withHierarchyParents(this.normalTextColors)
         .setOnUpdate(obj -> this.textColor3Value = (Integer)obj)
         .changeValue(this.textColor3Value);
      this.textColor4 = new Setting(this.settingList, "Text Color 4")
         .withHierarchyParents(this.normalTextColors)
         .setOnUpdate(obj -> this.textColor4Value = (Integer)obj)
         .changeValue(this.textColor4Value);
      this.textColor5 = new Setting(this.settingList, "Text Color 5")
         .withHierarchyParents(this.normalTextColors)
         .setOnUpdate(obj -> this.textColor5Value = (Integer)obj)
         .changeValue(this.textColor5Value);
      this.dullTextColors = new SettingGroup(this.settingList, "Dull Text Colors").changeValue(false);
      this.primaryTextColor = new Setting(this.settingList, "Primary Text Color")
         .withHierarchyParents(this.dullTextColors)
         .setOnUpdate(obj -> this.primaryTextColorValue2 = (Integer)obj)
         .changeValue(this.primaryTextColorValue2);
      this.textColor2 = new Setting(this.settingList, "Text Color 2")
         .withHierarchyParents(this.dullTextColors)
         .setOnUpdate(obj -> this.textColor2Value2 = (Integer)obj)
         .changeValue(this.textColor2Value2);
      this.textColor3 = new Setting(this.settingList, "Text Color 3")
         .withHierarchyParents(this.dullTextColors)
         .setOnUpdate(obj -> this.textColor3Value2 = (Integer)obj)
         .changeValue(this.textColor3Value2);
      this.textColor42 = new Setting(this.settingList, "Text Color 4")
         .withHierarchyParents(this.dullTextColors)
         .setOnUpdate(obj -> this.textColor4Value2 = (Integer)obj)
         .changeValue(this.textColor4Value2);
   }

   public void setTextColor2Value(int n) {
      this.textColor2Value = n;
   }

   public Setting getSliderOptionColors() {
      return this.sliderOptionColors;
   }

   public void setTextColor3(Setting setting) {
      this.textColor3 = setting;
   }

   public void setHoverBorderThicknessValue(float f) {
      this.hoverBorderThicknessValue = f;
   }

   public Setting getUnselecHoveringOvColor() {
      return this.unselecHoveringOvColor;
   }

   public String getThemeNameValue() {
      return this.themeNameValue;
   }

   public void setTrackHoverColorValue(int n) {
      this.trackHoverColorValue = n;
   }

   public int getDisableModHighlightColorValue() {
      return this.disableModHighlightColorValue;
   }

   public void setNormalHighlightColorValue(int n) {
      this.normalHighlightColorValue = n;
   }

   public void setButtonBackgroundColorValue(int n) {
      this.buttonBackgroundColorValue = n;
   }

   public void setTextColor22(Setting setting) {
      this.textColor22 = setting;
   }

   public Setting getTrackHoverColor() {
      return this.trackHoverColor;
   }

   public void setEnabledModColor(Setting setting) {
      this.enabledModColor = setting;
   }

   public Setting getTextColor3() {
      return this.textColor3;
   }

   public void setUnselecHoveringOlColorValue(int n) {
      this.unselecHoveringOlColorValue = n;
   }

   public void setThumbOutlineColorValue(int n) {
      this.thumbOutlineColorValue = n;
   }

   public int getModNameTextColorValue() {
      return this.modNameTextColorValue;
   }

   public Setting getModPreviewColors() {
      return this.modPreviewColors;
   }

   public Setting getSelectedOutlineColor() {
      return this.selectedOutlineColor;
   }

   public void setNeighboringModOverlayColor(Setting setting) {
      this.neighboringModOverlayColor = setting;
   }

   public void setUnselecHoveringOvColor(Setting setting) {
      this.unselecHoveringOvColor = setting;
   }

   public void setLabelHighlightColorValue(int n) {
      this.labelHighlightColorValue = n;
   }

   public Setting getLabelHighlightColor() {
      return this.labelHighlightColor;
   }

   public void setModNameTextColorValue(int n) {
      this.modNameTextColorValue = n;
   }

   public Setting getTextColor5() {
      return this.textColor5;
   }

   public void setLabelHighlightColor(Setting setting) {
      this.labelHighlightColor = setting;
   }

   public void setTrackColorValue2(int n) {
      this.trackColorValue2 = n;
   }

   public void setNormalTextColors(Setting setting) {
      this.normalTextColors = setting;
   }

   public void setScrollbarColors(Setting setting) {
      this.scrollbarColors = setting;
   }

   public Setting getTextColor4() {
      return this.textColor4;
   }

   public Setting getTrackColor2() {
      return this.trackColor2;
   }

   public int getThumbOutlineColorValue() {
      return this.thumbOutlineColorValue;
   }

   public void setSelectedButtonColorValue(int n) {
      this.selectedButtonColorValue = n;
   }

   public int getMenuBackgroundColorValue() {
      return this.menuBackgroundColorValue;
   }

   public void setDullTextColors(Setting setting) {
      this.dullTextColors = setting;
   }

   public void setThumbHoverColor(Setting setting) {
      this.thumbHoverColor = setting;
   }

   public void setDisableModHighlightColorValue(int n) {
      this.disableModHighlightColorValue = n;
   }

   public Setting getButtonOptions() {
      return this.buttonOptions;
   }

   public void setEnableModHighlightColorValue(int n) {
      this.enableModHighlightColorValue = n;
   }

   public void setTextColor5(Setting setting) {
      this.textColor5 = setting;
   }

   public void setMenuBackgroundColorValue(int n) {
      this.menuBackgroundColorValue = n;
   }

   public void setUnselectedOutlineColorValue(int n) {
      this.unselectedOutlineColorValue = n;
   }

   public void setSelectedOverlayColorValue(int n) {
      this.selectedOverlayColorValue = n;
   }

   public void setButtonCornerRadiusValue(float f) {
      this.buttonCornerRadiusValue = f;
   }

   public Setting getDisableModHighlightColor() {
      return this.disableModHighlightColor;
   }

   public float getHoverBorderThicknessValue() {
      return this.hoverBorderThicknessValue;
   }

   public int getUnselecHoveringOlColorValue() {
      return this.unselecHoveringOlColorValue;
   }

   public void setSelecHoveringOlColorValue(int n) {
      this.selecHoveringOlColorValue = n;
   }

   public Setting getUnselecHoveringOlColor() {
      return this.unselecHoveringOlColor;
   }

   public Setting getThumbOutlineColor() {
      return this.thumbOutlineColor;
   }

   public void setSettingList(List<Setting> list) {
      this.settingList = list;
   }

   public void setTextColor3Value2(int n) {
      this.textColor3Value2 = n;
   }

   public void setUnselecHoveringOvColorValue(int n) {
      this.unselecHoveringOvColorValue = n;
   }

   public void setSelectedOverlayColor(Setting setting) {
      this.selectedOverlayColor = setting;
   }

   public void setSelectedOutlineColor(Setting setting) {
      this.selectedOutlineColor = setting;
   }

   public int getSelectionOutlineColorValue() {
      return this.selectionOutlineColorValue;
   }

   public Setting getHoverBorderThickness() {
      return this.hoverBorderThickness;
   }

   public Setting getUnselectedOverlayColor() {
      return this.unselectedOverlayColor;
   }

   public void setTextColor2Value2(int n) {
      this.textColor2Value2 = n;
   }

   public Setting getSelectedButtonColor() {
      return this.selectedButtonColor;
   }

   public void setSnapLineColorValue(int n) {
      this.snapLineColorValue = n;
   }

   public Setting getBackgroundColors() {
      return this.backgroundColors;
   }

   public Setting getSelecHoveringOlColor() {
      return this.selecHoveringOlColor;
   }

   public Setting getModsHighlightColor() {
      return this.modsHighlightColor;
   }

   public Setting getThumbColor2() {
      return this.thumbColor2;
   }

   public void setHudEditorColors(Setting setting) {
      this.hudEditorColors = setting;
   }

   public void setSelecHoveringOvColorValue(int n) {
      this.selecHoveringOvColorValue = n;
   }

   public int getPrimaryTextColorValue2() {
      return this.primaryTextColorValue2;
   }

   public void setTextColor5Value(int n) {
      this.textColor5Value = n;
   }

   public void setPrimaryTextColor(Setting setting) {
      this.primaryTextColor = setting;
   }

   public int getUnselecHoveringOvColorValue() {
      return this.unselecHoveringOvColorValue;
   }

   public int getIntValue2() {
      return this.intValue2;
   }

   public void setHoverBorderThickness(Setting setting) {
      this.hoverBorderThickness = setting;
   }

   public int getSelectionColorValue() {
      return this.selectionColorValue;
   }

   public int getPrimaryTextColorValue() {
      return this.primaryTextColorValue;
   }

   public void setSelecHoveringOlColor(Setting setting) {
      this.selecHoveringOlColor = setting;
   }

   public Setting getDullTextColors() {
      return this.dullTextColors;
   }

   public void setTextColor32(Setting setting) {
      this.textColor32 = setting;
   }

   public int getModsHighlightColorValue() {
      return this.modsHighlightColorValue;
   }

   public Setting getSelectedOverlayColor() {
      return this.selectedOverlayColor;
   }

   public int getSelecHoveringOvColorValue() {
      return this.selecHoveringOvColorValue;
   }

   public Setting getButtonBackgroundColor() {
      return this.buttonBackgroundColor;
   }

   public Setting getNormalTextColors() {
      return this.normalTextColors;
   }

   public int getTrackColorValue2() {
      return this.trackColorValue2;
   }

   public void setThumbColorValue(int n) {
      this.thumbColorValue = n;
   }

   public void setDisabledModColor(Setting setting) {
      this.disabledModColor = setting;
   }

   public void setUnselecHoveringOlColor(Setting setting) {
      this.unselecHoveringOlColor = setting;
   }

   public Setting getScrollbarColors() {
      return this.scrollbarColors;
   }

   public void setBackgroundColors(Setting setting) {
      this.backgroundColors = setting;
   }

   public void setHudBorderColor(Setting setting) {
      this.hudBorderColor = setting;
   }

   public Setting getEnabledModColor() {
      return this.enabledModColor;
   }

   public Setting getEnableModHighlightColor() {
      return this.enableModHighlightColor;
   }

   public int getButtonBackgroundColorValue() {
      return this.buttonBackgroundColorValue;
   }

   public int getDisabledModColorValue() {
      return this.disabledModColorValue;
   }

   public int getTextColor2Value2() {
      return this.textColor2Value2;
   }

   public void setPrimaryBackgroundColorValue(int n) {
      this.primaryBackgroundColorValue = n;
   }

   public void setButtonCornerRadius(Setting setting) {
      this.buttonCornerRadius = setting;
   }

   public void setTextColor3Value(int n) {
      this.textColor3Value = n;
   }

   public void setUnselectedOutlineColor(Setting setting) {
      this.unselectedOutlineColor = setting;
   }

   public void setThumbColor2(Setting setting) {
      this.thumbColor2 = setting;
   }

   public int getNeighboringModOverlayColorValue() {
      return this.neighboringModOverlayColorValue;
   }

   public void setPrimaryTextColorValue2(int n) {
      this.primaryTextColorValue2 = n;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void setIntValue(int n) {
      this.intValue = n;
   }

   public Setting getPrimaryTextColor2() {
      return this.primaryTextColor2;
   }

   public void setSnapLineColor(Setting setting) {
      this.snapLineColor = setting;
   }

   public void setSelecHoveringOvColor(Setting setting) {
      this.selecHoveringOvColor = setting;
   }

   public void setThumbOutlineColor(Setting setting) {
      this.thumbOutlineColor = setting;
   }

   public Setting getThumbHoverColor() {
      return this.thumbHoverColor;
   }

   public Setting getThumbColor() {
      return this.thumbColor;
   }

   public void setTrackColorValue(int n) {
      this.trackColorValue = n;
   }

   public Setting getSetting() {
      return this.setting;
   }

   public void setButtonBackgroundColor(Setting setting) {
      this.buttonBackgroundColor = setting;
   }

   public void setSliderOptionColors(Setting setting) {
      this.sliderOptionColors = setting;
   }

   public Setting getNeighboringModOverlayColor() {
      return this.neighboringModOverlayColor;
   }

   public void setHudBorderColorValue(int n) {
      this.hudBorderColorValue = n;
   }

   public void setModsHighlightColorValue(int n) {
      this.modsHighlightColorValue = n;
   }

   public int getTextColor4Value2() {
      return this.textColor4Value2;
   }

   public Setting getUnselectedOutlineColor() {
      return this.unselectedOutlineColor;
   }

   public void setSelectedButtonColor(Setting setting) {
      this.selectedButtonColor = setting;
   }

   public int getHudBorderColorValue() {
      return this.hudBorderColorValue;
   }

   public void setEnableModHighlightColor(Setting setting) {
      this.enableModHighlightColor = setting;
   }

   public void setSelectionColor(Setting setting) {
      this.selectionColor = setting;
   }

   public void setTextColor2(Setting setting) {
      this.textColor2 = setting;
   }

   public void setButtonOptions(Setting setting) {
      this.buttonOptions = setting;
   }

   public Setting getTextColor32() {
      return this.textColor32;
   }

   public void setTextColor4(Setting setting) {
      this.textColor4 = setting;
   }

   public void setSetting(Setting setting) {
      this.setting = setting;
   }

   public int getEnabledModColorValue() {
      return this.enabledModColorValue;
   }

   public int getSelecHoveringOlColorValue() {
      return this.selecHoveringOlColorValue;
   }

   public Setting getHudEditorColors() {
      return this.hudEditorColors;
   }

   public int getSelectedOutlineColorValue() {
      return this.selectedOutlineColorValue;
   }

   public void setDisabledButtonColorValue(int n) {
      this.disabledButtonColorValue = n;
   }

   public int getEnableModHighlightColorValue() {
      return this.enableModHighlightColorValue;
   }

   public void setThumbColor(Setting setting) {
      this.thumbColor = setting;
   }

   public void setNeighboringModOverlayColorValue(int n) {
      this.neighboringModOverlayColorValue = n;
   }

   public void setTrackHoverColor(Setting setting) {
      this.trackHoverColor = setting;
   }

   public void setTextColor4Value2(int n) {
      this.textColor4Value2 = n;
   }

   public int getTrackHoverColorValue() {
      return this.trackHoverColorValue;
   }

   public float getButtonCornerRadiusValue() {
      return this.buttonCornerRadiusValue;
   }

   public Setting getButtonCornerRadius() {
      return this.buttonCornerRadius;
   }

   public void setNormalHighlightColor(Setting setting) {
      this.normalHighlightColor = setting;
   }

   public Setting getTextColor2() {
      return this.textColor2;
   }

   public Setting getNormalHighlightColor() {
      return this.normalHighlightColor;
   }

   public int getTextColor5Value() {
      return this.textColor5Value;
   }

   public int getThumbColorValue() {
      return this.thumbColorValue;
   }

   public void setModPreviewColors(Setting setting) {
      this.modPreviewColors = setting;
   }

   public void setDisabledButtonColor(Setting setting) {
      this.disabledButtonColor = setting;
   }

   public int getLabelHighlightColorValue() {
      return this.labelHighlightColorValue;
   }

   public Setting getPrimaryTextColor() {
      return this.primaryTextColor;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public void setSelectedOutlineColorValue(int n) {
      this.selectedOutlineColorValue = n;
   }

   public Setting getSnapLineColor() {
      return this.snapLineColor;
   }

   public int getUnselectedOutlineColorValue() {
      return this.unselectedOutlineColorValue;
   }

   public int getUnselectedOverlayColorValue() {
      return this.unselectedOverlayColorValue;
   }

   public void setUnselectedOverlayColorValue(int n) {
      this.unselectedOverlayColorValue = n;
   }

   public void setSelectionOutlineColor(Setting setting) {
      this.selectionOutlineColor = setting;
   }

   public Setting getPrimaryBackgroundColor() {
      return this.primaryBackgroundColor;
   }

   public FormatSetting getThemeName() {
      return this.themeName;
   }

   public int getTextColor4Value() {
      return this.textColor4Value;
   }

   public Setting getHudBorderColor() {
      return this.hudBorderColor;
   }

   public void setSelectionColorValue(int n) {
      this.selectionColorValue = n;
   }

   public Setting getTextColor22() {
      return this.textColor22;
   }

   public int getThumbColorValue2() {
      return this.thumbColorValue2;
   }

   public int getSelectedOverlayColorValue() {
      return this.selectedOverlayColorValue;
   }

   public List<Setting> getSettingList() {
      return this.settingList;
   }

   public void setUnselectedOverlayColor(Setting setting) {
      this.unselectedOverlayColor = setting;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public void setDisableModHighlightColor(Setting setting) {
      this.disableModHighlightColor = setting;
   }

   public Setting getSelectionOutlineColor() {
      return this.selectionOutlineColor;
   }

   public Setting getDisabledModColor() {
      return this.disabledModColor;
   }

   public Theme(
      String str,
      boolean flag,
      boolean flag2,
      int n,
      int n2,
      int n3,
      int n4,
      int n5,
      int n6,
      float f,
      float f2,
      int n7,
      int n8,
      int n9,
      int n10,
      int n11,
      int n12,
      int n13,
      int n14,
      int n15,
      int n16,
      int n17,
      int n18,
      int n19,
      int n20,
      int n21,
      int n22,
      int n23,
      int n24,
      int n25,
      int n26,
      int n27,
      int n28,
      int n29,
      int n30,
      int n31,
      int n32,
      int n33,
      int n34,
      int n35,
      int n36,
      int n37,
      int n38,
      int n39,
      int n40,
      int n41,
      int n42,
      int n43
   ) {
      this.themeNameValue = str.replaceAll(".json", "");
      this.flag = flag2;
      this.flag2 = flag;
      this.primaryBackgroundColorValue = n;
      this.menuBackgroundColorValue = n2;
      this.enabledModColorValue = n3;
      this.disabledModColorValue = n4;
      this.modNameTextColorValue = n5;
      this.buttonBackgroundColorValue = n6;
      this.hoverBorderThicknessValue = f;
      this.buttonCornerRadiusValue = f2;
      this.selectedButtonColorValue = n7;
      this.disabledButtonColorValue = n8;
      this.modsHighlightColorValue = n9;
      this.normalHighlightColorValue = n10;
      this.labelHighlightColorValue = n11;
      this.enableModHighlightColorValue = n12;
      this.disableModHighlightColorValue = n13;
      this.selectedOutlineColorValue = n14;
      this.unselectedOutlineColorValue = n15;
      this.unselectedOverlayColorValue = n16;
      this.selectedOverlayColorValue = n17;
      this.selecHoveringOlColorValue = n18;
      this.unselecHoveringOlColorValue = n19;
      this.unselecHoveringOvColorValue = n20;
      this.selecHoveringOvColorValue = n21;
      this.snapLineColorValue = n22;
      this.neighboringModOverlayColorValue = n23;
      this.hudBorderColorValue = n24;
      this.selectionOutlineColorValue = n25;
      this.selectionColorValue = n26;
      this.trackColorValue = n27;
      this.thumbColorValue = n28;
      this.thumbOutlineColorValue = n29;
      this.thumbColorValue2 = n30;
      this.thumbHoverColorValue = n31;
      this.trackColorValue2 = n32;
      this.trackHoverColorValue = n33;
      this.primaryTextColorValue = n34;
      this.textColor2Value = n35;
      this.textColor3Value = n36;
      this.textColor4Value = n37;
      this.textColor5Value = n38;
      this.intValue2 = n39;
      this.primaryTextColorValue2 = n40;
      this.textColor2Value2 = n41;
      this.textColor3Value2 = n42;
      this.textColor4Value2 = n43;
   }

   public void setThumbHoverColorValue(int n) {
      this.thumbHoverColorValue = n;
   }

   public Setting getDisabledButtonColor() {
      return this.disabledButtonColor;
   }

   public int getTextColor2Value() {
      return this.textColor2Value;
   }

   public void setIntValue2(int n) {
      this.intValue2 = n;
   }

   public Setting getSelecHoveringOvColor() {
      return this.selecHoveringOvColor;
   }

   public void setSelectionOutlineColorValue(int n) {
      this.selectionOutlineColorValue = n;
   }

   public void setModsHighlightColor(Setting setting) {
      this.modsHighlightColor = setting;
   }

   public Setting getTextColor42() {
      return this.textColor42;
   }

   public int getTextColor3Value2() {
      return this.textColor3Value2;
   }

   public void setEnabledModColorValue(int n) {
      this.enabledModColorValue = n;
   }

   public void setTrackColor2(Setting setting) {
      this.trackColor2 = setting;
   }

   public void setPrimaryBackgroundColor(Setting setting) {
      this.primaryBackgroundColor = setting;
   }

   public int getTextColor3Value() {
      return this.textColor3Value;
   }

   public void setThumbColorValue2(int n) {
      this.thumbColorValue2 = n;
   }

   public int getTrackColorValue() {
      return this.trackColorValue;
   }

   public int getSnapLineColorValue() {
      return this.snapLineColorValue;
   }

   public Setting getSelectionColor() {
      return this.selectionColor;
   }
}
