package net.minecraft.network.rcon;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class RConConsoleSource implements ICommandSender {
    public static RConConsoleSource instance = new RConConsoleSource();
    public StringBuffer buffer = new StringBuffer();

    @Override
    public String getCommandSenderName() {
        return "Rcon";
    }

    @Override
    public IChatComponent getFormattedCommandSenderName() {
        return new ChatComponentText(this.getCommandSenderName());
    }

    @Override
    public void addChatMessage(IChatComponent message) {
        this.buffer.append(message.getUnformattedText());
    }

    @Override
    public boolean canCommandSenderUseCommand(int permissionLevel, String command) {
        return true;
    }

    @Override
    public ChunkCoordinates getCommandSenderPosition() {
        return new ChunkCoordinates(0, 0, 0);
    }

    @Override
    public World getEntityWorld() {
        return MinecraftServer.getServer().getEntityWorld();
    }
}
