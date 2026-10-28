package net.minecraft.command;

public class CommandException extends RuntimeException {
    public Object[] errorObjects;

    public CommandException(String str, Object... objs) {
        super(str);
        this.errorObjects = objs;
    }

    public Object[] getErrorOjbects() {
        return this.errorObjects;
    }
}
