package shadersmod.client;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.optifine.Config;
import net.optifine.StrUtils;

public class ShaderOptionVariable extends ShaderOption {
    public static Pattern PATTERN_VARIABLE = Pattern.compile("^\\s*#define\\s+(\\w+)\\s+(-?[0-9\\.Ff]+|\\w+)\\s*(//.*)?$");

    public ShaderOptionVariable(String str, String str2, String str3, String[] strs, String str4) {
        super(str, str2, str3, strs, str3, str4);
        this.setVisible(this.getValues().length > 1);
    }

    @Override
    public String getSourceLine() {
        return "#define " + this.getName() + " " + this.getValue() + " // Shader option " + this.getValue();
    }

    @Override
    public String getValueText(String str) {
        String translate = Shaders.translate("prefix." + this.getName(), "");
        String valueText = super.getValueText(str);
        String translate2 = Shaders.translate("suffix." + this.getName(), "");
        return translate + valueText + translate2;
    }

    @Override
    public String getValueColor(String str) {
        String toLowerCase = str.toLowerCase();
        return !toLowerCase.equals("false") && !toLowerCase.equals("off") ? "§a" : "§c";
    }

    @Override
    public boolean matchesLine(String str) {
        Matcher matcher = PATTERN_VARIABLE.matcher(str);
        if (!matcher.matches()) {
            return false;
        }

        String group = matcher.group(1);
        return group.matches(this.getName());
    }

    public static ShaderOption parseOption(String str, String str2) {
        Matcher matcher = PATTERN_VARIABLE.matcher(str);
        if (!matcher.matches()) {
            return null;
        }

        String group = matcher.group(1);
        String group2 = matcher.group(2);
        String group3 = matcher.group(3);
        String segment = StrUtils.getSegment(group3, "[", "]");
        if (segment != null && segment.length() > 0) {
            group3 = group3.replace(segment, "").trim();
        }

        String[] parseValues2 = parseValues(group2, segment);
        if (group != null && group.length() > 0) {
            str2 = StrUtils.removePrefix(str2, "/shaders/");
            return new ShaderOptionVariable(group, group3, group2, parseValues2, str2);
        } else {
            return null;
        }
    }

    public static String[] parseValues(String str, String str2) {
        String[] strs = new String[]{str};
        if (str2 == null) {
            return strs;
        }

        str2 = str2.trim();
        str2 = StrUtils.removePrefix(str2, "[");
        str2 = StrUtils.removeSuffix(str2, "]");
        str2 = str2.trim();
        if (str2.length() <= 0) {
            return strs;
        }

        String[] tokenize = Config.tokenize(str2, " ");
        if (tokenize.length <= 0) {
            return strs;
        }

        if (!Arrays.asList(tokenize).contains(str)) {
            tokenize = (String[])Config.addObjectToArray(tokenize, str, 0);
        }

        return tokenize;
    }
}
