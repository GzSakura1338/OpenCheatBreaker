package com.cheatbreaker.client.minimap;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

public class RadarContact {
   public float angle;
   public double distance;
   public int yOffset = 0;
   public float rotationFactor;
   public double x;
   public String name = "_";
   public String text = "";
   public Entity entity = null;
   public int intValue = -1;
   public int intValue2 = -1;
   public int[] intArray;
   public Item item = null;
   public double z;
   public int y;
   public int intValue3 = -1;
   public int intValue4 = -1;
   public MobType mobType;

   public void setIntValue(int n) {
      this.intValue = n;
   }

   public void setIntValue2(int n) {
      this.intValue2 = n;
   }

   public RadarContact(Entity entity, MobType mobType) {
      this.entity = entity;
      this.mobType = mobType;
   }

   public void setName(String str) {
      this.name = str;
   }

   public void setIntValue3(int n) {
      this.intValue3 = n;
   }

   public void updatePosition() {
      this.x = this.entity.posX;
      this.y = (int)this.entity.posY + this.yOffset;
      this.z = this.entity.posZ;
   }

   public void setIntValue4(int n) {
      this.intValue4 = n;
   }
}
