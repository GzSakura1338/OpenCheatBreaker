package net.minecraft.client.gui.inventory;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.IntEvent1;
import com.cheatbreaker.client.util.TooltipCopyHelper;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public abstract class GuiContainer extends GuiScreen {
   public static ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");
   public int xSize = 176;
   public int ySize = 166;
   public Set<Slot> dragSplittingSlots = Sets.newHashSet();
   public int guiLeft;
   public int guiTop;
   public Slot theSlot;
   public Slot clickedSlot;
   public boolean isRightMouseClick;
   public ItemStack draggedStack;
   public int touchUpX;
   public int touchUpY;
   public Slot returningStackDestSlot;
   public long returningStackTime;
   public ItemStack returningStack;
   public Slot currentDragTargetSlot;
   public long dragItemDropDelay;
   public Container inventorySlots;
   public boolean dragSplitting;
   public int dragSplittingLimit;
   public int dragSplittingButton;
   public boolean ignoreMouseUp;
   public int dragSplittingRemnant;
   public long lastClickTime;
   public Slot lastClickSlot;
   public int lastClickButton;
   public boolean doubleClick;
   public ItemStack shiftClickedSlot;

   public GuiContainer(Container inventorySlotsIn) {
      this.inventorySlots = inventorySlotsIn;
      this.ignoreMouseUp = true;
   }

   @Override
   public void initGui() {
      super.initGui();
      this.mc.thePlayer.openContainer = this.inventorySlots;
      this.guiLeft = (this.width - this.xSize) / 2;
      this.guiTop = (this.height - this.ySize) / 2;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      int guiLeft = this.guiLeft;
      int guiTop = this.guiTop;
      this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      super.drawScreen(mouseX, mouseY, partialTicks);
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.pushMatrix();
      GlStateManager.translate(guiLeft, guiTop, 0.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableRescaleNormal();
      this.theSlot = null;
      short s = 240;
      short s2 = 240;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, s / 1.0F, s2 / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

      for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
         Slot slot = this.inventorySlots.inventorySlots.get(i);
         this.drawSlot(slot);
         if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.canBeHovered()) {
            this.theSlot = slot;
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int xDisplayPosition = slot.xDisplayPosition;
            int yDisplayPosition = slot.yDisplayPosition;
            GlStateManager.colorMask(true, true, true, false);
            this.drawGradientRect(xDisplayPosition, yDisplayPosition, xDisplayPosition + 16, yDisplayPosition + 16, -2130706433, -2130706433);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
         }
      }

      RenderHelper.disableStandardItemLighting();
      this.drawGuiContainerForegroundLayer(mouseX, mouseY);
      RenderHelper.enableGUIStandardItemLighting();
      InventoryPlayer inventory = this.mc.thePlayer.inventory;
      ItemStack itemStack = this.draggedStack == null ? inventory.getItemStack() : this.draggedStack;
      if (itemStack != null) {
         byte b = 8;
         int n = this.draggedStack == null ? 8 : 16;
         String str = null;
         if (this.draggedStack != null && this.isRightMouseClick) {
            itemStack = itemStack.copy();
            itemStack.stackSize = MathHelper.ceiling_float_int(itemStack.stackSize / 2.0F);
         } else if (this.dragSplitting && this.dragSplittingSlots.size() > 1) {
            itemStack = itemStack.copy();
            itemStack.stackSize = this.dragSplittingRemnant;
            if (itemStack.stackSize == 0) {
               str = "" + EnumChatFormatting.YELLOW + "0";
            }
         }

         this.drawItemStack(itemStack, mouseX - guiLeft - b, mouseY - guiTop - n, str);
      }

      if (this.returningStack != null) {
         float f = (float)(Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;
         if (f >= 1.0F) {
            f = 1.0F;
            this.returningStack = null;
         }

         int n = this.returningStackDestSlot.xDisplayPosition - this.touchUpX;
         int n2 = this.returningStackDestSlot.yDisplayPosition - this.touchUpY;
         int n3 = this.touchUpX + (int)(n * f);
         int n4 = this.touchUpY + (int)(n2 * f);
         this.drawItemStack(this.returningStack, n3, n4, (String)null);
      }

      GlStateManager.popMatrix();
      if ((
            !CheatBreaker.getInstance().getModManager().packTweaksMod.isEnabled()
               || (Boolean)CheatBreaker.getInstance().getModManager().packTweaksMod.showInventoryTooltips.getValue()
         )
         && inventory.getItemStack() == null
         && this.theSlot != null
         && this.theSlot.getHasStack()) {
         ItemStack stack = this.theSlot.getStack();
         this.renderToolTip(stack, mouseX, mouseY);
      }

      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
      RenderHelper.enableStandardItemLighting();
   }

   public void drawItemStack(ItemStack stack, int x, int y, String altText) {
      GlStateManager.translate(0.0F, 0.0F, 32.0F);
      zLevel = 200.0F;
      this.itemRender.zLevel = 200.0F;
      this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
      this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, stack, x, y - (this.draggedStack == null ? 0 : 8), altText);
      zLevel = 0.0F;
      this.itemRender.zLevel = 0.0F;
   }

   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
   }

   public abstract void drawGuiContainerBackgroundLayer(float f, int n, int n2);

   public void drawSlot(Slot slotIn) {
      int xDisplayPosition = slotIn.xDisplayPosition;
      int yDisplayPosition = slotIn.yDisplayPosition;
      ItemStack stack = slotIn.getStack();
      boolean flag = false;
      boolean flag2 = slotIn == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
      ItemStack itemStack = this.mc.thePlayer.inventory.getItemStack();
      String str = null;
      if (slotIn == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && stack != null) {
         stack = stack.copy();
         stack.stackSize /= 2;
      } else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && itemStack != null) {
         if (this.dragSplittingSlots.size() == 1) {
            return;
         }

         if (Container.canAddItemToSlot(slotIn, itemStack, true) && this.inventorySlots.canDragIntoSlot(slotIn)) {
            stack = itemStack.copy();
            flag = true;
            Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, stack, slotIn.getStack() == null ? 0 : slotIn.getStack().stackSize);
            if (stack.stackSize > stack.getMaxStackSize()) {
               str = EnumChatFormatting.YELLOW + "" + stack.getMaxStackSize();
               stack.stackSize = stack.getMaxStackSize();
            }

            if (stack.stackSize > slotIn.getItemStackLimit(stack)) {
               str = EnumChatFormatting.YELLOW + "" + slotIn.getItemStackLimit(stack);
               stack.stackSize = slotIn.getItemStackLimit(stack);
            }
         } else {
            this.dragSplittingSlots.remove(slotIn);
            this.updateDragSplitting();
         }
      }

      zLevel = 100.0F;
      this.itemRender.zLevel = 100.0F;
      if (stack == null) {
         String slotTexture = slotIn.getSlotTexture();
         if (slotTexture != null) {
            TextureAtlasSprite atlasSprite = this.mc.getTextureMapBlocks().getAtlasSprite(slotTexture);
            GlStateManager.disableLighting();
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.drawTexturedModalRect(xDisplayPosition, yDisplayPosition, atlasSprite, 16, 16);
            GlStateManager.enableLighting();
            flag2 = true;
         }
      }

      if (!flag2) {
         if (flag) {
            drawRect(xDisplayPosition, yDisplayPosition, xDisplayPosition + 16, yDisplayPosition + 16, -2130706433);
         }

         GlStateManager.enableDepth();
         this.itemRender.renderItemAndEffectIntoGUI(stack, xDisplayPosition, yDisplayPosition);
         this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, stack, xDisplayPosition, yDisplayPosition, str);
      }

      this.itemRender.zLevel = 0.0F;
      zLevel = 0.0F;
   }

   public void updateDragSplitting() {
      ItemStack itemStack = this.mc.thePlayer.inventory.getItemStack();
      if (itemStack != null && this.dragSplitting) {
         this.dragSplittingRemnant = itemStack.stackSize;

         for (Slot slot : this.dragSplittingSlots) {
            ItemStack itemStack2 = itemStack.copy();
            int n = slot.getStack() == null ? 0 : slot.getStack().stackSize;
            Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemStack2, n);
            if (itemStack2.stackSize > itemStack2.getMaxStackSize()) {
               itemStack2.stackSize = itemStack2.getMaxStackSize();
            }

            if (itemStack2.stackSize > slot.getItemStackLimit(itemStack2)) {
               itemStack2.stackSize = slot.getItemStackLimit(itemStack2);
            }

            this.dragSplittingRemnant = this.dragSplittingRemnant - (itemStack2.stackSize - n);
         }
      }
   }

   public Slot getSlotAtPosition(int x, int y) {
      for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
         Slot slot = this.inventorySlots.inventorySlots.get(i);
         if (this.isMouseOverSlot(slot, x, y)) {
            return slot;
         }
      }

      return null;
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (!TooltipCopyHelper.isCopyMouseButtonPressed(mouseButton)
         || this.theSlot == null
         || !this.theSlot.getHasStack()
         || !TooltipCopyHelper.copyToClipboard(TooltipCopyHelper.getTooltipText(this.theSlot.getStack()))) {
         CheatBreaker.getInstance().getEventBus().handleEvent(new IntEvent1(mouseButton));
         this.checkHotbarKeys(mouseButton - 100);
         super.mouseClicked(mouseX, mouseY, mouseButton);
         boolean flag = mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;
         Slot slotAtPosition = this.getSlotAtPosition(mouseX, mouseY);
         long systemTime = Minecraft.getSystemTime();
         this.doubleClick = this.lastClickSlot == slotAtPosition && systemTime - this.lastClickTime < 250L && this.lastClickButton == mouseButton;
         this.ignoreMouseUp = false;
         if (mouseButton == 0 || mouseButton == 1 || flag) {
            int guiLeft = this.guiLeft;
            int guiTop = this.guiTop;
            boolean flag2 = mouseX < guiLeft || mouseY < guiTop || mouseX >= guiLeft + this.xSize || mouseY >= guiTop + this.ySize;
            int n = -1;
            if (slotAtPosition != null) {
               n = slotAtPosition.slotNumber;
            }

            if (flag2) {
               n = -999;
            }

            if (this.mc.gameSettings.touchscreen && flag2 && this.mc.thePlayer.inventory.getItemStack() == null) {
               this.mc.displayGuiScreen((GuiScreen)null);
               return;
            }

            if (n != -1) {
               if (this.mc.gameSettings.touchscreen) {
                  if (slotAtPosition != null && slotAtPosition.getHasStack()) {
                     this.clickedSlot = slotAtPosition;
                     this.draggedStack = null;
                     this.isRightMouseClick = mouseButton == 1;
                  } else {
                     this.clickedSlot = null;
                  }
               } else if (!this.dragSplitting) {
                  if (this.mc.thePlayer.inventory.getItemStack() == null) {
                     if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                        this.handleMouseClick(slotAtPosition, n, mouseButton, 3);
                     } else {
                        boolean flag3 = n != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                        byte b = 0;
                        if (flag3) {
                           this.shiftClickedSlot = slotAtPosition != null && slotAtPosition.getHasStack() ? slotAtPosition.getStack() : null;
                           b = 1;
                        } else if (n == -999) {
                           b = 4;
                        }

                        this.handleMouseClick(slotAtPosition, n, mouseButton, b);
                     }

                     this.ignoreMouseUp = true;
                  } else {
                     this.dragSplitting = true;
                     this.dragSplittingButton = mouseButton;
                     this.dragSplittingSlots.clear();
                     if (mouseButton == 0) {
                        this.dragSplittingLimit = 0;
                     } else if (mouseButton == 1) {
                        this.dragSplittingLimit = 1;
                     } else if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                        this.dragSplittingLimit = 2;
                     }
                  }
               }
            }
         }

         this.lastClickSlot = slotAtPosition;
         this.lastClickTime = systemTime;
         this.lastClickButton = mouseButton;
      }
   }

   @Override
   public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      Slot slotAtPosition = this.getSlotAtPosition(mouseX, mouseY);
      ItemStack itemStack = this.mc.thePlayer.inventory.getItemStack();
      if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
         if (clickedMouseButton == 0 || clickedMouseButton == 1) {
            if (this.draggedStack == null) {
               if (slotAtPosition != this.clickedSlot && this.clickedSlot.getStack() != null) {
                  this.draggedStack = this.clickedSlot.getStack().copy();
               }
            } else if (this.draggedStack.stackSize > 1 && slotAtPosition != null && Container.canAddItemToSlot(slotAtPosition, this.draggedStack, false)) {
               long systemTime = Minecraft.getSystemTime();
               if (this.currentDragTargetSlot == slotAtPosition) {
                  if (systemTime - this.dragItemDropDelay > 500L) {
                     this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                     this.handleMouseClick(slotAtPosition, slotAtPosition.slotNumber, 1, 0);
                     this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                     this.dragItemDropDelay = systemTime + 750L;
                     this.draggedStack.stackSize--;
                  }
               } else {
                  this.currentDragTargetSlot = slotAtPosition;
                  this.dragItemDropDelay = systemTime;
               }
            }
         }
      } else if (this.dragSplitting
         && slotAtPosition != null
         && itemStack != null
         && itemStack.stackSize > this.dragSplittingSlots.size()
         && Container.canAddItemToSlot(slotAtPosition, itemStack, true)
         && slotAtPosition.isItemValid(itemStack)
         && this.inventorySlots.canDragIntoSlot(slotAtPosition)) {
         this.dragSplittingSlots.add(slotAtPosition);
         this.updateDragSplitting();
      }
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      Slot slotAtPosition = this.getSlotAtPosition(mouseX, mouseY);
      int guiLeft = this.guiLeft;
      int guiTop = this.guiTop;
      boolean flag = mouseX < guiLeft || mouseY < guiTop || mouseX >= guiLeft + this.xSize || mouseY >= guiTop + this.ySize;
      int n = -1;
      if (slotAtPosition != null) {
         n = slotAtPosition.slotNumber;
      }

      if (flag) {
         n = -999;
      }

      if (this.doubleClick && slotAtPosition != null && state == 0 && this.inventorySlots.canMergeSlot((ItemStack)null, slotAtPosition)) {
         if (isShiftKeyDown()) {
            if (slotAtPosition != null && slotAtPosition.inventory != null && this.shiftClickedSlot != null) {
               for (Slot slot : this.inventorySlots.inventorySlots) {
                  if (slot != null
                     && slot.canTakeStack(this.mc.thePlayer)
                     && slot.getHasStack()
                     && slot.inventory == slotAtPosition.inventory
                     && Container.canAddItemToSlot(slot, this.shiftClickedSlot, true)) {
                     this.handleMouseClick(slot, slot.slotNumber, state, 1);
                  }
               }
            }
         } else {
            this.handleMouseClick(slotAtPosition, n, state, 6);
         }

         this.doubleClick = false;
         this.lastClickTime = 0L;
      } else {
         if (this.dragSplitting && this.dragSplittingButton != state) {
            this.dragSplitting = false;
            this.dragSplittingSlots.clear();
            this.ignoreMouseUp = true;
            return;
         }

         if (this.ignoreMouseUp) {
            this.ignoreMouseUp = false;
            return;
         }

         if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
            if (state == 0 || state == 1) {
               if (this.draggedStack == null && slotAtPosition != this.clickedSlot) {
                  this.draggedStack = this.clickedSlot.getStack();
               }

               boolean canAddItemToSlot = Container.canAddItemToSlot(slotAtPosition, this.draggedStack, false);
               if (n != -1 && this.draggedStack != null && canAddItemToSlot) {
                  this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
                  this.handleMouseClick(slotAtPosition, n, 0, 0);
                  if (this.mc.thePlayer.inventory.getItemStack() != null) {
                     this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
                     this.touchUpX = mouseX - guiLeft;
                     this.touchUpY = mouseY - guiTop;
                     this.returningStackDestSlot = this.clickedSlot;
                     this.returningStack = this.draggedStack;
                     this.returningStackTime = Minecraft.getSystemTime();
                  } else {
                     this.returningStack = null;
                  }
               } else if (this.draggedStack != null) {
                  this.touchUpX = mouseX - guiLeft;
                  this.touchUpY = mouseY - guiTop;
                  this.returningStackDestSlot = this.clickedSlot;
                  this.returningStack = this.draggedStack;
                  this.returningStackTime = Minecraft.getSystemTime();
               }

               this.draggedStack = null;
               this.clickedSlot = null;
            }
         } else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
            this.handleMouseClick((Slot)null, -999, Container.packClickData(0, this.dragSplittingLimit), 5);

            for (Slot slot : this.dragSplittingSlots) {
               this.handleMouseClick(slot, slot.slotNumber, Container.packClickData(1, this.dragSplittingLimit), 5);
            }

            this.handleMouseClick((Slot)null, -999, Container.packClickData(2, this.dragSplittingLimit), 5);
         } else if (this.mc.thePlayer.inventory.getItemStack() != null) {
            if (state == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
               this.handleMouseClick(slotAtPosition, n, state, 3);
            } else {
               boolean flag2 = n != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
               if (flag2) {
                  this.shiftClickedSlot = slotAtPosition != null && slotAtPosition.getHasStack() ? slotAtPosition.getStack() : null;
               }

               this.handleMouseClick(slotAtPosition, n, state, flag2 ? 1 : 0);
            }
         }
      }

      if (this.mc.thePlayer.inventory.getItemStack() == null) {
         this.lastClickTime = 0L;
      }

      this.dragSplitting = false;
   }

   public boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
      return this.isPointInRegion(slotIn.xDisplayPosition, slotIn.yDisplayPosition, 16, 16, mouseX, mouseY);
   }

   public boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY) {
      int guiLeft = this.guiLeft;
      int guiTop = this.guiTop;
      pointX -= guiLeft;
      pointY -= guiTop;
      return pointX >= left - 1 && pointX < left + right + 1 && pointY >= top - 1 && pointY < top + bottom + 1;
   }

   public void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
      if (slotIn != null) {
         slotId = slotIn.slotNumber;
      }

      this.mc.playerController.windowClick(this.inventorySlots.windowId, slotId, clickedButton, clickType, this.mc.thePlayer);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (!TooltipCopyHelper.isCopyKeyPressed(keyCode)
         || this.theSlot == null
         || !this.theSlot.getHasStack()
         || !TooltipCopyHelper.copyToClipboard(TooltipCopyHelper.getTooltipText(this.theSlot.getStack()))) {
         if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
         }

         this.checkHotbarKeys(keyCode);
         if (this.theSlot != null && this.theSlot.getHasStack()) {
            if (keyCode == this.mc.gameSettings.keyBindPickBlock.getKeyCode()) {
               this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, 0, 3);
            } else if (keyCode == this.mc.gameSettings.keyBindDrop.getKeyCode()) {
               this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, 4);
            }
         }
      }
   }

   public boolean checkHotbarKeys(int keyCode) {
      if (this.mc.thePlayer.inventory.getItemStack() == null && this.theSlot != null) {
         for (int i = 0; i < 9; i++) {
            if (keyCode == this.mc.gameSettings.keyBindsHotbar[i].getKeyCode()) {
               this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, i, 2);
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public void onGuiClosed() {
      if (this.mc.thePlayer != null) {
         this.inventorySlots.onContainerClosed(this.mc.thePlayer);
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead) {
         this.mc.thePlayer.closeScreen();
      }
   }
}
