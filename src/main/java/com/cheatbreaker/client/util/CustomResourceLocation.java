package com.cheatbreaker.client.util;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;

public class CustomResourceLocation extends ResourceLocation {
    public List<ResourceLocation> resourceLocationList = new ArrayList<>();

    @Override
    public int hashCode() {
        int n = super.hashCode();
        int size = this.resourceLocationList.size();

        for (int i = 0; i < size; i++) {
            n = 31 * n + this.resourceLocationList.hashCode();
        }

        return n;
    }

    public CustomResourceLocation(String str) {
        super("it_would_be_very_ironic:if_this_would_match_with_regular_rls");
        this.resourceLocationList.add(ResourceLocationUtils.findResourceLocation(str));
    }

    @Override
    public String getResourceDomain() {
        return this.resourceLocationList.isEmpty() ? "" : this.resourceLocationList.get(0).getResourceDomain();
    }

    @Override
    public String toString() {
        return this.getResourceDomain() + ":" + this.getResourcePath();
    }

    @Override
    public String getResourcePath() {
        return this.resourceLocationList.isEmpty() ? "" : this.resourceLocationList.get(0).getResourcePath();
    }

    public CustomResourceLocation(String str, String str2) {
        super("it_would_be_very_ironic", "if_this_would_match_with_regular_rls");
        this.resourceLocationList.add(ResourceLocationUtils.createResourceLocation(str, str2));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CustomResourceLocation) {
            CustomResourceLocation customResourceLocation = (CustomResourceLocation)obj;
            boolean flag = this.resourceLocationList.size() == customResourceLocation.resourceLocationList.size();
            if (flag) {
                for (int i = 0; i < this.resourceLocationList.size(); i++) {
                    flag = flag && Objects.equal(this.resourceLocationList.get(i), customResourceLocation.resourceLocationList.get(i));
                }
            }

            return flag;
        } else {
            return super.equals(obj);
        }
    }
}
