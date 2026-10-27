package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityCaveSpider extends EntitySpider {
   public EntityCaveSpider(World worldIn) {
      super(worldIn);
      this.setSize(0.7F, 0.5F);
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(12.0);
   }

   @Override
   public boolean attackEntityAsMob(Entity entityIn) {
      if (super.attackEntityAsMob(entityIn)) {
         if (entityIn instanceof EntityLivingBase) {
            byte b = 0;
            if (this.worldObj.getDifficulty() == EnumDifficulty.NORMAL) {
               b = 7;
            } else if (this.worldObj.getDifficulty() == EnumDifficulty.HARD) {
               b = 15;
            }

            if (b > 0) {
               ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(Potion.poison.id, b * 20, 0));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      return livingdata;
   }

   @Override
   public float getEyeHeight() {
      return 0.45F;
   }
}
