package net.minecraft.client.gui.inventory;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiContainerCreative extends InventoryEffectRenderer {
   public static ResourceLocation creativeInventoryTabs = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   public static InventoryBasic inventory = new InventoryBasic("tmp", true, 45);
   public static int selectedTabIndex = CreativeTabs.tabBlock.getTabIndex();
   public float currentScroll;
   public boolean isScrolling;
   public boolean wasClicking;
   public GuiTextField searchField;
   public List<Slot> slots;
   public Slot deleteItemSlot;
   public boolean scrolling;
   public CreativeCrafting listener;

   public GuiContainerCreative(EntityPlayer entityPlayer) {
      super(new GuiContainerCreative.ContainerCreative(entityPlayer));
      entityPlayer.openContainer = this.inventorySlots;
      this.allowUserInput = true;
      this.ySize = 136;
      this.xSize = 195;
   }

   @Override
   public void updateScreen() {
      if (!this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
      }

      this.updateActivePotionEffects();
   }

   @Override
   public void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
      this.scrolling = true;
      boolean flag = clickType == 1;
      clickType = slotId == -999 && clickType == 0 ? 4 : clickType;
      if (slotIn == null && selectedTabIndex != CreativeTabs.tabInventory.getTabIndex() && clickType != 5) {
         InventoryPlayer inventory = this.mc.thePlayer.inventory;
         if (inventory.getItemStack() != null) {
            if (clickedButton == 0) {
               this.mc.thePlayer.dropPlayerItemWithRandomChoice(inventory.getItemStack(), true);
               this.mc.playerController.sendPacketDropItem(inventory.getItemStack());
               inventory.setItemStack((ItemStack)null);
            }

            if (clickedButton == 1) {
               ItemStack splitStack = inventory.getItemStack().splitStack(1);
               this.mc.thePlayer.dropPlayerItemWithRandomChoice(splitStack, true);
               this.mc.playerController.sendPacketDropItem(splitStack);
               if (inventory.getItemStack().stackSize == 0) {
                  inventory.setItemStack((ItemStack)null);
               }
            }
         }
      } else if (slotIn == this.deleteItemSlot && flag) {
         for (int i = 0; i < this.mc.thePlayer.inventoryContainer.getInventory().size(); i++) {
            this.mc.playerController.sendSlotPacket((ItemStack)null, i);
         }
      } else if (selectedTabIndex == CreativeTabs.tabInventory.getTabIndex()) {
         if (slotIn == this.deleteItemSlot) {
            this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
         } else if (clickType == 4 && slotIn != null && slotIn.getHasStack()) {
            ItemStack decrStackSize = slotIn.decrStackSize(clickedButton == 0 ? 1 : slotIn.getStack().getMaxStackSize());
            this.mc.thePlayer.dropPlayerItemWithRandomChoice(decrStackSize, true);
            this.mc.playerController.sendPacketDropItem(decrStackSize);
         } else if (clickType == 4 && this.mc.thePlayer.inventory.getItemStack() != null) {
            this.mc.thePlayer.dropPlayerItemWithRandomChoice(this.mc.thePlayer.inventory.getItemStack(), true);
            this.mc.playerController.sendPacketDropItem(this.mc.thePlayer.inventory.getItemStack());
            this.mc.thePlayer.inventory.setItemStack((ItemStack)null);
         } else {
            this.mc
               .thePlayer
               .inventoryContainer
               .slotClick(slotIn == null ? slotId : ((GuiContainerCreative.CreativeSlot)slotIn).slot.slotNumber, clickedButton, clickType, this.mc.thePlayer);
            this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
         }
      } else if (clickType != 5 && slotIn.inventory == inventory) {
         InventoryPlayer inventory = this.mc.thePlayer.inventory;
         ItemStack itemStack = inventory.getItemStack();
         ItemStack stack = slotIn.getStack();
         if (clickType == 2) {
            if (stack != null && clickedButton >= 0 && clickedButton < 9) {
               ItemStack itemStack2 = stack.copy();
               itemStack2.stackSize = itemStack2.getMaxStackSize();
               this.mc.thePlayer.inventory.setInventorySlotContents(clickedButton, itemStack2);
               this.mc.thePlayer.inventoryContainer.detectAndSendChanges();
            }

            return;
         }

         if (clickType == 3) {
            if (inventory.getItemStack() == null && slotIn.getHasStack()) {
               ItemStack itemStack2 = slotIn.getStack().copy();
               itemStack2.stackSize = itemStack2.getMaxStackSize();
               inventory.setItemStack(itemStack2);
            }

            return;
         }

         if (clickType == 4) {
            if (stack != null) {
               ItemStack itemStack2 = stack.copy();
               itemStack2.stackSize = clickedButton == 0 ? 1 : itemStack2.getMaxStackSize();
               this.mc.thePlayer.dropPlayerItemWithRandomChoice(itemStack2, true);
               this.mc.playerController.sendPacketDropItem(itemStack2);
            }

            return;
         }

         if (itemStack != null && stack != null && itemStack.isItemEqual(stack)) {
            if (clickedButton == 0) {
               if (flag) {
                  itemStack.stackSize = itemStack.getMaxStackSize();
               } else if (itemStack.stackSize < itemStack.getMaxStackSize()) {
                  itemStack.stackSize++;
               }
            } else if (itemStack.stackSize <= 1) {
               inventory.setItemStack((ItemStack)null);
            } else {
               itemStack.stackSize--;
            }
         } else if (stack != null && itemStack == null) {
            inventory.setItemStack(ItemStack.copyItemStack(stack));
            itemStack = inventory.getItemStack();
            if (flag) {
               itemStack.stackSize = itemStack.getMaxStackSize();
            }
         } else {
            inventory.setItemStack((ItemStack)null);
         }
      } else {
         this.inventorySlots.slotClick(slotIn == null ? slotId : slotIn.slotNumber, clickedButton, clickType, this.mc.thePlayer);
         if (Container.getDragEvent(clickedButton) == 2) {
            for (int i = 0; i < 9; i++) {
               this.mc.playerController.sendSlotPacket(this.inventorySlots.getSlot(45 + i).getStack(), 36 + i);
            }
         } else if (slotIn != null) {
            ItemStack stack = this.inventorySlots.getSlot(slotIn.slotNumber).getStack();
            this.mc.playerController.sendSlotPacket(stack, slotIn.slotNumber - this.inventorySlots.inventorySlots.size() + 9 + 36);
         }
      }
   }

   @Override
   public void updateActivePotionEffects() {
      int guiLeft = this.guiLeft;
      super.updateActivePotionEffects();
      if (this.searchField != null && this.guiLeft != guiLeft) {
         this.searchField.xPosition = this.guiLeft + 82;
      }
   }

   @Override
   public void initGui() {
      if (this.mc.playerController.isInCreativeMode()) {
         super.initGui();
         this.buttonList.clear();
         Keyboard.enableRepeatEvents(true);
         this.searchField = new GuiTextField(0, this.fontRendererObj, this.guiLeft + 82, this.guiTop + 6, 89, this.fontRendererObj.FONT_HEIGHT);
         this.searchField.setMaxStringLength(15);
         this.searchField.setEnableBackgroundDrawing(false);
         this.searchField.setVisible(false);
         this.searchField.setTextColor(16777215);
         int selectedTabIndex2 = selectedTabIndex;
         selectedTabIndex = -1;
         this.setCurrentCreativeTab(CreativeTabs.creativeTabArray[selectedTabIndex2]);
         this.listener = new CreativeCrafting(this.mc);
         this.mc.thePlayer.inventoryContainer.onCraftGuiOpened(this.listener);
      } else {
         this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
      }
   }

   @Override
   public void onGuiClosed() {
      super.onGuiClosed();
      if (this.mc.thePlayer != null && this.mc.thePlayer.inventory != null) {
         this.mc.thePlayer.inventoryContainer.removeCraftingFromCrafters(this.listener);
      }

      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (selectedTabIndex != CreativeTabs.tabAllSearch.getTabIndex()) {
         if (GameSettings.isKeyDown(this.mc.gameSettings.keyBindChat)) {
            this.setCurrentCreativeTab(CreativeTabs.tabAllSearch);
         } else {
            super.keyTyped(typedChar, keyCode);
         }
      } else {
         if (this.scrolling) {
            this.scrolling = false;
            this.searchField.setText("");
         }

         if (!this.checkHotbarKeys(keyCode)) {
            if (this.searchField.textboxKeyTyped(typedChar, keyCode)) {
               this.updateCreativeSearch();
            } else {
               super.keyTyped(typedChar, keyCode);
            }
         }
      }
   }

   public void updateCreativeSearch() {
      GuiContainerCreative.ContainerCreative inventorySlots = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
      inventorySlots.itemList.clear();

      for (Item item : Item.itemRegistry) {
         if (item != null && item.getCreativeTab() != null) {
            item.getSubItems(item, (CreativeTabs)null, inventorySlots.itemList);
         }
      }

      for (Enchantment enchantment : Enchantment.enchantmentsBookList) {
         if (enchantment != null && enchantment.type != null) {
            Items.enchanted_book.getAll(enchantment, inventorySlots.itemList);
         }
      }

      Iterator iterator = inventorySlots.itemList.iterator();
      String toLowerCase = this.searchField.getText().toLowerCase();

      while (iterator.hasNext()) {
         ItemStack itemStack = (ItemStack)iterator.next();
         boolean flag = false;

         for (String str : itemStack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips)) {
            if (EnumChatFormatting.getTextWithoutFormattingCodes(str).toLowerCase().contains(toLowerCase)) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            iterator.remove();
         }
      }

      this.currentScroll = 0.0F;
      inventorySlots.scrollTo(0.0F);
   }

   @Override
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      CreativeTabs creativeTabs = CreativeTabs.creativeTabArray[selectedTabIndex];
      if (creativeTabs.drawInForegroundOfTab()) {
         GlStateManager.disableBlend();
         this.fontRendererObj.drawString(I18n.format(creativeTabs.getTranslatedTabLabel()), 8.0F, 6.0F, 4210752);
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (mouseButton == 0) {
         int n = mouseX - this.guiLeft;
         int n2 = mouseY - this.guiTop;

         for (CreativeTabs creativeTabs : CreativeTabs.creativeTabArray) {
            if (this.isClickInTab(creativeTabs, n, n2)) {
               return;
            }
         }
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      if (state == 0) {
         int n = mouseX - this.guiLeft;
         int n2 = mouseY - this.guiTop;

         for (CreativeTabs creativeTabs : CreativeTabs.creativeTabArray) {
            if (this.isClickInTab(creativeTabs, n, n2)) {
               this.setCurrentCreativeTab(creativeTabs);
               return;
            }
         }
      }

      super.mouseReleased(mouseX, mouseY, state);
   }

   public boolean needsScrollBars() {
      return selectedTabIndex != CreativeTabs.tabInventory.getTabIndex()
         && CreativeTabs.creativeTabArray[selectedTabIndex].shouldHidePlayerInventory()
         && ((GuiContainerCreative.ContainerCreative)this.inventorySlots).isFull();
   }

   public void setCurrentCreativeTab(CreativeTabs creativeTabs) {
      int selectedTabIndex2 = selectedTabIndex;
      selectedTabIndex = creativeTabs.getTabIndex();
      GuiContainerCreative.ContainerCreative inventorySlots = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
      this.dragSplittingSlots.clear();
      inventorySlots.itemList.clear();
      creativeTabs.displayAllReleventItems(inventorySlots.itemList);
      if (creativeTabs == CreativeTabs.tabInventory) {
         Container inventoryContainer = this.mc.thePlayer.inventoryContainer;
         if (this.slots == null) {
            this.slots = inventorySlots.inventorySlots;
         }

         inventorySlots.inventorySlots = Lists.newArrayList();

         for (int i = 0; i < inventoryContainer.inventorySlots.size(); i++) {
            GuiContainerCreative.CreativeSlot creativeSlot = new GuiContainerCreative.CreativeSlot(inventoryContainer.inventorySlots.get(i), i);
            inventorySlots.inventorySlots.add(creativeSlot);
            if (i >= 5 && i < 9) {
               int n = i - 5;
               int n2 = n / 2;
               int n3 = n % 2;
               creativeSlot.xDisplayPosition = 9 + n2 * 54;
               creativeSlot.yDisplayPosition = 6 + n3 * 27;
            } else if (i >= 0 && i < 5) {
               creativeSlot.yDisplayPosition = -2000;
               creativeSlot.xDisplayPosition = -2000;
            } else if (i < inventoryContainer.inventorySlots.size()) {
               int n = i - 9;
               int n2 = n % 9;
               int n3 = n / 9;
               creativeSlot.xDisplayPosition = 9 + n2 * 18;
               if (i >= 36) {
                  creativeSlot.yDisplayPosition = 112;
               } else {
                  creativeSlot.yDisplayPosition = 54 + n3 * 18;
               }
            }
         }

         this.deleteItemSlot = new Slot(inventory, 0, 173, 112);
         inventorySlots.inventorySlots.add(this.deleteItemSlot);
      } else if (selectedTabIndex2 == CreativeTabs.tabInventory.getTabIndex()) {
         inventorySlots.inventorySlots = this.slots;
         this.slots = null;
      }

      if (this.searchField != null) {
         if (creativeTabs == CreativeTabs.tabAllSearch) {
            this.searchField.setVisible(true);
            this.searchField.setCanLoseFocus(false);
            this.searchField.setFocused(true);
            this.searchField.setText("");
            this.updateCreativeSearch();
         } else {
            this.searchField.setVisible(false);
            this.searchField.setCanLoseFocus(true);
            this.searchField.setFocused(false);
         }
      }

      this.currentScroll = 0.0F;
      inventorySlots.scrollTo(0.0F);
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int eventDWheel = Mouse.getEventDWheel();
      if (eventDWheel != 0 && this.needsScrollBars()) {
         int n = ((GuiContainerCreative.ContainerCreative)this.inventorySlots).itemList.size() / 9 - 5;
         if (eventDWheel > 0) {
            eventDWheel = 1;
         }

         if (eventDWheel < 0) {
            eventDWheel = -1;
         }

         this.currentScroll = (float)(this.currentScroll - (double)eventDWheel / n);
         this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
         ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      boolean isButtonDown = Mouse.isButtonDown(0);
      int guiLeft = this.guiLeft;
      int guiTop = this.guiTop;
      int n = guiLeft + 175;
      int n2 = guiTop + 18;
      int n3 = n + 14;
      int n4 = n2 + 112;
      if (!this.wasClicking && isButtonDown && mouseX >= n && mouseY >= n2 && mouseX < n3 && mouseY < n4) {
         this.isScrolling = this.needsScrollBars();
      }

      if (!isButtonDown) {
         this.isScrolling = false;
      }

      this.wasClicking = isButtonDown;
      if (this.isScrolling) {
         this.currentScroll = (mouseY - n2 - 7.5F) / (n4 - n2 - 15.0F);
         this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
         ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);

      for (CreativeTabs creativeTabs : CreativeTabs.creativeTabArray) {
         if (this.renderCreativeInventoryHoveringText(creativeTabs, mouseX, mouseY)) {
            break;
         }
      }

      if (this.deleteItemSlot != null
         && selectedTabIndex == CreativeTabs.tabInventory.getTabIndex()
         && this.isPointInRegion(this.deleteItemSlot.xDisplayPosition, this.deleteItemSlot.yDisplayPosition, 16, 16, mouseX, mouseY)) {
         this.drawCreativeTabHoveringText(I18n.format("inventory.binSlot"), mouseX, mouseY);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
   }

   @Override
   public void renderToolTip(ItemStack stack, int x, int y) {
      if (selectedTabIndex == CreativeTabs.tabAllSearch.getTabIndex()) {
         List tooltip = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
         CreativeTabs creativeTab = stack.getItem().getCreativeTab();
         if (creativeTab == null && stack.getItem() == Items.enchanted_book) {
            Map enchantments = EnchantmentHelper.getEnchantments(stack);
            if (enchantments.size() == 1) {
               Enchantment enchantmentById = Enchantment.getEnchantmentById((Integer)enchantments.keySet().iterator().next());

               for (CreativeTabs creativeTabs : CreativeTabs.creativeTabArray) {
                  if (creativeTabs.hasRelevantEnchantmentType(enchantmentById.type)) {
                     creativeTab = creativeTabs;
                     break;
                  }
               }
            }
         }

         if (creativeTab != null) {
            tooltip.add(1, "" + EnumChatFormatting.BOLD + EnumChatFormatting.BLUE + I18n.format(creativeTab.getTranslatedTabLabel()));
         }

         for (int i = 0; i < tooltip.size(); i++) {
            if (i == 0) {
               tooltip.set(i, stack.getRarity().rarityColor + (String)tooltip.get(i));
            } else {
               tooltip.set(i, EnumChatFormatting.GRAY + (String)tooltip.get(i));
            }
         }

         this.drawHoveringText(tooltip, x, y);
      } else {
         super.renderToolTip(stack, x, y);
      }
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderHelper.enableGUIStandardItemLighting();
      CreativeTabs creativeTabs = CreativeTabs.creativeTabArray[selectedTabIndex];

      for (CreativeTabs creativeTabs2 : CreativeTabs.creativeTabArray) {
         this.mc.getTextureManager().bindTexture(creativeInventoryTabs);
         if (creativeTabs2.getTabIndex() != selectedTabIndex) {
            this.renderTabIcon(creativeTabs2);
         }
      }

      this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + creativeTabs.getBackgroundImageName()));
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      this.searchField.drawTextBox();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      int n = this.guiLeft + 175;
      int n2 = this.guiTop + 18;
      int n3 = n2 + 112;
      this.mc.getTextureManager().bindTexture(creativeInventoryTabs);
      if (creativeTabs.shouldHidePlayerInventory()) {
         this.drawTexturedModalRect(n, n2 + (int)((n3 - n2 - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
      }

      this.renderTabIcon(creativeTabs);
      if (creativeTabs == CreativeTabs.tabInventory) {
         GuiInventory.drawEntityOnScreen(this.guiLeft + 43, this.guiTop + 45, 20, this.guiLeft + 43 - mouseX, this.guiTop + 45 - 30 - mouseY, this.mc.thePlayer);
      }
   }

   public boolean isClickInTab(CreativeTabs creativeTabs, int n, int n2) {
      int tabColumn = creativeTabs.getTabColumn();
      int n3 = 28 * tabColumn;
      int n4 = 0;
      if (tabColumn == 5) {
         n3 = this.xSize - 28 + 2;
      } else if (tabColumn > 0) {
         n3 += tabColumn;
      }

      if (creativeTabs.isTabInFirstRow()) {
         n4 -= 32;
      } else {
         n4 += this.ySize;
      }

      return n >= n3 && n <= n3 + 28 && n2 >= n4 && n2 <= n4 + 32;
   }

   public boolean renderCreativeInventoryHoveringText(CreativeTabs creativeTabs, int n, int n2) {
      int tabColumn = creativeTabs.getTabColumn();
      int n3 = 28 * tabColumn;
      int n4 = 0;
      if (tabColumn == 5) {
         n3 = this.xSize - 28 + 2;
      } else if (tabColumn > 0) {
         n3 += tabColumn;
      }

      if (creativeTabs.isTabInFirstRow()) {
         n4 -= 32;
      } else {
         n4 += this.ySize;
      }

      if (this.isPointInRegion(n3 + 3, n4 + 3, 23, 27, n, n2)) {
         this.drawCreativeTabHoveringText(I18n.format(creativeTabs.getTranslatedTabLabel()), n, n2);
         return true;
      } else {
         return false;
      }
   }

   public void renderTabIcon(CreativeTabs creativeTabs) {
      boolean flag = creativeTabs.getTabIndex() == selectedTabIndex;
      boolean isTabInFirstRow = creativeTabs.isTabInFirstRow();
      int tabColumn = creativeTabs.getTabColumn();
      int n = tabColumn * 28;
      int n2 = 0;
      int n3 = this.guiLeft + 28 * tabColumn;
      int guiTop = this.guiTop;
      byte b = 32;
      if (flag) {
         n2 += 32;
      }

      if (tabColumn == 5) {
         n3 = this.guiLeft + this.xSize - 28;
      } else if (tabColumn > 0) {
         n3 += tabColumn;
      }

      if (isTabInFirstRow) {
         guiTop -= 28;
      } else {
         n2 += 64;
         guiTop += this.ySize - 4;
      }

      GlStateManager.disableLighting();
      this.drawTexturedModalRect(n3, guiTop, n, n2, 28, b);
      zLevel = 100.0F;
      this.itemRender.zLevel = 100.0F;
      n3 += 6;
      guiTop = guiTop + 8 + (isTabInFirstRow ? 1 : -1);
      GlStateManager.enableLighting();
      GlStateManager.enableRescaleNormal();
      ItemStack iconItemStack = creativeTabs.getIconItemStack();
      this.itemRender.renderItemAndEffectIntoGUI(iconItemStack, n3, guiTop);
      this.itemRender.renderItemOverlays(this.fontRendererObj, iconItemStack, n3, guiTop);
      GlStateManager.disableLighting();
      this.itemRender.zLevel = 0.0F;
      zLevel = 0.0F;
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
      }

      if (button.id == 1) {
         this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
      }
   }

   public int getSelectedTabIndex() {
      return selectedTabIndex;
   }

   static class ContainerCreative extends Container {
      public List<ItemStack> itemList = Lists.newArrayList();

      public ContainerCreative(EntityPlayer entityPlayer) {
         InventoryPlayer inventory = entityPlayer.inventory;

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
               this.addSlotToContainer(new Slot(GuiContainerCreative.inventory, i * 9 + j, 9 + j * 18, 18 + i * 18));
            }
         }

         for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventory, i, 9 + i * 18, 112));
         }

         this.scrollTo(0.0F);
      }

      @Override
      public boolean canInteractWith(EntityPlayer playerIn) {
         return true;
      }

      public void scrollTo(float f) {
         int n = (this.itemList.size() + 9 - 1) / 9 - 5;
         int n2 = (int)(f * n + 0.5);
         if (n2 < 0) {
            n2 = 0;
         }

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
               int n3 = j + (i + n2) * 9;
               if (n3 >= 0 && n3 < this.itemList.size()) {
                  GuiContainerCreative.inventory.setInventorySlotContents(j + i * 9, this.itemList.get(n3));
               } else {
                  GuiContainerCreative.inventory.setInventorySlotContents(j + i * 9, (ItemStack)null);
               }
            }
         }
      }

      public boolean isFull() {
         return this.itemList.size() > 45;
      }

      @Override
      public void retrySlotClick(int slotId, int clickedButton, boolean mode, EntityPlayer playerIn) {
      }

      @Override
      public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
         if (index >= this.inventorySlots.size() - 9 && index < this.inventorySlots.size()) {
            Slot slot = this.inventorySlots.get(index);
            if (slot != null && slot.getHasStack()) {
               slot.putStack((ItemStack)null);
            }
         }

         return null;
      }

      @Override
      public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
         return slotIn.yDisplayPosition > 90;
      }

      @Override
      public boolean canDragIntoSlot(Slot slot) {
         return slot.inventory instanceof InventoryPlayer || slot.yDisplayPosition > 90 && slot.xDisplayPosition <= 162;
      }
   }

   class CreativeSlot extends Slot {
      public Slot slot;

      public CreativeSlot(Slot slot, int n) {
         super(slot.inventory, n, 0, 0);
         this.slot = slot;
      }

      @Override
      public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
         this.slot.onPickupFromSlot(playerIn, stack);
      }

      @Override
      public boolean isItemValid(ItemStack stack) {
         return this.slot.isItemValid(stack);
      }

      @Override
      public ItemStack getStack() {
         return this.slot.getStack();
      }

      @Override
      public boolean getHasStack() {
         return this.slot.getHasStack();
      }

      @Override
      public void putStack(ItemStack stack) {
         this.slot.putStack(stack);
      }

      @Override
      public void onSlotChanged() {
         this.slot.onSlotChanged();
      }

      @Override
      public int getSlotStackLimit() {
         return this.slot.getSlotStackLimit();
      }

      @Override
      public int getItemStackLimit(ItemStack stack) {
         return this.slot.getItemStackLimit(stack);
      }

      @Override
      public String getSlotTexture() {
         return this.slot.getSlotTexture();
      }

      @Override
      public ItemStack decrStackSize(int amount) {
         return this.slot.decrStackSize(amount);
      }

      @Override
      public boolean isHere(IInventory inv, int slotIn) {
         return this.slot.isHere(inv, slotIn);
      }
   }
}
