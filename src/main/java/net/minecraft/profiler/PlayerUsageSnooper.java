package net.minecraft.profiler;

import com.google.common.collect.Maps;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import net.minecraft.util.HttpUtil;

public class PlayerUsageSnooper {
    public Map systemInfo = Maps.newHashMap();
    public Map gameInfo = Maps.newHashMap();
    public String uniqueID = UUID.randomUUID().toString();
    public Timer threadTrigger = new Timer("Snooper Timer", true);
    public Object syncLock = new Object();
    public URL serverUrl;
    public IPlayerUsage playerStatsCollector;
    public long minecraftStartTimeMilis;
    public boolean isRunning;
    public int selfCounter;

    public PlayerUsageSnooper(String str, IPlayerUsage playerUsage, long l) {
        try {
            this.serverUrl = new URL("http://snoop.minecraft.net/" + str + "?version=" + 2);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException();
        }

        this.playerStatsCollector = playerUsage;
        this.minecraftStartTimeMilis = l;
    }

    public void startSnooper() {
        if (!this.isRunning) {
            this.isRunning = true;
            this.addJavaInfo();
            this.threadTrigger.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (PlayerUsageSnooper.this.playerStatsCollector.isSnooperEnabled()) {
                        HashMap map;
                        synchronized (PlayerUsageSnooper.this.syncLock) {
                            map = new HashMap(PlayerUsageSnooper.this.gameInfo);
                            if (PlayerUsageSnooper.this.selfCounter == 0) {
                                map.putAll(PlayerUsageSnooper.this.systemInfo);
                            }

                            map.put("snooper_count", PlayerUsageSnooper.this.selfCounter++);
                            map.put("snooper_token", PlayerUsageSnooper.this.uniqueID);
                        }

                        HttpUtil.postMap(PlayerUsageSnooper.this.serverUrl, map, true);
                    }
                }
            }, 0L, 900000L);
        }
    }

    public void addJavaInfo() {
        this.addJvmArgsToSnooper();
        this.addClientStat("snooper_token", this.uniqueID);
        this.addStatToSnooper("snooper_token", this.uniqueID);
        this.addStatToSnooper("os_name", System.getProperty("os.name"));
        this.addStatToSnooper("os_version", System.getProperty("os.version"));
        this.addStatToSnooper("os_architecture", System.getProperty("os.arch"));
        this.addStatToSnooper("java_version", System.getProperty("java.version"));
        this.addStatToSnooper("version", "1.7.10");
        this.playerStatsCollector.addServerTypeToSnooper(this);
    }

    public void addJvmArgsToSnooper() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List inputArguments = runtimeMXBean.getInputArguments();
        int n = 0;

        for (String str : (Iterable<String>)inputArguments) {
            if (str.startsWith("-X")) {
                this.addClientStat("jvm_arg[" + n++ + "]", str);
            }
        }

        this.addClientStat("jvm_args", n);
    }

    public void addMemoryStatsToSnooper() {
        this.addStatToSnooper("memory_total", Runtime.getRuntime().totalMemory());
        this.addStatToSnooper("memory_max", Runtime.getRuntime().maxMemory());
        this.addStatToSnooper("memory_free", Runtime.getRuntime().freeMemory());
        this.addStatToSnooper("cpu_cores", Runtime.getRuntime().availableProcessors());
        this.playerStatsCollector.addServerStatsToSnooper(this);
    }

    public void addClientStat(String str, Object obj) {
        Object syncLock = this.syncLock;
        synchronized (this.syncLock) {
            this.gameInfo.put(str, obj);
        }
    }

    public void addStatToSnooper(String str, Object obj) {
        Object syncLock = this.syncLock;
        synchronized (this.syncLock) {
            this.systemInfo.put(str, obj);
        }
    }

    public Map getCurrentStats() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        Object syncLock = this.syncLock;
        synchronized (this.syncLock) {
            this.addMemoryStatsToSnooper();

            for (Entry entry : (Iterable<Entry>)this.systemInfo.entrySet()) {
                linkedHashMap.put(entry.getKey(), entry.getValue().toString());
            }

            for (Entry entry : (Iterable<Entry>)this.gameInfo.entrySet()) {
                linkedHashMap.put(entry.getKey(), entry.getValue().toString());
            }

            return linkedHashMap;
        }
    }

    public boolean isSnooperRunning() {
        return this.isRunning;
    }

    public void stopSnooper() {
        this.threadTrigger.cancel();
    }

    public String getUniqueID() {
        return this.uniqueID;
    }

    public long getMinecraftStartTimeMillis() {
        return this.minecraftStartTimeMilis;
    }
}
