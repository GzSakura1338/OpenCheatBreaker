package net.minecraft.entity.monster;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySnowman extends EntityGolem implements IRangedAttackMob {
    public EntitySnowman(World world) {
        super(world);
        this.setSize(0.4F, 1.8F);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAIArrowAttack(this, 1.25, 20, 10.0F));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, true, false, IMob.mobSelector));
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2F);
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        super.onLivingUpdate();
        int n = MathHelper.floor_double(this.posX);
        int n2 = MathHelper.floor_double(this.posY);
        int n3 = MathHelper.floor_double(this.posZ);
        if (this.isWet()) {
            this.attackEntityFrom(DamageSource.drown, 1.0F);
        }

        if (this.worldObj.getBiomeGenForCoords(n, n3).getFloatTemperature(n, n2, n3) > 1.0F) {
            this.attackEntityFrom(DamageSource.onFire, 1.0F);
        }

        for (int i = 0; i < 4; i++) {
            n = MathHelper.floor_double(this.posX + (i % 2 * 2 - 1) * 0.25F);
            n2 = MathHelper.floor_double(this.posY);
            n3 = MathHelper.floor_double(this.posZ + (i / 2 % 2 * 2 - 1) * 0.25F);
            if (this.worldObj.getBlock(n, n2, n3).getMaterial() == Material.air
                && this.worldObj.getBiomeGenForCoords(n, n3).getFloatTemperature(n, n2, n3) < 0.8F
                && Blocks.snow_layer.canPlaceBlockAt(this.worldObj, n, n2, n3)) {
                this.worldObj.setBlock(n, n2, n3, Blocks.snow_layer);
            }
        }
    }

    @Override
    public Item getDropItem() {
        return Items.snowball;
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        int nextInt = this.rand.nextInt(16);

        for (int i = 0; i < nextInt; i++) {
            this.dropItem(Items.snowball, 1);
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase entityLivingBase, float f) {
        EntitySnowball entitySnowball = new EntitySnowball(this.worldObj, this);
        double d = entityLivingBase.posX - this.posX;
        double d2 = entityLivingBase.posY + entityLivingBase.getEyeHeight() - 1.1F - entitySnowball.posY;
        double d3 = entityLivingBase.posZ - this.posZ;
        float f2 = MathHelper.sqrt_double(d * d + d3 * d3) * 0.2F;
        entitySnowball.setThrowableHeading(d, d2 + f2, d3, 1.6F, 12.0F);
        this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(entitySnowball);
    }
}
