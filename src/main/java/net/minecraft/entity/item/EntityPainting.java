package net.minecraft.entity.item;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityPainting extends EntityHanging {
    public EntityPainting.EnumArt art;

    public EntityPainting(World world) {
        super(world);
    }

    public EntityPainting(World world, int n, int n2, int n3, int n4) {
        super(world, n, n2, n3, n4);
        ArrayList list = new ArrayList();

        for (EntityPainting.EnumArt enumArt : EntityPainting.EnumArt.values()) {
            this.art = enumArt;
            this.setDirection(n4);
            if (this.onValidSurface()) {
                list.add(enumArt);
            }
        }

        if (!list.isEmpty()) {
            this.art = (EntityPainting.EnumArt)list.get(this.rand.nextInt(list.size()));
        }

        this.setDirection(n4);
    }

    public EntityPainting(World world, int n, int n2, int n3, int n4, String str) {
        this(world, n, n2, n3, n4);

        for (EntityPainting.EnumArt enumArt : EntityPainting.EnumArt.values()) {
            if (enumArt.title.equals(str)) {
                this.art = enumArt;
                break;
            }
        }

        this.setDirection(n4);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setString("Motive", this.art.title);
        super.writeEntityToNBT(tagCompound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        String string = tagCompund.getString("Motive");

        for (EntityPainting.EnumArt enumArt : EntityPainting.EnumArt.values()) {
            if (enumArt.title.equals(string)) {
                this.art = enumArt;
            }
        }

        if (this.art == null) {
            this.art = EntityPainting.EnumArt.Kebab;
        }

        super.readEntityFromNBT(tagCompund);
    }

    @Override
    public int getWidthPixels() {
        return this.art.sizeX;
    }

    @Override
    public int getHeightPixels() {
        return this.art.sizeY;
    }

    @Override
    public void onBroken(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)entity;
            if (entityPlayer.capabilities.isCreativeMode) {
                return;
            }
        }

        this.entityDropItem(new ItemStack(Items.painting), 0.0F);
    }

    public enum EnumArt {
        Kebab("Kebab", 0, "Kebab", 16, 16, 0, 0),
        Aztec("Aztec", 1, "Aztec", 16, 16, 16, 0),
        Alban("Alban", 2, "Alban", 16, 16, 32, 0),
        Aztec2("Aztec2", 3, "Aztec2", 16, 16, 48, 0),
        Bomb("Bomb", 4, "Bomb", 16, 16, 64, 0),
        Plant("Plant", 5, "Plant", 16, 16, 80, 0),
        Wasteland("Wasteland", 6, "Wasteland", 16, 16, 96, 0),
        Pool("Pool", 7, "Pool", 32, 16, 0, 32),
        Courbet("Courbet", 8, "Courbet", 32, 16, 32, 32),
        Sea("Sea", 9, "Sea", 32, 16, 64, 32),
        Sunset("Sunset", 10, "Sunset", 32, 16, 96, 32),
        Creebet("Creebet", 11, "Creebet", 32, 16, 128, 32),
        Wanderer("Wanderer", 12, "Wanderer", 16, 32, 0, 64),
        Graham("Graham", 13, "Graham", 16, 32, 16, 64),
        Match("Match", 14, "Match", 32, 32, 0, 128),
        Bust("Bust", 15, "Bust", 32, 32, 32, 128),
        Stage("Stage", 16, "Stage", 32, 32, 64, 128),
        Void("Void", 17, "Void", 32, 32, 96, 128),
        SkullAndRoses("SkullAndRoses", 18, "SkullAndRoses", 32, 32, 128, 128),
        Wither("Wither", 19, "Wither", 32, 32, 160, 128),
        Fighters("Fighters", 20, "Fighters", 64, 32, 0, 96),
        Pointer("Pointer", 21, "Pointer", 64, 64, 0, 192),
        Pigscene("Pigscene", 22, "Pigscene", 64, 64, 64, 192),
        BurningSkull("BurningSkull", 23, "BurningSkull", 64, 64, 128, 192),
        Skeleton("Skeleton", 24, "Skeleton", 64, 48, 192, 64),
        DonkeyKong("DonkeyKong", 25, "DonkeyKong", 64, 48, 192, 112);

        public String title;
        public int sizeX;
        public int sizeY;
        public int offsetX;
        public int offsetY;
        public static int maxArtTitleLength = "SkullAndRoses".length();
        public static EntityPainting.EnumArt[] enumArtArray = new EntityPainting.EnumArt[]{
            Kebab,
            Aztec,
            Alban,
            Aztec2,
            Bomb,
            Plant,
            Wasteland,
            Pool,
            Courbet,
            Sea,
            Sunset,
            Creebet,
            Wanderer,
            Graham,
            Match,
            Bust,
            Stage,
            Void,
            SkullAndRoses,
            Wither,
            Fighters,
            Pointer,
            Pigscene,
            BurningSkull,
            Skeleton,
            DonkeyKong
        };

        EnumArt(String str, int n, String str2, int n2, int n3, int n4, int n5) {
            this.title = str2;
            this.sizeX = n2;
            this.sizeY = n3;
            this.offsetX = n4;
            this.offsetY = n5;
        }
    }
}
