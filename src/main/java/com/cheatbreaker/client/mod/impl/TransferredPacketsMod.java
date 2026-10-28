package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.CBPacketEvent1;
import com.cheatbreaker.client.event.CBPacketEvent2;
import com.cheatbreaker.client.mod.AbstractInternalMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.nethandler.CBPacket;
import com.google.gson.Gson;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class TransferredPacketsMod extends AbstractInternalMod {
    public void onCBPacketEvent2(CBPacketEvent2 cbpacketEvent2) {
        this.logPacket("Sent: ", cbpacketEvent2.getCbpacket());
    }

    public TransferredPacketsMod() throws java.io.IOException {
        super("Transferred Packets");
        this.setDescription("Sends chat messages about sent and received CheatBreaker packets.");
        this.setCategories(ModCategory.SERVER);
        this.addEventHandler(CBPacketEvent1.class, this::onCBPacketEvent1);
        this.addEventHandler(CBPacketEvent2.class, this::onCBPacketEvent2);
        this.setEnabledByDefault(true);
    }

    public void logPacket(String str, CBPacket cbpacket) {
        ChatComponentText chatComponentText = new ChatComponentText(EnumChatFormatting.GRAY + str + EnumChatFormatting.WHITE + cbpacket.getClass().getSimpleName());
        chatComponentText.getChatStyle().setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ChatComponentText(new Gson().toJson(cbpacket))));
        chatComponentText.setFlag2(true);
        this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
    }

    public void onCBPacketEvent1(CBPacketEvent1 cbpacketEvent1) {
        this.logPacket("Received: ", cbpacketEvent1.getCbpacket());
    }
}
