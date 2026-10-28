package net.minecraft.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemFishingRod extends Item {
    public IIcon theIcon;

    public ItemFishingRod() {
        this.setMaxDurability(64);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    public boolean shouldRotateAroundWhenRendering() {
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (player.fishEntity != null) {
            int handleHookRetraction = player.fishEntity.handleHookRetraction();
            itemStackIn.damageItem(handleHookRetraction, player);
            player.swingItem();
        } else {
            worldIn.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            if (!worldIn.isRemote) {
                worldIn.spawnEntityInWorld(new EntityFishHook(worldIn, player));
            }

            player.swingItem();
        }

        return itemStackIn;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(this.getIconString() + "_uncast");
        this.theIcon = register.registerIcon(this.getIconString() + "_cast");
    }

    public IIcon getCastTexture() {
        return this.theIcon;
    }

    @Override
    public boolean isItemTool(ItemStack itemStack) {
        return super.isItemTool(itemStack);
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }
}
