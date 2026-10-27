package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.util.EnumChatFormatting;

public class ScorePlayerTeam extends Team {
   public Set<String> membershipSet = Sets.newHashSet();
   public String namePrefixSPT = "";
   public String colorSuffix = "";
   public boolean allowFriendlyFire = true;
   public boolean canSeeFriendlyInvisibles = true;
   public Team.EnumVisible nameTagVisibility = Team.EnumVisible.ALWAYS;
   public Team.EnumVisible deathMessageVisibility = Team.EnumVisible.ALWAYS;
   public EnumChatFormatting chatFormat = EnumChatFormatting.RESET;
   public Scoreboard theScoreboard;
   public String registeredName;
   public String teamNameSPT;

   public ScorePlayerTeam(Scoreboard theScoreboardIn, String name) {
      this.theScoreboard = theScoreboardIn;
      this.registeredName = name;
      this.teamNameSPT = name;
   }

   @Override
   public String getRegisteredName() {
      return this.registeredName;
   }

   public String getTeamName() {
      return this.teamNameSPT;
   }

   public void setTeamName(String name) {
      if (name == null) {
         throw new IllegalArgumentException("Name cannot be null");
      }

      this.teamNameSPT = name;
      this.theScoreboard.sendTeamUpdate(this);
   }

   @Override
   public Collection<String> getMembershipCollection() {
      return this.membershipSet;
   }

   public String getColorPrefix() {
      return this.namePrefixSPT;
   }

   public void setNamePrefix(String prefix) {
      if (prefix == null) {
         throw new IllegalArgumentException("Prefix cannot be null");
      }

      this.namePrefixSPT = prefix;
      this.theScoreboard.sendTeamUpdate(this);
   }

   public String getColorSuffix() {
      return this.colorSuffix;
   }

   public void setNameSuffix(String suffix) {
      this.colorSuffix = suffix;
      this.theScoreboard.sendTeamUpdate(this);
   }

   @Override
   public String formatString(String input) {
      return this.getColorPrefix() + input + this.getColorSuffix();
   }

   public static String formatPlayerName(Team team, String str) {
      return team == null ? str : team.formatString(str);
   }

   @Override
   public boolean getAllowFriendlyFire() {
      return this.allowFriendlyFire;
   }

   public void setAllowFriendlyFire(boolean friendlyFire) {
      this.allowFriendlyFire = friendlyFire;
      this.theScoreboard.sendTeamUpdate(this);
   }

   @Override
   public boolean getSeeFriendlyInvisiblesEnabled() {
      return this.canSeeFriendlyInvisibles;
   }

   public void setSeeFriendlyInvisiblesEnabled(boolean friendlyInvisibles) {
      this.canSeeFriendlyInvisibles = friendlyInvisibles;
      this.theScoreboard.sendTeamUpdate(this);
   }

   @Override
   public Team.EnumVisible getNameTagVisibility() {
      return this.nameTagVisibility;
   }

   @Override
   public Team.EnumVisible getDeathMessageVisibility() {
      return this.deathMessageVisibility;
   }

   public void setNameTagVisibility(Team.EnumVisible enumVisible) {
      this.nameTagVisibility = enumVisible;
      this.theScoreboard.sendTeamUpdate(this);
   }

   public void setDeathMessageVisibility(Team.EnumVisible enumVisible) {
      this.deathMessageVisibility = enumVisible;
      this.theScoreboard.sendTeamUpdate(this);
   }

   public int getFriendlyFlagsBitwise() {
      int n = 0;
      if (this.getAllowFriendlyFire()) {
         n |= 1;
      }

      if (this.getSeeFriendlyInvisiblesEnabled()) {
         n |= 2;
      }

      return n;
   }

   public void setFriendlyFlagsBitwise(int n) {
      this.setAllowFriendlyFire((n & 1) > 0);
      this.setSeeFriendlyInvisiblesEnabled((n & 2) > 0);
   }

   public void setChatFormat(EnumChatFormatting enumChatFormatting) {
      this.chatFormat = enumChatFormatting;
   }

   public EnumChatFormatting getChatFormat() {
      return this.chatFormat;
   }
}
