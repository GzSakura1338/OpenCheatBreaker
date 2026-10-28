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
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;

public class NotificationsMod extends AbstractMod {
    public List<NotificationEntry> notificationEntryList = new CopyOnWriteArrayList<NotificationEntry>();
    public long longValue = System.currentTimeMillis();

    public void addNotification(String string, String string2, long l) {
        NotificationType notificationType;
        ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
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
        int n = ((NotificationEntry)object).targetY - ((NotificationEntry)object).height - 2;
        for (int i = this.notificationEntryList.size() - 1; i >= 0; --i) {
            NotificationEntry notificationEntry = this.notificationEntryList.get(i);
            notificationEntry.animationTicks = 0;
            notificationEntry.targetY = n;
            n -= 2 + notificationEntry.height;
        }
        this.notificationEntryList.add((NotificationEntry)object);
    }

    public void onClientTickEvent(ClientTickEvent clientTickEvent) {
        for (NotificationEntry notificationEntry : this.notificationEntryList) {
            notificationEntry.animate();
            long l = notificationEntry.createdAt + notificationEntry.duration - System.currentTimeMillis();
            if (l > 0L) continue;
            int n = notificationEntry.currentY;
            for (NotificationEntry notificationEntry2 : this.notificationEntryList) {
                if (notificationEntry2.currentY >= notificationEntry.currentY) continue;
                notificationEntry2.animationTicks = 0;
                notificationEntry2.targetY = n;
                n = notificationEntry2.currentY;
            }
            this.notificationEntryList.remove(notificationEntry);
        }
    }

    public NotificationsMod() throws java.io.IOException {
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

    public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
        for (NotificationEntry notificationEntry : this.notificationEntryList) {
            notificationEntry.render(scaledResolutionEvent3.getScaledResolution().getScaledWidth());
        }
    }

    public class NotificationEntry {
        public long createdAt = System.currentTimeMillis();
        public int animationTicks = 0;
        public NotificationsMod notificationsMod;
        public NotificationType notificationType;
        public String message;
        public /* synthetic */ NotificationsMod this$0;
        public long duration;
        public int height;
        public int targetY;
        public int currentY;

        public NotificationEntry(NotificationsMod notificationsMod, NotificationsMod notificationsMod2, ScaledResolution scaledResolution, NotificationType notificationType, String string, long l) {
            this.this$0 = notificationsMod;
            this.notificationsMod = notificationsMod2;
            this.notificationType = notificationType;
            this.message = string;
            this.duration = l;
            this.height = notificationType == NotificationType.NEUTRAL ? 16 : 20;
            this.targetY = scaledResolution.getScaledHeight() - 14 - this.height;
            this.currentY = scaledResolution.getScaledHeight() + this.height;
        }

        public void render(int n) {
            CustomFontRenderer customFontRenderer = CheatBreaker.getInstance().getFontManager().playRegular16;
            int n2 = this.currentY;
            float f = customFontRenderer.getStringWidth(this.message);
            int n3 = (int)(this.notificationType == NotificationType.NEUTRAL ? f + 10.0f : f + 30.0f);
            Gui.drawRect(n - 5 - n3, n2, n - 5, n2 + this.height, -1358954496);
            switch (this.notificationType) {
                case ERROR: {
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    RenderUtils.drawSquareTextureViaRenderEngine(new ResourceLocation("client/icons/error-64.png"), 6.0f, n - 10 - n3 + 9, n2 + 4);
                    Gui.drawRect((float)(n - 10) - f - 4.5f, n2 + 4, (float)(n - 10) - f - 4.0f, n2 + this.height - 4, -1342177281);
                    break;
                }
                case INFO: {
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 0.65f);
                    RenderUtils.drawSquareTextureViaRenderEngine(new ResourceLocation("client/icons/info-64.png"), 6.0f, n - 10 - n3 + 9, n2 + 4);
                    Gui.drawRect((float)(n - 10) - f - 4.5f, n2 + 4, (float)(n - 10) - f - 4.0f, n2 + this.height - 4, -1342177281);
                }
            }
            long l = this.duration - (this.createdAt + this.duration - System.currentTimeMillis());
            if (l > this.duration) {
                l = this.duration;
            }
            if (l < 0L) {
                l = 0L;
            }
            float f2 = f * ((float)l / (float)this.duration * 100.0f / 100.0f);
            Gui.drawRect((float)(n - 10) - f, (float)(n2 + this.height) - 4.4f, (float)(n - 10) - f + f, n2 + this.height - 4, 0x30666666);
            Gui.drawRect((float)(n - 10) - f, (float)(n2 + this.height) - 4.4f, (float)(n - 10) - f + f2, n2 + this.height - 4, -1878982912);
            customFontRenderer.drawString(this.message, (float)(n - 10) - f, n2 + (this.notificationType == NotificationType.NEUTRAL ? 2 : 4), -1);
        }

        public void animate() {
            if (this.targetY != -1) {
                ++this.animationTicks;
                float f = (float)this.animationTicks * ((float)this.animationTicks / 5.0f) / 7.0f;
                if (this.currentY > this.targetY) {
                    if ((float)this.currentY - f < (float)this.targetY) {
                        this.currentY = this.targetY;
                        this.targetY = -1;
                    } else {
                        this.currentY = (int)((float)this.currentY - f);
                    }
                } else if (this.currentY < this.targetY) {
                    if ((float)this.currentY + f > (float)this.targetY) {
                        this.currentY = this.targetY;
                        this.targetY = -1;
                    } else {
                        this.currentY = (int)((float)this.currentY + f);
                    }
                } else if (this.currentY == this.targetY) {
                    this.targetY = -1;
                }
            }
        }
    }
}
