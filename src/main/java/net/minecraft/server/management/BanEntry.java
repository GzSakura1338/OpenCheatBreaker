package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BanEntry extends UserListEntry {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    public Date banStartDate;
    public String bannedBy;
    public Date banEndDate;
    public String reason;

    public BanEntry(Object obj, Date date, String str, Date date2, String str2) {
        super(obj);
        this.banStartDate = date == null ? new Date() : date;
        this.bannedBy = str == null ? "(Unknown)" : str;
        this.banEndDate = date2;
        this.reason = str2 == null ? "Banned by an operator." : str2;
    }

    public BanEntry(Object obj, JsonObject jsonObject) {
        super(obj, jsonObject);

        Date date;
        try {
            date = jsonObject.has("created") ? dateFormat.parse(jsonObject.get("created").getAsString()) : new Date();
        } catch (ParseException e) {
            date = new Date();
        }

        this.banStartDate = date;
        this.bannedBy = jsonObject.has("source") ? jsonObject.get("source").getAsString() : "(Unknown)";

        Date date2;
        try {
            date2 = jsonObject.has("expires") ? dateFormat.parse(jsonObject.get("expires").getAsString()) : null;
        } catch (ParseException e) {
            date2 = null;
        }

        this.banEndDate = date2;
        this.reason = jsonObject.has("reason") ? jsonObject.get("reason").getAsString() : "Banned by an operator.";
    }

    public Date getBanEndDate() {
        return this.banEndDate;
    }

    public String getBanReason() {
        return this.reason;
    }

    @Override
    public boolean hasBanExpired() {
        return this.banEndDate != null && this.banEndDate.before(new Date());
    }

    @Override
    public void onSerialization(JsonObject data) {
        data.addProperty("created", dateFormat.format(this.banStartDate));
        data.addProperty("source", this.bannedBy);
        data.addProperty("expires", this.banEndDate == null ? "forever" : dateFormat.format(this.banEndDate));
        data.addProperty("reason", this.reason);
    }
}
