package net.minecraft.world.biome;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenSpikes;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeEndDecorator extends BiomeDecorator {
   public WorldGenerator spikeGen = new WorldGenSpikes(Blocks.end_stone);

   @Override
   public void genDecorations(BiomeGenBase biomeGenBaseIn) {
      this.generateOres();
      if (this.randomGenerator.nextInt(5) == 0) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         this.spikeGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.startPos.add(n, 0, n2)));
      }

      if (this.startPos.getX() == 0 && this.startPos.getZ() == 0) {
         EntityDragon entityDragon = new EntityDragon(this.currentWorld);
         entityDragon.setLocationAndAngles(0.0, 128.0, 0.0, this.randomGenerator.nextFloat() * 360.0F, 0.0F);
         this.currentWorld.spawnEntityInWorld(entityDragon);
      }
   }
}
