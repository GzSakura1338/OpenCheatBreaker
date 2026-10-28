package com.cheatbreaker.client.minimap;

import net.minecraft.block.Block;

public class BlockIdRepository {
    public static int airId = 0;
    public static int waterId = 9;
    public static int flowingWaterId = 8;
    public static int lavaId = 11;
    public static int flowingLavaId = 10;
    public static int iceId = 79;
    public static int grassId = 2;
    public static int leavesId = 18;
    public static int tallgrassId = 31;
    public static int reedsId = 83;
    public static int vineId = 106;
    public static int waterlilyId = 111;
    public static int leaves2Id = 161;
    public static int doublePlantId = 175;
    public static int webId = 30;
    public static int intValue = 55;
    public static int standingSignId = 63;
    public static int woodenDoorId = 64;
    public static int ladderId = 65;
    public static int wallSignId = 68;
    public static int ironDoorId = 71;
    public static int stoneButtonId = 77;
    public static int fenceId = 85;
    public static int fenceGateId = 107;
    public static int netherBrickFenceId = 113;
    public static int cobblestoneWallId = 139;
    public static int woodenButtonId = 143;
    public static Integer[] integerArray = new Integer[]{
        standingSignId,
        woodenDoorId,
        ladderId,
        wallSignId,
        ironDoorId,
        stoneButtonId,
        fenceId,
        vineId,
        fenceGateId,
        netherBrickFenceId,
        cobblestoneWallId,
        woodenButtonId
    };

    public static void loadBlockIds() {
        airId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:air"));
        waterId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:water"));
        flowingWaterId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:flowing_water"));
        lavaId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:lava"));
        flowingLavaId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:flowing_lava"));
        iceId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:ice"));
        grassId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:grass"));
        leavesId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:leaves"));
        tallgrassId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:tallgrass"));
        reedsId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:reeds"));
        vineId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:vine"));
        waterlilyId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:waterlily"));
        leaves2Id = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:leaves2"));
        doublePlantId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:double_plant"));
        webId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:web"));
        standingSignId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:standing_sign"));
        woodenDoorId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:wooden_door"));
        ladderId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:ladder"));
        wallSignId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:wall_sign"));
        ironDoorId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:iron_door"));
        stoneButtonId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:stone_button"));
        fenceId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:fence"));
        fenceGateId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:fence_gate"));
        netherBrickFenceId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:nether_brick_fence"));
        cobblestoneWallId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:cobblestone_wall"));
        woodenButtonId = Block.blockRegistry.getIDForObject(Block.blockRegistry.getObject("minecraft:wooden_button"));
        integerArray = new Integer[]{
            standingSignId,
            woodenDoorId,
            ladderId,
            wallSignId,
            ironDoorId,
            stoneButtonId,
            fenceId,
            vineId,
            fenceGateId,
            netherBrickFenceId,
            cobblestoneWallId,
            woodenButtonId
        };
    }
}
