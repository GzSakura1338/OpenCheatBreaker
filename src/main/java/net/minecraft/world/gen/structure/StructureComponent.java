package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public abstract class StructureComponent {
    public StructureBoundingBox boundingBox;
    public int componentType;
    public int coordBaseMode;

    public StructureComponent() {
    }

    public StructureComponent(int n) {
        this.componentType = n;
        this.coordBaseMode = -1;
    }

    public NBTTagCompound toNbt() {
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        nbttagCompound.setString("id", MapGenStructureIO.getId(this));
        nbttagCompound.setTag("BB", this.boundingBox.toNbt());
        nbttagCompound.setInteger("O", this.coordBaseMode);
        nbttagCompound.setInteger("GD", this.componentType);
        this.writeStructureToNBT(nbttagCompound);
        return nbttagCompound;
    }

    public abstract void writeStructureToNBT(NBTTagCompound nbttagCompound);

    public void fromNbt(World world, NBTTagCompound nbttagCompound) {
        if (nbttagCompound.hasKey("BB")) {
            this.boundingBox = new StructureBoundingBox(nbttagCompound.getIntArray("BB"));
        }

        this.coordBaseMode = nbttagCompound.getInteger("O");
        this.componentType = nbttagCompound.getInteger("GD");
        this.readStructureFromNBT(nbttagCompound);
    }

    public abstract void readStructureFromNBT(NBTTagCompound nbttagCompound);

    public void buildComponent(StructureComponent structureComponent, List list, Random random) {
    }

    public abstract boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox);

    public StructureBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public int getComponentType() {
        return this.componentType;
    }

    public static StructureComponent findIntersecting(List list, StructureBoundingBox structureBoundingBox) {
        for (StructureComponent structureComponent : (Iterable<StructureComponent>)list) {
            if (structureComponent.getBoundingBox() != null && structureComponent.getBoundingBox().intersectsWith(structureBoundingBox)) {
                return structureComponent;
            }
        }

        return null;
    }

    public ChunkPosition createChunkPosition() {
        return new ChunkPosition(this.boundingBox.getCenterX(), this.boundingBox.getCenterY(), this.boundingBox.getCenterZ());
    }

    public boolean isLiquidInStructureBoundingBox(World world, StructureBoundingBox structureBoundingBox) {
        int max = Math.max(this.boundingBox.minX - 1, structureBoundingBox.minX);
        int max2 = Math.max(this.boundingBox.minY - 1, structureBoundingBox.minY);
        int max3 = Math.max(this.boundingBox.minZ - 1, structureBoundingBox.minZ);
        int min = Math.min(this.boundingBox.maxX + 1, structureBoundingBox.maxX);
        int min2 = Math.min(this.boundingBox.maxY + 1, structureBoundingBox.maxY);
        int min3 = Math.min(this.boundingBox.maxZ + 1, structureBoundingBox.maxZ);

        for (int i = max; i <= min; i++) {
            for (int j = max3; j <= min3; j++) {
                if (world.getBlock(i, max2, j).getMaterial().isLiquid()) {
                    return true;
                }

                if (world.getBlock(i, min2, j).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }

        for (int i = max; i <= min; i++) {
            for (int j = max2; j <= min2; j++) {
                if (world.getBlock(i, j, max3).getMaterial().isLiquid()) {
                    return true;
                }

                if (world.getBlock(i, j, min3).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }

        for (int i = max3; i <= min3; i++) {
            for (int j = max2; j <= min2; j++) {
                if (world.getBlock(max, j, i).getMaterial().isLiquid()) {
                    return true;
                }

                if (world.getBlock(min, j, i).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }

        return false;
    }

    public int getXWithOffset(int n, int n2) {
        switch (this.coordBaseMode) {
            case 0:
            case 2:
                return this.boundingBox.minX + n;
            case 1:
                return this.boundingBox.maxX - n2;
            case 3:
                return this.boundingBox.minX + n2;
            default:
                return n;
        }
    }

    public int getYWithOffset(int n) {
        return this.coordBaseMode == -1 ? n : n + this.boundingBox.minY;
    }

    public int getZWithOffset(int n, int n2) {
        switch (this.coordBaseMode) {
            case 0:
                return this.boundingBox.minZ + n2;
            case 1:
            case 3:
                return this.boundingBox.minZ + n;
            case 2:
                return this.boundingBox.maxZ - n2;
            default:
                return n2;
        }
    }

    public int getMetadataWithOffset(Block block, int n) {
        if (block == Blocks.rail) {
            if (this.coordBaseMode == 1 || this.coordBaseMode == 3) {
                if (n == 1) {
                    return 0;
                }

                return 1;
            }
        } else if (block != Blocks.wooden_door && block != Blocks.iron_door) {
            if (block != Blocks.stone_stairs
                && block != Blocks.oak_stairs
                && block != Blocks.nether_brick_stairs
                && block != Blocks.stone_brick_stairs
                && block != Blocks.sandstone_stairs) {
                if (block == Blocks.ladder) {
                    if (this.coordBaseMode == 0) {
                        if (n == 2) {
                            return 3;
                        }

                        if (n == 3) {
                            return 2;
                        }
                    } else if (this.coordBaseMode == 1) {
                        if (n == 2) {
                            return 4;
                        }

                        if (n == 3) {
                            return 5;
                        }

                        if (n == 4) {
                            return 2;
                        }

                        if (n == 5) {
                            return 3;
                        }
                    } else if (this.coordBaseMode == 3) {
                        if (n == 2) {
                            return 5;
                        }

                        if (n == 3) {
                            return 4;
                        }

                        if (n == 4) {
                            return 2;
                        }

                        if (n == 5) {
                            return 3;
                        }
                    }
                } else if (block == Blocks.stone_button) {
                    if (this.coordBaseMode == 0) {
                        if (n == 3) {
                            return 4;
                        }

                        if (n == 4) {
                            return 3;
                        }
                    } else if (this.coordBaseMode == 1) {
                        if (n == 3) {
                            return 1;
                        }

                        if (n == 4) {
                            return 2;
                        }

                        if (n == 2) {
                            return 3;
                        }

                        if (n == 1) {
                            return 4;
                        }
                    } else if (this.coordBaseMode == 3) {
                        if (n == 3) {
                            return 2;
                        }

                        if (n == 4) {
                            return 1;
                        }

                        if (n == 2) {
                            return 3;
                        }

                        if (n == 1) {
                            return 4;
                        }
                    }
                } else if (block != Blocks.tripwire_hook && !(block instanceof BlockDirectional)) {
                    if (block == Blocks.piston || block == Blocks.sticky_piston || block == Blocks.lever || block == Blocks.dispenser) {
                        if (this.coordBaseMode == 0) {
                            if (n == 2 || n == 3) {
                                return Facing.oppositeSide[n];
                            }
                        } else if (this.coordBaseMode == 1) {
                            if (n == 2) {
                                return 4;
                            }

                            if (n == 3) {
                                return 5;
                            }

                            if (n == 4) {
                                return 2;
                            }

                            if (n == 5) {
                                return 3;
                            }
                        } else if (this.coordBaseMode == 3) {
                            if (n == 2) {
                                return 5;
                            }

                            if (n == 3) {
                                return 4;
                            }

                            if (n == 4) {
                                return 2;
                            }

                            if (n == 5) {
                                return 3;
                            }
                        }
                    }
                } else if (this.coordBaseMode == 0) {
                    if (n == 0 || n == 2) {
                        return Direction.rotateOpposite[n];
                    }
                } else if (this.coordBaseMode == 1) {
                    if (n == 2) {
                        return 1;
                    }

                    if (n == 0) {
                        return 3;
                    }

                    if (n == 1) {
                        return 2;
                    }

                    if (n == 3) {
                        return 0;
                    }
                } else if (this.coordBaseMode == 3) {
                    if (n == 2) {
                        return 3;
                    }

                    if (n == 0) {
                        return 1;
                    }

                    if (n == 1) {
                        return 2;
                    }

                    if (n == 3) {
                        return 0;
                    }
                }
            } else if (this.coordBaseMode == 0) {
                if (n == 2) {
                    return 3;
                }

                if (n == 3) {
                    return 2;
                }
            } else if (this.coordBaseMode == 1) {
                if (n == 0) {
                    return 2;
                }

                if (n == 1) {
                    return 3;
                }

                if (n == 2) {
                    return 0;
                }

                if (n == 3) {
                    return 1;
                }
            } else if (this.coordBaseMode == 3) {
                if (n == 0) {
                    return 2;
                }

                if (n == 1) {
                    return 3;
                }

                if (n == 2) {
                    return 1;
                }

                if (n == 3) {
                    return 0;
                }
            }
        } else if (this.coordBaseMode == 0) {
            if (n == 0) {
                return 2;
            }

            if (n == 2) {
                return 0;
            }
        } else {
            if (this.coordBaseMode == 1) {
                return n + 1 & 3;
            }

            if (this.coordBaseMode == 3) {
                return n + 3 & 3;
            }
        }

        return n;
    }

    public void placeBlockAtCurrentPosition(World world, Block block, int n, int n2, int n3, int n4, StructureBoundingBox structureBoundingBox) {
        int xWithOffset = this.getXWithOffset(n2, n4);
        int yWithOffset = this.getYWithOffset(n3);
        int zWithOffset = this.getZWithOffset(n2, n4);
        if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset)) {
            world.setBlock(xWithOffset, yWithOffset, zWithOffset, block, n, 2);
        }
    }

    public Block getBlockAtCurrentPosition(World world, int n, int n2, int n3, StructureBoundingBox structureBoundingBox) {
        int xWithOffset = this.getXWithOffset(n, n3);
        int yWithOffset = this.getYWithOffset(n2);
        int zWithOffset = this.getZWithOffset(n, n3);
        return !structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset) ? Blocks.air : world.getBlock(xWithOffset, yWithOffset, zWithOffset);
    }

    public void fillWithAir(World world, StructureBoundingBox structureBoundingBox, int n, int n2, int n3, int n4, int n5, int n6) {
        for (int i = n2; i <= n5; i++) {
            for (int j = n; j <= n4; j++) {
                for (int k = n3; k <= n6; k++) {
                    this.placeBlockAtCurrentPosition(world, Blocks.air, 0, j, i, k, structureBoundingBox);
                }
            }
        }
    }

    public void fillWithBlocks(
        World world, StructureBoundingBox structureBoundingBox, int n, int n2, int n3, int n4, int n5, int n6, Block block, Block block2, boolean flag
    ) {
        for (int i = n2; i <= n5; i++) {
            for (int j = n; j <= n4; j++) {
                for (int k = n3; k <= n6; k++) {
                    if (!flag || this.getBlockAtCurrentPosition(world, j, i, k, structureBoundingBox).getMaterial() != Material.air) {
                        if (i != n2 && i != n5 && j != n && j != n4 && k != n3 && k != n6) {
                            this.placeBlockAtCurrentPosition(world, block2, 0, j, i, k, structureBoundingBox);
                        } else {
                            this.placeBlockAtCurrentPosition(world, block, 0, j, i, k, structureBoundingBox);
                        }
                    }
                }
            }
        }
    }

    public void fillWithMetadataBlocks(
        World world,
        StructureBoundingBox structureBoundingBox,
        int n,
        int n2,
        int n3,
        int n4,
        int n5,
        int n6,
        Block block,
        int n7,
        Block block2,
        int n8,
        boolean flag
    ) {
        for (int i = n2; i <= n5; i++) {
            for (int j = n; j <= n4; j++) {
                for (int k = n3; k <= n6; k++) {
                    if (!flag || this.getBlockAtCurrentPosition(world, j, i, k, structureBoundingBox).getMaterial() != Material.air) {
                        if (i != n2 && i != n5 && j != n && j != n4 && k != n3 && k != n6) {
                            this.placeBlockAtCurrentPosition(world, block2, n8, j, i, k, structureBoundingBox);
                        } else {
                            this.placeBlockAtCurrentPosition(world, block, n7, j, i, k, structureBoundingBox);
                        }
                    }
                }
            }
        }
    }

    public void fillWithRandomizedBlocks(
        World world,
        StructureBoundingBox structureBoundingBox,
        int n,
        int n2,
        int n3,
        int n4,
        int n5,
        int n6,
        boolean flag,
        Random random,
        StructureComponent.BlockSelector blockSelector
    ) {
        for (int i = n2; i <= n5; i++) {
            for (int j = n; j <= n4; j++) {
                for (int k = n3; k <= n6; k++) {
                    if (!flag || this.getBlockAtCurrentPosition(world, j, i, k, structureBoundingBox).getMaterial() != Material.air) {
                        blockSelector.selectBlocks(
                            random, j, i, k, i == n2 || i == n5 || j == n || j == n4 || k == n3 || k == n6
                        );
                        this.placeBlockAtCurrentPosition(world, blockSelector.getBlock(), blockSelector.getSelectedBlockMetaData(), j, i, k, structureBoundingBox);
                    }
                }
            }
        }
    }

    public void randomlyFillWithBlocks(
        World world,
        StructureBoundingBox structureBoundingBox,
        Random random,
        float f,
        int n,
        int n2,
        int n3,
        int n4,
        int n5,
        int n6,
        Block block,
        Block block2,
        boolean flag
    ) {
        for (int i = n2; i <= n5; i++) {
            for (int j = n; j <= n4; j++) {
                for (int k = n3; k <= n6; k++) {
                    if (random.nextFloat() <= f && (!flag || this.getBlockAtCurrentPosition(world, j, i, k, structureBoundingBox).getMaterial() != Material.air)) {
                        if (i != n2 && i != n5 && j != n && j != n4 && k != n3 && k != n6) {
                            this.placeBlockAtCurrentPosition(world, block2, 0, j, i, k, structureBoundingBox);
                        } else {
                            this.placeBlockAtCurrentPosition(world, block, 0, j, i, k, structureBoundingBox);
                        }
                    }
                }
            }
        }
    }

    public void handleWorld(World world, StructureBoundingBox structureBoundingBox, Random random, float f, int n, int n2, int n3, Block block, int n4) {
        if (random.nextFloat() < f) {
            this.placeBlockAtCurrentPosition(world, block, n4, n, n2, n3, structureBoundingBox);
        }
    }

    public void handleWorld(World world, StructureBoundingBox structureBoundingBox, int n, int n2, int n3, int n4, int n5, int n6, Block block, boolean flag) {
        float f = n4 - n + 1;
        float f2 = n5 - n2 + 1;
        float f3 = n6 - n3 + 1;
        float f4 = n + f / 2.0F;
        float f5 = n3 + f3 / 2.0F;

        for (int i = n2; i <= n5; i++) {
            float f6 = (i - n2) / f2;

            for (int j = n; j <= n4; j++) {
                float f7 = (j - f4) / (f * 0.5F);

                for (int k = n3; k <= n6; k++) {
                    float f8 = (k - f5) / (f3 * 0.5F);
                    if (!flag || this.getBlockAtCurrentPosition(world, j, i, k, structureBoundingBox).getMaterial() != Material.air) {
                        float f9 = f7 * f7 + f6 * f6 + f8 * f8;
                        if (f9 <= 1.05F) {
                            this.placeBlockAtCurrentPosition(world, block, 0, j, i, k, structureBoundingBox);
                        }
                    }
                }
            }
        }
    }

    public void clearCurrentPositionBlocksUpwards(World world, int n, int n2, int n3, StructureBoundingBox structureBoundingBox) {
        int xWithOffset = this.getXWithOffset(n, n3);
        int yWithOffset = this.getYWithOffset(n2);
        int zWithOffset = this.getZWithOffset(n, n3);
        if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset)) {
            while (!world.isAirBlock(xWithOffset, yWithOffset, zWithOffset) && yWithOffset < 255) {
                world.setBlock(xWithOffset, yWithOffset, zWithOffset, Blocks.air, 0, 2);
                yWithOffset++;
            }
        }
    }

    public void handleWorld(World world, Block block, int n, int n2, int n3, int n4, StructureBoundingBox structureBoundingBox) {
        int xWithOffset = this.getXWithOffset(n2, n4);
        int yWithOffset = this.getYWithOffset(n3);
        int zWithOffset = this.getZWithOffset(n2, n4);
        if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset)) {
            while ((world.isAirBlock(xWithOffset, yWithOffset, zWithOffset) || world.getBlock(xWithOffset, yWithOffset, zWithOffset).getMaterial().isLiquid()) && yWithOffset > 1) {
                world.setBlock(xWithOffset, yWithOffset, zWithOffset, block, n, 2);
                yWithOffset--;
            }
        }
    }

    public boolean generateStructureChestContents(
        World world, StructureBoundingBox structureBoundingBox, Random random, int n, int n2, int n3, WeightedRandomChestContent[] weightedRandomChestContents, int n4
    ) {
        int xWithOffset = this.getXWithOffset(n, n3);
        int yWithOffset = this.getYWithOffset(n2);
        int zWithOffset = this.getZWithOffset(n, n3);
        if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset) && world.getBlock(xWithOffset, yWithOffset, zWithOffset) != Blocks.chest) {
            world.setBlock(xWithOffset, yWithOffset, zWithOffset, Blocks.chest, 0, 2);
            TileEntityChest tileEntity = (TileEntityChest)world.getTileEntity(xWithOffset, yWithOffset, zWithOffset);
            if (tileEntity != null) {
                WeightedRandomChestContent.generateChestContents(random, weightedRandomChestContents, tileEntity, n4);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean generateStructureDispenserContents(
        World world, StructureBoundingBox structureBoundingBox, Random random, int n, int n2, int n3, int n4, WeightedRandomChestContent[] weightedRandomChestContents, int n5
    ) {
        int xWithOffset = this.getXWithOffset(n, n3);
        int yWithOffset = this.getYWithOffset(n2);
        int zWithOffset = this.getZWithOffset(n, n3);
        if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset) && world.getBlock(xWithOffset, yWithOffset, zWithOffset) != Blocks.dispenser) {
            world.setBlock(xWithOffset, yWithOffset, zWithOffset, Blocks.dispenser, this.getMetadataWithOffset(Blocks.dispenser, n4), 2);
            TileEntityDispenser tileEntity = (TileEntityDispenser)world.getTileEntity(xWithOffset, yWithOffset, zWithOffset);
            if (tileEntity != null) {
                WeightedRandomChestContent.generateDispenserContents(random, weightedRandomChestContents, tileEntity, n5);
            }

            return true;
        } else {
            return false;
        }
    }

    public void placeDoorAtCurrentPosition(World world, StructureBoundingBox structureBoundingBox, Random random, int n, int n2, int n3, int n4) {
        int xWithOffset = this.getXWithOffset(n, n3);
        int yWithOffset = this.getYWithOffset(n2);
        int zWithOffset = this.getZWithOffset(n, n3);
        if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset)) {
            ItemDoor.placeDoorBlock(world, xWithOffset, yWithOffset, zWithOffset, n4, Blocks.wooden_door);
        }
    }

    public abstract static class BlockSelector {
        public Block block = Blocks.air;
        public int selectedBlockMetaData;

        public abstract void selectBlocks(Random random, int n, int n2, int n3, boolean flag);

        public Block getBlock() {
            return this.block;
        }

        public int getSelectedBlockMetaData() {
            return this.selectedBlockMetaData;
        }
    }
}
