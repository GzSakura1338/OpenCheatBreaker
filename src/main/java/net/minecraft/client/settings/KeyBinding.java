package net.minecraft.client.settings;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SimpleEvent17;
import com.cheatbreaker.client.mod.setting.Setting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IntHashMap;

public class KeyBinding implements Comparable<KeyBinding> {
   public static List<KeyBinding> keybindArray = Lists.newArrayList();
   public static IntHashMap<KeyBinding> hash = new IntHashMap<>();
   public static Set<String> keybindSet = Sets.newHashSet();
   public boolean pressed;
   public String keyDescription;
   public int keyCode;
   public int keyCodeDefault;
   public String keyCategory;
   public int pressTime;
   public boolean flag;
   public boolean flag2;
   public boolean flag3;
   public boolean flag4;
   public boolean flag5;
   public Setting setting;
   public boolean flag6;
   public boolean flag7;

   public static void onTick(int keyCode) {
      if (keyCode != 0) {
         for (KeyBinding keyBinding : keybindArray) {
            if (keyBinding.keyCode == keyCode) {
               keyBinding.pressTime++;
            }
         }
      }
   }

   public static void setKeyBindState(int keyCode, boolean pressed) {
      if (keyCode != 0) {
         for (KeyBinding keyBinding : keybindArray) {
            if (keyBinding.keyCode == keyCode) {
               keyBinding.pressed = pressed;
            }
         }
      }
   }

   public static void unPressAllKeys() {
      for (KeyBinding keyBinding : keybindArray) {
         keyBinding.unpressKey();
      }
   }

   public static void resetKeyBindingArrayAndHash() {
      hash.clearMap();

      for (KeyBinding keyBinding : keybindArray) {
         hash.addKey(keyBinding.keyCode, keyBinding);
      }

      if (CheatBreaker.getInstance() != null) {
         CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent17());
      }
   }

   public static Set<String> getKeybinds() {
      return keybindSet;
   }

   public KeyBinding(String description, int keyCode, String category) {
      this.keyDescription = description;
      this.keyCode = keyCode;
      this.keyCodeDefault = keyCode;
      this.keyCategory = category;
      keybindArray.add(this);
      hash.addKey(keyCode, this);
      keybindSet.add(category);
   }

   public boolean isKeyDown() {
      return this.pressed;
   }

   public String getKeyCategory() {
      return this.keyCategory;
   }

   public boolean isPressed() {
      if (this.pressTime == 0) {
         return false;
      }

      this.pressTime--;
      return true;
   }

   public void unpressKey() {
      this.pressTime = 0;
      this.pressed = false;
   }

   public String getKeyDescription() {
      return this.keyDescription;
   }

   public int getKeyCodeDefault() {
      return this.keyCodeDefault;
   }

   public int getKeyCode() {
      return this.keyCode;
   }

   public void setKeyCode(int keyCode) {
      this.keyCode = keyCode;
   }

   public int compareTo(KeyBinding keyBinding) {
      int compareTo = I18n.format(this.keyCategory).compareTo(I18n.format(keyBinding.keyCategory));
      if (compareTo == 0) {
         compareTo = I18n.format(this.keyDescription).compareTo(I18n.format(keyBinding.keyDescription));
      }

      return compareTo;
   }

   public void run() {
      this.process(this.flag2, this.flag4, this.flag6);
   }

   public KeyBinding(String str, int n, String str2, boolean flag) {
      this.pressed = false;
      this.keyDescription = str;
      this.keyCode = n;
      this.keyCodeDefault = n;
      this.keyCategory = str2;
      this.flag3 = flag;
      keybindArray.add(this);
      hash.addKey(n, this);
      keybindSet.add(str2);
   }

   public void run2() {
      if (this.setting != null) {
         int keyCode = this.getKeyCode();
         this.setting.setMouseBind(keyCode < 0);
         this.setting.changeValue(keyCode < 0 ? keyCode + 100 : keyCode);
         this.setting.setRequireShift(false);
         this.setting.setRequireCtrl(false);
         this.setting.setRequireAlt(false);
      }
   }

   public void process(boolean flag, boolean flag2, boolean flag3) {
      this.flag7 = flag;
      this.flag5 = flag2;
      this.flag = flag3;
      if (this.setting != null) {
         this.setting.setRequireShift(flag);
         this.setting.setRequireCtrl(flag2);
         this.setting.setRequireAlt(flag3);
      }
   }

   public boolean check() {
      return this.flag7 == this.flag2 && this.flag5 == this.flag4 && this.flag == this.flag6;
   }
}
