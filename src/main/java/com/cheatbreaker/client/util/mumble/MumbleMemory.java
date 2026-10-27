package com.cheatbreaker.client.util.mumble;

public class MumbleMemory {
   public final long uiVersionOffset = 0L;
   public final long uiTickOffset = 4L;
   public final long avatarPositionOffset = 8L;
   public final long avatarFrontOffset = 20L;
   public final long avatarTopOffset = 32L;
   public final long nameOffset = 44L;
   public long cameraPositionOffset;
   public long cameraFrontOffset;
   public long cameraTopOffset;
   public long identityOffset;
   public long contextLengthOffset;
   public long contextOffset;
   public long descriptionOffset;
   public int size;

   public MumbleMemory(int n) {
      this.cameraPositionOffset = 44L + 256L * n;
      this.cameraFrontOffset = this.cameraPositionOffset + 12L;
      this.cameraTopOffset = this.cameraFrontOffset + 12L;
      this.identityOffset = this.cameraTopOffset + 12L;
      this.contextLengthOffset = this.identityOffset + 256L * n;
      this.contextOffset = this.contextLengthOffset + 4L;
      this.descriptionOffset = this.contextOffset + 256L;
      this.size = (int)(this.descriptionOffset + 2048L * n);
   }
}
