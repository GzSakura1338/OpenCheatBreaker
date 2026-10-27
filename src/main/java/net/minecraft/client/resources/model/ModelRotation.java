package net.minecraft.client.resources.model;

import com.cheatbreaker.client.util.ForgeStub;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ITransformation;
import net.optifine.reflect.Reflector;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public enum ModelRotation implements ITransformation, IModelState {
   X0_Y0(0, 0),
   X0_Y90(0, 90),
   X0_Y180(0, 180),
   X0_Y270(0, 270),
   X90_Y0(90, 0),
   X90_Y90(90, 90),
   X90_Y180(90, 180),
   X90_Y270(90, 270),
   X180_Y0(180, 0),
   X180_Y90(180, 90),
   X180_Y180(180, 180),
   X180_Y270(180, 270),
   X270_Y0(270, 0),
   X270_Y90(270, 90),
   X270_Y180(270, 180),
   X270_Y270(270, 270);

   public int combinedXY;
   public Matrix4f matrix4d;
   public int quartersX;
   public int quartersY;
   public static Map<Integer, ModelRotation> mapRotations = Maps.newHashMap();

   public static int combineXY(int n, int n2) {
      return n * 360 + n2;
   }

   ModelRotation(int n, int n2) {
      this.combinedXY = combineXY(n, n2);
      this.matrix4d = new Matrix4f();
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.setIdentity();
      Matrix4f.rotate(-n * (float) (Math.PI / 180.0), new Vector3f(1.0F, 0.0F, 0.0F), matrix4f, matrix4f);
      this.quartersX = MathHelper.abs_int(n / 90);
      Matrix4f matrix4f2 = new Matrix4f();
      matrix4f2.setIdentity();
      Matrix4f.rotate(-n2 * (float) (Math.PI / 180.0), new Vector3f(0.0F, 1.0F, 0.0F), matrix4f2, matrix4f2);
      this.quartersY = MathHelper.abs_int(n2 / 90);
      Matrix4f.mul(matrix4f2, matrix4f, this.matrix4d);
   }

   public Matrix4f getMatrix4d() {
      return this.matrix4d;
   }

   public EnumFacing rotateFace(EnumFacing enumFacing) {
      EnumFacing enumFacing2 = enumFacing;

      for (int i = 0; i < this.quartersX; i++) {
         enumFacing2 = enumFacing2.rotateAround(Axis.X);
      }

      if (enumFacing2.getAxis() != Axis.Y) {
         for (int i = 0; i < this.quartersY; i++) {
            enumFacing2 = enumFacing2.rotateAround(Axis.Y);
         }
      }

      return enumFacing2;
   }

   public int rotateVertex(EnumFacing facing, int vertexIndex) {
      int vertexIndex2 = vertexIndex;
      if (facing.getAxis() == Axis.X) {
         vertexIndex2 = (vertexIndex + this.quartersX) % 4;
      }

      EnumFacing facing2 = facing;

      for (int i = 0; i < this.quartersX; i++) {
         facing2 = facing2.rotateAround(Axis.X);
      }

      if (facing2.getAxis() == Axis.Y) {
         vertexIndex2 = (vertexIndex2 + this.quartersY) % 4;
      }

      return vertexIndex2;
   }

   public static ModelRotation getModelRotation(int n, int n2) {
      return mapRotations.get(combineXY(MathHelper.normalizeAngle(n, 360), MathHelper.normalizeAngle(n2, 360)));
   }

   @Override
   public Optional<ForgeStub> apply(Optional<? extends IModelPart> optional) {
      return (Optional<ForgeStub>)Reflector.call(Reflector.ForgeHooksClient_applyTransform, this.createMatrix4f(), optional);
   }

   @Override
   public EnumFacing rotate(EnumFacing enumFacing) {
      return this.rotateFace(enumFacing);
   }

   @Override
   public int rotate(EnumFacing enumFacing, int n) {
      return this.rotateVertex(enumFacing, n);
   }

   static {
      for (ModelRotation modelRotation : values()) {
         mapRotations.put(modelRotation.combinedXY, modelRotation);
      }
   }

   @Override
   public javax.vecmath.Matrix4f createMatrix4f() {
      return Reflector.ForgeHooksClient_getMatrix.exists()
         ? (javax.vecmath.Matrix4f)Reflector.call(Reflector.ForgeHooksClient_getMatrix, this)
         : new javax.vecmath.Matrix4f();
   }
}
