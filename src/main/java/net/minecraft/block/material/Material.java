package net.minecraft.block.material;

public class Material {
   public static Material air = new MaterialTransparent(MapColor.airColor);
   public static Material grass = new Material(MapColor.grassColor);
   public static Material ground = new Material(MapColor.dirtColor);
   public static Material wood = new Material(MapColor.woodColor).setBurning();
   public static Material rock = new Material(MapColor.stoneColor).setRequiresTool();
   public static Material iron = new Material(MapColor.ironColor).setRequiresTool();
   public static Material anvil = new Material(MapColor.ironColor).setRequiresTool().setImmovableMobility();
   public static Material water = new MaterialLiquid(MapColor.waterColor).setNoPushMobility();
   public static Material lava = new MaterialLiquid(MapColor.tntColor).setNoPushMobility();
   public static Material leaves = new Material(MapColor.foliageColor).setBurning().setTranslucent().setNoPushMobility();
   public static Material plants = new MaterialLogic(MapColor.foliageColor).setNoPushMobility();
   public static Material vine = new MaterialLogic(MapColor.foliageColor).setBurning().setNoPushMobility().setReplaceable();
   public static Material sponge = new Material(MapColor.yellowColor);
   public static Material cloth = new Material(MapColor.clothColor).setBurning();
   public static Material fire = new MaterialTransparent(MapColor.airColor).setNoPushMobility();
   public static Material sand = new Material(MapColor.sandColor);
   public static Material circuits = new MaterialLogic(MapColor.airColor).setNoPushMobility();
   public static Material carpet = new MaterialLogic(MapColor.clothColor).setBurning();
   public static Material glass = new Material(MapColor.airColor).setTranslucent().setAdventureModeExempt();
   public static Material redstoneLight = new Material(MapColor.airColor).setAdventureModeExempt();
   public static Material tnt = new Material(MapColor.tntColor).setBurning().setTranslucent();
   public static Material coral = new Material(MapColor.foliageColor).setNoPushMobility();
   public static Material ice = new Material(MapColor.iceColor).setTranslucent().setAdventureModeExempt();
   public static Material packedIce = new Material(MapColor.iceColor).setAdventureModeExempt();
   public static Material snow = new MaterialLogic(MapColor.snowColor).setReplaceable().setTranslucent().setRequiresTool().setNoPushMobility();
   public static Material craftedSnow = new Material(MapColor.snowColor).setRequiresTool();
   public static Material cactus = new Material(MapColor.foliageColor).setTranslucent().setNoPushMobility();
   public static Material clay = new Material(MapColor.clayColor);
   public static Material gourd = new Material(MapColor.foliageColor).setNoPushMobility();
   public static Material dragonEgg = new Material(MapColor.foliageColor).setNoPushMobility();
   public static Material portal = new MaterialPortal(MapColor.airColor).setImmovableMobility();
   public static Material cake = new Material(MapColor.airColor).setNoPushMobility();
   public static Material web = (new Material(MapColor.clothColor) {
      @Override
      public boolean blocksMovement() {
         return false;
      }
   }).setRequiresTool().setNoPushMobility();
   public static Material piston = new Material(MapColor.stoneColor).setImmovableMobility();
   public static Material barrier = new Material(MapColor.airColor).setRequiresTool().setImmovableMobility();
   public boolean canBurn;
   public boolean replaceable;
   public boolean isTranslucent;
   public boolean requiresNoTool = true;
   public MapColor materialMapColor;
   public int mobilityFlag;
   public boolean isAdventureModeExempt;

   public Material(MapColor color) {
      this.materialMapColor = color;
   }

   public boolean isLiquid() {
      return false;
   }

   public boolean isSolid() {
      return true;
   }

   public boolean blocksLight() {
      return true;
   }

   public boolean blocksMovement() {
      return true;
   }

   public Material setTranslucent() {
      this.isTranslucent = true;
      return this;
   }

   public Material setRequiresTool() {
      this.requiresNoTool = false;
      return this;
   }

   public Material setBurning() {
      this.canBurn = true;
      return this;
   }

   public boolean getCanBurn() {
      return this.canBurn;
   }

   public Material setReplaceable() {
      this.replaceable = true;
      return this;
   }

   public boolean isReplaceable() {
      return this.replaceable;
   }

   public boolean isOpaque() {
      return this.isTranslucent ? false : this.blocksMovement();
   }

   public boolean isToolNotRequired() {
      return this.requiresNoTool;
   }

   public int getMaterialMobility() {
      return this.mobilityFlag;
   }

   public Material setNoPushMobility() {
      this.mobilityFlag = 1;
      return this;
   }

   public Material setImmovableMobility() {
      this.mobilityFlag = 2;
      return this;
   }

   public Material setAdventureModeExempt() {
      this.isAdventureModeExempt = true;
      return this;
   }

   public MapColor getMaterialMapColor() {
      return this.materialMapColor;
   }
}
