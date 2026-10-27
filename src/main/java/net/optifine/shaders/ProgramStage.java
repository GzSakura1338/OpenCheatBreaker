package net.optifine.shaders;

public enum ProgramStage {
   NONE(""),
   SHADOW("shadow"),
   GBUFFERS("gbuffers"),
   DEFERRED("deferred"),
   COMPOSITE("composite");

   public String name;

   ProgramStage(String str) {
      this.name = str;
   }

   public String getName() {
      return this.name;
   }
}
