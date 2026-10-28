package net.minecraft.entity.item;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFallingBlock extends Entity {
    public boolean shouldDropItem = true;
    public int fallHurtMax = 40;
    public int fallTime;
    public float fallHurtAmount = 2.0F;
    public boolean destroyedOnLanding;
    public boolean hurtEntities;
    public Block blockObj;
    public int metadata;
    public NBTTagCompound tileEntityData;

    public EntityFallingBlock(World world) {
        super(world);
    }

    public EntityFallingBlock(World world, double d, double d2, double d3, Block block) {
        this(world, d, d2, d3, block, 0);
    }

    public EntityFallingBlock(World world, double d, double d2, double d3, Block block, int n) {
        super(world);
        this.blockObj = block;
        this.metadata = n;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(d, d2, d3);
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.prevPosX = d;
        this.prevPosY = d2;
        this.prevPosZ = d3;
    }

    @Override
    public boolean canTriggerWalking() {
        return false;
    }

    @Override
    public void entityInit() {
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public void onUpdate() {
        if (this.blockObj.getMaterial() == Material.air) {
            this.setDead();
        } else {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.fallTime++;
            this.motionY -= 0.04F;
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.98F;
            this.motionY *= 0.98F;
            this.motionZ *= 0.98F;
            if (!this.worldObj.isRemote) {
                int n = MathHelper.floor_double(this.posX);
                int n2 = MathHelper.floor_double(this.posY);
                int n3 = MathHelper.floor_double(this.posZ);
                if (this.fallTime == 1) {
                    if (this.worldObj.getBlock(n, n2, n3) != this.blockObj) {
                        this.setDead();
                        return;
                    }

                    this.worldObj.setBlockToAir(n, n2, n3);
                }

                if (this.onGround) {
                    this.motionX *= 0.7F;
                    this.motionZ *= 0.7F;
                    this.motionY *= -0.5;
                    if (this.worldObj.getBlock(n, n2, n3) != Blocks.piston_extension) {
                        this.setDead();
                        if (!this.destroyedOnLanding
                            && this.worldObj.canPlaceEntityOnSide(this.blockObj, n, n2, n3, true, 1, null, null)
                            && !BlockFalling.canFallBelow(this.worldObj, n, n2 - 1, n3)
                            && this.worldObj.setBlock(n, n2, n3, this.blockObj, this.metadata, 3)) {
                            if (this.blockObj instanceof BlockFalling) {
                                ((BlockFalling)this.blockObj).playSoundWhenFallen(this.worldObj, n, n2, n3, this.metadata);
                            }

                            if (this.tileEntityData != null && this.blockObj instanceof ITileEntityProvider) {
                                TileEntity tileEntity = this.worldObj.getTileEntity(n, n2, n3);
                                if (tileEntity != null) {
                                    NBTTagCompound nbttagCompound = new NBTTagCompound();
                                    tileEntity.writeToNBT(nbttagCompound);

                                    for (String str : (Iterable<String>)this.tileEntityData.getKeySet()) {
                                        NBTBase tag = this.tileEntityData.getTag(str);
                                        if (!str.equals("x") && !str.equals("y") && !str.equals("z")) {
                                            nbttagCompound.setTag(str, tag.copy());
                                        }
                                    }

                                    tileEntity.readFromNBT(nbttagCompound);
                                    tileEntity.markDirty();
                                }
                            }
                        } else if (this.shouldDropItem && !this.destroyedOnLanding) {
                            this.entityDropItem(new ItemStack(this.blockObj, 1, this.blockObj.damageDropped(this.metadata)), 0.0F);
                        }
                    }
                } else if (this.fallTime > 100 && !this.worldObj.isRemote && (n2 < 1 || n2 > 256) || this.fallTime > 600) {
                    if (this.shouldDropItem) {
                        this.entityDropItem(new ItemStack(this.blockObj, 1, this.blockObj.damageDropped(this.metadata)), 0.0F);
                    }

                    this.setDead();
                }
            }
        }
    }

    @Override
    public void fall(float distance) {
        if (this.hurtEntities) {
            int n = MathHelper.ceiling_float_int(distance - 1.0F);
            if (n > 0) {
                ArrayList list = new ArrayList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox));
                boolean flag = this.blockObj == Blocks.anvil;
                DamageSource damageSource = flag ? DamageSource.anvil : DamageSource.fallingBlock;

                for (Entity entity : (Iterable<Entity>)list) {
                    entity.attackEntityFrom(damageSource, Math.min(MathHelper.floor_float(n * this.fallHurtAmount), this.fallHurtMax));
                }

                if (flag && this.rand.nextFloat() < 0.05F + n * 0.05) {
                    int n2 = this.metadata >> 2;
                    int n3 = this.metadata & 3;
                    if (++n2 > 2) {
                        this.destroyedOnLanding = true;
                    } else {
                        this.metadata = n3 | n2 << 2;
                    }
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setByte("Tile", (byte)Block.getIdFromBlock(this.blockObj));
        tagCompound.setInteger("TileID", Block.getIdFromBlock(this.blockObj));
        tagCompound.setByte("Data", (byte)this.metadata);
        tagCompound.setByte("Time", (byte)this.fallTime);
        tagCompound.setBoolean("DropItem", this.shouldDropItem);
        tagCompound.setBoolean("HurtEntities", this.hurtEntities);
        tagCompound.setFloat("FallHurtAmount", this.fallHurtAmount);
        tagCompound.setInteger("FallHurtMax", this.fallHurtMax);
        if (this.tileEntityData != null) {
            tagCompound.setTag("TileEntityData", this.tileEntityData);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        if (tagCompund.hasKey("TileID", 99)) {
            this.blockObj = Block.getBlockById(tagCompund.getInteger("TileID"));
        } else {
            this.blockObj = Block.getBlockById(tagCompund.getByte("Tile") & 255);
        }

        this.metadata = tagCompund.getByte("Data") & 255;
        this.fallTime = tagCompund.getByte("Time") & 255;
        if (tagCompund.hasKey("HurtEntities", 99)) {
            this.hurtEntities = tagCompund.getBoolean("HurtEntities");
            this.fallHurtAmount = tagCompund.getFloat("FallHurtAmount");
            this.fallHurtMax = tagCompund.getInteger("FallHurtMax");
        } else if (this.blockObj == Blocks.anvil) {
            this.hurtEntities = true;
        }

        if (tagCompund.hasKey("DropItem", 99)) {
            this.shouldDropItem = tagCompund.getBoolean("DropItem");
        }

        if (tagCompund.hasKey("TileEntityData", 10)) {
            this.tileEntityData = tagCompund.getCompoundTag("TileEntityData");
        }

        if (this.blockObj.getMaterial() == Material.air) {
            this.blockObj = Blocks.sand;
        }
    }

    @Override
    public float getShadowSize() {
        return 0.0F;
    }

    public World getWorldObj() {
        return this.worldObj;
    }

    public void setHurtEntities(boolean flag) {
        this.hurtEntities = flag;
    }

    @Override
    public boolean canRenderOnFire() {
        return false;
    }

    @Override
    public void addEntityCrashInfo(CrashReportCategory category) {
        super.addEntityCrashInfo(category);
        category.addCrashSection("Immitating block ID", Block.getIdFromBlock(this.blockObj));
        category.addCrashSection("Immitating block data", this.metadata);
    }

    public Block getBlock() {
        return this.blockObj;
    }
}
