package net.minecraft.client.particle;

import net.minecraft.world.World;

public interface IParticleFactory {
   EntityFX getEntityFX(int n, World world, double d, double d2, double d3, double d4, double d5, double d6, int... ints);
}
