package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatisticsFile extends StatFileWriter {
    public static Logger logger = LogManager.getLogger();
    public Set pendingStats = Sets.newHashSet();
    public int lastStatsUpdate = -300;
    public boolean flag = false;
    public MinecraftServer server;
    public File file;

    public StatisticsFile(MinecraftServer minecraftServer, File file) {
        this.server = minecraftServer;
        this.file = file;
    }

    public void run() {
        if (this.file.isFile()) {
            try {
                this.stats.clear();
                this.stats.putAll(this.findMap(FileUtils.readFileToString(this.file)));
            } catch (IOException e) {
                logger.error("Couldn't read statistics file " + this.file, e);
            } catch (JsonParseException e) {
                logger.error("Couldn't parse statistics file " + this.file, e);
            }
        }
    }

    public void save() {
        try {
            FileUtils.writeStringToFile(this.file, formatText(this.stats));
        } catch (IOException e) {
            logger.error("Couldn't save stats", e);
        }
    }

    @Override
    public void handleEntityPlayer(EntityPlayer entityPlayer, StatBase statBase, int n) {
        int n2 = statBase.isAchievement() ? this.writeStat(statBase) : 0;
        super.handleEntityPlayer(entityPlayer, statBase, n);
        this.pendingStats.add(statBase);
        if (statBase.isAchievement() && n2 == 0 && n > 0) {
            this.flag = true;
            if (this.server.isAnnouncingPlayerAchievements()) {
                this.server
                    .getConfigurationManager()
                    .sendChatMsg(new ChatComponentTranslation("chat.type.achievement", entityPlayer.getFormattedCommandSenderName(), statBase.createChatComponentText()));
            }
        }
    }

    public Set takePendingStats() {
        HashSet newHashSet = Sets.newHashSet(this.pendingStats);
        this.pendingStats.clear();
        this.flag = false;
        return newHashSet;
    }

    public Map findMap(String str) {
        JsonElement parse = new JsonParser().parse(str);
        if (!parse.isJsonObject()) {
            return Maps.newHashMap();
        }

        JsonObject asJsonObject = parse.getAsJsonObject();
        HashMap newHashMap = Maps.newHashMap();

        for (Entry entry : asJsonObject.entrySet()) {
            StatBase oneShotStat = StatList.getOneShotStat((String)entry.getKey());
            if (oneShotStat != null) {
                TupleIntJsonSerializable tupleIntJsonSerializable = new TupleIntJsonSerializable();
                if (((JsonElement)entry.getValue()).isJsonPrimitive() && ((JsonElement)entry.getValue()).getAsJsonPrimitive().isNumber()) {
                    tupleIntJsonSerializable.setIntegerValue(((JsonElement)entry.getValue()).getAsInt());
                } else if (((JsonElement)entry.getValue()).isJsonObject()) {
                    JsonObject asJsonObject2 = ((JsonElement)entry.getValue()).getAsJsonObject();
                    if (asJsonObject2.has("value") && asJsonObject2.get("value").isJsonPrimitive() && asJsonObject2.get("value").getAsJsonPrimitive().isNumber()) {
                        tupleIntJsonSerializable.setIntegerValue(asJsonObject2.getAsJsonPrimitive("value").getAsInt());
                    }

                    if (asJsonObject2.has("progress") && oneShotStat.getJsonElementProvider() != null) {
                        try {
                            Constructor constructor = oneShotStat.getJsonElementProvider().getConstructor();
                            IJsonSerializable newInstance = (IJsonSerializable)constructor.newInstance();
                            newInstance.read(asJsonObject2.get("progress"));
                            tupleIntJsonSerializable.setJsonSerializableValue(newInstance);
                        } catch (Throwable e) {
                            logger.warn("Invalid statistic progress in " + this.file, e);
                        }
                    }
                }

                newHashMap.put(oneShotStat, tupleIntJsonSerializable);
            } else {
                logger.warn("Invalid statistic in " + this.file + ": Don't know what " + entry.getKey() + " is");
            }
        }

        return newHashMap;
    }

    public static String formatText(Map map) {
        JsonObject jsonObject = new JsonObject();

        for (Entry entry : (Iterable<Entry>)map.entrySet()) {
            if (((TupleIntJsonSerializable)entry.getValue()).getJsonSerializableValue() != null) {
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("value", ((TupleIntJsonSerializable)entry.getValue()).getIntegerValue());

                try {
                    jsonObject2.add("progress", ((TupleIntJsonSerializable)entry.getValue()).getJsonSerializableValue().getSerializableElement());
                } catch (Throwable e) {
                    logger.warn("Couldn't save statistic " + ((StatBase)entry.getKey()).getStatName() + ": error serializing progress", e);
                }

                jsonObject.add(((StatBase)entry.getKey()).statId, jsonObject2);
            } else {
                jsonObject.addProperty(((StatBase)entry.getKey()).statId, ((TupleIntJsonSerializable)entry.getValue()).getIntegerValue());
            }
        }

        return jsonObject.toString();
    }

    public void updateStatSet() {
        for (StatBase statBase : (Iterable<StatBase>)this.stats.keySet()) {
            this.pendingStats.add(statBase);
        }
    }

    public void handleEntityPlayerMP(EntityPlayerMP entityPlayerMP) {
        int tickCounter = this.server.getTickCounter();
        HashMap newHashMap = Maps.newHashMap();
        if (this.flag || tickCounter - this.lastStatsUpdate > 300) {
            this.lastStatsUpdate = tickCounter;

            for (StatBase statBase : (Iterable<StatBase>)this.takePendingStats()) {
                newHashMap.put(statBase, this.writeStat(statBase));
            }
        }

        entityPlayerMP.playerNetServerHandler.sendPacket(new S37PacketStatistics(newHashMap));
    }

    public void handleEntityPlayerMP2(EntityPlayerMP entityPlayerMP) {
        HashMap newHashMap = Maps.newHashMap();

        for (Achievement achievement : (Iterable<Achievement>)AchievementList.achievementList) {
            if (this.hasAchievementUnlocked(achievement)) {
                newHashMap.put(achievement, this.writeStat(achievement));
                this.pendingStats.remove(achievement);
            }
        }

        entityPlayerMP.playerNetServerHandler.sendPacket(new S37PacketStatistics(newHashMap));
    }

    public boolean isFlag() {
        return this.flag;
    }
}
