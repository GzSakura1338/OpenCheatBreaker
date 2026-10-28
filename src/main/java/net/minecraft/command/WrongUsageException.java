package net.minecraft.command;

public class WrongUsageException extends SyntaxErrorException {
    public WrongUsageException(String str, Object... objs) {
        super(str, objs);
    }
}
