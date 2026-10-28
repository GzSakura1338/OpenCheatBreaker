package net.minecraft.stats;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class Achievement extends StatBase {
    public ItemStack theItemStack;
    public String achievementDescription;
    public int displayColumn;
    public int displayRow;
    public IStatStringFormat statStringFormatter;
    public Achievement parentAchievement;
    public boolean isSpecial;

    public Achievement(String str, String str2, int n, int n2, Item item, Achievement achievement) {
        this(str, str2, n, n2, new ItemStack(item), achievement);
    }

    public Achievement(String str, String str2, int n, int n2, Block block, Achievement achievement) {
        this(str, str2, n, n2, new ItemStack(block), achievement);
    }

    public Achievement(String str, String str2, int n, int n2, ItemStack itemStack, Achievement achievement) {
        super(str, new ChatComponentTranslation("achievement." + str2));
        this.theItemStack = itemStack;
        this.achievementDescription = "achievement." + str2 + ".desc";
        this.displayColumn = n;
        this.displayRow = n2;
        if (n < AchievementList.minDisplayColumn) {
            AchievementList.minDisplayColumn = n;
        }

        if (n2 < AchievementList.minDisplayRow) {
            AchievementList.minDisplayRow = n2;
        }

        if (n > AchievementList.maxDisplayColumn) {
            AchievementList.maxDisplayColumn = n;
        }

        if (n2 > AchievementList.maxDisplayRow) {
            AchievementList.maxDisplayRow = n2;
        }

        this.parentAchievement = achievement;
    }

    public Achievement initIndependentStat() {
        this.isIndependent = true;
        return this;
    }

    public Achievement setSpecial() {
        this.isSpecial = true;
        return this;
    }

    public Achievement registerStat() {
        super.registerStat();
        AchievementList.achievementList.add(this);
        return this;
    }

    @Override
    public boolean isAchievement() {
        return true;
    }

    @Override
    public IChatComponent getStatName() {
        IChatComponent statName = super.getStatName();
        statName.getChatStyle().setColor(this.getSpecial() ? EnumChatFormatting.DARK_PURPLE : EnumChatFormatting.GREEN);
        return statName;
    }

    public Achievement setJsonElementProvider(Class clazz) {
        return (Achievement)super.setJsonElementProvider(clazz);
    }

    public String getDescription() {
        return this.statStringFormatter != null
            ? this.statStringFormatter.formatString(StatCollector.translateToLocal(this.achievementDescription))
            : StatCollector.translateToLocal(this.achievementDescription);
    }

    public Achievement setStatStringFormatter(IStatStringFormat statStringFormat) {
        this.statStringFormatter = statStringFormat;
        return this;
    }

    public boolean getSpecial() {
        return this.isSpecial;
    }
}
