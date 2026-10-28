package com.cheatbreaker.client.util;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.network.packet.ViolationReportPacket;
import com.cheatbreaker.client.util.TimestampBuffer;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import net.minecraft.client.Minecraft;

public class ClickTracker {
    public static boolean enabled;
    public static TimestampBuffer clicks;
    public static TimestampBuffer reportedClicks;
    public static Deque<Long> pendingClicks;
    public static int tickCounter;
    public static double allowance;

    public static void onTick(int n) {
        ClickTracker.checkEveryFiveTicks(n);
    }

    public static void checkEveryFiveTicks(int n) {
        if (n % 5 == 0) {
            ClickTracker.analyze();
        }
    }

    public static int nextTick() {
        return tickCounter++;
    }

    public static int getTickCounter() {
        return tickCounter;
    }

    public static boolean tryConsumeAllowance() {
        boolean bl;
        boolean bl2 = bl = allowance <= 2.0;
        if (bl) {
            allowance += 1.0;
        }
        return bl;
    }

    public static void analyze() {
        int n = 0;
        int n2 = 0;
        Iterator<Integer> iterator = ClickTracker.clicks.ticks.descendingIterator();
        while (iterator.hasNext() && tickCounter - 100 <= iterator.next()) {
            ++n;
        }
        iterator = ClickTracker.reportedClicks.ticks.descendingIterator();
        while (iterator.hasNext() && tickCounter - 100 <= iterator.next()) {
            ++n2;
        }
        int n3 = n2 - n;
        if (n2 > 320) {
            CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new ViolationReportPacket(432, "CBAC:" + n2));
        }
    }

    public static int getInputClickCount() {
        return -1;
    }

    static {
        clicks = new TimestampBuffer();
        reportedClicks = new TimestampBuffer();
        pendingClicks = new ConcurrentLinkedDeque<Long>();
        allowance = 0.0;
    }

    public static void onClickTick() {
        if (!enabled) {
            ClickTracker.getInputClickCount();
            return;
        }
        if (Minecraft.getMinecraft().currentScreen != null && !Minecraft.getMinecraft().currentScreen.allowUserInput) {
            ClickTracker.getInputClickCount();
            return;
        }
        int n = ClickTracker.getInputClickCount();
        long l = System.nanoTime();
        for (int i = 0; i < n; ++i) {
            pendingClicks.add(l);
        }
    }

    public static void unused() {
    }

    public static boolean setEnabled(boolean bl) {
        enabled = bl;
        return enabled;
    }

    public static void flush(List<Long> list) {
        while (!pendingClicks.isEmpty()) {
            clicks.add(pendingClicks.removeFirst());
        }
        for (Long l : list) {
            reportedClicks.add(l);
        }
    }
}
