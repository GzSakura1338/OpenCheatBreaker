package com.cheatbreaker.client.friend;

import com.cheatbreaker.client.friends.Friend;

public class FriendBuilder {
   public FriendOnlineStatus friendOnlineStatus;
   public String text;
   public long longValue;
   public boolean flag;
   public int intValue;
   public String text2;
   public String text3;
   public String text4;

   public FriendBuilder setText(String str) {
      this.text = str;
      return this;
   }

   @Override
   public String toString() {
      return "Friend.FriendBuilder(playerId="
         + this.text3
         + ", name="
         + this.text
         + ", iconColor="
         + this.intValue
         + ", status="
         + this.text4
         + ", server="
         + this.text2
         + ", online="
         + this.flag
         + ", offlineSince="
         + this.longValue
         + ", onlineStatus="
         + this.friendOnlineStatus
         + ")";
   }

   public FriendBuilder setLongValue(long l) {
      this.longValue = l;
      return this;
   }

   public Friend createFriend() {
      return new Friend(this.text3, this.text, this.intValue, this.text4, this.text2, this.flag, this.longValue, this.friendOnlineStatus);
   }

   public FriendBuilder setText2(String str) {
      this.text2 = str;
      return this;
   }

   public FriendBuilder setFriendOnlineStatus(FriendOnlineStatus friendOnlineStatus) {
      this.friendOnlineStatus = friendOnlineStatus;
      return this;
   }

   public FriendBuilder setText3(String str) {
      this.text3 = str;
      return this;
   }

   public FriendBuilder setText4(String str) {
      this.text4 = str;
      return this;
   }

   public FriendBuilder setIntValue(int n) {
      this.intValue = n;
      return this;
   }

   public FriendBuilder setFlag(boolean flag) {
      this.flag = flag;
      return this;
   }
}
