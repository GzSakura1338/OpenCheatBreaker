package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {
    public int weight;

    public BlockPressurePlateWeighted(String str, Material material, int n) {
        super(str, material);
        this.weight = n;
    }

    @Override
    public int getPlateState(World world, int n, int n2, int n3) {
        int min = Math.min(world.getEntitiesWithinAABB(Entity.class, this.getSensitiveAABB(n, n2, n3)).size(), this.weight);
        if (min <= 0) {
            return 0;
        }

        float f = (float)Math.min(this.weight, min) / this.weight;
        return MathHelper.ceiling_float_int(f * 15.0F);
    }

    @Override
    public int getPowerFromMeta(int n) {
        return n;
    }

    @Override
    public int getMetaFromPower(int n) {
        return n;
    }

    @Override
    public int tickRate(World worldIn) {
        return 10;
    }
}
