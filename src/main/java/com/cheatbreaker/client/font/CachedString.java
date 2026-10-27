package com.cheatbreaker.client.font;

import java.util.List;

public class CachedString {
   public List<CachedStringPart> parts;
   public float width;
   public long expiryTime;
   public boolean shadow;

   public void releaseParts() {
      for (CachedStringPart cachedStringPart : this.parts) {
         cachedStringPart.glDeleteLists();
      }
   }

   public List<CachedStringPart> getParts() {
      return this.parts;
   }

   public void prepareParts() {
      for (CachedStringPart cachedStringPart : this.parts) {
         cachedStringPart.render();
      }
   }

   public void setExpiryTime(long l) {
      this.expiryTime = l;
   }

   public CachedString(List<CachedStringPart> list, float f, long l, boolean flag) {
      this.parts = list;
      this.width = f;
      this.expiryTime = l;
      this.shadow = flag;
   }

   public boolean isShadow() {
      return this.shadow;
   }

   public long getExpiryTime() {
      return this.expiryTime;
   }

   public float getWidth() {
      return this.width;
   }
}
