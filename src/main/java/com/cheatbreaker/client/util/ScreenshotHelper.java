package com.cheatbreaker.client.util;

import com.cheatbreaker.client.CheatBreaker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class ScreenshotHelper {
    public static Logger logger = LogManager.getLogger();
    public static int[] intArray;
    public static IntBuffer intBuffer;
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    public static void copyToClipboard(String str) {
        File file = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "screenshots" + File.separator + str);
        ArrayList list = new ArrayList();
        list.add(file);
        ImageTransferable imageTransferable = new ImageTransferable(list);

        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imageTransferable, (clipboard, transferable) -> System.out.println("Lost ownership"));
        } catch (IllegalStateException e) {
            CheatBreaker.getInstance().getModManager().notificationsMod.addNotification("Info", "&cCouldn't copy &fscreenshot, clipboard is in use.", 2000L);
            return;
        }

        CheatBreaker.getInstance().getModManager().notificationsMod.addNotification("Info", "&9Copied &fscreenshot.", 2000L);
    }

    public static IChatComponent saveScreenshot2(File file, int n, int n2, Framebuffer framebuffer) {
        return saveScreenshot(file, null, n, n2, framebuffer);
    }

    public static File nextScreenshotFile(File file) {
        String format = dateFormat.format(new Date());
        int n = 1;

        File file2;
        while ((file2 = new File(file, format + (n == 1 ? "" : "_" + n) + ".png")).exists()) {
            n++;
        }

        return file2;
    }

    public static IChatComponent saveScreenshot(File file, String str, int n, int n2, Framebuffer framebuffer) {
        try {
            File file2 = new File(file, "screenshots");
            if (!file2.exists()) {
                file2.mkdir();
            }

            if (OpenGlHelper.isFramebufferEnabled()) {
                n = framebuffer.framebufferTextureWidth;
                n2 = framebuffer.framebufferTextureHeight;
            }

            int n3 = n * n2;
            if (intBuffer == null || intBuffer.capacity() < n3) {
                intBuffer = BufferUtils.createIntBuffer(n3);
                intArray = new int[n3];
            }

            GL11.glPixelStorei(3333, 1);
            GL11.glPixelStorei(3317, 1);
            ((Buffer)intBuffer).clear();
            if (OpenGlHelper.isFramebufferEnabled()) {
                GL11.glBindTexture(3553, framebuffer.framebufferTexture);
                GL11.glGetTexImage(3553, 0, 32993, 33639, intBuffer);
            } else {
                GL11.glReadPixels(0, 0, n, n2, 32993, 33639, intBuffer);
            }

            intBuffer.get(intArray);
            TextureUtil.flipXY(intArray, n, n2);
            BufferedImage bufferedImage = null;
            if (OpenGlHelper.isFramebufferEnabled()) {
                bufferedImage = new BufferedImage(framebuffer.framebufferWidth, framebuffer.framebufferHeight, 1);

                int n4;
                for (int i = n4 = framebuffer.framebufferTextureHeight - framebuffer.framebufferHeight; i < framebuffer.framebufferTextureHeight; i++) {
                    for (int j = 0; j < framebuffer.framebufferWidth; j++) {
                        bufferedImage.setRGB(j, i - n4, intArray[i * framebuffer.framebufferTextureWidth + j]);
                    }
                }
            } else {
                bufferedImage = new BufferedImage(n, n2, 1);
                bufferedImage.setRGB(0, 0, n, n2, intArray, 0, n);
            }

            File file3 = str == null ? nextScreenshotFile(file2) : new File(file2, str);
            ImageIO.write(bufferedImage, "png", file3);
            ChatComponentText chatComponentText = new ChatComponentText(file3.getName());
            chatComponentText.getChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_FILE, file3.getAbsolutePath()));
            chatComponentText.getChatStyle().setUnderlined(true);
            return new ChatComponentTranslation("screenshot.success", chatComponentText);
        } catch (Exception e) {
            logger.warn("Couldn't save screenshot", e);
            return new ChatComponentTranslation("screenshot.failure", e.getMessage());
        }
    }

    public static void saveScreenshot(File file, int n, int n2, Framebuffer framebuffer) {
        try {
            if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().makeShutterSound.getValue()) {
                CheatBreaker.getInstance().getAudioManager().playSound("shutter");
            }

            File file2 = new File(file, "screenshots");
            if (!file2.exists()) {
                file2.mkdir();
            }

            if (OpenGlHelper.isFramebufferEnabled()) {
                n = framebuffer.framebufferTextureWidth;
                n2 = framebuffer.framebufferTextureHeight;
            }

            int n3 = n * n2;
            if (intBuffer == null || intBuffer.capacity() < n3) {
                intBuffer = BufferUtils.createIntBuffer(n3);
                intArray = new int[n3];
            }

            GL11.glPixelStorei(3333, 1);
            GL11.glPixelStorei(3317, 1);
            ((Buffer)intBuffer).clear();
            if (OpenGlHelper.isFramebufferEnabled()) {
                GL11.glBindTexture(3553, framebuffer.framebufferTexture);
                GL11.glGetTexImage(3553, 0, 32993, 33639, intBuffer);
            } else {
                GL11.glReadPixels(0, 0, n, n2, 32993, 33639, intBuffer);
            }

            intBuffer.get(intArray);
            int n4 = n;
            int n5 = n2;
            new Thread(
                    () -> {
                        TextureUtil.flipXY(intArray, n4, n5);
                        BufferedImage bufferedImage = null;
                        if (OpenGlHelper.isFramebufferEnabled()) {
                            bufferedImage = new BufferedImage(framebuffer.framebufferWidth, framebuffer.framebufferHeight, 1);

                            int n6;
                            for (int i = n6 = framebuffer.framebufferTextureHeight - framebuffer.framebufferHeight; i < framebuffer.framebufferTextureHeight; i++) {
                                for (int j = 0; j < framebuffer.framebufferWidth; j++) {
                                    bufferedImage.setRGB(j, i - n6, intArray[i * framebuffer.framebufferTextureWidth + j]);
                                }
                            }
                        } else {
                            bufferedImage = new BufferedImage(n4, n5, 1);
                            bufferedImage.setRGB(0, 0, n4, n5, intArray, 0, n4);
                        }

                        File nextScreenshotFile2 = nextScreenshotFile(file2);

                        try {
                            ImageIO.write(bufferedImage, "png", nextScreenshotFile2);
                            ChatComponentText chatComponentText = new ChatComponentText(
                                EnumChatFormatting.GOLD
                                    + ""
                                    + EnumChatFormatting.BOLD
                                    + ((boolean)CheatBreaker.getInstance().getKeybindSettings().compactOptions.getValue() ? " [OPN]" : " [Open]")
                            );
                            chatComponentText.getChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_FILE, nextScreenshotFile2.getCanonicalPath()));
                            chatComponentText.getChatStyle()
                                .setChatHoverEvent(new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_TEXT, new ChatComponentText(nextScreenshotFile2.getName())));
                            ChatComponentText chatComponentText2 = new ChatComponentText(
                                EnumChatFormatting.BLUE
                                    + ""
                                    + EnumChatFormatting.BOLD
                                    + ((boolean)CheatBreaker.getInstance().getKeybindSettings().compactOptions.getValue() ? " [CPY]" : " [Copy]")
                            );
                            chatComponentText2.getChatStyle().setChatClickEvent(new ClickEvent(Action.COPY_SCREENSHOT, nextScreenshotFile2.getName()));
                            chatComponentText2.getChatStyle()
                                .setChatHoverEvent(new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Copy the screenshot")));
                            ChatComponentText chatComponentText3 = new ChatComponentText(
                                EnumChatFormatting.GREEN
                                    + ""
                                    + EnumChatFormatting.BOLD
                                    + ((boolean)CheatBreaker.getInstance().getKeybindSettings().compactOptions.getValue() ? " [UPL]" : " [Upload]")
                            );
                            chatComponentText3.getChatStyle().setChatClickEvent(new ClickEvent(Action.UPLOAD_SCREENSHOT, nextScreenshotFile2.getName()));
                            chatComponentText3.getChatStyle()
                                .setChatHoverEvent(
                                    new HoverEvent(
                                        net.minecraft.event.HoverEvent.Action.SHOW_TEXT,
                                        new ChatComponentText("Upload to " + EnumChatFormatting.GREEN + "imgur.com & open")
                                    )
                                );
                            ChatComponentText chatComponentText4 = new ChatComponentText(
                                "Saved"
                                    + ((boolean)CheatBreaker.getInstance().getKeybindSettings().copyScreenshotAutomatically.getValue() ? " and copied " : " ")
                                    + "screenshot"
                            );
                            chatComponentText4.getChatStyle().setUnderlined(true);
                            if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().showOpenOption.getValue()) {
                                chatComponentText4.appendSibling(chatComponentText);
                            }

                            if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().showCopyOption.getValue()) {
                                chatComponentText4.appendSibling(chatComponentText2);
                            }

                            if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().showUploadOption.getValue()) {
                                chatComponentText4.appendSibling(chatComponentText3);
                            }

                            if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().copyScreenshotAutomatically.getValue()) {
                                copyToClipboard(nextScreenshotFile2.getName());
                            }

                            if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().sendMessage.getValue()) {
                                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(chatComponentText4);
                            }
                        } catch (IOException e) {
                            logger.warn("Couldn't save screenshot");
                        }
                    }
                )
                .start();
        } catch (Exception e) {
            logger.warn("Couldn't save screenshot", e);
        }
    }
}
