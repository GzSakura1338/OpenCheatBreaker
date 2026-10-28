package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFood extends Item {
    public int itemUseDuration = 32;
    public int healAmount;
    public boolean isWolfsFavoriteMeat;
    public float saturationModifier;
    public boolean alwaysEdible;
    public int potionId;
    public int potionDuration;
    public int potionAmplifier;
    public float potionEffectProbability;

    public ItemFood(int n, float f, boolean flag) {
        this.healAmount = n;
        this.isWolfsFavoriteMeat = flag;
        this.saturationModifier = f;
        this.setCreativeTab(CreativeTabs.tabFood);
    }

    public ItemFood(int n, boolean flag) {
        this(n, 0.6F, flag);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        itemStack.stackSize--;
        entityPlayer.getFoodStats().addStats(this, itemStack);
        world.playSoundAtEntity(entityPlayer, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        this.onFoodEaten(itemStack, world, entityPlayer);
        return itemStack;
    }

    public void onFoodEaten(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (!world.isRemote && this.potionId > 0 && world.rand.nextFloat() < this.potionEffectProbability) {
            entityPlayer.addPotionEffect(new PotionEffect(this.potionId, this.potionDuration * 20, this.potionAmplifier));
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.eat;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (player.canEat(this.alwaysEdible)) {
            player.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        }

        return itemStackIn;
    }

    public int getHealAmount(ItemStack itemStackIn) {
        return this.healAmount;
    }

    public float getSaturationModifier(ItemStack itemStackIn) {
        return this.saturationModifier;
    }

    public boolean isWolfsFavoriteMeat() {
        return this.isWolfsFavoriteMeat;
    }

    public ItemFood setPotionEffect(int n, int duration, int amplifier, float probability) {
        this.potionId = n;
        this.potionDuration = duration;
        this.potionAmplifier = amplifier;
        this.potionEffectProbability = probability;
        return this;
    }

    public ItemFood setAlwaysEdible() {
        this.alwaysEdible = true;
        return this;
    }
}
