package net.minecraft.entity.monster;

import java.util.List;
import java.util.UUID;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityWitch extends EntityMob implements IRangedAttackMob {
    public static UUID DRINKING_SPEED_PENALTY_MODIFIER_ID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    public static AttributeModifier attributeModifier = new AttributeModifier(DRINKING_SPEED_PENALTY_MODIFIER_ID, "Drinking speed penalty", -0.25, 0)
        .setSaved(false);
    public static Item[] witchDrops = new Item[]{
        Items.glowstone_dust, Items.sugar, Items.redstone, Items.spider_eye, Items.glass_bottle, Items.gunpowder, Items.stick, Items.stick
    };
    public int witchAttackTimer;

    public EntityWitch(World world) {
        super(world);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0, 60, 10.0F));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.getDataWatcher().addObject(21, (byte)0);
    }

    @Override
    public String getLivingSound() {
        return "mob.witch.idle";
    }

    @Override
    public String getHurtSound() {
        return "mob.witch.hurt";
    }

    @Override
    public String getDeathSound() {
        return "mob.witch.death";
    }

    public void setAggressive(boolean flag) {
        this.getDataWatcher().updateObject(21, (byte)(flag ? 1 : 0));
    }

    public boolean getAggressive() {
        return this.getDataWatcher().getWatchableObjectByte(21) == 1;
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(26.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        if (!this.worldObj.isRemote) {
            if (this.getAggressive()) {
                if (this.witchAttackTimer-- <= 0) {
                    this.setAggressive(false);
                    ItemStack heldItem = this.getHeldItem();
                    this.setCurrentItemOrArmor(0, null);
                    if (heldItem != null && heldItem.getItem() == Items.potionitem) {
                        List effects = Items.potionitem.getEffects(heldItem);
                        if (effects != null) {
                            for (PotionEffect potionEffect : (Iterable<PotionEffect>)effects) {
                                this.addPotionEffect(new PotionEffect(potionEffect));
                            }
                        }
                    }

                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(attributeModifier);
                }
            } else {
                short s = -1;
                if (this.rand.nextFloat() < 0.15F && this.isInsideOfMaterial(Material.water) && !this.isPotionActive(Potion.waterBreathing)) {
                    s = 8237;
                } else if (this.rand.nextFloat() < 0.15F && this.isBurning() && !this.isPotionActive(Potion.fireResistance)) {
                    s = 16307;
                } else if (this.rand.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
                    s = 16341;
                } else if (this.rand.nextFloat() < 0.25F
                    && this.getAttackTarget() != null
                    && !this.isPotionActive(Potion.moveSpeed)
                    && this.getAttackTarget().getDistanceSqToEntity(this) > 121.0) {
                    s = 16274;
                } else if (this.rand.nextFloat() < 0.25F
                    && this.getAttackTarget() != null
                    && !this.isPotionActive(Potion.moveSpeed)
                    && this.getAttackTarget().getDistanceSqToEntity(this) > 121.0) {
                    s = 16274;
                }

                if (s > -1) {
                    this.setCurrentItemOrArmor(0, new ItemStack(Items.potionitem, 1, s));
                    this.witchAttackTimer = this.getHeldItem().getMaxItemUseDuration();
                    this.setAggressive(true);
                    IAttributeInstance entityAttribute = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
                    entityAttribute.removeModifier(attributeModifier);
                    entityAttribute.applyModifier(attributeModifier);
                }
            }

            if (this.rand.nextFloat() < 7.5E-4F) {
                this.worldObj.setEntityState(this, (byte)15);
            }
        }

        super.onLivingUpdate();
    }

    @Override
    public void handleHealthUpdate(byte b) {
        if (b == 15) {
            for (int i = 0; i < this.rand.nextInt(35) + 10; i++) {
                this.worldObj
                    .spawnParticle(
                        "witchMagic",
                        this.posX + this.rand.nextGaussian() * 0.13F,
                        this.boundingBox.maxY + 0.5 + this.rand.nextGaussian() * 0.13F,
                        this.posZ + this.rand.nextGaussian() * 0.13F,
                        0.0,
                        0.0,
                        0.0
                    );
            }
        } else {
            super.handleHealthUpdate(b);
        }
    }

    @Override
    public float applyPotionDamageCalculations(DamageSource damageSource, float f) {
        f = super.applyPotionDamageCalculations(damageSource, f);
        if (damageSource.getEntity() == this) {
            f = 0.0F;
        }

        if (damageSource.isMagicDamage()) {
            f = (float)(f * 0.15);
        }

        return f;
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        int n2 = this.rand.nextInt(3) + 1;

        for (int i = 0; i < n2; i++) {
            int nextInt = this.rand.nextInt(3);
            Item item = witchDrops[this.rand.nextInt(witchDrops.length)];
            if (n > 0) {
                nextInt += this.rand.nextInt(n + 1);
            }

            for (int j = 0; j < nextInt; j++) {
                this.dropItem(item, 1);
            }
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase entityLivingBase, float f) {
        if (!this.getAggressive()) {
            EntityPotion entityPotion = new EntityPotion(this.worldObj, this, 32732);
            entityPotion.rotationPitch -= -20.0F;
            double d = entityLivingBase.posX + entityLivingBase.motionX - this.posX;
            double d2 = entityLivingBase.posY + entityLivingBase.getEyeHeight() - 1.1F - this.posY;
            double d3 = entityLivingBase.posZ + entityLivingBase.motionZ - this.posZ;
            float f2 = MathHelper.sqrt_double(d * d + d3 * d3);
            if (f2 >= 8.0F && !entityLivingBase.isPotionActive(Potion.moveSlowdown)) {
                entityPotion.setPotionDamage(32698);
            } else if (entityLivingBase.getHealth() >= 8.0F && !entityLivingBase.isPotionActive(Potion.poison)) {
                entityPotion.setPotionDamage(32660);
            } else if (f2 <= 3.0F && !entityLivingBase.isPotionActive(Potion.weakness) && this.rand.nextFloat() < 0.25F) {
                entityPotion.setPotionDamage(32696);
            }

            entityPotion.setThrowableHeading(d, d2 + f2 * 0.2F, d3, 0.75F, 8.0F);
            this.worldObj.spawnEntityInWorld(entityPotion);
        }
    }
}
