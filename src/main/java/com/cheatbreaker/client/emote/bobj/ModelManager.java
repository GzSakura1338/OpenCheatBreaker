package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.AnimationAction;
import com.cheatbreaker.client.emote.MeshMaterial;
import com.cheatbreaker.client.emote.ModelDefinition;
import com.cheatbreaker.client.emote.Transform;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.Map;

public class ModelManager {
    public Map<String, LoadedModel> loadedModels = new HashMap<>();
    public Map<String, ModelDefinitionEntry> definitions = new HashMap<>();
    public ModelDefinitionEntry defaultDefinition = new ModelDefinitionEntry(new ModelDefinition(), 0L);
    public static ModelManager instance = new ModelManager();
    public Gson gson;

    public MeshModel getModel(String str) {
        LoadedModel loadedModel = this.loadedModels.get(str);
        return loadedModel == null ? null : loadedModel.model;
    }

    public ModelDefinitionEntry getDefinition(String str) {
        ModelDefinitionEntry modelDefinitionEntry = this.definitions.get(str);
        return modelDefinitionEntry == null ? this.defaultDefinition : modelDefinitionEntry;
    }

    public ModelManager() {
        this.defaultDefinition.modelDefinition.rightHands.put("right_hand", new Transform("right_hand"));
        this.defaultDefinition.modelDefinition.leftHands.put("left_hand", new Transform("left_hand"));
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(MeshMaterial.class, new MeshMaterialAdapter());
        gsonBuilder.registerTypeAdapter(ModelDefinition.class, new ModelDefinitionDeserializer());
        gsonBuilder.registerTypeAdapter(ActionMap.class, new ActionMapDeserializer());
        gsonBuilder.registerTypeAdapter(Transform.class, new TransformDeserializer());
        gsonBuilder.registerTypeAdapter(AnimationAction.class, new AnimationActionDeserializer());
        this.gson = gsonBuilder.create();
    }
}
