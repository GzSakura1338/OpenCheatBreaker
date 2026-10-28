package net.minecraft.command;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public interface ICommandSender {
    String getCommandSenderName();

    IChatComponent getFormattedCommandSenderName();

    void addChatMessage(IChatComponent chatComponent);

    boolean canCommandSenderUseCommand(int n, String str);

    ChunkCoordinates getCommandSenderPosition();

    World getEntityWorld();
}
