package net.minecraft.entity.boss;

public class BossStatus {
    public static float healthScale;
    public static int statusBarTime;
    public static String bossName;
    public static boolean hasColorModifier;

    public static void setBossStatus(IBossDisplayData bossDisplayData, boolean flag) {
        healthScale = bossDisplayData.getHealth() / bossDisplayData.getMaxHealth();
        statusBarTime = 100;
        bossName = bossDisplayData.getFormattedCommandSenderName().getFormattedText();
        hasColorModifier = flag;
    }
}
