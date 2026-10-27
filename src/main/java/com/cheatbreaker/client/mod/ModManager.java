package com.cheatbreaker.client.mod;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.BlockIntEvent;
import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.EntityFXDoubleEvent;
import com.cheatbreaker.client.event.EventBus;
import com.cheatbreaker.client.event.GuiScreenEvent;
import com.cheatbreaker.client.event.IntEvent4;
import com.cheatbreaker.client.event.SimpleEvent13;
import com.cheatbreaker.client.event.SimpleEvent26;
import com.cheatbreaker.client.event.SimpleEvent3;
import com.cheatbreaker.client.event.SimpleEvent41;
import com.cheatbreaker.client.event.StringEvent1;
import com.cheatbreaker.client.input.ControllerMouseHelper;
import com.cheatbreaker.client.input.ModernKeybindHandler;
import com.cheatbreaker.client.mod.impl.AnimationsMod;
import com.cheatbreaker.client.mod.impl.ArmorStatusMod;
import com.cheatbreaker.client.mod.impl.AutoTextMod;
import com.cheatbreaker.client.mod.impl.BlockOverlayMod;
import com.cheatbreaker.client.mod.impl.BossBarMod;
import com.cheatbreaker.client.mod.impl.CPSMod;
import com.cheatbreaker.client.mod.impl.CameraController;
import com.cheatbreaker.client.mod.impl.ChatMod;
import com.cheatbreaker.client.mod.impl.ClockMod;
import com.cheatbreaker.client.mod.impl.ComboCounterMod;
import com.cheatbreaker.client.mod.impl.CooldownsMod;
import com.cheatbreaker.client.mod.impl.CoordinatesMod;
import com.cheatbreaker.client.mod.impl.CreditsMod;
import com.cheatbreaker.client.mod.impl.CrosshairMod;
import com.cheatbreaker.client.mod.impl.DateMod;
import com.cheatbreaker.client.mod.impl.DemoTimerMod;
import com.cheatbreaker.client.mod.impl.DirectionHUDMod;
import com.cheatbreaker.client.mod.impl.EmoteTestingMod;
import com.cheatbreaker.client.mod.impl.EnchantmentGlintMod;
import com.cheatbreaker.client.mod.impl.EnvironmentChangerMod;
import com.cheatbreaker.client.mod.impl.FOVChangerMod;
import com.cheatbreaker.client.mod.impl.FPSMod;
import com.cheatbreaker.client.mod.impl.FreeLookHandler;
import com.cheatbreaker.client.mod.impl.HUDEditorInformationMod;
import com.cheatbreaker.client.mod.impl.HitColorMod;
import com.cheatbreaker.client.mod.impl.HitboxesMod;
import com.cheatbreaker.client.mod.impl.HurtCamMod;
import com.cheatbreaker.client.mod.impl.HypixelCollectionMod;
import com.cheatbreaker.client.mod.impl.ItemPhysicsMod;
import com.cheatbreaker.client.mod.impl.KeyStrokesMod;
import com.cheatbreaker.client.mod.impl.MemoryUsageMod;
import com.cheatbreaker.client.mod.impl.MotionBlurMod;
import com.cheatbreaker.client.mod.impl.MumbleLinkMod;
import com.cheatbreaker.client.mod.impl.NametagMod;
import com.cheatbreaker.client.mod.impl.NickHiderMod;
import com.cheatbreaker.client.mod.impl.NoclipMod;
import com.cheatbreaker.client.mod.impl.NotificationsMod;
import com.cheatbreaker.client.mod.impl.OpacityMod;
import com.cheatbreaker.client.mod.impl.PackDisplayMod;
import com.cheatbreaker.client.mod.impl.PackTweaksMod;
import com.cheatbreaker.client.mod.impl.ParticlesMod;
import com.cheatbreaker.client.mod.impl.PerspectiveMod;
import com.cheatbreaker.client.mod.impl.PingMod;
import com.cheatbreaker.client.mod.impl.PlayerListMod;
import com.cheatbreaker.client.mod.impl.PlaytimeMod;
import com.cheatbreaker.client.mod.impl.PotionCounterMod;
import com.cheatbreaker.client.mod.impl.PotionEffectsMod;
import com.cheatbreaker.client.mod.impl.ReachDisplayMod;
import com.cheatbreaker.client.mod.impl.SPSMod;
import com.cheatbreaker.client.mod.impl.SaturationMod;
import com.cheatbreaker.client.mod.impl.ScoreboardMod;
import com.cheatbreaker.client.mod.impl.ServerAddressMod;
import com.cheatbreaker.client.mod.impl.SprintResetCounterMod;
import com.cheatbreaker.client.mod.impl.StaffNametagsMod;
import com.cheatbreaker.client.mod.impl.TNTTimerMod;
import com.cheatbreaker.client.mod.impl.TeamManager;
import com.cheatbreaker.client.mod.impl.TitlesMod;
import com.cheatbreaker.client.mod.impl.ToggleSprintMod;
import com.cheatbreaker.client.mod.impl.TransferredPacketsMod;
import com.cheatbreaker.client.mod.impl.TrimpMod;
import com.cheatbreaker.client.mod.impl.VersionWatermarkMod;
import com.cheatbreaker.client.mod.impl.ViewBobbingMod;
import com.cheatbreaker.client.mod.impl.VoiceChatUIMod;
import com.cheatbreaker.client.mod.impl.ZansMinimapMod;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingType;
import com.cheatbreaker.client.network.ReconnectHandler;
import com.cheatbreaker.client.ui.component.KeybindComponent;
import com.cheatbreaker.client.ui.screen.PlayerViewer;
import com.cheatbreaker.client.util.DeveloperFlags;
import com.cheatbreaker.client.voice.VoicePushToTalkHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockBush;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class ModManager {
   public List<AbstractMod> abstractModList = new ArrayList<>();
   public List<AbstractNamedMod> abstractNamedModList = new ArrayList<>();
   public List<AbstractInternalMod> abstractInternalModList = new ArrayList<>();
   public Map<Setting, KeybindComponent> settingToKeybindComponentMap = new HashMap<>();
   public int intValue = 0;
   public CoordinatesMod coordinatesMod;
   public ZansMinimapMod zansMinimapMod;
   public AnimationsMod animationsMod;
   public ToggleSprintMod toggleSprintMod;
   public PotionEffectsMod potionEffectsMod;
   public ArmorStatusMod armorStatusMod;
   public KeyStrokesMod keyStrokesMod;
   public ScoreboardMod scoreboardMod;
   public PlayerListMod playerListMod;
   public TitlesMod titlesMod;
   public int intValue2;
   public ChatMod chatMod;
   public BossBarMod bossBarMod;
   public CooldownsMod cooldownsMod;
   public NotificationsMod notificationsMod;
   public DirectionHUDMod directionHUDMod;
   public EnvironmentChangerMod environmentChangerMod;
   public FOVChangerMod fovchangerMod;
   public EnchantmentGlintMod enchantmentGlintMod;
   public CrosshairMod crosshairMod;
   public HitColorMod hitColorMod;
   public ParticlesMod particlesMod;
   public NametagMod nametagMod;
   public NickHiderMod nickHiderMod;
   public String text;
   public AutoTextMod autoTextMod;
   public MumbleLinkMod mumbleLinkMod;
   public TNTTimerMod tnttimerMod;
   public BlockOverlayMod blockOverlayMod;
   public PackTweaksMod packTweaksMod;
   public PerspectiveMod perspectiveMod;
   public HitboxesMod hitboxesMod;
   public HypixelCollectionMod hypixelCollectionMod;
   public CPSMod cpsmod;
   public SPSMod spsmod;
   public FPSMod fpsmod;
   public MemoryUsageMod memoryUsageMod;
   public PotionCounterMod potionCounterMod;
   public ComboCounterMod comboCounterMod;
   public SprintResetCounterMod sprintResetCounterMod;
   public ReachDisplayMod reachDisplayMod;
   public ServerAddressMod serverAddressMod;
   public PingMod pingMod;
   public ClockMod clockMod;
   public DateMod dateMod;
   public PlaytimeMod playtimeMod;
   public SaturationMod saturationMod;
   public PackDisplayMod packDisplayMod;
   public MotionBlurMod motionBlurMod;
   public DemoTimerMod demoTimerMod;
   public OpacityMod xray;
   public StaffNametagsMod nametags;
   public NoclipMod noclip;
   public TrimpMod bunnyhop;
   public VersionWatermarkMod versionWatermarkMod;
   public TransferredPacketsMod transferredPacketsMod;
   public HUDEditorInformationMod hudeditorInformationMod;
   public CreditsMod creditsMod;
   public EmoteTestingMod emoteTestingMod;
   public VoicePushToTalkHandler voicePushToTalkHandler;
   public FreeLookHandler freeLookHandler;
   public CameraController cameraController;
   public TeamManager teamManager;
   public ModernKeybindHandler modernKeybindHandler;
   public ControllerMouseHelper controllerMouseHelper;

   public void onStringEvent1(StringEvent1 stringEvent1) {
      if (stringEvent1.getMessage().equals("/cb debug") && CheatBreaker.getInstance().isDebug()) {
         stringEvent1.setFlag(true);
         DeveloperFlags.showInternalMods = !DeveloperFlags.showInternalMods;
         ChatComponentText chatComponentText = new ChatComponentText(EnumChatFormatting.GRAY + "Debug: " + EnumChatFormatting.RESET + DeveloperFlags.showInternalMods);
         chatComponentText.setHasIcon(true);
         Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(chatComponentText);

         for (AbstractInternalMod abstractInternalMod : this.abstractInternalModList) {
            if (abstractInternalMod.isEnabled()) {
               abstractInternalMod.onEnable();
            } else {
               abstractInternalMod.onDisable();
            }
         }
      }
   }

   public void setIntValue2(int n) {
      this.intValue2 = n;
   }

   public void onIntEvent4(IntEvent4 intEvent4) {
      if (intEvent4.getIntValue() != 0) {
         for (AbstractNamedMod abstractNamedMod : this.abstractNamedModList) {
            if (abstractNamedMod.isStaffModEnabled() && abstractNamedMod.toggleModKeybind.getIntValue() == intEvent4.getIntValue()) {
               abstractNamedMod.setEnabled(!abstractNamedMod.isEnabled());
            }
         }
      }
   }

   public AbstractMod findAbstractMod(String str) {
      return this.abstractModList.stream().filter(abstractMod -> abstractMod.getName().equalsIgnoreCase(str)).findFirst().orElse(null);
   }

   public void onSimpleEvent41(SimpleEvent41 simpleEvent41) {
      if (!CheatBreaker.getInstance().getKeybindSettings().showPotionInfoBackground.getBooleanValue()) {
         simpleEvent41.setFlag(true);
      }
   }

   public void onClientTickEvent(ClientTickEvent clientTickEvent) {
      if (this.intValue > 15) {
         CheatBreaker.getInstance().getKeybindSettings().entityDistanceCache.clear();
         CheatBreaker.getInstance().getKeybindSettings().tileEntityDistanceCache.clear();
         this.intValue = 0;
      }

      this.intValue++;
   }

   public void onSimpleEvent13(SimpleEvent13 simpleEvent13) {
      if (!this.potionEffectsMod.isEnabled() && !CheatBreaker.getInstance().getKeybindSettings().showPotionInfoInInventory.getBooleanValue()) {
         simpleEvent13.setFlag(true);
      }
   }

   public void setText(String str) {
      this.text = str;
   }

   public ModManager(EventBus eventBus) {
      this.abstractModList.add(this.coordinatesMod = new CoordinatesMod());
      this.abstractModList.add(this.zansMinimapMod = new ZansMinimapMod());
      this.abstractModList.add(this.animationsMod = new AnimationsMod());
      this.abstractModList.add(this.toggleSprintMod = new ToggleSprintMod());
      this.abstractModList.add(this.potionEffectsMod = new PotionEffectsMod());
      this.abstractModList.add(this.armorStatusMod = new ArmorStatusMod());
      this.abstractModList.add(this.keyStrokesMod = new KeyStrokesMod());
      this.abstractModList.add(this.scoreboardMod = new ScoreboardMod());
      this.abstractModList.add(this.playerListMod = new PlayerListMod());
      this.abstractModList.add(this.titlesMod = new TitlesMod());
      this.abstractModList.add(this.chatMod = new ChatMod());
      this.abstractModList.add(this.bossBarMod = new BossBarMod());
      this.abstractModList.add(this.cooldownsMod = new CooldownsMod());
      this.abstractModList.add(this.notificationsMod = new NotificationsMod());
      this.abstractModList.add(this.directionHUDMod = new DirectionHUDMod());
      this.abstractModList.add(this.environmentChangerMod = new EnvironmentChangerMod());
      this.abstractModList.add(this.fovchangerMod = new FOVChangerMod());
      this.abstractModList.add(this.enchantmentGlintMod = new EnchantmentGlintMod());
      this.abstractModList.add(this.crosshairMod = new CrosshairMod());
      this.abstractModList.add(this.hitColorMod = new HitColorMod());
      this.abstractModList.add(new HurtCamMod());
      this.abstractModList.add(this.particlesMod = new ParticlesMod());
      this.abstractModList.add(this.nametagMod = new NametagMod());
      this.abstractModList.add(this.nickHiderMod = new NickHiderMod());
      this.abstractModList.add(this.autoTextMod = new AutoTextMod());
      this.abstractModList.add(this.mumbleLinkMod = new MumbleLinkMod());
      this.abstractModList.add(this.tnttimerMod = new TNTTimerMod());
      this.abstractModList.add(this.blockOverlayMod = new BlockOverlayMod());
      this.abstractModList.add(this.packTweaksMod = new PackTweaksMod());
      this.abstractModList.add(new ItemPhysicsMod());
      this.abstractModList.add(this.perspectiveMod = new PerspectiveMod());
      this.abstractModList.add(new ViewBobbingMod());
      this.abstractModList.add(this.hitboxesMod = new HitboxesMod());
      this.abstractModList.add(this.hypixelCollectionMod = new HypixelCollectionMod());
      this.abstractModList.add(this.cpsmod = new CPSMod());
      this.abstractModList.add(this.spsmod = new SPSMod());
      this.abstractModList.add(this.fpsmod = new FPSMod());
      this.abstractModList.add(this.memoryUsageMod = new MemoryUsageMod());
      this.abstractModList.add(this.potionCounterMod = new PotionCounterMod());
      this.abstractModList.add(this.comboCounterMod = new ComboCounterMod());
      this.abstractModList.add(this.sprintResetCounterMod = new SprintResetCounterMod());
      this.abstractModList.add(this.reachDisplayMod = new ReachDisplayMod());
      this.abstractModList.add(this.serverAddressMod = new ServerAddressMod());
      this.abstractModList.add(this.pingMod = new PingMod());
      this.abstractModList.add(this.clockMod = new ClockMod());
      this.abstractModList.add(this.dateMod = new DateMod());
      this.abstractModList.add(this.playtimeMod = new PlaytimeMod());
      this.abstractModList.add(this.saturationMod = new SaturationMod());
      this.abstractModList.add(this.packDisplayMod = new PackDisplayMod());
      this.abstractModList.add(this.motionBlurMod = new MotionBlurMod());
      this.abstractModList.add(new VoiceChatUIMod());
      if (Minecraft.getMinecraft().isDemo()) {
         this.abstractModList.add(this.demoTimerMod = new DemoTimerMod());
      }

      this.abstractNamedModList.add(this.xray = new OpacityMod("xray"));
      this.abstractNamedModList.add(this.nametags = new StaffNametagsMod("nametags"));
      this.abstractNamedModList.add(this.noclip = new NoclipMod("noclip"));
      this.abstractNamedModList.add(this.bunnyhop = new TrimpMod("bunnyhop"));
      this.abstractInternalModList.add(this.versionWatermarkMod = new VersionWatermarkMod());
      this.abstractInternalModList.add(this.transferredPacketsMod = new TransferredPacketsMod());
      this.abstractInternalModList.add(this.hudeditorInformationMod = new HUDEditorInformationMod());
      this.abstractInternalModList.add(this.creditsMod = new CreditsMod());
      this.abstractInternalModList.add(this.emoteTestingMod = new EmoteTestingMod());
      this.voicePushToTalkHandler = new VoicePushToTalkHandler();
      this.freeLookHandler = new FreeLookHandler();
      this.cameraController = new CameraController();
      this.teamManager = new TeamManager();
      this.modernKeybindHandler = new ModernKeybindHandler();
      this.controllerMouseHelper = new ControllerMouseHelper();
      new ReconnectHandler();
      this.teamManager.process(true);
      eventBus.checkClass2(IntEvent4.class, this::onIntEvent4);
      eventBus.checkClass2(StringEvent1.class, this::onStringEvent1);
      eventBus.checkClass2(GuiScreenEvent.class, guiScreenEvent -> CheatBreaker.getInstance().setGuiScreen(guiScreenEvent.getCurrentScreen()));
      eventBus.checkClass2(SimpleEvent26.class, simpleEvent26 -> {
         ServerData currentServerData = Minecraft.getMinecraft().getCurrentServerData();
         if (currentServerData != null) {
            CheatBreaker.getInstance().getWebsocketController().handleString(currentServerData.serverIP, currentServerData.text, currentServerData.intValue);

            for (AbstractMod abstractMod : this.abstractModList) {
               if (abstractMod.getRestrictedServerAddresses() != null) {
                  for (String str : abstractMod.getRestrictedServerAddresses()) {
                     if (currentServerData.serverIP.toLowerCase().contains(str)) {
                        abstractMod.setRestricted(true);
                     }
                  }
               }
            }
         } else {
            CheatBreaker.getInstance().getWebsocketController().handleString("", "", 0);
         }
      });
      eventBus.checkClass2(SimpleEvent3.class, simpleEvent3 -> {
         CheatBreaker.getInstance().getWebsocketController().handleString("", "", 0);

         for (AbstractMod abstractMod : this.abstractNamedModList) {
            abstractMod.setEnabled(false);
            abstractMod.setStaffModEnabled(false);
         }

         for (AbstractMod abstractMod : this.abstractModList) {
            abstractMod.setRestricted(false);
         }

         PlayerViewer.run();
      });
      eventBus.checkClass2(ClientTickEvent.class, this::onClientTickEvent);
      eventBus.checkClass2(SimpleEvent13.class, this::onSimpleEvent13);
      eventBus.checkClass2(SimpleEvent41.class, this::onSimpleEvent41);
      eventBus.checkClass2(BlockIntEvent.class, blockIntEvent -> {
         if (CheatBreaker.getInstance().getKeybindSettings().hideFoliage.getBooleanValue() && blockIntEvent.getBlock() instanceof BlockBush) {
            blockIntEvent.setFlag(true);
         }
      });
      eventBus.checkClass2(
         EntityFXDoubleEvent.class,
         entityFXDoubleEvent -> {
            if (CheatBreaker.getInstance().getKeybindSettings().disableParticlePhysics.getBooleanValue()) {
               entityFXDoubleEvent.setFlag(true);
               entityFXDoubleEvent.getEntityFX()
                  .setEntityBoundingBox(
                     entityFXDoubleEvent.getEntityFX().getEntityBoundingBox().offset(entityFXDoubleEvent.getDoubleValue1(), entityFXDoubleEvent.getDoubleValue2(), entityFXDoubleEvent.getDoubleValue3())
                  );
               entityFXDoubleEvent.getEntityFX().resetPositionToBB();
            }
         }
      );
   }

   public boolean check() {
      Iterator iterator = this.abstractNamedModList.iterator();
      if (iterator.hasNext()) {
         AbstractMod abstractMod = (AbstractMod)iterator.next();
         return abstractMod.isStaffModEnabled();
      } else {
         return false;
      }
   }

   public boolean checkSetting(Setting setting, int n) {
      return this.checkSetting(setting, n, false, false, false);
   }

   public boolean checkSetting(Setting setting, int n, boolean flag, boolean flag2, boolean flag3) {
      boolean flag4 = false;
      Setting setting2 = null;

      for (Setting setting3 : this.settingToKeybindComponentMap.keySet()) {
         if (setting3 != setting && setting3.getType() == SettingType.INTEGER) {
            if (setting3.hasKeycode() && n == setting3.getKeyCode()) {
               setting2 = setting3;
               flag4 = true;
            }

            boolean flag5 = setting3.isRequireShift() == flag && setting3.isRequireCtrl() == flag2 && setting3.isRequireAlt() == flag3;
            if (setting3.hasMouseBind() && n == setting3.getIntValue() && flag5) {
               setting2 = setting3;
               flag4 = true;
            }

            if ((setting3.getName().toLowerCase().startsWith("hot key") || setting3.getName().toLowerCase().endsWith("keybind")) && n == setting3.getIntValue() && flag5
               )
             {
               setting2 = setting3;
               flag4 = true;
            }
         }
      }

      if (flag4) {
         CheatBreaker.getInstance()
            .getModManager()
            .notificationsMod
            .handleString("error", EnumChatFormatting.RED + setting.getName() + " is already bound to " + setting2.getName() + "!", 5000L);
      }

      return flag4;
   }
}
