package net.minecraft.client.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.StringEvent1;
import com.cheatbreaker.client.mod.impl.ToggleSprintInput;
import com.cheatbreaker.client.mod.impl.ToggleSprintMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C07PacketPlayerDigging;

import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;

import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class EntityPlayerSP extends AbstractClientPlayer {
   public ToggleSprintInput toggleSprintInput = new ToggleSprintInput(Minecraft.getMinecraft().gameSettings);
   public NetHandlerPlayClient sendQueue;
   public double lastReportedPosX;
   public double lastReportedPosY;
   public double lastReportedPosZ;
   public float lastReportedYaw;
   public float lastReportedPitch;
   public boolean serverSneakState;
   public boolean serverSprintState;
   public int positionUpdateTicks;
   public boolean hasValidHealth;
   public String clientBrand;
   public MovementInput movementInput;
   public StatFileWriter statWriter;
   public int sprintToggleTimer;
   public int sprintingTicksLeft;
   public float renderArmYaw;
   public float renderArmPitch;
   public float prevRenderArmYaw;
   public float prevRenderArmPitch;
   public int horseJumpPowerCounter;
   public float horseJumpPower;
   public float timeInPortal;
   public float prevTimeInPortal;
   public Minecraft mc;

   public EntityPlayerSP(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatFileWriter statFile) {
      super(worldIn, netHandler.getGameProfile());
      this.sendQueue = netHandler;
      this.statWriter = statFile;
      this.mc = mcIn;
      this.dimension = 0;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      return false;
   }

   @Override
   public void heal(float healAmount) {
   }

   @Override
   public void mountEntity(Entity entityIn) {
      super.mountEntity(entityIn);
      if (entityIn instanceof EntityMinecart) {
         this.mc.getSoundHandler().playSound(new MovingSoundMinecartRiding(this, (EntityMinecart)entityIn));
      }
   }

   @Override
   public void onUpdate() {
      if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, 0.0, this.posZ))) {
         super.onUpdate();
         if (this.isRiding()) {
            this.sendQueue.addToSendQueue(new C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
            this.sendQueue.addToSendQueue(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
         } else {
            this.onUpdateWalkingPlayer();
         }
      }
   }

   public void onUpdateWalkingPlayer() {
      boolean isSprinting = this.isSprinting();
      if (isSprinting != this.serverSprintState) {
         if (isSprinting) {
            this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SPRINTING));
         } else {
            this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SPRINTING));
         }

         this.serverSprintState = isSprinting;
      }

      boolean isSneaking = this.isSneaking();
      if (isSneaking != this.serverSneakState) {
         if (isSneaking) {
            this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SNEAKING));
         } else {
            this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SNEAKING));
         }

         this.serverSneakState = isSneaking;
      }

      if (this.isCurrentViewEntity()) {
         double d = this.posX - this.lastReportedPosX;
         double d2 = this.getEntityBoundingBox().minY - this.lastReportedPosY;
         double d3 = this.posZ - this.lastReportedPosZ;
         double d4 = this.rotationYaw - this.lastReportedYaw;
         double d5 = this.rotationPitch - this.lastReportedPitch;
         boolean flag = d * d + d2 * d2 + d3 * d3 > 9.0E-4 || this.positionUpdateTicks >= 20;
         boolean flag2 = d4 != 0.0 || d5 != 0.0;
         if (this.ridingEntity == null) {
            if (flag && flag2) {
               this.sendQueue
                  .addToSendQueue(
                     new C06PacketPlayerPosLook(
                        this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround
                     )
                  );
            } else if (flag) {
               this.sendQueue
                  .addToSendQueue(new C04PacketPlayerPosition(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.onGround));
            } else if (flag2) {
               this.sendQueue.addToSendQueue(new C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
            } else {
               this.sendQueue.addToSendQueue(new C03PacketPlayer(this.onGround));
            }
         } else {
            this.sendQueue
               .addToSendQueue(
                  new C06PacketPlayerPosLook(this.motionX, -999.0, this.motionZ, this.rotationYaw, this.rotationPitch, this.onGround)
               );
            flag = false;
         }

         this.positionUpdateTicks++;
         if (flag) {
            this.lastReportedPosX = this.posX;
            this.lastReportedPosY = this.getEntityBoundingBox().minY;
            this.lastReportedPosZ = this.posZ;
            this.positionUpdateTicks = 0;
         }

         if (flag2) {
            this.lastReportedYaw = this.rotationYaw;
            this.lastReportedPitch = this.rotationPitch;
         }
      }
   }

   @Override
   public EntityItem dropOneItem(boolean dropAll) {
      C07PacketPlayerDigging.Action action = dropAll ? C07PacketPlayerDigging.Action.DROP_ALL_ITEMS : C07PacketPlayerDigging.Action.DROP_ITEM;
      this.sendQueue.addToSendQueue(new C07PacketPlayerDigging(action, BlockPos.ORIGIN, EnumFacing.DOWN));
      return null;
   }

   @Override
   public void joinEntityItemWithWorld(EntityItem itemIn) {
   }

   public void sendChatMessage(String message) {
      StringEvent1 stringEvent1 = new StringEvent1(message);
      CheatBreaker.getInstance().getEventBus().handleEvent(stringEvent1);
      if (!stringEvent1.isFlag()) {
         this.sendQueue.addToSendQueue(new C01PacketChatMessage(message));
      }
   }

   @Override
   public void swingItem() {
      super.swingItem();
      this.sendQueue.addToSendQueue(new C0APacketAnimation());
   }

   @Override
   public void respawnPlayer() {
      this.sendQueue.addToSendQueue(new C16PacketClientStatus(EnumState.PERFORM_RESPAWN));
   }

   @Override
   public void damageEntity(DamageSource damageSrc, float damageAmount) {
      if (!this.isEntityInvulnerable(damageSrc)) {
         this.setHealth(this.getHealth() - damageAmount);
      }
   }

   @Override
   public void closeScreen() {
      this.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.openContainer.windowId));
      this.closeScreenAndDropStack();
   }

   public void closeScreenAndDropStack() {
      this.inventory.setItemStack(null);
      super.closeScreen();
      if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().guiBlur.getValue()) {
         Minecraft.getMinecraft().entityRenderer.run();
      }

      this.mc.displayGuiScreen(null);
   }

   public void setPlayerSPHealth(float health) {
      if (this.hasValidHealth) {
         float f = this.getHealth() - health;
         if (f <= 0.0F) {
            this.setHealth(health);
            if (f < 0.0F) {
               this.hurtResistantTime = this.maxHurtResistantTime / 2;
            }
         } else {
            this.lastDamage = f;
            this.setHealth(this.getHealth());
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.damageEntity(DamageSource.generic, f);
            this.hurtTime = this.maxHurtTime = 10;
         }
      } else {
         this.setHealth(health);
         this.hasValidHealth = true;
      }
   }

   @Override
   public void addStat(StatBase stat, int amount) {
      if (stat != null && stat.isIndependent) {
         super.addStat(stat, amount);
      }
   }

   @Override
   public void sendPlayerAbilities() {
      this.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(this.capabilities));
   }

   @Override
   public boolean isUser() {
      return true;
   }

   public void sendHorseJump() {
      this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.RIDING_JUMP, (int)(this.getHorseJumpPower() * 100.0F)));
   }

   public void sendHorseInventory() {
      this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.OPEN_INVENTORY));
   }

   public void setClientBrand(String brand) {
      this.clientBrand = brand;
   }

   public String getClientBrand() {
      return this.clientBrand;
   }

   public StatFileWriter getStatFileWriter() {
      return this.statWriter;
   }

   @Override
   public void addChatComponentMessage(IChatComponent chatComponent) {
      this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
   }

   @Override
   public boolean pushOutOfBlocks(double x, double y, double z) {
      if (this.noClip) {
         return false;
      }

      BlockPos blockPos = new BlockPos(x, y, z);
      double d = x - blockPos.getX();
      double d2 = z - blockPos.getZ();
      if (!this.isOpenBlockSpace(blockPos)) {
         byte b = -1;
         double d3 = 9999.0;
         if (this.isOpenBlockSpace(blockPos.west()) && d < d3) {
            d3 = d;
            b = 0;
         }

         if (this.isOpenBlockSpace(blockPos.east()) && 1.0 - d < d3) {
            d3 = 1.0 - d;
            b = 1;
         }

         if (this.isOpenBlockSpace(blockPos.north()) && d2 < d3) {
            d3 = d2;
            b = 4;
         }

         if (this.isOpenBlockSpace(blockPos.south()) && 1.0 - d2 < d3) {
            d3 = 1.0 - d2;
            b = 5;
         }

         float f = 0.1F;
         if (b == 0) {
            this.motionX = -f;
         }

         if (b == 1) {
            this.motionX = f;
         }

         if (b == 4) {
            this.motionZ = -f;
         }

         if (b == 5) {
            this.motionZ = f;
         }
      }

      return false;
   }

   public boolean isOpenBlockSpace(BlockPos pos) {
      return !this.worldObj.getBlockState(pos).getBlock().isNormalCube() && !this.worldObj.getBlockState(pos.up()).getBlock().isNormalCube();
   }

   @Override
   public void setSprinting(boolean sprinting) {
      super.setSprinting(sprinting);
      this.sprintingTicksLeft = sprinting ? 600 : 0;
   }

   public void setXPStats(float currentXP, int maxXP, int level) {
      this.experience = currentXP;
      this.experienceTotal = maxXP;
      this.experienceLevel = level;
   }

   @Override
   public void addChatMessage(IChatComponent component) {
      this.mc.ingameGUI.getChatGUI().printChatMessage(component);
   }

   @Override
   public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
      return permLevel <= 0;
   }

   @Override
   public BlockPos getPosition() {
      return new BlockPos(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5);
   }

   @Override
   public void playSound(String name, float volume, float pitch) {
      this.worldObj.playSound(this.posX, this.posY, this.posZ, name, volume, pitch, false);
   }

   @Override
   public boolean isServerWorld() {
      return true;
   }

   public boolean isRidingHorse() {
      return this.ridingEntity != null && this.ridingEntity instanceof EntityHorse && ((EntityHorse)this.ridingEntity).isHorseSaddled();
   }

   public float getHorseJumpPower() {
      return this.horseJumpPower;
   }

   @Override
   public void openEditSign(TileEntitySign signTile) {
      this.mc.displayGuiScreen(new GuiEditSign(signTile));
   }

   @Override
   public void openEditCommandBlock(CommandBlockLogic cmdBlockLogic) {
      this.mc.displayGuiScreen(new GuiCommandBlock(cmdBlockLogic));
   }

   @Override
   public void displayGUIBook(ItemStack bookStack) {
      Item item = bookStack.getItem();
      if (item == Items.writable_book) {
         this.mc.displayGuiScreen(new GuiScreenBook(this, bookStack, true));
      }
   }

   @Override
   public void displayGUIChest(IInventory chestInventory) {
      String str = chestInventory instanceof IInteractionObject ? ((IInteractionObject)chestInventory).getGuiID() : "minecraft:container";
      if ("minecraft:chest".equals(str)) {
         this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
      } else if ("minecraft:hopper".equals(str)) {
         this.mc.displayGuiScreen(new GuiHopper(this.inventory, chestInventory));
      } else if ("minecraft:furnace".equals(str)) {
         this.mc.displayGuiScreen(new GuiFurnace(this.inventory, chestInventory));
      } else if ("minecraft:brewing_stand".equals(str)) {
         this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, chestInventory));
      } else if ("minecraft:beacon".equals(str)) {
         this.mc.displayGuiScreen(new GuiBeacon(this.inventory, chestInventory));
      } else if (!"minecraft:dispenser".equals(str) && !"minecraft:dropper".equals(str)) {
         this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
      } else {
         this.mc.displayGuiScreen(new GuiDispenser(this.inventory, chestInventory));
      }
   }

   @Override
   public void displayGUIHorse(EntityHorse horse, IInventory horseInventory) {
      this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, horseInventory, horse));
   }

   @Override
   public void displayGui(IInteractionObject guiOwner) {
      String guiID = guiOwner.getGuiID();
      if ("minecraft:crafting_table".equals(guiID)) {
         this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj));
      } else if ("minecraft:enchanting_table".equals(guiID)) {
         this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, guiOwner));
      } else if ("minecraft:anvil".equals(guiID)) {
         this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.worldObj));
      }
   }

   @Override
   public void displayVillagerTradeGui(IMerchant villager) {
      this.mc.displayGuiScreen(new GuiMerchant(this.inventory, villager, this.worldObj));
   }

   @Override
   public void onCriticalHit(Entity entityHit) {
      this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT);
   }

   @Override
   public void onEnchantmentCritical(Entity entityHit) {
      this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT_MAGIC);
   }

   @Override
   public boolean isSneaking() {
      boolean flag = this.movementInput != null ? this.movementInput.sneak : false;
      return flag && !this.sleeping;
   }

   @Override
   public void updateEntityActionState() {
      super.updateEntityActionState();
      if (this.isCurrentViewEntity()) {
         this.moveStrafing = this.movementInput.moveStrafe;
         this.moveForward = this.movementInput.moveForward;
         this.isJumping = this.movementInput.jump;
         this.prevRenderArmYaw = this.renderArmYaw;
         this.prevRenderArmPitch = this.renderArmPitch;
         this.renderArmPitch = (float)(this.renderArmPitch + (this.rotationPitch - this.renderArmPitch) * 0.5);
         this.renderArmYaw = (float)(this.renderArmYaw + (this.rotationYaw - this.renderArmYaw) * 0.5);
      }
   }

   public boolean isCurrentViewEntity() {
      return this.mc.getRenderViewEntity() == this;
   }

   @Override
   public void onLivingUpdate() {
      if (CheatBreaker.getInstance().getModManager().toggleSprintMod.isEnabled()) {
         this.run();
      } else {
         if (this.sprintingTicksLeft > 0) {
            this.sprintingTicksLeft--;
            if (this.sprintingTicksLeft == 0) {
               this.setSprinting(false);
            }
         }

         if (this.sprintToggleTimer > 0) {
            this.sprintToggleTimer--;
         }

         this.prevTimeInPortal = this.timeInPortal;
         if (this.inPortal) {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
               this.mc.displayGuiScreen(null);
            }

            if (this.timeInPortal == 0.0F) {
               this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4F + 0.8F));
            }

            this.timeInPortal += 0.0125F;
            if (this.timeInPortal >= 1.0F) {
               this.timeInPortal = 1.0F;
            }

            this.inPortal = false;
         } else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
            this.timeInPortal += 0.006666667F;
            if (this.timeInPortal > 1.0F) {
               this.timeInPortal = 1.0F;
            }
         } else {
            if (this.timeInPortal > 0.0F) {
               this.timeInPortal -= 0.05F;
            }

            if (this.timeInPortal < 0.0F) {
               this.timeInPortal = 0.0F;
            }
         }

         if (this.timeUntilPortal > 0) {
            this.timeUntilPortal--;
         }

         boolean jump = this.movementInput.jump;
         boolean sneak = this.movementInput.sneak;
         float f = 0.8F;
         boolean flag = this.movementInput.moveForward >= f;
         this.movementInput.updatePlayerMoveState();
         if (this.isUsingItem() && !this.isRiding()) {
            this.movementInput.moveStrafe *= 0.2F;
            this.movementInput.moveForward *= 0.2F;
            this.sprintToggleTimer = 0;
         }

         this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
         this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
         this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
         this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
         boolean flag2 = this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;
         if (this.onGround
            && !sneak
            && !flag
            && this.movementInput.moveForward >= f
            && !this.isSprinting()
            && flag2
            && !this.isUsingItem()
            && !this.isPotionActive(Potion.blindness)) {
            if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
               this.sprintToggleTimer = 7;
            } else {
               this.setSprinting(true);
            }
         }

         if (!this.isSprinting()
            && this.movementInput.moveForward >= f
            && flag2
            && !this.isUsingItem()
            && !this.isPotionActive(Potion.blindness)
            && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
            this.setSprinting(true);
         }

         if (this.isSprinting() && (this.movementInput.moveForward < f || this.isCollidedHorizontally || !flag2)) {
            this.setSprinting(false);
         }

         if (this.capabilities.allowFlying) {
            if (this.mc.playerController.isSpectatorMode()) {
               if (!this.capabilities.isFlying) {
                  this.capabilities.isFlying = true;
                  this.sendPlayerAbilities();
               }
            } else if (!jump && this.movementInput.jump) {
               if (this.flyToggleTimer == 0) {
                  this.flyToggleTimer = 7;
               } else {
                  this.capabilities.isFlying = !this.capabilities.isFlying;
                  this.sendPlayerAbilities();
                  this.flyToggleTimer = 0;
               }
            }
         }

         if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
            if (this.movementInput.sneak) {
               this.motionY = this.motionY - this.capabilities.getFlySpeed() * 3.0F;
            }

            if (this.movementInput.jump) {
               this.motionY = this.motionY + this.capabilities.getFlySpeed() * 3.0F;
            }
         }

         if (this.isRidingHorse()) {
            if (this.horseJumpPowerCounter < 0) {
               this.horseJumpPowerCounter++;
               if (this.horseJumpPowerCounter == 0) {
                  this.horseJumpPower = 0.0F;
               }
            }

            if (jump && !this.movementInput.jump) {
               this.horseJumpPowerCounter = -10;
               this.sendHorseJump();
            } else if (!jump && this.movementInput.jump) {
               this.horseJumpPowerCounter = 0;
               this.horseJumpPower = 0.0F;
            } else if (jump) {
               this.horseJumpPowerCounter++;
               if (this.horseJumpPowerCounter < 10) {
                  this.horseJumpPower = this.horseJumpPowerCounter * 0.1F;
               } else {
                  this.horseJumpPower = 0.8F + 2.0F / (this.horseJumpPowerCounter - 9) * 0.1F;
               }
            }
         } else {
            this.horseJumpPower = 0.0F;
         }

         super.onLivingUpdate();
         if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
            this.capabilities.isFlying = false;
            this.sendPlayerAbilities();
         }
      }
   }

   public void run() {
      if (this.sprintingTicksLeft > 0) {
         this.sprintingTicksLeft--;
         if (this.sprintingTicksLeft == 0) {
            this.setSprinting(false);
         }
      }

      if (this.sprintToggleTimer > 0) {
         this.sprintToggleTimer--;
      }

      this.prevTimeInPortal = this.timeInPortal;
      if (this.inPortal) {
         if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
            this.mc.displayGuiScreen((GuiScreen)null);
         }

         if (this.timeInPortal == 0.0F) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4F + 0.8F));
         }

         this.timeInPortal += 0.0125F;
         if (this.timeInPortal >= 1.0F) {
            this.timeInPortal = 1.0F;
         }

         this.inPortal = false;
      } else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
         this.timeInPortal += 0.006666667F;
         if (this.timeInPortal > 1.0F) {
            this.timeInPortal = 1.0F;
         }
      } else {
         if (this.timeInPortal > 0.0F) {
            this.timeInPortal -= 0.05F;
         }

         if (this.timeInPortal < 0.0F) {
            this.timeInPortal = 0.0F;
         }
      }

      if (this.timeUntilPortal > 0) {
         this.timeUntilPortal--;
      }

      boolean jump = this.movementInput.jump;
      boolean sneak = this.movementInput.sneak;
      float f = 0.8F;
      boolean flag = this.movementInput.moveForward >= f;
      ToggleSprintInput.update(this.mc, (MovementInputFromOptions)this.movementInput, this);
      if (this.isUsingItem() && !this.isRiding()) {
         this.movementInput.moveStrafe *= 0.2F;
         this.movementInput.moveForward *= 0.2F;
         this.sprintToggleTimer = 0;
      }

      this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
      this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
      this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
      this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
      boolean flag2 = this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;
      boolean flag3 = !CheatBreaker.getInstance().getModManager().toggleSprintMod.isEnabled() || !(Boolean)ToggleSprintMod.toggleSprint.getValue();
      boolean value = (Boolean)ToggleSprintMod.doubleTap.getValue();
      if (ToggleSprintMod.flag) {
         this.setSprinting(false);
         this.toggleSprintInput.setSprintState(false, false);
         ToggleSprintMod.flag = false;
      }

      if (flag3) {
         if ((Boolean)ToggleSprintMod.doubleTap.getValue()
            && this.onGround
            && !sneak
            && !flag
            && this.movementInput.moveForward >= f
            && !this.isSprinting()
            && flag2
            && !this.isUsingItem()
            && !this.isPotionActive(Potion.blindness)) {
            if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
               this.sprintToggleTimer = 7;
            } else {
               this.setSprinting(true);
               this.toggleSprintInput.setSprintState(true, false);
            }

            if (!this.isSprinting()
               && this.movementInput.moveForward >= f
               && flag2
               && !this.isUsingItem()
               && !this.isPotionActive(Potion.blindness)
               && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
               this.setSprinting(true);
               this.toggleSprintInput.setSprintState(true, false);
            }
         }
      } else {
         boolean sprintToggled = ToggleSprintInput.sprintToggled;
         if (flag2 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness) && !ToggleSprintInput.sprintHeldLong && (!value || !this.isSprinting())) {
            this.setSprinting(sprintToggled);
         }

         if (value
            && !sprintToggled
            && this.onGround
            && !flag
            && this.movementInput.moveForward >= f
            && !this.isSprinting()
            && flag2
            && !this.isUsingItem()
            && !this.isPotionActive(Potion.blindness)) {
            if (this.sprintToggleTimer == 0) {
               this.sprintToggleTimer = 7;
            } else {
               this.setSprinting(true);
               this.toggleSprintInput.setSprintState(true, true);
               this.sprintToggleTimer = 0;
            }
         }
      }

      if (this.isSprinting() && (this.movementInput.moveForward < f || this.isCollidedHorizontally || !flag2)) {
         this.setSprinting(false);
         if (ToggleSprintInput.sprintHeldLong || flag3 || ToggleSprintInput.sprintLocked || this.isRiding()) {
            this.toggleSprintInput.setSprintState(false, false);
         }
      }

      if (ToggleSprintMod.flyBoost.getBooleanValue()
         && this.capabilities.isFlying
         && this.mc.gameSettings.keyBindSprint.isKeyDown()
         && this.capabilities.isCreativeMode) {
         this.capabilities.setFlySpeed(ToggleSprintMod.floatValue * ToggleSprintMod.flyBoostAmount.getFloatValue());
         if (this.movementInput.sneak) {
            this.motionY = this.motionY - 0.15 * ToggleSprintMod.flyBoostAmount.getFloatValue();
         }

         if (this.movementInput.jump) {
            this.motionY = this.motionY + 0.15 * ToggleSprintMod.flyBoostAmount.getFloatValue();
         }
      } else if (this.capabilities.getFlySpeed() != ToggleSprintMod.floatValue) {
         this.capabilities.setFlySpeed(ToggleSprintMod.floatValue);
      }

      if (this.capabilities.allowFlying) {
         if (this.mc.playerController.isSpectatorMode()) {
            if (!this.capabilities.isFlying) {
               this.capabilities.isFlying = true;
               this.sendPlayerAbilities();
            }
         } else if (!jump && this.movementInput.jump) {
            if (this.flyToggleTimer == 0) {
               this.flyToggleTimer = 7;
            } else {
               this.capabilities.isFlying = !this.capabilities.isFlying;
               this.sendPlayerAbilities();
               this.flyToggleTimer = 0;
            }
         }
      }

      if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
         if (this.movementInput.sneak) {
            this.motionY = this.motionY - this.capabilities.getFlySpeed() * 3.0F;
         }

         if (this.movementInput.jump) {
            this.motionY = this.motionY + this.capabilities.getFlySpeed() * 3.0F;
         }
      }

      if (this.isRidingHorse()) {
         if (this.horseJumpPowerCounter < 0) {
            this.horseJumpPowerCounter++;
            if (this.horseJumpPowerCounter == 0) {
               this.horseJumpPower = 0.0F;
            }
         }

         if (jump && !this.movementInput.jump) {
            this.horseJumpPowerCounter = -10;
            this.sendHorseJump();
         } else if (!jump && this.movementInput.jump) {
            this.horseJumpPowerCounter = 0;
            this.horseJumpPower = 0.0F;
         } else if (jump) {
            this.horseJumpPowerCounter++;
            if (this.horseJumpPowerCounter < 10) {
               this.horseJumpPower = this.horseJumpPowerCounter * 0.1F;
            } else {
               this.horseJumpPower = 0.8F + 2.0F / (this.horseJumpPowerCounter - 9) * 0.1F;
            }
         }
      } else {
         this.horseJumpPower = 0.0F;
      }

      super.onLivingUpdate();
      if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
         this.capabilities.isFlying = false;
         this.sendPlayerAbilities();
      }
   }

   public void handleString(String str) {
      ChatComponentText chatComponentText = new ChatComponentText(
         EnumChatFormatting.RED + "[C" + EnumChatFormatting.WHITE + "B" + EnumChatFormatting.RED + "] " + EnumChatFormatting.RESET
      );
      ChatComponentText chatComponentText2 = new ChatComponentText(EnumChatFormatting.GRAY + "Invalid Emote ID: " + str);
      chatComponentText.appendSibling(chatComponentText2);
      Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(chatComponentText);
   }
}
