package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.util.Date;

public class IPBanEntry extends BanEntry {
    public IPBanEntry(String str) {
        this(str, null, null, null, null);
    }

    public IPBanEntry(String str, Date date, String str2, Date date2, String str3) {
        super(str, date, str2, date2, str3);
    }

    public IPBanEntry(JsonObject jsonObject) {
        super(getIpFromJson(jsonObject), jsonObject);
    }

    public static String getIpFromJson(JsonObject jsonObject) {
        return jsonObject.has("ip") ? jsonObject.get("ip").getAsString() : null;
    }

    @Override
    public void onSerialization(JsonObject data) {
        if (this.getValue() != null) {
            data.addProperty("ip", (String)this.getValue());
            super.onSerialization(data);
        }
    }
}
