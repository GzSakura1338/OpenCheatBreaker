package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.cosmetic.Cosmetic;
import com.cheatbreaker.client.cosmetic.CosmeticType;
import com.cheatbreaker.client.cosmetic.RecentCosmeticsManager;
import com.cheatbreaker.client.emote.AbstractEmote;
import com.cheatbreaker.client.emote.EmoteManager;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.component.CosmeticItemComponent;
import com.cheatbreaker.client.ui.component.IconButton;
import com.cheatbreaker.client.ui.component.TextButton;
import com.cheatbreaker.client.ui.component.TextField;
import com.cheatbreaker.client.ui.screen.EmoteEntry;
import com.cheatbreaker.client.ui.screen.MainMenuScreen;
import com.cheatbreaker.client.ui.screen.MenuScreen;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import com.cheatbreaker.client.ui.screen.PlayerViewer;
import com.cheatbreaker.client.util.ClientValidator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class CosmeticsScreen extends MenuScreen {
    public List<CosmeticItemComponent> cosmeticItems = new ArrayList<CosmeticItemComponent>();
    public static final int ITEMS_PER_PAGE = 18;
    public IconButton previousPageButton = new IconButton(4.0f, new ResourceLocation("client/icons/left.png"));
    public static final int CONSTANT_100 = 100;
    public static final int CONSTANT_3 = 3;
    public static final double CONSTANT_33_0 = 33.0;
    public static final int CONSTANT_8 = 8;
    public IconButton nextPageButton = new IconButton(4.0f, new ResourceLocation("client/icons/right.png"));
    public TextButton capes = new TextButton("CAPES");
    public static final float CONSTANT_150_0 = 150.0f;
    public TextButton wings = new TextButton("WINGS");
    public TextButton hats = new TextButton("HATS");
    public static final int CONSTANT_2 = 2;
    public TextButton recent = new TextButton("RECENT");
    public TextButton emotes = new TextButton("EMOTES");
    public TextButton back = new TextButton("BACK");
    public static final int CONSTANT_0 = 0;
    public static final int CONSTANT_9 = 9;
    public static final double CONSTANT_11_0 = 11.0;
    public TextButton claim = new TextButton("CLAIM");
    public static final double CONSTANT_48_0 = 48.0;
    public int pageOffset = 0;
    public int activeTab = 0;
    public static final int CONSTANT_4 = 4;
    public int selectedWheelSlot = 0;
    public int emotePage = 0;
    public int hoveredWheelSlot = -1;
    public List<EmoteEntry> visibleEmotes = new ArrayList<EmoteEntry>();
    public static final int CONSTANT_5 = 5;
    public static final int CONSTANT_1 = 1;
    public List<EmoteEntry> filteredEmotes = new ArrayList<EmoteEntry>();
    public static final int CONSTANT_2_2 = 2;
    public TextField searchCapes;

    public CosmeticsScreen() {
        this.searchCapes = new TextField(CheatBreaker.getInstance().getFontManager().playRegular14, "Search Capes...", 0x2FFFFFFF, 0x6FFFFFFF);
        this.searchCapes.setMaxStringLength(256);
        this.layoutCosmetics(this.getCosmeticsOfType("cape"));
        this.refreshEmotes();
    }

    public void layoutCosmetics(List<Cosmetic> list) {
        Set<String> set = RecentCosmeticsManager.getFavorites();
        ArrayList<Cosmetic> arrayList = new ArrayList<Cosmetic>(list);
        Comparator<Cosmetic> comparator = Comparator.comparing(cosmetic -> !cosmetic.isEquipped());
        if (this.activeTab != 3) {
            comparator = comparator.thenComparing(cosmetic -> !set.contains(RecentCosmeticsManager.getKey(cosmetic)));
        }
        arrayList.sort(comparator);
        this.cosmeticItems.clear();
        for (Cosmetic cosmetic2 : arrayList) {
            CosmeticItemComponent cosmeticItemComponent = new CosmeticItemComponent(cosmetic2, 1.0f);
            cosmeticItemComponent.setHovered(set.contains(RecentCosmeticsManager.getKey(cosmetic2)));
            this.cosmeticItems.add(cosmeticItemComponent);
        }
    }

    public List<Integer> getEmoteWheelKeybinds() {
        ArrayList arrayList = CheatBreaker.getInstance().getKeybindSettings().emoteWheelQuickActionKeybinds.getListValue();
        while (arrayList.size() < 8) {
            arrayList.add(-1);
        }
        return arrayList;
    }

    public void setWheelKeybind(int n, int n2) {
        Setting setting = CheatBreaker.getInstance().getKeybindSettings().emoteWheelQuickActionKeybinds;
        List<Integer> list = this.getEmoteWheelKeybinds();
        list.set(n, n2);
        setting.changeValue(list);
    }

    public List<Cosmetic> getCurrentCosmetics() {
        if (this.activeTab == 3) {
            return this.getAllCosmetics();
        }
        if (this.activeTab == 4) {
            return this.getCosmeticsOfType("hat");
        }
        return this.getCosmeticsOfType(this.activeTab == 1 ? "dragon_wings" : "cape");
    }

    public String getKeyLabel(int n) {
        if (n < 0) {
            return "-";
        }
        String string = Keyboard.getKeyName((int)n);
        return string != null ? string : "?";
    }

    public int getColumnX(int n) {
        if (this.activeTab == 3) {
            return -235 + n * 102;
        }
        return n == 0 ? -235 : -75;
    }

    public void drawEmotesTab(float f, float f2, float f3, float f4) {
        int n;
        Object object;
        float f5;
        int n2;
        CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredString("All Emotes", f3 - 82.0f, f4 - 88.0f, -1);
        float f6 = f3 - 245.0f;
        float f7 = f3 - 80.0f;
        float f8 = f4 - 75.0f;
        float f9 = 16.0f;
        int n3 = this.emotePage * 18;
        for (n2 = 0; n2 < 2; ++n2) {
            f5 = n2 == 0 ? f6 : f7;
            for (int i = 0; i < 9; ++i) {
                boolean bl;
                int n4 = n3 + n2 * 9 + i;
                if (n4 >= this.filteredEmotes.size()) continue;
                object = this.filteredEmotes.get(n4);
                float f10 = f8 + (float)i * f9;
                boolean bl2 = bl = f >= f5 && f <= f5 + 150.0f && f2 >= f10 && f2 <= f10 + f9;
                if (bl) {
                    Gui.drawRect(f5, f10, f5 + 150.0f, f10 + f9, 0x40FFFFFF);
                }
                RenderUtils.applyColor(-1);
                RenderUtils.drawSquareTextureViaRenderEngine(((EmoteEntry)object).abstractEmote.getClientEmotes(), 7.0f, f5 + 2.0f, f10 + 1.0f);
                CheatBreaker.getInstance().getFontManager().playRegular16.drawString(((EmoteEntry)object).name, f5 + 22.0f, f10 + 3.0f, -1);
            }
        }
        if (this.filteredEmotes.isEmpty()) {
            CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredString(this.visibleEmotes.isEmpty() ? "Join our Discord for giveaways on emotes and other cosmetics!" : "There are no emotes matching your query.", f3 - 82.0f, f4 - 20.0f, -1);
        }
        if ((n2 = Math.max(1, (this.filteredEmotes.size() + 18 - 1) / 18)) > 1) {
            this.previousPageButton.setBounds(f3 - 30.0f, f4 + 84.0f, 15.0f, 15.0f);
            this.nextPageButton.setBounds(f3 + 25.0f, f4 + 84.0f, 15.0f, 15.0f);
            this.drawPageIndicator(this.emotePage, n2, f3, f4);
            this.previousPageButton.renderAndTestHover(f, f2, true);
            this.nextPageButton.renderAndTestHover(f, f2, true);
            this.previousPageButton.setEnabled(this.emotePage > 0);
            this.nextPageButton.setEnabled(this.emotePage < n2 - 1);
        }
        this.searchCapes.setBounds(f3 - 235.0f, f4 + 84.0f, 140.0f, 15.0f);
        this.searchCapes.render(f, f2, true);
        f5 = f3 + 172.0f;
        float f11 = f4 - 5.0f;
        CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredString("Quick Actions Wheel", f5, f4 - 88.0f, -1);
        List<Integer> list = this.getEmoteWheelActions();
        object = this.getEmoteWheelKeybinds();
        RenderUtils.applyColor(Integer.MIN_VALUE);
        RenderUtils.drawProgressRing(f5, f11, 50.0, 48.0, 100.0, 100, 100.0);
        RenderUtils.drawProgressRing(f5, f11, 11.0, 9.0, 100.0, 100, 100.0);
        for (n = 0; n < 8; ++n) {
            String string;
            float f12 = 22.5f + (float)n * 45.0f;
            RenderUtils.applyColor(n == this.selectedWheelSlot ? CheatBreaker.getInstance().getKeybindSettings().emoteRadialColor.getColorValue() | 0xFF000000 : 0x4D4D4D4D);
            RenderUtils.drawArc(f5, f11, 48.0, 11.0, 360.0 - (double)f12 + 90.0, 360.0 - (double)f12 + 90.0 + 45.0);
            double d = Math.toRadians((double)f12 - 90.0 - 22.5);
            double d2 = (double)f5 + 33.0 * Math.cos(d);
            double d3 = (double)f11 + 33.0 * Math.sin(d);
            int n5 = list.get(n);
            AbstractEmote abstractEmote = this.getEmote(n5);
            if (abstractEmote != null) {
                RenderUtils.applyColor(-1);
                RenderUtils.drawSquareTextureViaRenderEngine(abstractEmote.getClientEmotes(), 7.0f, (float)d2 - 7.0f, (float)d3 - 7.0f);
            }
            boolean bl = this.hoveredWheelSlot == n;
            String string2 = string = bl ? "..." : this.getKeyLabel((Integer)((List)object).get(n));
            int n6 = bl ? -171 : ((Integer)((List)object).get(n) >= 0 ? -5592406 : -11184811);
            CheatBreaker.getInstance().getFontManager().playRegular12.drawCenteredString(string, (float)d2, (float)d3 + 9.0f, n6);
        }
        n = list.get(this.selectedWheelSlot);
        AbstractEmote abstractEmote = this.getEmote(n);
        String string = "Slot " + (this.selectedWheelSlot + 1) + ": " + (abstractEmote != null ? abstractEmote.getDisplayName() : "Empty");
        CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredString(string, f5, f11 + 48.0f + 20.0f, -1);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawCenteredString("Click a slot, then click an emote to assign it.", f5, f11 + 48.0f + 34.0f, -1426063361);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawCenteredString("Right click a slot to clear it.", f5, f11 + 48.0f + 46.0f, -1426063361);
    }

    public int getColumn(CosmeticItemComponent cosmeticItemComponent, int n) {
        if (this.activeTab == 3) {
            CosmeticType cosmeticType = cosmeticItemComponent.getCosmetic().getCosmeticType();
            return cosmeticType == CosmeticType.CAPE ? 0 : (cosmeticType == CosmeticType.WINGS ? 1 : 2);
        }
        return n < 5 ? 0 : 1;
    }

    public void drawSectionTitle(String string, int n, boolean bl) {
        float f = (float)(Math.round(this.getScaledWidth() / 2.0f) + this.getColumnX(n)) + (float)this.getItemWidth() / 2.0f;
        float f2 = Math.round(this.getScaledHeight() / 2.0f);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawCenteredString(string, f, f2 - 91.0f, -1426063361);
        if (bl) {
            CheatBreaker.getInstance().getFontManager().playRegular14.drawCenteredString("None", f, f2 - 70.0f, 0x66FFFFFF);
        }
    }

    public void drawCosmeticsGrid(float f, float f2, int n) {
        double d;
        int n2;
        float f3;
        float f4 = Math.round(this.getScaledWidth() / 2.0f);
        float f5 = Math.round(this.getScaledHeight() / 2.0f);
        float f6 = f4 - 245.0f;
        float f7 = f4 - 80.0f;
        float f8 = f5 - 75.0f;
        float f9 = 16.0f;
        int n3 = Math.max(1, (this.filteredEmotes.size() + 18 - 1) / 18);
        if (this.emotePage > 0 && this.previousPageButton.containsPoint(f, f2)) {
            --this.emotePage;
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
            return;
        }
        if (this.emotePage < n3 - 1 && this.nextPageButton.containsPoint(f, f2)) {
            ++this.emotePage;
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
            return;
        }
        int n4 = this.emotePage * 18;
        for (int i = 0; i < 2; ++i) {
            f3 = i == 0 ? f6 : f7;
            for (n2 = 0; n2 < 9; ++n2) {
                int n5 = n4 + i * 9 + n2;
                if (n5 >= this.filteredEmotes.size()) continue;
                float f10 = f8 + (float)n2 * f9;
                if (!(f >= f3) || !(f <= f3 + 150.0f) || !(f2 >= f10) || !(f2 <= f10 + f9)) continue;
                EmoteEntry emoteEntry = this.filteredEmotes.get(n5);
                this.setWheelAction(this.selectedWheelSlot, emoteEntry.emoteId);
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
                return;
            }
        }
        float f11 = f4 + 172.0f;
        f3 = f5 - 5.0f;
        for (n2 = 0; n2 < 8; ++n2) {
            float f12 = 22.5f + (float)n2 * 45.0f;
            double d2 = Math.toRadians((double)f12 - 90.0 - 22.5);
            d = (double)f11 + 33.0 * Math.cos(d2);
            double d3 = (double)f3 + 33.0 * Math.sin(d2);
            float f13 = (float)d - 10.0f;
            float f14 = (float)d3 + 6.0f;
            if (!(f >= f13) || !(f <= f13 + 20.0f) || !(f2 >= f14) || !(f2 <= f14 + 9.0f)) continue;
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
            if (n == 1) {
                this.setWheelKeybind(n2, -1);
                this.hoveredWheelSlot = -1;
            } else {
                this.hoveredWheelSlot = this.hoveredWheelSlot == n2 ? -1 : n2;
            }
            return;
        }
        double d4 = f - f11;
        double d5 = f2 - f3;
        d = Math.sqrt(d4 * d4 + d5 * d5);
        if (d >= 9.0 && d <= 52.0) {
            int n6 = -1;
            double d6 = Double.MAX_VALUE;
            for (int i = 0; i < 8; ++i) {
                double d7;
                float f15 = 22.5f + (float)i * 45.0f;
                double d8 = Math.toRadians((double)f15 - 90.0 - 22.5);
                double d9 = (double)f11 + 33.0 * Math.cos(d8);
                double d10 = ((double)f - d9) * ((double)f - d9) + ((double)f2 - (d7 = (double)f3 + 33.0 * Math.sin(d8))) * ((double)f2 - d7);
                if (!(d10 < d6)) continue;
                d6 = d10;
                n6 = i;
            }
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
            if (n == 1) {
                this.setWheelAction(n6, -1);
            } else {
                this.selectedWheelSlot = n6;
            }
        }
    }

    public void updateEmoteSelection() {
        int n2;
        Set set;
        Set set2 = this.visibleEmotes.stream().map(emoteEntry -> emoteEntry.emoteId).collect(Collectors.toSet());
        if (!set2.equals(set = CheatBreaker.getInstance().getEmoteManager().getOwnedEmotes().stream().filter(n -> EmoteManager.emoteClassIds.containsKey(n) || EmoteManager.resourceEmoteIds.containsKey(n)).collect(Collectors.toSet()))) {
            this.refreshEmotes();
        }
        if (this.activeTab == 2) {
            return;
        }
        List<Cosmetic> list = this.getSearchResults();
        boolean bl = list.size() != this.cosmeticItems.size();
        for (n2 = 0; !bl && n2 < list.size(); ++n2) {
            bl = !list.contains(this.cosmeticItems.get(n2).getCosmetic());
        }
        if (bl) {
            this.layoutCosmetics(list);
            n2 = Math.max(0, (this.cosmeticItems.size() - 1) / 10) * 2;
            this.pageOffset = Math.min(this.pageOffset, n2);
        }
    }

    public boolean hasItems() {
        if (this.activeTab == 2) {
            return !this.visibleEmotes.isEmpty();
        }
        return !this.getCurrentCosmetics().isEmpty();
    }

    public boolean isOnCurrentPage(int n) {
        return this.activeTab == 3 || n >= this.pageOffset * 5 && n < (this.pageOffset + 2) * 5;
    }

    public void setWheelAction(int n, int n2) {
        Setting setting = CheatBreaker.getInstance().getKeybindSettings().emoteWheelQuickActions;
        List<Integer> list = this.getEmoteWheelActions();
        list.set(n, n2);
        setting.changeValue(list);
        CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendEmoteWheel();
    }

    @Override
    public void drawScreenScaled(float f, float f2) {
        if (Minecraft.getMinecraft().theWorld == null) {
            super.drawScreenScaled(f, f2);
        }
        if (!CheatBreaker.getInstance().isWebsocketConnected()) {
            ClientValidator.drawMessage(this.getScaledWidth() / 2.0f, this.getScaledHeight() / 2.0f - 10.0f);
            this.back.setBounds(this.getScaledWidth() / 2.0f - 30.0f, this.getScaledHeight() / 2.0f + 16.0f, 60.0f, 12.0f);
            this.back.renderAndTestHover(f, f2, true);
        } else {
            float f3 = Math.round(this.getScaledWidth() / 2.0f);
            float f4 = Math.round(this.getScaledHeight() / 2.0f);
            RenderUtils.drawRoundedRectFilledPrecise(f3 - 250.0f, f4 - 95.0f, f3 + 85.0f, f4 + 110.0f, 3.0, 0x63000000);
            RenderUtils.drawRoundedRectFilledPrecise(f3 + 95.0f, f4 - 95.0f, f3 + 250.0f, f4 + 110.0f, 3.0, 0x63000000);
            RenderUtils.drawRoundedRectFilledPrecise(f3 - 235.0f, f4 + 75.0f, f3 + 70.0f, f4 + 76.0f, 0.0, 0x50F5F5F5);
            this.back.setBounds(this.getScaledWidth() / 2.0f - 30.0f, this.getScaledHeight() / 2.0f + 125.0f, 60.0f, 12.0f);
            this.back.renderAndTestHover(f, f2, true);
            this.capes.setFlag(this.activeTab == 0);
            this.capes.setBounds(f3 - 250.0f, f4 - 109.0f, 50.0f, 12.0f);
            this.capes.renderAndTestHover(f, f2, true);
            this.wings.setFlag(this.activeTab == 1);
            this.wings.setBounds(f3 - 195.0f, f4 - 109.0f, 50.0f, 12.0f);
            this.wings.renderAndTestHover(f, f2, true);
            this.hats.setFlag(this.activeTab == 4);
            this.hats.setBounds(f3 - 140.0f, f4 - 109.0f, 50.0f, 12.0f);
            this.hats.renderAndTestHover(f, f2, true);
            this.recent.setFlag(this.activeTab == 3);
            this.recent.setBounds(f3 - 30.0f, f4 - 109.0f, 50.0f, 12.0f);
            this.recent.renderAndTestHover(f, f2, true);
            this.emotes.setFlag(this.activeTab == 2);
            this.emotes.setBounds(f3 - 85.0f, f4 - 109.0f, 50.0f, 12.0f);
            this.emotes.renderAndTestHover(f, f2, true);
            if (this.activeTab == 2) {
                this.drawEmotesTab(f, f2, f3, f4);
                return;
            }
            float f5 = f3 + (float)this.width - 50.0f - f;
            float f6 = f4 + 30.0f - f2;
            PlayerViewer.render(f3 + 170.0f, f4 + 10.0f, f5, f6, f, f2 + 50.0f);
            CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredString("Hint: Click to drag.", f3 + 170.0f, f4 + 72.0f, -419430401);
            if (this.cosmeticItems.isEmpty()) {
                String string = this.hasItems() ? "There are no cosmetics matching your query." : (this.activeTab == 3 ? "Capes, wings and hats you equip will show up here." : "Join our Discord for giveaways on emotes and other cosmetics!");
                CheatBreaker.getInstance().getFontManager().playRegular18.drawCenteredString(string, this.getScaledWidth() / 2.0f - 75.0f, this.getScaledHeight() / 2.0f + 4.0f, -1);
            }
            int n = 0;
            float[] fArray = new float[3];
            for (CosmeticItemComponent cosmeticItemComponent : this.cosmeticItems) {
                if (!this.isOnCurrentPage(n++)) continue;
                int n2 = this.getColumn(cosmeticItemComponent, n - 1 - this.pageOffset * 5);
                int n3 = (int)this.getScaledWidth() / 2 + this.getColumnX(n2);
                cosmeticItemComponent.setBounds(n3, (int)(this.getScaledHeight() / 2.0f - 80.0f + fArray[n2]), this.getItemWidth(), cosmeticItemComponent.getHeight());
                cosmeticItemComponent.render((int)f, (int)f2, 1.0f);
                int n4 = n2;
                fArray[n4] = fArray[n4] + (float)cosmeticItemComponent.getHeight();
            }
            if (this.activeTab == 3 && !this.cosmeticItems.isEmpty()) {
                this.drawSectionTitle("Recent Capes", 0, fArray[0] == 0.0f);
                this.drawSectionTitle("Recent Wings", 1, fArray[1] == 0.0f);
                this.drawSectionTitle("Recent Hats", 2, fArray[2] == 0.0f);
            }
            if (this.cosmeticItems.size() > 5 && this.activeTab != 3) {
                this.previousPageButton.setBounds(this.getScaledWidth() / 2.0f - 30.0f, this.getScaledHeight() / 2.0f + 84.0f, 15.0f, 15.0f);
                this.nextPageButton.setBounds(this.getScaledWidth() / 2.0f + 25.0f, this.getScaledHeight() / 2.0f + 84.0f, 15.0f, 15.0f);
                this.drawPageIndicator(this.pageOffset / 2, (this.cosmeticItems.size() + 9) / 10, this.getScaledWidth() / 2.0f, this.getScaledHeight() / 2.0f);
                this.previousPageButton.renderAndTestHover(f, f2, true);
                this.nextPageButton.renderAndTestHover(f, f2, true);
                this.previousPageButton.setEnabled(this.pageOffset != 0);
                this.nextPageButton.setEnabled((float)(this.pageOffset + 2) < (float)this.cosmeticItems.size() / 5.0f);
            }
            this.searchCapes.setBounds(this.getScaledWidth() / 2.0f - 235.0f, this.getScaledHeight() / 2.0f + 84.0f, 140.0f, 15.0f);
            this.searchCapes.render(f, f2, true);
        }
    }

    public void filterEmotes(String string) {
        this.filteredEmotes.clear();
        if (string == null || string.isEmpty()) {
            this.filteredEmotes.addAll(this.visibleEmotes);
            return;
        }
        String string2 = string.toLowerCase();
        for (EmoteEntry emoteEntry : this.visibleEmotes) {
            if (!emoteEntry.name.toLowerCase().contains(string2)) continue;
            this.filteredEmotes.add(emoteEntry);
        }
    }

    @Override
    public void mouseClickedScaled(float f, float f2, int n) throws java.io.IOException {
        if (Minecraft.getMinecraft().theWorld == null) {
            super.mouseClickedScaled(f, f2, n);
        }
        if (!this.searchCapes.containsPoint(f, f2) && this.searchCapes.isFocused()) {
            this.searchCapes.setFocused(false);
        } else if (this.searchCapes.containsPoint(f, f2) && !this.searchCapes.isFocused()) {
            this.searchCapes.setFocused(true);
        }
        if (this.wings.containsPoint(f, f2)) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
            this.activeTab = 1;
            this.searchCapes.setPlaceholder("Search Wings...");
            this.searchCapes.setText("");
            this.pageOffset = 0;
            this.layoutCosmetics(this.getCosmeticsOfType("dragon_wings"));
        }
        if (this.hats.containsPoint(f, f2)) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
            this.activeTab = 4;
            this.searchCapes.setPlaceholder("Search Hats...");
            this.searchCapes.setText("");
            this.pageOffset = 0;
            this.layoutCosmetics(this.getCosmeticsOfType("hat"));
        }
        if (this.capes.containsPoint(f, f2)) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
            this.activeTab = 0;
            this.searchCapes.setPlaceholder("Search Capes...");
            this.searchCapes.setText("");
            this.pageOffset = 0;
            this.layoutCosmetics(this.getCosmeticsOfType("cape"));
        }
        if (this.recent.containsPoint(f, f2)) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
            this.activeTab = 3;
            this.searchCapes.setPlaceholder("Search Recent...");
            this.searchCapes.setText("");
            this.pageOffset = 0;
            this.layoutCosmetics(this.getSearchResults());
        }
        if (this.emotes.containsPoint(f, f2)) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
            this.activeTab = 2;
            this.searchCapes.setPlaceholder("Search Emotes...");
            this.searchCapes.setText("");
            this.emotePage = 0;
            this.filterEmotes("");
        }
        if (this.claim.containsPoint(f, f2)) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
            this.mc2.displayGuiScreen(new MainMenuScreen());
        } else if (this.back.containsPoint(f, f2)) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
            if (Minecraft.getMinecraft().theWorld != null) {
                this.mc2.displayGuiScreen(new ModsScreen());
            } else {
                this.mc2.displayGuiScreen(new MainMenuScreen());
            }
        } else if (this.activeTab == 2) {
            this.drawCosmeticsGrid(f, f2, n);
        } else {
            if (this.cosmeticItems.size() > 5 && this.activeTab != 3) {
                if (this.pageOffset > 0 && this.previousPageButton.containsPoint(f, f2)) {
                    this.pageOffset -= 2;
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
                } else if (this.nextPageButton.containsPoint(f, f2) && (float)(this.pageOffset + 2) < (float)this.cosmeticItems.size() / 5.0f) {
                    this.pageOffset += 2;
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
                }
            }
            int n2 = 0;
            CosmeticItemComponent cosmeticItemComponent = null;
            boolean bl = false;
            for (CosmeticItemComponent cosmeticItemComponent2 : this.cosmeticItems) {
                if (!this.isOnCurrentPage(n2++)) continue;
                if (n == 1) {
                    if (!this.isOver(cosmeticItemComponent2, f, f2)) continue;
                    cosmeticItemComponent = cosmeticItemComponent2;
                    continue;
                }
                if (this.isOver(cosmeticItemComponent2, f, f2)) {
                    bl = true;
                }
                cosmeticItemComponent2.mouseClicked((int)f, (int)f2, n);
            }
            if (cosmeticItemComponent != null) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
                this.toggleCosmetic(cosmeticItemComponent.getCosmetic());
            } else if (bl) {
                this.layoutCosmetics(this.getSearchResults());
            }
        }
    }

    public List<Cosmetic> filterCosmetics(String string) {
        String string2 = string == null ? "" : string.toLowerCase().replace(" ", "_");
        return this.getCurrentCosmetics().stream().filter(cosmetic -> string2.isEmpty() || cosmetic.getName().toLowerCase().contains(string2)).collect(Collectors.toList());
    }

    @Override
    public void updateScreen() throws java.io.IOException {
        super.updateScreen();
        this.searchCapes.tick();
        this.updateEmoteSelection();
    }

    public List<Cosmetic> getSearchResults() {
        return this.filterCosmetics(this.searchCapes.getText());
    }

    public List<Cosmetic> getCosmeticsOfType(String string) {
        if (string == null || string.equalsIgnoreCase("all")) {
            return new ArrayList<Cosmetic>(CheatBreaker.getInstance().getCosmeticsManager().getList());
        }
        return CheatBreaker.getInstance().getCosmeticsManager().getList().stream().filter(cosmetic -> cosmetic.getCosmeticType().getId().toLowerCase().contains(string.toLowerCase())).collect(Collectors.toList());
    }

    public List<Cosmetic> getAllCosmetics() {
        List<Cosmetic> list = this.getCosmeticsOfType("all");
        RecentCosmeticsManager.registerEquipped(list);
        ArrayList<Cosmetic> arrayList = new ArrayList<Cosmetic>();
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        block0: for (String string : RecentCosmeticsManager.getRecent()) {
            for (Cosmetic cosmetic : list) {
                if (!RecentCosmeticsManager.getKey(cosmetic).equals(string)) continue;
                int n4 = cosmetic.getCosmeticType() == CosmeticType.CAPE ? n++ : (cosmetic.getCosmeticType() == CosmeticType.WINGS ? n2++ : n3++);
                if (n4 >= 5) continue block0;
                arrayList.add(cosmetic);
                continue block0;
            }
        }
        return arrayList;
    }

    public AbstractEmote getEmote(int n) {
        EmoteManager emoteManager = CheatBreaker.getInstance().getEmoteManager();
        return n >= 0 && emoteManager.getOwnedEmotes().contains(n) ? emoteManager.createEmote(n) : null;
    }

    public List<Integer> getEmoteWheelActions() {
        ArrayList arrayList = CheatBreaker.getInstance().getKeybindSettings().emoteWheelQuickActions.getListValue();
        while (arrayList.size() < 8) {
            arrayList.add(-1);
        }
        return arrayList;
    }

    public int getItemWidth() {
        return this.activeTab == 3 ? 100 : 152;
    }

    public void drawPageIndicator(int n, int n2, float f, float f2) {
        CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredString(n + 1 + " / " + n2, f + 5.0f, f2 + 86.5f, -1);
    }

    @Override
    public void keyTyped(char c, int n) throws java.io.IOException {
        if (this.hoveredWheelSlot >= 0) {
            if (n != 1) {
                this.setWheelKeybind(this.hoveredWheelSlot, n);
            }
            this.hoveredWheelSlot = -1;
            return;
        }
        if (this.searchCapes.isFocused()) {
            this.searchCapes.keyTyped(c, n);
            this.pageOffset = 0;
            this.emotePage = 0;
            if (this.activeTab == 2) {
                this.filterEmotes(this.searchCapes.getText());
            } else {
                this.layoutCosmetics(this.getSearchResults());
            }
        }
        super.keyTyped(c, n);
    }

    public boolean isOver(CosmeticItemComponent cosmeticItemComponent, float f, float f2) {
        return f > (float)cosmeticItemComponent.x && f < (float)(cosmeticItemComponent.x + cosmeticItemComponent.width) && f2 > (float)cosmeticItemComponent.y && f2 < (float)(cosmeticItemComponent.y + cosmeticItemComponent.height);
    }

    public void toggleCosmetic(Cosmetic cosmetic) {
        RecentCosmeticsManager.toggleFavorite(cosmetic);
        this.layoutCosmetics(this.getSearchResults());
    }

    public void refreshEmotes() {
        EmoteManager emoteManager = CheatBreaker.getInstance().getEmoteManager();
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (Object k : EmoteManager.emoteClassIds.keySet()) {
            arrayList.add((Integer)k);
        }
        arrayList.addAll(EmoteManager.resourceEmoteIds.keySet());
        this.visibleEmotes.clear();
        Iterator<Integer> iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            int n = (Integer)iterator.next();
            AbstractEmote abstractEmote = emoteManager.createEmote(n);
            if (abstractEmote == null || !emoteManager.getOwnedEmotes().contains(n)) continue;
            this.visibleEmotes.add(new EmoteEntry(n, abstractEmote));
        }
        this.visibleEmotes.sort(Comparator.comparing(emoteEntry -> emoteEntry.abstractEmote.getDisplayName(), String.CASE_INSENSITIVE_ORDER));
        this.filterEmotes(this.activeTab == 2 ? this.searchCapes.getText() : "");
        int n = Math.max(1, (this.filteredEmotes.size() + 18 - 1) / 18);
        this.emotePage = Math.min(this.emotePage, n - 1);
    }
}
