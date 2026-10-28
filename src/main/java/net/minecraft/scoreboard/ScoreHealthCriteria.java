package net.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class ScoreHealthCriteria extends ScoreDummyCriteria {
    public ScoreHealthCriteria(String str) {
        super(str);
    }

    @Override
    public int computeInt(List list) {
        float f = 0.0F;

        for (EntityPlayer entityPlayer : (Iterable<EntityPlayer>)list) {
            f += entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount();
        }

        if (list.size() > 0) {
            f /= list.size();
        }

        return MathHelper.ceiling_float_int(f);
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }
}
