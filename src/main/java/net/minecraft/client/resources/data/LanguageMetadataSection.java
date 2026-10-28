package net.minecraft.client.resources.data;

import java.util.Collection;

public class LanguageMetadataSection implements IMetadataSection {
    public Collection languages;

    public LanguageMetadataSection(Collection collection) {
        this.languages = collection;
    }

    public Collection getLanguages() {
        return this.languages;
    }
}
