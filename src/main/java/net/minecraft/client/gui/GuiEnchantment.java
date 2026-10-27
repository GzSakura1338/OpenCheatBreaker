package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import org.lwjgl.util.glu.Project;

public class GuiEnchantment extends GuiContainer {
   public static ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
   public static ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE = new ResourceLocation("textures/entity/enchanting_table_book.png");
   public static ModelBook MODEL_BOOK = new ModelBook();
   public Random random = new Random();
   public InventoryPlayer playerInventory;
   public ContainerEnchantment container;
   public int ticks;
   public float nextPageAngle;
   public float pageAngle;
   public float approximatePageAngle;
   public float pageRotationSpeed;
   public float nextPageTurningSpeed;
   public float pageTurningSpeed;
   public ItemStack stack;
   public IWorldNameable nameable;

   public GuiEnchantment(InventoryPlayer inventory, World worldIn, IWorldNameable worldNameable) {
      super(new ContainerEnchantment(inventory, worldIn));
      this.playerInventory = inventory;
      this.container = (ContainerEnchantment)this.inventorySlots;
      this.nameable = worldNameable;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRendererObj.drawString(this.nameable.getDisplayName().getUnformattedText(), 12.0F, 5.0F, 4210752);
      this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.doTick();
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      int n = (this.width - this.xSize) / 2;
      int n2 = (this.height - this.ySize) / 2;

      for (int i = 0; i < 3; i++) {
         int n3 = mouseX - (n + 60);
         int n4 = mouseY - (n2 + 14 + 19 * i);
         if (n3 >= 0 && n4 >= 0 && n3 < 108 && n4 < 19 && this.container.enchantItem(this.mc.thePlayer, i)) {
            this.mc.playerController.sendEnchantPacket(this.container.windowId, i);
         }
      }
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
      int n = (this.width - this.xSize) / 2;
      int n2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(n, n2, 0, 0, this.xSize, this.ySize);
      GlStateManager.pushMatrix();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      GlStateManager.viewport(
         (scaledResolution.getScaledWidth() - 320) / 2 * scaledResolution.getScaleFactor(),
         (scaledResolution.getScaledHeight() - 240) / 2 * scaledResolution.getScaleFactor(),
         320 * scaledResolution.getScaleFactor(),
         240 * scaledResolution.getScaleFactor()
      );
      GlStateManager.translate(-0.34F, 0.23F, 0.0F);
      Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
      float f = 1.0F;
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.translate(0.0F, 3.3F, -16.0F);
      GlStateManager.scale(f, f, f);
      float f2 = 5.0F;
      GlStateManager.scale(f2, f2, f2);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_BOOK_TEXTURE);
      GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
      float f3 = this.pageTurningSpeed + (this.nextPageTurningSpeed - this.pageTurningSpeed) * partialTicks;
      GlStateManager.translate((1.0F - f3) * 0.2F, (1.0F - f3) * 0.1F, (1.0F - f3) * 0.25F);
      GlStateManager.rotate(-(1.0F - f3) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
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

      GlStateManager.enableRescaleNormal();
      MODEL_BOOK.render((Entity)null, 0.0F, f4, f5, f3, 0.0F, 0.0625F);
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.matrixMode(5889);
      GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantmentNameParts.getInstance().reseedRandomGenerator(this.container.xpSeed);
      int lapisAmount = this.container.getLapisAmount();

      for (int i = 0; i < 3; i++) {
         int n3 = n + 60;
         int n4 = n3 + 20;
         byte b = 86;
         String generateNewRandomName = EnchantmentNameParts.getInstance().generateNewRandomName();
         zLevel = 0.0F;
         this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
         int n5 = this.container.enchantLevels[i];
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         if (n5 == 0) {
            this.drawTexturedModalRect(n3, n2 + 14 + 19 * i, 0, 185, 108, 19);
         } else {
            String str = "" + n5;
            FontRenderer standardGalacticFontRenderer = this.mc.standardGalacticFontRenderer;
            int n6 = 6839882;
            if ((lapisAmount < i + 1 || this.mc.thePlayer.experienceLevel < n5) && !this.mc.thePlayer.capabilities.isCreativeMode) {
               this.drawTexturedModalRect(n3, n2 + 14 + 19 * i, 0, 185, 108, 19);
               this.drawTexturedModalRect(n3 + 1, n2 + 15 + 19 * i, 16 * i, 239, 16, 16);
               standardGalacticFontRenderer.drawSplitString(generateNewRandomName, n4, n2 + 16 + 19 * i, b, (n6 & 16711422) >> 1);
               n6 = 4226832;
            } else {
               int n7 = mouseX - (n + 60);
               int n8 = mouseY - (n2 + 14 + 19 * i);
               if (n7 >= 0 && n8 >= 0 && n7 < 108 && n8 < 19) {
                  this.drawTexturedModalRect(n3, n2 + 14 + 19 * i, 0, 204, 108, 19);
                  n6 = 16777088;
               } else {
                  this.drawTexturedModalRect(n3, n2 + 14 + 19 * i, 0, 166, 108, 19);
               }

               this.drawTexturedModalRect(n3 + 1, n2 + 15 + 19 * i, 16 * i, 223, 16, 16);
               standardGalacticFontRenderer.drawSplitString(generateNewRandomName, n4, n2 + 16 + 19 * i, b, n6);
               n6 = 8453920;
            }

            standardGalacticFontRenderer = this.mc.fontRendererObj;
            standardGalacticFontRenderer.drawStringWithShadow(str, n4 + 86 - standardGalacticFontRenderer.getStringWidth(str), n2 + 16 + 19 * i + 7, n6);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      boolean isCreativeMode = this.mc.thePlayer.capabilities.isCreativeMode;
      int lapisAmount = this.container.getLapisAmount();

      for (int i = 0; i < 3; i++) {
         int n = this.container.enchantLevels[i];
         int n2 = this.container.enchantmentIds[i];
         int n3 = i + 1;
         if (this.isPointInRegion(60, 14 + 19 * i, 108, 17, mouseX, mouseY) && n > 0 && n2 >= 0) {
            ArrayList newArrayList = Lists.newArrayList();
            if (n2 >= 0 && Enchantment.getEnchantmentById(n2 & 0xFF) != null) {
               String translatedName = Enchantment.getEnchantmentById(n2 & 0xFF).getTranslatedName((n2 & 0xFF00) >> 8);
               newArrayList.add(EnumChatFormatting.WHITE.toString() + EnumChatFormatting.ITALIC.toString() + I18n.format("container.enchant.clue", translatedName));
            }

            if (!isCreativeMode) {
               if (n2 >= 0) {
                  newArrayList.add("");
               }

               if (this.mc.thePlayer.experienceLevel < n) {
                  newArrayList.add(EnumChatFormatting.RED.toString() + "Level Requirement: " + this.container.enchantLevels[i]);
               } else {
                  String str = "";
                  if (n3 == 1) {
                     str = I18n.format("container.enchant.lapis.one");
                  } else {
                     str = I18n.format("container.enchant.lapis.many", n3);
                  }

                  if (lapisAmount >= n3) {
                     newArrayList.add(EnumChatFormatting.GRAY.toString() + "" + str);
                  } else {
                     newArrayList.add(EnumChatFormatting.RED.toString() + "" + str);
                  }

                  if (n3 == 1) {
                     str = I18n.format("container.enchant.level.one");
                  } else {
                     str = I18n.format("container.enchant.level.many", n3);
                  }

                  newArrayList.add(EnumChatFormatting.GRAY.toString() + "" + str);
               }
            }

            this.drawHoveringText(newArrayList, mouseX, mouseY);
            break;
         }
      }
   }

   public void doTick() {
      ItemStack stack = this.inventorySlots.getSlot(0).getStack();
      if (!ItemStack.areItemStacksEqual(stack, this.stack)) {
         this.stack = stack;

         do {
            this.approximatePageAngle = this.approximatePageAngle + (this.random.nextInt(4) - this.random.nextInt(4));
         } while (!(this.nextPageAngle > this.approximatePageAngle + 1.0F) && !(this.nextPageAngle < this.approximatePageAngle - 1.0F));
      }

      this.ticks++;
      this.pageAngle = this.nextPageAngle;
      this.pageTurningSpeed = this.nextPageTurningSpeed;
      boolean flag = false;

      for (int i = 0; i < 3; i++) {
         if (this.container.enchantLevels[i] != 0) {
            flag = true;
         }
      }

      if (flag) {
         this.nextPageTurningSpeed += 0.2F;
      } else {
         this.nextPageTurningSpeed -= 0.2F;
      }

      this.nextPageTurningSpeed = MathHelper.clamp_float(this.nextPageTurningSpeed, 0.0F, 1.0F);
      float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4F;
      float f2 = 0.2F;
      f = MathHelper.clamp_float(f, -f2, f2);
      this.pageRotationSpeed = this.pageRotationSpeed + (f - this.pageRotationSpeed) * 0.9F;
      this.nextPageAngle = this.nextPageAngle + this.pageRotationSpeed;
   }
}
