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
    public Map byName = Maps.newHashMap();
    public Map byUuid = Maps.newHashMap();
    public LinkedList profiles = Lists.newLinkedList();
    public MinecraftServer server;
    public File usercacheFile;
    public Gson gson;
    public static ParameterizedType ENTRY_LIST_TYPE = new ParameterizedType() {
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

    public PlayerProfileCache(MinecraftServer minecraftServer, File file) {
        this.server = minecraftServer;
        this.usercacheFile = file;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(PlayerProfileCache.ProfileEntry.class, new PlayerProfileCache.Serializer(null));
        this.gson = gsonBuilder.create();
        this.load();
    }

    public static GameProfile findGameProfile(MinecraftServer minecraftServer, String str) {
        final GameProfile[] gameProfiles = new GameProfile[1];
        ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
            public void onProfileLookupSucceeded(GameProfile gameProfile) {
                gameProfiles[0] = gameProfile;
            }

            public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                gameProfiles[0] = null;
            }
        };
        minecraftServer.getGameProfileRepository().findProfilesByNames(new String[]{str}, Agent.MINECRAFT, profileLookupCallback);
        if (!minecraftServer.isServerInOnlineMode() && gameProfiles[0] == null) {
            UUID uUID = EntityPlayer.getUUID(new GameProfile(null, str));
            GameProfile gameProfile = new GameProfile(uUID, str);
            profileLookupCallback.onProfileLookupSucceeded(gameProfile);
        }

        return gameProfiles[0];
    }

    public void add(GameProfile gameProfile) {
        this.add(gameProfile, null);
    }

    public void add(GameProfile gameProfile, Date date) {
        UUID id = gameProfile.getId();
        if (date == null) {
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            instance.add(2, 1);
            date = instance.getTime();
        }

        String toLowerCase = gameProfile.getName().toLowerCase(Locale.ROOT);
        PlayerProfileCache.ProfileEntry profileEntry = new PlayerProfileCache.ProfileEntry(gameProfile, date, null);
        LinkedList profiles = this.profiles;
        synchronized (this.profiles) {
            if (this.byUuid.containsKey(id)) {
                PlayerProfileCache.ProfileEntry profileEntry2 = (PlayerProfileCache.ProfileEntry)this.byUuid.get(id);
                this.byName.remove(profileEntry2.getGameProfile().getName().toLowerCase(Locale.ROOT));
                this.byName.put(gameProfile.getName().toLowerCase(Locale.ROOT), profileEntry);
                this.profiles.remove(gameProfile);
            } else {
                this.byUuid.put(id, profileEntry);
                this.byName.put(toLowerCase, profileEntry);
            }

            this.profiles.addFirst(gameProfile);
        }
    }

    public GameProfile getGameProfileForUsername(String str) {
        String toLowerCase = str.toLowerCase(Locale.ROOT);
        PlayerProfileCache.ProfileEntry profileEntry = (PlayerProfileCache.ProfileEntry)this.byName.get(toLowerCase);
        if (profileEntry != null && new Date().getTime() >= profileEntry.date.getTime()) {
            this.byUuid.remove(profileEntry.getGameProfile().getId());
            this.byName.remove(profileEntry.getGameProfile().getName().toLowerCase(Locale.ROOT));
            LinkedList profiles = this.profiles;
            synchronized (this.profiles) {
                this.profiles.remove(profileEntry.getGameProfile());
            }

            profileEntry = null;
        }

        if (profileEntry != null) {
            GameProfile gameProfile = profileEntry.getGameProfile();
            LinkedList profiles = this.profiles;
            synchronized (this.profiles) {
                this.profiles.remove(gameProfile);
                this.profiles.addFirst(gameProfile);
            }
        } else {
            GameProfile findGameProfile2 = findGameProfile(this.server, toLowerCase);
            if (findGameProfile2 != null) {
                this.add(findGameProfile2);
                profileEntry = (PlayerProfileCache.ProfileEntry)this.byName.get(toLowerCase);
            }
        }

        this.save();
        return profileEntry == null ? null : profileEntry.getGameProfile();
    }

    public String[] getNames() {
        ArrayList newArrayList = Lists.newArrayList(this.byName.keySet());
        return (String[])newArrayList.toArray(new String[newArrayList.size()]);
    }

    public GameProfile getByUuid(UUID uuid) {
        PlayerProfileCache.ProfileEntry profileEntry = (PlayerProfileCache.ProfileEntry)this.byUuid.get(uuid);
        return profileEntry == null ? null : profileEntry.getGameProfile();
    }

    public PlayerProfileCache.ProfileEntry findProfileEntry(UUID uuid) {
        PlayerProfileCache.ProfileEntry profileEntry = (PlayerProfileCache.ProfileEntry)this.byUuid.get(uuid);
        if (profileEntry != null) {
            GameProfile gameProfile = profileEntry.getGameProfile();
            LinkedList profiles = this.profiles;
            synchronized (this.profiles) {
                this.profiles.remove(gameProfile);
                this.profiles.addFirst(gameProfile);
            }
        }

        return profileEntry;
    }

    public void load() {
        List list = null;
        BufferedReader bufferedReader = null;

        label67: {
            try {
                bufferedReader = Files.newReader(this.usercacheFile, Charsets.UTF_8);
                list = (List)this.gson.fromJson(bufferedReader, ENTRY_LIST_TYPE);
                break label67;
            } catch (FileNotFoundException e) {
            } finally {
                IOUtils.closeQuietly(bufferedReader);
            }

            return;
        }

        if (list != null) {
            this.byName.clear();
            this.byUuid.clear();
            LinkedList profiles = this.profiles;
            synchronized (this.profiles) {
                this.profiles.clear();
            }

            for (PlayerProfileCache.ProfileEntry profileEntry : (Iterable<PlayerProfileCache.ProfileEntry>)Lists.reverse(list)) {
                if (profileEntry != null) {
                    this.add(profileEntry.getGameProfile(), profileEntry.getDate());
                }
            }
        }
    }

    public void save() {
        String toJson = this.gson.toJson(this.getLastAccessedEntries(1000));
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

    public List getLastAccessedEntries(int n) {
        ArrayList newArrayList = Lists.newArrayList();
        LinkedList profiles = this.profiles;
        ArrayList list;
        synchronized (this.profiles) {
            list = Lists.newArrayList(Iterators.limit(this.profiles.iterator(), n));
        }

        for (GameProfile gameProfile : (Iterable<GameProfile>)list) {
            PlayerProfileCache.ProfileEntry findProfileEntry = this.findProfileEntry(gameProfile.getId());
            if (findProfileEntry != null) {
                newArrayList.add(findProfileEntry);
            }
        }

        return newArrayList;
    }

    public class ProfileEntry {
        public GameProfile gameProfile;
        public Date date;

        public ProfileEntry(GameProfile gameProfile, Date date) {
            this.gameProfile = gameProfile;
            this.date = date;
        }

        public GameProfile getGameProfile() {
            return this.gameProfile;
        }

        public Date getDate() {
            return this.date;
        }

        public ProfileEntry(GameProfile gameProfile, Date date, Object obj) {
            this(gameProfile, date);
        }
    }

    public class Serializer implements JsonDeserializer, JsonSerializer {
        public Serializer() {
        }

        public JsonElement serialize(PlayerProfileCache.ProfileEntry profileEntry, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", profileEntry.getGameProfile().getName());
            UUID id = profileEntry.getGameProfile().getId();
            jsonObject.addProperty("uuid", id == null ? "" : id.toString());
            jsonObject.addProperty("expiresOn", PlayerProfileCache.dateFormat.format(profileEntry.getDate()));
            return jsonObject;
        }

        public PlayerProfileCache.ProfileEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
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

                        return PlayerProfileCache.this.new ProfileEntry(new GameProfile(uuid, asString2), date, null);
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

        public JsonElement serialize(Object obj, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((PlayerProfileCache.ProfileEntry)obj, type, jsonSerializationContext);
        }

        public Serializer(Object obj) {
            this();
        }
    }
}
