package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
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

public interface INetHandlerPlayServer extends INetHandler {
    void processAnimation(C0APacketAnimation c0APacketAnimation);

    void processChatMessage(C01PacketChatMessage c01PacketChatMessage);

    void processTabComplete(C14PacketTabComplete c14PacketTabComplete);

    void processClientStatus(C16PacketClientStatus c16PacketClientStatus);

    void processClientSettings(C15PacketClientSettings c15PacketClientSettings);

    void processConfirmTransaction(C0FPacketConfirmTransaction c0FPacketConfirmTransaction);

    void processEnchantItem(C11PacketEnchantItem c11PacketEnchantItem);

    void processClickWindow(C0EPacketClickWindow c0EPacketClickWindow);

    void processCloseWindow(C0DPacketCloseWindow c0DPacketCloseWindow);

    void processVanilla250Packet(C17PacketCustomPayload c17PacketCustomPayload);

    void processUseEntity(C02PacketUseEntity c02PacketUseEntity) throws java.io.IOException;

    void processKeepAlive(C00PacketKeepAlive c00PacketKeepAlive);

    void processPlayer(C03PacketPlayer c03PacketPlayer) throws java.io.IOException;

    void processPlayerAbilities(C13PacketPlayerAbilities c13PacketPlayerAbilities);

    void processPlayerDigging(C07PacketPlayerDigging c07PacketPlayerDigging);

    void processEntityAction(C0BPacketEntityAction c0BPacketEntityAction) throws java.io.IOException;

    void processInput(C0CPacketInput c0CPacketInput);

    void processHeldItemChange(C09PacketHeldItemChange c09PacketHeldItemChange);

    void processCreativeInventoryAction(C10PacketCreativeInventoryAction c10PacketCreativeInventoryAction);

    void processUpdateSign(C12PacketUpdateSign c12PacketUpdateSign);

    void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement) throws java.io.IOException;
}
