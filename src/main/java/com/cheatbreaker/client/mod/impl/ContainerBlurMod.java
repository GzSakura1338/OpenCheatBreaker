package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.mod.AbstractMod;
import com.google.common.base.Throwables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;

public class ContainerBlurMod extends AbstractMod {
   public void run() {
      this.mc.entityRenderer.run2();
      ShaderGroup shaderGroup = Minecraft.getMinecraft().entityRenderer.getShaderGroup();

      try {
         if (this.mc.entityRenderer.isShaderActive() && this.mc.thePlayer != null) {
            for (Shader shader : shaderGroup.getListShaders()) {
               ;
            }
         }
      } catch (IllegalArgumentException e) {
         Throwables.propagate(e);
      }
   }

   public ContainerBlurMod() {
      super("Container Blur");
      this.setEnabledByDefault(false);
      this.setLabel("Container Blur", 1.1F);
      this.setDescription("Adds a blur to containers.");
      this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent);
   }

   public void onClientTickEvent(ClientTickEvent clientTickEvent) {
      if (this.mc.currentScreen == null && !this.mc.entityRenderer.isShaderActive()) {
         this.run();
      }
   }
}
