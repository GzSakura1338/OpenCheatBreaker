package com.cheatbreaker.client.voice;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.network.ClientNetHandler;
import com.cheatbreaker.client.util.Logging;
import com.cheatbreaker.nethandler.client.CBPacketClientVoice;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import net.minecraft.client.Minecraft;

public final class VoiceEngine {
    private static final VoiceEngine INSTANCE = new VoiceEngine();
    private static final AudioFormat FORMAT = new AudioFormat(VoiceCodec.SAMPLE_RATE, 16, 1, true, false);
    private static final long CAPTURE_IDLE_MS = 2000L;
    private static final int PLAYBACK_IDLE_FRAMES = 150;
    private static final int PREBUFFER_FRAMES = 3;
    private static final int MAX_QUEUED_FRAMES = 25;
    private static final float DEFAULT_MICROPHONE_PERCENT = 70.0F;
    private static final float DEFAULT_SPEAKER_PERCENT = 85.0F;

    private final Object captureLock = new Object();
    private final Object playbackLock = new Object();
    private final Map<UUID, ArrayDeque<short[]>> incoming = new LinkedHashMap<>();
    private final Set<UUID> active = new HashSet<>();
    private Thread captureThread;
    private Thread playbackThread;
    private volatile int captureGeneration;
    private volatile boolean transmitting;
    private volatile String microphone = "Default";
    private volatile float microphoneGain = 1.0F;
    private volatile float speakerGain = 1.0F;

    private VoiceEngine() {
    }

    public static VoiceEngine getInstance() {
        return INSTANCE;
    }

    public void setMicrophone(String name) {
        this.microphone = name;
        synchronized (this.captureLock) {
            this.captureGeneration++;
        }
    }

    public void setMicrophoneVolume(float percent) {
        this.microphoneGain = Math.max(0.0F, Math.min(100.0F, percent)) / DEFAULT_MICROPHONE_PERCENT;
    }

    public void setSpeakerVolume(float percent) {
        this.speakerGain = Math.max(0.0F, Math.min(100.0F, percent)) / DEFAULT_SPEAKER_PERCENT;
    }

    public float getMicrophoneGain() {
        return this.microphoneGain;
    }

    public float getSpeakerGain() {
        return this.speakerGain;
    }

    public boolean isTransmitting() {
        return this.transmitting;
    }

    public void setTransmitting(boolean transmitting) {
        this.transmitting = transmitting;
        if (transmitting) {
            synchronized (this.captureLock) {
                if (this.captureThread == null || !this.captureThread.isAlive()) {
                    this.captureThread = new Thread(this::captureLoop, "Voice Capture");
                    this.captureThread.setDaemon(true);
                    this.captureThread.start();
                }
            }
        }
    }

    public void play(UUID speaker, byte[] data) {
        short[] samples = VoiceCodec.decode(data);
        if (samples == null || speaker.toString().equals(Minecraft.getMinecraft().getSession().getPlayerID())) {
            return;
        }

        synchronized (this.playbackLock) {
            ArrayDeque<short[]> queue = this.incoming.computeIfAbsent(speaker, key -> new ArrayDeque<>());
            for (int i = 0; i < samples.length; i += VoiceCodec.FRAME_SAMPLES) {
                queue.addLast(Arrays.copyOfRange(samples, i, i + VoiceCodec.FRAME_SAMPLES));
            }

            while (queue.size() > MAX_QUEUED_FRAMES) {
                queue.pollFirst();
            }

            this.startPlaybackLocked();
        }
    }

    private void startPlaybackLocked() {
        if (this.playbackThread == null || !this.playbackThread.isAlive()) {
            this.playbackThread = new Thread(this::playbackLoop, "Voice Playback");
            this.playbackThread.setDaemon(true);
            this.playbackThread.start();
        }
    }

    private void captureLoop() {
        TargetDataLine line = null;
        int generation = this.captureGeneration;
        try {
            line = openMicrophone(this.microphone);
            line.start();
            byte[] pcm = new byte[VoiceCodec.FRAME_BYTES];
            long lastTransmit = System.currentTimeMillis();
            while (generation == this.captureGeneration) {
                int read = 0;
                while (read < pcm.length) {
                    int count = line.read(pcm, read, pcm.length - read);
                    if (count <= 0) {
                        break;
                    }

                    read += count;
                }

                if (read < pcm.length) {
                    break;
                }

                long now = System.currentTimeMillis();
                if (this.transmitting) {
                    lastTransmit = now;
                    short[] samples = VoiceCodec.toSamples(pcm, pcm.length);
                    VoiceCodec.applyGain(samples, this.microphoneGain);
                    send(VoiceCodec.encode(samples));
                } else if (now - lastTransmit > CAPTURE_IDLE_MS) {
                    break;
                }
            }
        } catch (LineUnavailableException | IllegalArgumentException | SecurityException e) {
            Logging.warnTagged("Voice", "Microphone unavailable: %s", e.toString());
        } finally {
            if (line != null) {
                line.stop();
                line.close();
            }

            synchronized (this.captureLock) {
                if (this.captureThread == Thread.currentThread()) {
                    this.captureThread = null;
                }
            }
        }
    }

    private void playbackLoop() {
        SourceDataLine line = null;
        try {
            line = (SourceDataLine)AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, FORMAT));
            line.open(FORMAT, VoiceCodec.FRAME_BYTES * 4);
            line.start();
            int idle = 0;
            while (idle < PLAYBACK_IDLE_FRAMES) {
                List<short[]> frames = this.nextFrames();
                idle = frames.isEmpty() ? idle + 1 : 0;
                byte[] pcm = VoiceCodec.toPcm(VoiceCodec.mix(frames, this.speakerGain));
                line.write(pcm, 0, pcm.length);
            }

            line.drain();
        } catch (LineUnavailableException | IllegalArgumentException | SecurityException e) {
            Logging.warnTagged("Voice", "Speaker unavailable: %s", e.toString());
            synchronized (this.playbackLock) {
                this.incoming.clear();
                this.active.clear();
            }
        } finally {
            if (line != null) {
                line.stop();
                line.close();
            }

            synchronized (this.playbackLock) {
                if (this.playbackThread == Thread.currentThread()) {
                    this.playbackThread = null;
                }

                if (!this.incoming.isEmpty()) {
                    this.startPlaybackLocked();
                }
            }
        }
    }

    private List<short[]> nextFrames() {
        List<short[]> frames = new ArrayList<>();
        synchronized (this.playbackLock) {
            Iterator<Map.Entry<UUID, ArrayDeque<short[]>>> it = this.incoming.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, ArrayDeque<short[]>> entry = it.next();
                ArrayDeque<short[]> queue = entry.getValue();
                if (!this.active.contains(entry.getKey())) {
                    if (queue.size() < PREBUFFER_FRAMES) {
                        continue;
                    }

                    this.active.add(entry.getKey());
                }

                short[] frame = queue.pollFirst();
                if (frame != null) {
                    frames.add(frame);
                }

                if (queue.isEmpty()) {
                    this.active.remove(entry.getKey());
                    it.remove();
                }
            }
        }

        return frames;
    }

    private static TargetDataLine openMicrophone(String name) throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMAT);
        int buffer = VoiceCodec.FRAME_BYTES * 8;
        if (name != null && !name.equals("Default") && !name.equals("Unknown")) {
            for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
                if (mixerInfo.getName().equals(name) || mixerInfo.getDescription().equals(name)) {
                    Mixer mixer = AudioSystem.getMixer(mixerInfo);
                    if (mixer.isLineSupported(info)) {
                        TargetDataLine line = (TargetDataLine)mixer.getLine(info);
                        line.open(FORMAT, buffer);
                        return line;
                    }
                }
            }
        }

        TargetDataLine line = (TargetDataLine)AudioSystem.getLine(info);
        line.open(FORMAT, buffer);
        return line;
    }

    private static void send(byte[] data) {
        ClientNetHandler net = CheatBreaker.getInstance().getNetHandler();
        if (net != null
            && Minecraft.getMinecraft().thePlayer != null
            && net.isBinaryChannelRegistered()
            && net.voiceEnabled
            && net.getVoiceChannel() != null) {
            net.sendPacket(new CBPacketClientVoice(data));
        }
    }
}
