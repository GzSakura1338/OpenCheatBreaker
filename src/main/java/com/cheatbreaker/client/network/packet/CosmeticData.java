package com.cheatbreaker.client.network.packet;

import java.util.List;

public class CosmeticData {
   public long id;
   public float scale;
   public boolean equipped;
   public String texturePath;
   public String name;
   public String type;
   public List<String> extras;

   public long getId() {
      return this.id;
   }

   public float getScale() {
      return this.scale;
   }

   public String getName() {
      return this.name;
   }

   public List<String> getExtras() {
      return this.extras;
   }

   public String getTexturePath() {
      return this.texturePath;
   }

   public CosmeticData(long l, float f, boolean flag, String str, String str2, String str3, List<String> list) {
      this.id = l;
      this.scale = f;
      this.equipped = flag;
      this.texturePath = str;
      this.name = str2;
      this.type = str3;
      this.extras = list;
   }

   public boolean isEquipped() {
      return this.equipped;
   }

   public String getType() {
      return this.type;
   }
}
