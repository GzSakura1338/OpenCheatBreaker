package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class ItemSoup extends ItemFood {
    public ItemSoup(int n) {
        super(n, false);
        this.setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        super.onItemUseFinish(itemStack, world, entityPlayer);
        return new ItemStack(Items.bowl);
    }
}
