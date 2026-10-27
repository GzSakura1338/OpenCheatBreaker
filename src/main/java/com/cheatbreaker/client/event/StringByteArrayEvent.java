package com.cheatbreaker.client.event;

public class StringByteArrayEvent extends Event {
   public String text;
   public byte[] bytes;

   public byte[] getBytes() {
      return this.bytes;
   }

   public StringByteArrayEvent(String str, byte[] bytes) {
      this.text = str;
      this.bytes = bytes;
   }

   public String getText() {
      return this.text;
   }
}
