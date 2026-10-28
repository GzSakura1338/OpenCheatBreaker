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
import net.java.games.input.WinTabDevice;
import net.java.games.input.Component.Identifier.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MouseHelper;
import org.lwjgl.input.Mouse;

public class ControllerMouseHelper extends MouseHelper {
    public AtomicDouble pendingDeltaX = new AtomicDouble();
    public AtomicDouble pendingDeltaY = new AtomicDouble();
    public AtomicBoolean controllerActive = new AtomicBoolean(false);
    public Set<AbstractController> controllers = ConcurrentHashMap.newKeySet();
    public ScheduledExecutorService pollingExecutor;
    public ControllerInputEnvironment controllerInputEnvironment;
    public int mismatchCount = 0;
    public int directSwitchVotes = 0;
    public boolean initialized = false;

    public void onRawInputMismatch() {
        if (this.mismatchCount > 5) {
            this.directSwitchVotes++;
            if (this.directSwitchVotes > 4) {
                CheatBreaker.getInstance().getKeybindSettings().inputMode.changeValue("Direct");
            }

            this.loadMouseControllers();
        }
    }

    public void checkRawInput() {
        if (!this.initialized) {
            int dX = Mouse.getDX();
            int dY = Mouse.getDY();
            if ((Math.abs(dX) > 5 || Math.abs(dY) > 5) && !this.controllerActive.get()) {
                this.mismatchCount++;
                this.onRawInputMismatch();
            } else if (this.controllerActive.get()) {
                this.mismatchCount = 0;
                this.directSwitchVotes = 0;
            }
        }
    }

    public ControllerMouseHelper() {
        CheatBreaker.getInstance().getEventBus().checkClass2(SimpleEvent28.class, this::onSimpleEvent28);
    }

    public void pollControllers() {
        if (Minecraft.getMinecraft().currentScreen == null && this.controllers != null) {
            if (this.controllers.isEmpty()) {
                this.loadMouseControllers();
                if (this.controllers.isEmpty()) {
                    return;
                }
            }

            this.controllerActive.set(false);
            String stringValue = CheatBreaker.getInstance().getKeybindSettings().usedMouse.getStringValue();
            this.controllers.stream().filter(abstractController -> "ALL".equals(stringValue) || abstractController.getName().equals(stringValue)).forEach(abstractController -> {
                abstractController.poll();
                if (abstractController instanceof net.java.games.input.Mouse) {
                    net.java.games.input.Mouse mouse = (net.java.games.input.Mouse)abstractController;
                    this.pendingDeltaX.addAndGet(mouse.getX().getPollData());
                    this.pendingDeltaY.addAndGet(mouse.getY().getPollData());
                } else {
                    WinTabDevice winTabDevice = (WinTabDevice)abstractController;
                    this.pendingDeltaX.addAndGet(winTabDevice.getComponent(Axis.X).getPollData());
                    this.pendingDeltaY.addAndGet(winTabDevice.getComponent(Axis.Y).getPollData());
                }

                this.controllerActive.set(this.controllerActive.get() || this.pendingDeltaX.get() != 0.0 || this.pendingDeltaY.get() != 0.0);
            });
            this.checkRawInput();
        }
    }

    public void startPolling() {
        if (CheatBreaker.getInstance().getKeybindSettings().rawMouseInput.getBooleanValue()) {
            this.stopPolling();
            this.pollingExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
            int n = (int)(1000.0F / CheatBreaker.getInstance().getKeybindSettings().mousePollingRate.getIntValue() * 1000.0F);
            this.pollingExecutor.scheduleAtFixedRate(this::pollControllers, 0L, n, TimeUnit.MICROSECONDS);
        }
    }

    public void stopPolling() {
        if (this.pollingExecutor != null && !this.pollingExecutor.isShutdown()) {
            this.pollingExecutor.shutdown();

            try {
                if (!this.pollingExecutor.awaitTermination(60L, TimeUnit.SECONDS)) {
                    this.pollingExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                this.pollingExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void mouseXYChange() {
        if (CheatBreaker.getInstance().getKeybindSettings().rawMouseInput.getBooleanValue()) {
            this.deltaX = (int)this.pendingDeltaX.getAndSet(0.0);
            this.deltaY = (int)(-this.pendingDeltaY.getAndSet(0.0));
        } else {
            super.mouseXYChange();
        }
    }

    public void onSimpleEvent28(SimpleEvent28 simpleEvent28) {
        Minecraft.getMinecraft().mouseHelper = this;
        this.startPolling();
    }

    public void loadMouseControllers() {
        if (!this.initialized) {
            this.initialized = true;
            this.controllerInputEnvironment = new ControllerInputEnvironment();
            this.controllerInputEnvironment.scanPluginPaths();
            this.controllers = Arrays.stream(this.controllerInputEnvironment.getControllers())
                .filter(controller -> controller instanceof net.java.games.input.Mouse || controller instanceof WinTabDevice)
                .map(AbstractController.class::cast)
                .collect(Collectors.toSet());
            CheatBreaker.getInstance()
                .getKeybindSettings()
                .usedMouse
                .setStringValues(Stream.concat(Stream.of("ALL"), this.controllers.stream().map(AbstractController::getName)).toArray(String[]::new));
            this.initialized = false;
        }
    }
}
