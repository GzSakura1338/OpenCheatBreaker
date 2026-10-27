package com.cheatbreaker.client.emote;

public class TwerkEmoteDefinition extends EmoteDefinition {
   @Override
   public boolean rendersCape() {
      return false;
   }

   @Override
   public boolean rendersWings() {
      return false;
   }

   @Override
   public boolean stopsOnMovement() {
      return true;
   }

   public TwerkEmoteDefinition(String str, int n, boolean flag) {
      super(str, n, flag);
   }
}
