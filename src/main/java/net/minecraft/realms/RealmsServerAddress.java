package net.minecraft.realms;

import java.util.Hashtable;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class RealmsServerAddress {
    public String host;
    public int port;

    public RealmsServerAddress(String str, int n) {
        this.host = str;
        this.port = n;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public static RealmsServerAddress parseString(String str) {
        if (str == null) {
            return null;
        }

        String[] split = str.split(":");
        if (str.startsWith("[")) {
            int indexOf = str.indexOf("]");
            if (indexOf > 0) {
                String substring = str.substring(1, indexOf);
                String trim = str.substring(indexOf + 1).trim();
                if (trim.startsWith(":") && trim.length() > 0) {
                    trim = trim.substring(1);
                    split = new String[]{substring, trim};
                } else {
                    split = new String[]{substring};
                }
            }
        }

        if (split.length > 2) {
            split = new String[]{str};
        }

        String str2 = split[0];
        int n = split.length > 1 ? parseInt(split[1], 25565) : 25565;
        if (n == 25565) {
            String[] lookupSrv2 = lookupSrv(str2);
            str2 = lookupSrv2[0];
            n = parseInt(lookupSrv2[1], 25565);
        }

        return new RealmsServerAddress(str2, n);
    }

    public static String[] lookupSrv(String str) {
        try {
            String str2 = "com.sun.jndi.dns.DnsContextFactory";
            Class.forName("com.sun.jndi.dns.DnsContextFactory");
            Hashtable hashtable = new Hashtable();
            hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            hashtable.put("java.naming.provider.url", "dns:");
            hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
            InitialDirContext initialDirContext = new InitialDirContext(hashtable);
            Attributes attributes = initialDirContext.getAttributes("_minecraft._tcp." + str, new String[]{"SRV"});
            String[] split = attributes.get("srv").get().toString().split(" ", 4);
            return new String[]{split[3], split[2]};
        } catch (Throwable e) {
            return new String[]{str, Integer.toString(25565)};
        }
    }

    public static int parseInt(String str, int n) {
        try {
            return Integer.parseInt(str.trim());
        } catch (Exception e) {
            return n;
        }
    }
}
