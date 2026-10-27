package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class BlockListComponent extends SettingComponent {
   public RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
   public List<Integer> integerList;
   public String text;

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      if (!this.isHidden(this.setting)) {
         try {
            int n4 = 0;
            int n5 = 0;

            for (Block block : Block.blockRegistry) {
               Item itemFromBlock = Item.getItemFromBlock(block);
               if (itemFromBlock != null) {
                  if (n4 >= 15) {
                     n4 = 0;
                     n5++;
                  }

                  int n6 = this.x + 12 + n4 * 20;
                  int n7 = this.y + 14 + n5 * 20;
                  boolean flag = n > (n6 - 2) * this.scale
                     && n < (n6 + 18) * this.scale
                     && n2 > (n7 - 2 + this.scrollOffsetY) * this.scale
                     && n2 < (n7 + 18 + this.scrollOffsetY) * this.scale;
                  if (flag && n3 == 0) {
                     int idFromItem = Item.getIdFromItem(itemFromBlock);
                     if (this.integerList.contains(idFromItem)) {
                        this.integerList.removeIf(integer -> integer == idFromItem);
                     } else {
                        this.integerList.add(idFromItem);
                     }

                     if (CheatBreaker.getInstance().getModManager().xray.isEnabled()) {
                        Minecraft.getMinecraft().renderGlobal.loadRenderers();
                     }

                     Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                  }

                  n4++;
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public void render(int n, int n2, float f) {
      CheatBreaker.getInstance().getFontManager().ubuntuM16.drawString(this.text.toUpperCase(), this.x + 10, this.y + 2, this.theme.getTextColor2Value());
      Minecraft minecraft = Minecraft.getMinecraft();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(32826);
      RenderHelper.enableStandardItemLighting();
      RenderHelper.enableGUIStandardItemLighting();
      int n3 = 0;
      int n4 = 0;

      for (Block block : Block.blockRegistry) {
         Item itemFromBlock = Item.getItemFromBlock(block);
         if (itemFromBlock != null) {
            if (n3 >= 15) {
               n3 = 0;
               n4++;
            }

            int n5 = this.x + 12 + n3 * 20;
            int n6 = this.y + 14 + n4 * 20;
            boolean flag = n > (n5 - 2) * this.scale
               && n < (n5 + 18) * this.scale
               && n2 > (n6 - 2 + this.scrollOffsetY) * this.scale
               && n2 < (n6 + 18 + this.scrollOffsetY) * this.scale;
            if (this.integerList.contains(Item.getIdFromItem(itemFromBlock))) {
               Gui.drawRect(n5 - 2, n6 - 2, n5 + 18, n6 + 18, 2130771712);
            } else if (flag) {
               Gui.drawRect(n5 - 2, n6 - 2, n5 + 18, n6 + 18, 1325400319);
            }

            this.renderItem.renderItemAndEffectIntoGUI(new ItemStack(itemFromBlock), n5, n6);
            n3++;
         }
      }

      RenderHelper.disableStandardItemLighting();
      GL11.glDisable(32826);
      GlStateManager.disableBlend();
   }

   public BlockListComponent(List<Integer> list, String str, float f) {
      super(f);
      this.height = 220;
      this.integerList = list;
      this.text = str;
   }
}
