package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class EntityBlockDustFX extends EntityDiggingFX {
    public EntityBlockDustFX(World world, double d, double d2, double d3, double d4, double d5, double d6, Block block, int n) {
        super(world, d, d2, d3, d4, d5, d6, block, n);
        this.motionX = d4;
        this.motionY = d5;
        this.motionZ = d6;
    }
}
