package net.minecraft.entity.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItem extends Entity {
    public static Logger logger = LogManager.getLogger();
    public int age;
    public int delayBeforeCanPickup;
    public int health = 5;
    public String thrower;
    public String owner;
    public float hoverStart = (float)(Math.random() * Math.PI * 2.0);

    public EntityItem(World worldIn, double x, double y, double z) {
        super(worldIn);
        this.setSize(0.25F, 0.25F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(x, y, z);
        this.rotationYaw = (float)(Math.random() * 360.0);
        this.motionX = (float)(Math.random() * 0.2F - 0.1F);
        this.motionY = 0.2F;
        this.motionZ = (float)(Math.random() * 0.2F - 0.1F);
    }

    public EntityItem(World worldIn, double x, double y, double z, ItemStack stack) {
        this(worldIn, x, y, z);
        this.setEntityItemStack(stack);
    }

    @Override
    public boolean canTriggerWalking() {
        return false;
    }

    public EntityItem(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.yOffset = this.height / 2.0F;
    }

    @Override
    public void entityInit() {
        this.getDataWatcher().addObjectByDataType(10, 5);
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        if (this.getEntityItem() == null) {
            this.setDead();
        } else {
            super.onUpdate();
            if (this.delayBeforeCanPickup > 0) {
                this.delayBeforeCanPickup--;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= 0.04F;
            this.noClip = this.pushOutOfBlocks(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0, this.posZ);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            boolean flag = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;
            if (flag || this.ticksExisted % 25 == 0) {
                if (this.worldObj
                        .getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                        .getMaterial()
                    == Material.lava) {
                    this.motionY = 0.2F;
                    this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
                    this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
                    this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
                }

                if (!this.worldObj.isRemote) {
                    this.searchForOtherItemsNearby();
                }
            }

            float f = 0.98F;
            if (this.onGround) {
                f = this.worldObj
                        .getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ))
                        .slipperiness
                    * 0.98F;
            }

            this.motionX *= f;
            this.motionY *= 0.98F;
            this.motionZ *= f;
            if (this.onGround) {
                this.motionY *= -0.5;
            }

            this.age++;
            if (!this.worldObj.isRemote && this.age >= 6000) {
                this.setDead();
            }
        }
    }

    public void searchForOtherItemsNearby() {
        for (EntityItem entityItem : (Iterable<EntityItem>)this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.5, 0.0, 0.5))) {
            this.combineItems(entityItem);
        }
    }

    public boolean combineItems(EntityItem entityItem) {
        if (entityItem == this) {
            return false;
        }

        if (entityItem.isEntityAlive() && this.isEntityAlive()) {
            ItemStack entityItem2 = this.getEntityItem();
            ItemStack entityItem3 = entityItem.getEntityItem();
            if (entityItem3.getItem() != entityItem2.getItem()) {
                return false;
            }

            if (entityItem3.hasTagCompound() ^ entityItem2.hasTagCompound()) {
                return false;
            }

            if (entityItem3.hasTagCompound() && !entityItem3.getTagCompound().equals(entityItem2.getTagCompound())) {
                return false;
            }

            if (entityItem3.getItem() == null) {
                return false;
            }

            if (entityItem3.getItem().getHasSubtypes() && entityItem3.getMetadata() != entityItem2.getMetadata()) {
                return false;
            }

            if (entityItem3.stackSize < entityItem2.stackSize) {
                return entityItem.combineItems(this);
            }

            if (entityItem3.stackSize + entityItem2.stackSize > entityItem3.getMaxStackSize()) {
                return false;
            }

            entityItem3.stackSize = entityItem3.stackSize + entityItem2.stackSize;
            entityItem.delayBeforeCanPickup = Math.max(entityItem.delayBeforeCanPickup, this.delayBeforeCanPickup);
            entityItem.age = Math.min(entityItem.age, this.age);
            entityItem.setEntityItemStack(entityItem3);
            this.setDead();
            return true;
        } else {
            return false;
        }
    }

    public void setAgeToCreativeDespawnTime() {
        this.age = 4800;
    }

    @Override
    public boolean handleWaterMovement() {
        return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
    }

    @Override
    public void dealFireDamage(int amount) {
        this.attackEntityFrom(DamageSource.inFire, amount);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        if (this.getEntityItem() != null && this.getEntityItem().getItem() == Items.nether_star && source.isExplosion()) {
            return false;
        }

        this.setBeenAttacked();
        this.health = (int)(this.health - amount);
        if (this.health <= 0) {
            this.setDead();
        }

        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setShort("Health", (byte)this.health);
        tagCompound.setShort("Age", (short)this.age);
        if (this.getThrower() != null) {
            tagCompound.setString("Thrower", this.thrower);
        }

        if (this.getOwner() != null) {
            tagCompound.setString("Owner", this.owner);
        }

        if (this.getEntityItem() != null) {
            tagCompound.setTag("Item", this.getEntityItem().writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        this.health = tagCompund.getShort("Health") & 255;
        this.age = tagCompund.getShort("Age");
        if (tagCompund.hasKey("Owner")) {
            this.owner = tagCompund.getString("Owner");
        }

        if (tagCompund.hasKey("Thrower")) {
            this.thrower = tagCompund.getString("Thrower");
        }

        NBTTagCompound compoundTag = tagCompund.getCompoundTag("Item");
        this.setEntityItemStack(ItemStack.loadItemStackFromNBT(compoundTag));
        if (this.getEntityItem() == null) {
            this.setDead();
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn) {
        if (!this.worldObj.isRemote) {
            ItemStack entityItem = this.getEntityItem();
            int stackSize = entityItem.stackSize;
            if (this.delayBeforeCanPickup == 0
                && (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(entityIn.getCommandSenderName()))
                && entityIn.inventory.addItemStackToInventory(entityItem)) {
                if (entityItem.getItem() == Item.getItemFromBlock(Blocks.log)) {
                    entityIn.triggerAchievement(AchievementList.mineWood);
                }

                if (entityItem.getItem() == Item.getItemFromBlock(Blocks.log2)) {
                    entityIn.triggerAchievement(AchievementList.mineWood);
                }

                if (entityItem.getItem() == Items.leather) {
                    entityIn.triggerAchievement(AchievementList.killCow);
                }

                if (entityItem.getItem() == Items.diamond) {
                    entityIn.triggerAchievement(AchievementList.diamonds);
                }

                if (entityItem.getItem() == Items.blaze_rod) {
                    entityIn.triggerAchievement(AchievementList.blazeRod);
                }

                if (entityItem.getItem() == Items.diamond && this.getThrower() != null) {
                    EntityPlayer playerEntityByName = this.worldObj.getPlayerEntityByName(this.getThrower());
                    if (playerEntityByName != null && playerEntityByName != entityIn) {
                        playerEntityByName.triggerAchievement(AchievementList.DIAMONDS_TO_YOU);
                    }
                }

                this.worldObj.playSoundAtEntity(entityIn, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                entityIn.onItemPickup(this, stackSize);
                if (entityItem.stackSize <= 0) {
                    this.setDead();
                }
            }
        }
    }

    @Override
    public String getCommandSenderName() {
        return StatCollector.translateToLocal("item." + this.getEntityItem().getUnlocalizedName());
    }

    @Override
    public boolean canAttackWithItem() {
        return false;
    }

    @Override
    public void travelToDimension(int dimensionId) throws java.io.IOException {
        super.travelToDimension(dimensionId);
        if (!this.worldObj.isRemote) {
            this.searchForOtherItemsNearby();
        }
    }

    public ItemStack getEntityItem() {
        ItemStack watchableObjectItemStack = this.getDataWatcher().getWatchableObjectItemStack(10);
        return watchableObjectItemStack == null ? new ItemStack(Blocks.stone) : watchableObjectItemStack;
    }

    public void setEntityItemStack(ItemStack itemStack) {
        this.getDataWatcher().updateObject(10, itemStack);
        this.getDataWatcher().setObjectWatched(10);
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String str) {
        this.owner = str;
    }

    public String getThrower() {
        return this.thrower;
    }

    public void setThrower(String str) {
        this.thrower = str;
    }
}
