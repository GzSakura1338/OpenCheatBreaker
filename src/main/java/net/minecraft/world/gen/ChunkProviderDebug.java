package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderDebug implements IChunkProvider {
   public static List<IBlockState> iBlockStateList = Lists.newArrayList();
   public static int intValue;
   public static int intValue2;
   public World world;

   public ChunkProviderDebug(World worldIn) {
      this.world = worldIn;
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      ChunkPrimer chunkPrimer = new ChunkPrimer();

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            int n = x * 16 + i;
            int n2 = z * 16 + j;
            chunkPrimer.setBlockState(i, 60, j, Blocks.barrier.getDefaultState());
            IBlockState findIBlockState2 = findIBlockState(n, n2);
            if (findIBlockState2 != null) {
               chunkPrimer.setBlockState(i, 70, j, findIBlockState2);
            }
         }
      }

      Chunk chunk = new Chunk(this.world, chunkPrimer, x, z);
      chunk.generateSkylightMap();
      BiomeGenBase[] loadBlockGeneratorData = this.world.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, x * 16, z * 16, 16, 16);
      byte[] biomeArray = chunk.getBiomeArray();

      for (int i = 0; i < biomeArray.length; i++) {
         biomeArray[i] = (byte)loadBlockGeneratorData[i].biomeID;
      }

      chunk.generateSkylightMap();
      return chunk;
   }

   public static IBlockState findIBlockState(int n, int n2) {
      IBlockState blockState = null;
      if (n > 0 && n2 > 0 && n % 2 != 0 && n2 % 2 != 0) {
         n /= 2;
         n2 /= 2;
         if (n <= intValue && n2 <= intValue2) {
            int n3 = MathHelper.abs_int(n * intValue + n2);
            if (n3 < iBlockStateList.size()) {
               blockState = iBlockStateList.get(n3);
            }
         }
      }

      return blockState;
   }

   @Override
   public boolean chunkExists(int x, int z) {
      return true;
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
   }

   @Override
   public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
      return false;
   }

   @Override
   public boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback) {
      return true;
   }

   @Override
   public void saveExtraData() {
   }

   @Override
   public boolean unloadQueuedChunks() {
      return false;
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public String makeString() {
      return "DebugLevelSource";
   }

   @Override
   public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      BiomeGenBase biomeGenForCoords = this.world.getBiomeGenForCoords(pos);
      return biomeGenForCoords.getSpawnableList(creatureType);
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      return null;
   }

   @Override
   public int getLoadedChunkCount() {
      return 0;
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }

   static {
      for (Block block : Block.blockRegistry) {
         iBlockStateList.addAll(block.getBlockState().getValidStates());
      }

      intValue = MathHelper.ceiling_float_int(MathHelper.sqrt_float(iBlockStateList.size()));
      intValue2 = MathHelper.ceiling_float_int((float)iBlockStateList.size() / intValue);
   }
}
