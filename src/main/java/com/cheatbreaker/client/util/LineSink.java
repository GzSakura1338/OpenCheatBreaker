package com.cheatbreaker.client.util;

public interface LineSink {
   void writeChars(char[] chars, int n, int n2) throws java.io.IOException;

   void endLine() throws java.io.IOException;

   default void writeString(String str) throws java.io.IOException {
      char[] toCharArray = str.toCharArray();
      this.writeChars(toCharArray, 0, toCharArray.length);
   }
}
