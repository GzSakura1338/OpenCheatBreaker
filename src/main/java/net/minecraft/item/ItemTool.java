package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.world.World;

public class ItemTool extends Item {
    public float efficiencyOnProperMaterial = 4.0F;
    public ToolMaterial toolMaterial;
    public Set effectiveBlocksTool;
    public float damageVsEntity;

    public ItemTool(float f, ToolMaterial toolMaterial, Set set) {
        this.toolMaterial = toolMaterial;
        this.effectiveBlocksTool = set;
        this.maxStackSize = 1;
        this.setMaxDurability(toolMaterial.getMaxUses());
        this.efficiencyOnProperMaterial = toolMaterial.getEfficiencyOnProperMaterial();
        this.damageVsEntity = f + toolMaterial.getDamageVsEntity();
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public float getStrVsBlock(ItemStack itemStack, Block block) {
        return this.effectiveBlocksTool.contains(block) ? this.efficiencyOnProperMaterial : 1.0F;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
        stack.damageItem(2, entityLivingBase2);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, int n, int n2, int n3, EntityLivingBase entityLivingBase) {
        if (blockIn.getBlockHardness(worldIn, n, n2, n3) != 0.0) {
            stack.damageItem(1, entityLivingBase);
        }

        return true;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    public ToolMaterial getToolMaterial() {
        return this.toolMaterial;
    }

    @Override
    public int getItemEnchantability() {
        return this.toolMaterial.getEnchantability();
    }

    public String getToolMaterialName() {
        return this.toolMaterial.toString();
    }

    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack itemStack2) {
        return this.toolMaterial.getBaseItemForRepair() == itemStack2.getItem() || super.getIsRepairable(itemStack, itemStack2);
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap itemAttributeModifiers = super.getItemAttributeModifiers();
        itemAttributeModifiers.put(
            SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Tool modifier", this.damageVsEntity, 0)
        );
        return itemAttributeModifiers;
    }
}
