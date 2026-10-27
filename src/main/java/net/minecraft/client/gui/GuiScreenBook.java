package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiScreenBook
extends GuiScreen {
    public static Logger logger = LogManager.getLogger();
    public static ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");
    public int bookImageWidth = 192;
    public int bookImageHeight = 192;
    public int bookTotalPages = 1;
    public boolean bookIsModified;
    public boolean bookGettingSigned;
    public int updateCount;
    public String bookTitle = "";
    public int lastClickIndex = -1;
    public EntityPlayer editingPlayer;
    public int currPage;
    public ItemStack bookObj;
    public boolean bookIsUnsigned;
    public List<IChatComponent> bookText;
    public NBTTagList bookPages;
    public NextPageButton buttonNextPage;
    public NextPageButton buttonPreviousPage;
    public GuiButton buttonDone;
    public GuiButton buttonSign;
    public GuiButton buttonFinalize;
    public GuiButton buttonCancel;

    public GuiScreenBook(EntityPlayer player, ItemStack book, boolean isUnsigned) {
        this.editingPlayer = player;
        this.bookObj = book;
        this.bookIsUnsigned = isUnsigned;
        if (book.hasTagCompound()) {
            NBTTagCompound nBTTagCompound = book.getTagCompound();
            this.bookPages = nBTTagCompound.getTagList("pages", 8);
            if (this.bookPages != null) {
                this.bookPages = (NBTTagList)this.bookPages.copy();
                this.bookTotalPages = this.bookPages.tagCount();
                if (this.bookTotalPages < 1) {
                    this.bookTotalPages = 1;
                }
            }
        }
        if (this.bookPages == null && isUnsigned) {
            this.bookPages = new NBTTagList();
            this.bookPages.appendTag(new NBTTagString(""));
            this.bookTotalPages = 1;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        ++this.updateCount;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents((boolean)true);
        if (this.bookIsUnsigned) {
            this.buttonSign = new GuiButton(3, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.signButton", new Object[0]));
            this.buttonList.add(this.buttonSign);
            this.buttonDone = new GuiButton(0, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.done", new Object[0]));
            this.buttonList.add(this.buttonDone);
            this.buttonFinalize = new GuiButton(5, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.finalizeButton", new Object[0]));
            this.buttonList.add(this.buttonFinalize);
            this.buttonCancel = new GuiButton(4, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.cancel", new Object[0]));
            this.buttonList.add(this.buttonCancel);
        } else {
            this.buttonDone = new GuiButton(0, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, I18n.format("gui.done", new Object[0]));
            this.buttonList.add(this.buttonDone);
        }
        int n = (this.width - this.bookImageWidth) / 2;
        int n2 = 2;
        this.buttonNextPage = new NextPageButton(1, n + 120, n2 + 154, true);
        this.buttonList.add(this.buttonNextPage);
        this.buttonPreviousPage = new NextPageButton(2, n + 38, n2 + 154, false);
        this.buttonList.add(this.buttonPreviousPage);
        this.updateButtons();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents((boolean)false);
    }

    public void updateButtons() {
        this.buttonNextPage.visible = !this.bookGettingSigned && (this.currPage < this.bookTotalPages - 1 || this.bookIsUnsigned);
        this.buttonPreviousPage.visible = !this.bookGettingSigned && this.currPage > 0;
        boolean bl = this.buttonDone.visible = !this.bookIsUnsigned || !this.bookGettingSigned;
        if (this.bookIsUnsigned) {
            this.buttonSign.visible = !this.bookGettingSigned;
            this.buttonCancel.visible = this.bookGettingSigned;
            this.buttonFinalize.visible = this.bookGettingSigned;
            this.buttonFinalize.enabled = this.bookTitle.trim().length() > 0;
        }
    }

    public void sendBookToServer(boolean publish) throws IOException {
        if (this.bookIsUnsigned && this.bookIsModified && this.bookPages != null) {
            Object object;
            while (this.bookPages.tagCount() > 1 && ((String)(object = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1))).length() == 0) {
                this.bookPages.removeTag(this.bookPages.tagCount() - 1);
            }
            if (this.bookObj.hasTagCompound()) {
                object = this.bookObj.getTagCompound();
                ((NBTTagCompound)object).setTag("pages", this.bookPages);
            } else {
                this.bookObj.setTagInfo("pages", this.bookPages);
            }
            object = "MC|BEdit";
            if (publish) {
                object = "MC|BSign";
                this.bookObj.setTagInfo("author", new NBTTagString(this.editingPlayer.getName()));
                this.bookObj.setTagInfo("title", new NBTTagString(this.bookTitle.trim()));
                for (int i = 0; i < this.bookPages.tagCount(); ++i) {
                    String string = this.bookPages.getStringTagAt(i);
                    ChatComponentText chatComponentText = new ChatComponentText(string);
                    string = IChatComponent.Serializer.componentToJson(chatComponentText);
                    this.bookPages.set(i, new NBTTagString(string));
                }
                this.bookObj.setItem(Items.written_book);
            }
            PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
            packetBuffer.writeItemStackToBuffer(this.bookObj);
            this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload((String)object, packetBuffer));
        }
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 0) {
                this.mc.displayGuiScreen(null);
                this.sendBookToServer(false);
            } else if (button.id == 3 && this.bookIsUnsigned) {
                this.bookGettingSigned = true;
            } else if (button.id == 1) {
                if (this.currPage < this.bookTotalPages - 1) {
                    ++this.currPage;
                } else if (this.bookIsUnsigned) {
                    this.addNewPage();
                    if (this.currPage < this.bookTotalPages - 1) {
                        ++this.currPage;
                    }
                }
            } else if (button.id == 2) {
                if (this.currPage > 0) {
                    --this.currPage;
                }
            } else if (button.id == 5 && this.bookGettingSigned) {
                this.sendBookToServer(true);
                this.mc.displayGuiScreen(null);
            } else if (button.id == 4 && this.bookGettingSigned) {
                this.bookGettingSigned = false;
            }
            this.updateButtons();
        }
    }

    public void addNewPage() {
        if (this.bookPages != null && this.bookPages.tagCount() < 50) {
            this.bookPages.appendTag(new NBTTagString(""));
            ++this.bookTotalPages;
            this.bookIsModified = true;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (this.bookIsUnsigned) {
            if (this.bookGettingSigned) {
                this.keyTypedInTitle(typedChar, keyCode);
            } else {
                this.keyTypedInBook(typedChar, keyCode);
            }
        }
    }

    public void keyTypedInBook(char typedChar, int keyCode) {
        if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            this.pageInsertIntoCurrent(GuiScreen.getClipboardString());
        } else {
            switch (keyCode) {
                case 14: {
                    String string = this.pageGetCurrent();
                    if (string.length() > 0) {
                        this.pageSetCurrent(string.substring(0, string.length() - 1));
                    }
                    return;
                }
                case 28: 
                case 156: {
                    this.pageInsertIntoCurrent("\n");
                    return;
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                this.pageInsertIntoCurrent(Character.toString(typedChar));
            }
        }
    }

    public void keyTypedInTitle(char c, int n) throws IOException {
        switch (n) {
            case 14: {
                if (!this.bookTitle.isEmpty()) {
                    this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
                    this.updateButtons();
                }
                return;
            }
            case 28: 
            case 156: {
                if (!this.bookTitle.isEmpty()) {
                    this.sendBookToServer(true);
                    this.mc.displayGuiScreen(null);
                }
                return;
            }
        }
        if (this.bookTitle.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(c)) {
            this.bookTitle = this.bookTitle + Character.toString(c);
            this.updateButtons();
            this.bookIsModified = true;
        }
    }

    public String pageGetCurrent() {
        return this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount() ? this.bookPages.getStringTagAt(this.currPage) : "";
    }

    public void pageSetCurrent(String string) {
        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            this.bookPages.set(this.currPage, new NBTTagString(string));
            this.bookIsModified = true;
        }
    }

    public void pageInsertIntoCurrent(String string) {
        String string2 = this.pageGetCurrent();
        String string3 = string2 + string;
        int n = this.fontRendererObj.splitStringWidth(string3 + "" + (Object)((Object)EnumChatFormatting.BLACK) + "_", 118);
        if (n <= 128 && string3.length() < 256) {
            this.pageSetCurrent(string3);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(bookGuiTextures);
        int n = (this.width - this.bookImageWidth) / 2;
        int n2 = 2;
        this.drawTexturedModalRect(n, n2, 0, 0, this.bookImageWidth, this.bookImageHeight);
        if (this.bookGettingSigned) {
            String string = this.bookTitle;
            if (this.bookIsUnsigned) {
                string = this.updateCount / 6 % 2 == 0 ? string + "" + (Object)((Object)EnumChatFormatting.BLACK) + "_" : string + "" + (Object)((Object)EnumChatFormatting.GRAY) + "_";
            }
            String string2 = I18n.format("book.editTitle", new Object[0]);
            int n3 = this.fontRendererObj.getStringWidth(string2);
            this.fontRendererObj.drawString(string2, n + 36 + (116 - n3) / 2, n2 + 16 + 16, 0);
            int n4 = this.fontRendererObj.getStringWidth(string);
            this.fontRendererObj.drawString(string, n + 36 + (116 - n4) / 2, n2 + 48, 0);
            String string3 = I18n.format("book.byAuthor", this.editingPlayer.getName());
            int n5 = this.fontRendererObj.getStringWidth(string3);
            this.fontRendererObj.drawString((Object)((Object)EnumChatFormatting.DARK_GRAY) + string3, n + 36 + (116 - n5) / 2, n2 + 48 + 10, 0);
            String string4 = I18n.format("book.finalizeWarning", new Object[0]);
            this.fontRendererObj.drawSplitString(string4, n + 36, n2 + 80, 116, 0);
        } else {
            String string = I18n.format("book.pageIndicator", this.currPage + 1, this.bookTotalPages);
            String string5 = "";
            if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
                string5 = this.bookPages.getStringTagAt(this.currPage);
            }
            if (this.bookIsUnsigned) {
                string5 = this.fontRendererObj.getBidiFlag() ? string5 + "_" : (this.updateCount / 6 % 2 == 0 ? string5 + "" + (Object)((Object)EnumChatFormatting.BLACK) + "_" : string5 + "" + (Object)((Object)EnumChatFormatting.GRAY) + "_");
            } else if (this.lastClickIndex != this.currPage) {
                IChatComponent iChatComponent;
                if (ItemEditableBook.validBookTagContents(this.bookObj.getTagCompound())) {
                    try {
                        iChatComponent = IChatComponent.Serializer.jsonToComponent(string5);
                        this.bookText = iChatComponent != null ? GuiUtilRenderComponents.splitText(iChatComponent, 116, this.fontRendererObj, true, true) : null;
                    }
                    catch (JsonParseException jsonParseException) {
                        this.bookText = null;
                    }
                } else {
                    iChatComponent = new ChatComponentText(EnumChatFormatting.DARK_RED.toString() + "* Invalid book tag *");
                    this.bookText = Lists.newArrayList(iChatComponent);
                }
                this.lastClickIndex = this.currPage;
            }
            int n6 = this.fontRendererObj.getStringWidth(string);
            this.fontRendererObj.drawString(string, n - n6 + this.bookImageWidth - 44, n2 + 16, 0);
            if (this.bookText == null) {
                this.fontRendererObj.drawSplitString(string5, n + 36, n2 + 16 + 16, 116, 0);
            } else {
                int n7 = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.bookText.size());
                for (int i = 0; i < n7; ++i) {
                    IChatComponent iChatComponent = this.bookText.get(i);
                    this.fontRendererObj.drawString(iChatComponent.getUnformattedText(), n + 36, n2 + 16 + 16 + i * this.fontRendererObj.FONT_HEIGHT, 0);
                }
                IChatComponent iChatComponent = this.getTextAt(mouseX, mouseY);
                if (iChatComponent != null) {
                    this.handleComponentHover(iChatComponent, mouseX, mouseY);
                }
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        IChatComponent iChatComponent;
        if (mouseButton == 0 && this.handleComponentClick(iChatComponent = this.getTextAt(mouseX, mouseY))) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean handleComponentClick(IChatComponent component) {
        ClickEvent clickEvent;
        ClickEvent clickEvent2 = clickEvent = component == null ? null : component.getChatStyle().getChatClickEvent();
        if (clickEvent == null) {
            return false;
        }
        if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String string = clickEvent.getValue();
            try {
                int n = Integer.parseInt(string) - 1;
                if (n >= 0 && n < this.bookTotalPages && n != this.currPage) {
                    this.currPage = n;
                    this.updateButtons();
                    return true;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return false;
        }
        boolean bl = super.handleComponentClick(component);
        if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.mc.displayGuiScreen(null);
        }
        return bl;
    }

    public IChatComponent getTextAt(int n, int n2) {
        if (this.bookText == null) {
            return null;
        }
        int n3 = n - (this.width - this.bookImageWidth) / 2 - 36;
        int n4 = n2 - 2 - 16 - 16;
        if (n3 >= 0 && n4 >= 0) {
            int n5 = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.bookText.size());
            if (n3 <= 116 && n4 < this.mc.fontRendererObj.FONT_HEIGHT * n5 + n5) {
                int n6 = n4 / this.mc.fontRendererObj.FONT_HEIGHT;
                if (n6 >= 0 && n6 < this.bookText.size()) {
                    IChatComponent iChatComponent = this.bookText.get(n6);
                    int n7 = 0;
                    for (IChatComponent iChatComponent2 : iChatComponent) {
                        if (!(iChatComponent2 instanceof ChatComponentText) || (n7 += this.mc.fontRendererObj.getStringWidth(((ChatComponentText)iChatComponent2).getChatComponentText_TextValue())) <= n3) continue;
                        return iChatComponent2;
                    }
                }
                return null;
            }
            return null;
        }
        return null;
    }

    public static class NextPageButton
    extends GuiButton {
        public boolean isRight;

        public NextPageButton(int n, int n2, int n3, boolean bl) {
            super(n, n2, n3, 23, 13, "");
            this.isRight = bl;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                boolean bl = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                mc.getTextureManager().bindTexture(bookGuiTextures);
                int n = 0;
                int n2 = 192;
                if (bl) {
                    n += 23;
                }
                if (!this.isRight) {
                    n2 += 13;
                }
                this.drawTexturedModalRect(this.xPosition, this.yPosition, n, n2, 23, 13);
            }
        }
    }
}
