package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.StringEvent1;
import com.cheatbreaker.client.mod.AbstractInternalMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CreditsMod extends AbstractInternalMod {
    public Set<UUID> uuidSet = Sets.newHashSet(
        new UUID[]{UUID.fromString("d8f72541-823d-4ded-9f7f-b67fdb34f43c"), UUID.fromString("285c25e3-74f6-47e0-81a6-4e74ceb54ed3")}
    );
    public String bullet = EnumChatFormatting.RED + "- " + EnumChatFormatting.WHITE;
    public String text = EnumChatFormatting.RED
        + "[C"
        + EnumChatFormatting.WHITE
        + "B"
        + EnumChatFormatting.RED
        + "] "
        + EnumChatFormatting.RESET
        + EnumChatFormatting.GRAY
        + "Credits: ";

    public CreditsMod() throws java.io.IOException {
        super("Credits");
        this.setDescription("Prints the credits for CheatBreaker in chat.");
        this.setCategories(ModCategory.MISC);
        this.addEventHandler(StringEvent1.class, this::onStringEvent1);
        this.setEnabledByDefault(true);
    }

    public void onStringEvent1(StringEvent1 stringEvent1) {
        if (stringEvent1.getMessage().equals("/cb credits")) {
            stringEvent1.setCancelled(true);
            if (!this.uuidSet.contains(this.mc.getSession().getUUID())) {
                this.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(this.text));
                this.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(this.bullet + "CheatBreaker LLC for the original client."));
                this.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(this.bullet + "CheatBreaker.net team for managing this version."));
            }
        }
    }
}
