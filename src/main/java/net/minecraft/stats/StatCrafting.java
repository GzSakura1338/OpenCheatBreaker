package net.minecraft.stats;

import net.minecraft.item.Item;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.IChatComponent;

public class StatCrafting extends StatBase {
   public Item item;

   public StatCrafting(String str, String str2, IChatComponent statNameIn, Item item) {
      super(str + str2, statNameIn);
      this.item = item;
      int idFromItem = Item.getIdFromItem(item);
      if (idFromItem != 0) {
         IScoreObjectiveCriteria.INSTANCES.put(str + idFromItem, this.getCriteria());
      }
   }

   public Item getItem() {
      return this.item;
   }
}
