package com.cheatbreaker.client.minimap;

public abstract class AbstractVoxelMap implements IVoxelMap {
   public static AbstractVoxelMap instance = null;

   public static AbstractVoxelMap getInstance() {
      return instance;
   }
}
