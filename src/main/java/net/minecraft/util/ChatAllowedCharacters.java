package net.minecraft.util;

public class ChatAllowedCharacters {
   public static char[] allowedCharactersArray = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};

   public static boolean isAllowedCharacter(char character) {
      return character != 167 && character >= ' ' && character != 127;
   }

   public static String filterAllowedCharacters(String input) {
      StringBuilder sb = new StringBuilder();

      for (char c : input.toCharArray()) {
         if (isAllowedCharacter(c)) {
            sb.append(c);
         }
      }

      return sb.toString();
   }
}
