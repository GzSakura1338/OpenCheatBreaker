package net.optifine;

public class WrUpdateControl implements IWrUpdateControl {
    public boolean hasForge = Reflector.ForgeHooksClient.exists();
    public int renderPass = 0;

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    public void setRenderPass(int n) {
        this.renderPass = n;
    }
}
