package net.optifine.reflect;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ReflectorForge {
   public static Object EVENT_RESULT_ALLOW = Reflector.getFieldValue(Reflector.Event_Result_ALLOW);
   public static Object EVENT_RESULT_DENY = Reflector.getFieldValue(Reflector.Event_Result_DENY);
   public static Object EVENT_RESULT_DEFAULT = Reflector.getFieldValue(Reflector.Event_Result_DEFAULT);

   public static void FMLClientHandler_trackBrokenTexture(ResourceLocation resourceLocation, String str) {
      if (!Reflector.FMLClientHandler_trackBrokenTexture.exists()) {
         Object obj = Reflector.call(Reflector.FMLClientHandler_instance);
         Reflector.call(obj, Reflector.FMLClientHandler_trackBrokenTexture, resourceLocation, str);
      }
   }

   public static void FMLClientHandler_trackMissingTexture(ResourceLocation resourceLocation) {
      if (!Reflector.FMLClientHandler_trackMissingTexture.exists()) {
         Object obj = Reflector.call(Reflector.FMLClientHandler_instance);
         Reflector.call(obj, Reflector.FMLClientHandler_trackMissingTexture, resourceLocation);
      }
   }

   public static void putLaunchBlackboard(String str, Object obj) {
      Map fieldValue = (Map)Reflector.getFieldValue(Reflector.Launch_blackboard);
      if (fieldValue != null) {
         fieldValue.put(str, obj);
      }
   }

   public static boolean renderFirstPersonHand(RenderGlobal renderGlobal, float f, int n) {
      return !Reflector.ForgeHooksClient_renderFirstPersonHand.exists()
         ? false
         : Reflector.callBoolean(Reflector.ForgeHooksClient_renderFirstPersonHand, renderGlobal, f, n);
   }

   public static InputStream getOptiFineResourceStream(String str) {
      if (!Reflector.OptiFineClassTransformer_instance.exists()) {
         return null;
      }

      Object fieldValue = Reflector.getFieldValue(Reflector.OptiFineClassTransformer_instance);
      if (fieldValue == null) {
         return null;
      }

      if (str.startsWith("/")) {
         str = str.substring(1);
      }

      byte[] bytes = (byte[])Reflector.call(fieldValue, Reflector.OptiFineClassTransformer_getOptiFineResource, str);
      return bytes == null ? null : new ByteArrayInputStream(bytes);
   }

   public static boolean blockHasTileEntity(IBlockState blockState) {
      Block block = blockState.getBlock();
      return !Reflector.ForgeBlock_hasTileEntity.exists() ? block.hasTileEntity() : Reflector.callBoolean(block, Reflector.ForgeBlock_hasTileEntity, blockState);
   }

   public static boolean isItemDamaged(ItemStack itemStack) {
      return !Reflector.ForgeItem_showDurabilityBar.exists()
         ? itemStack.isItemDamaged()
         : Reflector.callBoolean(itemStack.getItem(), Reflector.ForgeItem_showDurabilityBar, itemStack);
   }

   public static boolean armorHasOverlay(ItemArmor itemArmor, ItemStack itemStack) {
      int color = itemArmor.getColor(itemStack);
      return color != -1;
   }

   public static MapData getMapData(ItemMap itemMap, ItemStack itemStack, World world) {
      return Reflector.ForgeHooksClient.exists() ? ((ItemMap)itemStack.getItem()).getMapData(itemStack, world) : itemMap.getMapData(itemStack, world);
   }

   public static String[] getForgeModIds() {
      if (!Reflector.Loader.exists()) {
         return new String[0];
      }

      Object obj = Reflector.call(Reflector.Loader_instance);
      List list = (List)Reflector.call(obj, Reflector.Loader_getActiveModList);
      if (list == null) {
         return new String[0];
      }

      ArrayList list2 = new ArrayList();

      for (Object obj2 : list) {
         if (Reflector.ModContainer.isInstance(obj2)) {
            String callString = Reflector.callString(obj2, Reflector.ModContainer_getModId);
            if (callString != null) {
               list2.add(callString);
            }
         }
      }

      return (String[])list2.toArray(new String[list2.size()]);
   }

   public static boolean canEntitySpawn(EntityLiving entityLiving, World world, float f, float f2, float f3) {
      Object obj = Reflector.call(Reflector.ForgeEventFactory_canEntitySpawn, entityLiving, world, f, f2, f3);
      return obj == EVENT_RESULT_ALLOW || obj == EVENT_RESULT_DEFAULT && entityLiving.getCanSpawnHere() && entityLiving.isNotColliding();
   }

   public static boolean doSpecialSpawn(EntityLiving entityLiving, World world, float f, int n, float f2) {
      return Reflector.ForgeEventFactory_doSpecialSpawn.exists()
         ? Reflector.callBoolean(Reflector.ForgeEventFactory_doSpecialSpawn, entityLiving, world, f, n, f2)
         : false;
   }
}
