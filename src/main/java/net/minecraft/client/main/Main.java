package net.minecraft.client.main;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.util.DeveloperFlags;
import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Main {
    public static Type type = new ParameterizedType() {
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{String.class, new ParameterizedType() {
                @Override
                public Type[] getActualTypeArguments() {
                    return new Type[]{String.class};
                }

                @Override
                public Type getRawType() {
                    return Collection.class;
                }

                @Override
                public Type getOwnerType() {
                    return null;
                }
            }};
        }

        @Override
        public Type getRawType() {
            return Map.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    };

    public static void main(String[] strs) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();
        optionParser.accepts("demo");
        optionParser.accepts("fullscreen");
        optionParser.accepts("debug");
        ArgumentAcceptingOptionSpec withRequiredArg = optionParser.accepts("server").withRequiredArg();
        ArgumentAcceptingOptionSpec defaultsTo = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565, new Integer[0]);
        ArgumentAcceptingOptionSpec defaultsTo2 = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
        ArgumentAcceptingOptionSpec ofType = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec ofType2 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec withRequiredArg2 = optionParser.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec ofType3 = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
        ArgumentAcceptingOptionSpec withRequiredArg3 = optionParser.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec withRequiredArg4 = optionParser.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec defaultsTo3 = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L, new String[0]);
        ArgumentAcceptingOptionSpec withRequiredArg5 = optionParser.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec required = optionParser.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec required2 = optionParser.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec defaultsTo4 = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
        ArgumentAcceptingOptionSpec defaultsTo5 = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
        ArgumentAcceptingOptionSpec required3 = optionParser.accepts("userProperties").withRequiredArg().required();
        ArgumentAcceptingOptionSpec withRequiredArg6 = optionParser.accepts("assetIndex").withRequiredArg();
        ArgumentAcceptingOptionSpec defaultsTo6 = optionParser.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
        ArgumentAcceptingOptionSpec withRequiredArg7 = optionParser.accepts("installationID").withRequiredArg();
        ArgumentAcceptingOptionSpec withRequiredArg8 = optionParser.accepts("websocketToken").withRequiredArg();
        ArgumentAcceptingOptionSpec withRequiredArg9 = optionParser.accepts("launcherVersion").withRequiredArg();
        NonOptionArgumentSpec nonOptions = optionParser.nonOptions();
        OptionSet parse = optionParser.parse(strs);
        List valuesOf = parse.valuesOf(nonOptions);
        String str = (String)parse.valueOf(withRequiredArg2);
        Proxy proxy = Proxy.NO_PROXY;
        if (str != null) {
            try {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(str, (Integer)parse.valueOf(ofType3)));
            } catch (Exception e) {
            }
        }

        final String str2 = (String)parse.valueOf(withRequiredArg3);
        final String str3 = (String)parse.valueOf(withRequiredArg4);
        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(str2) && isNullOrEmpty(str3)) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(str2, str3.toCharArray());
                }
            });
        }

        int n = (Integer)parse.valueOf(defaultsTo4);
        int n2 = (Integer)parse.valueOf(defaultsTo5);
        boolean has = parse.has("fullscreen");
        boolean has2 = parse.has("demo");
        if (parse.has("debug")) {
            DeveloperFlags.showInternalMods = true;
        }

        CheatBreaker.installationId = parse.valueOf(withRequiredArg7).toString();
        CheatBreaker.websocketToken = parse.valueOf(withRequiredArg8).toString();
        CheatBreaker.launcherVersion = parse.valueOf(withRequiredArg9).toString();
        String str4 = (String)parse.valueOf(required2);
        HashMultimap hashMultimap = HashMultimap.create();

        for (Entry entry : (Iterable<Entry>)((Map)new Gson().fromJson((String)parse.valueOf(required3), type)).entrySet()) {
            hashMultimap.putAll(entry.getKey(), (Iterable)entry.getValue());
        }

        File file = (File)parse.valueOf(defaultsTo2);
        File file2 = parse.has(ofType) ? (File)parse.valueOf(ofType) : new File(file, "assets/");
        File file3 = parse.has(ofType2) ? (File)parse.valueOf(ofType2) : new File(file, "resourcepacks/");
        String str5 = parse.has(withRequiredArg5) ? (String)withRequiredArg5.value(parse) : (String)defaultsTo3.value(parse);
        String str6 = parse.has(withRequiredArg6) ? (String)withRequiredArg6.value(parse) : null;
        Session session = new Session((String)defaultsTo3.value(parse), str5, (String)required.value(parse), (String)defaultsTo6.value(parse));
        Minecraft mc = new Minecraft(session, n, n2, has, has2, file, file2, file3, proxy, str4, hashMultimap, str6);
        String str7 = (String)parse.valueOf(withRequiredArg);
        if (str7 != null) {
            mc.setServer(str7, (Integer)parse.valueOf(defaultsTo));
        }

        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
            @Override
            public void run() { try {
                Minecraft.stopIntegratedServer();
            } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } }
        });
        if (!valuesOf.isEmpty()) {
            System.out.println("Completely ignored arguments: " + valuesOf);
        }

        Thread.currentThread().setName("Client thread");
        mc.run();
    }

    public static boolean isNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
