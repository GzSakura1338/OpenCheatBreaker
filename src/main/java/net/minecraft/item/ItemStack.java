package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemStack {
    public static DecimalFormat DECIMALFORMAT = new DecimalFormat("#.###");
    public Block canDestroyCacheBlock = null;
    public int animationsToGo;
    public boolean canDestroyCacheResult = false;
    public NBTTagCompound stackTagCompound;
    public Block canPlaceOnCacheBlock = null;
    public EntityItemFrame itemFrame;
    public boolean canPlaceOnCacheResult = false;
    public Item item;
    public int stackSize;
    public int itemDamage;

    public ItemStack(Block blockIn) {
        this(blockIn, 1);
    }

    public ItemStack(Block blockIn, int amount) {
        this(blockIn, amount, 0);
    }

    public ItemStack(Block blockIn, int amount, int meta) {
        this(Item.getItemFromBlock(blockIn), amount, meta);
    }

    public ItemStack(Item itemIn) {
        this(itemIn, 1);
    }

    public ItemStack(Item itemIn, int amount) {
        this(itemIn, amount, 0);
    }

    public ItemStack(Item itemIn, int amount, int meta) {
        this.item = itemIn;
        this.stackSize = amount;
        this.itemDamage = meta;
        if (this.itemDamage < 0) {
            this.itemDamage = 0;
        }
    }

    public static ItemStack loadItemStackFromNBT(NBTTagCompound nbt) {
        ItemStack itemStack = new ItemStack();
        itemStack.readFromNBT(nbt);
        return itemStack.getItem() != null ? itemStack : null;
    }

    public ItemStack() {
    }

    public ItemStack splitStack(int amount) {
        ItemStack itemStack = new ItemStack(this.item, amount, this.itemDamage);
        if (this.stackTagCompound != null) {
            itemStack.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
        }
        this.stackSize -= amount;
        return itemStack;
    }

    public Item getItem() {
        return this.item;
    }

    public boolean onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        boolean bl = this.getItem().onItemUse(this, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
        if (bl) {
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
        }
        return bl;
    }

    public float getStrVsBlock(Block blockIn) {
        return this.getItem().getStrVsBlock(this, blockIn);
    }

    public ItemStack useItemRightClick(World worldIn, EntityPlayer playerIn) {
        return this.getItem().onItemRightClick(this, worldIn, playerIn);
    }

    public ItemStack onItemUseFinish(World worldIn, EntityPlayer playerIn) {
        return this.getItem().onItemUseFinish(this, worldIn, playerIn);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        ResourceLocation resourceLocation = Item.itemRegistry.getNameForObject(this.item);
        nbt.setString("id", resourceLocation == null ? "minecraft:air" : resourceLocation.toString());
        nbt.setByte("Count", (byte)this.stackSize);
        nbt.setShort("Damage", (short)this.itemDamage);
        if (this.stackTagCompound != null) {
            nbt.setTag("tag", this.stackTagCompound);
        }
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.item = nbt.hasKey("id", 8) ? Item.getByNameOrId(nbt.getString("id")) : Item.getItemById(nbt.getShort("id"));
        this.stackSize = nbt.getByte("Count");
        this.itemDamage = nbt.getShort("Damage");
        if (this.itemDamage < 0) {
            this.itemDamage = 0;
        }
        if (nbt.hasKey("tag", 10)) {
            this.stackTagCompound = nbt.getCompoundTag("tag");
            if (this.item != null) {
                this.item.updateItemStackNBT(this.stackTagCompound);
            }
        }
    }

    public int getMaxStackSize() {
        return this.getItem().getItemStackLimit();
    }

    public boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.isItemStackDamageable() || !this.isItemDamaged());
    }

    public boolean isItemStackDamageable() {
        return this.item == null ? false : (this.item.getMaxDamage() <= 0 ? false : !this.hasTagCompound() || !this.getTagCompound().getBoolean("Unbreakable"));
    }

    public boolean getHasSubtypes() {
        return this.item.getHasSubtypes();
    }

    public boolean isItemDamaged() {
        return this.isItemStackDamageable() && this.itemDamage > 0;
    }

    public int getItemDamage() {
        return this.itemDamage;
    }

    public int getMetadata() {
        return this.itemDamage;
    }

    public void setItemDamage(int meta) {
        this.itemDamage = meta;
        if (this.itemDamage < 0) {
            this.itemDamage = 0;
        }
    }

    public int getMaxDamage() {
        return this.item.getMaxDamage();
    }

    public boolean attemptDamageItem(int amount, Random rand) {
        if (!this.isItemStackDamageable()) {
            return false;
        }
        if (amount > 0) {
            int n = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, this);
            int n2 = 0;
            for (int i = 0; n > 0 && i < amount; ++i) {
                if (!EnchantmentDurability.negateDamage(this, n, rand)) continue;
                ++n2;
            }
            if ((amount -= n2) <= 0) {
                return false;
            }
        }
        this.itemDamage += amount;
        return this.itemDamage > this.getMaxDamage();
    }

    public void damageItem(int amount, EntityLivingBase entityIn) {
        if ((!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).capabilities.isCreativeMode) && this.isItemStackDamageable() && this.attemptDamageItem(amount, entityIn.getRNG())) {
            entityIn.renderBrokenItemStack(this);
            --this.stackSize;
            if (entityIn instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer)entityIn;
                entityPlayer.triggerAchievement(StatList.objectBreakStats[Item.getIdFromItem(this.item)]);
                if (this.stackSize == 0 && this.getItem() instanceof ItemBow) {
                    entityPlayer.destroyCurrentEquippedItem();
                }
            }
            if (this.stackSize < 0) {
                this.stackSize = 0;
            }
            this.itemDamage = 0;
        }
    }

    public void hitEntity(EntityLivingBase entityIn, EntityPlayer playerIn) {
        boolean bl = this.item.hitEntity(this, entityIn, playerIn);
        if (bl) {
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
        }
    }

    public void onBlockDestroyed(World worldIn, Block blockIn, BlockPos pos, EntityPlayer playerIn) {
        boolean bl = this.item.onBlockDestroyed(this, worldIn, blockIn, pos, playerIn);
        if (bl) {
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
        }
    }

    public boolean canHarvestBlock(Block blockIn) {
        return this.item.canHarvestBlock(blockIn);
    }

    public boolean interactWithEntity(EntityPlayer playerIn, EntityLivingBase entityIn) {
        return this.item.itemInteractionForEntity(this, playerIn, entityIn);
    }

    public ItemStack copy() {
        ItemStack itemStack = new ItemStack(this.item, this.stackSize, this.itemDamage);
        if (this.stackTagCompound != null) {
            itemStack.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
        }
        return itemStack;
    }

    public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB) {
        return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? (stackA.stackTagCompound == null && stackB.stackTagCompound != null ? false : stackA.stackTagCompound == null || stackA.stackTagCompound.equals(stackB.stackTagCompound)) : false);
    }

    public static boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB) {
        return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isItemStackEqual(stackB) : false);
    }

    public boolean isItemStackEqual(ItemStack other) {
        return this.stackSize != other.stackSize ? false : (this.item != other.item ? false : (this.itemDamage != other.itemDamage ? false : (this.stackTagCompound == null && other.stackTagCompound != null ? false : this.stackTagCompound == null || this.stackTagCompound.equals(other.stackTagCompound))));
    }

    public static boolean areItemsEqual(ItemStack stackA, ItemStack stackB) {
        return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isItemEqual(stackB) : false);
    }

    public boolean isItemEqual(ItemStack other) {
        return other != null && this.item == other.item && this.itemDamage == other.itemDamage;
    }

    public String getUnlocalizedName() {
        return this.item.getUnlocalizedName(this);
    }

    public static ItemStack copyItemStack(ItemStack stack) {
        return stack == null ? null : stack.copy();
    }

    public String toString() {
        return this.stackSize + "x" + this.item.getUnlocalizedName() + "@" + this.itemDamage;
    }

    public void updateAnimation(World worldIn, Entity entityIn, int inventorySlot, boolean isCurrentItem) {
        if (this.animationsToGo > 0) {
            --this.animationsToGo;
        }
        this.item.onUpdate(this, worldIn, entityIn, inventorySlot, isCurrentItem);
    }

    public void onCrafting(World worldIn, EntityPlayer playerIn, int amount) {
        playerIn.addStat(StatList.objectCraftStats[Item.getIdFromItem(this.item)], amount);
        this.item.onCreated(this, worldIn, playerIn);
    }

    public boolean getIsItemStackEqual(ItemStack itemStack) {
        return this.isItemStackEqual(itemStack);
    }

    public int getMaxItemUseDuration() {
        return this.getItem().getMaxItemUseDuration(this);
    }

    public EnumAction getItemUseAction() {
        return this.getItem().getItemUseAction(this);
    }

    public void onPlayerStoppedUsing(World worldIn, EntityPlayer playerIn, int timeLeft) {
        this.getItem().onPlayerStoppedUsing(this, worldIn, playerIn, timeLeft);
    }

    public boolean hasTagCompound() {
        return this.stackTagCompound != null;
    }

    public NBTTagCompound getTagCompound() {
        return this.stackTagCompound;
    }

    public NBTTagCompound getSubCompound(String key, boolean create) {
        if (this.stackTagCompound != null && this.stackTagCompound.hasKey(key, 10)) {
            return this.stackTagCompound.getCompoundTag(key);
        }
        if (create) {
            NBTTagCompound nBTTagCompound = new NBTTagCompound();
            this.setTagInfo(key, nBTTagCompound);
            return nBTTagCompound;
        }
        return null;
    }

    public NBTTagList getEnchantmentTagList() {
        return this.stackTagCompound == null ? null : this.stackTagCompound.getTagList("ench", 10);
    }

    public void setTagCompound(NBTTagCompound nbt) {
        this.stackTagCompound = nbt;
    }

    public String getDisplayName() {
        NBTTagCompound nBTTagCompound;
        String string = this.getItem().getItemStackDisplayName(this);
        if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display", 10) && (nBTTagCompound = this.stackTagCompound.getCompoundTag("display")).hasKey("Name", 8)) {
            string = nBTTagCompound.getString("Name");
        }
        return string;
    }

    public ItemStack setStackDisplayName(String displayName) {
        if (this.stackTagCompound == null) {
            this.stackTagCompound = new NBTTagCompound();
        }
        if (!this.stackTagCompound.hasKey("display", 10)) {
            this.stackTagCompound.setTag("display", new NBTTagCompound());
        }
        this.stackTagCompound.getCompoundTag("display").setString("Name", displayName);
        return this;
    }

    public void clearCustomName() {
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
        return this.stackTagCompound == null ? false : (!this.stackTagCompound.hasKey("display", 10) ? false : this.stackTagCompound.getCompoundTag("display").hasKey("Name", 8));
    }

    public List<String> getTooltip(EntityPlayer playerIn, boolean advanced) {
        NBTTagList nBTTagList;
        NBTTagList nBTTagList2;
        Multimap<String, AttributeModifier> multimap;
        ArrayList<String> arrayList = Lists.newArrayList();
        String string = this.getDisplayName();
        if (this.hasDisplayName()) {
            string = (Object)((Object)EnumChatFormatting.ITALIC) + string;
        }
        string = string + (Object)((Object)EnumChatFormatting.RESET);
        if (advanced) {
            String string2 = "";
            if (string.length() > 0) {
                string = string + " (";
                string2 = ")";
            }
            int n = Item.getIdFromItem(this.item);
            string = this.getHasSubtypes() ? string + String.format("#%04d/%d%s", n, this.itemDamage, string2) : string + String.format("#%04d%s", n, string2);
        } else if (!this.hasDisplayName() && this.item == Items.filled_map) {
            string = string + " #" + this.itemDamage;
        }
        arrayList.add(string);
        int n = 0;
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("HideFlags", 99)) {
            n = this.stackTagCompound.getInteger("HideFlags");
        }
        if ((n & 0x20) == 0) {
            this.item.addInformation(this, playerIn, arrayList, advanced);
        }
        if (this.hasTagCompound()) {
            int n2;
            NBTTagList nBTTagList3;
            if ((n & 1) == 0 && (nBTTagList3 = this.getEnchantmentTagList()) != null) {
                for (int i = 0; i < nBTTagList3.tagCount(); ++i) {
                    n2 = nBTTagList3.getCompoundTagAt(i).getShort("id");
                    short s = nBTTagList3.getCompoundTagAt(i).getShort("lvl");
                    if (Enchantment.getEnchantmentById(n2) == null) continue;
                    arrayList.add(Enchantment.getEnchantmentById(n2).getTranslatedName(s));
                }
            }
            if (this.stackTagCompound.hasKey("display", 10)) {
                NBTTagList nBTTagList4;
                NBTTagCompound nBTTagCompound = this.stackTagCompound.getCompoundTag("display");
                if (nBTTagCompound.hasKey("color", 3)) {
                    if (advanced) {
                        arrayList.add("Color: #" + Integer.toHexString(nBTTagCompound.getInteger("color")).toUpperCase());
                    } else {
                        arrayList.add((Object)((Object)EnumChatFormatting.ITALIC) + StatCollector.translateToLocal("item.dyed"));
                    }
                }
                if (nBTTagCompound.getTagId("Lore") == 9 && (nBTTagList4 = nBTTagCompound.getTagList("Lore", 8)).tagCount() > 0) {
                    for (n2 = 0; n2 < nBTTagList4.tagCount(); ++n2) {
                        arrayList.add((Object)((Object)EnumChatFormatting.DARK_PURPLE) + "" + (Object)((Object)EnumChatFormatting.ITALIC) + nBTTagList4.getStringTagAt(n2));
                    }
                }
            }
        }
        if (!(multimap = this.getAttributeModifiers()).isEmpty() && (n & 2) == 0) {
            arrayList.add("");
            for (Map.Entry<String, AttributeModifier> entry : multimap.entries()) {
                AttributeModifier attributeModifier = entry.getValue();
                double d = attributeModifier.getAmount();
                if (attributeModifier.getID() == Item.itemModifierUUID) {
                    d += (double)EnchantmentHelper.getModifierForCreature(this, EnumCreatureAttribute.UNDEFINED);
                }
                double d2 = attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2 ? d : d * 100.0;
                if (d > 0.0) {
                    arrayList.add((Object)((Object)EnumChatFormatting.BLUE) + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributeModifier.getOperation(), DECIMALFORMAT.format(d2), StatCollector.translateToLocal("attribute.name." + entry.getKey())));
                    continue;
                }
                if (!(d < 0.0)) continue;
                arrayList.add((Object)((Object)EnumChatFormatting.RED) + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributeModifier.getOperation(), DECIMALFORMAT.format(d2 *= -1.0), StatCollector.translateToLocal("attribute.name." + entry.getKey())));
            }
        }
        if (this.hasTagCompound() && this.getTagCompound().getBoolean("Unbreakable") && (n & 4) == 0) {
            arrayList.add((Object)((Object)EnumChatFormatting.BLUE) + StatCollector.translateToLocal("item.unbreakable"));
        }
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanDestroy", 9) && (n & 8) == 0 && (nBTTagList2 = this.stackTagCompound.getTagList("CanDestroy", 8)).tagCount() > 0) {
            arrayList.add("");
            arrayList.add((Object)((Object)EnumChatFormatting.GRAY) + StatCollector.translateToLocal("item.canBreak"));
            for (int i = 0; i < nBTTagList2.tagCount(); ++i) {
                Block block = Block.getBlockFromName(nBTTagList2.getStringTagAt(i));
                if (block != null) {
                    arrayList.add((Object)((Object)EnumChatFormatting.DARK_GRAY) + block.getLocalizedName());
                    continue;
                }
                arrayList.add((Object)((Object)EnumChatFormatting.DARK_GRAY) + "missingno");
            }
        }
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanPlaceOn", 9) && (n & 0x10) == 0 && (nBTTagList = this.stackTagCompound.getTagList("CanPlaceOn", 8)).tagCount() > 0) {
            arrayList.add("");
            arrayList.add((Object)((Object)EnumChatFormatting.GRAY) + StatCollector.translateToLocal("item.canPlace"));
            for (int i = 0; i < nBTTagList.tagCount(); ++i) {
                Block block = Block.getBlockFromName(nBTTagList.getStringTagAt(i));
                if (block != null) {
                    arrayList.add((Object)((Object)EnumChatFormatting.DARK_GRAY) + block.getLocalizedName());
                    continue;
                }
                arrayList.add((Object)((Object)EnumChatFormatting.DARK_GRAY) + "missingno");
            }
        }
        if (advanced) {
            if (this.isItemDamaged()) {
                arrayList.add("Durability: " + (this.getMaxDamage() - this.getItemDamage()) + " / " + this.getMaxDamage());
            }
            arrayList.add((Object)((Object)EnumChatFormatting.DARK_GRAY) + Item.itemRegistry.getNameForObject(this.item).toString());
            if (this.hasTagCompound()) {
                arrayList.add((Object)((Object)EnumChatFormatting.DARK_GRAY) + "NBT: " + this.getTagCompound().getKeySet().size() + " tag(s)");
            }
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
        return !this.getItem().isItemTool(this) ? false : !this.isItemEnchanted();
    }

    public void addEnchantment(Enchantment ench, int level) {
        if (this.stackTagCompound == null) {
            this.setTagCompound(new NBTTagCompound());
        }
        if (!this.stackTagCompound.hasKey("ench", 9)) {
            this.stackTagCompound.setTag("ench", new NBTTagList());
        }
        NBTTagList nBTTagList = this.stackTagCompound.getTagList("ench", 10);
        NBTTagCompound nBTTagCompound = new NBTTagCompound();
        nBTTagCompound.setShort("id", (short)ench.effectId);
        nBTTagCompound.setShort("lvl", (byte)level);
        nBTTagList.appendTag(nBTTagCompound);
    }

    public boolean isItemEnchanted() {
        return this.stackTagCompound != null && this.stackTagCompound.hasKey("ench", 9);
    }

    public void setTagInfo(String key, NBTBase value) {
        if (this.stackTagCompound == null) {
            this.setTagCompound(new NBTTagCompound());
        }
        this.stackTagCompound.setTag(key, value);
    }

    public boolean canEditBlocks() {
        return this.getItem().canItemEditBlocks();
    }

    public boolean isOnItemFrame() {
        return this.itemFrame != null;
    }

    public void setItemFrame(EntityItemFrame frame) {
        this.itemFrame = frame;
    }

    public EntityItemFrame getItemFrame() {
        return this.itemFrame;
    }

    public int getRepairCost() {
        return this.hasTagCompound() && this.stackTagCompound.hasKey("RepairCost", 3) ? this.stackTagCompound.getInteger("RepairCost") : 0;
    }

    public void setRepairCost(int cost) {
        if (!this.hasTagCompound()) {
            this.stackTagCompound = new NBTTagCompound();
        }
        this.stackTagCompound.setInteger("RepairCost", cost);
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers() {
        Multimap<String, AttributeModifier> multimap;
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("AttributeModifiers", 9)) {
            multimap = HashMultimap.create();
            NBTTagList nBTTagList = this.stackTagCompound.getTagList("AttributeModifiers", 10);
            for (int i = 0; i < nBTTagList.tagCount(); ++i) {
                NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(i);
                AttributeModifier attributeModifier = SharedMonsterAttributes.readAttributeModifierFromNBT(nBTTagCompound);
                if (attributeModifier == null || attributeModifier.getID().getLeastSignificantBits() == 0L || attributeModifier.getID().getMostSignificantBits() == 0L) continue;
                multimap.put(nBTTagCompound.getString("AttributeName"), attributeModifier);
            }
        } else {
            multimap = this.getItem().getItemAttributeModifiers();
        }
        return multimap;
    }

    public void setItem(Item newItem) {
        this.item = newItem;
    }

    public IChatComponent getChatComponent() {
        ChatComponentText chatComponentText = new ChatComponentText(this.getDisplayName());
        if (this.hasDisplayName()) {
            chatComponentText.getChatStyle().setItalic(true);
        }
        IChatComponent iChatComponent = new ChatComponentText("[").appendSibling(chatComponentText).appendText("]");
        if (this.item != null) {
            NBTTagCompound nBTTagCompound = new NBTTagCompound();
            this.writeToNBT(nBTTagCompound);
            iChatComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ChatComponentText(nBTTagCompound.toString())));
            iChatComponent.getChatStyle().setColor(this.getRarity().rarityColor);
        }
        return iChatComponent;
    }

    public boolean canDestroy(Block blockIn) {
        if (blockIn == this.canDestroyCacheBlock) {
            return this.canDestroyCacheResult;
        }
        this.canDestroyCacheBlock = blockIn;
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanDestroy", 9)) {
            NBTTagList nBTTagList = this.stackTagCompound.getTagList("CanDestroy", 8);
            for (int i = 0; i < nBTTagList.tagCount(); ++i) {
                Block block = Block.getBlockFromName(nBTTagList.getStringTagAt(i));
                if (block != blockIn) continue;
                this.canDestroyCacheResult = true;
                return true;
            }
        }
        this.canDestroyCacheResult = false;
        return false;
    }

    public boolean canPlaceOn(Block blockIn) {
        if (blockIn == this.canPlaceOnCacheBlock) {
            return this.canPlaceOnCacheResult;
        }
        this.canPlaceOnCacheBlock = blockIn;
        if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanPlaceOn", 9)) {
            NBTTagList nBTTagList = this.stackTagCompound.getTagList("CanPlaceOn", 8);
            for (int i = 0; i < nBTTagList.tagCount(); ++i) {
                Block block = Block.getBlockFromName(nBTTagList.getStringTagAt(i));
                if (block != blockIn) continue;
                this.canPlaceOnCacheResult = true;
                return true;
            }
        }
        this.canPlaceOnCacheResult = false;
        return false;
    }
}
