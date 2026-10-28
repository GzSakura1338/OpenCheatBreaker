package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ChatAllowedCharacters;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.PacketBuffer;
import io.netty.buffer.Unpooled;
import net.minecraft.init.Items;
import java.util.List;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class GuiScreenBook extends GuiScreen
{
    public static Logger logger;
    public static ResourceLocation bookGuiTextures;
    public final int bookImageWidth = 192;
    public final int bookImageHeight = 192;
    public int bookTotalPages;
    public boolean bookIsModified;
    public boolean bookGettingSigned;
    public int updateCount;
    public String bookTitle;
    public EntityPlayer editingPlayer;
    public ItemStack bookObj;
    public int currPage;
    public boolean bookIsUnsigned;
    public NBTTagList bookPages;
    public NextPageButton buttonNextPage;
    public NextPageButton buttonPreviousPage;
    public GuiButton buttonDone;
    public GuiButton buttonSign;
    public GuiButton buttonFinalize;
    public GuiButton buttonCancel;
    
    public GuiScreenBook(final EntityPlayer editingPlayer, final ItemStack bookObj, final boolean bookIsUnsigned) {
        this.bookTotalPages = 1;
        this.bookTitle = "";
        this.editingPlayer = editingPlayer;
        this.bookObj = bookObj;
        this.bookIsUnsigned = bookIsUnsigned;
        if (bookObj.hasTagCompound()) {
            this.bookPages = bookObj.getTagCompound().getTagList("pages", 8);
            if (this.bookPages != null) {
                this.bookPages = (NBTTagList)this.bookPages.copy();
                this.bookTotalPages = this.bookPages.tagCount();
                if (this.bookTotalPages < 1) {
                    this.bookTotalPages = 1;
                }
            }
        }
        if (this.bookPages == null && bookIsUnsigned) {
            (this.bookPages = new NBTTagList()).appendTag(new NBTTagString(""));
            this.bookTotalPages = 1;
        }
    }
    
    @Override
    public void updateScreen() throws java.io.IOException {
        super.updateScreen();
        ++this.updateCount;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        if (this.bookIsUnsigned) {
            final List<GuiButton> buttonList = this.buttonList;
            final int buttonId = 3;
            final int x = this.width / 2 - 100;
            final int n = 4;
            this.getClass();
            buttonList.add(this.buttonSign = new GuiButton(buttonId, x, n + 192, 98, 20, I18n.format("book.signButton", new Object[0])));
            final List<GuiButton> buttonList2 = this.buttonList;
            final int buttonId2 = 0;
            final int x2 = this.width / 2 + 2;
            final int n2 = 4;
            this.getClass();
            buttonList2.add(this.buttonDone = new GuiButton(buttonId2, x2, n2 + 192, 98, 20, I18n.format("gui.done", new Object[0])));
            final List<GuiButton> buttonList3 = this.buttonList;
            final int buttonId3 = 5;
            final int x3 = this.width / 2 - 100;
            final int n3 = 4;
            this.getClass();
            buttonList3.add(this.buttonFinalize = new GuiButton(buttonId3, x3, n3 + 192, 98, 20, I18n.format("book.finalizeButton", new Object[0])));
            final List<GuiButton> buttonList4 = this.buttonList;
            final int buttonId4 = 4;
            final int x4 = this.width / 2 + 2;
            final int n4 = 4;
            this.getClass();
            buttonList4.add(this.buttonCancel = new GuiButton(buttonId4, x4, n4 + 192, 98, 20, I18n.format("gui.cancel", new Object[0])));
        }
        else {
            final List<GuiButton> buttonList5 = this.buttonList;
            final int buttonId5 = 0;
            final int x5 = this.width / 2 - 100;
            final int n5 = 4;
            this.getClass();
            buttonList5.add(this.buttonDone = new GuiButton(buttonId5, x5, n5 + 192, 200, 20, I18n.format("gui.done", new Object[0])));
        }
        final int width = this.width;
        this.getClass();
        final int n6 = (width - 192) / 2;
        final int n7 = 2;
        this.buttonList.add(this.buttonNextPage = new NextPageButton(1, n6 + 120, n7 + 154, true));
        this.buttonList.add(this.buttonPreviousPage = new NextPageButton(2, n6 + 38, n7 + 154, false));
        this.updateButtons();
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    public void updateButtons() {
        this.buttonNextPage.visible = (!this.bookGettingSigned && (this.currPage < this.bookTotalPages - 1 || this.bookIsUnsigned));
        this.buttonPreviousPage.visible = (!this.bookGettingSigned && this.currPage > 0);
        this.buttonDone.visible = (!this.bookIsUnsigned || !this.bookGettingSigned);
        if (this.bookIsUnsigned) {
            this.buttonSign.visible = !this.bookGettingSigned;
            this.buttonCancel.visible = this.bookGettingSigned;
            this.buttonFinalize.visible = this.bookGettingSigned;
            this.buttonFinalize.enabled = (this.bookTitle.trim().length() > 0);
        }
    }
    
    public void sendBookToServer(final boolean b) {
        if (this.bookIsUnsigned && this.bookIsModified && this.bookPages != null) {
            while (this.bookPages.tagCount() > 1 && this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1).length() == 0) {
                this.bookPages.removeTag(this.bookPages.tagCount() - 1);
            }
            if (this.bookObj.hasTagCompound()) {
                this.bookObj.getTagCompound().setTag("pages", this.bookPages);
            }
            else {
                this.bookObj.setTagInfo("pages", this.bookPages);
            }
            String s = "MC|BEdit";
            if (b) {
                s = "MC|BSign";
                this.bookObj.setTagInfo("author", new NBTTagString(this.editingPlayer.getCommandSenderName()));
                this.bookObj.setTagInfo("title", new NBTTagString(this.bookTitle.trim()));
                this.bookObj.setItem(Items.written_book);
            }
            final ByteBuf buffer = Unpooled.buffer();
            try {
                new PacketBuffer(buffer).writeItemStackToBuffer(this.bookObj);
                this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(s, buffer));
            }
            catch (final Exception ex) {
                GuiScreenBook.logger.error("Couldn't send book info", (Throwable)ex);
            }
            finally {
                buffer.release();
            }
        }
    }
    
    @Override
    public void actionPerformed(final GuiButton button) throws java.io.IOException {
        if (button.enabled) {
            if (button.id == 0) {
                this.mc.displayGuiScreen(null);
                this.sendBookToServer(false);
            }
            else if (button.id == 3 && this.bookIsUnsigned) {
                this.bookGettingSigned = true;
            }
            else if (button.id == 1) {
                if (this.currPage < this.bookTotalPages - 1) {
                    ++this.currPage;
                }
                else if (this.bookIsUnsigned) {
                    this.addNewPage();
                    if (this.currPage < this.bookTotalPages - 1) {
                        ++this.currPage;
                    }
                }
            }
            else if (button.id == 2) {
                if (this.currPage > 0) {
                    --this.currPage;
                }
            }
            else if (button.id == 5 && this.bookGettingSigned) {
                this.sendBookToServer(true);
                this.mc.displayGuiScreen(null);
            }
            else if (button.id == 4 && this.bookGettingSigned) {
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
    public void keyTyped(final char typedChar, final int keyCode) throws java.io.IOException {
        super.keyTyped(typedChar, keyCode);
        if (this.bookIsUnsigned) {
            if (this.bookGettingSigned) {
                this.keyTypedInTitle(typedChar, keyCode);
            }
            else {
                this.keyTypedInBook(typedChar, keyCode);
            }
        }
    }
    
    public void keyTypedInBook(final char c, final int n) {
        switch (c) {
            case '\u0016': {
                this.pageInsertIntoCurrent(GuiScreen.getClipboardString());
                return;
            }
            default: {
                switch (n) {
                    case 14: {
                        final String pageGetCurrent = this.pageGetCurrent();
                        if (pageGetCurrent.length() > 0) {
                            this.pageSetCurrent(pageGetCurrent.substring(0, pageGetCurrent.length() - 1));
                        }
                        return;
                    }
                    case 28:
                    case 156: {
                        this.pageInsertIntoCurrent("\n");
                        return;
                    }
                    default: {
                        if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                            this.pageInsertIntoCurrent(Character.toString(c));
                        }
                        return;
                    }
                }
            }
        }
    }
    
    public void keyTypedInTitle(final char c, final int n) throws java.io.IOException {
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
            default: {
                if (this.bookTitle.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(c)) {
                    this.bookTitle += c;
                    this.updateButtons();
                    this.bookIsModified = true;
                }
            }
        }
    }
    
    public String pageGetCurrent() {
        return (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) ? this.bookPages.getStringTagAt(this.currPage) : "";
    }
    
    public void pageSetCurrent(final String s) {
        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            this.bookPages.setTag(this.currPage, new NBTTagString(s));
            this.bookIsModified = true;
        }
    }
    
    public void pageInsertIntoCurrent(final String str) {
        final String string = this.pageGetCurrent() + str;
        if (this.fontRendererObj.splitStringWidth(string + "" + EnumChatFormatting.BLACK + "_", 118) <= 118 && string.length() < 256) {
            this.pageSetCurrent(string);
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) throws java.io.IOException {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiScreenBook.bookGuiTextures);
        final int width = this.width;
        this.getClass();
        final int n = (width - 192) / 2;
        final int n2 = 2;
        final float n3 = (float)n;
        final float n4 = (float)n2;
        final float n5 = 0.0f;
        final float n6 = 0.0f;
        this.getClass();
        final int n7 = 192;
        this.getClass();
        Gui.drawTexturedRect(n3, n4, n5, n6, n7, 192);
        if (this.bookGettingSigned) {
            String text = this.bookTitle;
            if (this.bookIsUnsigned) {
                if (this.updateCount / 6 % 2 == 0) {
                    text = text + "" + EnumChatFormatting.BLACK + "_";
                }
                else {
                    text = text + "" + EnumChatFormatting.GRAY + "_";
                }
            }
            final String format = I18n.format("book.editTitle", new Object[0]);
            this.fontRendererObj.drawString(format, n + 36 + (116 - this.fontRendererObj.getStringWidth(format)) / 2, n2 + 16 + 16, 0);
            this.fontRendererObj.drawString(text, n + 36 + (116 - this.fontRendererObj.getStringWidth(text)) / 2, n2 + 48, 0);
            final String format2 = I18n.format("book.byAuthor", this.editingPlayer.getCommandSenderName());
            this.fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + format2, n + 36 + (116 - this.fontRendererObj.getStringWidth(format2)) / 2, n2 + 48 + 10, 0);
            this.fontRendererObj.drawSplitString(I18n.format("book.finalizeWarning", new Object[0]), n + 36, n2 + 80, 116, 0);
        }
        else {
            final String format3 = I18n.format("book.pageIndicator", this.currPage + 1, this.bookTotalPages);
            String s = "";
            if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
                s = this.bookPages.getStringTagAt(this.currPage);
            }
            if (this.bookIsUnsigned) {
                if (this.fontRendererObj.getBidiFlag()) {
                    s += "_";
                }
                else if (this.updateCount / 6 % 2 == 0) {
                    s = s + "" + EnumChatFormatting.BLACK + "_";
                }
                else {
                    s = s + "" + EnumChatFormatting.GRAY + "_";
                }
            }
            final int stringWidth = this.fontRendererObj.getStringWidth(format3);
            final FontRenderer fontRendererObj = this.fontRendererObj;
            final String text2 = format3;
            final int n8 = n - stringWidth;
            this.getClass();
            fontRendererObj.drawString(text2, n8 + 192 - 44, n2 + 16, 0);
            this.fontRendererObj.drawSplitString(s, n + 36, n2 + 16 + 16, 116, 0);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    static {
        GuiScreenBook.logger = LogManager.getLogger();
        GuiScreenBook.bookGuiTextures = new ResourceLocation("textures/gui/book.png");
    }
    
    public static class NextPageButton extends GuiButton
    {
        public boolean flag;
        
        public NextPageButton(final int buttonId, final int x, final int y, final boolean flag) {
            super(buttonId, x, y, 23, 13, "");
            this.flag = flag;
        }
        
        @Override
        public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
            if (this.visible) {
                final boolean b = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                mc.getTextureManager().bindTexture(GuiScreenBook.bookGuiTextures);
                int n = 0;
                int n2 = 192;
                if (b) {
                    n += 23;
                }
                if (!this.flag) {
                    n2 += 13;
                }
                Gui.drawTexturedRect((float)this.xPosition, (float)this.yPosition, (float)n, (float)n2, 23, 13);
            }
        }
    }
}
