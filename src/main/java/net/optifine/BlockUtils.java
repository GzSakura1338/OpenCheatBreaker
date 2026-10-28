package net.optifine;

import net.minecraft.block.Block;

public class BlockUtils {
    public static ReflectorClass ForgeBlock = new ReflectorClass(Block.class);
    public static ReflectorMethod ForgeBlock_setLightOpacity = new ReflectorMethod(ForgeBlock, "setLightOpacity");
    public static boolean directAccessValid = true;

    public static void setLightOpacity(Block block, int n) {
        if (directAccessValid) {
            try {
                block.setLightOpacity(n);
                return;
            } catch (IllegalAccessError e) {
                directAccessValid = false;
                if (!ForgeBlock_setLightOpacity.exists()) {
                    throw e;
                }
            }
        }

        Reflector.callVoid(block, ForgeBlock_setLightOpacity, n);
    }
}
