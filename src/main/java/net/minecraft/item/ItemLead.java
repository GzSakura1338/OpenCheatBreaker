package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemLead extends Item {
    public ItemLead() {
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        Block block = world.getBlock(n, n2, n3);
        if (block.getRenderType() == 11) {
            if (world.isRemote) {
                return true;
            }

            checkEntityPlayer(entityPlayer, world, n, n2, n3);
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkEntityPlayer(EntityPlayer entityPlayer, World world, int n, int n2, int n3) {
        EntityLeashKnot knotForBlock = EntityLeashKnot.getKnotForBlock(world, n, n2, n3);
        boolean flag = false;
        double d = 7.0;
        List entitiesWithinAABB = world.getEntitiesWithinAABB(
            EntityLiving.class, AxisAlignedBB.getBoundingBox(n - d, n2 - d, n3 - d, n + d, n2 + d, n3 + d)
        );
        if (entitiesWithinAABB != null) {
            for (EntityLiving entityLiving : (Iterable<EntityLiving>)entitiesWithinAABB) {
                if (entityLiving.getLeashed() && entityLiving.getLeashedToEntity() == entityPlayer) {
                    if (knotForBlock == null) {
                        knotForBlock = EntityLeashKnot.createEntityLeashKnot(world, n, n2, n3);
                    }

                    entityLiving.setLeashedToEntity(knotForBlock, true);
                    flag = true;
                }
            }
        }

        return flag;
    }
}
