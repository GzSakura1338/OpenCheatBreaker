package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class StatBase {
    public String statId;
    public IChatComponent statName;
    public boolean isIndependent;
    public IStatType type;
    public IScoreObjectiveCriteria criterion;
    public Class jsonElementProvider;
    public static NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.US);
    public static IStatType simpleStatType = new IStatType() {
        @Override
        public String format(int n) {
            return StatBase.numberFormat.format(n);
        }
    };
    public static DecimalFormat decimalFormat = new DecimalFormat("########0.00");
    public static IStatType timeStatType = new IStatType() {
        @Override
        public String format(int n) {
            double d = n / 20.0;
            double d2 = d / 60.0;
            double d3 = d2 / 60.0;
            double d4 = d3 / 24.0;
            double d5 = d4 / 365.0;
            return d5 > 0.5
                ? StatBase.decimalFormat.format(d5) + " y"
                : (
                    d4 > 0.5
                        ? StatBase.decimalFormat.format(d4) + " d"
                        : (d3 > 0.5 ? StatBase.decimalFormat.format(d3) + " h" : (d2 > 0.5 ? StatBase.decimalFormat.format(d2) + " m" : d + " s"))
                );
        }
    };
    public static IStatType distanceStatType = new IStatType() {
        @Override
        public String format(int n) {
            double d = n / 100.0;
            double d2 = d / 1000.0;
            return d2 > 0.5 ? StatBase.decimalFormat.format(d2) + " km" : (d > 0.5 ? StatBase.decimalFormat.format(d) + " m" : n + " cm");
        }
    };
    public static IStatType DAMAGE_PROVIDER = new IStatType() {
        @Override
        public String format(int n) {
            return StatBase.decimalFormat.format(n * 0.1);
        }
    };

    public StatBase(String str, IChatComponent chatComponent, IStatType statType) {
        this.statId = str;
        this.statName = chatComponent;
        this.type = statType;
        this.criterion = new ObjectiveStat(this);
        IScoreObjectiveCriteria.INSTANCES.put(this.criterion.getName(), this.criterion);
    }

    public StatBase(String str, IChatComponent chatComponent) {
        this(str, chatComponent, simpleStatType);
    }

    public StatBase initIndependentStat() {
        this.isIndependent = true;
        return this;
    }

    public StatBase registerStat() {
        if (StatList.oneShotStats.containsKey(this.statId)) {
            throw new RuntimeException(
                "Duplicate stat id: \"" + ((StatBase)StatList.oneShotStats.get(this.statId)).statName + "\" and \"" + this.statName + "\" at id " + this.statId
            );
        }

        StatList.allStats.add(this);
        StatList.oneShotStats.put(this.statId, this);
        return this;
    }

    public boolean isAchievement() {
        return false;
    }

    public String formatValue(int n) {
        return this.type.format(n);
    }

    public IChatComponent getStatName() {
        IChatComponent createCopy = this.statName.createCopy();
        createCopy.getChatStyle().setColor(EnumChatFormatting.GRAY);
        createCopy.getChatStyle().setChatHoverEvent(new HoverEvent(Action.SHOW_ACHIEVEMENT, new ChatComponentText(this.statId)));
        return createCopy;
    }

    public IChatComponent createChatComponentText() {
        IChatComponent statName = this.getStatName();
        IChatComponent appendText = new ChatComponentText("[").appendSibling(statName).appendText("]");
        appendText.setChatStyle(statName.getChatStyle());
        return appendText;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            StatBase statBase = (StatBase)obj;
            return this.statId.equals(statBase.statId);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.statId.hashCode();
    }

    @Override
    public String toString() {
        return "Stat{id="
            + this.statId
            + ", nameId="
            + this.statName
            + ", awardLocallyOnly="
            + this.isIndependent
            + ", formatter="
            + this.type
            + ", objectiveCriteria="
            + this.criterion
            + '}';
    }

    public IScoreObjectiveCriteria getCriterion() {
        return this.criterion;
    }

    public Class getJsonElementProvider() {
        return this.jsonElementProvider;
    }

    public StatBase setJsonElementProvider(Class clazz) {
        this.jsonElementProvider = clazz;
        return this;
    }
}
