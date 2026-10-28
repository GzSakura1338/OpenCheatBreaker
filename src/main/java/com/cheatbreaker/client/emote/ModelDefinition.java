package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.emote.bobj.ActionMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ModelDefinition {
    public String name = "";
    public String primaryMesh = "";
    public float scale = 1.0F;
    public float scaleGUI = 1.0F;
    public float scaleItems = 1.0F;
    public boolean renderHeldItems = true;
    public Map<String, Transform> leftHands = new HashMap<>();
    public Map<String, Transform> rightHands = new HashMap<>();
    public String head = "head";
    public ActionMap actions = new ActionMap();
    public Map<String, MeshMaterial> meshMaterials = new HashMap<>();

    public void copyFrom(ModelDefinition modelDefinition) {
        this.name = modelDefinition.name;
        this.primaryMesh = modelDefinition.primaryMesh;
        this.scale = modelDefinition.scale;
        this.scaleGUI = modelDefinition.scaleGUI;
        this.scaleItems = modelDefinition.scaleItems;
        this.renderHeldItems = modelDefinition.renderHeldItems;
        this.head = modelDefinition.head;
        this.actions.copyFrom(modelDefinition.actions);
        this.leftHands.clear();
        this.rightHands.clear();
        this.meshMaterials.clear();

        for (Entry entry : modelDefinition.leftHands.entrySet()) {
            this.leftHands.put((String)entry.getKey(), ((Transform)entry.getValue()).clone());
        }

        for (Entry entry : modelDefinition.rightHands.entrySet()) {
            this.rightHands.put((String)entry.getKey(), ((Transform)entry.getValue()).clone());
        }

        for (Entry entry : modelDefinition.meshMaterials.entrySet()) {
            this.meshMaterials.put((String)entry.getKey(), ((MeshMaterial)entry.getValue()).copy());
        }
    }

    public void readFromNbt(NBTTagCompound nbttagCompound) {
        if (nbttagCompound.hasKey("Name")) {
            this.name = nbttagCompound.getString("Name");
        }

        if (nbttagCompound.hasKey("Scale")) {
            this.scale = nbttagCompound.getFloat("Scale");
        }

        if (nbttagCompound.hasKey("ScaleGUI")) {
            this.scaleGUI = nbttagCompound.getFloat("ScaleGUI");
        }

        if (nbttagCompound.hasKey("ScaleItems")) {
            this.scaleItems = nbttagCompound.getFloat("ScaleItems");
        }

        if (nbttagCompound.hasKey("RenderHeldItems")) {
            this.renderHeldItems = nbttagCompound.getBoolean("RenderHeldItems");
        }

        if (nbttagCompound.hasKey("LeftHands")) {
            this.readTransforms(this.leftHands, nbttagCompound.getTag("LeftHands"));
        }

        if (nbttagCompound.hasKey("RightHands")) {
            this.readTransforms(this.rightHands, nbttagCompound.getTag("RightHands"));
        }

        if (nbttagCompound.hasKey("Head")) {
            this.head = nbttagCompound.getString("Head");
        }

        if (nbttagCompound.hasKey("Actions")) {
            this.actions.fromNbt(nbttagCompound.getCompoundTag("Actions"));
        }

        if (nbttagCompound.hasKey("Meshes")) {
            NBTTagCompound compoundTag = nbttagCompound.getCompoundTag("Meshes");

            for (String str : (Iterable<String>)compoundTag.getKeySet()) {
                NBTBase tag = compoundTag.getTag(str);
                MeshMaterial meshMaterial = this.meshMaterials.get(str);
                if (meshMaterial == null) {
                    this.meshMaterials.put(str, meshMaterial = new MeshMaterial());
                }

                meshMaterial.readFromNbt((NBTTagCompound)tag);
            }
        }
    }

    public NBTTagCompound writeToNbt(NBTTagCompound nbttagCompound) {
        if (nbttagCompound == null) {
            nbttagCompound = new NBTTagCompound();
        }

        if (!this.name.isEmpty()) {
            nbttagCompound.setString("Name", this.name);
        }

        if (this.scale != 1.0F) {
            nbttagCompound.setFloat("Scale", this.scale);
        }

        if (this.scaleGUI != 1.0F) {
            nbttagCompound.setFloat("ScaleGUI", this.scaleGUI);
        }

        if (this.scaleItems != 1.0F) {
            nbttagCompound.setFloat("ScaleItems", this.scaleItems);
        }

        if (!this.renderHeldItems) {
            nbttagCompound.setBoolean("RenderHeldItems", this.renderHeldItems);
        }

        if (!this.head.equals("head")) {
            nbttagCompound.setString("Head", this.head);
        }

        if (!this.leftHands.isEmpty()) {
            nbttagCompound.setTag("LeftHands", this.writeTransforms(this.leftHands));
        }

        if (!this.rightHands.isEmpty()) {
            nbttagCompound.setTag("RightHands", this.writeTransforms(this.rightHands));
        }

        NBTTagCompound toNbt = this.actions.toNbt((NBTTagCompound)null);
        if (toNbt != null && !toNbt.hasNoTags()) {
            nbttagCompound.setTag("Actions", toNbt);
        }

        if (!this.meshMaterials.isEmpty()) {
            NBTTagCompound nbttagCompound2 = new NBTTagCompound();

            for (Entry entry : this.meshMaterials.entrySet()) {
                nbttagCompound2.setTag((String)entry.getKey(), ((MeshMaterial)entry.getValue()).writeToNbt(null));
            }

            nbttagCompound.setTag("Meshes", nbttagCompound2);
        }

        return nbttagCompound;
    }

    public NBTTagCompound writeTransforms(Map<String, Transform> map) {
        NBTTagCompound nbttagCompound = new NBTTagCompound();

        for (Entry entry : map.entrySet()) {
            nbttagCompound.setTag((String)entry.getKey(), ((Transform)entry.getValue()).writeToNbt(null));
        }

        return nbttagCompound;
    }

    public void readTransforms(Map<String, Transform> map, NBTBase nbtbase) {
        map.clear();
        if (nbtbase instanceof NBTTagList) {
            NBTTagList nbttagList = (NBTTagList)nbtbase;

            for (int i = 0; i < nbttagList.tagCount(); i++) {
                String stringTagAt = nbttagList.getStringTagAt(i);
                map.put(stringTagAt, new Transform(stringTagAt));
            }
        } else if (nbtbase instanceof NBTTagCompound) {
            NBTTagCompound nbttagCompound = (NBTTagCompound)nbtbase;

            for (String str : (Iterable<String>)nbttagCompound.getKeySet()) {
                Transform transform = (Transform)map.get(str);
                if (transform == null) {
                    map.put(str, transform = new Transform(str));
                }

                transform.readFromNbt(nbttagCompound.getCompoundTag(str));
            }
        }
    }
}
