package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BanEntry<T> extends UserListEntry<T> {
   public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   public Date banStartDate;
   public String bannedBy;
   public Date banEndDate;
   public String reason;

   public BanEntry(T valueIn, Date startDate, String banner, Date endDate, String banReason) {
      super((T)valueIn);
      this.banStartDate = startDate == null ? new Date() : startDate;
      this.bannedBy = banner == null ? "(Unknown)" : banner;
      this.banEndDate = endDate;
      this.reason = banReason == null ? "Banned by an operator." : banReason;
   }

   public BanEntry(T valueIn, JsonObject json) {
      super((T)valueIn, json);

      Date date;
      try {
         date = json.has("created") ? dateFormat.parse(json.get("created").getAsString()) : new Date();
      } catch (ParseException e) {
         date = new Date();
      }

      this.banStartDate = date;
      this.bannedBy = json.has("source") ? json.get("source").getAsString() : "(Unknown)";

      Date date2;
      try {
         date2 = json.has("expires") ? dateFormat.parse(json.get("expires").getAsString()) : null;
      } catch (ParseException e) {
         date2 = null;
      }

      this.banEndDate = date2;
      this.reason = json.has("reason") ? json.get("reason").getAsString() : "Banned by an operator.";
   }

   public Date getBanEndDate() {
      return this.banEndDate;
   }

   public String getBanReason() {
      return this.reason;
   }

   @Override
   public boolean hasBanExpired() {
      return this.banEndDate == null ? false : this.banEndDate.before(new Date());
   }

   @Override
   public void onSerialization(JsonObject data) {
      data.addProperty("created", dateFormat.format(this.banStartDate));
      data.addProperty("source", this.bannedBy);
      data.addProperty("expires", this.banEndDate == null ? "forever" : dateFormat.format(this.banEndDate));
      data.addProperty("reason", this.reason);
   }
}
