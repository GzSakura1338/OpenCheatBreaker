package com.cheatbreaker.client.input;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SimpleEvent28;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.java.games.input.AbstractController;
import net.java.games.input.Mouse;
import net.java.games.input.WinTabDevice;
import net.java.games.input.Component.Identifier.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MouseHelper;

public class ControllerMouseHelper extends MouseHelper {
   public AtomicDouble atomicDouble = new AtomicDouble();
   public AtomicDouble atomicDouble2 = new AtomicDouble();
   public AtomicBoolean atomicBoolean = new AtomicBoolean(false);
   public Set<AbstractController> abstractControllerSet = ConcurrentHashMap.newKeySet();
   public ControllerInputEnvironment controllerInputEnvironment;
   public int intValue = 0;
   public int intValue2 = 0;
   public boolean flag = false;
   public ScheduledExecutorService scheduledExecutorService;

   public void onSimpleEvent28(SimpleEvent28 simpleEvent28) {
      Minecraft.getMinecraft().mouseHelper = this;
      this.run3();
   }

   public void run() {
      if (this.scheduledExecutorService != null && !this.scheduledExecutorService.isShutdown()) {
         this.scheduledExecutorService.shutdown();

         try {
            if (!this.scheduledExecutorService.awaitTermination(60L, TimeUnit.SECONDS)) {
               this.scheduledExecutorService.shutdownNow();
            }
         } catch (InterruptedException e) {
            this.scheduledExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
         }
      }
   }

   public void run2() {
      if (Minecraft.getMinecraft().currentScreen == null && this.abstractControllerSet != null) {
         if (this.abstractControllerSet.isEmpty()) {
            this.run6();
            if (this.abstractControllerSet.isEmpty()) {
               return;
            }
         }

         this.atomicBoolean.set(false);
         String stringValue = CheatBreaker.getInstance().getKeybindSettings().usedMouse.getStringValue();
         this.abstractControllerSet.stream().filter(abstractController -> "ALL".equals(stringValue) || abstractController.getName().equals(stringValue)).forEach(abstractController -> {
            abstractController.poll();
            if (abstractController instanceof Mouse) {
               Mouse mouse = (Mouse)abstractController;
               this.atomicDouble.addAndGet(mouse.getX().getPollData());
               this.atomicDouble2.addAndGet(mouse.getY().getPollData());
            } else {
               WinTabDevice winTabDevice = (WinTabDevice)abstractController;
               this.atomicDouble.addAndGet(winTabDevice.getComponent(Axis.X).getPollData());
               this.atomicDouble2.addAndGet(winTabDevice.getComponent(Axis.Y).getPollData());
            }

            this.atomicBoolean.set(this.atomicBoolean.get() || this.atomicDouble.get() != 0.0 || this.atomicDouble2.get() != 0.0);
         });
         this.run4();
      }
   }

   public void run3() {
      if (CheatBreaker.getInstance().getKeybindSettings().rawMouseInput.getBooleanValue()) {
         this.run();
         this.scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
         int n = (int)(1000.0F / CheatBreaker.getInstance().getKeybindSettings().rawPollingRate.getIntValue() * 1000.0F);
         this.scheduledExecutorService.scheduleAtFixedRate(this::run2, 0L, n, TimeUnit.MICROSECONDS);
      }
   }

   public void run4() {
      if (!this.flag) {
         int dX = org.lwjgl.input.Mouse.getDX();
         int dY = org.lwjgl.input.Mouse.getDY();
         if ((Math.abs(dX) > 5 || Math.abs(dY) > 5) && !this.atomicBoolean.get()) {
            this.intValue++;
            this.run5();
         } else if (this.atomicBoolean.get()) {
            this.intValue = 0;
            this.intValue2 = 0;
         }
      }
   }

   @Override
   public void mouseXYChange() {
      if (CheatBreaker.getInstance().getKeybindSettings().rawMouseInput.getBooleanValue()) {
         this.deltaX = (int)this.atomicDouble.getAndSet(0.0);
         this.deltaY = (int)(-this.atomicDouble2.getAndSet(0.0));
      } else {
         super.mouseXYChange();
      }
   }

   public void run5() {
      if (this.intValue > 5) {
         this.intValue2++;
         if (this.intValue2 > 4) {
            CheatBreaker.getInstance().getKeybindSettings().inputMode.changeValue("Direct");
         }

         this.run6();
      }
   }

   public ControllerMouseHelper() {
      CheatBreaker.getInstance().getEventBus().checkClass2(SimpleEvent28.class, this::onSimpleEvent28);
   }

   public void run6() {
      if (!this.flag) {
         this.flag = true;
         this.controllerInputEnvironment = new ControllerInputEnvironment();
         this.controllerInputEnvironment.run();
         this.abstractControllerSet = Arrays.stream(this.controllerInputEnvironment.getControllers())
            .filter(controller -> controller instanceof Mouse || controller instanceof WinTabDevice)
            .map(AbstractController.class::cast)
            .collect(Collectors.toSet());
         CheatBreaker.getInstance()
            .getKeybindSettings()
            .usedMouse
            .setStringValues(Stream.concat(Stream.of("ALL"), this.abstractControllerSet.stream().map(AbstractController::getName)).toArray(String[]::new));
         this.flag = false;
      }
   }
}
