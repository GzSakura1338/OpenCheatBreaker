package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureMineshaftPieces {
   public static List<WeightedRandomChestContent> CHEST_CONTENT_WEIGHT_LIST = Lists.newArrayList(
      new WeightedRandomChestContent[]{
         new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
         new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5),
         new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5),
         new WeightedRandomChestContent(Items.dye, EnumDyeColor.BLUE.getDyeDamage(), 4, 9, 5),
         new WeightedRandomChestContent(Items.diamond, 0, 1, 2, 3),
         new WeightedRandomChestContent(Items.coal, 0, 3, 8, 10),
         new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15),
         new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 1),
         new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.rail), 0, 4, 8, 1),
         new WeightedRandomChestContent(Items.melon_seeds, 0, 2, 4, 10),
         new WeightedRandomChestContent(Items.pumpkin_seeds, 0, 2, 4, 10),
         new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3),
         new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1)
      }
   );

   public static void registerStructurePieces() {
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Corridor.class, "MSCorridor");
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Cross.class, "MSCrossing");
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Room.class, "MSRoom");
      MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Stairs.class, "MSStairs");
   }

   public static StructureComponent pickPiece(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing, int type) {
      int nextInt = rand.nextInt(100);
      if (nextInt >= 80) {
         StructureBoundingBox boundingBox = StructureMineshaftPieces.Cross.getBoundingBox(listIn, rand, x, y, z, facing);
         if (boundingBox != null) {
            return new StructureMineshaftPieces.Cross(type, rand, boundingBox, facing);
         }
      } else if (nextInt >= 70) {
         StructureBoundingBox boundingBox = StructureMineshaftPieces.Stairs.getBoundingBox(listIn, rand, x, y, z, facing);
         if (boundingBox != null) {
            return new StructureMineshaftPieces.Stairs(type, rand, boundingBox, facing);
         }
      } else {
         StructureBoundingBox boundingBox = StructureMineshaftPieces.Corridor.getBoundingBox(listIn, rand, x, y, z, facing);
         if (boundingBox != null) {
            return new StructureMineshaftPieces.Corridor(type, rand, boundingBox, facing);
         }
      }

      return null;
   }

   public static StructureComponent pieceGenerator(
      StructureComponent componentIn, List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing, int type
   ) {
      if (type > 8) {
         return null;
      }

      if (Math.abs(x - componentIn.getBoundingBox().minX) <= 80 && Math.abs(z - componentIn.getBoundingBox().minZ) <= 80) {
         StructureComponent pickPiece2 = pickPiece(listIn, rand, x, y, z, facing, type + 1);
         if (pickPiece2 != null) {
            listIn.add(pickPiece2);
            pickPiece2.buildComponent(componentIn, listIn, rand);
         }

         return pickPiece2;
      } else {
         return null;
      }
   }

   public static class Corridor extends StructureComponent {
      public boolean hasRails;
      public boolean hasSpiders;
      public boolean spawnerPlaced;
      public int sectionCount;

      public Corridor() {
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         tagCompound.setBoolean("hr", this.hasRails);
         tagCompound.setBoolean("sc", this.hasSpiders);
         tagCompound.setBoolean("hps", this.spawnerPlaced);
         tagCompound.setInteger("Num", this.sectionCount);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         this.hasRails = tagCompound.getBoolean("hr");
         this.hasSpiders = tagCompound.getBoolean("sc");
         this.spawnerPlaced = tagCompound.getBoolean("hps");
         this.sectionCount = tagCompound.getInteger("Num");
      }

      public Corridor(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing) {
         super(type);
         this.coordBaseMode = facing;
         this.boundingBox = structurebb;
         this.hasRails = rand.nextInt(3) == 0;
         this.hasSpiders = !this.hasRails && rand.nextInt(23) == 0;
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.SOUTH) {
            this.sectionCount = structurebb.getXSize() / 5;
         } else {
            this.sectionCount = structurebb.getZSize() / 5;
         }
      }

      public static StructureBoundingBox getBoundingBox(List<StructureComponent> list, Random rand, int x, int y, int z, EnumFacing facing) {
         StructureBoundingBox structureBoundingBox = new StructureBoundingBox(x, y, z, x, y + 2, z);

         int n;
         for (n = rand.nextInt(3) + 2; n > 0; n--) {
            int n2 = n * 5;
            switch (facing) {
               case NORTH:
                  structureBoundingBox.maxX = x + 2;
                  structureBoundingBox.minZ = z - (n2 - 1);
                  break;
               case SOUTH:
                  structureBoundingBox.maxX = x + 2;
                  structureBoundingBox.maxZ = z + (n2 - 1);
                  break;
               case WEST:
                  structureBoundingBox.minX = x - (n2 - 1);
                  structureBoundingBox.maxZ = z + 2;
                  break;
               case EAST:
                  structureBoundingBox.maxX = x + (n2 - 1);
                  structureBoundingBox.maxZ = z + 2;
            }

            if (StructureComponent.findIntersecting(list, structureBoundingBox) == null) {
               break;
            }
         }

         return n > 0 ? structureBoundingBox : null;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int componentType = this.getComponentType();
         int nextInt = rand.nextInt(4);
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  if (nextInt <= 1) {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ - 1,
                        this.coordBaseMode,
                        componentType
                     );
                  } else if (nextInt == 2) {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ,
                        EnumFacing.WEST,
                        componentType
                     );
                  } else {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ,
                        EnumFacing.EAST,
                        componentType
                     );
                  }
                  break;
               case SOUTH:
                  if (nextInt <= 1) {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ + 1,
                        this.coordBaseMode,
                        componentType
                     );
                  } else if (nextInt == 2) {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ - 3,
                        EnumFacing.WEST,
                        componentType
                     );
                  } else {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ - 3,
                        EnumFacing.EAST,
                        componentType
                     );
                  }
                  break;
               case WEST:
                  if (nextInt <= 1) {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ,
                        this.coordBaseMode,
                        componentType
                     );
                  } else if (nextInt == 2) {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ - 1,
                        EnumFacing.NORTH,
                        componentType
                     );
                  } else {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.minX,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ + 1,
                        EnumFacing.SOUTH,
                        componentType
                     );
                  }
                  break;
               case EAST:
                  if (nextInt <= 1) {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ,
                        this.coordBaseMode,
                        componentType
                     );
                  } else if (nextInt == 2) {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX - 3,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.minZ - 1,
                        EnumFacing.NORTH,
                        componentType
                     );
                  } else {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn,
                        listIn,
                        rand,
                        this.boundingBox.maxX - 3,
                        this.boundingBox.minY - 1 + rand.nextInt(3),
                        this.boundingBox.maxZ + 1,
                        EnumFacing.SOUTH,
                        componentType
                     );
                  }
            }
         }

         if (componentType < 8) {
            if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.SOUTH) {
               for (int i = this.boundingBox.minX + 3; i + 3 <= this.boundingBox.maxX; i += 5) {
                  int nextInt2 = rand.nextInt(5);
                  if (nextInt2 == 0) {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn, listIn, rand, i, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType + 1
                     );
                  } else if (nextInt2 == 1) {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn, listIn, rand, i, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType + 1
                     );
                  }
               }
            } else {
               for (int i = this.boundingBox.minZ + 3; i + 3 <= this.boundingBox.maxZ; i += 5) {
                  int nextInt2 = rand.nextInt(5);
                  if (nextInt2 == 0) {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, i, EnumFacing.WEST, componentType + 1
                     );
                  } else if (nextInt2 == 1) {
                     StructureMineshaftPieces.pieceGenerator(
                        componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, i, EnumFacing.EAST, componentType + 1
                     );
                  }
               }
            }
         }
      }

      @Override
      public boolean generateChestContents(
         World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, List<WeightedRandomChestContent> listIn, int max
      ) {
         BlockPos blockPos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
         if (boundingBoxIn.isVecInside(blockPos) && worldIn.getBlockState(blockPos).getBlock().getMaterial() == Material.air) {
            int n = rand.nextBoolean() ? 1 : 0;
            worldIn.setBlockState(blockPos, Blocks.rail.getStateFromMeta(this.getMetadataWithOffset(Blocks.rail, n)), 2);
            EntityMinecartChest entityMinecartChest = new EntityMinecartChest(worldIn, blockPos.getX() + 0.5F, blockPos.getY() + 0.5F, blockPos.getZ() + 0.5F);
            WeightedRandomChestContent.generateChestContents(rand, listIn, entityMinecartChest, max);
            worldIn.spawnEntityInWorld(entityMinecartChest);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         boolean flag = false;
         byte b = 2;
         boolean flag2 = false;
         byte b2 = 2;
         int n = this.sectionCount * 5 - 1;
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 2, 1, n, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithOutlineUnderSeaLevel(
            worldIn, structureBoundingBoxIn, randomIn, 0.8F, 0, 2, 0, 2, 2, n, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false
         );
         if (this.hasSpiders) {
            this.fillWithOutlineUnderSeaLevel(
               worldIn, structureBoundingBoxIn, randomIn, 0.6F, 0, 0, 0, 2, 1, n, Blocks.web.getDefaultState(), Blocks.air.getDefaultState(), false
            );
         }

         for (int i = 0; i < this.sectionCount; i++) {
            int n2 = 2 + i * 5;
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 0, 0, n2, 0, 1, n2, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 2, 0, n2, 2, 1, n2, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false
            );
            if (randomIn.nextInt(4) == 0) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 0, 2, n2, 0, 2, n2, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 2, 2, n2, 2, 2, n2, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false
               );
            } else {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 0, 2, n2, 2, 2, n2, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false
               );
            }

            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 0, 2, n2 - 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 2, 2, n2 - 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 0, 2, n2 + 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 2, 2, n2 + 1, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 0, 2, n2 - 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 2, 2, n2 - 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 0, 2, n2 + 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 2, 2, n2 + 2, Blocks.web.getDefaultState());
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 1, 2, n2 - 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));
            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 1, 2, n2 + 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));
            if (randomIn.nextInt(100) == 0) {
               this.generateChestContents(
                  worldIn,
                  structureBoundingBoxIn,
                  randomIn,
                  2,
                  0,
                  n2 - 1,
                  WeightedRandomChestContent.combineLootTables(StructureMineshaftPieces.CHEST_CONTENT_WEIGHT_LIST, Items.enchanted_book.getRandom(randomIn)),
                  3 + randomIn.nextInt(4)
               );
            }

            if (randomIn.nextInt(100) == 0) {
               this.generateChestContents(
                  worldIn,
                  structureBoundingBoxIn,
                  randomIn,
                  0,
                  0,
                  n2 + 1,
                  WeightedRandomChestContent.combineLootTables(StructureMineshaftPieces.CHEST_CONTENT_WEIGHT_LIST, Items.enchanted_book.getRandom(randomIn)),
                  3 + randomIn.nextInt(4)
               );
            }

            if (this.hasSpiders && !this.spawnerPlaced) {
               int yWithOffset = this.getYWithOffset(0);
               int n3 = n2 - 1 + randomIn.nextInt(3);
               int xWithOffset = this.getXWithOffset(1, n3);
               n3 = this.getZWithOffset(1, n3);
               BlockPos blockPos = new BlockPos(xWithOffset, yWithOffset, n3);
               if (structureBoundingBoxIn.isVecInside(blockPos)) {
                  this.spawnerPlaced = true;
                  worldIn.setBlockState(blockPos, Blocks.mob_spawner.getDefaultState(), 2);
                  TileEntity tileEntity = worldIn.getTileEntity(blockPos);
                  if (tileEntity instanceof TileEntityMobSpawner) {
                     ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic().setEntityName("CaveSpider");
                  }
               }
            }
         }

         for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= n; j++) {
               byte b3 = -1;
               IBlockState blockStateFromPos = this.getBlockStateFromPos(worldIn, i, b3, j, structureBoundingBoxIn);
               if (blockStateFromPos.getBlock().getMaterial() == Material.air) {
                  byte b4 = -1;
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), i, b4, j, structureBoundingBoxIn);
               }
            }
         }

         if (this.hasRails) {
            for (int i = 0; i <= n; i++) {
               IBlockState blockStateFromPos = this.getBlockStateFromPos(worldIn, 1, -1, i, structureBoundingBoxIn);
               if (blockStateFromPos.getBlock().getMaterial() != Material.air && blockStateFromPos.getBlock().isFullBlock()) {
                  this.randomlyPlaceBlock(
                     worldIn, structureBoundingBoxIn, randomIn, 0.7F, 1, 0, i, Blocks.rail.getStateFromMeta(this.getMetadataWithOffset(Blocks.rail, 0))
                  );
               }
            }
         }

         return true;
      }
   }

   public static class Cross extends StructureComponent {
      public EnumFacing corridorDirection;
      public boolean isMultipleFloors;

      public Cross() {
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         tagCompound.setBoolean("tf", this.isMultipleFloors);
         tagCompound.setInteger("D", this.corridorDirection.getHorizontalIndex());
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         this.isMultipleFloors = tagCompound.getBoolean("tf");
         this.corridorDirection = EnumFacing.getHorizontal(tagCompound.getInteger("D"));
      }

      public Cross(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing) {
         super(type);
         this.corridorDirection = facing;
         this.boundingBox = structurebb;
         this.isMultipleFloors = structurebb.getYSize() > 3;
      }

      public static StructureBoundingBox getBoundingBox(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing) {
         StructureBoundingBox structureBoundingBox = new StructureBoundingBox(x, y, z, x, y + 2, z);
         if (rand.nextInt(4) == 0) {
            structureBoundingBox.maxY += 4;
         }

         switch (facing) {
            case NORTH:
               structureBoundingBox.minX = x - 1;
               structureBoundingBox.maxX = x + 3;
               structureBoundingBox.minZ = z - 4;
               break;
            case SOUTH:
               structureBoundingBox.minX = x - 1;
               structureBoundingBox.maxX = x + 3;
               structureBoundingBox.maxZ = z + 4;
               break;
            case WEST:
               structureBoundingBox.minX = x - 4;
               structureBoundingBox.minZ = z - 1;
               structureBoundingBox.maxZ = z + 3;
               break;
            case EAST:
               structureBoundingBox.maxX = x + 4;
               structureBoundingBox.minZ = z - 1;
               structureBoundingBox.maxZ = z + 3;
         }

         return StructureComponent.findIntersecting(listIn, structureBoundingBox) != null ? null : structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int componentType = this.getComponentType();
         switch (this.corridorDirection) {
            case NORTH:
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType
               );
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, componentType
               );
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, componentType
               );
               break;
            case SOUTH:
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType
               );
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, componentType
               );
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, componentType
               );
               break;
            case WEST:
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType
               );
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType
               );
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, componentType
               );
               break;
            case EAST:
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType
               );
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType
               );
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, componentType
               );
         }

         if (this.isMultipleFloors) {
            if (rand.nextBoolean()) {
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType
               );
            }

            if (rand.nextBoolean()) {
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.WEST, componentType
               );
            }

            if (rand.nextBoolean()) {
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.EAST, componentType
               );
            }

            if (rand.nextBoolean()) {
               StructureMineshaftPieces.pieceGenerator(
                  componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType
               );
            }
         }
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         if (this.isMultipleFloors) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX + 1,
               this.boundingBox.minY,
               this.boundingBox.minZ,
               this.boundingBox.maxX - 1,
               this.boundingBox.minY + 3 - 1,
               this.boundingBox.maxZ,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX,
               this.boundingBox.minY,
               this.boundingBox.minZ + 1,
               this.boundingBox.maxX,
               this.boundingBox.minY + 3 - 1,
               this.boundingBox.maxZ - 1,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX + 1,
               this.boundingBox.maxY - 2,
               this.boundingBox.minZ,
               this.boundingBox.maxX - 1,
               this.boundingBox.maxY,
               this.boundingBox.maxZ,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX,
               this.boundingBox.maxY - 2,
               this.boundingBox.minZ + 1,
               this.boundingBox.maxX,
               this.boundingBox.maxY,
               this.boundingBox.maxZ - 1,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX + 1,
               this.boundingBox.minY + 3,
               this.boundingBox.minZ + 1,
               this.boundingBox.maxX - 1,
               this.boundingBox.minY + 3,
               this.boundingBox.maxZ - 1,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         } else {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX + 1,
               this.boundingBox.minY,
               this.boundingBox.minZ,
               this.boundingBox.maxX - 1,
               this.boundingBox.maxY,
               this.boundingBox.maxZ,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               this.boundingBox.minX,
               this.boundingBox.minY,
               this.boundingBox.minZ + 1,
               this.boundingBox.maxX,
               this.boundingBox.maxY,
               this.boundingBox.maxZ - 1,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         }

         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX + 1,
            this.boundingBox.minY,
            this.boundingBox.minZ + 1,
            this.boundingBox.minX + 1,
            this.boundingBox.maxY,
            this.boundingBox.minZ + 1,
            Blocks.planks.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX + 1,
            this.boundingBox.minY,
            this.boundingBox.maxZ - 1,
            this.boundingBox.minX + 1,
            this.boundingBox.maxY,
            this.boundingBox.maxZ - 1,
            Blocks.planks.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.maxX - 1,
            this.boundingBox.minY,
            this.boundingBox.minZ + 1,
            this.boundingBox.maxX - 1,
            this.boundingBox.maxY,
            this.boundingBox.minZ + 1,
            Blocks.planks.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.maxX - 1,
            this.boundingBox.minY,
            this.boundingBox.maxZ - 1,
            this.boundingBox.maxX - 1,
            this.boundingBox.maxY,
            this.boundingBox.maxZ - 1,
            Blocks.planks.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );

         for (int minX = this.boundingBox.minX; minX <= this.boundingBox.maxX; minX++) {
            for (int minZ = this.boundingBox.minZ; minZ <= this.boundingBox.maxZ; minZ++) {
               if (this.getBlockStateFromPos(worldIn, minX, this.boundingBox.minY - 1, minZ, structureBoundingBoxIn).getBlock().getMaterial() == Material.air) {
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), minX, this.boundingBox.minY - 1, minZ, structureBoundingBoxIn);
               }
            }
         }

         return true;
      }
   }

   public static class Room extends StructureComponent {
      public List<StructureBoundingBox> roomsLinkedToTheRoom = Lists.newLinkedList();

      public Room() {
      }

      public Room(int type, Random rand, int x, int z) {
         super(type);
         this.boundingBox = new StructureBoundingBox(x, 50, z, x + 7 + rand.nextInt(6), 54 + rand.nextInt(6), z + 7 + rand.nextInt(6));
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int componentType = this.getComponentType();
         int n = this.boundingBox.getYSize() - 3 - 1;
         if (n <= 0) {
            n = 1;
         }

         int n2 = 0;

         while (n2 < this.boundingBox.getXSize()) {
            n2 += rand.nextInt(this.boundingBox.getXSize());
            if (n2 + 3 > this.boundingBox.getXSize()) {
               break;
            }

            StructureComponent pieceGenerator = StructureMineshaftPieces.pieceGenerator(
               componentIn,
               listIn,
               rand,
               this.boundingBox.minX + n2,
               this.boundingBox.minY + rand.nextInt(n) + 1,
               this.boundingBox.minZ - 1,
               EnumFacing.NORTH,
               componentType
            );
            if (pieceGenerator != null) {
               StructureBoundingBox boundingBox = pieceGenerator.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(boundingBox.minX, boundingBox.minY, this.boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, this.boundingBox.minZ + 1));
            }

            n2 += 4;
         }

         n2 = 0;

         while (n2 < this.boundingBox.getXSize()) {
            n2 += rand.nextInt(this.boundingBox.getXSize());
            if (n2 + 3 > this.boundingBox.getXSize()) {
               break;
            }

            StructureComponent pieceGenerator = StructureMineshaftPieces.pieceGenerator(
               componentIn,
               listIn,
               rand,
               this.boundingBox.minX + n2,
               this.boundingBox.minY + rand.nextInt(n) + 1,
               this.boundingBox.maxZ + 1,
               EnumFacing.SOUTH,
               componentType
            );
            if (pieceGenerator != null) {
               StructureBoundingBox boundingBox = pieceGenerator.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(boundingBox.minX, boundingBox.minY, this.boundingBox.maxZ - 1, boundingBox.maxX, boundingBox.maxY, this.boundingBox.maxZ));
            }

            n2 += 4;
         }

         n2 = 0;

         while (n2 < this.boundingBox.getZSize()) {
            n2 += rand.nextInt(this.boundingBox.getZSize());
            if (n2 + 3 > this.boundingBox.getZSize()) {
               break;
            }

            StructureComponent pieceGenerator = StructureMineshaftPieces.pieceGenerator(
               componentIn,
               listIn,
               rand,
               this.boundingBox.minX - 1,
               this.boundingBox.minY + rand.nextInt(n) + 1,
               this.boundingBox.minZ + n2,
               EnumFacing.WEST,
               componentType
            );
            if (pieceGenerator != null) {
               StructureBoundingBox boundingBox = pieceGenerator.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(this.boundingBox.minX, boundingBox.minY, boundingBox.minZ, this.boundingBox.minX + 1, boundingBox.maxY, boundingBox.maxZ));
            }

            n2 += 4;
         }

         n2 = 0;

         while (n2 < this.boundingBox.getZSize()) {
            n2 += rand.nextInt(this.boundingBox.getZSize());
            if (n2 + 3 > this.boundingBox.getZSize()) {
               break;
            }

            StructureComponent pieceGenerator = StructureMineshaftPieces.pieceGenerator(
               componentIn,
               listIn,
               rand,
               this.boundingBox.maxX + 1,
               this.boundingBox.minY + rand.nextInt(n) + 1,
               this.boundingBox.minZ + n2,
               EnumFacing.EAST,
               componentType
            );
            if (pieceGenerator != null) {
               StructureBoundingBox boundingBox = pieceGenerator.getBoundingBox();
               this.roomsLinkedToTheRoom
                  .add(new StructureBoundingBox(this.boundingBox.maxX - 1, boundingBox.minY, boundingBox.minZ, this.boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ));
            }

            n2 += 4;
         }
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX,
            this.boundingBox.minY,
            this.boundingBox.minZ,
            this.boundingBox.maxX,
            this.boundingBox.minY,
            this.boundingBox.maxZ,
            Blocks.dirt.getDefaultState(),
            Blocks.air.getDefaultState(),
            true
         );
         this.fillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX,
            this.boundingBox.minY + 1,
            this.boundingBox.minZ,
            this.boundingBox.maxX,
            Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY),
            this.boundingBox.maxZ,
            Blocks.air.getDefaultState(),
            Blocks.air.getDefaultState(),
            false
         );

         for (StructureBoundingBox structureBoundingBox : this.roomsLinkedToTheRoom) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               structureBoundingBox.minX,
               structureBoundingBox.maxY - 2,
               structureBoundingBox.minZ,
               structureBoundingBox.maxX,
               structureBoundingBox.maxY,
               structureBoundingBox.maxZ,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         }

         this.randomlyRareFillWithBlocks(
            worldIn,
            structureBoundingBoxIn,
            this.boundingBox.minX,
            this.boundingBox.minY + 4,
            this.boundingBox.minZ,
            this.boundingBox.maxX,
            this.boundingBox.maxY,
            this.boundingBox.maxZ,
            Blocks.air.getDefaultState(),
            false
         );
         return true;
      }

      @Override
      public void process(int n, int n2, int n3) {
         super.process(n, n2, n3);

         for (StructureBoundingBox structureBoundingBox : this.roomsLinkedToTheRoom) {
            structureBoundingBox.offset(n, n2, n3);
         }
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         NBTTagList nbttagList = new NBTTagList();

         for (StructureBoundingBox structureBoundingBox : this.roomsLinkedToTheRoom) {
            nbttagList.appendTag(structureBoundingBox.toNBTTagIntArray());
         }

         tagCompound.setTag("Entrances", nbttagList);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         NBTTagList tagList = tagCompound.getTagList("Entrances", 11);

         for (int i = 0; i < tagList.tagCount(); i++) {
            this.roomsLinkedToTheRoom.add(new StructureBoundingBox(tagList.getIntArrayAt(i)));
         }
      }
   }

   public static class Stairs extends StructureComponent {
      public Stairs() {
      }

      public Stairs(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing) {
         super(type);
         this.coordBaseMode = facing;
         this.boundingBox = structurebb;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
      }

      public static StructureBoundingBox getBoundingBox(List<StructureComponent> listIn, Random rand, int x, int y, int z, EnumFacing facing) {
         StructureBoundingBox structureBoundingBox = new StructureBoundingBox(x, y - 5, z, x, y + 2, z);
         switch (facing) {
            case NORTH:
               structureBoundingBox.maxX = x + 2;
               structureBoundingBox.minZ = z - 8;
               break;
            case SOUTH:
               structureBoundingBox.maxX = x + 2;
               structureBoundingBox.maxZ = z + 8;
               break;
            case WEST:
               structureBoundingBox.minX = x - 8;
               structureBoundingBox.maxZ = z + 2;
               break;
            case EAST:
               structureBoundingBox.maxX = x + 8;
               structureBoundingBox.maxZ = z + 2;
         }

         return StructureComponent.findIntersecting(listIn, structureBoundingBox) != null ? null : structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int componentType = this.getComponentType();
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case NORTH:
                  StructureMineshaftPieces.pieceGenerator(
                     componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType
                  );
                  break;
               case SOUTH:
                  StructureMineshaftPieces.pieceGenerator(
                     componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType
                  );
                  break;
               case WEST:
                  StructureMineshaftPieces.pieceGenerator(
                     componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.WEST, componentType
                  );
                  break;
               case EAST:
                  StructureMineshaftPieces.pieceGenerator(
                     componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.EAST, componentType
                  );
            }
         }
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 2, 7, 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 7, 2, 2, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);

         for (int i = 0; i < 5; i++) {
            this.fillWithBlocks(
               worldIn,
               structureBoundingBoxIn,
               0,
               5 - i - (i < 4 ? 1 : 0),
               2 + i,
               2,
               7 - i,
               2 + i,
               Blocks.air.getDefaultState(),
               Blocks.air.getDefaultState(),
               false
            );
         }

         return true;
      }
   }
}
