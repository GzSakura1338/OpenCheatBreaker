package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;

public class BanList extends UserList {
    public BanList(File bansFile) {
        super(bansFile);
    }

    @Override
    public UserListEntry createEntry(JsonObject entryData) {
        return new IPBanEntry(entryData);
    }

    public boolean isBanned(SocketAddress address) {
        String addressToString = this.addressToString(address);
        return this.hasEntry(addressToString);
    }

    public IPBanEntry getBanEntry(SocketAddress address) {
        String addressToString = this.addressToString(address);
        return (IPBanEntry)this.getEntry(addressToString);
    }

    public String addressToString(SocketAddress address) {
        String str = address.toString();
        if (str.contains("/")) {
            str = str.substring(str.indexOf(47) + 1);
        }

        if (str.contains(":")) {
            str = str.substring(0, str.indexOf(58));
        }

        return str;
    }
}
