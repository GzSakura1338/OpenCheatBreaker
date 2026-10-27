package com.cheatbreaker.client.util;

import com.google.common.base.Preconditions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.io.input.CharSequenceReader;

public class TextUtils {
   public static final String SOFT_BREAK_PUNCTUATION = "\"'‘’“”?./!,;:_";
   public static Set<Character> defaultSoftBreakChars = toCharSet("\"'‘’“”?./!,;:_");
   public static Function<CharSequence, Number> defaultLengthFunction = charSequence -> charSequence.length();
   public static final String PUNCTUATION = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

   public static TextWrapper wrapResource(String str) {
      return wrapResourceWithCharset(str, StandardCharsets.UTF_8);
   }

   public static boolean exceedsWidth(Function<? super CharSequence, ? extends Number> function, CharSequence charSequence, double d) {
      return ((Number)function.apply(charSequence)).doubleValue() > d;
   }

   public static TextWrapper wrapResourceWithCharset(String str, Charset charset) {
      return new TextWrapper(new BufferedReader(new InputStreamReader(TextUtils.class.getResourceAsStream(str), charset)), true);
   }

   public static void emitLine(LineSink lineSink, TextBuffer textBuffer) throws IOException {
      lineSink.writeChars(textBuffer.getChars(), 0, textBuffer.length());
      lineSink.endLine();
      textBuffer.setLength(0);
   }

   public static void trimLeadingBuffer(TextBuffer textBuffer) {
      int n = 0;

      while (n < textBuffer.length() && Character.isWhitespace(textBuffer.charAt(n))) {
         n++;
      }

      if (n < textBuffer.length() && n > 0) {
         textBuffer.delete(0, n);
      }
   }

   public static TextWrapper wrapReader(Reader reader) {
      return wrapReaderClosing(reader, false);
   }

   public static TextWrapper wrapStream(InputStream inputStream) {
      return wrapStreamWithCharset(inputStream, StandardCharsets.UTF_8);
   }

   public static boolean isBlank(CharSequence charSequence) {
      for (int i = 0; i < charSequence.length(); i++) {
         if (!Character.isWhitespace(charSequence.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   public static void wrapToWriter(
      Reader reader,
      final Writer writer,
      final String str,
      Number number,
      Function<? super CharSequence, ? extends Number> function,
      Set<Character> set,
      boolean flag,
      boolean flag2
   ) throws IOException {
      LineSink lineSink = new LineSink() {
         @Override
         public void endLine() throws IOException {
            writer.write(str);
         }

         @Override
         public void writeString(String str2) throws IOException {
            writer.write(str2);
         }

         @Override
         public void writeChars(char[] chars, int n, int n2) throws IOException {
            writer.write(chars, n, n2);
         }
      };
      wrapText(reader, lineSink, number, function, set, flag, flag2);
   }

   public static boolean isPunctuation(char c) {
      return "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".indexOf(c) != -1;
   }

   public static String trimLeading(String str) {
      TextBuffer textBuffer = new TextBuffer(str);
      trimLeadingBuffer(textBuffer);
      return textBuffer.toString();
   }

   public static void breakLongWord(LineSink lineSink, TextBuffer textBuffer, boolean flag) throws IOException {
      String str;
      if (flag && textBuffer.length() > 2 && !isBlank(str = textBuffer.substring(0, textBuffer.length() - 2))) {
         lineSink.writeString(str);
         lineSink.writeString("-");
         lineSink.endLine();
         textBuffer.delete(0, textBuffer.length() - 2);
      } else {
         String substring = textBuffer.substring(0, textBuffer.length() - 1);
         if (!isBlank(substring)) {
            lineSink.writeString(substring);
         }

         lineSink.endLine();
         textBuffer.delete(0, textBuffer.length() - 1);
      }
   }

   public static void wrapText(
      Reader reader, LineSink lineSink, Number number, Function<? super CharSequence, ? extends Number> function, Set<Character> set, boolean flag, boolean flag2
   ) throws IOException {
      TextBuffer textBuffer = new TextBuffer();
      TextBuffer textBuffer2 = new TextBuffer();
      CharSequence concat2 = concat(textBuffer, textBuffer2);
      double d = number.doubleValue();
      boolean flag3 = false;
      boolean flag4 = false;
      boolean flag5 = false;

      while (true) {
         int read = reader.read();
         if (read == -1) {
            if (textBuffer.length() > 0) {
               String str = textBuffer.toString() + textBuffer2;
               if (flag3) {
                  str = trimLeading(str);
               }

               lineSink.writeString(str);
            } else {
               if (flag3) {
                  trimLeadingBuffer(textBuffer2);
               }

               if (!isBlank(textBuffer2)) {
                  lineSink.writeChars(textBuffer2.getChars(), 0, textBuffer2.length());
               }
            }

            return;
         }

         char c = (char)read;
         flag4 = Character.isLetter(c) || set.contains(c);
         if (c == '\n') {
            textBuffer.append(textBuffer2);
            if (exceedsWidth(function, textBuffer, d)) {
               textBuffer.trimTrailing();
            }

            if (!isBlank(textBuffer)) {
               lineSink.writeChars(textBuffer.getChars(), 0, textBuffer.length());
            }

            lineSink.endLine();
            textBuffer2.setLength(0);
            textBuffer.setLength(0);
            flag3 = false;
         } else if (c != '\r') {
            if (flag4 && !flag5) {
               textBuffer2.appendChar(c);
               if (flag3 && textBuffer.length() == 0) {
                  trimLeadingBuffer(textBuffer2);
               }

               if (exceedsWidth(function, concat2, d)) {
                  if (textBuffer.length() > 0) {
                     emitLine(lineSink, textBuffer);
                     trimLeadingBuffer(textBuffer2);
                     if (exceedsWidth(function, textBuffer2, d)) {
                        if (flag2) {
                           breakLongWord(lineSink, textBuffer2, flag);
                        } else {
                           flag3 = true;
                        }
                     } else {
                        flag3 = true;
                     }
                  } else if (flag2) {
                     breakLongWord(lineSink, textBuffer2, flag);
                  } else {
                     flag3 = true;
                  }
               }
            } else {
               if (textBuffer2.length() > 0 && !isBlank(textBuffer2)) {
                  appendAndClear(textBuffer, textBuffer2);
                  if (flag3) {
                     trimLeadingBuffer(textBuffer);
                  }
               }

               textBuffer2.appendChar(c);
               if (exceedsWidth(function, concat2, d)) {
                  Preconditions.checkArgument(
                     textBuffer.length() > 0, "line length was zero. If this happens please contribute unit test that provokes this failure to the project!"
                  );
                  if (!isBlank(textBuffer)) {
                     emitLine(lineSink, textBuffer);
                  } else {
                     textBuffer.setLength(0);
                  }

                  flag3 = true;
               }
            }
         }

         flag5 = isPunctuation(c) && !set.contains(c);
      }
   }

   public static TextWrapper wrapSequence(CharSequence charSequence) {
      return wrapReaderClosing(new BufferedReader(new CharSequenceReader(charSequence)), true);
   }

   public static Set<Character> toCharSet(String str) {
      HashSet set = new HashSet();

      for (int i = 0; i < str.length(); i++) {
         set.add(str.charAt(i));
      }

      return set;
   }

   public static TextWrapper wrapReaderClosing(Reader reader, boolean flag) {
      return new TextWrapper(reader, flag);
   }

   public static CharSequence trimTrailing(CharSequence charSequence) {
      int n = charSequence.length();

      while (n > 0 && Character.isWhitespace(charSequence.charAt(n - 1))) {
         n--;
      }

      return n != charSequence.length() ? charSequence.subSequence(0, n) : charSequence;
   }

   public static CharSequence concat(CharSequence charSequence, CharSequence charSequence2) {
      return new JoinedCharSequence(charSequence, charSequence2);
   }

   public static void appendAndClear(TextBuffer textBuffer, TextBuffer textBuffer2) {
      textBuffer.append(textBuffer2);
      textBuffer2.setLength(0);
   }

   public static void closeReader(Reader reader) {
      try {
         reader.close();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public static TextWrapper wrapFile(File file, Charset charset) {
      try {
         return wrapReaderClosing(new BufferedReader(new InputStreamReader(new FileInputStream(file), charset)), true);
      } catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      }
   }

   public static TextWrapper wrapStreamWithCharset(InputStream inputStream, Charset charset) {
      return wrapReader(new BufferedReader(new InputStreamReader(inputStream, charset)));
   }
}
