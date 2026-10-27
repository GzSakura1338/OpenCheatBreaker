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
   public Map<String, Object> snooperStats = Maps.newHashMap();
   public Map<String, Object> clientStats = Maps.newHashMap();
   public String uniqueID = UUID.randomUUID().toString();
   public Timer threadTrigger = new Timer("Snooper Timer", true);
   public Object syncLock = new Object();
   public URL serverUrl;
   public IPlayerUsage playerStatsCollector;
   public long minecraftStartTimeMilis;
   public boolean isRunning;
   public int selfCounter;

   public PlayerUsageSnooper(String side, IPlayerUsage playerStatCollector, long startTime) {
      try {
         this.serverUrl = new URL("http://snoop.minecraft.net/" + side + "?version=" + 2);
      } catch (MalformedURLException e) {
         throw new IllegalArgumentException();
      }

      this.playerStatsCollector = playerStatCollector;
      this.minecraftStartTimeMilis = startTime;
   }

   public void startSnooper() {
      if (!this.isRunning) {
         this.isRunning = true;
         this.addOSData();
         this.threadTrigger.schedule(new TimerTask() {
            @Override
            public void run() {
               if (PlayerUsageSnooper.this.playerStatsCollector.isSnooperEnabled()) {
                  HashMap map;
                  synchronized (PlayerUsageSnooper.this.syncLock) {
                     map = Maps.newHashMap(PlayerUsageSnooper.this.clientStats);
                     if (PlayerUsageSnooper.this.selfCounter == 0) {
                        map.putAll(PlayerUsageSnooper.this.snooperStats);
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

   public void addOSData() {
      this.addJvmArgsToSnooper();
      this.addClientStat("snooper_token", this.uniqueID);
      this.addStatToSnooper("snooper_token", this.uniqueID);
      this.addStatToSnooper("os_name", System.getProperty("os.name"));
      this.addStatToSnooper("os_version", System.getProperty("os.version"));
      this.addStatToSnooper("os_architecture", System.getProperty("os.arch"));
      this.addStatToSnooper("java_version", System.getProperty("java.version"));
      this.addClientStat("version", "1.8.9");
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

   public void addClientStat(String statName, Object statValue) {
      synchronized (this.syncLock) {
         this.clientStats.put(statName, statValue);
      }
   }

   public void addStatToSnooper(String statName, Object statValue) {
      synchronized (this.syncLock) {
         this.snooperStats.put(statName, statValue);
      }
   }

   public Map<String, String> getCurrentStats() {
      LinkedHashMap newLinkedHashMap = Maps.newLinkedHashMap();
      synchronized (this.syncLock) {
         this.addMemoryStatsToSnooper();

         for (Entry entry : this.snooperStats.entrySet()) {
            newLinkedHashMap.put(entry.getKey(), entry.getValue().toString());
         }

         for (Entry entry : this.clientStats.entrySet()) {
            newLinkedHashMap.put(entry.getKey(), entry.getValue().toString());
         }

         return newLinkedHashMap;
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
