package net.optifine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ReflectorForge {
    public static void FMLClientHandler_trackBrokenTexture(ResourceLocation resourceLocation, String str) {
        if (!Reflector.FMLClientHandler_trackBrokenTexture.exists()) {
            Object obj = Reflector.call(Reflector.FMLClientHandler_instance);
            Reflector.call(obj, Reflector.FMLClientHandler_trackBrokenTexture, resourceLocation, str);
        }
    }

    public static void FMLClientHandler_trackMissingTexture(ResourceLocation resourceLocation) {
        if (!Reflector.FMLClientHandler_trackMissingTexture.exists()) {
            Object obj = Reflector.call(Reflector.FMLClientHandler_instance);
            Reflector.call(obj, Reflector.FMLClientHandler_trackMissingTexture, resourceLocation);
        }
    }

    public static void putLaunchBlackboard(String str, Object obj) {
        Map fieldValue = (Map)Reflector.getFieldValue(Reflector.Launch_blackboard);
        if (fieldValue != null) {
            fieldValue.put(str, obj);
        }
    }

    public static boolean renderFirstPersonHand(RenderGlobal renderGlobal, float f, int n) {
        return !Reflector.ForgeHooksClient_renderFirstPersonHand.exists()
            ? false
            : Reflector.callBoolean(Reflector.ForgeHooksClient_renderFirstPersonHand, renderGlobal, f, n);
    }

    public static InputStream getOptiFineResourceStream(String str) {
        if (!Reflector.OptiFineClassTransformer_instance.exists()) {
            return null;
        }

        Object fieldValue = Reflector.getFieldValue(Reflector.OptiFineClassTransformer_instance);
        if (fieldValue == null) {
            return null;
        }

        if (str.startsWith("/")) {
            str = str.substring(1);
        }

        byte[] bytes = (byte[])Reflector.call(fieldValue, Reflector.OptiFineClassTransformer_getOptiFineResource, str);
        return bytes == null ? null : new ByteArrayInputStream(bytes);
    }

    public static boolean blockHasTileEntity(World world, int n, int n2, int n3) {
        Block block = world.getBlock(n, n2, n3);
        if (!Reflector.ForgeBlock_hasTileEntity.exists()) {
            return block.hasTileEntity();
        }

        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        return Reflector.callBoolean(block, Reflector.ForgeBlock_hasTileEntity, blockMetadata);
    }

    public static String[] getForgeModIds() {
        if (!Reflector.Loader.exists()) {
            return new String[0];
        }

        Object obj = Reflector.call(Reflector.Loader_instance);
        List list = (List)Reflector.call(obj, Reflector.Loader_getActiveModList);
        if (list == null) {
            return new String[0];
        }

        ArrayList list2 = new ArrayList();

        for (Object obj2 : list) {
            if (Reflector.ModContainer.isInstance(obj2)) {
                String callString = Reflector.callString(obj2, Reflector.ModContainer_getModId);
                if (callString != null) {
                    list2.add(callString);
                }
            }
        }

        return (String[])list2.toArray(new String[list2.size()]);
    }
}
