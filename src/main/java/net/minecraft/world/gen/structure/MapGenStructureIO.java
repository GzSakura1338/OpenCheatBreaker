package net.minecraft.world.gen.structure;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapGenStructureIO {
    public static Logger logger = LogManager.getLogger();
    public static Map configIdMap = new HashMap();
    public static Map configClassMap = new HashMap();
    public static Map pieceIdMap = new HashMap();
    public static Map pieceClassMap = new HashMap();

    public static void registerStructure(Class clazz, String str) {
        configIdMap.put(str, clazz);
        configClassMap.put(clazz, str);
    }

    public static void registerStructureComponent(Class clazz, String str) {
        pieceIdMap.put(str, clazz);
        pieceClassMap.put(clazz, str);
    }

    public static String getId(StructureStart structureStart) {
        return (String)configClassMap.get(structureStart.getClass());
    }

    public static String getId(StructureComponent structureComponent) {
        return (String)pieceClassMap.get(structureComponent.getClass());
    }

    public static StructureStart getGeneratorConfigFromNbt(NBTTagCompound nbttagCompound, World world) {
        StructureStart structureStart = null;

        try {
            Class clazz = (Class)configIdMap.get(nbttagCompound.getString("id"));
            if (clazz != null) {
                structureStart = (StructureStart)clazz.newInstance();
            }
        } catch (Exception e) {
            logger.warn("Failed Start with id " + nbttagCompound.getString("id"));
            e.printStackTrace();
        }

        if (structureStart != null) {
            structureStart.fromNbt(world, nbttagCompound);
        } else {
            logger.warn("Skipping Structure with id " + nbttagCompound.getString("id"));
        }

        return structureStart;
    }

    public static StructureComponent getStructurePieceFromNbt(NBTTagCompound nbttagCompound, World world) {
        StructureComponent structureComponent = null;

        try {
            Class clazz = (Class)pieceIdMap.get(nbttagCompound.getString("id"));
            if (clazz != null) {
                structureComponent = (StructureComponent)clazz.newInstance();
            }
        } catch (Exception e) {
            logger.warn("Failed Piece with id " + nbttagCompound.getString("id"));
            e.printStackTrace();
        }

        if (structureComponent != null) {
            structureComponent.fromNbt(world, nbttagCompound);
        } else {
            logger.warn("Skipping Piece with id " + nbttagCompound.getString("id"));
        }

        return structureComponent;
    }

    static {
        registerStructure(StructureMineshaftStart.class, "Mineshaft");
        registerStructure(net.minecraft.world.gen.structure.MapGenVillage.Start.class, "Village");
        registerStructure(net.minecraft.world.gen.structure.MapGenNetherBridge.Start.class, "Fortress");
        registerStructure(net.minecraft.world.gen.structure.MapGenStronghold.Start.class, "Stronghold");
        registerStructure(net.minecraft.world.gen.structure.MapGenScatteredFeature.Start.class, "Temple");
        StructureMineshaftPieces.registerStructurePieces();
        StructureVillagePieces.registerVillagePieces();
        StructureNetherBridgePieces.registerNetherFortressPieces();
        StructureStrongholdPieces.registerStrongholdPieces();
        ComponentScatteredFeaturePieces.registerScatteredFeaturePieces();
    }
}
