package net.minecraft.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Explosion {
    public boolean isFlaming;
    public boolean isSmoking = true;
    public final int intValue = 16;
    public Random explosionRNG = new Random();
    public List affectedBlockPositions = new ArrayList();
    public Map affectedPlayers = new HashMap();
    public World worldObj;
    public Entity exploder;
    public float explosionSize;
    public double explosionX;
    public double explosionY;
    public double explosionZ;

    public Explosion(World world, Entity entity, double d, double d2, double d3, float f) {
        this.worldObj = world;
        this.exploder = entity;
        this.explosionSize = f;
        this.explosionX = d;
        this.explosionY = d2;
        this.explosionZ = d3;
    }

    public void doExplosionA() {
        float explosionSize = this.explosionSize;
        HashSet set = new HashSet();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    if (i == 0 || i == 16 - 1 || j == 0 || j == 16 - 1 || k == 0 || k == 16 - 1) {
                        double d = i / (16.0F - 1.0F) * 2.0F - 1.0F;
                        double d2 = j / (16.0F - 1.0F) * 2.0F - 1.0F;
                        double d3 = k / (16.0F - 1.0F) * 2.0F - 1.0F;
                        double sqrt = Math.sqrt(d * d + d2 * d2 + d3 * d3);
                        d /= sqrt;
                        d2 /= sqrt;
                        d3 /= sqrt;
                        float f = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                        double explosionX = this.explosionX;
                        double explosionY = this.explosionY;
                        double explosionZ = this.explosionZ;

                        for (float f2 = 0.3F; f > 0.0F; f -= f2 * 0.75F) {
                            int n = MathHelper.floor_double(explosionX);
                            int n2 = MathHelper.floor_double(explosionY);
                            int n3 = MathHelper.floor_double(explosionZ);
                            Block block = this.worldObj.getBlock(n, n2, n3);
                            if (block.getMaterial() != Material.air) {
                                float f3 = this.exploder != null
                                    ? this.exploder.getExplosionResistance(this, this.worldObj, n, n2, n3, block)
                                    : block.getExplosionResistance(this.exploder);
                                f -= (f3 + 0.3F) * f2;
                            }

                            if (f > 0.0F && (this.exploder == null || this.exploder.checkExplosion(this, this.worldObj, n, n2, n3, block, f))) {
                                set.add(new ChunkPosition(n, n2, n3));
                            }

                            explosionX += d * f2;
                            explosionY += d2 * f2;
                            explosionZ += d3 * f2;
                        }
                    }
                }
            }
        }

        this.affectedBlockPositions.addAll(set);
        this.explosionSize *= 2.0F;
        int n = MathHelper.floor_double(this.explosionX - this.explosionSize - 1.0);
        int n2 = MathHelper.floor_double(this.explosionX + this.explosionSize + 1.0);
        int n3 = MathHelper.floor_double(this.explosionY - this.explosionSize - 1.0);
        int n4 = MathHelper.floor_double(this.explosionY + this.explosionSize + 1.0);
        int n5 = MathHelper.floor_double(this.explosionZ - this.explosionSize - 1.0);
        int n6 = MathHelper.floor_double(this.explosionZ + this.explosionSize + 1.0);
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, AxisAlignedBB.getBoundingBox(n, n3, n5, n2, n4, n6));
        Vec3 createVectorHelper = Vec3.createVectorHelper(this.explosionX, this.explosionY, this.explosionZ);

        for (int i = 0; i < list.size(); i++) {
            Entity entity = (Entity)list.get(i);
            double d = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / this.explosionSize;
            if (d <= 1.0) {
                double d2 = entity.posX - this.explosionX;
                double d3 = entity.posY + entity.getEyeHeight() - this.explosionY;
                double d4 = entity.posZ - this.explosionZ;
                double d5 = MathHelper.sqrt_double(d2 * d2 + d3 * d3 + d4 * d4);
                if (d5 != 0.0) {
                    d2 /= d5;
                    d3 /= d5;
                    d4 /= d5;
                    double blockDensity = this.worldObj.getBlockDensity(createVectorHelper, entity.boundingBox);
                    double d6 = (1.0 - d) * blockDensity;
                    entity.attackEntityFrom(DamageSource.setExplosionSource(this), (int)((d6 * d6 + d6) / 2.0 * 8.0 * this.explosionSize + 1.0));
                    double computeDouble = EnchantmentProtection.computeDouble(entity, d6);
                    entity.motionX += d2 * computeDouble;
                    entity.motionY += d3 * computeDouble;
                    entity.motionZ += d4 * computeDouble;
                    if (entity instanceof EntityPlayer) {
                        this.affectedPlayers.put(entity, Vec3.createVectorHelper(d2 * d6, d3 * d6, d4 * d6));
                    }
                }
            }
        }

        this.explosionSize = explosionSize;
    }

    public void doExplosionB(boolean flag) {
        this.worldObj
            .playSoundEffect(
                this.explosionX,
                this.explosionY,
                this.explosionZ,
                "random.explode",
                4.0F,
                (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F
            );
        if (this.explosionSize >= 2.0F && this.isSmoking) {
            this.worldObj.spawnParticle("hugeexplosion", this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0);
        } else {
            this.worldObj.spawnParticle("largeexplode", this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0);
        }

        if (this.isSmoking) {
            for (ChunkPosition chunkPosition : (Iterable<ChunkPosition>)this.affectedBlockPositions) {
                int chunkPosX = chunkPosition.chunkPosX;
                int chunkPosY = chunkPosition.chunkPosY;
                int chunkPosZ = chunkPosition.chunkPosZ;
                Block block = this.worldObj.getBlock(chunkPosX, chunkPosY, chunkPosZ);
                if (flag) {
                    double d = chunkPosX + this.worldObj.rand.nextFloat();
                    double d2 = chunkPosY + this.worldObj.rand.nextFloat();
                    double d3 = chunkPosZ + this.worldObj.rand.nextFloat();
                    double d4 = d - this.explosionX;
                    double d5 = d2 - this.explosionY;
                    double d6 = d3 - this.explosionZ;
                    double d7 = MathHelper.sqrt_double(d4 * d4 + d5 * d5 + d6 * d6);
                    d4 /= d7;
                    d5 /= d7;
                    d6 /= d7;
                    double d8 = 0.5 / (d7 / this.explosionSize + 0.1);
                    d8 *= this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F;
                    d4 *= d8;
                    d5 *= d8;
                    d6 *= d8;
                    this.worldObj
                        .spawnParticle(
                            "explode",
                            (d + this.explosionX * 1.0) / 2.0,
                            (d2 + this.explosionY * 1.0) / 2.0,
                            (d3 + this.explosionZ * 1.0) / 2.0,
                            d4,
                            d5,
                            d6
                        );
                    this.worldObj.spawnParticle("smoke", d, d2, d3, d4, d5, d6);
                }

                if (block.getMaterial() != Material.air) {
                    if (block.canDropFromExplosion(this)) {
                        block.dropBlockAsItemWithChance(
                            this.worldObj, chunkPosX, chunkPosY, chunkPosZ, this.worldObj.getBlockMetadata(chunkPosX, chunkPosY, chunkPosZ), 1.0F / this.explosionSize, 0
                        );
                    }

                    this.worldObj.setBlock(chunkPosX, chunkPosY, chunkPosZ, Blocks.air, 0, 3);
                    block.onBlockDestroyedByExplosion(this.worldObj, chunkPosX, chunkPosY, chunkPosZ, this);
                }
            }
        }

        if (this.isFlaming) {
            for (ChunkPosition chunkPosition : (Iterable<ChunkPosition>)this.affectedBlockPositions) {
                int chunkPosX = chunkPosition.chunkPosX;
                int chunkPosY = chunkPosition.chunkPosY;
                int chunkPosZ = chunkPosition.chunkPosZ;
                Block block = this.worldObj.getBlock(chunkPosX, chunkPosY, chunkPosZ);
                Block block2 = this.worldObj.getBlock(chunkPosX, chunkPosY - 1, chunkPosZ);
                if (block.getMaterial() == Material.air && block2.isFullBlock() && this.explosionRNG.nextInt(3) == 0) {
                    this.worldObj.setBlock(chunkPosX, chunkPosY, chunkPosZ, Blocks.fire);
                }
            }
        }
    }

    public Map getAffectedPlayers() {
        return this.affectedPlayers;
    }

    public EntityLivingBase getExplosivePlacedBy() {
        return this.exploder == null
            ? null
            : (
                this.exploder instanceof EntityTNTPrimed
                    ? ((EntityTNTPrimed)this.exploder).getTntPlacedBy()
                    : (this.exploder instanceof EntityLivingBase ? (EntityLivingBase)this.exploder : null)
            );
    }
}
