package com.cheatbreaker.client.emote;

import java.util.HashMap;
import java.util.Map;

public class EmoteRegistry {
   public static Map emotes = new HashMap();

   public static void register(EmoteDefinition emoteDefinition) {
      emotes.put(emoteDefinition.name, emoteDefinition);
   }

   public static EmoteDefinition resolve(String str) {
      if (str != null && str.contains(":")) {
         String[] split = str.split(":");
         EmoteDefinition emoteDefinition = (EmoteDefinition)emotes.get(split[0]);
         return emoteDefinition == null ? null : emoteDefinition.createVariant(split[1]);
      } else {
         EmoteDefinition emoteDefinition = (EmoteDefinition)emotes.get(str);
         return emoteDefinition == null ? null : emoteDefinition.createInstance();
      }
   }

   public static void registerDefaults() {
      register(new EmoteDefinition("best_mates", 11, true));
      register(new EmoteDefinition("boneless", 40, true));
      register(new EmoteDefinition("default", 139, true));
      register(new EmoteDefinition("disco_fever", 175, true));
      register(new EmoteDefinition("electro_shuffle", 169, true));
      register(new EmoteDefinition("floss", 32, true));
      register(new EmoteDefinition("fresh", 101, true));
      register(new EmoteDefinition("gangnam_style", 18, true));
      register(new EmoteDefinition("hype", 68, true));
      register(new EmoteDefinition("infinite_dab", 19, true));
      register(new EmoteDefinition("orange_justice", 130, true));
      register(new EmoteDefinition("skibidi", 16, true));
      register(new EmoteDefinition("squat_kick", 232, true));
      register(new StarPowerEmoteDefinition("star_power", 160, true));
      register(new EmoteDefinition("l_dance", 16, true));
      register(new EmoteDefinition("tidy", 104, true));
      register(new EmoteDefinition("free_flow", 158, true));
      register(new EmoteDefinition("shimmer", 156, true));
      register(new EmoteDefinition("get_funky", 172, true));
      register(new EmoteDefinition("gun_lean", 144, true));
      register(new EmoteDefinition("boy", 29, false));
      register(new EmoteDefinition("bow", 43, false));
      register(new EmoteDefinition("calculated", 33, false));
      register(new EmoteDefinition("chicken", 19, true));
      register(new EmoteDefinition("clapping", 15, true));
      register(new EmoteDefinition("club", 20, true));
      register(new EmoteDefinition("confused", 140, false));
      register(new CryingEmoteDefinition("crying", 27, true));
      register(new EmoteDefinition("dab", 23, false));
      register(new EmoteDefinition("facepalm", 104, false));
      register(new EmoteDefinition("fist", 53, false));
      register(new EmoteDefinition("laughing", 15, true));
      register(new EmoteDefinition("no", 30, false));
      register(new EmoteDefinition("pointing", 33, false));
      register(new PopcornEmoteDefinition("popcorn", 102, true));
      register(new PureSaltEmoteDefinition("pure_salt", 104, false));
      register(new RockPaperScissorsEmoteDefinition("rock_paper_scissors", 60, false));
      register(new EmoteDefinition("salute", 50, false));
      register(new EmoteDefinition("shrug", 50, false));
      register(new EmoteDefinition("t_pose", 80, true));
      register(new EmoteDefinition("thinking", 100, true));
      register(new TwerkEmoteDefinition("twerk", 14, true));
      register(new EmoteDefinition("wave", 40, false));
      register(new EmoteDefinition("yes", 23, false));
      register(new EmoteDefinition("bitchslap", FrameTimeConverter.framesToTicks(100), false));
      register(new EmoteDefinition("bongo_cat", FrameTimeConverter.framesToTicks(238), false));
      register(new EmoteDefinition("breathtaking", FrameTimeConverter.framesToTicks(154), false));
      register(new DisgustedEmoteDefinition("disgusted", FrameTimeConverter.framesToTicks(200), false));
      register(new EmoteDefinition("exhausted", FrameTimeConverter.framesToTicks(330), true));
      register(new EmoteDefinition("punch", FrameTimeConverter.framesToTicks(58), false));
      register(new SneezeEmoteDefinition("sneeze", FrameTimeConverter.framesToTicks(200), false));
      register(new EmoteDefinition("threatening", FrameTimeConverter.framesToTicks(70), false));
      register(new EmoteDefinition("woah", FrameTimeConverter.framesToTicks(66), false));
      register(new EmoteDefinition("villager_news_christmas", 125, false));
   }

   public static boolean isRegistered(String str) {
      if (str.contains(":")) {
         str = str.split(":")[0];
      }

      return emotes.containsKey(str);
   }
}
