package net.minecraft.item;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.util.ItemTooltipPart;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemStack {
    public static DecimalFormat MODIFIER_FORMAT = new DecimalFormat("#.###");
    public Item theItem;
    public int animationsToGo;
    public int stackSize;
    public NBTTagCompound stackTagCompound;
    public int metadata;
    public EntityItemFrame itemFrame;

    public ItemStack(Block block) {
        this(block, 1);
    }

    public ItemStack(Block block, int n) {
        this(block, n, 0);
    }

    public ItemStack(Block block, int n, int n2) {
        this(Item.getItemFromBlock(block), n, n2);
    }

    public ItemStack(Item item) {
        this(item, 1);
    }

    public ItemStack(Item item, int n) {
        this(item, n, 0);
    }

    public ItemStack(Item item, int n, int n2) {
        this.theItem = item;
        this.stackSize = n;
        this.metadata = n2;
        if (this.metadata < 0) {
            this.metadata = 0;
        }
    }

    public static ItemStack loadItemStackFromNBT(NBTTagCompound nBTTagCompound) {
        ItemStack itemStack = new ItemStack();
        itemStack.readFromNBT(nBTTagCompound);
        return itemStack.getItem() != null ? itemStack : null;
    }

    public ItemStack() {
    }

    public ItemStack splitStack(int n) {
        ItemStack itemStack = new ItemStack(this.theItem, n, this.metadata);
        if (this.stackTagCompound != null) {
            itemStack.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
        }
        this.stackSize -= n;
        return itemStack;
    }

    public Item getItem() {
        return this.theItem;
    }

    public IIcon getIconIndex() {
        return this.getItem().getIconIndex(this);
    }

    public int getItemSpriteNumber() {
        return this.getItem().getSpriteNumber();
    }

    public boolean tryPlaceItemIntoWorld(EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) throws java.io.IOException {
        boolean bl = this.getItem().onItemUse(this, entityPlayer, world, n, n2, n3, n4, f, f2, f3);
        if (bl) {
            entityPlayer.addStat(StatList.objectUseStats[Item.getIdFromItem(this.theItem)], 1);
        }
        return bl;
    }

    public float getStrVsBlock(Block block) {
        return this.getItem().getStrVsBlock(this, block);
    }

    public ItemStack useItemRightClick(World world, EntityPlayer entityPlayer) throws java.io.IOException {
        return this.getItem().onItemRightClick(this, world, entityPlayer);
    }

    public ItemStack onItemUseFinish(World world, EntityPlayer entityPlayer) {
        return this.getItem().onItemUseFinish(this, world, entityPlayer);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nBTTagCompound) {
        nBTTagCompound.setShort("id", (short)Item.getIdFromItem(this.theItem));
        nBTTagCompound.setByte("Count", (byte)this.stackSize);
        nBTTagCompound.setShort("Damage", (short)this.metadata);
        if (this.stackTagCompound != null) {
            nBTTagCompound.setTag("tag", this.stackTagCompound);
        }
        return nBTTagCompound;
    }

    public void readFromNBT(NBTTagCompound nBTTagCompound) {
        this.theItem = Item.getItemById(nBTTagCompound.getShort("id"));
        this.stackSize = nBTTagCompound.getByte("Count");
        this.metadata = nBTTagCompound.getShort("Damage");
        if (this.metadata < 0) {
            this.metadata = 0;
        }
        if (nBTTagCompound.hasKey("tag", 10)) {
            this.stackTagCompound = nBTTagCompound.getCompoundTag("tag");
        }
    }

    public int getMaxStackSize() {
        return this.getItem().getItemStackLimit();
    }

    public boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.isItemDamaged() || !this.isItemDamaged());
    }

    public boolean isItemStackDamageable() {
        return this.theItem != null && this.theItem.getMaxDurability() > 0 && (!this.hasTagCompound() || !this.getTagCompound().getBoolean("Unbreakable"));
    }

    public boolean getHasSubtypes() {
        return this.theItem.getHasSubtypes();
    }

    public boolean isItemDamaged() {
        return this.isItemStackDamageable() && this.metadata > 0;
    }

    public int getCurrentDurability() {
        return this.metadata;
    }

    public int getMetadata() {
        return this.metadata;
    }

    public void setMetadata(int n) {
        this.metadata = n;
        if (this.metadata < 0) {
            this.metadata = 0;
        }
    }

    public int getMaxDurability() {
        return this.theItem.getMaxDurability();
    }

    public boolean attemptDamageItem(int n, Random random) {
        if (!this.isItemStackDamageable()) {
            return false;
        }
        if (n > 0) {
            int n2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, this);
            int n3 = 0;
            for (int i = 0; n2 > 0 && i < n; ++i) {
                if (!EnchantmentDurability.negateDamage(this, n2, random)) continue;
                ++n3;
            }
            if ((n -= n3) <= 0) {
                return false;
            }
        }
        this.metadata += n;
        return this.metadata > this.getMaxDurability();
    }

    public void damageItem(int n, EntityLivingBase entityLivingBase) {
        if ((!(entityLivingBase instanceof EntityPlayer) || !((EntityPlayer)entityLivingBase).capabilities.isCreativeMode) && this.isItemStackDamageable() && this.attemptDamageItem(n, entityLivingBase.getRNG())) {
            entityLivingBase.renderBrokenItemStack(this);
            --this.stackSize;
            if (entityLivingBase instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer)entityLivingBase;
                entityPlayer.addStat(StatList.objectBreakStats[Item.getIdFromItem(this.theItem)], 1);
                if (this.stackSize == 0 && this.getItem() instanceof ItemBow) {
                    entityPlayer.destroyCurrentEquippedItem();
                }
            }
            if (this.stackSize < 0) {
                this.stackSize = 0;
            }
            this.metadata = 0;
        }
    }

    public void hitEntity(EntityLivingBase entityLivingBase, EntityPlayer entityPlayer) {
        boolean bl = this.theItem.hitEntity(this, entityLivingBase, entityPlayer);
        if (bl) {
            entityPlayer.addStat(StatList.objectUseStats[Item.getIdFromItem(this.theItem)], 1);
        }
    }

    public void onBlockDestroyed(World world, Block block, int n, int n2, int n3, EntityPlayer entityPlayer) {
        boolean bl = this.theItem.onBlockDestroyed(this, world, block, n, n2, n3, entityPlayer);
        if (bl) {
            entityPlayer.addStat(StatList.objectUseStats[Item.getIdFromItem(this.theItem)], 1);
        }
    }

    public boolean canItemHarvestBlock(Block block) {
        return this.theItem.canItemHarvestBlock(block);
    }

    public boolean interactWithEntity(EntityPlayer entityPlayer, EntityLivingBase entityLivingBase) {
        return this.theItem.itemInteractionForEntity(this, entityPlayer, entityLivingBase);
    }

    public ItemStack copy() {
        ItemStack itemStack = new ItemStack(this.theItem, this.stackSize, this.metadata);
        if (this.stackTagCompound != null) {
            itemStack.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
        }
        return itemStack;
    }

    public static boolean areItemStackTagsEqual(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack == null && itemStack2 == null || itemStack != null && itemStack2 != null && (itemStack.stackTagCompound != null || itemStack2.stackTagCompound == null) && (itemStack.stackTagCompound == null || itemStack.stackTagCompound.equals(itemStack2.stackTagCompound));
    }

    public static boolean areItemStacksEqual(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack == null && itemStack2 == null || itemStack != null && itemStack2 != null && itemStack.isItemStackEqual(itemStack2);
    }

    public boolean isItemStackEqual(ItemStack itemStack) {
        return !(this.stackSize != itemStack.stackSize || this.theItem != itemStack.theItem || this.metadata != itemStack.metadata || this.stackTagCompound == null && itemStack.stackTagCompound != null || this.stackTagCompound != null && !this.stackTagCompound.equals(itemStack.stackTagCompound));
    }

    public boolean isItemEqual(ItemStack itemStack) {
        return this.theItem == itemStack.theItem && this.metadata == itemStack.metadata;
    }

    public String getUnlocalizedName() {
        return this.theItem.getUnlocalizedName(this);
    }

    public static ItemStack copyItemStack(ItemStack itemStack) {
        return itemStack == null ? null : itemStack.copy();
    }

    public String toString() {
        return this.stackSize + "x" + this.theItem.getUnlocalizedName() + "@" + this.metadata;
    }

    public void updateAnimation(World world, Entity entity, int n, boolean bl) {
        if (this.animationsToGo > 0) {
            --this.animationsToGo;
        }
        this.theItem.onUpdate(this, world, entity, n, bl);
    }

    public void onCrafting(World world, EntityPlayer entityPlayer, int n) {
        entityPlayer.addStat(StatList.objectCraftStats[Item.getIdFromItem(this.theItem)], n);
        this.theItem.onCreated(this, world, entityPlayer);
    }

    public int getMaxItemUseDuration() {
        return this.getItem().getMaxItemUseDuration(this);
    }

    public EnumAction getItemUseAction() {
        return this.getItem().getItemUseAction(this);
    }

    public void onPlayerStoppedUsing(World world, EntityPlayer entityPlayer, int n) {
        this.getItem().onPlayerStoppedUsing(this, world, entityPlayer, n);
    }

    public boolean hasTagCompound() {
        return this.stackTagCompound != null;
    }

    public NBTTagCompound getTagCompound() {
        return this.stackTagCompound;
    }

    public NBTTagList getEnchantmentTagList() {
        return this.stackTagCompound == null ? null : this.stackTagCompound.getTagList("ench", 10);
    }

    public void setTagCompound(NBTTagCompound nBTTagCompound) {
        this.stackTagCompound = nBTTagCompound;
    }

    public String getDisplayName() {
        NBTTagCompound nBTTagCompound;
        String string = this.getItem().getItemStackDisplayName(this);
        if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display", 10) && (nBTTagCompound = this.stackTagCompound.getCompoundTag("display")).hasKey("Name", 8)) {
            string = nBTTagCompound.getString("Name");
        }
        return string;
    }

    public ItemStack setStackDisplayName(String string) {
        if (this.stackTagCompound == null) {
            this.stackTagCompound = new NBTTagCompound();
        }
        if (!this.stackTagCompound.hasKey("display", 10)) {
            this.stackTagCompound.setTag("display", new NBTTagCompound());
        }
        this.stackTagCompound.getCompoundTag("display").setString("Name", string);
        return this;
    }

    public void removeCustomName() {
        if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display", 10)) {
            NBTTagCompound nBTTagCompound = this.stackTagCompound.getCompoundTag("display");
            nBTTagCompound.removeTag("Name");
            if (nBTTagCompound.hasNoTags()) {
                this.stackTagCompound.removeTag("display");
                if (this.stackTagCompound.hasNoTags()) {
                    this.setTagCompound(null);
                }
            }
        }
    }

    public boolean hasDisplayName() {
        return this.stackTagCompound != null && this.stackTagCompound.hasKey("display", 10) && this.stackTagCompound.getCompoundTag("display").hasKey("Name", 8);
    }

    public List<String> getTooltip(EntityPlayer entityPlayer, boolean bl) {
        Object object;
        int n;
        String string = this.getDisplayName();
        ArrayList<String> arrayList = new ArrayList<String>();
        if (this.checkItemTooltipPart(ItemTooltipPart.DISPLAY_NAME)) {
            if (this.hasDisplayName()) {
                string = (Object)((Object)EnumChatFormatting.ITALIC) + string + (Object)((Object)EnumChatFormatting.RESET);
            }
            if (bl) {
                String string2 = "";
                if (string.length() > 0) {
                    string = string + " (";
                    string2 = ")";
                }
                n = Item.getIdFromItem(this.theItem);
                string = this.getHasSubtypes() ? string + String.format("#%04d/%d%s", n, this.metadata, string2) : string + String.format("#%04d%s", n, string2);
            } else if (!this.hasDisplayName() && this.theItem == Items.filled_map) {
                string = string + " #" + this.metadata;
            }
            arrayList.add(string);
        }
        if (this.checkItemTooltipPart(ItemTooltipPart.ITEM_STACK_INFORMATION)) {
            this.theItem.addInformation(this, entityPlayer, arrayList, bl);
        }
        if (this.hasTagCompound()) {
            NBTTagList nBTTagList = this.getEnchantmentTagList();
            if (this.checkItemTooltipPart(ItemTooltipPart.ENCHANTMENTS) && nBTTagList != null) {
                for (n = 0; n < nBTTagList.tagCount(); ++n) {
                    short s = nBTTagList.getCompoundTagAt(n).getShort("id");
                    short s2 = nBTTagList.getCompoundTagAt(n).getShort("lvl");
                    if (Enchantment.enchantmentsList[s] == null) continue;
                    arrayList.add(Enchantment.enchantmentsList[s].getTranslatedName(s2));
                }
            }
            if (this.stackTagCompound.hasKey("display", 10)) {
                NBTTagCompound nBTTagCompound = this.stackTagCompound.getCompoundTag("display");
                if (this.checkItemTooltipPart(ItemTooltipPart.COLOR) && nBTTagCompound.hasKey("color", 3)) {
                    if (bl) {
                        arrayList.add("Color: #" + Integer.toHexString(nBTTagCompound.getInteger("color")).toUpperCase());
                    } else {
                        arrayList.add((Object)((Object)EnumChatFormatting.ITALIC) + StatCollector.translateToLocal("item.dyed"));
                    }
                }
                object = nBTTagCompound.getTagList("Lore", 8);
                if (this.checkItemTooltipPart(ItemTooltipPart.LORE) && nBTTagCompound.getTagId("Lore") == 9 && ((NBTTagList)object).tagCount() > 0) {
                    for (int i = 0; i < ((NBTTagList)object).tagCount(); ++i) {
                        arrayList.add((Object)((Object)EnumChatFormatting.DARK_PURPLE) + "" + (Object)((Object)EnumChatFormatting.ITALIC) + ((NBTTagList)object).getStringTagAt(i));
                    }
                }
            }
        }
        Multimap<String, AttributeModifier> multimap = this.getAttributeModifiers();
        if (this.checkItemTooltipPart(ItemTooltipPart.ATTRIBUTE_MODIFIERS) && !multimap.isEmpty() && CheatBreaker.getInstance().getKeybindSettings().firstLaunch) {
            arrayList.add("");
            for (Map.Entry entry : multimap.entries()) {
                double d;
                object = (AttributeModifier)entry.getValue();
                double d2 = ((AttributeModifier)object).getAmount();
                if (((AttributeModifier)object).getID() == Item.itemModifierUUID) {
                    d2 += (double)EnchantmentHelper.getAttackDamage(this, EnumCreatureAttribute.UNDEFINED);
                }
                double d3 = d = ((AttributeModifier)object).getOperation() != 1 && ((AttributeModifier)object).getOperation() != 2 ? d2 : d2 * 100.0;
                if (d2 > 0.0) {
                    arrayList.add((Object)((Object)EnumChatFormatting.BLUE) + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + ((AttributeModifier)object).getOperation(), MODIFIER_FORMAT.format(d), StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())));
                    continue;
                }
                if (!(d2 < 0.0)) continue;
                arrayList.add((Object)((Object)EnumChatFormatting.RED) + StatCollector.translateToLocalFormatted("attribute.modifier.take." + ((AttributeModifier)object).getOperation(), MODIFIER_FORMAT.format(d *= -1.0), StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())));
            }
        }
        if (this.checkItemTooltipPart(ItemTooltipPart.UNBREAKABLE) && this.hasTagCompound() && this.getTagCompound().getBoolean("Unbreakable")) {
            arrayList.add((Object)((Object)EnumChatFormatting.BLUE) + StatCollector.translateToLocal("item.unbreakable"));
        }
        if (this.checkItemTooltipPart(ItemTooltipPart.DURABILITY) && bl && this.isItemDamaged()) {
            arrayList.add("Durability: " + (this.getMaxDurability() - this.getCurrentDurability()) + " / " + this.getMaxDurability());
        }
        return arrayList;
    }

    public boolean hasEffect() {
        return this.getItem().hasEffect(this);
    }

    public EnumRarity getRarity() {
        return this.getItem().getRarity(this);
    }

    public boolean isItemEnchantable() {
        return this.getItem().isItemTool(this) && !this.isItemEnchanted();
    }

    public void addEnchantment(Enchantment enchantment, int n) {
        if (this.stackTagCompound == null) {
            this.setTagCompound(new NBTTagCompound());
        }
        if (!this.stackTagCompound.hasKey("ench", 9)) {
            this.stackTagCompound.setTag("ench", new NBTTagList());
        }
        NBTTagList nBTTagList = this.stackTagCompound.getTagList("ench", 10);
        NBTTagCompound nBTTagCompound = new NBTTagCompound();
        nBTTagCompound.setShort("id", (short)enchantment.effectId);
        nBTTagCompound.setShort("lvl", (byte)n);
        nBTTagList.appendTag(nBTTagCompound);
    }

    public boolean isItemEnchanted() {
        return this.stackTagCompound != null && this.stackTagCompound.hasKey("ench", 9);
    }

    public void setTagInfo(String string, NBTBase nBTBase) {
        if (this.stackTagCompound == null) {
            this.setTagCompound(new NBTTagCompound());
        }
        this.stackTagCompound.setTag(string, nBTBase);
    }

    public boolean canEditBlocks() {
        return this.getItem().canItemEditBlocks();
    }

    public boolean isOnItemFrame() {
        return this.itemFrame != null;
    }

    public void setItemFrame(EntityItemFrame entityItemFrame) {
        this.itemFrame = entityItemFrame;
    }

    public EntityItemFrame getItemFrame() {
        return this.itemFrame;
    }

    public int getRepairCost() {
        return this.hasTagCompound() && this.stackTagCompound.hasKey("RepairCost", 3) ? this.stackTagCompound.getInteger("RepairCost") : 0;
    }

    public void setRepairCost(int n) {
        if (!this.hasTagCompound()) {
            this.stackTagCompound = new NBTTagCompound();
        }
        this.stackTagCompound.setInteger("RepairCost", n);
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers() {
        Multimap<String, AttributeModifier> hashMultimap;
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("AttributeModifiers", 9)) {
            hashMultimap = HashMultimap.create();
            NBTTagList nBTTagList = this.stackTagCompound.getTagList("AttributeModifiers", 10);
            for (int i = 0; i < nBTTagList.tagCount(); ++i) {
                NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(i);
                AttributeModifier attributeModifier = SharedMonsterAttributes.readAttributeModifierFromNBT(nBTTagCompound);
                if (attributeModifier.getID().getLeastSignificantBits() == 0L || attributeModifier.getID().getMostSignificantBits() == 0L) continue;
                hashMultimap.put(nBTTagCompound.getString("AttributeName"), attributeModifier);
            }
        } else {
            hashMultimap = this.getItem().getItemAttributeModifiers();
        }
        return hashMultimap;
    }

    public void setItem(Item item) {
        this.theItem = item;
    }

    public IChatComponent toHoverableText() {
        IChatComponent iChatComponent = new ChatComponentText("[").appendText(this.getDisplayName()).appendText("]");
        if (this.theItem != null) {
            NBTTagCompound nBTTagCompound = new NBTTagCompound();
            this.writeToNBT(nBTTagCompound);
            iChatComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ChatComponentText(nBTTagCompound.toString())));
            iChatComponent.getChatStyle().setColor(this.getRarity().rarityColor);
        }
        return iChatComponent;
    }

    public boolean checkItemTooltipPart(ItemTooltipPart itemTooltipPart) {
        if (!this.hasTagCompound()) {
            return true;
        }
        if (!this.getTagCompound().hasKey("hidden")) {
            return true;
        }
        NBTTagCompound nBTTagCompound = this.getTagCompound().getCompoundTag("hidden");
        return !nBTTagCompound.hasKey(itemTooltipPart.name()) || nBTTagCompound.getByte(itemTooltipPart.name()) != 1;
    }
}
