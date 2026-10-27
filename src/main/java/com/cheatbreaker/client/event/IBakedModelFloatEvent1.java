package com.cheatbreaker.client.event;

import net.minecraft.client.resources.model.IBakedModel;

public class IBakedModelFloatEvent1 extends CancellableEvent {
   public float floatValue;
   public IBakedModel bakedModel;

   public void setFloatValue(float f) {
      this.floatValue = f;
   }

   public IBakedModel getBakedModel() {
      return this.bakedModel;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public IBakedModelFloatEvent1(float f, IBakedModel bakedModel) {
      this.floatValue = f;
      this.bakedModel = bakedModel;
   }
}
