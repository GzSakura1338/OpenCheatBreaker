package shadersmod.client;

import java.util.ArrayList;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.optifine.Lang;

public class GuiSlotShaders extends GuiSlot {
    public ArrayList shaderslist;
    public int selectedIndex;
    public long lastClickedCached = 0L;
    public GuiShaders shadersGui;

    public GuiSlotShaders(GuiShaders guiShaders, int n, int n2, int n3, int n4, int n5) {
        super(guiShaders.getMc(), n, n2, n3, n4, n5);
        this.shadersGui = guiShaders;
        this.updateList();
        int n6 = this.selectedIndex * n5;
        int n7 = (n4 - n3) / 2;
        if (n6 > n7) {
            this.scrollBy(n6 - n7);
        }
    }

    @Override
    public int getListWidth() {
        return this.width - 20;
    }

    public void updateList() {
        this.shaderslist = Shaders.listOfShaders();
        this.selectedIndex = 0;
        int n = 0;

        for (int size = this.shaderslist.size(); n < size; n++) {
            if (((String)this.shaderslist.get(n)).equals(Shaders.currentshadername)) {
                this.selectedIndex = n;
                break;
            }
        }
    }

    @Override
    public int getSize() {
        return this.shaderslist.size();
    }

    @Override
    public void elementClicked(int n, boolean flag, int n2, int n3) {
        if (n != this.selectedIndex || this.lastClickedCached != System.currentTimeMillis()) {
            this.selectedIndex = n;
            this.lastClickedCached = System.currentTimeMillis();
            Shaders.setShaderPack((String)this.shaderslist.get(n));
            Shaders.uninit();
            this.shadersGui.updateButtons();
        }
    }

    @Override
    public boolean isSelected(int n) {
        return n == this.selectedIndex;
    }

    @Override
    public int getScrollBarX() {
        return this.width - 6;
    }

    @Override
    public int getContentHeight() {
        return this.getSize() * 18;
    }

    @Override
    public void drawBackground() {
    }

    @Override
    public void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6) {
        String str = (String)this.shaderslist.get(n);
        if (str.equals(Shaders.packNameNone)) {
            str = Lang.get("of.options.shaders.packNone");
        } else if (str.equals(Shaders.packNameDefault)) {
            str = Lang.get("of.options.shaders.packDefault");
        }

        this.shadersGui.drawCenteredString(str, this.width / 2, n3 + 1, 14737632);
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }
}
