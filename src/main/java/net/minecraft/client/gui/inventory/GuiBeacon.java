package net.minecraft.client.gui.inventory;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiBeacon extends GuiContainer {
   public static Logger logger = LogManager.getLogger();
   public static ResourceLocation beaconGuiTextures = new ResourceLocation("textures/gui/container/beacon.png");
   public IInventory tileBeacon;
   public GuiBeacon.ConfirmButton beaconConfirmButton;
   public boolean buttonsNotDrawn;

   public GuiBeacon(InventoryPlayer playerInventory, IInventory tileBeaconIn) {
      super(new ContainerBeacon(playerInventory, tileBeaconIn));
      this.tileBeacon = tileBeaconIn;
      this.xSize = 230;
      this.ySize = 219;
   }

   @Override
   public void initGui() {
      super.initGui();
      this.buttonList.add(this.beaconConfirmButton = new GuiBeacon.ConfirmButton(-1, this.guiLeft + 164, this.guiTop + 107));
      this.buttonList.add(new GuiBeacon.CancelButton(-2, this.guiLeft + 190, this.guiTop + 107));
      this.buttonsNotDrawn = true;
      this.beaconConfirmButton.enabled = false;
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      int field = this.tileBeacon.getField(0);
      int fieldRef2 = this.tileBeacon.getField(1);
      int fieldRef3 = this.tileBeacon.getField(2);
      if (this.buttonsNotDrawn && field >= 0) {
         this.buttonsNotDrawn = false;

         for (int i = 0; i <= 2; i++) {
            int length = TileEntityBeacon.effectsList[i].length;
            int n = length * 22 + (length - 1) * 2;

            for (int j = 0; j < length; j++) {
               int id = TileEntityBeacon.effectsList[i][j].id;
               GuiBeacon.PowerButton powerButton = new GuiBeacon.PowerButton(
                  i << 8 | id, this.guiLeft + 76 + j * 24 - n / 2, this.guiTop + 22 + i * 25, id, i
               );
               this.buttonList.add(powerButton);
               if (i >= field) {
                  powerButton.enabled = false;
               } else if (id == fieldRef2) {
                  powerButton.setDisabled(true);
               }
            }
         }

         byte b = 3;
         int n = TileEntityBeacon.effectsList[b].length + 1;
         int n2 = n * 22 + (n - 1) * 2;

         for (int i = 0; i < n - 1; i++) {
            int id = TileEntityBeacon.effectsList[b][i].id;
            GuiBeacon.PowerButton powerButton = new GuiBeacon.PowerButton(
               b << 8 | id, this.guiLeft + 167 + i * 24 - n2 / 2, this.guiTop + 47, id, b
            );
            this.buttonList.add(powerButton);
            if (b >= field) {
               powerButton.enabled = false;
            } else if (id == fieldRef3) {
               powerButton.setDisabled(true);
            }
         }

         if (fieldRef2 > 0) {
            GuiBeacon.PowerButton powerButton = new GuiBeacon.PowerButton(
               b << 8 | fieldRef2, this.guiLeft + 167 + (n - 1) * 24 - n2 / 2, this.guiTop + 47, fieldRef2, b
            );
            this.buttonList.add(powerButton);
            if (b >= field) {
               powerButton.enabled = false;
            } else if (fieldRef2 == fieldRef3) {
               powerButton.setDisabled(true);
            }
         }
      }

      this.beaconConfirmButton.enabled = this.tileBeacon.getStackInSlot(0) != null && fieldRef2 > 0;
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.id == -2) {
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (button.id == -1) {
         String str = "MC|Beacon";
         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
         packetBuffer.writeInt(this.tileBeacon.getField(1));
         packetBuffer.writeInt(this.tileBeacon.getField(2));
         this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(str, packetBuffer));
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (button instanceof GuiBeacon.PowerButton) {
         if (((GuiBeacon.PowerButton)button).isDisabled()) {
            return;
         }

         int id = button.id;
         int n = id & 0xFF;
         int n2 = id >> 8;
         if (n2 < 3) {
            this.tileBeacon.setField(1, n);
         } else {
            this.tileBeacon.setField(2, n);
         }

         this.buttonList.clear();
         this.initGui();
         this.updateScreen();
      }
   }

   @Override
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      RenderHelper.disableStandardItemLighting();
      this.drawCenteredString(this.fontRendererObj, I18n.format("tile.beacon.primary"), 62, 10, 14737632);
      this.drawCenteredString(this.fontRendererObj, I18n.format("tile.beacon.secondary"), 169, 10, 14737632);

      for (GuiButton guiButton : this.buttonList) {
         if (guiButton.isMouseOver()) {
            guiButton.drawButtonForegroundLayer(mouseX - this.guiLeft, mouseY - this.guiTop);
            break;
         }
      }

      RenderHelper.enableGUIStandardItemLighting();
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(beaconGuiTextures);
      int n = (this.width - this.xSize) / 2;
      int n2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(n, n2, 0, 0, this.xSize, this.ySize);
      this.itemRender.zLevel = 100.0F;
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.emerald), n + 42, n2 + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.diamond), n + 42 + 22, n2 + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.gold_ingot), n + 42 + 44, n2 + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.iron_ingot), n + 42 + 66, n2 + 109);
      this.itemRender.zLevel = 0.0F;
   }

   static class Button extends GuiButton {
      public ResourceLocation texture;
      public int u;
      public int v;
      public boolean disabled;

      public Button(int n, int n2, int n3, ResourceLocation resourceLocation, int n4, int n5) {
         super(n, n2, n3, 22, 22, "");
         this.texture = resourceLocation;
         this.u = n4;
         this.v = n5;
      }

      @Override
      public void drawButton(Minecraft mc, int mouseX, int mouseY) {
         if (this.visible) {
            mc.getTextureManager().bindTexture(GuiBeacon.beaconGuiTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition
               && mouseY >= this.yPosition
               && mouseX < this.xPosition + this.width
               && mouseY < this.yPosition + this.height;
            short s = 219;
            int n = 0;
            if (!this.enabled) {
               n += this.width * 2;
            } else if (this.disabled) {
               n += this.width * 1;
            } else if (this.hovered) {
               n += this.width * 3;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, n, s, this.width, this.height);
            if (!GuiBeacon.beaconGuiTextures.equals(this.texture)) {
               mc.getTextureManager().bindTexture(this.texture);
            }

            this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 2, this.u, this.v, 18, 18);
         }
      }

      public boolean isDisabled() {
         return this.disabled;
      }

      public void setDisabled(boolean flag) {
         this.disabled = flag;
      }
   }

   class CancelButton extends GuiBeacon.Button {
      public CancelButton(int n, int n2, int n3) {
         super(n, n2, n3, GuiBeacon.beaconGuiTextures, 112, 220);
      }

      @Override
      public void drawButtonForegroundLayer(int mouseX, int mouseY) {
         GuiBeacon.this.drawCreativeTabHoveringText(I18n.format("gui.cancel"), mouseX, mouseY);
      }
   }

   class ConfirmButton extends GuiBeacon.Button {
      public ConfirmButton(int n, int n2, int n3) {
         super(n, n2, n3, GuiBeacon.beaconGuiTextures, 90, 220);
      }

      @Override
      public void drawButtonForegroundLayer(int mouseX, int mouseY) {
         GuiBeacon.this.drawCreativeTabHoveringText(I18n.format("gui.done"), mouseX, mouseY);
      }
   }

   class PowerButton extends GuiBeacon.Button {
      public int secondaryEffectId;
      public int baseLevels;

      public PowerButton(int n, int n2, int n3, int n4, int n5) {
         super(
            n,
            n2,
            n3,
            GuiContainer.inventoryBackground,
            0 + Potion.potionTypes[n4].getStatusIconIndex() % 8 * 18,
            198 + Potion.potionTypes[n4].getStatusIconIndex() / 8 * 18
         );
         this.secondaryEffectId = n4;
         this.baseLevels = n5;
      }

      @Override
      public void drawButtonForegroundLayer(int mouseX, int mouseY) {
         String format = I18n.format(Potion.potionTypes[this.secondaryEffectId].getName());
         if (this.baseLevels >= 3 && this.secondaryEffectId != Potion.regeneration.id) {
            format = format + " II";
         }

         GuiBeacon.this.drawCreativeTabHoveringText(format, mouseX, mouseY);
      }
   }
}
