package com.cheatbreaker.client.minimap;

import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityWaypointContainer extends Entity {
   public ArrayList<Waypoint> waypoints = new ArrayList<>();
   public final boolean unusedFlag = false;

   @Override
   public float getBrightness(float f) {
      return 1.0F;
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound nbttagCompound) {
   }

   @Override
   public int getBrightnessForRender(float f) {
      return 15728880;
   }

   @Override
   public void entityInit() {
   }

   @Override
   public boolean isInRangeToRender3d(double d, double d2, double d3) {
      return true;
   }

   public EntityWaypointContainer(World world) {
      super(world);
      this.ignoreFrustumCheck = true;
   }

   public void addWaypoint(Waypoint waypoint) {
      this.waypoints.add(waypoint);
   }

   public void removeWaypoint(Waypoint waypoint) {
      this.waypoints.remove(waypoint);
   }

   public void sortWaypoints() {
      Collections.sort(this.waypoints, Collections.reverseOrder());
   }

   @Override
   public void onUpdate() {
      EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
      if (thePlayer != null) {
         this.setPosition(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
      }

      this.sortWaypoints();
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound nbttagCompound) {
   }
}
