package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemFishFood extends ItemFood {
    public boolean cooked;

    public ItemFishFood(boolean flag) {
        super(0, 0.0F, false);
        this.cooked = flag;
    }

    @Override
    public int getHealAmount(ItemStack itemStackIn) {
        ItemFishFood.FishType fishTypeForItemStack = ItemFishFood.FishType.getFishTypeForItemStack(itemStackIn);
        return this.cooked && fishTypeForItemStack.getCookable() ? fishTypeForItemStack.getCookedHealAmount() : fishTypeForItemStack.getUncookedHealAmount();
    }

    @Override
    public float getSaturationModifier(ItemStack itemStackIn) {
        ItemFishFood.FishType fishTypeForItemStack = ItemFishFood.FishType.getFishTypeForItemStack(itemStackIn);
        return this.cooked && fishTypeForItemStack.getCookable() ? fishTypeForItemStack.getCookedSaturationModifier() : fishTypeForItemStack.getUncookedSaturationModifier();
    }

    @Override
    public String getPotionEffect(ItemStack itemStack) {
        return ItemFishFood.FishType.getFishTypeForItemStack(itemStack) == ItemFishFood.FishType.PUFFERFISH ? PotionHelper.WATER_BREATHING : null;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        for (ItemFishFood.FishType fishType : ItemFishFood.FishType.values()) {
            fishType.registerIcon(register);
        }
    }

    @Override
    public void onFoodEaten(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        ItemFishFood.FishType fishTypeForItemStack = ItemFishFood.FishType.getFishTypeForItemStack(itemStack);
        if (fishTypeForItemStack == ItemFishFood.FishType.PUFFERFISH) {
            entityPlayer.addPotionEffect(new PotionEffect(Potion.poison.id, 1200, 3));
            entityPlayer.addPotionEffect(new PotionEffect(Potion.hunger.id, 300, 2));
            entityPlayer.addPotionEffect(new PotionEffect(Potion.confusion.id, 300, 1));
        }

        super.onFoodEaten(itemStack, world, entityPlayer);
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        ItemFishFood.FishType fishTypeForItemDamage = ItemFishFood.FishType.getFishTypeForItemDamage(n);
        return this.cooked && fishTypeForItemDamage.getCookable() ? fishTypeForItemDamage.getCookedIcon() : fishTypeForItemDamage.getUncookedIcon();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        for (ItemFishFood.FishType fishType : ItemFishFood.FishType.values()) {
            if (!this.cooked || fishType.getCookable()) {
                list.add(new ItemStack(this, 1, fishType.getItemDamage()));
            }
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        ItemFishFood.FishType fishTypeForItemStack = ItemFishFood.FishType.getFishTypeForItemStack(stack);
        return this.getUnlocalizedName() + "." + fishTypeForItemStack.getUnlocalizedNamePart() + "." + (this.cooked && fishTypeForItemStack.getCookable() ? "cooked" : "raw");
    }

    public enum FishType {
        COD("COD", 0, 0, "cod", 2, 0.1F, 5, 0.6F),
        SALMON("SALMON", 1, 1, "salmon", 2, 0.1F, 6, 0.8F),
        CLOWNFISH("CLOWNFISH", 2, 2, "clownfish", 1, 0.1F),
        PUFFERFISH("PUFFERFISH", 3, 3, "pufferfish", 1, 0.1F);

        public boolean cookable = false;
        public int itemDamage;
        public IIcon uncookedIcon;
        public IIcon cookedIcon;
        public String unlocalizedNamePart;
        public int uncookedHealAmount;
        public float uncookedSaturationModifier;
        public int cookedHealAmount;
        public float cookedSaturationModifier;
        public static Map itemDamageToFishTypeMap = Maps.newHashMap();
        public static ItemFishFood.FishType[] fishTypeArray = new ItemFishFood.FishType[]{COD, SALMON, CLOWNFISH, PUFFERFISH};

        public int getItemDamage() {
            return this.itemDamage;
        }

        public String getUnlocalizedNamePart() {
            return this.unlocalizedNamePart;
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

        public void registerIcon(IIconRegister iconRegister) {
            this.uncookedIcon = iconRegister.registerIcon("fish_" + this.unlocalizedNamePart + "_raw");
            if (this.cookable) {
                this.cookedIcon = iconRegister.registerIcon("fish_" + this.unlocalizedNamePart + "_cooked");
            }
        }

        public IIcon getUncookedIcon() {
            return this.uncookedIcon;
        }

        public IIcon getCookedIcon() {
            return this.cookedIcon;
        }

        public boolean getCookable() {
            return this.cookable;
        }

        public static ItemFishFood.FishType getFishTypeForItemDamage(int n) {
            ItemFishFood.FishType fishType = (ItemFishFood.FishType)itemDamageToFishTypeMap.get(n);
            return fishType == null ? COD : fishType;
        }

        public static ItemFishFood.FishType getFishTypeForItemStack(ItemStack itemStack) {
            return itemStack.getItem() instanceof ItemFishFood ? getFishTypeForItemDamage(itemStack.getMetadata()) : COD;
        }

        static {
            for (ItemFishFood.FishType fishType : values()) {
                itemDamageToFishTypeMap.put(fishType.getItemDamage(), fishType);
            }
        }

        FishType(String str, int n, int n2, String str2, int n3, float f, int n4, float f2) {
            this.itemDamage = n2;
            this.unlocalizedNamePart = str2;
            this.uncookedHealAmount = n3;
            this.uncookedSaturationModifier = f;
            this.cookedHealAmount = n4;
            this.cookedSaturationModifier = f2;
            this.cookable = true;
        }

        FishType(String str, int n, int n2, String str2, int n3, float f) {
            this.itemDamage = n2;
            this.unlocalizedNamePart = str2;
            this.uncookedHealAmount = n3;
            this.uncookedSaturationModifier = f;
            this.cookedHealAmount = 0;
            this.cookedSaturationModifier = 0.0F;
            this.cookable = false;
        }
    }
}
