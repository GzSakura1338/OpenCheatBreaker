package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.emote.AbstractEmote;
import com.cheatbreaker.client.emote.EmotePlayHelper;
import com.cheatbreaker.client.emote.HandsUpEmote;
import com.cheatbreaker.client.emote.LegacyDabEmote;
import com.cheatbreaker.client.emote.LegacyFacepalmEmote;
import com.cheatbreaker.client.emote.LegacyFlossEmote;
import com.cheatbreaker.client.emote.LegacyShrugEmote;
import com.cheatbreaker.client.emote.LegacyTPoseEmote;
import com.cheatbreaker.client.emote.LegacyWaveEmote;
import com.cheatbreaker.client.emote.NarutoRunEmote;
import com.cheatbreaker.client.emote.PlayerEmoteState;
import com.cheatbreaker.client.emote.ResourceEmote;
import com.cheatbreaker.client.emote.SuperFacepalmEmote;
import com.cheatbreaker.client.event.AbstractClientPlayerCustomModelPlayerFloatEvent;
import com.cheatbreaker.client.event.BooleanIntEvent;
import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.EventPhase;
import com.cheatbreaker.client.event.IntEvent4;
import com.cheatbreaker.client.event.SimpleEvent3;
import com.cheatbreaker.client.network.packet.PlayEmoteRequestPacket;
import com.cheatbreaker.client.network.packet.StopEmotePacket;
import com.cheatbreaker.client.ui.screen.PreviewPlayer;
import com.cheatbreaker.client.ui.screen.QuickEmoteWheelScreen;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class EmoteManager {
    public AbstractEmote abstractEmote;
    public Map<UUID, AbstractEmote> activeEmotes = new ConcurrentHashMap<UUID, AbstractEmote>();
    public static BiMap emoteClassIds = ((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)ImmutableBiMap.builder().put((Object)0, LegacyWaveEmote.class)).put((Object)1, HandsUpEmote.class)).put((Object)2, LegacyFlossEmote.class)).put((Object)3, LegacyDabEmote.class)).put((Object)4, LegacyTPoseEmote.class)).put((Object)5, LegacyShrugEmote.class)).put((Object)6, LegacyFacepalmEmote.class)).put((Object)58, NarutoRunEmote.class)).put((Object)61, SuperFacepalmEmote.class)).build();
    public boolean keepPerspective;
    public Map<UUID, ModelBiped> modelBackups = new ConcurrentHashMap<UUID, ModelBiped>();
    public List<Integer> ownedEmotes = new CopyOnWriteArrayList<Integer>();
    public boolean changedPerspective;
    public long pauseStart;
    public static BiMap<Integer, String> resourceEmoteIds = ((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)((ImmutableBiMap.Builder)ImmutableBiMap.builder().put((Object)7, "fresh")).put((Object)8, "hype")).put((Object)9, "squat_kick")).put((Object)10, "l_dance")).put((Object)11, "tidy")).put((Object)12, "free_flow")).put((Object)13, "shimmer")).put((Object)14, "get_funky")).put((Object)15, "gun_lean")).put((Object)16, "gangnam_style")).put((Object)17, "salute")).put((Object)18, "bitchslap")).put((Object)19, "bongo_cat")).put((Object)20, "breathtaking")).put((Object)21, "disgusted")).put((Object)22, "exhausted")).put((Object)23, "punch")).put((Object)24, "sneeze")).put((Object)25, "threatening")).put((Object)26, "woah")).put((Object)27, "boneless")).put((Object)28, "best_mates")).put((Object)29, "default")).put((Object)30, "disco_fever")).put((Object)31, "electro_shuffle")).put((Object)32, "floss")).put((Object)33, "infinite_dab")).put((Object)34, "orange_justice")).put((Object)35, "skibidi")).put((Object)36, "boy")).put((Object)37, "bow")).put((Object)38, "calculated")).put((Object)39, "chicken")).put((Object)40, "clapping")).put((Object)41, "club")).put((Object)42, "confused")).put((Object)43, "crying")).put((Object)44, "dab")).put((Object)45, "facepalm")).put((Object)46, "fist")).put((Object)47, "laughing")).put((Object)48, "no")).put((Object)49, "pointing")).put((Object)50, "popcorn")).put((Object)51, "pure_salt")).put((Object)52, "shrug")).put((Object)53, "t_pose")).put((Object)54, "thinking")).put((Object)55, "twerk")).put((Object)56, "wave")).put((Object)57, "yes")).put((Object)59, "rock_paper_scissors")).put((Object)60, "star_power")).put((Object)62, "villager_news_christmas")).build();

    public void resetModelRotations(ModelBiped modelBiped) {
        if (modelBiped == null) {
            return;
        }
        for (ModelRenderer modelRenderer : new ModelRenderer[]{modelBiped.bipedHead, modelBiped.bipedHeadwear, modelBiped.bipedBody, modelBiped.bipedLeftArm, modelBiped.bipedRightArm, modelBiped.bipedLeftLeg, modelBiped.bipedRightLeg, modelBiped.bipedCloak}) {
            if (modelRenderer == null) continue;
            modelRenderer.rotateAngleX = 0.0f;
            modelRenderer.rotateAngleY = 0.0f;
            modelRenderer.rotateAngleZ = 0.0f;
            modelRenderer.offsetX = 0.0f;
            modelRenderer.offsetY = 0.0f;
            modelRenderer.offsetZ = 0.0f;
        }
    }

    public void setChangedPerspective(boolean bl) {
        this.changedPerspective = bl;
    }

    public void tick() {
        long l;
        long l2 = System.currentTimeMillis();
        if (this.pauseStart != 0L && Minecraft.getMinecraft().isGamePaused() && (l = l2 - this.pauseStart) > 0L) {
            this.activeEmotes.values().forEach(abstractEmote -> abstractEmote.delayAnimations(l));
        }
        this.pauseStart = l2;
    }

    public boolean isChangedPerspective() {
        return this.changedPerspective;
    }

    public AbstractEmote createEmote(int n) {
        if (emoteClassIds.containsKey(n)) {
            try {
                return (AbstractEmote)((Class)emoteClassIds.get(n)).newInstance();
            }
            catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }
        if (resourceEmoteIds.containsKey(n)) {
            return new ResourceEmote((String)resourceEmoteIds.get(n));
        }
        return null;
    }

    public boolean isEmoting(Entity entity) {
        return !(entity instanceof PreviewPlayer) && this.activeEmotes.containsKey(entity.getUniqueID());
    }

    public void refreshLocalEmote() {
        AbstractEmote abstractEmote;
        EntityClientPlayerMP entityClientPlayerMP = Minecraft.getMinecraft().thePlayer;
        AbstractEmote abstractEmote2 = abstractEmote = entityClientPlayerMP == null ? null : this.activeEmotes.get(entityClientPlayerMP.getUniqueID());
        if (abstractEmote != null && !this.isOwned(abstractEmote)) {
            this.stopEmote(entityClientPlayerMP);
        }
    }

    public List<Integer> getOwnedEmotes() {
        return this.ownedEmotes;
    }

    public void onIntEvent4(IntEvent4 intEvent4) {
        if (Minecraft.getMinecraft().currentScreen != null || this.ownedEmotes.isEmpty()) {
            return;
        }
        ArrayList arrayList = CheatBreaker.getInstance().getKeybindSettings().emoteWheelQuickActionKeybinds.getListValue();
        ArrayList arrayList2 = CheatBreaker.getInstance().getKeybindSettings().emoteWheelQuickActions.getListValue();
        for (int i = 0; i < arrayList.size() && i < arrayList2.size(); ++i) {
            AbstractEmote abstractEmote;
            Integer n = (Integer)arrayList.get(i);
            if (n == null || n < 0 || n.intValue() != intEvent4.getIntValue()) continue;
            Integer n2 = (Integer)arrayList2.get(i);
            if (n2 == null || n2 < 0 || !this.ownedEmotes.contains(n2)) {
                return;
            }
            EntityClientPlayerMP entityClientPlayerMP = Minecraft.getMinecraft().thePlayer;
            AbstractEmote abstractEmote2 = abstractEmote = entityClientPlayerMP == null ? null : this.activeEmotes.get(entityClientPlayerMP.getUniqueID());
            if (abstractEmote != null && this.getEmoteId(abstractEmote) == n2.intValue()) {
                this.stopEmote(entityClientPlayerMP);
            } else {
                this.playLocalEmote(this.createEmote(n2));
            }
            return;
        }
    }

    public void setAbstractEmote(AbstractEmote abstractEmote) {
        this.abstractEmote = abstractEmote;
    }

    public int getEmoteId(AbstractEmote abstractEmote) {
        if (abstractEmote instanceof ResourceEmote) {
            return (Integer)resourceEmoteIds.inverse().get(((ResourceEmote)abstractEmote).getEmoteKey());
        }
        return (Integer)emoteClassIds.inverse().get(abstractEmote.getClass());
    }

    public AbstractEmote getAbstractEmote() {
        return this.abstractEmote;
    }

    public void playLocalEmote(AbstractEmote abstractEmote) {
        if (this.isOwned(abstractEmote)) {
            if (this.changedPerspective) {
                this.changedPerspective = false;
                this.keepPerspective = true;
            }
            this.abstractEmote = abstractEmote;
            this.stopEmote(Minecraft.getMinecraft().thePlayer);
            int n = this.getEmoteId(abstractEmote);
            CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new PlayEmoteRequestPacket(n));
        }
    }

    public Map<UUID, AbstractEmote> getActiveEmotes() {
        return this.activeEmotes;
    }

    public void setOwnedEmotes(List<Integer> list) {
        this.ownedEmotes = list;
    }

    public EmoteManager() {
        CheatBreaker.getInstance().getEventBus().checkClass2(ClientTickEvent.class, this::onClientTickEvent);
        CheatBreaker.getInstance().getEventBus().checkClass2(SimpleEvent3.class, this::onSimpleEvent3);
        CheatBreaker.getInstance().getEventBus().checkClass2(AbstractClientPlayerCustomModelPlayerFloatEvent.class, this::onAbstractClientPlayerCustomModelPlayerFloatEvent);
        CheatBreaker.getInstance().getEventBus().checkClass2(BooleanIntEvent.class, booleanIntEvent -> {
            if (booleanIntEvent.getEventButton() == 0 && booleanIntEvent.isFlag()) {
                this.stopEmote(Minecraft.getMinecraft().thePlayer);
            }
        });
        CheatBreaker.getInstance().getEventBus().checkClass2(IntEvent4.class, intEvent4 -> { try {
            if (Minecraft.getMinecraft().currentScreen == null && !this.ownedEmotes.isEmpty() && intEvent4.getIntValue() == CheatBreaker.getInstance().getKeybindSettings().emoteWheelKeybind.getIntValue() && !CheatBreaker.getInstance().getKeybindSettings().emoteWheelKeybind.hasMouseBind() && CheatBreaker.getInstance().getKeybindSettings().emoteWheelKeybind.areModifiersDown()) {
                Minecraft.getMinecraft().displayGuiScreen(new QuickEmoteWheelScreen(intEvent4.getIntValue()));
            }
        } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } });
        CheatBreaker.getInstance().getEventBus().checkClass2(IntEvent4.class, this::onIntEvent4);
    }

    public long getPauseStart() {
        return this.pauseStart;
    }

    public void startEmote(AbstractClientPlayer abstractClientPlayer, AbstractEmote abstractEmote) {
        if (abstractClientPlayer.getUniqueID().equals(Minecraft.getMinecraft().thePlayer.getUniqueID())) {
            if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 || this.keepPerspective) {
                Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
                this.changedPerspective = true;
            }
            this.keepPerspective = false;
        }
        this.stopEmote(abstractClientPlayer);
        this.abstractEmote = abstractEmote;
        this.activeEmotes.put(abstractClientPlayer.getUniqueID(), abstractEmote);
        if (abstractEmote instanceof ResourceEmote) {
            EmotePlayHelper.playEmoteByName(((ResourceEmote)abstractEmote).getEmoteKey(), abstractClientPlayer);
        }
    }

    public boolean isOwned(AbstractEmote abstractEmote) {
        int n = this.getEmoteId(abstractEmote);
        return this.ownedEmotes.contains(n);
    }

    public void onSimpleEvent3(SimpleEvent3 simpleEvent3) {
        Minecraft minecraft = Minecraft.getMinecraft();
        for (UUID uUID : new ArrayList<UUID>(this.activeEmotes.keySet())) {
            EntityPlayer entityPlayer = minecraft.theWorld == null ? null : minecraft.theWorld.getPlayerEntityByUUID(uUID);
            if (!(entityPlayer instanceof AbstractClientPlayer)) continue;
            this.stopEmote((AbstractClientPlayer)entityPlayer);
        }
        this.modelBackups.values().forEach(this::resetModelRotations);
        this.activeEmotes.clear();
        this.modelBackups.clear();
        this.abstractEmote = null;
        if (this.changedPerspective) {
            minecraft.gameSettings.thirdPersonView = 0;
        }
        this.changedPerspective = false;
        this.keepPerspective = false;
        PlayerEmoteState.states.clear();
    }

    public void setPauseStart(long l) {
        this.pauseStart = l;
    }

    public void onAbstractClientPlayerCustomModelPlayerFloatEvent(AbstractClientPlayerCustomModelPlayerFloatEvent abstractClientPlayerCustomModelPlayerFloatEvent) {
        this.tick();
        if (abstractClientPlayerCustomModelPlayerFloatEvent.getAbstractClientPlayer() instanceof PreviewPlayer) {
            return;
        }
        AbstractEmote abstractEmote = this.activeEmotes.get(abstractClientPlayerCustomModelPlayerFloatEvent.getAbstractClientPlayer().getUniqueID());
        if (abstractEmote != null) {
            if (abstractClientPlayerCustomModelPlayerFloatEvent.getEventPhase() == EventPhase.START) {
                this.modelBackups.put(abstractClientPlayerCustomModelPlayerFloatEvent.getAbstractClientPlayer().getUniqueID(), abstractClientPlayerCustomModelPlayerFloatEvent.getModelPlayer());
                abstractEmote.applyPose(abstractClientPlayerCustomModelPlayerFloatEvent.getAbstractClientPlayer(), abstractClientPlayerCustomModelPlayerFloatEvent.getModelPlayer(), abstractClientPlayerCustomModelPlayerFloatEvent.getScale());
            } else {
                abstractEmote.afterModelRender(abstractClientPlayerCustomModelPlayerFloatEvent.getAbstractClientPlayer(), abstractClientPlayerCustomModelPlayerFloatEvent.getScale());
            }
        }
    }

    public Map<UUID, ModelBiped> getModelBackups() {
        return this.modelBackups;
    }

    public void setKeepPerspective(boolean bl) {
        this.keepPerspective = bl;
    }

    public boolean isKeepPerspective() {
        return this.keepPerspective;
    }

    public void onClientTickEvent(ClientTickEvent clientTickEvent) {
        if (Minecraft.getMinecraft().theWorld == null) {
            return;
        }
        this.tick();
        if (!this.activeEmotes.isEmpty()) {
            ArrayList<UUID> arrayList = new ArrayList<UUID>();
            this.activeEmotes.forEach((uUID, abstractEmote) -> {
                EntityPlayer entityPlayer = Minecraft.getMinecraft().theWorld.getPlayerEntityByUUID((UUID)uUID);
                if (abstractEmote.isFinished() && entityPlayer != null && entityPlayer.getUniqueID() != null && this.activeEmotes.containsKey(entityPlayer.getUniqueID())) {
                    this.abstractEmote = null;
                    this.activeEmotes.remove(entityPlayer.getUniqueID());
                    abstractEmote.restorePerspective((AbstractClientPlayer)entityPlayer);
                    this.resetModelRotations(this.getModelBackups().remove(uUID));
                    arrayList.add((UUID)uUID);
                }
            });
            arrayList.forEach(this.activeEmotes::remove);
        }
    }

    public void stopEmote(AbstractClientPlayer abstractClientPlayer) {
        if (this.activeEmotes.containsKey(abstractClientPlayer.getUniqueID())) {
            this.abstractEmote = null;
            AbstractEmote abstractEmote = this.activeEmotes.get(abstractClientPlayer.getUniqueID());
            abstractEmote.restorePerspective(abstractClientPlayer);
            this.activeEmotes.remove(abstractClientPlayer.getUniqueID());
            this.resetModelRotations(this.modelBackups.remove(abstractClientPlayer.getUniqueID()));
            EntityClientPlayerMP entityClientPlayerMP = Minecraft.getMinecraft().thePlayer;
            if (entityClientPlayerMP != null && abstractClientPlayer.getUniqueID().equals(entityClientPlayerMP.getUniqueID())) {
                CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new StopEmotePacket());
            }
        }
    }
}
