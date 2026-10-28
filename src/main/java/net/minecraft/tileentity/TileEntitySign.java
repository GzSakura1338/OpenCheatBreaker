package net.minecraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S33PacketUpdateSign;

public class TileEntitySign extends TileEntity {
    public String[] signText = new String[]{"", "", "", ""};
    public int lineBeingEdited = -1;
    public boolean isEditable = true;
    public EntityPlayer editor;

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("Text1", this.signText[0]);
        compound.setString("Text2", this.signText[1]);
        compound.setString("Text3", this.signText[2]);
        compound.setString("Text4", this.signText[3]);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.isEditable = false;
        super.readFromNBT(compound);

        for (int i = 0; i < 4; i++) {
            this.signText[i] = compound.getString("Text" + (i + 1));
            if (this.signText[i].length() > 15) {
                this.signText[i] = this.signText[i].substring(0, 15);
            }
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        String[] strs = new String[4];
        System.arraycopy(this.signText, 0, strs, 0, 4);
        return new S33PacketUpdateSign(this.xCoord, this.yCoord, this.zCoord, strs);
    }

    public boolean getIsEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean flag) {
        this.isEditable = flag;
        if (!flag) {
            this.editor = null;
        }
    }

    public void setEditor(EntityPlayer entityPlayer) {
        this.editor = entityPlayer;
    }

    public EntityPlayer getEditor() {
        return this.editor;
    }
}
