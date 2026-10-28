package net.minecraft.command;

public class PlayerNotFoundException extends CommandException {
    public PlayerNotFoundException() {
        this("commands.generic.player.notFound");
    }

    public PlayerNotFoundException(String str, Object... objs) {
        super(str, objs);
    }
}
