package com.cheatbreaker.client.input;

import com.cheatbreaker.client.CheatBreaker;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.util.plugins.Plugins;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ControllerInputEnvironment extends ControllerEnvironment {
    public static Logger logger = LogManager.getLogger("Input Controller Environment");
    public Collection<String> loadedPluginClasses = new ArrayList<>();
    public ArrayList<Controller> controllerList;

    public void addControllers(Controller[] controllers) {
        this.controllerList.addAll(Arrays.asList(controllers));
    }

    public Controller[] getControllers() {
        if (this.controllerList == null) {
            this.controllerList = new ArrayList<>();
            this.scanPluginPaths();
            ArrayList list = new ArrayList();
            String trim = System.getProperty("os.name", "").trim();
            if (trim.equalsIgnoreCase("Linux")) {
                list.add("net.java.games.input.LinuxEnvironmentPlugin");
            } else if (trim.equalsIgnoreCase("Mac OS X")) {
                list.add("net.java.games.input.OSXEnvironmentPlugin");
            } else if (trim.contains("Windows")) {
                if (CheatBreaker.getInstance().getKeybindSettings().inputMode.getStringValue().equals("Direct & Raw")) {
                    list.add("net.java.games.input.DirectAndRawInputEnvironmentPlugin");
                } else {
                    list.add("net.java.games.input.DirectInputEnvironmentPlugin");
                }
            } else {
                logger.warn("Trying to use default plugin, OS name {} not recognised", trim);
            }

            for (String str : (Iterable<String>)list) {
                try {
                    if (!this.loadedPluginClasses.contains(str)) {
                        logger.info("Loading: {}", str);
                        Class forName = Class.forName(str);
                        ControllerEnvironment newInstance = (ControllerEnvironment)forName.getDeclaredConstructor().newInstance();
                        this.addControllers(newInstance.getControllers());
                        this.loadedPluginClasses.add(newInstance.getClass().getName());
                    }
                } catch (Exception e) {
                    logger.error("Could not load class {}", str, e);
                }
            }
        }

        if (this.controllerList.isEmpty()) {
            logger.warn("Could not find any controllers");
            CheatBreaker.getInstance().getKeybindSettings().rawMouseInput.changeValue(false);
        }

        return this.controllerList.toArray(new Controller[0]);
    }

    public boolean isSupported() {
        return true;
    }

    public void scanPluginDirectory(String str) {
        File file = new File(str);
        if (file.exists()) {
            try {
                Plugins plugins = new Plugins(file);
                Class[] extends2 = plugins.getExtends(ControllerEnvironment.class);

                for (Class clazz : extends2) {
                    logger.info("ControllerEnvironment {} loaded by {}", clazz.getName(), clazz.getClassLoader());
                    ControllerEnvironment newInstance = (ControllerEnvironment)clazz.getDeclaredConstructor().newInstance();
                    if (newInstance.isSupported()) {
                        this.addControllers(newInstance.getControllers());
                        this.loadedPluginClasses.add(newInstance.getClass().getName());
                    } else {
                        logger.warn("{} is not supported", clazz.getName());
                    }
                }
            } catch (Exception e) {
                logger.error("Could not scan controllers", e);
            }
        }
    }

    public void scanPluginPaths() {
        String property = System.getProperty("jinput.controllerPluginPath");
        if (property == null) {
            property = "controller";
        }

        this.scanPluginDirectory(System.getProperty("java.home") + File.separator + "lib" + File.separator + property);
        this.scanPluginDirectory(System.getProperty("user.dir") + File.separator + property);
    }
}
