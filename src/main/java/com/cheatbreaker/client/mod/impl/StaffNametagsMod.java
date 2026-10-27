package com.cheatbreaker.client.mod.impl;

import java.util.Iterator;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import java.nio.IntBuffer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.util.glu.GLU;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import com.cheatbreaker.client.util.FastTrig;
import org.lwjgl.opengl.GL11;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import org.lwjgl.BufferUtils;
import java.util.List;
import java.util.Map;
import java.nio.FloatBuffer;
import com.cheatbreaker.client.mod.AbstractNamedMod;

public class StaffNametagsMod extends AbstractNamedMod
{
    public FloatBuffer modelviewBuffer;
    public static Map<String, List<String>> playerTags;
    public FloatBuffer projectionBuffer;
    
    public static Map<String, List<String>> getPlayerTags() {
        return StaffNametagsMod.playerTags;
    }
    
    static {
        StaffNametagsMod.playerTags = null;
    }
    
    public static void setPlayerTags(final Map<String, List<String>> playerTags) {
        StaffNametagsMod.playerTags = playerTags;
    }
    
    public StaffNametagsMod(final String s) {
        super(s);
        this.modelviewBuffer = BufferUtils.createFloatBuffer(16);
        this.projectionBuffer = BufferUtils.createFloatBuffer(16);
        this.setStaffMod(true);
        this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
    }
    
    public void onScaledResolutionEvent3(final ScaledResolutionEvent3 scaledResolutionEvent3) {
        final IntBuffer intBuffer = BufferUtils.createIntBuffer(16);
        GL11.glGetInteger(2978, intBuffer);
        final float renderPartialTicks = this.mc.timer.renderPartialTicks;
        final float n = (float)(this.mc.thePlayer.lastTickPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.lastTickPosX) * renderPartialTicks);
        final float n2 = (float)(this.mc.thePlayer.lastTickPosY + (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * renderPartialTicks);
        final float n3 = (float)(this.mc.thePlayer.lastTickPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.lastTickPosZ) * renderPartialTicks);
        final double n4 = (this.mc.thePlayer.rotationPitch + 90.0f) * 3.141592653589793 / 180.0;
        final double n5 = (this.mc.thePlayer.rotationYaw + 90.0f) * 3.141592653589793 / 180.0;
        final boolean b = StaffNametagsMod.playerTags != null;
        Vec3 vec3 = new Vec3(FastTrig.sin(n4) * FastTrig.cos(n5), FastTrig.cos(n4), FastTrig.sin(n4) * FastTrig.sin(n5));
        if (this.mc.gameSettings.thirdPersonView == 2) {
            vec3 = new Vec3(vec3.xCoord * -1.0, vec3.yCoord * -1.0, vec3.zCoord * -1.0);
        }
        for (int i = 0; i < this.mc.theWorld.getLoadedEntityList().size(); ++i) {
            final Entity entity = this.mc.theWorld.getLoadedEntityList().get(i);
            if (entity instanceof AbstractClientPlayer) {
                final AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)entity;
                if (abstractClientPlayer != this.mc.thePlayer || this.mc.gameSettings.thirdPersonView != 0) {
                    final Scoreboard worldScoreboard = abstractClientPlayer.getWorldScoreboard();
                    final ScoreObjective objectiveInDisplaySlot = worldScoreboard.getObjectiveInDisplaySlot(2);
                    float n6 = 0.0f;
                    if (abstractClientPlayer.getDistanceSqToEntity(this.mc.thePlayer) < 100.0 && objectiveInDisplaySlot != null) {
                        n6 += this.mc.fontRendererObj.FONT_HEIGHT * 0.0375f;
                    }
                    float n7 = (float)(abstractClientPlayer.lastTickPosX + (abstractClientPlayer.posX - abstractClientPlayer.lastTickPosX) * renderPartialTicks - n);
                    float n8 = (float)(abstractClientPlayer.lastTickPosY + (abstractClientPlayer.posY - abstractClientPlayer.lastTickPosY) * renderPartialTicks - n2) + abstractClientPlayer.height + (abstractClientPlayer.isSneaking() ? 0.3f : 0.55f) + n6;
                    float n9 = (float)(abstractClientPlayer.lastTickPosZ + (abstractClientPlayer.posZ - abstractClientPlayer.lastTickPosZ) * renderPartialTicks - n3);
                    final Vec3 vec4 = new Vec3(n7, n8, n9);
                    final double lengthVector = vec4.lengthVector();
                    final Vec3 normalize;
                    if (vec3.dotProduct(normalize = vec4.normalize()) <= 0.02) {
                        final double sin = FastTrig.sin(1.5533430342749535);
                        final double cos = FastTrig.cos(1.5533430342749535);
                        final Vec3 crossProduct = vec3.crossProduct(normalize);
                        final double xCoord = crossProduct.xCoord;
                        final double yCoord = crossProduct.yCoord;
                        final double zCoord = crossProduct.zCoord;
                        final double n10 = cos + xCoord * xCoord * (1.0 - cos);
                        final double n11 = xCoord * yCoord * (1.0 - cos) - zCoord * sin;
                        final double n12 = xCoord * zCoord * (1.0 - cos) + yCoord * sin;
                        final double n13 = yCoord * xCoord * (1.0 - cos) + zCoord * sin;
                        final double n14 = cos + yCoord * yCoord * (1.0 - cos);
                        final double n15 = yCoord * zCoord * (1.0 - cos) - xCoord * sin;
                        final double n16 = zCoord * xCoord * (1.0 - cos) - yCoord * sin;
                        final double n17 = zCoord * yCoord * (1.0 - cos) + xCoord * sin;
                        final double n18 = cos + zCoord * zCoord * (1.0 - cos);
                        n7 = (float)(lengthVector * (n10 * vec3.xCoord + n11 * vec3.yCoord + n12 * vec3.zCoord));
                        n8 = (float)(lengthVector * (n13 * vec3.xCoord + n14 * vec3.yCoord + n15 * vec3.zCoord));
                        n9 = (float)(lengthVector * (n16 * vec3.xCoord + n17 * vec3.yCoord + n18 * vec3.zCoord));
                    }
                    final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(3);
                    GLU.gluProject(n7, n8, n9, this.modelviewBuffer, this.projectionBuffer, intBuffer, floatBuffer);
                    final float n19 = floatBuffer.get(0) / scaledResolutionEvent3.getScaledResolution().getScaleFactor();
                    final float n20 = floatBuffer.get(1) / scaledResolutionEvent3.getScaledResolution().getScaleFactor();
                    GlStateManager.pushMatrix();
                    GL11.glTranslatef(n19, scaledResolutionEvent3.getScaledResolution().getScaledHeight() - n20, 0.0f);
                    final float health = abstractClientPlayer.getHealth();
                    String text = abstractClientPlayer.getName() + ((health != 1.0) ? (" | " + this.findEnumChatFormatting(health) + health) : "");
                    if (objectiveInDisplaySlot != null) {
                        final Score valueFromObjective = worldScoreboard.getValueFromObjective(abstractClientPlayer.getName(), objectiveInDisplaySlot);
                        text = text + EnumChatFormatting.WHITE + " | " + this.findEnumChatFormatting((float)valueFromObjective.getScorePoints()) + valueFromObjective.getScorePoints();
                    }
                    final float n21 = (float)this.mc.fontRendererObj.getStringWidth(EnumChatFormatting.getTextWithoutFormattingCodes(text));
                    this.mc.fontRendererObj.drawStringWithShadow(text, (float)((int)(-n21) / 2), (float)(-this.mc.fontRendererObj.FONT_HEIGHT), -1);
                    if (b && StaffNametagsMod.playerTags.containsKey(abstractClientPlayer.getGameProfile().getId().toString())) {
                        int n22 = 1;
                        final Iterator iterator = StaffNametagsMod.playerTags.get(abstractClientPlayer.getGameProfile().getId().toString()).iterator();
                        while (iterator.hasNext()) {
                            this.mc.fontRendererObj.computeInt2((String)iterator.next(), (float)((int)(-n21) / 2), (float)(-this.mc.fontRendererObj.FONT_HEIGHT * ++n22), -1);
                        }
                    }
                    GlStateManager.popMatrix();
                }
            }
        }
    }
    
    public EnumChatFormatting findEnumChatFormatting(final float n) {
        return (n > 15.0f) ? EnumChatFormatting.DARK_GREEN : ((n > 10.0f) ? EnumChatFormatting.YELLOW : ((n > 5.0f) ? EnumChatFormatting.RED : EnumChatFormatting.DARK_RED));
    }
}
