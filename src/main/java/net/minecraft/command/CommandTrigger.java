package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandTrigger
extends CommandBase {
    @Override
    public String getCommandName() {
        return "trigger";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.trigger.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        Object object;
        EntityPlayerMP entityPlayerMP;
        if (args.length < 3) {
            throw new WrongUsageException("commands.trigger.usage", new Object[0]);
        }
        if (sender instanceof EntityPlayerMP) {
            entityPlayerMP = (EntityPlayerMP)sender;
        } else {
            object = sender.getCommandSenderEntity();
            if (!(object instanceof EntityPlayerMP)) {
                throw new CommandException("commands.trigger.invalidPlayer", new Object[0]);
            }
            entityPlayerMP = (EntityPlayerMP)object;
        }
        object = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
        ScoreObjective scoreObjective = ((Scoreboard)object).getObjective(args[0]);
        if (scoreObjective != null && scoreObjective.getCriteria() == IScoreObjectiveCriteria.TRIGGER) {
            int n = CommandTrigger.parseInt(args[2]);
            if (!((Scoreboard)object).entityHasObjective(entityPlayerMP.getName(), scoreObjective)) {
                throw new CommandException("commands.trigger.invalidObjective", args[0]);
            }
            Score score = ((Scoreboard)object).getValueFromObjective(entityPlayerMP.getName(), scoreObjective);
            if (score.isLocked()) {
                throw new CommandException("commands.trigger.disabled", args[0]);
            }
            if ("set".equals(args[1])) {
                score.setScorePoints(n);
            } else {
                if (!"add".equals(args[1])) {
                    throw new CommandException("commands.trigger.invalidMode", args[1]);
                }
                score.increseScore(n);
            }
            score.setLocked(true);
            if (entityPlayerMP.theItemInWorldManager.isCreative()) {
                CommandTrigger.notifyOperators(sender, (ICommand)this, "commands.trigger.success", args[0], args[1], args[2]);
            }
        } else {
            throw new CommandException("commands.trigger.invalidObjective", args[0]);
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            Scoreboard scoreboard = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
            ArrayList<String> arrayList = Lists.newArrayList();
            for (ScoreObjective scoreObjective : scoreboard.getScoreObjectives()) {
                if (scoreObjective.getCriteria() != IScoreObjectiveCriteria.TRIGGER) continue;
                arrayList.add(scoreObjective.getName());
            }
            return CommandTrigger.getListOfStringsMatchingLastWord(args, arrayList.toArray(new String[arrayList.size()]));
        }
        return args.length == 2 ? CommandTrigger.getListOfStringsMatchingLastWord(args, "add", "set") : null;
    }
}
