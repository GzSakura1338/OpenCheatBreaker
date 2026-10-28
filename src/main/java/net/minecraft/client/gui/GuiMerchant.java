package net.minecraft.client.gui;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class GuiMerchant extends GuiContainer {
    public static Logger logger = LogManager.getLogger();
    public static ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/villager.png");
    public IMerchant trader;
    public GuiMerchant.MerchantButton merchantButton;
    public GuiMerchant.MerchantButton merchantButton2;
    public int page;
    public String text;

    public GuiMerchant(InventoryPlayer inventoryPlayer, IMerchant merchant, World world, String str) {
        super(new ContainerMerchant(inventoryPlayer, merchant, world));
        this.trader = merchant;
        this.text = str != null && str.length() >= 1 ? str : I18n.format("entity.Villager.name");
    }

    @Override
    public void initGui() throws java.io.IOException {
        super.initGui();
        int n = (this.width - this.xSize) / 2;
        int n2 = (this.height - this.ySize) / 2;
        this.buttonList.add(this.merchantButton = new GuiMerchant.MerchantButton(1, n + 120 + 27, n2 + 24 - 1, true));
        this.buttonList.add(this.merchantButton2 = new GuiMerchant.MerchantButton(2, n + 36 - 19, n2 + 24 - 1, false));
        this.merchantButton.enabled = false;
        this.merchantButton2.enabled = false;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(this.text, this.xSize / 2 - this.fontRendererObj.getStringWidth(this.text) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    public void updateScreen() throws java.io.IOException {
        super.updateScreen();
        MerchantRecipeList recipes = this.trader.getRecipes(this.mc.thePlayer);
        if (recipes != null) {
            this.merchantButton.enabled = this.page < recipes.size() - 1;
            this.merchantButton2.enabled = this.page > 0;
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        boolean flag = false;
        if (button == this.merchantButton) {
            this.page++;
            flag = true;
        } else if (button == this.merchantButton2) {
            this.page--;
            flag = true;
        }

        if (flag) {
            ((ContainerMerchant)this.inventorySlots).setCurrentRecipeIndex(this.page);
            ByteBuf buffer = Unpooled.buffer();

            try {
                buffer.writeInt(this.page);
                this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|TrSel", buffer));
            } catch (Exception e) {
                logger.error("Couldn't send trade info", e);
            } finally {
                buffer.release();
            }
        }
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int n = (this.width - this.xSize) / 2;
        int n2 = (this.height - this.ySize) / 2;
        drawTexturedRect(n, n2, 0.0F, 0.0F, this.xSize, this.ySize);
        MerchantRecipeList recipes = this.trader.getRecipes(this.mc.thePlayer);
        if (recipes != null && !recipes.isEmpty()) {
            int page = this.page;
            MerchantRecipe merchantRecipe = (MerchantRecipe)recipes.get(page);
            if (merchantRecipe.isRecipeDisabled()) {
                this.mc.getTextureManager().bindTexture(TEXTURE);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(2896);
                drawTexturedRect(this.guiLeft + 83, this.guiTop + 21, 212.0F, 0.0F, 28, 21);
                drawTexturedRect(this.guiLeft + 83, this.guiTop + 51, 212.0F, 0.0F, 28, 21);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        super.drawScreen(mouseX, mouseY, partialTicks);
        MerchantRecipeList recipes = this.trader.getRecipes(this.mc.thePlayer);
        if (recipes != null && !recipes.isEmpty()) {
            int n = (this.width - this.xSize) / 2;
            int n2 = (this.height - this.ySize) / 2;
            int page = this.page;
            MerchantRecipe merchantRecipe = (MerchantRecipe)recipes.get(page);
            GL11.glPushMatrix();
            ItemStack itemToBuy = merchantRecipe.getItemToBuy();
            ItemStack secondItemToBuy = merchantRecipe.getSecondItemToBuy();
            ItemStack itemToSell = merchantRecipe.getItemToSell();
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(2896);
            GL11.glEnable(32826);
            GL11.glEnable(2903);
            GL11.glEnable(2896);
            itemRender.zLevel = 100.0F;
            itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemToBuy, n + 36, n2 + 24);
            itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemToBuy, n + 36, n2 + 24);
            if (secondItemToBuy != null) {
                itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), secondItemToBuy, n + 62, n2 + 24);
                itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), secondItemToBuy, n + 62, n2 + 24);
            }

            itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemToSell, n + 120, n2 + 24);
            itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemToSell, n + 120, n2 + 24);
            itemRender.zLevel = 0.0F;
            GL11.glDisable(2896);
            if (this.isPointInRegion(36, 24, 16, 16, mouseX, mouseY)) {
                this.renderToolTip(itemToBuy, mouseX, mouseY);
            } else if (secondItemToBuy != null && this.isPointInRegion(62, 24, 16, 16, mouseX, mouseY)) {
                this.renderToolTip(secondItemToBuy, mouseX, mouseY);
            } else if (this.isPointInRegion(120, 24, 16, 16, mouseX, mouseY)) {
                this.renderToolTip(itemToSell, mouseX, mouseY);
            }

            GL11.glPopMatrix();
            GL11.glEnable(2896);
            GL11.glEnable(2929);
            RenderHelper.enableStandardItemLighting();
        }
    }

    public IMerchant getMerchant() {
        return this.trader;
    }

    public static class MerchantButton extends GuiButton {
        public boolean flag;

        public MerchantButton(int n, int n2, int n3, boolean flag) {
            super(n, n2, n3, 12, 19, "");
            this.flag = flag;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                mc.getTextureManager().bindTexture(GuiMerchant.TEXTURE);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                boolean flag = mouseX >= this.xPosition
                    && mouseY >= this.yPosition
                    && mouseX < this.xPosition + this.width
                    && mouseY < this.yPosition + this.height;
                int n = 0;
                int n2 = 176;
                if (!this.enabled) {
                    n2 += this.width * 2;
                } else if (flag) {
                    n2 += this.width;
                }

                if (!this.flag) {
                    n += this.height;
                }

                drawTexturedRect(this.xPosition, this.yPosition, n2, n, this.width, this.height);
            }
        }
    }
}
