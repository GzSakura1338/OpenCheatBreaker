package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.render.GlStateCache;
import com.cheatbreaker.client.ui.MoveDirection;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.util.ResourceLocation;

public class SelectedPacksList extends PackList {
   public static ResourceLocation resourcePacksPng = new ResourceLocation("textures/gui/resource_packs.png");
   public ResourcePacksScreen resourcePacksScreen2;
   public MoveDirection moveDirection;

   public void handlePackFolder(PackFolder packFolder, File file, Entry entry) {
      if (packFolder.getText().equals(file.getName())) {
         if (this.resourcePacksScreen2.isSearching()) {
            if (packFolder.getEntryList().contains(entry)) {
               super.resourcePacksScreen.addSearchResult(entry);
            }
         } else {
            if (!packFolder.getEntryList().contains(entry)) {
               packFolder.getEntryList().add(entry);
            }

            this.resourcePacksScreen2.sortEntries(packFolder.getEntryList());
         }
      } else {
         for (PackFolder packFolder2 : packFolder.getPackFolderList()) {
            this.handlePackFolder(packFolder2, file, entry);
         }
      }
   }

   public SelectedPacksList(ResourcePacksScreen resourcePacksScreen, int n, int n2, int n3, int n4, int n5, List<Entry> list) {
      super(resourcePacksScreen, n, n2, n3, n4, n5, I18n.format("resourcePack.selected.title"), list);
      this.resourcePacksScreen2 = resourcePacksScreen;
      this.moveDirection = MoveDirection.MOVE_BACK;
   }

   @Override
   public void drawSlot(int n, int n2, int n3, int n4, int n5, int n6, boolean flag) {
      if (flag) {
         Gui.drawRect(this.left, n3 - 1, n2 + 1, n3 + n4 + 3, -2134851392);
         GlStateCache.process(1.0F, 1.0F, 1.0F, 1.0F);
      }

      super.drawSlot(n, n2, n3, n4, n5, n6, flag);
      if (flag) {
         GlStateCache.process(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc2.getTextureManager().bindTexture(resourcePacksPng);
         if (n2 - 40 <= n5 && n5 < n2) {
            if (n3 <= n6 && n6 < n3 + n4 / 2 - 2) {
               if (n == 0) {
                  if (this.moveDirection == MoveDirection.MOVE_DOWN) {
                     this.moveDirection = MoveDirection.MOVE_BACK;
                  }
               } else {
                  this.moveDirection = MoveDirection.MOVE_UP;
                  Gui.drawModalRectWithCustomSizedTexture(n2 - 40, n3, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               }

               if (n != super.getSize() - 1) {
                  Gui.drawModalRectWithCustomSizedTexture(n2 - 40, n3, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            } else {
               if (n != 0) {
                  Gui.drawModalRectWithCustomSizedTexture(n2 - 40, n3, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }

               if (n == super.getSize() - 1) {
                  if (this.moveDirection == MoveDirection.MOVE_UP) {
                     this.moveDirection = MoveDirection.MOVE_BACK;
                  }
               } else {
                  this.moveDirection = MoveDirection.MOVE_DOWN;
                  Gui.drawModalRectWithCustomSizedTexture(n2 - 40, n3, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               }
            }
         } else {
            this.moveDirection = MoveDirection.MOVE_BACK;
            if (n != 0) {
               Gui.drawModalRectWithCustomSizedTexture(n2 - 40, n3, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }

            if (n != super.getSize() - 1) {
               Gui.drawModalRectWithCustomSizedTexture(n2 - 40, n3, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }
      }
   }

   public void handleEntry(Entry entry) {
      for (File file : Objects.requireNonNull(this.mc2.getResourcePackRepository().getDirResourcepacks().listFiles())) {
         if (ResourcePackScanner.checkFile2(file)) {
            Optional optional;
            if (file.getName().equals(entry.getResourcePackName())
               && (optional = ResourcePackScanner.findOptional(file)).isPresent()
               && ((Entry)optional.get()).equals(entry)) {
               if (this.resourcePacksScreen2.isSearching()) {
                  super.resourcePacksScreen.addSearchResult(entry);
               } else {
                  super.resourcePacksScreen.addAvailable(entry);
               }
            }
         } else if (ResourcePackScanner.checkFile(file)) {
            this.handleFile(file, entry);
         }
      }
   }

   public void handleFile(File file, Entry entry) {
      for (File file2 : Objects.requireNonNull(file.listFiles())) {
         if (ResourcePackScanner.checkFile2(file2)) {
            Optional optional;
            if (file2.getName().equals(entry.getResourcePackName())
               && (optional = ResourcePackScanner.findOptional(file2)).isPresent()
               && ((Entry)optional.get()).equals(entry)) {
               for (PackFolder packFolder : this.resourcePacksScreen2.getFolders()) {
                  this.handlePackFolder(packFolder, file, entry);
               }
            }
         } else if (ResourcePackScanner.checkFile(file2)) {
            this.handleFile(file2, entry);
         }
      }
   }

   @Override
   public void elementClicked(int n, boolean flag) {
      if (0 <= n && n < super.getSize()) {
         switch (this.moveDirection) {
            case MOVE_UP:
               if (n - 1 >= 0) {
                  Collections.swap(this.entryList, n, n - 1);
               }
               break;
            case MOVE_DOWN:
               if (n + 1 < super.getSize()) {
                  Collections.swap(this.entryList, n, n + 1);
               }
               break;
            default:
               Entry entry = this.entryList.get(n);
               this.handleEntry(entry);
               this.entryList.remove(entry);
         }
      }
   }
}
