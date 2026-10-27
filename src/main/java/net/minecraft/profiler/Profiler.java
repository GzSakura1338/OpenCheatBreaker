package net.minecraft.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.optifine.Config;
import net.optifine.Lagometer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler {
   public static Logger logger = LogManager.getLogger();
   public List<String> sectionList = Lists.newArrayList();
   public List<Long> timestampList = Lists.newArrayList();
   public boolean profilingEnabled;
   public String profilingSection = "";
   public Map<String, Long> profilingMap = Maps.newHashMap();
   public boolean profilerGlobalEnabled = true;
   public boolean profilerLocalEnabled = this.profilerGlobalEnabled;
   public static final String SCHEDULED_EXECUTABLES = "scheduledExecutables";
   public static final String TICK = "tick";
   public static final String PRE_RENDER_ERRORS = "preRenderErrors";
   public static final String RENDER = "render";
   public static final String DISPLAY = "display";
   public static int HASH_SCHEDULED_EXECUTABLES = "scheduledExecutables".hashCode();
   public static int HASH_TICK = "tick".hashCode();
   public static int HASH_PRE_RENDER_ERRORS = "preRenderErrors".hashCode();
   public static int HASH_RENDER = "render".hashCode();
   public static int HASH_DISPLAY = "display".hashCode();

   public void clearProfiling() {
      this.profilingMap.clear();
      this.profilingSection = "";
      this.sectionList.clear();
      this.profilerLocalEnabled = this.profilerGlobalEnabled;
   }

   public void startSection(String name) {
      if (Lagometer.isActive()) {
         int n = name.hashCode();
         if (n == HASH_SCHEDULED_EXECUTABLES && name.equals("scheduledExecutables")) {
            Lagometer.timerScheduledExecutables.start();
         } else if (n == HASH_TICK && name.equals("tick") && Config.isMinecraftThread()) {
            Lagometer.timerScheduledExecutables.end();
            Lagometer.timerTick.start();
         } else if (n == HASH_PRE_RENDER_ERRORS && name.equals("preRenderErrors")) {
            Lagometer.timerTick.end();
         }
      }

      if (Config.isFastRender()) {
         int n = name.hashCode();
         if (n == HASH_RENDER && name.equals("render")) {
            GlStateManager.clearEnabled = false;
         } else if (n == HASH_DISPLAY && name.equals("display")) {
            GlStateManager.clearEnabled = true;
         }
      }

      if (this.profilerLocalEnabled && this.profilingEnabled) {
         if (this.profilingSection.length() > 0) {
            this.profilingSection = this.profilingSection + ".";
         }

         this.profilingSection = this.profilingSection + name;
         this.sectionList.add(this.profilingSection);
         this.timestampList.add(System.nanoTime());
      }
   }

   public void endSection() {
      if (this.profilerLocalEnabled && this.profilingEnabled) {
         long nanoTime = System.nanoTime();
         long l = this.timestampList.remove(this.timestampList.size() - 1);
         this.sectionList.remove(this.sectionList.size() - 1);
         long l2 = nanoTime - l;
         if (this.profilingMap.containsKey(this.profilingSection)) {
            this.profilingMap.put(this.profilingSection, this.profilingMap.get(this.profilingSection) + l2);
         } else {
            this.profilingMap.put(this.profilingSection, l2);
         }

         if (l2 > 100000000L) {
            logger.warn("Something's taking too long! '" + this.profilingSection + "' took aprox " + l2 / 1000000.0 + " ms");
         }

         this.profilingSection = !this.sectionList.isEmpty() ? this.sectionList.get(this.sectionList.size() - 1) : "";
      }
   }

   public List<Profiler.Result> getProfilingData(String profilerName) {
      if (!this.profilingEnabled) {
         return null;
      }

      long l = this.profilingMap.containsKey("root") ? this.profilingMap.get("root") : 0L;
      long l2 = this.profilingMap.containsKey(profilerName) ? this.profilingMap.get(profilerName) : -1L;
      ArrayList newArrayList = Lists.newArrayList();
      if (profilerName.length() > 0) {
         profilerName = profilerName + ".";
      }

      long l3 = 0L;

      for (String str : this.profilingMap.keySet()) {
         if (str.length() > profilerName.length() && str.startsWith(profilerName) && str.indexOf(".", profilerName.length() + 1) < 0) {
            l3 += this.profilingMap.get(str);
         }
      }

      float f = (float)l3;
      if (l3 < l2) {
         l3 = l2;
      }

      if (l < l3) {
         l = l3;
      }

      for (String str : this.profilingMap.keySet()) {
         if (str.length() > profilerName.length() && str.startsWith(profilerName) && str.indexOf(".", profilerName.length() + 1) < 0) {
            long l4 = this.profilingMap.get(str);
            double d = l4 * 100.0 / l3;
            double d2 = l4 * 100.0 / l;
            String substring = str.substring(profilerName.length());
            newArrayList.add(new Profiler.Result(substring, d, d2));
         }
      }

      for (String str : this.profilingMap.keySet()) {
         this.profilingMap.put(str, this.profilingMap.get(str) * 950L / 1000L);
      }

      if ((float)l3 > f) {
         newArrayList.add(new Profiler.Result("unspecified", ((float)l3 - f) * 100.0 / l3, ((float)l3 - f) * 100.0 / l));
      }

      Collections.sort(newArrayList);
      newArrayList.add(0, new Profiler.Result(profilerName, 100.0, l3 * 100.0 / l));
      return newArrayList;
   }

   public void endStartSection(String name) {
      if (this.profilerLocalEnabled) {
         this.endSection();
         this.startSection(name);
      }
   }

   public String getNameOfLastSection() {
      return this.sectionList.size() == 0 ? "[UNKNOWN]" : this.sectionList.get(this.sectionList.size() - 1);
   }

   public void startSection(Class<?> clazz) {
      if (this.profilingEnabled) {
         this.startSection(clazz.getSimpleName());
      }
   }

   public static final class Result implements Comparable<Profiler.Result> {
      public String name;
      public double relativePercentage;
      public double absolutePercentage;

      public Result(String profilerName, double usePercentage, double totalUsePercentage) {
         this.name = profilerName;
         this.relativePercentage = usePercentage;
         this.absolutePercentage = totalUsePercentage;
      }

      public int compareTo(Profiler.Result result) {
         return result.relativePercentage < this.relativePercentage
            ? -1
            : (result.relativePercentage > this.relativePercentage ? 1 : result.name.compareTo(this.name));
      }

      public int getColor() {
         return (this.name.hashCode() & 11184810) + 4473924;
      }
   }
}
