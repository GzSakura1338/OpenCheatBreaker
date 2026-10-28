package com.cheatbreaker.client.audio;

import com.cheatbreaker.client.util.Logging;
import java.lang.reflect.Field;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import paulscode.sound.libraries.LibraryJavaSound;

public final class JavaSoundMixer {
    private static final Line.Info CLIP = new Line.Info(Clip.class);
    private static final Line.Info SOURCE = new Line.Info(SourceDataLine.class);

    private JavaSoundMixer() {
    }

    public static void select() {
        try {
            Mixer mixer = findMixer();
            if (mixer != null) {
                Field field = LibraryJavaSound.class.getDeclaredField("myMixer");
                field.setAccessible(true);
                field.set(null, mixer);
            }
        } catch (ReflectiveOperationException | RuntimeException e) {
            Logging.warnTagged("Sound", "Could not select a Java Sound mixer: %s", e.toString());
        }
    }

    public static Mixer findMixer() {
        Mixer defaultMixer = AudioSystem.getMixer(null);
        if (defaultMixer != null && usable(defaultMixer)) {
            return defaultMixer;
        }

        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            Mixer mixer = AudioSystem.getMixer(info);
            if (usable(mixer)) {
                return mixer;
            }
        }

        return null;
    }

    private static boolean usable(Mixer mixer) {
        return mixer.isLineSupported(CLIP) && mixer.isLineSupported(SOURCE);
    }
}
