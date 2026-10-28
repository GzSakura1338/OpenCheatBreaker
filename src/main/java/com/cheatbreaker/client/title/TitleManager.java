package com.cheatbreaker.client.title;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.mod.impl.TitleType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class TitleManager {
    public Minecraft mc = Minecraft.getMinecraft();
    public ObjectArrayList<Title> objectArrayList = new ObjectArrayList();

    public void onClientTickEvent(ClientTickEvent clientTickEvent) {
        if (!this.objectArrayList.isEmpty()) {
            this.objectArrayList.removeIf(title -> title.createdAt + Title.getDisplayTime(title) < System.currentTimeMillis());
        }
    }

    public void render(ScaledResolutionEvent3 scaledResolutionEvent3, Title title) {
        boolean flag = Title.getType(title) == TitleType.SUBTITLE;
        float f = flag ? 4.0F : 1.5F;
        float f2 = flag ? -30.0F : 10.0F;
        float f3;
        GL11.glScalef(f3 = f * Title.getScale(title), f3, f3);
        float alpha = this.getAlpha(title);
        this.mc
            .fontRendererObj
            .drawStringWithShadow(
                Title.getMessage(title),
                (int)(scaledResolutionEvent3.getScaledResolution().getScaledWidth() / 2 / f3),
                (int)((scaledResolutionEvent3.getScaledResolution().getScaledHeight() / 2 - this.mc.fontRendererObj.FONT_HEIGHT / 2 + f2) / f3),
                new Color(1.0F, 1.0F, 1.0F, alpha).getRGB()
            );
        GL11.glScalef(1.0F / f3, 1.0F / f3, 1.0F / f3);
    }

    public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
        if (!this.objectArrayList.isEmpty()) {
            GlStateManager.enableBlend();

            try {
                ObjectListIterator objectListIterator = this.objectArrayList.iterator();

                while (objectListIterator.hasNext()) {
                    Title title = (Title)objectListIterator.next();
                    this.render(scaledResolutionEvent3, title);
                }
            } finally {
                GlStateManager.disableBlend();
            }
        }
    }

    public float getAlpha(Title title) {
        float f = 255.0F;
        if (title.isFadingIn()) {
            long l = Title.getFadeInTime(title) - (System.currentTimeMillis() - title.createdAt);
            f = 1.0F - (float)l / (float)Title.getFadeInTime(title);
        } else if (title.isFadingOut()) {
            long l = Title.getDisplayTime(title) - (System.currentTimeMillis() - title.createdAt);
            f = (float)l <= 0.0F ? 0.0F : (float)l / (float)Title.getFadeOutTime(title);
        }

        return Math.max(0.15F, Math.min(1.0F, f));
    }

    public ObjectArrayList<Title> getObjectArrayList() {
        return this.objectArrayList;
    }
}
