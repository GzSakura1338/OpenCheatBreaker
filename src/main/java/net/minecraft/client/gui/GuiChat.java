package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.IntEvent5;
import com.cheatbreaker.client.event.SimpleEvent32;
import com.cheatbreaker.client.event.SimpleEvent36;
import com.cheatbreaker.client.util.ScreenshotHelper;
import com.cheatbreaker.client.util.TooltipCopyHelper;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiChat extends GuiScreen implements GuiYesNoCallback {
    public static Set supportedProtocols = Sets.newHashSet(new String[]{"http", "https"});
    public static Logger logger = LogManager.getLogger();
    public String historyBuffer = "";
    public int sentHistoryCursor = -1;
    public boolean playerNamesFound;
    public boolean waitingOnAutocomplete;
    public int autocompleteIndex;
    public List foundPlayerNames = new ArrayList();
    public URI clickedURI;
    public GuiTextField inputField;
    public String defaultInputFieldText = "";

    public GuiChat() {
    }

    public GuiChat(String str) {
        this.defaultInputFieldText = str;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        this.inputField = new GuiTextField(this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
        this.inputField.setMaxStringLength(100);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setFocused(true);
        this.inputField.setText(this.defaultInputFieldText);
        this.inputField.setCanLoseFocus(false);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        this.mc.ingameGUI.getChatGUI().resetScroll();
    }

    @Override
    public void updateScreen() {
        this.inputField.updateCursorCounter();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws java.io.IOException {
        if (!TooltipCopyHelper.isCopyKeyPressed(keyCode)
            || !this.inputField.getSelectedText().isEmpty()
            || !TooltipCopyHelper.copyToClipboard(this.mc.ingameGUI.getChatGUI().formatText(Mouse.getX(), Mouse.getY()))) {
            this.waitingOnAutocomplete = false;
            if (keyCode == 15) {
                this.autocompletePlayerNames();
            } else {
                this.playerNamesFound = false;
            }

            if (keyCode == 1) {
                this.mc.displayGuiScreen(null);
            } else if (keyCode == 28 || keyCode == 156) {
                String trim = this.inputField.getText().trim();
                if (trim.length() > 0) {
                    this.submitChatMessage(trim);
                }

                this.mc.displayGuiScreen(null);
            } else if (keyCode == 200) {
                this.getSentHistory(-1);
            } else if (keyCode == 208) {
                this.getSentHistory(1);
            } else if (keyCode == 201) {
                this.mc.ingameGUI.getChatGUI().scroll(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
            } else if (keyCode == 209) {
                this.mc.ingameGUI.getChatGUI().scroll(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
            } else {
                this.inputField.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }

    public void submitChatMessage(String str) {
        this.mc.ingameGUI.getChatGUI().addToSentMessages(str);
        this.mc.thePlayer.sendChatMessage(str);
    }

    @Override
    public void handleMouseInput() throws java.io.IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0) {
            if (eventDWheel > 1) {
                eventDWheel = 1;
            }

            if (eventDWheel < -1) {
                eventDWheel = -1;
            }

            if (!isShiftKeyDown()) {
                eventDWheel *= 7;
            }

            this.mc.ingameGUI.getChatGUI().scroll(eventDWheel);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException {
        if (!TooltipCopyHelper.isCopyMouseButtonPressed(mouseButton)
            || !TooltipCopyHelper.copyToClipboard(this.mc.ingameGUI.getChatGUI().formatText(Mouse.getX(), Mouse.getY()))) {
            if (mouseButton == 0 && this.mc.gameSettings.chatLinks) {
                IChatComponent chatComponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
                if (chatComponent != null) {
                    ClickEvent chatClickEvent = chatComponent.getChatStyle().getChatClickEvent();
                    if (chatClickEvent != null) {
                        if (isShiftKeyDown()) {
                            this.inputField.writeText(chatComponent.getUnformattedTextForChat());
                        } else if (chatClickEvent.getAction() == Action.OPEN_URL) {
                            try {
                                URI uri = new URI(chatClickEvent.getValue());
                                if (!supportedProtocols.contains(uri.getScheme().toLowerCase())) {
                                    throw new URISyntaxException(chatClickEvent.getValue(), "Unsupported protocol: " + uri.getScheme().toLowerCase());
                                }

                                if (this.mc.gameSettings.chatLinksPrompt) {
                                    this.clickedURI = uri;
                                    this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, chatClickEvent.getValue(), 0, false));
                                } else {
                                    this.openLink(uri);
                                }
                            } catch (URISyntaxException e) {
                                logger.error("Can't open url for " + chatClickEvent, e);
                            }
                        } else if (chatClickEvent.getAction() == Action.OPEN_FILE) {
                            URI toURI = new File(chatClickEvent.getValue()).toURI();
                            this.openLink(toURI);
                        } else if (chatClickEvent.getAction() == Action.SUGGEST_COMMAND) {
                            this.inputField.setText(chatClickEvent.getValue());
                        } else if (chatClickEvent.getAction() == Action.RUN_COMMAND) {
                            this.submitChatMessage(chatClickEvent.getValue());
                        } else if (chatClickEvent.getAction() == Action.UPLOAD_SCREENSHOT) {
                            this.handleString(chatClickEvent.getValue());
                        } else if (chatClickEvent.getAction() == Action.COPY_SCREENSHOT) {
                            ScreenshotHelper.copyToClipboard(chatClickEvent.getValue());
                        } else {
                            logger.error("Don't know how to handle " + chatClickEvent);
                        }

                        return;
                    }
                }
            }

            this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) throws java.io.IOException {
        if (id == 0) {
            if (result) {
                this.openLink(this.clickedURI);
            }

            this.clickedURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    public void openLink(URI uri) {
        try {
            Class forName = Class.forName("java.awt.Desktop");
            Object invoke = forName.getMethod("getDesktop").invoke(null);
            forName.getMethod("browse", URI.class).invoke(invoke, uri);
        } catch (Throwable e) {
            logger.error("Couldn't open link", e);
        }
    }

    public void autocompletePlayerNames() {
        if (this.playerNamesFound) {
            this.inputField
                .deleteFromCursor(this.inputField.getWordSkipPosition(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
            if (this.autocompleteIndex >= this.foundPlayerNames.size()) {
                this.autocompleteIndex = 0;
            }
        } else {
            int wordSkipPosition = this.inputField.getWordSkipPosition(-1, this.inputField.getCursorPosition(), false);
            this.foundPlayerNames.clear();
            this.autocompleteIndex = 0;
            String toLowerCase = this.inputField.getText().substring(wordSkipPosition).toLowerCase();
            String substring = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
            this.sendAutocompleteRequest(substring, toLowerCase);
            if (this.foundPlayerNames.isEmpty()) {
                return;
            }

            this.playerNamesFound = true;
            this.inputField.deleteFromCursor(wordSkipPosition - this.inputField.getCursorPosition());
        }

        if (this.foundPlayerNames.size() > 1) {
            StringBuilder sb = new StringBuilder();

            for (String str : (Iterable<String>)this.foundPlayerNames) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(str);
            }

            this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(sb.toString()), 1);
        }

        this.inputField.writeText((String)this.foundPlayerNames.get(this.autocompleteIndex++));
    }

    public void sendAutocompleteRequest(String str, String str2) {
        if (str.length() >= 1) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(str));
            this.waitingOnAutocomplete = true;
        }
    }

    public void getSentHistory(int n) {
        int n2 = this.sentHistoryCursor + n;
        int size = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        if (n2 < 0) {
            n2 = 0;
        }

        if (n2 > size) {
            n2 = size;
        }

        if (n2 != this.sentHistoryCursor) {
            if (n2 == size) {
                this.sentHistoryCursor = size;
                this.inputField.setText(this.historyBuffer);
            } else {
                if (this.sentHistoryCursor == size) {
                    this.historyBuffer = this.inputField.getText();
                }

                this.inputField.setText((String)this.mc.ingameGUI.getChatGUI().getSentMessages().get(n2));
                this.sentHistoryCursor = n2;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        SimpleEvent32 simpleEvent32 = new SimpleEvent32();
        CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent32);
        IntEvent5 intEvent5 = new IntEvent5(Integer.MIN_VALUE);
        CheatBreaker.getInstance().getEventBus().handleEvent(intEvent5);
        if (!intEvent5.isCancelled()) {
            drawRect(2.0F, this.height - 14, this.width - 2, this.height - 2, intEvent5.getMIN_VALUE());
        }

        this.inputField.drawTextBox();
        IChatComponent chatComponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
        if (chatComponent != null && chatComponent.getChatStyle().getChatHoverEvent() != null) {
            HoverEvent chatHoverEvent = chatComponent.getChatStyle().getChatHoverEvent();
            if (chatHoverEvent.getAction() == net.minecraft.event.HoverEvent.Action.SHOW_ITEM) {
                ItemStack itemStack = null;

                try {
                    NBTBase createNBTException = JsonToNBT.createNBTException(chatHoverEvent.getValue().getUnformattedText());
                    if (createNBTException != null && createNBTException instanceof NBTTagCompound) {
                        itemStack = ItemStack.loadItemStackFromNBT((NBTTagCompound)createNBTException);
                    }
                } catch (NBTException e) {
                }

                if (itemStack != null) {
                    this.renderToolTip(itemStack, mouseX, mouseY);
                } else {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", mouseX, mouseY);
                }
            } else if (chatHoverEvent.getAction() == net.minecraft.event.HoverEvent.Action.SHOW_TEXT) {
                this.drawHoveringText(Splitter.on("\n").splitToList(chatHoverEvent.getValue().getFormattedText()), mouseX, mouseY);
            } else if (chatHoverEvent.getAction() == net.minecraft.event.HoverEvent.Action.SHOW_ACHIEVEMENT) {
                StatBase oneShotStat = StatList.getOneShotStat(chatHoverEvent.getValue().getUnformattedText());
                if (oneShotStat != null) {
                    IChatComponent statName = oneShotStat.getStatName();
                    ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("stats.tooltip.type." + (oneShotStat.isAchievement() ? "achievement" : "statistic"));
                    chatComponentTranslation.getChatStyle().setItalic(true);
                    String str = oneShotStat instanceof Achievement ? ((Achievement)oneShotStat).getDescription() : null;
                    ArrayList newArrayList = Lists.newArrayList(new String[]{statName.getFormattedText(), chatComponentTranslation.getFormattedText()});
                    if (str != null) {
                        newArrayList.addAll(this.fontRendererObj.listFormattedStringToWidth(str, 150));
                    }

                    this.drawHoveringText(newArrayList, mouseX, mouseY);
                } else {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", mouseX, mouseY);
                }
            }

            GL11.glDisable(2896);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        SimpleEvent36 simpleEvent36 = new SimpleEvent36();
        CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent36);
    }

    public void onAutocompleteResponse(String[] strs) {
        if (this.waitingOnAutocomplete) {
            this.playerNamesFound = false;
            this.foundPlayerNames.clear();
            String[] strs2 = strs;
            int length = strs.length;

            for (int i = 0; i < length; i++) {
                String str = strs2[i];
                if (str.length() > 0) {
                    this.foundPlayerNames.add(str);
                }
            }

            String substring = this.inputField.getText().substring(this.inputField.getWordSkipPosition(-1, this.inputField.getCursorPosition(), false));
            String commonPrefix = StringUtils.getCommonPrefix(strs);
            if (commonPrefix.length() > 0 && !substring.equalsIgnoreCase(commonPrefix)) {
                this.inputField
                    .deleteFromCursor(this.inputField.getWordSkipPosition(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
                this.inputField.writeText(commonPrefix);
            } else if (this.foundPlayerNames.size() > 0) {
                this.playerNamesFound = true;
                this.autocompletePlayerNames();
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void handleString(String str) {
        File file = new File(this.mc.mcDataDir + File.separator + "screenshots" + File.separator + str);
        if (file.exists()) {
            GuiIngame.flag = true;
            new Thread(() -> {
                try {
                    BufferedImage read = ImageIO.read(file);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(read, "png", byteArrayOutputStream);
                    URL url = new URL("https://api.imgur.com/3/image");
                    String str2 = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(Base64.encodeBase64String(byteArrayOutputStream.toByteArray()), "UTF-8");
                    str2 = str2 + "&" + URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode("7fd132c453b5486", "UTF-8");
                    URLConnection openConnection = url.openConnection();
                    openConnection.setDoOutput(true);
                    openConnection.setDoInput(true);
                    openConnection.setRequestProperty("Authorization", "Client-ID 7fd132c453b5486");
                    openConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openConnection.getOutputStream());
                    outputStreamWriter.write(str2);
                    outputStreamWriter.flush();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();

                    String str3;
                    while ((str3 = bufferedReader.readLine()) != null) {
                        sb.append(str3).append(System.lineSeparator());
                    }

                    bufferedReader.close();
                    GuiIngame.flag = false;
                    Gson gson = new GsonBuilder().create();
                    JsonObject fromJson = (JsonObject)gson.fromJson(sb.toString(), JsonObject.class);
                    String str4 = "https://i.imgur.com/" + fromJson.get("data").getAsJsonObject().get("id").getAsString() + ".png";
                    this.clickedURI = new URI(str4);
                    GuiConfirmOpenLink guiConfirmOpenLink = new GuiConfirmOpenLink(this, str4, 0, true);
                    guiConfirmOpenLink.disableSecurityWarning();
                    this.mc.displayGuiScreen(guiConfirmOpenLink);
                } catch (Exception e) {
                    GuiIngame.flag = false;
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
