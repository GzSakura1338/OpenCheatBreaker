package net.minecraft.item;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

public class ItemFireworkCharge extends Item {
    public IIcon iIcon;

    @Override
    public IIcon getIconFromDamageForRenderPass(int n, int n2) {
        return n2 > 0 ? this.iIcon : super.getIconFromDamageForRenderPass(n, n2);
    }

    @Override
    public int getColorFromItemStack(ItemStack itemStack, int n) {
        if (n != 1) {
            return super.getColorFromItemStack(itemStack, n);
        }

        NBTBase explosionNbt = getExplosionNbt(itemStack, "Colors");
        if (explosionNbt != null && explosionNbt instanceof NBTTagIntArray) {
            NBTTagIntArray nbttagIntArray = (NBTTagIntArray)explosionNbt;
            int[] intArray = nbttagIntArray.getIntArray();
            if (intArray.length == 1) {
                return intArray[0];
            }

            int n2 = 0;
            int n3 = 0;
            int n4 = 0;
            int[] ints = intArray;
            int length = intArray.length;

            for (int i = 0; i < length; i++) {
                int n5 = ints[i];
                n2 += (n5 & 0xFF0000) >> 16;
                n3 += (n5 & 0xFF00) >> 8;
                n4 += (n5 & 0xFF) >> 0;
            }

            n2 /= intArray.length;
            n3 /= intArray.length;
            n4 /= intArray.length;
            return n2 << 16 | n3 << 8 | n4;
        } else {
            return 9079434;
        }
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    public static NBTBase getExplosionNbt(ItemStack itemStack, String str) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound compoundTag = itemStack.getTagCompound().getCompoundTag("Explosion");
            if (compoundTag != null) {
                return compoundTag.getTag(str);
            }
        }

        return null;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean flag) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound compoundTag = itemStack.getTagCompound().getCompoundTag("Explosion");
            if (compoundTag != null) {
                addExplosionInfo(compoundTag, list);
            }
        }
    }

    public static void addExplosionInfo(NBTTagCompound nbttagCompound, List list) {
        byte byte2 = nbttagCompound.getByte("Type");
        if (byte2 >= 0 && byte2 <= 4) {
            list.add(StatCollector.translateToLocal("item.fireworksCharge.type." + byte2).trim());
        } else {
            list.add(StatCollector.translateToLocal("item.fireworksCharge.type").trim());
        }

        int[] intArray = nbttagCompound.getIntArray("Colors");
        if (intArray.length > 0) {
            boolean flag = true;
            String str = "";
            int[] ints = intArray;
            int length = intArray.length;

            for (int i = 0; i < length; i++) {
                int n = ints[i];
                if (!flag) {
                    str = str + ", ";
                }

                flag = false;
                boolean flag2 = false;

                for (int j = 0; j < 16; j++) {
                    if (n == ItemDye.dyeColors[j]) {
                        flag2 = true;
                        str = str + StatCollector.translateToLocal("item.fireworksCharge." + ItemDye.dyeColorNames[j]);
                        break;
                    }
                }

                if (!flag2) {
                    str = str + StatCollector.translateToLocal("item.fireworksCharge.customColor");
                }
            }

            list.add(str);
        }

        int[] intArray2 = nbttagCompound.getIntArray("FadeColors");
        if (intArray2.length > 0) {
            boolean flag = true;
            String str = StatCollector.translateToLocal("item.fireworksCharge.fadeTo") + " ";
            int[] ints = intArray2;
            int length = intArray2.length;

            for (int i = 0; i < length; i++) {
                int n = ints[i];
                if (!flag) {
                    str = str + ", ";
                }

                flag = false;
                boolean flag2 = false;

                for (int j = 0; j < 16; j++) {
                    if (n == ItemDye.dyeColors[j]) {
                        flag2 = true;
                        str = str + StatCollector.translateToLocal("item.fireworksCharge." + ItemDye.dyeColorNames[j]);
                        break;
                    }
                }

                if (!flag2) {
                    str = str + StatCollector.translateToLocal("item.fireworksCharge.customColor");
                }
            }

            list.add(str);
        }

        boolean boolean2 = nbttagCompound.getBoolean("Trail");
        if (boolean2) {
            list.add(StatCollector.translateToLocal("item.fireworksCharge.trail"));
        }

        boolean boolean3 = nbttagCompound.getBoolean("Flicker");
        if (boolean3) {
            list.add(StatCollector.translateToLocal("item.fireworksCharge.flicker"));
        }
    }

    @Override
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        this.iIcon = register.registerIcon(this.getIconString() + "_overlay");
    }
}
