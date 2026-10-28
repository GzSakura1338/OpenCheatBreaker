package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public abstract class ChatComponentStyle implements IChatComponent {
    public List siblings = Lists.newArrayList();
    public ChatStyle style;

    @Override
    public IChatComponent appendSibling(IChatComponent component) {
        component.getChatStyle().setParentStyle(this.getChatStyle());
        this.siblings.add(component);
        return this;
    }

    @Override
    public List getSiblings() {
        return this.siblings;
    }

    @Override
    public IChatComponent appendText(String text) {
        return this.appendSibling(new ChatComponentText(text));
    }

    @Override
    public IChatComponent setChatStyle(ChatStyle style) {
        this.style = style;

        for (IChatComponent chatComponent : (Iterable<IChatComponent>)this.siblings) {
            chatComponent.getChatStyle().setParentStyle(this.getChatStyle());
        }

        return this;
    }

    @Override
    public ChatStyle getChatStyle() {
        if (this.style == null) {
            this.style = new ChatStyle();

            for (IChatComponent chatComponent : (Iterable<IChatComponent>)this.siblings) {
                chatComponent.getChatStyle().setParentStyle(this.style);
            }
        }

        return this.style;
    }

    @Override
    public Iterator iterator() {
        return Iterators.concat(Iterators.forArray(new ChatComponentStyle[]{this}), createDeepCopyIterator(this.siblings));
    }

    @Override
    public String getUnformattedText() {
        StringBuilder sb = new StringBuilder();

        for (IChatComponent chatComponent : (Iterable<IChatComponent>)this) {
            sb.append(chatComponent.getUnformattedTextForChat());
        }

        return sb.toString();
    }

    @Override
    public String getFormattedText() {
        StringBuilder sb = new StringBuilder();

        for (IChatComponent chatComponent : (Iterable<IChatComponent>)this) {
            sb.append(chatComponent.getChatStyle().getFormattingCode());
            sb.append(chatComponent.getUnformattedTextForChat());
            sb.append(EnumChatFormatting.RESET);
        }

        return sb.toString();
    }

    public static Iterator createDeepCopyIterator(Iterable components) {
        Iterator concat = Iterators.concat(Iterators.transform(components.iterator(), new Function() {
            public Iterator apply(IChatComponent chatComponent) {
                return chatComponent.iterator();
            }

            public Object apply(Object obj) {
                return this.apply((IChatComponent)obj);
            }
        }));
        return Iterators.transform(concat, new Function() {
            public IChatComponent apply(IChatComponent chatComponent) {
                IChatComponent createCopy = chatComponent.createCopy();
                createCopy.setChatStyle(createCopy.getChatStyle().createDeepCopy());
                return createCopy;
            }

            public Object apply(Object obj) {
                return this.apply((IChatComponent)obj);
            }
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ChatComponentStyle)) {
            return false;
        }

        ChatComponentStyle chatComponentStyle = (ChatComponentStyle)obj;
        return this.siblings.equals(chatComponentStyle.siblings) && this.getChatStyle().equals(chatComponentStyle.getChatStyle());
    }

    @Override
    public int hashCode() {
        return 31 * this.style.hashCode() + this.siblings.hashCode();
    }

    @Override
    public String toString() {
        return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
    }
}
