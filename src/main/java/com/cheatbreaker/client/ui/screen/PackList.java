package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.util.PackTextUtils;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.ResourcePackRepository.Entry;

public abstract class PackList extends SlotList {
   public ResourcePacksScreen resourcePacksScreen;
   public Minecraft mc2;
   public List<Entry> entryList;

   public List<Entry> unmodifiableList() {
      return Collections.unmodifiableList(this.entryList);
   }

   @Override
   public int getSize() {
      return this.entryList.size();
   }

   @Override
   public void drawSlot(int n, int n2, int n3, int n4, int n5, int n6, boolean flag) {
      if (0 <= n && n < this.getSize()) {
         Entry entry = this.entryList.get(n);
         boolean value = (Boolean)CheatBreaker.getInstance().getKeybindSettings().showPackIcons.getValue();
         if (value) {
            entry.bindTexturePackIcon(this.mc2.getTextureManager());
            Gui.drawModalRectWithCustomSizedTexture(this.left + 2, n3 + 1, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
         }

         this.mc2
            .fontRendererObj
            .drawString(
               PackTextUtils.createStringBuilder(entry.getResourcePackName(), this.width - 46), this.left + (value ? 36.0F : 2.0F), n3 + 2.0F, -1, true
            );
         if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().showPackDescriptions.getValue()) {
            List listFormattedStringToWidth = this.mc2.fontRendererObj.listFormattedStringToWidth(entry.getTexturePackDescription(), this.width - 46);

            for (int i = 0; i < listFormattedStringToWidth.size(); i++) {
               String str = (String)listFormattedStringToWidth.get(i);
               if (i == 1 && listFormattedStringToWidth.size() > 2) {
                  str = PackTextUtils.createStringBuilder(str, this.width - 46);
               }

               this.mc2.fontRendererObj.drawString(str, this.left + (value ? 36.0F : 2.0F), n3 + 13.0F + 10.0F * i, -5592406, true);
               if (i == 1) {
                  break;
               }
            }
         }
      }
   }

   @Override
   public void drawBackground() {
      Gui.drawRect(this.left, this.top, this.intValue3, this.bottom, CheatBreaker.getInstance().getKeybindSettings().listBackgroundColor.getColorValue());
   }

   @Override
   public abstract void elementClicked(int n, boolean flag);

   public PackList(ResourcePacksScreen resourcePacksScreen, int n, int n2, int n3, int n4, int n5, String str, List<Entry> list) {
      super(resourcePacksScreen.mc, n, n2, n3, n4, n5, str);
      this.resourcePacksScreen = resourcePacksScreen;
      this.mc2 = resourcePacksScreen.mc;
      this.entryList = list;
   }
}
