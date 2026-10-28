package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.network.packet.MessagePacket;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.component.DraggableElement;
import com.cheatbreaker.client.ui.component.ScrollBar;
import com.cheatbreaker.client.ui.component.SectionHeader;
import com.cheatbreaker.client.ui.component.TextField;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.sun.javafx.geom.Vec2d;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ConsoleWindow extends DraggableElement {
    public List<String> stringList = new ArrayList<String>();
    public int intValue = 0;
    public AtomicBoolean atomicBoolean = new AtomicBoolean();
    public Vec2d dragStart = new Vec2d();
    public float startHeight;
    public float startX;
    public int dragEdges = 0;
    public float startWidth;
    public TextField textField;
    public ScrollBar scrollBar;
    public SectionHeader send;
    public float startY;
    public SectionHeader sectionHeader;

    @Override
    public void keyTyped(char c, int n) {
        if (this.textField.isFocused() && !this.textField.getText().equals("") && n == 28) {
            this.executeCommand();
        }
        try {
            if (this.textField.isFocused() && n == 200 && this.stringList.size() != 0) {
                if (this.intValue != 0) {
                    --this.intValue;
                }
                this.textField.setText(this.stringList.get(this.intValue));
            } else if (this.textField.isFocused() && n == 208 && this.stringList.size() != 0) {
                if (this.intValue != this.stringList.size() - 1) {
                    ++this.intValue;
                }
                this.textField.setText(this.stringList.get(this.intValue));
            }
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            this.intValue = 0;
        }
        this.textField.keyTyped(c, n);
        this.send.keyTyped(c, n);
        this.scrollBar.keyTyped(c, n);
        this.sectionHeader.keyTyped(c, n);
    }

    public void updateResize(float f, float f2) {
        if (this.atomicBoolean.get()) {
            float f3;
            if (!Mouse.isButtonDown((int)0)) {
                this.atomicBoolean.set(false);
                return;
            }
            float f4 = f - (float)this.dragStart.x;
            float f5 = f2 - (float)this.dragStart.y;
            float f6 = this.startX;
            float f7 = this.startY;
            float f8 = this.startWidth;
            float f9 = this.startHeight;
            if ((this.dragEdges & 1) != 0) {
                f3 = this.startWidth - 200.0f;
                f4 = Math.min(f4, f3);
                f6 += f4;
                f8 -= f4;
            } else if ((this.dragEdges & 2) != 0) {
                f8 += f4;
            }
            if ((this.dragEdges & 4) != 0) {
                f3 = this.startHeight - 100.0f;
                f5 = Math.min(f5, f3);
                f7 += f5;
                f9 -= f5;
            } else if ((this.dragEdges & 8) != 0) {
                f9 += f5;
            }
            f8 = Math.max(f8, 200.0f);
            f9 = Math.max(f9, 100.0f);
            this.setBounds(f6, f7, f8, f9);
        }
    }

    @Override
    public boolean mouseReleased(float f, float f2, int n, boolean bl) {
        if (!bl) {
            return false;
        }
        this.textField.mouseReleased(f, f2, n, bl);
        this.send.mouseReleased(f, f2, n, bl);
        this.scrollBar.mouseReleased(f, f2, n, bl);
        this.sectionHeader.mouseReleased(f, f2, n, bl);
        return false;
    }

    @Override
    public void tick() {
        this.textField.tick();
        this.send.tick();
        this.scrollBar.tick();
        this.sectionHeader.tick();
    }

    @Override
    public void onClose() {
        this.textField.onClose();
        this.send.onClose();
        this.scrollBar.onClose();
        this.sectionHeader.onClose();
    }

    @Override
    public boolean mouseClicked(float f, float f2, int n, boolean bl) throws java.io.IOException {
        boolean bl2;
        this.textField.mouseClicked(f, f2, n, bl);
        this.scrollBar.mouseClicked(f, f2, n, bl);
        if (!bl) {
            return false;
        }
        if (!this.textField.getText().equals("") && this.send.containsPoint(f, f2)) {
            this.executeCommand();
        }
        this.send.mouseClicked(f, f2, n, bl);
        int n2 = 4;
        boolean bl3 = f >= this.x && f <= this.x + (float)n2;
        boolean bl4 = f >= this.x + this.width - (float)n2 && f <= this.x + this.width;
        boolean bl5 = f2 >= this.y && f2 <= this.y + (float)n2;
        boolean bl6 = f2 >= this.y + this.height - (float)n2 && f2 <= this.y + this.height;
        int n3 = 0;
        if (bl3 && bl5) {
            n3 = 5;
        } else if (bl4 && bl5) {
            n3 = 6;
        } else if (bl3 && bl6) {
            n3 = 9;
        } else if (bl4 && bl6) {
            n3 = 10;
        }
        boolean bl7 = bl2 = this.textField.containsPoint(f, f2) || this.send.containsPoint(f, f2) || this.sectionHeader.containsPoint(f, f2);
        if (n3 != 0 && n == 0) {
            this.atomicBoolean.set(true);
            this.dragEdges = n3;
            this.dragStart.set(f, f2);
            this.startX = this.x;
            this.startY = this.y;
            this.startWidth = this.width;
            this.startHeight = this.height;
            return true;
        }
        if (this.containsPoint(f, f2) && f2 < this.y + 12.0f) {
            this.startDrag(f, f2);
        }
        if (this.sectionHeader.containsPoint(f, f2)) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
            FriendsScreen.getFriendsScreen().removeElements(this);
            return true;
        }
        return false;
    }

    @Override
    public void handleMouseInput() {
        this.scrollBar.handleMouseInput();
    }

    public ConsoleWindow() {
        this.textField = new TextField(CheatBreaker.getInstance().getFontManager().playRegular14, "", 0x2FFFFFFF, 0x6FFFFFFF);
        this.textField.setMaxStringLength(256);
        this.scrollBar = new ScrollBar(this);
        this.send = new SectionHeader("SEND");
        this.sectionHeader = new SectionHeader("X");
    }

    @Override
    public void setBounds(float f, float f2, float f3, float f4) {
        super.setBounds(f, f2, f3, f4);
        this.textField.setBounds(f + 2.0f, f2 + f4 - 15.0f, f3 - 40.0f, 13.0f);
        this.scrollBar.setBounds(f + f3 - 6.0f, f2 + 12.0f + 3.0f, 4.0f, f4 - 32.0f);
        this.send.setBounds(f + f3 - 37.0f, f2 + f4 - 15.0f, 35.0f, 13.0f);
        this.sectionHeader.setBounds(f + f3 - 12.0f, f2 + 2.0f, 10.0f, 10.0f);
    }

    @Override
    public void render(float f, float f2, boolean bl) {
        this.updateDrag(f, f2);
        this.updateResize(f, f2);
        Gui.drawRectWithOutsetBorder(this.x, this.y, this.x + this.width, this.y + this.height, 0.5f, -16777216, -15395563);
        GlStateManager.pushMatrix();
        Gui.drawRect(this.x, this.y - 0.5f, this.x + this.width, this.y, -1357572843);
        Gui.drawRect(this.x, this.y + this.height, this.x + this.width, this.y + this.height + 0.5f, -1357572843);
        CheatBreaker.getInstance().getFontManager().playRegular14.drawString("Console", this.x + 4.0f, this.y + 3.0f, -1);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        Gui.drawRect(this.x + 2.0f, this.y + 12.0f + 3.0f, this.x + this.width - 2.0f, this.y + this.height - 17.0f, -1356783327);
        this.scrollBar.scrollUp(f, f2, bl);
        try {
            if (CheatBreaker.getInstance().getWebsocketController().isFlag()) {
                GlStateManager.pushMatrix();
                GL11.glEnable((int)3089);
                FriendsScreen friendsScreen = FriendsScreen.getFriendsScreen();
                RenderUtils.scissorScaled((int)(this.x + 2.0f), (int)(this.y + 12.0f + 3.0f), (int)(this.x + this.width - 2.0f), (int)(this.y + this.height - 17.0f), (int)((float)friendsScreen.getScaledResolution().getScaleFactor() * friendsScreen.getUiScale()), (int)friendsScreen.getScaledHeight());
                List<String> list = CheatBreaker.getInstance().getServerMessages();
                int n = 0;
                for (int i = list.size() - 1; i >= 0; --i) {
                    String string = list.get(i);
                    String[] stringArray = CheatBreaker.getInstance().getFontManager().playRegular14.wrapText(string, this.width - 10.0f).split("\n");
                    n += stringArray.length * 10;
                    int n2 = 0;
                    for (String string2 : stringArray) {
                        CheatBreaker.getInstance().getFontManager().playRegular14.drawString(string2, this.x + 6.0f, this.y + this.height - 19.0f - (float)n + (float)(n2 * 10), -1);
                        ++n2;
                    }
                }
                this.scrollBar.setContentHeight(n + 4);
                GL11.glDisable((int)3089);
                GlStateManager.popMatrix();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        this.scrollBar.endScrollRegion(f, f2, bl);
        GlStateManager.popMatrix();
        this.textField.renderAndTestHover(f, f2, bl);
        this.send.renderAndTestHover(f, f2, bl);
        this.sectionHeader.renderAndTestHover(f, f2, bl);
    }

    public void executeCommand() {
        String string = this.textField.getText();
        if (string.equals("clear") || string.equals("cls")) {
            CheatBreaker.getInstance().getServerMessages().clear();
            CheatBreaker.getInstance().getKeybindSettings().firstLaunch = false;
        } else if (string.equalsIgnoreCase("wsReconnect") && CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().isClosed()) {
            try {
                CheatBreaker.getInstance().getWebsocketController().connect();
            }
            catch (URISyntaxException uRISyntaxException) {
                CheatBreaker.getInstance().getServerMessages().add("Invalid URL: " + uRISyntaxException.getInput());
                uRISyntaxException.printStackTrace();
            }
        } else {
            CheatBreaker.getInstance().getServerMessages().add((Object)((Object)EnumChatFormatting.GRAY) + "> " + string);
            CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new MessagePacket(string));
            this.stringList.add(string);
            this.intValue = this.stringList.size();
        }
        this.textField.setText("");
        this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0f));
    }

    @Override
    public boolean mouseClickedFallback(float f, float f2, int n) {
        if (!this.textField.containsPoint(f, f2) && this.textField.isFocused()) {
            this.textField.setFocused(false);
        }
        return false;
    }

    public boolean isFocused() {
        return this.textField.isFocused();
    }
}
