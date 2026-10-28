package net.minecraft.client.gui.inventory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class GuiBeacon extends GuiContainer {
    public static Logger logger = LogManager.getLogger();
    public static ResourceLocation beaconGuiTextures = new ResourceLocation("textures/gui/container/beacon.png");
    public TileEntityBeacon tileBeacon;
    public GuiBeacon.ConfirmButton beaconConfirmButton;
    public boolean buttonsNotDrawn;

    public GuiBeacon(InventoryPlayer inventoryPlayer, TileEntityBeacon tileEntityBeacon) {
        super(new ContainerBeacon(inventoryPlayer, tileEntityBeacon));
        this.tileBeacon = tileEntityBeacon;
        this.xSize = 230;
        this.ySize = 219;
    }

    @Override
    public void initGui() throws java.io.IOException {
        super.initGui();
        this.buttonList.add(this.beaconConfirmButton = new GuiBeacon.ConfirmButton(-1, this.guiLeft + 164, this.guiTop + 107));
        this.buttonList.add(new GuiBeacon.CancelButton(-2, this.guiLeft + 190, this.guiTop + 107));
        this.buttonsNotDrawn = true;
        this.beaconConfirmButton.enabled = false;
    }

    @Override
    public void updateScreen() throws java.io.IOException {
        super.updateScreen();
        if (this.buttonsNotDrawn && this.tileBeacon.getLevels() >= 0) {
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
                    if (i >= this.tileBeacon.getLevels()) {
                        powerButton.enabled = false;
                    } else if (id == this.tileBeacon.getPrimaryEffect()) {
                        powerButton.setFlag(true);
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
                if (b >= this.tileBeacon.getLevels()) {
                    powerButton.enabled = false;
                } else if (id == this.tileBeacon.getSecondaryEffect()) {
                    powerButton.setFlag(true);
                }
            }

            if (this.tileBeacon.getPrimaryEffect() > 0) {
                GuiBeacon.PowerButton powerButton = new GuiBeacon.PowerButton(
                    b << 8 | this.tileBeacon.getPrimaryEffect(),
                    this.guiLeft + 167 + (n - 1) * 24 - n2 / 2,
                    this.guiTop + 47,
                    this.tileBeacon.getPrimaryEffect(),
                    b
                );
                this.buttonList.add(powerButton);
                if (b >= this.tileBeacon.getLevels()) {
                    powerButton.enabled = false;
                } else if (this.tileBeacon.getPrimaryEffect() == this.tileBeacon.getSecondaryEffect()) {
                    powerButton.setFlag(true);
                }
            }
        }

        this.beaconConfirmButton.enabled = this.tileBeacon.getStackInSlot(0) != null && this.tileBeacon.getPrimaryEffect() > 0;
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.id == -2) {
            this.mc.displayGuiScreen(null);
        } else if (button.id == -1) {
            String str = "MC|Beacon";
            ByteBuf buffer = Unpooled.buffer();

            try {
                buffer.writeInt(this.tileBeacon.getPrimaryEffect());
                buffer.writeInt(this.tileBeacon.getSecondaryEffect());
                this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(str, buffer));
            } catch (Exception e) {
                logger.error("Couldn't send beacon info", e);
            } finally {
                buffer.release();
            }

            this.mc.displayGuiScreen(null);
        } else if (button instanceof GuiBeacon.PowerButton) {
            if (((GuiBeacon.PowerButton)button).isFlag()) {
                return;
            }

            int id = button.id;
            int n = id & 0xFF;
            int n2 = id >> 8;
            if (n2 < 3) {
                this.tileBeacon.setPrimaryEffect(n);
            } else {
                this.tileBeacon.setSecondaryEffect(n);
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
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(beaconGuiTextures);
        int n = (this.width - this.xSize) / 2;
        int n2 = (this.height - this.ySize) / 2;
        drawTexturedRect(n, n2, 0.0F, 0.0F, this.xSize, this.ySize);
        itemRender.zLevel = 100.0F;
        itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), new ItemStack(Items.emerald), n + 42, n2 + 109);
        itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), new ItemStack(Items.diamond), n + 42 + 22, n2 + 109);
        itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), new ItemStack(Items.gold_ingot), n + 42 + 44, n2 + 109);
        itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), new ItemStack(Items.iron_ingot), n + 42 + 66, n2 + 109);
        itemRender.zLevel = 0.0F;
    }

    public static class Button extends GuiButton {
        public ResourceLocation resourceLocation;
        public int intValue;
        public int intValue2;
        public boolean flag;

        public Button(int n, int n2, int n3, ResourceLocation resourceLocation, int n4, int n5) {
            super(n, n2, n3, 22, 22, "");
            this.resourceLocation = resourceLocation;
            this.intValue = n4;
            this.intValue2 = n5;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                mc.getTextureManager().bindTexture(GuiBeacon.beaconGuiTextures);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= this.xPosition
                    && mouseY >= this.yPosition
                    && mouseX < this.xPosition + this.width
                    && mouseY < this.yPosition + this.height;
                short s = 219;
                int n = 0;
                if (!this.enabled) {
                    n += this.width * 2;
                } else if (this.flag) {
                    n += this.width * 1;
                } else if (this.hovered) {
                    n += this.width * 3;
                }

                drawTexturedRect(this.xPosition, this.yPosition, n, s, this.width, this.height);
                if (!GuiBeacon.beaconGuiTextures.equals(this.resourceLocation)) {
                    mc.getTextureManager().bindTexture(this.resourceLocation);
                }

                drawTexturedRect(this.xPosition + 2, this.yPosition + 2, this.intValue, this.intValue2, 18, 18);
            }
        }

        public boolean isFlag() {
            return this.flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }
    }

    public class CancelButton extends GuiBeacon.Button {
        public CancelButton(int n, int n2, int n3) {
            super(n, n2, n3, GuiBeacon.beaconGuiTextures, 112, 220);
        }

        @Override
        public void drawButtonForegroundLayer(int mouseX, int mouseY) {
            GuiBeacon.this.drawCreativeTabHoveringText(I18n.format("gui.cancel"), mouseX, mouseY);
        }
    }

    public class ConfirmButton extends GuiBeacon.Button {
        public ConfirmButton(int n, int n2, int n3) {
            super(n, n2, n3, GuiBeacon.beaconGuiTextures, 90, 220);
        }

        @Override
        public void drawButtonForegroundLayer(int mouseX, int mouseY) {
            GuiBeacon.this.drawCreativeTabHoveringText(I18n.format("gui.done"), mouseX, mouseY);
        }
    }

    public class PowerButton extends GuiBeacon.Button {
        public int intValue3;
        public int intValue22;

        public PowerButton(int n, int n2, int n3, int n4, int n5) {
            super(
                n,
                n2,
                n3,
                GuiContainer.inventoryBackground,
                0 + Potion.potionTypes[n4].getStatusIconIndex() % 8 * 18,
                198 + Potion.potionTypes[n4].getStatusIconIndex() / 8 * 18
            );
            this.intValue3 = n4;
            this.intValue22 = n5;
        }

        @Override
        public void drawButtonForegroundLayer(int mouseX, int mouseY) {
            String format = I18n.format(Potion.potionTypes[this.intValue3].getName());
            if (this.intValue22 >= 3 && this.intValue3 != Potion.regeneration.id) {
                format = format + " II";
            }

            GuiBeacon.this.drawCreativeTabHoveringText(format, mouseX, mouseY);
        }
    }
}
