package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.util.ResourceLocationUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class MeshMaterial {
    public int textureFilter = 9728;
    public boolean normals = false;
    public boolean smooth = false;
    public boolean visible = true;
    public boolean lighting = true;
    public int color = 16777215;
    public ResourceLocation texture;

    public MeshMaterial copy() {
        MeshMaterial meshMaterial = new MeshMaterial();
        meshMaterial.texture = this.texture;
        meshMaterial.textureFilter = this.textureFilter;
        meshMaterial.normals = this.normals;
        meshMaterial.smooth = this.smooth;
        meshMaterial.visible = this.visible;
        meshMaterial.lighting = this.lighting;
        meshMaterial.color = this.color;
        return meshMaterial;
    }

    public NBTTagCompound writeToNbt(NBTTagCompound nbttagCompound) {
        if (nbttagCompound == null) {
            nbttagCompound = new NBTTagCompound();
        }

        if (this.texture != null) {
            nbttagCompound.setTag("Texture", ResourceLocationUtils.toNbt(this.texture));
        }

        nbttagCompound.setString("Filtering", this.textureFilter == 9728 ? "nearest" : "linear");
        nbttagCompound.setBoolean("Normals", this.normals);
        nbttagCompound.setBoolean("Smooth", this.smooth);
        nbttagCompound.setBoolean("Visible", this.visible);
        nbttagCompound.setBoolean("Lighting", this.lighting);
        nbttagCompound.setInteger("Color", this.color);
        return nbttagCompound;
    }

    @Override
    public Object clone() {
        return this.copy();
    }

    public void readFromNbt(NBTTagCompound nbttagCompound) {
        if (nbttagCompound.hasKey("Texture")) {
            this.texture = ResourceLocationUtils.fromNbt(nbttagCompound.getTag("Texture"));
        }

        if (nbttagCompound.hasKey("Filtering")) {
            int n = this.textureFilter = nbttagCompound.getString("Filtering").equalsIgnoreCase("linear") ? 9729 : 9728;
        }

        if (nbttagCompound.hasKey("Normals")) {
            this.normals = nbttagCompound.getBoolean("Normals");
        }

        if (nbttagCompound.hasKey("Smooth")) {
            this.smooth = nbttagCompound.getBoolean("Smooth");
        }

        if (nbttagCompound.hasKey("Visible")) {
            this.visible = nbttagCompound.getBoolean("Visible");
        }

        if (nbttagCompound.hasKey("Lighting")) {
            this.lighting = nbttagCompound.getBoolean("Lighting");
        }

        if (nbttagCompound.hasKey("Color")) {
            this.color = nbttagCompound.getInteger("Color");
        }
    }
}
