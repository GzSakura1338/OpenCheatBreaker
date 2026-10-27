package net.minecraft.creativetab;

import java.util.List;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class CreativeTabs {
   public static CreativeTabs[] creativeTabArray = new CreativeTabs[12];
   public static CreativeTabs tabBlock = new CreativeTabs(0, "buildingBlocks") {
      @Override
      public Item getTabIconItem() {
         return Item.getItemFromBlock(Blocks.brick_block);
      }
   };
   public static CreativeTabs tabDecorations = new CreativeTabs(1, "decorations") {
      @Override
      public Item getTabIconItem() {
         return Item.getItemFromBlock(Blocks.double_plant);
      }

      @Override
      public int getIconItemDamage() {
         return EnumPlantType.PAEONIA.getMeta();
      }
   };
   public static CreativeTabs tabRedstone = new CreativeTabs(2, "redstone") {
      @Override
      public Item getTabIconItem() {
         return Items.redstone;
      }
   };
   public static CreativeTabs tabTransport = new CreativeTabs(3, "transportation") {
      @Override
      public Item getTabIconItem() {
         return Item.getItemFromBlock(Blocks.golden_rail);
      }
   };
   public static CreativeTabs tabMisc = (new CreativeTabs(4, "misc") {
      @Override
      public Item getTabIconItem() {
         return Items.lava_bucket;
      }
   }).setRelevantEnchantmentTypes(EnumEnchantmentType.ALL);
   public static CreativeTabs tabAllSearch = (new CreativeTabs(5, "search") {
      @Override
      public Item getTabIconItem() {
         return Items.compass;
      }
   }).setBackgroundImageName("item_search.png");
   public static CreativeTabs tabFood = new CreativeTabs(6, "food") {
      @Override
      public Item getTabIconItem() {
         return Items.apple;
      }
   };
   public static CreativeTabs tabTools = (new CreativeTabs(7, "tools") {
      @Override
      public Item getTabIconItem() {
         return Items.iron_axe;
      }
   }).setRelevantEnchantmentTypes(EnumEnchantmentType.DIGGER, EnumEnchantmentType.FISHING_ROD, EnumEnchantmentType.BREAKABLE);
   public static CreativeTabs tabCombat = (new CreativeTabs(8, "combat") {
         @Override
         public Item getTabIconItem() {
            return Items.golden_sword;
         }
      })
      .setRelevantEnchantmentTypes(
         EnumEnchantmentType.ARMOR,
         EnumEnchantmentType.ARMOR_FEET,
         EnumEnchantmentType.ARMOR_HEAD,
         EnumEnchantmentType.ARMOR_LEGS,
         EnumEnchantmentType.ARMOR_TORSO,
         EnumEnchantmentType.BOW,
         EnumEnchantmentType.WEAPON
      );
   public static CreativeTabs tabBrewing = new CreativeTabs(9, "brewing") {
      @Override
      public Item getTabIconItem() {
         return Items.potionitem;
      }
   };
   public static CreativeTabs tabMaterials = new CreativeTabs(10, "materials") {
      @Override
      public Item getTabIconItem() {
         return Items.stick;
      }
   };
   public static CreativeTabs tabInventory = (new CreativeTabs(11, "inventory") {
      @Override
      public Item getTabIconItem() {
         return Item.getItemFromBlock(Blocks.chest);
      }
   }).setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();
   public String theTexture = "items.png";
   public boolean hasScrollbar = true;
   public boolean drawTitle = true;
   public int tabIndex;
   public String tabLabel;
   public EnumEnchantmentType[] enchantmentTypes;
   public ItemStack iconItemStack;

   public CreativeTabs(int index, String label) {
      this.tabIndex = index;
      this.tabLabel = label;
      creativeTabArray[index] = this;
   }

   public int getTabIndex() {
      return this.tabIndex;
   }

   public String getTabLabel() {
      return this.tabLabel;
   }

   public String getTranslatedTabLabel() {
      return "itemGroup." + this.getTabLabel();
   }

   public ItemStack getIconItemStack() {
      if (this.iconItemStack == null) {
         this.iconItemStack = new ItemStack(this.getTabIconItem(), 1, this.getIconItemDamage());
      }

      return this.iconItemStack;
   }

   public abstract Item getTabIconItem();

   public int getIconItemDamage() {
      return 0;
   }

   public String getBackgroundImageName() {
      return this.theTexture;
   }

   public CreativeTabs setBackgroundImageName(String texture) {
      this.theTexture = texture;
      return this;
   }

   public boolean drawInForegroundOfTab() {
      return this.drawTitle;
   }

   public CreativeTabs setNoTitle() {
      this.drawTitle = false;
      return this;
   }

   public boolean shouldHidePlayerInventory() {
      return this.hasScrollbar;
   }

   public CreativeTabs setNoScrollbar() {
      this.hasScrollbar = false;
      return this;
   }

   public int getTabColumn() {
      return this.tabIndex % 6;
   }

   public boolean isTabInFirstRow() {
      return this.tabIndex < 6;
   }

   public EnumEnchantmentType[] getRelevantEnchantmentTypes() {
      return this.enchantmentTypes;
   }

   public CreativeTabs setRelevantEnchantmentTypes(EnumEnchantmentType... types) {
      this.enchantmentTypes = types;
      return this;
   }

   public boolean hasRelevantEnchantmentType(EnumEnchantmentType enchantmentType) {
      if (this.enchantmentTypes == null) {
         return false;
      }

      for (EnumEnchantmentType enumEnchantmentType : this.enchantmentTypes) {
         if (enumEnchantmentType == enchantmentType) {
            return true;
         }
      }

      return false;
   }

   public void displayAllReleventItems(List<ItemStack> list) {
      for (Item item : Item.itemRegistry) {
         if (item != null && item.getCreativeTab() == this) {
            item.getSubItems(item, this, list);
         }
      }

      if (this.getRelevantEnchantmentTypes() != null) {
         this.addEnchantmentBooksToList(list, this.getRelevantEnchantmentTypes());
      }
   }

   public void addEnchantmentBooksToList(List<ItemStack> itemList, EnumEnchantmentType... enchantmentType) {
      for (Enchantment enchantment : Enchantment.enchantmentsBookList) {
         if (enchantment != null && enchantment.type != null) {
            boolean flag = false;

            for (int i = 0; i < enchantmentType.length && !flag; i++) {
               if (enchantment.type == enchantmentType[i]) {
                  flag = true;
               }
            }

            if (flag) {
               itemList.add(Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment, enchantment.getMaxLevel())));
            }
         }
      }
   }
}
