package net.minecraft.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemBow extends Item {
    public static String[] bowPullIconNameArray = new String[]{"pulling_0", "pulling_1", "pulling_2"};
    public IIcon[] iconArray;

    public ItemBow() {
        this.maxStackSize = 1;
        this.setMaxDurability(384);
        this.setCreativeTab(CreativeTabs.tabCombat);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer entityPlayer, int n) {
        boolean flag = entityPlayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemStack) > 0;
        if (flag || entityPlayer.inventory.hasItem(Items.arrow)) {
            int n2 = this.getMaxItemUseDuration(itemStack) - n;
            float f = n2 / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;
            if (f < 0.1) {
                return;
            }

            if (f > 1.0F) {
                f = 1.0F;
            }

            EntityArrow entityArrow = new EntityArrow(world, entityPlayer, f * 2.0F);
            if (f == 1.0F) {
                entityArrow.setIsCritical(true);
            }

            int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack);
            if (enchantmentLevel > 0) {
                entityArrow.setDamage(entityArrow.getDamage() + enchantmentLevel * 0.5 + 0.5);
            }

            int enchantmentLevel2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack);
            if (enchantmentLevel2 > 0) {
                entityArrow.setKnockbackStrength(enchantmentLevel2);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack) > 0) {
                entityArrow.setFire(100);
            }

            itemStack.damageItem(1, entityPlayer);
            world.playSoundAtEntity(entityPlayer, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
            if (flag) {
                entityArrow.canBePickedUp = 2;
            } else {
                entityPlayer.inventory.consumeInventoryItem(Items.arrow);
            }

            if (!world.isRemote) {
                world.spawnEntityInWorld(entityArrow);
            }
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return itemStack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.bow;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (player.capabilities.isCreativeMode || player.inventory.hasItem(Items.arrow)) {
            player.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        }

        return itemStackIn;
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(this.getIconString() + "_standby");
        this.iconArray = new IIcon[bowPullIconNameArray.length];

        for (int i = 0; i < this.iconArray.length; i++) {
            this.iconArray[i] = register.registerIcon(this.getIconString() + "_" + bowPullIconNameArray[i]);
        }
    }

    public IIcon getItemIconForUseDuration(int n) {
        return this.iconArray[n];
    }
}
