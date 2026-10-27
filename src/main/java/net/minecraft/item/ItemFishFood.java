package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFishFood extends ItemFood {
   public boolean cooked;

   public ItemFishFood(boolean cooked) {
      super(0, 0.0F, false);
      this.cooked = cooked;
   }

   @Override
   public int getHealAmount(ItemStack stack) {
      ItemFishFood.FishType byItemStack = ItemFishFood.FishType.byItemStack(stack);
      return this.cooked && byItemStack.canCook() ? byItemStack.getCookedHealAmount() : byItemStack.getUncookedHealAmount();
   }

   @Override
   public float getSaturationModifier(ItemStack stack) {
      ItemFishFood.FishType byItemStack = ItemFishFood.FishType.byItemStack(stack);
      return this.cooked && byItemStack.canCook() ? byItemStack.getCookedSaturationModifier() : byItemStack.getUncookedSaturationModifier();
   }

   @Override
   public String getPotionEffect(ItemStack stack) {
      return ItemFishFood.FishType.byItemStack(stack) == ItemFishFood.FishType.PUFFERFISH ? "+0-1+2+3+13&4-4" : null;
   }

   @Override
   public void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
      ItemFishFood.FishType byItemStack = ItemFishFood.FishType.byItemStack(stack);
      if (byItemStack == ItemFishFood.FishType.PUFFERFISH) {
         player.addPotionEffect(new PotionEffect(Potion.poison.id, 1200, 3));
         player.addPotionEffect(new PotionEffect(Potion.hunger.id, 300, 2));
         player.addPotionEffect(new PotionEffect(Potion.confusion.id, 300, 1));
      }

      super.onFoodEaten(stack, worldIn, player);
   }

   @Override
   public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
      for (ItemFishFood.FishType fishType : ItemFishFood.FishType.values()) {
         if (!this.cooked || fishType.canCook()) {
            subItems.add(new ItemStack(this, 1, fishType.getMetadata()));
         }
      }
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      ItemFishFood.FishType byItemStack = ItemFishFood.FishType.byItemStack(stack);
      return this.getUnlocalizedName() + "." + byItemStack.getUnlocalizedName() + "." + (this.cooked && byItemStack.canCook() ? "cooked" : "raw");
   }

   public enum FishType {
      COD(0, "cod", 2, 0.1F, 5, 0.6F),
      SALMON(1, "salmon", 2, 0.1F, 6, 0.8F),
      CLOWNFISH(2, "clownfish", 1, 0.1F),
      PUFFERFISH(3, "pufferfish", 1, 0.1F);

      public boolean cookable = false;
      public int meta;
      public String unlocalizedName;
      public int uncookedHealAmount;
      public float uncookedSaturationModifier;
      public int cookedHealAmount;
      public float cookedSaturationModifier;
      public static Map<Integer, ItemFishFood.FishType> META_LOOKUP = Maps.newHashMap();

      FishType(int meta, String unlocalizedName, int uncookedHeal, float uncookedSaturation, int cookedHeal, float cookedSaturation) {
         this.meta = meta;
         this.unlocalizedName = unlocalizedName;
         this.uncookedHealAmount = uncookedHeal;
         this.uncookedSaturationModifier = uncookedSaturation;
         this.cookedHealAmount = cookedHeal;
         this.cookedSaturationModifier = cookedSaturation;
         this.cookable = true;
      }

      FishType(int meta, String unlocalizedName, int uncookedHeal, float uncookedSaturation) {
         this.meta = meta;
         this.unlocalizedName = unlocalizedName;
         this.uncookedHealAmount = uncookedHeal;
         this.uncookedSaturationModifier = uncookedSaturation;
         this.cookedHealAmount = 0;
         this.cookedSaturationModifier = 0.0F;
         this.cookable = false;
      }

      public int getMetadata() {
         return this.meta;
      }

      public String getUnlocalizedName() {
         return this.unlocalizedName;
      }

      public int getUncookedHealAmount() {
         return this.uncookedHealAmount;
      }

      public float getUncookedSaturationModifier() {
         return this.uncookedSaturationModifier;
      }

      public int getCookedHealAmount() {
         return this.cookedHealAmount;
      }

      public float getCookedSaturationModifier() {
         return this.cookedSaturationModifier;
      }

      public boolean canCook() {
         return this.cookable;
      }

      public static ItemFishFood.FishType byMetadata(int meta) {
         ItemFishFood.FishType fishType = META_LOOKUP.get(meta);
         return fishType == null ? COD : fishType;
      }

      public static ItemFishFood.FishType byItemStack(ItemStack stack) {
         return stack.getItem() instanceof ItemFishFood ? byMetadata(stack.getMetadata()) : COD;
      }

      static {
         for (ItemFishFood.FishType fishType : values()) {
            META_LOOKUP.put(fishType.getMetadata(), fishType);
         }
      }
   }
}
