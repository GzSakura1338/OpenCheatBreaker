package net.minecraft.client.gui;

import java.util.Random;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public class GuiEnchantment extends GuiContainer {
    public static ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
    public static ResourceLocation BOOK_TEXTURE = new ResourceLocation("textures/entity/enchanting_table_book.png");
    public static ModelBook BOOK_MODEL = new ModelBook();
    public Random random = new Random();
    public ContainerEnchantment enchantingScreenHandler = (ContainerEnchantment)this.inventorySlots;
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float approximatePageAngle;
    public float pageRotationSpeed;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    public ItemStack stack;
    public String text;

    public GuiEnchantment(InventoryPlayer inventoryPlayer, World world, int n, int n2, int n3, String str) {
        super(new ContainerEnchantment(inventoryPlayer, world, n, n2, n3));
        this.text = str;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(this.text == null ? I18n.format("container.enchant") : this.text, 12, 5, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    public void updateScreen() throws java.io.IOException {
        super.updateScreen();
        this.doTick();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int n = (this.width - this.xSize) / 2;
        int n2 = (this.height - this.ySize) / 2;

        for (int i = 0; i < 3; i++) {
            int n3 = mouseX - (n + 60);
            int n4 = mouseY - (n2 + 14 + 19 * i);
            if (n3 >= 0 && n4 >= 0 && n3 < 108 && n4 < 19 && this.enchantingScreenHandler.enchantItem(this.mc.thePlayer, i)) {
                this.mc.playerController.sendEnchantPacket(this.enchantingScreenHandler.windowId, i);
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
        GL11.glPushMatrix();
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glViewport(
            (scaledResolution.getScaledWidth() - 320) / 2 * scaledResolution.getScaleFactor(),
            (scaledResolution.getScaledHeight() - 240) / 2 * scaledResolution.getScaleFactor(),
            320 * scaledResolution.getScaleFactor(),
            240 * scaledResolution.getScaleFactor()
        );
        GL11.glTranslatef(-0.34F, 0.23F, 0.0F);
        Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
        float f = 1.0F;
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        RenderHelper.enableStandardItemLighting();
        GL11.glTranslatef(0.0F, 3.3F, -16.0F);
        GL11.glScalef(f, f, f);
        float f2 = 5.0F;
        GL11.glScalef(f2, f2, f2);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BOOK_TEXTURE);
        GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
        float f3 = this.pageTurningSpeed + (this.nextPageTurningSpeed - this.pageTurningSpeed) * partialTicks;
        GL11.glTranslatef((1.0F - f3) * 0.2F, (1.0F - f3) * 0.1F, (1.0F - f3) * 0.25F);
        GL11.glRotatef(-(1.0F - f3) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        float f4 = this.pageAngle + (this.nextPageAngle - this.pageAngle) * partialTicks + 0.25F;
        float f5 = this.pageAngle + (this.nextPageAngle - this.pageAngle) * partialTicks + 0.75F;
        f4 = (f4 - MathHelper.truncateDoubleToInt(f4)) * 1.6F - 0.3F;
        f5 = (f5 - MathHelper.truncateDoubleToInt(f5)) * 1.6F - 0.3F;
        if (f4 < 0.0F) {
            f4 = 0.0F;
        }

        if (f5 < 0.0F) {
            f5 = 0.0F;
        }

        if (f4 > 1.0F) {
            f4 = 1.0F;
        }

        if (f5 > 1.0F) {
            f5 = 1.0F;
        }

        GL11.glEnable(32826);
        BOOK_MODEL.render(null, 0.0F, f4, f5, f3, 0.0F, 0.0625F);
        GL11.glDisable(32826);
        RenderHelper.disableStandardItemLighting();
        GL11.glMatrixMode(5889);
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        EnchantmentNameParts.instance.reseedRandomGenerator(this.enchantingScreenHandler.nameSeed);

        for (int i = 0; i < 3; i++) {
            String generateNewRandomName = EnchantmentNameParts.instance.generateNewRandomName();
            floatValue = 0.0F;
            this.mc.getTextureManager().bindTexture(TEXTURE);
            int n3 = this.enchantingScreenHandler.enchantLevels[i];
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (n3 == 0) {
                drawTexturedRect(n + 60, n2 + 14 + 19 * i, 0.0F, 185.0F, 108, 19);
            } else {
                String str = "" + n3;
                FontRenderer standardGalacticFontRenderer = this.mc.standardGalacticFontRenderer;
                int n4 = 6839882;
                if (this.mc.thePlayer.experienceLevel < n3 && !this.mc.thePlayer.capabilities.isCreativeMode) {
                    drawTexturedRect(n + 60, n2 + 14 + 19 * i, 0.0F, 185.0F, 108, 19);
                    standardGalacticFontRenderer.drawSplitString(generateNewRandomName, n + 62, n2 + 16 + 19 * i, 104, (n4 & 16711422) >> 1);
                    standardGalacticFontRenderer = this.mc.fontRendererObj;
                    n4 = 4226832;
                    standardGalacticFontRenderer.drawShadowedString(str, n + 62 + 104 - standardGalacticFontRenderer.getStringWidth(str), n2 + 16 + 19 * i + 7, n4);
                } else {
                    int n5 = mouseX - (n + 60);
                    int n6 = mouseY - (n2 + 14 + 19 * i);
                    if (n5 >= 0 && n6 >= 0 && n5 < 108 && n6 < 19) {
                        drawTexturedRect(n + 60, n2 + 14 + 19 * i, 0.0F, 204.0F, 108, 19);
                        n4 = 16777088;
                    } else {
                        drawTexturedRect(n + 60, n2 + 14 + 19 * i, 0.0F, 166.0F, 108, 19);
                    }

                    standardGalacticFontRenderer.drawSplitString(generateNewRandomName, n + 62, n2 + 16 + 19 * i, 104, n4);
                    standardGalacticFontRenderer = this.mc.fontRendererObj;
                    n4 = 8453920;
                    standardGalacticFontRenderer.drawShadowedString(str, n + 62 + 104 - standardGalacticFontRenderer.getStringWidth(str), n2 + 16 + 19 * i + 7, n4);
                }
            }
        }
    }

    public void doTick() {
        ItemStack stack = this.inventorySlots.getSlot(0).getStack();
        if (!ItemStack.areItemStacksEqual(stack, this.stack)) {
            this.stack = stack;

            do {
                this.approximatePageAngle = this.approximatePageAngle + (this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.nextPageAngle <= this.approximatePageAngle + 1.0F && this.nextPageAngle >= this.approximatePageAngle - 1.0F);
        }

        this.ticks++;
        this.pageAngle = this.nextPageAngle;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        boolean flag = false;

        for (int i = 0; i < 3; i++) {
            if (this.enchantingScreenHandler.enchantLevels[i] != 0) {
                flag = true;
            }
        }

        if (flag) {
            this.nextPageTurningSpeed += 0.2F;
        } else {
            this.nextPageTurningSpeed -= 0.2F;
        }

        if (this.nextPageTurningSpeed < 0.0F) {
            this.nextPageTurningSpeed = 0.0F;
        }

        if (this.nextPageTurningSpeed > 1.0F) {
            this.nextPageTurningSpeed = 1.0F;
        }

        float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4F;
        float f2 = 0.2F;
        if (f < -f2) {
            f = -f2;
        }

        if (f > f2) {
            f = f2;
        }

        this.pageRotationSpeed = this.pageRotationSpeed + (f - this.pageRotationSpeed) * 0.9F;
        this.nextPageAngle = this.nextPageAngle + this.pageRotationSpeed;
    }
}
