package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistrySimple implements IRegistry {
    public static Logger logger = LogManager.getLogger();
    public Map registryObjects = this.createUnderlyingMap();

    public Map createUnderlyingMap() {
        return Maps.newHashMap();
    }

    @Override
    public Object getObject(Object obj) {
        return this.registryObjects.get(obj);
    }

    @Override
    public void putObject(Object obj, Object obj2) {
        if (this.registryObjects.containsKey(obj)) {
            logger.debug("Adding duplicate key '" + obj + "' to registry");
        }

        this.registryObjects.put(obj, obj2);
    }

    public Set getKeys() {
        return Collections.unmodifiableSet(this.registryObjects.keySet());
    }

    public boolean containsKey(Object obj) {
        return this.registryObjects.containsKey(obj);
    }
}
