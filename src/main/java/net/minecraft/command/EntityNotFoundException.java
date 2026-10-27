package net.minecraft.command;

public class EntityNotFoundException extends CommandException {
   public EntityNotFoundException() {
      this("commands.generic.entity.notFound");
   }

   public EntityNotFoundException(String str, Object... objs) {
      super(str, objs);
   }
}
