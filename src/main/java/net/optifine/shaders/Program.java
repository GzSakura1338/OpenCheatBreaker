package net.optifine.shaders;

import java.nio.IntBuffer;
import java.util.Arrays;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.config.RenderScale;

public class Program {
   public Boolean[] buffersFlip = new Boolean[8];
   public boolean[] toggleColorTextures = new boolean[8];
   public int index;
   public String name;
   public GlAlphaState alphaState;
   public GlBlendState blendState;
   public RenderScale renderScale;
   public ProgramStage programStage;
   public int intValue;
   public int ref;
   public String drawBufSettings;
   public IntBuffer drawBuffers;
   public IntBuffer drawBuffersBuffer;
   public int compositeMipmapSetting;
   public int countInstances;
   public Program programBackup;

   public Program(int n, String str, ProgramStage programStage, Program program) {
      this.index = n;
      this.name = str;
      this.programStage = programStage;
      this.programBackup = program;
   }

   public Program(int n, String str, ProgramStage programStage, boolean flag) {
      this.index = n;
      this.name = str;
      this.programStage = programStage;
      this.programBackup = flag ? this : null;
   }

   public void resetProperties() {
      this.alphaState = null;
      this.blendState = null;
      this.renderScale = null;
      Arrays.fill(this.buffersFlip, null);
   }

   public void resetId() {
      this.intValue = 0;
      this.ref = 0;
   }

   public void resetConfiguration() {
      this.drawBufSettings = null;
      this.compositeMipmapSetting = 0;
      this.countInstances = 0;
      if (this.drawBuffersBuffer == null) {
         this.drawBuffersBuffer = Shaders.nextIntBuffer(8);
      }
   }

   public void copyFrom(Program program) {
      this.intValue = program.getId();
      this.alphaState = program.getAlphaState();
      this.blendState = program.getBlendState();
      this.renderScale = program.getRenderScale();
      System.arraycopy(program.getBuffersFlip(), 0, this.buffersFlip, 0, this.buffersFlip.length);
      this.drawBufSettings = program.getDrawBufSettings();
      this.drawBuffers = program.getDrawBuffers();
      this.compositeMipmapSetting = program.getCompositeMipmapSetting();
      this.countInstances = program.getCountInstances();
      System.arraycopy(program.getToggleColorTextures(), 0, this.toggleColorTextures, 0, this.toggleColorTextures.length);
   }

   public int getIndex() {
      return this.index;
   }

   public String getName() {
      return this.name;
   }

   public ProgramStage getProgramStage() {
      return this.programStage;
   }

   public Program getProgramBackup() {
      return this.programBackup;
   }

   public int getId() {
      return this.intValue;
   }

   public int getRef() {
      return this.ref;
   }

   public String getDrawBufSettings() {
      return this.drawBufSettings;
   }

   public IntBuffer getDrawBuffers() {
      return this.drawBuffers;
   }

   public IntBuffer getDrawBuffersBuffer() {
      return this.drawBuffersBuffer;
   }

   public int getCompositeMipmapSetting() {
      return this.compositeMipmapSetting;
   }

   public int getCountInstances() {
      return this.countInstances;
   }

   public GlAlphaState getAlphaState() {
      return this.alphaState;
   }

   public GlBlendState getBlendState() {
      return this.blendState;
   }

   public RenderScale getRenderScale() {
      return this.renderScale;
   }

   public Boolean[] getBuffersFlip() {
      return this.buffersFlip;
   }

   public boolean[] getToggleColorTextures() {
      return this.toggleColorTextures;
   }

   public void setId(int n) {
      this.intValue = n;
   }

   public void setRef(int n) {
      this.ref = n;
   }

   public void setDrawBufSettings(String str) {
      this.drawBufSettings = str;
   }

   public void setDrawBuffers(IntBuffer intBuffer) {
      this.drawBuffers = intBuffer;
   }

   public void setCompositeMipmapSetting(int n) {
      this.compositeMipmapSetting = n;
   }

   public void setCountInstances(int n) {
      this.countInstances = n;
   }

   public void setAlphaState(GlAlphaState glAlphaState) {
      this.alphaState = glAlphaState;
   }

   public void setBlendState(GlBlendState glBlendState) {
      this.blendState = glBlendState;
   }

   public void setRenderScale(RenderScale renderScale) {
      this.renderScale = renderScale;
   }

   public String getRealProgramName() {
      if (this.intValue == 0) {
         return "none";
      }

      Program program;
      for (program = this; program.getRef() != this.intValue; program = program.getProgramBackup()) {
         if (program.getProgramBackup() == null || program.getProgramBackup() == program) {
            return "unknown";
         }
      }

      return program.getName();
   }

   @Override
   public String toString() {
      return "name: " + this.name + ", id: " + this.intValue + ", ref: " + this.ref + ", real: " + this.getRealProgramName();
   }
}
