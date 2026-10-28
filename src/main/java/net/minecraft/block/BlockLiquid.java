package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockLiquid extends Block {
    public IIcon[] textures;

    public BlockLiquid(Material material) {
        super(material);
        float f = 0.0F;
        float f2 = 0.0F;
        this.setBlockBounds(0.0F + f2, 0.0F + f, 0.0F + f2, 1.0F + f2, 1.0F + f, 1.0F + f2);
        this.setTickRandomly(true);
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z) {
        return this.blockMaterial != Material.lava;
    }

    @Override
    public int getBlockColor() {
        return 16777215;
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        if (this.blockMaterial != Material.water) {
            return 16777215;
        }

        int n = 0;
        int n2 = 0;
        int n3 = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int waterColorMultiplier = worldIn.getBiomeGenForCoords(x + j, z + i).waterColorMultiplier;
                n += (waterColorMultiplier & 0xFF0000) >> 16;
                n2 += (waterColorMultiplier & 0xFF00) >> 8;
                n3 += waterColorMultiplier & 0xFF;
            }
        }

        return (n / 9 & 0xFF) << 16 | (n2 / 9 & 0xFF) << 8 | n3 / 9 & 0xFF;
    }

    public static float getLiquidHeightPercent(int n) {
        if (n >= 8) {
            n = 0;
        }

        return (n + 1) / 9.0F;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side != 0 && side != 1 ? this.textures[1] : this.textures[0];
    }

    public int computeInt(World world, int n, int n2, int n3) {
        return world.getBlock(n, n2, n3).getMaterial() == this.blockMaterial ? world.getBlockMetadata(n, n2, n3) : -1;
    }

    public int getEffectiveFlowDecay(IBlockAccess blockAccess, int n, int n2, int n3) {
        if (blockAccess.getBlock(n, n2, n3).getMaterial() != this.blockMaterial) {
            return -1;
        }

        int blockMetadata = blockAccess.getBlockMetadata(n, n2, n3);
        if (blockMetadata >= 8) {
            blockMetadata = 0;
        }

        return blockMetadata;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean canStopRayTrace(int meta, boolean includeLiquid) {
        return includeLiquid && meta == 0;
    }

    @Override
    public boolean isBlockSolid(IBlockAccess worldIn, int x, int y, int z, int side) {
        Material material = worldIn.getBlock(x, y, z).getMaterial();
        return material != this.blockMaterial && (side == 1 || material != Material.ice && super.isBlockSolid(worldIn, x, y, z, side));
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        Material material = worldIn.getBlock(x, y, z).getMaterial();
        return material != this.blockMaterial && (side == 1 || super.shouldSideBeRendered(worldIn, x, y, z, side));
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }

    @Override
    public int getRenderType() {
        return 4;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return null;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    public Vec3 getFlowVector(IBlockAccess blockAccess, int n, int n2, int n3) {
        Vec3 createVectorHelper = Vec3.createVectorHelper(0.0, 0.0, 0.0);
        int effectiveFlowDecay = this.getEffectiveFlowDecay(blockAccess, n, n2, n3);

        for (int i = 0; i < 4; i++) {
            int n4 = n;
            int n5 = n3;
            if (i == 0) {
                n4 = n - 1;
            }

            if (i == 1) {
                n5 = n3 - 1;
            }

            if (i == 2) {
                n4++;
            }

            if (i == 3) {
                n5++;
            }

            int effectiveFlowDecay2 = this.getEffectiveFlowDecay(blockAccess, n4, n2, n5);
            if (effectiveFlowDecay2 < 0) {
                if (!blockAccess.getBlock(n4, n2, n5).getMaterial().blocksMovement()) {
                    effectiveFlowDecay2 = this.getEffectiveFlowDecay(blockAccess, n4, n2 - 1, n5);
                    if (effectiveFlowDecay2 >= 0) {
                        int n6 = effectiveFlowDecay2 - (effectiveFlowDecay - 8);
                        createVectorHelper = createVectorHelper.addVector((n4 - n) * n6, (n2 - n2) * n6, (n5 - n3) * n6);
                    }
                }
            } else if (effectiveFlowDecay2 >= 0) {
                int n6 = effectiveFlowDecay2 - effectiveFlowDecay;
                createVectorHelper = createVectorHelper.addVector((n4 - n) * n6, (n2 - n2) * n6, (n5 - n3) * n6);
            }
        }

        if (blockAccess.getBlockMetadata(n, n2, n3) >= 8) {
            boolean flag = false;
            if (flag || this.isBlockSolid(blockAccess, n, n2, n3 - 1, 2)) {
                flag = true;
            }

            if (flag || this.isBlockSolid(blockAccess, n, n2, n3 + 1, 3)) {
                flag = true;
            }

            if (flag || this.isBlockSolid(blockAccess, n - 1, n2, n3, 4)) {
                flag = true;
            }

            if (flag || this.isBlockSolid(blockAccess, n + 1, n2, n3, 5)) {
                flag = true;
            }

            if (flag || this.isBlockSolid(blockAccess, n, n2 + 1, n3 - 1, 2)) {
                flag = true;
            }

            if (flag || this.isBlockSolid(blockAccess, n, n2 + 1, n3 + 1, 3)) {
                flag = true;
            }

            if (flag || this.isBlockSolid(blockAccess, n - 1, n2 + 1, n3, 4)) {
                flag = true;
            }

            if (flag || this.isBlockSolid(blockAccess, n + 1, n2 + 1, n3, 5)) {
                flag = true;
            }

            if (flag) {
                createVectorHelper = createVectorHelper.normalize().addVector(0.0, -6.0, 0.0);
            }
        }

        return createVectorHelper.normalize();
    }

    @Override
    public void modifyEntityVelocity(World worldIn, int x, int y, int z, Entity entityIn, Vec3 velocity) {
        Vec3 flowVector = this.getFlowVector(worldIn, x, y, z);
        velocity.xCoord = velocity.xCoord + flowVector.xCoord;
        velocity.yCoord = velocity.yCoord + flowVector.yCoord;
        velocity.zCoord = velocity.zCoord + flowVector.zCoord;
    }

    @Override
    public int tickRate(World worldIn) {
        return this.blockMaterial == Material.water ? 5 : (this.blockMaterial == Material.lava ? (worldIn.provider.hasNoSky ? 10 : 30) : 0);
    }

    @Override
    public int getMixedBrightnessForBlock(IBlockAccess worldIn, int x, int y, int z) {
        int lightBrightnessForSkyBlocks = worldIn.getLightBrightnessForSkyBlocks(x, y, z, 0);
        int lightBrightnessForSkyBlocks2 = worldIn.getLightBrightnessForSkyBlocks(x, y + 1, z, 0);
        int n = lightBrightnessForSkyBlocks & 0xFF;
        int n2 = lightBrightnessForSkyBlocks2 & 0xFF;
        int n3 = lightBrightnessForSkyBlocks >> 16 & 0xFF;
        int n4 = lightBrightnessForSkyBlocks2 >> 16 & 0xFF;
        return (n > n2 ? n : n2) | (n3 > n4 ? n3 : n4) << 16;
    }

    @Override
    public int getRenderBlockPass() {
        return this.blockMaterial == Material.water ? 1 : 0;
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        if (this.blockMaterial == Material.water) {
            if (random.nextInt(10) == 0) {
                int blockMetadata = worldIn.getBlockMetadata(x, y, z);
                if (blockMetadata <= 0 || blockMetadata >= 8) {
                    worldIn.spawnParticle("suspended", x + random.nextFloat(), y + random.nextFloat(), z + random.nextFloat(), 0.0, 0.0, 0.0);
                }
            }

            for (int i = 0; i < 0; i++) {
                int nextInt = random.nextInt(4);
                int n = x;
                int n2 = z;
                if (nextInt == 0) {
                    n = x - 1;
                }

                if (nextInt == 1) {
                    n++;
                }

                if (nextInt == 2) {
                    n2 = z - 1;
                }

                if (nextInt == 3) {
                    n2++;
                }

                if (worldIn.getBlock(n, y, n2).getMaterial() == Material.air
                    && (worldIn.getBlock(n, y - 1, n2).getMaterial().blocksMovement() || worldIn.getBlock(n, y - 1, n2).getMaterial().isLiquid())) {
                    float f = 0.0625F;
                    double d = x + random.nextFloat();
                    double d2 = y + random.nextFloat();
                    double d3 = z + random.nextFloat();
                    if (nextInt == 0) {
                        d = x - f;
                    }

                    if (nextInt == 1) {
                        d = x + 1 + f;
                    }

                    if (nextInt == 2) {
                        d3 = z - f;
                    }

                    if (nextInt == 3) {
                        d3 = z + 1 + f;
                    }

                    double d4 = 0.0;
                    double d5 = 0.0;
                    if (nextInt == 0) {
                        d4 = -f;
                    }

                    if (nextInt == 1) {
                        d4 = f;
                    }

                    if (nextInt == 2) {
                        d5 = -f;
                    }

                    if (nextInt == 3) {
                        d5 = f;
                    }

                    worldIn.spawnParticle("splash", d, d2, d3, d4, 0.0, d5);
                }
            }
        }

        if (this.blockMaterial == Material.water && random.nextInt(64) == 0) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            if (blockMetadata > 0 && blockMetadata < 8) {
                worldIn.playSound(x + 0.5F, y + 0.5F, z + 0.5F, "liquid.water", random.nextFloat() * 0.25F + 0.75F, random.nextFloat() * 1.0F + 0.5F, false);
            }
        }

        if (this.blockMaterial == Material.lava && worldIn.getBlock(x, y + 1, z).getMaterial() == Material.air && !worldIn.getBlock(x, y + 1, z).isOpaqueCube()) {
            if (random.nextInt(100) == 0) {
                double d = x + random.nextFloat();
                double d2 = y + this.maxY;
                double d3 = z + random.nextFloat();
                worldIn.spawnParticle("lava", d, d2, d3, 0.0, 0.0, 0.0);
                worldIn.playSound(d, d2, d3, "liquid.lavapop", 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }

            if (random.nextInt(200) == 0) {
                worldIn.playSound(x, y, z, "liquid.lava", 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        }

        if (random.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && !worldIn.getBlock(x, y - 2, z).getMaterial().blocksMovement()) {
            double d = x + random.nextFloat();
            double d2 = y - 1.05;
            double d3 = z + random.nextFloat();
            if (this.blockMaterial == Material.water) {
                worldIn.spawnParticle("dripWater", d, d2, d3, 0.0, 0.0, 0.0);
            } else {
                worldIn.spawnParticle("dripLava", d, d2, d3, 0.0, 0.0, 0.0);
            }
        }
    }

    public static double getFlowDirection(IBlockAccess blockAccess, int n, int n2, int n3, Material material) {
        Vec3 vec3 = null;
        if (material == Material.water) {
            vec3 = Blocks.flowing_water.getFlowVector(blockAccess, n, n2, n3);
        }

        if (material == Material.lava) {
            vec3 = Blocks.flowing_lava.getFlowVector(blockAccess, n, n2, n3);
        }

        return vec3.xCoord == 0.0 && vec3.zCoord == 0.0 ? -1000.0 : Math.atan2(vec3.zCoord, vec3.xCoord) - (Math.PI / 2);
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        this.handleWorld(worldIn, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        this.handleWorld(worldIn, x, y, z);
    }

    public void handleWorld(World world, int n, int n2, int n3) {
        if (world.getBlock(n, n2, n3) == this && this.blockMaterial == Material.lava) {
            boolean flag = false;
            if (flag || world.getBlock(n, n2, n3 - 1).getMaterial() == Material.water) {
                flag = true;
            }

            if (flag || world.getBlock(n, n2, n3 + 1).getMaterial() == Material.water) {
                flag = true;
            }

            if (flag || world.getBlock(n - 1, n2, n3).getMaterial() == Material.water) {
                flag = true;
            }

            if (flag || world.getBlock(n + 1, n2, n3).getMaterial() == Material.water) {
                flag = true;
            }

            if (flag || world.getBlock(n, n2 + 1, n3).getMaterial() == Material.water) {
                flag = true;
            }

            if (flag) {
                int blockMetadata = world.getBlockMetadata(n, n2, n3);
                if (blockMetadata == 0) {
                    world.setBlock(n, n2, n3, Blocks.obsidian);
                } else if (blockMetadata <= 4) {
                    world.setBlock(n, n2, n3, Blocks.cobblestone);
                }

                this.handleWorld2(world, n, n2, n3);
            }
        }
    }

    public void handleWorld2(World world, int n, int n2, int n3) {
        world.playSoundEffect(n + 0.5F, n2 + 0.5F, n3 + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

        for (int i = 0; i < 8; i++) {
            world.spawnParticle("largesmoke", n + Math.random(), n2 + 1.2, n3 + Math.random(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        if (this.blockMaterial == Material.lava) {
            this.textures = new IIcon[]{reg.registerIcon("lava_still"), reg.registerIcon("lava_flow")};
        } else {
            this.textures = new IIcon[]{reg.registerIcon("water_still"), reg.registerIcon("water_flow")};
        }
    }

    public static IIcon getLiquidIcon(String str) {
        return str == "water_still"
            ? Blocks.flowing_water.textures[0]
            : (
                str == "water_flow"
                    ? Blocks.flowing_water.textures[1]
                    : (str == "lava_still" ? Blocks.flowing_lava.textures[0] : (str == "lava_flow" ? Blocks.flowing_lava.textures[1] : null))
            );
    }
}
