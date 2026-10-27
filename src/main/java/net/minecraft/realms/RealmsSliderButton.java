package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class RealmsSliderButton extends RealmsButton {
   public float value = 1.0F;
   public boolean sliding;
   public float minValue;
   public float maxValue;
   public int steps;

   public RealmsSliderButton(int n, int n2, int n3, int n4, int n5, int n6) {
      this(n, n2, n3, n4, n6, 0, 1.0F, n5);
   }

   public RealmsSliderButton(int n, int n2, int n3, int n4, int n5, int n6, float f, float f2) {
      super(n, n2, n3, n4, 20, "");
      this.minValue = f;
      this.maxValue = f2;
      this.value = this.toPct(n6);
      this.getProxy().displayString = this.getMessage();
   }

   public String getMessage() {
      return "";
   }

   public float toPct(float f) {
      return MathHelper.clamp_float((this.clamp(f) - this.minValue) / (this.maxValue - this.minValue), 0.0F, 1.0F);
   }

   public float toValue(float f) {
      return this.clamp(this.minValue + (this.maxValue - this.minValue) * MathHelper.clamp_float(f, 0.0F, 1.0F));
   }

   public float clamp(float f) {
      f = this.clampSteps(f);
      return MathHelper.clamp_float(f, this.minValue, this.maxValue);
   }

   public float clampSteps(float f) {
      if (this.steps > 0) {
         f = this.steps * Math.round(f / this.steps);
      }

      return f;
   }

   @Override
   public int getYImage(boolean flag) {
      return 0;
   }

   @Override
   public void renderBg(int n, int n2) {
      if (this.getProxy().visible) {
         if (this.sliding) {
            this.value = (float)(n - (this.getProxy().xPosition + 4)) / (this.getProxy().getButtonWidth() - 8);
            this.value = MathHelper.clamp_float(this.value, 0.0F, 1.0F);
            float toValue = this.toValue(this.value);
            this.clicked(toValue);
            this.value = this.toPct(toValue);
            this.getProxy().displayString = this.getMessage();
         }

         Minecraft.getMinecraft().getTextureManager().bindTexture(WIDGETS_LOCATION);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.blit(this.getProxy().xPosition + (int)(this.value * (this.getProxy().getButtonWidth() - 8)), this.getProxy().yPosition, 0, 66, 4, 20);
         this.blit(this.getProxy().xPosition + (int)(this.value * (this.getProxy().getButtonWidth() - 8)) + 4, this.getProxy().yPosition, 196, 66, 4, 20);
      }
   }

   @Override
   public void clicked(int n, int n2) {
      this.value = (float)(n - (this.getProxy().xPosition + 4)) / (this.getProxy().getButtonWidth() - 8);
      this.value = MathHelper.clamp_float(this.value, 0.0F, 1.0F);
      this.clicked(this.toValue(this.value));
      this.getProxy().displayString = this.getMessage();
      this.sliding = true;
   }

   public void clicked(float f) {
   }

   @Override
   public void released(int n, int n2) {
      this.sliding = false;
   }
}
