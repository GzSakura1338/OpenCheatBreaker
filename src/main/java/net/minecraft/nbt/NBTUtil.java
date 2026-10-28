package net.minecraft.nbt;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import net.minecraft.util.StringUtils;

public class NBTUtil {
    public static GameProfile readGameProfileFromNBT(NBTTagCompound compound) {
        String str = null;
        String str2 = null;
        if (compound.hasKey("Name", 8)) {
            str = compound.getString("Name");
        }

        if (compound.hasKey("Id", 8)) {
            str2 = compound.getString("Id");
        }

        if (StringUtils.isNullOrEmpty(str) && StringUtils.isNullOrEmpty(str2)) {
            return null;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(str2);
        } catch (Throwable e) {
            uuid = null;
        }

        GameProfile gameProfile = new GameProfile(uuid, str);
        if (compound.hasKey("Properties", 10)) {
            NBTTagCompound compoundTag = compound.getCompoundTag("Properties");

            for (String str3 : (Iterable<String>)compoundTag.getKeySet()) {
                NBTTagList tagList = compoundTag.getTagList(str3, 10);

                for (int i = 0; i < tagList.tagCount(); i++) {
                    NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                    String string = compoundTagAt.getString("Value");
                    if (compoundTagAt.hasKey("Signature", 8)) {
                        gameProfile.getProperties().put(str3, new Property(str3, string, compoundTagAt.getString("Signature")));
                    } else {
                        gameProfile.getProperties().put(str3, new Property(str3, string));
                    }
                }
            }
        }

        return gameProfile;
    }

    public static void writeGameProfileToNBT(NBTTagCompound compound, GameProfile profile) {
        if (!StringUtils.isNullOrEmpty(profile.getName())) {
            compound.setString("Name", profile.getName());
        }

        if (profile.getId() != null) {
            compound.setString("Id", profile.getId().toString());
        }

        if (!profile.getProperties().isEmpty()) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();

            for (String str : profile.getProperties().keySet()) {
                NBTTagList nbttagList = new NBTTagList();

                for (Property property : profile.getProperties().get(str)) {
                    NBTTagCompound nbttagCompound2 = new NBTTagCompound();
                    nbttagCompound2.setString("Value", property.getValue());
                    if (property.hasSignature()) {
                        nbttagCompound2.setString("Signature", property.getSignature());
                    }

                    nbttagList.appendTag(nbttagCompound2);
                }

                nbttagCompound.setTag(str, nbttagList);
            }

            compound.setTag("Properties", nbttagCompound);
        }
    }
}
