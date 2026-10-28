package net.minecraft.realms;

import java.lang.reflect.Constructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsBridge extends RealmsScreen {
    public static Logger LOGGER = LogManager.getLogger();
    public GuiScreen previousScreen;

    public void switchToRealms(GuiScreen guiScreen) {
        this.previousScreen = guiScreen;

        try {
            Class forName = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
            Constructor declaredConstructor = forName.getDeclaredConstructor(RealmsScreen.class);
            declaredConstructor.setAccessible(true);
            Object newInstance = declaredConstructor.newInstance(this);
            Minecraft.getMinecraft().displayGuiScreen(((RealmsScreen)newInstance).getProxy());
        } catch (Exception e) {
            LOGGER.error("Realms module missing", e);
        }
    }

    @Override
    public void init() throws java.io.IOException {
        Minecraft.getMinecraft().displayGuiScreen(this.previousScreen);
    }
}
