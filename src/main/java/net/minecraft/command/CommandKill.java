package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;

public class CommandKill extends CommandBase {
    @Override
    public String getCommandName() {
        return "kill";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.kill.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        EntityPlayerMP commandSenderAsPlayer = getCommandSenderAsPlayer(sender);
        commandSenderAsPlayer.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
        sender.addChatMessage(new ChatComponentTranslation("commands.kill.success"));
    }
}
