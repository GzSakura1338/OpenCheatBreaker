package net.minecraft.tileentity;

import com.google.gson.JsonParseException;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent.Serializer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class TileEntitySign extends TileEntity {
   public IChatComponent[] signText = new IChatComponent[]{
      new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText("")
   };
   public int lineBeingEdited = -1;
   public boolean isEditable = true;
   public EntityPlayer player;
   public CommandResultStats stats = new CommandResultStats();

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);

      for (int i = 0; i < 4; i++) {
         String componentToJson = Serializer.componentToJson(this.signText[i]);
         compound.setString("Text" + (i + 1), componentToJson);
      }

      this.stats.writeStatsToNBT(compound);
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      this.isEditable = false;
      super.readFromNBT(compound);
      ICommandSender commandSender = new ICommandSender() {
         @Override
         public String getName() {
            return "Sign";
         }

         @Override
         public IChatComponent getDisplayName() {
            return new ChatComponentText(this.getName());
         }

         @Override
         public void addChatMessage(IChatComponent component) {
         }

         @Override
         public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
            return true;
         }

         @Override
         public BlockPos getPosition() {
            return TileEntitySign.this.pos;
         }

         @Override
         public Vec3 getPositionVector() {
            return new Vec3(TileEntitySign.this.pos.getX() + 0.5, TileEntitySign.this.pos.getY() + 0.5, TileEntitySign.this.pos.getZ() + 0.5);
         }

         @Override
         public World getEntityWorld() {
            return TileEntitySign.this.worldObj;
         }

         @Override
         public Entity getCommandSenderEntity() {
            return null;
         }

         @Override
         public boolean sendCommandFeedback() {
            return false;
         }

         @Override
         public void setCommandStat(Type type, int amount) {
         }
      };

      for (int i = 0; i < 4; i++) {
         String string = compound.getString("Text" + (i + 1));

         try {
            IChatComponent jsonToComponent = Serializer.jsonToComponent(string);

            try {
               this.signText[i] = ChatComponentProcessor.processComponent(commandSender, jsonToComponent, (Entity)null);
            } catch (CommandException e) {
               this.signText[i] = jsonToComponent;
            }
         } catch (JsonParseException e) {
            this.signText[i] = new ChatComponentText(string);
         }
      }

      this.stats.readStatsFromNBT(compound);
   }

   @Override
   public Packet getDescriptionPacket() {
      IChatComponent[] chatComponents = new IChatComponent[4];
      System.arraycopy(this.signText, 0, chatComponents, 0, 4);
      return new S33PacketUpdateSign(this.worldObj, this.pos, chatComponents);
   }

   @Override
   public boolean alwaysFalse() {
      return true;
   }

   public boolean getIsEditable() {
      return this.isEditable;
   }

   public void setEditable(boolean isEditableIn) {
      this.isEditable = isEditableIn;
      if (!isEditableIn) {
         this.player = null;
      }
   }

   public void setPlayer(EntityPlayer playerIn) {
      this.player = playerIn;
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }

   public boolean executeCommand(final EntityPlayer playerIn) {
      ICommandSender commandSender = new ICommandSender() {
         @Override
         public String getName() {
            return playerIn.getName();
         }

         @Override
         public IChatComponent getDisplayName() {
            return playerIn.getDisplayName();
         }

         @Override
         public void addChatMessage(IChatComponent component) {
         }

         @Override
         public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
            return permLevel <= 2;
         }

         @Override
         public BlockPos getPosition() {
            return TileEntitySign.this.pos;
         }

         @Override
         public Vec3 getPositionVector() {
            return new Vec3(TileEntitySign.this.pos.getX() + 0.5, TileEntitySign.this.pos.getY() + 0.5, TileEntitySign.this.pos.getZ() + 0.5);
         }

         @Override
         public World getEntityWorld() {
            return playerIn.getEntityWorld();
         }

         @Override
         public Entity getCommandSenderEntity() {
            return playerIn;
         }

         @Override
         public boolean sendCommandFeedback() {
            return false;
         }

         @Override
         public void setCommandStat(Type type, int amount) {
            TileEntitySign.this.stats.setCommandStatScore(this, type, amount);
         }
      };

      for (int i = 0; i < this.signText.length; i++) {
         ChatStyle chatStyle = this.signText[i] == null ? null : this.signText[i].getChatStyle();
         if (chatStyle != null && chatStyle.getChatClickEvent() != null) {
            ClickEvent chatClickEvent = chatStyle.getChatClickEvent();
            if (chatClickEvent.getAction() == Action.RUN_COMMAND) {
               MinecraftServer.getServer().getCommandManager().executeCommand(commandSender, chatClickEvent.getValue());
            }
         }
      }

      return true;
   }

   public CommandResultStats getStats() {
      return this.stats;
   }
}
