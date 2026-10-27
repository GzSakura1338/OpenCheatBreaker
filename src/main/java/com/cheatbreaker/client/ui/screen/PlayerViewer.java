package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.cosmetic.SkinModel;
import com.cheatbreaker.client.ui.notification.Notification;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.ResourceLocation;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class PlayerViewer {
   public static final float CONSTANT_2_5 = 2.5F;
   public static boolean flag = true;
   public static ResourceLocation guiButtonPress;
   public static PreviewPlayer previewPlayer;
   public static float floatValue = 180.0F;
   public static ResourceLocation resourceLocation;
   public static float floatValue2;
   public static final String TEXT_HTTPS_API_MINECRAFTSERVI = "https://api.minecraftservices.com/minecraft/profile/skins";
   public static boolean flag2;
   public static int intValue = 0;

   public static void run() {
      Minecraft minecraft = Minecraft.getMinecraft();
      if (previewPlayer != null && minecraft.thePlayer == previewPlayer) {
         minecraft.thePlayer = null;
      }

      PreviewWorld previewWorld = new PreviewWorld(minecraft);
      previewPlayer = new PreviewPlayer(minecraft, previewWorld, new PreviewNetHandler(minecraft), new StatFileWriter());
      previewPlayer.dimension = 0;
      previewPlayer.movementInput = new MovementInputFromOptions(minecraft.gameSettings);
      previewPlayer.capabilities = new PlayerCapabilities();
      RenderManager.renderManager.renderEngine = minecraft.getTextureManager();
      RenderManager.renderManager.livingPlayer = previewPlayer;
      RenderManager.renderManager.options = minecraft.gameSettings;
      RenderManager.renderManager.worldObj = previewWorld;
      RenderManager.renderManager.textRenderer = minecraft.fontRendererObj;
      previewPlayer.posX = 0.0;
      previewPlayer.prevPosX = 0.0;
      previewPlayer.posY = 0.0;
      previewPlayer.prevPosY = 0.0;
      previewPlayer.posZ = 0.0;
      previewPlayer.prevPosZ = 0.0;
      previewPlayer.motionX = 0.0;
      previewPlayer.motionY = 0.0;
      previewPlayer.motionZ = 0.0;
      previewPlayer.preparePlayerToSpawn();
      if (minecraft.thePlayer == null) {
         minecraft.thePlayer = previewPlayer;
      }
   }

   public static ResourceLocation findResourceLocation(String str, String str2) throws java.io.IOException {
      File file = new File(str2);
      InputStream newInputStream = Files.newInputStream(file.toPath());
      BufferedImage read = ImageIO.read(newInputStream);
      read = new ImageBufferDownload().parseUserSkin(read);
      DynamicTexture dynamicTexture = new DynamicTexture(read);
      return Minecraft.getMinecraft().renderEngine.getDynamicTextureLocation(str, dynamicTexture);
   }

   public static void handleFile(File file, SkinModel skinModel) {
      String str = "";

      try {
         CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
         String token = Minecraft.getMinecraft().getSession().getToken();
         MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
         multipartEntityBuilder.addTextBody("variant", skinModel.getText(), ContentType.TEXT_PLAIN);
         multipartEntityBuilder.addBinaryBody("file", Files.newInputStream(file.toPath()), ContentType.IMAGE_PNG, "skin.png");
         HttpPost httpPost = new HttpPost("https://api.minecraftservices.com/minecraft/profile/skins");
         httpPost.setEntity(multipartEntityBuilder.build());
         httpPost.addHeader("Authorization", "Bearer " + token);
         HttpResponse execute = closeableHttpClient.execute(httpPost);
         int statusCode = execute.getStatusLine().getStatusCode();
         if (statusCode == 401) {
            str = EnumChatFormatting.RED + "Invalid Token, try logging out and back in again,";
         }

         if (statusCode == 200) {
            str = "Skin Changed Successfully!";
         }
      } catch (Exception e) {
         e.printStackTrace();
         str = EnumChatFormatting.RED + "Failed to change skin, file invalid?";
      }

      Notification.post("Skin Changer", str);
   }

   public static void render(float f, float f2, float f3, float f4, float f5, float f6) {
      PreviewPlayer previewPlayer2 = previewPlayer;
      if (Minecraft.getMinecraft().thePlayer == null) {
         Minecraft.getMinecraft().thePlayer = previewPlayer;
      } else if (RenderManager.renderManager.livingPlayer != previewPlayer) {
         RenderManager.renderManager.livingPlayer = previewPlayer;
      }

      boolean flag3 = f5 > f - 30.0F && f5 < f + 30.0F && f6 > f2 - 70.0F && f6 < f2 + 70.0F;
      if (flag2 && !Mouse.isButtonDown(0)) {
         flag2 = false;
      } else if (!flag2 && Mouse.isButtonDown(0)) {
         flag2 = flag3;
         if (flag2) {
            floatValue2 = f5;
         }
      }

      if (flag2 && flag) {
         floatValue = floatValue + (f5 - floatValue2) * 2.5F;
         floatValue2 = f5;
      }

      float f7 = 70.0F;
      GlStateManager.enableColorMaterial();
      GlStateManager.pushMatrix();
      GlStateManager.translate(f, f2, 50.0F);
      GL11.glScalef(-f7, f7, f7);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      float renderYawOffset = previewPlayer2.renderYawOffset;
      GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-((float)Math.atan(f4 / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
      previewPlayer2.renderYawOffset = (float)Math.atan(f3 / 40.0F) * 20.0F - floatValue;
      previewPlayer2.rotationYaw = (float)Math.atan(f3 / 40.0F) * 40.0F - floatValue;
      previewPlayer2.rotationPitch = -((float)Math.atan(f4 / 40.0F)) * 20.0F;
      previewPlayer2.rotationYawHead = previewPlayer2.rotationYaw;
      previewPlayer2.prevRotationYawHead = previewPlayer2.rotationYaw;
      GL11.glTranslatef(0.0F, (float)(previewPlayer2.getYOffset() - 0.4F), 0.0F);
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      renderManager.setPlayerViewY(180.0F);
      renderManager.setPlayerViewY(180.0F);
      renderManager.setRenderShadow(false);
      if (flag3 && !flag2) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
      } else {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      renderManager.renderEntityWithPosYaw(previewPlayer2, 0.0, 0.0, 0.0, 0.0F, 1.0F);
      renderManager.setRenderShadow(true);
      previewPlayer2.renderYawOffset = renderYawOffset;
      previewPlayer2.rotationYaw = 0.0F;
      previewPlayer2.rotationPitch = 0.0F;
      previewPlayer2.prevRotationYawHead = 0.0F;
      previewPlayer2.rotationYawHead = 0.0F;
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }
}
