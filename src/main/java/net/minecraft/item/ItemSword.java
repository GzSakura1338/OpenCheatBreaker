package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.world.World;

public class ItemSword extends Item {
    public ToolMaterial repairMaterial;
    public float attackMultiplier;

    public ItemSword(ToolMaterial toolMaterial) {
        this.repairMaterial = toolMaterial;
        this.maxStackSize = 1;
        this.setMaxDurability(toolMaterial.getMaxUses());
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.attackMultiplier = 4.0F + toolMaterial.getDamageVsEntity();
    }

    public float getAttackDamage() {
        return this.repairMaterial.getDamageVsEntity();
    }

    @Override
    public float getStrVsBlock(ItemStack itemStack, Block block) {
        if (block == Blocks.web) {
            return 15.0F;
        }

        Material material = block.getMaterial();
        return material != Material.plants && material != Material.vine && material != Material.coral && material != Material.leaves && material != Material.gourd ? 1.0F : 1.5F;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
        stack.damageItem(1, entityLivingBase2);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, int n, int n2, int n3, EntityLivingBase entityLivingBase) {
        if (blockIn.getBlockHardness(worldIn, n, n2, n3) != 0.0) {
            stack.damageItem(2, entityLivingBase);
        }

        return true;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 72000;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        player.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        return itemStackIn;
    }

    @Override
    public boolean canItemHarvestBlock(Block block) {
        return block == Blocks.web;
    }

    @Override
    public int getItemEnchantability() {
        return this.repairMaterial.getEnchantability();
    }

    public String getToolMaterialName() {
        return this.repairMaterial.toString();
    }

    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack itemStack2) {
        return this.repairMaterial.getBaseItemForRepair() == itemStack2.getItem() || super.getIsRepairable(itemStack, itemStack2);
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap itemAttributeModifiers = super.getItemAttributeModifiers();
        itemAttributeModifiers.put(
            SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
            new AttributeModifier(itemModifierUUID, "Weapon modifier", this.attackMultiplier, 0)
        );
        return itemAttributeModifiers;
    }
}
