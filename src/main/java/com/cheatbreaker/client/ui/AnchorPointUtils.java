package com.cheatbreaker.client.ui;

import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.HudAlignment;
import net.minecraft.client.gui.ScaledResolution;

public class AnchorPointUtils {
   public static float[] getAnchoredPosition(AnchorPoint anchorPoint, ScaledResolution scaledResolution, float f, float f2, float f3) {
      float f4 = 0.0F;
      float f5 = 0.0F;
      f *= f3;
      f2 *= f3;
      switch (anchorPoint) {
         case LEFT_TOP:
            f4 = 2.0F;
            f5 = 2.0F;
            break;
         case RIGHT_TOP:
            f4 = scaledResolution.getScaledWidth() - f - 2.0F;
            f5 = 2.0F;
            break;
         case MIDDLE_TOP:
            f4 = scaledResolution.getScaledWidth() / 2 - f / 2.0F;
            f5 = 2.0F;
            break;
         case LEFT_MIDDLE:
            f4 = 2.0F;
            f5 = scaledResolution.getScaledHeight() / 2 - f2 / 2.0F;
            break;
         case RIGHT_MIDDLE:
            f4 = scaledResolution.getScaledWidth() - f;
            f5 = scaledResolution.getScaledHeight() / 2 - f2 / 2.0F;
            break;
         case MIDDLE_MIDDLE:
            f4 = scaledResolution.getScaledWidth() / 2 - f / 2.0F;
            f5 = scaledResolution.getScaledHeight() / 2 - f2 / 2.0F;
            break;
         case LEFT_BOTTOM:
            f5 = scaledResolution.getScaledHeight() - f2 - 2.0F;
            f4 = 2.0F;
            break;
         case MIDDLE_BOTTOM_RIGHT:
            f4 = scaledResolution.getScaledWidth() / 2;
            f5 = scaledResolution.getScaledHeight() - f2 - 2.0F;
            break;
         case MIDDLE_BOTTOM_LEFT:
            f4 = scaledResolution.getScaledWidth() / 2 - f;
            f5 = scaledResolution.getScaledHeight() - f2 - 2.0F;
            break;
         case RIGHT_BOTTOM:
            f4 = scaledResolution.getScaledWidth() - f;
            f5 = scaledResolution.getScaledHeight() - f2;
      }

      return new float[]{f4, f5};
   }

   public static float[] getCellOrigin(float f, float f2, ScaledResolution scaledResolution) {
      float scaledWidth = scaledResolution.getScaledWidth();
      float scaledHeight = scaledResolution.getScaledHeight();
      AnchorPoint anchorPointAt = getAnchorPointAt(f, f2, scaledResolution);
      float f3 = 0.0F;
      float f4 = 0.0F;
      switch (anchorPointAt) {
         case LEFT_TOP:
            f3 = 0.0F;
            f4 = 0.0F;
            break;
         case RIGHT_TOP:
            f3 = scaledWidth / 3.0F * 2.0F;
            f4 = 0.0F;
            break;
         case MIDDLE_TOP:
            f3 = scaledWidth / 3.0F;
            f4 = 0.0F;
            break;
         case LEFT_MIDDLE:
            f3 = 0.0F;
            f4 = scaledHeight / 3.0F;
            break;
         case RIGHT_MIDDLE:
            f3 = scaledWidth / 3.0F * 2.0F;
            f4 = scaledHeight / 3.0F;
            break;
         case MIDDLE_MIDDLE:
            f3 = scaledWidth / 3.0F;
            f4 = scaledHeight / 3.0F;
            break;
         case LEFT_BOTTOM:
            f3 = 0.0F;
            f4 = scaledHeight / 3.0F * 2.0F;
            break;
         case MIDDLE_BOTTOM_RIGHT:
            f3 = scaledWidth / 3.0F + scaledWidth / 6.0F;
            f4 = scaledHeight / 3.0F * 2.0F;
            break;
         case MIDDLE_BOTTOM_LEFT:
            f3 = scaledWidth / 3.0F;
            f4 = scaledHeight / 3.0F * 2.0F;
            break;
         case RIGHT_BOTTOM:
            f3 = scaledWidth / 3.0F * 2.0F;
            f4 = scaledHeight / 3.0F * 2.0F;
      }

      return new float[]{f3, f4};
   }

   public static float[] getModCenterInCell(AbstractMod abstractMod, float f, float f2, ScaledResolution scaledResolution) {
      float scaledWidth = scaledResolution.getScaledWidth();
      float scaledHeight = scaledResolution.getScaledHeight();
      AnchorPoint anchorPointAt = getAnchorPointAt(f, f2, scaledResolution);
      float f3 = abstractMod.hudWidth * abstractMod.getHudScale();
      float f4 = abstractMod.hudHeight * abstractMod.getHudScale();
      float f5 = 0.0F;
      float f6 = 0.0F;
      switch (anchorPointAt) {
         case LEFT_TOP:
            f5 = f3 / 2.0F;
            f6 = f4 / 2.0F;
            break;
         case RIGHT_TOP:
            f5 = scaledWidth / 3.0F - f3 / 2.0F;
            f6 = f4 / 2.0F;
            break;
         case MIDDLE_TOP:
            f5 = scaledWidth / 6.0F;
            f6 = f4 / 2.0F;
            break;
         case LEFT_MIDDLE:
            f5 = f3 / 2.0F;
            f6 = scaledHeight / 6.0F;
            break;
         case RIGHT_MIDDLE:
            f5 = scaledWidth / 3.0F - f3 / 2.0F;
            f6 = scaledHeight / 6.0F;
            break;
         case MIDDLE_MIDDLE:
            f5 = scaledWidth / 6.0F;
            f6 = scaledHeight / 6.0F;
            break;
         case LEFT_BOTTOM:
            f5 = f3 / 2.0F;
            f6 = scaledHeight / 3.0F - f4 / 2.0F;
            break;
         case MIDDLE_BOTTOM_RIGHT:
            f5 = f3 / 2.0F;
            f6 = scaledHeight / 3.0F - f4 / 2.0F;
            break;
         case MIDDLE_BOTTOM_LEFT:
            f5 = scaledWidth / 6.0F - f3 / 2.0F;
            f6 = scaledHeight / 3.0F - f4 / 2.0F;
            break;
         case RIGHT_BOTTOM:
            f5 = scaledWidth / 3.0F - f3 / 2.0F;
            f6 = scaledHeight / 3.0F - f4 / 2.0F;
      }

      return new float[]{f5, f6};
   }

   public static HudAlignment getVerticalAlignment(AnchorPoint anchorPoint) {
      switch (anchorPoint) {
         case LEFT_TOP:
         case RIGHT_TOP:
         case MIDDLE_TOP:
            return HudAlignment.TOP;
         case LEFT_MIDDLE:
         case RIGHT_MIDDLE:
         case MIDDLE_MIDDLE:
            return HudAlignment.CENTER;
         case LEFT_BOTTOM:
         case MIDDLE_BOTTOM_RIGHT:
         case MIDDLE_BOTTOM_LEFT:
         case RIGHT_BOTTOM:
            return HudAlignment.BOTTOM;
         default:
            return null;
      }
   }

   public static HudAlignment getHorizontalAlignment(AnchorPoint anchorPoint) {
      switch (anchorPoint) {
         case LEFT_TOP:
         case LEFT_MIDDLE:
         case LEFT_BOTTOM:
         case MIDDLE_BOTTOM_RIGHT:
            return HudAlignment.LEFT;
         case RIGHT_TOP:
         case RIGHT_MIDDLE:
         case MIDDLE_BOTTOM_LEFT:
         case RIGHT_BOTTOM:
            return HudAlignment.RIGHT;
         case MIDDLE_TOP:
         case MIDDLE_MIDDLE:
            return HudAlignment.CENTER;
         default:
            return null;
      }
   }

   public static AnchorPoint getAnchorPointAt(float f, float f2, ScaledResolution scaledResolution) {
      int scaledWidth = scaledResolution.getScaledWidth();
      int scaledHeight = scaledResolution.getScaledHeight();
      if (f < scaledWidth / 3 && f2 < scaledHeight / 3) {
         return AnchorPoint.LEFT_TOP;
      } else if (f > scaledWidth / 3 * 2 && f2 < scaledHeight / 3) {
         return AnchorPoint.RIGHT_TOP;
      } else if (f2 < scaledHeight / 3) {
         return AnchorPoint.MIDDLE_TOP;
      } else if (f < scaledWidth / 3 && f2 < scaledHeight / 3 * 2) {
         return AnchorPoint.LEFT_MIDDLE;
      } else if (f > scaledWidth / 3 * 2 && f2 < scaledHeight / 3 * 2) {
         return AnchorPoint.RIGHT_MIDDLE;
      } else if (f2 < scaledHeight / 3 * 2) {
         return AnchorPoint.MIDDLE_MIDDLE;
      } else if (f < scaledWidth / 3) {
         return AnchorPoint.LEFT_BOTTOM;
      } else if (f < scaledWidth / 3 * 2) {
         return f > scaledWidth / 3 + scaledWidth / 6 ? AnchorPoint.MIDDLE_BOTTOM_RIGHT : AnchorPoint.MIDDLE_BOTTOM_LEFT;
      } else {
         return AnchorPoint.RIGHT_BOTTOM;
      }
   }

   public static float[] getAnchorOffset(AnchorPoint anchorPoint) {
      float f = 0.0F;
      float f2 = 0.0F;
      switch (anchorPoint) {
         case RIGHT_MIDDLE:
            f = -2.0F;
            break;
         case MIDDLE_MIDDLE:
            f2 = -50.0F;
            f = 0.0F;
            break;
         case LEFT_BOTTOM:
            f = 2.0F;
            f2 = -34.0F;
      }

      return new float[]{f, f2};
   }
}
