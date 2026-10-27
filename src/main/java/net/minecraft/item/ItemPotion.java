package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemPotion
extends Item {
    public Map<Integer, List<PotionEffect>> effectCache = Maps.newHashMap();
    public static Map<List<PotionEffect>, Integer> SUB_ITEMS_CACHE = Maps.newLinkedHashMap();

    public ItemPotion() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabBrewing);
    }

    public List<PotionEffect> getEffects(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomPotionEffects", 9)) {
            ArrayList<PotionEffect> arrayList = Lists.newArrayList();
            NBTTagList nBTTagList = stack.getTagCompound().getTagList("CustomPotionEffects", 10);
            for (int i = 0; i < nBTTagList.tagCount(); ++i) {
                NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(i);
                PotionEffect potionEffect = PotionEffect.readCustomPotionEffectFromNBT(nBTTagCompound);
                if (potionEffect == null) continue;
                arrayList.add(potionEffect);
            }
            return arrayList;
        }
        List<PotionEffect> list = this.effectCache.get(stack.getMetadata());
        if (list == null) {
            list = PotionHelper.getPotionEffects(stack.getMetadata(), false);
            this.effectCache.put(stack.getMetadata(), list);
        }
        return list;
    }

    public List<PotionEffect> getEffects(int meta) {
        List<PotionEffect> list = this.effectCache.get(meta);
        if (list == null) {
            list = PotionHelper.getPotionEffects(meta, false);
            this.effectCache.put(meta, list);
        }
        return list;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        List<PotionEffect> list;
        if (!playerIn.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        if (!worldIn.isRemote && (list = this.getEffects(stack)) != null) {
            for (PotionEffect potionEffect : list) {
                playerIn.addPotionEffect(new PotionEffect(potionEffect));
            }
        }
        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
        if (!playerIn.capabilities.isCreativeMode) {
            if (stack.stackSize <= 0) {
                return new ItemStack(Items.glass_bottle);
            }
            playerIn.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
        }
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        if (ItemPotion.isSplash(itemStackIn.getMetadata())) {
            if (!playerIn.capabilities.isCreativeMode) {
                --itemStackIn.stackSize;
            }
            worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
            if (!worldIn.isRemote) {
                worldIn.spawnEntityInWorld(new EntityPotion(worldIn, (EntityLivingBase)playerIn, itemStackIn));
            }
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
            return itemStackIn;
        }
        playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        return itemStackIn;
    }

    public static boolean isSplash(int meta) {
        return (meta & 0x4000) != 0;
    }

    public int getColorFromDamage(int meta) {
        return PotionHelper.getLiquidColor(meta, false);
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return renderPass > 0 ? 0xFFFFFF : this.getColorFromDamage(stack.getMetadata());
    }

    public boolean isEffectInstant(int meta) {
        List<PotionEffect> list = this.getEffects(meta);
        if (list != null && !list.isEmpty()) {
            for (PotionEffect potionEffect : list) {
                if (!Potion.potionTypes[potionEffect.getPotionID()].isInstant()) continue;
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        List<PotionEffect> list;
        if (stack.getMetadata() == 0) {
            return StatCollector.translateToLocal("item.emptyPotion.name").trim();
        }
        String string = "";
        if (ItemPotion.isSplash(stack.getMetadata())) {
            string = StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
        }
        if ((list = Items.potionitem.getEffects(stack)) != null && !list.isEmpty()) {
            String string2 = list.get(0).getEffectName();
            string2 = string2 + ".postfix";
            return string + StatCollector.translateToLocal(string2).trim();
        }
        String string3 = PotionHelper.getPotionPrefix(stack.getMetadata());
        return StatCollector.translateToLocal(string3).trim() + " " + super.getItemStackDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if (stack.getMetadata() != 0) {
            Object object;
            List<PotionEffect> list = Items.potionitem.getEffects(stack);
            HashMultimap<String, AttributeModifier> hashMultimap = HashMultimap.create();
            if (list != null && !list.isEmpty()) {
                for (PotionEffect object2 : list) {
                    object = StatCollector.translateToLocal(object2.getEffectName()).trim();
                    Potion potion = Potion.potionTypes[object2.getPotionID()];
                    Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();
                    if (map != null && map.size() > 0) {
                        for (Map.Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
                            AttributeModifier attributeModifier = entry.getValue();
                            AttributeModifier attributeModifier2 = new AttributeModifier(attributeModifier.getName(), potion.getAttributeModifierAmount(object2.getAmplifier(), attributeModifier), attributeModifier.getOperation());
                            hashMultimap.put(entry.getKey().getAttributeUnlocalizedName(), attributeModifier2);
                        }
                    }
                    if (object2.getAmplifier() > 0) {
                        object = (String)object + " " + StatCollector.translateToLocal("potion.potency." + object2.getAmplifier()).trim();
                    }
                    if (object2.getDuration() > 20) {
                        object = (String)object + " (" + Potion.getDurationString(object2) + ")";
                    }
                    if (potion.isBadEffect()) {
                        tooltip.add((Object)((Object)EnumChatFormatting.RED) + (String)object);
                        continue;
                    }
                    tooltip.add((Object)((Object)EnumChatFormatting.GRAY) + (String)object);
                }
            } else {
                String iterator = StatCollector.translateToLocal("potion.empty").trim();
                tooltip.add((Object)((Object)EnumChatFormatting.GRAY) + (String)((Object)iterator));
            }
            if (!hashMultimap.isEmpty()) {
                tooltip.add("");
                tooltip.add((Object)((Object)EnumChatFormatting.DARK_PURPLE) + StatCollector.translateToLocal("potion.effects.whenDrank"));
                for (Map.Entry entry : hashMultimap.entries()) {
                    object = (AttributeModifier)entry.getValue();
                    double d = ((AttributeModifier)object).getAmount();
                    double d2 = ((AttributeModifier)object).getOperation() != 1 && ((AttributeModifier)object).getOperation() != 2 ? ((AttributeModifier)object).getAmount() : ((AttributeModifier)object).getAmount() * 100.0;
                    if (d > 0.0) {
                        tooltip.add((Object)((Object)EnumChatFormatting.BLUE) + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + ((AttributeModifier)object).getOperation(), ItemStack.DECIMALFORMAT.format(d2), StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())));
                        continue;
                    }
                    if (!(d < 0.0)) continue;
                    tooltip.add((Object)((Object)EnumChatFormatting.RED) + StatCollector.translateToLocalFormatted("attribute.modifier.take." + ((AttributeModifier)object).getOperation(), ItemStack.DECIMALFORMAT.format(d2 *= -1.0), StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())));
                }
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        List<PotionEffect> list = this.getEffects(stack);
        return list != null && !list.isEmpty();
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        super.getSubItems(itemIn, tab, subItems);
        if (SUB_ITEMS_CACHE.isEmpty()) {
            for (int i = 0; i <= 15; ++i) {
                for (int j = 0; j <= 1; ++j) {
                    int n = j == 0 ? i | 0x2000 : i | 0x4000;
                    for (int k = 0; k <= 2; ++k) {
                        List<PotionEffect> list;
                        int n2 = n;
                        if (k != 0) {
                            if (k == 1) {
                                n2 = n | 0x20;
                            } else if (k == 2) {
                                n2 = n | 0x40;
                            }
                        }
                        if ((list = PotionHelper.getPotionEffects(n2, false)) == null || list.isEmpty()) continue;
                        SUB_ITEMS_CACHE.put(list, n2);
                    }
                }
            }
        }
        for (int j : SUB_ITEMS_CACHE.values()) {
            subItems.add(new ItemStack(itemIn, 1, j));
        }
    }
}
