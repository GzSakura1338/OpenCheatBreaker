package net.minecraft.server.management;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreYggdrasilConverter {
    public static Logger LOGGER = LogManager.getLogger();
    public static File OLD_IPBAN_FILE = new File("banned-ips.txt");
    public static File OLD_PLAYERBAN_FILE = new File("banned-players.txt");
    public static File OLD_OPS_FILE = new File("ops.txt");
    public static File OLD_WHITELIST_FILE = new File("white-list.txt");

    public static void lookupNames(MinecraftServer server, Collection names, ProfileLookupCallback callback) {
        String[] toArray = (String[])Iterators.toArray(Iterators.filter(names.iterator(), new Predicate() {
            public boolean apply(String str) {
                return !StringUtils.isNullOrEmpty(str);
            }

            public boolean apply(Object obj) {
                return this.apply((String)obj);
            }
        }), String.class);
        if (server.isServerInOnlineMode()) {
            server.getGameProfileRepository().findProfilesByNames(toArray, Agent.MINECRAFT, callback);
        } else {
            String[] strs = toArray;
            int length = toArray.length;

            for (int i = 0; i < length; i++) {
                String str = strs[i];
                UUID uUID = EntityPlayer.getUUID(new GameProfile(null, str));
                GameProfile gameProfile = new GameProfile(uUID, str);
                callback.onProfileLookupSucceeded(gameProfile);
            }
        }
    }

    public static String getPlayerUuidByName(String str) {
        if (!StringUtils.isNullOrEmpty(str) && str.length() <= 16) {
            final MinecraftServer server = MinecraftServer.getServer();
            GameProfile gameProfileForUsername = server.getPlayerProfileCache().getGameProfileForUsername(str);
            if (gameProfileForUsername != null && gameProfileForUsername.getId() != null) {
                return gameProfileForUsername.getId().toString();
            } else if (!server.isSinglePlayer() && server.isServerInOnlineMode()) {
                final ArrayList newArrayList = Lists.newArrayList();
                ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameProfile) {
                        server.getPlayerProfileCache().add(gameProfile);
                        newArrayList.add(gameProfile);
                    }

                    public void onProfileLookupFailed(GameProfile gameProfile, Exception exception) {
                        PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for " + gameProfile.getName(), exception);
                    }
                };
                lookupNames(server, Lists.newArrayList(new String[]{str}), profileLookupCallback);
                return newArrayList.size() > 0 && ((GameProfile)newArrayList.get(0)).getId() != null ? ((GameProfile)newArrayList.get(0)).getId().toString() : "";
            } else {
                return EntityPlayer.getUUID(new GameProfile(null, str)).toString();
            }
        } else {
            return str;
        }
    }
}
