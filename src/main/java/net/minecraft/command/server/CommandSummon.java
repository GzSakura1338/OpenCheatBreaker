package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandSummon extends CommandBase {
   @Override
   public String getCommandName() {
      return "summon";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.summon.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.summon.usage");
      }

      String str = args[0];
      BlockPos position = sender.getPosition();
      Vec3 positionVector = sender.getPositionVector();
      double xCoord = positionVector.xCoord;
      double yCoord = positionVector.yCoord;
      double zCoord = positionVector.zCoord;
      if (args.length >= 4) {
         xCoord = parseDouble(xCoord, args[1], true);
         yCoord = parseDouble(yCoord, args[2], false);
         zCoord = parseDouble(zCoord, args[3], true);
         position = new BlockPos(xCoord, yCoord, zCoord);
      }

      World entityWorld = sender.getEntityWorld();
      if (!entityWorld.isBlockLoaded(position)) {
         throw new CommandException("commands.summon.outOfWorld");
      }

      if ("LightningBolt".equals(str)) {
         entityWorld.addWeatherEffect(new EntityLightningBolt(entityWorld, xCoord, yCoord, zCoord));
         notifyOperators(sender, this, "commands.summon.success");
      } else {
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         boolean flag = false;
         if (args.length >= 5) {
            IChatComponent chatComponentFromNthArg = getChatComponentFromNthArg(sender, args, 4);

            try {
               nbttagCompound = JsonToNBT.getTagFromJson(chatComponentFromNthArg.getUnformattedText());
               flag = true;
            } catch (NBTException e) {
               throw new CommandException("commands.summon.tagError", e.getMessage());
            }
         }

         nbttagCompound.setString("id", str);

         Entity entity;
         try {
            entity = EntityList.createEntityFromNBT(nbttagCompound, entityWorld);
         } catch (RuntimeException e) {
            throw new CommandException("commands.summon.failed");
         }

         if (entity == null) {
            throw new CommandException("commands.summon.failed");
         }

         entity.setLocationAndAngles(xCoord, yCoord, zCoord, entity.rotationYaw, entity.rotationPitch);
         if (!flag && entity instanceof EntityLiving) {
            ((EntityLiving)entity).onInitialSpawn(entityWorld.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData)null);
         }

         entityWorld.spawnEntityInWorld(entity);
         Entity entity2 = entity;

         for (NBTTagCompound nbttagCompound2 = nbttagCompound; entity2 != null && nbttagCompound2.hasKey("Riding", 10); nbttagCompound2 = nbttagCompound2.getCompoundTag("Riding")) {
            Entity createEntityFromNBT = EntityList.createEntityFromNBT(nbttagCompound2.getCompoundTag("Riding"), entityWorld);
            if (createEntityFromNBT != null) {
               createEntityFromNBT.setLocationAndAngles(xCoord, yCoord, zCoord, createEntityFromNBT.rotationYaw, createEntityFromNBT.rotationPitch);
               entityWorld.spawnEntityInWorld(createEntityFromNBT);
               entity2.mountEntity(createEntityFromNBT);
            }

            entity2 = createEntityFromNBT;
         }

         notifyOperators(sender, this, "commands.summon.success");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList())
         : (args.length > 1 && args.length <= 4 ? findList(args, 1, pos) : null);
   }
}
