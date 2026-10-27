package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenDoublePlant extends WorldGenerator {
   public EnumPlantType type;

   public void setPlantType(EnumPlantType enumPlantType) {
      this.type = enumPlantType;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      boolean flag = false;

      for (int i = 0; i < 64; i++) {
         BlockPos blockPos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.isAirBlock(blockPos) && (!worldIn.provider.getHasNoSky() || blockPos.getY() < 254) && Blocks.double_plant.canPlaceBlockAt(worldIn, blockPos)) {
            Blocks.double_plant.placeAt(worldIn, blockPos, this.type, 2);
            flag = true;
         }
      }

      return flag;
   }
}
