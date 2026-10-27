package net.minecraft.tileentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;

public class TileEntitySkull extends TileEntity {
   public int skullType;
   public int skullRotation;
   public GameProfile playerProfile = null;

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setByte("SkullType", (byte)(this.skullType & 0xFF));
      compound.setByte("Rot", (byte)(this.skullRotation & 0xFF));
      if (this.playerProfile != null) {
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         NBTUtil.writeGameProfile(nbttagCompound, this.playerProfile);
         compound.setTag("Owner", nbttagCompound);
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.skullType = compound.getByte("SkullType");
      this.skullRotation = compound.getByte("Rot");
      if (this.skullType == 3) {
         if (compound.hasKey("Owner", 10)) {
            this.playerProfile = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag("Owner"));
         } else if (compound.hasKey("ExtraType", 8)) {
            String string = compound.getString("ExtraType");
            if (!StringUtils.isNullOrEmpty(string)) {
               this.playerProfile = new GameProfile((UUID)null, string);
               this.updatePlayerProfile();
            }
         }
      }
   }

   public GameProfile getPlayerProfile() {
      return this.playerProfile;
   }

   @Override
   public Packet getDescriptionPacket() {
      NBTTagCompound nbttagCompound = new NBTTagCompound();
      this.writeToNBT(nbttagCompound);
      return new S35PacketUpdateTileEntity(this.pos, 4, nbttagCompound);
   }

   public void setType(int type) {
      this.skullType = type;
      this.playerProfile = null;
   }

   public void setPlayerProfile(GameProfile playerProfile) {
      this.skullType = 3;
      this.playerProfile = playerProfile;
      this.updatePlayerProfile();
   }

   public void updatePlayerProfile() {
      this.playerProfile = updateGameprofile(this.playerProfile);
      this.markDirty();
   }

   public static GameProfile updateGameprofile(GameProfile input) {
      if (input != null && !StringUtils.isNullOrEmpty(input.getName())) {
         if (input.isComplete() && input.getProperties().containsKey("textures")) {
            return input;
         }

         if (MinecraftServer.getServer() == null) {
            return input;
         }

         GameProfile gameProfileForUsername = MinecraftServer.getServer().getPlayerProfileCache().getGameProfileForUsername(input.getName());
         if (gameProfileForUsername == null) {
            return input;
         }

         Property first = (Property)Iterables.getFirst(gameProfileForUsername.getProperties().get("textures"), null);
         if (first == null) {
            gameProfileForUsername = MinecraftServer.getServer().getMinecraftSessionService().fillProfileProperties(gameProfileForUsername, true);
         }

         return gameProfileForUsername;
      } else {
         return input;
      }
   }

   public int getSkullType() {
      return this.skullType;
   }

   public int getSkullRotation() {
      return this.skullRotation;
   }

   public void setSkullRotation(int rotation) {
      this.skullRotation = rotation;
   }
}
