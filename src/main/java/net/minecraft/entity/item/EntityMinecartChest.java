package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityMinecartChest extends EntityMinecartContainer {
    public EntityMinecartChest(World world) {
        super(world);
    }

    public EntityMinecartChest(World world, double d, double d2, double d3) {
        super(world, d, d2, d3);
    }

    @Override
    public void killMinecart(DamageSource damageSource) {
        super.killMinecart(damageSource);
        this.dropItemWithOffset(Item.getItemFromBlock(Blocks.chest), 1, 0.0F);
    }

    @Override
    public int getSizeInventory() {
        return 27;
    }

    @Override
    public int getMinecartType() {
        return 1;
    }

    @Override
    public Block getDefaultDisplayTile() {
        return Blocks.chest;
    }

    @Override
    public int getDefaultDisplayTileOffset() {
        return 8;
    }
}
