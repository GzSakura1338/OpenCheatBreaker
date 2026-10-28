package com.cheatbreaker.client.minimap;

import net.minecraft.util.ResourceLocation;

public enum MobType {
    BLANK("Blank", false, 2, 4, "textures/entity/bat.png", "", false, false),
    BLANKHOSTILE("Monster", false, 8, 16, "textures/entity/zombie/zombie.png", "", true, false),
    BLANKNEUTRAL("Mob", false, 8, 16, "textures/entity/pig/pig.png", "", false, true),
    BLANKTAME("Unknown Tame", false, 8, 16, "textures/entity/wolf/wolf.png", "", false, true),
    BAT("Bat", true, 8, 32, "textures/entity/bat.png", "", false, true),
    BLAZE("Blaze", true, 8, 16, "textures/entity/blaze.png", "", true, false),
    CAT("Cat", true, 5, 16, "textures/entity/cat/siamese.png", "", false, true),
    CAVESPIDER("CaveSpider", true, 8, 16, "textures/entity/spider/cave_spider.png", "", true, false),
    CHICKEN("Chicken", true, 6, 16, "textures/entity/chicken.png", "", false, true),
    COW("Cow", true, 10, 32, "textures/entity/cow/cow.png", "", false, true),
    CREEPER("Creeper", true, 8, 16, "textures/entity/creeper/creeper.png", "", true, false),
    ENDERDRAGON("EnderDragon", true, 16, 32, "textures/entity/enderdragon/dragon.png", "", true, false),
    ENDERMAN("Enderman", true, 8, 16, "textures/entity/enderman/enderman.png", "", true, false),
    GHAST("Ghast", true, 16, 32, "textures/entity/ghast/ghast.png", "", true, false),
    GHASTATTACKING("Ghast", false, 16, 32, "textures/entity/ghast/ghast_shooting.png", "", true, false),
    HORSE("EntityHorse", true, 8, 32, "textures/entity/horse/horse_creamy.png", "", false, true),
    IRONGOLEM("VillagerGolem", true, 8, 32, "textures/entity/iron_golem.png", "", false, true),
    MAGMA("LavaSlime", true, 8, 16, "textures/entity/slime/magmacube.png", "", true, false),
    MOOSHROOM("MushroomCow", true, 40, 128, "textures/entity/cow/mooshroom.png", "", false, true),
    OCELOT("Ozelot", true, 5, 16, "textures/entity/cat/ocelot.png", "", false, true),
    PIG("Pig", true, 8, 16, "textures/entity/pig/pig.png", "", false, true),
    PIGZOMBIE("PigZombie", true, 8, 16, "textures/entity/zombie_pigman.png", "", true, true),
    PLAYER("Player", false, 8, 16, "textures/entity/steve.png", "", false, false),
    SHEEP("Sheep", true, 6, 16, "textures/entity/sheep/sheep.png", "", false, true),
    SILVERFISH("Silverfish", true, 6, 16, "textures/entity/silverfish.png", "", true, false),
    SKELETON("Skeleton", true, 8, 16, "textures/entity/skeleton/skeleton.png", "", true, false),
    SKELETONWITHER("Skeleton", false, 8, 16, "textures/entity/skeleton/wither_skeleton.png", "", true, false),
    SLIME("Slime", true, 8, 16, "textures/entity/slime/slime.png", "", true, false),
    SNOWGOLEM("SnowMan", true, 8, 16, "textures/entity/snowman.png", "", false, true),
    SPIDER("Spider", true, 8, 16, "textures/entity/spider/spider.png", "", true, false),
    SPIDERJOCKEY("Skeleton", false, 8, 32, "textures/entity/skeleton/skeleton.png", "textures/entity/spider/spider.png", true, false),
    SPIDERJOCKEYWITHER("Skeleton", false, 8, 32, "textures/entity/skeleton/wither_skeleton.png", "textures/entity/spider/spider.png", true, false),
    SQUID("Squid", true, 6, 16, "textures/entity/squid.png", "", false, true),
    VILLAGER("Villager", true, 8, 32, "textures/entity/villager/farmer.png", "", false, true),
    WITCH("Witch", true, 10, 32, "textures/entity/witch.png", "", true, false),
    WITHER("WitherBoss", true, 24, 64, "textures/entity/wither/wither.png", "", true, false),
    WITHERINVULNERABLE("WitherBoss", false, 24, 64, "textures/entity/wither/wither_invulnerable.png", "", true, false),
    WOLF("Wolf", true, 6, 16, "textures/entity/wolf/wolf.png", "", true, true),
    WOLFANGRY("Wolf", false, 6, 16, "textures/entity/wolf/wolf_angry.png", "", true, false),
    WOLFTAME("Wolf", false, 6, 16, "textures/entity/wolf/wolf_tame.png", "", false, true),
    ZOMBIE("Zombie", true, 8, 16, "textures/entity/zombie/zombie.png", "", true, false),
    ZOMBIEVILLAGER("Zombie", false, 8, 32, "textures/entity/zombie/zombie_villager.png", "", true, false),
    UNKNOWN("Unknown", false, 8, 16, "/mob/UNKNOWN.png", "", true, true),
    CUSTOM("Custom", false, 8, 16, "/mob/UNKNOWN.png", "", true, true),
    AUTO("Auto", false, 8, 16, "/mob/UNKNOWN.png", "", true, true);

    public String mobName;
    public boolean flag;
    public int intValue;
    public int intValue2;
    public ResourceLocation texture;
    public ResourceLocation overlayTexture;
    public boolean flag2;
    public boolean flag3;
    public boolean selected;

    MobType(String str, boolean flag, int n, int n2, String str2, String str3, boolean flag2, boolean flag3) {
        this.mobName = str;
        this.flag = flag;
        this.intValue = n;
        this.intValue2 = n2;
        this.texture = new ResourceLocation(str2);
        this.overlayTexture = str3.equals("") ? null : new ResourceLocation(str3);
        this.flag2 = flag2;
        this.flag3 = flag3;
        this.selected = true;
    }

    public static MobType findMobType(String str) {
        for (MobType mobType : values()) {
            if (mobType.mobName.equals(str)) {
                return mobType;
            }
        }

        return null;
    }

    public int ordinal2() {
        return this.ordinal();
    }
}
