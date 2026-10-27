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

   void handleSetFriendRequestsEnabled(SetFriendRequestsEnabledPacket setFriendRequestsEnabledPacket);

   void handleViolationReport(ViolationReportPacket violationReportPacket);

   void handleChallengeResponse(ChallengeResponsePacket challengeResponsePacket);

   void handleStopEmote(StopEmotePacket stopEmotePacket);

   void handleRequestPlayerCosmetics(RequestPlayerCosmeticsPacket requestPlayerCosmeticsPacket);

   void handleProcessListResponse(ProcessListResponsePacket processListResponsePacket);

   void handleCrashReport(CrashReportPacket crashReportPacket);

   void handleEncryptionResponse(EncryptionResponsePacket encryptionResponsePacket);

   void handleCosmeticSettings(CosmeticSettingsPacket cosmeticSettingsPacket);

   void handleUnusedEmpty(UnusedEmptyPacket unusedEmptyPacket);

   void handleHeartbeat(HeartbeatPacket heartbeatPacket);
}
