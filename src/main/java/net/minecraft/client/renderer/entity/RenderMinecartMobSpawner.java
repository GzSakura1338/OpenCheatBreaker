package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;

public class RenderMinecartMobSpawner extends RenderMinecart {
    public void render(EntityMinecartMobSpawner entityMinecartMobSpawner, float f, Block block, int n) {
        super.render(entityMinecartMobSpawner, f, block, n);
        if (block == Blocks.mob_spawner) {
            TileEntityMobSpawnerRenderer.renderEntity(entityMinecartMobSpawner.getSpawnerBehavior(), entityMinecartMobSpawner.posX, entityMinecartMobSpawner.posY, entityMinecartMobSpawner.posZ, f);
        }
    }

    @Override
    public void render(EntityMinecart entityMinecart, float f, Block block, int n) {
        this.render((EntityMinecartMobSpawner)entityMinecart, f, block, n);
    }
}
