package shadersmod.client;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ShaderProfile {
    public String name = null;
    public Map<String, String> mapOptionValues = new LinkedHashMap<>();
    public Set<String> disabledPrograms = new LinkedHashSet<>();

    public ShaderProfile(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    public void addOptionValue(String str, String str2) {
        this.mapOptionValues.put(str, str2);
    }

    public void addOptionValues(ShaderProfile shaderProfile) {
        if (shaderProfile != null) {
            this.mapOptionValues.putAll(shaderProfile.mapOptionValues);
        }
    }

    public void applyOptionValues(ShaderOption[] shaderOptions) {
        for (int i = 0; i < shaderOptions.length; i++) {
            ShaderOption shaderOption = shaderOptions[i];
            String name = shaderOption.getName();
            String str = this.mapOptionValues.get(name);
            if (str != null) {
                shaderOption.setValue(str);
            }
        }
    }

    public String[] getOptions() {
        Set keySet = this.mapOptionValues.keySet();
        return (String[])keySet.toArray(new String[keySet.size()]);
    }

    public String getValue(String str) {
        return this.mapOptionValues.get(str);
    }

    public void addDisabledProgram(String str) {
        this.disabledPrograms.add(str);
    }

    public void removeDisabledProgram(String str) {
        this.disabledPrograms.remove(str);
    }

    public Collection<String> getDisabledPrograms() {
        return new LinkedHashSet<>(this.disabledPrograms);
    }

    public void addDisabledPrograms(Collection<String> collection) {
        this.disabledPrograms.addAll(collection);
    }

    public boolean isProgramDisabled(String str) {
        return this.disabledPrograms.contains(str);
    }
}
