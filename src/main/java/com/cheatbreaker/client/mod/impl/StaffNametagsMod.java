package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.mod.AbstractNamedMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.util.FastTrig;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.optifine.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class StaffNametagsMod extends AbstractNamedMod {
    public FloatBuffer modelviewBuffer = BufferUtils.createFloatBuffer((int)16);
    public FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer((int)16);
    public static Map<String, List<String>> playerTags = null;

    public static void setPlayerTags(Map<String, List<String>> map) {
        playerTags = map;
    }

    public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
        IntBuffer intBuffer = BufferUtils.createIntBuffer((int)16);
        GL11.glGetInteger((int)2978, (IntBuffer)intBuffer);
        float f = this.mc.timer.renderPartialTicks;
        float f2 = (float)(this.mc.thePlayer.lastTickPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.lastTickPosX) * (double)f);
        float f3 = (float)(this.mc.thePlayer.lastTickPosY + (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * (double)f);
        float f4 = (float)(this.mc.thePlayer.lastTickPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.lastTickPosZ) * (double)f);
        double d = (double)(this.mc.thePlayer.rotationPitch + 90.0f) * Math.PI / 180.0;
        double d2 = (double)(this.mc.thePlayer.rotationYaw + 90.0f) * Math.PI / 180.0;
        boolean bl = playerTags != null;
        Vec3 vec3 = new Vec3(FastTrig.sin(d) * FastTrig.cos(d2), FastTrig.cos(d), FastTrig.sin(d) * FastTrig.sin(d2));
        if (this.mc.gameSettings.thirdPersonView == 2) {
            vec3 = new Vec3(vec3.xCoord * -1.0, vec3.yCoord * -1.0, vec3.zCoord * -1.0);
        }
        for (int i = 0; i < this.mc.theWorld.getLoadedEntityList().size(); ++i) {
            Object object;
            AbstractClientPlayer abstractClientPlayer;
            Entity entity = (Entity)this.mc.theWorld.getLoadedEntityList().get(i);
            if (!(entity instanceof AbstractClientPlayer) || (abstractClientPlayer = (AbstractClientPlayer)entity) == this.mc.thePlayer && this.mc.gameSettings.thirdPersonView == 0) continue;
            Scoreboard scoreboard = abstractClientPlayer.getWorldScoreboard();
            ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(2);
            float f5 = 0.0f;
            if (abstractClientPlayer.getDistanceSqToEntity(this.mc.thePlayer) < 100.0 && scoreObjective != null) {
                f5 = (float)((double)f5 + (double)((float)this.mc.fontRendererObj.FONT_HEIGHT * 0.0375f));
            }
            float f6 = (float)(abstractClientPlayer.lastTickPosX + (abstractClientPlayer.posX - abstractClientPlayer.lastTickPosX) * (double)f - (double)f2);
            float f7 = (float)(abstractClientPlayer.lastTickPosY + (abstractClientPlayer.posY - abstractClientPlayer.lastTickPosY) * (double)f - (double)f3) + abstractClientPlayer.height + (abstractClientPlayer.isSneaking() ? 0.3f : 0.55f) + f5;
            float f8 = (float)(abstractClientPlayer.lastTickPosZ + (abstractClientPlayer.posZ - abstractClientPlayer.lastTickPosZ) * (double)f - (double)f4);
            Vec3 vec33 = new Vec3(f6, f7, f8);
            double d3 = vec33.lengthVector();
            vec33 = vec33.normalize();
            if (vec3.dotProduct(vec33) <= 0.02) {
                double d4 = FastTrig.sin(1.5533430342749535);
                double d5 = FastTrig.cos(1.5533430342749535);
                object = vec3.crossProduct(vec33);
                double d6 = ((Vec3)object).xCoord;
                double d7 = ((Vec3)object).yCoord;
                double d8 = ((Vec3)object).zCoord;
                double d9 = d5 + d6 * d6 * (1.0 - d5);
                double d10 = d6 * d7 * (1.0 - d5) - d8 * d4;
                double d11 = d6 * d8 * (1.0 - d5) + d7 * d4;
                double d12 = d7 * d6 * (1.0 - d5) + d8 * d4;
                double d13 = d5 + d7 * d7 * (1.0 - d5);
                double d14 = d7 * d8 * (1.0 - d5) - d6 * d4;
                double d15 = d8 * d6 * (1.0 - d5) - d7 * d4;
                double d16 = d8 * d7 * (1.0 - d5) + d6 * d4;
                double d17 = d5 + d8 * d8 * (1.0 - d5);
                f6 = (float)(d3 * (d9 * vec3.xCoord + d10 * vec3.yCoord + d11 * vec3.zCoord));
                f7 = (float)(d3 * (d12 * vec3.xCoord + d13 * vec3.yCoord + d14 * vec3.zCoord));
                f8 = (float)(d3 * (d15 * vec3.xCoord + d16 * vec3.yCoord + d17 * vec3.zCoord));
            }
            FloatBuffer floatBuffer = BufferUtils.createFloatBuffer((int)3);
            GLU.gluProject((float)f6, (float)f7, (float)f8, (FloatBuffer)this.modelviewBuffer, (FloatBuffer)this.projectionBuffer, (IntBuffer)intBuffer, (FloatBuffer)floatBuffer);
            float f9 = floatBuffer.get(0) / (float)scaledResolutionEvent3.getScaledResolution().getScaleFactor();
            float f10 = floatBuffer.get(1) / (float)scaledResolutionEvent3.getScaledResolution().getScaleFactor();
            GlStateManager.pushMatrix();
            GL11.glTranslatef((float)f9, (float)((float)scaledResolutionEvent3.getScaledResolution().getScaledHeight() - f10), (float)0.0f);
            float f11 = abstractClientPlayer.getHealth();
            object = abstractClientPlayer.getCommandSenderName() + ((double)f11 != 1.0 ? " | " + (Object)((Object)this.findEnumChatFormatting(f11)) + f11 : "");
            if (scoreObjective != null) {
                Score score = scoreboard.getValueFromObjective(abstractClientPlayer.getCommandSenderName(), scoreObjective);
                object = (String)object + (Object)((Object)EnumChatFormatting.WHITE) + " | " + (Object)((Object)this.findEnumChatFormatting(score.getScorePoints())) + score.getScorePoints();
            }
            float f12 = this.mc.fontRendererObj.getStringWidth(EnumChatFormatting.getTextWithoutFormattingCodes((String)object));
            this.mc.fontRendererObj.drawShadowedString((String)object, (int)(-f12) / 2, -this.mc.fontRendererObj.FONT_HEIGHT, -1);
            if (bl && playerTags.containsKey(abstractClientPlayer.getGameProfile().getId().toString())) {
                int n = 1;
                for (String string : playerTags.get(abstractClientPlayer.getGameProfile().getId().toString())) {
                    this.mc.fontRendererObj.drawStringWithShadow(string, (int)(-f12) / 2, -this.mc.fontRendererObj.FONT_HEIGHT * ++n, -1);
                }
            }
            GlStateManager.popMatrix();
        }
    }

    public EnumChatFormatting findEnumChatFormatting(float f) {
        return f > 15.0f ? EnumChatFormatting.DARK_GREEN : (f > 10.0f ? EnumChatFormatting.YELLOW : (f > 5.0f ? EnumChatFormatting.RED : EnumChatFormatting.DARK_RED));
    }

    public static Map<String, List<String>> getPlayerTags() {
        return playerTags;
    }

    public StaffNametagsMod(String string) {
        super(string);
        this.setStaffMod(true);
        this.setCategories(ModCategory.HUD);
        this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
    }
}
