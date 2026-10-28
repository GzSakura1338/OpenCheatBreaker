package net.minecraft.world.gen.structure;

import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.MathHelper;

public class MapGenMineshaft extends MapGenStructure {
    public double chance = 0.004;

    public MapGenMineshaft() {
    }

    @Override
    public String getStructureName() {
        return "Mineshaft";
    }

    public MapGenMineshaft(Map map) {
        for (Entry entry : (Iterable<Entry>)map.entrySet()) {
            if (entry.getKey().equals("chance")) {
                this.chance = MathHelper.parseDoubleWithDefault((String)entry.getValue(), this.chance);
            }
        }
    }

    @Override
    public boolean canSpawnStructureAtCoords(int n, int n2) {
        return this.rand.nextDouble() < this.chance && this.rand.nextInt(80) < Math.max(Math.abs(n), Math.abs(n2));
    }

    @Override
    public StructureStart getStructureStart(int n, int n2) {
        return new StructureMineshaftStart(this.worldObj, this.rand, n, n2);
    }
}
