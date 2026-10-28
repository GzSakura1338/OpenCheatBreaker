package com.cheatbreaker.client.ui.screen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.resources.ResourcePackRepository.Entry;

public class PackFolder {
    public String name;
    public List<Entry> entryList;
    public List<Entry> entries;
    public List<PackFolder> packFolderList;
    public PackFolder packFolder;

    public PackFolder getPackFolder() {
        return this.packFolder;
    }

    public PackFolder(File file, List<Entry> list, PackFolder packFolder) {
        this.name = file.getName();
        this.entryList = new ArrayList<>();
        this.entries = list;
        this.packFolderList = new ArrayList<>();
        this.packFolder = packFolder;
        this.packFolderList.add(new PackFolder(packFolder));

        for (File file2 : ResourcePackScanner.listFiles(file, null)) {
            if (!ResourcePackScanner.isValidZipPack(file2)) {
                if (ResourcePackScanner.checkFile(file2)) {
                    this.packFolderList.add(new PackFolder(file2, list, this));
                }
            } else {
                for (Entry entry : this.entries) {
                    if (entry.getResourcePackName().equals(file2.getName())) {
                        Optional createEntry = ResourcePackScanner.createEntry(file2);
                        if (createEntry.isPresent() && ((Entry)createEntry.get()).equals(entry)) {
                            break;
                        }
                    }
                }

                ResourcePackScanner.createEntry(file2).ifPresent(this.entryList::add);
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public PackFolder(PackFolder packFolder) {
        this.name = "Back to " + (packFolder == null ? "Main Folder" : packFolder.name);
        this.entryList = new ArrayList<>();
        this.entries = new ArrayList<>();
        this.packFolderList = new ArrayList<>();
        this.packFolder = packFolder;
    }

    public List<Entry> getEntryList() {
        return this.entryList;
    }

    public List<PackFolder> getPackFolderList() {
        return this.packFolderList;
    }

    public PackFolder(File file, List<Entry> list) {
        this(file, list, null);
    }
}
