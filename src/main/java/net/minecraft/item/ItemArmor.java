package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.IEntitySelector.ArmoredMob;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemArmor extends Item {
    public static int[] maxDamageArray = new int[]{11, 16, 15, 13};
    public static String[] CLOTH_OVERLAY_NAMES = new String[]{
        "leather_helmet_overlay", "leather_chestplate_overlay", "leather_leggings_overlay", "leather_boots_overlay"
    };
    public static String[] EMPTY_SLOT_NAMES = new String[]{
        "empty_armor_slot_helmet", "empty_armor_slot_chestplate", "empty_armor_slot_leggings", "empty_armor_slot_boots"
    };
    public static IBehaviorDispenseItem dispenserBehavior = new BehaviorDefaultDispenseItem() {
        @Override
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing facingDirection = BlockDispenser.getFacingDirection(source.getBlockMetadata());
            int n = source.getXInt() + facingDirection.getFrontOffsetX();
            int n2 = source.getYInt() + facingDirection.getFrontOffsetY();
            int n3 = source.getZInt() + facingDirection.getFrontOffsetZ();
            AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(n, n2, n3, n + 1, n2 + 1, n3 + 1);
            List selectEntitiesWithinAABB = source.getWorld().selectEntitiesWithinAABB(EntityLivingBase.class, boundingBox, new ArmoredMob(stack));
            if (selectEntitiesWithinAABB.size() > 0) {
                EntityLivingBase entityLivingBase = (EntityLivingBase)selectEntitiesWithinAABB.get(0);
                int n4 = entityLivingBase instanceof EntityPlayer ? 1 : 0;
                int armorPosition = EntityLiving.getArmorPosition(stack);
                ItemStack itemStack = stack.copy();
                itemStack.stackSize = 1;
                entityLivingBase.setCurrentItemOrArmor(armorPosition - n4, itemStack);
                if (entityLivingBase instanceof EntityLiving) {
                    ((EntityLiving)entityLivingBase).setEquipmentDropChance(armorPosition, 2.0F);
                }

                stack.stackSize--;
                return stack;
            } else {
                return super.dispenseStack(source, stack);
            }
        }
    };
    public ItemArmor.ArmorMaterial material;
    public int armorType;
    public int renderIndex;
    public int damageReduceAmount;
    public IIcon overlayIcon;
    public IIcon emptySlotIcon;

    public ItemArmor(ItemArmor.ArmorMaterial armorMaterial, int n, int n2) {
        this.material = armorMaterial;
        this.armorType = n2;
        this.renderIndex = n;
        this.damageReduceAmount = armorMaterial.getDamageReductionAmount(n2);
        this.setMaxDurability(armorMaterial.getDurability(n2));
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabCombat);
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserBehavior);
    }

    @Override
    public int getColorFromItemStack(ItemStack itemStack, int n) {
        if (n > 0) {
            return 16777215;
        }

        int color = this.getColor(itemStack);
        if (color < 0) {
            color = 16777215;
        }

        return color;
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return this.material == ItemArmor.ArmorMaterial.CLOTH;
    }

    @Override
    public int getItemEnchantability() {
        return this.material.getEnchantability();
    }

    public ItemArmor.ArmorMaterial getArmorMaterial() {
        return this.material;
    }

    public boolean hasColor(ItemStack itemStack) {
        return this.material == ItemArmor.ArmorMaterial.CLOTH
            && itemStack.hasTagCompound()
            && itemStack.getTagCompound().hasKey("display", 10)
            && itemStack.getTagCompound().getCompoundTag("display").hasKey("color", 3);
    }

    public int getColor(ItemStack itemStack) {
        if (this.material != ItemArmor.ArmorMaterial.CLOTH) {
            return -1;
        }

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound == null) {
            return 10511680;
        }

        NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
        return compoundTag == null ? 10511680 : (compoundTag.hasKey("color", 3) ? compoundTag.getInteger("color") : 10511680);
    }

    @Override
    public IIcon getIconFromDamageForRenderPass(int n, int n2) {
        return n2 == 1 ? this.overlayIcon : super.getIconFromDamageForRenderPass(n, n2);
    }

    public void removeColor(ItemStack itemStack) {
        if (this.material == ItemArmor.ArmorMaterial.CLOTH) {
            NBTTagCompound tagCompound = itemStack.getTagCompound();
            if (tagCompound != null) {
                NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                if (compoundTag.hasKey("color")) {
                    compoundTag.removeTag("color");
                }
            }
        }
    }

    public void setColor(ItemStack itemStack, int n) {
        if (this.material != ItemArmor.ArmorMaterial.CLOTH) {
            throw new UnsupportedOperationException("Can't dye non-leather!");
        }

        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
            itemStack.setTagCompound(tagCompound);
        }

        NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
        if (!tagCompound.hasKey("display", 10)) {
            tagCompound.setTag("display", compoundTag);
        }

        compoundTag.setInteger("color", n);
    }

    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack itemStack2) {
        return this.material.getBaseItemForRepair() == itemStack2.getItem() || super.getIsRepairable(itemStack, itemStack2);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        if (this.material == ItemArmor.ArmorMaterial.CLOTH) {
            this.overlayIcon = register.registerIcon(CLOTH_OVERLAY_NAMES[this.armorType]);
        }

        this.emptySlotIcon = register.registerIcon(EMPTY_SLOT_NAMES[this.armorType]);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        int n = EntityLiving.getArmorPosition(itemStackIn) - 1;
        ItemStack currentArmor = player.getCurrentArmor(n);
        if (currentArmor == null) {
            player.setCurrentItemOrArmor(n, itemStackIn.copy());
            itemStackIn.stackSize = 0;
        }

        return itemStackIn;
    }

    public static IIcon getBackgroundIcon(int n) {
        switch (n) {
            case 0:
                return Items.diamond_helmet.emptySlotIcon;
            case 1:
                return Items.diamond_chestplate.emptySlotIcon;
            case 2:
                return Items.diamond_leggings.emptySlotIcon;
            case 3:
                return Items.diamond_boots.emptySlotIcon;
            default:
                return null;
        }
    }

    public enum ArmorMaterial {
        CLOTH("CLOTH", 0, 5, new int[]{1, 3, 2, 1}, 15),
        CHAIN("CHAIN", 1, 15, new int[]{2, 5, 4, 1}, 12),
        IRON("IRON", 2, 15, new int[]{2, 6, 5, 2}, 9),
        GOLD("GOLD", 3, 7, new int[]{2, 5, 3, 1}, 25),
        DIAMOND("DIAMOND", 4, 33, new int[]{3, 8, 6, 3}, 10);

        public int maxDamageFactor;
        public int[] damageReductionAmountArray;
        public int enchantability;

        public int getDurability(int n) {
            return ItemArmor.maxDamageArray[n] * this.maxDamageFactor;
        }

        public int getDamageReductionAmount(int n) {
            return this.damageReductionAmountArray[n];
        }

        public int getEnchantability() {
            return this.enchantability;
        }

        public Item getBaseItemForRepair() {
            return this == CLOTH
                ? Items.leather
                : (
                    this == CHAIN
                        ? Items.iron_ingot
                        : (this == GOLD ? Items.gold_ingot : (this == IRON ? Items.iron_ingot : (this == DIAMOND ? Items.diamond : null)))
                );
        }

        ArmorMaterial(String str, int n, int n2, int[] ints, int n3) {
            this.maxDamageFactor = n2;
            this.damageReductionAmountArray = ints;
            this.enchantability = n3;
        }
    }
}
