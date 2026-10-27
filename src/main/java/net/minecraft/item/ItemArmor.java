package net.minecraft.item;

import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.block.BlockDispenser;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EntitySelectors.ArmoredMob;
import net.minecraft.world.World;

public class ItemArmor extends Item {
   public static int[] maxDamageArray = new int[]{11, 16, 15, 13};
   public static String[] EMPTY_SLOT_NAMES = new String[]{
      "minecraft:items/empty_armor_slot_helmet",
      "minecraft:items/empty_armor_slot_chestplate",
      "minecraft:items/empty_armor_slot_leggings",
      "minecraft:items/empty_armor_slot_boots"
   };
   public static IBehaviorDispenseItem dispenserBehavior = new BehaviorDefaultDispenseItem() {
      @Override
      public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         BlockPos offset = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
         int n = offset.getX();
         int n2 = offset.getY();
         int n3 = offset.getZ();
         AxisAlignedBB axisAlignedBB = new AxisAlignedBB(n, n2, n3, n + 1, n2 + 1, n3 + 1);
         List entitiesWithinAABB = source.getWorld()
            .getEntitiesWithinAABB(EntityLivingBase.class, axisAlignedBB, Predicates.and(EntitySelectors.NOT_SPECTATING, new ArmoredMob(stack)));
         if (entitiesWithinAABB.size() > 0) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entitiesWithinAABB.get(0);
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

   public ItemArmor(ItemArmor.ArmorMaterial material, int renderIndex, int armorType) {
      this.material = material;
      this.armorType = armorType;
      this.renderIndex = renderIndex;
      this.damageReduceAmount = material.getDamageReductionAmount(armorType);
      this.setMaxDamage(material.getDurability(armorType));
      this.maxStackSize = 1;
      this.setCreativeTab(CreativeTabs.tabCombat);
      BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserBehavior);
   }

   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      if (renderPass > 0) {
         return 16777215;
      }

      int color = this.getColor(stack);
      if (color < 0) {
         color = 16777215;
      }

      return color;
   }

   @Override
   public int getItemEnchantability() {
      return this.material.getEnchantability();
   }

   public ItemArmor.ArmorMaterial getArmorMaterial() {
      return this.material;
   }

   public boolean hasColor(ItemStack stack) {
      return this.material != ItemArmor.ArmorMaterial.LEATHER
         ? false
         : (
            !stack.hasTagCompound()
               ? false
               : (!stack.getTagCompound().hasKey("display", 10) ? false : stack.getTagCompound().getCompoundTag("display").hasKey("color", 3))
         );
   }

   public int getColor(ItemStack stack) {
      if (this.material != ItemArmor.ArmorMaterial.LEATHER) {
         return -1;
      }

      NBTTagCompound tagCompound = stack.getTagCompound();
      if (tagCompound != null) {
         NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
         if (compoundTag != null && compoundTag.hasKey("color", 3)) {
            return compoundTag.getInteger("color");
         }
      }

      return 10511680;
   }

   public void removeColor(ItemStack stack) {
      if (this.material == ItemArmor.ArmorMaterial.LEATHER) {
         NBTTagCompound tagCompound = stack.getTagCompound();
         if (tagCompound != null) {
            NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
            if (compoundTag.hasKey("color")) {
               compoundTag.removeTag("color");
            }
         }
      }
   }

   public void setColor(ItemStack stack, int color) {
      if (this.material != ItemArmor.ArmorMaterial.LEATHER) {
         throw new UnsupportedOperationException("Can't dye non-leather!");
      }

      NBTTagCompound tagCompound = stack.getTagCompound();
      if (tagCompound == null) {
         tagCompound = new NBTTagCompound();
         stack.setTagCompound(tagCompound);
      }

      NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
      if (!tagCompound.hasKey("display", 10)) {
         tagCompound.setTag("display", compoundTag);
      }

      compoundTag.setInteger("color", color);
   }

   @Override
   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return this.material.getRepairItem() == repair.getItem() ? true : super.getIsRepairable(toRepair, repair);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      int n = EntityLiving.getArmorPosition(itemStackIn) - 1;
      ItemStack currentArmor = playerIn.getCurrentArmor(n);
      if (currentArmor == null) {
         playerIn.setCurrentItemOrArmor(n, itemStackIn.copy());
         itemStackIn.stackSize = 0;
      }

      return itemStackIn;
   }

   public enum ArmorMaterial {
      LEATHER("leather", 5, new int[]{1, 3, 2, 1}, 15),
      CHAIN("chainmail", 15, new int[]{2, 5, 4, 1}, 12),
      IRON("iron", 15, new int[]{2, 6, 5, 2}, 9),
      GOLD("gold", 7, new int[]{2, 5, 3, 1}, 25),
      DIAMOND("diamond", 33, new int[]{3, 8, 6, 3}, 10);

      public String name;
      public int maxDamageFactor;
      public int[] damageReductionAmountArray;
      public int enchantability;

      ArmorMaterial(String name, int maxDamage, int[] reductionAmounts, int enchantability) {
         this.name = name;
         this.maxDamageFactor = maxDamage;
         this.damageReductionAmountArray = reductionAmounts;
         this.enchantability = enchantability;
      }

      public int getDurability(int armorType) {
         return ItemArmor.maxDamageArray[armorType] * this.maxDamageFactor;
      }

      public int getDamageReductionAmount(int armorType) {
         return this.damageReductionAmountArray[armorType];
      }

      public int getEnchantability() {
         return this.enchantability;
      }

      public Item getRepairItem() {
         return this == LEATHER
            ? Items.leather
            : (
               this == CHAIN
                  ? Items.iron_ingot
                  : (this == GOLD ? Items.gold_ingot : (this == IRON ? Items.iron_ingot : (this == DIAMOND ? Items.diamond : null)))
            );
      }

      public String getName() {
         return this.name;
      }
   }
}
