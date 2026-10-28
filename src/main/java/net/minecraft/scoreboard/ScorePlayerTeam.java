package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;

public class ScorePlayerTeam extends Team {
    public Set<String> membershipSet = Sets.newHashSet();
    public String namePrefixSPT = "";
    public String colorSuffix = "";
    public boolean allowFriendlyFire = true;
    public boolean canSeeFriendlyInvisibles = true;
    public Scoreboard theScoreboard;
    public String name;
    public String teamNameSPT;

    public ScorePlayerTeam(Scoreboard scoreboard, String str) {
        this.theScoreboard = scoreboard;
        this.name = str;
        this.teamNameSPT = str;
    }

    @Override
    public String getRegisteredName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.teamNameSPT;
    }

    public void setTeamName(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }

        this.teamNameSPT = str;
        this.theScoreboard.broadcastTeamRemoved(this);
    }

    public Collection<String> getMembershipCollection() {
        return this.membershipSet;
    }

    public String getColorPrefix() {
        return this.namePrefixSPT;
    }

    public void setNamePrefix(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }

        this.namePrefixSPT = str;
        this.theScoreboard.broadcastTeamRemoved(this);
    }

    public String getColorSuffix() {
        return this.colorSuffix;
    }

    public void setNameSuffix(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Suffix cannot be null");
        }

        this.colorSuffix = str;
        this.theScoreboard.broadcastTeamRemoved(this);
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

    public void setAllowFriendlyFire(boolean flag) {
        this.allowFriendlyFire = flag;
        this.theScoreboard.broadcastTeamRemoved(this);
    }

    @Override
    public boolean shouldShowFriendlyInvisibles() {
        return this.canSeeFriendlyInvisibles;
    }

    public void setSeeFriendlyInvisiblesEnabled(boolean flag) {
        this.canSeeFriendlyInvisibles = flag;
        this.theScoreboard.broadcastTeamRemoved(this);
    }

    public int getFriendlyFlagsBitwise() {
        int n = 0;
        if (this.getAllowFriendlyFire()) {
            n |= 1;
        }

        if (this.shouldShowFriendlyInvisibles()) {
            n |= 2;
        }

        return n;
    }

    public void setFriendlyFlagsBitwise(int n) {
        this.setAllowFriendlyFire((n & 1) > 0);
        this.setSeeFriendlyInvisiblesEnabled((n & 2) > 0);
    }
}
