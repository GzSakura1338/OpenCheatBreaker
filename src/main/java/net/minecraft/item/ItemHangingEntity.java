package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class ItemHangingEntity extends Item {
    public Class hangingEntityClass;

    public ItemHangingEntity(Class clazz) {
        this.hangingEntityClass = clazz;
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (n4 == 0) {
            return false;
        }

        if (n4 == 1) {
            return false;
        }

        int n5 = Direction.facingToDirection[n4];
        EntityHanging createHangingEntity = this.createHangingEntity(world, n, n2, n3, n5);
        if (!entityPlayer.canPlayerEdit(n, n2, n3, n4, itemStack)) {
            return false;
        }

        if (createHangingEntity != null && createHangingEntity.onValidSurface()) {
            if (!world.isRemote) {
                world.spawnEntityInWorld(createHangingEntity);
            }

            itemStack.stackSize--;
        }

        return true;
    }

    public EntityHanging createHangingEntity(World world, int n, int n2, int n3, int n4) {
        return this.hangingEntityClass == EntityPainting.class
            ? new EntityPainting(world, n, n2, n3, n4)
            : (this.hangingEntityClass == EntityItemFrame.class ? new EntityItemFrame(world, n, n2, n3, n4) : null);
    }
}
