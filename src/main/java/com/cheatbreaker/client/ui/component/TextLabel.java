package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.ColorAnimation;
import com.cheatbreaker.client.util.TextUtils;

public class TextLabel extends UIElement {
    public boolean flag = false;
    public String text;
    public int intValue;
    public ColorAnimation colorAnimation;

    @Override
    public void render(float f, float f2, boolean flag) {
        if (this.flag) {
            String wrapToString = TextUtils.wrapSequence(this.text).setMaxWidth(50).setHyphenate(false).wrapToString();
            String[] split = wrapToString.split("\n");
            int n = -3;

            for (String str : split) {
                CheatBreaker.getInstance()
                    .getFontManager()
                    .playRegular14
                    .drawStringWithOutline(
                        str,
                        this.x,
                        this.y
                            - CheatBreaker.getInstance().getFontManager().playRegular14.getFont().getSize() / 2.0F
                            + n * CheatBreaker.getInstance().getFontManager().playRegular14.getFont().getSize() / 2.0F,
                        this.colorAnimation.getColor(this.containsPoint(f, f2) && flag).getRGB()
                    );
                n++;
            }
        } else {
            double d = this.x;
            double d2 = this.y;
            CheatBreaker.getInstance()
                .getFontManager()
                .playRegular18
                .drawStringWithOutline(this.text, d, d2, this.colorAnimation.getColor(this.containsPoint(f, f2) && flag).getRGB());
        }
    }

    public TextLabel(String str) {
        this.text = str;
        this.colorAnimation = new ColorAnimation(-1879048193, -806424850);
    }

    public TextLabel(String str, int n) {
        this(str);
        this.flag = true;
        this.intValue = n;
    }
}
