package net.optifine.model;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;
import net.optifine.Config;

public class ModelUtils {
   public static void dbgModel(IBakedModel bakedModel) {
      if (bakedModel != null) {
         Config.dbg(
            "Model: "
               + bakedModel
               + ", ao: "
               + bakedModel.isAmbientOcclusion()
               + ", gui3d: "
               + bakedModel.isGui3d()
               + ", builtIn: "
               + bakedModel.isBuiltInRenderer()
               + ", particle: "
               + bakedModel.getParticleTexture()
         );
         EnumFacing[] enumFacings = EnumFacing.VALUES;

         for (int i = 0; i < enumFacings.length; i++) {
            EnumFacing enumFacing = enumFacings[i];
            List faceQuads = bakedModel.getFaceQuads(enumFacing);
            dbgQuads(enumFacing.getName(), faceQuads, "  ");
         }

         List generalQuads = bakedModel.getGeneralQuads();
         dbgQuads("General", generalQuads, "  ");
      }
   }

   public static void dbgQuads(String str, List list, String str2) {
      for (Object obj : list) {
         BakedQuad bakedQuad = (BakedQuad)obj;
         dbgQuad(str, bakedQuad, str2);
      }
   }

   public static void dbgQuad(String str, BakedQuad bakedQuad, String str2) {
      Config.dbg(
         str2
            + "Quad: "
            + bakedQuad.getClass().getName()
            + ", type: "
            + str
            + ", face: "
            + bakedQuad.getFace()
            + ", tint: "
            + bakedQuad.getTintIndex()
            + ", sprite: "
            + bakedQuad.getSprite()
      );
      dbgVertexData(bakedQuad.getVertexData(), "  " + str2);
   }

   public static void dbgVertexData(int[] ints, String str) {
      int n = ints.length / 4;
      Config.dbg(str + "Length: " + ints.length + ", step: " + n);

      for (int i = 0; i < 4; i++) {
         int n2 = i * n;
         float intBitsToFloat = Float.intBitsToFloat(ints[n2 + 0]);
         float intBitsToFloat2 = Float.intBitsToFloat(ints[n2 + 1]);
         float intBitsToFloat3 = Float.intBitsToFloat(ints[n2 + 2]);
         int n3 = ints[n2 + 3];
         float intBitsToFloat4 = Float.intBitsToFloat(ints[n2 + 4]);
         float intBitsToFloat5 = Float.intBitsToFloat(ints[n2 + 5]);
         Config.dbg(str + i + " xyz: " + intBitsToFloat + "," + intBitsToFloat2 + "," + intBitsToFloat3 + " col: " + n3 + " u,v: " + intBitsToFloat4 + "," + intBitsToFloat5);
      }
   }

   public static IBakedModel duplicateModel(IBakedModel bakedModel) {
      List duplicateQuadList2 = duplicateQuadList(bakedModel.getGeneralQuads());
      EnumFacing[] enumFacings = EnumFacing.VALUES;
      ArrayList list = new ArrayList();

      for (int i = 0; i < enumFacings.length; i++) {
         EnumFacing enumFacing = enumFacings[i];
         List faceQuads = bakedModel.getFaceQuads(enumFacing);
         List duplicateQuadList3 = duplicateQuadList(faceQuads);
         list.add(duplicateQuadList3);
      }

      return new SimpleBakedModel(duplicateQuadList2, list, bakedModel.isAmbientOcclusion(), bakedModel.isGui3d(), bakedModel.getParticleTexture(), bakedModel.getItemCameraTransforms());
   }

   public static List duplicateQuadList(List list) {
      ArrayList list2 = new ArrayList();

      for (Object obj : list) {
         BakedQuad bakedQuad = (BakedQuad)obj;
         BakedQuad duplicateQuad2 = duplicateQuad(bakedQuad);
         list2.add(duplicateQuad2);
      }

      return list2;
   }

   public static BakedQuad duplicateQuad(BakedQuad bakedQuad) {
      return new BakedQuad((int[])bakedQuad.getVertexData().clone(), bakedQuad.getTintIndex(), bakedQuad.getFace(), bakedQuad.getSprite());
   }
}
