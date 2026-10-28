package com.cheatbreaker.client.ui.component;

public class SettingControl extends SettingComponent {
    public IconComponent resetCog64Png;

    public SettingControl(float f) {
        super(f);
        this.resetCog64Png = new IconComponent(null, "reset-cog-64.png", this.x - 10, this.y - this.height / 2, 20, 10, f);
        this.resetCog64Png.setSmall(true);
    }

    @Override
    public void render(int n, int n2, float f) {
        this.renderSettingLabel(this.setting, n, n2);
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) {
    }
}
