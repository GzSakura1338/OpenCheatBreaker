package net.minecraft.scoreboard;

public abstract class Team {
    public boolean isSameTeam(Team other) {
        return other != null && this == other;
    }

    public abstract String getRegisteredName();

    public abstract String formatString(String str);

    public abstract boolean shouldShowFriendlyInvisibles();

    public abstract boolean getAllowFriendlyFire();
}
