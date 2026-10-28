package net.minecraft.client.gui.achievement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiStats extends GuiScreen implements IProgressMeter {
    public static RenderItem renderItem = new RenderItem();
    public String screenTitle = "Select world";
    public boolean doesGuiPauseGame = true;
    public GuiStats.StatsGeneral generalStats;
    public GuiStats.StatsItem itemStats;
    public GuiStats.StatsBlock blockStats;
    public GuiStats.StatsMobsList mobStats;
    public GuiScreen parentScreen;
    public GuiSlot displaySlot;
    public StatFileWriter statHandler;

    public GuiStats(GuiScreen guiScreen, StatFileWriter statFileWriter) {
        this.parentScreen = guiScreen;
        this.statHandler = statFileWriter;
    }

    @Override
    public void initGui() {
        this.screenTitle = I18n.format("gui.stats");
        this.mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(EnumState.REQUEST_STATS));
    }

    public void createButtons() {
        this.buttonList.add(new GuiButton(0, this.width / 2 + 4, this.height - 28, 150, 20, I18n.format("gui.done")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 160, this.height - 52, 80, 20, I18n.format("stat.generalButton")));
        GuiButton guiButton;
        this.buttonList.add(guiButton = new GuiButton(2, this.width / 2 - 80, this.height - 52, 80, 20, I18n.format("stat.blocksButton")));
        GuiButton guiButton2;
        this.buttonList.add(guiButton2 = new GuiButton(3, this.width / 2, this.height - 52, 80, 20, I18n.format("stat.itemsButton")));
        GuiButton guiButton3;
        this.buttonList.add(guiButton3 = new GuiButton(4, this.width / 2 + 80, this.height - 52, 80, 20, I18n.format("stat.mobsButton")));
        if (this.blockStats.getSize() == 0) {
            guiButton.enabled = false;
        }

        if (this.itemStats.getSize() == 0) {
            guiButton2.enabled = false;
        }

        if (this.mobStats.getSize() == 0) {
            guiButton3.enabled = false;
        }
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.enabled) {
            if (button.id == 0) {
                this.mc.displayGuiScreen(this.parentScreen);
            } else if (button.id == 1) {
                this.displaySlot = this.generalStats;
            } else if (button.id == 3) {
                this.displaySlot = this.itemStats;
            } else if (button.id == 2) {
                this.displaySlot = this.blockStats;
            } else if (button.id == 4) {
                this.displaySlot = this.mobStats;
            } else {
                this.displaySlot.actionPerformed(button);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        if (this.doesGuiPauseGame) {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
            this.drawCenteredString(
                this.fontRendererObj,
                lanSearchStates[(int)(Minecraft.getSystemTime() / 150L % lanSearchStates.length)],
                this.width / 2,
                this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2,
                16777215
            );
        } else {
            this.displaySlot.drawScreen(mouseX, mouseY, partialTicks);
            this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 20, 16777215);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void doneLoading() {
        if (this.doesGuiPauseGame) {
            this.generalStats = new GuiStats.StatsGeneral();
            this.generalStats.registerScrollButtons(1, 1);
            this.itemStats = new GuiStats.StatsItem();
            this.itemStats.registerScrollButtons(1, 1);
            this.blockStats = new GuiStats.StatsBlock();
            this.blockStats.registerScrollButtons(1, 1);
            this.mobStats = new GuiStats.StatsMobsList();
            this.mobStats.registerScrollButtons(1, 1);
            this.displaySlot = this.generalStats;
            this.createButtons();
            this.doesGuiPauseGame = false;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return !this.doesGuiPauseGame;
    }

    public void drawStatsScreen(int n, int n2, Item item) {
        this.drawButtonBackground(n + 1, n2 + 1);
        GL11.glEnable(32826);
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), new ItemStack(item, 1, 0), n + 2, n2 + 2);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(32826);
    }

    public void drawButtonBackground(int n, int n2) {
        this.drawSprite(n, n2, 0, 0);
    }

    public void drawSprite(int n, int n2, int n3, int n4) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(statIcons);
        float f = 0.0078125F;
        float f2 = 0.0078125F;
        boolean flag = true;
        boolean flag2 = true;
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertexWithUV(n + 0, n2 + 18, floatValue, (n3 + 0) * 0.0078125F, (n4 + 18) * 0.0078125F);
        instance.addVertexWithUV(n + 18, n2 + 18, floatValue, (n3 + 18) * 0.0078125F, (n4 + 18) * 0.0078125F);
        instance.addVertexWithUV(n + 18, n2 + 0, floatValue, (n3 + 18) * 0.0078125F, (n4 + 0) * 0.0078125F);
        instance.addVertexWithUV(n + 0, n2 + 0, floatValue, (n3 + 0) * 0.0078125F, (n4 + 0) * 0.0078125F);
        instance.draw();
    }

    public abstract class Stats extends GuiSlot {
        public int intValue = -1;
        public List statsHolder;
        public Comparator statSorter;
        public int intValue2 = -1;
        public int intValue3;

        public Stats() {
            super(GuiStats.this.mc, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 20);
            this.setShowSelectionBox(false);
            this.setHasListHeader(true, 20);
        }

        @Override
        public void elementClicked(int n, boolean flag, int n2, int n3) {
        }

        @Override
        public boolean isSelected(int n) {
            return false;
        }

        @Override
        public void drawBackground() {
            GuiStats.this.drawDefaultBackground();
        }

        @Override
        public void drawListHeader(int n, int n2, Tessellator tessellator) {
            if (!Mouse.isButtonDown(0)) {
                this.intValue = -1;
            }

            if (this.intValue == 0) {
                GuiStats.this.drawSprite(n + 115 - 18, n2 + 1, 0, 0);
            } else {
                GuiStats.this.drawSprite(n + 115 - 18, n2 + 1, 0, 18);
            }

            if (this.intValue == 1) {
                GuiStats.this.drawSprite(n + 165 - 18, n2 + 1, 0, 0);
            } else {
                GuiStats.this.drawSprite(n + 165 - 18, n2 + 1, 0, 18);
            }

            if (this.intValue == 2) {
                GuiStats.this.drawSprite(n + 215 - 18, n2 + 1, 0, 0);
            } else {
                GuiStats.this.drawSprite(n + 215 - 18, n2 + 1, 0, 18);
            }

            if (this.intValue2 != -1) {
                short s = 79;
                byte b = 18;
                if (this.intValue2 == 1) {
                    s = 129;
                } else if (this.intValue2 == 2) {
                    s = 179;
                }

                if (this.intValue3 == 1) {
                    b = 36;
                }

                GuiStats.this.drawSprite(n + s, n2 + 1, b, 0);
            }
        }

        @Override
        public void process(int n, int n2) {
            this.intValue = -1;
            if (n >= 79 && n < 115) {
                this.intValue = 0;
            } else if (n >= 129 && n < 165) {
                this.intValue = 1;
            } else if (n >= 179 && n < 215) {
                this.intValue = 2;
            }

            if (this.intValue >= 0) {
                this.process(this.intValue);
                GuiStats.this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            }
        }

        @Override
        public int getSize() {
            return this.statsHolder.size();
        }

        public StatCrafting get(int n) {
            return (StatCrafting)this.statsHolder.get(n);
        }

        public abstract String formatText(int n);

        public void handleStatBase(StatBase statBase, int n, int n2, boolean flag) {
            if (statBase != null) {
                String formatValue = statBase.formatValue(GuiStats.this.statHandler.writeStat(statBase));
                GuiStats.this.drawString(
                    GuiStats.this.fontRendererObj, formatValue, n - GuiStats.this.fontRendererObj.getStringWidth(formatValue), n2 + 5, flag ? 16777215 : 9474192
                );
            } else {
                String str = "-";
                GuiStats.this.drawString(
                    GuiStats.this.fontRendererObj, str, n - GuiStats.this.fontRendererObj.getStringWidth(str), n2 + 5, flag ? 16777215 : 9474192
                );
            }
        }

        @Override
        public void renderDecorations(int n, int n2) {
            if (n2 >= this.top && n2 <= this.bottom) {
                int slotIndexFromScreenCoords = this.getSlotIndexFromScreenCoords(n, n2);
                int n3 = this.width / 2 - 92 - 16;
                if (slotIndexFromScreenCoords >= 0) {
                    if (n < n3 + 40 || n > n3 + 40 + 20) {
                        return;
                    }

                    StatCrafting statCrafting = this.get(slotIndexFromScreenCoords);
                    this.handleStatCrafting(statCrafting, n, n2);
                } else {
                    String str = "";
                    if (n >= n3 + 115 - 18 && n <= n3 + 115) {
                        str = this.formatText(0);
                    } else if (n >= n3 + 165 - 18 && n <= n3 + 165) {
                        str = this.formatText(1);
                    } else {
                        if (n < n3 + 215 - 18 || n > n3 + 215) {
                            return;
                        }

                        str = this.formatText(2);
                    }

                    str = ("" + I18n.format(str)).trim();
                    if (str.length() > 0) {
                        int n4 = n + 12;
                        int n5 = n2 - 12;
                        int stringWidth = GuiStats.this.fontRendererObj.getStringWidth(str);
                        Gui.drawVerticalGradient(n4 - 3, n5 - 3, n4 + stringWidth + 3, n5 + 8 + 3, -1073741824, -1073741824);
                        GuiStats.this.fontRendererObj.drawShadowedString(str, n4, n5, -1);
                    }
                }
            }
        }

        public void handleStatCrafting(StatCrafting statCrafting, int n, int n2) {
            if (statCrafting != null) {
                Item item = statCrafting.getItem();
                String trim = ("" + I18n.format(item.getUnlocalizedName() + ".name")).trim();
                if (trim.length() > 0) {
                    int n3 = n + 12;
                    int n4 = n2 - 12;
                    int stringWidth = GuiStats.this.fontRendererObj.getStringWidth(trim);
                    Gui.drawVerticalGradient(n3 - 3, n4 - 3, n3 + stringWidth + 3, n4 + 8 + 3, -1073741824, -1073741824);
                    GuiStats.this.fontRendererObj.drawShadowedString(trim, n3, n4, -1);
                }
            }
        }

        public void process(int n) {
            if (n != this.intValue2) {
                this.intValue2 = n;
                this.intValue3 = -1;
            } else if (this.intValue3 == -1) {
                this.intValue3 = 1;
            } else {
                this.intValue2 = -1;
                this.intValue3 = 0;
            }

            Collections.sort(this.statsHolder, this.statSorter);
        }
    }

    public class StatsBlock extends GuiStats.Stats {
        public StatsBlock() {
            this.statsHolder = new ArrayList();

            for (StatCrafting statCrafting : (Iterable<StatCrafting>)StatList.objectMineStats) {
                boolean flag = false;
                int idFromItem = Item.getIdFromItem(statCrafting.getItem());
                if (GuiStats.this.statHandler.writeStat(statCrafting) > 0) {
                    flag = true;
                } else if (StatList.objectUseStats[idFromItem] != null && GuiStats.this.statHandler.writeStat(StatList.objectUseStats[idFromItem]) > 0) {
                    flag = true;
                } else if (StatList.objectCraftStats[idFromItem] != null && GuiStats.this.statHandler.writeStat(StatList.objectCraftStats[idFromItem]) > 0) {
                    flag = true;
                }

                if (flag) {
                    this.statsHolder.add(statCrafting);
                }
            }

            this.statSorter = new Comparator() {
                public int compare(StatCrafting statCrafting, StatCrafting statCrafting2) {
                    int idFromItem = Item.getIdFromItem(statCrafting.getItem());
                    int idFromItem2 = Item.getIdFromItem(statCrafting2.getItem());
                    StatBase statBase = null;
                    StatBase statBase2 = null;
                    if (StatsBlock.this.intValue2 == 2) {
                        statBase = StatList.mineBlockStatArray[idFromItem];
                        statBase2 = StatList.mineBlockStatArray[idFromItem2];
                    } else if (StatsBlock.this.intValue2 == 0) {
                        statBase = StatList.objectCraftStats[idFromItem];
                        statBase2 = StatList.objectCraftStats[idFromItem2];
                    } else if (StatsBlock.this.intValue2 == 1) {
                        statBase = StatList.objectUseStats[idFromItem];
                        statBase2 = StatList.objectUseStats[idFromItem2];
                    }

                    if (statBase != null || statBase2 != null) {
                        if (statBase == null) {
                            return 1;
                        }

                        if (statBase2 == null) {
                            return -1;
                        }

                        int writeStat = GuiStats.this.statHandler.writeStat(statBase);
                        int writeStat2 = GuiStats.this.statHandler.writeStat(statBase2);
                        if (writeStat != writeStat2) {
                            return (writeStat - writeStat2) * StatsBlock.this.intValue3;
                        }
                    }

                    return idFromItem - idFromItem2;
                }

                @Override
                public int compare(Object obj, Object obj2) {
                    return this.compare((StatCrafting)obj, (StatCrafting)obj2);
                }
            };
        }

        @Override
        public void drawListHeader(int n, int n2, Tessellator tessellator) {
            super.drawListHeader(n, n2, tessellator);
            if (this.intValue == 0) {
                GuiStats.this.drawSprite(n + 115 - 18 + 1, n2 + 1 + 1, 18, 18);
            } else {
                GuiStats.this.drawSprite(n + 115 - 18, n2 + 1, 18, 18);
            }

            if (this.intValue == 1) {
                GuiStats.this.drawSprite(n + 165 - 18 + 1, n2 + 1 + 1, 36, 18);
            } else {
                GuiStats.this.drawSprite(n + 165 - 18, n2 + 1, 36, 18);
            }

            if (this.intValue == 2) {
                GuiStats.this.drawSprite(n + 215 - 18 + 1, n2 + 1 + 1, 54, 18);
            } else {
                GuiStats.this.drawSprite(n + 215 - 18, n2 + 1, 54, 18);
            }
        }

        @Override
        public void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6) {
            StatCrafting statCrafting = this.get(n);
            Item item = statCrafting.getItem();
            GuiStats.this.drawStatsScreen(n2 + 40, n3, item);
            int idFromItem = Item.getIdFromItem(item);
            this.handleStatBase(StatList.objectCraftStats[idFromItem], n2 + 115, n3, n % 2 == 0);
            this.handleStatBase(StatList.objectUseStats[idFromItem], n2 + 165, n3, n % 2 == 0);
            this.handleStatBase(statCrafting, n2 + 215, n3, n % 2 == 0);
        }

        @Override
        public String formatText(int n) {
            return n == 0 ? "stat.crafted" : (n == 1 ? "stat.used" : "stat.mined");
        }
    }

    public class StatsGeneral extends GuiSlot {
        public StatsGeneral() {
            super(GuiStats.this.mc, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 10);
            this.setShowSelectionBox(false);
        }

        @Override
        public int getSize() {
            return StatList.generalStats.size();
        }

        @Override
        public void elementClicked(int n, boolean flag, int n2, int n3) {
        }

        @Override
        public boolean isSelected(int n) {
            return false;
        }

        @Override
        public int getContentHeight() {
            return this.getSize() * 10;
        }

        @Override
        public void drawBackground() {
            GuiStats.this.drawDefaultBackground();
        }

        @Override
        public void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6) {
            StatBase statBase = (StatBase)StatList.generalStats.get(n);
            GuiStats.this.drawString(
                GuiStats.this.fontRendererObj, statBase.getStatName().getUnformattedText(), n2 + 2, n3 + 1, n % 2 == 0 ? 16777215 : 9474192
            );
            String formatValue = statBase.formatValue(GuiStats.this.statHandler.writeStat(statBase));
            GuiStats.this.drawString(
                GuiStats.this.fontRendererObj,
                formatValue,
                n2 + 2 + 213 - GuiStats.this.fontRendererObj.getStringWidth(formatValue),
                n3 + 1,
                n % 2 == 0 ? 16777215 : 9474192
            );
        }
    }

    public class StatsItem extends GuiStats.Stats {
        public StatsItem() {
            this.statsHolder = new ArrayList();

            for (StatCrafting statCrafting : (Iterable<StatCrafting>)StatList.itemStats) {
                boolean flag = false;
                int idFromItem = Item.getIdFromItem(statCrafting.getItem());
                if (GuiStats.this.statHandler.writeStat(statCrafting) > 0) {
                    flag = true;
                } else if (StatList.objectBreakStats[idFromItem] != null && GuiStats.this.statHandler.writeStat(StatList.objectBreakStats[idFromItem]) > 0) {
                    flag = true;
                } else if (StatList.objectCraftStats[idFromItem] != null && GuiStats.this.statHandler.writeStat(StatList.objectCraftStats[idFromItem]) > 0) {
                    flag = true;
                }

                if (flag) {
                    this.statsHolder.add(statCrafting);
                }
            }

            this.statSorter = new Comparator() {
                public int compare(StatCrafting statCrafting, StatCrafting statCrafting2) {
                    int idFromItem = Item.getIdFromItem(statCrafting.getItem());
                    int idFromItem2 = Item.getIdFromItem(statCrafting2.getItem());
                    StatBase statBase = null;
                    StatBase statBase2 = null;
                    if (StatsItem.this.intValue2 == 0) {
                        statBase = StatList.objectBreakStats[idFromItem];
                        statBase2 = StatList.objectBreakStats[idFromItem2];
                    } else if (StatsItem.this.intValue2 == 1) {
                        statBase = StatList.objectCraftStats[idFromItem];
                        statBase2 = StatList.objectCraftStats[idFromItem2];
                    } else if (StatsItem.this.intValue2 == 2) {
                        statBase = StatList.objectUseStats[idFromItem];
                        statBase2 = StatList.objectUseStats[idFromItem2];
                    }

                    if (statBase != null || statBase2 != null) {
                        if (statBase == null) {
                            return 1;
                        }

                        if (statBase2 == null) {
                            return -1;
                        }

                        int writeStat = GuiStats.this.statHandler.writeStat(statBase);
                        int writeStat2 = GuiStats.this.statHandler.writeStat(statBase2);
                        if (writeStat != writeStat2) {
                            return (writeStat - writeStat2) * StatsItem.this.intValue3;
                        }
                    }

                    return idFromItem - idFromItem2;
                }

                @Override
                public int compare(Object obj, Object obj2) {
                    return this.compare((StatCrafting)obj, (StatCrafting)obj2);
                }
            };
        }

        @Override
        public void drawListHeader(int n, int n2, Tessellator tessellator) {
            super.drawListHeader(n, n2, tessellator);
            if (this.intValue == 0) {
                GuiStats.this.drawSprite(n + 115 - 18 + 1, n2 + 1 + 1, 72, 18);
            } else {
                GuiStats.this.drawSprite(n + 115 - 18, n2 + 1, 72, 18);
            }

            if (this.intValue == 1) {
                GuiStats.this.drawSprite(n + 165 - 18 + 1, n2 + 1 + 1, 18, 18);
            } else {
                GuiStats.this.drawSprite(n + 165 - 18, n2 + 1, 18, 18);
            }

            if (this.intValue == 2) {
                GuiStats.this.drawSprite(n + 215 - 18 + 1, n2 + 1 + 1, 36, 18);
            } else {
                GuiStats.this.drawSprite(n + 215 - 18, n2 + 1, 36, 18);
            }
        }

        @Override
        public void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6) {
            StatCrafting statCrafting = this.get(n);
            Item item = statCrafting.getItem();
            GuiStats.this.drawStatsScreen(n2 + 40, n3, item);
            int idFromItem = Item.getIdFromItem(item);
            this.handleStatBase(StatList.objectBreakStats[idFromItem], n2 + 115, n3, n % 2 == 0);
            this.handleStatBase(StatList.objectCraftStats[idFromItem], n2 + 165, n3, n % 2 == 0);
            this.handleStatBase(statCrafting, n2 + 215, n3, n % 2 == 0);
        }

        @Override
        public String formatText(int n) {
            return n == 1 ? "stat.crafted" : (n == 2 ? "stat.used" : "stat.depleted");
        }
    }

    public class StatsMobsList extends GuiSlot {
        public List list = new ArrayList();

        public StatsMobsList() {
            super(GuiStats.this.mc, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, GuiStats.this.fontRendererObj.FONT_HEIGHT * 4);
            this.setShowSelectionBox(false);

            for (EntityEggInfo entityEggInfo : (Iterable<EntityEggInfo>)EntityList.entityEggs.values()) {
                if (GuiStats.this.statHandler.writeStat(entityEggInfo.statBase) > 0 || GuiStats.this.statHandler.writeStat(entityEggInfo.statBase2) > 0) {
                    this.list.add(entityEggInfo);
                }
            }
        }

        @Override
        public int getSize() {
            return this.list.size();
        }

        @Override
        public void elementClicked(int n, boolean flag, int n2, int n3) {
        }

        @Override
        public boolean isSelected(int n) {
            return false;
        }

        @Override
        public int getContentHeight() {
            return this.getSize() * GuiStats.this.fontRendererObj.FONT_HEIGHT * 4;
        }

        @Override
        public void drawBackground() {
            GuiStats.this.drawDefaultBackground();
        }

        @Override
        public void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6) {
            EntityEggInfo entityEggInfo = (EntityEggInfo)this.list.get(n);
            String format = I18n.format("entity." + EntityList.getStringFromID(entityEggInfo.spawnedID) + ".name");
            int writeStat = GuiStats.this.statHandler.writeStat(entityEggInfo.statBase);
            int writeStat2 = GuiStats.this.statHandler.writeStat(entityEggInfo.statBase2);
            String format2 = I18n.format("stat.entityKills", writeStat, format);
            String format3 = I18n.format("stat.entityKilledBy", format, writeStat2);
            if (writeStat == 0) {
                format2 = I18n.format("stat.entityKills.none", format);
            }

            if (writeStat2 == 0) {
                format3 = I18n.format("stat.entityKilledBy.none", format);
            }

            GuiStats.this.drawString(GuiStats.this.fontRendererObj, format, n2 + 2 - 10, n3 + 1, 16777215);
            GuiStats.this.drawString(
                GuiStats.this.fontRendererObj, format2, n2 + 2, n3 + 1 + GuiStats.this.fontRendererObj.FONT_HEIGHT, writeStat == 0 ? 6316128 : 9474192
            );
            GuiStats.this.drawString(
                GuiStats.this.fontRendererObj, format3, n2 + 2, n3 + 1 + GuiStats.this.fontRendererObj.FONT_HEIGHT * 2, writeStat2 == 0 ? 6316128 : 9474192
            );
        }
    }
}
