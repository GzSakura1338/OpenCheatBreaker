package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;

public class BlockPartFace {
   public static EnumFacing FACING_DEFAULT = null;
   public EnumFacing cullFace;
   public int tintIndex;
   public String texture;
   public BlockFaceUV blockFaceUV;

   public BlockPartFace(EnumFacing cullFaceIn, int tintIndexIn, String textureIn, BlockFaceUV blockFaceUVIn) {
      this.cullFace = cullFaceIn;
      this.tintIndex = tintIndexIn;
      this.texture = textureIn;
      this.blockFaceUV = blockFaceUVIn;
   }

   static class Deserializer implements JsonDeserializer<BlockPartFace> {
      public Deserializer() {
      }

      public BlockPartFace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject asJsonObject = jsonElement.getAsJsonObject();
         EnumFacing parseCullFace = this.parseCullFace(asJsonObject);
         int parseTintIndex = this.parseTintIndex(asJsonObject);
         String parseTexture = this.parseTexture(asJsonObject);
         BlockFaceUV deserialize = (BlockFaceUV)jsonDeserializationContext.deserialize(asJsonObject, BlockFaceUV.class);
         return new BlockPartFace(parseCullFace, parseTintIndex, parseTexture, deserialize);
      }

      public int parseTintIndex(JsonObject jsonObject) {
         return JsonUtils.getInt(jsonObject, "tintindex", -1);
      }

      public String parseTexture(JsonObject jsonObject) {
         return JsonUtils.getString(jsonObject, "texture");
      }

      public EnumFacing parseCullFace(JsonObject jsonObject) {
         String string = JsonUtils.getString(jsonObject, "cullface", "");
         return EnumFacing.byName(string);
      }
   }
}
