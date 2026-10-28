package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.ServerConfigurationManager;

public class IntegratedPlayerList extends ServerConfigurationManager {
    public NBTTagCompound hostPlayerData;

    public IntegratedPlayerList(IntegratedServer integratedServer) {
        super(integratedServer);
        this.setViewDistance(10);
    }

    @Override
    public void writePlayerData(EntityPlayerMP player) {
        if (player.getCommandSenderName().equals(this.getServerInstance().getServerOwner())) {
            this.hostPlayerData = new NBTTagCompound();
            player.writeToNBT(this.hostPlayerData);
        }

        super.writePlayerData(player);
    }

    @Override
    public String allowUserToConnect(SocketAddress address, GameProfile profile) {
        return profile.getName().equalsIgnoreCase(this.getServerInstance().getServerOwner()) && this.getPlayerByUsername(profile.getName()) != null
            ? "That name is already taken."
            : super.allowUserToConnect(address, profile);
    }

    public IntegratedServer getServerInstance() {
        return (IntegratedServer)super.getServerInstance();
    }

    @Override
    public NBTTagCompound getHostPlayerData() {
        return this.hostPlayerData;
    }
}
