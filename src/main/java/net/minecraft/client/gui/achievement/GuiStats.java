package net.minecraft.client.gui.achievement;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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

public class GuiStats extends GuiScreen implements IProgressMeter {
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
      this.doesGuiPauseGame = true;
      this.mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(EnumState.REQUEST_STATS));
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      if (this.displaySlot != null) {
         this.displaySlot.handleMouseInput();
      }
   }

   public void createLists() {
      this.generalStats = new GuiStats.StatsGeneral(this.mc);
      this.generalStats.registerScrollButtons(1, 1);
      this.itemStats = new GuiStats.StatsItem(this.mc);
      this.itemStats.registerScrollButtons(1, 1);
      this.blockStats = new GuiStats.StatsBlock(this.mc);
      this.blockStats.registerScrollButtons(1, 1);
      this.mobStats = new GuiStats.StatsMobsList(this.mc);
      this.mobStats.registerScrollButtons(1, 1);
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
   public void actionPerformed(GuiButton button) throws IOException {
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
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
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
         this.createLists();
         this.createButtons();
         this.displaySlot = this.generalStats;
         this.doesGuiPauseGame = false;
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return !this.doesGuiPauseGame;
   }

   public void drawStatsScreen(int n, int n2, Item item) {
      this.drawButtonBackground(n + 1, n2 + 1);
      GlStateManager.enableRescaleNormal();
      RenderHelper.enableGUIStandardItemLighting();
      this.itemRender.renderItemIntoGUI(new ItemStack(item, 1, 0), n + 2, n2 + 2);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
   }

   public void drawButtonBackground(int n, int n2) {
      this.drawSprite(n, n2, 0, 0);
   }

   public void drawSprite(int n, int n2, int n3, int n4) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(statIcons);
      float f = 0.0078125F;
      float f2 = 0.0078125F;
      byte b = 18;
      byte b2 = 18;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(n + 0, n2 + 18, zLevel).tex((n3 + 0) * 0.0078125F, (n4 + 18) * 0.0078125F).endVertex();
      worldRenderer.pos(n + 18, n2 + 18, zLevel).tex((n3 + 18) * 0.0078125F, (n4 + 18) * 0.0078125F).endVertex();
      worldRenderer.pos(n + 18, n2 + 0, zLevel).tex((n3 + 18) * 0.0078125F, (n4 + 0) * 0.0078125F).endVertex();
      worldRenderer.pos(n + 0, n2 + 0, zLevel).tex((n3 + 0) * 0.0078125F, (n4 + 0) * 0.0078125F).endVertex();
      instance.draw();
   }

   abstract class Stats extends GuiSlot {
      public int headerMode = -1;
      public List<StatCrafting> statsHolder;
      public Comparator<StatCrafting> statSorter;
      public int sortColumnMode = -1;
      public int sortOrder;

      public Stats(Minecraft mcIn) {
         super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 20);
         this.setShowSelectionBox(false);
         this.setHasListHeader(true, 20);
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return false;
      }

      @Override
      public void drawBackground() {
         GuiStats.this.drawDefaultBackground();
      }

      @Override
      public void drawListHeader(int n, int n2, Tessellator tessellator) {
         if (!Mouse.isButtonDown(0)) {
            this.headerMode = -1;
         }

         if (this.headerMode == 0) {
            GuiStats.this.drawSprite(n + 115 - 18, n2 + 1, 0, 0);
         } else {
            GuiStats.this.drawSprite(n + 115 - 18, n2 + 1, 0, 18);
         }

         if (this.headerMode == 1) {
            GuiStats.this.drawSprite(n + 165 - 18, n2 + 1, 0, 0);
         } else {
            GuiStats.this.drawSprite(n + 165 - 18, n2 + 1, 0, 18);
         }

         if (this.headerMode == 2) {
            GuiStats.this.drawSprite(n + 215 - 18, n2 + 1, 0, 0);
         } else {
            GuiStats.this.drawSprite(n + 215 - 18, n2 + 1, 0, 18);
         }

         if (this.sortColumnMode != -1) {
            short s = 79;
            byte b = 18;
            if (this.sortColumnMode == 1) {
               s = 129;
            } else if (this.sortColumnMode == 2) {
               s = 179;
            }

            if (this.sortOrder == 1) {
               b = 36;
            }

            GuiStats.this.drawSprite(n + s, n2 + 1, b, 0);
         }
      }

      @Override
      public void process(int n, int n2) {
         this.headerMode = -1;
         if (n >= 79 && n < 115) {
            this.headerMode = 0;
         } else if (n >= 129 && n < 165) {
            this.headerMode = 1;
         } else if (n >= 179 && n < 215) {
            this.headerMode = 2;
         }

         if (this.headerMode >= 0) {
            this.changeSortOrder(this.headerMode);
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         }
      }

      @Override
      public int getSize() {
         return this.statsHolder.size();
      }

      public StatCrafting getEntry(int n) {
         return this.statsHolder.get(n);
      }

      public abstract String formatText(int n);

      public void renderStat(StatBase statBase, int n, int n2, boolean flag) {
         if (statBase != null) {
            String format = statBase.format(GuiStats.this.statHandler.readStat(statBase));
            GuiStats.this.drawString(
               GuiStats.this.fontRendererObj, format, n - GuiStats.this.fontRendererObj.getStringWidth(format), n2 + 5, flag ? 16777215 : 9474192
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

               StatCrafting entry = this.getEntry(slotIndexFromScreenCoords);
               this.render(entry, n, n2);
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
                  GuiStats.this.drawGradientRect(n4 - 3, n5 - 3, n4 + stringWidth + 3, n5 + 8 + 3, -1073741824, -1073741824);
                  GuiStats.this.fontRendererObj.drawStringWithShadow(str, n4, n5, -1);
               }
            }
         }
      }

      public void render(StatCrafting statCrafting, int n, int n2) {
         if (statCrafting != null) {
            Item item = statCrafting.getItem();
            ItemStack itemStack = new ItemStack(item);
            String unlocalizedName = itemStack.getUnlocalizedName();
            String trim = ("" + I18n.format(unlocalizedName + ".name")).trim();
            if (trim.length() > 0) {
               int n3 = n + 12;
               int n4 = n2 - 12;
               int stringWidth = GuiStats.this.fontRendererObj.getStringWidth(trim);
               GuiStats.this.drawGradientRect(n3 - 3, n4 - 3, n3 + stringWidth + 3, n4 + 8 + 3, -1073741824, -1073741824);
               GuiStats.this.fontRendererObj.drawStringWithShadow(trim, n3, n4, -1);
            }
         }
      }

      public void changeSortOrder(int n) {
         if (n != this.sortColumnMode) {
            this.sortColumnMode = n;
            this.sortOrder = -1;
         } else if (this.sortOrder == -1) {
            this.sortOrder = 1;
         } else {
            this.sortColumnMode = -1;
            this.sortOrder = 0;
         }

         Collections.sort(this.statsHolder, this.statSorter);
      }
   }

   class StatsBlock extends GuiStats.Stats {
      public StatsBlock(Minecraft mcIn) {
         super(mcIn);
         this.statsHolder = Lists.newArrayList();

         for (StatCrafting statCrafting : StatList.objectMineStats) {
            boolean flag = false;
            int idFromItem = Item.getIdFromItem(statCrafting.getItem());
            if (GuiStats.this.statHandler.readStat(statCrafting) > 0) {
               flag = true;
            } else if (StatList.objectUseStats[idFromItem] != null && GuiStats.this.statHandler.readStat(StatList.objectUseStats[idFromItem]) > 0) {
               flag = true;
            } else if (StatList.objectCraftStats[idFromItem] != null && GuiStats.this.statHandler.readStat(StatList.objectCraftStats[idFromItem]) > 0) {
               flag = true;
            }

            if (flag) {
               this.statsHolder.add(statCrafting);
            }
         }

         this.statSorter = new Comparator<StatCrafting>() {
            public int compare(StatCrafting statCrafting, StatCrafting statCrafting2) {
               int idFromItem = Item.getIdFromItem(statCrafting.getItem());
               int idFromItem2 = Item.getIdFromItem(statCrafting2.getItem());
               StatBase statBase = null;
               StatBase statBase2 = null;
               if (StatsBlock.this.sortColumnMode == 2) {
                  statBase = StatList.mineBlockStatArray[idFromItem];
                  statBase2 = StatList.mineBlockStatArray[idFromItem2];
               } else if (StatsBlock.this.sortColumnMode == 0) {
                  statBase = StatList.objectCraftStats[idFromItem];
                  statBase2 = StatList.objectCraftStats[idFromItem2];
               } else if (StatsBlock.this.sortColumnMode == 1) {
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

                  int readStat = GuiStats.this.statHandler.readStat(statBase);
                  int readStat2 = GuiStats.this.statHandler.readStat(statBase2);
                  if (readStat != readStat2) {
                     return (readStat - readStat2) * StatsBlock.this.sortOrder;
                  }
               }

               return idFromItem - idFromItem2;
            }
         };
      }

      @Override
      public void drawListHeader(int n, int n2, Tessellator tessellator) {
         super.drawListHeader(n, n2, tessellator);
         if (this.headerMode == 0) {
            GuiStats.this.drawSprite(n + 115 - 18 + 1, n2 + 1 + 1, 18, 18);
         } else {
            GuiStats.this.drawSprite(n + 115 - 18, n2 + 1, 18, 18);
         }

         if (this.headerMode == 1) {
            GuiStats.this.drawSprite(n + 165 - 18 + 1, n2 + 1 + 1, 36, 18);
         } else {
            GuiStats.this.drawSprite(n + 165 - 18, n2 + 1, 36, 18);
         }

         if (this.headerMode == 2) {
            GuiStats.this.drawSprite(n + 215 - 18 + 1, n2 + 1 + 1, 54, 18);
         } else {
            GuiStats.this.drawSprite(n + 215 - 18, n2 + 1, 54, 18);
         }
      }

      @Override
      public void drawSlot(int entryID, int n, int n2, int n3, int mouseXIn, int mouseYIn) {
         StatCrafting entry = this.getEntry(entryID);
         Item item = entry.getItem();
         GuiStats.this.drawStatsScreen(n + 40, n2, item);
         int idFromItem = Item.getIdFromItem(item);
         this.renderStat(StatList.objectCraftStats[idFromItem], n + 115, n2, entryID % 2 == 0);
         this.renderStat(StatList.objectUseStats[idFromItem], n + 165, n2, entryID % 2 == 0);
         this.renderStat(entry, n + 215, n2, entryID % 2 == 0);
      }

      @Override
      public String formatText(int n) {
         return n == 0 ? "stat.crafted" : (n == 1 ? "stat.used" : "stat.mined");
      }
   }

   class StatsGeneral extends GuiSlot {
      public StatsGeneral(Minecraft mcIn) {
         super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 10);
         this.setShowSelectionBox(false);
      }

      @Override
      public int getSize() {
         return StatList.generalStats.size();
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      }

      @Override
      public boolean isSelected(int slotIndex) {
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
      public void drawSlot(int entryID, int n, int n2, int n3, int mouseXIn, int mouseYIn) {
         StatBase statBase = StatList.generalStats.get(entryID);
         GuiStats.this.drawString(
            GuiStats.this.fontRendererObj, statBase.getStatName().getUnformattedText(), n + 2, n2 + 1, entryID % 2 == 0 ? 16777215 : 9474192
         );
         String format = statBase.format(GuiStats.this.statHandler.readStat(statBase));
         GuiStats.this.drawString(
            GuiStats.this.fontRendererObj,
            format,
            n + 2 + 213 - GuiStats.this.fontRendererObj.getStringWidth(format),
            n2 + 1,
            entryID % 2 == 0 ? 16777215 : 9474192
         );
      }
   }

   class StatsItem extends GuiStats.Stats {
      public StatsItem(Minecraft mcIn) {
         super(mcIn);
         this.statsHolder = Lists.newArrayList();

         for (StatCrafting statCrafting : StatList.itemStats) {
            boolean flag = false;
            int idFromItem = Item.getIdFromItem(statCrafting.getItem());
            if (GuiStats.this.statHandler.readStat(statCrafting) > 0) {
               flag = true;
            } else if (StatList.objectBreakStats[idFromItem] != null && GuiStats.this.statHandler.readStat(StatList.objectBreakStats[idFromItem]) > 0) {
               flag = true;
            } else if (StatList.objectCraftStats[idFromItem] != null && GuiStats.this.statHandler.readStat(StatList.objectCraftStats[idFromItem]) > 0) {
               flag = true;
            }

            if (flag) {
               this.statsHolder.add(statCrafting);
            }
         }

         this.statSorter = new Comparator<StatCrafting>() {
            public int compare(StatCrafting statCrafting, StatCrafting statCrafting2) {
               int idFromItem = Item.getIdFromItem(statCrafting.getItem());
               int idFromItem2 = Item.getIdFromItem(statCrafting2.getItem());
               StatBase statBase = null;
               StatBase statBase2 = null;
               if (StatsItem.this.sortColumnMode == 0) {
                  statBase = StatList.objectBreakStats[idFromItem];
                  statBase2 = StatList.objectBreakStats[idFromItem2];
               } else if (StatsItem.this.sortColumnMode == 1) {
                  statBase = StatList.objectCraftStats[idFromItem];
                  statBase2 = StatList.objectCraftStats[idFromItem2];
               } else if (StatsItem.this.sortColumnMode == 2) {
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

                  int readStat = GuiStats.this.statHandler.readStat(statBase);
                  int readStat2 = GuiStats.this.statHandler.readStat(statBase2);
                  if (readStat != readStat2) {
                     return (readStat - readStat2) * StatsItem.this.sortOrder;
                  }
               }

               return idFromItem - idFromItem2;
            }
         };
      }

      @Override
      public void drawListHeader(int n, int n2, Tessellator tessellator) {
         super.drawListHeader(n, n2, tessellator);
         if (this.headerMode == 0) {
            GuiStats.this.drawSprite(n + 115 - 18 + 1, n2 + 1 + 1, 72, 18);
         } else {
            GuiStats.this.drawSprite(n + 115 - 18, n2 + 1, 72, 18);
         }

         if (this.headerMode == 1) {
            GuiStats.this.drawSprite(n + 165 - 18 + 1, n2 + 1 + 1, 18, 18);
         } else {
            GuiStats.this.drawSprite(n + 165 - 18, n2 + 1, 18, 18);
         }

         if (this.headerMode == 2) {
            GuiStats.this.drawSprite(n + 215 - 18 + 1, n2 + 1 + 1, 36, 18);
         } else {
            GuiStats.this.drawSprite(n + 215 - 18, n2 + 1, 36, 18);
         }
      }

      @Override
      public void drawSlot(int entryID, int n, int n2, int n3, int mouseXIn, int mouseYIn) {
         StatCrafting entry = this.getEntry(entryID);
         Item item = entry.getItem();
         GuiStats.this.drawStatsScreen(n + 40, n2, item);
         int idFromItem = Item.getIdFromItem(item);
         this.renderStat(StatList.objectBreakStats[idFromItem], n + 115, n2, entryID % 2 == 0);
         this.renderStat(StatList.objectCraftStats[idFromItem], n + 165, n2, entryID % 2 == 0);
         this.renderStat(entry, n + 215, n2, entryID % 2 == 0);
      }

      @Override
      public String formatText(int n) {
         return n == 1 ? "stat.crafted" : (n == 2 ? "stat.used" : "stat.depleted");
      }
   }

   class StatsMobsList extends GuiSlot {
      public List<EntityEggInfo> entries = Lists.newArrayList();

      public StatsMobsList(Minecraft mcIn) {
         super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, GuiStats.this.fontRendererObj.FONT_HEIGHT * 4);
         this.setShowSelectionBox(false);

         for (EntityEggInfo entityEggInfo : EntityList.entityEggs.values()) {
            if (GuiStats.this.statHandler.readStat(entityEggInfo.killEntityStat) > 0 || GuiStats.this.statHandler.readStat(entityEggInfo.killedByEntityStat) > 0) {
               this.entries.add(entityEggInfo);
            }
         }
      }

      @Override
      public int getSize() {
         return this.entries.size();
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      }

      @Override
      public boolean isSelected(int slotIndex) {
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
      public void drawSlot(int entryID, int n, int n2, int n3, int mouseXIn, int mouseYIn) {
         EntityEggInfo entityEggInfo = this.entries.get(entryID);
         String format = I18n.format("entity." + EntityList.getStringFromID(entityEggInfo.spawnedID) + ".name");
         int readStat = GuiStats.this.statHandler.readStat(entityEggInfo.killEntityStat);
         int readStat2 = GuiStats.this.statHandler.readStat(entityEggInfo.killedByEntityStat);
         String format2 = I18n.format("stat.entityKills", readStat, format);
         String format3 = I18n.format("stat.entityKilledBy", format, readStat2);
         if (readStat == 0) {
            format2 = I18n.format("stat.entityKills.none", format);
         }

         if (readStat2 == 0) {
            format3 = I18n.format("stat.entityKilledBy.none", format);
         }

         GuiStats.this.drawString(GuiStats.this.fontRendererObj, format, n + 2 - 10, n2 + 1, 16777215);
         GuiStats.this.drawString(
            GuiStats.this.fontRendererObj, format2, n + 2, n2 + 1 + GuiStats.this.fontRendererObj.FONT_HEIGHT, readStat == 0 ? 6316128 : 9474192
         );
         GuiStats.this.drawString(
            GuiStats.this.fontRendererObj, format3, n + 2, n2 + 1 + GuiStats.this.fontRendererObj.FONT_HEIGHT * 2, readStat2 == 0 ? 6316128 : 9474192
         );
      }
   }
}
