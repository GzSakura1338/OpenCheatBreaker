package net.minecraft.profiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.optifine.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler {
    public static Logger logger = LogManager.getLogger();
    public List sectionList = new ArrayList();
    public List timestampList = new ArrayList();
    public boolean profilingEnabled;
    public String profilingSection = "";
    public Map profilingMap = new HashMap();
    public boolean profilerGlobalEnabled = true;
    public boolean profilerLocalEnabled = this.profilerGlobalEnabled;
    public long startTickNano = 0L;
    public long timeTickNano = 0L;
    public long startUpdateChunksNano = 0L;
    public long timeUpdateChunksNano = 0L;

    public void clearProfiling() {
        this.profilingMap.clear();
        this.profilingSection = "";
        this.sectionList.clear();
        this.profilerLocalEnabled = this.profilerGlobalEnabled;
    }

    public void startSection(String name) {
        if (Config.getGameSettings().showDebugInfo) {
            if (this.startTickNano == 0L && name.equals("tick")) {
                this.startTickNano = System.nanoTime();
            }

            if (this.startTickNano != 0L && name.equals("preRenderErrors")) {
                this.timeTickNano = System.nanoTime() - this.startTickNano;
                this.startTickNano = 0L;
            }

            if (this.startUpdateChunksNano == 0L && name.equals("updatechunks")) {
                this.startUpdateChunksNano = System.nanoTime();
            }

            if (this.startUpdateChunksNano != 0L && name.equals("terrain")) {
                this.timeUpdateChunksNano = System.nanoTime() - this.startUpdateChunksNano;
                this.startUpdateChunksNano = 0L;
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
            long l = (Long)this.timestampList.remove(this.timestampList.size() - 1);
            this.sectionList.remove(this.sectionList.size() - 1);
            long l2 = nanoTime - l;
            if (this.profilingMap.containsKey(this.profilingSection)) {
                this.profilingMap.put(this.profilingSection, (Long)this.profilingMap.get(this.profilingSection) + l2);
            } else {
                this.profilingMap.put(this.profilingSection, l2);
            }

            if (l2 > 100000000L) {
                logger.warn("Something's taking too long! '" + this.profilingSection + "' took aprox " + l2 / 1000000.0 + " ms");
            }

            this.profilingSection = !this.sectionList.isEmpty() ? (String)this.sectionList.get(this.sectionList.size() - 1) : "";
        }
    }

    public List getProfilingData(String str) {
        this.profilerLocalEnabled = this.profilerGlobalEnabled;
        if (!this.profilerLocalEnabled) {
            return new ArrayList<>(Arrays.asList(new Profiler.Result("root", 0.0, 0.0)));
        }

        if (!this.profilingEnabled) {
            return null;
        }

        long l = this.profilingMap.containsKey("root") ? (Long)this.profilingMap.get("root") : 0L;
        long l2 = this.profilingMap.containsKey(str) ? (Long)this.profilingMap.get(str) : -1L;
        ArrayList list = new ArrayList();
        if (str.length() > 0) {
            str = str + ".";
        }

        long l3 = 0L;

        for (String str2 : (Iterable<String>)this.profilingMap.keySet()) {
            if (str2.length() > str.length() && str2.startsWith(str) && str2.indexOf(".", str.length() + 1) < 0) {
                l3 += (Long)this.profilingMap.get(str2);
            }
        }

        float f = (float)l3;
        if (l3 < l2) {
            l3 = l2;
        }

        if (l < l3) {
            l = l3;
        }

        for (String str2 : (Iterable<String>)this.profilingMap.keySet()) {
            if (str2.length() > str.length() && str2.startsWith(str) && str2.indexOf(".", str.length() + 1) < 0) {
                long l4 = (Long)this.profilingMap.get(str2);
                double d = l4 * 100.0 / l3;
                double d2 = l4 * 100.0 / l;
                String substring = str2.substring(str.length());
                list.add(new Profiler.Result(substring, d, d2));
            }
        }

        for (String str2 : (Iterable<String>)this.profilingMap.keySet()) {
            this.profilingMap.put(str2, (Long)this.profilingMap.get(str2) * 999L / 1000L);
        }

        if ((float)l3 > f) {
            list.add(new Profiler.Result("unspecified", ((float)l3 - f) * 100.0 / l3, ((float)l3 - f) * 100.0 / l));
        }

        Collections.sort(list);
        list.add(0, new Profiler.Result(str, 100.0, l3 * 100.0 / l));
        return list;
    }

    public void endStartSection(String name) {
        if (this.profilerLocalEnabled) {
            this.endSection();
            this.startSection(name);
        }
    }

    public String getNameOfLastSection() {
        return this.sectionList.size() == 0 ? "[UNKNOWN]" : (String)this.sectionList.get(this.sectionList.size() - 1);
    }

    public String getProfilingSection() {
        return this.profilingSection;
    }

    public static final class Result implements Comparable {
        public String text;
        public double doubleValue;
        public double doubleValue2;

        public Result(String str, double d, double d2) {
            this.text = str;
            this.doubleValue = d;
            this.doubleValue2 = d2;
        }

        public int compareTo(Profiler.Result result) {
            return result.doubleValue < this.doubleValue ? -1 : (result.doubleValue > this.doubleValue ? 1 : result.text.compareTo(this.text));
        }

        public int hashCode2() {
            return (this.text.hashCode() & 11184810) + 4473924;
        }

        @Override
        public int compareTo(Object obj) {
            return this.compareTo((Profiler.Result)obj);
        }
    }
}
