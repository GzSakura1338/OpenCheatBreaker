package net.optifine;

import java.io.File;
import net.minecraft.client.resources.AbstractResourcePack;

public class ResourceUtils {
    public static ReflectorClass ForgeAbstractResourcePack = new ReflectorClass(AbstractResourcePack.class);
    public static ReflectorField ForgeAbstractResourcePack_resourcePackFile = new ReflectorField(ForgeAbstractResourcePack, "resourcePackFile");
    public static boolean directAccessValid = true;

    public static File getResourcePackFile(AbstractResourcePack abstractResourcePack) {
        if (directAccessValid) {
            try {
                return abstractResourcePack.resourcePackFile;
            } catch (IllegalAccessError e) {
                directAccessValid = false;
                if (!ForgeAbstractResourcePack_resourcePackFile.exists()) {
                    throw e;
                }
            }
        }

        return (File)Reflector.getFieldValue(abstractResourcePack, ForgeAbstractResourcePack_resourcePackFile);
    }
}
