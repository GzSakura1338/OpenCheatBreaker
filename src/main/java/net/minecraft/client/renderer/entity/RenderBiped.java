package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;

public class RenderBiped extends RenderLiving {
    public ModelBiped modelBipedMain;
    public float floatValue;
    public ModelBiped modelArmourChestplate;
    public ModelBiped modelBiped;
    public static Map map = Maps.newHashMap();
    public static String[] bipedArmorFilenamePrefix = new String[]{"leather", "chainmail", "iron", "diamond", "gold"};

    public RenderBiped(ModelBiped modelBiped, float f) {
        this(modelBiped, f, 1.0F);
    }

    public RenderBiped(ModelBiped modelBiped, float f, float f2) {
        super(modelBiped, f);
        this.modelBipedMain = modelBiped;
        this.floatValue = f2;
        this.run();
    }

    public void run() {
        this.modelArmourChestplate = new ModelBiped(1.0F);
        this.modelBiped = new ModelBiped(0.5F);
    }

    public static ResourceLocation findResourceLocation(ItemArmor itemArmor, int n) {
        return createResourceLocation(itemArmor, n, null);
    }

    public static ResourceLocation createResourceLocation(ItemArmor itemArmor, int n, String str) {
        String format = String.format(
            "textures/models/armor/%s_layer_%d%s.png",
            bipedArmorFilenamePrefix[itemArmor.renderIndex],
            n == 2 ? 2 : 1,
            str == null ? "" : String.format("_%s", str)
        );
        ResourceLocation resourceLocation = (ResourceLocation)map.get(format);
        if (resourceLocation == null) {
            resourceLocation = new ResourceLocation(format);
            map.put(format, resourceLocation);
        }

        return resourceLocation;
    }

    public int shouldRenderPass(EntityLiving entityLiving, int n, float f) {
        ItemStack findItemStack = entityLiving.findItemStack(3 - n);
        if (findItemStack != null) {
            Item item = findItemStack.getItem();
            if (item instanceof ItemArmor) {
                ItemArmor itemArmor = (ItemArmor)item;
                this.bindTexture(findResourceLocation(itemArmor, n));
                ModelBiped modelBiped = n == 2 ? this.modelBiped : this.modelArmourChestplate;
                modelBiped.bipedHead.showModel = n == 0;
                modelBiped.bipedHeadwear.showModel = n == 0;
                modelBiped.bipedBody.showModel = n == 1 || n == 2;
                modelBiped.bipedRightArm.showModel = n == 1;
                modelBiped.bipedLeftArm.showModel = n == 1;
                modelBiped.bipedRightLeg.showModel = n == 2 || n == 3;
                modelBiped.bipedLeftLeg.showModel = n == 2 || n == 3;
                this.setRenderPassModel(modelBiped);
                modelBiped.swingProgress = this.mainModel.swingProgress;
                modelBiped.isRiding = this.mainModel.isRiding;
                modelBiped.isChild = this.mainModel.isChild;
                if (itemArmor.getArmorMaterial() == ArmorMaterial.CLOTH) {
                    int color = itemArmor.getColor(findItemStack);
                    float f2 = (color >> 16 & 0xFF) / 255.0F;
                    float f3 = (color >> 8 & 0xFF) / 255.0F;
                    float f4 = (color & 0xFF) / 255.0F;
                    GL11.glColor3f(f2, f3, f4);
                    if (findItemStack.isItemEnchanted()) {
                        return 31;
                    }

                    return 16;
                }

                GL11.glColor3f(1.0F, 1.0F, 1.0F);
                if (findItemStack.isItemEnchanted()) {
                    return 15;
                }

                return 1;
            }
        }

        return -1;
    }

    public void handleEntityLiving(EntityLiving entityLiving, int n, float f) {
        ItemStack findItemStack = entityLiving.findItemStack(3 - n);
        if (findItemStack != null) {
            Item item = findItemStack.getItem();
            if (item instanceof ItemArmor) {
                this.bindTexture(createResourceLocation((ItemArmor)item, n, "overlay"));
                float f2 = 1.0F;
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        ItemStack heldItem = entityLiving.getHeldItem();
        this.handleEntityLiving(entityLiving, heldItem);
        double d4 = d2 - entityLiving.yOffset;
        if (entityLiving.isSneaking()) {
            d4 -= 0.125;
        }

        super.doRender(entityLiving, d, d4, d3, f, f2);
        this.modelArmourChestplate.aimedBow = this.modelBiped.aimedBow = this.modelBipedMain.aimedBow = false;
        this.modelArmourChestplate.isSneak = this.modelBiped.isSneak = this.modelBipedMain.isSneak = false;
        this.modelArmourChestplate.heldItemRight = this.modelBiped.heldItemRight = this.modelBipedMain.heldItemRight = 0;
    }

    public ResourceLocation getEntityTexture(EntityLiving entityLiving) {
        return null;
    }

    public void handleEntityLiving(EntityLiving entityLiving, ItemStack itemStack) {
        this.modelArmourChestplate.heldItemRight = this.modelBiped.heldItemRight = this.modelBipedMain.heldItemRight = itemStack != null ? 1 : 0;
        this.modelArmourChestplate.isSneak = this.modelBiped.isSneak = this.modelBipedMain.isSneak = entityLiving.isSneaking();
    }

    public void renderEquippedItems(EntityLiving entityLiving, float f) {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        super.renderEquippedItems(entityLiving, f);
        ItemStack heldItem = entityLiving.getHeldItem();
        ItemStack findItemStack = entityLiving.findItemStack(3);
        if (findItemStack != null) {
            GL11.glPushMatrix();
            this.modelBipedMain.bipedHead.postRender(0.0625F);
            Item item = findItemStack.getItem();
            if (item instanceof ItemBlock) {
                if (RenderBlocks.renderItemIn3d(Block.getBlockFromItem(item).getRenderType())) {
                    float f2 = 0.625F;
                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(f2, -f2, -f2);
                }

                this.renderManager.itemRenderer.renderItem(entityLiving, findItemStack, 0);
            } else if (item == Items.skull) {
                float f2 = 1.0625F;
                GL11.glScalef(f2, -f2, -f2);
                GameProfile gameProfile = null;
                if (findItemStack.hasTagCompound()) {
                    NBTTagCompound tagCompound = findItemStack.getTagCompound();
                    if (tagCompound.hasKey("SkullOwner", 10)) {
                        gameProfile = NBTUtil.readGameProfileFromNBT(tagCompound.getCompoundTag("SkullOwner"));
                    } else if (tagCompound.hasKey("SkullOwner", 8) && !StringUtils.isNullOrEmpty(tagCompound.getString("SkullOwner"))) {
                        gameProfile = new GameProfile(null, tagCompound.getString("SkullOwner"));
                    }
                }

                TileEntitySkullRenderer.instance.render(-0.5F, 0.0F, -0.5F, 1, 180.0F, findItemStack.getMetadata(), gameProfile);
            }

            GL11.glPopMatrix();
        }

        if (heldItem != null && heldItem.getItem() != null) {
            Item item = heldItem.getItem();
            GL11.glPushMatrix();
            if (this.mainModel.isChild) {
                float f2 = 0.5F;
                GL11.glTranslatef(0.0F, 0.625F, 0.0F);
                GL11.glRotatef(-20.0F, -1.0F, 0.0F, 0.0F);
                GL11.glScalef(f2, f2, f2);
            }

            this.modelBipedMain.bipedRightArm.postRender(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
            if (item instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(item).getRenderType())) {
                float f2 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                f2 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-f2, -f2, f2);
            } else if (item == Items.bow) {
                float f2 = 0.625F;
                GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            } else if (item.isFull3D()) {
                float f2 = 0.625F;
                if (item.shouldRotateAroundWhenRendering()) {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                this.glTranslatef();
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            } else {
                float f2 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(f2, f2, f2);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }

            if (heldItem.getItem().requiresMultipleRenderPasses()) {
                for (int i = 0; i <= 1; i++) {
                    int colorFromItemStack = heldItem.getItem().getColorFromItemStack(heldItem, i);
                    float f2 = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
                    float f3 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
                    float f4 = (colorFromItemStack & 0xFF) / 255.0F;
                    GL11.glColor4f(f2, f3, f4, 1.0F);
                    this.renderManager.itemRenderer.renderItem(entityLiving, heldItem, i);
                }
            } else {
                int colorFromItemStack = heldItem.getItem().getColorFromItemStack(heldItem, 0);
                float f2 = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
                float f3 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
                float f4 = (colorFromItemStack & 0xFF) / 255.0F;
                GL11.glColor4f(f2, f3, f4, 1.0F);
                this.renderManager.itemRenderer.renderItem(entityLiving, heldItem, 0);
            }

            GL11.glPopMatrix();
        }
    }

    public void glTranslatef() {
        GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
    }

    @Override
    public void handleEntityLiving(EntityLivingBase entityLivingBase, int n, float f) {
        this.handleEntityLiving((EntityLiving)entityLivingBase, n, f);
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass((EntityLiving)entityLivingBase, n, f);
    }

    @Override
    public void renderEquippedItems(EntityLivingBase entityLivingBase, float f) {
        this.renderEquippedItems((EntityLiving)entityLivingBase, f);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityLiving)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityLiving)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityLiving)entity, d, d2, d3, f, f2);
    }
}
