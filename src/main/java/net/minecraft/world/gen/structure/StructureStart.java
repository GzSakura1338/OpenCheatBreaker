package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public abstract class StructureStart {
    public LinkedList components = new LinkedList();
    public StructureBoundingBox boundingBox;
    public int chunkX;
    public int chunkZ;

    public StructureStart() {
    }

    public StructureStart(int n, int n2) {
        this.chunkX = n;
        this.chunkZ = n2;
    }

    public StructureBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public LinkedList getComponents() {
        return this.components;
    }

    public void generateStructure(World world, Random random, StructureBoundingBox structureBoundingBox) {
        Iterator iterator = this.components.iterator();

        while (iterator.hasNext()) {
            StructureComponent structureComponent = (StructureComponent)iterator.next();
            if (structureComponent.getBoundingBox().intersectsWith(structureBoundingBox) && !structureComponent.addComponentParts(world, random, structureBoundingBox)) {
                iterator.remove();
            }
        }
    }

    public void updateBoundingBox() {
        this.boundingBox = StructureBoundingBox.getNewBoundingBox();

        for (StructureComponent structureComponent : (Iterable<StructureComponent>)this.components) {
            this.boundingBox.expandTo(structureComponent.getBoundingBox());
        }
    }

    public NBTTagCompound toNbt(int n, int n2) {
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        nbttagCompound.setString("id", MapGenStructureIO.getId(this));
        nbttagCompound.setInteger("ChunkX", n);
        nbttagCompound.setInteger("ChunkZ", n2);
        nbttagCompound.setTag("BB", this.boundingBox.toNbt());
        NBTTagList nbttagList = new NBTTagList();

        for (StructureComponent structureComponent : (Iterable<StructureComponent>)this.components) {
            nbttagList.appendTag(structureComponent.toNbt());
        }

        nbttagCompound.setTag("Children", nbttagList);
        this.serialize(nbttagCompound);
        return nbttagCompound;
    }

    public void serialize(NBTTagCompound nbttagCompound) {
    }

    public void fromNbt(World world, NBTTagCompound nbttagCompound) {
        this.chunkX = nbttagCompound.getInteger("ChunkX");
        this.chunkZ = nbttagCompound.getInteger("ChunkZ");
        if (nbttagCompound.hasKey("BB")) {
            this.boundingBox = new StructureBoundingBox(nbttagCompound.getIntArray("BB"));
        }

        NBTTagList tagList = nbttagCompound.getTagList("Children", 10);

        for (int i = 0; i < tagList.tagCount(); i++) {
            this.components.add(MapGenStructureIO.getStructurePieceFromNbt(tagList.getCompoundTagAt(i), world));
        }

        this.handleNBTTagCompound(nbttagCompound);
    }

    public void handleNBTTagCompound(NBTTagCompound nbttagCompound) {
    }

    public void markAvailableHeight(World world, Random random, int n) {
        int n2 = 63 - n;
        int n3 = this.boundingBox.getYSize() + 1;
        if (n3 < n2) {
            n3 += random.nextInt(n2 - n3);
        }

        int n4 = n3 - this.boundingBox.maxY;
        this.boundingBox.offset(0, n4, 0);

        for (StructureComponent structureComponent : (Iterable<StructureComponent>)this.components) {
            structureComponent.getBoundingBox().offset(0, n4, 0);
        }
    }

    public void setRandomHeight(World world, Random random, int n, int n2) {
        int n3 = n2 - n + 1 - this.boundingBox.getYSize();
        boolean flag = true;
        int n4;
        if (n3 > 1) {
            n4 = n + random.nextInt(n3);
        } else {
            n4 = n;
        }

        int n5 = n4 - this.boundingBox.minY;
        this.boundingBox.offset(0, n5, 0);

        for (StructureComponent structureComponent : (Iterable<StructureComponent>)this.components) {
            structureComponent.getBoundingBox().offset(0, n5, 0);
        }
    }

    public boolean isSizeableStructure() {
        return true;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }
}
