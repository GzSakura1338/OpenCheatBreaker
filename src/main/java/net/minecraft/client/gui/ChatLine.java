package net.minecraft.client.gui;

import net.minecraft.util.IChatComponent;

public class ChatLine {
    public IChatComponent iChatComponent;
    public IChatComponent lineString;
    public int updateCounterCreated;
    public int chatLineID;

    public IChatComponent getChatComponent() {
        return this.lineString;
    }

    public ChatLine(int n, IChatComponent chatComponent, int n2) {
        this.lineString = chatComponent;
        this.updateCounterCreated = n;
        this.chatLineID = n2;
    }

    public int getUpdatedCounter() {
        return this.updateCounterCreated;
    }

    public int getChatLineID() {
        return this.chatLineID;
    }

    public IChatComponent getIChatComponent() {
        return this.iChatComponent;
    }

    public ChatLine setIChatComponent(IChatComponent chatComponent) {
        this.iChatComponent = chatComponent;
        return this;
    }
}
