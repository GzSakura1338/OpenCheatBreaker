package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockSandStone.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class StructureVillagePieces {
   public static void registerVillagePieces() {
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.House1.class, "ViBH");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Field1.class, "ViDF");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Field2.class, "ViF");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Torch.class, "ViL");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Hall.class, "ViPH");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.House4Garden.class, "ViSH");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.WoodHut.class, "ViSmH");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Church.class, "ViST");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.House2.class, "ViS");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Start.class, "ViStart");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Path.class, "ViSR");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.House3.class, "ViTRH");
      MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Well.class, "ViW");
   }

   public static List<StructureVillagePieces.PieceWeight> getStructureVillageWeightedPieceList(Random random, int size) {
      ArrayList newArrayList = Lists.newArrayList();
      newArrayList.add(
         new StructureVillagePieces.PieceWeight(
            StructureVillagePieces.House4Garden.class, 4, MathHelper.getRandomIntegerInRange(random, 2 + size, 4 + size * 2)
         )
      );
      newArrayList.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Church.class, 20, MathHelper.getRandomIntegerInRange(random, 0 + size, 1 + size)));
      newArrayList.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House1.class, 20, MathHelper.getRandomIntegerInRange(random, 0 + size, 2 + size)));
      newArrayList.add(
         new StructureVillagePieces.PieceWeight(StructureVillagePieces.WoodHut.class, 3, MathHelper.getRandomIntegerInRange(random, 2 + size, 5 + size * 3))
      );
      newArrayList.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Hall.class, 15, MathHelper.getRandomIntegerInRange(random, 0 + size, 2 + size)));
      newArrayList.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Field1.class, 3, MathHelper.getRandomIntegerInRange(random, 1 + size, 4 + size)));
      newArrayList.add(
         new StructureVillagePieces.PieceWeight(StructureVillagePieces.Field2.class, 3, MathHelper.getRandomIntegerInRange(random, 2 + size, 4 + size * 2))
      );
      newArrayList.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House2.class, 15, MathHelper.getRandomIntegerInRange(random, 0, 1 + size)));
      newArrayList.add(
         new StructureVillagePieces.PieceWeight(StructureVillagePieces.House3.class, 8, MathHelper.getRandomIntegerInRange(random, 0 + size, 3 + size * 2))
      );
      Iterator iterator = newArrayList.iterator();

      while (iterator.hasNext()) {
         if (((StructureVillagePieces.PieceWeight)iterator.next()).villagePiecesLimit == 0) {
            iterator.remove();
         }
      }

      return newArrayList;
   }

   public static int getTotalWeight(List<StructureVillagePieces.PieceWeight> list) {
      boolean flag = false;
      int n = 0;

      for (StructureVillagePieces.PieceWeight pieceWeight : list) {
         if (pieceWeight.villagePiecesLimit > 0 && pieceWeight.villagePiecesSpawned < pieceWeight.villagePiecesLimit) {
            flag = true;
         }

         n += pieceWeight.villagePieceWeight;
      }

      return flag ? n : -1;
   }

   public static StructureVillagePieces.Village createPiece(
      StructureVillagePieces.Start start,
      StructureVillagePieces.PieceWeight weight,
      List<StructureComponent> list,
      Random rand,
      int n,
      int n2,
      int n3,
      EnumFacing facing,
      int n4
   ) {
      Class villagePieceClass = weight.villagePieceClass;
      StructureVillagePieces.Village village = null;
      if (villagePieceClass == StructureVillagePieces.House4Garden.class) {
         village = StructureVillagePieces.House4Garden.create(start, list, rand, n, n2, n3, facing, n4);
      } else if (villagePieceClass == StructureVillagePieces.Church.class) {
         village = StructureVillagePieces.Church.create(start, list, rand, n, n2, n3, facing, n4);
      } else if (villagePieceClass == StructureVillagePieces.House1.class) {
         village = StructureVillagePieces.House1.create(start, list, rand, n, n2, n3, facing, n4);
      } else if (villagePieceClass == StructureVillagePieces.WoodHut.class) {
         village = StructureVillagePieces.WoodHut.create(start, list, rand, n, n2, n3, facing, n4);
      } else if (villagePieceClass == StructureVillagePieces.Hall.class) {
         village = StructureVillagePieces.Hall.create(start, list, rand, n, n2, n3, facing, n4);
      } else if (villagePieceClass == StructureVillagePieces.Field1.class) {
         village = StructureVillagePieces.Field1.create(start, list, rand, n, n2, n3, facing, n4);
      } else if (villagePieceClass == StructureVillagePieces.Field2.class) {
         village = StructureVillagePieces.Field2.create(start, list, rand, n, n2, n3, facing, n4);
      } else if (villagePieceClass == StructureVillagePieces.House2.class) {
         village = StructureVillagePieces.House2.create(start, list, rand, n, n2, n3, facing, n4);
      } else if (villagePieceClass == StructureVillagePieces.House3.class) {
         village = StructureVillagePieces.House3.create(start, list, rand, n, n2, n3, facing, n4);
      }

      return village;
   }

   public static StructureVillagePieces.Village pickPiece(
      StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2, int n3, EnumFacing facing, int n4
   ) {
      int totalWeight = getTotalWeight(start.structureVillageWeightedPieceList);
      if (totalWeight <= 0) {
         return null;
      }

      int n5 = 0;

      while (n5 < 5) {
         n5++;
         int nextInt = rand.nextInt(totalWeight);

         for (StructureVillagePieces.PieceWeight pieceWeight : start.structureVillageWeightedPieceList) {
            nextInt -= pieceWeight.villagePieceWeight;
            if (nextInt < 0) {
               if (!pieceWeight.canSpawnMoreVillagePiecesOfType(n4)
                  || pieceWeight == start.structVillagePieceWeight && start.structureVillageWeightedPieceList.size() > 1) {
                  break;
               }

               StructureVillagePieces.Village createPiece2 = createPiece(start, pieceWeight, list, rand, n, n2, n3, facing, n4);
               if (createPiece2 != null) {
                  pieceWeight.villagePiecesSpawned++;
                  start.structVillagePieceWeight = pieceWeight;
                  if (!pieceWeight.canSpawnMoreVillagePieces()) {
                     start.structureVillageWeightedPieceList.remove(pieceWeight);
                  }

                  return createPiece2;
               }
            }
         }
      }

      StructureBoundingBox structureBoundingBox = StructureVillagePieces.Torch.create(start, list, rand, n, n2, n3, facing);
      return structureBoundingBox != null ? new StructureVillagePieces.Torch(start, n4, rand, structureBoundingBox, facing) : null;
   }

   public static StructureComponent generate(
      StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2, int n3, EnumFacing facing, int n4
   ) {
      if (n4 > 50) {
         return null;
      }

      if (Math.abs(n - start.getBoundingBox().minX) <= 112 && Math.abs(n3 - start.getBoundingBox().minZ) <= 112) {
         StructureVillagePieces.Village pickPiece2 = pickPiece(start, list, rand, n, n2, n3, facing, n4 + 1);
         if (pickPiece2 != null) {
            int n5 = (pickPiece2.boundingBox.minX + pickPiece2.boundingBox.maxX) / 2;
            int n6 = (pickPiece2.boundingBox.minZ + pickPiece2.boundingBox.maxZ) / 2;
            int n7 = pickPiece2.boundingBox.maxX - pickPiece2.boundingBox.minX;
            int n8 = pickPiece2.boundingBox.maxZ - pickPiece2.boundingBox.minZ;
            int n9 = n7 > n8 ? n7 : n8;
            if (start.getWorldChunkManager().areBiomesViable(n5, n6, n9 / 2 + 4, MapGenVillage.villageSpawnBiomes)) {
               list.add(pickPiece2);
               start.structureComponentList.add(pickPiece2);
               return pickPiece2;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static StructureComponent createPath(
      StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2, int n3, EnumFacing facing, int n4
   ) {
      if (n4 > 3 + start.terrainType) {
         return null;
      }

      if (Math.abs(n - start.getBoundingBox().minX) <= 112 && Math.abs(n3 - start.getBoundingBox().minZ) <= 112) {
         StructureBoundingBox findStructureBoundingBox = StructureVillagePieces.Path.findStructureBoundingBox(start, list, rand, n, n2, n3, facing);
         if (findStructureBoundingBox != null && findStructureBoundingBox.minY > 10) {
            StructureVillagePieces.Path path = new StructureVillagePieces.Path(start, n4, rand, findStructureBoundingBox, facing);
            int n5 = (path.boundingBox.minX + path.boundingBox.maxX) / 2;
            int n6 = (path.boundingBox.minZ + path.boundingBox.maxZ) / 2;
            int n7 = path.boundingBox.maxX - path.boundingBox.minX;
            int n8 = path.boundingBox.maxZ - path.boundingBox.minZ;
            int n9 = n7 > n8 ? n7 : n8;
            if (start.getWorldChunkManager().areBiomesViable(n5, n6, n9 / 2 + 4, MapGenVillage.villageSpawnBiomes)) {
               list.add(path);
               start.structureComponentList2.add(path);
               return path;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static class Church extends StructureVillagePieces.Village {
      public Church() {
      }

      public Church(StructureVillagePieces.Start start, int n, Random rand, StructureBoundingBox structureBoundingBox, EnumFacing facing) {
         super(start, n);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox;
      }

      public static StructureVillagePieces.Church create(
         StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2, int n3, EnumFacing facing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 5, 12, 9, facing);
         return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureVillagePieces.Church(start, n4, rand, componentToAddBoundingBox, facing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.hPos < 0) {
            this.hPos = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.hPos < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.hPos - this.boundingBox.maxY + 12 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 3, 3, 7, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 3, 9, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 0, 0, 3, 0, 8, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 1, 0, 3, 10, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 10, 3, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 10, 3, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 4, 0, 4, 7, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 0, 4, 4, 4, 7, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 1, 8, 3, 4, 8, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 5, 4, 3, 10, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 5, 5, 3, 5, 7, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 9, 0, 4, 9, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 4, 0, 4, 4, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 0, 11, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 11, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 2, 11, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 2, 11, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 1, 1, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 1, 1, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 2, 1, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 3, 1, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 3, 1, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 1, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 1, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 3, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 1)), 1, 2, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 0)), 3, 2, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 4, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 4, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 6, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 7, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 4, 6, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 4, 7, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 6, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 7, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 6, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 7, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 3, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 4, 3, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 3, 8, structureBoundingBoxIn);
         this.setBlockState(
            worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.getOpposite()), 2, 4, 7, structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.rotateY()), 1, 4, 6, structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.rotateYCCW()), 3, 4, 6, structureBoundingBoxIn
         );
         this.setBlockState(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 2, 4, 5, structureBoundingBoxIn);
         int metadataWithOffset = this.getMetadataWithOffset(Blocks.ladder, 4);

         for (int i = 1; i <= 9; i++) {
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 3, i, 3, structureBoundingBoxIn);
         }

         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, 2, 0, structureBoundingBoxIn);
         this.placeDoorCurrentPosition(
            worldIn, structureBoundingBoxIn, randomIn, 2, 1, 0, EnumFacing.getHorizontal(this.getMetadataWithOffset(Blocks.oak_door, 1))
         );
         if (this.getBlockStateFromPos(worldIn, 2, 0, -1, structureBoundingBoxIn).getBlock().getMaterial() == Material.air
            && this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).getBlock().getMaterial() != Material.air) {
            this.setBlockState(
               worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 0, -1, structureBoundingBoxIn
            );
         }

         for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 5; j++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j, 12, i, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), j, -1, i, structureBoundingBoxIn);
            }
         }

         this.spawnVillagers(worldIn, structureBoundingBoxIn, 2, 1, 2, 1);
         return true;
      }

      @Override
      public int computeInt(int n, int n2) {
         return 2;
      }
   }

   public static class Field1 extends StructureVillagePieces.Village {
      public Block cropTypeA;
      public Block cropTypeB;
      public Block cropTypeC;
      public Block cropTypeD;

      public Field1() {
      }

      public Field1(StructureVillagePieces.Start start, int n, Random rand, StructureBoundingBox structureBoundingBox, EnumFacing facing) {
         super(start, n);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox;
         this.cropTypeA = this.getCrop(rand);
         this.cropTypeB = this.getCrop(rand);
         this.cropTypeC = this.getCrop(rand);
         this.cropTypeD = this.getCrop(rand);
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("CA", Block.blockRegistry.getIDForObject(this.cropTypeA));
         tagCompound.setInteger("CB", Block.blockRegistry.getIDForObject(this.cropTypeB));
         tagCompound.setInteger("CC", Block.blockRegistry.getIDForObject(this.cropTypeC));
         tagCompound.setInteger("CD", Block.blockRegistry.getIDForObject(this.cropTypeD));
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.cropTypeA = Block.getBlockById(tagCompound.getInteger("CA"));
         this.cropTypeB = Block.getBlockById(tagCompound.getInteger("CB"));
         this.cropTypeC = Block.getBlockById(tagCompound.getInteger("CC"));
         this.cropTypeD = Block.getBlockById(tagCompound.getInteger("CD"));
      }

      public Block getCrop(Random rand) {
         switch (rand.nextInt(5)) {
            case 0:
               return Blocks.carrots;
            case 1:
               return Blocks.potatoes;
            default:
               return Blocks.wheat;
         }
      }

      public static StructureVillagePieces.Field1 create(
         StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2, int n3, EnumFacing facing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 13, 4, 9, facing);
         return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureVillagePieces.Field1(start, n4, rand, componentToAddBoundingBox, facing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.hPos < 0) {
            this.hPos = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.hPos < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.hPos - this.boundingBox.maxY + 4 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 12, 4, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 2, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 0, 1, 5, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 0, 1, 8, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 0, 1, 11, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 0, 6, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 0, 0, 12, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 11, 0, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 8, 11, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 0, 1, 3, 0, 7, Blocks.water.getDefaultState(), Blocks.water.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 0, 1, 9, 0, 7, Blocks.water.getDefaultState(), Blocks.water.getDefaultState(), false);

         for (int i = 1; i <= 7; i++) {
            this.setBlockState(worldIn, this.cropTypeA.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 1, 1, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, this.cropTypeA.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 2, 1, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, this.cropTypeB.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 4, 1, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, this.cropTypeB.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 5, 1, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, this.cropTypeC.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 7, 1, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, this.cropTypeC.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 8, 1, i, structureBoundingBoxIn);
            this.setBlockState(
               worldIn, this.cropTypeD.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 10, 1, i, structureBoundingBoxIn
            );
            this.setBlockState(
               worldIn, this.cropTypeD.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 11, 1, i, structureBoundingBoxIn
            );
         }

         for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 13; j++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j, 4, i, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.dirt.getDefaultState(), j, -1, i, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Field2 extends StructureVillagePieces.Village {
      public Block cropTypeA;
      public Block cropTypeB;

      public Field2() {
      }

      public Field2(StructureVillagePieces.Start start, int n, Random rand, StructureBoundingBox structureBoundingBox, EnumFacing facing) {
         super(start, n);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox;
         this.cropTypeA = this.getCrop(rand);
         this.cropTypeB = this.getCrop(rand);
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("CA", Block.blockRegistry.getIDForObject(this.cropTypeA));
         tagCompound.setInteger("CB", Block.blockRegistry.getIDForObject(this.cropTypeB));
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.cropTypeA = Block.getBlockById(tagCompound.getInteger("CA"));
         this.cropTypeB = Block.getBlockById(tagCompound.getInteger("CB"));
      }

      public Block getCrop(Random rand) {
         switch (rand.nextInt(5)) {
            case 0:
               return Blocks.carrots;
            case 1:
               return Blocks.potatoes;
            default:
               return Blocks.wheat;
         }
      }

      public static StructureVillagePieces.Field2 create(
         StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2, int n3, EnumFacing facing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 7, 4, 9, facing);
         return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureVillagePieces.Field2(start, n4, rand, componentToAddBoundingBox, facing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.hPos < 0) {
            this.hPos = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.hPos < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.hPos - this.boundingBox.maxY + 4 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 6, 4, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 2, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 0, 1, 5, 0, 7, Blocks.farmland.getDefaultState(), Blocks.farmland.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 0, 6, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 5, 0, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 8, 5, 0, 8, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 0, 1, 3, 0, 7, Blocks.water.getDefaultState(), Blocks.water.getDefaultState(), false);

         for (int i = 1; i <= 7; i++) {
            this.setBlockState(worldIn, this.cropTypeA.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 1, 1, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, this.cropTypeA.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 2, 1, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, this.cropTypeB.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 4, 1, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, this.cropTypeB.getStateFromMeta(MathHelper.getRandomIntegerInRange(randomIn, 2, 7)), 5, 1, i, structureBoundingBoxIn);
         }

         for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 7; j++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j, 4, i, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.dirt.getDefaultState(), j, -1, i, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Hall extends StructureVillagePieces.Village {
      public Hall() {
      }

      public Hall(StructureVillagePieces.Start start, int n, Random rand, StructureBoundingBox structureBoundingBox, EnumFacing facing) {
         super(start, n);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox;
      }

      public static StructureVillagePieces.Hall create(
         StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2, int n3, EnumFacing facing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 9, 7, 11, facing);
         return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureVillagePieces.Hall(start, n4, rand, componentToAddBoundingBox, facing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.hPos < 0) {
            this.hPos = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.hPos < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.hPos - this.boundingBox.maxY + 7 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 7, 4, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 6, 8, 4, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 6, 8, 0, 10, Blocks.dirt.getDefaultState(), Blocks.dirt.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, 0, 6, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 6, 2, 1, 10, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 1, 6, 8, 1, 10, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 10, 7, 1, 10, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 7, 0, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 3, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 0, 0, 8, 3, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 0, 0, 7, 1, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 0, 5, 7, 1, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 7, 3, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 5, 7, 3, 5, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 1, 8, 4, 1, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 4, 8, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 2, 8, 5, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 0, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 0, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 4, 3, structureBoundingBoxIn);
         int metadataWithOffset = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
         int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.oak_stairs, 2);

         for (int i = -1; i <= 2; i++) {
            for (int j = 0; j <= 8; j++) {
               this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset), j, 4 + i, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset2), j, 4 + i, 5 - i, structureBoundingBoxIn);
            }
         }

         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 0, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 0, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 8, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 8, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 3, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 5, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 6, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 2, 1, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.wooden_pressure_plate.getDefaultState(), 2, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 1, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.oak_stairs, 3)), 2, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.oak_stairs, 1)), 1, 1, 3, structureBoundingBoxIn);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 0, 1, 7, 0, 3, Blocks.double_stone_slab.getDefaultState(), Blocks.double_stone_slab.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.double_stone_slab.getDefaultState(), 6, 1, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.double_stone_slab.getDefaultState(), 6, 1, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 2, 3, 1, structureBoundingBoxIn);
         this.placeDoorCurrentPosition(
            worldIn, structureBoundingBoxIn, randomIn, 2, 1, 0, EnumFacing.getHorizontal(this.getMetadataWithOffset(Blocks.oak_door, 1))
         );
         if (this.getBlockStateFromPos(worldIn, 2, 0, -1, structureBoundingBoxIn).getBlock().getMaterial() == Material.air
            && this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).getBlock().getMaterial() != Material.air) {
            this.setBlockState(
               worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 0, -1, structureBoundingBoxIn
            );
         }

         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 6, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 6, 2, 5, structureBoundingBoxIn);
         this.setBlockState(
            worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.getOpposite()), 6, 3, 4, structureBoundingBoxIn
         );
         this.placeDoorCurrentPosition(
            worldIn, structureBoundingBoxIn, randomIn, 6, 1, 5, EnumFacing.getHorizontal(this.getMetadataWithOffset(Blocks.oak_door, 1))
         );

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j, 7, i, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), j, -1, i, structureBoundingBoxIn);
            }
         }

         this.spawnVillagers(worldIn, structureBoundingBoxIn, 4, 1, 2, 2);
         return true;
      }

      @Override
      public int computeInt(int n, int n2) {
         return n == 0 ? 4 : super.computeInt(n, n2);
      }
   }

   public static class House1 extends StructureVillagePieces.Village {
      public House1() {
      }

      public House1(StructureVillagePieces.Start start, int n, Random rand, StructureBoundingBox structureBoundingBox, EnumFacing facing) {
         super(start, n);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox;
      }

      public static StructureVillagePieces.House1 create(
         StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2, int n3, EnumFacing facing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 9, 9, 6, facing);
         return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureVillagePieces.House1(start, n4, rand, componentToAddBoundingBox, facing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.hPos < 0) {
            this.hPos = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.hPos < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.hPos - this.boundingBox.maxY + 9 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 7, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 8, 0, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 0, 8, 5, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 1, 8, 6, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 7, 2, 8, 7, 3, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         int metadataWithOffset = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
         int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.oak_stairs, 2);

         for (int i = -1; i <= 2; i++) {
            for (int j = 0; j <= 8; j++) {
               this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset), j, 6 + i, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset2), j, 6 + i, 5 - i, structureBoundingBoxIn);
            }
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 1, 5, 8, 1, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 1, 0, 8, 1, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 1, 0, 7, 1, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 4, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 5, 0, 4, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 2, 5, 8, 4, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 2, 0, 8, 4, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 1, 0, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 5, 7, 4, 5, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 2, 1, 8, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 7, 4, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 4, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 5, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 6, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 4, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 5, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 6, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 3, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 3, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 3, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 5, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 6, 2, 5, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 1, 7, 4, 1, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 4, 7, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 4, 7, 3, 4, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 7, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.oak_stairs, 0)), 7, 1, 3, structureBoundingBoxIn);
         int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
         this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset3), 6, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset3), 5, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset3), 4, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset3), 3, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 6, 1, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.wooden_pressure_plate.getDefaultState(), 6, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 1, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.wooden_pressure_plate.getDefaultState(), 4, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.crafting_table.getDefaultState(), 7, 1, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 1, 2, 0, structureBoundingBoxIn);
         this.placeDoorCurrentPosition(
            worldIn, structureBoundingBoxIn, randomIn, 1, 1, 0, EnumFacing.getHorizontal(this.getMetadataWithOffset(Blocks.oak_door, 1))
         );
         if (this.getBlockStateFromPos(worldIn, 1, 0, -1, structureBoundingBoxIn).getBlock().getMaterial() == Material.air
            && this.getBlockStateFromPos(worldIn, 1, -1, -1, structureBoundingBoxIn).getBlock().getMaterial() != Material.air) {
            this.setBlockState(
               worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 1, 0, -1, structureBoundingBoxIn
            );
         }

         for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j, 9, i, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), j, -1, i, structureBoundingBoxIn);
            }
         }

         this.spawnVillagers(worldIn, structureBoundingBoxIn, 2, 1, 2, 1);
         return true;
      }

      @Override
      public int computeInt(int n, int n2) {
         return 1;
      }
   }

   public static class House2 extends StructureVillagePieces.Village {
      public static List<WeightedRandomChestContent> villageBlacksmithChestContents = Lists.newArrayList(
         new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3),
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5),
            new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.apple, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_sword, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_chestplate, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_helmet, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_leggings, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_boots, 0, 1, 1, 5),
            new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.obsidian), 0, 3, 7, 5),
            new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.sapling), 0, 3, 7, 5),
            new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3),
            new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
         }
      );
      public boolean hasMadeChest;

      public House2() {
      }

      public House2(StructureVillagePieces.Start start, int n, Random rand, StructureBoundingBox structureBoundingBox, EnumFacing facing) {
         super(start, n);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox;
      }

      public static StructureVillagePieces.House2 create(
         StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2, int n3, EnumFacing facing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 10, 6, 7, facing);
         return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureVillagePieces.House2(start, n4, rand, componentToAddBoundingBox, facing)
            : null;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Chest", this.hasMadeChest);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.hasMadeChest = tagCompound.getBoolean("Chest");
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.hPos < 0) {
            this.hPos = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.hPos < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.hPos - this.boundingBox.maxY + 6 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 9, 4, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 9, 0, 6, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 4, 0, 9, 4, 6, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 9, 5, 6, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 8, 5, 5, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 2, 3, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 4, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 3, 4, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 6, 0, 4, 6, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 3, 3, 1, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 3, 3, 2, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 3, 5, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 5, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 6, 5, 3, 6, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 0, 5, 3, 0, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 0, 9, 3, 0, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 1, 4, 9, 4, 6, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.flowing_lava.getDefaultState(), 7, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.flowing_lava.getDefaultState(), 8, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), 9, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), 9, 2, 4, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 4, 8, 2, 5, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, 1, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.furnace.getDefaultState(), 6, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.furnace.getDefaultState(), 6, 3, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.double_stone_slab.getDefaultState(), 8, 1, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 4, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 2, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.wooden_pressure_plate.getDefaultState(), 2, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 1, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.oak_stairs, 3)), 2, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.oak_stairs, 1)), 1, 1, 4, structureBoundingBoxIn);
         if (!this.hasMadeChest
            && structureBoundingBoxIn.isVecInside(new BlockPos(this.getXWithOffset(5, 5), this.getYWithOffset(1), this.getZWithOffset(5, 5)))) {
            this.hasMadeChest = true;
            this.generateChestContents(worldIn, structureBoundingBoxIn, randomIn, 5, 1, 5, villageBlacksmithChestContents, 3 + randomIn.nextInt(6));
         }

         for (int i = 6; i <= 8; i++) {
            if (this.getBlockStateFromPos(worldIn, i, 0, -1, structureBoundingBoxIn).getBlock().getMaterial() == Material.air
               && this.getBlockStateFromPos(worldIn, i, -1, -1, structureBoundingBoxIn).getBlock().getMaterial() != Material.air) {
               this.setBlockState(
                  worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), i, 0, -1, structureBoundingBoxIn
               );
            }
         }

         for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 10; j++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j, 6, i, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), j, -1, i, structureBoundingBoxIn);
            }
         }

         this.spawnVillagers(worldIn, structureBoundingBoxIn, 7, 1, 1, 1);
         return true;
      }

      @Override
      public int computeInt(int n, int n2) {
         return 3;
      }
   }

   public static class House3 extends StructureVillagePieces.Village {
      public House3() {
      }

      public House3(StructureVillagePieces.Start start, int n, Random rand, StructureBoundingBox structureBoundingBox, EnumFacing facing) {
         super(start, n);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox;
      }

      public static StructureVillagePieces.House3 create(
         StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2, int n3, EnumFacing facing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 9, 7, 12, facing);
         return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureVillagePieces.House3(start, n4, rand, componentToAddBoundingBox, facing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.hPos < 0) {
            this.hPos = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.hPos < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.hPos - this.boundingBox.maxY + 7 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 7, 4, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 6, 8, 4, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 5, 8, 0, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 7, 0, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 3, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 0, 0, 8, 3, 10, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 0, 0, 7, 2, 0, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 0, 5, 2, 1, 5, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 0, 6, 2, 3, 10, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 3, 0, 10, 7, 3, 10, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 7, 3, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 5, 2, 3, 5, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 1, 8, 4, 1, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 4, 3, 4, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 2, 8, 5, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 0, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 0, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 4, 4, structureBoundingBoxIn);
         int metadataWithOffset = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
         int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.oak_stairs, 2);

         for (int i = -1; i <= 2; i++) {
            for (int j = 0; j <= 8; j++) {
               this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset), j, 4 + i, i, structureBoundingBoxIn);
               if ((i > -1 || j <= 1) && (i > 0 || j <= 3) && (i > 1 || j <= 4 || j >= 6)) {
                  this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset2), j, 4 + i, 5 - i, structureBoundingBoxIn);
               }
            }
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 4, 5, 3, 4, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 4, 2, 7, 4, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 4, 4, 5, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 5, 4, 6, 5, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 6, 3, 5, 6, 10, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.oak_stairs, 0);

         for (int i = 4; i >= 1; i--) {
            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), i, 2 + i, 7 - i, structureBoundingBoxIn);

            for (int j = 8 - i; j <= 10; j++) {
               this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset3), i, 2 + i, j, structureBoundingBoxIn);
            }
         }

         int metadataWithOffset4 = this.getMetadataWithOffset(Blocks.oak_stairs, 1);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 6, 6, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 7, 5, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset4), 6, 6, 4, structureBoundingBoxIn);

         for (int i = 6; i <= 8; i++) {
            for (int j = 5; j <= 10; j++) {
               this.setBlockState(worldIn, Blocks.oak_stairs.getStateFromMeta(metadataWithOffset4), i, 12 - i, j, structureBoundingBoxIn);
            }
         }

         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 0, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 0, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 4, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 5, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 6, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 8, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 8, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 8, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 8, 2, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 8, 2, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 2, 2, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 7, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 2, 2, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 4, 4, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 5, 4, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 6, 4, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 5, 5, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 2, 3, 1, structureBoundingBoxIn);
         this.placeDoorCurrentPosition(
            worldIn, structureBoundingBoxIn, randomIn, 2, 1, 0, EnumFacing.getHorizontal(this.getMetadataWithOffset(Blocks.oak_door, 1))
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, -1, 3, 2, -1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         if (this.getBlockStateFromPos(worldIn, 2, 0, -1, structureBoundingBoxIn).getBlock().getMaterial() == Material.air
            && this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).getBlock().getMaterial() != Material.air) {
            this.setBlockState(
               worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 0, -1, structureBoundingBoxIn
            );
         }

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j, 7, i, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), j, -1, i, structureBoundingBoxIn);
            }
         }

         for (int i = 5; i < 11; i++) {
            for (int j = 2; j < 9; j++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j, 7, i, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), j, -1, i, structureBoundingBoxIn);
            }
         }

         this.spawnVillagers(worldIn, structureBoundingBoxIn, 4, 1, 2, 2);
         return true;
      }
   }

   public static class House4Garden extends StructureVillagePieces.Village {
      public boolean isRoofAccessible;

      public House4Garden() {
      }

      public House4Garden(StructureVillagePieces.Start start, int n, Random rand, StructureBoundingBox structureBoundingBox, EnumFacing facing) {
         super(start, n);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox;
         this.isRoofAccessible = rand.nextBoolean();
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Terrace", this.isRoofAccessible);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.isRoofAccessible = tagCompound.getBoolean("Terrace");
      }

      public static StructureVillagePieces.House4Garden create(
         StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2, int n3, EnumFacing facing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 5, 6, 5, facing);
         return StructureComponent.findIntersecting(list, componentToAddBoundingBox) != null ? null : new StructureVillagePieces.House4Garden(start, n4, rand, componentToAddBoundingBox, facing);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.hPos < 0) {
            this.hPos = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.hPos < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.hPos - this.boundingBox.maxY + 6 - 1, 0);
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 0, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 0, 4, 4, 4, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 1, 3, 4, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 0, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 0, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 0, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 0, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 0, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 0, 3, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 1, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 3, 4, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 4, 3, 3, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 2, 2, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 4, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 1, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 1, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 2, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 3, 3, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 3, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 3, 1, 0, structureBoundingBoxIn);
         if (this.getBlockStateFromPos(worldIn, 2, 0, -1, structureBoundingBoxIn).getBlock().getMaterial() == Material.air
            && this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).getBlock().getMaterial() != Material.air) {
            this.setBlockState(
               worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 2, 0, -1, structureBoundingBoxIn
            );
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 3, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         if (this.isRoofAccessible) {
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 2, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 3, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 5, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 2, 5, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 3, 5, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 5, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 0, 5, 3, structureBoundingBoxIn);
         }

         if (this.isRoofAccessible) {
            int metadataWithOffset = this.getMetadataWithOffset(Blocks.ladder, 3);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 3, 1, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 3, 2, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 3, 3, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 3, 4, 3, structureBoundingBoxIn);
         }

         this.setBlockState(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 2, 3, 1, structureBoundingBoxIn);

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j, 6, i, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), j, -1, i, structureBoundingBoxIn);
            }
         }

         this.spawnVillagers(worldIn, structureBoundingBoxIn, 1, 1, 2, 1);
         return true;
      }
   }

   public static class Path extends StructureVillagePieces.Road {
      public int length;

      public Path() {
      }

      public Path(StructureVillagePieces.Start start, int n, Random rand, StructureBoundingBox structureBoundingBox, EnumFacing facing) {
         super(start, n);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox;
         this.length = Math.max(structureBoundingBox.getXSize(), structureBoundingBox.getZSize());
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("Length", this.length);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.length = tagCompound.getInteger("Length");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         boolean flag = false;

         for (int nextInt = rand.nextInt(5); nextInt < this.length - 8; nextInt += 2 + rand.nextInt(5)) {
            StructureComponent nextComponentNN = this.getNextComponentNN((StructureVillagePieces.Start)componentIn, listIn, rand, 0, nextInt);
            if (nextComponentNN != null) {
               nextInt += Math.max(nextComponentNN.boundingBox.getXSize(), nextComponentNN.boundingBox.getZSize());
               flag = true;
            }
         }

         for (int nextInt = rand.nextInt(5); nextInt < this.length - 8; nextInt += 2 + rand.nextInt(5)) {
            StructureComponent nextComponentPP = this.getNextComponentPP((StructureVillagePieces.Start)componentIn, listIn, rand, 0, nextInt);
            if (nextComponentPP != null) {
               nextInt += Math.max(nextComponentPP.boundingBox.getXSize(), nextComponentPP.boundingBox.getZSize());
               flag = true;
            }
         }

         if (flag && rand.nextInt(3) > 0 && this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  StructureVillagePieces.createPath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY,
                     this.boundingBox.minZ,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
                  break;
               case SOUTH:
                  StructureVillagePieces.createPath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY,
                     this.boundingBox.maxZ - 2,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
                  break;
               case WEST:
                  StructureVillagePieces.createPath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.minX,
                     this.boundingBox.minY,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
                  break;
               case EAST:
                  StructureVillagePieces.createPath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.maxX - 2,
                     this.boundingBox.minY,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
            }
         }

         if (flag && rand.nextInt(3) > 0 && this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  StructureVillagePieces.createPath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY,
                     this.boundingBox.minZ,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
                  break;
               case SOUTH:
                  StructureVillagePieces.createPath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY,
                     this.boundingBox.maxZ - 2,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
                  break;
               case WEST:
                  StructureVillagePieces.createPath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.minX,
                     this.boundingBox.minY,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
                  break;
               case EAST:
                  StructureVillagePieces.createPath(
                     (StructureVillagePieces.Start)componentIn,
                     listIn,
                     rand,
                     this.boundingBox.maxX - 2,
                     this.boundingBox.minY,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
            }
         }
      }

      public static StructureBoundingBox findStructureBoundingBox(
         StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2, int n3, EnumFacing facing
      ) {
         for (int i = 7 * MathHelper.getRandomIntegerInRange(rand, 3, 5); i >= 7; i -= 7) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 3, 3, i, facing);
            if (StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null) {
               return componentToAddBoundingBox;
            }
         }

         return null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         IBlockState checkSandStone = this.checkSandStone(Blocks.gravel.getDefaultState());
         IBlockState checkSandStone2 = this.checkSandStone(Blocks.cobblestone.getDefaultState());

         for (int minX = this.boundingBox.minX; minX <= this.boundingBox.maxX; minX++) {
            for (int minZ = this.boundingBox.minZ; minZ <= this.boundingBox.maxZ; minZ++) {
               BlockPos blockPos = new BlockPos(minX, 64, minZ);
               if (structureBoundingBoxIn.isVecInside(blockPos)) {
                  blockPos = worldIn.getTopSolidOrLiquidBlock(blockPos).down();
                  worldIn.setBlockState(blockPos, checkSandStone, 2);
                  worldIn.setBlockState(blockPos.down(), checkSandStone2, 2);
               }
            }
         }

         return true;
      }
   }

   public static class PieceWeight {
      public Class<? extends StructureVillagePieces.Village> villagePieceClass;
      public int villagePieceWeight;
      public int villagePiecesSpawned;
      public int villagePiecesLimit;

      public PieceWeight(Class<? extends StructureVillagePieces.Village> clazz, int n, int n2) {
         this.villagePieceClass = clazz;
         this.villagePieceWeight = n;
         this.villagePiecesLimit = n2;
      }

      public boolean canSpawnMoreVillagePiecesOfType(int n) {
         return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
      }

      public boolean canSpawnMoreVillagePieces() {
         return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
      }
   }

   public abstract static class Road extends StructureVillagePieces.Village {
      public Road() {
      }

      public Road(StructureVillagePieces.Start start, int type) {
         super(start, type);
      }
   }

   public static class Start extends StructureVillagePieces.Well {
      public List<StructureComponent> structureComponentList = Lists.newArrayList();
      public List<StructureComponent> structureComponentList2 = Lists.newArrayList();
      public WorldChunkManager worldChunkMngr;
      public StructureVillagePieces.PieceWeight structVillagePieceWeight;
      public List<StructureVillagePieces.PieceWeight> structureVillageWeightedPieceList;
      public int terrainType;
      public boolean inDesert;

      public Start() {
      }

      public Start(WorldChunkManager chunkManagerIn, int n, Random rand, int n2, int n3, List<StructureVillagePieces.PieceWeight> list, int n4) {
         super((StructureVillagePieces.Start)null, 0, rand, n2, n3);
         this.worldChunkMngr = chunkManagerIn;
         this.structureVillageWeightedPieceList = list;
         this.terrainType = n4;
         BiomeGenBase biomeGenerator = chunkManagerIn.getBiomeGenerator(new BlockPos(n2, 0, n3), BiomeGenBase.DEFAULT);
         this.inDesert = biomeGenerator == BiomeGenBase.desert || biomeGenerator == BiomeGenBase.desertHills;
         this.setDesert(this.inDesert);
      }

      public WorldChunkManager getWorldChunkManager() {
         return this.worldChunkMngr;
      }
   }

   public static class Torch extends StructureVillagePieces.Village {
      public Torch() {
      }

      public Torch(StructureVillagePieces.Start start, int n, Random rand, StructureBoundingBox structureBoundingBox, EnumFacing facing) {
         super(start, n);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox;
      }

      public static StructureBoundingBox create(
         StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2, int n3, EnumFacing facing
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 3, 4, 2, facing);
         return StructureComponent.findIntersecting(list, componentToAddBoundingBox) != null ? null : componentToAddBoundingBox;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.hPos < 0) {
            this.hPos = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.hPos < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.hPos - this.boundingBox.maxY + 4 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 2, 3, 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 0, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 2, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.wool.getStateFromMeta(EnumDyeColor.WHITE.getDyeDamage()), 1, 3, 0, structureBoundingBoxIn);
         boolean flag = this.coordBaseMode == EnumFacing.EAST || this.coordBaseMode == EnumFacing.NORTH;
         this.setBlockState(
            worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.rotateY()), flag ? 2 : 0, 3, 0, structureBoundingBoxIn
         );
         this.setBlockState(worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode), 1, 3, 1, structureBoundingBoxIn);
         this.setBlockState(
            worldIn,
            Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.rotateYCCW()),
            flag ? 0 : 2,
            3,
            0,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn, Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, this.coordBaseMode.getOpposite()), 1, 3, -1, structureBoundingBoxIn
         );
         return true;
      }
   }

   abstract static class Village extends StructureComponent {
      public int hPos = -1;
      public int villagersSpawned;
      public boolean isDesertVillage;

      public Village() {
      }

      public Village(StructureVillagePieces.Start start, int type) {
         super(type);
         if (start != null) {
            this.isDesertVillage = start.inDesert;
         }
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         tagCompound.setInteger("HPos", this.hPos);
         tagCompound.setInteger("VCount", this.villagersSpawned);
         tagCompound.setBoolean("Desert", this.isDesertVillage);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         this.hPos = tagCompound.getInteger("HPos");
         this.villagersSpawned = tagCompound.getInteger("VCount");
         this.isDesertVillage = tagCompound.getBoolean("Desert");
      }

      public StructureComponent getNextComponentNN(StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  return StructureVillagePieces.generate(
                     start,
                     list,
                     rand,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ + n2,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
               case SOUTH:
                  return StructureVillagePieces.generate(
                     start,
                     list,
                     rand,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ + n2,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureVillagePieces.generate(
                     start,
                     list,
                     rand,
                     this.boundingBox.minX + n2,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureVillagePieces.generate(
                     start,
                     list,
                     rand,
                     this.boundingBox.minX + n2,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      public StructureComponent getNextComponentPP(StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  return StructureVillagePieces.generate(
                     start,
                     list,
                     rand,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ + n2,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
               case SOUTH:
                  return StructureVillagePieces.generate(
                     start,
                     list,
                     rand,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ + n2,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureVillagePieces.generate(
                     start,
                     list,
                     rand,
                     this.boundingBox.minX + n2,
                     this.boundingBox.minY + n,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureVillagePieces.generate(
                     start,
                     list,
                     rand,
                     this.boundingBox.minX + n2,
                     this.boundingBox.minY + n,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      public int getAverageGroundLevel(World worldIn, StructureBoundingBox structureBoundingBox) {
         int n = 0;
         int n2 = 0;
         MutableBlockPos mutableBlockPos = new MutableBlockPos();

         for (int minZ = this.boundingBox.minZ; minZ <= this.boundingBox.maxZ; minZ++) {
            for (int minX = this.boundingBox.minX; minX <= this.boundingBox.maxX; minX++) {
               mutableBlockPos.set(minX, 64, minZ);
               if (structureBoundingBox.isVecInside(mutableBlockPos)) {
                  n += Math.max(worldIn.getTopSolidOrLiquidBlock(mutableBlockPos).getY(), worldIn.provider.getAverageGroundLevel());
                  n2++;
               }
            }
         }

         return n2 == 0 ? -1 : n / n2;
      }

      public static boolean canVillageGoDeeper(StructureBoundingBox structureBoundingBox) {
         return structureBoundingBox != null && structureBoundingBox.minY > 10;
      }

      public void spawnVillagers(World worldIn, StructureBoundingBox structureBoundingBox, int n, int n2, int n3, int n4) {
         if (this.villagersSpawned < n4) {
            for (int villagersSpawned = this.villagersSpawned; villagersSpawned < n4; villagersSpawned++) {
               int xWithOffset = this.getXWithOffset(n + villagersSpawned, n3);
               int yWithOffset = this.getYWithOffset(n2);
               int zWithOffset = this.getZWithOffset(n + villagersSpawned, n3);
               if (!structureBoundingBox.isVecInside(new BlockPos(xWithOffset, yWithOffset, zWithOffset))) {
                  break;
               }

               this.villagersSpawned++;
               EntityVillager entityVillager = new EntityVillager(worldIn);
               entityVillager.setLocationAndAngles(xWithOffset + 0.5, yWithOffset, zWithOffset + 0.5, 0.0F, 0.0F);
               entityVillager.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityVillager)), (IEntityLivingData)null);
               entityVillager.setProfession(this.computeInt(villagersSpawned, entityVillager.getProfession()));
               worldIn.spawnEntityInWorld(entityVillager);
            }
         }
      }

      public int computeInt(int n, int n2) {
         return n2;
      }

      public IBlockState checkSandStone(IBlockState blockState) {
         if (this.isDesertVillage) {
            if (blockState.getBlock() == Blocks.log || blockState.getBlock() == Blocks.log2) {
               return Blocks.sandstone.getDefaultState();
            }

            if (blockState.getBlock() == Blocks.cobblestone) {
               return Blocks.sandstone.getStateFromMeta(EnumType.DEFAULT.getMetadata());
            }

            if (blockState.getBlock() == Blocks.planks) {
               return Blocks.sandstone.getStateFromMeta(EnumType.SMOOTH.getMetadata());
            }

            if (blockState.getBlock() == Blocks.oak_stairs) {
               return Blocks.sandstone_stairs.getDefaultState().withProperty(BlockStairs.FACING, blockState.getValue(BlockStairs.FACING));
            }

            if (blockState.getBlock() == Blocks.stone_stairs) {
               return Blocks.sandstone_stairs.getDefaultState().withProperty(BlockStairs.FACING, blockState.getValue(BlockStairs.FACING));
            }

            if (blockState.getBlock() == Blocks.gravel) {
               return Blocks.sandstone.getDefaultState();
            }
         }

         return blockState;
      }

      @Override
      public void setBlockState(World worldIn, IBlockState blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn) {
         IBlockState checkSandStone = this.checkSandStone(blockstateIn);
         super.setBlockState(worldIn, checkSandStone, x, y, z, boundingboxIn);
      }

      @Override
      public void fillWithBlocks(
         World worldIn,
         StructureBoundingBox boundingboxIn,
         int xMin,
         int yMin,
         int zMin,
         int xMax,
         int yMax,
         int zMax,
         IBlockState boundaryBlockState,
         IBlockState insideBlockState,
         boolean existingOnly
      ) {
         IBlockState checkSandStone = this.checkSandStone(boundaryBlockState);
         IBlockState checkSandStone2 = this.checkSandStone(insideBlockState);
         super.fillWithBlocks(worldIn, boundingboxIn, xMin, yMin, zMin, xMax, yMax, zMax, checkSandStone, checkSandStone2, existingOnly);
      }

      @Override
      public void replaceAirAndLiquidDownwards(World worldIn, IBlockState blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn) {
         IBlockState checkSandStone = this.checkSandStone(blockstateIn);
         super.replaceAirAndLiquidDownwards(worldIn, checkSandStone, x, y, z, boundingboxIn);
      }

      public void setDesert(boolean flag) {
         this.isDesertVillage = flag;
      }
   }

   public static class Well extends StructureVillagePieces.Village {
      public Well() {
      }

      public Well(StructureVillagePieces.Start start, int n, Random rand, int n2, int n3) {
         super(start, n);
         this.coordBaseMode = Plane.HORIZONTAL.random(rand);
         switch (this.coordBaseMode) {
            case NORTH:
            case SOUTH:
               this.boundingBox = new StructureBoundingBox(n2, 64, n3, n2 + 6 - 1, 78, n3 + 6 - 1);
               break;
            default:
               this.boundingBox = new StructureBoundingBox(n2, 64, n3, n2 + 6 - 1, 78, n3 + 6 - 1);
         }
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         StructureVillagePieces.createPath(
            (StructureVillagePieces.Start)componentIn,
            listIn,
            rand,
            this.boundingBox.minX - 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.minZ + 1,
            EnumFacing.WEST,
            this.getComponentType()
         );
         StructureVillagePieces.createPath(
            (StructureVillagePieces.Start)componentIn,
            listIn,
            rand,
            this.boundingBox.maxX + 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.minZ + 1,
            EnumFacing.EAST,
            this.getComponentType()
         );
         StructureVillagePieces.createPath(
            (StructureVillagePieces.Start)componentIn,
            listIn,
            rand,
            this.boundingBox.minX + 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.minZ - 1,
            EnumFacing.NORTH,
            this.getComponentType()
         );
         StructureVillagePieces.createPath(
            (StructureVillagePieces.Start)componentIn,
            listIn,
            rand,
            this.boundingBox.minX + 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.maxZ + 1,
            EnumFacing.SOUTH,
            this.getComponentType()
         );
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.hPos < 0) {
            this.hPos = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.hPos < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.hPos - this.boundingBox.maxY + 3, 0);
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 0, 1, 4, 12, 4, Blocks.cobblestone.getDefaultState(), Blocks.flowing_water.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, 12, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 3, 12, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, 12, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 3, 12, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 13, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 14, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 13, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 14, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 13, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 1, 14, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 13, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 4, 14, 4, structureBoundingBoxIn);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 15, 1, 4, 15, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );

         for (int i = 0; i <= 5; i++) {
            for (int j = 0; j <= 5; j++) {
               if (j == 0 || j == 5 || i == 0 || i == 5) {
                  this.setBlockState(worldIn, Blocks.gravel.getDefaultState(), j, 11, i, structureBoundingBoxIn);
                  this.clearCurrentPositionBlocksUpwards(worldIn, j, 12, i, structureBoundingBoxIn);
               }
            }
         }

         return true;
      }
   }

   public static class WoodHut extends StructureVillagePieces.Village {
      public boolean isTallHouse;
      public int tablePosition;

      public WoodHut() {
      }

      public WoodHut(StructureVillagePieces.Start start, int n, Random rand, StructureBoundingBox structureBoundingBox, EnumFacing facing) {
         super(start, n);
         this.coordBaseMode = facing;
         this.boundingBox = structureBoundingBox;
         this.isTallHouse = rand.nextBoolean();
         this.tablePosition = rand.nextInt(3);
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("T", this.tablePosition);
         tagCompound.setBoolean("C", this.isTallHouse);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.tablePosition = tagCompound.getInteger("T");
         this.isTallHouse = tagCompound.getBoolean("C");
      }

      public static StructureVillagePieces.WoodHut create(
         StructureVillagePieces.Start start, List<StructureComponent> list, Random rand, int n, int n2, int n3, EnumFacing facing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 4, 6, 5, facing);
         return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureVillagePieces.WoodHut(start, n4, rand, componentToAddBoundingBox, facing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.hPos < 0) {
            this.hPos = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
            if (this.hPos < 0) {
               return true;
            }

            this.boundingBox.offset(0, this.hPos - this.boundingBox.maxY + 6 - 1, 0);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 3, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 3, 0, 4, Blocks.cobblestone.getDefaultState(), Blocks.cobblestone.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 2, 0, 3, Blocks.dirt.getDefaultState(), Blocks.dirt.getDefaultState(), false);
         if (this.isTallHouse) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 1, 2, 4, 3, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         } else {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 2, 5, 3, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         }

         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 1, 4, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 2, 4, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 1, 4, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 2, 4, 4, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 0, 4, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 0, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 0, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 3, 4, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 3, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.log.getDefaultState(), 3, 4, 3, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 3, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 3, 3, 0, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 4, 0, 3, 4, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 4, 3, 3, 4, Blocks.log.getDefaultState(), Blocks.log.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 1, 3, 3, 3, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 2, 3, 0, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 4, 2, 3, 4, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.glass_pane.getDefaultState(), 3, 2, 2, structureBoundingBoxIn);
         if (this.tablePosition > 0) {
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), this.tablePosition, 1, 3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.wooden_pressure_plate.getDefaultState(), this.tablePosition, 2, 3, structureBoundingBoxIn);
         }

         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.air.getDefaultState(), 1, 2, 0, structureBoundingBoxIn);
         this.placeDoorCurrentPosition(
            worldIn, structureBoundingBoxIn, randomIn, 1, 1, 0, EnumFacing.getHorizontal(this.getMetadataWithOffset(Blocks.oak_door, 1))
         );
         if (this.getBlockStateFromPos(worldIn, 1, 0, -1, structureBoundingBoxIn).getBlock().getMaterial() == Material.air
            && this.getBlockStateFromPos(worldIn, 1, -1, -1, structureBoundingBoxIn).getBlock().getMaterial() != Material.air) {
            this.setBlockState(
               worldIn, Blocks.stone_stairs.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_stairs, 3)), 1, 0, -1, structureBoundingBoxIn
            );
         }

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
               this.clearCurrentPositionBlocksUpwards(worldIn, j, 6, i, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.cobblestone.getDefaultState(), j, -1, i, structureBoundingBoxIn);
            }
         }

         this.spawnVillagers(worldIn, structureBoundingBoxIn, 1, 1, 2, 1);
         return true;
      }
   }
}
