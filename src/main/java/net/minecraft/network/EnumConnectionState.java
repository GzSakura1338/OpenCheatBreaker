package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.Map;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S14PacketEntity.S15PacketEntityRelMove;
import net.minecraft.network.play.server.S14PacketEntity.S16PacketEntityLook;
import net.minecraft.network.play.server.S14PacketEntity.S17PacketEntityLookMove;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import org.apache.logging.log4j.LogManager;

public enum EnumConnectionState {
    HANDSHAKING("HANDSHAKING", 0, -1, null) {
        {
            this.findEnumConnectionState(0, C00Handshake.class);
        }
    },
    PLAY("PLAY", 1, 0, null) {
        {
            this.findEnumConnectionState2(0, S00PacketKeepAlive.class);
            this.findEnumConnectionState2(1, S01PacketJoinGame.class);
            this.findEnumConnectionState2(2, S02PacketChat.class);
            this.findEnumConnectionState2(3, S03PacketTimeUpdate.class);
            this.findEnumConnectionState2(4, S04PacketEntityEquipment.class);
            this.findEnumConnectionState2(5, S05PacketSpawnPosition.class);
            this.findEnumConnectionState2(6, S06PacketUpdateHealth.class);
            this.findEnumConnectionState2(7, S07PacketRespawn.class);
            this.findEnumConnectionState2(8, S08PacketPlayerPosLook.class);
            this.findEnumConnectionState2(9, S09PacketHeldItemChange.class);
            this.findEnumConnectionState2(10, S0APacketUseBed.class);
            this.findEnumConnectionState2(11, S0BPacketAnimation.class);
            this.findEnumConnectionState2(12, S0CPacketSpawnPlayer.class);
            this.findEnumConnectionState2(13, S0DPacketCollectItem.class);
            this.findEnumConnectionState2(14, S0EPacketSpawnObject.class);
            this.findEnumConnectionState2(15, S0FPacketSpawnMob.class);
            this.findEnumConnectionState2(16, S10PacketSpawnPainting.class);
            this.findEnumConnectionState2(17, S11PacketSpawnExperienceOrb.class);
            this.findEnumConnectionState2(18, S12PacketEntityVelocity.class);
            this.findEnumConnectionState2(19, S13PacketDestroyEntities.class);
            this.findEnumConnectionState2(20, S14PacketEntity.class);
            this.findEnumConnectionState2(21, S15PacketEntityRelMove.class);
            this.findEnumConnectionState2(22, S16PacketEntityLook.class);
            this.findEnumConnectionState2(23, S17PacketEntityLookMove.class);
            this.findEnumConnectionState2(24, S18PacketEntityTeleport.class);
            this.findEnumConnectionState2(25, S19PacketEntityHeadLook.class);
            this.findEnumConnectionState2(26, S19PacketEntityStatus.class);
            this.findEnumConnectionState2(27, S1BPacketEntityAttach.class);
            this.findEnumConnectionState2(28, S1CPacketEntityMetadata.class);
            this.findEnumConnectionState2(29, S1DPacketEntityEffect.class);
            this.findEnumConnectionState2(30, S1EPacketRemoveEntityEffect.class);
            this.findEnumConnectionState2(31, S1FPacketSetExperience.class);
            this.findEnumConnectionState2(32, S20PacketEntityProperties.class);
            this.findEnumConnectionState2(33, S21PacketChunkData.class);
            this.findEnumConnectionState2(34, S22PacketMultiBlockChange.class);
            this.findEnumConnectionState2(35, S23PacketBlockChange.class);
            this.findEnumConnectionState2(36, S24PacketBlockAction.class);
            this.findEnumConnectionState2(37, S25PacketBlockBreakAnim.class);
            this.findEnumConnectionState2(38, S26PacketMapChunkBulk.class);
            this.findEnumConnectionState2(39, S27PacketExplosion.class);
            this.findEnumConnectionState2(40, S28PacketEffect.class);
            this.findEnumConnectionState2(41, S29PacketSoundEffect.class);
            this.findEnumConnectionState2(42, S2APacketParticles.class);
            this.findEnumConnectionState2(43, S2BPacketChangeGameState.class);
            this.findEnumConnectionState2(44, S2CPacketSpawnGlobalEntity.class);
            this.findEnumConnectionState2(45, S2DPacketOpenWindow.class);
            this.findEnumConnectionState2(46, S2EPacketCloseWindow.class);
            this.findEnumConnectionState2(47, S2FPacketSetSlot.class);
            this.findEnumConnectionState2(48, S30PacketWindowItems.class);
            this.findEnumConnectionState2(49, S31PacketWindowProperty.class);
            this.findEnumConnectionState2(50, S32PacketConfirmTransaction.class);
            this.findEnumConnectionState2(51, S33PacketUpdateSign.class);
            this.findEnumConnectionState2(52, S34PacketMaps.class);
            this.findEnumConnectionState2(53, S35PacketUpdateTileEntity.class);
            this.findEnumConnectionState2(54, S36PacketSignEditorOpen.class);
            this.findEnumConnectionState2(55, S37PacketStatistics.class);
            this.findEnumConnectionState2(56, S38PacketPlayerListItem.class);
            this.findEnumConnectionState2(57, S39PacketPlayerAbilities.class);
            this.findEnumConnectionState2(58, S3APacketTabComplete.class);
            this.findEnumConnectionState2(59, S3BPacketScoreboardObjective.class);
            this.findEnumConnectionState2(60, S3CPacketUpdateScore.class);
            this.findEnumConnectionState2(61, S3DPacketDisplayScoreboard.class);
            this.findEnumConnectionState2(62, S3EPacketTeams.class);
            this.findEnumConnectionState2(63, S3FPacketCustomPayload.class);
            this.findEnumConnectionState2(64, S40PacketDisconnect.class);
            this.findEnumConnectionState(0, C00PacketKeepAlive.class);
            this.findEnumConnectionState(1, C01PacketChatMessage.class);
            this.findEnumConnectionState(2, C02PacketUseEntity.class);
            this.findEnumConnectionState(3, C03PacketPlayer.class);
            this.findEnumConnectionState(4, C04PacketPlayerPosition.class);
            this.findEnumConnectionState(5, C05PacketPlayerLook.class);
            this.findEnumConnectionState(6, C06PacketPlayerPosLook.class);
            this.findEnumConnectionState(7, C07PacketPlayerDigging.class);
            this.findEnumConnectionState(8, C08PacketPlayerBlockPlacement.class);
            this.findEnumConnectionState(9, C09PacketHeldItemChange.class);
            this.findEnumConnectionState(10, C0APacketAnimation.class);
            this.findEnumConnectionState(11, C0BPacketEntityAction.class);
            this.findEnumConnectionState(12, C0CPacketInput.class);
            this.findEnumConnectionState(13, C0DPacketCloseWindow.class);
            this.findEnumConnectionState(14, C0EPacketClickWindow.class);
            this.findEnumConnectionState(15, C0FPacketConfirmTransaction.class);
            this.findEnumConnectionState(16, C10PacketCreativeInventoryAction.class);
            this.findEnumConnectionState(17, C11PacketEnchantItem.class);
            this.findEnumConnectionState(18, C12PacketUpdateSign.class);
            this.findEnumConnectionState(19, C13PacketPlayerAbilities.class);
            this.findEnumConnectionState(20, C14PacketTabComplete.class);
            this.findEnumConnectionState(21, C15PacketClientSettings.class);
            this.findEnumConnectionState(22, C16PacketClientStatus.class);
            this.findEnumConnectionState(23, C17PacketCustomPayload.class);
        }
    },
    STATUS("STATUS", 2, 1, null) {
        {
            this.findEnumConnectionState(0, C00PacketServerQuery.class);
            this.findEnumConnectionState2(0, S00PacketServerInfo.class);
            this.findEnumConnectionState(1, C01PacketPing.class);
            this.findEnumConnectionState2(1, S01PacketPong.class);
        }
    },
    LOGIN("LOGIN", 3, 2, null) {
        {
            this.findEnumConnectionState2(0, S00PacketDisconnect.class);
            this.findEnumConnectionState2(1, S01PacketEncryptionRequest.class);
            this.findEnumConnectionState2(2, S02PacketLoginSuccess.class);
            this.findEnumConnectionState(0, C00PacketLoginStart.class);
            this.findEnumConnectionState(1, C01PacketEncryptionResponse.class);
        }
    };

    public static TIntObjectMap STATES_BY_ID = new TIntObjectHashMap();
    public BiMap CLIENT_TO_SERVER = HashBiMap.create();
    public BiMap SERVER_TO_CLIENT = HashBiMap.create();
    public int id;
    public static Map STATES_BY_CLASS = Maps.newHashMap();

    EnumConnectionState(int protocolId) {
        this.id = protocolId;
    }

    public BiMap getClientToServer() {
        return this.CLIENT_TO_SERVER;
    }

    public BiMap getServerToClient() {
        return this.SERVER_TO_CLIENT;
    }

    public BiMap getReceivablePackets(boolean flag) {
        return flag ? this.getServerToClient() : this.getClientToServer();
    }

    public BiMap getSendablePackets(boolean flag) {
        return flag ? this.getClientToServer() : this.getServerToClient();
    }

    public int getId() {
        return this.id;
    }

    public static EnumConnectionState getById(int stateId) {
        return (EnumConnectionState)STATES_BY_ID.get(stateId);
    }

    public static EnumConnectionState getFromPacket(Packet packetIn) {
        return (EnumConnectionState)STATES_BY_CLASS.get(packetIn.getClass());
    }

    static {
        for (EnumConnectionState enumConnectionState : values()) {
            STATES_BY_ID.put(enumConnectionState.getId(), enumConnectionState);

            for (Class clazz : (Iterable<Class>)Iterables.concat(enumConnectionState.getServerToClient().values(), enumConnectionState.getClientToServer().values())) {
                if (STATES_BY_CLASS.containsKey(clazz) && STATES_BY_CLASS.get(clazz) != enumConnectionState) {
                    throw new Error("Packet " + clazz + " is already assigned to protocol " + STATES_BY_CLASS.get(clazz) + " - can't reassign to " + enumConnectionState);
                }

                STATES_BY_CLASS.put(clazz, enumConnectionState);
            }
        }
    }

    EnumConnectionState(String str, int n, int n2, Object obj) {
        this(n2);
    }

    public EnumConnectionState findEnumConnectionState(int n, Class clazz) {
        if (this.CLIENT_TO_SERVER.containsKey(n)) {
            String str = "Serverbound packet ID " + n + " is already assigned to " + this.CLIENT_TO_SERVER.get(n) + "; cannot re-assign to " + clazz;
            LogManager.getLogger().fatal(str);
            throw new IllegalArgumentException(str);
        } else if (this.CLIENT_TO_SERVER.containsValue(clazz)) {
            String str = "Serverbound packet "
                + clazz
                + " is already assigned to ID "
                + this.CLIENT_TO_SERVER.inverse().get(clazz)
                + "; cannot re-assign to "
                + n;
            LogManager.getLogger().fatal(str);
            throw new IllegalArgumentException(str);
        } else {
            this.CLIENT_TO_SERVER.put(n, clazz);
            return this;
        }
    }

    public EnumConnectionState findEnumConnectionState2(int n, Class clazz) {
        if (this.SERVER_TO_CLIENT.containsKey(n)) {
            String str = "Clientbound packet ID " + n + " is already assigned to " + this.SERVER_TO_CLIENT.get(n) + "; cannot re-assign to " + clazz;
            LogManager.getLogger().fatal(str);
            throw new IllegalArgumentException(str);
        } else if (this.SERVER_TO_CLIENT.containsValue(clazz)) {
            String str = "Clientbound packet "
                + clazz
                + " is already assigned to ID "
                + this.SERVER_TO_CLIENT.inverse().get(clazz)
                + "; cannot re-assign to "
                + n;
            LogManager.getLogger().fatal(str);
            throw new IllegalArgumentException(str);
        } else {
            this.SERVER_TO_CLIENT.put(n, clazz);
            return this;
        }
    }
}
