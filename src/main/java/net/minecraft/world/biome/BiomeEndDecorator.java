package net.minecraft.world.biome;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenSpikes;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeEndDecorator extends BiomeDecorator {
    public WorldGenerator spikeGen = new WorldGenSpikes(Blocks.end_stone);

    @Override
    public void genDecorations(BiomeGenBase biomeGenBase) {
        this.generateOres();
        if (this.randomGenerator.nextInt(5) == 0) {
            int n = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int n2 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int topSolidOrLiquidBlock = this.currentWorld.getTopSolidOrLiquidBlock(n, n2);
            this.spikeGen.generate(this.currentWorld, this.randomGenerator, n, topSolidOrLiquidBlock, n2);
        }

        if (this.chunk_X == 0 && this.chunk_Z == 0) {
            EntityDragon entityDragon = new EntityDragon(this.currentWorld);
            entityDragon.setLocationAndAngles(0.0, 128.0, 0.0, this.randomGenerator.nextFloat() * 360.0F, 0.0F);
            this.currentWorld.spawnEntityInWorld(entityDragon);
        }
    }
}
