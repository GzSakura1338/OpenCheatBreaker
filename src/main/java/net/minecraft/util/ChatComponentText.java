package net.minecraft.util;

public class ChatComponentText extends ChatComponentStyle {
    public String text;
    public boolean flag;
    public boolean flag2;

    public ChatComponentText(String msg) {
        this.text = msg;
    }

    public String getChatComponentText_TextValue() {
        return this.text;
    }

    @Override
    public String getUnformattedTextForChat() {
        return this.text;
    }

    public ChatComponentText createCopy() {
        ChatComponentText chatComponentText = new ChatComponentText(this.text);
        chatComponentText.setChatStyle(this.getChatStyle().createShallowCopy());

        for (IChatComponent chatComponent : (Iterable<IChatComponent>)this.getSiblings()) {
            chatComponentText.appendSibling(chatComponent.createCopy());
        }

        return chatComponentText;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ChatComponentText)) {
            return false;
        }

        ChatComponentText chatComponentText = (ChatComponentText)obj;
        return this.text.equals(chatComponentText.getChatComponentText_TextValue()) && super.equals(obj);
    }

    @Override
    public String toString() {
        return "TextComponent{text='" + this.text + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setFlag2(boolean flag) {
        this.flag2 = flag;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public boolean isFlag2() {
        return this.flag2;
    }
}
