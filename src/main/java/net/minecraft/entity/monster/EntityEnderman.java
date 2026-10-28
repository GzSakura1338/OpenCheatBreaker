package net.minecraft.entity.monster;

import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityEnderman extends EntityMob {
    public static UUID attackingSpeedBoostModifierUUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    public static AttributeModifier attackingSpeedBoostModifier = new AttributeModifier(attackingSpeedBoostModifierUUID, "Attacking speed boost", 6.2F, 0)
        .setSaved(false);
    public static boolean[] carriableBlocks = new boolean[256];
    public int teleportDelay;
    public int stareTimer;
    public Entity lastEntityToAttack;
    public boolean isAggressive;

    public EntityEnderman(World world) {
        super(world);
        this.setSize(0.6F, 2.9F);
        this.stepHeight = 1.0F;
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(7.0);
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)0));
        this.dataWatcher.addObject(17, new Byte((byte)0));
        this.dataWatcher.addObject(18, new Byte((byte)0));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setShort("carried", (short)Block.getIdFromBlock(this.getCarriedBlock()));
        tagCompound.setShort("carriedData", (short)this.getCarryingData());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.setCarriedBlock(Block.getBlockById(tagCompund.getShort("carried")));
        this.setCarryingData(tagCompund.getShort("carriedData"));
    }

    @Override
    public Entity findPlayerToAttack() {
        EntityPlayer closestVulnerablePlayerToEntity = this.worldObj.getClosestVulnerablePlayerToEntity(this, 64.0);
        if (closestVulnerablePlayerToEntity != null) {
            if (this.shouldAttackPlayer(closestVulnerablePlayerToEntity)) {
                this.isAggressive = true;
                if (this.stareTimer == 0) {
                    this.worldObj.playSoundEffect(closestVulnerablePlayerToEntity.posX, closestVulnerablePlayerToEntity.posY, closestVulnerablePlayerToEntity.posZ, "mob.endermen.stare", 1.0F, 1.0F);
                }

                if (this.stareTimer++ == 5) {
                    this.stareTimer = 0;
                    this.setScreaming(true);
                    return closestVulnerablePlayerToEntity;
                }
            } else {
                this.stareTimer = 0;
            }
        }

        return null;
    }

    public boolean shouldAttackPlayer(EntityPlayer entityPlayer) {
        ItemStack itemStack = entityPlayer.inventory.armorInventory[3];
        if (itemStack != null && itemStack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
            return false;
        }

        Vec3 normalize = entityPlayer.getLook(1.0F).normalize();
        Vec3 createVectorHelper = Vec3.createVectorHelper(
            this.posX - entityPlayer.posX, this.boundingBox.minY + this.height / 2.0F - (entityPlayer.posY + entityPlayer.getEyeHeight()), this.posZ - entityPlayer.posZ
        );
        double lengthVector = createVectorHelper.lengthVector();
        createVectorHelper = createVectorHelper.normalize();
        double dotProduct = normalize.dotProduct(createVectorHelper);
        return dotProduct > 1.0 - 0.025 / lengthVector && entityPlayer.canEntityBeSeen(this);
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        if (this.isWet()) {
            this.attackEntityFrom(DamageSource.drown, 1.0F);
        }

        if (this.lastEntityToAttack != this.entityToAttack) {
            IAttributeInstance entityAttribute = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            entityAttribute.removeModifier(attackingSpeedBoostModifier);
            if (this.entityToAttack != null) {
                entityAttribute.applyModifier(attackingSpeedBoostModifier);
            }
        }

        this.lastEntityToAttack = this.entityToAttack;
        if (!this.worldObj.isRemote && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
            if (this.getCarriedBlock().getMaterial() == Material.air) {
                if (this.rand.nextInt(20) == 0) {
                    int n = MathHelper.floor_double(this.posX - 2.0 + this.rand.nextDouble() * 4.0);
                    int n2 = MathHelper.floor_double(this.posY + this.rand.nextDouble() * 3.0);
                    int n3 = MathHelper.floor_double(this.posZ - 2.0 + this.rand.nextDouble() * 4.0);
                    Block block = this.worldObj.getBlock(n, n2, n3);
                    if (carriableBlocks[Block.getIdFromBlock(block)]) {
                        this.setCarriedBlock(block);
                        this.setCarryingData(this.worldObj.getBlockMetadata(n, n2, n3));
                        this.worldObj.setBlock(n, n2, n3, Blocks.air);
                    }
                }
            } else if (this.rand.nextInt(2000) == 0) {
                int n = MathHelper.floor_double(this.posX - 1.0 + this.rand.nextDouble() * 2.0);
                int n2 = MathHelper.floor_double(this.posY + this.rand.nextDouble() * 2.0);
                int n3 = MathHelper.floor_double(this.posZ - 1.0 + this.rand.nextDouble() * 2.0);
                Block block = this.worldObj.getBlock(n, n2, n3);
                Block block2 = this.worldObj.getBlock(n, n2 - 1, n3);
                if (block.getMaterial() == Material.air && block2.getMaterial() != Material.air && block2.renderAsNormalBlock()) {
                    this.worldObj.setBlock(n, n2, n3, this.getCarriedBlock(), this.getCarryingData(), 3);
                    this.setCarriedBlock(Blocks.air);
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            this.worldObj
                .spawnParticle(
                    "portal",
                    this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                    this.posY + this.rand.nextDouble() * this.height - 0.25,
                    this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                    (this.rand.nextDouble() - 0.5) * 2.0,
                    -this.rand.nextDouble(),
                    (this.rand.nextDouble() - 0.5) * 2.0
                );
        }

        if (this.worldObj.isDaytime() && !this.worldObj.isRemote) {
            float brightness = this.getBrightness(1.0F);
            if (brightness > 0.5F
                && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F) {
                this.entityToAttack = null;
                this.setScreaming(false);
                this.isAggressive = false;
                this.teleportRandomly();
            }
        }

        if (this.isWet() || this.isBurning()) {
            this.entityToAttack = null;
            this.setScreaming(false);
            this.isAggressive = false;
            this.teleportRandomly();
        }

        if (this.isScreaming() && !this.isAggressive && this.rand.nextInt(100) == 0) {
            this.setScreaming(false);
        }

        this.isJumping = false;
        if (this.entityToAttack != null) {
            this.faceEntity(this.entityToAttack, 100.0F, 100.0F);
        }

        if (!this.worldObj.isRemote && this.isEntityAlive()) {
            if (this.entityToAttack != null) {
                if (this.entityToAttack instanceof EntityPlayer && this.shouldAttackPlayer((EntityPlayer)this.entityToAttack)) {
                    if (this.entityToAttack.getDistanceSqToEntity(this) < 16.0) {
                        this.teleportRandomly();
                    }

                    this.teleportDelay = 0;
                } else if (this.entityToAttack.getDistanceSqToEntity(this) > 256.0 && this.teleportDelay++ >= 30 && this.teleportToEntity(this.entityToAttack)) {
                    this.teleportDelay = 0;
                }
            } else {
                this.setScreaming(false);
                this.teleportDelay = 0;
            }
        }

        super.onLivingUpdate();
    }

    public boolean teleportRandomly() {
        double d = this.posX + (this.rand.nextDouble() - 0.5) * 64.0;
        double d2 = this.posY + (this.rand.nextInt(64) - 32);
        double d3 = this.posZ + (this.rand.nextDouble() - 0.5) * 64.0;
        return this.teleportTo(d, d2, d3);
    }

    public boolean teleportToEntity(Entity entity) {
        Vec3 createVectorHelper = Vec3.createVectorHelper(
            this.posX - entity.posX, this.boundingBox.minY + this.height / 2.0F - entity.posY + entity.getEyeHeight(), this.posZ - entity.posZ
        );
        createVectorHelper = createVectorHelper.normalize();
        double d = 16.0;
        double d2 = this.posX + (this.rand.nextDouble() - 0.5) * 8.0 - createVectorHelper.xCoord * d;
        double d3 = this.posY + (this.rand.nextInt(16) - 8) - createVectorHelper.yCoord * d;
        double d4 = this.posZ + (this.rand.nextDouble() - 0.5) * 8.0 - createVectorHelper.zCoord * d;
        return this.teleportTo(d2, d3, d4);
    }

    public boolean teleportTo(double d, double d2, double d3) {
        double posX = this.posX;
        double posY = this.posY;
        double posZ = this.posZ;
        this.posX = d;
        this.posY = d2;
        this.posZ = d3;
        boolean flag = false;
        int n = MathHelper.floor_double(this.posX);
        int n2 = MathHelper.floor_double(this.posY);
        int n3 = MathHelper.floor_double(this.posZ);
        if (this.worldObj.blockExists(n, n2, n3)) {
            boolean flag2 = false;

            while (!flag2 && n2 > 0) {
                Block block = this.worldObj.getBlock(n, n2 - 1, n3);
                if (block.getMaterial().blocksMovement()) {
                    flag2 = true;
                } else {
                    this.posY--;
                    n2--;
                }
            }

            if (flag2) {
                this.setPosition(this.posX, this.posY, this.posZ);
                if (this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox)) {
                    flag = true;
                }
            }
        }

        if (!flag) {
            this.setPosition(posX, posY, posZ);
            return false;
        }

        short s = 128;

        for (int i = 0; i < s; i++) {
            double d4 = i / (s - 1.0);
            float f = (this.rand.nextFloat() - 0.5F) * 0.2F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 0.2F;
            float f3 = (this.rand.nextFloat() - 0.5F) * 0.2F;
            double d5 = posX + (this.posX - posX) * d4 + (this.rand.nextDouble() - 0.5) * this.width * 2.0;
            double d6 = posY + (this.posY - posY) * d4 + this.rand.nextDouble() * this.height;
            double d7 = posZ + (this.posZ - posZ) * d4 + (this.rand.nextDouble() - 0.5) * this.width * 2.0;
            this.worldObj.spawnParticle("portal", d5, d6, d7, f, f2, f3);
        }

        this.worldObj.playSoundEffect(posX, posY, posZ, "mob.endermen.portal", 1.0F, 1.0F);
        this.playSound("mob.endermen.portal", 1.0F, 1.0F);
        return true;
    }

    @Override
    public String getLivingSound() {
        return this.isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
    }

    @Override
    public String getHurtSound() {
        return "mob.endermen.hit";
    }

    @Override
    public String getDeathSound() {
        return "mob.endermen.death";
    }

    @Override
    public Item getDropItem() {
        return Items.ender_pearl;
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        Item dropItem = this.getDropItem();
        if (dropItem != null) {
            int nextInt = this.rand.nextInt(2 + n);

            for (int i = 0; i < nextInt; i++) {
                this.dropItem(dropItem, 1);
            }
        }
    }

    public void setCarriedBlock(Block block) {
        this.dataWatcher.updateObject(16, (byte)(Block.getIdFromBlock(block) & 0xFF));
    }

    public Block getCarriedBlock() {
        return Block.getBlockById(this.dataWatcher.getWatchableObjectByte(16));
    }

    public void setCarryingData(int n) {
        this.dataWatcher.updateObject(17, (byte)(n & 0xFF));
    }

    public int getCarryingData() {
        return this.dataWatcher.getWatchableObjectByte(17);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        this.setScreaming(true);
        if (source instanceof EntityDamageSource && source.getEntity() instanceof EntityPlayer) {
            this.isAggressive = true;
        }

        if (source instanceof EntityDamageSourceIndirect) {
            this.isAggressive = false;

            for (int i = 0; i < 64; i++) {
                if (this.teleportRandomly()) {
                    return true;
                }
            }

            return false;
        } else {
            return super.attackEntityFrom(source, amount);
        }
    }

    public boolean isScreaming() {
        return this.dataWatcher.getWatchableObjectByte(18) > 0;
    }

    public void setScreaming(boolean flag) {
        this.dataWatcher.updateObject(18, (byte)(flag ? 1 : 0));
    }

    static {
        carriableBlocks[Block.getIdFromBlock(Blocks.grass)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.dirt)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.sand)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.gravel)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.yellow_flower)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.red_flower)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.brown_mushroom)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.red_mushroom)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.tnt)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.cactus)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.clay)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.pumpkin)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.melon_block)] = true;
        carriableBlocks[Block.getIdFromBlock(Blocks.mycelium)] = true;
    }
}
