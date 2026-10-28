package net.minecraft.stats;

import net.minecraft.item.Item;
import net.minecraft.util.IChatComponent;

public class StatCrafting extends StatBase {
    public Item item;

    public StatCrafting(String str, IChatComponent chatComponent, Item item) {
        super(str, chatComponent);
        this.item = item;
    }

    public Item getItem() {
        return this.item;
    }
}
