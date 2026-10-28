package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityEnchantmentTable extends TileEntity {
    public int tickCount;
    public float pageFlip;
    public float pageFlipPrev;
    public float nextPageTurningVelocity;
    public float pageTurningVelocity;
    public float bookSpread;
    public float bookSpreadPrev;
    public float bookRotation;
    public float bookRotationPrev;
    public float closedBookAngle;
    public static Random RANDOM = new Random();
    public String customName;

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (this.length()) {
            compound.setString("CustomName", this.customName);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        this.bookSpreadPrev = this.bookSpread;
        this.bookRotationPrev = this.bookRotation;
        EntityPlayer closestPlayer = this.worldObj.getClosestPlayer(this.xCoord + 0.5F, this.yCoord + 0.5F, this.zCoord + 0.5F, 3.0);
        if (closestPlayer != null) {
            double d = closestPlayer.posX - (this.xCoord + 0.5F);
            double d2 = closestPlayer.posZ - (this.zCoord + 0.5F);
            this.closedBookAngle = (float)Math.atan2(d2, d);
            this.bookSpread += 0.1F;
            if (this.bookSpread < 0.5F || RANDOM.nextInt(40) == 0) {
                float nextPageTurningVelocity = this.nextPageTurningVelocity;

                do {
                    this.nextPageTurningVelocity = this.nextPageTurningVelocity + (RANDOM.nextInt(4) - RANDOM.nextInt(4));
                } while (nextPageTurningVelocity == this.nextPageTurningVelocity);
            }
        } else {
            this.closedBookAngle += 0.02F;
            this.bookSpread -= 0.1F;
        }

        while (this.bookRotation >= (float) Math.PI) {
            this.bookRotation -= (float) (Math.PI * 2);
        }

        while (this.bookRotation < (float) -Math.PI) {
            this.bookRotation += (float) (Math.PI * 2);
        }

        while (this.closedBookAngle >= (float) Math.PI) {
            this.closedBookAngle -= (float) (Math.PI * 2);
        }

        while (this.closedBookAngle < (float) -Math.PI) {
            this.closedBookAngle += (float) (Math.PI * 2);
        }

        float f = this.closedBookAngle - this.bookRotation;

        while (f >= (float) Math.PI) {
            f -= (float) (Math.PI * 2);
        }

        while (f < (float) -Math.PI) {
            f += (float) (Math.PI * 2);
        }

        this.bookRotation += f * 0.4F;
        if (this.bookSpread < 0.0F) {
            this.bookSpread = 0.0F;
        }

        if (this.bookSpread > 1.0F) {
            this.bookSpread = 1.0F;
        }

        this.tickCount++;
        this.pageFlipPrev = this.pageFlip;
        float f2 = (this.nextPageTurningVelocity - this.pageFlip) * 0.4F;
        float f3 = 0.2F;
        if (f2 < -f3) {
            f2 = -f3;
        }

        if (f2 > f3) {
            f2 = f3;
        }

        this.pageTurningVelocity = this.pageTurningVelocity + (f2 - this.pageTurningVelocity) * 0.9F;
        this.pageFlip = this.pageFlip + this.pageTurningVelocity;
    }

    public String getText() {
        return this.length() ? this.customName : "container.enchant";
    }

    public boolean length() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(String str) {
        this.customName = str;
    }
}
