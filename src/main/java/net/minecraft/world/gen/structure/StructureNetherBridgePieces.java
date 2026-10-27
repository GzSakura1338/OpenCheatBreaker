package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.World;

public class StructureNetherBridgePieces {
   public static StructureNetherBridgePieces.PieceWeight[] primaryComponents = new StructureNetherBridgePieces.PieceWeight[]{
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Straight.class, 30, 0, true),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing3.class, 10, 4),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing.class, 10, 4),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Stairs.class, 10, 3),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Throne.class, 5, 2),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Entrance.class, 5, 1)
   };
   public static StructureNetherBridgePieces.PieceWeight[] secondaryComponents = new StructureNetherBridgePieces.PieceWeight[]{
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor5.class, 25, 0, true),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing2.class, 15, 5),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor2.class, 5, 10),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor.class, 5, 10),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor3.class, 10, 3, true),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor4.class, 7, 2),
      new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.NetherStalkRoom.class, 5, 2)
   };

   public static void registerNetherFortressPieces() {
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Crossing3.class, "NeBCr");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.End.class, "NeBEF");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Straight.class, "NeBS");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor3.class, "NeCCS");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor4.class, "NeCTB");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Entrance.class, "NeCE");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Crossing2.class, "NeSCSC");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor.class, "NeSCLT");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor5.class, "NeSC");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor2.class, "NeSCRT");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.NetherStalkRoom.class, "NeCSR");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Throne.class, "NeMT");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Crossing.class, "NeRC");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Stairs.class, "NeSR");
      MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Start.class, "NeStart");
   }

   public static StructureNetherBridgePieces.Piece createPiece(
      StructureNetherBridgePieces.PieceWeight pieceWeight, List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
   ) {
      Class weightClass = pieceWeight.weightClass;
      StructureNetherBridgePieces.Piece piece = null;
      if (weightClass == StructureNetherBridgePieces.Straight.class) {
         piece = StructureNetherBridgePieces.Straight.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (weightClass == StructureNetherBridgePieces.Crossing3.class) {
         piece = StructureNetherBridgePieces.Crossing3.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (weightClass == StructureNetherBridgePieces.Crossing.class) {
         piece = StructureNetherBridgePieces.Crossing.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (weightClass == StructureNetherBridgePieces.Stairs.class) {
         piece = StructureNetherBridgePieces.Stairs.create(list, random, n, n2, n3, n4, enumFacing);
      } else if (weightClass == StructureNetherBridgePieces.Throne.class) {
         piece = StructureNetherBridgePieces.Throne.create(list, random, n, n2, n3, n4, enumFacing);
      } else if (weightClass == StructureNetherBridgePieces.Entrance.class) {
         piece = StructureNetherBridgePieces.Entrance.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (weightClass == StructureNetherBridgePieces.Corridor5.class) {
         piece = StructureNetherBridgePieces.Corridor5.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (weightClass == StructureNetherBridgePieces.Corridor2.class) {
         piece = StructureNetherBridgePieces.Corridor2.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (weightClass == StructureNetherBridgePieces.Corridor.class) {
         piece = StructureNetherBridgePieces.Corridor.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (weightClass == StructureNetherBridgePieces.Corridor3.class) {
         piece = StructureNetherBridgePieces.Corridor3.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (weightClass == StructureNetherBridgePieces.Corridor4.class) {
         piece = StructureNetherBridgePieces.Corridor4.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (weightClass == StructureNetherBridgePieces.Crossing2.class) {
         piece = StructureNetherBridgePieces.Crossing2.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (weightClass == StructureNetherBridgePieces.NetherStalkRoom.class) {
         piece = StructureNetherBridgePieces.NetherStalkRoom.create(list, random, n, n2, n3, enumFacing, n4);
      }

      return piece;
   }

   public static class Corridor extends StructureNetherBridgePieces.Piece {
      public boolean containsChest;

      public Corridor() {
      }

      public Corridor(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
         this.containsChest = random.nextInt(3) == 0;
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.containsChest = tagCompound.getBoolean("Chest");
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Chest", this.containsChest);
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentX((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, 1, true);
      }

      public static StructureNetherBridgePieces.Corridor create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, 0, 0, 5, 7, 5, enumFacing);
         return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureNetherBridgePieces.Corridor(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 1, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 4, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 4, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 3, 1, 4, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 3, 3, 4, 4, 3, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 4, 3, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 3, 4, 1, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 3, 3, 4, 3, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         if (this.containsChest
            && structureBoundingBoxIn.isVecInside(new BlockPos(this.getXWithOffset(3, 3), this.getYWithOffset(2), this.getZWithOffset(3, 3)))) {
            this.containsChest = false;
            this.generateChestContents(worldIn, structureBoundingBoxIn, randomIn, 3, 2, 3, POSSIBLE_CHEST_CONTENTS, 2 + randomIn.nextInt(4));
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 0, 4, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Corridor2 extends StructureNetherBridgePieces.Piece {
      public boolean containsChest;

      public Corridor2() {
      }

      public Corridor2(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
         this.containsChest = random.nextInt(3) == 0;
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.containsChest = tagCompound.getBoolean("Chest");
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Chest", this.containsChest);
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentZ((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, 1, true);
      }

      public static StructureNetherBridgePieces.Corridor2 create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, 0, 0, 5, 7, 5, enumFacing);
         return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureNetherBridgePieces.Corridor2(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 1, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 4, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 1, 0, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 3, 0, 4, 3, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 4, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 2, 4, 4, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 3, 4, 1, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 3, 3, 4, 3, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         if (this.containsChest
            && structureBoundingBoxIn.isVecInside(new BlockPos(this.getXWithOffset(1, 3), this.getYWithOffset(2), this.getZWithOffset(1, 3)))) {
            this.containsChest = false;
            this.generateChestContents(worldIn, structureBoundingBoxIn, randomIn, 1, 2, 3, POSSIBLE_CHEST_CONTENTS, 2 + randomIn.nextInt(4));
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 0, 4, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Corridor3 extends StructureNetherBridgePieces.Piece {
      public Corridor3() {
      }

      public Corridor3(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 1, 0, true);
      }

      public static StructureNetherBridgePieces.Corridor3 create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -7, 0, 5, 14, 10, enumFacing);
         return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureNetherBridgePieces.Corridor3(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         int metadataWithOffset = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 2);

         for (int i = 0; i <= 9; i++) {
            int max = Math.max(1, 7 - i);
            int min = Math.min(Math.max(max + 5, 14 - i), 13);
            int n = i;
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 0, 0, i, 4, max, i, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 1, max + 1, i, 3, min - 1, i, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false
            );
            if (i <= 6) {
               this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset), 1, max + 1, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset), 2, max + 1, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset), 3, max + 1, i, structureBoundingBoxIn);
            }

            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               0,
               min,
               i,
               4,
               min,
               i,
               Blocks.nether_brick.getDefaultState(),
               Blocks.nether_brick.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               0,
               max + 1,
               i,
               0,
               min - 1,
               i,
               Blocks.nether_brick.getDefaultState(),
               Blocks.nether_brick.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               4,
               max + 1,
               i,
               4,
               min - 1,
               i,
               Blocks.nether_brick.getDefaultState(),
               Blocks.nether_brick.getDefaultState(),
               false
            );
            if ((i & 1) == 0) {
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  0,
                  max + 2,
                  i,
                  0,
                  max + 3,
                  i,
                  Blocks.nether_brick_fence.getDefaultState(),
                  Blocks.nether_brick_fence.getDefaultState(),
                  false
               );
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  4,
                  max + 2,
                  i,
                  4,
                  max + 3,
                  i,
                  Blocks.nether_brick_fence.getDefaultState(),
                  Blocks.nether_brick_fence.getDefaultState(),
                  false
               );
            }

            for (int j = 0; j <= 4; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), j, -1, n, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Corridor4 extends StructureNetherBridgePieces.Piece {
      public Corridor4() {
      }

      public Corridor4(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         byte b = 1;
         if (this.coordBaseMode == EnumFacing.WEST || this.coordBaseMode == EnumFacing.NORTH) {
            b = 5;
         }

         this.getNextComponentX((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, b, rand.nextInt(8) > 0);
         this.getNextComponentZ((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, b, rand.nextInt(8) > 0);
      }

      public static StructureNetherBridgePieces.Corridor4 create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -3, 0, 0, 9, 7, 9, enumFacing);
         return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureNetherBridgePieces.Corridor4(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 8, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 8, 5, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 0, 8, 6, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 2, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 2, 0, 8, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 3, 0, 1, 4, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 3, 0, 7, 4, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 4, 8, 2, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 4, 2, 2, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 4, 7, 2, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 8, 8, 3, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 6, 0, 3, 7, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 3, 6, 8, 3, 7, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 4, 0, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 3, 4, 8, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 3, 5, 2, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 3, 5, 7, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 4, 5, 1, 5, 5, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 4, 5, 7, 5, 5, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );

         for (int i = 0; i <= 5; i++) {
            for (int j = 0; j <= 8; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), j, -1, i, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Corridor5 extends StructureNetherBridgePieces.Piece {
      public Corridor5() {
      }

      public Corridor5(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 1, 0, true);
      }

      public static StructureNetherBridgePieces.Corridor5 create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, 0, 0, 5, 7, 5, enumFacing);
         return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureNetherBridgePieces.Corridor5(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 1, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 4, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 4, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 1, 0, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 3, 0, 4, 3, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 3, 1, 4, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 3, 3, 4, 4, 3, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 0, 4, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Crossing extends StructureNetherBridgePieces.Piece {
      public Crossing() {
      }

      public Crossing(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 2, 0, false);
         this.getNextComponentX((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, 2, false);
         this.getNextComponentZ((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, 2, false);
      }

      public static StructureNetherBridgePieces.Crossing create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -2, 0, 0, 7, 9, 7, enumFacing);
         return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureNetherBridgePieces.Crossing(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 6, 1, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 6, 7, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 1, 6, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 6, 1, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 2, 0, 6, 6, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 2, 6, 6, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 6, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 5, 0, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 2, 0, 6, 6, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 2, 5, 6, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 6, 0, 4, 6, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 0, 4, 5, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 6, 6, 4, 6, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 6, 4, 5, 6, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 2, 0, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 2, 0, 5, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 6, 2, 6, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 5, 2, 6, 5, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );

         for (int i = 0; i <= 6; i++) {
            for (int j = 0; j <= 6; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Crossing2 extends StructureNetherBridgePieces.Piece {
      public Crossing2() {
      }

      public Crossing2(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 1, 0, true);
         this.getNextComponentX((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, 1, true);
         this.getNextComponentZ((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 0, 1, true);
      }

      public static StructureNetherBridgePieces.Crossing2 create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, 0, 0, 5, 7, 5, enumFacing);
         return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureNetherBridgePieces.Crossing2(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 1, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 4, 5, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 4, 5, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 4, 0, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 4, 4, 5, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 0, 4, 6, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Crossing3 extends StructureNetherBridgePieces.Piece {
      public Crossing3() {
      }

      public Crossing3(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      public Crossing3(Random random, int n, int n2) {
         super(0);
         this.coordBaseMode = Plane.HORIZONTAL.random(random);
         switch (this.coordBaseMode) {
            case NORTH:
            case SOUTH:
               this.boundingBox = new StructureBoundingBox(n, 64, n2, n + 19 - 1, 73, n2 + 19 - 1);
               break;
            default:
               this.boundingBox = new StructureBoundingBox(n, 64, n2, n + 19 - 1, 73, n2 + 19 - 1);
         }
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 8, 3, false);
         this.getNextComponentX((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 3, 8, false);
         this.getNextComponentZ((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 3, 8, false);
      }

      public static StructureNetherBridgePieces.Crossing3 create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -8, -3, 0, 19, 10, 19, enumFacing);
         return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureNetherBridgePieces.Crossing3(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 3, 0, 11, 4, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 7, 18, 4, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 5, 0, 10, 7, 18, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 8, 18, 7, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 5, 0, 7, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 5, 11, 7, 5, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 0, 11, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 11, 11, 5, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 7, 7, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 7, 18, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 11, 7, 5, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 11, 18, 5, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 2, 0, 11, 2, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 2, 13, 11, 2, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 0, 0, 11, 1, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 0, 15, 11, 1, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 7; i <= 11; i++) {
            for (int j = 0; j <= 2; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, 18 - j, structureBoundingBoxIn);
            }
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 7, 5, 2, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 13, 2, 7, 18, 2, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 7, 3, 1, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 15, 0, 7, 18, 1, 11, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 0; i <= 2; i++) {
            for (int j = 7; j <= 11; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), 18 - i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class End extends StructureNetherBridgePieces.Piece {
      public int fillSeed;

      public End() {
      }

      public End(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
         this.fillSeed = random.nextInt();
      }

      public static StructureNetherBridgePieces.End create(List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -3, 0, 5, 10, 8, enumFacing);
         return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureNetherBridgePieces.End(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.fillSeed = tagCompound.getInteger("Seed");
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("Seed", this.fillSeed);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         Random random = new Random(this.fillSeed);

         for (int i = 0; i <= 4; i++) {
            for (int j = 3; j <= 4; j++) {
               int nextInt = random.nextInt(8);
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  i,
                  j,
                  0,
                  i,
                  j,
                  nextInt,
                  Blocks.nether_brick.getDefaultState(),
                  Blocks.nether_brick.getDefaultState(),
                  false
               );
            }
         }

         int nextInt = random.nextInt(8);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 0, 0, 5, nextInt, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         nextInt = random.nextInt(8);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 5, 0, 4, 5, nextInt, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 0; i <= 4; i++) {
            int nextInt2 = random.nextInt(5);
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               i,
               2,
               0,
               i,
               2,
               nextInt2,
               Blocks.nether_brick.getDefaultState(),
               Blocks.nether_brick.getDefaultState(),
               false
            );
         }

         for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 1; j++) {
               int nextInt2 = random.nextInt(3);
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  i,
                  j,
                  0,
                  i,
                  j,
                  nextInt2,
                  Blocks.nether_brick.getDefaultState(),
                  Blocks.nether_brick.getDefaultState(),
                  false
               );
            }
         }

         return true;
      }
   }

   public static class Entrance extends StructureNetherBridgePieces.Piece {
      public Entrance() {
      }

      public Entrance(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 5, 3, true);
      }

      public static StructureNetherBridgePieces.Entrance create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -5, -3, 0, 13, 14, 13, enumFacing);
         return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureNetherBridgePieces.Entrance(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 0, 12, 4, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 12, 13, 12, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 0, 1, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 0, 12, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 11, 4, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 11, 10, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 9, 11, 7, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 0, 4, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 0, 10, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 9, 0, 7, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 11, 2, 10, 12, 10, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 8, 0, 7, 8, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );

         for (int i = 1; i <= 11; i += 2) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               i,
               10,
               0,
               i,
               11,
               0,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               i,
               10,
               12,
               i,
               11,
               12,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               0,
               10,
               i,
               0,
               11,
               i,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               12,
               10,
               i,
               12,
               11,
               i,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), i, 13, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), i, 13, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 0, 13, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 12, 13, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), i + 1, 13, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), i + 1, 13, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, i + 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 12, 13, i + 1, structureBoundingBoxIn);
         }

         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 12, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 12, 13, 0, structureBoundingBoxIn);

         for (int i = 3; i <= 9; i += 2) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               1,
               7,
               i,
               1,
               8,
               i,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               11,
               7,
               i,
               11,
               8,
               i,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 8, 2, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 4, 12, 2, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 0, 0, 8, 1, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 0, 9, 8, 1, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 4, 3, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 0, 4, 12, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 4; i <= 8; i++) {
            for (int j = 0; j <= 2; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, 12 - j, structureBoundingBoxIn);
            }
         }

         for (int i = 0; i <= 2; i++) {
            for (int j = 4; j <= 8; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), 12 - i, -1, j, structureBoundingBoxIn);
            }
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 5, 5, 7, 5, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 6, 6, 4, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 6, 0, 6, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.flowing_lava.getDefaultState(), 6, 5, 6, structureBoundingBoxIn);
         BlockPos blockPos = new BlockPos(this.getXWithOffset(6, 6), this.getYWithOffset(5), this.getZWithOffset(6, 6));
         if (structureBoundingBoxIn.isVecInside(blockPos)) {
            worldIn.forceBlockUpdateTick(Blocks.flowing_lava, blockPos, randomIn);
         }

         return true;
      }
   }

   public static class NetherStalkRoom extends StructureNetherBridgePieces.Piece {
      public NetherStalkRoom() {
      }

      public NetherStalkRoom(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 5, 3, true);
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 5, 11, true);
      }

      public static StructureNetherBridgePieces.NetherStalkRoom create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -5, -3, 0, 13, 14, 13, enumFacing);
         return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureNetherBridgePieces.NetherStalkRoom(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 0, 12, 4, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 12, 13, 12, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 0, 1, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 11, 5, 0, 12, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 11, 4, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 11, 10, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 9, 11, 7, 12, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 0, 4, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 0, 10, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 9, 0, 7, 12, 1, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 11, 2, 10, 12, 10, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 1; i <= 11; i += 2) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               i,
               10,
               0,
               i,
               11,
               0,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               i,
               10,
               12,
               i,
               11,
               12,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               0,
               10,
               i,
               0,
               11,
               i,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               12,
               10,
               i,
               12,
               11,
               i,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), i, 13, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), i, 13, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 0, 13, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 12, 13, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), i + 1, 13, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), i + 1, 13, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, i + 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 12, 13, i + 1, structureBoundingBoxIn);
         }

         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 12, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 0, 13, 0, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 12, 13, 0, structureBoundingBoxIn);

         for (int i = 3; i <= 9; i += 2) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               1,
               7,
               i,
               1,
               8,
               i,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               11,
               7,
               i,
               11,
               8,
               i,
               Blocks.nether_brick_fence.getDefaultState(),
               Blocks.nether_brick_fence.getDefaultState(),
               false
            );
         }

         int metadataWithOffset = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 3);

         for (int i = 0; i <= 6; i++) {
            int n = i + 4;

            for (int j = 5; j <= 7; j++) {
               this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset), j, 5 + i, n, structureBoundingBoxIn);
            }

            if (n >= 5 && n <= 8) {
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  5,
                  5,
                  n,
                  7,
                  i + 4,
                  n,
                  Blocks.nether_brick.getDefaultState(),
                  Blocks.nether_brick.getDefaultState(),
                  false
               );
            } else if (n >= 9 && n <= 10) {
               this.fillWithBlocks(
                  worldIn,
                  structureBoundingBoxIn,
                  5,
                  8,
                  n,
                  7,
                  i + 4,
                  n,
                  Blocks.nether_brick.getDefaultState(),
                  Blocks.nether_brick.getDefaultState(),
                  false
               );
            }

            if (i >= 1) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 5, 6 + i, n, 7, 9 + i, n, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false
               );
            }
         }

         for (int i = 5; i <= 7; i++) {
            this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset), i, 12, 11, structureBoundingBoxIn);
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 6, 7, 5, 7, 7, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 7, 6, 7, 7, 7, 7, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 13, 12, 7, 13, 12, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 2, 3, 5, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 9, 3, 5, 10, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 4, 2, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 5, 2, 10, 5, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 5, 9, 10, 5, 10, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 10, 5, 4, 10, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 0);
         int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 1);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset3), 4, 5, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset3), 4, 5, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset3), 4, 5, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset3), 4, 5, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset2), 8, 5, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset2), 8, 5, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset2), 8, 5, 9, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_stairs.getStateFromMeta(metadataWithOffset2), 8, 5, 10, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 4, 4, 4, 4, 8, Blocks.soul_sand.getDefaultState(), Blocks.soul_sand.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 4, 4, 9, 4, 8, Blocks.soul_sand.getDefaultState(), Blocks.soul_sand.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 3, 5, 4, 4, 5, 8, Blocks.nether_wart.getDefaultState(), Blocks.nether_wart.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 8, 5, 4, 9, 5, 8, Blocks.nether_wart.getDefaultState(), Blocks.nether_wart.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 0, 8, 2, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 4, 12, 2, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 0, 0, 8, 1, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 0, 9, 8, 1, 12, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 4, 3, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 0, 4, 12, 1, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 4; i <= 8; i++) {
            for (int j = 0; j <= 2; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, 12 - j, structureBoundingBoxIn);
            }
         }

         for (int i = 0; i <= 2; i++) {
            for (int j = 4; j <= 8; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), 12 - i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   abstract static class Piece extends StructureComponent {
      public static List<WeightedRandomChestContent> POSSIBLE_CHEST_CONTENTS = Lists.newArrayList(
         new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 5),
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 5),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.golden_sword, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.golden_chestplate, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.flint_and_steel, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.nether_wart, 0, 3, 7, 5),
            new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10),
            new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 8),
            new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 3),
            new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.obsidian), 0, 2, 4, 2)
         }
      );

      public Piece() {
      }

      public Piece(int n) {
         super(n);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
      }

      public int getTotalWeight(List<StructureNetherBridgePieces.PieceWeight> list) {
         boolean flag = false;
         int n = 0;

         for (StructureNetherBridgePieces.PieceWeight pieceWeight : list) {
            if (pieceWeight.limit > 0 && pieceWeight.generatedCount < pieceWeight.limit) {
               flag = true;
            }

            n += pieceWeight.weight;
         }

         return flag ? n : -1;
      }

      public StructureNetherBridgePieces.Piece pickPiece(
         StructureNetherBridgePieces.Start start,
         List<StructureNetherBridgePieces.PieceWeight> list,
         List<StructureComponent> list2,
         Random random,
         int n,
         int n2,
         int n3,
         EnumFacing enumFacing,
         int n4
      ) {
         int totalWeight = this.getTotalWeight(list);
         boolean flag = totalWeight > 0 && n4 <= 30;
         int n5 = 0;

         while (n5 < 5 && flag) {
            n5++;
            int nextInt = random.nextInt(totalWeight);

            for (StructureNetherBridgePieces.PieceWeight pieceWeight : list) {
               nextInt -= pieceWeight.weight;
               if (nextInt < 0) {
                  if (!pieceWeight.canGenerate(n4) || pieceWeight == start.theNetherBridgePieceWeight && !pieceWeight.repeatable) {
                     break;
                  }

                  StructureNetherBridgePieces.Piece createPiece = StructureNetherBridgePieces.createPiece(pieceWeight, list2, random, n, n2, n3, enumFacing, n4);
                  if (createPiece != null) {
                     pieceWeight.generatedCount++;
                     start.theNetherBridgePieceWeight = pieceWeight;
                     if (!pieceWeight.canGenerate()) {
                        list.remove(pieceWeight);
                     }

                     return createPiece;
                  }
               }
            }
         }

         return StructureNetherBridgePieces.End.create(list2, random, n, n2, n3, enumFacing, n4);
      }

      public StructureComponent generate(
         StructureNetherBridgePieces.Start start,
         List<StructureComponent> list,
         Random random,
         int n,
         int n2,
         int n3,
         EnumFacing enumFacing,
         int n4,
         boolean flag
      ) {
         if (Math.abs(n - start.getBoundingBox().minX) <= 112 && Math.abs(n3 - start.getBoundingBox().minZ) <= 112) {
            List primaryWeights = start.primaryWeights;
            if (flag) {
               primaryWeights = start.secondaryWeights;
            }

            StructureNetherBridgePieces.Piece pickPiece = this.pickPiece(start, primaryWeights, list, random, n, n2, n3, enumFacing, n4 + 1);
            if (pickPiece != null) {
               list.add(pickPiece);
               start.pieces.add(pickPiece);
            }

            return pickPiece;
         } else {
            return StructureNetherBridgePieces.End.create(list, random, n, n2, n3, enumFacing, n4);
         }
      }

      public StructureComponent getNextComponentNormal(
         StructureNetherBridgePieces.Start start, List<StructureComponent> list, Random random, int n, int n2, boolean flag
      ) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  return this.generate(
                     start,
                     list,
                     random,
                     this.boundingBox.minX + n,
                     this.boundingBox.minY + n2,
                     this.boundingBox.minZ - 1,
                     this.coordBaseMode,
                     this.getComponentType(),
                     flag
                  );
               case SOUTH:
                  return this.generate(
                     start,
                     list,
                     random,
                     this.boundingBox.minX + n,
                     this.boundingBox.minY + n2,
                     this.boundingBox.maxZ + 1,
                     this.coordBaseMode,
                     this.getComponentType(),
                     flag
                  );
               case WEST:
                  return this.generate(
                     start,
                     list,
                     random,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + n2,
                     this.boundingBox.minZ + n,
                     this.coordBaseMode,
                     this.getComponentType(),
                     flag
                  );
               case EAST:
                  return this.generate(
                     start,
                     list,
                     random,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + n2,
                     this.boundingBox.minZ + n,
                     this.coordBaseMode,
                     this.getComponentType(),
                     flag
                  );
            }
         }

         return null;
      }

      public StructureComponent getNextComponentX(
         StructureNetherBridgePieces.Start start, List<StructureComponent> list, Random random, int n, int n2, boolean flag
      ) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  return this.generate(
                     start,
                     list,
                     random,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ + n2,
                     EnumFacing.WEST,
                     this.getComponentType(),
                     flag
                  );
               case SOUTH:
                  return this.generate(
                     start,
                     list,
                     random,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ + n2,
                     EnumFacing.WEST,
                     this.getComponentType(),
                     flag
                  );
               case WEST:
                  return this.generate(
                     start,
                     list,
                     random,
                     this.boundingBox.minX + n2,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType(),
                     flag
                  );
               case EAST:
                  return this.generate(
                     start,
                     list,
                     random,
                     this.boundingBox.minX + n2,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType(),
                     flag
                  );
            }
         }

         return null;
      }

      public StructureComponent getNextComponentZ(
         StructureNetherBridgePieces.Start start, List<StructureComponent> list, Random random, int n, int n2, boolean flag
      ) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  return this.generate(
                     start,
                     list,
                     random,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ + n2,
                     EnumFacing.EAST,
                     this.getComponentType(),
                     flag
                  );
               case SOUTH:
                  return this.generate(
                     start,
                     list,
                     random,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ + n2,
                     EnumFacing.EAST,
                     this.getComponentType(),
                     flag
                  );
               case WEST:
                  return this.generate(
                     start,
                     list,
                     random,
                     this.boundingBox.minX + n2,
                     this.boundingBox.minY + n,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType(),
                     flag
                  );
               case EAST:
                  return this.generate(
                     start,
                     list,
                     random,
                     this.boundingBox.minX + n2,
                     this.boundingBox.minY + n,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType(),
                     flag
                  );
            }
         }

         return null;
      }

      public static boolean isAboveGround(StructureBoundingBox structureBoundingBox) {
         return structureBoundingBox != null && structureBoundingBox.minY > 10;
      }
   }

   static class PieceWeight {
      public Class<? extends StructureNetherBridgePieces.Piece> weightClass;
      public int weight;
      public int generatedCount;
      public int limit;
      public boolean repeatable;

      public PieceWeight(Class<? extends StructureNetherBridgePieces.Piece> clazz, int n, int n2, boolean flag) {
         this.weightClass = clazz;
         this.weight = n;
         this.limit = n2;
         this.repeatable = flag;
      }

      public PieceWeight(Class<? extends StructureNetherBridgePieces.Piece> clazz, int n, int n2) {
         this(clazz, n, n2, false);
      }

      public boolean canGenerate(int n) {
         return this.limit == 0 || this.generatedCount < this.limit;
      }

      public boolean canGenerate() {
         return this.limit == 0 || this.generatedCount < this.limit;
      }
   }

   public static class Stairs extends StructureNetherBridgePieces.Piece {
      public Stairs() {
      }

      public Stairs(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentZ((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 6, 2, false);
      }

      public static StructureNetherBridgePieces.Stairs create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, int n4, EnumFacing enumFacing
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -2, 0, 0, 7, 11, 7, enumFacing);
         return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureNetherBridgePieces.Stairs(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 6, 1, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 6, 10, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 1, 8, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 2, 0, 6, 8, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 1, 0, 8, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 2, 1, 6, 8, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 2, 6, 5, 8, 6, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 2, 0, 5, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 3, 2, 6, 5, 2, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 3, 4, 6, 5, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.nether_brick.getDefaultState(), 5, 2, 5, structureBoundingBoxIn);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 2, 5, 4, 3, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 3, 2, 5, 3, 4, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 2, 5, 2, 5, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 2, 5, 1, 6, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 7, 1, 5, 7, 4, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 8, 2, 6, 8, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 6, 0, 4, 8, 0, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 5, 0, 4, 5, 0, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );

         for (int i = 0; i <= 6; i++) {
            for (int j = 0; j <= 6; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   public static class Start extends StructureNetherBridgePieces.Crossing3 {
      public StructureNetherBridgePieces.PieceWeight theNetherBridgePieceWeight;
      public List<StructureComponent> pieces = Lists.newArrayList();
      public List<StructureNetherBridgePieces.PieceWeight> primaryWeights;
      public List<StructureNetherBridgePieces.PieceWeight> secondaryWeights;

      public Start() {
      }

      public Start(Random random, int n, int n2) {
         super(random, n, n2);
         this.primaryWeights = Lists.newArrayList();

         for (StructureNetherBridgePieces.PieceWeight pieceWeight : StructureNetherBridgePieces.primaryComponents) {
            pieceWeight.generatedCount = 0;
            this.primaryWeights.add(pieceWeight);
         }

         this.secondaryWeights = Lists.newArrayList();

         for (StructureNetherBridgePieces.PieceWeight pieceWeight : StructureNetherBridgePieces.secondaryComponents) {
            pieceWeight.generatedCount = 0;
            this.secondaryWeights.add(pieceWeight);
         }
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
      }
   }

   public static class Straight extends StructureNetherBridgePieces.Piece {
      public Straight() {
      }

      public Straight(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureNetherBridgePieces.Start)componentIn, listIn, rand, 1, 3, false);
      }

      public static StructureNetherBridgePieces.Straight create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -3, 0, 5, 10, 19, enumFacing);
         return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureNetherBridgePieces.Straight(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 0, 4, 4, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 0, 3, 7, 18, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 0, 0, 5, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 5, 0, 4, 5, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 0, 4, 2, 5, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 2, 13, 4, 2, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 1, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 0, 15, 4, 1, 18, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );

         for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 2; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, 18 - j, structureBoundingBoxIn);
            }
         }

         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 3, 4, 0, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            0,
            3,
            14,
            0,
            4,
            14,
            Blocks.nether_brick_fence.getDefaultState(),
            Blocks.nether_brick_fence.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            0,
            1,
            17,
            0,
            4,
            17,
            Blocks.nether_brick_fence.getDefaultState(),
            Blocks.nether_brick_fence.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 4, 1, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 3, 4, 4, 4, 4, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            4,
            3,
            14,
            4,
            4,
            14,
            Blocks.nether_brick_fence.getDefaultState(),
            Blocks.nether_brick_fence.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            4,
            1,
            17,
            4,
            4,
            17,
            Blocks.nether_brick_fence.getDefaultState(),
            Blocks.nether_brick_fence.getDefaultState(),
            false
         );
         return true;
      }
   }

   public static class Throne extends StructureNetherBridgePieces.Piece {
      public boolean hasSpawner;

      public Throne() {
      }

      public Throne(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.hasSpawner = tagCompound.getBoolean("Mob");
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Mob", this.hasSpawner);
      }

      public static StructureNetherBridgePieces.Throne create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, int n4, EnumFacing enumFacing
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -2, 0, 0, 7, 8, 9, enumFacing);
         return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureNetherBridgePieces.Throne(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 6, 7, 7, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 0, 0, 5, 1, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 2, 1, 5, 2, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 3, 2, 5, 3, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 4, 3, 5, 4, 7, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 2, 0, 1, 4, 2, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 2, 0, 5, 4, 2, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 5, 2, 1, 5, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 5, 2, 5, 5, 3, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 5, 3, 0, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 5, 3, 6, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 5, 8, 5, 5, 8, Blocks.nether_brick.getDefaultState(), Blocks.nether_brick.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 1, 6, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.nether_brick_fence.getDefaultState(), 5, 6, 3, structureBoundingBoxIn);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 0, 6, 3, 0, 6, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 6, 6, 3, 6, 6, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 6, 8, 5, 7, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 2, 8, 8, 4, 8, 8, Blocks.nether_brick_fence.getDefaultState(), Blocks.nether_brick_fence.getDefaultState(), false
         );
         if (!this.hasSpawner) {
            BlockPos blockPos = new BlockPos(this.getXWithOffset(3, 5), this.getYWithOffset(5), this.getZWithOffset(3, 5));
            if (structureBoundingBoxIn.isVecInside(blockPos)) {
               this.hasSpawner = true;
               worldIn.setBlockState(blockPos, Blocks.mob_spawner.getDefaultState(), 2);
               TileEntity tileEntity = worldIn.getTileEntity(blockPos);
               if (tileEntity instanceof TileEntityMobSpawner) {
                  ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic().setEntityName("Blaze");
               }
            }
         }

         for (int i = 0; i <= 6; i++) {
            for (int j = 0; j <= 6; j++) {
               this.replaceAirAndLiquidDownwards(worldIn, Blocks.nether_brick.getDefaultState(), i, -1, j, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }
}
