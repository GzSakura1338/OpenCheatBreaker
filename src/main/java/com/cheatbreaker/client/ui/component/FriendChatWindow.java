package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.friends.Friend;
import com.cheatbreaker.client.network.TimedEntry;
import com.cheatbreaker.client.network.packet.FriendMessagePacket;
import com.cheatbreaker.client.network.packet.ToggleEntryPacket;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.cheatbreaker.client.util.SneakyThrow;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class FriendChatWindow extends DraggableElement {
    public final int intValue = 22;
    public Friend friend;
    public TextField message;
    public SectionHeader send;
    public ScrollBar scrollBar;
    public ScrollBar scrollBar2;
    public SectionHeader sectionHeader;

    @Override
    public boolean mouseClickedFallback(float f, float f2, int n) {
        if (!this.message.containsPoint(f, f2) && this.message.isFocused()) {
            this.message.setFocused(false);
        }

        return false;
    }

    @Override
    public boolean mouseReleased(float f, float f2, int n, boolean flag) {
        if (!flag) {
            return false;
        }

        this.message.mouseReleased(f, f2, n, flag);
        this.send.mouseReleased(f, f2, n, flag);
        this.scrollBar.mouseReleased(f, f2, n, flag);
        this.sectionHeader.mouseReleased(f, f2, n, flag);
        return false;
    }

    public FriendChatWindow(Friend friend) {
        this.friend = friend;
        this.message = new TextField(CheatBreaker.getInstance().getFontManager().playRegular14, "Message", 805306367, 1879048191);
        this.message.setMaxStringLength(256);
        this.send = new SectionHeader("SEND");
        this.scrollBar = new ScrollBar(this);
        this.scrollBar2 = new ScrollBar(this);
        this.sectionHeader = new SectionHeader("X");
    }

    @Override
    public void keyTyped(char c, int n) {
        if (this.message.isFocused() && !this.message.getText().equals("") && n == 28) {
            this.sendMessage();
            TimedEntry orElse = this.cheatBreaker
                .getLocalTyping()
                .stream()
                .filter(timedEntry -> timedEntry.getKey().equals(this.friend.getPlayerId()))
                .findFirst()
                .orElse(null);
            if (orElse != null) {
                this.handleTimedEntry(orElse);
            }
        } else if (this.message.isFocused()) {
            if (this.cheatBreaker.getLocalTyping().stream().noneMatch(timedEntry -> timedEntry.getKey().equals(this.friend.getPlayerId()))) {
                this.cheatBreaker.getLocalTyping().add(new TimedEntry(this.friend.getPlayerId(), false));
                this.cheatBreaker.getPlayerassetsCheatbreakerNet().sendPacket(new ToggleEntryPacket(this.friend.getPlayerId(), true));
            } else {
                TimedEntry orElse = this.cheatBreaker
                    .getLocalTyping()
                    .stream()
                    .filter(timedEntry -> timedEntry.getKey().equals(this.friend.getPlayerId()))
                    .findFirst()
                    .orElse(null);
                if (orElse != null) {
                    if (!orElse.isExpired()) {
                        orElse.setRemoveAt(System.currentTimeMillis());
                    } else {
                        this.cheatBreaker.getLocalTyping().remove(orElse);
                        this.cheatBreaker.getPlayerassetsCheatbreakerNet().sendPacket(new ToggleEntryPacket(this.friend.getPlayerId(), false));
                        this.cheatBreaker.getLocalTyping().add(new TimedEntry(this.friend.getPlayerId(), false));
                        this.cheatBreaker.getPlayerassetsCheatbreakerNet().sendPacket(new ToggleEntryPacket(this.friend.getPlayerId(), true));
                    }
                }
            }
        }

        this.message.keyTyped(c, n);
        this.send.keyTyped(c, n);
        this.scrollBar.keyTyped(c, n);
        this.sectionHeader.keyTyped(c, n);
    }

    public void handleTimedEntry(TimedEntry timedEntry) {
        this.cheatBreaker.getLocalTyping().remove(timedEntry);
        this.cheatBreaker.getPlayerassetsCheatbreakerNet().sendPacket(new ToggleEntryPacket(timedEntry.getKey(), false));
    }

    @Override
    public void onClose() {
        this.message.onClose();
        this.send.onClose();
        this.scrollBar.onClose();
        this.sectionHeader.onClose();
    }

    public Friend getFriend() {
        return this.friend;
    }

    @Override
    public boolean mouseClicked(float f, float f2, int n, boolean flag) throws java.io.IOException {
        this.message.mouseClicked(f, f2, n, flag);
        if (!flag) {
            return false;
        }

        if (!this.message.getText().equals("") && this.send.containsPoint(f, f2)) {
            this.sendMessage();
        }

        this.send.mouseClicked(f, f2, n, flag);
        this.scrollBar.mouseClicked(f, f2, n, flag);
        if (this.containsPoint(f, f2) && f2 < this.y + 22.0F) {
            this.startDrag(f, f2);
        }

        if (!this.sectionHeader.containsPoint(f, f2)) {
            return false;
        }

        this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
        FriendsScreen.getFriendsScreen().removeElements(this);

        for (TimedEntry timedEntry : this.cheatBreaker.getLocalTyping()) {
            this.cheatBreaker.getPlayerassetsCheatbreakerNet().sendPacket(new ToggleEntryPacket(timedEntry.getKey(), false));
        }

        return true;
    }

    @Override
    public void handleMouseInput() {
        this.scrollBar.handleMouseInput();
        this.scrollBar2.handleMouseInput();
    }

    public void sendMessage() {
        String text = this.message.getText();
        CheatBreaker.getInstance().getFriendsManager().addLogEntry(this.friend.getPlayerId(), text);
        CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new FriendMessagePacket(this.friend.getPlayerId(), text));
        this.message.setText("");
        this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
    }

    @Override
    public void render(float f, float f2, boolean flag) {
        this.updateDrag(f, f2);
        Gui.drawRectWithOutsetBorder(this.x, this.y, this.x + 23.0F, this.y + this.height, 0.5F, -16777216, -14869219);
        Gui.drawRectWithOutsetBorder(this.x + 23.0F, this.y, this.x + this.width, this.y + this.height, 0.5F, -16777216, -15395563);
        GlStateManager.pushMatrix();
        Gui.drawRect(this.x + 25.0F, this.y - 0.5F, this.x + this.width, this.y, -1357572843);
        Gui.drawRect(this.x + 25.0F, this.y + this.height, this.x + this.width, this.y + this.height + 0.5F, -1357572843);
        Gui.drawRect(
            this.x + 27.0F,
            this.y + 3.0F,
            this.x + 43.0F,
            this.y + 19.0F,
            this.friend.isOnline() ? Friend.getStatusColor(this.friend.getOnlineStatus()) : -13158601
        );
        this.cheatBreaker.getFontManager().playRegular16.drawString(this.friend.getName(), this.x + 52.0F, this.y + 2.0F, -1);
        String str = this.cheatBreaker.getFriendsTyping().stream().anyMatch(timedEntry -> timedEntry.getKey().equals(this.friend.getPlayerId()))
            ? "Typing..."
            : this.friend.getStatusText();
        float f3 = this.x + 52.0F;
        float f4 = this.y + 11.0F;
        this.cheatBreaker.getFontManager().playRegular12.drawString(str, f3, f4, -5460820);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        ResourceLocation headTexture = CheatBreaker.getInstance()
            .getFontManager()
            .getHeadTexture(EnumChatFormatting.getTextWithoutFormattingCodes(this.friend.getName()));
        RenderUtils.drawSquareTextureViaRenderEngine(headTexture, 7.0F, this.x + 28.0F, this.y + 4.0F);
        Gui.drawRect(this.x + 27.0F, this.y + 22.0F, this.x + this.width - 2.0F, this.y + this.height - 17.0F, -1356783327);
        this.scrollBar2.scrollDown(f, f2, flag);
        GlStateManager.pushMatrix();
        GL11.glEnable(3089);
        FriendsScreen friendsScreen = FriendsScreen.getFriendsScreen();
        RenderUtils.scissorScaled(
            0,
            (int)(this.y + 2.0F),
            (int)friendsScreen.getScaledWidth(),
            (int)(this.y + this.height - 2.0F),
            (int)(friendsScreen.getScaledResolution().getScaleFactor() * friendsScreen.getUiScale()),
            (int)friendsScreen.getScaledHeight()
        );
        byte b = 18;
        int n = 0;
        ObjectIterator objectIterator = this.cheatBreaker.getFriendsManager().getFriends().values().iterator();

        while (objectIterator.hasNext()) {
            Friend friend = (Friend)objectIterator.next();
            if (friend == this.friend || this.cheatBreaker.getFriendsManager().getMessageHistory().containsKey(friend.getPlayerId()) || friend.isOnline()) {
                float f5 = this.y + 3.0F + n;
                boolean flag2 = f > this.x
                    && f < this.x + 25.0F
                    && f2 > f5 - this.scrollBar2.getScrollOffset()
                    && f2 < f5 + 16.0F - this.scrollBar2.getScrollOffset()
                    && f2 > this.y
                    && f2 < this.y + this.height;
                Gui.drawRect(this.x + 3.0F, f5, this.x + 19.0F, f5 + 16.0F, friend.isOnline() ? Friend.getStatusColor(friend.getOnlineStatus()) : -13158601);
                GlStateManager.color(1.0F, 1.0F, 1.0F, flag2 ? 1.0F : 0.85F);
                ResourceLocation headTexture2 = CheatBreaker.getInstance()
                    .getFontManager()
                    .getHeadTexture(EnumChatFormatting.getTextWithoutFormattingCodes(friend.getName()));
                RenderUtils.drawSquareTextureViaRenderEngine(headTexture2, 7.0F, this.x + 4.0F, this.y + 4.0F + n);
                if (flag2) {
                    float stringWidth = this.cheatBreaker
                        .getFontManager()
                        .robotoRegular13
                        .getStringWidth(EnumChatFormatting.getTextWithoutFormattingCodes(friend.getName()));
                    RenderUtils.drawRoundedRectFilledPrecise(this.x - 10.0F - stringWidth, f5 + 2.0F, this.x - 2.0F, f5 + 14.0F, 6.0, -1895825408);
                    this.cheatBreaker.getFontManager().robotoRegular13.drawString(friend.getName(), this.x - 6.0F - stringWidth, f5 + 4.0F, -1);
                    if (Mouse.isButtonDown(0) && this.friend != friend) {
                        this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                        this.friend = friend;
                    }
                }

                n += 18;
            }
        }

        this.scrollBar2.setContentHeight(n);
        GL11.glDisable(3089);
        GlStateManager.popMatrix();
        this.scrollBar2.render(f, f2, flag);
        this.scrollBar.scrollUp(f, f2, flag);

        try {
            if (CheatBreaker.getInstance().getFriendsManager().getMessageHistory().containsKey(this.friend.getPlayerId())) {
                GlStateManager.pushMatrix();
                GL11.glEnable(3089);
                RenderUtils.scissorScaled(
                    (int)(this.x + 2.0F),
                    (int)(this.y + 22.0F),
                    (int)(this.x + this.width - 2.0F),
                    (int)(this.y + this.height - 17.0F),
                    (int)(friendsScreen.getScaledResolution().getScaleFactor() * friendsScreen.getUiScale()),
                    (int)friendsScreen.getScaledHeight()
                );
                List list = (List)CheatBreaker.getInstance().getFriendsManager().getMessageHistory().get(this.friend.getPlayerId());
                int n2 = 0;

                for (int i = list.size() - 1; i >= 0; i--) {
                    String str2 = (String)list.get(i);
                    String[] split = CheatBreaker.getInstance().getFontManager().playRegular14.wrapText(str2, this.width - 25.0F).split("\n");
                    n2 += split.length * 10;
                    int n3 = 0;

                    for (String str3 : split) {
                        f3 = this.x + 31.0F;
                        CheatBreaker.getInstance().getFontManager().playRegular14.drawString(str3, f3, this.y + this.height - 19.0F - n2 + n3 * 10, -1);
                        n3++;
                    }
                }

                this.scrollBar.setContentHeight(n2 + 4);
                GL11.glDisable(3089);
                GlStateManager.popMatrix();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.scrollBar.endScrollRegion(f, f2, flag);
        GlStateManager.popMatrix();
        this.message.renderAndTestHover(f, f2, flag);
        this.send.renderAndTestHover(f, f2, flag);
        this.sectionHeader.renderAndTestHover(f, f2, flag);
    }

    @Override
    public void setBounds(float f, float f2, float f3, float f4) {
        super.setBounds(f, f2, f3, f4);
        this.message.setBounds(f + 26.0F, f2 + f4 - 15.0F, f3 - 62.0F, 13.0F);
        this.send.setBounds(f + f3 - 37.0F, f2 + f4 - 15.0F, 35.0F, 13.0F);
        this.scrollBar.setBounds(f + f3 - 6.0F, f2 + 22.0F, 4.0F, f4 - 39.0F);
        this.scrollBar2.setBounds(f + 2.0F, f2 + 2.0F, 0.0F, f4 - 4.0F);
        this.sectionHeader.setBounds(f + f3 - 12.0F, f2 + 2.0F, 10.0F, 16.0F);
    }

    @Override
    public void tick() {
        this.message.tick();
        this.send.tick();
        this.scrollBar.tick();
        this.sectionHeader.tick();
    }

    public static String decrypt(byte[] bytes) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(CheatBreaker.CHAT_AES_KEY, "AES");
            Cipher instance = Cipher.getInstance("AES");
            instance.init(2, secretKeySpec);
            return new String(instance.doFinal(bytes));
        } catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }
}
