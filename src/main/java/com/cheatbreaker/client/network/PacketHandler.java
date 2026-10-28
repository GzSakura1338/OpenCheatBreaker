package com.cheatbreaker.client.network;

import com.cheatbreaker.client.network.packet.FriendMessagePacket;
import com.cheatbreaker.client.network.packet.FriendRemovedPacket;
import com.cheatbreaker.client.network.packet.FriendRequestReceivedPacket;
import com.cheatbreaker.client.network.packet.FriendRequestResponsePacket;
import com.cheatbreaker.client.network.packet.FriendServerPacket;
import com.cheatbreaker.client.network.packet.FriendStatusPacket;
import com.cheatbreaker.client.network.packet.MessagePacket;
import com.cheatbreaker.client.network.packet.PlayEmoteRequestPacket;
import com.cheatbreaker.client.network.packet.RecentCosmeticsPacket;
import com.cheatbreaker.client.network.packet.StringTriplePacket;
import com.cheatbreaker.client.network.packet.ToggleEntryPacket;

public interface PacketHandler {
    void handleStringTriple(StringTriplePacket stringTriplePacket);

    void handleRecentCosmetics(RecentCosmeticsPacket recentCosmeticsPacket);

    void handleFriendServer(FriendServerPacket friendServerPacket);

    void handleFriendRequestReceived(FriendRequestReceivedPacket friendRequestReceivedPacket);

    void handleFriendStatus(FriendStatusPacket friendStatusPacket);

    void handleMessage(MessagePacket messagePacket);

    void handlePlayEmoteRequest(PlayEmoteRequestPacket playEmoteRequestPacket);

    void handleFriendRemoved(FriendRemovedPacket friendRemovedPacket);

    void handleFriendMessage(FriendMessagePacket friendMessagePacket);

    void handleFriendRequestResponse(FriendRequestResponsePacket friendRequestResponsePacket);

    void handleToggleEntry(ToggleEntryPacket toggleEntryPacket);
}
