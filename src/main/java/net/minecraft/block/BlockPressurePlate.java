package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockPressurePlate extends BlockBasePressurePlate {
    public BlockPressurePlate.Sensitivity sensitivity;

    public BlockPressurePlate(String str, Material material, BlockPressurePlate.Sensitivity sensitivity) {
        super(str, material);
        this.sensitivity = sensitivity;
    }

    @Override
    public int getMetaFromPower(int n) {
        return n > 0 ? 1 : 0;
    }

    @Override
    public int getPowerFromMeta(int n) {
        return n == 1 ? 15 : 0;
    }

    @Override
    public int getPlateState(World world, int n, int n2, int n3) {
        List list = null;
        if (this.sensitivity == BlockPressurePlate.Sensitivity.everything) {
            list = world.getEntitiesWithinAABBExcludingEntity(null, this.getSensitiveAABB(n, n2, n3));
        }

        if (this.sensitivity == BlockPressurePlate.Sensitivity.mobs) {
            list = world.getEntitiesWithinAABB(EntityLivingBase.class, this.getSensitiveAABB(n, n2, n3));
        }

        if (this.sensitivity == BlockPressurePlate.Sensitivity.players) {
            list = world.getEntitiesWithinAABB(EntityPlayer.class, this.getSensitiveAABB(n, n2, n3));
        }

        if (list != null && !list.isEmpty()) {
            for (Entity entity : (Iterable<Entity>)list) {
                if (!entity.doesEntityNotTriggerPressurePlate()) {
                    return 15;
                }
            }
        }

        return 0;
    }

    public enum Sensitivity {
        everything("everything", 0),
        mobs("mobs", 1),
        players("players", 2);

        Sensitivity(String str, int n) {
        }
    }
}
