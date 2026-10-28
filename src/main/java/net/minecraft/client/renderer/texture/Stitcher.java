package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.util.MathHelper;

public class Stitcher {
    public Set setStitchHolders = new HashSet(256);
    public List stitchSlots = new ArrayList(256);
    public int mipmapLevelStitcher;
    public int currentWidth;
    public int currentHeight;
    public int maxWidth;
    public int maxHeight;
    public boolean forcePowerOf2;
    public int maxTileDimension;

    public Stitcher(int n, int n2, boolean flag, int n3, int n4) {
        this.mipmapLevelStitcher = n4;
        this.maxWidth = n;
        this.maxHeight = n2;
        this.forcePowerOf2 = flag;
        this.maxTileDimension = n3;
    }

    public int getCurrentWidth() {
        return this.currentWidth;
    }

    public int getCurrentHeight() {
        return this.currentHeight;
    }

    public void addSprite(TextureAtlasSprite textureAtlasSprite) {
        Stitcher.Holder holder = new Stitcher.Holder(textureAtlasSprite, this.mipmapLevelStitcher);
        if (this.maxTileDimension > 0) {
            holder.setNewDimension(this.maxTileDimension);
        }

        this.setStitchHolders.add(holder);
    }

    public void doStitch() {
        Stitcher.Holder[] toArray = (net.minecraft.client.renderer.texture.Stitcher.Holder[])this.setStitchHolders.toArray(new Stitcher.Holder[this.setStitchHolders.size()]);
        Arrays.sort(toArray);
        Stitcher.Holder[] holders = toArray;
        int length = toArray.length;

        for (int i = 0; i < length; i++) {
            Stitcher.Holder holder = holders[i];
            if (!this.allocateSlot(holder)) {
                String format = String.format(
                    "Unable to fit: %s, size: %dx%d, atlas: %dx%d, atlasMax: %dx%d - Maybe try a lower resolution resourcepack?",
                    holder.getAtlasSprite().getIconName(),
                    holder.getAtlasSprite().getIconWidth(),
                    holder.getAtlasSprite().getIconHeight(),
                    this.currentWidth,
                    this.currentHeight,
                    this.maxWidth,
                    this.maxHeight
                );
                throw new StitcherException(holder, format);
            }
        }

        if (this.forcePowerOf2) {
            this.currentWidth = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
            this.currentHeight = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
        }
    }

    public List getStichSlots() {
        ArrayList newArrayList = Lists.newArrayList();

        for (Stitcher.Slot slot : (Iterable<Stitcher.Slot>)this.stitchSlots) {
            slot.getAllStitchSlots(newArrayList);
        }

        ArrayList newArrayList2 = Lists.newArrayList();

        for (Stitcher.Slot slot : (Iterable<Stitcher.Slot>)newArrayList) {
            Stitcher.Holder stitchHolder = slot.getStitchHolder();
            TextureAtlasSprite atlasSprite = stitchHolder.getAtlasSprite();
            atlasSprite.initSprite(this.currentWidth, this.currentHeight, slot.getOriginX(), slot.getOriginY(), stitchHolder.isRotated());
            newArrayList2.add(atlasSprite);
        }

        return newArrayList2;
    }

    public static int getMipmapDimension(int n, int n2) {
        return (n >> n2) + ((n & (1 << n2) - 1) == 0 ? 0 : 1) << n2;
    }

    public boolean allocateSlot(Stitcher.Holder holder) {
        for (int i = 0; i < this.stitchSlots.size(); i++) {
            if (((Stitcher.Slot)this.stitchSlots.get(i)).addSlot(holder)) {
                return true;
            }

            holder.rotate();
            if (((Stitcher.Slot)this.stitchSlots.get(i)).addSlot(holder)) {
                return true;
            }

            holder.rotate();
        }

        return this.expandAndAllocateSlot(holder);
    }

    public boolean expandAndAllocateSlot(Stitcher.Holder holder) {
        int min = Math.min(holder.getWidth(), holder.getHeight());
        boolean flag = this.currentWidth == 0 && this.currentHeight == 0;
        boolean flag2;
        if (this.forcePowerOf2) {
            int roundUpToPowerOfTwo = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
            int roundUpToPowerOfTwo2 = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
            int roundUpToPowerOfTwo3 = MathHelper.roundUpToPowerOfTwo(this.currentWidth + min);
            int roundUpToPowerOfTwo4 = MathHelper.roundUpToPowerOfTwo(this.currentHeight + min);
            boolean flag3 = roundUpToPowerOfTwo3 <= this.maxWidth;
            boolean flag4 = roundUpToPowerOfTwo4 <= this.maxHeight;
            if (!flag3 && !flag4) {
                return false;
            }

            boolean flag5 = roundUpToPowerOfTwo != roundUpToPowerOfTwo3;
            boolean flag6 = roundUpToPowerOfTwo2 != roundUpToPowerOfTwo4;
            if (flag5 ^ flag6) {
                flag2 = !flag5;
            } else {
                flag2 = flag3 && roundUpToPowerOfTwo <= roundUpToPowerOfTwo2;
            }
        } else {
            boolean flag3 = this.currentWidth + min <= this.maxWidth;
            boolean flag4 = this.currentHeight + min <= this.maxHeight;
            if (!flag3 && !flag4) {
                return false;
            }

            flag2 = flag3 && (flag || this.currentWidth <= this.currentHeight);
        }

        int max = Math.max(holder.getWidth(), holder.getHeight());
        if (MathHelper.roundUpToPowerOfTwo((!flag2 ? this.currentHeight : this.currentWidth) + max) > (!flag2 ? this.maxHeight : this.maxWidth)) {
            return false;
        }

        Stitcher.Slot slot;
        if (flag2) {
            if (holder.getWidth() > holder.getHeight()) {
                holder.rotate();
            }

            if (this.currentHeight == 0) {
                this.currentHeight = holder.getHeight();
            }

            slot = new Stitcher.Slot(this.currentWidth, 0, holder.getWidth(), this.currentHeight);
            this.currentWidth = this.currentWidth + holder.getWidth();
        } else {
            slot = new Stitcher.Slot(0, this.currentHeight, this.currentWidth, holder.getHeight());
            this.currentHeight = this.currentHeight + holder.getHeight();
        }

        slot.addSlot(holder);
        this.stitchSlots.add(slot);
        return true;
    }

    public static class Holder implements Comparable {
        public float scaleFactor = 1.0F;
        public TextureAtlasSprite theTexture;
        public int width;
        public int height;
        public int mipmapLevelHolder;
        public boolean rotated;

        public Holder(TextureAtlasSprite textureAtlasSprite, int n) {
            this.theTexture = textureAtlasSprite;
            this.width = textureAtlasSprite.getIconWidth();
            this.height = textureAtlasSprite.getIconHeight();
            this.mipmapLevelHolder = n;
            this.rotated = Stitcher.getMipmapDimension(this.height, n) > Stitcher.getMipmapDimension(this.width, n);
        }

        public TextureAtlasSprite getAtlasSprite() {
            return this.theTexture;
        }

        public int getWidth() {
            return this.rotated
                ? Stitcher.getMipmapDimension((int)(this.height * this.scaleFactor), this.mipmapLevelHolder)
                : Stitcher.getMipmapDimension((int)(this.width * this.scaleFactor), this.mipmapLevelHolder);
        }

        public int getHeight() {
            return this.rotated
                ? Stitcher.getMipmapDimension((int)(this.width * this.scaleFactor), this.mipmapLevelHolder)
                : Stitcher.getMipmapDimension((int)(this.height * this.scaleFactor), this.mipmapLevelHolder);
        }

        public void rotate() {
            this.rotated = !this.rotated;
        }

        public boolean isRotated() {
            return this.rotated;
        }

        public void setNewDimension(int n) {
            if (this.width > n && this.height > n) {
                this.scaleFactor = (float)n / Math.min(this.width, this.height);
            }
        }

        @Override
        public String toString() {
            return "Holder{width=" + this.width + ", height=" + this.height + '}';
        }

        public int compareTo(Stitcher.Holder holder) {
            int n;
            if (this.getHeight() == holder.getHeight()) {
                if (this.getWidth() == holder.getWidth()) {
                    if (this.theTexture.getIconName() == null) {
                        return holder.theTexture.getIconName() == null ? 0 : -1;
                    }

                    return this.theTexture.getIconName().compareTo(holder.theTexture.getIconName());
                }

                n = this.getWidth() < holder.getWidth() ? 1 : -1;
            } else {
                n = this.getHeight() < holder.getHeight() ? 1 : -1;
            }

            return n;
        }

        @Override
        public int compareTo(Object obj) {
            return this.compareTo((Stitcher.Holder)obj);
        }
    }

    public static class Slot {
        public int originX;
        public int originY;
        public int width;
        public int height;
        public List subSlots;
        public Stitcher.Holder holder;

        public Slot(int n, int n2, int n3, int n4) {
            this.originX = n;
            this.originY = n2;
            this.width = n3;
            this.height = n4;
        }

        public Stitcher.Holder getStitchHolder() {
            return this.holder;
        }

        public int getOriginX() {
            return this.originX;
        }

        public int getOriginY() {
            return this.originY;
        }

        public boolean addSlot(Stitcher.Holder holder) {
            if (this.holder != null) {
                return false;
            }

            int width = holder.getWidth();
            int height = holder.getHeight();
            if (width <= this.width && height <= this.height) {
                if (width == this.width && height == this.height) {
                    this.holder = holder;
                    return true;
                }

                if (this.subSlots == null) {
                    this.subSlots = new ArrayList(1);
                    this.subSlots.add(new Stitcher.Slot(this.originX, this.originY, width, height));
                    int n = this.width - width;
                    int n2 = this.height - height;
                    if (n2 > 0 && n > 0) {
                        int max = Math.max(this.height, n);
                        int max2 = Math.max(this.width, n2);
                        if (max >= max2) {
                            this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + height, width, n2));
                            this.subSlots.add(new Stitcher.Slot(this.originX + width, this.originY, n, this.height));
                        } else {
                            this.subSlots.add(new Stitcher.Slot(this.originX + width, this.originY, n, height));
                            this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + height, this.width, n2));
                        }
                    } else if (n == 0) {
                        this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + height, width, n2));
                    } else if (n2 == 0) {
                        this.subSlots.add(new Stitcher.Slot(this.originX + width, this.originY, n, height));
                    }
                }

                for (Stitcher.Slot slot : (Iterable<Stitcher.Slot>)this.subSlots) {
                    if (slot.addSlot(holder)) {
                        return true;
                    }
                }

                return false;
            } else {
                return false;
            }
        }

        public void getAllStitchSlots(List list) {
            if (this.holder != null) {
                list.add(this);
            } else if (this.subSlots != null) {
                for (Stitcher.Slot slot : (Iterable<Stitcher.Slot>)this.subSlots) {
                    slot.getAllStitchSlots(list);
                }
            }
        }

        @Override
        public String toString() {
            return "Slot{originX="
                + this.originX
                + ", originY="
                + this.originY
                + ", width="
                + this.width
                + ", height="
                + this.height
                + ", texture="
                + this.holder
                + ", subSlots="
                + this.subSlots
                + '}';
        }
    }
}
