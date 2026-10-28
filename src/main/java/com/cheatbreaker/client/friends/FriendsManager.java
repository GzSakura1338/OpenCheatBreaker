package com.cheatbreaker.client.friends;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.StringBooleanIntEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public class FriendsManager {
    public Object2ObjectOpenHashMap<String, Friend> friends = new Object2ObjectOpenHashMap();
    public Object2ObjectOpenHashMap<String, FriendRequest> friendRequests = new Object2ObjectOpenHashMap();
    public Object2ObjectOpenHashMap<String, List<String>> messageHistory = new Object2ObjectOpenHashMap();
    public Object2ObjectOpenHashMap<String, List<String>> unreadMessages = new Object2ObjectOpenHashMap();
    public Object2ObjectOpenHashMap<String, List<String>> logEntries = new Object2ObjectOpenHashMap();
    public DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public void markConversationRead(String str) {
        if (this.unreadMessages.containsKey(str)) {
            List list = (List)this.unreadMessages.get(str);
            if (!this.messageHistory.containsKey(str)) {
                this.messageHistory.put(str, new ObjectArrayList());
            }

            ((List)this.messageHistory.get(str)).addAll(list);
            this.unreadMessages.remove(str);
        }
    }

    public Object2ObjectOpenHashMap<String, List<String>> getUnreadMessages() {
        return this.unreadMessages;
    }

    public Object2ObjectOpenHashMap<String, List<String>> getLogEntries() {
        return this.logEntries;
    }

    public Object2ObjectOpenHashMap<String, List<String>> getMessageHistory() {
        return this.messageHistory;
    }

    public void addUnreadMessage(String str, String str2) {
        Friend friend = this.get(str);
        if (friend != null) {
            if (!this.unreadMessages.containsKey(str)) {
                this.unreadMessages.put(str, new ObjectArrayList());
            }

            String str3 = EnumChatFormatting.GRAY + LocalDateTime.now().format(this.dateTimeFormatter);
            String str4 = EnumChatFormatting.GREEN + friend.getName() + EnumChatFormatting.RESET;
            String str5 = str3 + " " + str4 + ": " + str2;
            ((List)this.unreadMessages.get(str)).add(str5);
        }
    }

    public FriendsManager() {
        CheatBreaker.getInstance().getEventBus().checkClass2(StringBooleanIntEvent.class, this::onStringBooleanIntEvent);
    }

    public Friend get(String str) {
        return (Friend)this.friends.get(str);
    }

    public void addLogEntry(String str, String str2) {
        Friend friend = this.get(str);
        if (friend != null) {
            if (!this.logEntries.containsKey(str)) {
                this.logEntries.put(str, new ObjectArrayList());
            }

            ((List)this.logEntries.get(str)).add(friend.getName() + ": " + str2);
            String str3 = EnumChatFormatting.GRAY + LocalDateTime.now().format(this.dateTimeFormatter);
            String str4 = EnumChatFormatting.AQUA + Minecraft.getMinecraft().getSession().getUsername() + EnumChatFormatting.RESET;
            String str5 = str3 + " " + str4 + ": " + str2;
            this.addHistoryMessage(str, str5);
        }
    }

    public void onStringBooleanIntEvent(StringBooleanIntEvent stringBooleanIntEvent) {
        this.friends.clear();
        this.friendRequests.clear();
    }

    public String getText() {
        switch (CheatBreaker.getInstance().getWebsocketController().friendOnlineStatus) {
            case AWAY:
                return "Away";
            case BUSY:
                return "Busy";
            case OFFLINE:
                return "Offline";
            default:
                return "Online";
        }
    }

    public void addHistoryMessage(String str, String str2) {
        Friend friend = this.get(str);
        if (friend != null) {
            if (!this.messageHistory.containsKey(str)) {
                this.messageHistory.put(str, new ObjectArrayList());
            }

            ((List)this.messageHistory.get(str)).add(str2);
        }
    }

    public Object2ObjectOpenHashMap<String, FriendRequest> getFriendRequests() {
        return this.friendRequests;
    }

    public Object2ObjectOpenHashMap<String, Friend> getFriends() {
        return this.friends;
    }
}
