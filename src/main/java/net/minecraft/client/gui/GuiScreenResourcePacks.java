package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;

public class GuiScreenResourcePacks extends GuiScreen {
   public static Logger logger = LogManager.getLogger();
   public boolean changed = false;
   public List<ResourcePackListEntry> availableResourcePacks;
   public List<ResourcePackListEntry> selectedResourcePacks;
   public GuiResourcePackAvailable availableResourcePacksList;
   public GuiResourcePackSelected selectedResourcePacksList;
   public GuiScreen parentScreen;

   public GuiScreenResourcePacks(GuiScreen parentScreenIn) {
      this.parentScreen = parentScreenIn;
   }

   @Override
   public void initGui() {
      this.buttonList.add(new GuiOptionButton(2, this.width / 2 - 154, this.height - 48, I18n.format("resourcePack.openFolder")));
      this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 4, this.height - 48, I18n.format("gui.done")));
      if (!this.changed) {
         this.availableResourcePacks = Lists.newArrayList();
         this.selectedResourcePacks = Lists.newArrayList();
         ResourcePackRepository resourcePackRepository = this.mc.getResourcePackRepository();
         resourcePackRepository.updateRepositoryEntriesAll();
         ArrayList newArrayList = Lists.newArrayList(resourcePackRepository.getRepositoryEntriesAll());
         newArrayList.removeAll(resourcePackRepository.getRepositoryEntries());

         for (Entry entry : (Iterable<Entry>)newArrayList) {
            this.availableResourcePacks.add(new ResourcePackListEntryFound(this, entry));
         }

         for (Entry entry : Lists.reverse(resourcePackRepository.getRepositoryEntries())) {
            this.selectedResourcePacks.add(new ResourcePackListEntryFound(this, entry));
         }

         this.selectedResourcePacks.add(new ResourcePackListEntryDefault(this));
      }

      this.availableResourcePacksList = new GuiResourcePackAvailable(this.mc, 200, this.height, this.availableResourcePacks);
      this.availableResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
      this.availableResourcePacksList.registerScrollButtons(7, 8);
      this.selectedResourcePacksList = new GuiResourcePackSelected(this.mc, 200, this.height, this.selectedResourcePacks);
      this.selectedResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 + 4);
      this.selectedResourcePacksList.registerScrollButtons(7, 8);
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.selectedResourcePacksList.handleMouseInput();
      this.availableResourcePacksList.handleMouseInput();
   }

   public boolean hasResourcePackEntry(ResourcePackListEntry resourcePackListEntry) {
      return this.selectedResourcePacks.contains(resourcePackListEntry);
   }

   public List<ResourcePackListEntry> getListContaining(ResourcePackListEntry resourcePackListEntry) {
      return this.hasResourcePackEntry(resourcePackListEntry) ? this.selectedResourcePacks : this.availableResourcePacks;
   }

   public List<ResourcePackListEntry> getAvailableResourcePacks() {
      return this.availableResourcePacks;
   }

   public List<ResourcePackListEntry> getSelectedResourcePacks() {
      return this.selectedResourcePacks;
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 2) {
            File dirResourcepacks = this.mc.getResourcePackRepository().getDirResourcepacks();
            String absolutePath = dirResourcepacks.getAbsolutePath();
            if (Util.getOSType() == EnumOS.OSX) {
               try {
                  logger.info(absolutePath);
                  Runtime.getRuntime().exec(new String[]{"/usr/bin/open", absolutePath});
                  return;
               } catch (IOException e) {
                  logger.error("Couldn't open file", e);
               }
            } else if (Util.getOSType() == EnumOS.WINDOWS) {
               String format = String.format("cmd.exe /C start \"Open file\" \"%s\"", absolutePath);

               try {
                  Runtime.getRuntime().exec(format);
                  return;
               } catch (IOException e) {
                  logger.error("Couldn't open file", e);
               }
            }

            boolean flag = false;

            try {
               Class forName = Class.forName("java.awt.Desktop");
               Object invoke = forName.getMethod("getDesktop").invoke(null);
               forName.getMethod("browse", URI.class).invoke(invoke, dirResourcepacks.toURI());
            } catch (Throwable e) {
               logger.error("Couldn't open link", e);
               flag = true;
            }

            if (flag) {
               logger.info("Opening via system class!");
               Sys.openURL("file://" + absolutePath);
            }
         } else if (button.id == 1) {
            if (this.changed) {
               ArrayList newArrayList = Lists.newArrayList();

               for (ResourcePackListEntry resourcePackListEntry : this.selectedResourcePacks) {
                  if (resourcePackListEntry instanceof ResourcePackListEntryFound) {
                     newArrayList.add(((ResourcePackListEntryFound)resourcePackListEntry).getEntry());
                  }
               }

               Collections.reverse(newArrayList);
               this.mc.getResourcePackRepository().setRepositories(newArrayList);
               this.mc.gameSettings.resourcePacks.clear();
               this.mc.gameSettings.incompatibleResourcePacks.clear();

               for (Entry entry : (Iterable<Entry>)newArrayList) {
                  this.mc.gameSettings.resourcePacks.add(entry.getResourcePackName());
                  if (entry.getFormat() != 1) {
                     this.mc.gameSettings.incompatibleResourcePacks.add(entry.getResourcePackName());
                  }
               }

               this.mc.gameSettings.saveOptions();
               this.mc.refreshResources();
            }

            this.mc.displayGuiScreen(this.parentScreen);
         }
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.availableResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
      this.selectedResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawBackground(0);
      this.availableResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
      this.selectedResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.title"), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public void markChanged() {
      this.changed = true;
   }
}
