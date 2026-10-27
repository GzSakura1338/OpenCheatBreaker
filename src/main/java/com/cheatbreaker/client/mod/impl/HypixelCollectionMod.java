package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityLivingBaseDoubleEvent2;
import com.cheatbreaker.client.event.EntityLivingBaseDoubleEvent4;
import com.cheatbreaker.client.event.SimpleEvent6;
import com.cheatbreaker.client.event.StringEvent3;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

public class HypixelCollectionMod extends AbstractMod {
   public Pattern friendRequestPattern = Pattern.compile(
      "§m----------------------------------------------------Friend request from (?<name>.+)\\[ACCEPT\\] - \\[DENY\\] - \\[IGNORE\\].*"
   );
   public Pattern bedDestructionPattern = Pattern.compile("BED DESTRUCTION > Your Bed .*");
   public String[] eliminationMessages = new String[]{"You have been eliminated!", "Reward Summary"};
   public Pattern guildJoinPattern = Pattern.compile("Guild > (?<name>.+) joined\\..*");
   public Pattern friendJoinPattern = Pattern.compile("Friend > (?<name>.+) joined\\..*");
   public List<String> winMessages = Arrays.asList(
      "1st Killer - ",
      "1st Place - ",
      "Winner: ",
      " - Damage Dealt - ",
      "Winning Team -",
      "1st - ",
      "Winners: ",
      "Winner: ",
      "Winning Team: ",
      " won the game!",
      "Top Seeker: ",
      "1st Place: ",
      "Last team standing!",
      "Winner #1 (",
      "Top Survivors",
      "Winners - ",
      "Sumo Duel - "
   );
   public Map<UUID, Integer> levels = new ConcurrentHashMap<>();
   public Set<UUID> pendingLevelLookups = Sets.newSetFromMap(new ConcurrentHashMap());
   public JsonParser jsonParser = new JsonParser();
   public RenderManager renderManager = this.mc.getRenderManager();
   public Setting autoCommandOptions = new SettingGroup(this, "Auto Command Options").changeValue(true);
   public Setting autoTip = new Setting(this, "Auto Tip").changeValue(false).withHierarchyParents(this.autoCommandOptions);
   public Setting autoFriend = new Setting(this, "Auto Friend").changeValue(false).withHierarchyParents(this.autoCommandOptions);
   public Setting autoFriendMode = new Setting(this, "Auto Friend Mode")
      .changeValue("Accept")
      .setStringValues("Accept", "Deny", "Ignore")
      .withHierarchyParents(this.autoFriend);
   public Setting autoFriendDelay = new Setting(this, "Auto Friend Delay")
      .setUnit("s")
      .changeValue(0.0F)
      .setRange(0.0F, 15.0F)
      .withHierarchyParents(this.autoFriend);
   public Setting autoChatOptions = new SettingGroup(this, "Auto Chat Options").changeValue(true);
   public HypixelCollectionMod.AutoChatOption autoGg = new HypixelCollectionMod.AutoChatOption("GG").withMaxDelay(5.0F);
   public HypixelCollectionMod.AutoChatOption autoGl = new HypixelCollectionMod.AutoChatOption("GL");
   public HypixelCollectionMod.AutoChatOption autoWb = new HypixelCollectionMod.AutoChatOption("WB").withMinDelay(2.0F);
   public Setting welcomeBackFriends = new Setting(this, "Welcome Back Friends").changeValue(true).withHierarchyParents(this.autoWb.auto);
   public Setting welcomeBackGuildMembers = new Setting(this, "Welcome Back Guild Members").changeValue(true).withHierarchyParents(this.autoWb.auto);
   public Setting bedWarsOptions = new SettingGroup(this, "Bed Wars Options").changeValue(true);
   public Setting hardcoreHearts = new Setting(this, "Hardcore Hearts")
      .setDescription("Changes your hearts to show hardcore hearts when you lose your own bed.")
      .changeValue(false)
      .withParents(this.bedWarsOptions);
   public Setting levelheadOptions = new SettingGroup(this, "Levelhead Options").changeValue(true).setCondition(() -> false);
   public Setting levelhead = new Setting(this, "Levelhead").changeValue(true).withParents(this.levelheadOptions).setCondition(() -> false);
   public Setting showOnSelf = new Setting(this, "Show on self").changeValue(true).withParents(this.levelhead).setCondition(() -> false);
   public Setting levelTagPosition = new Setting(this, "Level Tag Position")
      .changeValue(false)
      .setToggleTexts("Below", "Above")
      .withHierarchyParents(this.levelhead)
      .setCondition(() -> false);
   public Setting levelPrefixString = new Setting(this, "Level Prefix String").changeValue("Level:").withParents(this.levelhead).setCondition(() -> false);
   public Setting levelSuffixString = new Setting(this, "Level Suffix String").changeValue("").withParents(this.levelhead).setCondition(() -> false);
   public Setting levelPrefixColor = new Setting(this, "Level Prefix Color")
      .changeValue(-11141121)
      .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
      .withHierarchyParents(this.levelhead)
      .setCondition(() -> false);
   public Setting levelAmountColor = new Setting(this, "Level Amount Color")
      .changeValue(-171)
      .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
      .withHierarchyParents(this.levelhead)
      .setCondition(() -> false);
   public Setting levelSuffixColor = new Setting(this, "Level Suffix Color")
      .changeValue(-11141121)
      .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
      .withHierarchyParents(this.levelhead)
      .setCondition(() -> false);

   public void sendChatDelayed(String str, float f) {
      new Thread(() -> {
         try {
            Thread.sleep((long)f * 1000L);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }

         this.mc.thePlayer.sendChatMessage(str);
      }).start();
   }

   public void renderStandingLevelTag(EntityLivingBase entityLivingBase, String str, String str2, String str3, double d, double d2, double d3, int n) {
      double distanceSqToEntity = entityLivingBase.getDistanceSqToEntity(this.renderManager.livingPlayer);
      if (distanceSqToEntity <= n * n) {
         FontRenderer fontRendererObj = this.mc.fontRendererObj;
         float f = 1.6F;
         float f2 = 0.016666668F * f;
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)d + 0.0F, (float)d2 + entityLivingBase.height + 0.5F, (float)d3);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
         GlStateManager.scale(-f2, -f2, f2);
         GlStateManager.disableLighting();
         GlStateManager.depthMask(false);
         GlStateManager.disableDepth();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         byte b = 0;
         int n2 = fontRendererObj.getStringWidth(str) / 2 + fontRendererObj.getStringWidth(str2) / 2 + fontRendererObj.getStringWidth(str3) / 2;
         GlStateManager.disableTexture2D();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
         worldRenderer.pos(-n2 - 1, -1 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         worldRenderer.pos(-n2 - 1, 8 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         worldRenderer.pos(n2 + 1, 8 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         worldRenderer.pos(n2 + 1, -1 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         instance.draw();
         GlStateManager.enableTexture2D();
         GlStateManager.enableDepth();
         GlStateManager.depthMask(true);
         fontRendererObj.drawString(
            str,
            -fontRendererObj.getStringWidth(str) / 2 + -fontRendererObj.getStringWidth(str2) / 2 + -fontRendererObj.getStringWidth(str3) / 2,
            b,
            this.levelPrefixColor.getColorValue()
         );
         fontRendererObj.drawString(
            str2,
            fontRendererObj.getStringWidth(str) / 2 + -fontRendererObj.getStringWidth(str2) / 2 + -fontRendererObj.getStringWidth(str3) / 2,
            b,
            this.levelAmountColor.getColorValue()
         );
         fontRendererObj.drawString(
            str3,
            fontRendererObj.getStringWidth(str) / 2 + fontRendererObj.getStringWidth(str2) / 2 + -fontRendererObj.getStringWidth(str3) / 2,
            b,
            this.levelSuffixColor.getColorValue()
         );
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }

   public void onSimpleEvent6(SimpleEvent6 simpleEvent6) {
      if (Minecraft.getMinecraft().thePlayer != null) {
         if (Minecraft.getMinecraft().getCurrentServerData() != null) {
            if (this.autoTip.getBooleanValue() && Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains("hypixel")) {
               this.mc.thePlayer.sendChatMessage("/tip all");
            }
         }
      }
   }

   public boolean shouldHideMessage(String str) {
      if (!this.autoTip.getBooleanValue()
         || !str.startsWith("You can't tip the same person")
            && !str.equals("Still processing your most recent request!")
            && !str.startsWith("You've already tipped that person")
            && !str.equals("You cannot tip yourself!")
            && !str.startsWith("You can only use the /tip command")
            && !str.equals("You are not allowed to use commands as a spectator!")
            && !str.equals("Slow down! You can only use /tip every few seconds.")
            && !str.startsWith("You've already tipped someone in the past hour in ")) {
         return this.autoGl.filter.getBooleanValue() && Arrays.stream(str.split(" ")).anyMatch(str2 -> str2.equalsIgnoreCase(this.autoGl.keyword))
            ? true
            : this.autoGg.filter.getBooleanValue() && Arrays.stream(str.split(" ")).anyMatch(str2 -> str2.equalsIgnoreCase(this.autoGg.keyword));
      } else {
         return true;
      }
   }

   public boolean isOnHypixel() {
      return this.mc.getCurrentServerData() != null && this.mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel");
   }

   public void onEntityLivingBaseDoubleEvent2(EntityLivingBaseDoubleEvent2 entityLivingBaseDoubleEvent2) {
      if (this.isOnHypixel() && this.levelhead.getBooleanValue() && !this.levelTagPosition.getBooleanValue()) {
         EntityLivingBase entityLivingBase = entityLivingBaseDoubleEvent2.getEntityLivingBase();
         UUID uniqueID = entityLivingBase.getUniqueID();
         if (!this.levels.containsKey(uniqueID)) {
            this.levels.put(uniqueID, -1);
            this.pendingLevelLookups.add(uniqueID);
         }

         int n;
         if ((n = this.levels.getOrDefault(entityLivingBase.getUniqueID(), -1)) > 0
            && (entityLivingBaseDoubleEvent2.getEntityLivingBase() != this.mc.thePlayer || this.showOnSelf.getBooleanValue())) {
            String stringValue = this.levelPrefixString.getStringValue();
            String stringValue2 = this.levelSuffixString.getStringValue();
            String str = (!stringValue.isEmpty() ? " " : "") + n + (!stringValue2.isEmpty() ? " " : "");
            this.renderLevelTag(entityLivingBaseDoubleEvent2.getEntityLivingBase(), stringValue, str, stringValue2, entityLivingBaseDoubleEvent2.getX(), entityLivingBaseDoubleEvent2.getY(), entityLivingBaseDoubleEvent2.getZ());
            entityLivingBaseDoubleEvent2.setY(entityLivingBaseDoubleEvent2.getY() + this.mc.fontRendererObj.FONT_HEIGHT * 1.15F * entityLivingBaseDoubleEvent2.getDoubleValue4());
         }
      }
   }

   public void startLevelThread() {
      new Thread(
            () -> {
               while (true) {
                  if (this.isEnabled() && this.levelhead.getBooleanValue() && !this.pendingLevelLookups.isEmpty()) {
                     ImmutableSet copyOf = ImmutableSet.copyOf(this.pendingLevelLookups);
                     this.pendingLevelLookups.clear();
                     UnmodifiableIterator unmodifiableIterator = copyOf.iterator();

                     while (unmodifiableIterator.hasNext()) {
                        try {
                           UUID uuid = (UUID)unmodifiableIterator.next();
                           URL url = new URL("https://api.slothpixel.me/api/players/" + uuid);
                           HttpURLConnection openConnection = (HttpURLConnection)url.openConnection();
                           openConnection.setRequestProperty(
                              "User-Agent",
                              "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.5.1 Safari/605.1.15"
                           );
                           openConnection.setRequestMethod("GET");
                           openConnection.setReadTimeout(8000);
                           openConnection.setConnectTimeout(8000);
                           openConnection.setDoOutput(true);
                           String str = IOUtils.toString(openConnection.getInputStream(), Charsets.UTF_8);
                           JsonObject asJsonObject = this.jsonParser.parse(str).getAsJsonObject();
                           this.levels.put(uuid, asJsonObject.get("level").getAsInt());
                        } catch (Exception e) {
                           e.printStackTrace();
                        }
                     }
                  }

                  try {
                     Thread.sleep(2000L);
                  } catch (InterruptedException e) {
                  }
               }
            },
            "Hypixel Level Thread"
         )
         .start();
   }

   public void onEntityLivingBaseDoubleEvent4(EntityLivingBaseDoubleEvent4 entityLivingBaseDoubleEvent4) {
      if (this.isOnHypixel() && this.levelhead.getBooleanValue() && this.levelTagPosition.getBooleanValue()) {
         EntityLivingBase entityLivingBase = entityLivingBaseDoubleEvent4.getEntityLivingBase();
         UUID uniqueID = entityLivingBase.getUniqueID();
         if (!this.levels.containsKey(uniqueID)) {
            this.levels.put(uniqueID, -1);
            this.pendingLevelLookups.add(uniqueID);
         }

         int n;
         if ((n = this.levels.getOrDefault(entityLivingBase.getUniqueID(), -1)) > 0
            && (entityLivingBaseDoubleEvent4.getEntityLivingBase() != this.mc.thePlayer || this.showOnSelf.getBooleanValue())) {
            String stringValue = this.levelPrefixString.getStringValue();
            String stringValue2 = this.levelSuffixString.getStringValue();
            String str = (!stringValue.isEmpty() ? " " : "") + n + (!stringValue2.isEmpty() ? " " : "");
            if (entityLivingBaseDoubleEvent4.getEntityLivingBase() != this.mc.thePlayer
               || !this.showOnSelf.getBooleanValue()
               || CheatBreaker.getInstance().getModManager().nametagMod.isEnabled()
                  && CheatBreaker.getInstance().getModManager().nametagMod.shownOwnNametag.getBooleanValue()) {
               entityLivingBaseDoubleEvent4.setY(entityLivingBaseDoubleEvent4.getY() + this.mc.fontRendererObj.FONT_HEIGHT * 1.15F * entityLivingBaseDoubleEvent4.getDoubleValue4());
            }

            this.renderLevelTag(entityLivingBaseDoubleEvent4.getEntityLivingBase(), stringValue, str, stringValue2, entityLivingBaseDoubleEvent4.getX(), entityLivingBaseDoubleEvent4.getY(), entityLivingBaseDoubleEvent4.getZ());
         }
      }
   }

   public void renderLevelTag(EntityLivingBase entityLivingBase, String str, String str2, String str3, double d, double d2, double d3) {
      if (entityLivingBase instanceof EntityPlayer) {
         double distanceSqToEntity = entityLivingBase.getDistanceSqToEntity(this.renderManager.livingPlayer);
         byte b = 64;
         byte b2 = 32;
         float f = entityLivingBase.isSneaking() ? b2 : b;
         if (distanceSqToEntity < f * f) {
            GlStateManager.alphaFunc(516, 0.1F);
            if (entityLivingBase.isSneaking()) {
               FontRenderer fontRendererObj = this.mc.fontRendererObj;
               GlStateManager.pushMatrix();
               GlStateManager.translate(d, d2 + entityLivingBase.height + 0.5 - (entityLivingBase.isChild() ? entityLivingBase.height / 2.0F : 0.0F), d3);
               GL11.glNormal3f(0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
               GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
               GlStateManager.translate(0.0F, 9.374999F, 0.0F);
               GlStateManager.disableLighting();
               GlStateManager.depthMask(false);
               GlStateManager.enableBlend();
               GlStateManager.disableDepth();
               GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
               int n = fontRendererObj.getStringWidth(str) / 2 + fontRendererObj.getStringWidth(str2) / 2 + fontRendererObj.getStringWidth(str3) / 2;
               Tessellator instance = Tessellator.getInstance();
               WorldRenderer worldRenderer = instance.getWorldRenderer();
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
               worldRenderer.pos(-n - 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldRenderer.pos(-n - 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldRenderer.pos(n + 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldRenderer.pos(n + 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               instance.draw();
               GlStateManager.enableTexture2D();
               GlStateManager.depthMask(true);
               fontRendererObj.drawString(
                  str,
                  -fontRendererObj.getStringWidth(str) / 2 + -fontRendererObj.getStringWidth(str2) / 2 + -fontRendererObj.getStringWidth(str3) / 2,
                  0.0F,
                  this.levelPrefixColor.getColorValue()
               );
               fontRendererObj.drawString(
                  str2,
                  fontRendererObj.getStringWidth(str) / 2 + -fontRendererObj.getStringWidth(str2) / 2 + -fontRendererObj.getStringWidth(str3) / 2,
                  0.0F,
                  this.levelAmountColor.getColorValue()
               );
               fontRendererObj.drawString(
                  str3,
                  fontRendererObj.getStringWidth(str) / 2 + fontRendererObj.getStringWidth(str2) / 2 + -fontRendererObj.getStringWidth(str3) / 2,
                  0.0F,
                  this.levelSuffixColor.getColorValue()
               );
               GlStateManager.enableLighting();
               GlStateManager.disableBlend();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.popMatrix();
            } else {
               this.renderStandingLevelTag(entityLivingBase, str, str2, str3, d, d2 - (entityLivingBase.isChild() ? entityLivingBase.height / 2.0F : 0.0), d3, b);
            }
         }
      }
   }

   public void sendWelcomeBack(String str) {
      Matcher matcher;
      if (this.welcomeBackGuildMembers.getBooleanValue() && (matcher = this.guildJoinPattern.matcher(str)).matches()) {
         String group = matcher.group("name");
         this.sendChatDelayed("/gc " + this.autoWb.auto2.getStringValue().replaceAll("%USER%", group), this.autoWb.send.getFloatValue());
      }

      if (this.welcomeBackFriends.getBooleanValue() && (matcher = this.friendJoinPattern.matcher(str)).matches()) {
         String group = matcher.group("name");
         this.sendChatDelayed("/msg " + group + " " + this.autoWb.auto2.getStringValue().replaceAll("%USER%", group), this.autoWb.send.getFloatValue());
      }
   }

   public HypixelCollectionMod() {
      super("Hypixel Collection");
      this.addEventHandler(StringEvent3.class, this::onStringEvent3);
      this.addEventHandler(SimpleEvent6.class, this::onSimpleEvent6);
      this.setIcon(new ResourceLocation("client/icons/mods/hypixel.png"), 21, 40);
      this.setDescription("A collection of mods made for the Hypixel Network.");
      this.setAuthors("2Pi (Auto GG, Auto Friend, Auto Tip)", "Sk1er (Auto GL, Hardcore Hearts, Levelhead)", "MayaqqDev (Auto WB)");
      this.setCategories(ModCategory.SERVER);
      this.setEnabledByDefault(true);
      new Timer().scheduleAtFixedRate(new TimerTask() {
         @Override
         public void run() {
            CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent6());
         }
      }, TimeUnit.SECONDS.toMillis(15L), TimeUnit.MINUTES.toMillis(1L));
   }

   public void onStringEvent3(StringEvent3 stringEvent3) {
      if (this.mc.getCurrentServerData() != null && this.mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel")) {
         String text = stringEvent3.getText();
         if (this.shouldHideMessage(text)) {
            stringEvent3.setFlag(true);
            return;
         }

         if (text.contains(this.eliminationMessages[0]) || text.contains(this.eliminationMessages[1])) {
            this.mc.thePlayer.worldObj.getWorldInfo().setHardcore(false);
         }

         if (this.hardcoreHearts.getBooleanValue() && this.bedDestructionPattern.matcher(text).matches()) {
            this.mc.thePlayer.worldObj.getWorldInfo().setHardcore(true);
         }

         if (this.autoGg.auto.getBooleanValue() && this.winMessages.stream().anyMatch(text::contains)) {
            this.sendChatDelayed("/achat " + this.autoGg.auto2.getStringValue(), this.autoGg.send.getFloatValue());
         }

         if (this.autoGl.auto.getBooleanValue() && text.startsWith("The game starts in 5 seconds!")) {
            this.sendChatDelayed("/achat " + this.autoGl.auto2.getStringValue(), this.autoGl.send.getFloatValue());
         }

         if (this.autoWb.auto.getBooleanValue()) {
            this.sendWelcomeBack(text);
         }

         Matcher matcher;
         if (this.autoFriend.getBooleanValue() && (matcher = this.friendRequestPattern.matcher(text.replace("\n", ""))).matches()) {
            String group = matcher.group("name");
            if (group.startsWith("[")) {
               group = group.substring(group.indexOf("] ") + 2);
            }

            this.sendChatDelayed("/friend " + this.autoFriendMode.getStringValue().toLowerCase() + " " + group, this.autoFriendDelay.getFloatValue());
         }
      }
   }

   public class AutoChatOption {
      public float minDelay = 0.0F;
      public float maxDelay = 15.0F;
      public String keyword;
      public Setting auto;
      public Setting send;
      public Setting auto2;
      public Setting filter;

      public HypixelCollectionMod.AutoChatOption withMinDelay(float f) {
         this.minDelay = f;
         return this;
      }

      public HypixelCollectionMod.AutoChatOption withMaxDelay(float f) {
         this.maxDelay = f;
         return this;
      }

      public AutoChatOption(String str) {
         this.keyword = str;
         this.auto = new Setting(HypixelCollectionMod.this, "Auto " + str).changeValue(false).withParents(HypixelCollectionMod.this.autoChatOptions);
         this.send = new Setting(HypixelCollectionMod.this, "Send " + str + " Message Delay")
            .setUnit("s")
            .changeValue(this.minDelay)
            .setRange(this.minDelay, this.maxDelay)
            .withHierarchyParents(this.auto);
         this.auto2 = new Setting(HypixelCollectionMod.this, "Auto " + str + " String")
            .changeValue(str.toLowerCase())
            .withHierarchyParents(this.auto)
            .setCustomizationLevel(SettingsLevel.MEDIUM);
         this.filter = new Setting(HypixelCollectionMod.this, "Filter " + str + " Messages").changeValue(false).withHierarchyParents(this.auto);
      }
   }
}
