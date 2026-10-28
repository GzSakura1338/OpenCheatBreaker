package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.AnimationAction;
import com.cheatbreaker.client.emote.MeshMaterial;
import com.cheatbreaker.client.emote.ModelData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL15;

public class MeshModel {
    public String name;
    public ModelData modelData;
    public Minecraft mc;
    public List<RenderableMesh> meshes;

    public void buildMeshes() {
        for (Entry entry : BobjLoader.compileMeshes(this.modelData).entrySet()) {
            RenderableMesh renderableMesh = new RenderableMesh(this, (String)entry.getKey(), (BobjCompiledData)entry.getValue());
            renderableMesh.minecraft = new ResourceLocation("minecraft", "textures/entity/steve.png");
            this.meshes.add(renderableMesh);
        }
    }

    public void render(Map map) {
        for (RenderableMesh renderableMesh : this.meshes) {
            renderableMesh.render(this.mc, map == null ? null : (MeshMaterial)map.get(renderableMesh.name));
        }

        GL15.glBindBuffer(34962, 0);
        GL15.glBindBuffer(34963, 0);
    }

    public void setModelData(ModelData modelData) {
        this.modelData = modelData;
        this.disposeMeshes();
        this.buildMeshes();
    }

    public ActionPlayer createActionPlayer(ActionPlayer actionPlayer, AnimationAction animationAction, boolean flag, int n) {
        BobjAction bobjAction = this.modelData.actions.get(animationAction.name);
        if (bobjAction == null) {
            return null;
        } else if (actionPlayer != null && actionPlayer.action == bobjAction) {
            actionPlayer.animationAction = animationAction;
            actionPlayer.setSpeedFactor(1.0F);
            return actionPlayer;
        } else {
            return new ActionPlayer(bobjAction, animationAction, flag, n);
        }
    }

    public MeshModel(String str, ModelData modelData) {
        this.name = str;
        this.modelData = modelData;
        this.mc = Minecraft.getMinecraft();
        this.meshes = new ArrayList<>();
    }

    public void disposeMeshes() {
        for (RenderableMesh renderableMesh : this.meshes) {
            renderableMesh.deleteBuffers();
        }

        this.meshes.clear();
    }

    public ActionPlayer createActionPlayerDefault(ActionPlayer actionPlayer, AnimationAction animationAction, boolean flag) {
        return this.createActionPlayer(actionPlayer, animationAction, flag, 1);
    }
}
