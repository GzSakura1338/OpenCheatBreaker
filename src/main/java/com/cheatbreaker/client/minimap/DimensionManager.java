package com.cheatbreaker.client.minimap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldProvider;

public class DimensionManager implements IDimensionManager {
    public IVoxelMap voxelMap;
    public ArrayList<DimensionContainer> dimensions;

    @Override
    public DimensionContainer getDimensionById(int n) {
        for (DimensionContainer dimensionContainer : this.dimensions) {
            if (dimensionContainer.id == n) {
                return dimensionContainer;
            }
        }

        return null;
    }

    public DimensionContainer getByName(String str) {
        for (DimensionContainer dimensionContainer : this.dimensions) {
            if (dimensionContainer.name.equals(str)) {
                return dimensionContainer;
            }
        }

        return null;
    }

    @Override
    public void ensureDimension(int n) {
        DimensionContainer dimensionById = this.getDimensionById(n);
        if (dimensionById == null) {
            dimensionById = new DimensionContainer("notLoaded", n);
            this.dimensions.add(dimensionById);
            Collections.sort(this.dimensions, new Comparator<DimensionContainer>() {
                public int compare(DimensionContainer dimensionContainer, DimensionContainer dimensionContainer2) {
                    return dimensionContainer.id - dimensionContainer2.id;
                }
            });
        }

        if (dimensionById.name.equals("notLoaded") || dimensionById.name.equals("failedToLoad")) {
            try {
                dimensionById.name = Minecraft.getMinecraft().theWorld.provider.getDimensionName();
            } catch (Exception e) {
                dimensionById.name = "dimension " + n + "(" + Minecraft.getMinecraft().theWorld.provider.getClass().getSimpleName() + ")";
            }
        }
    }

    public DimensionManager(IVoxelMap voxelMap) {
        this.voxelMap = voxelMap;
        this.dimensions = new ArrayList<>();
    }

    @Override
    public void populateDimensions() {
        this.dimensions.clear();

        for (int i = -1; i <= 1; i++) {
            String str = "notLoaded";
            WorldProvider worldProvider = null;

            try {
                worldProvider = WorldProvider.getProviderForDimension(i);
            } catch (Exception e) {
                worldProvider = null;
            }

            if (worldProvider != null) {
                try {
                    str = worldProvider.getDimensionName();
                } catch (Exception e) {
                    str = "failedToLoad";
                }

                DimensionContainer dimensionContainer = new DimensionContainer(str, i);
                this.dimensions.add(dimensionContainer);
            }
        }

        for (Waypoint waypoint : this.voxelMap.getWaypointManager().getWaypoints()) {
            for (Integer integer : waypoint.dimensions) {
                if (this.getDimensionById(integer) == null) {
                    String str = "notLoaded";
                    WorldProvider worldProvider = null;

                    try {
                        worldProvider = WorldProvider.getProviderForDimension(integer);
                    } catch (Exception e) {
                        worldProvider = null;
                    }

                    if (worldProvider != null) {
                        try {
                            str = worldProvider.getDimensionName();
                        } catch (Exception e) {
                            str = "failedToLoad";
                        }

                        DimensionContainer dimensionContainer = new DimensionContainer(str, integer);
                        this.dimensions.add(dimensionContainer);
                    }
                }
            }
        }

        Collections.sort(this.dimensions, new Comparator<DimensionContainer>() {
            public int compare(DimensionContainer dimensionContainer, DimensionContainer dimensionContainer2) {
                return dimensionContainer.id - dimensionContainer2.id;
            }
        });
    }

    @Override
    public ArrayList<DimensionContainer> getDimensions() {
        return this.dimensions;
    }
}
