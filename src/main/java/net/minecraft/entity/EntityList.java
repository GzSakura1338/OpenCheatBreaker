package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart.EnumMinecartType;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityList {
   public static Logger logger = LogManager.getLogger();
   public static Map<String, Class<? extends Entity>> stringToClassMapping = Maps.newHashMap();
   public static Map<Class<? extends Entity>, String> classToStringMapping = Maps.newHashMap();
   public static Map<Integer, Class<? extends Entity>> idToClassMapping = Maps.newHashMap();
   public static Map<Class<? extends Entity>, Integer> classToIDMapping = Maps.newHashMap();
   public static Map<String, Integer> stringToIDMapping = Maps.newHashMap();
   public static Map<Integer, EntityList.EntityEggInfo> entityEggs = Maps.newLinkedHashMap();

   public static void addMapping(Class<? extends Entity> entityClass, String entityName, int id) {
      if (stringToClassMapping.containsKey(entityName)) {
         throw new IllegalArgumentException("ID is already registered: " + entityName);
      }

      if (idToClassMapping.containsKey(id)) {
         throw new IllegalArgumentException("ID is already registered: " + id);
      }

      if (id == 0) {
         throw new IllegalArgumentException("Cannot register to reserved id: " + id);
      }

      if (entityClass == null) {
         throw new IllegalArgumentException("Cannot register null clazz for id: " + id);
      }

      stringToClassMapping.put(entityName, entityClass);
      classToStringMapping.put(entityClass, entityName);
      idToClassMapping.put(id, entityClass);
      classToIDMapping.put(entityClass, id);
      stringToIDMapping.put(entityName, id);
   }

   public static void addMapping(Class<? extends Entity> entityClass, String entityName, int entityID, int baseColor, int spotColor) {
      addMapping(entityClass, entityName, entityID);
      entityEggs.put(entityID, new EntityList.EntityEggInfo(entityID, baseColor, spotColor));
   }

   public static Entity createEntityByName(String entityName, World worldIn) {
      Entity entity = null;

      try {
         Class clazz = stringToClassMapping.get(entityName);
         if (clazz != null) {
            entity = (Entity)clazz.getConstructor(World.class).newInstance(worldIn);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return entity;
   }

   public static Entity createEntityFromNBT(NBTTagCompound nbt, World worldIn) {
      Entity entity = null;
      if ("Minecart".equals(nbt.getString("id"))) {
         nbt.setString("id", EnumMinecartType.byNetworkID(nbt.getInteger("Type")).getName());
         nbt.removeTag("Type");
      }

      try {
         Class clazz = stringToClassMapping.get(nbt.getString("id"));
         if (clazz != null) {
            entity = (Entity)clazz.getConstructor(World.class).newInstance(worldIn);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      if (entity != null) {
         entity.readFromNBT(nbt);
      } else {
         logger.warn("Skipping Entity with id " + nbt.getString("id"));
      }

      return entity;
   }

   public static Entity createEntityByID(int entityID, World worldIn) {
      Entity entity = null;

      try {
         Class classFromID = getClassFromID(entityID);
         if (classFromID != null) {
            entity = (Entity)classFromID.getConstructor(World.class).newInstance(worldIn);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      if (entity == null) {
         logger.warn("Skipping Entity with id " + entityID);
      }

      return entity;
   }

   public static int getEntityID(Entity entityIn) {
      Integer integer = classToIDMapping.get(entityIn.getClass());
      return integer == null ? 0 : integer;
   }

   public static Class<? extends Entity> getClassFromID(int entityID) {
      return idToClassMapping.get(entityID);
   }

   public static String getEntityString(Entity entityIn) {
      return classToStringMapping.get(entityIn.getClass());
   }

   public static int getIDFromString(String entityName) {
      Integer integer = stringToIDMapping.get(entityName);
      return integer == null ? 90 : integer;
   }

   public static String getStringFromID(int entityID) {
      return classToStringMapping.get(getClassFromID(entityID));
   }

   public static void load() {
   }

   public static List<String> getEntityNameList() {
      Set keySet = stringToClassMapping.keySet();
      ArrayList newArrayList = Lists.newArrayList();

      for (String str : (Iterable<String>)keySet) {
         Class clazz = stringToClassMapping.get(str);
         if ((clazz.getModifiers() & 1024) != 1024) {
            newArrayList.add(str);
         }
      }

      newArrayList.add("LightningBolt");
      return newArrayList;
   }

   public static boolean isStringEntityName(Entity entityIn, String entityName) {
      String entityString = getEntityString(entityIn);
      if (entityString == null && entityIn instanceof EntityPlayer) {
         entityString = "Player";
      } else if (entityString == null && entityIn instanceof EntityLightningBolt) {
         entityString = "LightningBolt";
      }

      return entityName.equals(entityString);
   }

   public static boolean isStringValidEntityName(String entityName) {
      return "Player".equals(entityName) || getEntityNameList().contains(entityName);
   }

   static {
      addMapping(EntityItem.class, "Item", 1);
      addMapping(EntityXPOrb.class, "XPOrb", 2);
      addMapping(EntityEgg.class, "ThrownEgg", 7);
      addMapping(EntityLeashKnot.class, "LeashKnot", 8);
      addMapping(EntityPainting.class, "Painting", 9);
      addMapping(EntityArrow.class, "Arrow", 10);
      addMapping(EntitySnowball.class, "Snowball", 11);
      addMapping(EntityLargeFireball.class, "Fireball", 12);
      addMapping(EntitySmallFireball.class, "SmallFireball", 13);
      addMapping(EntityEnderPearl.class, "ThrownEnderpearl", 14);
      addMapping(EntityEnderEye.class, "EyeOfEnderSignal", 15);
      addMapping(EntityPotion.class, "ThrownPotion", 16);
      addMapping(EntityExpBottle.class, "ThrownExpBottle", 17);
      addMapping(EntityItemFrame.class, "ItemFrame", 18);
      addMapping(EntityWitherSkull.class, "WitherSkull", 19);
      addMapping(EntityTNTPrimed.class, "PrimedTnt", 20);
      addMapping(EntityFallingBlock.class, "FallingSand", 21);
      addMapping(EntityFireworkRocket.class, "FireworksRocketEntity", 22);
      addMapping(EntityArmorStand.class, "ArmorStand", 30);
      addMapping(EntityBoat.class, "Boat", 41);
      addMapping(EntityMinecartEmpty.class, EnumMinecartType.RIDEABLE.getName(), 42);
      addMapping(EntityMinecartChest.class, EnumMinecartType.CHEST.getName(), 43);
      addMapping(EntityMinecartFurnace.class, EnumMinecartType.FURNACE.getName(), 44);
      addMapping(EntityMinecartTNT.class, EnumMinecartType.TNT.getName(), 45);
      addMapping(EntityMinecartHopper.class, EnumMinecartType.HOPPER.getName(), 46);
      addMapping(EntityMinecartMobSpawner.class, EnumMinecartType.SPAWNER.getName(), 47);
      addMapping(EntityMinecartCommandBlock.class, EnumMinecartType.COMMAND_BLOCK.getName(), 40);
      addMapping(EntityLiving.class, "Mob", 48);
      addMapping(EntityMob.class, "Monster", 49);
      addMapping(EntityCreeper.class, "Creeper", 50, 894731, 0);
      addMapping(EntitySkeleton.class, "Skeleton", 51, 12698049, 4802889);
      addMapping(EntitySpider.class, "Spider", 52, 3419431, 11013646);
      addMapping(EntityGiantZombie.class, "Giant", 53);
      addMapping(EntityZombie.class, "Zombie", 54, 44975, 7969893);
      addMapping(EntitySlime.class, "Slime", 55, 5349438, 8306542);
      addMapping(EntityGhast.class, "Ghast", 56, 16382457, 12369084);
      addMapping(EntityPigZombie.class, "PigZombie", 57, 15373203, 5009705);
      addMapping(EntityEnderman.class, "Enderman", 58, 1447446, 0);
      addMapping(EntityCaveSpider.class, "CaveSpider", 59, 803406, 11013646);
      addMapping(EntitySilverfish.class, "Silverfish", 60, 7237230, 3158064);
      addMapping(EntityBlaze.class, "Blaze", 61, 16167425, 16775294);
      addMapping(EntityMagmaCube.class, "LavaSlime", 62, 3407872, 16579584);
      addMapping(EntityDragon.class, "EnderDragon", 63);
      addMapping(EntityWither.class, "WitherBoss", 64);
      addMapping(EntityBat.class, "Bat", 65, 4996656, 986895);
      addMapping(EntityWitch.class, "Witch", 66, 3407872, 5349438);
      addMapping(EntityEndermite.class, "Endermite", 67, 1447446, 7237230);
      addMapping(EntityGuardian.class, "Guardian", 68, 5931634, 15826224);
      addMapping(EntityPig.class, "Pig", 90, 15771042, 14377823);
      addMapping(EntitySheep.class, "Sheep", 91, 15198183, 16758197);
      addMapping(EntityCow.class, "Cow", 92, 4470310, 10592673);
      addMapping(EntityChicken.class, "Chicken", 93, 10592673, 16711680);
      addMapping(EntitySquid.class, "Squid", 94, 2243405, 7375001);
      addMapping(EntityWolf.class, "Wolf", 95, 14144467, 13545366);
      addMapping(EntityMooshroom.class, "MushroomCow", 96, 10489616, 12040119);
      addMapping(EntitySnowman.class, "SnowMan", 97);
      addMapping(EntityOcelot.class, "Ozelot", 98, 15720061, 5653556);
      addMapping(EntityIronGolem.class, "VillagerGolem", 99);
      addMapping(EntityHorse.class, "EntityHorse", 100, 12623485, 15656192);
      addMapping(EntityRabbit.class, "Rabbit", 101, 10051392, 7555121);
      addMapping(EntityVillager.class, "Villager", 120, 5651507, 12422002);
      addMapping(EntityEnderCrystal.class, "EnderCrystal", 200);
   }

   public static class EntityEggInfo {
      public int spawnedID;
      public int primaryColor;
      public int secondaryColor;
      public StatBase killEntityStat;
      public StatBase killedByEntityStat;

      public EntityEggInfo(int id, int baseColor, int spotColor) {
         this.spawnedID = id;
         this.primaryColor = baseColor;
         this.secondaryColor = spotColor;
         this.killEntityStat = StatList.getStatKillEntity(this);
         this.killedByEntityStat = StatList.getStatEntityKilledBy(this);
      }
   }
}
