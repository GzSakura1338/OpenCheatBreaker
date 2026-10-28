package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.EnumChatFormatting;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class FriendsListPanel extends ListPanel<FriendListEntry> {
    public List<FriendListEntry> friendListEntryList = new ArrayList<>();
    public TextField filter = new TextField(CheatBreaker.getInstance().getFontManager().playRegular14, "Filter", 805306367, 1879048191);
    public ScrollBar scrollBar = new ScrollBar(this);

    public List<FriendListEntry> getFriendListEntryList() {
        return this.friendListEntryList;
    }

    @Override
    public void handleMouseInput() {
        this.scrollBar.handleMouseInput();
    }

    public boolean checkFriendListEntry(FriendListEntry friendListEntry) {
        return this.filter.getText().equals("")
            || EnumChatFormatting.getTextWithoutFormattingCodes(friendListEntry.getFriend().getName()).toLowerCase().startsWith(this.filter.getText().toLowerCase());
    }

    public FriendsListPanel(List<FriendListEntry> list) {
        super(list);
    }

    @Override
    public boolean mouseClicked(float f, float f2, int n, boolean flag) throws java.io.IOException {
        this.filter.mouseClicked(f, f2 - this.scrollBar.getScrollOffset(), n, flag);
        if (this.filter.isFocused() && n == 1 && this.filter.getText().equals("")) {
            this.setBounds();
        }

        if (!flag) {
            return false;
        }

        this.scrollBar.mouseClicked(f, f2, n, flag);
        boolean flag2 = false;

        for (FriendListEntry friendListEntry : this.list) {
            if (this.checkFriendListEntry(friendListEntry)) {
                if (flag2) {
                    break;
                }

                flag2 = friendListEntry.mouseClicked(f, f2 - this.scrollBar.getScrollOffset(), n, flag && !this.scrollBar.containsPoint(f, f2));
            }
        }

        return flag2;
    }

    public void setBounds() {
        this.setBounds(this.x, this.y, this.width, this.height);
    }

    @Override
    public void onClose() {
        this.filter.onClose();
        this.scrollBar.onClose();
    }

    @Override
    public void tick() {
        this.filter.tick();
        this.scrollBar.tick();
    }

    @Override
    public boolean mouseReleased(float f, float f2, int n, boolean flag) {
        this.filter.mouseReleased(f, f2 - this.scrollBar.getScrollOffset(), n, flag);
        this.scrollBar.mouseReleased(f, f2, n, flag);
        if (!flag) {
            return false;
        }

        boolean flag2 = false;

        for (FriendListEntry friendListEntry : this.list) {
            if (this.checkFriendListEntry(friendListEntry)) {
                if (flag2) {
                    break;
                }

                flag2 = friendListEntry.mouseReleased(f, f2, n, flag);
            }
        }

        return flag2;
    }

    @Override
    public void render(float f, float f2, boolean flag) {
        if (!this.friendListEntryList.isEmpty()) {
            this.list.removeAll(this.friendListEntryList);
            FriendsScreen.getFriendsScreen().getFriendsListPanel().setBounds();
        }

        if (!CheatBreaker.getInstance().isWebsocketConnected()) {
            float f3 = this.x + this.width / 2.0F;
            float f4 = this.y + 10.0F;
            CheatBreaker.getInstance().getFontManager().playBold18.drawCenteredString("Connection lost", f3, f4, -1);
            f3 = this.x + this.width / 2.0F;
            f4 = this.y + 22.0F;
            CheatBreaker.getInstance().getFontManager().playRegular14.drawCenteredString("Please try again later.", f3, f4, -1);
        } else {
            GlStateManager.pushMatrix();
            GL11.glEnable(3089);
            FriendsScreen friendsScreen = FriendsScreen.getFriendsScreen();
            this.scrollBar.scrollDown(f, f2, flag);
            RenderUtils.scissorScaled(
                (int)this.x,
                (int)this.y,
                (int)(this.x + this.width),
                (int)(this.y + this.height),
                (int)(friendsScreen.getScaledResolution().getScaleFactor() * friendsScreen.getUiScale()),
                (int)friendsScreen.getScaledHeight()
            );
            ImmutableList copyOf = ImmutableList.copyOf(this.list);
            UnmodifiableIterator unmodifiableIterator = copyOf.iterator();

            while (unmodifiableIterator.hasNext()) {
                FriendListEntry friendListEntry = (FriendListEntry)unmodifiableIterator.next();
                if (this.checkFriendListEntry(friendListEntry)) {
                    friendListEntry.renderAndTestHover(f, f2 - this.scrollBar.getScrollOffset(), flag && !this.scrollBar.containsPoint(f, f2));
                }
            }

            if (copyOf.isEmpty()) {
                float f3 = this.x + this.width / 2.0F;
                float f4 = this.y + 30.0F;
                CheatBreaker.getInstance().getFontManager().playBold18.drawCenteredString("No friends", f3, f4, -1);
            }

            this.filter.renderAndTestHover(f, f2 - this.scrollBar.getScrollOffset(), flag);
            GL11.glDisable(3089);
            GlStateManager.popMatrix();
            this.scrollBar.render(f, f2, flag);
        }
    }

    @Override
    public void setBounds(float f, float f2, float f3, float f4) {
        super.setBounds(f, f2, f3, f4);
        this.filter.setBounds(0.0F, f2, f3, 13.0F);
        this.scrollBar.setBounds(f + f3 - 4.0F, f2, 4.0F, f4);
        this.list.sort((friendListEntry, friendListEntry2) -> {
            String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(friendListEntry.getFriend().getName());
            String textWithoutFormattingCodes2 = EnumChatFormatting.getTextWithoutFormattingCodes(friendListEntry2.getFriend().getName());
            if (friendListEntry.getFriend().isOnline() == friendListEntry2.getFriend().isOnline()) {
                return textWithoutFormattingCodes.compareTo(textWithoutFormattingCodes2);
            } else {
                return friendListEntry.getFriend().isOnline() ? -1 : 1;
            }
        });
        boolean flag = true;
        int n = 0;

        for (FriendListEntry friendListEntry : this.list) {
            if (this.checkFriendListEntry(friendListEntry)) {
                friendListEntry.setBounds(f, f2 + 14.0F + n * 22, f3, 22.0F);
                n++;
            }
        }

        this.scrollBar.setContentHeight(14.0F + this.list.size() * 22.0F);
    }

    @Override
    public void keyTyped(char c, int n) {
        super.keyTyped(c, n);
        this.filter.keyTyped(c, n);
        this.scrollBar.keyTyped(c, n);
        this.setBounds(this.x, this.y, this.width, this.height);
    }
}
