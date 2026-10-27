package com.cheatbreaker.client.util;

import java.util.Arrays;

public class TextBuffer implements CharSequence {
   public char[] chars;
   public int size;

   public TextBuffer(String str) {
      this(str.toCharArray(), str.length());
   }

   @Override
   public CharSequence subSequence(int n, int n2) {
      char[] chars = new char[n2 - n];
      System.arraycopy(this.chars, n, chars, 0, n2 - n);
      return new TextBuffer(chars, chars.length);
   }

   @Override
   public int length() {
      return this.size;
   }

   @Override
   public char charAt(int n) {
      return this.chars[n];
   }

   public TextBuffer trimTrailing() {
      int n = this.length();

      while (n > 0 && Character.isWhitespace(this.charAt(n - 1))) {
         n--;
      }

      this.size = n;
      return this;
   }

   public String substring(int n, int n2) {
      return new String(this.chars, n, n2 - n);
   }

   public void append(TextBuffer textBuffer) {
      int n = textBuffer.length();
      this.ensureCapacity(n);
      System.arraycopy(textBuffer.chars, 0, this.chars, this.size, n);
      this.size += n;
   }

   public void setLength(int n) {
      this.size = n;
   }

   public void ensureCapacity(int n) {
      if (this.size + n > this.chars.length) {
         this.chars = Arrays.copyOf(this.chars, this.newCapacity(n));
      }
   }

   public void delete(int n, int n2) {
      System.arraycopy(this.chars, n2, this.chars, n, this.size - n2);
      this.size -= n2 - n;
   }

   public TextBuffer() {
      this(new char[16], 0);
   }

   public TextBuffer(char[] chars, int n) {
      this.chars = chars;
      this.size = n;
   }

   public void appendChar(char c) {
      this.ensureCapacity(1);
      this.chars[this.size] = c;
      this.size++;
   }

   public int newCapacity(int n) {
      int n2 = this.chars.length * 2;
      if (n2 < this.size + n) {
         n2 = this.size + n;
      }

      return n2;
   }

   @Override
   public String toString() {
      return new String(this.chars, 0, this.size);
   }

   public char[] getChars() {
      return this.chars;
   }
}
