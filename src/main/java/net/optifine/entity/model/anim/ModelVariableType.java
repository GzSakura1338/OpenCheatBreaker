package net.optifine.entity.model.anim;

import net.minecraft.client.model.ModelRenderer;
import net.optifine.Config;

public enum ModelVariableType {
   POS_X("tx"),
   POS_Y("ty"),
   POS_Z("tz"),
   ANGLE_X("rx"),
   ANGLE_Y("ry"),
   ANGLE_Z("rz"),
   OFFSET_X("ox"),
   OFFSET_Y("oy"),
   OFFSET_Z("oz"),
   SCALE_X("sx"),
   SCALE_Y("sy"),
   SCALE_Z("sz");

   public String name;
   public static ModelVariableType[] VALUES = values();

   ModelVariableType(String str) {
      this.name = str;
   }

   public String getName() {
      return this.name;
   }

   public float getFloat(ModelRenderer modelRenderer) {
      switch (this) {
         case POS_X:
            return modelRenderer.rotationPointX;
         case POS_Y:
            return modelRenderer.rotationPointY;
         case POS_Z:
            return modelRenderer.rotationPointZ;
         case ANGLE_X:
            return modelRenderer.rotateAngleX;
         case ANGLE_Y:
            return modelRenderer.rotateAngleY;
         case ANGLE_Z:
            return modelRenderer.rotateAngleZ;
         case OFFSET_X:
            return modelRenderer.offsetX;
         case OFFSET_Y:
            return modelRenderer.offsetY;
         case OFFSET_Z:
            return modelRenderer.offsetZ;
         case SCALE_X:
            return modelRenderer.scaleX;
         case SCALE_Y:
            return modelRenderer.scaleY;
         case SCALE_Z:
            return modelRenderer.scaleZ;
         default:
            Config.warn("GetFloat not supported for: " + this);
            return 0.0F;
      }
   }

   public void setFloat(ModelRenderer modelRenderer, float f) {
      switch (this) {
         case POS_X:
            modelRenderer.rotationPointX = f;
            return;
         case POS_Y:
            modelRenderer.rotationPointY = f;
            return;
         case POS_Z:
            modelRenderer.rotationPointZ = f;
            return;
         case ANGLE_X:
            modelRenderer.rotateAngleX = f;
            return;
         case ANGLE_Y:
            modelRenderer.rotateAngleY = f;
            return;
         case ANGLE_Z:
            modelRenderer.rotateAngleZ = f;
            return;
         case OFFSET_X:
            modelRenderer.offsetX = f;
            return;
         case OFFSET_Y:
            modelRenderer.offsetY = f;
            return;
         case OFFSET_Z:
            modelRenderer.offsetZ = f;
            return;
         case SCALE_X:
            modelRenderer.scaleX = f;
            return;
         case SCALE_Y:
            modelRenderer.scaleY = f;
            return;
         case SCALE_Z:
            modelRenderer.scaleZ = f;
            return;
         default:
            Config.warn("SetFloat not supported for: " + this);
      }
   }

   public static ModelVariableType parse(String str) {
      for (int i = 0; i < VALUES.length; i++) {
         ModelVariableType modelVariableType = VALUES[i];
         if (modelVariableType.getName().equals(str)) {
            return modelVariableType;
         }
      }

      return null;
   }
}
