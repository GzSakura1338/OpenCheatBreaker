package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.ModelDefinition;

public class ModelDefinitionEntry {
    public ModelDefinition modelDefinition;
    public long lastModified;

    public ModelDefinitionEntry(ModelDefinition modelDefinition, long l) {
        this.modelDefinition = modelDefinition;
        this.lastModified = l;
    }
}
