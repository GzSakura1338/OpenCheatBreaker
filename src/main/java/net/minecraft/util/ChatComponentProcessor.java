package net.minecraft.util;

import net.minecraft.command.CommandException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.Entity;
import net.minecraft.command.ICommandSender;

public class ChatComponentProcessor
{
    public static IChatComponent processComponent(final ICommandSender commandSender, final IChatComponent component, final Entity entityIn) throws CommandException {
        IChatComponent matchEntitiesToChatComponent;
        if (component instanceof ChatComponentScore) {
            final ChatComponentScore chatComponentScore = (ChatComponentScore)component;
            String s = chatComponentScore.getName();
            if (PlayerSelector.hasArguments(s)) {
                final List<Entity> matchEntities = PlayerSelector.matchEntities(commandSender, s, (Class<? extends Entity>)Entity.class);
                if (matchEntities.size() != 1) {
                    throw new EntityNotFoundException();
                }
                s = matchEntities.get(0).getName();
            }
            matchEntitiesToChatComponent = ((entityIn != null && s.equals("*")) ? new ChatComponentScore(entityIn.getName(), chatComponentScore.getObjective()) : new ChatComponentScore(s, chatComponentScore.getObjective()));
            ((ChatComponentScore)matchEntitiesToChatComponent).setValue(chatComponentScore.getUnformattedTextForChat());
        }
        else if (component instanceof ChatComponentSelector) {
            matchEntitiesToChatComponent = PlayerSelector.matchEntitiesToChatComponent(commandSender, ((ChatComponentSelector)component).getSelector());
            if (matchEntitiesToChatComponent == null) {
                matchEntitiesToChatComponent = new ChatComponentText("");
            }
        }
        else if (component instanceof ChatComponentText) {
            matchEntitiesToChatComponent = new ChatComponentText(((ChatComponentText)component).getChatComponentText_TextValue());
        }
        else {
            if (!(component instanceof ChatComponentTranslation)) {
                return component;
            }
            final Object[] formatArgs = ((ChatComponentTranslation)component).getFormatArgs();
            for (int i = 0; i < formatArgs.length; ++i) {
                final Object o = formatArgs[i];
                if (o instanceof IChatComponent) {
                    formatArgs[i] = processComponent(commandSender, (IChatComponent)o, entityIn);
                }
            }
            matchEntitiesToChatComponent = new ChatComponentTranslation(((ChatComponentTranslation)component).getKey(), formatArgs);
        }
        final ChatStyle chatStyle = component.getChatStyle();
        if (chatStyle != null) {
            matchEntitiesToChatComponent.setChatStyle(chatStyle.createShallowCopy());
        }
        final Iterator<IChatComponent> iterator = component.getSiblings().iterator();
        while (iterator.hasNext()) {
            matchEntitiesToChatComponent.appendSibling(processComponent(commandSender, iterator.next(), entityIn));
        }
        return matchEntitiesToChatComponent;
    }
}
