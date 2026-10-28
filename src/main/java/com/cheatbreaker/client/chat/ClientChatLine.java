package com.cheatbreaker.client.chat;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.IChatComponent;

public class ClientChatLine extends ChatLine {
    public boolean highlighted;
    public boolean hasIcon;

    public static ClientChatLine from(ChatLine chatLine) {
        return new ClientChatLine(chatLine.getUpdatedCounter(), chatLine.getChatComponent(), chatLine.getChatLineID());
    }

    public void setHasIcon(boolean flag) {
        this.hasIcon = flag;
    }

    public ClientChatLine(int n, IChatComponent chatComponent, int n2) {
        super(n, chatComponent, n2);
    }

    public boolean hasIcon() {
        return this.hasIcon;
    }

    public boolean isHighlighted() {
        return this.highlighted;
    }

    public void setHighlighted(boolean flag) {
        this.highlighted = flag;
    }
}
