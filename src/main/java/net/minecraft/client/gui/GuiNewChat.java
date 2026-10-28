package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.chat.ClientChatLine;
import com.cheatbreaker.client.event.SimpleEvent4;
import com.cheatbreaker.client.event.SimpleEvent7;
import com.cheatbreaker.client.mod.impl.ChatMod;
import com.cheatbreaker.client.render.RenderUtils;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class GuiNewChat extends Gui {
    public static Logger logger = LogManager.getLogger();
    public ResourceLocation logoWhitePng = new ResourceLocation("client/logo_white.png");
    public List<String> sentMessages = new ArrayList<>();
    public List<ChatLine> chatLines = new ArrayList<>();
    public List<ChatLine> visibleMessages = new ArrayList<>();
    public int scrollPos;
    public boolean isScrolled;
    public List<ChatLine> chatLineList = new ArrayList<>();
    public int intValue;
    public int intValue2;
    public List<ChatLine> chatLineList2 = new ArrayList<>();
    public float floatValue3;
    public boolean flag = false;
    public long longValue = System.currentTimeMillis();
    public static final int CONSTANT_17 = 17;
    public float floatValue2;
    public Minecraft mc;

    public GuiNewChat(Minecraft mc) {
        this.mc = mc;
    }

    public void drawChat(int n) {
        if (this.mc.gameSettings.chatVisibility != EnumChatVisibility.HIDDEN) {
            int lineCount = this.getLineCount();
            boolean flag = false;
            int n2 = 0;
            int size = this.visibleMessages.size();
            float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
            if (size > 0) {
                if (this.getChatOpen()) {
                    flag = true;
                }

                float chatScale = this.getChatScale();
                int n3 = MathHelper.ceiling_float_int(this.getChatWidth() / chatScale);
                GL11.glPushMatrix();
                GL11.glTranslatef(2.0F, 20.0F, 0.0F);
                GL11.glScalef(chatScale, chatScale, 1.0F);

                for (int i = 0; i + this.scrollPos < this.visibleMessages.size() && i < lineCount; i++) {
                    ChatLine chatLine = this.visibleMessages.get(i + this.scrollPos);
                    if (chatLine != null) {
                        int n4 = n - chatLine.getUpdatedCounter();
                        if (n4 < 200 || flag) {
                            double d = n4 / 200.0;
                            d = 1.0 - d;
                            d *= 10.0;
                            if (d < 0.0) {
                                d = 0.0;
                            }

                            if (d > 1.0) {
                                d = 1.0;
                            }

                            d *= d;
                            int n5 = (int)(255.0 * d);
                            if (flag) {
                                n5 = 255;
                            }

                            n5 = (int)(n5 * f);
                            n2++;
                            if (n5 > 3) {
                                byte b = 0;
                                int n6 = -i * 9;
                                drawRect(b, n6 - 9, b + n3 + 4, n6, n5 / 2 << 24);
                                String formattedText = chatLine.getChatComponent().getFormattedText();
                                this.mc.fontRendererObj.drawShadowedString(formattedText, b, n6 - 8, 16777215 + (n5 << 24));
                                GL11.glDisable(3008);
                            }
                        }
                    }
                }

                if (flag) {
                    int n4 = this.mc.fontRendererObj.FONT_HEIGHT;
                    GL11.glTranslatef(-3.0F, 0.0F, 0.0F);
                    int n5 = size * n4 + size;
                    int n6 = n2 * n4 + n2;
                    int n7 = this.scrollPos * n6 / size;
                    int n8 = n6 * n6 / n5;
                    if (n5 != n6) {
                        int n9 = n7 > 0 ? 170 : 96;
                        int n10 = this.isScrolled ? 13382451 : 3355562;
                        drawRect(0.0F, -n7, 2.0F, -n7 - n8, n10 + (n9 << 24));
                        drawRect(2.0F, -n7, 1.0F, -n7 - n8, 13421772 + (n9 << 24));
                    }
                }

                GL11.glPopMatrix();
            }
        }
    }

    public void clearChatMessages() {
        this.visibleMessages.clear();
        this.chatLines.clear();
        this.sentMessages.clear();
    }

    public void printChatMessage(IChatComponent chatComponent) {
        this.printChatMessageWithOptionalDeletion(chatComponent, 0);
    }

    public void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int n) {
        this.floatValue2 = 0.0F;
        this.setChatLine(chatComponent, n, this.mc.ingameGUI.getUpdateCounter(), false);
        logger.info("[CHAT] " + chatComponent.getUnformattedText());
    }

    public String formatColors(String str) {
        return Minecraft.getMinecraft().gameSettings.chatColours ? str : EnumChatFormatting.getTextWithoutFormattingCodes(str);
    }

    public void setChatLine(IChatComponent chatComponent, int n, int n2, boolean flag) {
        if (n != 0) {
            this.deleteChatLine(n);
        }

        int n3 = MathHelper.floor_float(this.getChatWidth() / this.getChatScale());
        int n4 = 0;
        boolean flag2 = chatComponent instanceof ChatComponentText && ((ChatComponentText)chatComponent).isFlag2();
        boolean flag3 = chatComponent instanceof ChatComponentText && ((ChatComponentText)chatComponent).isFlag();
        ChatComponentText chatComponentText = new ChatComponentText("");
        chatComponentText.setFlag2(flag2);
        chatComponentText.setFlag(flag3);
        ArrayList newArrayList = Lists.newArrayList();
        ArrayList newArrayList2 = Lists.newArrayList(chatComponent);

        for (int i = 0; i < newArrayList2.size(); i++) {
            IChatComponent chatComponent2 = (IChatComponent)newArrayList2.get(i);
            String formatColors = this.formatColors(chatComponent2.getChatStyle().getFormattingCode() + chatComponent2.getUnformattedTextForChat());
            int stringWidth = this.mc.fontRendererObj.getStringWidth(formatColors);
            ChatComponentText chatComponentText2 = new ChatComponentText(formatColors);
            chatComponentText2.setChatStyle(chatComponent2.getChatStyle().createShallowCopy());
            boolean flag4 = false;
            int n5 = flag2 && newArrayList.isEmpty() ? Math.max(0, n3 - 17) : n3;
            if (n4 + stringWidth > n5) {
                String trimStringToWidth = this.mc.fontRendererObj.trimStringToWidth(formatColors, n5 - n4, false);
                String str = trimStringToWidth.length() < formatColors.length() ? formatColors.substring(trimStringToWidth.length()) : null;
                if (str != null && str.length() > 0) {
                    int lastIndexOf = trimStringToWidth.lastIndexOf(" ");
                    if (lastIndexOf >= 0 && this.mc.fontRendererObj.getStringWidth(formatColors.substring(0, lastIndexOf)) > 0) {
                        trimStringToWidth = formatColors.substring(0, lastIndexOf);
                        str = formatColors.substring(lastIndexOf);
                    }

                    ChatComponentText chatComponentText3 = new ChatComponentText(str);
                    chatComponentText3.setChatStyle(chatComponent2.getChatStyle().createShallowCopy());
                    newArrayList2.add(i + 1, chatComponentText3);
                }

                stringWidth = this.mc.fontRendererObj.getStringWidth(trimStringToWidth);
                chatComponentText2 = new ChatComponentText(trimStringToWidth);
                chatComponentText2.setChatStyle(chatComponent2.getChatStyle().createShallowCopy());
                flag4 = true;
            }

            if (n4 + stringWidth <= n5) {
                n4 += stringWidth;
                chatComponentText.appendSibling(chatComponentText2);
            } else {
                flag4 = true;
            }

            if (flag4) {
                newArrayList.add(chatComponentText);
                n4 = 0;
                chatComponentText = new ChatComponentText("");
                chatComponentText.setFlag(flag3);
            }
        }

        newArrayList.add(chatComponentText);
        boolean chatOpen = this.getChatOpen();

        for (ChatComponentText chatComponentText2 : (Iterable<ChatComponentText>)newArrayList) {
            ChatLine chatLine = new ChatLine(n2, chatComponentText2, n);
            ClientChatLine from = ClientChatLine.from(chatLine);
            from.setIChatComponent(chatComponent);
            if (chatComponentText2.isFlag2() || chatComponentText2.isFlag()) {
                from.setHasIcon(chatComponentText2.isFlag2());
                from.setHighlighted(chatComponentText2.isFlag());
                this.visibleMessages.add(0, from);
            }

            if (!chatComponentText2.isFlag2() && !chatComponentText2.isFlag()) {
                this.visibleMessages.add(0, new ChatLine(n2, chatComponentText2, n).setIChatComponent(chatComponent));
            }

            if (chatOpen && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }
        }

        SimpleEvent7 simpleEvent7 = new SimpleEvent7();
        CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent7);

        while (this.visibleMessages.size() > 100 && !simpleEvent7.isCancelled()) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }

        if (!flag) {
            this.chatLines.add(0, new ChatLine(n2, chatComponent, n));

            while (this.chatLines.size() > 100 && !simpleEvent7.isCancelled()) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }

        this.findList(newArrayList);
    }

    public void refreshChat() {
        this.visibleMessages.clear();
        this.resetScroll();

        for (int i = this.chatLines.size() - 1; i >= 0; i--) {
            ChatLine chatLine = this.chatLines.get(i);
            this.setChatLine(chatLine.getChatComponent(), chatLine.getChatLineID(), chatLine.getUpdatedCounter(), true);
        }

        CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent4());
    }

    public List getSentMessages() {
        return this.sentMessages;
    }

    public void addToSentMessages(String str) {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(str)) {
            this.sentMessages.add(str);
        }
    }

    public void resetScroll() {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    public void scroll(int n) {
        this.scrollPos += n;
        int size = this.visibleMessages.size();
        if (this.scrollPos > size - this.getLineCount()) {
            this.scrollPos = size - this.getLineCount();
        }

        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    public IChatComponent getChatComponent(int n, int n2) {
        int[] intArray = this.getIntArray(n, n2);
        if (intArray == null) {
            return null;
        }

        ChatLine chatLine = this.visibleMessages.get(intArray[0]);
        int n3 = 0;

        for (Object obj : chatLine.getChatComponent()) {
            IChatComponent chatComponent = (IChatComponent)obj;
            if (chatComponent instanceof ChatComponentText) {
                n3 += this.mc.fontRendererObj.getStringWidth(this.formatColors(((ChatComponentText)chatComponent).getChatComponentText_TextValue()));
                if (n3 > intArray[1]) {
                    return chatComponent;
                }
            }
        }

        return null;
    }

    public boolean getChatOpen() {
        return this.mc.currentScreen instanceof GuiChat;
    }

    public void deleteChatLine(int n) {
        Iterator iterator = this.visibleMessages.iterator();

        while (iterator.hasNext()) {
            ChatLine chatLine = (ChatLine)iterator.next();
            if (chatLine.getChatLineID() == n) {
                iterator.remove();
            }
        }

        iterator = this.chatLines.iterator();

        while (iterator.hasNext()) {
            ChatLine chatLine = (ChatLine)iterator.next();
            if (chatLine.getChatLineID() == n) {
                iterator.remove();
                break;
            }
        }
    }

    public int getChatWidth() {
        return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }

    public int getChatHeight() {
        return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    public float getChatScale() {
        return this.mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(float f) {
        short s = 320;
        byte b = 40;
        return MathHelper.floor_float(f * (s - b) + b);
    }

    public static int calculateChatboxHeight(float f) {
        short s = 180;
        byte b = 20;
        return MathHelper.floor_float(f * (s - b) + b);
    }

    public int getLineCount() {
        return this.getChatHeight() / 9;
    }

    public int computeInt(int n) {
        this.intValue2 = n;
        return n;
    }

    public int computeInt2(int n) {
        if (this.flag && this.intValue2 <= this.intValue) {
            int n2 = n >> 24 & 0xFF;
            n2 = (int)(n2 * this.floatValue3);
            return n & 16777215 | n2 << 24;
        } else {
            return n;
        }
    }

    public void drawChat(int n, boolean flag) {
        this.flag = flag;
        ChatMod chatMod = CheatBreaker.getInstance().getModManager().chatMod;
        if (this.mc.gameSettings.chatVisibility != EnumChatVisibility.HIDDEN) {
            int lineCount = this.getLineCount();
            boolean flag2 = false;
            int n2 = 0;
            int size = this.visibleMessages.size();
            float f = this.mc.gameSettings.chatOpacity * chatMod.masterOpacity.getFloatValue() / 100.0F * 0.9F + 0.1F;
            long currentTimeMillis = System.currentTimeMillis();
            long l = currentTimeMillis - this.longValue;
            this.longValue = currentTimeMillis;
            this.process(l);
            float floatValue2 = this.floatValue2;
            this.floatValue3 = min(1.0F - --floatValue2 * floatValue2 * floatValue2 * floatValue2, 0.0F, 1.0F);
            if (size > 0) {
                if (this.getChatOpen()) {
                    flag2 = true;
                }

                float chatScale = this.getChatScale();
                int n3 = MathHelper.ceiling_float_int(this.getChatWidth() / chatScale);
                GL11.glPushMatrix();
                float hudHeight = chatMod.hudHeight;
                if (this.flag && !this.isScrolled) {
                    hudHeight += (9.0F - 9.0F * this.floatValue3) * this.getChatScale();
                }

                GL11.glTranslatef(0.0F, hudHeight, 0.0F);
                GL11.glScalef(chatScale, chatScale, 1.0F);

                for (int i = 0; i + this.scrollPos < this.visibleMessages.size() && i < lineCount; i++) {
                    ChatLine chatLine = this.visibleMessages.get(this.computeInt(i + this.scrollPos));
                    if (chatLine != null) {
                        int n4 = n - chatLine.getUpdatedCounter();
                        if (n4 < 200 || flag2) {
                            double d = n4 / 200.0;
                            d = 1.0 - d;
                            d *= 10.0;
                            if (d < 0.0) {
                                d = 0.0;
                            }

                            if (d > 1.0) {
                                d = 1.0;
                            }

                            d *= d;
                            int n5 = (int)(255.0 * d);
                            if (flag2) {
                                n5 = 255;
                            }

                            n5 = (int)(n5 * f);
                            n2++;
                            if (n5 > 3) {
                                byte b = 0;
                                int n6 = -i * 9;
                                String formattedText = chatLine.getChatComponent().getFormattedText();
                                boolean flag3 = chatLine instanceof ClientChatLine && ((ClientChatLine)chatLine).hasIcon();
                                float f2 = flag3 ? 17.0F : 0.0F;
                                if (this.mc.currentScreen instanceof GuiChat && chatMod.showBackground.getValue().equals("While Typing")
                                    || chatMod.showBackground.getValue().equals("ON")) {
                                    int n7 = this.chatLineList2.contains(chatLine) ? -1431699456 : chatMod.backgroundColor.getColorValue();
                                    int n8 = (int)((chatMod.backgroundColor.getColorValue() >> 24 & 0xFF) * (n5 / 255.0F));
                                    int n9 = n7 >> 16 & 0xFF;
                                    int n10 = n7 >> 8 & 0xFF;
                                    int n11 = n7 & 0xFF;
                                    int n12 = chatMod.backgroundWidthType.getValue().equals("Full")
                                        ? n3 + 4
                                        : (int)(this.mc.fontRendererObj.getStringWidth(formattedText) + f2);
                                    drawRect(b, n6 - 9, b + n12, n6, this.computeInt2(new Color(n9, n10, n11, n8).getRGB()));
                                }

                                GL11.glEnable(3042);
                                boolean flag4 = !chatMod.stripColorFormatting.getValue().equals("OFF");
                                if (formattedText.contains(Minecraft.getMinecraft().getSession().getUsername())) {
                                    String username = Minecraft.getMinecraft().getSession().getUsername();
                                    if (!chatMod.highlightColour.getStringValue().equalsIgnoreCase("None") && chatMod.highlightOwnName.getBooleanValue()) {
                                        formattedText = formattedText.replaceAll(
                                                Minecraft.getMinecraft().getSession().getUsername(),
                                                chatMod.getHighlightFormatting() + Minecraft.getMinecraft().getSession().getUsername()
                                            )
                                            + chatMod.getLastFormatting(formattedText.split(username)[0]);
                                    }
                                }

                                for (int j = 0; j + this.scrollPos < this.visibleMessages.size() && j < this.getLineCount(); j++) {
                                    ChatLine chatLine2 = this.visibleMessages.get(j + this.scrollPos);
                                    if ((flag2 || 10.0 - (n - chatLine2.getUpdatedCounter()) / 20.0 > 1.0) && chatLine2 instanceof ClientChatLine) {
                                        ClientChatLine clientChatLine = (ClientChatLine)chatLine2;
                                        if (clientChatLine.hasIcon()) {
                                            this.chatLineList.add(chatLine2);
                                        }

                                        if (clientChatLine.isHighlighted()) {
                                            this.chatLineList2.add(chatLine2);
                                        }
                                    }
                                }

                                this.mc
                                    .fontRendererObj
                                    .drawString(
                                        chatMod.stripColors(formattedText),
                                        b + f2,
                                        n6 - 8,
                                        this.computeInt3(flag4 ? chatMod.textColor.getColorValue() : 16777215 + (n5 << 24)),
                                        chatMod.textShadow.getBooleanValue()
                                    );
                                GL11.glDisable(3008);
                            }
                        }
                    }
                }

                if (flag2) {
                    int n4 = this.mc.fontRendererObj.FONT_HEIGHT;
                    GL11.glTranslatef(-3.0F, 0.0F, 0.0F);
                    int n5 = size * n4 + size;
                    int n6 = n2 * n4 + n2;
                    int n7 = this.scrollPos * n6 / size;
                    int n8 = n6 * n6 / n5;
                    if (n5 != n6) {
                        int n9 = n7 > 0 ? 170 : 96;
                        int n10 = this.isScrolled ? 13382451 : 3355562;
                        drawRect(0.0F, -n7, 2.0F, -n7 - n8, n10 + (n9 << 24));
                        drawRect(2.0F, -n7, 1.0F, -n7 - n8, 13421772 + (n9 << 24));
                    }
                }

                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                int n4 = 0;

                for (int i = 0; i + this.scrollPos < this.visibleMessages.size() && i < this.getLineCount(); i++) {
                    ChatLine chatLine = this.visibleMessages.get(i + this.scrollPos);
                    int n5 = n - chatLine.getUpdatedCounter();
                    if (n5 < 200 || flag2) {
                        n4 -= Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
                        if (chatLine instanceof ClientChatLine) {
                            ClientChatLine clientChatLine = (ClientChatLine)chatLine;
                            if (clientChatLine.hasIcon()) {
                                double d = n5 / 200.0;
                                d = 1.0 - d;
                                d *= 10.0;
                                if (d < 0.0) {
                                    d = 0.0;
                                }

                                if (d > 1.0) {
                                    d = 1.0;
                                }

                                d *= d;
                                int n6 = (int)(255.0 * d);
                                if (flag2) {
                                    n6 = 255;
                                }

                                n6 = (int)(n6 * f);
                                float min2 = min(n6 / 255.0F, 0.0F, 1.0F);
                                if (!(min2 <= 0.02F)) {
                                    if (chatMod.textShadow.getBooleanValue()) {
                                        GL11.glColor4f(0.4F, 0.4F, 0.4F, min2);
                                        RenderUtils.drawTexture(this.logoWhitePng, this.getChatOpen() ? 5.105F : 2.1F, n4 + 1.9F, 14.0F, 6.9F);
                                    }

                                    GL11.glColor4f(1.0F, 1.0F, 1.0F, min2);
                                    RenderUtils.drawTexture(this.logoWhitePng, this.getChatOpen() ? 4.105F : 1.1F, n4 + 0.9F, 14.0F, 6.9F);
                                }
                            }
                        }
                    }
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();
            }
        }
    }

    public List<ChatComponentText> findList(List<ChatComponentText> list) {
        this.intValue = list.size() - 1;
        return list;
    }

    public int computeInt3(int n) {
        ChatMod chatMod = CheatBreaker.getInstance().getModManager().chatMod;
        if (this.flag && this.intValue2 <= this.intValue) {
            int n2 = n >> 24 & 0xFF;
            n2 = (int)(n2 * this.floatValue3);
            return n & 16777215 | n2 << 24;
        } else {
            int n2 = n >> 24 & 0xFF;
            return new Color(n >> 16 & 0xFF, n >> 8 & 0xFF, n & 0xFF, n2).getRGB();
        }
    }

    public static float min(float f, float f2, float f3) {
        return f < f2 ? f2 : Math.min(f, f3);
    }

    public void printMessage(IChatComponent chatComponent) {
        this.floatValue2 = 0.0F;
        this.setChatLine(chatComponent, 0, this.mc.ingameGUI.getUpdateCounter(), false);
    }

    public void process(long l) {
        if (this.floatValue2 < 1.0F) {
            this.floatValue2 = this.floatValue2 + 0.001F * CheatBreaker.getInstance().getModManager().chatMod.smoothChatSpeed.getFloatValue() / 2.0F * (float)l;
        }

        this.floatValue2 = min(this.floatValue2, 0.0F, 1.0F);
    }

    public String formatText(int n, int n2) {
        int[] intArray = this.getIntArray(n, n2);
        if (intArray == null) {
            return null;
        }

        ChatLine chatLine = this.visibleMessages.get(intArray[0]);
        IChatComponent chatComponent = chatLine.getIChatComponent() != null ? chatLine.getIChatComponent() : chatLine.getChatComponent();
        return EnumChatFormatting.getTextWithoutFormattingCodes(chatComponent.getUnformattedText());
    }

    public int[] getIntArray(int n, int n2) {
        if (!this.getChatOpen()) {
            return null;
        }

        ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int scaleFactor = scaledResolution.getScaleFactor();
        float chatScale = this.getChatScale();
        int n3 = n / scaleFactor - 2;
        int n4 = n2 / scaleFactor - 28;
        ChatMod chatMod = CheatBreaker.getInstance().getModManager().chatMod;
        if (chatMod.isEnabled()) {
            float[] hudPosition = chatMod.getHudPosition(scaledResolution, true);
            n3 = (int)(n / scaleFactor / chatMod.getHudScale() - hudPosition[0]);
            n4 = (int)(n2 / scaleFactor / chatMod.getHudScale() - this.mc.displayHeight / scaleFactor / chatMod.getHudScale() + chatMod.hudHeight + hudPosition[1]);
        }

        n3 = MathHelper.floor_float(n3 / chatScale);
        n4 = MathHelper.floor_float(n4 / chatScale);
        if (n3 >= 0 && n4 >= 0) {
            int min = Math.min(this.getLineCount(), this.visibleMessages.size());
            if (n3 <= MathHelper.floor_float(this.getChatWidth() / this.getChatScale()) && n4 < this.mc.fontRendererObj.FONT_HEIGHT * min + min) {
                int n5 = n4 / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;
                if (n5 >= 0 && n5 < this.visibleMessages.size()) {
                    return new int[]{n5, n3};
                }
            }
        }

        return null;
    }
}
