package net.minecraft.network;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkStatistics {
    public static Logger LOGGER = LogManager.getLogger();
    public static Marker NETSTAT_MARKER = MarkerManager.getMarker("NETSTAT_MARKER", NetworkManager.logMarkerStat);
    public NetworkStatistics.Tracker tracker = new NetworkStatistics.Tracker();
    public NetworkStatistics.Tracker tracker2 = new NetworkStatistics.Tracker();

    public void process(int n, long l) {
        this.tracker.process(n, l);
    }

    public void process2(int n, long l) {
        this.tracker2.process(n, l);
    }

    public long getLongValue() {
        return this.tracker.getLongValue();
    }

    public long getLongValue2() {
        return this.tracker2.getLongValue();
    }

    public long getLongValue3() {
        return this.tracker.getLongValue2();
    }

    public long getLongValue4() {
        return this.tracker2.getLongValue2();
    }

    public NetworkStatistics.PacketStat getPacketStat() {
        return this.tracker.getPacketStat();
    }

    public NetworkStatistics.PacketStat getPacketStat2() {
        return this.tracker.getPacketStat2();
    }

    public NetworkStatistics.PacketStat getPacketStat3() {
        return this.tracker2.getPacketStat();
    }

    public NetworkStatistics.PacketStat getPacketStat4() {
        return this.tracker2.getPacketStat2();
    }

    public NetworkStatistics.PacketStat findPacketStat(int n) {
        return this.tracker.createPacketStat(n);
    }

    public NetworkStatistics.PacketStat findPacketStat2(int n) {
        return this.tracker2.createPacketStat(n);
    }

    public static class PacketStat {
        public int packetId;
        public NetworkStatistics.PacketStatData data;

        public PacketStat(int id, NetworkStatistics.PacketStatData statData) {
            this.packetId = id;
            this.data = statData;
        }

        @Override
        public String toString() {
            return "PacketStat(" + this.packetId + ")" + this.data;
        }
    }

    public static class PacketStatData {
        public long totalBytes;
        public int count;
        public double averageBytes;

        public PacketStatData(long l, int n, double d) {
            this.totalBytes = l;
            this.count = n;
            this.averageBytes = d;
        }

        public NetworkStatistics.PacketStatData createPacketStatData(long l) {
            return new NetworkStatistics.PacketStatData(l + this.totalBytes, this.count + 1, (l + this.totalBytes) / (this.count + 1));
        }

        public long getTotalBytes() {
            return this.totalBytes;
        }

        public int getCount() {
            return this.count;
        }

        @Override
        public String toString() {
            return "{totalBytes=" + this.totalBytes + ", count=" + this.count + ", averageBytes=" + this.averageBytes + '}';
        }

        public PacketStatData(long l, int n, double d, Object obj) {
            this(l, n, d);
        }
    }

    public static class Tracker {
        public AtomicReference[] atomicReferenceArray = new AtomicReference[100];

        public Tracker() {
            for (int i = 0; i < 100; i++) {
                this.atomicReferenceArray[i] = new AtomicReference<>(new NetworkStatistics.PacketStatData(0L, 0, 0.0, null));
            }
        }

        public void process(int n, long l) {
            try {
                if (n < 0 || n >= 100) {
                    return;
                }

                NetworkStatistics.PacketStatData packetStatData;
                NetworkStatistics.PacketStatData packetStatData2;
                do {
                    packetStatData = (NetworkStatistics.PacketStatData)this.atomicReferenceArray[n].get();
                    packetStatData2 = packetStatData.createPacketStatData(l);
                } while (!this.atomicReferenceArray[n].compareAndSet(packetStatData, packetStatData2));
            } catch (Exception e) {
                if (NetworkStatistics.LOGGER.isDebugEnabled()) {
                    NetworkStatistics.LOGGER.debug(NetworkStatistics.NETSTAT_MARKER, "NetStat failed with packetId: " + n, e);
                }
            }
        }

        public long getLongValue() {
            long l = 0L;

            for (int i = 0; i < 100; i++) {
                l += ((NetworkStatistics.PacketStatData)this.atomicReferenceArray[i].get()).getTotalBytes();
            }

            return l;
        }

        public long getLongValue2() {
            long l = 0L;

            for (int i = 0; i < 100; i++) {
                l += ((NetworkStatistics.PacketStatData)this.atomicReferenceArray[i].get()).getCount();
            }

            return l;
        }

        public NetworkStatistics.PacketStat getPacketStat() {
            int n = -1;
            NetworkStatistics.PacketStatData packetStatData = new NetworkStatistics.PacketStatData(-1L, -1, 0.0, null);

            for (int i = 0; i < 100; i++) {
                NetworkStatistics.PacketStatData packetStatData2 = (NetworkStatistics.PacketStatData)this.atomicReferenceArray[i].get();
                if (packetStatData2.totalBytes > packetStatData.totalBytes) {
                    n = i;
                    packetStatData = packetStatData2;
                }
            }

            return new NetworkStatistics.PacketStat(n, packetStatData);
        }

        public NetworkStatistics.PacketStat getPacketStat2() {
            int n = -1;
            NetworkStatistics.PacketStatData packetStatData = new NetworkStatistics.PacketStatData(-1L, -1, 0.0, null);

            for (int i = 0; i < 100; i++) {
                NetworkStatistics.PacketStatData packetStatData2 = (NetworkStatistics.PacketStatData)this.atomicReferenceArray[i].get();
                if (packetStatData2.count > packetStatData.count) {
                    n = i;
                    packetStatData = packetStatData2;
                }
            }

            return new NetworkStatistics.PacketStat(n, packetStatData);
        }

        public NetworkStatistics.PacketStat createPacketStat(int n) {
            return n >= 0 && n < 100
                ? new NetworkStatistics.PacketStat(n, (NetworkStatistics.PacketStatData)this.atomicReferenceArray[n].get())
                : null;
        }
    }
}
