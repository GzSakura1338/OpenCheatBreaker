package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;

public class PlayerProfileCache {
   public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   public Map<String, PlayerProfileCache.ProfileEntry> usernameToProfileEntryMap = Maps.newHashMap();
   public Map<UUID, PlayerProfileCache.ProfileEntry> uuidToProfileEntryMap = Maps.newHashMap();
   public LinkedList<GameProfile> gameProfiles = Lists.newLinkedList();
   public MinecraftServer mcServer;
   public File usercacheFile;
   public Gson gson;
   public static ParameterizedType TYPE = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{PlayerProfileCache.ProfileEntry.class};
      }

      @Override
      public Type getRawType() {
         return List.class;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }
   };

   public PlayerProfileCache(MinecraftServer server, File cacheFile) {
      this.mcServer = server;
      this.usercacheFile = cacheFile;
      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeHierarchyAdapter(PlayerProfileCache.ProfileEntry.class, new PlayerProfileCache.Serializer());
      this.gson = gsonBuilder.create();
      this.load();
   }

   public static GameProfile getGameProfile(MinecraftServer server, String username) {
      final GameProfile[] gameProfiles = new GameProfile[1];
      ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
         public void onProfileLookupSucceeded(GameProfile gameProfile) {
            gameProfiles[0] = gameProfile;
         }

         public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
            gameProfiles[0] = null;
         }
      };
      server.getGameProfileRepository().findProfilesByNames(new String[]{username}, Agent.MINECRAFT, profileLookupCallback);
      if (!server.isServerInOnlineMode() && gameProfiles[0] == null) {
         UUID uUID = EntityPlayer.getUUID(new GameProfile((UUID)null, username));
         GameProfile gameProfile = new GameProfile(uUID, username);
         profileLookupCallback.onProfileLookupSucceeded(gameProfile);
      }

      return gameProfiles[0];
   }

   public void addEntry(GameProfile gameProfile) {
      this.addEntry(gameProfile, (Date)null);
   }

   public void addEntry(GameProfile gameProfile, Date expirationDate) {
      UUID id = gameProfile.getId();
      if (expirationDate == null) {
         Calendar instance = Calendar.getInstance();
         instance.setTime(new Date());
         instance.add(2, 1);
         expirationDate = instance.getTime();
      }

      String toLowerCase = gameProfile.getName().toLowerCase(Locale.ROOT);
      PlayerProfileCache.ProfileEntry profileEntry = new PlayerProfileCache.ProfileEntry(gameProfile, expirationDate);
      if (this.uuidToProfileEntryMap.containsKey(id)) {
         PlayerProfileCache.ProfileEntry profileEntry2 = this.uuidToProfileEntryMap.get(id);
         this.usernameToProfileEntryMap.remove(profileEntry2.getGameProfile().getName().toLowerCase(Locale.ROOT));
         this.gameProfiles.remove(gameProfile);
      }

      this.usernameToProfileEntryMap.put(gameProfile.getName().toLowerCase(Locale.ROOT), profileEntry);
      this.uuidToProfileEntryMap.put(id, profileEntry);
      this.gameProfiles.addFirst(gameProfile);
      this.save();
   }

   public GameProfile getGameProfileForUsername(String username) {
      String toLowerCase = username.toLowerCase(Locale.ROOT);
      PlayerProfileCache.ProfileEntry profileEntry = this.usernameToProfileEntryMap.get(toLowerCase);
      if (profileEntry != null && new Date().getTime() >= profileEntry.expirationDate.getTime()) {
         this.uuidToProfileEntryMap.remove(profileEntry.getGameProfile().getId());
         this.usernameToProfileEntryMap.remove(profileEntry.getGameProfile().getName().toLowerCase(Locale.ROOT));
         this.gameProfiles.remove(profileEntry.getGameProfile());
         profileEntry = null;
      }

      if (profileEntry != null) {
         GameProfile gameProfile = profileEntry.getGameProfile();
         this.gameProfiles.remove(gameProfile);
         this.gameProfiles.addFirst(gameProfile);
      } else {
         GameProfile gameProfile = getGameProfile(this.mcServer, toLowerCase);
         if (gameProfile != null) {
            this.addEntry(gameProfile);
            profileEntry = this.usernameToProfileEntryMap.get(toLowerCase);
         }
      }

      this.save();
      return profileEntry == null ? null : profileEntry.getGameProfile();
   }

   public String[] getUsernames() {
      ArrayList newArrayList = Lists.newArrayList(this.usernameToProfileEntryMap.keySet());
      return (String[])newArrayList.toArray(new String[newArrayList.size()]);
   }

   public GameProfile getProfileByUUID(UUID uuid) {
      PlayerProfileCache.ProfileEntry profileEntry = this.uuidToProfileEntryMap.get(uuid);
      return profileEntry == null ? null : profileEntry.getGameProfile();
   }

   public PlayerProfileCache.ProfileEntry getByUUID(UUID uuid) {
      PlayerProfileCache.ProfileEntry profileEntry = this.uuidToProfileEntryMap.get(uuid);
      if (profileEntry != null) {
         GameProfile gameProfile = profileEntry.getGameProfile();
         this.gameProfiles.remove(gameProfile);
         this.gameProfiles.addFirst(gameProfile);
      }

      return profileEntry;
   }

   public void load() {
      BufferedReader bufferedReader = null;

      try {
         bufferedReader = Files.newReader(this.usercacheFile, Charsets.UTF_8);
         List fromJson = (List)this.gson.fromJson(bufferedReader, TYPE);
         this.usernameToProfileEntryMap.clear();
         this.uuidToProfileEntryMap.clear();
         this.gameProfiles.clear();

         for (PlayerProfileCache.ProfileEntry profileEntry : (Iterable<PlayerProfileCache.ProfileEntry>)Lists.reverse(fromJson)) {
            if (profileEntry != null) {
               this.addEntry(profileEntry.getGameProfile(), profileEntry.getExpirationDate());
            }
         }
      } catch (FileNotFoundException e) {
      } catch (JsonParseException e) {
      } finally {
         IOUtils.closeQuietly(bufferedReader);
      }
   }

   public void save() {
      String toJson = this.gson.toJson(this.getEntriesWithLimit(1000));
      BufferedWriter bufferedWriter = null;

      try {
         bufferedWriter = Files.newWriter(this.usercacheFile, Charsets.UTF_8);
         bufferedWriter.write(toJson);
         return;
      } catch (FileNotFoundException e) {
         return;
      } catch (IOException e) {
      } finally {
         IOUtils.closeQuietly(bufferedWriter);
      }
   }

   public List<PlayerProfileCache.ProfileEntry> getEntriesWithLimit(int limitSize) {
      ArrayList newArrayList = Lists.newArrayList();

      for (GameProfile gameProfile : Lists.newArrayList(Iterators.limit(this.gameProfiles.iterator(), limitSize))) {
         PlayerProfileCache.ProfileEntry byUUID = this.getByUUID(gameProfile.getId());
         if (byUUID != null) {
            newArrayList.add(byUUID);
         }
      }

      return newArrayList;
   }

   class ProfileEntry {
      public GameProfile gameProfile;
      public Date expirationDate;

      public ProfileEntry(GameProfile gameProfileIn, Date expirationDateIn) {
         this.gameProfile = gameProfileIn;
         this.expirationDate = expirationDateIn;
      }

      public GameProfile getGameProfile() {
         return this.gameProfile;
      }

      public Date getExpirationDate() {
         return this.expirationDate;
      }
   }

   class Serializer implements JsonDeserializer<PlayerProfileCache.ProfileEntry>, JsonSerializer<PlayerProfileCache.ProfileEntry> {
      public Serializer() {
      }

      public JsonElement serialize(PlayerProfileCache.ProfileEntry profileEntry, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("name", profileEntry.getGameProfile().getName());
         UUID id = profileEntry.getGameProfile().getId();
         jsonObject.addProperty("uuid", id == null ? "" : id.toString());
         jsonObject.addProperty("expiresOn", PlayerProfileCache.dateFormat.format(profileEntry.getExpirationDate()));
         return jsonObject;
      }

      public PlayerProfileCache.ProfileEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         if (jsonElement.isJsonObject()) {
            JsonObject asJsonObject = jsonElement.getAsJsonObject();
            JsonElement jsonElement2 = asJsonObject.get("name");
            JsonElement jsonElement3 = asJsonObject.get("uuid");
            JsonElement jsonElement4 = asJsonObject.get("expiresOn");
            if (jsonElement2 != null && jsonElement3 != null) {
               String asString = jsonElement3.getAsString();
               String asString2 = jsonElement2.getAsString();
               Date date = null;
               if (jsonElement4 != null) {
                  try {
                     date = PlayerProfileCache.dateFormat.parse(jsonElement4.getAsString());
                  } catch (ParseException e) {
                     date = null;
                  }
               }

               if (asString2 != null && asString != null) {
                  UUID uuid;
                  try {
                     uuid = UUID.fromString(asString);
                  } catch (Throwable e) {
                     return null;
                  }

                  return PlayerProfileCache.this.new ProfileEntry(new GameProfile(uuid, asString2), date);
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }
}
