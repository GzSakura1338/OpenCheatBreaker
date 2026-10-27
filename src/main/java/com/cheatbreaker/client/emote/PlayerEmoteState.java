package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.emote.bobj.ActionPlayer;
import com.cheatbreaker.client.emote.bobj.BobjArmature;
import com.cheatbreaker.client.emote.bobj.PlayerMeshRenderer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.vecmath.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerEmoteState implements EmoteState {
   public int ticksPlayed;
   public PlayerMeshRenderer meshes;
   public EmoteDefinition emote;
   public double lastX;
   public static Map<UUID, PlayerEmoteState> states = new ConcurrentHashMap<>();
   public double lastY;
   public double lastZ;
   public EmoteSource source = EmoteSource.CLIENT;
   public ActionPlayer actionPlayer;

   public void tickClient(EntityLivingBase entityLivingBase) {
      if (this.source != EmoteSource.SERVER) {
         if (this.emote != null && this.shouldStop(entityLivingBase)) {
            CheatBreaker.getInstance().getEmoteManager().stopEmote((AbstractClientPlayer)entityLivingBase);
         }

         this.lastX = entityLivingBase.posX;
         this.lastY = entityLivingBase.posY;
         this.lastZ = entityLivingBase.posZ;
      }

      if (this.emote != null && this.actionPlayer != null) {
         this.ticksPlayed++;
      }

      if (this.meshes != null) {
         this.meshes.tick(entityLivingBase);
      }
   }

   public boolean shouldStop(EntityLivingBase entityLivingBase) {
      boolean flag = this.emote.stopsOnMovement() && Math.abs(entityLivingBase.posX - this.lastX + (entityLivingBase.posY - this.lastY) + (entityLivingBase.posZ - this.lastZ)) > 0.015;
      return flag
         || this.emote.hasRepeatLimit() && this.getCompletedLoops() > this.emote.getMaxRepeats()
         || !this.emote.looping && this.ticksPlayed >= this.emote.durationTicks;
   }

   @Override
   public void setEmote(EmoteDefinition emoteDefinition, EntityLivingBase entityLivingBase) {
      boolean isRemote = entityLivingBase.worldObj.isRemote;
      if (isRemote) {
         this.stopEmote(entityLivingBase);
      }

      this.emote = emoteDefinition;
      this.ticksPlayed = 0;
      if (isRemote) {
         this.startEmote(emoteDefinition, entityLivingBase);
      }
   }

   @Override
   public EmoteDefinition getEmote() {
      return this.emote;
   }

   @Override
   public void setSource(EmoteSource emoteSource) {
      this.source = emoteSource;
   }

   @Override
   public void tick(EntityLivingBase entityLivingBase) {
      if (entityLivingBase.worldObj.isRemote) {
         this.tickClient(entityLivingBase);
      } else {
         if (this.emote != null) {
            if (this.shouldStop(entityLivingBase) && entityLivingBase instanceof AbstractClientPlayer) {
               CheatBreaker.getInstance().getEmoteManager().stopEmote((AbstractClientPlayer)entityLivingBase);
            }

            this.ticksPlayed++;
         }

         this.lastX = entityLivingBase.posX;
         this.lastY = entityLivingBase.posY;
         this.lastZ = entityLivingBase.posZ;
      }
   }

   public int getCompletedLoops() {
      return this.ticksPlayed < this.emote.durationTicks ? 0 : (int)Math.floor((double)this.ticksPlayed / this.emote.durationTicks);
   }

   @Override
   public EmoteSource getSource() {
      return this.source;
   }

   public static void tickPlayer(EntityPlayer entityPlayer) {
      PlayerEmoteState playerEmoteState = states.get(entityPlayer.getUniqueID());
      if (playerEmoteState != null) {
         playerEmoteState.tick(entityPlayer);
      }
   }

   public void createMeshes(EntityLivingBase entityLivingBase) {
      AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)entityLivingBase;
      this.meshes = new PlayerMeshRenderer(abstractClientPlayer.getSkinType(), new NBTTagCompound());
      NBTTagCompound nbttagCompound = new NBTTagCompound();
      NBTTagCompound nbttagCompound2 = new NBTTagCompound();
      nbttagCompound.setTag("body", nbttagCompound2);
      nbttagCompound2.setString("Texture", abstractClientPlayer.getLocationSkin().toString());
      this.meshes.nbttagCompound.setTag("Meshes", nbttagCompound);
      this.meshes.loadModel();
   }

   public static EmoteState getOrCreate(UUID uuid) {
      return states.computeIfAbsent(uuid, uuid2 -> new PlayerEmoteState());
   }

   public void stopEmote(EntityLivingBase entityLivingBase) {
      if (this.emote != null) {
         this.emote.onStop(this.meshes);
         this.emote = null;
         CheatBreaker.getInstance().getEmoteManager().stopEmote((AbstractClientPlayer)entityLivingBase);
      }
   }

   @Override
   public boolean renderEmote(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f) {
      if (this.meshes == null) {
         this.createMeshes(entityLivingBase);
      }

      boolean flag = this.meshes != null;
      if (flag) {
         if (entityLivingBase instanceof AbstractClientPlayer) {
            AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)entityLivingBase;
            String skinType = abstractClientPlayer.getSkinType();
            if (!skinType.equals(this.meshes.modelName)) {
               this.meshes.modelName = skinType;
               this.meshes.model = null;
               this.meshes.loadModel();
            }

            if (this.meshes.model == null) {
               return false;
            }

            this.meshes.modelDefinition.meshMaterials.get("body").texture = abstractClientPlayer.getLocationSkin();
         }

         if (this.meshes.model == null) {
            return false;
         }

         this.meshes.render(entityLivingBase, d, d2, d3, 0.0F, f);
         BobjArmature armature = this.meshes.model.meshes.get(0).armature;
         Minecraft minecraft = Minecraft.getMinecraft();
         if (EmoteEntityRenderer.b_(entityLivingBase)) {
            RenderManager renderManager = minecraft.getRenderManager();
            Vector4f bonePosition = this.meshes.getBonePosition(entityLivingBase, armature.bonesByName.get("head"), 0.0F, 0.0F, 0.0F, f);
            float f2 = bonePosition.x - (float)renderManager.viewerPosX;
            float f3 = bonePosition.y - (float)renderManager.viewerPosY + 0.2F - 1.5078125F;
            float f4 = bonePosition.z - (float)renderManager.viewerPosZ;
            EmoteEntityRenderer.handleEntityLivingBase(entityLivingBase, entityLivingBase.getDisplayName().getFormattedText(), f2, f3, f4);
         }

         if (this.emote != null && this.actionPlayer != null && !Minecraft.getMinecraft().isGamePaused()) {
            this.emote.onTick(entityLivingBase, armature, this.meshes, (int)this.actionPlayer.getInterpolatedTime(0.0F), f);
         }
      }

      return flag;
   }

   public static EmoteState getForEntity(Entity entity) {
      return getOrCreate(entity.getUniqueID());
   }

   public void startEmote(EmoteDefinition emoteDefinition, EntityLivingBase entityLivingBase) {
      if (this.meshes == null) {
         this.createMeshes(entityLivingBase);
      }

      try {
         if (emoteDefinition != null) {
            AnimationAction action = this.meshes.definitionEntry.modelDefinition.actions.getAction("emote_" + emoteDefinition.name);
            if (emoteDefinition.name.equals("gun_lean")) {
               action.tick = 8;
            }

            this.actionPlayer = this.meshes.model.createActionPlayerDefault(null, action, emoteDefinition.looping);
            this.meshes.setForcedAction(this.actionPlayer);
            emoteDefinition.onStart(this.meshes);
         } else {
            this.actionPlayer = null;
            this.meshes.setForcedAction((ActionPlayer)null);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
