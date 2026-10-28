package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenBigTree extends WorldGenAbstractTree {
    public static byte[] otherCoordPairs = new byte[]{2, 0, 0, 1, 2, 1};
    public Random rand = new Random();
    public World worldObj;
    public int[] basePos = new int[]{0, 0, 0};
    public int heightLimit;
    public int height;
    public double heightAttenuation = 0.618;
    public double branchDensity = 1.0;
    public double branchSlope = 0.381;
    public double scaleWidth = 1.0;
    public double leafDensity = 1.0;
    public int trunkSize = 1;
    public int heightLimitLimit = 12;
    public int leafDistanceLimit = 4;
    public int[][] leafNodes;

    public WorldGenBigTree(boolean flag) {
        super(flag);
    }

    public void generateLeafNodeList() {
        this.height = (int)(this.heightLimit * this.heightAttenuation);
        if (this.height >= this.heightLimit) {
            this.height = this.heightLimit - 1;
        }

        int n = (int)(1.382 + Math.pow(this.leafDensity * this.heightLimit / 13.0, 2.0));
        if (n < 1) {
            n = 1;
        }

        int[][] intses = new int[n * this.heightLimit][4];
        int n2 = this.basePos[1] + this.heightLimit - this.leafDistanceLimit;
        int n3 = 1;
        int n4 = this.basePos[1] + this.height;
        int n5 = n2 - this.basePos[1];
        intses[0][0] = this.basePos[0];
        intses[0][1] = n2;
        intses[0][2] = this.basePos[2];
        intses[0][3] = n4;
        n2--;

        while (n5 >= 0) {
            int n6 = 0;
            float layerSize = this.layerSize(n5);
            if (layerSize < 0.0F) {
                n2--;
                n5--;
            } else {
                double d = 0.5;

                while (n6 < n) {
                    double d2 = this.scaleWidth * layerSize * (this.rand.nextFloat() + 0.328);
                    double d3 = this.rand.nextFloat() * 2.0 * Math.PI;
                    int n7 = MathHelper.floor_double(d2 * Math.sin(d3) + this.basePos[0] + d);
                    int n8 = MathHelper.floor_double(d2 * Math.cos(d3) + this.basePos[2] + d);
                    int[] ints = new int[]{n7, n2, n8};
                    int[] ints2 = new int[]{n7, n2 + this.leafDistanceLimit, n8};
                    if (this.checkBlockLine(ints, ints2) == -1) {
                        int[] ints3 = new int[]{this.basePos[0], this.basePos[1], this.basePos[2]};
                        double sqrt = Math.sqrt(Math.pow(Math.abs(this.basePos[0] - ints[0]), 2.0) + Math.pow(Math.abs(this.basePos[2] - ints[2]), 2.0));
                        double d4 = sqrt * this.branchSlope;
                        if (ints[1] - d4 > n4) {
                            ints3[1] = n4;
                        } else {
                            ints3[1] = (int)(ints[1] - d4);
                        }

                        if (this.checkBlockLine(ints3, ints) == -1) {
                            intses[n3][0] = n7;
                            intses[n3][1] = n2;
                            intses[n3][2] = n8;
                            intses[n3][3] = ints3[1];
                            n3++;
                        }
                    }

                    n6++;
                }

                n2--;
                n5--;
            }
        }

        this.leafNodes = new int[n3][4];
        System.arraycopy(intses, 0, this.leafNodes, 0, n3);
    }

    public void process(int n, int n2, int n3, float f, byte b, Block block) {
        int n4 = (int)(f + 0.618);
        byte b2 = otherCoordPairs[b];
        byte b3 = otherCoordPairs[b + 3];
        int[] ints = new int[]{n, n2, n3};
        int[] ints2 = new int[]{0, 0, 0};
        int n5 = -n4;
        int n6 = -n4;

        for (ints2[b] = ints[b]; n5 <= n4; n5++) {
            ints2[b2] = ints[b2] + n5;
            n6 = -n4;

            while (n6 <= n4) {
                double d = Math.pow(Math.abs(n5) + 0.5, 2.0) + Math.pow(Math.abs(n6) + 0.5, 2.0);
                if (d > f * f) {
                    n6++;
                } else {
                    ints2[b3] = ints[b3] + n6;
                    Block block2 = this.worldObj.getBlock(ints2[0], ints2[1], ints2[2]);
                    if (block2.getMaterial() != Material.air && block2.getMaterial() != Material.leaves) {
                        n6++;
                    } else {
                        this.setBlockAndNotifyAdequately(this.worldObj, ints2[0], ints2[1], ints2[2], block, 0);
                        n6++;
                    }
                }
            }
        }
    }

    public float layerSize(int n) {
        if (n < (float)this.heightLimit * 0.3) {
            return -1.618F;
        }

        float f = this.heightLimit / 2.0F;
        float f2 = this.heightLimit / 2.0F - n;
        float f3;
        if (f2 == 0.0F) {
            f3 = f;
        } else if (Math.abs(f2) >= f) {
            f3 = 0.0F;
        } else {
            f3 = (float)Math.sqrt(Math.pow(Math.abs(f), 2.0) - Math.pow(Math.abs(f2), 2.0));
        }

        return f3 * 0.5F;
    }

    public float leafSize(int n) {
        return n >= 0 && n < this.leafDistanceLimit ? (n != 0 && n != this.leafDistanceLimit - 1 ? 3.0F : 2.0F) : -1.0F;
    }

    public void generateLeafNode(int n, int n2, int n3) {
        int n4 = n2;

        for (int i = n2 + this.leafDistanceLimit; n4 < i; n4++) {
            float leafSize = this.leafSize(n4 - n2);
            this.process(n, n4, n3, leafSize, (byte)1, Blocks.leaves);
        }
    }

    public void process(int[] ints, int[] ints2, Block block) {
        int[] ints3 = new int[]{0, 0, 0};
        byte b = 0;
        int n = 0;

        while (b < 3) {
            ints3[b] = ints2[b] - ints[b];
            if (Math.abs(ints3[b]) > Math.abs(ints3[n])) {
                n = b;
            }

            b++;
        }

        if (ints3[n] != 0) {
            byte b2 = otherCoordPairs[n];
            byte b3 = otherCoordPairs[n + 3];
            int n2;
            if (ints3[n] > 0) {
                n2 = 1;
            } else {
                n2 = -1;
            }

            double d = (double)ints3[b2] / ints3[n];
            double d2 = (double)ints3[b3] / ints3[n];
            int[] ints4 = new int[]{0, 0, 0};
            int n3 = 0;

            for (int i = ints3[n] + n2; n3 != i; n3 += n2) {
                ints4[n] = MathHelper.floor_double(ints[n] + n3 + 0.5);
                ints4[b2] = MathHelper.floor_double(ints[b2] + n3 * d + 0.5);
                ints4[b3] = MathHelper.floor_double(ints[b3] + n3 * d2 + 0.5);
                int n4 = 0;
                int abs = Math.abs(ints4[0] - ints[0]);
                int abs2 = Math.abs(ints4[2] - ints[2]);
                int max = Math.max(abs, abs2);
                if (max > 0) {
                    if (abs == max) {
                        n4 = 4;
                    } else if (abs2 == max) {
                        n4 = 8;
                    }
                }

                this.setBlockAndNotifyAdequately(this.worldObj, ints4[0], ints4[1], ints4[2], block, n4);
            }
        }
    }

    public void generateLeaves() {
        int n = 0;

        for (int length = this.leafNodes.length; n < length; n++) {
            int n2 = this.leafNodes[n][0];
            int n3 = this.leafNodes[n][1];
            int n4 = this.leafNodes[n][2];
            this.generateLeafNode(n2, n3, n4);
        }
    }

    public boolean leafNodeNeedsBase(int n) {
        return n >= this.heightLimit * 0.2;
    }

    public void generateTrunk() {
        int n = this.basePos[0];
        int n2 = this.basePos[1];
        int n3 = this.basePos[1] + this.height;
        int n4 = this.basePos[2];
        int[] ints = new int[]{n, n2, n4};
        int[] ints2 = new int[]{n, n3, n4};
        this.process(ints, ints2, Blocks.log);
        if (this.trunkSize == 2) {
            ints[0]++;
            ints2[0]++;
            this.process(ints, ints2, Blocks.log);
            ints[2]++;
            ints2[2]++;
            this.process(ints, ints2, Blocks.log);
            ints[0] += -1;
            ints2[0] += -1;
            this.process(ints, ints2, Blocks.log);
        }
    }

    public void generateLeafNodeBases() {
        int n = 0;
        int length = this.leafNodes.length;
        int[] ints = new int[]{this.basePos[0], this.basePos[1], this.basePos[2]};

        while (n < length) {
            int[] ints2 = this.leafNodes[n];
            int[] ints3 = new int[]{ints2[0], ints2[1], ints2[2]};
            ints[1] = ints2[3];
            int n2 = ints[1] - this.basePos[1];
            if (this.leafNodeNeedsBase(n2)) {
                this.process(ints, ints3, Blocks.log);
            }

            n++;
        }
    }

    public int checkBlockLine(int[] ints, int[] ints2) {
        int[] ints3 = new int[]{0, 0, 0};
        byte b = 0;
        int n = 0;

        while (b < 3) {
            ints3[b] = ints2[b] - ints[b];
            if (Math.abs(ints3[b]) > Math.abs(ints3[n])) {
                n = b;
            }

            b++;
        }

        if (ints3[n] == 0) {
            return -1;
        }

        byte b2 = otherCoordPairs[n];
        byte b3 = otherCoordPairs[n + 3];
        int n2;
        if (ints3[n] > 0) {
            n2 = 1;
        } else {
            n2 = -1;
        }

        double d = (double)ints3[b2] / ints3[n];
        double d2 = (double)ints3[b3] / ints3[n];
        int[] ints4 = new int[]{0, 0, 0};
        int n3 = 0;

        int n4;
        for (n4 = ints3[n] + n2; n3 != n4; n3 += n2) {
            ints4[n] = ints[n] + n3;
            ints4[b2] = MathHelper.floor_double(ints[b2] + n3 * d);
            ints4[b3] = MathHelper.floor_double(ints[b3] + n3 * d2);
            Block block = this.worldObj.getBlock(ints4[0], ints4[1], ints4[2]);
            if (!this.isBlockReplaceable(block)) {
                break;
            }
        }

        return n3 == n4 ? -1 : Math.abs(n3);
    }

    public boolean validTreeLocation() {
        int[] ints = new int[]{this.basePos[0], this.basePos[1], this.basePos[2]};
        int[] ints2 = new int[]{this.basePos[0], this.basePos[1] + this.heightLimit - 1, this.basePos[2]};
        Block block = this.worldObj.getBlock(this.basePos[0], this.basePos[1] - 1, this.basePos[2]);
        if (block != Blocks.dirt && block != Blocks.grass && block != Blocks.farmland) {
            return false;
        }

        int checkBlockLine = this.checkBlockLine(ints, ints2);
        if (checkBlockLine == -1) {
            return true;
        }

        if (checkBlockLine < 6) {
            return false;
        }

        this.heightLimit = checkBlockLine;
        return true;
    }

    @Override
    public void setScale(double d, double d2, double d3) {
        this.heightLimitLimit = (int)(d * 12.0);
        if (d > 0.5) {
            this.leafDistanceLimit = 5;
        }

        this.scaleWidth = d2;
        this.leafDensity = d3;
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        this.worldObj = world;
        long nextLong = random.nextLong();
        this.rand.setSeed(nextLong);
        this.basePos[0] = n;
        this.basePos[1] = n2;
        this.basePos[2] = n3;
        if (this.heightLimit == 0) {
            this.heightLimit = 5 + this.rand.nextInt(this.heightLimitLimit);
        }

        if (!this.validTreeLocation()) {
            return false;
        }

        this.generateLeafNodeList();
        this.generateLeaves();
        this.generateTrunk();
        this.generateLeafNodeBases();
        return true;
    }
}
