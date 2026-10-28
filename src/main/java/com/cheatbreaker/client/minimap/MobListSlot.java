package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.util.Translations;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class MobListSlot extends GuiSlot {
    public SelectMobsScreen selectMobsScreen;
    public RadarSettings radarSettings;
    public ArrayList<String> stringList;

    @Override
    public void elementClicked(int n, boolean flag, int n2, int n3) {
        this.selectMobsScreen.setSelectedMob(this.stringList.get(n));
        int n4 = this.selectMobsScreen.getWidth() / 2 - 92 - 16;
        byte b = 3;
        short s = 215;
        if (this.mouseX >= n4 + s - 16 - b && this.mouseX <= n4 + s + b) {
            this.selectMobsScreen.toggleSelected();
        } else if (flag) {
            Mouse.next();
            this.selectMobsScreen.toggleSelected();
            return;
        }
    }

    @Override
    public void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6) {
        String str = this.stringList.get(n);
        boolean flag = false;
        boolean flag2 = false;
        boolean flag3 = true;
        MobType findMobType = MobType.findMobType(str);
        if (findMobType != null) {
            flag = findMobType.flag2;
            flag2 = findMobType.flag3;
            flag3 = findMobType.selected;
        } else {
            CustomMob findCustomMob = CustomMobs.findCustomMob(str);
            if (findCustomMob != null) {
                flag = findCustomMob.hostile;
                flag2 = findCustomMob.neutral;
                flag3 = findCustomMob.enabled;
            }
        }

        int n7 = flag ? 255 : 0;
        int n8 = flag2 ? 255 : 0;
        int n9 = -16777216 + (n7 << 16) + (n8 << 8) + 0;
        this.selectMobsScreen
            .drawCenteredString(this.selectMobsScreen.getFontRendererObj(), getLocalizedMobName(str), this.selectMobsScreen.getWidth() / 2, n3 + 3, n9);
        byte b = 3;
        if (this.mouseX >= n2 - b && this.mouseY >= n3 && this.mouseX <= n2 + 215 + b && this.mouseY <= n3 + this.slotHeight) {
            String str2;
            if (this.mouseX >= n2 + 215 - 16 - b && this.mouseX <= n2 + 215 + b) {
                str2 = flag3 ? Translations.translate("options.minimap.mobs.disable") : Translations.translate("options.minimap.mobs.enable");
            } else {
                str2 = flag3 ? Translations.translate("options.minimap.mobs.enabled") : Translations.translate("options.minimap.mobs.disabled");
            }

            SelectMobsScreen.setTooltip(this.selectMobsScreen, str2);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlUtils.bindTextureByName("textures/gui/container/inventory.png");
        int n10 = flag3 ? 72 : 90;
        short s = 216;
        Gui.drawTexturedRect(n2 + 198, n3 - 2, n10, s, 16, 16);
    }

    @Override
    public int getContentHeight() {
        return this.getSize() * 18;
    }

    @Override
    public boolean isSelected(int n) {
        return this.stringList.get(n).equals(this.selectMobsScreen.selectedMob);
    }

    public static String getLocalizedMobName(String str) {
        str = StatCollector.translateToLocal("entity." + str + ".name");
        return str.replaceAll("^entity.", "").replaceAll(".name$", "");
    }

    @Override
    public void drawBackground() {
        this.selectMobsScreen.drawDefaultBackground();
    }

    @Override
    public int getSize() {
        return this.stringList.size();
    }

    public MobListSlot(SelectMobsScreen selectMobsScreen) {
        super(selectMobsScreen.radarSettings.mc, selectMobsScreen.getWidth(), selectMobsScreen.getHeight(), 32, selectMobsScreen.getHeight() - 41 + 4, 18);
        this.selectMobsScreen = selectMobsScreen;
        this.radarSettings = this.selectMobsScreen.radarSettings;
        this.stringList = new ArrayList<>();

        for (MobType mobType : MobType.values()) {
            if (mobType.flag && (mobType.flag2 && this.radarSettings.showHostiles || mobType.flag3 && this.radarSettings.showNeutrals)) {
                this.stringList.add(mobType.mobName);
            }
        }

        for (CustomMob customMob : CustomMobs.customMobList) {
            if (customMob.hostile && this.radarSettings.showHostiles || customMob.neutral && this.radarSettings.showNeutrals) {
                this.stringList.add(customMob.name);
            }
        }

        final Collator collator = Translations.getCollator();
        Collections.sort(this.stringList, new Comparator<String>() {
            public int compare(String str, String str2) {
                str = MobListSlot.getLocalizedMobName(str);
                str2 = MobListSlot.getLocalizedMobName(str2);
                return collator.compare(str, str2);
            }
        });
    }
}
