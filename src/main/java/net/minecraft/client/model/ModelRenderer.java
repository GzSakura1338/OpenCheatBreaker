package net.minecraft.client.model;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.optifine.ModelSprite;
import org.lwjgl.opengl.GL11;

public class ModelRenderer {
    public List spriteList = new ArrayList();
    public boolean mirrorV = false;
    public int textureOffsetX;
    public int textureOffsetY;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    public boolean compiled;
    public int displayList;
    public boolean mirror;
    public float textureWidth = 64.0F;
    public boolean isHidden;
    public float textureHeight = 32.0F;
    public List childModels;
    public boolean showModel = true;
    public List cubeList = new ArrayList();
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public ModelBase baseModel;
    public String boxName;
    public float savedScale;

    public ModelRenderer(ModelBase modelBase, String str) {
        this.baseModel = modelBase;
        modelBase.boxList.add(this);
        this.boxName = str;
        this.setTextureSize(modelBase.textureWidth, modelBase.textureHeight);
    }

    public ModelRenderer(ModelBase modelBase) {
        this(modelBase, (String)null);
    }

    public ModelRenderer(ModelBase modelBase, int n, int n2) {
        this(modelBase);
        this.setTextureOffset(n, n2);
    }

    public void addChild(ModelRenderer modelRenderer) {
        if (this.childModels == null) {
            this.childModels = new ArrayList();
        }

        this.childModels.add(modelRenderer);
    }

    public ModelRenderer setTextureOffset(int n, int n2) {
        this.textureOffsetX = n;
        this.textureOffsetY = n2;
        return this;
    }

    public ModelRenderer addBox(String str, float f, float f2, float f3, int n, int n2, int n3) {
        str = this.boxName + "." + str;
        TextureOffset textureOffset = this.baseModel.getTextureOffset(str);
        this.setTextureOffset(textureOffset.textureOffsetX, textureOffset.textureOffsetY);
        this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, f, f2, f3, n, n2, n3, 0.0F).setName(str));
        return this;
    }

    public ModelRenderer addBox(float f, float f2, float f3, int n, int n2, int n3) {
        this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, f, f2, f3, n, n2, n3, 0.0F));
        return this;
    }

    public void addBox(float f, float f2, float f3, int n, int n2, int n3, float f4) {
        this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, f, f2, f3, n, n2, n3, f4));
    }

    public void setRotationPoint(float f, float f2, float f3) {
        this.rotationPointX = f;
        this.rotationPointY = f2;
        this.rotationPointZ = f3;
    }

    public void render(float f) {
        if (!this.isHidden && this.showModel) {
            if (!this.compiled) {
                this.compileDisplayList(f);
            }

            GL11.glTranslatef(this.offsetX, this.offsetY, this.offsetZ);
            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
                    GL11.glCallList(this.displayList);
                    if (this.childModels != null) {
                        for (int i = 0; i < this.childModels.size(); i++) {
                            ((ModelRenderer)this.childModels.get(i)).render(f);
                        }
                    }
                } else {
                    GL11.glTranslatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
                    GL11.glCallList(this.displayList);
                    if (this.childModels != null) {
                        for (int i = 0; i < this.childModels.size(); i++) {
                            ((ModelRenderer)this.childModels.get(i)).render(f);
                        }
                    }

                    GL11.glTranslatef(-this.rotationPointX * f, -this.rotationPointY * f, -this.rotationPointZ * f);
                }
            } else {
                GL11.glPushMatrix();
                GL11.glTranslatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
                if (this.rotateAngleZ != 0.0F) {
                    GL11.glRotatef(this.rotateAngleZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (this.rotateAngleY != 0.0F) {
                    GL11.glRotatef(this.rotateAngleY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F) {
                    GL11.glRotatef(this.rotateAngleX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                }

                GL11.glCallList(this.displayList);
                if (this.childModels != null) {
                    for (int i = 0; i < this.childModels.size(); i++) {
                        ((ModelRenderer)this.childModels.get(i)).render(f);
                    }
                }

                GL11.glPopMatrix();
            }

            GL11.glTranslatef(-this.offsetX, -this.offsetY, -this.offsetZ);
        }
    }

    public void renderWithRotation(float f) {
        if (!this.isHidden && this.showModel) {
            if (!this.compiled) {
                this.compileDisplayList(f);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
            if (this.rotateAngleY != 0.0F) {
                GL11.glRotatef(this.rotateAngleY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
                GL11.glRotatef(this.rotateAngleX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }

            if (this.rotateAngleZ != 0.0F) {
                GL11.glRotatef(this.rotateAngleZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            GL11.glCallList(this.displayList);
            GL11.glPopMatrix();
        }
    }

    public void postRender(float f) {
        if (!this.isHidden && this.showModel) {
            if (!this.compiled) {
                this.compileDisplayList(f);
            }

            if (this.rotateAngleX != 0.0F || this.rotateAngleY != 0.0F || this.rotateAngleZ != 0.0F) {
                GL11.glTranslatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
                if (this.rotateAngleZ != 0.0F) {
                    GL11.glRotatef(this.rotateAngleZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (this.rotateAngleY != 0.0F) {
                    GL11.glRotatef(this.rotateAngleY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F) {
                    GL11.glRotatef(this.rotateAngleX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                }
            } else if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
                GL11.glTranslatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
            }
        }
    }

    public void compileDisplayList(float f) {
        if (this.displayList == 0) {
            this.savedScale = f;
            this.displayList = GLAllocation.generateDisplayLists(1);
        }

        GL11.glNewList(this.displayList, 4864);
        Tessellator instance = Tessellator.instance;

        for (int i = 0; i < this.cubeList.size(); i++) {
            ((ModelBox)this.cubeList.get(i)).render(instance, f);
        }

        for (int i = 0; i < this.spriteList.size(); i++) {
            ModelSprite modelSprite = (ModelSprite)this.spriteList.get(i);
            modelSprite.render(Tessellator.instance, f);
        }

        GL11.glEndList();
        this.compiled = true;
    }

    public ModelRenderer setTextureSize(int n, int n2) {
        this.textureWidth = n;
        this.textureHeight = n2;
        return this;
    }

    public void addSprite(float f, float f2, float f3, int n, int n2, int n3, float f4) {
        this.spriteList.add(new ModelSprite(this, this.textureOffsetX, this.textureOffsetY, f, f2, f3, n, n2, n3, f4));
    }

    public boolean getCompiled() {
        return this.compiled;
    }

    public int getDisplayList() {
        return this.displayList;
    }

    public void resetDisplayList() {
        if (this.compiled) {
            this.compiled = false;
            this.compileDisplayList(this.savedScale);
        }
    }
}
