package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.emote.AbstractEmote;
import com.cheatbreaker.client.event.StringEvent1;
import com.cheatbreaker.client.mod.AbstractInternalMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class EmoteTestingMod extends AbstractInternalMod {
    public Set<UUID> uuidSet = Sets.newHashSet(
        new UUID[]{UUID.fromString("d8f72541-823d-4ded-9f7f-b67fdb34f43c"), UUID.fromString("285c25e3-74f6-47e0-81a6-4e74ceb54ed3")}
    );
    public String usage = EnumChatFormatting.RED + "Usage: /cb emote <id>";

    public void onStringEvent1(StringEvent1 stringEvent1) {
        if (stringEvent1.getMessage().startsWith("/cb emote")) {
            stringEvent1.setCancelled(true);
            String[] split = stringEvent1.getMessage().split(" ");
            if (split.length != 3) {
                ChatComponentText chatComponentText = new ChatComponentText(EnumChatFormatting.RED + "Usage: /cb emote <id>");
                chatComponentText.setFlag2(true);
                this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
                return;
            }

            if (split[2].equals("list")) {
                StringBuilder sb = new StringBuilder();

                for (int n : CheatBreaker.getInstance().getEmoteManager().getOwnedEmotes()) {
                    AbstractEmote createEmote = CheatBreaker.getInstance().getEmoteManager().createEmote(n);
                    if (!sb.toString().equals("")) {
                        sb.append(", ");
                    }

                    sb.append(createEmote == null ? "unknown" : createEmote.getDisplayName()).append(" (ID: ").append(n).append(")");
                }

                ChatComponentText chatComponentText = new ChatComponentText(EnumChatFormatting.GRAY + "Emotes: " + sb);
                chatComponentText.setFlag2(true);
                this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
                return;
            }

            AbstractEmote createEmote = CheatBreaker.getInstance().getEmoteManager().createEmote(Integer.parseInt(split[2]));
            if (createEmote == null) {
                ChatComponentText chatComponentText = new ChatComponentText(EnumChatFormatting.RED + "No emote with ID " + split[2]);
                chatComponentText.setFlag2(true);
                this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
                return;
            }

            ChatComponentText chatComponentText = new ChatComponentText(EnumChatFormatting.GRAY + "Doing emote: " + createEmote.getDisplayName());
            chatComponentText.setFlag2(true);
            this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
            CheatBreaker.getInstance().getEmoteManager().startEmote(this.mc.thePlayer, createEmote);
        }
    }

    public EmoteTestingMod() throws java.io.IOException {
        super("Emote Testing");
        this.setDescription("Allows you to activate emotes via commands.");
        this.setCategories(ModCategory.MISC);
        this.addEventHandler(StringEvent1.class, this::onStringEvent1);
        this.setEnabledByDefault(true);
    }
}
