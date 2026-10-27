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

         for (String str3 : compoundTag.getKeySet()) {
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

   public static NBTTagCompound writeGameProfile(NBTTagCompound tagCompound, GameProfile profile) {
      if (!StringUtils.isNullOrEmpty(profile.getName())) {
         tagCompound.setString("Name", profile.getName());
      }

      if (profile.getId() != null) {
         tagCompound.setString("Id", profile.getId().toString());
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

         tagCompound.setTag("Properties", nbttagCompound);
      }

      return tagCompound;
   }

   public static boolean matches(NBTBase nbtbase, NBTBase nbtbase2, boolean flag) {
      if (nbtbase == nbtbase2) {
         return true;
      }

      if (nbtbase == null) {
         return true;
      }

      if (nbtbase2 == null) {
         return false;
      }

      if (!nbtbase.getClass().equals(nbtbase2.getClass())) {
         return false;
      }

      if (nbtbase instanceof NBTTagCompound) {
         NBTTagCompound nbttagCompound = (NBTTagCompound)nbtbase;
         NBTTagCompound nbttagCompound2 = (NBTTagCompound)nbtbase2;

         for (String str : nbttagCompound.getKeySet()) {
            NBTBase tag = nbttagCompound.getTag(str);
            if (!matches(tag, nbttagCompound2.getTag(str), flag)) {
               return false;
            }
         }

         return true;
      } else if (nbtbase instanceof NBTTagList && flag) {
         NBTTagList nbttagList = (NBTTagList)nbtbase;
         NBTTagList nbttagList2 = (NBTTagList)nbtbase2;
         if (nbttagList.tagCount() == 0) {
            return nbttagList2.tagCount() == 0;
         }

         for (int i = 0; i < nbttagList.tagCount(); i++) {
            NBTBase nbtbase3 = nbttagList.get(i);
            boolean flag2 = false;

            for (int j = 0; j < nbttagList2.tagCount(); j++) {
               if (matches(nbtbase3, nbttagList2.get(j), flag)) {
                  flag2 = true;
                  break;
               }
            }

            if (!flag2) {
               return false;
            }
         }

         return true;
      } else {
         return nbtbase.equals(nbtbase2);
      }
   }
}
