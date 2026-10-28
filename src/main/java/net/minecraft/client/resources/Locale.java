package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Locale {
    public static Splitter splitter = Splitter.on('=').limit(2);
    public static Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    public Map translations = Maps.newHashMap();
    public boolean rightToLeft;

    public synchronized void loadLocaleDataFiles(IResourceManager resourceManager, List list) {
        this.translations.clear();

        for (String str : (Iterable<String>)list) {
            String format = String.format("lang/%s.lang", str);

            for (String str2 : (Iterable<String>)resourceManager.getResourceDomains()) {
                try {
                    this.loadLocaleData(resourceManager.getAllResources(new ResourceLocation(str2, format)));
                } catch (IOException e) {
                }
            }
        }

        this.checkUnicode();
    }

    public boolean isUnicode() {
        return this.rightToLeft;
    }

    public void checkUnicode() {
        this.rightToLeft = false;
        int n = 0;
        int n2 = 0;

        for (String str : (Iterable<String>)this.translations.values()) {
            int n3 = str.length();
            n2 += n3;

            for (int i = 0; i < n3; i++) {
                if (str.charAt(i) >= 256) {
                    n++;
                }
            }
        }

        float f = (float)n / n2;
        this.rightToLeft = f > 0.1;
    }

    public void loadLocaleData(List list) throws IOException {
        for (IResource resource : (Iterable<IResource>)list) {
            this.loadLocaleData(resource.getInputStream());
        }
    }

    public void loadLocaleData(InputStream inputStream) throws IOException {
        for (String str : IOUtils.readLines(inputStream, Charsets.UTF_8)) {
            if (!str.isEmpty() && str.charAt(0) != '#') {
                String[] toArray = (String[])Iterables.toArray(splitter.split(str), String.class);
                if (toArray != null && toArray.length == 2) {
                    String str2 = toArray[0];
                    String replaceAll = TOKEN_PATTERN.matcher(toArray[1]).replaceAll("%$1s");
                    this.translations.put(str2, replaceAll);
                }
            }
        }
    }

    public String translateKeyPrivate(String str) {
        String str2 = (String)this.translations.get(str);
        return str2 == null ? str : str2;
    }

    public String formatMessage(String str, Object[] objs) {
        String translateKeyPrivate = this.translateKeyPrivate(str);

        try {
            return String.format(translateKeyPrivate, objs);
        } catch (IllegalFormatException e) {
            return "Format error: " + translateKeyPrivate;
        }
    }
}
