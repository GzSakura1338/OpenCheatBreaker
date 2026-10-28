package net.minecraft.entity.monster;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IAnimals;

public interface IMob extends IAnimals {
    IEntitySelector mobSelector = new IEntitySelector() {
        @Override
        public boolean isEntityApplicable(Entity entity) {
            return entity instanceof IMob;
        }
    };
}
