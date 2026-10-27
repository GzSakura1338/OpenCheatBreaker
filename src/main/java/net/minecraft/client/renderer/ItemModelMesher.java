package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ISmartItemModel;
import net.optifine.Config;
import net.optifine.CustomItems;
import net.optifine.reflect.Reflector;

public class ItemModelMesher {
   public Map<Integer, ModelResourceLocation> simpleShapes = Maps.newHashMap();
   public Map<Integer, IBakedModel> simpleShapesCache = Maps.newHashMap();
   public Map<Item, ItemMeshDefinition> shapers = Maps.newHashMap();
   public ModelManager modelManager;

   public ItemModelMesher(ModelManager modelManager) {
      this.modelManager = modelManager;
   }

   public TextureAtlasSprite getParticleIcon(Item item) {
      return this.getParticleIcon(item, 0);
   }

   public TextureAtlasSprite getParticleIcon(Item item, int meta) {
      return this.getItemModel(new ItemStack(item, 1, meta)).getParticleTexture();
   }

   public IBakedModel getItemModel(ItemStack stack) {
      Item item = stack.getItem();
      IBakedModel itemModel = this.getItemModel(item, this.getMetadata(stack));
      if (itemModel == null) {
         ItemMeshDefinition itemMeshDefinition = this.shapers.get(item);
         if (itemMeshDefinition != null) {
            itemModel = this.modelManager.getModel(itemMeshDefinition.getModelLocation(stack));
         }
      }

      if (Reflector.ForgeHooksClient.exists() && itemModel instanceof ISmartItemModel) {
         itemModel = ((ISmartItemModel)itemModel).handleItemState(stack);
      }

      if (itemModel == null) {
         itemModel = this.modelManager.getMissingModel();
      }

      if (Config.isCustomItems()) {
         itemModel = CustomItems.getCustomItemModel(stack, itemModel, (ResourceLocation)null, true);
      }

      return itemModel;
   }

   public int getMetadata(ItemStack stack) {
      return stack.isItemStackDamageable() ? 0 : stack.getMetadata();
   }

   public IBakedModel getItemModel(Item item, int meta) {
      return this.simpleShapesCache.get(this.getIndex(item, meta));
   }

   public int getIndex(Item item, int meta) {
      return Item.getIdFromItem(item) << 16 | meta;
   }

   public void register(Item item, int meta, ModelResourceLocation location) {
      this.simpleShapes.put(this.getIndex(item, meta), location);
      this.simpleShapesCache.put(this.getIndex(item, meta), this.modelManager.getModel(location));
   }

   public void register(Item item, ItemMeshDefinition definition) {
      this.shapers.put(item, definition);
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void rebuildCache() {
      this.simpleShapesCache.clear();

      for (Entry entry : this.simpleShapes.entrySet()) {
         this.simpleShapesCache.put((Integer)entry.getKey(), this.modelManager.getModel((ModelResourceLocation)entry.getValue()));
      }
   }
}
