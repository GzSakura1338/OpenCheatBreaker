package com.cheatbreaker.client.audio;

import com.cheatbreaker.client.util.Logging;
import java.io.InputStream;
import javazoom.jl.decoder.JavaLayerHook;
import javazoom.jl.decoder.JavaLayerUtils;

public class ResourceJavaLayerHook implements JavaLayerHook {
    public InputStream getResourceAsStream(String name) {
        InputStream stream = JavaLayerUtils.class.getResourceAsStream(name);
        if (stream == null) {
            String path = "javazoom/jl/decoder/" + name;
            Logging.debug("Retrieving: " + path);
            stream = ResourceJavaLayerHook.class.getClassLoader().getResourceAsStream(path);
        }

        return stream;
    }
}
