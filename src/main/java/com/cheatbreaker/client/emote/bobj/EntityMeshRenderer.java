package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.BlockModelParser;
import com.cheatbreaker.client.emote.Bone;
import com.cheatbreaker.client.emote.EmoteCapeLayer;
import com.cheatbreaker.client.emote.EmoteEntityRenderer;
import com.cheatbreaker.client.emote.EmoteWingsLayer;
import com.cheatbreaker.client.emote.ModelDefinition;
import com.cheatbreaker.client.emote.Transform;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class EntityMeshRenderer {
   public String modelName;
   public MeshAnimatorFactory animatorFactory;
   public static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
   public ActionPlayer forcedAction;
   public int reloadCounter;
   public long lastModified;
   public MeshModel model;
   public ModelDefinition modelDefinition;
   public MeshAnimator animator;
   public static final float headOffsetY = 1.5078125F;
   public Vector4f vector4f;
   public static float[] matrixArray = new float[16];
   public Matrix4f matrix4f;
   public static MeshAnimatorFactory defaultAnimatorFactory = PlayerMeshAnimator::new;
   public static EmoteCapeLayer defaultOption = new EmoteCapeLayer(Minecraft.getMinecraft().getRenderManager().getSkinMap().get("default"));
   public Minecraft mc;
   public NBTTagCompound nbttagCompound;
   public static EmoteWingsLayer wingLayer = new EmoteWingsLayer();
   public ModelDefinitionEntry definitionEntry;

   public void updatePose(EntityLivingBase entityLivingBase, BobjArmature bobjArmature, float f, float f2) {
      for (Bone bone : bobjArmature.bones) {
         bone.resetPose();
      }

      this.applyHeadRotation(entityLivingBase, bobjArmature, f, f2);

      for (Bone bone : bobjArmature.bones) {
         bobjArmature.boneMatrices[bone.index] = bone.computeSkinMatrix();
      }
   }

   public void applyHeadRotation(EntityLivingBase entityLivingBase, BobjArmature bobjArmature, float f, float f2) {
      Bone bone = bobjArmature.bonesByName.get(this.modelDefinition.head);
      if (bone != null) {
         float f3 = entityLivingBase.prevRotationYawHead + (entityLivingBase.rotationYawHead - entityLivingBase.prevRotationYawHead) * f2;
         float f4 = entityLivingBase.prevRotationPitch + (entityLivingBase.rotationPitch - entityLivingBase.prevRotationPitch) * f2;
         f3 = (f - f3) / 180.0F * (float) Math.PI;
         bone.rotationX = f4 / 180.0F * (float) Math.PI;
         bone.rotationY = f3;
      }

      if (this.animator != null) {
         this.animator.applyActions(bobjArmature, f2);
      }
   }

   public void setForcedAction(ActionPlayer actionPlayer) {
      this.forcedAction = actionPlayer;
      if (this.animator != null) {
         this.animator.setForcedAction(actionPlayer);
      }
   }

   public void renderHeldItem(EntityLivingBase entityLivingBase, ItemStack itemStack, BobjArmature bobjArmature, Transform transform, TransformType transformType, float f) {
   }

   public void renderHeadItem(EntityLivingBase entityLivingBase, Bone bone) {
      ItemStack equipmentInSlot = entityLivingBase.getEquipmentInSlot(4);
      if (equipmentInSlot != null && bone != null) {
         Item item = equipmentInSlot.getItem();
         if (!(item instanceof ItemArmor)) {
            GlStateManager.pushMatrix();
            this.applyBoneTransform(bone);
            GlStateManager.translate(0.0F, 0.25F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.625F, 0.625F, 0.625F);
            this.mc.getItemRenderer().renderItem(entityLivingBase, equipmentInSlot, TransformType.HEAD);
            GlStateManager.popMatrix();
         }
      }
   }

   public Vector4f getBonePosition(EntityLivingBase entityLivingBase, Bone bone, float f, float f2, float f3, float f4) {
      this.vector4f.set(f, f2, f3, 1.0F);
      bone.worldMatrix.transform(this.vector4f);
      this.matrix4f.setIdentity();
      this.matrix4f.rotY((180.0F - entityLivingBase.renderYawOffset + 180.0F) / 180.0F * (float) Math.PI);
      this.matrix4f.transform(this.vector4f);
      this.vector4f.scale(0.9375F);
      float f5 = (float)(entityLivingBase.lastTickPosX + (entityLivingBase.posX - entityLivingBase.lastTickPosX) * f4);
      float f6 = (float)(entityLivingBase.lastTickPosY + (entityLivingBase.posY - entityLivingBase.lastTickPosY) * f4);
      float f7 = (float)(entityLivingBase.lastTickPosZ + (entityLivingBase.posZ - entityLivingBase.lastTickPosZ) * f4);
      this.vector4f.x += f5;
      this.vector4f.y += f6;
      this.vector4f.z += f7;
      return this.vector4f;
   }

   public void tick(EntityLivingBase entityLivingBase) {
      this.loadModel();
      if (this.animator != null) {
         this.checkForReload();
         this.animator.tick(entityLivingBase);
      }
   }

   public void applyBoneTransform(Bone bone) {
      this.multiplyMatrix(bone.worldMatrix);
   }

   public void renderHeldItems(EntityLivingBase entityLivingBase, BobjArmature bobjArmature) {
      if (this.modelDefinition.renderHeldItems) {
         float scaleItems = this.modelDefinition.scaleItems;
         ItemStack heldItem = entityLivingBase.getHeldItem();
         if (heldItem != null && this.modelDefinition.rightHands != null) {
            for (Transform transform : this.modelDefinition.rightHands.values()) {
               this.renderHeldItem(entityLivingBase, heldItem, bobjArmature, transform, TransformType.THIRD_PERSON, scaleItems);
            }
         }
      }
   }

   public void checkForReload() {
      this.reloadCounter++;
      if (this.reloadCounter > 10) {
         this.reloadCounter = 0;
         if (this.lastModified < this.definitionEntry.lastModified) {
            this.model = null;
            this.loadModel();
         }
      }
   }

   public void renderGui(EntityPlayer entityPlayer, int n, int n2, float f, float f2) {
      this.loadModel();
      if (this.model != null && this.model.meshes.size() > 0) {
         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableAlpha();
         GlStateManager.enableDepth();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         float scaleGUI = this.modelDefinition.scaleGUI;
         GL11.glPushMatrix();
         GL11.glTranslatef(n, n2, 0.0F);
         GL11.glScalef(f * scaleGUI, -f * scaleGUI, f * scaleGUI);
         GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
         float rotationYawHead = entityPlayer.rotationYawHead;
         float prevRotationYawHead = entityPlayer.prevRotationYawHead;
         float rotationPitch = entityPlayer.rotationPitch;
         float prevRotationPitch = entityPlayer.prevRotationPitch;
         entityPlayer.prevRotationYawHead = 0.0F;
         entityPlayer.rotationYawHead = 0.0F;
         entityPlayer.prevRotationPitch = 0.0F;
         entityPlayer.rotationPitch = 0.0F;
         this.renderModel(entityPlayer, this.model.meshes.get(0), 0.0F, 0.0F);
         entityPlayer.rotationYawHead = rotationYawHead;
         entityPlayer.prevRotationYawHead = prevRotationYawHead;
         entityPlayer.rotationPitch = rotationPitch;
         entityPlayer.prevRotationPitch = prevRotationPitch;
         GL11.glPopMatrix();
         GlStateManager.disableDepth();
         GlStateManager.disableAlpha();
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableRescaleNormal();
      }
   }

   public void render(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
      if (this.model != null && this.model.meshes.size() > 0) {
         GlStateManager.disableCull();
         GlStateManager.enableAlpha();
         float f3 = entityLivingBase.prevRenderYawOffset + (entityLivingBase.renderYawOffset - entityLivingBase.prevRenderYawOffset) * f2;
         if (entityLivingBase.isRiding()) {
            Entity ridingEntity = entityLivingBase.ridingEntity;
            if (ridingEntity instanceof EntityLivingBase) {
               EntityLivingBase entityLivingBase2 = (EntityLivingBase)ridingEntity;
               f3 = entityLivingBase2.prevRenderYawOffset + (entityLivingBase2.renderYawOffset - entityLivingBase2.prevRenderYawOffset) * f2;
            } else {
               f3 = ridingEntity.prevRotationYaw + (ridingEntity.rotationYaw - ridingEntity.prevRotationYaw) * f2;
            }

            if (ridingEntity instanceof EntityMinecart) {
               f3 += 90.0F;
            }
         }

         float scale = this.modelDefinition.scale;
         GL11.glPushMatrix();
         GL11.glTranslated(d, d2, d3);
         GL11.glScalef(scale, scale, scale);
         if (entityLivingBase.isPlayerSleeping()) {
            GlStateManager.rotate(((EntityPlayer)entityLivingBase).getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GL11.glRotatef(180.0F - (f3 - 180.0F), 0.0F, 1.0F, 0.0F);
         }

         this.renderModel(entityLivingBase, this.model.meshes.get(0), f3, f2);
         GL11.glPopMatrix();
         GlStateManager.enableCull();
      }
   }

   public void loadModel() {
      if (this.model == null) {
         MeshModel model = ModelManager.instance.getModel(this.modelName);
         if (model != null) {
            this.model = model;
            this.definitionEntry = ModelManager.instance.getDefinition(model.name);
            this.modelDefinition.copyFrom(this.definitionEntry.modelDefinition);
            this.modelDefinition.readFromNbt(this.nbttagCompound);
            this.animator = this.animatorFactory.createAnimator(this);
            this.animator.setForcedAction(this.forcedAction);
            this.lastModified = this.definitionEntry.lastModified;
         }
      }
   }

   public void setModel(String str, NBTTagCompound nbttagCompound) {
      this.model = null;
      this.animator = null;
      this.modelName = str;
      this.nbttagCompound = nbttagCompound;
   }

   public void multiplyMatrix(Matrix4f matrix4f) {
      matrixArray[0] = matrix4f.m00;
      matrixArray[1] = matrix4f.m10;
      matrixArray[2] = matrix4f.m20;
      matrixArray[3] = matrix4f.m30;
      matrixArray[4] = matrix4f.m01;
      matrixArray[5] = matrix4f.m11;
      matrixArray[6] = matrix4f.m21;
      matrixArray[7] = matrix4f.m31;
      matrixArray[8] = matrix4f.m02;
      matrixArray[9] = matrix4f.m12;
      matrixArray[10] = matrix4f.m22;
      matrixArray[11] = matrix4f.m32;
      matrixArray[12] = matrix4f.m03;
      matrixArray[13] = matrix4f.m13;
      matrixArray[14] = matrix4f.m23;
      matrixArray[15] = matrix4f.m33;
      ((Buffer)matrixBuffer).clear();
      matrixBuffer.put(matrixArray);
      ((Buffer)matrixBuffer).flip();
      GL11.glMultMatrix(matrixBuffer);
   }

   public void renderModel(EntityLivingBase entityLivingBase, RenderableMesh renderableMesh, float f, float f2) {
      BobjArmature armature = renderableMesh.armature;
      this.updatePose(entityLivingBase, armature, f, f2);

      for (RenderableMesh renderableMesh2 : this.model.meshes) {
         renderableMesh2.updateSkinning();
      }

      GlStateManager.enableRescaleNormal();
      boolean checkEntityLivingBase = EmoteEntityRenderer.checkEntityLivingBase(entityLivingBase, f2);
      this.model.render(this.modelDefinition.meshMaterials);
      if (checkEntityLivingBase) {
         EmoteEntityRenderer.run2();
      }

      GlStateManager.disableRescaleNormal();
      boolean checkEntityLivingBase2 = EmoteEntityRenderer.checkEntityLivingBase(entityLivingBase, f2);
      GlStateManager.pushMatrix();
      defaultOption.doRenderLayer2(entityLivingBase, 0.0F, 0.0F, f2, 0.0F, 0.0F, 0.0F, 0.0F);
      GlStateManager.popMatrix();
      if (checkEntityLivingBase2) {
         EmoteEntityRenderer.run2();
      }

      boolean checkEntityLivingBase3 = EmoteEntityRenderer.checkEntityLivingBase(entityLivingBase, f2);
      GlStateManager.pushMatrix();
      wingLayer.handleEntityLivingBase(entityLivingBase, f2);
      GlStateManager.popMatrix();
      if (checkEntityLivingBase3) {
         EmoteEntityRenderer.run2();
      }

      BlockModelParser forEntity = BlockModelParser.forEntity(entityLivingBase);
      Bone bone = armature.bonesByName.get(this.modelDefinition.head);
      if (forEntity != null && bone != null) {
         boolean checkEntityLivingBase4 = EmoteEntityRenderer.checkEntityLivingBase(entityLivingBase, f2);
         GlStateManager.pushMatrix();
         this.applyBoneTransform(bone);
         forEntity.renderStatic();
         GlStateManager.popMatrix();
         if (checkEntityLivingBase4) {
            EmoteEntityRenderer.run2();
         }
      }

      this.renderHeldItems(entityLivingBase, armature);
      this.renderHeadItem(entityLivingBase, armature.bonesByName.get(this.modelDefinition.head));
   }

   public EntityMeshRenderer(String str, NBTTagCompound nbttagCompound) {
      this.animatorFactory = defaultAnimatorFactory;
      this.modelDefinition = new ModelDefinition();
      this.vector4f = new Vector4f();
      this.matrix4f = new Matrix4f();
      this.setModel(str, nbttagCompound);
      this.mc = Minecraft.getMinecraft();
   }
}
