package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.IntEvent5;
import com.cheatbreaker.client.event.SimpleEvent32;
import com.cheatbreaker.client.event.SimpleEvent36;
import com.cheatbreaker.client.util.TooltipCopyHelper;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiChat extends GuiScreen {
   public static Logger logger = LogManager.getLogger();
   public String historyBuffer = "";
   public int sentHistoryCursor = -1;
   public boolean playerNamesFound;
   public boolean waitingOnAutocomplete;
   public int autocompleteIndex;
   public List<String> foundPlayerNames = Lists.newArrayList();
   public GuiTextField inputField;
   public String defaultInputFieldText = "";

   public GuiChat() {
   }

   public GuiChat(String defaultText) {
      this.defaultInputFieldText = defaultText;
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
      this.inputField = new GuiTextField(0, this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
      this.inputField.setMaxStringLength(100);
      this.inputField.setEnableBackgroundDrawing(false);
      this.inputField.setFocused(true);
      this.inputField.setText(this.defaultInputFieldText);
      this.inputField.setCanLoseFocus(false);
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      this.mc.ingameGUI.getChatGUI().resetScroll();
   }

   @Override
   public void updateScreen() {
      this.inputField.updateCursorCounter();
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (!TooltipCopyHelper.isCopyKeyPressed(keyCode)
         || !this.inputField.getSelectedText().isEmpty()
         || !TooltipCopyHelper.copyToClipboard(this.mc.ingameGUI.getChatGUI().getHoveredText(Mouse.getX(), Mouse.getY()))) {
         this.waitingOnAutocomplete = false;
         if (keyCode == 15) {
            this.autocompletePlayerNames();
         } else {
            this.playerNamesFound = false;
         }

         if (keyCode == 1) {
            this.mc.displayGuiScreen((GuiScreen)null);
         } else if (keyCode == 28 || keyCode == 156) {
            String trim = this.inputField.getText().trim();
            if (trim.length() > 0) {
               this.sendChatMessage(trim);
            }

            this.mc.displayGuiScreen((GuiScreen)null);
         } else if (keyCode == 200) {
            this.getSentHistory(-1);
         } else if (keyCode == 208) {
            this.getSentHistory(1);
         } else if (keyCode == 201) {
            this.mc.ingameGUI.getChatGUI().scroll(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
         } else if (keyCode == 209) {
            this.mc.ingameGUI.getChatGUI().scroll(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
         } else {
            this.inputField.textboxKeyTyped(typedChar, keyCode);
         }
      }
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int eventDWheel = Mouse.getEventDWheel();
      if (eventDWheel != 0) {
         if (eventDWheel > 1) {
            eventDWheel = 1;
         }

         if (eventDWheel < -1) {
            eventDWheel = -1;
         }

         if (!isShiftKeyDown()) {
            eventDWheel *= 7;
         }

         this.mc.ingameGUI.getChatGUI().scroll(eventDWheel);
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (!TooltipCopyHelper.isCopyMouseButtonPressed(mouseButton)
         || !TooltipCopyHelper.copyToClipboard(this.mc.ingameGUI.getChatGUI().getHoveredText(Mouse.getX(), Mouse.getY()))) {
         if (mouseButton == 0) {
            IChatComponent chatComponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
            if (this.handleComponentClick(chatComponent)) {
               return;
            }
         }

         this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
         super.mouseClicked(mouseX, mouseY, mouseButton);
      }
   }

   @Override
   public void setText(String newChatText, boolean shouldOverwrite) {
      if (shouldOverwrite) {
         this.inputField.setText(newChatText);
      } else {
         this.inputField.writeText(newChatText);
      }
   }

   public void autocompletePlayerNames() {
      if (this.playerNamesFound) {
         this.inputField
            .deleteFromCursor(this.inputField.getWordSkipPosition(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
         if (this.autocompleteIndex >= this.foundPlayerNames.size()) {
            this.autocompleteIndex = 0;
         }
      } else {
         int wordSkipPosition = this.inputField.getWordSkipPosition(-1, this.inputField.getCursorPosition(), false);
         this.foundPlayerNames.clear();
         this.autocompleteIndex = 0;
         String toLowerCase = this.inputField.getText().substring(wordSkipPosition).toLowerCase();
         String substring = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
         this.sendAutocompleteRequest(substring, toLowerCase);
         if (this.foundPlayerNames.isEmpty()) {
            return;
         }

         this.playerNamesFound = true;
         this.inputField.deleteFromCursor(wordSkipPosition - this.inputField.getCursorPosition());
      }

      if (this.foundPlayerNames.size() > 1) {
         StringBuilder sb = new StringBuilder();

         for (String str : this.foundPlayerNames) {
            if (sb.length() > 0) {
               sb.append(", ");
            }

            sb.append(str);
         }

         this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(sb.toString()), 1);
      }

      this.inputField.writeText(this.foundPlayerNames.get(this.autocompleteIndex++));
   }

   public void sendAutocompleteRequest(String str, String str2) {
      if (str.length() >= 1) {
         BlockPos blockPos = null;
         if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
            blockPos = this.mc.objectMouseOver.getBlockPos();
         }

         this.mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(str, blockPos));
         this.waitingOnAutocomplete = true;
      }
   }

   public void getSentHistory(int msgPos) {
      int n = this.sentHistoryCursor + msgPos;
      int size = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
      n = MathHelper.clamp_int(n, 0, size);
      if (n != this.sentHistoryCursor) {
         if (n == size) {
            this.sentHistoryCursor = size;
            this.inputField.setText(this.historyBuffer);
         } else {
            if (this.sentHistoryCursor == size) {
               this.historyBuffer = this.inputField.getText();
            }

            this.inputField.setText(this.mc.ingameGUI.getChatGUI().getSentMessages().get(n));
            this.sentHistoryCursor = n;
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      SimpleEvent32 simpleEvent32 = new SimpleEvent32();
      CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent32);
      IntEvent5 intEvent5 = new IntEvent5(Integer.MIN_VALUE);
      CheatBreaker.getInstance().getEventBus().handleEvent(intEvent5);
      if (!intEvent5.isFlag()) {
         drawRect(2, this.height - 14, this.width - 2, this.height - 2, intEvent5.getMIN_VALUE());
      }

      this.inputField.drawTextBox();
      IChatComponent chatComponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
      if (chatComponent != null && chatComponent.getChatStyle().getChatHoverEvent() != null) {
         this.handleComponentHover(chatComponent, mouseX, mouseY);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
      SimpleEvent36 simpleEvent36 = new SimpleEvent36();
      CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent36);
   }

   public void onAutocompleteResponse(String[] strs) {
      if (this.waitingOnAutocomplete) {
         this.playerNamesFound = false;
         this.foundPlayerNames.clear();

         for (String str : strs) {
            if (str.length() > 0) {
               this.foundPlayerNames.add(str);
            }
         }

         String substring = this.inputField.getText().substring(this.inputField.getWordSkipPosition(-1, this.inputField.getCursorPosition(), false));
         String commonPrefix = StringUtils.getCommonPrefix(strs);
         if (commonPrefix.length() > 0 && !substring.equalsIgnoreCase(commonPrefix)) {
            this.inputField
               .deleteFromCursor(this.inputField.getWordSkipPosition(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
            this.inputField.writeText(commonPrefix);
         } else if (this.foundPlayerNames.size() > 0) {
            this.playerNamesFound = true;
            this.autocompletePlayerNames();
         }
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }
}
