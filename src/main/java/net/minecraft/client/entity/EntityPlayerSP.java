package net.minecraft.client.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.FloatEvent9;
import com.cheatbreaker.client.event.SimpleEvent16;
import com.cheatbreaker.client.event.SimpleEvent48;
import com.cheatbreaker.client.mod.impl.ToggleSprintInput;
import com.cheatbreaker.client.mod.impl.ToggleSprintMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.world.World;

public class EntityPlayerSP extends AbstractClientPlayer {
    public MovementInput movementInput;
    public MouseFilter mouseFilter = new MouseFilter();
    public int sprintToggleTimer;
    public int sprintingTicksLeft;
    public float renderArmYaw;
    public float renderArmPitch;
    public float prevRenderArmYaw;
    public float prevRenderArmPitch;
    public int horseJumpPowerCounter;
    public float horseJumpPower;
    public MouseFilter mouseFilter2 = new MouseFilter();
    public MouseFilter mouseFilter3 = new MouseFilter();
    public ToggleSprintInput toggleSprintInput = new ToggleSprintInput(Minecraft.getMinecraft().gameSettings);
    public float timeInPortal;
    public float prevTimeInPortal;
    public Minecraft mc;

    public EntityPlayerSP(Minecraft mc, World world, Session session, int n) {
        super(world, session.getProfile());
        this.mc = mc;
        this.dimension = n;
    }

    @Override
    public void updateEntityActionState() {
        super.updateEntityActionState();
        this.moveStrafing = this.movementInput.moveStrafe;
        this.moveForward = this.movementInput.moveForward;
        this.isJumping = this.movementInput.jump;
        this.prevRenderArmYaw = this.renderArmYaw;
        this.prevRenderArmPitch = this.renderArmPitch;
        this.renderArmPitch = (float)(this.renderArmPitch + (this.rotationPitch - this.renderArmPitch) * 0.5);
        this.renderArmYaw = (float)(this.renderArmYaw + (this.rotationYaw - this.renderArmYaw) * 0.5);
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        SimpleEvent16 simpleEvent16 = new SimpleEvent16();
        CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent16);
        if (!simpleEvent16.isCancelled()) {
            if (this.sprintingTicksLeft > 0) {
                this.sprintingTicksLeft--;
                if (this.sprintingTicksLeft == 0) {
                    this.setSprinting(false);
                }
            }

            if (this.sprintToggleTimer > 0) {
                this.sprintToggleTimer--;
            }

            if (this.mc.playerController.enableEverythingIsScrewedUpMode()) {
                this.posX = this.posZ = 0.5;
                this.posX = 0.0;
                this.posZ = 0.0;
                this.rotationYaw = this.ticksExisted / 12.0F;
                this.rotationPitch = 10.0F;
                this.posY = 68.5;
            } else {
                this.prevTimeInPortal = this.timeInPortal;
                if (this.inPortal) {
                    if (this.mc.currentScreen != null) {
                        this.mc.displayGuiScreen(null);
                    }

                    if (this.timeInPortal == 0.0F) {
                        this.mc
                            .getSoundHandler()
                            .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4F + 0.8F));
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
                float f = 0.8F;
                boolean flag = this.movementInput.moveForward >= f;
                this.movementInput.updatePlayerMoveState();
                if (this.isUsingItem() && !this.isRiding()) {
                    this.movementInput.moveStrafe *= 0.2F;
                    this.movementInput.moveForward *= 0.2F;
                    this.sprintToggleTimer = 0;
                }

                if (this.movementInput.sneak && this.yOffset2 < 0.2F) {
                    this.yOffset2 = 0.2F;
                }

                this.pushOutOfBlocks(this.posX - this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ + this.width * 0.35);
                this.pushOutOfBlocks(this.posX - this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ - this.width * 0.35);
                this.pushOutOfBlocks(this.posX + this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ - this.width * 0.35);
                this.pushOutOfBlocks(this.posX + this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ + this.width * 0.35);
                boolean flag2 = this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;
                if (this.onGround
                    && !flag
                    && this.movementInput.moveForward >= f
                    && !this.isSprinting()
                    && flag2
                    && !this.isUsingItem()
                    && !this.isPotionActive(Potion.blindness)) {
                    if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.getIsKeyPressed()) {
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
                    && this.mc.gameSettings.keyBindSprint.getIsKeyPressed()) {
                    this.setSprinting(true);
                }

                if (this.isSprinting() && (this.movementInput.moveForward < f || this.isCollidedHorizontally || !flag2)) {
                    this.setSprinting(false);
                }

                if (this.capabilities.allowFlying && !jump && this.movementInput.jump) {
                    if (this.flyToggleTimer == 0) {
                        this.flyToggleTimer = 7;
                    } else {
                        this.capabilities.isFlying = !this.capabilities.isFlying;
                        this.sendPlayerAbilities();
                        this.flyToggleTimer = 0;
                    }
                }

                if (this.capabilities.isFlying) {
                    if (this.movementInput.sneak) {
                        this.motionY -= 0.15;
                    }

                    if (this.movementInput.jump) {
                        this.motionY += 0.15;
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
                if (this.onGround && this.capabilities.isFlying) {
                    this.capabilities.isFlying = false;
                    this.sendPlayerAbilities();
                }
            }
        }
    }

    public float getFOVMultiplier() {
        float f = 1.0F;
        if (this.capabilities.isFlying) {
            f *= 1.1F;
        }

        SimpleEvent48 simpleEvent48 = new SimpleEvent48();
        CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent48);
        if (!simpleEvent48.isCancelled()) {
            IAttributeInstance entityAttribute = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            f = (float)(f * ((entityAttribute.getAttributeValue() / this.capabilities.getWalkSpeed() + 1.0) / 2.0));
        }

        if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
            f = 1.0F;
        }

        if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow) {
            int itemInUseDuration = this.getItemInUseDuration();
            float f2 = itemInUseDuration / 20.0F;
            if (f2 > 1.0F) {
                f2 = 1.0F;
            } else {
                f2 *= f2;
            }

            FloatEvent9 floatEvent9 = new FloatEvent9(0.15F);
            CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent9);
            f *= 1.0F - f2 * floatEvent9.getFloatValue();
        }

        return f;
    }

    @Override
    public void closeScreen() throws java.io.IOException {
        super.closeScreen();
        if (CheatBreaker.getInstance().getKeybindSettings().guiBlur.getBooleanValue()) {
            this.mc.entityRenderer.stopUseShader();
        }

        this.mc.displayGuiScreen(null);
    }

    @Override
    public void displayGUIEditSign(TileEntity tileEntity) throws java.io.IOException {
        if (tileEntity instanceof TileEntitySign) {
            this.mc.displayGuiScreen(new GuiEditSign((TileEntitySign)tileEntity));
        } else if (tileEntity instanceof TileEntityCommandBlock) {
            this.mc.displayGuiScreen(new GuiCommandBlock(((TileEntityCommandBlock)tileEntity).getCommandExecutor()));
        }
    }

    @Override
    public void handleCommandBlockLogic(CommandBlockLogic commandBlockLogic) throws java.io.IOException {
        this.mc.displayGuiScreen(new GuiCommandBlock(commandBlockLogic));
    }

    @Override
    public void displayGUIBook(ItemStack itemStack) throws java.io.IOException {
        Item item = itemStack.getItem();
        if (item == Items.written_book) {
            this.mc.displayGuiScreen(new GuiScreenBook(this, itemStack, false));
        } else if (item == Items.writable_book) {
            this.mc.displayGuiScreen(new GuiScreenBook(this, itemStack, true));
        }
    }

    @Override
    public void displayGUIChest(IInventory inventory) throws java.io.IOException {
        this.mc.displayGuiScreen(new GuiChest(this.inventory, inventory));
    }

    @Override
    public void handleTileEntityHopper(TileEntityHopper tileEntityHopper) throws java.io.IOException {
        this.mc.displayGuiScreen(new GuiHopper(this.inventory, tileEntityHopper));
    }

    @Override
    public void displayGUIHopperMinecart(EntityMinecartHopper entityMinecartHopper) throws java.io.IOException {
        this.mc.displayGuiScreen(new GuiHopper(this.inventory, entityMinecartHopper));
    }

    @Override
    public void displayGUIHorse(EntityHorse entityHorse, IInventory inventory) throws java.io.IOException {
        this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, inventory, entityHorse));
    }

    @Override
    public void displayGUIWorkbench(int n, int n2, int n3) throws java.io.IOException {
        this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj, n, n2, n3));
    }

    @Override
    public void displayGUIEnchantment(int n, int n2, int n3, String str) throws java.io.IOException {
        this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, n, n2, n3, str));
    }

    @Override
    public void displayGUIAnvil(int n, int n2, int n3) throws java.io.IOException {
        this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.worldObj, n, n2, n3));
    }

    @Override
    public void handleTileEntityFurnace(TileEntityFurnace tileEntityFurnace) throws java.io.IOException {
        this.mc.displayGuiScreen(new GuiFurnace(this.inventory, tileEntityFurnace));
    }

    @Override
    public void handleTileEntityBrewingStand(TileEntityBrewingStand tileEntityBrewingStand) throws java.io.IOException {
        this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, tileEntityBrewingStand));
    }

    @Override
    public void handleTileEntityBeacon(TileEntityBeacon tileEntityBeacon) throws java.io.IOException {
        this.mc.displayGuiScreen(new GuiBeacon(this.inventory, tileEntityBeacon));
    }

    @Override
    public void handleTileEntityDispenser(TileEntityDispenser tileEntityDispenser) throws java.io.IOException {
        this.mc.displayGuiScreen(new GuiDispenser(this.inventory, tileEntityDispenser));
    }

    @Override
    public void displayGUIMerchant(IMerchant merchant, String str) throws java.io.IOException {
        this.mc.displayGuiScreen(new GuiMerchant(this.inventory, merchant, this.worldObj, str));
    }

    @Override
    public void onCriticalHit(Entity entity) {
        this.mc.effectRenderer.addEffect(new EntityCrit2FX(this.mc.theWorld, entity));
    }

    @Override
    public void onEnchantmentCritical(Entity entity) {
        EntityCrit2FX entityCrit2FX = new EntityCrit2FX(this.mc.theWorld, entity, "magicCrit");
        this.mc.effectRenderer.addEffect(entityCrit2FX);
    }

    @Override
    public void onItemPickup(Entity entity, int n) {
        this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, entity, this, -0.5F));
    }

    @Override
    public boolean isSneaking() {
        return this.movementInput.sneak && !this.sleeping;
    }

    public void setPlayerSPHealth(float f) {
        float f2 = this.getHealth() - f;
        if (f2 <= 0.0F) {
            this.setHealth(f);
            if (f2 < 0.0F) {
                this.hurtResistantTime = this.maxHurtResistantTime / 2;
            }
        } else {
            this.lastDamage = f2;
            this.setHealth(this.getHealth());
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.damageEntity(DamageSource.generic, f2);
            this.hurtTime = this.maxHurtTime = 10;
        }
    }

    @Override
    public void addChatComponentMessage(IChatComponent chatComponent) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
    }

    public boolean isBlockNormal(int n, int n2, int n3) {
        return this.worldObj.getBlock(n, n2, n3).isNormalCube();
    }

    @Override
    public boolean pushOutOfBlocks(double x, double y, double z) {
        int n = MathHelper.floor_double(x);
        int n2 = MathHelper.floor_double(y);
        int n3 = MathHelper.floor_double(z);
        double d = x - n;
        double d2 = z - n3;
        if (this.isBlockNormal(n, n2, n3) || this.isBlockNormal(n, n2 + 1, n3)) {
            boolean flag = !this.isBlockNormal(n - 1, n2, n3) && !this.isBlockNormal(n - 1, n2 + 1, n3);
            boolean flag2 = !this.isBlockNormal(n + 1, n2, n3) && !this.isBlockNormal(n + 1, n2 + 1, n3);
            boolean flag3 = !this.isBlockNormal(n, n2, n3 - 1) && !this.isBlockNormal(n, n2 + 1, n3 - 1);
            boolean flag4 = !this.isBlockNormal(n, n2, n3 + 1) && !this.isBlockNormal(n, n2 + 1, n3 + 1);
            byte b = -1;
            double d3 = 9999.0;
            if (flag && d < d3) {
                d3 = d;
                b = 0;
            }

            if (flag2 && 1.0 - d < d3) {
                d3 = 1.0 - d;
                b = 1;
            }

            if (flag3 && d2 < d3) {
                d3 = d2;
                b = 4;
            }

            if (flag4 && 1.0 - d2 < d3) {
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

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        this.sprintingTicksLeft = sprinting ? 600 : 0;
    }

    public void setXPStats(float f, int n, int n2) {
        this.experience = f;
        this.experienceTotal = n;
        this.experienceLevel = n2;
    }

    @Override
    public void addChatMessage(IChatComponent message) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(message);
    }

    @Override
    public boolean canCommandSenderUseCommand(int permissionLevel, String command) {
        return permissionLevel <= 0;
    }

    @Override
    public ChunkCoordinates getCommandSenderPosition() {
        return new ChunkCoordinates(MathHelper.floor_double(this.posX + 0.5), MathHelper.floor_double(this.posY + 0.5), MathHelper.floor_double(this.posZ + 0.5));
    }

    @Override
    public void playSound(String name, float volume, float pitch) {
        this.worldObj.playSound(this.posX, this.posY - this.yOffset, this.posZ, name, volume, pitch, false);
    }

    @Override
    public boolean isServerWorld() {
        return true;
    }

    public boolean isRidingHorse() {
        return this.ridingEntity != null && this.ridingEntity instanceof EntityHorse;
    }

    public float getHorseJumpPower() {
        return this.horseJumpPower;
    }

    public void sendHorseJump() {
    }

    public void run10() throws java.io.IOException {
        if (this.sprintingTicksLeft > 0) {
            this.sprintingTicksLeft--;
            if (this.sprintingTicksLeft == 0) {
                this.setSprinting(false);
            }
        }

        if (this.sprintToggleTimer > 0) {
            this.sprintToggleTimer--;
        }

        if (this.mc.playerController.enableEverythingIsScrewedUpMode()) {
            this.posX = this.posZ = 0.5;
            this.posX = 0.0;
            this.posZ = 0.0;
            this.rotationYaw = this.ticksExisted / 12.0F;
            this.rotationPitch = 10.0F;
            this.posY = 68.5;
        } else {
            this.prevTimeInPortal = this.timeInPortal;
            if (this.inPortal) {
                if (this.mc.currentScreen != null) {
                    this.mc.displayGuiScreen(null);
                }

                if (this.timeInPortal == 0.0F) {
                    this.mc
                        .getSoundHandler()
                        .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("portal.trigger"), this.getRNG().nextFloat() * 0.4F + 0.8F));
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
            float f = 0.8F;
            boolean flag = this.movementInput.moveForward >= f;
            ToggleSprintInput.update(this.mc, (MovementInputFromOptions)this.movementInput, this);
            if (this.isUsingItem() && !this.isRiding()) {
                this.movementInput.moveStrafe *= 0.2F;
                this.movementInput.moveForward *= 0.2F;
                this.sprintToggleTimer = 0;
            }

            if (this.movementInput.sneak && this.yOffset2 < 0.2F) {
                this.yOffset2 = 0.2F;
            }

            this.pushOutOfBlocks(this.posX - this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ + this.width * 0.35);
            this.pushOutOfBlocks(this.posX - this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ - this.width * 0.35);
            this.pushOutOfBlocks(this.posX + this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ - this.width * 0.35);
            this.pushOutOfBlocks(this.posX + this.width * 0.35, this.boundingBox.minY + 0.5, this.posZ + this.width * 0.35);
            boolean flag2 = this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.isFlying;
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
                    && !flag
                    && this.movementInput.moveForward >= f
                    && !this.isSprinting()
                    && flag2
                    && !this.isUsingItem()
                    && !this.isPotionActive(Potion.blindness)) {
                    if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.getIsKeyPressed()) {
                        this.sprintToggleTimer = 7;
                    } else {
                        this.setSprinting(true);
                        this.toggleSprintInput.setSprintState(true, false);
                    }
                }

                if (!this.isSprinting()
                    && this.movementInput.moveForward >= f
                    && flag2
                    && !this.isUsingItem()
                    && !this.isPotionActive(Potion.blindness)
                    && this.mc.gameSettings.keyBindSprint.getIsKeyPressed()) {
                    this.setSprinting(true);
                    this.toggleSprintInput.setSprintState(true, false);
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

            if ((Boolean)ToggleSprintMod.flyBoost.getValue()
                && this.capabilities.isFlying
                && this.mc.gameSettings.keyBindSprint.getIsKeyPressed()
                && this.capabilities.isCreativeMode) {
                this.capabilities.setFlySpeed(ToggleSprintMod.floatValue * (Float)ToggleSprintMod.flyBoostAmount.getValue());
                if (this.movementInput.sneak) {
                    this.motionY = this.motionY - 0.15 * ((Float)ToggleSprintMod.flyBoostAmount.getValue()).floatValue();
                }

                if (this.movementInput.jump) {
                    this.motionY = this.motionY + 0.15 * ((Float)ToggleSprintMod.flyBoostAmount.getValue()).floatValue();
                }
            } else if (this.capabilities.getFlySpeed() != ToggleSprintMod.floatValue) {
                this.capabilities.setFlySpeed(ToggleSprintMod.floatValue);
            }

            if (this.capabilities.allowFlying && !jump && this.movementInput.jump) {
                if (this.flyToggleTimer == 0) {
                    this.flyToggleTimer = 7;
                } else {
                    this.capabilities.isFlying = !this.capabilities.isFlying;
                    this.sendPlayerAbilities();
                    this.flyToggleTimer = 0;
                }
            }

            if (this.capabilities.isFlying) {
                if (this.movementInput.sneak) {
                    this.motionY -= 0.15;
                }

                if (this.movementInput.jump) {
                    this.motionY += 0.15;
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
                    this.horseJumpPowerCounter -= 10;
                    this.horseJumpPowerCounter = -10;
                    ((EntityClientPlayerMP)this).sendQueue.addToSendQueue(new C0BPacketEntityAction(this, 6, (int)(this.getHorseJumpPower() * 100.0F)));
                } else if (!jump && this.movementInput.jump) {
                    this.horseJumpPowerCounter = 0;
                    this.horseJumpPower = 0.0F;
                } else if (jump) {
                    this.horseJumpPowerCounter++;
                    this.horseJumpPower = this.horseJumpPowerCounter < 10
                        ? this.horseJumpPowerCounter * 0.1F
                        : 0.8F + 2.0F / (this.horseJumpPowerCounter - 9) * 0.1F;
                }
            } else {
                this.horseJumpPower = 0.0F;
            }

            super.onLivingUpdate();
            if (this.onGround && this.capabilities.isFlying) {
                this.capabilities.isFlying = false;
                this.sendPlayerAbilities();
            }
        }
    }
}
