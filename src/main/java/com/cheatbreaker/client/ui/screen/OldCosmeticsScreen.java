package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.cosmetic.Cosmetic;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.component.CosmeticItemComponent;
import com.cheatbreaker.client.util.ClientValidator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;

public class OldCosmeticsScreen extends LegacyMainMenuScreen {
    public List list = new ArrayList();
    public ResourceLocation leftPng = new ResourceLocation("client/icons/left.png");
    public ResourceLocation rightPng = new ResourceLocation("client/icons/right.png");
    public int intValue = 0;

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        super.mouseClicked(n, n2, n3);
        if (this.list.size() > 5) {
            boolean flag = n > this.width / 2 - 40 && n < this.width / 2 - 1 && n2 > this.height / 2 + 80 && n2 < this.height / 2 + 100;
            boolean flag2 = n > this.width / 2 + 1 && n < this.width / 2 + 40 && n2 > this.height / 2 + 80 && n2 < this.height / 2 + 100;
            if (this.intValue > 0 && flag) {
                this.intValue--;
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            } else if (flag2 && this.intValue + 1 < this.list.size() / 5.0F) {
                this.intValue++;
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            }
        }

        int n4 = 0;

        for (Object obj : this.list) {
            n4++;
            if (n4 - 1 >= this.intValue * 5 && n4 - 1 < (this.intValue + 1) * 5) {
                ((CosmeticItemComponent)obj).mouseClicked(n, n2, n3);
            }
        }
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        super.drawScreen(n, n2, f);
        if (!CheatBreaker.getInstance().isWebsocketConnected()) {
            ClientValidator.drawMessage(this.width / 2, this.height / 2 - 10);
        } else {
            RenderUtils.drawRoundedRectFilledPrecise(this.width / 2 - 80, this.height / 2 - 78, this.width / 2 + 80, this.height / 2 + 100, 14.0, -1342177281);
            if (this.list.isEmpty()) {
                float f2 = this.width / 2;
                CheatBreaker.getInstance()
                    .getFontManager()
                    .playRegular16
                    .drawCenteredString("You don't own any cosmetics.", f2, this.height / 2 + 4, -6381922);
            } else {
                float f2 = this.height / 2 - 68;
                float f3 = this.height / 2 + 92;
                float f4 = this.width / 2 + 68;
                float f5 = this.width / 2 + 74;
                CheatBreaker.getInstance()
                    .getFontManager()
                    .playBold18
                    .drawCenteredString("Cosmetics (" + this.list.size() + ")", this.width / 2, this.height / 2 - 90, -1);
                int n3 = 0;
                float f6 = 0.0F;

                for (Object obj : this.list) {
                    CosmeticItemComponent cosmeticItemComponent = (CosmeticItemComponent)obj;
                    n3++;
                    if (n3 - 1 >= this.intValue * 5 && n3 - 1 < (this.intValue + 1) * 5) {
                        cosmeticItemComponent.setBounds(this.width / 2 - 76, (int)(this.height / 2 - 72 + f6), 152, cosmeticItemComponent.getHeight());
                        cosmeticItemComponent.render(n, n2, f);
                        f6 += cosmeticItemComponent.getHeight();
                    }
                }

                if (this.list.size() > 5) {
                    boolean flag = n > this.width / 2 - 40 && n < this.width / 2 - 1 && n2 > this.height / 2 + 80 && n2 < this.height / 2 + 100;
                    GlStateManager.color(0.0F, 0.0F, 0.0F, flag ? 0.45F : 0.25F);
                    RenderUtils.drawSquareTextureViaRenderEngine(this.leftPng, 4.0F, this.width / 2 - 10, this.height / 2 + 84);
                    boolean flag2 = n > this.width / 2 + 1 && n < this.width / 2 + 40 && n2 > this.height / 2 + 80 && n2 < this.height / 2 + 100;
                    GlStateManager.color(0.0F, 0.0F, 0.0F, flag2 ? 0.45F : 0.25F);
                    RenderUtils.drawSquareTextureViaRenderEngine(this.rightPng, 4.0F, this.width / 2 + 10, this.height / 2 + 84);
                }
            }
        }

        this.drawButtons(n, n2);
    }

    public OldCosmeticsScreen() {
        for (Cosmetic cosmetic : CheatBreaker.getInstance().getCosmeticsManager().getList()) {
            this.list.add(new CosmeticItemComponent(cosmetic, 1.0F));
        }
    }
}
