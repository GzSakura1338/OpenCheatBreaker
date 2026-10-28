package net.minecraft.util;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityPlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;

public class FoodStats {
    public int foodLevel = 20;
    public float foodSaturationLevel = 5.0F;
    public float foodExhaustionLevel;
    public int foodTimer;
    public int prevFoodLevel = 20;

    public void addStats(int n, float f) {
        this.foodLevel = Math.min(n + this.foodLevel, 20);
        this.foodSaturationLevel = Math.min(this.foodSaturationLevel + n * f * 2.0F, this.foodLevel);
    }

    public void addStats(ItemFood itemFood, ItemStack itemStack) {
        this.addStats(itemFood.getHealAmount(itemStack), itemFood.getSaturationModifier(itemStack));
    }

    public void onUpdate(EntityPlayer entityPlayer) {
        EnumDifficulty difficultySetting = entityPlayer.worldObj.difficultySetting;
        this.prevFoodLevel = this.foodLevel;
        if (this.foodExhaustionLevel > 4.0F) {
            this.foodExhaustionLevel -= 4.0F;
            if (this.foodSaturationLevel > 0.0F) {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
            } else if (difficultySetting != EnumDifficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        if (entityPlayer.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && this.foodLevel >= 18 && entityPlayer.shouldHeal()) {
            this.foodTimer++;
            if (this.foodTimer >= 80) {
                entityPlayer.heal(1.0F);
                this.addExhaustion(3.0F);
                this.foodTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            this.foodTimer++;
            if (this.foodTimer >= 80) {
                if (entityPlayer.getHealth() > 10.0F || difficultySetting == EnumDifficulty.HARD || entityPlayer.getHealth() > 1.0F && difficultySetting == EnumDifficulty.NORMAL) {
                    entityPlayer.attackEntityFrom(DamageSource.starve, 1.0F);
                }

                this.foodTimer = 0;
            }
        } else {
            this.foodTimer = 0;
        }

        CheatBreaker.getInstance().getEventBus().handleEvent(new EntityPlayerEvent(entityPlayer));
    }

    public void readNBT(NBTTagCompound nbttagCompound) {
        if (nbttagCompound.hasKey("foodLevel", 99)) {
            this.foodLevel = nbttagCompound.getInteger("foodLevel");
            this.foodTimer = nbttagCompound.getInteger("foodTickTimer");
            this.foodSaturationLevel = nbttagCompound.getFloat("foodSaturationLevel");
            this.foodExhaustionLevel = nbttagCompound.getFloat("foodExhaustionLevel");
        }
    }

    public void writeNBT(NBTTagCompound nbttagCompound) {
        nbttagCompound.setInteger("foodLevel", this.foodLevel);
        nbttagCompound.setInteger("foodTickTimer", this.foodTimer);
        nbttagCompound.setFloat("foodSaturationLevel", this.foodSaturationLevel);
        nbttagCompound.setFloat("foodExhaustionLevel", this.foodExhaustionLevel);
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public int getPrevFoodLevel() {
        return this.prevFoodLevel;
    }

    public boolean needFood() {
        return this.foodLevel < 20;
    }

    public void addExhaustion(float f) {
        this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + f, 40.0F);
    }

    public float getSaturationLevel() {
        return this.foodSaturationLevel;
    }

    public void setFoodLevel(int n) {
        this.foodLevel = n;
    }

    public void setFoodSaturationLevel(float f) {
        this.foodSaturationLevel = f;
    }
}
