package com.cheatbreaker.client.event;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;

public class AbstractClientPlayerCustomModelPlayerFloatEvent extends Event {
   public EventPhase eventPhase;
   public AbstractClientPlayer abstractClientPlayer;
   public ModelPlayer modelPlayer;
   public float scale;

   public AbstractClientPlayer getAbstractClientPlayer() {
      return this.abstractClientPlayer;
   }

   public float getScale() {
      return this.scale;
   }

   public EventPhase getEventPhase() {
      return this.eventPhase;
   }

   public AbstractClientPlayerCustomModelPlayerFloatEvent(EventPhase eventPhase, AbstractClientPlayer abstractClientPlayer, ModelPlayer modelPlayer, float f) {
      this.eventPhase = eventPhase;
      this.abstractClientPlayer = abstractClientPlayer;
      this.modelPlayer = modelPlayer;
      this.scale = f;
   }

   public ModelPlayer getModelPlayer() {
      return this.modelPlayer;
   }
}
