package com.cheatbreaker.client.minimap;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ImageUtils {
    public static BufferedImage blankImage(BufferedImage bufferedImage, int n, int n2) {
        return blankImage(bufferedImage, n, n2, 64, 32);
    }

    public static BufferedImage fillOutline(BufferedImage bufferedImage, boolean flag, boolean flag2, boolean flag3, int n) {
        if (flag && n != 2 && n != 3 && n != -1) {
            bufferedImage = fillOutline(bufferedImage, true, flag2, flag3);
        }

        return fillOutline(bufferedImage, false, flag2, flag3);
    }

    public static BufferedImage blankImage(String str, int n, int n2, int n3, int n4, int n5, int n6) {
        return blankImage(str, n, n2, 64, 32, n3, n4, n5, n6);
    }

    public static BufferedImage crop(BufferedImage bufferedImage, int n, int n2, int n3, int n4, int n5, int n6) {
        if (bufferedImage.getType() != 6) {
            BufferedImage bufferedImage2 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), 6);
            Graphics2D createGraphics = bufferedImage2.createGraphics();
            createGraphics.drawImage(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
            createGraphics.dispose();
            bufferedImage = bufferedImage2;
        }

        float f = bufferedImage.getWidth(null) / n5;
        return bufferedImage.getSubimage((int)(n * f), (int)(n2 * f), (int)(n3 * f), (int)(n4 * f));
    }

    public static BufferedImage trimTransparent(BufferedImage bufferedImage) {
        int n = -1;
        int width = bufferedImage.getWidth();
        int n2 = -1;
        int height = bufferedImage.getHeight();
        boolean flag = false;
        int n3 = 0;

        while (!flag) {
            n++;

            for (int i = 0; i < bufferedImage.getHeight(); i++) {
                n3 = bufferedImage.getRGB(n, i);
                if (n3 >> 24 != 0) {
                    flag = true;
                }
            }
        }

        flag = false;

        while (!flag) {
            width--;

            for (int i = 0; i < bufferedImage.getHeight(); i++) {
                n3 = bufferedImage.getRGB(width, i);
                if (n3 >> 24 != 0) {
                    flag = true;
                }
            }
        }

        flag = false;

        while (!flag) {
            n2++;

            for (int i = 0; i < bufferedImage.getWidth(); i++) {
                n3 = bufferedImage.getRGB(i, n2);
                if (n3 >> 24 != 0) {
                    flag = true;
                }
            }
        }

        flag = false;

        while (!flag) {
            height--;

            for (int i = 0; i < bufferedImage.getWidth(); i++) {
                n3 = bufferedImage.getRGB(i, height);
                if (n3 >> 24 != 0) {
                    flag = true;
                }
            }
        }

        return bufferedImage.getSubimage(n, n2, width - n + 1, height - n2 + 1);
    }

    public static BufferedImage fillOutline(BufferedImage bufferedImage, boolean flag, boolean flag2, boolean flag3) {
        float f = flag3 ? 2.29F : 4.0F;
        BufferedImage bufferedImage2 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        Graphics graphics = bufferedImage2.getGraphics();
        graphics.drawImage(bufferedImage, 0, 0, null);
        graphics.dispose();
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                int rGB = bufferedImage.getRGB(j, i);
                if ((rGB >> 24 & 0xFF) == 0) {
                    int neighborColor = getNeighborColor(j, i, bufferedImage);
                    if (neighborColor != 65116) {
                        if (flag) {
                            if (flag2 && !(i <= width / f) && !(i >= width - 1 - width / f) && !(j <= height / f) && !(j >= height - 1 - height / f)) {
                                neighborColor = 0;
                            } else {
                                neighborColor = -16777216;
                            }
                        } else {
                            int n = neighborColor >> 16 & 0xFF;
                            int n2 = neighborColor >> 8 & 0xFF;
                            int n3 = neighborColor >> 0 & 0xFF;
                            neighborColor = 0 | (n & 0xFF) << 16 | (n2 & 0xFF) << 8 | n3 & 0xFF;
                        }

                        bufferedImage2.setRGB(j, i, neighborColor);
                    }
                }
            }
        }

        return bufferedImage2;
    }

    public static BufferedImage centerIn128(BufferedImage bufferedImage) {
        BufferedImage bufferedImage2 = new BufferedImage(128, 128, bufferedImage.getType());
        Graphics graphics = bufferedImage2.getGraphics();
        graphics.drawImage(bufferedImage, 64 - bufferedImage.getWidth() / 2, 64 - bufferedImage.getHeight() / 2, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        graphics.dispose();
        return bufferedImage2;
    }

    public static BufferedImage blankImage(String str, int n, int n2) {
        return blankImage(str, n, n2, 64, 32);
    }

    public static int getNeighborColor(int n, int n2, BufferedImage bufferedImage) {
        if (n > 0) {
            int rGB = bufferedImage.getRGB(n - 1, n2);
            if ((rGB >> 24 & 0xFF) > 50) {
                return rGB;
            }
        }

        if (n < bufferedImage.getWidth() - 1) {
            int rGB = bufferedImage.getRGB(n + 1, n2);
            if ((rGB >> 24 & 0xFF) > 50) {
                return rGB;
            }
        }

        if (n2 > 0) {
            int rGB = bufferedImage.getRGB(n, n2 - 1);
            if ((rGB >> 24 & 0xFF) > 50) {
                return rGB;
            }
        }

        if (n2 < bufferedImage.getHeight() - 1) {
            int rGB = bufferedImage.getRGB(n, n2 + 1);
            if ((rGB >> 24 & 0xFF) > 50) {
                return rGB;
            }
        }

        if (n > 0 && n2 > 0) {
            int rGB = bufferedImage.getRGB(n - 1, n2 - 1);
            if ((rGB >> 24 & 0xFF) > 50) {
                return rGB;
            }
        }

        if (n > 0 && n2 < bufferedImage.getHeight() - 1) {
            int rGB = bufferedImage.getRGB(n - 1, n2 + 1);
            if ((rGB >> 24 & 0xFF) > 50) {
                return rGB;
            }
        }

        if (n < bufferedImage.getWidth() - 1 && n2 > 0) {
            int rGB = bufferedImage.getRGB(n + 1, n2 - 1);
            if ((rGB >> 24 & 0xFF) > 50) {
                return rGB;
            }
        }

        if (n < bufferedImage.getWidth() - 1 && n2 < bufferedImage.getHeight() - 1) {
            int rGB = bufferedImage.getRGB(n + 1, n2 + 1);
            if ((rGB >> 24 & 0xFF) > 50) {
                return rGB;
            }
        }

        return 65116;
    }

    public static BufferedImage fillOutline(BufferedImage bufferedImage, boolean flag) {
        return fillOutline(bufferedImage, flag, false, false, 0);
    }

    public static BufferedImage flipHorizontal(BufferedImage bufferedImage) {
        AffineTransform scaleInstance = AffineTransform.getScaleInstance(-1.0, 1.0);
        scaleInstance.translate(-bufferedImage.getWidth(null), 0.0);
        AffineTransformOp affineTransformOp = new AffineTransformOp(scaleInstance, 1);
        return affineTransformOp.filter(bufferedImage, null);
    }

    public static BufferedImage blankImage(BufferedImage bufferedImage, int n, int n2, int n3, int n4) {
        return blankImage(bufferedImage, n, n2, n3, n4, 0, 0, 0, 0);
    }

    public static BufferedImage blankImage(String str, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        try {
            InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(str)).getInputStream();
            BufferedImage read = ImageIO.read(inputStream);
            inputStream.close();
            BufferedImage bufferedImage = new BufferedImage(n * read.getWidth() / n3, n2 * read.getWidth() / n3, 6);
            Graphics2D createGraphics = bufferedImage.createGraphics();
            createGraphics.setColor(new Color(n5, n6, n7, n8));
            createGraphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
            createGraphics.dispose();
            return bufferedImage;
        } catch (Exception e) {
            System.err.println("Failed getting mob: " + str + " - " + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage scale(BufferedImage bufferedImage, float f) {
        BufferedImage bufferedImage2 = new BufferedImage((int)(bufferedImage.getWidth() * f), (int)(bufferedImage.getHeight() * f), bufferedImage.getType());
        Graphics2D createGraphics = bufferedImage2.createGraphics();
        createGraphics.drawImage(bufferedImage, 0, 0, (int)(bufferedImage.getWidth() * f), (int)(bufferedImage.getHeight() * f), null);
        createGraphics.dispose();
        return bufferedImage2;
    }

    public static BufferedImage blankImage(String str, int n, int n2, int n3, int n4) {
        return blankImage(str, n, n2, n3, n4, 0, 0, 0, 0);
    }

    public static BufferedImage loadImage(ResourceLocation resourceLocation, int n, int n2, int n3, int n4) {
        return loadImage(resourceLocation, n, n2, n3, n4, 64, 32);
    }

    public static BufferedImage overlayImage(BufferedImage bufferedImage, BufferedImage bufferedImage2, float f, int n, int n2, int n3) {
        int n4 = bufferedImage.getWidth() / n2;
        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawImage(bufferedImage2, (int)(f * n4), n * n4, null);
        graphics.dispose();
        return bufferedImage;
    }

    public static BufferedImage createImageFromTexture(int n) {
        GL11.glBindTexture(3553, n);
        return createImageFromBoundTexture();
    }

    public static BufferedImage crop(BufferedImage bufferedImage, int n, int n2, int n3, int n4) {
        return crop(bufferedImage, n, n2, n3, n4, 64, 32);
    }

    public static BufferedImage loadImage(ResourceLocation resourceLocation, int n, int n2, int n3, int n4, int n5, int n6) {
        try {
            InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
            BufferedImage read = ImageIO.read(inputStream);
            inputStream.close();
            return crop(read, n, n2, n3, n4, n5, n6);
        } catch (Exception e) {
            System.err.println("Failed getting mob: " + resourceLocation.toString() + " - " + e.getLocalizedMessage());
            return null;
        }
    }

    public static BufferedImage blankImage(BufferedImage bufferedImage, int n, int n2, int n3, int n4, int n5, int n6) {
        return blankImage(bufferedImage, n, n2, 64, 32, n3, n4, n5, n6);
    }

    public static BufferedImage blankImage(BufferedImage bufferedImage, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        BufferedImage bufferedImage2 = new BufferedImage(n * bufferedImage.getWidth() / n3, n2 * bufferedImage.getWidth() / n3, 6);
        Graphics2D createGraphics = bufferedImage2.createGraphics();
        createGraphics.setColor(new Color(n5, n6, n7, n8));
        createGraphics.fillRect(0, 0, bufferedImage2.getWidth(), bufferedImage2.getHeight());
        createGraphics.dispose();
        return bufferedImage2;
    }

    public static float getEdgeOpacity(BufferedImage bufferedImage) {
        int n = bufferedImage.getWidth() * 2 + bufferedImage.getHeight() * 2 - 2;
        int n2 = 0;
        int n3 = 0;

        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            n3 = bufferedImage.getRGB(0, i);
            if (n3 >> 24 != 0) {
                n2++;
            }

            n3 = bufferedImage.getRGB(bufferedImage.getWidth() - 1, i);
            if (n3 >> 24 != 0) {
                n2++;
            }
        }

        for (int i = 1; i < bufferedImage.getWidth() - 1; i++) {
            n3 = bufferedImage.getRGB(i, 0);
            if (n3 >> 24 != 0) {
                n2++;
            }

            n3 = bufferedImage.getRGB(i, bufferedImage.getHeight() - 1);
            if (n3 >> 24 != 0) {
                n2++;
            }
        }

        return n2 / n;
    }

    public static BufferedImage padToPowerOfTwo(BufferedImage bufferedImage) {
        int max = Math.max(bufferedImage.getWidth(), bufferedImage.getHeight());
        int n = 1;

        while (Math.pow(2.0, n - 1) < max) {
            n++;
        }

        int pow = (int)Math.pow(2.0, n);
        BufferedImage bufferedImage2 = new BufferedImage(pow, pow, bufferedImage.getType());
        Graphics graphics = bufferedImage2.getGraphics();
        graphics.drawImage(bufferedImage, (pow - bufferedImage.getWidth()) / 2, (pow - bufferedImage.getHeight()) / 2, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        graphics.dispose();
        return bufferedImage2;
    }

    public static BufferedImage drawQuestionMark(BufferedImage bufferedImage, String str) {
        Graphics2D createGraphics = bufferedImage.createGraphics();
        createGraphics.setColor(new Color(0, 0, 0, 255));
        createGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        createGraphics.setFont(new Font("Arial", 0, bufferedImage.getHeight()));
        FontMetrics fontMetrics = createGraphics.getFontMetrics();
        int n = (bufferedImage.getWidth() - fontMetrics.stringWidth("?")) / 2;
        int n2 = fontMetrics.getAscent() + (bufferedImage.getHeight() - (fontMetrics.getAscent() + fontMetrics.getDescent())) / 2;
        createGraphics.drawString("?", n, n2);
        createGraphics.dispose();
        return bufferedImage;
    }

    public static BufferedImage createImageFromBoundTexture() {
        int glGetTexLevelParameteri = GL11.glGetTexLevelParameteri(3553, 0, 4096);
        int glGetTexLevelParameteri2 = GL11.glGetTexLevelParameteri(3553, 0, 4097);
        ByteBuffer order = ByteBuffer.allocateDirect(glGetTexLevelParameteri * glGetTexLevelParameteri2 * 4).order(ByteOrder.nativeOrder());
        GL11.glGetTexImage(3553, 0, 6408, 5121, order);
        BufferedImage bufferedImage = new BufferedImage(glGetTexLevelParameteri, glGetTexLevelParameteri2, 6);
        ((Buffer)order).position(0);
        byte[] bytes = new byte[order.remaining()];
        order.get(bytes);

        for (int i = 0; i < glGetTexLevelParameteri; i++) {
            for (int j = 0; j < glGetTexLevelParameteri2; j++) {
                int n = j * glGetTexLevelParameteri * 4 + i * 4;
                byte b = 0;
                int n2 = b | (bytes[n + 2] & 255) << 0;
                n2 |= (bytes[n + 1] & 255) << 8;
                n2 |= (bytes[n + 0] & 255) << 16;
                n2 |= (bytes[n + 3] & 255) << 24;
                bufferedImage.setRGB(i, j, n2);
            }
        }

        return bufferedImage;
    }
}
