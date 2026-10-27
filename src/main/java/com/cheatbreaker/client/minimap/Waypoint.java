package com.cheatbreaker.client.minimap;

import java.io.Serializable;
import java.util.TreeSet;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class Waypoint implements Serializable, Comparable {
   public String imageSuffix = "";
   public String world = "";
   public boolean deathpoint = false;
   public static final long serialVersionUID = 8136790917447997951L;
   public boolean forced;
   public boolean unusedFlag;
   public String world_6 = "";
   public TreeSet<Integer> dimensions = new TreeSet<>();
   public boolean inWorld = true;
   public boolean inDimension = true;
   public float red = 0.0F;
   public float green = 1.0F;
   public float blue = 0.0F;
   public String name;
   public int x;
   public boolean fromServer;
   public int z;
   public int y;
   public boolean enabled;

   public int getY() {
      return this.y;
   }

   public int getRgbColor() {
      return -16777216 + ((int)(this.red * 255.0F) << 16) + ((int)(this.green * 255.0F) << 8) + (int)(this.blue * 255.0F);
   }

   @Override
   public int compareTo(Object obj) {
      double distanceSqToEntity = this.getDistanceSqToEntity(Minecraft.getMinecraft().thePlayer);
      double distanceSqToEntity2 = ((Waypoint)obj).getDistanceSqToEntity(Minecraft.getMinecraft().thePlayer);
      return Double.compare(distanceSqToEntity, distanceSqToEntity2);
   }

   public boolean isActive() {
      return this.enabled && this.inWorld && this.inDimension;
   }

   public Waypoint(
      String str, int n, int n2, int n3, boolean flag, float f, float f2, float f3, String str2, String str3, TreeSet<Integer> treeSet
   ) {
      this.name = str;
      this.x = n;
      this.z = n2;
      this.y = n3;
      this.enabled = flag;
      this.red = f;
      this.green = f2;
      this.blue = f3;
      this.imageSuffix = str2;
      this.world = str3;
      this.dimensions = treeSet;
   }

   public void setX(int n) {
      this.x = Minecraft.getMinecraft().thePlayer.dimension == -1 ? n * 8 : n;
   }

   public double getDistanceSqToEntity(Entity entity) {
      double d = this.getX() - entity.posX;
      double d2 = this.getY() - entity.posY;
      double d3 = this.getZ() - entity.posZ;
      return d * d + d2 * d2 + d3 * d3;
   }

   public void setY(int n) {
      this.y = n;
   }

   public Waypoint(
      String str,
      int n,
      int n2,
      int n3,
      boolean flag,
      float f,
      float f2,
      float f3,
      String str2,
      String str3,
      TreeSet<Integer> treeSet,
      boolean flag2,
      boolean flag3
   ) {
      this(str, n, n2, n3, flag, f, f2, f3, str2, str3, treeSet);
      this.deathpoint = flag2;
      this.unusedFlag = flag3;
      if (!treeSet.contains(Minecraft.getMinecraft().thePlayer.dimension)) {
         this.inDimension = false;
      }
   }

   public Waypoint(
      String str,
      int n,
      int n2,
      int n3,
      boolean flag,
      float f,
      float f2,
      float f3,
      String str2,
      String str3,
      TreeSet<Integer> treeSet,
      boolean flag2
   ) {
      this(str, n, n2, n3, flag, f, f2, f3, str2, str3, treeSet);
      this.deathpoint = flag2;
      if (!treeSet.contains(Minecraft.getMinecraft().thePlayer.dimension)) {
         this.inDimension = false;
      }
   }

   public void setZ(int n) {
      this.z = Minecraft.getMinecraft().thePlayer.dimension == -1 ? n * 8 : n;
   }

   public int getZ() {
      return Minecraft.getMinecraft().thePlayer.dimension == -1 ? this.z / 8 : this.z;
   }

   public int getX() {
      return Minecraft.getMinecraft().thePlayer.dimension == -1 ? this.x / 8 : this.x;
   }
}
