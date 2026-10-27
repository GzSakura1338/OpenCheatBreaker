package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class StructureOceanMonumentPieces {
   public static void registerOceanMonumentPieces() {
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.MonumentBuilding.class, "OMB");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.MonumentCoreRoom.class, "OMCR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleXRoom.class, "OMDXR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleXYRoom.class, "OMDXYR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleYRoom.class, "OMDYR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleYZRoom.class, "OMDYZR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.DoubleZRoom.class, "OMDZR");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.EntryRoom.class, "OMEntry");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.Penthouse.class, "OMPenthouse");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.SimpleRoom.class, "OMSimple");
      MapGenStructureIO.registerStructureComponent(StructureOceanMonumentPieces.SimpleTopRoom.class, "OMSimpleT");
   }

   public static class DoubleXRoom extends StructureOceanMonumentPieces.Piece {
      public DoubleXRoom() {
      }

      public DoubleXRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         super(1, enumFacing, roomDefinition, 2, 1, 1);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition = this.setting.neighbors[EnumFacing.EAST.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition setting = this.setting;
         if (this.setting.roomIndex / 25 > 0) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 8, 0, roomDefinition.neighborPresences[EnumFacing.DOWN.getIndex()]);
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 0, setting.neighborPresences[EnumFacing.DOWN.getIndex()]);
         }

         if (setting.neighbors[EnumFacing.UP.getIndex()] == null) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 1, 4, 1, 7, 4, 6, PRISMARINE);
         }

         if (roomDefinition.neighbors[EnumFacing.UP.getIndex()] == null) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 8, 4, 1, 14, 4, 6, PRISMARINE);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 3, 0, 15, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 15, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 7, 14, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 7, PRISMARINE, PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 2, 0, 15, 2, 7, PRISMARINE, PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 15, 2, 0, PRISMARINE, PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 14, 2, 7, PRISMARINE, PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 1, 0, 15, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 15, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 7, 14, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 0, 10, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 2, 0, 9, 2, 3, PRISMARINE, PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 3, 0, 10, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.setBlockState(worldIn, SEA_LANTERN, 6, 2, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, SEA_LANTERN, 9, 2, 3, structureBoundingBoxIn);
         if (setting.neighborPresences[EnumFacing.SOUTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, false);
         }

         if (setting.neighborPresences[EnumFacing.NORTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7, false);
         }

         if (setting.neighborPresences[EnumFacing.WEST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, false);
         }

         if (roomDefinition.neighborPresences[EnumFacing.SOUTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 11, 1, 0, 12, 2, 0, false);
         }

         if (roomDefinition.neighborPresences[EnumFacing.NORTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 11, 1, 7, 12, 2, 7, false);
         }

         if (roomDefinition.neighborPresences[EnumFacing.EAST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 15, 1, 3, 15, 2, 4, false);
         }

         return true;
      }
   }

   public static class DoubleXYRoom extends StructureOceanMonumentPieces.Piece {
      public DoubleXYRoom() {
      }

      public DoubleXYRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         super(1, enumFacing, roomDefinition, 2, 2, 1);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition = this.setting.neighbors[EnumFacing.EAST.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition setting = this.setting;
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = setting.neighbors[EnumFacing.UP.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition roomDefinition3 = roomDefinition.neighbors[EnumFacing.UP.getIndex()];
         if (this.setting.roomIndex / 25 > 0) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 8, 0, roomDefinition.neighborPresences[EnumFacing.DOWN.getIndex()]);
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 0, setting.neighborPresences[EnumFacing.DOWN.getIndex()]);
         }

         if (roomDefinition2.neighbors[EnumFacing.UP.getIndex()] == null) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 1, 8, 1, 7, 8, 6, PRISMARINE);
         }

         if (roomDefinition3.neighbors[EnumFacing.UP.getIndex()] == null) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 8, 8, 1, 14, 8, 6, PRISMARINE);
         }

         for (int i = 1; i <= 7; i++) {
            IBlockState blockState = PRISMARINE_BRICKS;
            if (i == 2 || i == 6) {
               blockState = PRISMARINE;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i, 0, 0, i, 7, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, i, 0, 15, i, 7, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, i, 0, 15, i, 0, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, i, 7, 14, i, 7, blockState, blockState, false);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 3, 2, 7, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 4, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 5, 4, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 3, 13, 7, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 11, 1, 2, 12, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 11, 1, 5, 12, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 3, 5, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 1, 3, 10, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 7, 2, 10, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 5, 2, 5, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 5, 2, 10, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 5, 5, 5, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 5, 5, 10, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.setBlockState(worldIn, PRISMARINE_BRICKS, 6, 6, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, PRISMARINE_BRICKS, 9, 6, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, PRISMARINE_BRICKS, 6, 6, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, PRISMARINE_BRICKS, 9, 6, 5, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 3, 6, 4, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 4, 3, 10, 4, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.setBlockState(worldIn, SEA_LANTERN, 5, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, SEA_LANTERN, 5, 4, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, SEA_LANTERN, 10, 4, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, SEA_LANTERN, 10, 4, 5, structureBoundingBoxIn);
         if (setting.neighborPresences[EnumFacing.SOUTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, false);
         }

         if (setting.neighborPresences[EnumFacing.NORTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7, false);
         }

         if (setting.neighborPresences[EnumFacing.WEST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, false);
         }

         if (roomDefinition.neighborPresences[EnumFacing.SOUTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 11, 1, 0, 12, 2, 0, false);
         }

         if (roomDefinition.neighborPresences[EnumFacing.NORTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 11, 1, 7, 12, 2, 7, false);
         }

         if (roomDefinition.neighborPresences[EnumFacing.EAST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 15, 1, 3, 15, 2, 4, false);
         }

         if (roomDefinition2.neighborPresences[EnumFacing.SOUTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 5, 0, 4, 6, 0, false);
         }

         if (roomDefinition2.neighborPresences[EnumFacing.NORTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 5, 7, 4, 6, 7, false);
         }

         if (roomDefinition2.neighborPresences[EnumFacing.WEST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 0, 5, 3, 0, 6, 4, false);
         }

         if (roomDefinition3.neighborPresences[EnumFacing.SOUTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 11, 5, 0, 12, 6, 0, false);
         }

         if (roomDefinition3.neighborPresences[EnumFacing.NORTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 11, 5, 7, 12, 6, 7, false);
         }

         if (roomDefinition3.neighborPresences[EnumFacing.EAST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 15, 5, 3, 15, 6, 4, false);
         }

         return true;
      }
   }

   public static class DoubleYRoom extends StructureOceanMonumentPieces.Piece {
      public DoubleYRoom() {
      }

      public DoubleYRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         super(1, enumFacing, roomDefinition, 1, 2, 1);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.setting.roomIndex / 25 > 0) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 0, this.setting.neighborPresences[EnumFacing.DOWN.getIndex()]);
         }

         StructureOceanMonumentPieces.RoomDefinition roomDefinition = this.setting.neighbors[EnumFacing.UP.getIndex()];
         if (roomDefinition.neighbors[EnumFacing.UP.getIndex()] == null) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 1, 8, 1, 6, 8, 6, PRISMARINE);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 0, 0, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 4, 0, 7, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 0, 6, 4, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 7, 6, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 4, 1, 2, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 2, 1, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 1, 5, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 4, 2, 6, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 4, 5, 2, 4, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 5, 1, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 5, 5, 4, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 4, 5, 6, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         StructureOceanMonumentPieces.RoomDefinition setting = this.setting;

         for (int i = 1; i <= 5; i += 4) {
            int n = 0;
            if (setting.neighborPresences[EnumFacing.SOUTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, i, n, 2, i + 2, n, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, i, n, 5, i + 2, n, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, i + 2, n, 4, i + 2, n, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i, n, 7, i + 2, n, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i + 1, n, 7, i + 1, n, PRISMARINE, PRISMARINE, false);
            }

            n = 7;
            if (setting.neighborPresences[EnumFacing.NORTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, i, n, 2, i + 2, n, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, i, n, 5, i + 2, n, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, i + 2, n, 4, i + 2, n, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i, n, 7, i + 2, n, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i + 1, n, 7, i + 1, n, PRISMARINE, PRISMARINE, false);
            }

            int n2 = 0;
            if (setting.neighborPresences[EnumFacing.WEST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n2, i, 2, n2, i + 2, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n2, i, 5, n2, i + 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n2, i + 2, 3, n2, i + 2, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n2, i, 0, n2, i + 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n2, i + 1, 0, n2, i + 1, 7, PRISMARINE, PRISMARINE, false);
            }

            n2 = 7;
            if (setting.neighborPresences[EnumFacing.EAST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n2, i, 2, n2, i + 2, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n2, i, 5, n2, i + 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n2, i + 2, 3, n2, i + 2, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n2, i, 0, n2, i + 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n2, i + 1, 0, n2, i + 1, 7, PRISMARINE, PRISMARINE, false);
            }

            setting = roomDefinition;
         }

         return true;
      }
   }

   public static class DoubleYZRoom extends StructureOceanMonumentPieces.Piece {
      public DoubleYZRoom() {
      }

      public DoubleYZRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         super(1, enumFacing, roomDefinition, 1, 2, 2);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition = this.setting.neighbors[EnumFacing.NORTH.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition setting = this.setting;
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = roomDefinition.neighbors[EnumFacing.UP.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition roomDefinition3 = setting.neighbors[EnumFacing.UP.getIndex()];
         if (this.setting.roomIndex / 25 > 0) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 8, roomDefinition.neighborPresences[EnumFacing.DOWN.getIndex()]);
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 0, setting.neighborPresences[EnumFacing.DOWN.getIndex()]);
         }

         if (roomDefinition3.neighbors[EnumFacing.UP.getIndex()] == null) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 1, 8, 1, 6, 8, 7, PRISMARINE);
         }

         if (roomDefinition2.neighbors[EnumFacing.UP.getIndex()] == null) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 1, 8, 8, 6, 8, 14, PRISMARINE);
         }

         for (int i = 1; i <= 7; i++) {
            IBlockState blockState = PRISMARINE_BRICKS;
            if (i == 2 || i == 6) {
               blockState = PRISMARINE;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, i, 0, 0, i, 15, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, i, 0, 7, i, 15, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, i, 0, 6, i, 0, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, i, 15, 6, i, 15, blockState, blockState, false);
         }

         for (int i = 1; i <= 7; i++) {
            IBlockState blockState = DARK_PRISMARINE;
            if (i == 2 || i == 6) {
               blockState = SEA_LANTERN;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, i, 7, 4, i, 8, blockState, blockState, false);
         }

         if (setting.neighborPresences[EnumFacing.SOUTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, false);
         }

         if (setting.neighborPresences[EnumFacing.EAST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4, false);
         }

         if (setting.neighborPresences[EnumFacing.WEST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, false);
         }

         if (roomDefinition.neighborPresences[EnumFacing.NORTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 1, 15, 4, 2, 15, false);
         }

         if (roomDefinition.neighborPresences[EnumFacing.WEST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 0, 1, 11, 0, 2, 12, false);
         }

         if (roomDefinition.neighborPresences[EnumFacing.EAST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 7, 1, 11, 7, 2, 12, false);
         }

         if (roomDefinition3.neighborPresences[EnumFacing.SOUTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 5, 0, 4, 6, 0, false);
         }

         if (roomDefinition3.neighborPresences[EnumFacing.EAST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 7, 5, 3, 7, 6, 4, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 2, 6, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 2, 6, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 5, 6, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         }

         if (roomDefinition3.neighborPresences[EnumFacing.WEST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 0, 5, 3, 0, 6, 4, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 2, 2, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 2, 1, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 5, 1, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         }

         if (roomDefinition2.neighborPresences[EnumFacing.NORTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 5, 15, 4, 6, 15, false);
         }

         if (roomDefinition2.neighborPresences[EnumFacing.WEST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 0, 5, 11, 0, 6, 12, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 10, 2, 4, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 10, 1, 3, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 13, 1, 3, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         }

         if (roomDefinition2.neighborPresences[EnumFacing.EAST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 7, 5, 11, 7, 6, 12, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 4, 10, 6, 4, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 10, 6, 3, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 13, 6, 3, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         }

         return true;
      }
   }

   public static class DoubleZRoom extends StructureOceanMonumentPieces.Piece {
      public DoubleZRoom() {
      }

      public DoubleZRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         super(1, enumFacing, roomDefinition, 1, 1, 2);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition = this.setting.neighbors[EnumFacing.NORTH.getIndex()];
         StructureOceanMonumentPieces.RoomDefinition setting = this.setting;
         if (this.setting.roomIndex / 25 > 0) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 8, roomDefinition.neighborPresences[EnumFacing.DOWN.getIndex()]);
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 0, setting.neighborPresences[EnumFacing.DOWN.getIndex()]);
         }

         if (setting.neighbors[EnumFacing.UP.getIndex()] == null) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 1, 4, 1, 6, 4, 7, PRISMARINE);
         }

         if (roomDefinition.neighbors[EnumFacing.UP.getIndex()] == null) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 1, 4, 8, 6, 4, 14, PRISMARINE);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 0, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 0, 7, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 7, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 15, 6, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 15, PRISMARINE, PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 0, 7, 2, 15, PRISMARINE, PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 7, 2, 0, PRISMARINE, PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 15, 6, 2, 15, PRISMARINE, PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 0, 7, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 7, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 15, 6, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 1, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 1, 6, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 1, 1, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 1, 6, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 13, 1, 1, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 13, 6, 1, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 13, 1, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 13, 6, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 6, 2, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 6, 5, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 9, 2, 3, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 9, 5, 3, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 6, 4, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 9, 4, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, 7, 2, 2, 8, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 7, 5, 2, 8, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.setBlockState(worldIn, SEA_LANTERN, 2, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, SEA_LANTERN, 5, 2, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, SEA_LANTERN, 2, 2, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, SEA_LANTERN, 5, 2, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, PRISMARINE_BRICKS, 2, 3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, PRISMARINE_BRICKS, 5, 3, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, PRISMARINE_BRICKS, 2, 3, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, PRISMARINE_BRICKS, 5, 3, 10, structureBoundingBoxIn);
         if (setting.neighborPresences[EnumFacing.SOUTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, false);
         }

         if (setting.neighborPresences[EnumFacing.EAST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4, false);
         }

         if (setting.neighborPresences[EnumFacing.WEST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, false);
         }

         if (roomDefinition.neighborPresences[EnumFacing.NORTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 1, 15, 4, 2, 15, false);
         }

         if (roomDefinition.neighborPresences[EnumFacing.WEST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 0, 1, 11, 0, 2, 12, false);
         }

         if (roomDefinition.neighborPresences[EnumFacing.EAST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 7, 1, 11, 7, 2, 12, false);
         }

         return true;
      }
   }

   public static class EntryRoom extends StructureOceanMonumentPieces.Piece {
      public EntryRoom() {
      }

      public EntryRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         super(1, enumFacing, roomDefinition, 1, 1, 1);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 2, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 1, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 2, 0, 7, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 7, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 2, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         if (this.setting.neighborPresences[EnumFacing.NORTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7, false);
         }

         if (this.setting.neighborPresences[EnumFacing.WEST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 0, 1, 3, 1, 2, 4, false);
         }

         if (this.setting.neighborPresences[EnumFacing.EAST.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 6, 1, 3, 7, 2, 4, false);
         }

         return true;
      }
   }

   static class FitSimpleRoomHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      public FitSimpleRoomHelper() {
      }

      @Override
      public boolean checkRoomDefinition(StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         return true;
      }

      @Override
      public StructureOceanMonumentPieces.Piece createDoubleZRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         roomDefinition.used = true;
         return new StructureOceanMonumentPieces.SimpleRoom(enumFacing, roomDefinition, random);
      }
   }

   static class FitSimpleRoomTopHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      public FitSimpleRoomTopHelper() {
      }

      @Override
      public boolean checkRoomDefinition(StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         return !roomDefinition.neighborPresences[EnumFacing.WEST.getIndex()]
            && !roomDefinition.neighborPresences[EnumFacing.EAST.getIndex()]
            && !roomDefinition.neighborPresences[EnumFacing.NORTH.getIndex()]
            && !roomDefinition.neighborPresences[EnumFacing.SOUTH.getIndex()]
            && !roomDefinition.neighborPresences[EnumFacing.UP.getIndex()];
      }

      @Override
      public StructureOceanMonumentPieces.Piece createDoubleZRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         roomDefinition.used = true;
         return new StructureOceanMonumentPieces.SimpleTopRoom(enumFacing, roomDefinition, random);
      }
   }

   public static class MonumentBuilding extends StructureOceanMonumentPieces.Piece {
      public StructureOceanMonumentPieces.RoomDefinition roomDefinition;
      public StructureOceanMonumentPieces.RoomDefinition roomDefinition2;
      public List<StructureOceanMonumentPieces.Piece> pieceList = Lists.newArrayList();

      public MonumentBuilding() {
      }

      public MonumentBuilding(Random random, int n, int n2, EnumFacing enumFacing) {
         super(0);
         this.coordBaseMode = enumFacing;
         switch (this.coordBaseMode) {
            case NORTH:
            case SOUTH:
               this.boundingBox = new StructureBoundingBox(n, 39, n2, n + 58 - 1, 61, n2 + 58 - 1);
               break;
            default:
               this.boundingBox = new StructureBoundingBox(n, 39, n2, n + 58 - 1, 61, n2 + 58 - 1);
         }

         List findList = this.findList(random);
         this.roomDefinition.used = true;
         this.pieceList.add(new StructureOceanMonumentPieces.EntryRoom(this.coordBaseMode, this.roomDefinition));
         this.pieceList.add(new StructureOceanMonumentPieces.MonumentCoreRoom(this.coordBaseMode, this.roomDefinition2, random));
         ArrayList newArrayList = Lists.newArrayList();
         newArrayList.add(new StructureOceanMonumentPieces.XYDoubleRoomFitHelper());
         newArrayList.add(new StructureOceanMonumentPieces.YZDoubleRoomFitHelper());
         newArrayList.add(new StructureOceanMonumentPieces.ZDoubleRoomFitHelper());
         newArrayList.add(new StructureOceanMonumentPieces.XDoubleRoomFitHelper());
         newArrayList.add(new StructureOceanMonumentPieces.YDoubleRoomFitHelper());
         newArrayList.add(new StructureOceanMonumentPieces.FitSimpleRoomTopHelper());
         newArrayList.add(new StructureOceanMonumentPieces.FitSimpleRoomHelper());

         for (StructureOceanMonumentPieces.RoomDefinition roomDefinition : (Iterable<StructureOceanMonumentPieces.RoomDefinition>)findList) {
            if (!roomDefinition.used && !roomDefinition.isAboveLevelThree()) {
               for (StructureOceanMonumentPieces.MonumentRoomFitHelper monumentRoomFitHelper : (Iterable<StructureOceanMonumentPieces.MonumentRoomFitHelper>)newArrayList) {
                  if (monumentRoomFitHelper.checkRoomDefinition(roomDefinition)) {
                     this.pieceList.add(monumentRoomFitHelper.createDoubleZRoom(this.coordBaseMode, roomDefinition, random));
                     break;
                  }
               }
            }
         }

         int minY = this.boundingBox.minY;
         int xWithOffset = this.getXWithOffset(9, 22);
         int zWithOffset = this.getZWithOffset(9, 22);

         for (StructureOceanMonumentPieces.Piece piece : this.pieceList) {
            piece.getBoundingBox().offset(xWithOffset, minY, zWithOffset);
         }

         StructureBoundingBox structureBoundingBox = StructureBoundingBox.create(
            this.getXWithOffset(1, 1),
            this.getYWithOffset(1),
            this.getZWithOffset(1, 1),
            this.getXWithOffset(23, 21),
            this.getYWithOffset(8),
            this.getZWithOffset(23, 21)
         );
         StructureBoundingBox structureBoundingBox2 = StructureBoundingBox.create(
            this.getXWithOffset(34, 1),
            this.getYWithOffset(1),
            this.getZWithOffset(34, 1),
            this.getXWithOffset(56, 21),
            this.getYWithOffset(8),
            this.getZWithOffset(56, 21)
         );
         StructureBoundingBox structureBoundingBox3 = StructureBoundingBox.create(
            this.getXWithOffset(22, 22),
            this.getYWithOffset(13),
            this.getZWithOffset(22, 22),
            this.getXWithOffset(35, 35),
            this.getYWithOffset(17),
            this.getZWithOffset(35, 35)
         );
         int nextInt = random.nextInt();
         this.pieceList.add(new StructureOceanMonumentPieces.WingRoom(this.coordBaseMode, structureBoundingBox, nextInt++));
         this.pieceList.add(new StructureOceanMonumentPieces.WingRoom(this.coordBaseMode, structureBoundingBox2, nextInt++));
         this.pieceList.add(new StructureOceanMonumentPieces.Penthouse(this.coordBaseMode, structureBoundingBox3));
      }

      public List<StructureOceanMonumentPieces.RoomDefinition> findList(Random random) {
         StructureOceanMonumentPieces.RoomDefinition[] roomDefinitions = new StructureOceanMonumentPieces.RoomDefinition[75];

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
               byte b = 0;
               int index = getIndex(i, b, j);
               roomDefinitions[index] = new StructureOceanMonumentPieces.RoomDefinition(index);
            }
         }

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
               byte b = 1;
               int index = getIndex(i, b, j);
               roomDefinitions[index] = new StructureOceanMonumentPieces.RoomDefinition(index);
            }
         }

         for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
               byte b = 2;
               int index = getIndex(i, b, j);
               roomDefinitions[index] = new StructureOceanMonumentPieces.RoomDefinition(index);
            }
         }

         this.roomDefinition = roomDefinitions[TWO_ZERO_ZERO_INDEX];

         for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
               for (int k = 0; k < 3; k++) {
                  int index = getIndex(i, k, j);
                  if (roomDefinitions[index] != null) {
                     for (EnumFacing enumFacing : EnumFacing.values()) {
                        int n = i + enumFacing.getFrontOffsetX();
                        int n2 = k + enumFacing.getFrontOffsetY();
                        int n3 = j + enumFacing.getFrontOffsetZ();
                        if (n >= 0 && n < 5 && n3 >= 0 && n3 < 5 && n2 >= 0 && n2 < 3) {
                           int index2 = getIndex(n, n2, n3);
                           if (roomDefinitions[index2] != null) {
                              if (n3 != j) {
                                 roomDefinitions[index].setNeighbor(enumFacing.getOpposite(), roomDefinitions[index2]);
                              } else {
                                 roomDefinitions[index].setNeighbor(enumFacing, roomDefinitions[index2]);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         StructureOceanMonumentPieces.RoomDefinition roomDefinition;
         roomDefinitions[TWO_TWO_ZERO_INDEX].setNeighbor(EnumFacing.UP, roomDefinition = new StructureOceanMonumentPieces.RoomDefinition(1003));
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2;
         roomDefinitions[ZERO_ONE_ZERO_INDEX].setNeighbor(EnumFacing.SOUTH, roomDefinition2 = new StructureOceanMonumentPieces.RoomDefinition(1001));
         StructureOceanMonumentPieces.RoomDefinition roomDefinition3;
         roomDefinitions[FOUR_ONE_ZERO_INDEX].setNeighbor(EnumFacing.SOUTH, roomDefinition3 = new StructureOceanMonumentPieces.RoomDefinition(1002));
         roomDefinition.used = true;
         roomDefinition2.used = true;
         roomDefinition3.used = true;
         this.roomDefinition.flag = true;
         this.roomDefinition2 = roomDefinitions[getIndex(random.nextInt(4), 0, 2)];
         this.roomDefinition2.used = true;
         this.roomDefinition2.neighbors[EnumFacing.EAST.getIndex()].used = true;
         this.roomDefinition2.neighbors[EnumFacing.NORTH.getIndex()].used = true;
         this.roomDefinition2.neighbors[EnumFacing.EAST.getIndex()].neighbors[EnumFacing.NORTH.getIndex()].used = true;
         this.roomDefinition2.neighbors[EnumFacing.UP.getIndex()].used = true;
         this.roomDefinition2.neighbors[EnumFacing.EAST.getIndex()].neighbors[EnumFacing.UP.getIndex()].used = true;
         this.roomDefinition2.neighbors[EnumFacing.NORTH.getIndex()].neighbors[EnumFacing.UP.getIndex()].used = true;
         this.roomDefinition2.neighbors[EnumFacing.EAST.getIndex()].neighbors[EnumFacing.NORTH.getIndex()].neighbors[EnumFacing.UP.getIndex()].used = true;
         ArrayList newArrayList = Lists.newArrayList();

         for (StructureOceanMonumentPieces.RoomDefinition roomDefinition4 : roomDefinitions) {
            if (roomDefinition4 != null) {
               roomDefinition4.checkNeighborStates();
               newArrayList.add(roomDefinition4);
            }
         }

         roomDefinition.checkNeighborStates();
         Collections.shuffle(newArrayList, random);
         int n = 1;

         for (StructureOceanMonumentPieces.RoomDefinition roomDefinition4 : (Iterable<StructureOceanMonumentPieces.RoomDefinition>)newArrayList) {
            int n2 = 0;
            int n3 = 0;

            while (n2 < 2 && n3 < 5) {
               n3++;
               int nextInt = random.nextInt(6);
               if (roomDefinition4.neighborPresences[nextInt]) {
                  int index = EnumFacing.getFront(nextInt).getOpposite().getIndex();
                  roomDefinition4.neighborPresences[nextInt] = false;
                  roomDefinition4.neighbors[nextInt].neighborPresences[index] = false;
                  if (roomDefinition4.check(n++) && roomDefinition4.neighbors[nextInt].check(n++)) {
                     n2++;
                  } else {
                     roomDefinition4.neighborPresences[nextInt] = true;
                     roomDefinition4.neighbors[nextInt].neighborPresences[index] = true;
                  }
               }
            }
         }

         newArrayList.add(roomDefinition);
         newArrayList.add(roomDefinition2);
         newArrayList.add(roomDefinition3);
         return newArrayList;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         int n = Math.max(worldIn.getSeaLevel(), 64) - this.boundingBox.minY;
         this.setAirAndWater(worldIn, structureBoundingBoxIn, 0, 0, 0, 58, n, 58, false);
         this.process(false, 0, worldIn, randomIn, structureBoundingBoxIn);
         this.process(true, 33, worldIn, randomIn, structureBoundingBoxIn);
         this.handleWorld(worldIn, randomIn, structureBoundingBoxIn);
         this.handleWorld2(worldIn, randomIn, structureBoundingBoxIn);
         this.handleWorld3(worldIn, randomIn, structureBoundingBoxIn);
         this.handleWorld4(worldIn, randomIn, structureBoundingBoxIn);
         this.handleWorld5(worldIn, randomIn, structureBoundingBoxIn);
         this.handleWorld6(worldIn, randomIn, structureBoundingBoxIn);

         for (int i = 0; i < 7; i++) {
            int n2 = 0;

            while (n2 < 7) {
               if (n2 == 0 && i == 3) {
                  n2 = 6;
               }

               int n3 = i * 9;
               int n4 = n2 * 9;

               for (int j = 0; j < 4; j++) {
                  for (int k = 0; k < 4; k++) {
                     this.setBlockState(worldIn, PRISMARINE_BRICKS, n3 + j, 0, n4 + k, structureBoundingBoxIn);
                     this.replaceAirAndLiquidDownwards(worldIn, PRISMARINE_BRICKS, n3 + j, -1, n4 + k, structureBoundingBoxIn);
                  }
               }

               if (i != 0 && i != 6) {
                  n2 += 6;
               } else {
                  n2++;
               }
            }
         }

         for (int i = 0; i < 5; i++) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, -1 - i, 0 + i * 2, -1 - i, -1 - i, 23, 58 + i, false);
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 58 + i, 0 + i * 2, -1 - i, 58 + i, 23, 58 + i, false);
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 0 - i, 0 + i * 2, -1 - i, 57 + i, 23, -1 - i, false);
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 0 - i, 0 + i * 2, 58 + i, 57 + i, 23, 58 + i, false);
         }

         for (StructureOceanMonumentPieces.Piece piece : this.pieceList) {
            if (piece.getBoundingBox().intersectsWith(structureBoundingBoxIn)) {
               piece.addComponentParts(worldIn, randomIn, structureBoundingBoxIn);
            }
         }

         return true;
      }

      public void process(boolean flag, int n, World worldIn, Random random, StructureBoundingBox structureBoundingBox) {
         byte b = 24;
         if (this.checkStructureBoundingBox(structureBoundingBox, n, 0, n + 23, 20)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 0, 0, 0, n + 24, 0, 20, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(worldIn, structureBoundingBox, n + 0, 1, 0, n + 24, 10, 20, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, n + i, i + 1, i, n + i, i + 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, n + i + 7, i + 5, i + 7, n + i + 7, i + 5, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, n + 17 - i, i + 5, i + 7, n + 17 - i, i + 5, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false
               );
               this.fillWithBlocks(worldIn, structureBoundingBox, n + 24 - i, i + 1, i, n + 24 - i, i + 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, n + i + 1, i + 1, i, n + 23 - i, i + 1, i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, n + i + 8, i + 5, i + 7, n + 16 - i, i + 5, i + 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false
               );
            }

            this.fillWithBlocks(worldIn, structureBoundingBox, n + 4, 4, 4, n + 6, 4, 20, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 7, 4, 4, n + 17, 4, 6, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 18, 4, 4, n + 20, 4, 20, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 11, 8, 11, n + 13, 8, 20, PRISMARINE, PRISMARINE, false);
            this.setBlockState(worldIn, PRISMARINE2, n + 12, 9, 12, structureBoundingBox);
            this.setBlockState(worldIn, PRISMARINE2, n + 12, 9, 15, structureBoundingBox);
            this.setBlockState(worldIn, PRISMARINE2, n + 12, 9, 18, structureBoundingBox);
            int n2 = flag ? n + 19 : n + 5;
            int n3 = flag ? n + 5 : n + 19;

            for (int i = 20; i >= 5; i -= 3) {
               this.setBlockState(worldIn, PRISMARINE2, n2, 5, i, structureBoundingBox);
            }

            for (int i = 19; i >= 7; i -= 3) {
               this.setBlockState(worldIn, PRISMARINE2, n3, 5, i, structureBoundingBox);
            }

            for (int i = 0; i < 4; i++) {
               int n4 = flag ? n + (24 - (17 - i * 3)) : n + 17 - i * 3;
               this.setBlockState(worldIn, PRISMARINE2, n4, 5, 5, structureBoundingBox);
            }

            this.setBlockState(worldIn, PRISMARINE2, n3, 5, 5, structureBoundingBox);
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 11, 1, 12, n + 13, 7, 12, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 12, 1, 11, n + 12, 7, 13, PRISMARINE, PRISMARINE, false);
         }
      }

      public void handleWorld(World worldIn, Random random, StructureBoundingBox structureBoundingBox) {
         if (this.checkStructureBoundingBox(structureBoundingBox, 22, 5, 35, 17)) {
            this.setAirAndWater(worldIn, structureBoundingBox, 25, 0, 0, 32, 8, 20, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, 24, 2, 5 + i * 4, 24, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBox, 22, 4, 5 + i * 4, 23, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.setBlockState(worldIn, PRISMARINE_BRICKS, 25, 5, 5 + i * 4, structureBoundingBox);
               this.setBlockState(worldIn, PRISMARINE_BRICKS, 26, 6, 5 + i * 4, structureBoundingBox);
               this.setBlockState(worldIn, SEA_LANTERN, 26, 5, 5 + i * 4, structureBoundingBox);
               this.fillWithBlocks(worldIn, structureBoundingBox, 33, 2, 5 + i * 4, 33, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBox, 34, 4, 5 + i * 4, 35, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.setBlockState(worldIn, PRISMARINE_BRICKS, 32, 5, 5 + i * 4, structureBoundingBox);
               this.setBlockState(worldIn, PRISMARINE_BRICKS, 31, 6, 5 + i * 4, structureBoundingBox);
               this.setBlockState(worldIn, SEA_LANTERN, 31, 5, 5 + i * 4, structureBoundingBox);
               this.fillWithBlocks(worldIn, structureBoundingBox, 27, 6, 5 + i * 4, 30, 6, 5 + i * 4, PRISMARINE, PRISMARINE, false);
            }
         }
      }

      public void handleWorld2(World worldIn, Random random, StructureBoundingBox structureBoundingBox) {
         if (this.checkStructureBoundingBox(structureBoundingBox, 15, 20, 42, 21)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 15, 0, 21, 42, 0, 21, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 26, 1, 21, 31, 3, 21, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 12, 21, 36, 12, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 17, 11, 21, 40, 11, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 16, 10, 21, 41, 10, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 15, 7, 21, 42, 9, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 16, 6, 21, 41, 6, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 17, 5, 21, 40, 5, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 4, 21, 36, 4, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 22, 3, 21, 26, 3, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 31, 3, 21, 35, 3, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 23, 2, 21, 25, 2, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 32, 2, 21, 34, 2, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 28, 4, 20, 29, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 27, 3, 21, structureBoundingBox);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 30, 3, 21, structureBoundingBox);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 26, 2, 21, structureBoundingBox);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 31, 2, 21, structureBoundingBox);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 25, 1, 21, structureBoundingBox);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 32, 1, 21, structureBoundingBox);

            for (int i = 0; i < 7; i++) {
               this.setBlockState(worldIn, DARK_PRISMARINE, 28 - i, 6 + i, 21, structureBoundingBox);
               this.setBlockState(worldIn, DARK_PRISMARINE, 29 + i, 6 + i, 21, structureBoundingBox);
            }

            for (int i = 0; i < 4; i++) {
               this.setBlockState(worldIn, DARK_PRISMARINE, 28 - i, 9 + i, 21, structureBoundingBox);
               this.setBlockState(worldIn, DARK_PRISMARINE, 29 + i, 9 + i, 21, structureBoundingBox);
            }

            this.setBlockState(worldIn, DARK_PRISMARINE, 28, 12, 21, structureBoundingBox);
            this.setBlockState(worldIn, DARK_PRISMARINE, 29, 12, 21, structureBoundingBox);

            for (int i = 0; i < 3; i++) {
               this.setBlockState(worldIn, DARK_PRISMARINE, 22 - i * 2, 8, 21, structureBoundingBox);
               this.setBlockState(worldIn, DARK_PRISMARINE, 22 - i * 2, 9, 21, structureBoundingBox);
               this.setBlockState(worldIn, DARK_PRISMARINE, 35 + i * 2, 8, 21, structureBoundingBox);
               this.setBlockState(worldIn, DARK_PRISMARINE, 35 + i * 2, 9, 21, structureBoundingBox);
            }

            this.setAirAndWater(worldIn, structureBoundingBox, 15, 13, 21, 42, 15, 21, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 15, 1, 21, 15, 6, 21, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 16, 1, 21, 16, 5, 21, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 17, 1, 21, 20, 4, 21, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 21, 1, 21, 21, 3, 21, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 22, 1, 21, 22, 2, 21, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 23, 1, 21, 24, 1, 21, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 42, 1, 21, 42, 6, 21, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 41, 1, 21, 41, 5, 21, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 37, 1, 21, 40, 4, 21, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 36, 1, 21, 36, 3, 21, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 33, 1, 21, 34, 1, 21, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 35, 1, 21, 35, 2, 21, false);
         }
      }

      public void handleWorld3(World worldIn, Random random, StructureBoundingBox structureBoundingBox) {
         if (this.checkStructureBoundingBox(structureBoundingBox, 21, 21, 36, 36)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 0, 22, 36, 0, 36, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 21, 1, 22, 36, 23, 36, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBox, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBox, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBox, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            this.fillWithBlocks(worldIn, structureBoundingBox, 25, 16, 25, 32, 16, 32, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 25, 17, 25, 25, 19, 25, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 32, 17, 25, 32, 19, 25, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 25, 17, 32, 25, 19, 32, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 32, 17, 32, 32, 19, 32, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 26, 20, 26, structureBoundingBox);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 27, 21, 27, structureBoundingBox);
            this.setBlockState(worldIn, SEA_LANTERN, 27, 20, 27, structureBoundingBox);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 26, 20, 31, structureBoundingBox);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 27, 21, 30, structureBoundingBox);
            this.setBlockState(worldIn, SEA_LANTERN, 27, 20, 30, structureBoundingBox);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 31, 20, 31, structureBoundingBox);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 30, 21, 30, structureBoundingBox);
            this.setBlockState(worldIn, SEA_LANTERN, 30, 20, 30, structureBoundingBox);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 31, 20, 26, structureBoundingBox);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 30, 21, 27, structureBoundingBox);
            this.setBlockState(worldIn, SEA_LANTERN, 30, 20, 27, structureBoundingBox);
            this.fillWithBlocks(worldIn, structureBoundingBox, 28, 21, 27, 29, 21, 27, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 27, 21, 28, 27, 21, 29, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 28, 21, 30, 29, 21, 30, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 30, 21, 28, 30, 21, 29, PRISMARINE, PRISMARINE, false);
         }
      }

      public void handleWorld4(World worldIn, Random random, StructureBoundingBox structureBoundingBox) {
         if (this.checkStructureBoundingBox(structureBoundingBox, 0, 21, 6, 58)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 0, 0, 21, 6, 0, 57, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 0, 1, 21, 6, 7, 57, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 4, 4, 21, 6, 4, 53, PRISMARINE, PRISMARINE, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, i, i + 1, 21, i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            for (int i = 23; i < 53; i += 3) {
               this.setBlockState(worldIn, PRISMARINE2, 5, 5, i, structureBoundingBox);
            }

            this.setBlockState(worldIn, PRISMARINE2, 5, 5, 52, structureBoundingBox);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, i, i + 1, 21, i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            this.fillWithBlocks(worldIn, structureBoundingBox, 4, 1, 52, 6, 3, 52, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 5, 1, 51, 5, 3, 53, PRISMARINE, PRISMARINE, false);
         }

         if (this.checkStructureBoundingBox(structureBoundingBox, 51, 21, 58, 58)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 51, 0, 21, 57, 0, 57, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 51, 1, 21, 57, 7, 57, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 51, 4, 21, 53, 4, 53, PRISMARINE, PRISMARINE, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, 57 - i, i + 1, 21, 57 - i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            for (int i = 23; i < 53; i += 3) {
               this.setBlockState(worldIn, PRISMARINE2, 52, 5, i, structureBoundingBox);
            }

            this.setBlockState(worldIn, PRISMARINE2, 52, 5, 52, structureBoundingBox);
            this.fillWithBlocks(worldIn, structureBoundingBox, 51, 1, 52, 53, 3, 52, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 52, 1, 51, 52, 3, 53, PRISMARINE, PRISMARINE, false);
         }

         if (this.checkStructureBoundingBox(structureBoundingBox, 0, 51, 57, 57)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 7, 0, 51, 50, 0, 57, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 7, 1, 51, 50, 10, 57, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, i + 1, i + 1, 57 - i, 56 - i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
         }
      }

      public void handleWorld5(World worldIn, Random random, StructureBoundingBox structureBoundingBox) {
         if (this.checkStructureBoundingBox(structureBoundingBox, 7, 21, 13, 50)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 7, 0, 21, 13, 0, 50, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 7, 1, 21, 13, 10, 50, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 11, 8, 21, 13, 8, 53, PRISMARINE, PRISMARINE, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, i + 7, i + 5, 21, i + 7, i + 5, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            for (int i = 21; i <= 45; i += 3) {
               this.setBlockState(worldIn, PRISMARINE2, 12, 9, i, structureBoundingBox);
            }
         }

         if (this.checkStructureBoundingBox(structureBoundingBox, 44, 21, 50, 54)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 44, 0, 21, 50, 0, 50, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 44, 1, 21, 50, 10, 50, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 44, 8, 21, 46, 8, 53, PRISMARINE, PRISMARINE, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, 50 - i, i + 5, 21, 50 - i, i + 5, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            for (int i = 21; i <= 45; i += 3) {
               this.setBlockState(worldIn, PRISMARINE2, 45, 9, i, structureBoundingBox);
            }
         }

         if (this.checkStructureBoundingBox(structureBoundingBox, 8, 44, 49, 54)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 14, 0, 44, 43, 0, 50, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 14, 1, 44, 43, 10, 50, false);

            for (int i = 12; i <= 45; i += 3) {
               this.setBlockState(worldIn, PRISMARINE2, i, 9, 45, structureBoundingBox);
               this.setBlockState(worldIn, PRISMARINE2, i, 9, 52, structureBoundingBox);
               if (i == 12 || i == 18 || i == 24 || i == 33 || i == 39 || i == 45) {
                  this.setBlockState(worldIn, PRISMARINE2, i, 9, 47, structureBoundingBox);
                  this.setBlockState(worldIn, PRISMARINE2, i, 9, 50, structureBoundingBox);
                  this.setBlockState(worldIn, PRISMARINE2, i, 10, 45, structureBoundingBox);
                  this.setBlockState(worldIn, PRISMARINE2, i, 10, 46, structureBoundingBox);
                  this.setBlockState(worldIn, PRISMARINE2, i, 10, 51, structureBoundingBox);
                  this.setBlockState(worldIn, PRISMARINE2, i, 10, 52, structureBoundingBox);
                  this.setBlockState(worldIn, PRISMARINE2, i, 11, 47, structureBoundingBox);
                  this.setBlockState(worldIn, PRISMARINE2, i, 11, 50, structureBoundingBox);
                  this.setBlockState(worldIn, PRISMARINE2, i, 12, 48, structureBoundingBox);
                  this.setBlockState(worldIn, PRISMARINE2, i, 12, 49, structureBoundingBox);
               }
            }

            for (int i = 0; i < 3; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, 8 + i, 5 + i, 54, 49 - i, 5 + i, 54, PRISMARINE, PRISMARINE, false);
            }

            this.fillWithBlocks(worldIn, structureBoundingBox, 11, 8, 54, 46, 8, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 14, 8, 44, 43, 8, 53, PRISMARINE, PRISMARINE, false);
         }
      }

      public void handleWorld6(World worldIn, Random random, StructureBoundingBox structureBoundingBox) {
         if (this.checkStructureBoundingBox(structureBoundingBox, 14, 21, 20, 43)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 14, 0, 21, 20, 0, 43, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 14, 1, 22, 20, 14, 43, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 18, 12, 22, 20, 12, 39, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 18, 12, 21, 20, 12, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            for (int i = 23; i <= 39; i += 3) {
               this.setBlockState(worldIn, PRISMARINE2, 19, 13, i, structureBoundingBox);
            }
         }

         if (this.checkStructureBoundingBox(structureBoundingBox, 37, 21, 43, 43)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 37, 0, 21, 43, 0, 43, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 37, 1, 22, 43, 14, 43, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 37, 12, 22, 39, 12, 39, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 37, 12, 21, 39, 12, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, 43 - i, i + 9, 21, 43 - i, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            for (int i = 23; i <= 39; i += 3) {
               this.setBlockState(worldIn, PRISMARINE2, 38, 13, i, structureBoundingBox);
            }
         }

         if (this.checkStructureBoundingBox(structureBoundingBox, 15, 37, 42, 43)) {
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 0, 37, 36, 0, 43, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(worldIn, structureBoundingBox, 21, 1, 37, 36, 14, 43, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, 21, 12, 37, 36, 12, 39, PRISMARINE, PRISMARINE, false);

            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBox, 15 + i, i + 9, 43 - i, 42 - i, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            for (int i = 21; i <= 36; i += 3) {
               this.setBlockState(worldIn, PRISMARINE2, i, 13, 38, structureBoundingBox);
            }
         }
      }
   }

   public static class MonumentCoreRoom extends StructureOceanMonumentPieces.Piece {
      public MonumentCoreRoom() {
      }

      public MonumentCoreRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         super(1, enumFacing, roomDefinition, 2, 2, 2);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.handleWorld(worldIn, structureBoundingBoxIn, 1, 8, 0, 14, 8, 14, PRISMARINE);
         byte b = 7;
         IBlockState blockState = PRISMARINE_BRICKS;
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, b, 0, 0, b, 15, blockState, blockState, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, b, 0, 15, b, 15, blockState, blockState, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, b, 0, 15, b, 0, blockState, blockState, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, b, 15, 14, b, 15, blockState, blockState, false);

         for (int i = 1; i <= 6; i++) {
            blockState = PRISMARINE_BRICKS;
            if (i == 2 || i == 6) {
               blockState = PRISMARINE;
            }

            for (int j = 0; j <= 15; j += 15) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, j, i, 0, j, i, 1, blockState, blockState, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, j, i, 6, j, i, 9, blockState, blockState, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, j, i, 14, j, i, 15, blockState, blockState, false);
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, i, 0, 1, i, 0, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, i, 0, 9, i, 0, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 14, i, 0, 14, i, 0, blockState, blockState, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, i, 15, 14, i, 15, blockState, blockState, false);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 6, 9, 6, 9, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 4, 7, 8, 5, 8, Blocks.gold_block.getDefaultState(), Blocks.gold_block.getDefaultState(), false);

         for (int i = 3; i <= 6; i += 3) {
            for (int j = 6; j <= 9; j += 3) {
               this.setBlockState(worldIn, SEA_LANTERN, j, i, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, SEA_LANTERN, j, i, 9, structureBoundingBoxIn);
            }
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 6, 5, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 9, 5, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 1, 6, 10, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 1, 9, 10, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 5, 6, 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 5, 9, 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 10, 6, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 10, 9, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 5, 5, 6, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 10, 5, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 2, 5, 10, 6, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 2, 10, 10, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 7, 1, 5, 7, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 7, 1, 10, 7, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 7, 9, 5, 7, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 7, 9, 10, 7, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 7, 5, 6, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 7, 10, 6, 7, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 7, 5, 14, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 7, 10, 14, 7, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 2, 2, 1, 3, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 3, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 2, 13, 1, 3, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 2, 12, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 12, 2, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 13, 3, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 12, 13, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 13, 12, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         return true;
      }
   }

   interface MonumentRoomFitHelper {
      boolean checkRoomDefinition(StructureOceanMonumentPieces.RoomDefinition roomDefinition);

      StructureOceanMonumentPieces.Piece createDoubleZRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random);
   }

   public static class Penthouse extends StructureOceanMonumentPieces.Piece {
      public Penthouse() {
      }

      public Penthouse(EnumFacing enumFacing, StructureBoundingBox structureBoundingBox) {
         super(enumFacing, structureBoundingBox);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, -1, 2, 11, -1, 11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, -1, 0, 1, -1, 11, PRISMARINE, PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, -1, 0, 13, -1, 11, PRISMARINE, PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, -1, 0, 11, -1, 1, PRISMARINE, PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, -1, 12, 11, -1, 13, PRISMARINE, PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 0, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 0, 0, 13, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 0, 12, 0, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 13, 12, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);

         for (int i = 2; i <= 11; i += 3) {
            this.setBlockState(worldIn, SEA_LANTERN, 0, 0, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 13, 0, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, i, 0, 0, structureBoundingBoxIn);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, 3, 4, 0, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 0, 3, 11, 0, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 0, 9, 9, 0, 11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.setBlockState(worldIn, PRISMARINE_BRICKS, 5, 0, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, PRISMARINE_BRICKS, 8, 0, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, PRISMARINE_BRICKS, 10, 0, 10, structureBoundingBoxIn);
         this.setBlockState(worldIn, PRISMARINE_BRICKS, 3, 0, 10, structureBoundingBoxIn);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 0, 3, 3, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 0, 3, 10, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 10, 7, 0, 10, DARK_PRISMARINE, DARK_PRISMARINE, false);
         int n = 3;

         for (int i = 0; i < 2; i++) {
            for (int j = 2; j <= 8; j += 3) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n, 0, j, n, 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            n = 10;
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 10, 5, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 10, 8, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, -1, 7, 7, -1, 8, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.setAirAndWater(worldIn, structureBoundingBoxIn, 6, -1, 3, 7, -1, 4, false);
         this.checkWorld(worldIn, structureBoundingBoxIn, 6, 1, 6);
         return true;
      }
   }

   public abstract static class Piece extends StructureComponent {
      public static IBlockState PRISMARINE = Blocks.prismarine.getStateFromMeta(BlockPrismarine.ROUGH_META);
      public static IBlockState PRISMARINE_BRICKS = Blocks.prismarine.getStateFromMeta(BlockPrismarine.BRICKS_META);
      public static IBlockState DARK_PRISMARINE = Blocks.prismarine.getStateFromMeta(BlockPrismarine.DARK_META);
      public static IBlockState PRISMARINE2 = PRISMARINE_BRICKS;
      public static IBlockState SEA_LANTERN = Blocks.sea_lantern.getDefaultState();
      public static IBlockState WATER = Blocks.water.getDefaultState();
      public static int TWO_ZERO_ZERO_INDEX = getIndex(2, 0, 0);
      public static int TWO_TWO_ZERO_INDEX = getIndex(2, 2, 0);
      public static int ZERO_ONE_ZERO_INDEX = getIndex(0, 1, 0);
      public static int FOUR_ONE_ZERO_INDEX = getIndex(4, 1, 0);
      public StructureOceanMonumentPieces.RoomDefinition setting;

      public static int getIndex(int n, int n2, int n3) {
         return n2 * 25 + n3 * 5 + n;
      }

      public Piece() {
         super(0);
      }

      public Piece(int n) {
         super(n);
      }

      public Piece(EnumFacing enumFacing, StructureBoundingBox structureBoundingBox) {
         super(1);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      public Piece(int n, EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, int n2, int n3, int n4) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.setting = roomDefinition;
         int roomIndex = roomDefinition.roomIndex;
         int n5 = roomIndex % 5;
         int n6 = roomIndex / 5 % 5;
         int n7 = roomIndex / 25;
         if (enumFacing != EnumFacing.NORTH && enumFacing != EnumFacing.SOUTH) {
            this.boundingBox = new StructureBoundingBox(0, 0, 0, n4 * 8 - 1, n3 * 4 - 1, n2 * 8 - 1);
         } else {
            this.boundingBox = new StructureBoundingBox(0, 0, 0, n2 * 8 - 1, n3 * 4 - 1, n4 * 8 - 1);
         }

         switch (enumFacing) {
            case NORTH:
               this.boundingBox.offset(n5 * 8, n7 * 4, -(n6 + n4) * 8 + 1);
               break;
            case SOUTH:
               this.boundingBox.offset(n5 * 8, n7 * 4, n6 * 8);
               break;
            case WEST:
               this.boundingBox.offset(-(n6 + n4) * 8 + 1, n7 * 4, n5 * 8);
               break;
            default:
               this.boundingBox.offset(n6 * 8, n7 * 4, n5 * 8);
         }
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
      }

      public void setAirAndWater(World world, StructureBoundingBox structureBoundingBox, int n, int n2, int n3, int n4, int n5, int n6, boolean flag) {
         for (int i = n2; i <= n5; i++) {
            for (int j = n; j <= n4; j++) {
               for (int k = n3; k <= n6; k++) {
                  if (!flag || this.getBlockStateFromPos(world, j, i, k, structureBoundingBox).getBlock().getMaterial() != Material.air) {
                     if (this.getYWithOffset(i) >= world.getSeaLevel()) {
                        this.setBlockState(world, Blocks.air.getDefaultState(), j, i, k, structureBoundingBox);
                     } else {
                        this.setBlockState(world, WATER, j, i, k, structureBoundingBox);
                     }
                  }
               }
            }
         }
      }

      public void handleWorld(World worldIn, StructureBoundingBox structureBoundingBox, int n, int n2, boolean flag) {
         if (flag) {
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 0, 0, n2 + 0, n + 2, 0, n2 + 8 - 1, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 5, 0, n2 + 0, n + 8 - 1, 0, n2 + 8 - 1, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 3, 0, n2 + 0, n + 4, 0, n2 + 2, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 3, 0, n2 + 5, n + 4, 0, n2 + 8 - 1, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 3, 0, n2 + 2, n + 4, 0, n2 + 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 3, 0, n2 + 5, n + 4, 0, n2 + 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 2, 0, n2 + 3, n + 2, 0, n2 + 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 5, 0, n2 + 3, n + 5, 0, n2 + 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         } else {
            this.fillWithBlocks(worldIn, structureBoundingBox, n + 0, 0, n2 + 0, n + 8 - 1, 0, n2 + 8 - 1, PRISMARINE, PRISMARINE, false);
         }
      }

      public void handleWorld(World worldIn, StructureBoundingBox structureBoundingBox, int n, int n2, int n3, int n4, int n5, int n6, IBlockState blockState) {
         for (int i = n2; i <= n5; i++) {
            for (int j = n; j <= n4; j++) {
               for (int k = n3; k <= n6; k++) {
                  if (this.getBlockStateFromPos(worldIn, j, i, k, structureBoundingBox) == WATER) {
                     this.setBlockState(worldIn, blockState, j, i, k, structureBoundingBox);
                  }
               }
            }
         }
      }

      public boolean checkStructureBoundingBox(StructureBoundingBox structureBoundingBox, int n, int n2, int n3, int n4) {
         int xWithOffset = this.getXWithOffset(n, n2);
         int zWithOffset = this.getZWithOffset(n, n2);
         int xWithOffset2 = this.getXWithOffset(n3, n4);
         int zWithOffset2 = this.getZWithOffset(n3, n4);
         return structureBoundingBox.intersectsWith(Math.min(xWithOffset, xWithOffset2), Math.min(zWithOffset, zWithOffset2), Math.max(xWithOffset, xWithOffset2), Math.max(zWithOffset, zWithOffset2));
      }

      public boolean checkWorld(World worldIn, StructureBoundingBox structureBoundingBox, int n, int n2, int n3) {
         int xWithOffset = this.getXWithOffset(n, n3);
         int yWithOffset = this.getYWithOffset(n2);
         int zWithOffset = this.getZWithOffset(n, n3);
         if (structureBoundingBox.isVecInside(new BlockPos(xWithOffset, yWithOffset, zWithOffset))) {
            EntityGuardian entityGuardian = new EntityGuardian(worldIn);
            entityGuardian.setElder(true);
            entityGuardian.heal(entityGuardian.getMaxHealth());
            entityGuardian.setLocationAndAngles(xWithOffset + 0.5, yWithOffset, zWithOffset + 0.5, 0.0F, 0.0F);
            entityGuardian.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityGuardian)), (IEntityLivingData)null);
            worldIn.spawnEntityInWorld(entityGuardian);
            return true;
         } else {
            return false;
         }
      }
   }

   static class RoomDefinition {
      public StructureOceanMonumentPieces.RoomDefinition[] neighbors = new StructureOceanMonumentPieces.RoomDefinition[6];
      public boolean[] neighborPresences = new boolean[6];
      public int roomIndex;
      public boolean used;
      public boolean flag;
      public int intValue;

      public RoomDefinition(int n) {
         this.roomIndex = n;
      }

      public void setNeighbor(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         this.neighbors[enumFacing.getIndex()] = roomDefinition;
         roomDefinition.neighbors[enumFacing.getOpposite().getIndex()] = this;
      }

      public void checkNeighborStates() {
         for (int i = 0; i < 6; i++) {
            this.neighborPresences[i] = this.neighbors[i] != null;
         }
      }

      public boolean check(int n) {
         if (this.flag) {
            return true;
         }

         this.intValue = n;

         for (int i = 0; i < 6; i++) {
            if (this.neighbors[i] != null && this.neighborPresences[i] && this.neighbors[i].intValue != n && this.neighbors[i].check(n)) {
               return true;
            }
         }

         return false;
      }

      public boolean isAboveLevelThree() {
         return this.roomIndex >= 75;
      }

      public int countNeighbors() {
         int n = 0;

         for (int i = 0; i < 6; i++) {
            if (this.neighborPresences[i]) {
               n++;
            }
         }

         return n;
      }
   }

   public static class SimpleRoom extends StructureOceanMonumentPieces.Piece {
      public int roomType;

      public SimpleRoom() {
      }

      public SimpleRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         super(1, enumFacing, roomDefinition, 1, 1, 1);
         this.roomType = random.nextInt(3);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.setting.roomIndex / 25 > 0) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 0, this.setting.neighborPresences[EnumFacing.DOWN.getIndex()]);
         }

         if (this.setting.neighbors[EnumFacing.UP.getIndex()] == null) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 1, 4, 1, 6, 4, 6, PRISMARINE);
         }

         boolean flag = this.roomType != 0
            && randomIn.nextBoolean()
            && !this.setting.neighborPresences[EnumFacing.DOWN.getIndex()]
            && !this.setting.neighborPresences[EnumFacing.UP.getIndex()]
            && this.setting.countNeighbors() > 1;
         if (this.roomType == 0) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 2, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 2, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 2, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 2, 2, 0, PRISMARINE, PRISMARINE, false);
            this.setBlockState(worldIn, SEA_LANTERN, 1, 2, 1, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 0, 7, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 3, 0, 7, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 0, 7, 2, 2, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 0, 6, 2, 0, PRISMARINE, PRISMARINE, false);
            this.setBlockState(worldIn, SEA_LANTERN, 6, 2, 1, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 5, 2, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 5, 2, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 5, 0, 2, 7, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 2, 2, 7, PRISMARINE, PRISMARINE, false);
            this.setBlockState(worldIn, SEA_LANTERN, 1, 2, 6, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 5, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 3, 5, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 5, 7, 2, 7, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 7, 6, 2, 7, PRISMARINE, PRISMARINE, false);
            this.setBlockState(worldIn, SEA_LANTERN, 6, 2, 6, structureBoundingBoxIn);
            if (this.setting.neighborPresences[EnumFacing.SOUTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 0, 4, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 0, 4, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 0, 4, 2, 0, PRISMARINE, PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 1, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            if (this.setting.neighborPresences[EnumFacing.NORTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 7, 4, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 6, 4, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 7, 4, 2, 7, PRISMARINE, PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 6, 4, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            if (this.setting.neighborPresences[EnumFacing.WEST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 3, 0, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 3, 1, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 3, 0, 2, 4, PRISMARINE, PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 3, 1, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            if (this.setting.neighborPresences[EnumFacing.EAST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 3, 7, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 3, 7, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 3, 7, 2, 4, PRISMARINE, PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 3, 7, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
         } else if (this.roomType == 1) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 2, 2, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 5, 2, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 5, 5, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 2, 5, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.setBlockState(worldIn, SEA_LANTERN, 2, 2, 2, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 2, 2, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 5, 2, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 5, 2, 2, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 1, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 7, 1, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 6, 0, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 7, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 6, 7, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 0, 7, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 1, 7, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.setBlockState(worldIn, PRISMARINE, 1, 2, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE, 0, 2, 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE, 1, 2, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE, 0, 2, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE, 6, 2, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE, 7, 2, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE, 6, 2, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE, 7, 2, 1, structureBoundingBoxIn);
            if (!this.setting.neighborPresences[EnumFacing.SOUTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 6, 2, 0, PRISMARINE, PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            if (!this.setting.neighborPresences[EnumFacing.NORTH.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 6, 2, 7, PRISMARINE, PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            if (!this.setting.neighborPresences[EnumFacing.WEST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 1, 0, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 1, 0, 2, 6, PRISMARINE, PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 1, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }

            if (!this.setting.neighborPresences[EnumFacing.EAST.getIndex()]) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 1, 7, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 1, 7, 2, 6, PRISMARINE, PRISMARINE, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 1, 7, 1, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
         } else if (this.roomType == 2) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            if (this.setting.neighborPresences[EnumFacing.SOUTH.getIndex()]) {
               this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, false);
            }

            if (this.setting.neighborPresences[EnumFacing.NORTH.getIndex()]) {
               this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7, false);
            }

            if (this.setting.neighborPresences[EnumFacing.WEST.getIndex()]) {
               this.setAirAndWater(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, false);
            }

            if (this.setting.neighborPresences[EnumFacing.EAST.getIndex()]) {
               this.setAirAndWater(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4, false);
            }
         }

         if (flag) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 3, 4, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 2, 3, 4, 2, 4, PRISMARINE, PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 3, 3, 4, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         }

         return true;
      }
   }

   public static class SimpleTopRoom extends StructureOceanMonumentPieces.Piece {
      public SimpleTopRoom() {
      }

      public SimpleTopRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         super(1, enumFacing, roomDefinition, 1, 1, 1);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.setting.roomIndex / 25 > 0) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 0, 0, this.setting.neighborPresences[EnumFacing.DOWN.getIndex()]);
         }

         if (this.setting.neighbors[EnumFacing.UP.getIndex()] == null) {
            this.handleWorld(worldIn, structureBoundingBoxIn, 1, 4, 1, 6, 4, 6, PRISMARINE);
         }

         for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 6; j++) {
               if (randomIn.nextInt(3) != 0) {
                  int n = 2 + (randomIn.nextInt(4) == 0 ? 0 : 1);
                  this.fillWithBlocks(
                     worldIn,
                     structureBoundingBoxIn,
                     i,
                     n,
                     j,
                     i,
                     3,
                     j,
                     Blocks.sponge.getStateFromMeta(1),
                     Blocks.sponge.getStateFromMeta(1),
                     false
                  );
               }
            }
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         if (this.setting.neighborPresences[EnumFacing.SOUTH.getIndex()]) {
            this.setAirAndWater(worldIn, structureBoundingBoxIn, 3, 1, 0, 4, 2, 0, false);
         }

         return true;
      }
   }

   public static class WingRoom extends StructureOceanMonumentPieces.Piece {
      public int intValue;

      public WingRoom() {
      }

      public WingRoom(EnumFacing enumFacing, StructureBoundingBox structureBoundingBox, int n) {
         super(enumFacing, structureBoundingBox);
         this.intValue = n & 1;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.intValue == 0) {
            for (int i = 0; i < 4; i++) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false
               );
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 0, 6, 15, 0, 16, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 6, 6, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 16, 0, 6, 16, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 7, 7, 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 1, 7, 15, 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 1, 6, 9, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 6, 15, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 1, 7, 9, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 1, 7, 14, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 0, 5, 13, 0, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 0, 7, 12, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 10, 8, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 14, 0, 10, 14, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);

            for (int i = 18; i >= 7; i -= 3) {
               this.setBlockState(worldIn, SEA_LANTERN, 6, 3, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, SEA_LANTERN, 16, 3, i, structureBoundingBoxIn);
            }

            this.setBlockState(worldIn, SEA_LANTERN, 10, 0, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 12, 0, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 10, 0, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 12, 0, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 8, 3, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 14, 3, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 4, 2, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 4, 1, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 4, 0, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 18, 2, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 18, 1, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 18, 0, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 4, 2, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 4, 1, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 4, 0, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 18, 2, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, SEA_LANTERN, 18, 1, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 18, 0, 18, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 9, 7, 20, structureBoundingBoxIn);
            this.setBlockState(worldIn, PRISMARINE_BRICKS, 13, 7, 20, structureBoundingBoxIn);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 0, 21, 7, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 15, 0, 21, 16, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.checkWorld(worldIn, structureBoundingBoxIn, 11, 2, 16);
         } else if (this.intValue == 1) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 3, 18, 13, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 0, 18, 9, 2, 18, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 0, 18, 13, 2, 18, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            int n = 9;
            byte b = 20;
            byte b2 = 5;

            for (int i = 0; i < 2; i++) {
               this.setBlockState(worldIn, PRISMARINE_BRICKS, n, b2 + 1, b, structureBoundingBoxIn);
               this.setBlockState(worldIn, SEA_LANTERN, n, b2, b, structureBoundingBoxIn);
               this.setBlockState(worldIn, PRISMARINE_BRICKS, n, b2 - 1, b, structureBoundingBoxIn);
               n = 13;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 3, 7, 15, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            n = 10;

            for (int i = 0; i < 2; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n, 0, 10, n, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n, 0, 12, n, 6, 12, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.setBlockState(worldIn, SEA_LANTERN, n, 0, 10, structureBoundingBoxIn);
               this.setBlockState(worldIn, SEA_LANTERN, n, 0, 12, structureBoundingBoxIn);
               this.setBlockState(worldIn, SEA_LANTERN, n, 4, 10, structureBoundingBoxIn);
               this.setBlockState(worldIn, SEA_LANTERN, n, 4, 12, structureBoundingBoxIn);
               n = 12;
            }

            n = 8;

            for (int i = 0; i < 2; i++) {
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n, 0, 7, n, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               this.fillWithBlocks(worldIn, structureBoundingBoxIn, n, 0, 14, n, 2, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
               n = 14;
            }

            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 3, 8, 8, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 14, 3, 8, 14, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.checkWorld(worldIn, structureBoundingBoxIn, 11, 5, 13);
         }

         return true;
      }
   }

   static class XDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      public XDoubleRoomFitHelper() {
      }

      @Override
      public boolean checkRoomDefinition(StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         return roomDefinition.neighborPresences[EnumFacing.EAST.getIndex()] && !roomDefinition.neighbors[EnumFacing.EAST.getIndex()].used;
      }

      @Override
      public StructureOceanMonumentPieces.Piece createDoubleZRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         roomDefinition.used = true;
         roomDefinition.neighbors[EnumFacing.EAST.getIndex()].used = true;
         return new StructureOceanMonumentPieces.DoubleXRoom(enumFacing, roomDefinition, random);
      }
   }

   static class XYDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      public XYDoubleRoomFitHelper() {
      }

      @Override
      public boolean checkRoomDefinition(StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         if (roomDefinition.neighborPresences[EnumFacing.EAST.getIndex()]
            && !roomDefinition.neighbors[EnumFacing.EAST.getIndex()].used
            && roomDefinition.neighborPresences[EnumFacing.UP.getIndex()]
            && !roomDefinition.neighbors[EnumFacing.UP.getIndex()].used) {
            StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = roomDefinition.neighbors[EnumFacing.EAST.getIndex()];
            return roomDefinition2.neighborPresences[EnumFacing.UP.getIndex()] && !roomDefinition2.neighbors[EnumFacing.UP.getIndex()].used;
         } else {
            return false;
         }
      }

      @Override
      public StructureOceanMonumentPieces.Piece createDoubleZRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         roomDefinition.used = true;
         roomDefinition.neighbors[EnumFacing.EAST.getIndex()].used = true;
         roomDefinition.neighbors[EnumFacing.UP.getIndex()].used = true;
         roomDefinition.neighbors[EnumFacing.EAST.getIndex()].neighbors[EnumFacing.UP.getIndex()].used = true;
         return new StructureOceanMonumentPieces.DoubleXYRoom(enumFacing, roomDefinition, random);
      }
   }

   static class YDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      public YDoubleRoomFitHelper() {
      }

      @Override
      public boolean checkRoomDefinition(StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         return roomDefinition.neighborPresences[EnumFacing.UP.getIndex()] && !roomDefinition.neighbors[EnumFacing.UP.getIndex()].used;
      }

      @Override
      public StructureOceanMonumentPieces.Piece createDoubleZRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         roomDefinition.used = true;
         roomDefinition.neighbors[EnumFacing.UP.getIndex()].used = true;
         return new StructureOceanMonumentPieces.DoubleYRoom(enumFacing, roomDefinition, random);
      }
   }

   static class YZDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      public YZDoubleRoomFitHelper() {
      }

      @Override
      public boolean checkRoomDefinition(StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         if (roomDefinition.neighborPresences[EnumFacing.NORTH.getIndex()]
            && !roomDefinition.neighbors[EnumFacing.NORTH.getIndex()].used
            && roomDefinition.neighborPresences[EnumFacing.UP.getIndex()]
            && !roomDefinition.neighbors[EnumFacing.UP.getIndex()].used) {
            StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = roomDefinition.neighbors[EnumFacing.NORTH.getIndex()];
            return roomDefinition2.neighborPresences[EnumFacing.UP.getIndex()] && !roomDefinition2.neighbors[EnumFacing.UP.getIndex()].used;
         } else {
            return false;
         }
      }

      @Override
      public StructureOceanMonumentPieces.Piece createDoubleZRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         roomDefinition.used = true;
         roomDefinition.neighbors[EnumFacing.NORTH.getIndex()].used = true;
         roomDefinition.neighbors[EnumFacing.UP.getIndex()].used = true;
         roomDefinition.neighbors[EnumFacing.NORTH.getIndex()].neighbors[EnumFacing.UP.getIndex()].used = true;
         return new StructureOceanMonumentPieces.DoubleYZRoom(enumFacing, roomDefinition, random);
      }
   }

   static class ZDoubleRoomFitHelper implements StructureOceanMonumentPieces.MonumentRoomFitHelper {
      public ZDoubleRoomFitHelper() {
      }

      @Override
      public boolean checkRoomDefinition(StructureOceanMonumentPieces.RoomDefinition roomDefinition) {
         return roomDefinition.neighborPresences[EnumFacing.NORTH.getIndex()] && !roomDefinition.neighbors[EnumFacing.NORTH.getIndex()].used;
      }

      @Override
      public StructureOceanMonumentPieces.Piece createDoubleZRoom(EnumFacing enumFacing, StructureOceanMonumentPieces.RoomDefinition roomDefinition, Random random) {
         StructureOceanMonumentPieces.RoomDefinition roomDefinition2 = roomDefinition;
         if (!roomDefinition.neighborPresences[EnumFacing.NORTH.getIndex()] || roomDefinition.neighbors[EnumFacing.NORTH.getIndex()].used) {
            roomDefinition2 = roomDefinition.neighbors[EnumFacing.SOUTH.getIndex()];
         }

         roomDefinition2.used = true;
         roomDefinition2.neighbors[EnumFacing.NORTH.getIndex()].used = true;
         return new StructureOceanMonumentPieces.DoubleZRoom(enumFacing, roomDefinition2, random);
      }
   }
}
