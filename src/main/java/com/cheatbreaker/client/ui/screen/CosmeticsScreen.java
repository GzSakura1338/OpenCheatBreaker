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
import com.cheatbreaker.client.util.ClientValidator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class CosmeticsScreen extends MenuScreen {
   public static final int CONSTANT_3 = 3;
   public List<CosmeticItemComponent> cosmeticItems = new ArrayList<>();
   public IconButton previousPageButton = new IconButton(4.0F, new ResourceLocation("client/icons/left.png"));
   public IconButton nextPageButton = new IconButton(4.0F, new ResourceLocation("client/icons/right.png"));
   public static final int CONSTANT_8 = 8;
   public static final int CONSTANT_0 = 0;
   public static final double CONSTANT_11_0 = 11.0;
   public TextButton capes = new TextButton("CAPES");
   public TextButton wings = new TextButton("WINGS");
   public TextButton hats = new TextButton("HATS");
   public TextButton recent = new TextButton("RECENT");
   public static final int CONSTANT_4 = 4;
   public static final int CONSTANT_100 = 100;
   public TextButton emotes = new TextButton("EMOTES");
   public static final int CONSTANT_5 = 5;
   public static final int CONSTANT_2 = 2;
   public static final double CONSTANT_33_0 = 33.0;
   public TextButton back = new TextButton("BACK");
   public TextButton claim = new TextButton("CLAIM");
   public static final float CONSTANT_150_0 = 150.0F;
   public int pageOffset = 0;
   public int activeTab = 0;
   public int selectedWheelSlot = 0;
   public static final int CONSTANT_2_2 = 2;
   public int emotePage = 0;
   public int hoveredWheelSlot = -1;
   public List<EmoteEntry> visibleEmotes = new ArrayList<>();
   public List<EmoteEntry> filteredEmotes = new ArrayList<>();
   public static final int CONSTANT_9 = 9;
   public static final double CONSTANT_48_0 = 48.0;
   public static final int CONSTANT_1 = 1;
   public static final int ITEMS_PER_PAGE = 18;
   public TextField searchCapes = new TextField(CheatBreaker.getInstance().getFontManager().playRegular14, "Search Capes...", 805306367, 1879048191);

   public List<Integer> getEmoteWheelActions() {
      ArrayList listValue = CheatBreaker.getInstance().getKeybindSettings().emoteWheelQuickActions.getListValue();

      while (listValue.size() < 8) {
         listValue.add(-1);
      }

      return listValue;
   }

   public void refreshEmotes() {
      EmoteManager emoteManager = CheatBreaker.getInstance().getEmoteManager();
      ArrayList list = new ArrayList();

      for (Object obj : EmoteManager.emoteClassIds.keySet()) {
         list.add((Integer)obj);
      }

      list.addAll(EmoteManager.resourceEmoteIds.keySet());
      this.visibleEmotes.clear();

      for (int n : (Iterable<Integer>)list) {
         AbstractEmote createEmote = emoteManager.createEmote(n);
         if (createEmote != null && emoteManager.getOwnedEmotes().contains(n)) {
            this.visibleEmotes.add(new EmoteEntry(n, createEmote));
         }
      }

      this.visibleEmotes.sort(Comparator.comparing(emoteEntry -> emoteEntry.abstractEmote.getText(), String.CASE_INSENSITIVE_ORDER));
      this.filterEmotes(this.activeTab == 2 ? this.searchCapes.getText() : "");
      int max = Math.max(1, (this.filteredEmotes.size() + 18 - 1) / 18);
      this.emotePage = Math.min(this.emotePage, max - 1);
   }

   public void drawSectionTitle(String str, int n, boolean flag) {
      float f = Math.round(this.getScaledWidth() / 2.0F) + this.getColumnX(n) + this.getItemWidth() / 2.0F;
      float round = Math.round(this.getScaledHeight() / 2.0F);
      CheatBreaker.getInstance().getFontManager().playRegular14.drawCenteredString(str, f, round - 91.0F, -1426063361);
      if (flag) {
         CheatBreaker.getInstance().getFontManager().playRegular14.drawCenteredString("None", f, round - 70.0F, 1728053247);
      }
   }

   public boolean hasItems() {
      return this.activeTab == 2 ? !this.visibleEmotes.isEmpty() : !this.getCurrentCosmetics().isEmpty();
   }

   public List<Integer> getEmoteWheelKeybinds() {
      ArrayList listValue = CheatBreaker.getInstance().getKeybindSettings().emoteWheelQuickActionKeybinds.getListValue();

      while (listValue.size() < 8) {
         listValue.add(-1);
      }

      return listValue;
   }

   public String getKeyLabel(int n) {
      if (n < 0) {
         return "-";
      }

      String keyName = Keyboard.getKeyName(n);
      return keyName != null ? keyName : "?";
   }

   public void layoutCosmetics(List<Cosmetic> list) {
      Set favorites = RecentCosmeticsManager.getFavorites();
      ArrayList list2 = new ArrayList(list);
      Comparator comparing = Comparator.comparing((Cosmetic cosmetic) -> !cosmetic.isEquipped());
      if (this.activeTab != 3) {
         comparing = comparing.thenComparing(obj -> !favorites.contains(RecentCosmeticsManager.getKey((Cosmetic)obj)));
      }

      list2.sort(comparing);
      this.cosmeticItems.clear();

      for (Cosmetic cosmetic : (Iterable<Cosmetic>)list2) {
         CosmeticItemComponent cosmeticItemComponent = new CosmeticItemComponent(cosmetic, 1.0F);
         cosmeticItemComponent.setFlag(favorites.contains(RecentCosmeticsManager.getKey(cosmetic)));
         this.cosmeticItems.add(cosmeticItemComponent);
      }
   }

   public List<Cosmetic> getSearchResults() {
      return this.filterCosmetics(this.searchCapes.getText());
   }

   @Override
   public void drawScreenScaled(float f, float f2) {
      if (Minecraft.getMinecraft().theWorld == null) {
         super.drawScreenScaled(f, f2);
      }

      if (!CheatBreaker.getInstance().isWebsocketConnected()) {
         ClientValidator.process(this.getScaledWidth() / 2.0F, this.getScaledHeight() / 2.0F - 10.0F);
         this.back.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() / 2.0F + 16.0F, 60.0F, 12.0F);
         this.back.renderAndTestHover(f, f2, true);
      } else {
         float round = Math.round(this.getScaledWidth() / 2.0F);
         float round2 = Math.round(this.getScaledHeight() / 2.0F);
         RenderUtils.drawRoundedRectFilledPrecise(round - 250.0F, round2 - 95.0F, round + 85.0F, round2 + 110.0F, 3.0, 1660944384);
         RenderUtils.drawRoundedRectFilledPrecise(round + 95.0F, round2 - 95.0F, round + 250.0F, round2 + 110.0F, 3.0, 1660944384);
         RenderUtils.drawRoundedRectFilledPrecise(round - 235.0F, round2 + 75.0F, round + 70.0F, round2 + 76.0F, 0.0, 1358296565);
         this.back.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() / 2.0F + 125.0F, 60.0F, 12.0F);
         this.back.renderAndTestHover(f, f2, true);
         this.capes.setFlag2(this.activeTab == 0);
         this.capes.setBounds(round - 250.0F, round2 - 109.0F, 50.0F, 12.0F);
         this.capes.renderAndTestHover(f, f2, true);
         this.wings.setFlag2(this.activeTab == 1);
         this.wings.setBounds(round - 195.0F, round2 - 109.0F, 50.0F, 12.0F);
         this.wings.renderAndTestHover(f, f2, true);
         this.hats.setFlag2(this.activeTab == 4);
         this.hats.setBounds(round - 140.0F, round2 - 109.0F, 50.0F, 12.0F);
         this.hats.renderAndTestHover(f, f2, true);
         this.recent.setFlag2(this.activeTab == 3);
         this.recent.setBounds(round - 30.0F, round2 - 109.0F, 50.0F, 12.0F);
         this.recent.renderAndTestHover(f, f2, true);
         this.emotes.setFlag2(this.activeTab == 2);
         this.emotes.setBounds(round - 85.0F, round2 - 109.0F, 50.0F, 12.0F);
         this.emotes.renderAndTestHover(f, f2, true);
         if (this.activeTab == 2) {
            this.drawEmotesTab(f, f2, round, round2);
            return;
         }

         float f3 = round + this.width - 50.0F - f;
         float f4 = round2 + 30.0F - f2;
         PlayerViewer.render(round + 170.0F, round2 + 10.0F, f3, f4, f, f2 + 50.0F);
         CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredString("Hint: Click to drag.", round + 170.0F, round2 + 72.0F, -419430401);
         if (this.cosmeticItems.isEmpty()) {
            String str;
            if (this.hasItems()) {
               str = "There are no cosmetics matching your query.";
            } else if (this.activeTab == 3) {
               str = "Capes, wings and hats you equip will show up here.";
            } else {
               str = "Join our Discord for giveaways on emotes and other cosmetics!";
            }

            CheatBreaker.getInstance()
               .getFontManager()
               .playRegular18
               .drawCenteredString(str, this.getScaledWidth() / 2.0F - 75.0F, this.getScaledHeight() / 2.0F + 4.0F, -1);
         }

         int n = 0;
         float[] floats = new float[3];

         for (CosmeticItemComponent cosmeticItemComponent : this.cosmeticItems) {
            if (this.isOnCurrentPage(n++)) {
               int column = this.getColumn(cosmeticItemComponent, n - 1 - this.pageOffset * 5);
               int n2 = (int)this.getScaledWidth() / 2 + this.getColumnX(column);
               cosmeticItemComponent.setBounds(n2, (int)(this.getScaledHeight() / 2.0F - 80.0F + floats[column]), this.getItemWidth(), cosmeticItemComponent.getHeight());
               cosmeticItemComponent.render((int)f, (int)f2, 1.0F);
               floats[column] += cosmeticItemComponent.getHeight();
            }
         }

         if (this.activeTab == 3 && !this.cosmeticItems.isEmpty()) {
            this.drawSectionTitle("Recent Capes", 0, floats[0] == 0.0F);
            this.drawSectionTitle("Recent Wings", 1, floats[1] == 0.0F);
            this.drawSectionTitle("Recent Hats", 2, floats[2] == 0.0F);
         }

         if (this.cosmeticItems.size() > 5 && this.activeTab != 3) {
            this.previousPageButton.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() / 2.0F + 84.0F, 15.0F, 15.0F);
            this.nextPageButton.setBounds(this.getScaledWidth() / 2.0F + 25.0F, this.getScaledHeight() / 2.0F + 84.0F, 15.0F, 15.0F);
            this.drawPageIndicator(this.pageOffset / 2, (this.cosmeticItems.size() + 9) / 10, this.getScaledWidth() / 2.0F, this.getScaledHeight() / 2.0F);
            this.previousPageButton.renderAndTestHover(f, f2, true);
            this.nextPageButton.renderAndTestHover(f, f2, true);
            this.previousPageButton.setEnabled(this.pageOffset != 0);
            this.nextPageButton.setEnabled(this.pageOffset + 2 < this.cosmeticItems.size() / 5.0F);
         }

         this.searchCapes.setBounds(this.getScaledWidth() / 2.0F - 235.0F, this.getScaledHeight() / 2.0F + 84.0F, 140.0F, 15.0F);
         this.searchCapes.render(f, f2, true);
      }
   }

   public List<Cosmetic> filterCosmetics(String str) {
      String str2 = str == null ? "" : str.toLowerCase().replace(" ", "_");
      return this.getCurrentCosmetics().stream().filter(cosmetic -> str2.isEmpty() || cosmetic.getName().toLowerCase().contains(str2)).collect(Collectors.toList());
   }

   public int getItemWidth() {
      return this.activeTab == 3 ? 100 : 152;
   }

   public void drawPageIndicator(int n, int n2, float f, float f2) {
      CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredString(n + 1 + " / " + n2, f + 5.0F, f2 + 86.5F, -1);
   }

   public AbstractEmote getEmote(int n) {
      EmoteManager emoteManager = CheatBreaker.getInstance().getEmoteManager();
      return n >= 0 && emoteManager.getOwnedEmotes().contains(n) ? emoteManager.createEmote(n) : null;
   }

   @Override
   public void mouseClickedScaled(float f, float f2, int n) {
      if (Minecraft.getMinecraft().theWorld == null) {
         super.mouseClickedScaled(f, f2, n);
      }

      if (!this.searchCapes.containsPoint(f, f2) && this.searchCapes.isFocused()) {
         this.searchCapes.setFocused(false);
      } else if (this.searchCapes.containsPoint(f, f2) && !this.searchCapes.isFocused()) {
         this.searchCapes.setFocused(true);
      }

      if (this.wings.containsPoint(f, f2)) {
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.activeTab = 1;
         this.searchCapes.setPlaceholder("Search Wings...");
         this.searchCapes.setText("");
         this.pageOffset = 0;
         this.layoutCosmetics(this.getCosmeticsOfType("dragon_wings"));
      }

      if (this.hats.containsPoint(f, f2)) {
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.activeTab = 4;
         this.searchCapes.setPlaceholder("Search Hats...");
         this.searchCapes.setText("");
         this.pageOffset = 0;
         this.layoutCosmetics(this.getCosmeticsOfType("hat"));
      }

      if (this.capes.containsPoint(f, f2)) {
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.activeTab = 0;
         this.searchCapes.setPlaceholder("Search Capes...");
         this.searchCapes.setText("");
         this.pageOffset = 0;
         this.layoutCosmetics(this.getCosmeticsOfType("cape"));
      }

      if (this.recent.containsPoint(f, f2)) {
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.activeTab = 3;
         this.searchCapes.setPlaceholder("Search Recent...");
         this.searchCapes.setText("");
         this.pageOffset = 0;
         this.layoutCosmetics(this.getSearchResults());
      }

      if (this.emotes.containsPoint(f, f2)) {
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.activeTab = 2;
         this.searchCapes.setPlaceholder("Search Emotes...");
         this.searchCapes.setText("");
         this.emotePage = 0;
         this.filterEmotes("");
      }

      if (this.claim.containsPoint(f, f2)) {
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.mc2.displayGuiScreen(new MainMenuScreen());
      } else if (this.back.containsPoint(f, f2)) {
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
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
               Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            } else if (this.nextPageButton.containsPoint(f, f2) && this.pageOffset + 2 < this.cosmeticItems.size() / 5.0F) {
               this.pageOffset += 2;
               Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            }
         }

         int n2 = 0;
         CosmeticItemComponent cosmeticItemComponent = null;
         boolean flag = false;

         for (CosmeticItemComponent cosmeticItemComponent2 : this.cosmeticItems) {
            if (this.isOnCurrentPage(n2++)) {
               if (n == 1) {
                  if (this.isOver(cosmeticItemComponent2, f, f2)) {
                     cosmeticItemComponent = cosmeticItemComponent2;
                  }
               } else {
                  if (this.isOver(cosmeticItemComponent2, f, f2)) {
                     flag = true;
                  }

                  cosmeticItemComponent2.mouseClicked((int)f, (int)f2, n);
               }
            }
         }

         if (cosmeticItemComponent != null) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            this.toggleCosmetic(cosmeticItemComponent.getCosmetic());
         } else if (flag) {
            this.layoutCosmetics(this.getSearchResults());
         }
      }
   }

   public void filterEmotes(String str) {
      this.filteredEmotes.clear();
      if (str != null && !str.isEmpty()) {
         String toLowerCase = str.toLowerCase();

         for (EmoteEntry emoteEntry : this.visibleEmotes) {
            if (emoteEntry.text.toLowerCase().contains(toLowerCase)) {
               this.filteredEmotes.add(emoteEntry);
            }
         }
      } else {
         this.filteredEmotes.addAll(this.visibleEmotes);
      }
   }

   public void toggleCosmetic(Cosmetic cosmetic) {
      RecentCosmeticsManager.toggleFavorite(cosmetic);
      this.layoutCosmetics(this.getSearchResults());
   }

   public void drawCosmeticsGrid(float f, float f2, int n) {
      float round = Math.round(this.getScaledWidth() / 2.0F);
      float round2 = Math.round(this.getScaledHeight() / 2.0F);
      float f3 = round - 245.0F;
      float f4 = round - 80.0F;
      float f5 = round2 - 75.0F;
      float f6 = 16.0F;
      int max = Math.max(1, (this.filteredEmotes.size() + 18 - 1) / 18);
      if (this.emotePage > 0 && this.previousPageButton.containsPoint(f, f2)) {
         this.emotePage--;
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
      } else if (this.emotePage < max - 1 && this.nextPageButton.containsPoint(f, f2)) {
         this.emotePage++;
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
      } else {
         int n2 = this.emotePage * 18;

         for (int i = 0; i < 2; i++) {
            float f7 = i == 0 ? f3 : f4;

            for (int j = 0; j < 9; j++) {
               int n3 = n2 + i * 9 + j;
               if (n3 < this.filteredEmotes.size()) {
                  float f8 = f5 + j * f6;
                  if (f >= f7 && f <= f7 + 150.0F && f2 >= f8 && f2 <= f8 + f6) {
                     EmoteEntry emoteEntry = this.filteredEmotes.get(n3);
                     this.setWheelAction(this.selectedWheelSlot, emoteEntry.emoteId);
                     Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                     return;
                  }
               }
            }
         }

         float f7 = round + 172.0F;
         float f8 = round2 - 5.0F;

         for (int i = 0; i < 8; i++) {
            float f9 = 22.5F + i * 45.0F;
            double toRadians = Math.toRadians(f9 - 90.0 - 22.5);
            double d = f7 + 33.0 * Math.cos(toRadians);
            double d2 = f8 + 33.0 * Math.sin(toRadians);
            float f10 = (float)d - 10.0F;
            float f11 = (float)d2 + 6.0F;
            if (f >= f10 && f <= f10 + 20.0F && f2 >= f11 && f2 <= f11 + 9.0F) {
               Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
               if (n == 1) {
                  this.setWheelKeybind(i, -1);
                  this.hoveredWheelSlot = -1;
               } else {
                  this.hoveredWheelSlot = this.hoveredWheelSlot == i ? -1 : i;
               }

               return;
            }
         }

         double d = f - f7;
         double d2 = f2 - f8;
         double sqrt = Math.sqrt(d * d + d2 * d2);
         if (sqrt >= 9.0 && sqrt <= 52.0) {
            int n3 = -1;
            double d3 = Double.MAX_VALUE;

            for (int i = 0; i < 8; i++) {
               float f9 = 22.5F + i * 45.0F;
               double toRadians = Math.toRadians(f9 - 90.0 - 22.5);
               double d4 = f7 + 33.0 * Math.cos(toRadians);
               double d5 = f8 + 33.0 * Math.sin(toRadians);
               double d6 = (f - d4) * (f - d4) + (f2 - d5) * (f2 - d5);
               if (d6 < d3) {
                  d3 = d6;
                  n3 = i;
               }
            }

            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            if (n == 1) {
               this.setWheelAction(n3, -1);
            } else {
               this.selectedWheelSlot = n3;
            }
         }
      }
   }

   public void updateEmoteSelection() {
      Set set = this.visibleEmotes.stream().map(emoteEntry -> emoteEntry.emoteId).collect(Collectors.toSet());
      Set set2 = CheatBreaker.getInstance()
         .getEmoteManager()
         .getOwnedEmotes()
         .stream()
         .filter(integer -> EmoteManager.emoteClassIds.containsKey(integer) || EmoteManager.resourceEmoteIds.containsKey(integer))
         .collect(Collectors.toSet());
      if (!set.equals(set2)) {
         this.refreshEmotes();
      }

      if (this.activeTab != 2) {
         List searchResults = this.getSearchResults();
         boolean flag = searchResults.size() != this.cosmeticItems.size();

         for (int i = 0; !flag && i < searchResults.size(); i++) {
            flag = !searchResults.contains(this.cosmeticItems.get(i).getCosmetic());
         }

         if (flag) {
            this.layoutCosmetics(searchResults);
            int n = Math.max(0, (this.cosmeticItems.size() - 1) / 10) * 2;
            this.pageOffset = Math.min(this.pageOffset, n);
         }
      }
   }

   public List<Cosmetic> getAllCosmetics() {
      List cosmeticsOfType = this.getCosmeticsOfType("all");
      RecentCosmeticsManager.registerEquipped(cosmeticsOfType);
      ArrayList list = new ArrayList();
      int n = 0;
      int n2 = 0;
      int n3 = 0;

      for (String str : RecentCosmeticsManager.getRecent()) {
         for (Cosmetic cosmetic : (Iterable<Cosmetic>)cosmeticsOfType) {
            if (RecentCosmeticsManager.getKey(cosmetic).equals(str)) {
               int n4 = cosmetic.getCosmeticType() == CosmeticType.CAPE ? n++ : (cosmetic.getCosmeticType() == CosmeticType.WINGS ? n2++ : n3++);
               if (n4 < 5) {
                  list.add(cosmetic);
               }
               break;
            }
         }
      }

      return list;
   }

   public CosmeticsScreen() {
      this.searchCapes.setMaxStringLength(256);
      this.layoutCosmetics(this.getCosmeticsOfType("cape"));
      this.refreshEmotes();
   }

   public void setWheelAction(int n, int n2) {
      Setting emoteWheelQuickActions = CheatBreaker.getInstance().getKeybindSettings().emoteWheelQuickActions;
      List emoteWheelActions = this.getEmoteWheelActions();
      emoteWheelActions.set(n, n2);
      emoteWheelQuickActions.changeValue(emoteWheelActions);
      CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendEmoteWheel();
   }

   public List<Cosmetic> getCosmeticsOfType(String str) {
      return str != null && !str.equalsIgnoreCase("all")
         ? CheatBreaker.getInstance()
            .getCosmeticsManager()
            .getList()
            .stream()
            .filter(cosmetic -> cosmetic.getCosmeticType().getText().toLowerCase().contains(str.toLowerCase()))
            .collect(Collectors.toList())
         : new ArrayList<>(CheatBreaker.getInstance().getCosmeticsManager().getList());
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.searchCapes.tick();
      this.updateEmoteSelection();
   }

   public boolean isOnCurrentPage(int n) {
      return this.activeTab == 3 || n >= this.pageOffset * 5 && n < (this.pageOffset + 2) * 5;
   }

   public boolean isOver(CosmeticItemComponent cosmeticItemComponent, float f, float f2) {
      return f > cosmeticItemComponent.x && f < cosmeticItemComponent.x + cosmeticItemComponent.width && f2 > cosmeticItemComponent.y && f2 < cosmeticItemComponent.y + cosmeticItemComponent.height;
   }

   public void setWheelKeybind(int n, int n2) {
      Setting emoteWheelQuickActionKeybinds = CheatBreaker.getInstance().getKeybindSettings().emoteWheelQuickActionKeybinds;
      List emoteWheelKeybinds = this.getEmoteWheelKeybinds();
      emoteWheelKeybinds.set(n, n2);
      emoteWheelQuickActionKeybinds.changeValue(emoteWheelKeybinds);
   }

   public List<Cosmetic> getCurrentCosmetics() {
      if (this.activeTab == 3) {
         return this.getAllCosmetics();
      } else {
         return this.activeTab == 4 ? this.getCosmeticsOfType("hat") : this.getCosmeticsOfType(this.activeTab == 1 ? "dragon_wings" : "cape");
      }
   }

   public int getColumnX(int n) {
      if (this.activeTab == 3) {
         return -235 + n * 102;
      } else {
         return n == 0 ? -235 : -75;
      }
   }

   @Override
   public void keyTyped(char c, int n) throws java.io.IOException {
      if (this.hoveredWheelSlot >= 0) {
         if (n != 1) {
            this.setWheelKeybind(this.hoveredWheelSlot, n);
         }

         this.hoveredWheelSlot = -1;
      } else {
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
   }

   public void drawEmotesTab(float f, float f2, float f3, float f4) {
      CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredString("All Emotes", f3 - 82.0F, f4 - 88.0F, -1);
      float f5 = f3 - 245.0F;
      float f6 = f3 - 80.0F;
      float f7 = f4 - 75.0F;
      float f8 = 16.0F;
      int n = this.emotePage * 18;

      for (int i = 0; i < 2; i++) {
         float f9 = i == 0 ? f5 : f6;

         for (int j = 0; j < 9; j++) {
            int n2 = n + i * 9 + j;
            if (n2 < this.filteredEmotes.size()) {
               EmoteEntry emoteEntry = this.filteredEmotes.get(n2);
               float f10 = f7 + j * f8;
               boolean flag = f >= f9 && f <= f9 + 150.0F && f2 >= f10 && f2 <= f10 + f8;
               if (flag) {
                  Gui.drawRect(f9, f10, f9 + 150.0F, f10 + f8, 1090519039);
               }

               RenderUtils.applyColor(-1);
               RenderUtils.drawSquareTextureViaRenderEngine(emoteEntry.abstractEmote.getClientEmotes(), 7.0F, f9 + 2.0F, f10 + 1.0F);
               CheatBreaker.getInstance().getFontManager().playRegular16.drawString(emoteEntry.text, f9 + 22.0F, f10 + 3.0F, -1);
            }
         }
      }

      if (this.filteredEmotes.isEmpty()) {
         CheatBreaker.getInstance()
            .getFontManager()
            .playRegular16
            .drawCenteredString(
               this.visibleEmotes.isEmpty() ? "Join our Discord for giveaways on emotes and other cosmetics!" : "There are no emotes matching your query.",
               f3 - 82.0F,
               f4 - 20.0F,
               -1
            );
      }

      int max = Math.max(1, (this.filteredEmotes.size() + 18 - 1) / 18);
      if (max > 1) {
         this.previousPageButton.setBounds(f3 - 30.0F, f4 + 84.0F, 15.0F, 15.0F);
         this.nextPageButton.setBounds(f3 + 25.0F, f4 + 84.0F, 15.0F, 15.0F);
         this.drawPageIndicator(this.emotePage, max, f3, f4);
         this.previousPageButton.renderAndTestHover(f, f2, true);
         this.nextPageButton.renderAndTestHover(f, f2, true);
         this.previousPageButton.setEnabled(this.emotePage > 0);
         this.nextPageButton.setEnabled(this.emotePage < max - 1);
      }

      this.searchCapes.setBounds(f3 - 235.0F, f4 + 84.0F, 140.0F, 15.0F);
      this.searchCapes.render(f, f2, true);
      float f9 = f3 + 172.0F;
      float f10 = f4 - 5.0F;
      CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredString("Quick Actions Wheel", f9, f4 - 88.0F, -1);
      List emoteWheelActions = this.getEmoteWheelActions();
      List emoteWheelKeybinds = this.getEmoteWheelKeybinds();
      RenderUtils.applyColor(Integer.MIN_VALUE);
      RenderUtils.drawProgressRing(f9, f10, 50.0, 48.0, 100.0, 100, 100.0);
      RenderUtils.drawProgressRing(f9, f10, 11.0, 9.0, 100.0, 100, 100.0);

      for (int i = 0; i < 8; i++) {
         float f11 = 22.5F + i * 45.0F;
         RenderUtils.applyColor(
            i == this.selectedWheelSlot ? CheatBreaker.getInstance().getKeybindSettings().emoteRadialColor.getColorValue() | 0xFF000000 : 1296911693
         );
         RenderUtils.drawArc(f9, f10, 48.0, 11.0, 360.0 - f11 + 90.0, 360.0 - f11 + 90.0 + 45.0);
         double toRadians = Math.toRadians(f11 - 90.0 - 22.5);
         double d = f9 + 33.0 * Math.cos(toRadians);
         double d2 = f10 + 33.0 * Math.sin(toRadians);
         int n2 = (Integer)emoteWheelActions.get(i);
         AbstractEmote emote = this.getEmote(n2);
         if (emote != null) {
            RenderUtils.applyColor(-1);
            RenderUtils.drawSquareTextureViaRenderEngine(emote.getClientEmotes(), 7.0F, (float)d - 7.0F, (float)d2 - 7.0F);
         }

         boolean flag = this.hoveredWheelSlot == i;
         String str = flag ? "..." : this.getKeyLabel((Integer)emoteWheelKeybinds.get(i));
         int n3 = flag ? -171 : ((Integer)emoteWheelKeybinds.get(i) >= 0 ? -5592406 : -11184811);
         CheatBreaker.getInstance().getFontManager().playRegular12.drawCenteredString(str, (float)d, (float)d2 + 9.0F, n3);
      }

      int n2 = (Integer)emoteWheelActions.get(this.selectedWheelSlot);
      AbstractEmote emote = this.getEmote(n2);
      String str = "Slot " + (this.selectedWheelSlot + 1) + ": " + (emote != null ? emote.getText() : "Empty");
      CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredString(str, f9, f10 + 48.0F + 20.0F, -1);
      CheatBreaker.getInstance()
         .getFontManager()
         .playRegular14
         .drawCenteredString("Click a slot, then click an emote to assign it.", f9, f10 + 48.0F + 34.0F, -1426063361);
      CheatBreaker.getInstance()
         .getFontManager()
         .playRegular14
         .drawCenteredString("Right click a slot to clear it.", f9, f10 + 48.0F + 46.0F, -1426063361);
   }

   public int getColumn(CosmeticItemComponent cosmeticItemComponent, int n) {
      if (this.activeTab == 3) {
         CosmeticType cosmeticType = cosmeticItemComponent.getCosmetic().getCosmeticType();
         return cosmeticType == CosmeticType.CAPE ? 0 : (cosmeticType == CosmeticType.WINGS ? 1 : 2);
      } else {
         return n < 5 ? 0 : 1;
      }
   }
}
