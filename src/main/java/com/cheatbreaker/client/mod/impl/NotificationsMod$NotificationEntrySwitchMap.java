package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.notification.NotificationType;

public class NotificationsMod$NotificationEntrySwitchMap {
    public static /* synthetic */ int[] switchMapNotificationType;

    static {
        switchMapNotificationType = new int[NotificationType.values().length];
        try {
            NotificationsMod$NotificationEntrySwitchMap.switchMapNotificationType[NotificationType.ERROR.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            NotificationsMod$NotificationEntrySwitchMap.switchMapNotificationType[NotificationType.INFO.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
