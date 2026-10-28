package net.minecraft.client.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SetClientBrandEvent;
import com.cheatbreaker.client.event.StringEvent1;
import com.cheatbreaker.client.mod.impl.TrimpMod;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Session;
import net.minecraft.world.World;

public class EntityClientPlayerMP extends EntityPlayerSP {
    public List list = new ArrayList();
    public NetHandlerPlayClient sendQueue;
    public double oldPosX;
    public double oldMinY;
    public double oldPosY;
    public double oldPosZ;
    public float oldRotationYaw;
    public float oldRotationPitch;
    public boolean wasOnGround;
    public boolean wasSneaking;
    public boolean wasSprinting;
    public int ticksSinceMovePacket;
    public boolean hasSetHealth;
    public String clientBrand;
    public StatFileWriter statFileWriter;
    public boolean flag3;

    public EntityClientPlayerMP(Minecraft mc, World world, Session session, NetHandlerPlayClient netHandlerPlayClient, StatFileWriter statFileWriter) {
        super(mc, world, session, 0);
        this.sendQueue = netHandlerPlayClient;
        this.statFileWriter = statFileWriter;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void heal(float f) {
    }

    @Override
    public void mountEntity(Entity entityIn) {
        super.mountEntity(entityIn);
        if (entityIn instanceof EntityMinecart) {
            this.mc.getSoundHandler().playSound(new MovingSoundMinecartRiding(this, (EntityMinecart)entityIn));
        }
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        if (this.worldObj.blockExists(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ))) {
            super.onUpdate();
            if (this.isRiding()) {
                this.sendQueue.addToSendQueue(new C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
                this.sendQueue.addToSendQueue(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            } else {
                this.sendMotionUpdates();
            }
        }
    }

    public void sendMotionUpdates() {
        boolean isSprinting = this.isSprinting();
        if (isSprinting != this.wasSprinting) {
            if (isSprinting) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, 4));
            } else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, 5));
            }

            this.wasSprinting = isSprinting;
        }

        boolean isSneaking = this.isSneaking();
        if (isSneaking != this.wasSneaking) {
            if (isSneaking) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, 1));
            } else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, 2));
            }

            this.wasSneaking = isSneaking;
        }

        double d = this.posX - this.oldPosX;
        double d2 = this.boundingBox.minY - this.oldMinY;
        double d3 = this.posZ - this.oldPosZ;
        double d4 = this.rotationYaw - this.oldRotationYaw;
        double d5 = this.rotationPitch - this.oldRotationPitch;
        boolean flag = d * d + d2 * d2 + d3 * d3 > 9.0E-4 || this.ticksSinceMovePacket >= 20;
        boolean flag2 = d4 != 0.0 || d5 != 0.0;
        if (this.ridingEntity != null) {
            this.sendQueue
                .addToSendQueue(new C06PacketPlayerPosLook(this.motionX, -999.0, -999.0, this.motionZ, this.rotationYaw, this.rotationPitch, this.onGround));
            flag = false;
        } else if (flag && flag2) {
            this.sendQueue
                .addToSendQueue(
                    new C06PacketPlayerPosLook(this.posX, this.boundingBox.minY, this.posY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround)
                );
        } else if (flag) {
            this.sendQueue.addToSendQueue(new C04PacketPlayerPosition(this.posX, this.boundingBox.minY, this.posY, this.posZ, this.onGround));
        } else if (flag2) {
            this.sendQueue.addToSendQueue(new C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
        } else {
            this.sendQueue.addToSendQueue(new C03PacketPlayer(this.onGround));
        }

        this.ticksSinceMovePacket++;
        this.wasOnGround = this.onGround;
        if (flag) {
            this.oldPosX = this.posX;
            this.oldMinY = this.boundingBox.minY;
            this.oldPosY = this.posY;
            this.oldPosZ = this.posZ;
            this.ticksSinceMovePacket = 0;
        }

        if (flag2) {
            this.oldRotationYaw = this.rotationYaw;
            this.oldRotationPitch = this.rotationPitch;
        }
    }

    @Override
    public EntityItem dropOneItem(boolean flag) {
        int n = flag ? 3 : 4;
        this.sendQueue.addToSendQueue(new C07PacketPlayerDigging(n, 0, 0, 0, 0));
        return null;
    }

    @Override
    public void joinEntityItemWithWorld(EntityItem entityItem) {
    }

    public void sendChatMessage(String str) {
        StringEvent1 stringEvent1 = new StringEvent1(str);
        CheatBreaker.getInstance().getEventBus().handleEvent(stringEvent1);
        if (!stringEvent1.isCancelled()) {
            this.sendQueue.addToSendQueue(new C01PacketChatMessage(str));
        }
    }

    @Override
    public void swingItem() {
        super.swingItem();
        this.sendQueue.addToSendQueue(new C0APacketAnimation(this, 1));
    }

    @Override
    public void respawnPlayer() {
        this.sendQueue.addToSendQueue(new C16PacketClientStatus(EnumState.PERFORM_RESPAWN));
    }

    @Override
    public void damageEntity(DamageSource damageSource, float f) {
        if (!this.isEntityInvulnerable()) {
            this.setHealth(this.getHealth() - f);
        }
    }

    @Override
    public void closeScreen() throws java.io.IOException {
        this.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.openContainer.windowId));
        this.closeScreenNoPacket();
    }

    public void closeScreenNoPacket() throws java.io.IOException {
        this.inventory.setItemStack(null);
        super.closeScreen();
    }

    @Override
    public void setPlayerSPHealth(float f) {
        if (this.hasSetHealth) {
            super.setPlayerSPHealth(f);
        } else {
            this.setHealth(f);
            this.hasSetHealth = true;
        }
    }

    @Override
    public void addStat(StatBase statBase, int n) {
        if (statBase != null && statBase.isIndependent) {
            super.addStat(statBase, n);
        }
    }

    @Override
    public void sendPlayerAbilities() {
        this.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(this.capabilities));
    }

    @Override
    public void sendHorseJump() {
        this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, 6, (int)(this.getHorseJumpPower() * 100.0F)));
    }

    public void sendHorseInteraction() {
        this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, 7));
    }

    public void setClientBrand(String brand) {
        CheatBreaker.getInstance().getEventBus().handleEvent(new SetClientBrandEvent(brand));
        this.clientBrand = brand;
    }

    public String getClientBrand() {
        return this.clientBrand;
    }

    public StatFileWriter getStatFileWriter() {
        return this.statFileWriter;
    }

    public boolean check() {
        float f;
        double d;
        if ((Boolean)TrimpMod.trimp.getValue() && this.isSneaking() && (d = this.sqrt_double()) > (f = this.getFloatValue4())) {
            double d2 = d / f * 0.5;
            if (d2 > 1.0) {
                d2 = 1.0;
            }

            this.motionY = this.motionY + d2 * d * ((Float)TrimpMod.trimpMultiplier.getValue()).floatValue();
            if ((Float)TrimpMod.trimpMultiplier.getValue() > 0.0F) {
                float f2 = 1.0F / (Float)TrimpMod.trimpMultiplier.getValue();
                this.motionX *= f2;
                this.motionZ *= f2;
            }

            this.process10(30);
            return true;
        } else {
            return false;
        }
    }

    public void run9() {
        this.flag3 = false;
    }

    public float getFloatValue() {
        float f = 0.91F;
        if (this.onGround) {
            f = 0.54600006F;
            Block block = this.worldObj
                .getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
            if (block != null) {
                f = block.slipperiness * 0.91F;
            }
        }

        return f;
    }

    public float getFloatValue2() {
        float aIMoveSpeed = this.getAIMoveSpeed();
        return !this.isSneaking() ? aIMoveSpeed * 2.15F : aIMoveSpeed * 1.11F;
    }

    @Override
    public void moveEntityWithHeading(float f, float f2) {
        if (TrimpMod.isActive()) {
            double posX = this.posX;
            double posY = this.posY;
            double posZ = this.posZ;
            if (this.capabilities.isFlying && this.ridingEntity == null) {
                super.moveEntityWithHeading(f, f2);
            } else {
                this.process3(f, f2);
            }

            this.addMovementStat(this.posX - posX, this.posY - posY, this.posZ - posZ);
        } else {
            super.moveEntityWithHeading(f, f2);
        }
    }

    public double sqrt_double() {
        return MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
    }

    @Override
    public void jump() {
        if (TrimpMod.isActive()) {
            if (this.isSprinting()) {
                float f = this.rotationYaw * (float) (Math.PI / 180.0);
                this.motionX = this.motionX + MathHelper.sin(f) * 0.2F;
                this.motionZ = this.motionZ - MathHelper.cos(f) * 0.2F;
            }

            this.run3();
            this.flag3 = true;
        }

        super.jump();
    }

    public void process(float f, float f2) {
        if ((!this.isInWater() || this.capabilities.isFlying) && (!this.handleLavaMovement() || this.capabilities.isFlying)) {
            float floatValue = this.getFloatValue();
            this.moveFlying(f, f2, this.getFloatValue5());
            this.run7();
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.run6();
            this.run4();
            this.process(floatValue);
            this.run8();
        } else {
            super.moveEntityWithHeading(f, f2);
        }
    }

    public void process(float f, double d, double d2, double d3) {
        float f2 = f;
        float f3 = (float)((Double)TrimpMod.maxAirAccelPerTick.getValue()).doubleValue();
        if (f2 > f3) {
            f2 = f3;
        }

        double d4;
        if (!((d4 = f2 - (this.motionX * d + this.motionZ * d2)) <= 0.0)) {
            double d5 = d3 * f * 0.05F;
            if (d5 > d4) {
                d5 = d4;
            }

            this.motionX += d5 * d;
            this.motionZ += d5 * d2;
        }
    }

    public void process(double d) {
        this.motionX *= d;
        this.motionY *= d;
        this.motionZ *= d;
    }

    public void process(float f, float f2, double d, double d2, double d3) {
        float f3 = f - f2;
        if (f3 > 0.0F) {
            float f4 = (float)(d3 * f * 0.05F);
            if (f4 > f3) {
                f4 = f3;
            }

            this.motionX += f4 * d;
            this.motionZ += f4 * d2;
        }
    }

    public void run2() {
        this.isJumping = false;
        if (!this.list.isEmpty()) {
            this.list.clear();
        }
    }

    public void run3() {
        this.process3(this.getFloatValue4());
        boolean check = this.check();
        if (!check) {
            this.process2(this.getFloatValue4());
        }
    }

    public void process10(int n) {
        int n2 = MathHelper.floor_double(this.posX);
        int n3;
        int n4;
        Block block = this.worldObj.getBlock(n2, n4 = MathHelper.floor_double(this.posY - 0.2F - this.yOffset), n3 = MathHelper.floor_double(this.posZ));
        if (block != null && block.getMaterial() != Material.air) {
            for (int i = 0; i < n; i++) {
                this.worldObj
                    .spawnParticle(
                        "blockcrack_" + Block.getIdFromBlock(block) + "_" + this.worldObj.getBlockMetadata(n2, n4, n3),
                        this.posX + (new Random().nextFloat() - 0.5) * this.width,
                        this.boundingBox.minY + 0.1,
                        this.posZ + (new Random().nextFloat() - 0.5) * this.width,
                        -this.motionX * 4.0,
                        1.5,
                        -this.motionZ * 4.0
                    );
            }
        }
    }

    @Override
    public void moveFlying(float f, float f2, float f3) {
        if (TrimpMod.isActive()) {
            if (this.capabilities.isFlying && this.ridingEntity == null || this.isInWater()) {
                super.moveFlying(f, f2, f3);
                return;
            }

            float[] floatArray = this.getFloatArray(f, f2);
            float f4;
            float[] floats = new float[]{floatArray[0] * (f4 = f3 * 2.15F), floatArray[1] * f4};
            this.list.add(floats);
        } else {
            super.moveFlying(f, f2, f3);
        }
    }

    public void run4() {
        this.motionY = this.worldObj.isRemote
                && (
                    !this.worldObj.blockExists((int)this.posX, 0, (int)this.posZ)
                        || !this.worldObj.getChunkFromBlockCoords((int)this.posX, (int)this.posZ).isChunkLoaded
                )
            ? (this.posY > 0.0 ? -0.1 : 0.0)
            : (this.motionY -= 0.08);
        this.motionY *= 0.98;
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        if (TrimpMod.isActive()) {
            this.run2();
        }

        super.onLivingUpdate();
    }

    public void run5() {
        double d = this.sqrt_double();
        if (!(d <= 0.0)) {
            float f = 0.0F;
            float f2 = 0.005F;
            double max = Math.max(d, f2);
            double d2 = f;
            float f3 = 1.0F;
            float f4;
            double d3 = d - (float)(d2 + max * (f4 = 1.0F * this.getFloatValue3()) * 0.05F);
            if (d3 < 0.0) {
                d3 = 0.0;
            }

            if (d3 != d) {
                double d4;
                this.motionX = this.motionX * (d4 = d3 / d);
                this.motionZ *= d4;
            }
        }
    }

    public void process(float f) {
        this.motionX *= f;
        this.motionZ *= f;
    }

    public void process2(float f, float f2) {
        double posY = this.posY;
        this.moveFlying(f, f2, 0.04F);
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.8F;
        this.motionY *= 0.8F;
        this.motionZ *= 0.8F;
        this.motionY -= 0.02;
        if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6F - this.posY + posY, this.motionZ)) {
            this.motionY = 0.3;
        }
    }

    public void process3(float f, float f2) {
        if (this.handleLavaMovement() && !this.capabilities.isFlying) {
            super.moveEntityWithHeading(f, f2);
        } else {
            if (this.isInWater() && !this.capabilities.isFlying) {
                if (!(Boolean)TrimpMod.sharking.getValue()) {
                    super.moveEntityWithHeading(f, f2);
                    return;
                }

                this.process4(f, f2);
            } else {
                float f3 = f == 0.0F && f2 == 0.0F ? 0.0F : this.getFloatValue2();
                float[] floatArray = this.getFloatArray(f, f2);
                boolean flag = this.onGround && !this.isJumping;
                float floatValue = this.getFloatValue();
                if (flag) {
                    this.process(floatValue);
                    double value = (Double)TrimpMod.accelerate.getValue();
                    if (f3 != 0.0F) {
                        value *= this.getFloatValue5() * 1.2989583F * 1.6551725F / f3;
                        this.process2(f3, floatArray[0], floatArray[1], value);
                    }

                    if (!this.list.isEmpty()) {
                        float f4 = f3 / this.getFloatValue4();

                        for (float[] floats : (Iterable<float[]>)this.list) {
                            this.motionY = this.motionY + floats[0] * f4;
                            this.motionX = this.motionX + floats[1] * f4;
                        }
                    }
                } else {
                    double value = (Double)TrimpMod.airAccelerate.getValue();
                    this.process(f3, floatArray[0], floatArray[1], value);
                    if ((Boolean)TrimpMod.sharking.getValue() && (Double)TrimpMod.sharkingSurfaceTension.getValue() > 0.0 && this.isJumping && this.motionY < 0.0) {
                        AxisAlignedBB offsetBoundingBox = this.boundingBox.getOffsetBoundingBox(this.motionX, this.motionY, this.motionZ);
                        boolean isAnyLiquid = this.worldObj.isAnyLiquid(offsetBoundingBox);
                        if (isAnyLiquid) {
                            this.motionY = this.motionY * (Double)TrimpMod.sharkingSurfaceTension.getValue();
                        }
                    }
                }

                this.run7();
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.run6();
                this.run4();
            }

            this.run8();
        }
    }

    public void run6() {
        if (this.isCollidedHorizontally && this.isOnLadder()) {
            this.motionY = 0.2;
        }
    }

    public float getFloatValue3() {
        float f = 1.0F;
        if (this.onGround) {
            Block block = this.worldObj
                .getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
            f = 1.0F - block.slipperiness;
        }

        return f;
    }

    public void process4(float f, float f2) {
        double posY = this.posY;
        float f3 = f == 0.0F && f2 == 0.0F ? 0.0F : this.getFloatValue4();
        float[] floatArray = this.getFloatArray(f, f2);
        boolean flag = this.isJumping && this.isOffsetPositionInLiquid(0.0, 1.0, 0.0);
        double d = this.sqrt_double();
        if (flag && !(d < 0.078F)) {
            if (d > 0.09) {
                this.process((Double)TrimpMod.sharkingWaterFriction.getValue());
            }

            if (d > 0.098) {
                this.process(f3, floatArray[0], floatArray[1], (Double)TrimpMod.accelerate.getValue());
            } else {
                this.process2(0.098F, floatArray[0], floatArray[1], (Double)TrimpMod.accelerate.getValue());
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionY = 0.0;
        } else {
            this.process2(f, f2);
        }

        if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6F - this.posY + posY, this.motionZ)) {
            this.motionY = 0.3F;
        }

        if (!this.list.isEmpty()) {
            float f4 = f3 / this.getFloatValue4();

            for (Object obj : this.list) {
                this.motionX = this.motionX + (double)((Float[])obj)[0].floatValue() * f4;
                this.motionZ = this.motionZ + (double)((Float[])obj)[1].floatValue() * f4;
            }
        }
    }

    public void process2(float f) {
        if (!(Boolean)TrimpMod.uncappedBunnyhop.getValue()) {
            float value = (Float)TrimpMod.hardCap.getValue();
            float f2 = (float)this.sqrt_double();
            float f3;
            if (f2 > (f3 = f * value) && f3 != 0.0F) {
                float f4 = f3 / f2;
                this.motionX *= f4;
                this.motionZ *= f4;
                this.process10(30);
            }
        }
    }

    public float getFloatValue4() {
        float aIMoveSpeed = this.getAIMoveSpeed();
        return aIMoveSpeed * 2.15F;
    }

    public void run7() {
        if (this.isOnLadder()) {
            float f = 0.15F;
            if (this.motionX < -f) {
                this.motionX = -f;
            }

            if (this.motionX > f) {
                this.motionX = f;
            }

            if (this.motionZ < -f) {
                this.motionZ = -f;
            }

            if (this.motionZ > f) {
                this.motionZ = f;
            }

            this.fallDistance = 0.0F;
            if (this.motionY < -0.15) {
                this.motionY = -0.15;
            }

            if (this.isSneaking() && this.motionY < 0.0) {
                this.motionY = 0.0;
            }
        }
    }

    public void process2(float f, double d, double d2, double d3) {
        double d4 = this.motionX * d + this.motionZ * d2;
        double d5 = f - d4;
        if (!(d5 <= 0.0)) {
            double d6 = d3 * f / this.getFloatValue() * 0.05F;
            if (d6 > d5) {
                d6 = d5;
            }

            this.motionX += d6 * d;
            this.motionZ += d6 * d2;
        }
    }

    public void run8() {
        this.prevLimbSwingAmount = this.limbSwingAmount;
        double d = this.posX - this.prevPosX;
        double d2 = this.posZ - this.prevPosZ;
        float f = MathHelper.sqrt_double(d * d + d2 * d2) * 4.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        this.limbSwingAmount = this.limbSwingAmount + (f - this.limbSwingAmount) * 0.4F;
        this.limbSwing = this.limbSwing + this.limbSwingAmount;
    }

    public float getFloatValue5() {
        float floatValue = this.getFloatValue();
        float f = 0.16277136F / (floatValue * floatValue * floatValue);
        return this.getAIMoveSpeed() * f;
    }

    public void process3(float f) {
        float value = (Float)TrimpMod.softCap.getValue();
        float value2 = (Float)TrimpMod.softCapDegen.getValue();
        if ((Boolean)TrimpMod.uncappedBunnyhop.getValue()) {
            value = 1.0F;
            value2 = 1.0F;
        }

        float f2;
        float f3;
        if ((f3 = (float)this.sqrt_double()) > (f2 = f * value)) {
            if (value2 != 1.0F) {
                float f4 = (f3 - f2) * value2 + f2;
                float f5 = f4 / f3;
                this.motionX *= f5;
                this.motionZ *= f5;
            }

            this.process10(10);
        }
    }

    public float[] getFloatArray(float f, float f2) {
        float f3 = f * f + f2 * f2;
        float[] floats = new float[]{0.0F, 0.0F};
        if (f3 >= 1.0E-4F) {
            if ((f3 = MathHelper.sqrt_float(f3)) < 1.0F) {
                f3 = 1.0F;
            }

            f3 = 1.0F / f3;
            float sin = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F);
            float cos = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
            float f4;
            float f5;
            floats[0] = (f4 = f * f3) * cos - (f5 = f2 * f3) * sin;
            floats[1] = f5 * cos + f4 * sin;
        }

        return floats;
    }
}
