package net.minecraft.command.server;

import io.netty.buffer.ByteBuf;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent.Serializer;
import net.minecraft.world.World;

public abstract class CommandBlockLogic implements ICommandSender {
    public static SimpleDateFormat timestampFormat = new SimpleDateFormat("HH:mm:ss");
    public int successCount;
    public boolean trackOutput = true;
    public IChatComponent lastOutput = null;
    public String commandStored = "";
    public String customName = "@";

    public int getSuccessCount() {
        return this.successCount;
    }

    public IChatComponent getLastOutput() {
        return this.lastOutput;
    }

    public void writeDataToNBT(NBTTagCompound nbttagCompound) {
        nbttagCompound.setString("Command", this.commandStored);
        nbttagCompound.setInteger("SuccessCount", this.successCount);
        nbttagCompound.setString("CustomName", this.customName);
        if (this.lastOutput != null) {
            nbttagCompound.setString("LastOutput", Serializer.componentToJson(this.lastOutput));
        }

        nbttagCompound.setBoolean("TrackOutput", this.trackOutput);
    }

    public void readDataFromNBT(NBTTagCompound nbttagCompound) {
        this.commandStored = nbttagCompound.getString("Command");
        this.successCount = nbttagCompound.getInteger("SuccessCount");
        if (nbttagCompound.hasKey("CustomName", 8)) {
            this.customName = nbttagCompound.getString("CustomName");
        }

        if (nbttagCompound.hasKey("LastOutput", 8)) {
            this.lastOutput = Serializer.jsonToComponent(nbttagCompound.getString("LastOutput"));
        }

        if (nbttagCompound.hasKey("TrackOutput", 1)) {
            this.trackOutput = nbttagCompound.getBoolean("TrackOutput");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(int permissionLevel, String command) {
        return permissionLevel <= 2;
    }

    public void setCommand(String str) {
        this.commandStored = str;
    }

    public String getCustomName() {
        return this.commandStored;
    }

    public void execute(World world) {
        if (world.isRemote) {
            this.successCount = 0;
        }

        MinecraftServer server = MinecraftServer.getServer();
        if (server != null && server.isCommandBlockEnabled()) {
            ICommandManager commandManager = server.getCommandManager();
            this.successCount = commandManager.executeCommand(this, this.commandStored);
        } else {
            this.successCount = 0;
        }
    }

    @Override
    public String getCommandSenderName() {
        return this.customName;
    }

    @Override
    public IChatComponent getFormattedCommandSenderName() {
        return new ChatComponentText(this.getCommandSenderName());
    }

    public void setName(String str) {
        this.customName = str;
    }

    @Override
    public void addChatMessage(IChatComponent message) {
        if (this.trackOutput && this.getEntityWorld() != null && !this.getEntityWorld().isRemote) {
            this.lastOutput = new ChatComponentText("[" + timestampFormat.format(new Date()) + "] ").appendSibling(message);
            this.run();
        }
    }

    public abstract void run();

    public abstract int constantInt();

    public abstract void write(ByteBuf byteBuf);

    public void setLastOutput(IChatComponent chatComponent) {
        this.lastOutput = chatComponent;
    }
}
