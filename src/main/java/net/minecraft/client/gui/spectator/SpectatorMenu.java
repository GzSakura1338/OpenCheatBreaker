package net.minecraft.client.gui.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class SpectatorMenu {
   public static ISpectatorMenuObject CLOSE_COMMAND = new SpectatorMenu.EndSpectatorObject();
   public static ISpectatorMenuObject PREVIOUS_PAGE_COMMAND = new SpectatorMenu.MoveMenuObject(-1, true);
   public static ISpectatorMenuObject NEXT_PAGE_COMMAND = new SpectatorMenu.MoveMenuObject(1, true);
   public static ISpectatorMenuObject DISABLED_NEXT_PAGE_COMMAND = new SpectatorMenu.MoveMenuObject(1, false);
   public static ISpectatorMenuObject BLANK_COMMAND = new ISpectatorMenuObject() {
      @Override
      public void handleSpectatorMenu(SpectatorMenu menu) {
      }

      @Override
      public IChatComponent getSpectatorName() {
         return new ChatComponentText("");
      }

      @Override
      public void render(float f, int alpha) {
      }

      @Override
      public boolean isEnabled() {
         return false;
      }
   };
   public List<SpectatorDetails> stateStack = Lists.newArrayList();
   public ISpectatorMenuView currentGroup = new BaseSpectatorGroup();
   public int selectedSlot = -1;
   public ISpectatorMenuRecipient closeCallback;
   public int page;

   public SpectatorMenu(ISpectatorMenuRecipient spectatorMenuRecipient) {
      this.closeCallback = spectatorMenuRecipient;
   }

   public ISpectatorMenuObject getCommand(int n) {
      int n2 = n + this.page * 6;
      return this.page > 0 && n == 0
         ? PREVIOUS_PAGE_COMMAND
         : (
            n == 7
               ? (n2 < this.currentGroup.getCommands().size() ? NEXT_PAGE_COMMAND : DISABLED_NEXT_PAGE_COMMAND)
               : (
                  n == 8
                     ? CLOSE_COMMAND
                     : (
                        n2 >= 0 && n2 < this.currentGroup.getCommands().size()
                           ? (ISpectatorMenuObject)MoreObjects.firstNonNull(this.currentGroup.getCommands().get(n2), BLANK_COMMAND)
                           : BLANK_COMMAND
                     )
               )
         );
   }

   public List<ISpectatorMenuObject> getCommands() {
      ArrayList newArrayList = Lists.newArrayList();

      for (int i = 0; i <= 8; i++) {
         newArrayList.add(this.getCommand(i));
      }

      return newArrayList;
   }

   public ISpectatorMenuObject getSelectedCommand() {
      return this.getCommand(this.selectedSlot);
   }

   public ISpectatorMenuView getCurrentGroup() {
      return this.currentGroup;
   }

   public void useCommand(int n) {
      ISpectatorMenuObject command = this.getCommand(n);
      if (command != BLANK_COMMAND) {
         if (this.selectedSlot == n && command.isEnabled()) {
            command.handleSpectatorMenu(this);
         } else {
            this.selectedSlot = n;
         }
      }
   }

   public void close() {
      this.closeCallback.handleSpectatorMenu(this);
   }

   public int getSelectedSlot() {
      return this.selectedSlot;
   }

   public void selectElement(ISpectatorMenuView spectatorMenuView) {
      this.stateStack.add(this.getCurrentState());
      this.currentGroup = spectatorMenuView;
      this.selectedSlot = -1;
      this.page = 0;
   }

   public SpectatorDetails getCurrentState() {
      return new SpectatorDetails(this.currentGroup, this.getCommands(), this.selectedSlot);
   }

   static class EndSpectatorObject implements ISpectatorMenuObject {
      public EndSpectatorObject() {
      }

      @Override
      public void handleSpectatorMenu(SpectatorMenu menu) {
         menu.close();
      }

      @Override
      public IChatComponent getSpectatorName() {
         return new ChatComponentText("Close menu");
      }

      @Override
      public void render(float f, int alpha) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_TEXTURE);
         Gui.drawModalRectWithCustomSizedTexture(0, 0, 128.0F, 0.0F, 16, 16, 256.0F, 256.0F);
      }

      @Override
      public boolean isEnabled() {
         return true;
      }
   }

   static class MoveMenuObject implements ISpectatorMenuObject {
      public int direction;
      public boolean enabled;

      public MoveMenuObject(int n, boolean flag) {
         this.direction = n;
         this.enabled = flag;
      }

      @Override
      public void handleSpectatorMenu(SpectatorMenu menu) {
         menu.page = this.direction;
      }

      @Override
      public IChatComponent getSpectatorName() {
         return this.direction < 0 ? new ChatComponentText("Previous Page") : new ChatComponentText("Next Page");
      }

      @Override
      public void render(float f, int alpha) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_TEXTURE);
         if (this.direction < 0) {
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 144.0F, 0.0F, 16, 16, 256.0F, 256.0F);
         } else {
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 160.0F, 0.0F, 16, 16, 256.0F, 256.0F);
         }
      }

      @Override
      public boolean isEnabled() {
         return this.enabled;
      }
   }
}
