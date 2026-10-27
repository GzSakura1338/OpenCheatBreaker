package net.minecraft.client.resources;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public abstract class ResourcePackListEntry implements IGuiListEntry {
   public static ResourceLocation RESOURCE_PACKS_TEXTURE = new ResourceLocation("textures/gui/resource_packs.png");
   public static IChatComponent INCOMPATIBLE_TEXT = new ChatComponentTranslation("resourcePack.incompatible");
   public static IChatComponent INCOMPATIBLE_OLD_TEXT = new ChatComponentTranslation("resourcePack.incompatible.old");
   public static IChatComponent INCOMPATIBLE_NEW_TEXT = new ChatComponentTranslation("resourcePack.incompatible.new");
   public GuiScreenResourcePacks resourcePacksGUI;
   public Minecraft mc;

   public ResourcePackListEntry(GuiScreenResourcePacks resourcePacksGUIIn) {
      this.resourcePacksGUI = resourcePacksGUIIn;
      this.mc = Minecraft.getMinecraft();
   }

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      int format = this.getFormat();
      if (format != 1) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         Gui.drawRect(x - 1, y - 1, x + listWidth - 9, y + slotHeight + 1, -8978432);
      }

      this.run();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      String resourcePackName = this.getResourcePackName();
      String createStringBuilder = this.createStringBuilder();
      if ((this.mc.gameSettings.touchscreen || isSelected) && this.alwaysTrue()) {
         this.mc.getTextureManager().bindTexture(RESOURCE_PACKS_TEXTURE);
         Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int n = mouseX - x;
         int n2 = mouseY - y;
         if (format < 1) {
            resourcePackName = INCOMPATIBLE_TEXT.getFormattedText();
            createStringBuilder = INCOMPATIBLE_OLD_TEXT.getFormattedText();
         } else if (format > 1) {
            resourcePackName = INCOMPATIBLE_TEXT.getFormattedText();
            createStringBuilder = INCOMPATIBLE_NEW_TEXT.getFormattedText();
         }

         if (this.hasResourcePackEntry()) {
            if (n < 32) {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         } else {
            if (this.hasResourcePackEntry2()) {
               if (n < 16) {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.canSortUp()) {
               if (n < 32 && n > 16 && n2 < 16) {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.check()) {
               if (n < 32 && n > 16 && n2 > 16) {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }
         }
      }

      int stringWidth = this.mc.fontRendererObj.getStringWidth(resourcePackName);
      if (stringWidth > 157) {
         resourcePackName = this.mc.fontRendererObj.trimStringToWidth(resourcePackName, 157 - this.mc.fontRendererObj.getStringWidth("...")) + "...";
      }

      this.mc.fontRendererObj.drawStringWithShadow(resourcePackName, x + 32 + 2, y + 1, 16777215);
      List listFormattedStringToWidth = this.mc.fontRendererObj.listFormattedStringToWidth(createStringBuilder, 157);

      for (int i = 0; i < 2 && i < listFormattedStringToWidth.size(); i++) {
         this.mc.fontRendererObj.drawStringWithShadow((String)listFormattedStringToWidth.get(i), x + 32 + 2, y + 12 + 10 * i, 8421504);
      }
   }

   public abstract int getFormat();

   public abstract String createStringBuilder();

   public abstract String getResourcePackName();

   public abstract void run();

   public boolean alwaysTrue() {
      return true;
   }

   public boolean hasResourcePackEntry() {
      return !this.resourcePacksGUI.hasResourcePackEntry(this);
   }

   public boolean hasResourcePackEntry2() {
      return this.resourcePacksGUI.hasResourcePackEntry(this);
   }

   public boolean canSortUp() {
      List listContaining = this.resourcePacksGUI.getListContaining(this);
      int indexOf = listContaining.indexOf(this);
      return indexOf > 0 && ((ResourcePackListEntry)listContaining.get(indexOf - 1)).alwaysTrue();
   }

   public boolean check() {
      List listContaining = this.resourcePacksGUI.getListContaining(this);
      int indexOf = listContaining.indexOf(this);
      return indexOf >= 0 && indexOf < listContaining.size() - 1 && ((ResourcePackListEntry)listContaining.get(indexOf + 1)).alwaysTrue();
   }

   @Override
   public boolean mousePressed(int slotIndex, int n, int n2, int n3, int n4, int n5) {
      if (this.alwaysTrue() && n4 <= 32) {
         if (this.hasResourcePackEntry()) {
            this.resourcePacksGUI.markChanged();
            int format = this.getFormat();
            if (format != 1) {
               String format2 = I18n.format("resourcePack.incompatible.confirm.title");
               String format3 = I18n.format("resourcePack.incompatible.confirm." + (format > 1 ? "new" : "old"));
               this.mc.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
                  @Override
                  public void confirmClicked(boolean result, int id) {
                     List listContaining = ResourcePackListEntry.this.resourcePacksGUI.getListContaining(ResourcePackListEntry.this);
                     ResourcePackListEntry.this.mc.displayGuiScreen(ResourcePackListEntry.this.resourcePacksGUI);
                     if (result) {
                        listContaining.remove(ResourcePackListEntry.this);
                        ResourcePackListEntry.this.resourcePacksGUI.getSelectedResourcePacks().add(0, ResourcePackListEntry.this);
                     }
                  }
               }, format2, format3, 0));
            } else {
               this.resourcePacksGUI.getListContaining(this).remove(this);
               this.resourcePacksGUI.getSelectedResourcePacks().add(0, this);
            }

            return true;
         }

         if (n4 < 16 && this.hasResourcePackEntry2()) {
            this.resourcePacksGUI.getListContaining(this).remove(this);
            this.resourcePacksGUI.getAvailableResourcePacks().add(0, this);
            this.resourcePacksGUI.markChanged();
            return true;
         }

         if (n4 > 16 && n5 < 16 && this.canSortUp()) {
            List listContaining = this.resourcePacksGUI.getListContaining(this);
            int indexOf = listContaining.indexOf(this);
            listContaining.remove(this);
            listContaining.add(indexOf - 1, this);
            this.resourcePacksGUI.markChanged();
            return true;
         }

         if (n4 > 16 && n5 > 16 && this.check()) {
            List listContaining = this.resourcePacksGUI.getListContaining(this);
            int indexOf = listContaining.indexOf(this);
            listContaining.remove(this);
            listContaining.add(indexOf + 1, this);
            this.resourcePacksGUI.markChanged();
            return true;
         }
      }

      return false;
   }

   @Override
   public void setSelected(int n, int n2, int n3) {
   }

   @Override
   public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
   }
}
