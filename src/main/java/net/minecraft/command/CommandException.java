package net.minecraft.command;

public class CommandException extends Exception {
   public Object[] errorObjects;

   public CommandException(String message, Object... objects) {
      super(message);
      this.errorObjects = objects;
   }

   public Object[] getErrorObjects() {
      return this.errorObjects;
   }
}
