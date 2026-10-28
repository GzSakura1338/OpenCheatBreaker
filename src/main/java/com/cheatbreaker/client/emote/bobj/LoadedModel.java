package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.ModelData;
import java.io.File;

public class LoadedModel {
    public MeshModel model;
    public File file;
    public long lastModified;

    public LoadedModel(MeshModel meshModel, File file, long l) {
        this.model = meshModel;
        this.file = file;
        this.lastModified = l;
    }

    public void reload(ModelData modelData, long l) {
        this.model.setModelData(modelData);
        this.lastModified = l;
    }
}
