package com.cheatbreaker.client.network;

import com.cheatbreaker.client.network.packet.ChallengeResponsePacket;
import com.cheatbreaker.client.network.packet.CosmeticSettingsPacket;
import com.cheatbreaker.client.network.packet.CrashReportPacket;
import com.cheatbreaker.client.network.packet.EmoteWheelPacket;
import com.cheatbreaker.client.network.packet.EncryptionResponsePacket;
import com.cheatbreaker.client.network.packet.HeartbeatPacket;
import com.cheatbreaker.client.network.packet.ProcessListResponsePacket;
import com.cheatbreaker.client.network.packet.RequestPlayerCosmeticsPacket;
import com.cheatbreaker.client.network.packet.SetFriendRequestsEnabledPacket;
import com.cheatbreaker.client.network.packet.StopEmotePacket;
import com.cheatbreaker.client.network.packet.UnusedEmptyPacket;
import com.cheatbreaker.client.network.packet.ViolationReportPacket;

public interface ServerPacketHandler extends PacketHandler {
    void handleEmoteWheel(EmoteWheelPacket emoteWheelPacket);

    void handleStopEmote(StopEmotePacket stopEmotePacket);

    void handleSetFriendRequestsEnabled(SetFriendRequestsEnabledPacket setFriendRequestsEnabledPacket);

    void handleProcessListResponse(ProcessListResponsePacket processListResponsePacket);

    void handleEncryptionResponse(EncryptionResponsePacket encryptionResponsePacket);

    void handleChallengeResponse(ChallengeResponsePacket challengeResponsePacket);

    void handleUnusedEmpty(UnusedEmptyPacket unusedEmptyPacket);

    void handleViolationReport(ViolationReportPacket violationReportPacket);

    void handleHeartbeat(HeartbeatPacket heartbeatPacket);

    void handleCosmeticSettings(CosmeticSettingsPacket cosmeticSettingsPacket);

    void handleRequestPlayerCosmetics(RequestPlayerCosmeticsPacket requestPlayerCosmeticsPacket);

    void handleCrashReport(CrashReportPacket crashReportPacket);
}
