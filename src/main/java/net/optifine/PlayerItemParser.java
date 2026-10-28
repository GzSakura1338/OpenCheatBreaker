package net.optifine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

public class PlayerItemParser {
    public static JsonParser jsonParser = new JsonParser();
    public static final String ITEM_TEXTURE_SIZE = "textureSize";
    public static final String ITEM_USE_PLAYER_TEXTURE = "usePlayerTexture";
    public static final String ITEM_MODELS = "models";
    public static final String MODEL_ID = "id";
    public static final String MODEL_BASE_ID = "baseId";
    public static final String MODEL_ATTACH_TO = "attachTo";
    public static final String MODEL_INVERT_AXIS = "invertAxis";
    public static final String MODEL_MIRROR_TEXTURE = "mirrorTexture";
    public static final String MODEL_TRANSLATE = "translate";
    public static final String MODEL_ROTATE = "rotate";
    public static final String MODEL_SCALE = "scale";
    public static final String MODEL_BOXES = "boxes";
    public static final String MODEL_SPRITES = "sprites";
    public static final String MODEL_SUBMODEL = "submodel";
    public static final String MODEL_SUBMODELS = "submodels";
    public static final String BOX_TEXTURE_OFFSET = "textureOffset";
    public static final String BOX_COORDINATES = "coordinates";
    public static final String BOX_SIZE_ADD = "sizeAdd";
    public static final String ITEM_TYPE_MODEL = "PlayerItem";
    public static final String MODEL_TYPE_BOX = "ModelBox";
    public static final String TEXT_TYPE = "type";
    public static final String TEXT_TYPE2 = "type";

    public static PlayerItemModel parseItemModel(JsonObject jsonObject) {
        String string = Json.getString(jsonObject, "type");
        if (!Config.equals(string, "PlayerItem")) {
            throw new JsonParseException("Unknown model type: " + string);
        }

        int[] parseIntArray = Json.parseIntArray(jsonObject.get("textureSize"), 2);
        checkNull(parseIntArray, "Missing texture size");
        Dimension dimension = new Dimension(parseIntArray[0], parseIntArray[1]);
        boolean boolean2 = Json.getBoolean(jsonObject, "usePlayerTexture", false);
        JsonArray jsonArray = (JsonArray)jsonObject.get("models");
        checkNull(jsonArray, "Missing elements");
        HashMap map = new HashMap();
        ArrayList list = new ArrayList();
        new ArrayList();

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject2 = (JsonObject)jsonArray.get(i);
            String string2 = Json.getString(jsonObject2, "baseId");
            if (string2 != null) {
                JsonObject jsonObject3 = (JsonObject)map.get(string2);
                if (jsonObject3 == null) {
                    Config.warn("BaseID not found: " + string2);
                    continue;
                }

                for (Entry entry : jsonObject3.entrySet()) {
                    if (!jsonObject2.has((String)entry.getKey())) {
                        jsonObject2.add((String)entry.getKey(), (JsonElement)entry.getValue());
                    }
                }
            }

            String string3 = Json.getString(jsonObject2, "id");
            if (string3 != null) {
                if (!map.containsKey(string3)) {
                    map.put(string3, jsonObject2);
                } else {
                    Config.warn("Duplicate model ID: " + string3);
                }
            }

            PlayerItemRenderer parseItemRenderer2 = parseItemRenderer(jsonObject2, dimension);
            if (parseItemRenderer2 != null) {
                list.add(parseItemRenderer2);
            }
        }

        PlayerItemRenderer[] toArray = (PlayerItemRenderer[])list.toArray(new PlayerItemRenderer[list.size()]);
        return new PlayerItemModel(dimension, boolean2, toArray);
    }

    public static void checkNull(Object obj, String str) {
        if (obj == null) {
            throw new JsonParseException(str);
        }
    }

    public static ResourceLocation makeResourceLocation(String str) {
        int indexOf = str.indexOf(58);
        if (indexOf < 0) {
            return new ResourceLocation(str);
        }

        String substring = str.substring(0, indexOf);
        String substring2 = str.substring(indexOf + 1);
        return new ResourceLocation(substring, substring2);
    }

    public static int parseAttachModel(String str) {
        if (str == null) {
            return 0;
        }

        if (str.equals("body")) {
            return 0;
        }

        if (str.equals("head")) {
            return 1;
        }

        if (str.equals("leftArm")) {
            return 2;
        }

        if (str.equals("rightArm")) {
            return 3;
        }

        if (str.equals("leftLeg")) {
            return 4;
        }

        if (str.equals("rightLeg")) {
            return 5;
        }

        if (str.equals("cape")) {
            return 6;
        }

        Config.warn("Unknown attachModel: " + str);
        return 0;
    }

    public static PlayerItemRenderer parseItemRenderer(JsonObject jsonObject, Dimension dimension) {
        String string = Json.getString(jsonObject, "type");
        if (!Config.equals(string, "ModelBox")) {
            Config.warn("Unknown model type: " + string);
            return null;
        } else {
            String string2 = Json.getString(jsonObject, "attachTo");
            int parseAttachModel2 = parseAttachModel(string2);
            float float2 = Json.getFloat(jsonObject, "scale", 1.0F);
            ModelPlayerItem modelPlayerItem = new ModelPlayerItem();
            modelPlayerItem.textureWidth = dimension.width;
            modelPlayerItem.textureHeight = dimension.height;
            ModelRenderer parseModelRenderer2 = parseModelRenderer(jsonObject, modelPlayerItem);
            return new PlayerItemRenderer(parseAttachModel2, float2, parseModelRenderer2);
        }
    }

    public static ModelRenderer parseModelRenderer(JsonObject jsonObject, ModelBase modelBase) {
        ModelRenderer modelRenderer = new ModelRenderer(modelBase);
        String toLowerCase = Json.getString(jsonObject, "invertAxis", "").toLowerCase();
        boolean flag = toLowerCase.contains("x");
        boolean flag2 = toLowerCase.contains("y");
        boolean flag3 = toLowerCase.contains("z");
        float[] parseFloatArray = Json.parseFloatArray(jsonObject.get("translate"), 3, new float[3]);
        if (flag) {
            parseFloatArray[0] = -parseFloatArray[0];
        }

        if (flag2) {
            parseFloatArray[1] = -parseFloatArray[1];
        }

        if (flag3) {
            parseFloatArray[2] = -parseFloatArray[2];
        }

        float[] parseFloatArray2 = Json.parseFloatArray(jsonObject.get("rotate"), 3, new float[3]);

        for (int i = 0; i < parseFloatArray2.length; i++) {
            parseFloatArray2[i] = parseFloatArray2[i] / 180.0F * (float) Math.PI;
        }

        if (flag) {
            parseFloatArray2[0] = -parseFloatArray2[0];
        }

        if (flag2) {
            parseFloatArray2[1] = -parseFloatArray2[1];
        }

        if (flag3) {
            parseFloatArray2[2] = -parseFloatArray2[2];
        }

        modelRenderer.setRotationPoint(parseFloatArray[0], parseFloatArray[1], parseFloatArray[2]);
        modelRenderer.rotateAngleX = parseFloatArray2[0];
        modelRenderer.rotateAngleY = parseFloatArray2[1];
        modelRenderer.rotateAngleZ = parseFloatArray2[2];
        String toLowerCase2 = Json.getString(jsonObject, "mirrorTexture", "").toLowerCase();
        boolean flag4 = toLowerCase2.contains("u");
        boolean flag5 = toLowerCase2.contains("v");
        if (flag4) {
            modelRenderer.mirror = true;
        }

        if (flag5) {
            modelRenderer.mirrorV = true;
        }

        JsonArray asJsonArray = jsonObject.getAsJsonArray("boxes");
        if (asJsonArray != null) {
            for (int i = 0; i < asJsonArray.size(); i++) {
                JsonObject asJsonObject = asJsonArray.get(i).getAsJsonObject();
                int[] parseIntArray = Json.parseIntArray(asJsonObject.get("textureOffset"), 2);
                if (parseIntArray == null) {
                    throw new JsonParseException("Texture offset not specified");
                }

                float[] parseFloatArray3 = Json.parseFloatArray(asJsonObject.get("coordinates"), 6);
                if (parseFloatArray3 == null) {
                    throw new JsonParseException("Coordinates not specified");
                }

                if (flag) {
                    parseFloatArray3[0] = -parseFloatArray3[0] - parseFloatArray3[3];
                }

                if (flag2) {
                    parseFloatArray3[1] = -parseFloatArray3[1] - parseFloatArray3[4];
                }

                if (flag3) {
                    parseFloatArray3[2] = -parseFloatArray3[2] - parseFloatArray3[5];
                }

                float float2 = Json.getFloat(asJsonObject, "sizeAdd", 0.0F);
                modelRenderer.setTextureOffset(parseIntArray[0], parseIntArray[1]);
                modelRenderer.addBox(parseFloatArray3[0], parseFloatArray3[1], parseFloatArray3[2], (int)parseFloatArray3[3], (int)parseFloatArray3[4], (int)parseFloatArray3[5], float2);
            }
        }

        JsonArray asJsonArray2 = jsonObject.getAsJsonArray("sprites");
        if (asJsonArray2 != null) {
            for (int i = 0; i < asJsonArray2.size(); i++) {
                JsonObject asJsonObject = asJsonArray2.get(i).getAsJsonObject();
                int[] parseIntArray = Json.parseIntArray(asJsonObject.get("textureOffset"), 2);
                if (parseIntArray == null) {
                    throw new JsonParseException("Texture offset not specified");
                }

                float[] parseFloatArray3 = Json.parseFloatArray(asJsonObject.get("coordinates"), 6);
                if (parseFloatArray3 == null) {
                    throw new JsonParseException("Coordinates not specified");
                }

                if (flag) {
                    parseFloatArray3[0] = -parseFloatArray3[0] - parseFloatArray3[3];
                }

                if (flag2) {
                    parseFloatArray3[1] = -parseFloatArray3[1] - parseFloatArray3[4];
                }

                if (flag3) {
                    parseFloatArray3[2] = -parseFloatArray3[2] - parseFloatArray3[5];
                }

                float float2 = Json.getFloat(asJsonObject, "sizeAdd", 0.0F);
                modelRenderer.setTextureOffset(parseIntArray[0], parseIntArray[1]);
                modelRenderer.addSprite(parseFloatArray3[0], parseFloatArray3[1], parseFloatArray3[2], (int)parseFloatArray3[3], (int)parseFloatArray3[4], (int)parseFloatArray3[5], float2);
            }
        }

        JsonObject jsonObject2 = (JsonObject)jsonObject.get("submodel");
        if (jsonObject2 != null) {
            ModelRenderer parseModelRenderer2 = parseModelRenderer(jsonObject2, modelBase);
            modelRenderer.addChild(parseModelRenderer2);
        }

        JsonArray jsonArray = (JsonArray)jsonObject.get("submodels");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject3 = (JsonObject)jsonArray.get(i);
                ModelRenderer parseModelRenderer2 = parseModelRenderer(jsonObject3, modelBase);
                modelRenderer.addChild(parseModelRenderer2);
            }
        }

        return modelRenderer;
    }
}
