package com.cheatbreaker.client.network;

import com.cheatbreaker.client.network.packet.BlockedServerPacket;
import com.cheatbreaker.client.network.packet.BooleanStringPacket;
import com.cheatbreaker.client.network.packet.ChallengeRequestPacket;
import com.cheatbreaker.client.network.packet.ConnectionAcceptedPacket;
import com.cheatbreaker.client.network.packet.EncryptionRequestPacket;
import com.cheatbreaker.client.network.packet.FriendListPacket;
import com.cheatbreaker.client.network.packet.FriendRequestsPacket;
import com.cheatbreaker.client.network.packet.IntegerListPacket;
import com.cheatbreaker.client.network.packet.KickPacket;
import com.cheatbreaker.client.network.packet.NotificationPacket;
import com.cheatbreaker.client.network.packet.OutgoingFriendRequestPacket;
import com.cheatbreaker.client.network.packet.OwnedEmotesPacket;
import com.cheatbreaker.client.network.packet.PlayEmotePacket;
import com.cheatbreaker.client.network.packet.PlayerCosmeticsPacket;
import com.cheatbreaker.client.network.packet.PlayerStopEmotePacket;
import com.cheatbreaker.client.network.packet.ProcessListRequestPacket;
import com.cheatbreaker.client.network.packet.RemoveBlockedServerPacket;
import com.cheatbreaker.client.network.packet.ServerChatMessagePacket;
import com.cheatbreaker.client.network.packet.ShutdownPacket;
import com.cheatbreaker.client.network.packet.SyncProfilesPacket;

public interface WebsocketPacketHandler extends PacketHandler {
    void handleFriendRequests(FriendRequestsPacket friendRequestsPacket);

    void handlePlayerStopEmote(PlayerStopEmotePacket playerStopEmotePacket);

    void handleShutdown(ShutdownPacket shutdownPacket);

    void handleKick(KickPacket kickPacket);

    void handleBlockedServer(BlockedServerPacket blockedServerPacket);

    void handleIntegerList(IntegerListPacket integerListPacket);

    void handleBooleanString(BooleanStringPacket booleanStringPacket);

    void handleConnectionAccepted(ConnectionAcceptedPacket connectionAcceptedPacket);

    void handlePlayEmote(PlayEmotePacket playEmotePacket);

    void handleFriendList(FriendListPacket friendListPacket);

    void handleOutgoingFriendRequest(OutgoingFriendRequestPacket outgoingFriendRequestPacket);

    void handleEncryptionRequest(EncryptionRequestPacket encryptionRequestPacket);

    void handleServerChatMessage(ServerChatMessagePacket serverChatMessagePacket);

    void handleNotification(NotificationPacket notificationPacket);

    void handlePlayerCosmetics(PlayerCosmeticsPacket playerCosmeticsPacket);

    void handleOwnedEmotes(OwnedEmotesPacket ownedEmotesPacket);

    void handleSyncProfiles(SyncProfilesPacket syncProfilesPacket);

    void handleRemoveBlockedServer(RemoveBlockedServerPacket removeBlockedServerPacket);

    void handleProcessListRequest(ProcessListRequestPacket processListRequestPacket);

    void handleChallengeRequest(ChallengeRequestPacket challengeRequestPacket);
}
