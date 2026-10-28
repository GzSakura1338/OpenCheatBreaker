package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class EntitySilverfish extends EntityMob {
    public int allySummonCooldown;

    public EntitySilverfish(World world) {
        super(world);
        this.setSize(0.3F, 0.7F);
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6F);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0);
    }

    @Override
    public boolean canTriggerWalking() {
        return false;
    }

    @Override
    public Entity findPlayerToAttack() {
        double d = 8.0;
        return this.worldObj.getClosestVulnerablePlayerToEntity(this, d);
    }

    @Override
    public String getLivingSound() {
        return "mob.silverfish.say";
    }

    @Override
    public String getHurtSound() {
        return "mob.silverfish.hit";
    }

    @Override
    public String getDeathSound() {
        return "mob.silverfish.kill";
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        if (this.allySummonCooldown <= 0 && (source instanceof EntityDamageSource || source == DamageSource.magic)) {
            this.allySummonCooldown = 20;
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void attackEntity(Entity entity, float f) {
        if (this.attackTime <= 0 && f < 1.2F && entity.boundingBox.maxY > this.boundingBox.minY && entity.boundingBox.minY < this.boundingBox.maxY) {
            this.attackTime = 20;
            this.attackEntityAsMob(entity);
        }
    }

    @Override
    public void playStepSound(int x, int y, int z, Block blockIn) {
        this.playSound("mob.silverfish.step", 0.15F, 1.0F);
    }

    @Override
    public Item getDropItem() {
        return Item.getItemById(0);
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        this.renderYawOffset = this.rotationYaw;
        super.onUpdate();
    }

    @Override
    public void updateEntityActionState() {
        super.updateEntityActionState();
        if (!this.worldObj.isRemote) {
            if (this.allySummonCooldown > 0) {
                this.allySummonCooldown--;
                if (this.allySummonCooldown == 0) {
                    int n = MathHelper.floor_double(this.posX);
                    int n2 = MathHelper.floor_double(this.posY);
                    int n3 = MathHelper.floor_double(this.posZ);
                    boolean flag = false;

                    for (int i = 0; !flag && i <= 5 && i >= -5; i = (i <= 0 ? 1 - i : 0 - i)) {
                        for (int j = 0; !flag && j <= 10 && j >= -10; j = (j <= 0 ? 1 - j : 0 - j)) {
                            for (int k = 0; !flag && k <= 10 && k >= -10; k = (k <= 0 ? 1 - k : 0 - k)) {
                                if (this.worldObj.getBlock(n + j, n2 + i, n3 + k) == Blocks.monster_egg) {
                                    if (!this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                                        int blockMetadata = this.worldObj.getBlockMetadata(n + j, n2 + i, n3 + k);
                                        ImmutablePair findImmutablePair = BlockSilverfish.findImmutablePair(blockMetadata);
                                        this.worldObj.setBlock(n + j, n2 + i, n3 + k, (Block)findImmutablePair.getLeft(), (Integer)findImmutablePair.getRight(), 3);
                                    } else {
                                        this.worldObj.breakBlock(n + j, n2 + i, n3 + k, false);
                                    }

                                    Blocks.monster_egg.onBlockDestroyedByPlayer(this.worldObj, n + j, n2 + i, n3 + k, 0);
                                    if (this.rand.nextBoolean()) {
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (this.entityToAttack == null && !this.hasPath()) {
                int n = MathHelper.floor_double(this.posX);
                int n2 = MathHelper.floor_double(this.posY + 0.5);
                int n3 = MathHelper.floor_double(this.posZ);
                int nextInt = this.rand.nextInt(6);
                Block block = this.worldObj
                    .getBlock(n + Facing.offsetsXForSide[nextInt], n2 + Facing.offsetsYForSide[nextInt], n3 + Facing.offsetsZForSide[nextInt]);
                int blockMetadata = this.worldObj
                    .getBlockMetadata(n + Facing.offsetsXForSide[nextInt], n2 + Facing.offsetsYForSide[nextInt], n3 + Facing.offsetsZForSide[nextInt]);
                if (BlockSilverfish.checkBlock(block)) {
                    this.worldObj
                        .setBlock(
                            n + Facing.offsetsXForSide[nextInt],
                            n2 + Facing.offsetsYForSide[nextInt],
                            n3 + Facing.offsetsZForSide[nextInt],
                            Blocks.monster_egg,
                            BlockSilverfish.computeInt(block, blockMetadata),
                            3
                        );
                    this.spawnExplosionParticle();
                    this.setDead();
                } else {
                    this.updateWanderPath();
                }
            } else if (this.entityToAttack != null && !this.hasPath()) {
                this.entityToAttack = null;
            }
        }
    }

    @Override
    public float getBlockPathWeight(int n, int n2, int n3) {
        return this.worldObj.getBlock(n, n2 - 1, n3) == Blocks.stone ? 10.0F : super.getBlockPathWeight(n, n2, n3);
    }

    @Override
    public boolean isValidLightLevel() {
        return true;
    }

    @Override
    public boolean getCanSpawnHere() {
        if (super.getCanSpawnHere()) {
            EntityPlayer closestPlayerToEntity = this.worldObj.getClosestPlayerToEntity(this, 5.0);
            return closestPlayerToEntity == null;
        } else {
            return false;
        }
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }
}
