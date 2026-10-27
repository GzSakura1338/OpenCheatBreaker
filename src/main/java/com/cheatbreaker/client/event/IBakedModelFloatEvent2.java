package com.cheatbreaker.client.event;

import net.minecraft.client.resources.model.IBakedModel;

public class IBakedModelFloatEvent2 extends CancellableEvent {
   public float floatValue;
   public IBakedModel bakedModel;

   public IBakedModelFloatEvent2(float f, IBakedModel bakedModel) {
      this.floatValue = f;
      this.bakedModel = bakedModel;
   }

   public void setFloatValue(float f) {
      this.floatValue = f;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public IBakedModel getBakedModel() {
      return this.bakedModel;
   }
}
