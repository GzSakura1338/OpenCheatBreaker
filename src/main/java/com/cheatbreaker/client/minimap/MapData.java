package com.cheatbreaker.client.minimap;

import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.world.biome.BiomeGenBase;

public class MapData {
   public static final int OCEAN_FLOOR_BLOCK_ID = 6;
   public Object dataLock = new Object();
   public static final int TRANSPARENT_HEIGHT = 10;
   public static final int OCEAN_FLOOR_METADATA = 7;
   public Object labelsLock = new Object();
   public static final int TRANSPARENT_TINT = 13;
   public static final int METADATA = 2;
   public static final int DATA_SIZE = 16;
   public static final int HEIGHT = 0;
   public ArrayList<MapData.BiomeLabel> biomeLabels = new ArrayList<>();
   public int width;
   public static final int OCEAN_FLOOR_HEIGHT = 5;
   public static final int TRANSPARENT_BLOCK_ID = 11;
   public int height;
   public static final int OCEAN_FLOOR_TINT = 8;
   public MapData.BiomeCell[][] biomeCells;
   public ArrayList<MapData.BiomeSegment> biomeSegments;
   public static final int TRANSPARENT_METADATA = 12;
   public static final int TINT = 3;
   public static final int BIOME_ID = 15;
   public int[] data;
   public static final int LIGHT = 4;
   public static final int BLOCK_ID = 1;
   public static final int TRANSPARENT_LIGHT = 14;
   public static final int OCEAN_FLOOR_LIGHT = 9;

   public int getOceanFloorBlockId(int n, int n2) {
      return this.getData(n, n2, 6);
   }

   public void setOceanFloorLight(int n, int n2, int n3) {
      this.setData(n, n2, 9, n3);
   }

   public int getData(int n, int n2, int n3) {
      int n4 = (n + n2 * this.width) * 16 + n3;
      return this.data[n4];
   }

   public int getOceanFloorTint(int n, int n2) {
      return this.getData(n, n2, 8);
   }

   public int getOceanFloorLight(int n, int n2) {
      return this.getData(n, n2, 9);
   }

   public void setTransparentTint(int n, int n2, int n3) {
      this.setData(n, n2, 13, n3);
   }

   public void setData(int n, int n2, int n3, int n4) {
      int n5 = (n + n2 * this.width) * 16 + n3;
      this.data[n5] = n4;
   }

   public int getTransparentLight(int n, int n2) {
      return this.getData(n, n2, 14);
   }

   public void segmentBiomes() {
      this.biomeCells = new MapData.BiomeCell[this.width][this.height];
      this.biomeSegments = new ArrayList<>();

      for (int i = 0; i < this.width; i++) {
         for (int j = 0; j < this.height; j++) {
            this.biomeCells[i][j] = new MapData.BiomeCell(i, j, this.getBiomeId(i, j));
         }
      }

      synchronized (this.dataLock) {
         for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
               if (!this.biomeCells[i][j].assigned) {
                  MapData.BiomeSegment biomeSegment = new MapData.BiomeSegment(this.biomeCells[i][j]);
                  this.biomeSegments.add(biomeSegment);
                  biomeSegment.floodFill();
               }
            }
         }
      }
   }

   public void setOceanFloorMetadata(int n, int n2, int n3) {
      this.setData(n, n2, 7, n3);
   }

   public void setOceanFloorBlockId(int n, int n2, int n3) {
      this.setData(n, n2, 6, n3);
   }

   public int getTint(int n, int n2) {
      return this.getData(n, n2, 3);
   }

   public int getOceanFloorMetadata(int n, int n2) {
      return this.getData(n, n2, 7);
   }

   public void setTint(int n, int n2, int n3) {
      this.setData(n, n2, 3, n3);
   }

   public ArrayList<MapData.BiomeLabel> getBiomeLabels() {
      ArrayList list = new ArrayList();
      synchronized (this.labelsLock) {
         list.addAll(this.biomeLabels);
         return list;
      }
   }

   public int getTransparentBlockId(int n, int n2) {
      return this.getData(n, n2, 11);
   }

   public int getOceanFloorHeight(int n, int n2) {
      return this.getData(n, n2, 5);
   }

   public void shiftX(int n) {
      synchronized (this.dataLock) {
         if (n > 0) {
            System.arraycopy(this.data, n * 16, this.data, 0, this.data.length - n * 16);
         } else if (n < 0) {
            System.arraycopy(this.data, 0, this.data, -n * 16, this.data.length + n * 16);
         }
      }
   }

   public int getTransparentTint(int n, int n2) {
      return this.getData(n, n2, 13);
   }

   public void setTransparentBlockId(int n, int n2, int n3) {
      this.setData(n, n2, 11, n3);
   }

   public void setMetadata(int n, int n2, int n3) {
      this.setData(n, n2, 2, n3);
   }

   public void setHeight(int n, int n2, int n3) {
      this.setData(n, n2, 0, n3);
   }

   public int getTransparentMetadata(int n, int n2) {
      return this.getData(n, n2, 12);
   }

   public void setTransparentMetadata(int n, int n2, int n3) {
      this.setData(n, n2, 12, n3);
   }

   public int getBlockId(int n, int n2) {
      return this.getData(n, n2, 1);
   }

   public void computeBiomeLabels() {
      if (this.biomeSegments != null) {
         for (int i = 0; i < this.biomeSegments.size(); i++) {
            MapData.BiomeSegment biomeSegment = this.biomeSegments.get(i);
            if (biomeSegment.biomeId != -1) {
               biomeSegment.finish();
            }
         }
      }

      synchronized (this.labelsLock) {
         this.biomeLabels.clear();
         if (this.biomeSegments != null) {
            for (int i = 0; i < this.biomeSegments.size(); i++) {
               MapData.BiomeSegment biomeSegment = this.biomeSegments.get(i);
               if (biomeSegment.biomeId != -1) {
                  MapData.BiomeLabel biomeLabel = new MapData.BiomeLabel();
                  biomeLabel.biomeId = biomeSegment.biomeId;
                  biomeLabel.cellCount = biomeSegment.cells.size();
                  biomeLabel.x = biomeSegment.labelX;
                  biomeLabel.z = biomeSegment.labelZ;
                  this.biomeLabels.add(biomeLabel);
               }
            }
         }
      }
   }

   public int getBiomeId(int n, int n2) {
      return this.getData(n, n2, 15);
   }

   public int getTransparentHeight(int n, int n2) {
      return this.getData(n, n2, 10);
   }

   public int getMetadata(int n, int n2) {
      return this.getData(n, n2, 2);
   }

   public void setTransparentLight(int n, int n2, int n3) {
      this.setData(n, n2, 14, n3);
   }

   public int getLight(int n, int n2) {
      return this.getData(n, n2, 4);
   }

   public int getHeight(int n, int n2) {
      return this.getData(n, n2, 0);
   }

   public void setOceanFloorHeight(int n, int n2, int n3) {
      this.setData(n, n2, 5, n3);
   }

   public void setBiomeId(int n, int n2, int n3) {
      this.setData(n, n2, 15, n3);
   }

   public void shiftZ(int n) {
      synchronized (this.dataLock) {
         if (n > 0) {
            System.arraycopy(this.data, n * this.width * 16, this.data, 0, this.data.length - n * this.width * 16);
         } else if (n < 0) {
            System.arraycopy(this.data, 0, this.data, -n * this.width * 16, this.data.length + n * this.width * 16);
         }
      }
   }

   public MapData(int n, int n2) {
      this.width = n;
      this.height = n2;
      this.data = new int[n * n2 * 16];
   }

   public void setOceanFloorTint(int n, int n2, int n3) {
      this.setData(n, n2, 8, n3);
   }

   public void setBlockId(int n, int n2, int n3) {
      this.setData(n, n2, 1, n3);
   }

   public void setTransparentHeight(int n, int n2, int n3) {
      this.setData(n, n2, 10, n3);
   }

   public void setLight(int n, int n2, int n3) {
      this.setData(n, n2, 4, n3);
   }

   public class BiomeCell {
      public boolean assigned = false;
      public boolean queued = false;
      public boolean unusedFlag = false;
      public int borderDepth = -1;
      public int biomeId = -1;
      public int x;
      public int z;

      public BiomeCell(int n, int n2, int n3) {
         this.x = n;
         this.z = n2;
         this.biomeId = n3;
      }
   }

   public class BiomeLabel {
      public int biomeId = -1;
      public int cellCount = 0;
      public int x = 0;
      public int z = 0;
   }

   public class BiomeSegment {
      public int labelX = 0;
      public int labelZ = 0;
      public int biomeId;
      public ArrayList<MapData.BiomeCell> cells;
      public ArrayList<MapData.BiomeCell> borderCells;

      public void updateCentroid() {
         this.computeCentroid(this.cells);
      }

      public void computeCentroid(Collection<MapData.BiomeCell> collection) {
         this.labelX = 0;
         this.labelZ = 0;

         for (MapData.BiomeCell biomeCell : collection) {
            this.labelX = this.labelX + biomeCell.x;
            this.labelZ = this.labelZ + biomeCell.z;
         }

         this.labelX = this.labelX / collection.size();
         this.labelZ = this.labelZ / collection.size();
      }

      public void finish() {
         this.updateCentroid();
         this.shrinkBorder();
      }

      public void shrinkBorder() {
         float f = Minecraft.getMinecraft().fontRendererObj.getStringWidth(BiomeGenBase.getBiomeGenArray()[this.biomeId].biomeName) + 8;
         float f2 = MapData.this.width / 32;
         float f3 = 2.0F;
         float f4 = f / 16.0F * f2 / f3;
         int n = 0;

         while (this.borderCells.size() > 0 && n < f4) {
            this.borderCells = this.expandBorder(this.borderCells, ++n);
         }

         if (this.borderCells.size() > 0) {
            ArrayList list = new ArrayList();

            for (MapData.BiomeCell biomeCell : this.cells) {
               if (biomeCell.borderDepth < 0 || biomeCell.borderDepth == n) {
                  list.add(biomeCell);
               }
            }

            this.snapToNearestCell(list);
         }
      }

      public ArrayList<MapData.BiomeCell> expandBorder(Collection<MapData.BiomeCell> collection, int n) {
         ArrayList list = new ArrayList();

         for (MapData.BiomeCell biomeCell : collection) {
            if (biomeCell.x < MapData.this.width - 2) {
               MapData.BiomeCell biomeCell2 = MapData.this.biomeCells[biomeCell.x + 1][biomeCell.z];
               MapData.BiomeCell biomeCell3 = MapData.this.biomeCells[biomeCell.x + 2][biomeCell.z];
               if (biomeCell2.biomeId == biomeCell.biomeId && biomeCell2.borderDepth < 0 && biomeCell3.biomeId == biomeCell.biomeId && biomeCell3.borderDepth < 0) {
                  biomeCell2.borderDepth = n;
                  biomeCell3.borderDepth = n;
                  list.add(biomeCell3);
               } else if (biomeCell2.biomeId == biomeCell.biomeId && biomeCell2.borderDepth < 0) {
                  biomeCell2.borderDepth = n;
                  list.add(biomeCell2);
               } else if (biomeCell3.biomeId == biomeCell.biomeId && biomeCell3.borderDepth < 0) {
                  biomeCell3.borderDepth = n;
                  list.add(biomeCell3);
               }
            }

            if (biomeCell.x > 1) {
               MapData.BiomeCell biomeCell2 = MapData.this.biomeCells[biomeCell.x - 1][biomeCell.z];
               MapData.BiomeCell biomeCell3 = MapData.this.biomeCells[biomeCell.x - 2][biomeCell.z];
               if (biomeCell2.biomeId == biomeCell.biomeId && biomeCell2.borderDepth < 0 && biomeCell3.biomeId == biomeCell.biomeId && biomeCell3.borderDepth < 0) {
                  biomeCell2.borderDepth = n;
                  biomeCell3.borderDepth = n;
                  list.add(biomeCell3);
               } else if (biomeCell2.biomeId == biomeCell.biomeId && biomeCell2.borderDepth < 0) {
                  biomeCell2.borderDepth = n;
                  list.add(biomeCell2);
               } else if (biomeCell3.biomeId == biomeCell.biomeId && biomeCell3.borderDepth < 0) {
                  biomeCell3.borderDepth = n;
                  list.add(biomeCell3);
               }
            }

            if (biomeCell.z < MapData.this.height - 1) {
               MapData.BiomeCell biomeCell2 = MapData.this.biomeCells[biomeCell.x][biomeCell.z + 1];
               if (biomeCell2.biomeId == biomeCell.biomeId && biomeCell2.borderDepth < 0) {
                  biomeCell2.borderDepth = n;
                  list.add(biomeCell2);
               }
            }

            if (biomeCell.z > 0) {
               MapData.BiomeCell biomeCell2 = MapData.this.biomeCells[biomeCell.x][biomeCell.z - 1];
               if (biomeCell2.biomeId == biomeCell.biomeId && biomeCell2.borderDepth < 0) {
                  biomeCell2.borderDepth = n;
                  list.add(biomeCell2);
               }
            }
         }

         if (list.size() > 0) {
            return list;
         }

         this.computeCentroid(collection);
         this.snapToNearestCell(collection);
         return list;
      }

      public void floodFill() {
         ArrayList list = new ArrayList();
         list.add(this.cells.remove(0));

         while (list.size() > 0) {
            MapData.BiomeCell biomeCell = (MapData.BiomeCell)list.remove(0);
            biomeCell.queued = false;
            if (biomeCell.biomeId == this.biomeId) {
               this.cells.add(biomeCell);
               biomeCell.assigned = true;
               boolean flag = false;
               if (biomeCell.x < MapData.this.width - 1) {
                  MapData.BiomeCell biomeCell2 = MapData.this.biomeCells[biomeCell.x + 1][biomeCell.z];
                  if (!biomeCell2.assigned && !biomeCell2.queued) {
                     list.add(biomeCell2);
                     biomeCell2.queued = true;
                  }

                  if (biomeCell2.biomeId != biomeCell.biomeId) {
                     flag = true;
                  }
               } else {
                  flag = true;
               }

               if (biomeCell.x > 0) {
                  MapData.BiomeCell biomeCell2 = MapData.this.biomeCells[biomeCell.x - 1][biomeCell.z];
                  if (!biomeCell2.assigned && !biomeCell2.queued) {
                     list.add(biomeCell2);
                     biomeCell2.queued = true;
                  }

                  if (biomeCell2.biomeId != biomeCell.biomeId) {
                     flag = true;
                  }
               } else {
                  flag = true;
               }

               if (biomeCell.z < MapData.this.height - 1) {
                  MapData.BiomeCell biomeCell2 = MapData.this.biomeCells[biomeCell.x][biomeCell.z + 1];
                  if (!biomeCell2.assigned && !biomeCell2.queued) {
                     list.add(biomeCell2);
                     biomeCell2.queued = true;
                  }

                  if (biomeCell2.biomeId != biomeCell.biomeId) {
                     flag = true;
                  }
               } else {
                  flag = true;
               }

               if (biomeCell.z > 0) {
                  MapData.BiomeCell biomeCell2 = MapData.this.biomeCells[biomeCell.x][biomeCell.z - 1];
                  if (!biomeCell2.assigned && !biomeCell2.queued) {
                     list.add(biomeCell2);
                     biomeCell2.queued = true;
                  }

                  if (biomeCell2.biomeId != biomeCell.biomeId) {
                     flag = true;
                  }
               } else {
                  flag = true;
               }

               if (flag) {
                  biomeCell.borderDepth = 0;
                  this.borderCells.add(biomeCell);
               }
            }
         }
      }

      public BiomeSegment(MapData.BiomeCell biomeCell) {
         this.biomeId = biomeCell.biomeId;
         this.cells = new ArrayList<>();
         this.cells.add(biomeCell);
         this.borderCells = new ArrayList<>();
      }

      public void snapToNearestCell(Collection<MapData.BiomeCell> collection) {
         int n = 131072;
         MapData.BiomeCell biomeCell = null;

         for (MapData.BiomeCell biomeCell2 : collection) {
            int n2 = (biomeCell2.x - this.labelX) * (biomeCell2.x - this.labelX) + (biomeCell2.z - this.labelZ) * (biomeCell2.z - this.labelZ);
            if (n2 < n) {
               n = n2;
               biomeCell = biomeCell2;
            }
         }

         this.labelX = biomeCell.x;
         this.labelZ = biomeCell.z;
      }
   }
}
