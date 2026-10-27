package net.optifine.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldNameable;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class TileEntityUtils {
   public static String getTileEntityName(IBlockAccess blockAccess, BlockPos blockPos) {
      TileEntity tileEntity = blockAccess.getTileEntity(blockPos);
      return getTileEntityName(tileEntity);
   }

   public static String getTileEntityName(TileEntity tileEntity) {
      if (!(tileEntity instanceof IWorldNameable)) {
         return null;
      }

      IWorldNameable worldNameable = (IWorldNameable)tileEntity;
      updateTileEntityName(tileEntity);
      return !worldNameable.hasCustomName() ? null : worldNameable.getName();
   }

   public static void updateTileEntityName(TileEntity tileEntity) {
      BlockPos pos = tileEntity.getPos();
      String tileEntityRawName = getTileEntityRawName(tileEntity);
      if (tileEntityRawName == null) {
         String serverTileEntityRawName = getServerTileEntityRawName(pos);
         serverTileEntityRawName = Config.normalize(serverTileEntityRawName);
         setTileEntityRawName(tileEntity, serverTileEntityRawName);
      }
   }

   public static String getServerTileEntityRawName(BlockPos blockPos) {
      TileEntity tileEntity = IntegratedServerUtils.getTileEntity(blockPos);
      return tileEntity == null ? null : getTileEntityRawName(tileEntity);
   }

   public static String getTileEntityRawName(TileEntity tileEntity) {
      if (tileEntity instanceof TileEntityBeacon) {
         return (String)Reflector.getFieldValue(tileEntity, Reflector.TileEntityBeacon_customName);
      }

      if (tileEntity instanceof TileEntityBrewingStand) {
         return (String)Reflector.getFieldValue(tileEntity, Reflector.TileEntityBrewingStand_customName);
      }

      if (tileEntity instanceof TileEntityEnchantmentTable) {
         return (String)Reflector.getFieldValue(tileEntity, Reflector.TileEntityEnchantmentTable_customName);
      }

      if (tileEntity instanceof TileEntityFurnace) {
         return (String)Reflector.getFieldValue(tileEntity, Reflector.TileEntityFurnace_customName);
      }

      if (tileEntity instanceof IWorldNameable) {
         IWorldNameable worldNameable = (IWorldNameable)tileEntity;
         if (worldNameable.hasCustomName()) {
            return worldNameable.getName();
         }
      }

      return null;
   }

   public static boolean setTileEntityRawName(TileEntity tileEntity, String str) {
      if (tileEntity instanceof TileEntityBeacon) {
         return Reflector.setFieldValue(tileEntity, Reflector.TileEntityBeacon_customName, str);
      } else if (tileEntity instanceof TileEntityBrewingStand) {
         return Reflector.setFieldValue(tileEntity, Reflector.TileEntityBrewingStand_customName, str);
      } else if (tileEntity instanceof TileEntityEnchantmentTable) {
         return Reflector.setFieldValue(tileEntity, Reflector.TileEntityEnchantmentTable_customName, str);
      } else if (tileEntity instanceof TileEntityFurnace) {
         return Reflector.setFieldValue(tileEntity, Reflector.TileEntityFurnace_customName, str);
      } else if (tileEntity instanceof TileEntityChest) {
         ((TileEntityChest)tileEntity).setCustomName(str);
         return true;
      } else if (tileEntity instanceof TileEntityDispenser) {
         ((TileEntityDispenser)tileEntity).setCustomName(str);
         return true;
      } else if (tileEntity instanceof TileEntityHopper) {
         ((TileEntityHopper)tileEntity).setCustomName(str);
         return true;
      } else {
         return false;
      }
   }
}
