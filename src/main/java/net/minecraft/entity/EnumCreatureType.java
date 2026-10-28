package net.minecraft.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;

public enum EnumCreatureType {
    monster(IMob.class, 70, Material.air, false, false),
    creature(EntityAnimal.class, 10, Material.air, true, true),
    ambient(EntityAmbientCreature.class, 15, Material.air, true, false),
    waterCreature(EntityWaterMob.class, 5, Material.water, true, false);

    public Class creatureClass;
    public int maxNumberOfCreature;
    public Material creatureMaterial;
    public boolean isPeacefulCreature;
    public boolean isAnimal;

    EnumCreatureType(Class clazz, int n, Material material, boolean flag, boolean flag2) {
        this.creatureClass = clazz;
        this.maxNumberOfCreature = n;
        this.creatureMaterial = material;
        this.isPeacefulCreature = flag;
        this.isAnimal = flag2;
    }

    public Class getCreatureClass() {
        return this.creatureClass;
    }

    public int getMaxNumberOfCreature() {
        return this.maxNumberOfCreature;
    }

    public Material getCreatureMaterial() {
        return this.creatureMaterial;
    }

    public boolean getPeacefulCreature() {
        return this.isPeacefulCreature;
    }

    public boolean getAnimal() {
        return this.isAnimal;
    }
}
