package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class CommandEntityData extends CommandBase {
   @Override
   public String getCommandName() {
      return "entitydata";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.entitydata.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.entitydata.usage");
      }

      Entity entity = getEntity(sender, args[0]);
      if (entity instanceof EntityPlayer) {
         throw new CommandException("commands.entitydata.noPlayers", entity.getDisplayName());
      }

      NBTTagCompound nbttagCompound = new NBTTagCompound();
      entity.writeToNBT(nbttagCompound);
      NBTTagCompound nbttagCompound2 = (NBTTagCompound)nbttagCompound.copy();

      NBTTagCompound nbttagCompound3;
      try {
         nbttagCompound3 = JsonToNBT.getTagFromJson(getChatComponentFromNthArg(sender, args, 1).getUnformattedText());
      } catch (NBTException e) {
         throw new CommandException("commands.entitydata.tagError", e.getMessage());
      }

      nbttagCompound3.removeTag("UUIDMost");
      nbttagCompound3.removeTag("UUIDLeast");
      nbttagCompound.merge(nbttagCompound3);
      if (nbttagCompound.equals(nbttagCompound2)) {
         throw new CommandException("commands.entitydata.failed", nbttagCompound.toString());
      }

      entity.readFromNBT(nbttagCompound);
      notifyOperators(sender, this, "commands.entitydata.success", nbttagCompound.toString());
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
