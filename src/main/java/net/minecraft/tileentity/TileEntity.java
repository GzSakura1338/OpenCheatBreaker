package net.minecraft.tileentity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntity {
    public static Logger logger = LogManager.getLogger();
    public static Map nameToClassMap = new HashMap();
    public static Map classToNameMap = new HashMap();
    public World worldObj;
    public int xCoord;
    public int yCoord;
    public int zCoord;
    public boolean tileEntityInvalid;
    public int blockMetadata = -1;
    public Block blockType;

    public static void addMapping(Class cl, String id) {
        if (nameToClassMap.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate id: " + id);
        }

        nameToClassMap.put(id, cl);
        classToNameMap.put(cl, id);
    }

    public World getWorld() {
        return this.worldObj;
    }

    public void setWorldObj(World worldIn) {
        this.worldObj = worldIn;
    }

    public boolean hasWorldObj() {
        return this.worldObj != null;
    }

    public void readFromNBT(NBTTagCompound compound) {
        this.xCoord = compound.getInteger("x");
        this.yCoord = compound.getInteger("y");
        this.zCoord = compound.getInteger("z");
    }

    public void writeToNBT(NBTTagCompound compound) {
        String str = (String)classToNameMap.get(this.getClass());
        if (str == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }

        compound.setString("id", str);
        compound.setInteger("x", this.xCoord);
        compound.setInteger("y", this.yCoord);
        compound.setInteger("z", this.zCoord);
    }

    public void updateEntity() {
    }

    public static TileEntity createAndLoadEntity(NBTTagCompound nbt) {
        TileEntity tileEntity = null;

        try {
            Class clazz = (Class)nameToClassMap.get(nbt.getString("id"));
            if (clazz != null) {
                tileEntity = (TileEntity)clazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (tileEntity != null) {
            tileEntity.readFromNBT(nbt);
        } else {
            logger.warn("Skipping BlockEntity with id " + nbt.getString("id"));
        }

        return tileEntity;
    }

    public int getBlockMetadata() {
        if (this.blockMetadata == -1) {
            this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        }

        return this.blockMetadata;
    }

    public void markDirty() {
        if (this.worldObj != null) {
            this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
            this.worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
            if (this.getBlockType() != Blocks.air) {
                this.worldObj.updateNeighborsAboutBlockChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
            }
        }
    }

    public double getDistanceSq(double d, double d2, double d3) {
        double d4 = this.xCoord + 0.5 - d;
        double d5 = this.yCoord + 0.5 - d2;
        double d6 = this.zCoord + 0.5 - d3;
        return d4 * d4 + d5 * d5 + d6 * d6;
    }

    public double getMaxRenderDistanceSquared() {
        return 4096.0;
    }

    public Block getBlockType() {
        if (this.blockType == null) {
            this.blockType = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
        }

        return this.blockType;
    }

    public Packet getDescriptionPacket() {
        return null;
    }

    public boolean isInvalid() {
        return this.tileEntityInvalid;
    }

    public void invalidate() {
        this.tileEntityInvalid = true;
    }

    public void validate() {
        this.tileEntityInvalid = false;
    }

    public boolean receiveClientEvent(int id, int type) {
        return false;
    }

    public void updateContainingBlockInfo() {
        this.blockType = null;
        this.blockMetadata = -1;
    }

    public void addInfoToCrashReport(CrashReportCategory reportCategory) {
        reportCategory.addCrashSectionCallable("Name", new Callable() {
            public String call() {
                return TileEntity.classToNameMap.get(TileEntity.this.getClass()) + " // " + TileEntity.this.getClass().getCanonicalName();
            }
        });
        CrashReportCategory.addBlockInfo(reportCategory, this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), this.getBlockMetadata());
        reportCategory.addCrashSectionCallable(
            "Actual block type",
            new Callable() {
                public String call() {
                    int idFromBlock = Block.getIdFromBlock(TileEntity.this.worldObj.getBlock(TileEntity.this.xCoord, TileEntity.this.yCoord, TileEntity.this.zCoord));

                    try {
                        return String.format(
                            "ID #%d (%s // %s)", idFromBlock, Block.getBlockById(idFromBlock).getUnlocalizedName(), Block.getBlockById(idFromBlock).getClass().getCanonicalName()
                        );
                    } catch (Throwable e) {
                        return "ID #" + idFromBlock;
                    }
                }
            }
        );
        reportCategory.addCrashSectionCallable("Actual block data value", new Callable() {
            public String call() {
                int blockMetadata = TileEntity.this.worldObj.getBlockMetadata(TileEntity.this.xCoord, TileEntity.this.yCoord, TileEntity.this.zCoord);
                if (blockMetadata < 0) {
                    return "Unknown? (Got " + blockMetadata + ")";
                }

                String replace = String.format("%4s", Integer.toBinaryString(blockMetadata)).replace(" ", "0");
                return String.format("%1$d / 0x%1$X / 0b%2$s", blockMetadata, replace);
            }
        });
    }

    static {
        addMapping(TileEntityFurnace.class, "Furnace");
        addMapping(TileEntityChest.class, "Chest");
        addMapping(TileEntityEnderChest.class, "EnderChest");
        addMapping(TileEntityJukebox.class, "RecordPlayer");
        addMapping(TileEntityDispenser.class, "Trap");
        addMapping(TileEntityDropper.class, "Dropper");
        addMapping(TileEntitySign.class, "Sign");
        addMapping(TileEntityMobSpawner.class, "MobSpawner");
        addMapping(TileEntityNote.class, "Music");
        addMapping(TileEntityPiston.class, "Piston");
        addMapping(TileEntityBrewingStand.class, "Cauldron");
        addMapping(TileEntityEnchantmentTable.class, "EnchantTable");
        addMapping(TileEntityEndPortal.class, "Airportal");
        addMapping(TileEntityCommandBlock.class, "Control");
        addMapping(TileEntityBeacon.class, "Beacon");
        addMapping(TileEntitySkull.class, "Skull");
        addMapping(TileEntityDaylightDetector.class, "DLDetector");
        addMapping(TileEntityHopper.class, "Hopper");
        addMapping(TileEntityComparator.class, "Comparator");
        addMapping(TileEntityFlowerPot.class, "FlowerPot");
    }
}
