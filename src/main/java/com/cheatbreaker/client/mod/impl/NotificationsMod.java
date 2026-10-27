package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.SimpleEvent12;
import com.cheatbreaker.client.event.SimpleEvent20;
import com.cheatbreaker.client.font.CustomFontRenderer;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.notification.NotificationType;
import com.cheatbreaker.client.render.RenderUtils;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class NotificationsMod
extends AbstractMod {
    public List<NotificationEntry> notificationEntryList = new CopyOnWriteArrayList<NotificationEntry>();
    public long longValue = System.currentTimeMillis();

    public void onClientTickEvent(ClientTickEvent clientTickEvent) {
        for (NotificationEntry notificationEntry : this.notificationEntryList) {
            notificationEntry.run();
            long l = notificationEntry.longValue + notificationEntry.longValue2 - System.currentTimeMillis();
            if (l > 0L) continue;
            int n = notificationEntry.intValue4;
            for (NotificationEntry notificationEntry2 : this.notificationEntryList) {
                if (notificationEntry2.intValue4 >= notificationEntry.intValue4) continue;
                notificationEntry2.intValue = 0;
                notificationEntry2.intValue3 = n;
                n = notificationEntry2.intValue4;
            }
            this.notificationEntryList.remove(notificationEntry);
        }
    }

    public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
        for (NotificationEntry notificationEntry : this.notificationEntryList) {
            notificationEntry.render(scaledResolutionEvent3.getScaledResolution().getScaledWidth());
        }
    }

    public void handleString(String string, String string2, long l) {
        NotificationType notificationType;
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        if (l < 2000L) {
            l = 2000L;
        }
        string2 = string2.replaceAll("&([abcdefghijklmrABCDEFGHIJKLMNR0-9])|(&$)", "\u00a7$1");
        switch (string.toLowerCase()) {
            case "info": {
                notificationType = NotificationType.INFO;
                break;
            }
            case "error": {
                notificationType = NotificationType.ERROR;
                break;
            }
            default: {
                notificationType = NotificationType.NEUTRAL;
            }
        }
        Object object = new NotificationEntry(this, this, scaledResolution, notificationType, string2, l);
        int n = ((NotificationEntry)object).intValue3 - ((NotificationEntry)object).intValue2 - 2;
        for (int i = this.notificationEntryList.size() - 1; i >= 0; --i) {
            NotificationEntry notificationEntry = this.notificationEntryList.get(i);
            notificationEntry.intValue = 0;
            notificationEntry.intValue3 = n;
            n -= 2 + notificationEntry.intValue2;
        }
        this.notificationEntryList.add((NotificationEntry)object);
    }

    public NotificationsMod() {
        super("Notifications");
        this.setCategories(ModCategory.HUD);
        this.addEventHandler(SimpleEvent12.class, simpleEvent12 -> {
            this.longValue = System.currentTimeMillis();
        });
        this.addEventHandler(SimpleEvent20.class, simpleEvent20 -> {
            this.longValue = System.currentTimeMillis();
        });
        this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent);
        this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
        this.setCanToggle(false);
        this.setEnabledByDefault(true);
    }

    public class NotificationEntry {
        public long longValue = System.currentTimeMillis();
        public int intValue = 0;
        public NotificationsMod notificationsMod;
        public NotificationType notificationType;
        public String text;
        public /* synthetic */ NotificationsMod this$0;
        public long longValue2;
        public int intValue2;
        public int intValue3;
        public int intValue4;

        public void run() {
            if (this.intValue3 != -1) {
                ++this.intValue;
                float f = (float)this.intValue * ((float)this.intValue / 5.0f) / 7.0f;
                if (this.intValue4 > this.intValue3) {
                    if ((float)this.intValue4 - f < (float)this.intValue3) {
                        this.intValue4 = this.intValue3;
                        this.intValue3 = -1;
                    } else {
                        this.intValue4 = (int)((float)this.intValue4 - f);
                    }
                } else if (this.intValue4 < this.intValue3) {
                    if ((float)this.intValue4 + f > (float)this.intValue3) {
                        this.intValue4 = this.intValue3;
                        this.intValue3 = -1;
                    } else {
                        this.intValue4 = (int)((float)this.intValue4 + f);
                    }
                } else if (this.intValue4 == this.intValue3) {
                    this.intValue3 = -1;
                }
            }
        }

        public NotificationEntry(NotificationsMod notificationsMod, NotificationsMod notificationsMod2, ScaledResolution scaledResolution, NotificationType notificationType, String string, long l) {
            this.this$0 = notificationsMod;
            this.notificationsMod = notificationsMod2;
            this.notificationType = notificationType;
            this.text = string;
            this.longValue2 = l;
            this.intValue2 = notificationType == NotificationType.NEUTRAL ? 16 : 20;
            this.intValue3 = scaledResolution.getScaledHeight() - 14 - this.intValue2;
            this.intValue4 = scaledResolution.getScaledHeight() + this.intValue2;
        }

        public void render(int n) {
            CustomFontRenderer customFontRenderer = CheatBreaker.getInstance().getFontManager().playRegular16;
            int n2 = this.intValue4;
            float f = customFontRenderer.getStringWidth(this.text);
            int n3 = (int)(this.notificationType == NotificationType.NEUTRAL ? f + 10.0f : f + 30.0f);
            Gui.drawRect(n - 5 - n3, n2, n - 5, n2 + this.intValue2, -1358954496);
            switch (this.notificationType) {
                case ERROR: {
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    RenderUtils.drawSquareTextureViaRenderEngine(new ResourceLocation("client/icons/error-64.png"), 6.0f, n - 10 - n3 + 9, n2 + 4);
                    Gui.drawRect((float)(n - 10) - f - 4.5f, (float)(n2 + 4), (float)(n - 10) - f - 4.0f, (float)(n2 + this.intValue2 - 4), -1342177281);
                    break;
                }
                case INFO: {
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 0.65f);
                    RenderUtils.drawSquareTextureViaRenderEngine(new ResourceLocation("client/icons/info-64.png"), 6.0f, n - 10 - n3 + 9, n2 + 4);
                    Gui.drawRect((float)(n - 10) - f - 4.5f, (float)(n2 + 4), (float)(n - 10) - f - 4.0f, (float)(n2 + this.intValue2 - 4), -1342177281);
                }
            }
            long l = this.longValue2 - (this.longValue + this.longValue2 - System.currentTimeMillis());
            if (l > this.longValue2) {
                l = this.longValue2;
            }
            if (l < 0L) {
                l = 0L;
            }
            float f2 = f * ((float)l / (float)this.longValue2 * 100.0f / 100.0f);
            Gui.drawRect((float)(n - 10) - f, (float)(n2 + this.intValue2) - 4.4f, (float)(n - 10) - f + f, (float)(n2 + this.intValue2 - 4), 0x30666666);
            Gui.drawRect((float)(n - 10) - f, (float)(n2 + this.intValue2) - 4.4f, (float)(n - 10) - f + f2, (float)(n2 + this.intValue2 - 4), -1878982912);
            customFontRenderer.drawString(this.text, (float)(n - 10) - f, n2 + (this.notificationType == NotificationType.NEUTRAL ? 2 : 4), -1);
        }
    }
}
