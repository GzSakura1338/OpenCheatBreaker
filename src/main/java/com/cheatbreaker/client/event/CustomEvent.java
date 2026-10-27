package com.cheatbreaker.client.event;

import com.cheatbreaker.client.config.Profile;

public class CustomEvent extends CancellableEvent {
   public Profile profile;

   public CustomEvent(Profile profile) {
      this.profile = profile;
   }

   public Profile getProfile() {
      return this.profile;
   }
}
