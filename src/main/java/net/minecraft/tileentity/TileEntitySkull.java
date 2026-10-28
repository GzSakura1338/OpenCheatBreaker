package net.minecraft.tileentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;

public class TileEntitySkull extends TileEntity {
    public int skullType;
    public int skullRotation;
    public GameProfile owner = null;

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setByte("SkullType", (byte)(this.skullType & 0xFF));
        compound.setByte("Rot", (byte)(this.skullRotation & 0xFF));
        if (this.owner != null) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            NBTUtil.writeGameProfileToNBT(nbttagCompound, this.owner);
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
                this.owner = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag("Owner"));
            } else if (compound.hasKey("ExtraType", 8) && !StringUtils.isNullOrEmpty(compound.getString("ExtraType"))) {
                this.owner = new GameProfile(null, compound.getString("ExtraType"));
                this.loadOwnerProperties();
            }
        }
    }

    public GameProfile getOwner() {
        return this.owner;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        this.writeToNBT(nbttagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 4, nbttagCompound);
    }

    public void setSkullType(int n) {
        this.skullType = n;
        this.owner = null;
    }

    public void setOwnerAndType(GameProfile gameProfile) {
        this.skullType = 3;
        this.owner = gameProfile;
        this.loadOwnerProperties();
    }

    public void loadOwnerProperties() {
        if (this.owner != null
            && !StringUtils.isNullOrEmpty(this.owner.getName())
            && (!this.owner.isComplete() || !this.owner.getProperties().containsKey("textures"))) {
            GameProfile gameProfileForUsername = MinecraftServer.getServer().getPlayerProfileCache().getGameProfileForUsername(this.owner.getName());
            if (gameProfileForUsername != null) {
                Property first = (Property)Iterables.getFirst(gameProfileForUsername.getProperties().get("textures"), null);
                if (first == null) {
                    gameProfileForUsername = MinecraftServer.getServer().getMinecraftSessionService().fillProfileProperties(gameProfileForUsername, true);
                }

                this.owner = gameProfileForUsername;
                this.markDirty();
            }
        }
    }

    public int getSkullType() {
        return this.skullType;
    }

    public int getSkullRotation() {
        return this.skullRotation;
    }

    public void setSkullRotation(int n) {
        this.skullRotation = n;
    }
}
