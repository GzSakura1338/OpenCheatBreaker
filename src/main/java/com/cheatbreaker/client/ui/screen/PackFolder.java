package com.cheatbreaker.client.ui.screen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.resources.ResourcePackRepository.Entry;

public class PackFolder {
   public String text;
   public List<Entry> entryList;
   public List<Entry> entryList2;
   public List<PackFolder> packFolderList;
   public PackFolder packFolder;

   public PackFolder(File file, List<Entry> list, PackFolder packFolder) {
      this.text = file.getName();
      this.entryList = new ArrayList<>();
      this.entryList2 = list;
      this.packFolderList = new ArrayList<>();
      this.packFolder = packFolder;
      this.packFolderList.add(new PackFolder(packFolder));

      for (File file2 : Objects.requireNonNull(file.listFiles())) {
         if (!ResourcePackScanner.checkFile2(file2)) {
            if (ResourcePackScanner.checkFile(file2)) {
               this.packFolderList.add(new PackFolder(file2, list, this));
            }
         } else {
            for (Entry entry : this.entryList2) {
               if (entry.getResourcePackName().equals(file2.getName())) {
                  Optional findOptional = ResourcePackScanner.findOptional(file2);
                  if (findOptional.isPresent() && ((Entry)findOptional.get()).equals(entry)) {
                     break;
                  }
               }
            }

            ResourcePackScanner.findOptional(file2).ifPresent(this.entryList::add);
         }
      }
   }

   public List<PackFolder> getPackFolderList() {
      return this.packFolderList;
   }

   public List<Entry> getEntryList2() {
      return this.entryList2;
   }

   public PackFolder(PackFolder packFolder) {
      this.text = "Back to " + (packFolder == null ? "Main Folder" : packFolder.text);
      this.entryList = new ArrayList<>();
      this.entryList2 = new ArrayList<>();
      this.packFolderList = new ArrayList<>();
      this.packFolder = packFolder;
   }

   public PackFolder getPackFolder() {
      return this.packFolder;
   }

   public String getText() {
      return this.text;
   }

   public List<Entry> getEntryList() {
      return this.entryList;
   }

   public PackFolder(File file, List<Entry> list) {
      this(file, list, null);
   }
}
