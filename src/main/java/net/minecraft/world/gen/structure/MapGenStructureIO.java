package net.minecraft.world.gen.structure;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapGenStructureIO {
   public static Logger logger = LogManager.getLogger();
   public static Map<String, Class<? extends StructureStart>> startNameToClassMap = Maps.newHashMap();
   public static Map<Class<? extends StructureStart>, String> startClassToNameMap = Maps.newHashMap();
   public static Map<String, Class<? extends StructureComponent>> componentNameToClassMap = Maps.newHashMap();
   public static Map<Class<? extends StructureComponent>, String> componentClassToNameMap = Maps.newHashMap();

   public static void registerStructure(Class<? extends StructureStart> startClass, String structureName) {
      startNameToClassMap.put(structureName, startClass);
      startClassToNameMap.put(startClass, structureName);
   }

   public static void registerStructureComponent(Class<? extends StructureComponent> componentClass, String componentName) {
      componentNameToClassMap.put(componentName, componentClass);
      componentClassToNameMap.put(componentClass, componentName);
   }

   public static String getStructureStartName(StructureStart start) {
      return startClassToNameMap.get(start.getClass());
   }

   public static String getStructureComponentName(StructureComponent component) {
      return componentClassToNameMap.get(component.getClass());
   }

   public static StructureStart getStructureStart(NBTTagCompound tagCompound, World worldIn) {
      StructureStart structureStart = null;

      try {
         Class clazz = startNameToClassMap.get(tagCompound.getString("id"));
         if (clazz != null) {
            structureStart = (StructureStart)clazz.newInstance();
         }
      } catch (Exception e) {
         logger.warn("Failed Start with id " + tagCompound.getString("id"));
         e.printStackTrace();
      }

      if (structureStart != null) {
         structureStart.readStructureComponentsFromNBT(worldIn, tagCompound);
      } else {
         logger.warn("Skipping Structure with id " + tagCompound.getString("id"));
      }

      return structureStart;
   }

   public static StructureComponent getStructureComponent(NBTTagCompound tagCompound, World worldIn) {
      StructureComponent structureComponent = null;

      try {
         Class clazz = componentNameToClassMap.get(tagCompound.getString("id"));
         if (clazz != null) {
            structureComponent = (StructureComponent)clazz.newInstance();
         }
      } catch (Exception e) {
         logger.warn("Failed Piece with id " + tagCompound.getString("id"));
         e.printStackTrace();
      }

      if (structureComponent != null) {
         structureComponent.readStructureBaseNBT(worldIn, tagCompound);
      } else {
         logger.warn("Skipping Piece with id " + tagCompound.getString("id"));
      }

      return structureComponent;
   }

   static {
      registerStructure(StructureMineshaftStart.class, "Mineshaft");
      registerStructure(MapGenVillage.Start.class, "Village");
      registerStructure(MapGenNetherBridge.Start.class, "Fortress");
      registerStructure(MapGenStronghold.Start.class, "Stronghold");
      registerStructure(MapGenScatteredFeature.Start.class, "Temple");
      registerStructure(StructureOceanMonument.StartMonument.class, "Monument");
      StructureMineshaftPieces.registerStructurePieces();
      StructureVillagePieces.registerVillagePieces();
      StructureNetherBridgePieces.registerNetherFortressPieces();
      StructureStrongholdPieces.registerStrongholdPieces();
      ComponentScatteredFeaturePieces.registerScatteredFeaturePieces();
      StructureOceanMonumentPieces.registerOceanMonumentPieces();
   }
}
