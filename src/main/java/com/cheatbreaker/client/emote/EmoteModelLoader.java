package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.emote.bobj.BobjLoader;
import com.cheatbreaker.client.emote.bobj.LoadedModel;
import com.cheatbreaker.client.emote.bobj.MeshModel;
import com.cheatbreaker.client.emote.bobj.ModelDefinitionEntry;
import com.cheatbreaker.client.emote.bobj.ModelManager;
import com.cheatbreaker.client.event.SimpleEvent11;
import java.io.File;
import java.nio.charset.Charset;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.IOUtils;

public class EmoteModelLoader {
    public static File emotesDir;

    public void load() {
        emotesDir = new File(Minecraft.getMinecraft().mcDataDir, "cheatbreaker/emoticons");
        EntityPoseSnapshot entityPoseSnapshot = new EntityPoseSnapshot();
        CheatBreaker.getInstance().getEventBus().checkClass2(SimpleEvent11.class, entityPoseSnapshot::onSimpleEvent11);
        EmoteEntityRenderer.init();
        EmoteRegistry.registerDefaults();

        try {
            Class clazz = this.getClass();
            ModelManager instance = ModelManager.instance;
            ModelData load = BobjLoader.load(clazz.getResourceAsStream("/assets/minecraft/emoticons/models/entity/props.bobj"));
            ModelData load2 = BobjLoader.load(clazz.getResourceAsStream("/assets/minecraft/emoticons/models/entity/props_simple.bobj"));
            ModelData load3 = BobjLoader.load(clazz.getResourceAsStream("/assets/minecraft/emoticons/models/entity/default.bobj"));
            ModelData load4 = BobjLoader.load(clazz.getResourceAsStream("/assets/minecraft/emoticons/models/entity/slim.bobj"));
            ModelData load5 = BobjLoader.load(clazz.getResourceAsStream("/assets/minecraft/emoticons/models/entity/default_simple.bobj"));
            ModelData load6 = BobjLoader.load(clazz.getResourceAsStream("/assets/minecraft/emoticons/models/entity/slim_simple.bobj"));
            ModelData load7 = BobjLoader.load(clazz.getResourceAsStream("/assets/minecraft/emoticons/models/entity/actions.bobj"));
            ModelData load8 = BobjLoader.load(clazz.getResourceAsStream("/assets/minecraft/emoticons/models/entity/extra.bobj"));
            load3.actions.putAll(load7.actions);
            load3.actions.putAll(load8.actions);
            load4.actions.putAll(load7.actions);
            load4.actions.putAll(load8.actions);
            load5.actions.putAll(load7.actions);
            load5.actions.putAll(load8.actions);
            load6.actions.putAll(load7.actions);
            BobjLoader.mergeModelData(load3, load);
            BobjLoader.mergeModelData(load4, load);
            BobjLoader.mergeModelData(load5, load2);
            BobjLoader.mergeModelData(load6, load2);
            MeshModel meshModel = new MeshModel("default", load3);
            MeshModel meshModel2 = new MeshModel("slim", load4);
            MeshModel meshModel3 = new MeshModel("default_simple", load5);
            MeshModel meshModel4 = new MeshModel("slim_simple", load6);
            meshModel.buildMeshes();
            meshModel2.buildMeshes();
            meshModel3.buildMeshes();
            meshModel4.buildMeshes();
            instance.loadedModels.put("default", new LoadedModel(meshModel, emotesDir, 1L));
            instance.loadedModels.put("slim", new LoadedModel(meshModel2, emotesDir, 1L));
            instance.loadedModels.put("default_simple", new LoadedModel(meshModel3, emotesDir, 1L));
            instance.loadedModels.put("slim_simple", new LoadedModel(meshModel4, emotesDir, 1L));
            ModelDefinition fromJson = (ModelDefinition)instance.gson
                .fromJson(
                    IOUtils.toString(clazz.getResourceAsStream("/assets/minecraft/emoticons/models/entity/default.json"), Charset.defaultCharset()),
                    ModelDefinition.class
                );
            ModelDefinition fromJson2 = (ModelDefinition)instance.gson
                .fromJson(
                    IOUtils.toString(clazz.getResourceAsStream("/assets/minecraft/emoticons/models/entity/slim.json"), Charset.defaultCharset()),
                    ModelDefinition.class
                );
            ModelDefinition fromJson3 = (ModelDefinition)instance.gson
                .fromJson(
                    IOUtils.toString(clazz.getResourceAsStream("/assets/minecraft/emoticons/models/entity/default_simple.json"), Charset.defaultCharset()),
                    ModelDefinition.class
                );
            ModelDefinition fromJson4 = (ModelDefinition)instance.gson
                .fromJson(
                    IOUtils.toString(clazz.getResourceAsStream("/assets/minecraft/emoticons/models/entity/slim_simple.json"), Charset.defaultCharset()),
                    ModelDefinition.class
                );
            instance.definitions.put("default", new ModelDefinitionEntry(fromJson, 1L));
            instance.definitions.put("slim", new ModelDefinitionEntry(fromJson2, 1L));
            instance.definitions.put("default_simple", new ModelDefinitionEntry(fromJson3, 1L));
            instance.definitions.put("slim_simple", new ModelDefinitionEntry(fromJson4, 1L));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
