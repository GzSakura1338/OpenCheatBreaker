package com.cheatbreaker.client.util;

import com.cheatbreaker.client.CheatBreaker;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class ServerTransferLookup {
    public static final String TEXT_MC_TRANSFER_ACCEPT_FROM = "mc_transfer_accept_from";

    public static void lookupTxtRecords(String str, Consumer<HashSet<String>> consumer) {
        new Thread(() -> {
            try {
                Class.forName("com.sun.jndi.dns.DnsContextFactory");
                Hashtable hashtable = new Hashtable();
                hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
                hashtable.put("java.naming.provider.url", "dns:");
                hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
                InitialDirContext initialDirContext = new InitialDirContext(hashtable);

                try {
                    String str2 = str;

                    HashSet set;
                    for (set = new HashSet(); str2.indexOf(".") < str2.lastIndexOf(".") && set.isEmpty(); str2 = str2.substring(str2.indexOf(".") + 1)) {
                        readTxtRecords(str2, set, initialDirContext);
                    }

                    if (set.isEmpty()) {
                        readTxtRecords(str2, set, initialDirContext);
                    }

                    consumer.accept(set);
                } catch (Throwable e) {
                    consumer.accept(null);
                }
            } catch (Throwable e) {
                CheatBreaker.getInstance().getLogger().info("Failed to start SRV redirect resolved, some servers might not work", e);
            }
        }).start();
    }

    public static void readTxtRecords(String str, Set<String> set, InitialDirContext initialDirContext) {
        try {
            Attributes attributes = initialDirContext.getAttributes(str, new String[]{"TXT"});
            Attribute attribute = attributes.get("TXT");
            if (attribute != null) {
                NamingEnumeration all = attribute.getAll();

                while (all.hasMore()) {
                    Object obj = all.next();
                    String[] strs;
                    if (obj != null && (strs = obj.toString().split("=")).length == 2 && strs[0].equalsIgnoreCase("mc_transfer_accept_from")) {
                        String[] split = strs[1].split(",");
                        set.addAll(Arrays.stream(split).collect(Collectors.toSet()));
                    }
                }
            }
        } catch (NamingException e) {
        }
    }

    public static String createStringBuffer(String str) {
        return new StringBuffer(str).reverse().toString();
    }
}
