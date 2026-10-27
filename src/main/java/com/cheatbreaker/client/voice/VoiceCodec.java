package com.cheatbreaker.client.voice;

import java.util.List;

public final class VoiceCodec {
   public static final int SAMPLE_RATE = 16000;
   public static final int FRAME_SAMPLES = 320;
   public static final int FRAME_BYTES = FRAME_SAMPLES * 2;
   public static final byte FORMAT_MU_LAW = 1;
   private static final int BIAS = 0x84;
   private static final int CLIP = 32635;

   private VoiceCodec() {
   }

   public static byte[] encode(short[] samples) {
      byte[] data = new byte[samples.length + 1];
      data[0] = FORMAT_MU_LAW;
      for (int i = 0; i < samples.length; i++) {
         data[i + 1] = encodeSample(samples[i]);
      }

      return data;
   }

   public static short[] decode(byte[] data) {
      if (data == null || data.length < 2 || data[0] != FORMAT_MU_LAW) {
         return null;
      }

      short[] samples = new short[data.length - 1];
      for (int i = 0; i < samples.length; i++) {
         samples[i] = decodeSample(data[i + 1]);
      }

      return samples;
   }

   public static byte encodeSample(short pcm) {
      int sample = pcm;
      int sign = (sample >> 8) & 0x80;
      if (sign != 0) {
         sample = -sample;
      }

      if (sample > CLIP) {
         sample = CLIP;
      }

      sample += BIAS;
      int exponent = 7;
      for (int mask = 0x4000; (sample & mask) == 0 && exponent > 0; mask >>= 1) {
         exponent--;
      }

      int mantissa = (sample >> (exponent + 3)) & 0x0F;
      return (byte)~(sign | (exponent << 4) | mantissa);
   }

   public static short decodeSample(byte encoded) {
      int value = ~encoded & 0xFF;
      int sign = value & 0x80;
      int exponent = (value >> 4) & 0x07;
      int mantissa = value & 0x0F;
      int sample = (((mantissa << 3) + BIAS) << exponent) - BIAS;
      return (short)(sign != 0 ? -sample : sample);
   }

   public static short[] toSamples(byte[] pcm, int length) {
      short[] samples = new short[length / 2];
      for (int i = 0; i < samples.length; i++) {
         samples[i] = (short)((pcm[i * 2] & 0xFF) | (pcm[i * 2 + 1] << 8));
      }

      return samples;
   }

   public static byte[] toPcm(short[] samples) {
      byte[] pcm = new byte[samples.length * 2];
      for (int i = 0; i < samples.length; i++) {
         pcm[i * 2] = (byte)samples[i];
         pcm[i * 2 + 1] = (byte)(samples[i] >> 8);
      }

      return pcm;
   }

   public static void applyGain(short[] samples, float gain) {
      if (gain == 1.0F) {
         return;
      }

      for (int i = 0; i < samples.length; i++) {
         samples[i] = clamp(Math.round(samples[i] * gain));
      }
   }

   public static short[] mix(List<short[]> frames, float gain) {
      int[] sum = new int[FRAME_SAMPLES];
      for (short[] frame : frames) {
         for (int i = 0; i < FRAME_SAMPLES && i < frame.length; i++) {
            sum[i] += frame[i];
         }
      }

      short[] mixed = new short[FRAME_SAMPLES];
      for (int i = 0; i < FRAME_SAMPLES; i++) {
         mixed[i] = clamp(Math.round(sum[i] * gain));
      }

      return mixed;
   }

   private static short clamp(int value) {
      return (short)Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, value));
   }
}
