package com.cheatbreaker.client.radio;

import com.cheatbreaker.client.CheatBreaker;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDeviceBase;

public class RadioVolume extends AudioDeviceBase {
   public SourceDataLine sourceDataLine = null;
   public AudioFormat audioFormat = null;
   public byte[] byteArray = new byte[4096];

   public byte[] getByteArray(short[] shorts, int n, int n2) {
      byte[] byteArray = this.getByteArray(n2 * 2);
      int n3 = 0;

      while (n2-- > 0) {
         short s = shorts[n++];
         byteArray[n3++] = (byte)s;
         byteArray[n3++] = (byte)(s >>> 8);
      }

      return byteArray;
   }

   public AudioFormat createAudioFormat() {
      if (this.audioFormat == null) {
         Decoder decoder = this.getDecoder();
         this.audioFormat = new AudioFormat(decoder.getOutputFrequency(), 16, decoder.getOutputChannels(), true, false);
      }

      return this.audioFormat;
   }

   public void setAudioFormat(AudioFormat audioFormat) {
      this.audioFormat = audioFormat;
   }

   public void process(float f) {
      if (this.sourceDataLine != null) {
         FloatControl control = (FloatControl)this.sourceDataLine.getControl(Type.MASTER_GAIN);
         float f2 = control.getMaximum() - control.getMinimum();
         float f3 = f2 * (f / 100.0F) + control.getMinimum();
         control.setValue(f3);
      }
   }

   public void flushImpl() {
      if (this.sourceDataLine != null) {
         this.sourceDataLine.drain();
      }
   }

   public byte[] getByteArray(int n) {
      if (this.byteArray.length < n) {
         this.byteArray = new byte[n + 1024];
      }

      return this.byteArray;
   }

   public void writeImpl(short[] shorts, int n, int n2) throws JavaLayerException {
      if (this.sourceDataLine == null) {
         this.run();
      }

      byte[] byteArray = this.getByteArray(shorts, n, n2);
      this.sourceDataLine.write(byteArray, 0, n2 * 2);
   }

   public void handleAudioFormat(AudioFormat audioFormat) {
      if (!this.isOpen()) {
         this.setAudioFormat(audioFormat);
         this.openImpl();
         this.setOpen(true);
      }
   }

   public Info createInfo() {
      AudioFormat createAudioFormat = this.createAudioFormat();
      return new Info(SourceDataLine.class, createAudioFormat);
   }

   public void run() throws JavaLayerException {
      Throwable throwable = null;

      try {
         Line line = AudioSystem.getLine(this.createInfo());
         if (line instanceof SourceDataLine) {
            this.sourceDataLine = (SourceDataLine)line;
            this.sourceDataLine.open(this.audioFormat);
            this.sourceDataLine.start();
            this.process(((Integer)CheatBreaker.getInstance().getKeybindSettings().radioVolume.getValue()).intValue());
         }
      } catch (RuntimeException | LineUnavailableException | LinkageError e) {
         throwable = e;
      }

      if (this.sourceDataLine == null) {
         throw new JavaLayerException("cannot obtain source audio line", throwable);
      }
   }

   public void openImpl() {
   }

   public int getPosition() {
      int n = 0;
      if (this.sourceDataLine != null) {
         n = (int)(this.sourceDataLine.getMicrosecondPosition() / 1000L);
      }

      return n;
   }

   public void closeImpl() {
      if (this.sourceDataLine != null) {
         this.sourceDataLine.close();
      }
   }
}
