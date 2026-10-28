package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemPotion extends Item {
    public HashMap effectCache = new HashMap();
    public static Map STATUS_EFFECTS_2 = new LinkedHashMap();
    public IIcon iIcon;
    public IIcon iIcon2;
    public IIcon iIcon3;

    public ItemPotion() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDurability(0);
        this.setCreativeTab(CreativeTabs.tabBrewing);
    }

    public List getEffects(ItemStack itemStack) {
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("CustomPotionEffects", 9)) {
            ArrayList list = new ArrayList();
            NBTTagList tagList = itemStack.getTagCompound().getTagList("CustomPotionEffects", 10);

            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                PotionEffect readCustomPotionEffectFromNBT = PotionEffect.readCustomPotionEffectFromNBT(compoundTagAt);
                if (readCustomPotionEffectFromNBT != null) {
                    list.add(readCustomPotionEffectFromNBT);
                }
            }

            return list;
        } else {
            List list = (List)this.effectCache.get(itemStack.getMetadata());
            if (list == null) {
                list = PotionHelper.getPotionEffects(itemStack.getMetadata(), false);
                this.effectCache.put(itemStack.getMetadata(), list);
            }

            return list;
        }
    }

    public List getEffects(int n) {
        List list = (List)this.effectCache.get(n);
        if (list == null) {
            list = PotionHelper.getPotionEffects(n, false);
            this.effectCache.put(n, list);
        }

        return list;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (!entityPlayer.capabilities.isCreativeMode) {
            itemStack.stackSize--;
        }

        if (!world.isRemote) {
            List effects = this.getEffects(itemStack);
            if (effects != null) {
                for (PotionEffect potionEffect : (Iterable<PotionEffect>)effects) {
                    entityPlayer.addPotionEffect(new PotionEffect(potionEffect));
                }
            }
        }

        if (!entityPlayer.capabilities.isCreativeMode) {
            if (itemStack.stackSize <= 0) {
                return new ItemStack(Items.glass_bottle);
            }

            entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
        }

        return itemStack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.drink;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (isSplash(itemStackIn.getMetadata())) {
            if (!player.capabilities.isCreativeMode) {
                itemStackIn.stackSize--;
            }

            worldIn.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            if (!worldIn.isRemote) {
                worldIn.spawnEntityInWorld(new EntityPotion(worldIn, player, itemStackIn));
            }

            return itemStackIn;
        } else {
            player.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
            return itemStackIn;
        }
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        return false;
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        return isSplash(n) ? this.iIcon : this.iIcon2;
    }

    @Override
    public IIcon getIconFromDamageForRenderPass(int n, int n2) {
        return n2 == 0 ? this.iIcon3 : super.getIconFromDamageForRenderPass(n, n2);
    }

    public static boolean isSplash(int n) {
        return (n & 16384) != 0;
    }

    public int getColorFromDamage(int n) {
        return PotionHelper.getColor(n, false);
    }

    @Override
    public int getColorFromItemStack(ItemStack itemStack, int n) {
        return n > 0 ? 16777215 : this.getColorFromDamage(itemStack.getMetadata());
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    public boolean isEffectInstant(int n) {
        List effects = this.getEffects(n);
        if (effects != null && !effects.isEmpty()) {
            for (PotionEffect potionEffect : (Iterable<PotionEffect>)effects) {
                if (Potion.potionTypes[potionEffect.getPotionID()].isInstant()) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemStack) {
        if (itemStack.getMetadata() == 0) {
            return StatCollector.translateToLocal("item.emptyPotion.name").trim();
        }

        String str = "";
        if (isSplash(itemStack.getMetadata())) {
            str = StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
        }

        List effects = Items.potionitem.getEffects(itemStack);
        if (effects != null && !effects.isEmpty()) {
            String effectName = ((PotionEffect)effects.get(0)).getEffectName();
            effectName = effectName + ".postfix";
            return str + StatCollector.translateToLocal(effectName).trim();
        } else {
            String computeInt2 = PotionHelper.computeInt2(itemStack.getMetadata());
            return StatCollector.translateToLocal(computeInt2).trim() + " " + super.getItemStackDisplayName(itemStack);
        }
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean flag) {
        if (itemStack.getMetadata() != 0) {
            List effects = Items.potionitem.getEffects(itemStack);
            HashMultimap hashMultimap = HashMultimap.create();
            if (effects != null && !effects.isEmpty()) {
                for (PotionEffect potionEffect : (Iterable<PotionEffect>)effects) {
                    String trim = StatCollector.translateToLocal(potionEffect.getEffectName()).trim();
                    Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
                    Map attributeModifiers = potion.getAttributeModifiers();
                    if (attributeModifiers != null && attributeModifiers.size() > 0) {
                        for (Entry entry : (Iterable<Entry>)attributeModifiers.entrySet()) {
                            AttributeModifier value = (AttributeModifier)entry.getValue();
                            AttributeModifier attributeModifier = new AttributeModifier(value.getName(), potion.computeDouble(potionEffect.getAmplifier(), value), value.getOperation());
                            hashMultimap.put(((IAttribute)entry.getKey()).getAttributeUnlocalizedName(), attributeModifier);
                        }
                    }

                    if (potionEffect.getAmplifier() > 0) {
                        trim = trim + " " + StatCollector.translateToLocal("potion.potency." + potionEffect.getAmplifier()).trim();
                    }

                    if (potionEffect.getDuration() > 20) {
                        trim = trim + " (" + Potion.getDurationString(potionEffect) + ")";
                    }

                    if (potion.isBadEffect()) {
                        list.add(EnumChatFormatting.RED + trim);
                    } else {
                        list.add(EnumChatFormatting.GRAY + trim);
                    }
                }
            } else {
                String trim = StatCollector.translateToLocal("potion.empty").trim();
                list.add(EnumChatFormatting.GRAY + trim);
            }

            if (!hashMultimap.isEmpty()) {
                list.add("");
                list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));

                for (Entry entry : (Iterable<Entry>)hashMultimap.entries()) {
                    AttributeModifier value = (AttributeModifier)entry.getValue();
                    double amount = value.getAmount();
                    double d;
                    if (value.getOperation() != 1 && value.getOperation() != 2) {
                        d = value.getAmount();
                    } else {
                        d = value.getAmount() * 100.0;
                    }

                    if (amount > 0.0) {
                        list.add(
                            EnumChatFormatting.BLUE
                                + StatCollector.translateToLocalFormatted(
                                    "attribute.modifier.plus." + value.getOperation(),
                                    ItemStack.MODIFIER_FORMAT.format(d),
                                    StatCollector.translateToLocal("attribute.name." + entry.getKey())
                                )
                        );
                    } else if (amount < 0.0) {
                        d *= -1.0;
                        list.add(
                            EnumChatFormatting.RED
                                + StatCollector.translateToLocalFormatted(
                                    "attribute.modifier.take." + value.getOperation(),
                                    ItemStack.MODIFIER_FORMAT.format(d),
                                    StatCollector.translateToLocal("attribute.name." + entry.getKey())
                                )
                        );
                    }
                }
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack itemStack) {
        List effects = this.getEffects(itemStack);
        return effects != null && !effects.isEmpty();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        super.getSubItems(item, creativeTabs, list);
        if (STATUS_EFFECTS_2.isEmpty()) {
            for (int i = 0; i <= 15; i++) {
                for (int j = 0; j <= 1; j++) {
                    int n;
                    if (j == 0) {
                        n = i | 8192;
                    } else {
                        n = i | 16384;
                    }

                    for (int k = 0; k <= 2; k++) {
                        int n2 = n;
                        if (k != 0) {
                            if (k == 1) {
                                n2 = n | 32;
                            } else if (k == 2) {
                                n2 = n | 64;
                            }
                        }

                        List potionEffects = PotionHelper.getPotionEffects(n2, false);
                        if (potionEffects != null && !potionEffects.isEmpty()) {
                            STATUS_EFFECTS_2.put(potionEffects, n2);
                        }
                    }
                }
            }
        }

        for (Object obj : STATUS_EFFECTS_2.values()) {
            int n = (Integer)obj;
            list.add(new ItemStack(item, 1, n));
        }
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.iIcon2 = register.registerIcon(this.getIconString() + "_bottle_drinkable");
        this.iIcon = register.registerIcon(this.getIconString() + "_bottle_splash");
        this.iIcon3 = register.registerIcon(this.getIconString() + "_overlay");
    }

    public static IIcon findIIcon(String str) {
        return str.equals("bottle_drinkable")
            ? Items.potionitem.iIcon2
            : (str.equals("bottle_splash") ? Items.potionitem.iIcon : (str.equals("overlay") ? Items.potionitem.iIcon3 : null));
    }
}
