package com.cheatbreaker.client.radio;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.component.RadioPanel;
import com.cheatbreaker.client.ui.component.UIElement;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class StationEntry extends UIElement {
   public ResourceLocation star21Png = new ResourceLocation("client/icons/star-21.png");
   public ResourceLocation starFilled21Png = new ResourceLocation("client/icons/star-filled-21.png");
   public RadioPanel radioPanel;
   public Station station;

   public boolean check(float f, float f2) {
      return this.containsPoint(f, f2) && f < this.x + 22.0F;
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      if (this.check(f, f2) && flag) {
         Gui.drawRect(this.x, this.y, this.x + 22.0F, this.y + this.height, -13158601);
      } else if (this.containsPoint(f, f2) && flag) {
         Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -13158601);
      }

      boolean isFavorite = this.station.isFavorite();
      if (isFavorite) {
         GlStateManager.color(0.95F, 0.72F, 0.15F, 1.0F);
      } else {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }

      boolean flag2 = CheatBreaker.getInstance().getRadioManager().getStation() == this.station;
      RenderUtils.drawSquareTextureViaRenderEngine(isFavorite ? this.starFilled21Png : this.star21Png, 5.0F, this.x + 6.0F, this.y + 5.0F);
      CheatBreaker.getInstance().getFontManager().playRegular14.drawString(this.station.getName(), this.x + 24.0F, this.y + 1.5F, flag2 ? -13369549 : -1);
      CheatBreaker.getInstance().getFontManager().playRegular14.drawString(this.station.getGenre(), this.x + 24.0F, this.y + 9.5F, -1342177281);
   }

   @Override
   public boolean mouseClicked(float f, float f2, int n, boolean flag) {
      if (!flag) {
         return false;
      }

      if (this.check(f, f2) && flag) {
         this.station.setFavorite(!this.station.isFavorite());
         this.radioPanel.setBounds();
         return true;
      }

      if (this.containsPoint(f, f2) && flag) {
         if (RadioApi.isPlaying()) {
            RadioApi.stop();
         }

         this.station.playing = true;
         CheatBreaker.getInstance().getRadioManager().getRadioPlayerThread().handleStation(this.station);
         CheatBreaker.getInstance().getRadioManager().handleStation(this.station);
      }

      return false;
   }

   public StationEntry(RadioPanel radioPanel, Station station) {
      this.radioPanel = radioPanel;
      this.station = station;
   }

   public Station getStation() {
      return this.station;
   }
}
