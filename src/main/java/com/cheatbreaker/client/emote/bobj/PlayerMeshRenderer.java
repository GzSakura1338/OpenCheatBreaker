package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.MeshMaterial;
import com.cheatbreaker.client.emote.Transform;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class PlayerMeshRenderer extends EntityMeshRenderer {
   public ItemStack itemStack = null;
   public static final int HELMET_SLOT = 4;
   public static Map<String, ResourceLocation> armorTextureCache = Maps.newHashMap();
   public static final int CHESTPLATE_SLOT = 3;
   public static final int BOOTS_SLOT = 1;
   public static final int LEGGINGS_SLOT = 2;
   public float heldItemScale = 0.0F;

   @Override
   public void renderModel(EntityLivingBase entityLivingBase, RenderableMesh renderableMesh, float f, float f2) {
      this.updateArmorMaterials(entityLivingBase);
      super.renderModel(entityLivingBase, renderableMesh, f, f2);
   }

   public void updateArmorMaterial(MeshMaterial meshMaterial, EntityLivingBase entityLivingBase, int n) {
      ItemStack equipmentInSlot = entityLivingBase.getEquipmentInSlot(n);
      if (equipmentInSlot != null && equipmentInSlot.getItem() instanceof ItemArmor) {
         ItemArmor item = (ItemArmor)equipmentInSlot.getItem();
         meshMaterial.visible = true;
         meshMaterial.texture = this.getArmorTexture(entityLivingBase, equipmentInSlot, n, null);
         meshMaterial.color = -1;
         if (item.hasColor(equipmentInSlot)) {
            meshMaterial.color = item.getColor(equipmentInSlot);
         }
      } else {
         meshMaterial.visible = false;
         meshMaterial.color = -1;
      }
   }

   public PlayerMeshRenderer(String str, NBTTagCompound nbttagCompound) {
      super(str, nbttagCompound);
   }

   public ResourceLocation getArmorTexture(Entity entity, ItemStack itemStack, int n, String str) {
      String name = ((ItemArmor)itemStack.getItem()).getArmorMaterial().getName();
      String str2 = "minecraft";
      int indexOf = name.indexOf(58);
      if (indexOf != -1) {
         str2 = name.substring(0, indexOf);
         name = name.substring(indexOf + 1);
      }

      String format = String.format(
         "%s:textures/models/armor/%s_layer_%d%s.png", str2, name, this.isLeggingsSlot(n) ? 2 : 1, str == null ? "" : String.format("_%s", str)
      );
      ResourceLocation resourceLocation = armorTextureCache.get(format);
      if (resourceLocation == null) {
         resourceLocation = new ResourceLocation(format);
         armorTextureCache.put(format, resourceLocation);
      }

      return resourceLocation;
   }

   public void updateArmorMaterials(EntityLivingBase entityLivingBase) {
      MeshMaterial meshMaterial = this.modelDefinition.meshMaterials.get("armor_helmet");
      MeshMaterial meshMaterial2 = this.modelDefinition.meshMaterials.get("armor_chest");
      MeshMaterial meshMaterial3 = this.modelDefinition.meshMaterials.get("armor_leggings");
      MeshMaterial meshMaterial4 = this.modelDefinition.meshMaterials.get("armor_feet");
      if (meshMaterial != null) {
         this.updateArmorMaterial(meshMaterial, entityLivingBase, 4);
      }

      if (meshMaterial2 != null) {
         this.updateArmorMaterial(meshMaterial2, entityLivingBase, 3);
      }

      if (meshMaterial3 != null) {
         this.updateArmorMaterial(meshMaterial3, entityLivingBase, 2);
      }

      if (meshMaterial4 != null) {
         this.updateArmorMaterial(meshMaterial4, entityLivingBase, 1);
      }
   }

   public boolean isLeggingsSlot(int n) {
      return n == 2;
   }

   @Override
   public void renderHeldItems(EntityLivingBase entityLivingBase, BobjArmature bobjArmature) {
      if (this.modelDefinition.renderHeldItems) {
         float scaleItems = this.modelDefinition.scaleItems;
         ItemStack heldItem = entityLivingBase.getHeldItem();
         if (this.itemStack != null) {
            if (this.heldItemScale > 0.0F) {
               for (Transform transform : this.modelDefinition.rightHands.values()) {
                  this.renderHeldItem(entityLivingBase, this.itemStack, bobjArmature, transform, TransformType.THIRD_PERSON, scaleItems * this.heldItemScale);
               }
            }
         } else if (heldItem != null && this.modelDefinition.rightHands != null) {
            for (Transform transform : this.modelDefinition.rightHands.values()) {
               this.renderHeldItem(entityLivingBase, heldItem, bobjArmature, transform, TransformType.THIRD_PERSON, scaleItems);
            }
         }
      }
   }
}
