package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.render.RenderUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ListComponent extends SettingComponent {
   public List<Potion> potionList = new ArrayList<>();

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      try {
         int n4 = 0;
         int n5 = 0;

         for (Integer integer : (Integer[])this.setting.getAcceptedValues()) {
            if (integer != null) {
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
                  int n8 = integer;
                  if (((List)this.setting.getValue()).contains(n8)) {
                     ((List)this.setting.getValue()).removeIf(obj -> (Integer)obj == n8);
                  } else {
                     this.setting.getListValue().add(n8);
                  }

                  this.setting.changeValue(this.setting.getListValue());
                  Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
               }

               n4++;
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Override
   public void render(int n, int n2, float f) {
      CheatBreaker.getInstance()
         .getFontManager()
         .ubuntuM16
         .drawString(this.setting.getName().toUpperCase(), this.x + 10, this.y + 2, this.theme.getTextColor2Value());
      Minecraft minecraft = Minecraft.getMinecraft();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(32826);
      RenderHelper.enableStandardItemLighting();
      RenderHelper.enableGUIStandardItemLighting();
      int n3 = 0;
      int n4 = 0;

      for (Potion potion : this.potionList) {
         if (potion != null) {
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
            if (this.setting.getListValue().contains(potion.id)) {
               Gui.drawRect(n5 - 2, n6 - 2, n5 + 18, n6 + 18, 2130771712);
            } else if (flag) {
               Gui.drawRect(n5 - 2, n6 - 2, n5 + 18, n6 + 18, 1325400319);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
            int statusIconIndex = potion.getStatusIconIndex();
            RenderUtils.drawTexturedRect256(n5 - 1, n6 - 1.0F, statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
            n3++;
         }
      }

      RenderHelper.disableStandardItemLighting();
      GL11.glDisable(32826);
      GlStateManager.disableBlend();
   }

   public void run() {
      this.potionList.add(Potion.moveSpeed);
      this.potionList.add(Potion.moveSlowdown);
      this.potionList.add(Potion.digSpeed);
      this.potionList.add(Potion.digSlowdown);
      this.potionList.add(Potion.damageBoost);
      this.potionList.add(Potion.jump);
      this.potionList.add(Potion.confusion);
      this.potionList.add(Potion.regeneration);
      this.potionList.add(Potion.resistance);
      this.potionList.add(Potion.fireResistance);
      this.potionList.add(Potion.waterBreathing);
      this.potionList.add(Potion.invisibility);
      this.potionList.add(Potion.blindness);
      this.potionList.add(Potion.nightVision);
      this.potionList.add(Potion.hunger);
      this.potionList.add(Potion.weakness);
      this.potionList.add(Potion.poison);
      this.potionList.add(Potion.wither);
      this.potionList.add(Potion.absorption);
   }

   public ListComponent(Setting setting, float f) {
      super(f);
      this.height = 50;
      this.setting = setting;
      this.run();
   }
}
