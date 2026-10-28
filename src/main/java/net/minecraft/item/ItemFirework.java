package net.minecraft.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemFirework extends Item {
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (!world.isRemote) {
            EntityFireworkRocket entityFireworkRocket = new EntityFireworkRocket(world, n + f, n2 + f2, n3 + f3, itemStack);
            world.spawnEntityInWorld(entityFireworkRocket);
            if (!entityPlayer.capabilities.isCreativeMode) {
                itemStack.stackSize--;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean flag) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound compoundTag = itemStack.getTagCompound().getCompoundTag("Fireworks");
            if (compoundTag != null) {
                if (compoundTag.hasKey("Flight", 99)) {
                    list.add(StatCollector.translateToLocal("item.fireworks.flight") + " " + compoundTag.getByte("Flight"));
                }

                NBTTagList tagList = compoundTag.getTagList("Explosions", 10);
                if (tagList != null && tagList.tagCount() > 0) {
                    for (int i = 0; i < tagList.tagCount(); i++) {
                        NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                        ArrayList list2 = new ArrayList();
                        ItemFireworkCharge.addExplosionInfo(compoundTagAt, list2);
                        if (list2.size() > 0) {
                            for (int j = 1; j < list2.size(); j++) {
                                list2.set(j, "  " + list2.get(j));
                            }

                            list.addAll(list2);
                        }
                    }
                }
            }
        }
    }
}
