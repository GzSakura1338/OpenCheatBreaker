package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.SimpleEvent3;
import com.cheatbreaker.client.ui.Side;
import com.cheatbreaker.client.util.FastTrig;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class TeamManager {
   public int[] intArray = new int[]{-15007996, -43234, -3603713, -16580641, -8912129, -16601345, -2786, -64828, -15629042, -10744187};
   public Minecraft mc = Minecraft.getMinecraft();
   public FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
   public FloatBuffer floatBuffer2 = BufferUtils.createFloatBuffer(16);
   public boolean flag = false;
   public List<TeamMember> teamMemberList = new ArrayList<>();

   public FloatBuffer getFloatBuffer() {
      return this.floatBuffer;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void render(TeamMember teamMember, Side side, float f, float f2) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      if (teamMember.isLeader()) {
         GlStateManager.color(0.0F, 0.0F, 1.0F, 0.66F);
      } else {
         Color color = teamMember.getColor();
         GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 0.66F);
      }

      float f3 = 8.0F;
      float f4 = 10.0F;
      GlStateManager.pushMatrix();
      GL11.glTranslatef(f, f2, 0.0F);
      switch (side) {
         case TOP:
            worldRenderer.begin(7, DefaultVertexFormats.POSITION);
            worldRenderer.pos(f3 / 2.0F, f4 / 2.0F, 0.0).endVertex();
            worldRenderer.pos(-f3 / 2.0F, 0.0, 0.0).endVertex();
            worldRenderer.pos(f3 / 2.0F, -f4 / 2.0F, 0.0).endVertex();
            worldRenderer.pos(-f3 / 2.0F, 0.0, 0.0).endVertex();
            instance.draw();
            break;
         case LEFT:
            worldRenderer.begin(7, DefaultVertexFormats.POSITION);
            worldRenderer.pos(-f3 / 2.0F, f4 / 2.0F, 0.0).endVertex();
            worldRenderer.pos(f3 / 2.0F, 0.0, 0.0).endVertex();
            worldRenderer.pos(-f3 / 2.0F, -f4 / 2.0F, 0.0).endVertex();
            worldRenderer.pos(f3 / 2.0F, 0.0, 0.0).endVertex();
            instance.draw();
            break;
         case BOTTOM:
            worldRenderer.begin(7, DefaultVertexFormats.POSITION);
            worldRenderer.pos(-f3 / 2.0F, -f4 / 2.0F, 0.0).endVertex();
            worldRenderer.pos(0.0, f4 / 2.0F, 0.0).endVertex();
            worldRenderer.pos(f3 / 2.0F, -f4 / 2.0F, 0.0).endVertex();
            worldRenderer.pos(0.0, f4 / 2.0F, 0.0).endVertex();
            instance.draw();
            break;
         case RIGHT:
            worldRenderer.begin(7, DefaultVertexFormats.POSITION);
            worldRenderer.pos(-f3 / 2.0F, f4 / 2.0F, 0.0).endVertex();
            worldRenderer.pos(0.0, -f4 / 2.0F, 0.0).endVertex();
            worldRenderer.pos(f3 / 2.0F, f4 / 2.0F, 0.0).endVertex();
            worldRenderer.pos(0.0, -f4 / 2.0F, 0.0).endVertex();
            instance.draw();
      }

      GlStateManager.popMatrix();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public List<TeamMember> getTeamMemberList() {
      return this.teamMemberList;
   }

   public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
      if (!this.teamMemberList.isEmpty()) {
         IntBuffer createIntBuffer = BufferUtils.createIntBuffer(16);
         GL11.glGetInteger(2978, createIntBuffer);
         float renderPartialTicks = this.mc.timer.renderPartialTicks;
         float f = (float)(this.mc.thePlayer.lastTickPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.lastTickPosX) * renderPartialTicks);
         float f2 = (float)(this.mc.thePlayer.lastTickPosY + (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * renderPartialTicks);
         float f3 = (float)(this.mc.thePlayer.lastTickPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.lastTickPosZ) * renderPartialTicks);
         double d = (this.mc.thePlayer.rotationPitch + 90.0F) * Math.PI / 180.0;
         double d2 = (this.mc.thePlayer.rotationYaw + 90.0F) * Math.PI / 180.0;
         Vec3 vec3 = new Vec3(FastTrig.sin(d) * FastTrig.cos(d2), FastTrig.cos(d), FastTrig.sin(d) * FastTrig.sin(d2));
         if (this.mc.gameSettings.thirdPersonView == 2) {
            vec3 = new Vec3(vec3.xCoord * -1.0, vec3.yCoord * -1.0, vec3.zCoord * -1.0);
         }

         for (TeamMember teamMember : this.teamMemberList) {
            EntityPlayer findEntityPlayer = this.mc.theWorld.findEntityPlayer(teamMember.getPlayerId());
            if (findEntityPlayer == null) {
               if (System.currentTimeMillis() - teamMember.getUpdatedAt() <= teamMember.getUpdateInterval()) {
                  double d3 = teamMember.getVec3().zCoord - f;
                  double d4 = teamMember.getVec3().yCoord - f2;
                  double d5 = teamMember.getVec3().zCoord - f3;
                  double sqrt = this.sqrt(teamMember.getVec3().xCoord, teamMember.getVec3().yCoord, teamMember.getVec3().zCoord);
                  double d6;
                  if (sqrt > (d6 = this.mc.gameSettings.getOptionFloatValue(Options.RENDER_DISTANCE) * 16.0F)) {
                     d3 = d3 / sqrt * d6;
                     d4 = d4 / sqrt * d6;
                     d5 = d5 / sqrt * d6;
                  }

                  this.render(scaledResolutionEvent3.getScaledResolution(), teamMember, (float)d3, (float)d4, (float)d5, createIntBuffer, vec3, (int)sqrt);
               }
            } else if (findEntityPlayer != this.mc.thePlayer) {
               float f4 = (float)(findEntityPlayer.lastTickPosX + (findEntityPlayer.posX - findEntityPlayer.lastTickPosX) * renderPartialTicks - f);
               float f5 = (float)(findEntityPlayer.lastTickPosY + (findEntityPlayer.posY - findEntityPlayer.lastTickPosY) * renderPartialTicks - f2) + findEntityPlayer.height + 1.0F;
               float f6 = (float)(findEntityPlayer.lastTickPosZ + (findEntityPlayer.posZ - findEntityPlayer.lastTickPosZ) * renderPartialTicks - f3);
               double sqrt = this.sqrt(findEntityPlayer.posX, findEntityPlayer.posY, findEntityPlayer.posZ);
               this.render(scaledResolutionEvent3.getScaledResolution(), teamMember, f4, f5, f6, createIntBuffer, vec3, (int)sqrt);
            }
         }
      }
   }

   public void onSimpleEvent3(SimpleEvent3 simpleEvent3) {
      this.teamMemberList.clear();
   }

   public void render(TeamMember teamMember, float f, float f2, float f3) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      if (teamMember.isLeader()) {
         GlStateManager.color(0.0F, 0.0F, 1.0F, 0.66F);
      } else {
         Color color = teamMember.getColor();
         GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 0.66F);
      }

      GlStateManager.pushMatrix();
      GL11.glScalef(0.6F, 0.6F, 0.6F);
      GL11.glRotatef(45.0F, 0.0F, 0.0F, 1.0F);
      GL11.glTranslatef(f * 2.0F, 0.0F, 0.0F);
      GL11.glRotatef(90.0F, 0.0F, 0.0F, -1.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(-f, f2, 0.0).endVertex();
      worldRenderer.pos(-f, f2 + f3 / 2.0F, 0.0).endVertex();
      worldRenderer.pos(f, f2 + f3 / 2.0F, 0.0).endVertex();
      worldRenderer.pos(f, f2, 0.0).endVertex();
      instance.draw();
      GL11.glRotatef(90.0F, 0.0F, 0.0F, -1.0F);
      GL11.glTranslatef(f * 2.0F + 1.0F, f3 / 2.0F + 1.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(-f / 2.0F + 1.0F, f2, 0.0).endVertex();
      worldRenderer.pos(-f / 2.0F + 1.0F, f2 + f3 / 2.0F, 0.0).endVertex();
      worldRenderer.pos(f, f2 + f3 / 2.0F, 0.0).endVertex();
      worldRenderer.pos(f, f2, 0.0).endVertex();
      instance.draw();
      GlStateManager.popMatrix();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public Minecraft getMc() {
      return this.mc;
   }

   public void render(ScaledResolution scaledResolution, TeamMember teamMember, float f, float f2, float f3, IntBuffer intBuffer, Vec3 vec3, int n) {
      Vec3 vec32 = new Vec3(f, f2, f3);
      double lengthVector = vec32.lengthVector();
      if (vec3.dotProduct(vec32 = vec32.normalize()) <= 0.02) {
         double sin = FastTrig.sin(1.5533430342749535);
         double cos = FastTrig.cos(1.5533430342749535);
         Vec3 crossProduct = vec3.crossProduct(vec32);
         double xCoord = crossProduct.xCoord;
         double yCoord = crossProduct.yCoord;
         double zCoord = crossProduct.zCoord;
         double d = cos + xCoord * xCoord * (1.0 - cos);
         double d2 = xCoord * yCoord * (1.0 - cos) - zCoord * sin;
         double d3 = xCoord * zCoord * (1.0 - cos) + yCoord * sin;
         double d4 = yCoord * xCoord * (1.0 - cos) + zCoord * sin;
         double d5 = cos + yCoord * yCoord * (1.0 - cos);
         double d6 = yCoord * zCoord * (1.0 - cos) - xCoord * sin;
         double d7 = zCoord * xCoord * (1.0 - cos) - yCoord * sin;
         double d8 = zCoord * yCoord * (1.0 - cos) + xCoord * sin;
         double d9 = cos + zCoord * zCoord * (1.0 - cos);
         f = (float)(lengthVector * (d * vec3.xCoord + d2 * vec3.yCoord + d3 * vec3.zCoord));
         f2 = (float)(lengthVector * (d4 * vec3.xCoord + d5 * vec3.yCoord + d6 * vec3.zCoord));
         f3 = (float)(lengthVector * (d7 * vec3.xCoord + d8 * vec3.yCoord + d9 * vec3.zCoord));
      }

      FloatBuffer createFloatBuffer = BufferUtils.createFloatBuffer(3);
      GLU.gluProject(f, f2, f3, this.floatBuffer, this.floatBuffer2, intBuffer, createFloatBuffer);
      float f4 = createFloatBuffer.get(0) / scaledResolution.getScaleFactor();
      float f5 = createFloatBuffer.get(1) / scaledResolution.getScaleFactor();
      Side side = null;
      byte b = 8;
      byte b2 = 10;
      int n2 = -4 - b2;
      float f6 = scaledResolution.getScaledHeight() - f5;
      if (f6 < 0.0F) {
         side = Side.RIGHT;
         f5 = scaledResolution.getScaledHeight() - 6;
      } else if (f6 > scaledResolution.getScaledHeight() - b2) {
         side = Side.BOTTOM;
         f5 = 6.0F;
      }

      if (f4 - b < 0.0F) {
         side = Side.TOP;
         f4 = 6.0F;
      } else if (f4 > scaledResolution.getScaledWidth() - b) {
         side = Side.LEFT;
         f4 = scaledResolution.getScaledWidth() - 6;
      }

      GlStateManager.pushMatrix();
      GL11.glTranslatef(f4, scaledResolution.getScaledHeight() - f5, 0.0F);
      if (side != null) {
         if (CheatBreaker.getInstance().getKeybindSettings().showOffScreenMarker.getBooleanValue()) {
            this.render(teamMember, side, 0.0F, 0.0F);
         }
      } else {
         this.render(teamMember, b, n2, b2);
         if (n > 40 && CheatBreaker.getInstance().getKeybindSettings().showDistance.getBooleanValue()) {
            this.mc.fontRendererObj.computeInt2("(" + n + "m)", 0.0F, 10.0F, -1);
         }
      }

      GlStateManager.popMatrix();
   }

   public void process(boolean flag) {
      if (flag && !this.flag) {
         this.flag = true;
         CheatBreaker.getInstance().getEventBus().checkClass2(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
         CheatBreaker.getInstance().getEventBus().checkClass2(SimpleEvent3.class, this::onSimpleEvent3);
      } else if (!flag && this.flag) {
         this.flag = false;
         CheatBreaker.getInstance().getEventBus().checkClass(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
         CheatBreaker.getInstance().getEventBus().checkClass(SimpleEvent3.class, this::onSimpleEvent3);
      }
   }

   public int[] getIntArray() {
      return this.intArray;
   }

   public FloatBuffer getFloatBuffer2() {
      return this.floatBuffer2;
   }

   public TeamMember findTeamMember(String str) {
      for (TeamMember teamMember : this.teamMemberList) {
         if (teamMember.getPlayerId().equals(str)) {
            return teamMember;
         }
      }

      return null;
   }

   public double sqrt(double d, double d2, double d3) {
      double d4 = d - this.mc.thePlayer.posX;
      double d5 = d2 - this.mc.thePlayer.posY;
      double d6 = d3 - this.mc.thePlayer.posZ;
      return Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
   }
}
