package net.optifine.entity.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.entity.model.anim.ModelResolver;
import net.optifine.entity.model.anim.ModelUpdater;

public class CustomEntityModels {
   public static boolean active = false;
   public static Map<Class, Render> originalEntityRenderMap = null;
   public static Map<Class, TileEntitySpecialRenderer> originalTileEntityRenderMap = null;

   public static void update() {
      Map entityRenderMap = getEntityRenderMap();
      Map tileEntityRenderMap = getTileEntityRenderMap();
      if (entityRenderMap == null) {
         Config.warn("Entity render map not found, custom entity models are DISABLED.");
      } else if (tileEntityRenderMap == null) {
         Config.warn("Tile entity render map not found, custom entity models are DISABLED.");
      } else {
         active = false;
         entityRenderMap.clear();
         tileEntityRenderMap.clear();
         entityRenderMap.putAll(originalEntityRenderMap);
         tileEntityRenderMap.putAll(originalTileEntityRenderMap);
         if (Config.isCustomEntityModels()) {
            ResourceLocation[] modelLocations = getModelLocations();

            for (int i = 0; i < modelLocations.length; i++) {
               ResourceLocation resourceLocation = modelLocations[i];
               Config.dbg("CustomEntityModel: " + resourceLocation.getResourcePath());
               IEntityRenderer parseEntityRender2 = parseEntityRender(resourceLocation);
               if (parseEntityRender2 != null) {
                  Class entityClass = parseEntityRender2.getEntityClass();
                  if (entityClass != null) {
                     if (parseEntityRender2 instanceof Render) {
                        entityRenderMap.put(entityClass, (Render)parseEntityRender2);
                     } else if (parseEntityRender2 instanceof TileEntitySpecialRenderer) {
                        tileEntityRenderMap.put(entityClass, (TileEntitySpecialRenderer)parseEntityRender2);
                     } else {
                        Config.warn("Unknown renderer type: " + parseEntityRender2.getClass().getName());
                     }

                     active = true;
                  }
               }
            }
         }
      }
   }

   public static Map<Class, Render> getEntityRenderMap() {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      Map entityRenderMap = renderManager.getEntityRenderMap();
      if (entityRenderMap == null) {
         return null;
      }

      if (originalEntityRenderMap == null) {
         originalEntityRenderMap = new HashMap<>(entityRenderMap);
      }

      return entityRenderMap;
   }

   public static Map<Class, TileEntitySpecialRenderer> getTileEntityRenderMap() {
      Map mapSpecialRenderers = TileEntityRendererDispatcher.instance.mapSpecialRenderers;
      if (originalTileEntityRenderMap == null) {
         originalTileEntityRenderMap = new HashMap<>(mapSpecialRenderers);
      }

      return mapSpecialRenderers;
   }

   public static ResourceLocation[] getModelLocations() {
      String str = "optifine/cem/";
      String str2 = ".jem";
      ArrayList list = new ArrayList();
      String[] modelNames = CustomModelRegistry.getModelNames();

      for (int i = 0; i < modelNames.length; i++) {
         String str3 = modelNames[i];
         String str4 = str + str3 + str2;
         ResourceLocation resourceLocation = new ResourceLocation(str4);
         if (Config.hasResource(resourceLocation)) {
            list.add(resourceLocation);
         }
      }

      return (ResourceLocation[])list.toArray(new ResourceLocation[list.size()]);
   }

   public static IEntityRenderer parseEntityRender(ResourceLocation resourceLocation) {
      try {
         JsonObject loadJson = CustomEntityModelParser.loadJson(resourceLocation);
         return parseEntityRender(loadJson, resourceLocation.getResourcePath());
      } catch (IOException e) {
         Config.error("" + e.getClass().getName() + ": " + e.getMessage());
         return null;
      } catch (JsonParseException e) {
         Config.error("" + e.getClass().getName() + ": " + e.getMessage());
         return null;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public static IEntityRenderer parseEntityRender(JsonObject jsonObject, String str) {
      CustomEntityRenderer parseEntityRender = CustomEntityModelParser.parseEntityRender(jsonObject, str);
      String name = parseEntityRender.getName();
      ModelAdapter modelAdapter = CustomModelRegistry.getModelAdapter(name);
      checkNull(modelAdapter, "Entity not found: " + name);
      Class entityClass = modelAdapter.getEntityClass();
      checkNull(entityClass, "Entity class not found: " + name);
      IEntityRenderer makeEntityRender2 = makeEntityRender(modelAdapter, parseEntityRender);
      if (makeEntityRender2 == null) {
         return null;
      }

      makeEntityRender2.setEntityClass(entityClass);
      return makeEntityRender2;
   }

   public static IEntityRenderer makeEntityRender(ModelAdapter modelAdapter, CustomEntityRenderer customEntityRenderer) {
      ResourceLocation textureLocation = customEntityRenderer.getTextureLocation();
      CustomModelRenderer[] customModelRenderers = customEntityRenderer.getCustomModelRenderers();
      float shadowSize = customEntityRenderer.getShadowSize();
      if (shadowSize < 0.0F) {
         shadowSize = modelAdapter.getShadowSize();
      }

      ModelBase makeModel = modelAdapter.makeModel();
      if (makeModel == null) {
         return null;
      }

      ModelResolver modelResolver = new ModelResolver(modelAdapter, makeModel, customModelRenderers);
      if (!modifyModel(modelAdapter, makeModel, customModelRenderers, modelResolver)) {
         return null;
      }

      IEntityRenderer makeEntityRender = modelAdapter.makeEntityRender(makeModel, shadowSize);
      if (makeEntityRender == null) {
         throw new JsonParseException("Entity renderer is null, model: " + modelAdapter.getName() + ", adapter: " + modelAdapter.getClass().getName());
      }

      if (textureLocation != null) {
         makeEntityRender.setLocationTextureCustom(textureLocation);
      }

      return makeEntityRender;
   }

   public static boolean modifyModel(ModelAdapter modelAdapter, ModelBase modelBase, CustomModelRenderer[] customModelRenderers, ModelResolver modelResolver) {
      for (int i = 0; i < customModelRenderers.length; i++) {
         CustomModelRenderer customModelRenderer = customModelRenderers[i];
         if (!modifyModel(modelAdapter, modelBase, customModelRenderer, modelResolver)) {
            return false;
         }
      }

      return true;
   }

   public static boolean modifyModel(ModelAdapter modelAdapter, ModelBase modelBase, CustomModelRenderer customModelRenderer, ModelResolver modelResolver) {
      String modelPart = customModelRenderer.getModelPart();
      ModelRenderer modelRenderer = modelAdapter.getModelRenderer(modelBase, modelPart);
      if (modelRenderer == null) {
         Config.warn("Model part not found: " + modelPart + ", model: " + modelBase);
         return false;
      }

      if (!customModelRenderer.isAttach()) {
         if (modelRenderer.cubeList != null) {
            modelRenderer.cubeList.clear();
         }

         if (modelRenderer.spriteList != null) {
            modelRenderer.spriteList.clear();
         }

         if (modelRenderer.childModels != null) {
            ModelRenderer[] modelRenderers = modelAdapter.getModelRenderers(modelBase);
            Set newSetFromMap = Collections.newSetFromMap(new IdentityHashMap());
            newSetFromMap.addAll(Arrays.asList(modelRenderers));
            List childModels = modelRenderer.childModels;
            Iterator iterator = childModels.iterator();

            while (iterator.hasNext()) {
               ModelRenderer modelRenderer2 = (ModelRenderer)iterator.next();
               if (!newSetFromMap.contains(modelRenderer2)) {
                  iterator.remove();
               }
            }
         }
      }

      modelRenderer.addChild(customModelRenderer.getModelRenderer());
      ModelUpdater modelUpdater = customModelRenderer.getModelUpdater();
      if (modelUpdater != null) {
         modelResolver.setThisModelRenderer(customModelRenderer.getModelRenderer());
         modelResolver.setPartModelRenderer(modelRenderer);
         if (!modelUpdater.initialize(modelResolver)) {
            return false;
         }

         customModelRenderer.getModelRenderer().setModelUpdater(modelUpdater);
      }

      return true;
   }

   public static void checkNull(Object obj, String str) {
      if (obj == null) {
         throw new JsonParseException(str);
      }
   }

   public static boolean isActive() {
      return active;
   }
}
