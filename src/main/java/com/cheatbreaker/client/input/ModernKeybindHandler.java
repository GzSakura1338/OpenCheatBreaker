package com.cheatbreaker.client.input;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.IntEvent4;
import com.cheatbreaker.client.mod.KeybindSettings;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class ModernKeybindHandler {
    public boolean screenWasOpen;
    public Minecraft mc;

    public ModernKeybindHandler() {
        CheatBreaker.getInstance().getEventBus().checkClass2(ClientTickEvent.class, this::onClientTickEvent);
        CheatBreaker.getInstance().getEventBus().checkClass2(IntEvent4.class, this::onIntEvent4);
        this.mc = Minecraft.getMinecraft();
    }

    public void onClientTickEvent(ClientTickEvent clientTickEvent) {
        boolean flag = this.mc.currentScreen != null;
        KeybindSettings keybindSettings = CheatBreaker.getInstance().getKeybindSettings();
        if (!flag && this.screenWasOpen && keybindSettings.modernKeybindHandling.getBooleanValue()) {
            Class<GameSettings> class2 = GameSettings.class;
            Field[] declaredFields = class2.getDeclaredFields();

            for (Field field : declaredFields) {
                if (field.getType().equals(KeyBinding.class)) {
                    field.setAccessible(true);

                    try {
                        KeyBinding keyBinding = (KeyBinding)field.get(this.mc.gameSettings);
                        boolean flag2 = !keybindSettings.excludeSneakKeybind.getBooleanValue() || !keyBinding.getKeyDescription().equalsIgnoreCase("key.sneak");
                        boolean flag3 = !keybindSettings.throwKeybind.getBooleanValue() || !keyBinding.getKeyDescription().equalsIgnoreCase("key.use");
                        if (!keyBinding.getKeyDescription().equalsIgnoreCase("key.inventory")
                            && !keyBinding.getKeyDescription().equalsIgnoreCase("key.chat")
                            && !keyBinding.getKeyDescription().equalsIgnoreCase("key.command")
                            && flag2
                            && flag3
                            && keyBinding.getKeyCode() > 0
                            && Keyboard.isKeyDown(keyBinding.getKeyCode())) {
                            KeyBinding.setKeyBindState(keyBinding.getKeyCode(), true);
                            KeyBinding.onTick(keyBinding.getKeyCode());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        this.screenWasOpen = flag;
    }

    public void onIntEvent4(IntEvent4 intEvent4) {
        if (this.mc.thePlayer != null && Keyboard.isCreated() && Keyboard.getEventKeyState()) {
            int eventKey = Keyboard.getEventKey();
            switch (eventKey) {
                case 144:
                    if (this.mc.gameSettings.keyBindsHotbar[5].getKeyCode() == 7) {
                        this.mc.thePlayer.inventory.currentItem = 5;
                    }
                    break;
                case 145:
                    if (this.mc.gameSettings.keyBindsHotbar[1].getKeyCode() == 3) {
                        this.mc.thePlayer.inventory.currentItem = 1;
                    }
            }
        }
    }
}
