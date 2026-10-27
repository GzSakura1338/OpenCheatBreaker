package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public abstract class StructureComponent {
   public StructureBoundingBox boundingBox;
   public EnumFacing coordBaseMode;
   public int componentType;

   public StructureComponent() {
   }

   public StructureComponent(int type) {
      this.componentType = type;
   }

   public NBTTagCompound createStructureBaseNBT() {
      NBTTagCompound nbttagCompound = new NBTTagCompound();
      nbttagCompound.setString("id", MapGenStructureIO.getStructureComponentName(this));
      nbttagCompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
      nbttagCompound.setInteger("O", this.coordBaseMode == null ? -1 : this.coordBaseMode.getHorizontalIndex());
      nbttagCompound.setInteger("GD", this.componentType);
      this.writeStructureToNBT(nbttagCompound);
      return nbttagCompound;
   }

   public abstract void writeStructureToNBT(NBTTagCompound nbttagCompound);

   public void readStructureBaseNBT(World worldIn, NBTTagCompound tagCompound) {
      if (tagCompound.hasKey("BB")) {
         this.boundingBox = new StructureBoundingBox(tagCompound.getIntArray("BB"));
      }

      int integer = tagCompound.getInteger("O");
      this.coordBaseMode = integer == -1 ? null : EnumFacing.getHorizontal(integer);
      this.componentType = tagCompound.getInteger("GD");
      this.readStructureFromNBT(tagCompound);
   }

   public abstract void readStructureFromNBT(NBTTagCompound nbttagCompound);

   public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
   }

   public abstract boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox);

   public StructureBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public int getComponentType() {
      return this.componentType;
   }

   public static StructureComponent findIntersecting(List<StructureComponent> listIn, StructureBoundingBox boundingboxIn) {
      for (StructureComponent structureComponent : listIn) {
         if (structureComponent.getBoundingBox() != null && structureComponent.getBoundingBox().intersectsWith(boundingboxIn)) {
            return structureComponent;
         }
      }

      return null;
   }

   public BlockPos getBoundingBoxCenter() {
      return new BlockPos(this.boundingBox.getCenter());
   }

   public boolean isLiquidInStructureBoundingBox(World worldIn, StructureBoundingBox boundingboxIn) {
      int max = Math.max(this.boundingBox.minX - 1, boundingboxIn.minX);
      int max2 = Math.max(this.boundingBox.minY - 1, boundingboxIn.minY);
      int max3 = Math.max(this.boundingBox.minZ - 1, boundingboxIn.minZ);
      int min = Math.min(this.boundingBox.maxX + 1, boundingboxIn.maxX);
      int min2 = Math.min(this.boundingBox.maxY + 1, boundingboxIn.maxY);
      int min3 = Math.min(this.boundingBox.maxZ + 1, boundingboxIn.maxZ);
      MutableBlockPos mutableBlockPos = new MutableBlockPos();

      for (int i = max; i <= min; i++) {
         for (int j = max3; j <= min3; j++) {
            if (worldIn.getBlockState(mutableBlockPos.set(i, max2, j)).getBlock().getMaterial().isLiquid()) {
               return true;
            }

            if (worldIn.getBlockState(mutableBlockPos.set(i, min2, j)).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for (int i = max; i <= min; i++) {
         for (int j = max2; j <= min2; j++) {
            if (worldIn.getBlockState(mutableBlockPos.set(i, j, max3)).getBlock().getMaterial().isLiquid()) {
               return true;
            }

            if (worldIn.getBlockState(mutableBlockPos.set(i, j, min3)).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for (int i = max3; i <= min3; i++) {
         for (int j = max2; j <= min2; j++) {
            if (worldIn.getBlockState(mutableBlockPos.set(max, j, i)).getBlock().getMaterial().isLiquid()) {
               return true;
            }

            if (worldIn.getBlockState(mutableBlockPos.set(min, j, i)).getBlock().getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      return false;
   }

   public int getXWithOffset(int x, int z) {
      if (this.coordBaseMode == null) {
         return x;
      }

      switch (this.coordBaseMode) {
         case NORTH:
         case SOUTH:
            return this.boundingBox.minX + x;
         case WEST:
            return this.boundingBox.maxX - z;
         case EAST:
            return this.boundingBox.minX + z;
         default:
            return x;
      }
   }

   public int getYWithOffset(int y) {
      return this.coordBaseMode == null ? y : y + this.boundingBox.minY;
   }

   public int getZWithOffset(int x, int z) {
      if (this.coordBaseMode == null) {
         return z;
      }

      switch (this.coordBaseMode) {
         case NORTH:
            return this.boundingBox.maxZ - z;
         case SOUTH:
            return this.boundingBox.minZ + z;
         case WEST:
         case EAST:
            return this.boundingBox.minZ + x;
         default:
            return z;
      }
   }

   public int getMetadataWithOffset(Block blockIn, int meta) {
      if (blockIn == Blocks.rail) {
         if (this.coordBaseMode == EnumFacing.WEST || this.coordBaseMode == EnumFacing.EAST) {
            if (meta == 1) {
               return 0;
            }

            return 1;
         }
      } else if (blockIn instanceof BlockDoor) {
         if (this.coordBaseMode == EnumFacing.SOUTH) {
            if (meta == 0) {
               return 2;
            }

            if (meta == 2) {
               return 0;
            }
         } else {
            if (this.coordBaseMode == EnumFacing.WEST) {
               return meta + 1 & 3;
            }

            if (this.coordBaseMode == EnumFacing.EAST) {
               return meta + 3 & 3;
            }
         }
      } else if (blockIn != Blocks.stone_stairs
         && blockIn != Blocks.oak_stairs
         && blockIn != Blocks.nether_brick_stairs
         && blockIn != Blocks.stone_brick_stairs
         && blockIn != Blocks.sandstone_stairs) {
         if (blockIn == Blocks.ladder) {
            if (this.coordBaseMode == EnumFacing.SOUTH) {
               if (meta == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }

               if (meta == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.WEST) {
               if (meta == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.WEST.getIndex();
               }

               if (meta == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.EAST.getIndex();
               }

               if (meta == EnumFacing.WEST.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }

               if (meta == EnumFacing.EAST.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.EAST) {
               if (meta == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.EAST.getIndex();
               }

               if (meta == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.WEST.getIndex();
               }

               if (meta == EnumFacing.WEST.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }

               if (meta == EnumFacing.EAST.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }
            }
         } else if (blockIn == Blocks.stone_button) {
            if (this.coordBaseMode == EnumFacing.SOUTH) {
               if (meta == 3) {
                  return 4;
               }

               if (meta == 4) {
                  return 3;
               }
            } else if (this.coordBaseMode == EnumFacing.WEST) {
               if (meta == 3) {
                  return 1;
               }

               if (meta == 4) {
                  return 2;
               }

               if (meta == 2) {
                  return 3;
               }

               if (meta == 1) {
                  return 4;
               }
            } else if (this.coordBaseMode == EnumFacing.EAST) {
               if (meta == 3) {
                  return 2;
               }

               if (meta == 4) {
                  return 1;
               }

               if (meta == 2) {
                  return 3;
               }

               if (meta == 1) {
                  return 4;
               }
            }
         } else if (blockIn == Blocks.tripwire_hook || blockIn instanceof BlockDirectional) {
            EnumFacing horizontal = EnumFacing.getHorizontal(meta);
            if (this.coordBaseMode == EnumFacing.SOUTH) {
               if (horizontal == EnumFacing.SOUTH || horizontal == EnumFacing.NORTH) {
                  return horizontal.getOpposite().getHorizontalIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.WEST) {
               if (horizontal == EnumFacing.NORTH) {
                  return EnumFacing.WEST.getHorizontalIndex();
               }

               if (horizontal == EnumFacing.SOUTH) {
                  return EnumFacing.EAST.getHorizontalIndex();
               }

               if (horizontal == EnumFacing.WEST) {
                  return EnumFacing.NORTH.getHorizontalIndex();
               }

               if (horizontal == EnumFacing.EAST) {
                  return EnumFacing.SOUTH.getHorizontalIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.EAST) {
               if (horizontal == EnumFacing.NORTH) {
                  return EnumFacing.EAST.getHorizontalIndex();
               }

               if (horizontal == EnumFacing.SOUTH) {
                  return EnumFacing.WEST.getHorizontalIndex();
               }

               if (horizontal == EnumFacing.WEST) {
                  return EnumFacing.NORTH.getHorizontalIndex();
               }

               if (horizontal == EnumFacing.EAST) {
                  return EnumFacing.SOUTH.getHorizontalIndex();
               }
            }
         } else if (blockIn == Blocks.piston || blockIn == Blocks.sticky_piston || blockIn == Blocks.lever || blockIn == Blocks.dispenser) {
            if (this.coordBaseMode == EnumFacing.SOUTH) {
               if (meta == EnumFacing.NORTH.getIndex() || meta == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.getFront(meta).getOpposite().getIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.WEST) {
               if (meta == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.WEST.getIndex();
               }

               if (meta == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.EAST.getIndex();
               }

               if (meta == EnumFacing.WEST.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }

               if (meta == EnumFacing.EAST.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }
            } else if (this.coordBaseMode == EnumFacing.EAST) {
               if (meta == EnumFacing.NORTH.getIndex()) {
                  return EnumFacing.EAST.getIndex();
               }

               if (meta == EnumFacing.SOUTH.getIndex()) {
                  return EnumFacing.WEST.getIndex();
               }

               if (meta == EnumFacing.WEST.getIndex()) {
                  return EnumFacing.NORTH.getIndex();
               }

               if (meta == EnumFacing.EAST.getIndex()) {
                  return EnumFacing.SOUTH.getIndex();
               }
            }
         }
      } else if (this.coordBaseMode == EnumFacing.SOUTH) {
         if (meta == 2) {
            return 3;
         }

         if (meta == 3) {
            return 2;
         }
      } else if (this.coordBaseMode == EnumFacing.WEST) {
         if (meta == 0) {
            return 2;
         }

         if (meta == 1) {
            return 3;
         }

         if (meta == 2) {
            return 0;
         }

         if (meta == 3) {
            return 1;
         }
      } else if (this.coordBaseMode == EnumFacing.EAST) {
         if (meta == 0) {
            return 2;
         }

         if (meta == 1) {
            return 3;
         }

         if (meta == 2) {
            return 1;
         }

         if (meta == 3) {
            return 0;
         }
      }

      return meta;
   }

   public void setBlockState(World worldIn, IBlockState blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn) {
      BlockPos blockPos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      if (boundingboxIn.isVecInside(blockPos)) {
         worldIn.setBlockState(blockPos, blockstateIn, 2);
      }
   }

   public IBlockState getBlockStateFromPos(World worldIn, int x, int y, int z, StructureBoundingBox boundingboxIn) {
      int xWithOffset = this.getXWithOffset(x, z);
      int yWithOffset = this.getYWithOffset(y);
      int zWithOffset = this.getZWithOffset(x, z);
      BlockPos blockPos = new BlockPos(xWithOffset, yWithOffset, zWithOffset);
      return !boundingboxIn.isVecInside(blockPos) ? Blocks.air.getDefaultState() : worldIn.getBlockState(blockPos);
   }

   public void fillWithAir(World worldIn, StructureBoundingBox structurebb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      for (int minY2 = minY; minY2 <= maxY; minY2++) {
         for (int minX2 = minX; minX2 <= maxX; minX2++) {
            for (int minZ2 = minZ; minZ2 <= maxZ; minZ2++) {
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), minX2, minY2, minZ2, structurebb);
            }
         }
      }
   }

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
      for (int yMin2 = yMin; yMin2 <= yMax; yMin2++) {
         for (int xMin2 = xMin; xMin2 <= xMax; xMin2++) {
            for (int zMin2 = zMin; zMin2 <= zMax; zMin2++) {
               if (!existingOnly || this.getBlockStateFromPos(worldIn, xMin2, yMin2, zMin2, boundingboxIn).getBlock().getMaterial() != Material.air) {
                  if (yMin2 != yMin && yMin2 != yMax && xMin2 != xMin && xMin2 != xMax && zMin2 != zMin && zMin2 != zMax) {
                     this.setBlockState(worldIn, insideBlockState, xMin2, yMin2, zMin2, boundingboxIn);
                  } else {
                     this.setBlockState(worldIn, boundaryBlockState, xMin2, yMin2, zMin2, boundingboxIn);
                  }
               }
            }
         }
      }
   }

   public void fillWithRandomizedBlocks(
      World worldIn,
      StructureBoundingBox boundingboxIn,
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ,
      boolean alwaysReplace,
      Random rand,
      StructureComponent.BlockSelector blockselector
   ) {
      for (int minY2 = minY; minY2 <= maxY; minY2++) {
         for (int minX2 = minX; minX2 <= maxX; minX2++) {
            for (int minZ2 = minZ; minZ2 <= maxZ; minZ2++) {
               if (!alwaysReplace || this.getBlockStateFromPos(worldIn, minX2, minY2, minZ2, boundingboxIn).getBlock().getMaterial() != Material.air) {
                  blockselector.selectBlocks(
                     rand, minX2, minY2, minZ2, minY2 == minY || minY2 == maxY || minX2 == minX || minX2 == maxX || minZ2 == minZ || minZ2 == maxZ
                  );
                  this.setBlockState(worldIn, blockselector.getBlockState(), minX2, minY2, minZ2, boundingboxIn);
               }
            }
         }
      }
   }

   public void fillWithOutlineUnderSeaLevel(
      World worldIn,
      StructureBoundingBox boundingboxIn,
      Random rand,
      float chance,
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ,
      IBlockState blockstate1,
      IBlockState blockstate2,
      boolean flag
   ) {
      for (int minY2 = minY; minY2 <= maxY; minY2++) {
         for (int minX2 = minX; minX2 <= maxX; minX2++) {
            for (int minZ2 = minZ; minZ2 <= maxZ; minZ2++) {
               if (rand.nextFloat() <= chance
                  && (!flag || this.getBlockStateFromPos(worldIn, minX2, minY2, minZ2, boundingboxIn).getBlock().getMaterial() != Material.air)) {
                  if (minY2 != minY && minY2 != maxY && minX2 != minX && minX2 != maxX && minZ2 != minZ && minZ2 != maxZ) {
                     this.setBlockState(worldIn, blockstate2, minX2, minY2, minZ2, boundingboxIn);
                  } else {
                     this.setBlockState(worldIn, blockstate1, minX2, minY2, minZ2, boundingboxIn);
                  }
               }
            }
         }
      }
   }

   public void randomlyPlaceBlock(World worldIn, StructureBoundingBox boundingboxIn, Random rand, float chance, int x, int y, int z, IBlockState blockstateIn) {
      if (rand.nextFloat() < chance) {
         this.setBlockState(worldIn, blockstateIn, x, y, z, boundingboxIn);
      }
   }

   public void randomlyRareFillWithBlocks(
      World worldIn, StructureBoundingBox boundingboxIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState blockstateIn, boolean flag
   ) {
      float f = maxX - minX + 1;
      float f2 = maxY - minY + 1;
      float f3 = maxZ - minZ + 1;
      float f4 = minX + f / 2.0F;
      float f5 = minZ + f3 / 2.0F;

      for (int minY2 = minY; minY2 <= maxY; minY2++) {
         float f6 = (minY2 - minY) / f2;

         for (int minX2 = minX; minX2 <= maxX; minX2++) {
            float f7 = (minX2 - f4) / (f * 0.5F);

            for (int minZ2 = minZ; minZ2 <= maxZ; minZ2++) {
               float f8 = (minZ2 - f5) / (f3 * 0.5F);
               if (!flag || this.getBlockStateFromPos(worldIn, minX2, minY2, minZ2, boundingboxIn).getBlock().getMaterial() != Material.air) {
                  float f9 = f7 * f7 + f6 * f6 + f8 * f8;
                  if (f9 <= 1.05F) {
                     this.setBlockState(worldIn, blockstateIn, minX2, minY2, minZ2, boundingboxIn);
                  }
               }
            }
         }
      }
   }

   public void clearCurrentPositionBlocksUpwards(World worldIn, int x, int y, int z, StructureBoundingBox structurebb) {
      BlockPos blockPos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      if (structurebb.isVecInside(blockPos)) {
         while (!worldIn.isAirBlock(blockPos) && blockPos.getY() < 255) {
            worldIn.setBlockState(blockPos, Blocks.air.getDefaultState(), 2);
            blockPos = blockPos.up();
         }
      }
   }

   public void replaceAirAndLiquidDownwards(World worldIn, IBlockState blockstateIn, int x, int y, int z, StructureBoundingBox boundingboxIn) {
      int xWithOffset = this.getXWithOffset(x, z);
      int yWithOffset = this.getYWithOffset(y);
      int zWithOffset = this.getZWithOffset(x, z);
      if (boundingboxIn.isVecInside(new BlockPos(xWithOffset, yWithOffset, zWithOffset))) {
         while (
            (worldIn.isAirBlock(new BlockPos(xWithOffset, yWithOffset, zWithOffset)) || worldIn.getBlockState(new BlockPos(xWithOffset, yWithOffset, zWithOffset)).getBlock().getMaterial().isLiquid())
               && yWithOffset > 1
         ) {
            worldIn.setBlockState(new BlockPos(xWithOffset, yWithOffset, zWithOffset), blockstateIn, 2);
            yWithOffset--;
         }
      }
   }

   public boolean generateChestContents(
      World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, List<WeightedRandomChestContent> listIn, int max
   ) {
      BlockPos blockPos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      if (boundingBoxIn.isVecInside(blockPos) && worldIn.getBlockState(blockPos).getBlock() != Blocks.chest) {
         IBlockState defaultState = Blocks.chest.getDefaultState();
         worldIn.setBlockState(blockPos, Blocks.chest.correctFacing(worldIn, blockPos, defaultState), 2);
         TileEntity tileEntity = worldIn.getTileEntity(blockPos);
         if (tileEntity instanceof TileEntityChest) {
            WeightedRandomChestContent.generateChestContents(rand, listIn, (TileEntityChest)tileEntity, max);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean generateDispenserContents(
      World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, int meta, List<WeightedRandomChestContent> listIn, int max
   ) {
      BlockPos blockPos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      if (boundingBoxIn.isVecInside(blockPos) && worldIn.getBlockState(blockPos).getBlock() != Blocks.dispenser) {
         worldIn.setBlockState(blockPos, Blocks.dispenser.getStateFromMeta(this.getMetadataWithOffset(Blocks.dispenser, meta)), 2);
         TileEntity tileEntity = worldIn.getTileEntity(blockPos);
         if (tileEntity instanceof TileEntityDispenser) {
            WeightedRandomChestContent.generateDispenserContents(rand, listIn, (TileEntityDispenser)tileEntity, max);
         }

         return true;
      } else {
         return false;
      }
   }

   public void placeDoorCurrentPosition(World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, EnumFacing facing) {
      BlockPos blockPos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
      if (boundingBoxIn.isVecInside(blockPos)) {
         ItemDoor.placeDoor(worldIn, blockPos, facing.rotateYCCW(), Blocks.oak_door);
      }
   }

   public void process(int n, int n2, int n3) {
      this.boundingBox.offset(n, n2, n3);
   }

   public abstract static class BlockSelector {
      public IBlockState blockstate = Blocks.air.getDefaultState();

      public abstract void selectBlocks(Random random, int n, int n2, int n3, boolean flag);

      public IBlockState getBlockState() {
         return this.blockstate;
      }
   }
}
