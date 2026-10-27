package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.ui.component.UIElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public abstract class UIScreen extends GuiScreen {
   public float scaledWidth;
   public List<UIElement> elements;
   public List<UIElement> unfocusableElements;
   public ScaledResolution scaledResolution;
   public float scaledHeight;
   public int initialElementCount = 0;

   public void setUnfocusableElements(UIElement... uielements) {
      this.unfocusableElements = new ArrayList<>();
      this.unfocusableElements.addAll(Arrays.asList(uielements));
   }

   @Override
   public void setWorldAndResolution(Minecraft mc, int n, int n2) {
      this.mc = mc;
      this.fontRendererObj = mc.fontRendererObj;
      this.width = n;
      this.height = n2;
      this.buttonList.clear();
      this.scaledResolution = new ScaledResolution(this.mc);
      float uiScale = this.getUiScale();
      this.scaledWidth = n / uiScale;
      this.scaledHeight = n2 / uiScale;
      this.initGui();
   }

   @Override
   public void drawScreen(int n, int n2, float f) {
      float uiScale = this.getUiScale();
      GlStateManager.pushMatrix();
      GL11.glScalef(uiScale, uiScale, uiScale);
      this.drawScreenScaled(n / uiScale, n2 / uiScale);
      GlStateManager.popMatrix();
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      float uiScale = this.getUiScale();
      this.mouseClickedScaled(n / uiScale, n2 / uiScale, n3);
   }

   public void dispatchMouseClicked(float f, float f2, int n, UIElement... uielements) {
      List asList = Arrays.asList(uielements);
      UIElement uielement = null;
      boolean flag = false;

      for (UIElement uielement2 : this.elements) {
         if (!asList.contains(uielement2) && uielement2.containsPoint(f, f2)) {
            if (!this.unfocusableElements.contains(uielement2)) {
               uielement = uielement2;
            }

            if (uielement2.mouseClicked(f, f2, n, this.isTopmostAt(uielement2, f, f2, uielements))) {
               flag = true;
               break;
            }
         }
      }

      if (!flag) {
         if (uielement != null) {
            this.elements.add(this.elements.remove(this.elements.indexOf(uielement)));
         }

         for (UIElement uielement2 : this.elements) {
            if (uielement2.mouseClickedFallback(f, f2, n)) {
               break;
            }
         }
      }
   }

   public void removeElements(UIElement... uielements) {
      this.elements.removeAll(Arrays.asList(uielements));
      this.initGui();
   }

   public float getScaledWidth() {
      return this.scaledWidth;
   }

   public void setScaledResolution(ScaledResolution scaledResolution) {
      this.scaledResolution = scaledResolution;
   }

   public abstract void mouseClickedScaled(float f, float f2, int n);

   public void dispatchMouseReleased(float f, float f2, int n) {
      for (UIElement uielement : this.elements) {
         if (uielement.containsPoint(f, f2)) {
            uielement.mouseReleased(f, f2, n, this.isTopmostAt(uielement, f, f2));
         }
      }
   }

   public void renderElements(float f, float f2, UIElement... uielements) {
      List asList = Arrays.asList(uielements);

      for (UIElement uielement : this.elements) {
         if (!asList.contains(uielement)) {
            uielement.renderAndTestHover(f, f2, this.isTopmostAt(uielement, f, f2));
         }
      }
   }

   public void dispatchKeyTyped(char c, int n) {
      for (UIElement uielement : this.elements) {
         uielement.keyTyped(c, n);
      }
   }

   public abstract void drawScreenScaled(float f, float f2);

   public ScaledResolution getScaledResolution() {
      return this.scaledResolution;
   }

   public float getScaledHeight() {
      return this.scaledHeight;
   }

   public boolean isTopmostAt(UIElement uielement, float f, float f2, UIElement... uielements) {
      List asList = Arrays.asList(uielements);
      boolean flag = true;

      UIElement uielement2;
      for (int i = this.elements.size() - 1; i >= 0 && (uielement2 = this.elements.get(i)) != uielement; i--) {
         if (!asList.contains(uielement2) && uielement2.containsPoint(f, f2)) {
            flag = false;
            break;
         }
      }

      return flag;
   }

   public List<UIElement> getElements() {
      return this.elements;
   }

   public float getUiScale() {
      float f;
      switch (this.scaledResolution.getScaleFactor()) {
         case 1:
            f = 0.5F;
            break;
         case 2:
         default:
            f = 1.0F;
            break;
         case 3:
            f = 1.5F;
            break;
         case 4:
            f = 2.0F;
            break;
         case 5:
            f = 2.5F;
            break;
         case 6:
            f = 3.0F;
      }

      return 1.0F / f;
   }

   public abstract void mouseReleasedScaled(float f, float f2, int n);

   public void dispatchMouseInput() {
      this.elements.forEach(UIElement::handleMouseInput);
   }

   public void closeElements() {
      this.elements.forEach(UIElement::onClose);
   }

   public void tickElements() {
      this.elements.forEach(UIElement::tick);
   }

   public void setElements(UIElement... uielements) {
      this.elements = new ArrayList<>();
      this.elements.addAll(Arrays.asList(uielements));
      this.initialElementCount = this.elements.size();
   }

   @Override
   public void mouseReleased(int n, int n2, int n3) {
      float uiScale = this.getUiScale();
      this.mouseReleasedScaled(n / uiScale, n2 / uiScale, n3);
   }

   public void addElements(UIElement... uielements) {
      this.elements.addAll(Arrays.asList(uielements));
      this.initGui();
   }
}
