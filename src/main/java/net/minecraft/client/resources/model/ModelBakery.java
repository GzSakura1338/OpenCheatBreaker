package net.minecraft.client.resources.model;

import com.cheatbreaker.client.util.ForgeStub;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variant;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition.Variants;
import net.minecraft.client.renderer.texture.IIconCreator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistrySimple;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ITransformation;
import net.minecraftforge.fml.common.registry.RegistryDelegate;
import net.optifine.CustomItems;
import net.optifine.reflect.Reflector;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBakery {
   public static Set<ResourceLocation> LOCATIONS_BUILTIN_TEXTURES = Sets.newHashSet(
      new ResourceLocation[]{
         new ResourceLocation("blocks/water_flow"),
         new ResourceLocation("blocks/water_still"),
         new ResourceLocation("blocks/lava_flow"),
         new ResourceLocation("blocks/lava_still"),
         new ResourceLocation("blocks/destroy_stage_0"),
         new ResourceLocation("blocks/destroy_stage_1"),
         new ResourceLocation("blocks/destroy_stage_2"),
         new ResourceLocation("blocks/destroy_stage_3"),
         new ResourceLocation("blocks/destroy_stage_4"),
         new ResourceLocation("blocks/destroy_stage_5"),
         new ResourceLocation("blocks/destroy_stage_6"),
         new ResourceLocation("blocks/destroy_stage_7"),
         new ResourceLocation("blocks/destroy_stage_8"),
         new ResourceLocation("blocks/destroy_stage_9"),
         new ResourceLocation("items/empty_armor_slot_helmet"),
         new ResourceLocation("items/empty_armor_slot_chestplate"),
         new ResourceLocation("items/empty_armor_slot_leggings"),
         new ResourceLocation("items/empty_armor_slot_boots")
      }
   );
   public static Logger LOGGER = LogManager.getLogger();
   public static ModelResourceLocation MODEL_MISSING = new ModelResourceLocation("builtin/missing", "missing");
   public static Map<String, String> BUILT_IN_MODELS = Maps.newHashMap();
   public static Joiner JOINER = Joiner.on(" -> ");
   public Map<ResourceLocation, TextureAtlasSprite> sprites = Maps.newHashMap();
   public Map<ResourceLocation, ModelBlock> models = Maps.newLinkedHashMap();
   public Map<ModelResourceLocation, Variants> variants = Maps.newLinkedHashMap();
   public FaceBakery faceBakery = new FaceBakery();
   public ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
   public RegistrySimple<ModelResourceLocation, IBakedModel> bakedRegistry = new RegistrySimple<>();
   public Map<String, ResourceLocation> itemLocations = Maps.newLinkedHashMap();
   public Map<ResourceLocation, ModelBlockDefinition> blockDefinitions = Maps.newHashMap();
   public Map<Item, List<String>> variantNames = Maps.newIdentityHashMap();
   public static ModelBlock MODEL_GENERATED = ModelBlock.deserialize(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   public static ModelBlock MODEL_COMPASS = ModelBlock.deserialize(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   public static ModelBlock MODEL_CLOCK = ModelBlock.deserialize(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   public static ModelBlock MODEL_ENTITY = ModelBlock.deserialize(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   public IResourceManager resourceManager;
   public TextureMap textureMap;
   public BlockModelShapes blockModelShapes;
   public static Map<RegistryDelegate<Item>, Set<String>> customVariantNames = Maps.newHashMap();

   public ModelBakery(IResourceManager resourceManager, TextureMap textureMap, BlockModelShapes blockModelShapes) {
      this.resourceManager = resourceManager;
      this.textureMap = textureMap;
      this.blockModelShapes = blockModelShapes;
   }

   public IRegistry<ModelResourceLocation, IBakedModel> setupModelRegistry() {
      this.loadVariantItemModels();
      this.loadModelsCheck();
      this.loadSprites();
      this.bakeItemModels();
      this.bakeBlockModels();
      return this.bakedRegistry;
   }

   public void loadVariantItemModels() {
      this.loadVariants(this.blockModelShapes.getBlockStateMapper().putAllStateModelLocations().values());
      this.variants
         .put(
            MODEL_MISSING,
            new Variants(
               MODEL_MISSING.getVariant(),
               Lists.newArrayList(
                  new Variant[]{
                     new Variant(new ResourceLocation(MODEL_MISSING.getResourcePath()), ModelRotation.X0_Y0, false, 1)
                  }
               )
            )
         );
      ResourceLocation resourceLocation = new ResourceLocation("item_frame");
      ModelBlockDefinition modelBlockDefinition = this.getModelBlockDefinition(resourceLocation);
      this.registerVariant(modelBlockDefinition, new ModelResourceLocation(resourceLocation, "normal"));
      this.registerVariant(modelBlockDefinition, new ModelResourceLocation(resourceLocation, "map"));
      this.loadVariantModels();
      this.loadItemModels();
   }

   public void loadVariants(Collection<ModelResourceLocation> collection) {
      for (ModelResourceLocation modelResourceLocation : collection) {
         try {
            ModelBlockDefinition modelBlockDefinition = this.getModelBlockDefinition(modelResourceLocation);

            try {
               this.registerVariant(modelBlockDefinition, modelResourceLocation);
            } catch (Exception e) {
               LOGGER.warn("Unable to load variant: " + modelResourceLocation.getVariant() + " from " + modelResourceLocation, e);
            }
         } catch (Exception e) {
            LOGGER.warn("Unable to load definition " + modelResourceLocation, e);
         }
      }
   }

   public void registerVariant(ModelBlockDefinition modelBlockDefinition, ModelResourceLocation modelResourceLocation) {
      this.variants.put(modelResourceLocation, modelBlockDefinition.getVariants(modelResourceLocation.getVariant()));
   }

   public ModelBlockDefinition getModelBlockDefinition(ResourceLocation resourceLocation) {
      ResourceLocation blockStateLocation = this.getBlockStateLocation(resourceLocation);
      ModelBlockDefinition modelBlockDefinition = this.blockDefinitions.get(blockStateLocation);
      if (modelBlockDefinition == null) {
         ArrayList newArrayList = Lists.newArrayList();

         try {
            for (IResource resource : this.resourceManager.getAllResources(blockStateLocation)) {
               InputStream inputStream = null;

               try {
                  inputStream = resource.getInputStream();
                  ModelBlockDefinition parseFromReader = ModelBlockDefinition.parseFromReader(new InputStreamReader(inputStream, Charsets.UTF_8));
                  newArrayList.add(parseFromReader);
               } catch (Exception e) {
                  throw new RuntimeException(
                     "Encountered an exception when loading model definition of '"
                        + resourceLocation
                        + "' from: '"
                        + resource.getResourceLocation()
                        + "' in resourcepack: '"
                        + resource.getResourcePackName()
                        + "'",
                     e
                  );
               } finally {
                  IOUtils.closeQuietly(inputStream);
               }
            }
         } catch (IOException e) {
            throw new RuntimeException("Encountered an exception when loading model definition of model " + blockStateLocation.toString(), e);
         }

         modelBlockDefinition = new ModelBlockDefinition((List<ModelBlockDefinition>)newArrayList);
         this.blockDefinitions.put(blockStateLocation, modelBlockDefinition);
      }

      return modelBlockDefinition;
   }

   public ResourceLocation getBlockStateLocation(ResourceLocation resourceLocation) {
      return new ResourceLocation(resourceLocation.getResourceDomain(), "blockstates/" + resourceLocation.getResourcePath() + ".json");
   }

   public void loadVariantModels() {
      for (ModelResourceLocation modelResourceLocation : this.variants.keySet()) {
         for (Variant variant : this.variants.get(modelResourceLocation).getVariants()) {
            ResourceLocation modelLocation = variant.getModelLocation();
            if (this.models.get(modelLocation) == null) {
               try {
                  ModelBlock loadModel = this.loadModel(modelLocation);
                  this.models.put(modelLocation, loadModel);
               } catch (Exception e) {
                  LOGGER.warn("Unable to load block model: '" + modelLocation + "' for variant: '" + modelResourceLocation + "'", e);
               }
            }
         }
      }
   }

   public ModelBlock loadModel(ResourceLocation resourceLocation) throws IOException {
      String resourcePath = resourceLocation.getResourcePath();
      if ("builtin/generated".equals(resourcePath)) {
         return MODEL_GENERATED;
      }

      if ("builtin/compass".equals(resourcePath)) {
         return MODEL_COMPASS;
      }

      if ("builtin/clock".equals(resourcePath)) {
         return MODEL_CLOCK;
      }

      if ("builtin/entity".equals(resourcePath)) {
         return MODEL_ENTITY;
      }

      Reader reader;
      if (resourcePath.startsWith("builtin/")) {
         String substring = resourcePath.substring("builtin/".length());
         String str = BUILT_IN_MODELS.get(substring);
         if (str == null) {
            throw new FileNotFoundException(resourceLocation.toString());
         }

         reader = new StringReader(str);
      } else {
         resourceLocation = this.getModelLocation(resourceLocation);
         IResource resource = this.resourceManager.getResource(resourceLocation);
         reader = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);
      }

      ModelBlock modelBlock;
      try {
         ModelBlock deserialize = ModelBlock.deserialize(reader);
         deserialize.name = resourceLocation.toString();
         modelBlock = deserialize;
         String basePath = TextureUtils.getBasePath(resourceLocation.getResourcePath());
         fixModelLocations(deserialize, basePath);
      } finally {
         reader.close();
      }

      return modelBlock;
   }

   public ResourceLocation getModelLocation(ResourceLocation resourceLocation) {
      ResourceLocation resourceLocation2 = resourceLocation;
      String resourcePath = resourceLocation.getResourcePath();
      if (!resourcePath.startsWith("mcpatcher") && !resourcePath.startsWith("optifine")) {
         return new ResourceLocation(resourceLocation.getResourceDomain(), "models/" + resourceLocation.getResourcePath() + ".json");
      }

      if (!resourcePath.endsWith(".json")) {
         resourceLocation2 = new ResourceLocation(resourceLocation.getResourceDomain(), resourcePath + ".json");
      }

      return resourceLocation2;
   }

   public void loadItemModels() {
      this.registerVariantNames();

      for (Item item : Item.itemRegistry) {
         for (String str : this.getVariantNames(item)) {
            ResourceLocation itemLocation = this.getItemLocation(str);
            this.itemLocations.put(str, itemLocation);
            if (this.models.get(itemLocation) == null) {
               try {
                  ModelBlock loadModel = this.loadModel(itemLocation);
                  this.models.put(itemLocation, loadModel);
               } catch (Exception e) {
                  LOGGER.warn("Unable to load item model: '" + itemLocation + "' for item: '" + Item.itemRegistry.getNameForObject(item) + "'", e);
               }
            }
         }
      }
   }

   public void loadItemModel(String str, ResourceLocation resourceLocation, ResourceLocation resourceLocation2) {
      this.itemLocations.put(str, resourceLocation);
      if (this.models.get(resourceLocation) == null) {
         try {
            ModelBlock loadModel = this.loadModel(resourceLocation);
            this.models.put(resourceLocation, loadModel);
         } catch (Exception e) {
            LOGGER.warn("Unable to load item model: '{}' for item: '{}'", new Object[]{resourceLocation, resourceLocation2});
            LOGGER.warn(e.getClass().getName() + ": " + e.getMessage());
         }
      }
   }

   public void registerVariantNames() {
      this.variantNames.clear();
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stone),
            Lists.newArrayList(new String[]{"stone", "granite", "granite_smooth", "diorite", "diorite_smooth", "andesite", "andesite_smooth"})
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.dirt), Lists.newArrayList(new String[]{"dirt", "coarse_dirt", "podzol"}));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.planks),
            Lists.newArrayList(new String[]{"oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks"})
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.sapling),
            Lists.newArrayList(new String[]{"oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling", "acacia_sapling", "dark_oak_sapling"})
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.sand), Lists.newArrayList(new String[]{"sand", "red_sand"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.log), Lists.newArrayList(new String[]{"oak_log", "spruce_log", "birch_log", "jungle_log"}));
      this.variantNames
         .put(Item.getItemFromBlock(Blocks.leaves), Lists.newArrayList(new String[]{"oak_leaves", "spruce_leaves", "birch_leaves", "jungle_leaves"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.sponge), Lists.newArrayList(new String[]{"sponge", "sponge_wet"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.sandstone), Lists.newArrayList(new String[]{"sandstone", "chiseled_sandstone", "smooth_sandstone"}));
      this.variantNames
         .put(Item.getItemFromBlock(Blocks.red_sandstone), Lists.newArrayList(new String[]{"red_sandstone", "chiseled_red_sandstone", "smooth_red_sandstone"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.tallgrass), Lists.newArrayList(new String[]{"dead_bush", "tall_grass", "fern"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.deadbush), Lists.newArrayList(new String[]{"dead_bush"}));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.wool),
            Lists.newArrayList(
               new String[]{
                  "black_wool",
                  "red_wool",
                  "green_wool",
                  "brown_wool",
                  "blue_wool",
                  "purple_wool",
                  "cyan_wool",
                  "silver_wool",
                  "gray_wool",
                  "pink_wool",
                  "lime_wool",
                  "yellow_wool",
                  "light_blue_wool",
                  "magenta_wool",
                  "orange_wool",
                  "white_wool"
               }
            )
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.yellow_flower), Lists.newArrayList(new String[]{"dandelion"}));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.red_flower),
            Lists.newArrayList(
               new String[]{"poppy", "blue_orchid", "allium", "houstonia", "red_tulip", "orange_tulip", "white_tulip", "pink_tulip", "oxeye_daisy"}
            )
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stone_slab),
            Lists.newArrayList(
               new String[]{"stone_slab", "sandstone_slab", "cobblestone_slab", "brick_slab", "stone_brick_slab", "nether_brick_slab", "quartz_slab"}
            )
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.stone_slab2), Lists.newArrayList(new String[]{"red_sandstone_slab"}));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stained_glass),
            Lists.newArrayList(
               new String[]{
                  "black_stained_glass",
                  "red_stained_glass",
                  "green_stained_glass",
                  "brown_stained_glass",
                  "blue_stained_glass",
                  "purple_stained_glass",
                  "cyan_stained_glass",
                  "silver_stained_glass",
                  "gray_stained_glass",
                  "pink_stained_glass",
                  "lime_stained_glass",
                  "yellow_stained_glass",
                  "light_blue_stained_glass",
                  "magenta_stained_glass",
                  "orange_stained_glass",
                  "white_stained_glass"
               }
            )
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.monster_egg),
            Lists.newArrayList(
               new String[]{
                  "stone_monster_egg",
                  "cobblestone_monster_egg",
                  "stone_brick_monster_egg",
                  "mossy_brick_monster_egg",
                  "cracked_brick_monster_egg",
                  "chiseled_brick_monster_egg"
               }
            )
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stonebrick),
            Lists.newArrayList(new String[]{"stonebrick", "mossy_stonebrick", "cracked_stonebrick", "chiseled_stonebrick"})
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.wooden_slab),
            Lists.newArrayList(new String[]{"oak_slab", "spruce_slab", "birch_slab", "jungle_slab", "acacia_slab", "dark_oak_slab"})
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.cobblestone_wall), Lists.newArrayList(new String[]{"cobblestone_wall", "mossy_cobblestone_wall"}));
      this.variantNames
         .put(Item.getItemFromBlock(Blocks.anvil), Lists.newArrayList(new String[]{"anvil_intact", "anvil_slightly_damaged", "anvil_very_damaged"}));
      this.variantNames
         .put(Item.getItemFromBlock(Blocks.quartz_block), Lists.newArrayList(new String[]{"quartz_block", "chiseled_quartz_block", "quartz_column"}));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stained_hardened_clay),
            Lists.newArrayList(
               new String[]{
                  "black_stained_hardened_clay",
                  "red_stained_hardened_clay",
                  "green_stained_hardened_clay",
                  "brown_stained_hardened_clay",
                  "blue_stained_hardened_clay",
                  "purple_stained_hardened_clay",
                  "cyan_stained_hardened_clay",
                  "silver_stained_hardened_clay",
                  "gray_stained_hardened_clay",
                  "pink_stained_hardened_clay",
                  "lime_stained_hardened_clay",
                  "yellow_stained_hardened_clay",
                  "light_blue_stained_hardened_clay",
                  "magenta_stained_hardened_clay",
                  "orange_stained_hardened_clay",
                  "white_stained_hardened_clay"
               }
            )
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.stained_glass_pane),
            Lists.newArrayList(
               new String[]{
                  "black_stained_glass_pane",
                  "red_stained_glass_pane",
                  "green_stained_glass_pane",
                  "brown_stained_glass_pane",
                  "blue_stained_glass_pane",
                  "purple_stained_glass_pane",
                  "cyan_stained_glass_pane",
                  "silver_stained_glass_pane",
                  "gray_stained_glass_pane",
                  "pink_stained_glass_pane",
                  "lime_stained_glass_pane",
                  "yellow_stained_glass_pane",
                  "light_blue_stained_glass_pane",
                  "magenta_stained_glass_pane",
                  "orange_stained_glass_pane",
                  "white_stained_glass_pane"
               }
            )
         );
      this.variantNames.put(Item.getItemFromBlock(Blocks.leaves2), Lists.newArrayList(new String[]{"acacia_leaves", "dark_oak_leaves"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.log2), Lists.newArrayList(new String[]{"acacia_log", "dark_oak_log"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.prismarine), Lists.newArrayList(new String[]{"prismarine", "prismarine_bricks", "dark_prismarine"}));
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.carpet),
            Lists.newArrayList(
               new String[]{
                  "black_carpet",
                  "red_carpet",
                  "green_carpet",
                  "brown_carpet",
                  "blue_carpet",
                  "purple_carpet",
                  "cyan_carpet",
                  "silver_carpet",
                  "gray_carpet",
                  "pink_carpet",
                  "lime_carpet",
                  "yellow_carpet",
                  "light_blue_carpet",
                  "magenta_carpet",
                  "orange_carpet",
                  "white_carpet"
               }
            )
         );
      this.variantNames
         .put(
            Item.getItemFromBlock(Blocks.double_plant),
            Lists.newArrayList(new String[]{"sunflower", "syringa", "double_grass", "double_fern", "double_rose", "paeonia"})
         );
      this.variantNames.put(Items.bow, Lists.newArrayList(new String[]{"bow", "bow_pulling_0", "bow_pulling_1", "bow_pulling_2"}));
      this.variantNames.put(Items.coal, Lists.newArrayList(new String[]{"coal", "charcoal"}));
      this.variantNames.put(Items.fishing_rod, Lists.newArrayList(new String[]{"fishing_rod", "fishing_rod_cast"}));
      this.variantNames.put(Items.fish, Lists.newArrayList(new String[]{"cod", "salmon", "clownfish", "pufferfish"}));
      this.variantNames.put(Items.cooked_fish, Lists.newArrayList(new String[]{"cooked_cod", "cooked_salmon"}));
      this.variantNames
         .put(
            Items.dye,
            Lists.newArrayList(
               new String[]{
                  "dye_black",
                  "dye_red",
                  "dye_green",
                  "dye_brown",
                  "dye_blue",
                  "dye_purple",
                  "dye_cyan",
                  "dye_silver",
                  "dye_gray",
                  "dye_pink",
                  "dye_lime",
                  "dye_yellow",
                  "dye_light_blue",
                  "dye_magenta",
                  "dye_orange",
                  "dye_white"
               }
            )
         );
      this.variantNames.put(Items.potionitem, Lists.newArrayList(new String[]{"bottle_drinkable", "bottle_splash"}));
      this.variantNames.put(Items.skull, Lists.newArrayList(new String[]{"skull_skeleton", "skull_wither", "skull_zombie", "skull_char", "skull_creeper"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.oak_fence_gate), Lists.newArrayList(new String[]{"oak_fence_gate"}));
      this.variantNames.put(Item.getItemFromBlock(Blocks.oak_fence), Lists.newArrayList(new String[]{"oak_fence"}));
      this.variantNames.put(Items.oak_door, Lists.newArrayList(new String[]{"oak_door"}));

      for (Entry entry : customVariantNames.entrySet()) {
         this.variantNames.put((Item)((RegistryDelegate)entry.getKey()).get(), Lists.newArrayList(((Set)entry.getValue()).iterator()));
      }

      CustomItems.update();
      CustomItems.loadModels(this);
   }

   public List<String> getVariantNames(Item item) {
      List list = this.variantNames.get(item);
      if (list == null) {
         list = Collections.singletonList(Item.itemRegistry.getNameForObject(item).toString());
      }

      return list;
   }

   public ResourceLocation getItemLocation(String str) {
      ResourceLocation resourceLocation = new ResourceLocation(str);
      if (Reflector.ForgeHooksClient.exists()) {
         resourceLocation = new ResourceLocation(str.replaceAll("#.*", ""));
      }

      return new ResourceLocation(resourceLocation.getResourceDomain(), "item/" + resourceLocation.getResourcePath());
   }

   public void bakeBlockModels() {
      for (ModelResourceLocation modelResourceLocation : this.variants.keySet()) {
         WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
         int n = 0;

         for (Variant variant : this.variants.get(modelResourceLocation).getVariants()) {
            ModelBlock modelBlock = this.models.get(variant.getModelLocation());
            if (modelBlock != null && modelBlock.isResolved()) {
               n++;
               builder.add(this.bakeModel(modelBlock, variant.getRotation(), variant.isUvLocked()), variant.getWeight());
            } else {
               LOGGER.warn("Missing model for: " + modelResourceLocation);
            }
         }

         if (n == 0) {
            LOGGER.warn("No weighted models for: " + modelResourceLocation);
         } else if (n == 1) {
            this.bakedRegistry.putObject(modelResourceLocation, builder.first());
         } else {
            this.bakedRegistry.putObject(modelResourceLocation, builder.build());
         }
      }

      for (Entry entry : this.itemLocations.entrySet()) {
         ResourceLocation value = (ResourceLocation)entry.getValue();
         ModelResourceLocation modelResourceLocation = new ModelResourceLocation((String)entry.getKey(), "inventory");
         if (Reflector.ModelLoader_getInventoryVariant.exists()) {
            modelResourceLocation = (ModelResourceLocation)Reflector.call(Reflector.ModelLoader_getInventoryVariant, entry.getKey());
         }

         ModelBlock modelBlock = this.models.get(value);
         if (modelBlock == null || !modelBlock.isResolved()) {
            LOGGER.warn("Missing model for: " + value);
         } else if (this.isCustomRenderer(modelBlock)) {
            this.bakedRegistry.putObject(modelResourceLocation, new BuiltInModel(modelBlock.getAllTransforms()));
         } else {
            this.bakedRegistry.putObject(modelResourceLocation, this.bakeModel(modelBlock, ModelRotation.X0_Y0, false));
         }
      }
   }

   public Set<ResourceLocation> getVariantsTextureLocations() {
      HashSet newHashSet = Sets.newHashSet();
      ArrayList newArrayList = Lists.newArrayList(this.variants.keySet());
      Collections.sort(newArrayList, new Comparator<ModelResourceLocation>() {
         public int compare(ModelResourceLocation modelResourceLocation, ModelResourceLocation modelResourceLocation2) {
            return modelResourceLocation.toString().compareTo(modelResourceLocation2.toString());
         }
      });

      for (ModelResourceLocation modelResourceLocation : (Iterable<ModelResourceLocation>)newArrayList) {
         Variants variants = this.variants.get(modelResourceLocation);

         for (Variant variant : variants.getVariants()) {
            ModelBlock modelBlock = this.models.get(variant.getModelLocation());
            if (modelBlock == null) {
               LOGGER.warn("Missing model for: " + modelResourceLocation);
            } else {
               newHashSet.addAll(this.getTextureLocations(modelBlock));
            }
         }
      }

      newHashSet.addAll(LOCATIONS_BUILTIN_TEXTURES);
      return newHashSet;
   }

   public IBakedModel bakeModel(ModelBlock modelBlockIn, ModelRotation modelRotationIn, boolean uvLocked) {
      return this.bakeModel(modelBlockIn, (ITransformation)modelRotationIn, uvLocked);
   }

   public IBakedModel bakeModel(ModelBlock modelBlock, ITransformation transformation, boolean flag) {
      TextureAtlasSprite textureAtlasSprite = this.sprites.get(new ResourceLocation(modelBlock.resolveTextureName("particle")));
      SimpleBakedModel.Builder setTexture = new SimpleBakedModel.Builder(modelBlock).setTexture(textureAtlasSprite);

      for (BlockPart blockPart : modelBlock.getElements()) {
         for (EnumFacing enumFacing : blockPart.mapFaces.keySet()) {
            BlockPartFace blockPartFace = blockPart.mapFaces.get(enumFacing);
            TextureAtlasSprite textureAtlasSprite2 = this.sprites.get(new ResourceLocation(modelBlock.resolveTextureName(blockPartFace.texture)));
            boolean flag2 = true;
            if (Reflector.ForgeHooksClient.exists()) {
               flag2 = ForgeStub.alwaysFalse(transformation.createMatrix4f());
            }

            if (blockPartFace.cullFace != null && flag2) {
               setTexture.addFaceQuad(transformation.rotate(blockPartFace.cullFace), this.makeBakedQuad(blockPart, blockPartFace, textureAtlasSprite2, enumFacing, transformation, flag));
            } else {
               setTexture.addGeneralQuad(this.makeBakedQuad(blockPart, blockPartFace, textureAtlasSprite2, enumFacing, transformation, flag));
            }
         }
      }

      return setTexture.makeBakedModel();
   }

   public BakedQuad makeBakedQuad(BlockPart blockPart, BlockPartFace blockPartFace, TextureAtlasSprite textureAtlasSprite, EnumFacing enumFacing, ModelRotation modelRotation, boolean flag) {
      return Reflector.ForgeHooksClient.exists()
         ? this.makeBakedQuad(blockPart, blockPartFace, textureAtlasSprite, enumFacing, modelRotation, flag)
         : this.faceBakery.makeBakedQuad(blockPart.positionFrom, blockPart.positionTo, blockPartFace, textureAtlasSprite, enumFacing, modelRotation, blockPart.partRotation, flag, blockPart.shade);
   }

   public BakedQuad makeBakedQuad(BlockPart blockPart, BlockPartFace blockPartFace, TextureAtlasSprite textureAtlasSprite, EnumFacing enumFacing, ITransformation transformation, boolean flag) {
      return this.faceBakery.makeBakedQuad(blockPart.positionFrom, blockPart.positionTo, blockPartFace, textureAtlasSprite, enumFacing, transformation, blockPart.partRotation, flag, blockPart.shade);
   }

   public void loadModelsCheck() {
      this.loadModels();

      for (ModelBlock modelBlock : this.models.values()) {
         modelBlock.getParentFromMap(this.models);
      }

      ModelBlock.checkModelHierarchy(this.models);
   }

   public void loadModels() {
      ArrayDeque newArrayDeque = Queues.newArrayDeque();
      HashSet newHashSet = Sets.newHashSet();

      for (ResourceLocation resourceLocation : this.models.keySet()) {
         newHashSet.add(resourceLocation);
         ResourceLocation parentLocation = this.models.get(resourceLocation).getParentLocation();
         if (parentLocation != null) {
            newArrayDeque.add(parentLocation);
         }
      }

      while (!newArrayDeque.isEmpty()) {
         ResourceLocation pop = (ResourceLocation)newArrayDeque.pop();

         try {
            if (this.models.get(pop) != null) {
               continue;
            }

            ModelBlock loadModel = this.loadModel(pop);
            this.models.put(pop, loadModel);
            ResourceLocation parentLocation = loadModel.getParentLocation();
            if (parentLocation != null && !newHashSet.contains(parentLocation)) {
               newArrayDeque.add(parentLocation);
            }
         } catch (Exception e) {
            LOGGER.warn("In parent chain: " + JOINER.join(this.getParentPath(pop)) + "; unable to load model: '" + pop + "'");
         }

         newHashSet.add(pop);
      }
   }

   public List<ResourceLocation> getParentPath(ResourceLocation resourceLocation) {
      ArrayList newArrayList = Lists.newArrayList(new ResourceLocation[]{resourceLocation});
      ResourceLocation resourceLocation2 = resourceLocation;

      while ((resourceLocation2 = this.getParentLocation(resourceLocation2)) != null) {
         newArrayList.add(0, resourceLocation2);
      }

      return newArrayList;
   }

   public ResourceLocation getParentLocation(ResourceLocation resourceLocation) {
      for (Entry entry : this.models.entrySet()) {
         ModelBlock value = (ModelBlock)entry.getValue();
         if (value != null && resourceLocation.equals(value.getParentLocation())) {
            return (ResourceLocation)entry.getKey();
         }
      }

      return null;
   }

   public Set<ResourceLocation> getTextureLocations(ModelBlock modelBlock) {
      HashSet newHashSet = Sets.newHashSet();

      for (BlockPart blockPart : modelBlock.getElements()) {
         for (BlockPartFace blockPartFace : blockPart.mapFaces.values()) {
            ResourceLocation resourceLocation = new ResourceLocation(modelBlock.resolveTextureName(blockPartFace.texture));
            newHashSet.add(resourceLocation);
         }
      }

      newHashSet.add(new ResourceLocation(modelBlock.resolveTextureName("particle")));
      return newHashSet;
   }

   public void loadSprites() {
      final Set variantsTextureLocations = this.getVariantsTextureLocations();
      variantsTextureLocations.addAll(this.getItemsTextureLocations());
      variantsTextureLocations.remove(TextureMap.LOCATION_MISSING_TEXTURE);
      IIconCreator iconCreator = new IIconCreator() {
         @Override
         public void registerSprites(TextureMap iconRegistry) {
            for (ResourceLocation resourceLocation : (Iterable<ResourceLocation>)variantsTextureLocations) {
               TextureAtlasSprite registerSprite = iconRegistry.registerSprite(resourceLocation);
               ModelBakery.this.sprites.put(resourceLocation, registerSprite);
            }
         }
      };
      this.textureMap.loadSprites(this.resourceManager, iconCreator);
      this.sprites.put(new ResourceLocation("missingno"), this.textureMap.getMissingSprite());
   }

   public Set<ResourceLocation> getItemsTextureLocations() {
      HashSet newHashSet = Sets.newHashSet();

      for (ResourceLocation resourceLocation : this.itemLocations.values()) {
         ModelBlock modelBlock = this.models.get(resourceLocation);
         if (modelBlock != null) {
            newHashSet.add(new ResourceLocation(modelBlock.resolveTextureName("particle")));
            if (this.hasItemModel(modelBlock)) {
               for (String str : ItemModelGenerator.LAYERS) {
                  ResourceLocation resourceLocation2 = new ResourceLocation(modelBlock.resolveTextureName(str));
                  if (modelBlock.getRootModel() == MODEL_COMPASS && !TextureMap.LOCATION_MISSING_TEXTURE.equals(resourceLocation2)) {
                     TextureAtlasSprite.setLocationNameCompass(resourceLocation2.toString());
                  } else if (modelBlock.getRootModel() == MODEL_CLOCK && !TextureMap.LOCATION_MISSING_TEXTURE.equals(resourceLocation2)) {
                     TextureAtlasSprite.setLocationNameClock(resourceLocation2.toString());
                  }

                  newHashSet.add(resourceLocation2);
               }
            } else if (!this.isCustomRenderer(modelBlock)) {
               for (BlockPart blockPart : modelBlock.getElements()) {
                  for (BlockPartFace blockPartFace : blockPart.mapFaces.values()) {
                     ResourceLocation resourceLocation2 = new ResourceLocation(modelBlock.resolveTextureName(blockPartFace.texture));
                     newHashSet.add(resourceLocation2);
                  }
               }
            }
         }
      }

      return newHashSet;
   }

   public boolean hasItemModel(ModelBlock modelBlock) {
      if (modelBlock == null) {
         return false;
      }

      ModelBlock rootModel = modelBlock.getRootModel();
      return rootModel == MODEL_GENERATED || rootModel == MODEL_COMPASS || rootModel == MODEL_CLOCK;
   }

   public boolean isCustomRenderer(ModelBlock modelBlock) {
      if (modelBlock == null) {
         return false;
      }

      ModelBlock rootModel = modelBlock.getRootModel();
      return rootModel == MODEL_ENTITY;
   }

   public void bakeItemModels() {
      for (ResourceLocation resourceLocation : this.itemLocations.values()) {
         ModelBlock modelBlock = this.models.get(resourceLocation);
         if (this.hasItemModel(modelBlock)) {
            ModelBlock makeItemModel = this.makeItemModel(modelBlock);
            if (makeItemModel != null) {
               makeItemModel.name = resourceLocation.toString();
            }

            this.models.put(resourceLocation, makeItemModel);
         } else if (this.isCustomRenderer(modelBlock)) {
            this.models.put(resourceLocation, modelBlock);
         }
      }

      for (TextureAtlasSprite textureAtlasSprite : this.sprites.values()) {
         if (!textureAtlasSprite.hasAnimationMetadata()) {
            textureAtlasSprite.clearFramesTextureData();
         }
      }
   }

   public ModelBlock makeItemModel(ModelBlock modelBlock) {
      return this.itemModelGenerator.makeItemModel(this.textureMap, modelBlock);
   }

   public ModelBlock getModelBlock(ResourceLocation resourceLocation) {
      return this.models.get(resourceLocation);
   }

   public static void fixModelLocations(ModelBlock modelBlock, String str) {
      ResourceLocation fixModelLocation2 = fixModelLocation(modelBlock.getParentLocation(), str);
      if (fixModelLocation2 != modelBlock.getParentLocation()) {
         Reflector.setFieldValue(modelBlock, Reflector.ModelBlock_parentLocation, fixModelLocation2);
      }

      Map fieldValue = (Map)Reflector.getFieldValue(modelBlock, Reflector.ModelBlock_textures);
      if (fieldValue != null) {
         for (Entry entry : (Iterable<Entry>)fieldValue.entrySet()) {
            String value = (String)entry.getValue();
            String fixResourcePath2 = fixResourcePath(value, str);
            if (fixResourcePath2 != value) {
               entry.setValue(fixResourcePath2);
            }
         }
      }
   }

   public static ResourceLocation fixModelLocation(ResourceLocation resourceLocation, String str) {
      if (resourceLocation != null && str != null) {
         if (!resourceLocation.getResourceDomain().equals("minecraft")) {
            return resourceLocation;
         }

         String resourcePath = resourceLocation.getResourcePath();
         String fixResourcePath2 = fixResourcePath(resourcePath, str);
         if (fixResourcePath2 != resourcePath) {
            resourceLocation = new ResourceLocation(resourceLocation.getResourceDomain(), fixResourcePath2);
         }

         return resourceLocation;
      } else {
         return resourceLocation;
      }
   }

   public static String fixResourcePath(String str, String str2) {
      str = TextureUtils.fixResourcePath(str, str2);
      str = StrUtils.removeSuffix(str, ".json");
      return StrUtils.removeSuffix(str, ".png");
   }

   public static void addVariantName(Item item, String... strs) {
      RegistryDelegate fieldValue = (RegistryDelegate)Reflector.getFieldValue(item, Reflector.ForgeItem_delegate);
      if (customVariantNames.containsKey(fieldValue)) {
         customVariantNames.get(fieldValue).addAll(Lists.newArrayList(strs));
      } else {
         customVariantNames.put(fieldValue, Sets.newHashSet(strs));
      }
   }

   public static <T extends ResourceLocation> void registerItemVariants(Item item, T... values) {
      RegistryDelegate fieldValue = (RegistryDelegate)Reflector.getFieldValue(item, Reflector.ForgeItem_delegate);
      if (!customVariantNames.containsKey(fieldValue)) {
         customVariantNames.put(fieldValue, Sets.newHashSet());
      }

      for (ResourceLocation resourceLocation : values) {
         customVariantNames.get(fieldValue).add(resourceLocation.toString());
      }
   }

   static {
      BUILT_IN_MODELS.put(
         "missing",
         "{ \"textures\": {   \"particle\": \"missingno\",   \"missingno\": \"missingno\"}, \"elements\": [ {     \"from\": [ 0, 0, 0 ],     \"to\": [ 16, 16, 16 ],     \"faces\": {         \"down\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"down\", \"texture\": \"#missingno\" },         \"up\":    { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"up\", \"texture\": \"#missingno\" },         \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"north\", \"texture\": \"#missingno\" },         \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"south\", \"texture\": \"#missingno\" },         \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"west\", \"texture\": \"#missingno\" },         \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"east\", \"texture\": \"#missingno\" }    }}]}"
      );
      MODEL_GENERATED.name = "generation marker";
      MODEL_COMPASS.name = "compass generation marker";
      MODEL_CLOCK.name = "class generation marker";
      MODEL_ENTITY.name = "block entity marker";
   }
}
