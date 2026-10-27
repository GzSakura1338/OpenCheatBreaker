package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.EnumFaceDirection.Constants;
import net.minecraft.client.renderer.EnumFaceDirection.VertexInformation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;
import net.minecraftforge.client.model.ITransformation;
import net.optifine.Config;
import net.optifine.model.BlockModelUtils;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class FaceBakery {
   public static float SCALE_ROTATION_22_5 = 1.0F / (float)Math.cos((float) (Math.PI / 8)) - 1.0F;
   public static float SCALE_ROTATION_GENERAL = 1.0F / (float)Math.cos(Math.PI / 4) - 1.0F;

   public BakedQuad makeBakedQuad(
      Vector3f posFrom,
      Vector3f posTo,
      BlockPartFace face,
      TextureAtlasSprite sprite,
      EnumFacing facing,
      ModelRotation modelRotationIn,
      BlockPartRotation partRotation,
      boolean uvLocked,
      boolean shade
   ) {
      return this.makeBakedQuad(posFrom, posTo, face, sprite, facing, (ITransformation)modelRotationIn, partRotation, uvLocked, shade);
   }

   public BakedQuad makeBakedQuad(
      Vector3f vector3f,
      Vector3f vector3f2,
      BlockPartFace blockPartFace,
      TextureAtlasSprite textureAtlasSprite,
      EnumFacing enumFacing,
      ITransformation transformation,
      BlockPartRotation blockPartRotation,
      boolean flag,
      boolean flag2
   ) {
      int[] makeQuadVertexData = this.makeQuadVertexData(blockPartFace, textureAtlasSprite, enumFacing, this.getPositionsDiv16(vector3f, vector3f2), transformation, blockPartRotation, flag, flag2);
      EnumFacing facingFromVertexData = getFacingFromVertexData(makeQuadVertexData);
      if (flag) {
         this.lockUv(makeQuadVertexData, facingFromVertexData, blockPartFace.blockFaceUV, textureAtlasSprite);
      }

      if (blockPartRotation == null) {
         this.applyFacing(makeQuadVertexData, facingFromVertexData);
      }

      if (Reflector.ForgeHooksClient_fillNormal.exists()) {
         Reflector.call(Reflector.ForgeHooksClient_fillNormal, makeQuadVertexData, facingFromVertexData);
      }

      return new BakedQuad(makeQuadVertexData, blockPartFace.tintIndex, facingFromVertexData);
   }

   public int[] makeQuadVertexData(
      BlockPartFace blockPartFace, TextureAtlasSprite textureAtlasSprite, EnumFacing enumFacing, float[] floats, ITransformation transformation, BlockPartRotation blockPartRotation, boolean flag, boolean flag2
   ) {
      byte b = 28;
      if (Config.isShaders()) {
         b = 56;
      }

      int[] ints = new int[b];

      for (int i = 0; i < 4; i++) {
         this.fillVertexData(ints, i, enumFacing, blockPartFace, floats, textureAtlasSprite, transformation, blockPartRotation, flag, flag2);
      }

      return ints;
   }

   public int getFaceShadeColor(EnumFacing facing) {
      float faceBrightness = getFaceBrightness(facing);
      int n = MathHelper.clamp_int((int)(faceBrightness * 255.0F), 0, 255);
      return 0xFF000000 | n << 16 | n << 8 | n;
   }

   public static float getFaceBrightness(EnumFacing enumFacing) {
      switch (enumFacing) {
         case DOWN:
            if (Config.isShaders()) {
               return Shaders.blockLightLevel05;
            }

            return 0.5F;
         case UP:
            return 1.0F;
         case NORTH:
         case SOUTH:
            if (Config.isShaders()) {
               return Shaders.blockLightLevel08;
            }

            return 0.8F;
         case WEST:
         case EAST:
            if (Config.isShaders()) {
               return Shaders.blockLightLevel06;
            }

            return 0.6F;
         default:
            return 1.0F;
      }
   }

   public float[] getPositionsDiv16(Vector3f pos1, Vector3f pos2) {
      float[] floats = new float[EnumFacing.values().length];
      floats[Constants.WEST_INDEX] = pos1.x / 16.0F;
      floats[Constants.DOWN_INDEX] = pos1.y / 16.0F;
      floats[Constants.NORTH_INDEX] = pos1.z / 16.0F;
      floats[Constants.EAST_INDEX] = pos2.x / 16.0F;
      floats[Constants.UP_INDEX] = pos2.y / 16.0F;
      floats[Constants.SOUTH_INDEX] = pos2.z / 16.0F;
      return floats;
   }

   public void fillVertexData(
      int[] ints,
      int n,
      EnumFacing enumFacing,
      BlockPartFace blockPartFace,
      float[] floats,
      TextureAtlasSprite textureAtlasSprite,
      ITransformation transformation,
      BlockPartRotation blockPartRotation,
      boolean flag,
      boolean flag2
   ) {
      EnumFacing rotate = transformation.rotate(enumFacing);
      int n2 = flag2 ? this.getFaceShadeColor(rotate) : -1;
      VertexInformation vertexInformation = EnumFaceDirection.getFacing(enumFacing).getVertexInformation(n);
      Vector3f vector3f = new Vector3f(floats[vertexInformation.xIndex], floats[vertexInformation.yIndex], floats[vertexInformation.zIndex]);
      this.rotatePart(vector3f, blockPartRotation);
      int rotateVertex = this.rotateVertex(vector3f, enumFacing, n, transformation, flag);
      BlockModelUtils.snapVertexPosition(vector3f);
      this.storeVertexData(ints, rotateVertex, n, vector3f, n2, textureAtlasSprite, blockPartFace.blockFaceUV);
   }

   public void storeVertexData(
      int[] faceData, int storeIndex, int vertexIndex, Vector3f position, int shadeColor, TextureAtlasSprite sprite, BlockFaceUV faceUV
   ) {
      int n = faceData.length / 4;
      int n2 = storeIndex * n;
      faceData[n2] = Float.floatToRawIntBits(position.x);
      faceData[n2 + 1] = Float.floatToRawIntBits(position.y);
      faceData[n2 + 2] = Float.floatToRawIntBits(position.z);
      faceData[n2 + 3] = shadeColor;
      faceData[n2 + 4] = Float.floatToRawIntBits(
         sprite.getInterpolatedU(faceUV.computeFloat(vertexIndex) * 0.999 + faceUV.computeFloat((vertexIndex + 2) % 4) * 0.001)
      );
      faceData[n2 + 4 + 1] = Float.floatToRawIntBits(
         sprite.getInterpolatedV(faceUV.computeFloat2(vertexIndex) * 0.999 + faceUV.computeFloat2((vertexIndex + 2) % 4) * 0.001)
      );
   }

   public void rotatePart(Vector3f vector3f, BlockPartRotation partRotation) {
      if (partRotation != null) {
         Matrix4f matrixIdentity = this.getMatrixIdentity();
         Vector3f vector3f2 = new Vector3f(0.0F, 0.0F, 0.0F);
         switch (partRotation.axis) {
            case X:
               Matrix4f.rotate(partRotation.angle * (float) (Math.PI / 180.0), new Vector3f(1.0F, 0.0F, 0.0F), matrixIdentity, matrixIdentity);
               vector3f2.set(0.0F, 1.0F, 1.0F);
               break;
            case Y:
               Matrix4f.rotate(partRotation.angle * (float) (Math.PI / 180.0), new Vector3f(0.0F, 1.0F, 0.0F), matrixIdentity, matrixIdentity);
               vector3f2.set(1.0F, 0.0F, 1.0F);
               break;
            case Z:
               Matrix4f.rotate(partRotation.angle * (float) (Math.PI / 180.0), new Vector3f(0.0F, 0.0F, 1.0F), matrixIdentity, matrixIdentity);
               vector3f2.set(1.0F, 1.0F, 0.0F);
         }

         if (partRotation.rescale) {
            if (Math.abs(partRotation.angle) == 22.5F) {
               vector3f2.scale(SCALE_ROTATION_22_5);
            } else {
               vector3f2.scale(SCALE_ROTATION_GENERAL);
            }

            Vector3f.add(vector3f2, new Vector3f(1.0F, 1.0F, 1.0F), vector3f2);
         } else {
            vector3f2.set(1.0F, 1.0F, 1.0F);
         }

         this.rotateScale(vector3f, new Vector3f(partRotation.origin), matrixIdentity, vector3f2);
      }
   }

   public int rotateVertex(Vector3f position, EnumFacing facing, int vertexIndex, ModelRotation modelRotationIn, boolean uvLocked) {
      return this.rotateVertex(position, facing, vertexIndex, modelRotationIn, uvLocked);
   }

   public int rotateVertex(Vector3f vector3f, EnumFacing enumFacing, int n, ITransformation transformation, boolean flag) {
      if (transformation == ModelRotation.X0_Y0) {
         return n;
      }

      if (Reflector.ForgeHooksClient_transform.exists()) {
         Reflector.call(Reflector.ForgeHooksClient_transform, vector3f, transformation.createMatrix4f());
      } else {
         this.rotateScale(vector3f, new Vector3f(0.5F, 0.5F, 0.5F), ((ModelRotation)transformation).getMatrix4d(), new Vector3f(1.0F, 1.0F, 1.0F));
      }

      return transformation.rotate(enumFacing, n);
   }

   public void rotateScale(Vector3f position, Vector3f rotationOrigin, Matrix4f rotationMatrix, Vector3f scale) {
      Vector4f vector4f = new Vector4f(position.x - rotationOrigin.x, position.y - rotationOrigin.y, position.z - rotationOrigin.z, 1.0F);
      Matrix4f.transform(rotationMatrix, vector4f, vector4f);
      vector4f.x = vector4f.x * scale.x;
      vector4f.y = vector4f.y * scale.y;
      vector4f.z = vector4f.z * scale.z;
      position.set(vector4f.x + rotationOrigin.x, vector4f.y + rotationOrigin.y, vector4f.z + rotationOrigin.z);
   }

   public Matrix4f getMatrixIdentity() {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.setIdentity();
      return matrix4f;
   }

   public static EnumFacing getFacingFromVertexData(int[] faceData) {
      int n = faceData.length / 4;
      int n2 = n * 2;
      int n3 = n * 3;
      Vector3f vector3f = new Vector3f(Float.intBitsToFloat(faceData[0]), Float.intBitsToFloat(faceData[1]), Float.intBitsToFloat(faceData[2]));
      Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(faceData[n]), Float.intBitsToFloat(faceData[n + 1]), Float.intBitsToFloat(faceData[n + 2]));
      Vector3f vector3f3 = new Vector3f(Float.intBitsToFloat(faceData[n2]), Float.intBitsToFloat(faceData[n2 + 1]), Float.intBitsToFloat(faceData[n2 + 2]));
      Vector3f vector3f4 = new Vector3f();
      Vector3f vector3f5 = new Vector3f();
      Vector3f vector3f6 = new Vector3f();
      Vector3f.sub(vector3f, vector3f2, vector3f4);
      Vector3f.sub(vector3f3, vector3f2, vector3f5);
      Vector3f.cross(vector3f5, vector3f4, vector3f6);
      float sqrt = (float)Math.sqrt(vector3f6.x * vector3f6.x + vector3f6.y * vector3f6.y + vector3f6.z * vector3f6.z);
      vector3f6.x /= sqrt;
      vector3f6.y /= sqrt;
      vector3f6.z /= sqrt;
      EnumFacing enumFacing = null;
      float f = 0.0F;

      for (EnumFacing enumFacing2 : EnumFacing.values()) {
         Vec3i directionVec = enumFacing2.getDirectionVec();
         Vector3f vector3f7 = new Vector3f(directionVec.getX(), directionVec.getY(), directionVec.getZ());
         float dot = Vector3f.dot(vector3f6, vector3f7);
         if (dot >= 0.0F && dot > f) {
            f = dot;
            enumFacing = enumFacing2;
         }
      }

      return enumFacing == null ? EnumFacing.UP : enumFacing;
   }

   public void lockUv(int[] ints, EnumFacing facing, BlockFaceUV blockFaceUV, TextureAtlasSprite textureAtlasSprite) {
      for (int i = 0; i < 4; i++) {
         this.lockVertexUv(i, ints, facing, blockFaceUV, textureAtlasSprite);
      }
   }

   public void applyFacing(int[] ints, EnumFacing enumFacing) {
      int[] ints2 = new int[ints.length];
      System.arraycopy(ints, 0, ints2, 0, ints.length);
      float[] floats = new float[EnumFacing.values().length];
      floats[Constants.WEST_INDEX] = 999.0F;
      floats[Constants.DOWN_INDEX] = 999.0F;
      floats[Constants.NORTH_INDEX] = 999.0F;
      floats[Constants.EAST_INDEX] = -999.0F;
      floats[Constants.UP_INDEX] = -999.0F;
      floats[Constants.SOUTH_INDEX] = -999.0F;
      int n = ints.length / 4;

      for (int i = 0; i < 4; i++) {
         int n2 = n * i;
         float intBitsToFloat = Float.intBitsToFloat(ints2[n2]);
         float intBitsToFloat2 = Float.intBitsToFloat(ints2[n2 + 1]);
         float intBitsToFloat3 = Float.intBitsToFloat(ints2[n2 + 2]);
         if (intBitsToFloat < floats[Constants.WEST_INDEX]) {
            floats[Constants.WEST_INDEX] = intBitsToFloat;
         }

         if (intBitsToFloat2 < floats[Constants.DOWN_INDEX]) {
            floats[Constants.DOWN_INDEX] = intBitsToFloat2;
         }

         if (intBitsToFloat3 < floats[Constants.NORTH_INDEX]) {
            floats[Constants.NORTH_INDEX] = intBitsToFloat3;
         }

         if (intBitsToFloat > floats[Constants.EAST_INDEX]) {
            floats[Constants.EAST_INDEX] = intBitsToFloat;
         }

         if (intBitsToFloat2 > floats[Constants.UP_INDEX]) {
            floats[Constants.UP_INDEX] = intBitsToFloat2;
         }

         if (intBitsToFloat3 > floats[Constants.SOUTH_INDEX]) {
            floats[Constants.SOUTH_INDEX] = intBitsToFloat3;
         }
      }

      EnumFaceDirection facing = EnumFaceDirection.getFacing(enumFacing);

      for (int i = 0; i < 4; i++) {
         int n2 = n * i;
         VertexInformation vertexInformation = facing.getVertexInformation(i);
         float f = floats[vertexInformation.xIndex];
         float f2 = floats[vertexInformation.yIndex];
         float f3 = floats[vertexInformation.zIndex];
         ints[n2] = Float.floatToRawIntBits(f);
         ints[n2 + 1] = Float.floatToRawIntBits(f2);
         ints[n2 + 2] = Float.floatToRawIntBits(f3);

         for (int j = 0; j < 4; j++) {
            int n3 = n * j;
            float intBitsToFloat = Float.intBitsToFloat(ints2[n3]);
            float intBitsToFloat2 = Float.intBitsToFloat(ints2[n3 + 1]);
            float intBitsToFloat3 = Float.intBitsToFloat(ints2[n3 + 2]);
            if (MathHelper.epsilonEquals(f, intBitsToFloat) && MathHelper.epsilonEquals(f2, intBitsToFloat2) && MathHelper.epsilonEquals(f3, intBitsToFloat3)) {
               ints[n2 + 4] = ints2[n3 + 4];
               ints[n2 + 4 + 1] = ints2[n3 + 4 + 1];
            }
         }
      }
   }

   public void lockVertexUv(int n, int[] ints, EnumFacing facing, BlockFaceUV blockFaceUV, TextureAtlasSprite textureAtlasSprite) {
      int n2 = ints.length / 4;
      int n3 = n2 * n;
      float intBitsToFloat = Float.intBitsToFloat(ints[n3]);
      float intBitsToFloat2 = Float.intBitsToFloat(ints[n3 + 1]);
      float intBitsToFloat3 = Float.intBitsToFloat(ints[n3 + 2]);
      if (intBitsToFloat < -0.1F || intBitsToFloat >= 1.1F) {
         intBitsToFloat -= MathHelper.floor_float(intBitsToFloat);
      }

      if (intBitsToFloat2 < -0.1F || intBitsToFloat2 >= 1.1F) {
         intBitsToFloat2 -= MathHelper.floor_float(intBitsToFloat2);
      }

      if (intBitsToFloat3 < -0.1F || intBitsToFloat3 >= 1.1F) {
         intBitsToFloat3 -= MathHelper.floor_float(intBitsToFloat3);
      }

      float f = 0.0F;
      float f2 = 0.0F;
      switch (facing) {
         case DOWN:
            f = intBitsToFloat * 16.0F;
            f2 = (1.0F - intBitsToFloat3) * 16.0F;
            break;
         case UP:
            f = intBitsToFloat * 16.0F;
            f2 = intBitsToFloat3 * 16.0F;
            break;
         case NORTH:
            f = (1.0F - intBitsToFloat) * 16.0F;
            f2 = (1.0F - intBitsToFloat2) * 16.0F;
            break;
         case SOUTH:
            f = intBitsToFloat * 16.0F;
            f2 = (1.0F - intBitsToFloat2) * 16.0F;
            break;
         case WEST:
            f = intBitsToFloat3 * 16.0F;
            f2 = (1.0F - intBitsToFloat2) * 16.0F;
            break;
         case EAST:
            f = (1.0F - intBitsToFloat3) * 16.0F;
            f2 = (1.0F - intBitsToFloat2) * 16.0F;
      }

      int n4 = blockFaceUV.getDirectionIndex(n) * n2;
      ints[n4 + 4] = Float.floatToRawIntBits(textureAtlasSprite.getInterpolatedU(f));
      ints[n4 + 4 + 1] = Float.floatToRawIntBits(textureAtlasSprite.getInterpolatedV(f2));
   }
}
