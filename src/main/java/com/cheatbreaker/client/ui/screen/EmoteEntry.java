package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.emote.AbstractEmote;

public class EmoteEntry {
   public int emoteId;
   public AbstractEmote abstractEmote;
   public String text;

   public EmoteEntry(int n, AbstractEmote abstractEmote) {
      this.emoteId = n;
      this.abstractEmote = abstractEmote;
      this.text = abstractEmote.getText();
   }
}
