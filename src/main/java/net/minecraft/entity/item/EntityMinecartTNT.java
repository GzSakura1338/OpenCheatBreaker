package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityMinecartTNT extends EntityMinecart {
    public int minecartTNTFuse = -1;

    public EntityMinecartTNT(World world) {
        super(world);
    }

    public EntityMinecartTNT(World world, double d, double d2, double d3) {
        super(world, d, d2, d3);
    }

    @Override
    public int getMinecartType() {
        return 3;
    }

    @Override
    public Block getDefaultDisplayTile() {
        return Blocks.tnt;
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        super.onUpdate();
        if (this.minecartTNTFuse > 0) {
            this.minecartTNTFuse--;
            this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
        } else if (this.minecartTNTFuse == 0) {
            this.explodeCart(this.motionX * this.motionX + this.motionZ * this.motionZ);
        }

        if (this.isCollidedHorizontally) {
            double d = this.motionX * this.motionX + this.motionZ * this.motionZ;
            if (d >= 0.01F) {
                this.explodeCart(d);
            }
        }
    }

    @Override
    public void killMinecart(DamageSource damageSource) {
        super.killMinecart(damageSource);
        double d = this.motionX * this.motionX + this.motionZ * this.motionZ;
        if (!damageSource.isExplosion()) {
            this.entityDropItem(new ItemStack(Blocks.tnt, 1), 0.0F);
        }

        if (damageSource.isFireDamage() || damageSource.isExplosion() || d >= 0.01F) {
            this.explodeCart(d);
        }
    }

    public void explodeCart(double d) {
        if (!this.worldObj.isRemote) {
            double sqrt = Math.sqrt(d);
            if (sqrt > 5.0) {
                sqrt = 5.0;
            }

            this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)(4.0 + this.rand.nextDouble() * 1.5 * sqrt), true);
            this.setDead();
        }
    }

    @Override
    public void fall(float distance) {
        if (distance >= 3.0F) {
            float f = distance / 10.0F;
            this.explodeCart(f * f);
        }

        super.fall(distance);
    }

    @Override
    public void onActivatorRailPass(int n, int n2, int n3, boolean flag) {
        if (flag && this.minecartTNTFuse < 0) {
            this.ignite();
        }
    }

    @Override
    public void handleHealthUpdate(byte b) {
        if (b == 10) {
            this.ignite();
        } else {
            super.handleHealthUpdate(b);
        }
    }

    public void ignite() {
        this.minecartTNTFuse = 80;
        if (!this.worldObj.isRemote) {
            this.worldObj.setEntityState(this, (byte)10);
            this.worldObj.playSoundAtEntity(this, "game.tnt.primed", 1.0F, 1.0F);
        }
    }

    public int getFuseTicks() {
        return this.minecartTNTFuse;
    }

    public boolean isIgnited() {
        return this.minecartTNTFuse > -1;
    }

    @Override
    public float getExplosionResistance(Explosion explosionIn, World worldIn, int x, int y, int z, Block blockIn) {
        return !this.isIgnited() || !BlockRailBase.isRailBlock(blockIn) && !BlockRailBase.isRailBlockAt(worldIn, x, y + 1, z)
            ? super.getExplosionResistance(explosionIn, worldIn, x, y, z, blockIn)
            : 0.0F;
    }

    @Override
    public boolean checkExplosion(Explosion explosionIn, World worldIn, int x, int y, int z, Block blockIn, float unused) {
        return (!this.isIgnited() || !BlockRailBase.isRailBlock(blockIn) && !BlockRailBase.isRailBlockAt(worldIn, x, y + 1, z))
            && super.checkExplosion(explosionIn, worldIn, x, y, z, blockIn, unused);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        if (tagCompund.hasKey("TNTFuse", 99)) {
            this.minecartTNTFuse = tagCompund.getInteger("TNTFuse");
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("TNTFuse", this.minecartTNTFuse);
    }
}
