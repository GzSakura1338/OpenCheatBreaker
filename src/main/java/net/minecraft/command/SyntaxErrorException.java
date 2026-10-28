package net.minecraft.command;

public class SyntaxErrorException extends CommandException {
    public SyntaxErrorException() {
        this("commands.generic.snytax");
    }

    public SyntaxErrorException(String str, Object... objs) {
        super(str, objs);
    }
}
