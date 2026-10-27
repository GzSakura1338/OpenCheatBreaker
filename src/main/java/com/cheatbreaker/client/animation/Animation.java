package com.cheatbreaker.client.animation;

public abstract class Animation {
   public float frozenProgress;
   public boolean playing = true;
   public int currentLoop = 1;
   public int loopCount = 1;
   public long startTime;
   public long duration;
   public long pausedElapsed;
   public boolean triggered;
   public boolean repeating;
   public float value;
   public long skippedTime;

   public void setCurrentLoop(int n) {
      this.currentLoop = n;
   }

   public long getRemainingTime() {
      return this.startTime + this.duration - this.skippedTime - System.currentTimeMillis();
   }

   public void pause() {
      this.playing = false;
      this.frozenProgress = this.computeProgress();
      this.pausedElapsed = System.currentTimeMillis() - this.startTime;
   }

   public boolean isTriggered() {
      return this.triggered;
   }

   public void enableRepeat() {
      this.repeating = true;
   }

   public void setPlaying(boolean flag) {
      this.playing = flag;
   }

   public Animation(long l, float f) {
      this.duration = l;
      this.value = f;
   }

   public void setTriggered(boolean flag) {
      this.triggered = flag;
   }

   public float getProgress() {
      if (this.startTime == 0L) {
         return 0.0F;
      }

      if (!this.isFinished()) {
         return this.playing ? this.computeProgress() : this.frozenProgress;
      }

      if (this.repeating || this.loopCount >= 1 && this.currentLoop < this.loopCount) {
         this.start();
         this.currentLoop++;
      }

      return this.value;
   }

   public long getClampedRemainingTime() {
      long l = this.playing ? this.getRemainingTime() : System.currentTimeMillis() - this.pausedElapsed + this.duration - System.currentTimeMillis();
      return Math.min(this.duration, Math.max(0L, l));
   }

   public void setDuration(long l) {
      this.duration = l;
   }

   public void setRepeating(boolean flag) {
      this.repeating = flag;
   }

   public void setPausedElapsed(long l) {
      this.pausedElapsed = l;
   }

   public void seek(long l) {
      this.startTime = System.currentTimeMillis() - l;
      this.playing = true;
   }

   public boolean isFinished() {
      return this.getRemainingTime() <= 0L && this.playing;
   }

   public void delayStart(long l) {
      if (this.startTime != 0L) {
         this.startTime += l;
      }
   }

   public void setStartTime(long l) {
      this.startTime = l;
   }

   public long getDuration() {
      return this.duration;
   }

   public long getElapsedTime() {
      return this.duration - this.getClampedRemainingTime();
   }

   public long getPausedElapsed() {
      return this.pausedElapsed;
   }

   public abstract float computeProgress();

   public boolean hasStarted() {
      return this.startTime != 0L;
   }

   public long getSkippedTime() {
      return this.skippedTime;
   }

   public void setFrozenProgress(float f) {
      this.frozenProgress = f;
   }

   public boolean isRunning() {
      return this.startTime != 0L && this.getRemainingTime() > 0L;
   }

   public float getProgressClamped() {
      if (this.startTime == 0L) {
         return 0.0F;
      } else {
         return this.getRemainingTime() <= 0L ? 1.0F : this.computeProgress();
      }
   }

   public long getStartTime() {
      return this.startTime;
   }

   public void setSkippedTime(long l) {
      this.skippedTime = l;
   }

   public void restartFrom(float f) {
      this.startTime = System.currentTimeMillis();
      this.skippedTime = f == 0.0F ? 0L : (long)((float)this.duration * (1.0F - f));
      this.playing = true;
   }

   public float updateToggle(boolean flag) {
      if (flag && !this.triggered) {
         this.triggered = true;
         this.restartFrom(this.getProgressClamped());
      } else if (this.triggered && !flag) {
         this.triggered = false;
         this.restartFrom(this.getProgressClamped());
      }

      if (this.startTime == 0L) {
         return 0.0F;
      }

      float progressClamped = this.getProgressClamped();
      return this.triggered ? progressClamped : 1.0F - progressClamped;
   }

   public void resume() {
      this.startTime = System.currentTimeMillis() - this.pausedElapsed;
      this.playing = true;
   }

   public int getLoopCount() {
      return this.loopCount;
   }

   public float getFrozenProgress() {
      return this.frozenProgress;
   }

   public int getCurrentLoop() {
      return this.currentLoop;
   }

   public boolean isRepeating() {
      return this.repeating;
   }

   public float getValue() {
      return this.value;
   }

   public void start() {
      this.startTime = System.currentTimeMillis();
      this.playing = true;
      this.skippedTime = 0L;
   }

   public boolean isPlaying() {
      return this.playing;
   }

   public void setLoopCount(int n) {
      this.loopCount = n;
   }

   public void reset() {
      this.startTime = 0L;
      this.currentLoop = 1;
   }
}
