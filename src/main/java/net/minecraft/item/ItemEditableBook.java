package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent.Serializer;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

public class ItemEditableBook extends Item {
   public ItemEditableBook() {
      this.setMaxStackSize(1);
   }

   public static boolean validBookTagContents(NBTTagCompound nbt) {
      if (!ItemWritableBook.isNBTValid(nbt)) {
         return false;
      }

      if (!nbt.hasKey("title", 8)) {
         return false;
      }

      String string = nbt.getString("title");
      return string != null && string.length() <= 32 ? nbt.hasKey("author", 8) : false;
   }

   public static int getGeneration(ItemStack book) {
      return book.getTagCompound().getInteger("generation");
   }

   @Override
   public String getItemStackDisplayName(ItemStack stack) {
      if (stack.hasTagCompound()) {
         NBTTagCompound tagCompound = stack.getTagCompound();
         String string = tagCompound.getString("title");
         if (!StringUtils.isNullOrEmpty(string)) {
            return string;
         }
      }

      return super.getItemStackDisplayName(stack);
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      if (stack.hasTagCompound()) {
         NBTTagCompound tagCompound = stack.getTagCompound();
         String string = tagCompound.getString("author");
         if (!StringUtils.isNullOrEmpty(string)) {
            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("book.byAuthor", string));
         }

         tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("book.generation." + tagCompound.getInteger("generation")));
      }
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      if (!worldIn.isRemote) {
         this.resolveContents(itemStackIn, playerIn);
      }

      playerIn.displayGUIBook(itemStackIn);
      playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return itemStackIn;
   }

   public void resolveContents(ItemStack stack, EntityPlayer player) {
      if (stack != null && stack.getTagCompound() != null) {
         NBTTagCompound tagCompound = stack.getTagCompound();
         if (!tagCompound.getBoolean("resolved")) {
            tagCompound.setBoolean("resolved", true);
            if (validBookTagContents(tagCompound)) {
               NBTTagList tagList = tagCompound.getTagList("pages", 8);

               for (int i = 0; i < tagList.tagCount(); i++) {
                  String stringTagAt = tagList.getStringTagAt(i);

                  IChatComponent chatComponent;
                  try {
                     chatComponent = Serializer.jsonToComponent(stringTagAt);
                     chatComponent = ChatComponentProcessor.processComponent(player, chatComponent, player);
                  } catch (Exception e) {
                     chatComponent = new ChatComponentText(stringTagAt);
                  }

                  tagList.set(i, new NBTTagString(Serializer.componentToJson(chatComponent)));
               }

               tagCompound.setTag("pages", tagList);
               if (player instanceof EntityPlayerMP && player.getCurrentEquippedItem() == stack) {
                  Slot slotFromInventory = player.openContainer.getSlotFromInventory(player.inventory, player.inventory.currentItem);
                  ((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, slotFromInventory.slotNumber, stack));
               }
            }
         }
      }
   }

   @Override
   public boolean hasEffect(ItemStack stack) {
      return true;
   }
}
