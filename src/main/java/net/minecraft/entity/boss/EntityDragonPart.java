package net.minecraft.entity.boss;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class EntityDragonPart extends Entity {
    public IEntityMultiPart entityDragonObj;
    public String name;

    public EntityDragonPart(IEntityMultiPart entityMultiPart, String str, float f, float f2) {
        super(entityMultiPart.getWorldObj());
        this.setSize(f, f2);
        this.entityDragonObj = entityMultiPart;
        this.name = str;
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

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return !this.isEntityInvulnerable() && this.entityDragonObj.attackEntityFromPart(this, source, amount);
    }

    @Override
    public boolean isEntityEqual(Entity entityIn) {
        return this == entityIn || this.entityDragonObj == entityIn;
    }
}
