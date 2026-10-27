package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public abstract class StructureStart {
   public LinkedList<StructureComponent> components = new LinkedList<>();
   public StructureBoundingBox boundingBox;
   public int chunkPosX;
   public int chunkPosZ;

   public StructureStart() {
   }

   public StructureStart(int chunkX, int chunkZ) {
      this.chunkPosX = chunkX;
      this.chunkPosZ = chunkZ;
   }

   public StructureBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public LinkedList<StructureComponent> getComponents() {
      return this.components;
   }

   public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb) {
      Iterator iterator = this.components.iterator();

      while (iterator.hasNext()) {
         StructureComponent structureComponent = (StructureComponent)iterator.next();
         if (structureComponent.getBoundingBox().intersectsWith(structurebb) && !structureComponent.addComponentParts(worldIn, rand, structurebb)) {
            iterator.remove();
         }
      }
   }

   public void updateBoundingBox() {
      this.boundingBox = StructureBoundingBox.getNewBoundingBox();

      for (StructureComponent structureComponent : this.components) {
         this.boundingBox.expandTo(structureComponent.getBoundingBox());
      }
   }

   public NBTTagCompound writeStructureComponentsToNBT(int chunkX, int chunkZ) {
      NBTTagCompound nbttagCompound = new NBTTagCompound();
      nbttagCompound.setString("id", MapGenStructureIO.getStructureStartName(this));
      nbttagCompound.setInteger("ChunkX", chunkX);
      nbttagCompound.setInteger("ChunkZ", chunkZ);
      nbttagCompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
      NBTTagList nbttagList = new NBTTagList();

      for (StructureComponent structureComponent : this.components) {
         nbttagList.appendTag(structureComponent.createStructureBaseNBT());
      }

      nbttagCompound.setTag("Children", nbttagList);
      this.writeToNBT(nbttagCompound);
      return nbttagCompound;
   }

   public void writeToNBT(NBTTagCompound tagCompound) {
   }

   public void readStructureComponentsFromNBT(World worldIn, NBTTagCompound tagCompound) {
      this.chunkPosX = tagCompound.getInteger("ChunkX");
      this.chunkPosZ = tagCompound.getInteger("ChunkZ");
      if (tagCompound.hasKey("BB")) {
         this.boundingBox = new StructureBoundingBox(tagCompound.getIntArray("BB"));
      }

      NBTTagList tagList = tagCompound.getTagList("Children", 10);

      for (int i = 0; i < tagList.tagCount(); i++) {
         this.components.add(MapGenStructureIO.getStructureComponent(tagList.getCompoundTagAt(i), worldIn));
      }

      this.readFromNBT(tagCompound);
   }

   public void readFromNBT(NBTTagCompound tagCompound) {
   }

   public void markAvailableHeight(World worldIn, Random rand, int n) {
      int n2 = worldIn.getSeaLevel() - n;
      int n3 = this.boundingBox.getYSize() + 1;
      if (n3 < n2) {
         n3 += rand.nextInt(n2 - n3);
      }

      int n4 = n3 - this.boundingBox.maxY;
      this.boundingBox.offset(0, n4, 0);

      for (StructureComponent structureComponent : this.components) {
         structureComponent.process(0, n4, 0);
      }
   }

   public void setRandomHeight(World worldIn, Random rand, int n, int n2) {
      int n3 = n2 - n + 1 - this.boundingBox.getYSize();
      int n4 = 1;
      if (n3 > 1) {
         n4 = n + rand.nextInt(n3);
      } else {
         n4 = n;
      }

      int n5 = n4 - this.boundingBox.minY;
      this.boundingBox.offset(0, n5, 0);

      for (StructureComponent structureComponent : this.components) {
         structureComponent.process(0, n5, 0);
      }
   }

   public boolean isSizeableStructure() {
      return true;
   }

   public boolean checkChunkCoordIntPair(ChunkCoordIntPair pair) {
      return true;
   }

   public void handleChunkCoordIntPair(ChunkCoordIntPair pair) {
   }

   public int getChunkPosX() {
      return this.chunkPosX;
   }

   public int getChunkPosZ() {
      return this.chunkPosZ;
   }
}
