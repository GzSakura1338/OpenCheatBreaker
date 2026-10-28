package net.minecraft.entity.effect;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityLightningBolt extends EntityWeatherEffect {
    public int lightningState;
    public long boltVertex;
    public int boltLivingTime;

    public EntityLightningBolt(World world, double d, double d2, double d3) {
        super(world);
        this.setLocationAndAngles(d, d2, d3, 0.0F, 0.0F);
        this.lightningState = 2;
        this.boltVertex = this.rand.nextLong();
        this.boltLivingTime = this.rand.nextInt(3) + 1;
        if (!world.isRemote
            && world.getGameRules().getGameRuleBooleanValue("doFireTick")
            && (world.difficultySetting == EnumDifficulty.NORMAL || world.difficultySetting == EnumDifficulty.HARD)
            && world.doChunksNearChunkExist(MathHelper.floor_double(d), MathHelper.floor_double(d2), MathHelper.floor_double(d3), 10)) {
            int n = MathHelper.floor_double(d);
            int n2 = MathHelper.floor_double(d2);
            int n3 = MathHelper.floor_double(d3);
            if (world.getBlock(n, n2, n3).getMaterial() == Material.air && Blocks.fire.canPlaceBlockAt(world, n, n2, n3)) {
                world.setBlock(n, n2, n3, Blocks.fire);
            }

            for (int i = 0; i < 4; i++) {
                n2 = MathHelper.floor_double(d) + this.rand.nextInt(3) - 1;
                n3 = MathHelper.floor_double(d2) + this.rand.nextInt(3) - 1;
                int n4 = MathHelper.floor_double(d3) + this.rand.nextInt(3) - 1;
                if (world.getBlock(n2, n3, n4).getMaterial() == Material.air && Blocks.fire.canPlaceBlockAt(world, n2, n3, n4)) {
                    world.setBlock(n2, n3, n4, Blocks.fire);
                }
            }
        }
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        super.onUpdate();
        if (this.lightningState == 2) {
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "ambient.weather.thunder", 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.explode", 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
        }

        this.lightningState--;
        if (this.lightningState < 0) {
            if (this.boltLivingTime == 0) {
                this.setDead();
            } else if (this.lightningState < -this.rand.nextInt(10)) {
                this.boltLivingTime--;
                this.lightningState = 1;
                this.boltVertex = this.rand.nextLong();
                if (!this.worldObj.isRemote
                    && this.worldObj.getGameRules().getGameRuleBooleanValue("doFireTick")
                    && this.worldObj
                        .doChunksNearChunkExist(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 10)) {
                    int n = MathHelper.floor_double(this.posX);
                    int n2 = MathHelper.floor_double(this.posY);
                    int n3 = MathHelper.floor_double(this.posZ);
                    if (this.worldObj.getBlock(n, n2, n3).getMaterial() == Material.air && Blocks.fire.canPlaceBlockAt(this.worldObj, n, n2, n3)) {
                        this.worldObj.setBlock(n, n2, n3, Blocks.fire);
                    }
                }
            }
        }

        if (this.lightningState >= 0) {
            if (this.worldObj.isRemote) {
                this.worldObj.lastLightningBolt = 2;
            } else {
                double d = 3.0;
                List list = this.worldObj
                    .getEntitiesWithinAABBExcludingEntity(
                        this,
                        AxisAlignedBB.getBoundingBox(this.posX - d, this.posY - d, this.posZ - d, this.posX + d, this.posY + 6.0 + d, this.posZ + d)
                    );

                for (int i = 0; i < list.size(); i++) {
                    Entity entity = (Entity)list.get(i);
                    entity.onStruckByLightning(this);
                }
            }
        }
    }

    @Override
    public void entityInit() {
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
    }
}
