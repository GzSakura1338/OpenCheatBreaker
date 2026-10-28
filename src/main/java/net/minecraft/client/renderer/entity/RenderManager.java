package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityDoubleFloatEvent;
import com.cheatbreaker.client.mod.impl.FreeLookHandler;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
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
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderManager {
    public Map entityRenderMap = new HashMap();
    public static RenderManager instance = new RenderManager();
    public FontRenderer textRenderer;
    public static double renderPosX;
    public static double renderPosY;
    public static double renderPosZ;
    public TextureManager renderEngine;
    public ItemRenderer itemRenderer;
    public World worldObj;
    public EntityLivingBase livingPlayer;
    public Entity entity;
    public float playerViewY;
    public float playerViewX;
    public GameSettings options;
    public double viewerPosX;
    public double viewerPosY;
    public double viewerPosZ;
    public static boolean debugBoundingBox;
    public HashMap<String, RenderPlayer> stringToRenderPlayerMap = new HashMap<>();

    public RenderManager() {
        this.entityRenderMap.put(EntityCaveSpider.class, new RenderCaveSpider());
        this.entityRenderMap.put(EntitySpider.class, new RenderSpider());
        this.entityRenderMap.put(EntityPig.class, new RenderPig(new ModelPig(), new ModelPig(0.5F), 0.7F));
        this.entityRenderMap.put(EntitySheep.class, new RenderSheep(new ModelSheep2(), new ModelSheep1(), 0.7F));
        this.entityRenderMap.put(EntityCow.class, new RenderCow(new ModelCow(), 0.7F));
        this.entityRenderMap.put(EntityMooshroom.class, new RenderMooshroom(new ModelCow(), 0.7F));
        this.entityRenderMap.put(EntityWolf.class, new RenderWolf(new ModelWolf(), new ModelWolf(), 0.5F));
        this.entityRenderMap.put(EntityChicken.class, new RenderChicken(new ModelChicken(), 0.3F));
        this.entityRenderMap.put(EntityOcelot.class, new RenderOcelot(new ModelOcelot(), 0.4F));
        this.entityRenderMap.put(EntitySilverfish.class, new RenderSilverfish());
        this.entityRenderMap.put(EntityCreeper.class, new RenderCreeper());
        this.entityRenderMap.put(EntityEnderman.class, new RenderEnderman());
        this.entityRenderMap.put(EntitySnowman.class, new RenderSnowMan());
        this.entityRenderMap.put(EntitySkeleton.class, new RenderSkeleton());
        this.entityRenderMap.put(EntityWitch.class, new RenderWitch());
        this.entityRenderMap.put(EntityBlaze.class, new RenderBlaze());
        this.entityRenderMap.put(EntityZombie.class, new RenderZombie());
        this.entityRenderMap.put(EntitySlime.class, new RenderSlime(new ModelSlime(16), new ModelSlime(0), 0.25F));
        this.entityRenderMap.put(EntityMagmaCube.class, new RenderMagmaCube());
        RenderPlayer renderPlayer = new RenderPlayer();
        this.entityRenderMap.put(EntityPlayer.class, renderPlayer);
        this.stringToRenderPlayerMap.put("default", renderPlayer);
        this.stringToRenderPlayerMap.put("slim", new RenderPlayer(true));
        this.entityRenderMap.put(EntityGiantZombie.class, new RenderGiantZombie(new ModelZombie(), 0.5F, 6.0F));
        this.entityRenderMap.put(EntityGhast.class, new RenderGhast());
        this.entityRenderMap.put(EntitySquid.class, new RenderSquid(new ModelSquid(), 0.7F));
        this.entityRenderMap.put(EntityVillager.class, new RenderVillager());
        this.entityRenderMap.put(EntityIronGolem.class, new RenderIronGolem());
        this.entityRenderMap.put(EntityBat.class, new RenderBat());
        this.entityRenderMap.put(EntityDragon.class, new RenderDragon());
        this.entityRenderMap.put(EntityEnderCrystal.class, new RenderEnderCrystal());
        this.entityRenderMap.put(EntityWither.class, new RenderWither());
        this.entityRenderMap.put(Entity.class, new RenderEntity());
        this.entityRenderMap.put(EntityPainting.class, new RenderPainting());
        this.entityRenderMap.put(EntityItemFrame.class, new RenderItemFrame());
        this.entityRenderMap.put(EntityLeashKnot.class, new RenderLeashKnot());
        this.entityRenderMap.put(EntityArrow.class, new RenderArrow());
        this.entityRenderMap.put(EntitySnowball.class, new RenderSnowball(Items.snowball));
        this.entityRenderMap.put(EntityEnderPearl.class, new RenderSnowball(Items.ender_pearl));
        this.entityRenderMap.put(EntityEnderEye.class, new RenderSnowball(Items.ender_eye));
        this.entityRenderMap.put(EntityEgg.class, new RenderSnowball(Items.egg));
        this.entityRenderMap.put(EntityPotion.class, new RenderSnowball(Items.potionitem, 16384));
        this.entityRenderMap.put(EntityExpBottle.class, new RenderSnowball(Items.experience_bottle));
        this.entityRenderMap.put(EntityFireworkRocket.class, new RenderSnowball(Items.fireworks));
        this.entityRenderMap.put(EntityLargeFireball.class, new RenderFireball(2.0F));
        this.entityRenderMap.put(EntitySmallFireball.class, new RenderFireball(0.5F));
        this.entityRenderMap.put(EntityWitherSkull.class, new RenderWitherSkull());
        this.entityRenderMap.put(EntityItem.class, new RenderItem());
        this.entityRenderMap.put(EntityXPOrb.class, new RenderXPOrb());
        this.entityRenderMap.put(EntityTNTPrimed.class, new RenderTNTPrimed());
        this.entityRenderMap.put(EntityFallingBlock.class, new RenderFallingBlock());
        this.entityRenderMap.put(EntityMinecartTNT.class, new RenderTntMinecart());
        this.entityRenderMap.put(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner());
        this.entityRenderMap.put(EntityMinecart.class, new RenderMinecart());
        this.entityRenderMap.put(EntityBoat.class, new RenderBoat());
        this.entityRenderMap.put(EntityFishHook.class, new RenderFish());
        this.entityRenderMap.put(EntityHorse.class, new RenderHorse(new ModelHorse(), 0.75F));
        this.entityRenderMap.put(EntityLightningBolt.class, new RenderLightningBolt());

        for (Render render : (Iterable<Render>)this.entityRenderMap.values()) {
            render.setRenderManager(this);
        }

        for (RenderPlayer renderPlayer2 : this.stringToRenderPlayerMap.values()) {
            renderPlayer2.setRenderManager(this);
        }
    }

    public Render getEntityClassRenderObject(Class clazz) {
        Render render = (Render)this.entityRenderMap.get(clazz);
        if (render == null && clazz != Entity.class) {
            render = this.getEntityClassRenderObject(clazz.getSuperclass());
            this.entityRenderMap.put(clazz, render);
        }

        return render;
    }

    public Render getEntityRenderObject(Entity entity) {
        if (entity instanceof AbstractClientPlayer) {
            String text = ((AbstractClientPlayer)entity).getText();
            RenderPlayer renderPlayer = this.stringToRenderPlayerMap.get(text);
            return renderPlayer != null ? renderPlayer : this.stringToRenderPlayerMap.get("default");
        } else {
            return this.getEntityClassRenderObject(entity.getClass());
        }
    }

    public void cacheActiveRenderInfo(World world, TextureManager textureManager, FontRenderer fontRenderer, EntityLivingBase entityLivingBase, Entity entity, GameSettings gameSettings, float f) {
        this.worldObj = world;
        this.renderEngine = textureManager;
        this.options = gameSettings;
        this.livingPlayer = entityLivingBase;
        this.entity = entity;
        this.textRenderer = fontRenderer;
        if (entityLivingBase.isPlayerSleeping()) {
            Block block = world.getBlock(MathHelper.floor_double(entityLivingBase.posX), MathHelper.floor_double(entityLivingBase.posY), MathHelper.floor_double(entityLivingBase.posZ));
            if (block == Blocks.bed) {
                int blockMetadata = world.getBlockMetadata(MathHelper.floor_double(entityLivingBase.posX), MathHelper.floor_double(entityLivingBase.posY), MathHelper.floor_double(entityLivingBase.posZ));
                int n = blockMetadata & 3;
                this.playerViewY = n * 90 + 180;
                this.playerViewX = 0.0F;
            }
        } else {
            FreeLookHandler freeLookHandler = CheatBreaker.getInstance().getModManager().freeLookHandler;
            if (freeLookHandler.active) {
                this.playerViewY = freeLookHandler.prevYaw + (freeLookHandler.yaw - freeLookHandler.prevYaw) * f;
                this.playerViewX = freeLookHandler.prevPitch + (freeLookHandler.pitch - freeLookHandler.prevPitch) * f;
            } else {
                this.playerViewY = entityLivingBase.prevRotationYaw + (entityLivingBase.rotationYaw - entityLivingBase.prevRotationYaw) * f;
                this.playerViewX = entityLivingBase.prevRotationPitch + (entityLivingBase.rotationPitch - entityLivingBase.prevRotationPitch) * f;
            }
        }

        if (gameSettings.thirdPersonView == 2) {
            this.playerViewY += 180.0F;
            this.playerViewX = -this.playerViewX;
        }

        this.viewerPosX = entityLivingBase.lastTickPosX + (entityLivingBase.posX - entityLivingBase.lastTickPosX) * f;
        this.viewerPosY = entityLivingBase.lastTickPosY + (entityLivingBase.posY - entityLivingBase.lastTickPosY) * f;
        this.viewerPosZ = entityLivingBase.lastTickPosZ + (entityLivingBase.posZ - entityLivingBase.lastTickPosZ) * f;
    }

    public boolean renderEntitySimple(Entity entity, float f) {
        return this.renderEntityStatic(entity, f, false);
    }

    public boolean renderEntityStatic(Entity entity, float f, boolean flag) {
        if (entity.ticksExisted == 0) {
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
        }

        double d = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * f;
        double d2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * f;
        double d3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * f;
        float f2 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * f;
        int brightnessForRender = entity.getBrightnessForRender(f);
        if (entity.isBurning()) {
            brightnessForRender = 15728880;
        }

        int n = brightnessForRender % 65536;
        int n2 = brightnessForRender / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        return this.doRenderEntity(entity, d - renderPosX, d2 - renderPosY, d3 - renderPosZ, f2, f, flag);
    }

    public boolean renderEntityWithPosYaw(Entity entity, double d, double d2, double d3, float f, float f2) {
        return this.doRenderEntity(entity, d, d2, d3, f, f2, false);
    }

    public boolean doRenderEntity(Entity entity, double d, double d2, double d3, float f, float f2, boolean flag) {
        Render render = null;

        try {
            render = this.getEntityRenderObject(entity);
            if (render != null && this.renderEngine != null) {
                if (!render.isStaticEntity() || flag) {
                    try {
                        render.doRender(entity, d, d2, d3, f, f2);
                    } catch (Throwable e) {
                        throw new ReportedException(CrashReport.makeCrashReport(e, "Rendering entity in world"));
                    }

                    try {
                        render.doRenderShadowAndFire(entity, d, d2, d3, f, f2);
                    } catch (Throwable e) {
                        throw new ReportedException(CrashReport.makeCrashReport(e, "Post-rendering entity in world"));
                    }

                    if (!entity.isInvisible() && !flag) {
                        try {
                            CheatBreaker.getInstance().getEventBus().handleEvent(new EntityDoubleFloatEvent(entity, d, d2, d3, f, f2));
                        } catch (Throwable e) {
                            throw new ReportedException(CrashReport.makeCrashReport(e, "Rendering entity hitbox in world"));
                        }
                    }
                }

                return true;
            } else {
                return this.renderEngine == null;
            }
        } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Rendering entity in world");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Entity being rendered");
            entity.addEntityCrashInfo(makeCategory);
            CrashReportCategory makeCategory2 = makeCrashReport.makeCategory("Renderer details");
            makeCategory2.addCrashSection("Assigned renderer", render);
            makeCategory2.addCrashSection("Location", CrashReportCategory.createPositionString(d, d2, d3));
            makeCategory2.addCrashSection("Rotation", f);
            makeCategory2.addCrashSection("Delta", f2);
            throw new ReportedException(makeCrashReport);
        }
    }

    public void set(World world) {
        this.worldObj = world;
    }

    public double getDistanceToCamera(double d, double d2, double d3) {
        double d4 = d - this.viewerPosX;
        double d5 = d2 - this.viewerPosY;
        double d6 = d3 - this.viewerPosZ;
        return d4 * d4 + d5 * d5 + d6 * d6;
    }

    public FontRenderer getFontRenderer() {
        return this.textRenderer;
    }

    public void updateIcons(IIconRegister iconRegister) {
        for (Object obj : this.entityRenderMap.values()) {
            Render render = (Render)obj;
            render.updateIcons(iconRegister);
        }
    }

    public Map<Class, Render> getEntityRenderMap() {
        return this.entityRenderMap;
    }
}
