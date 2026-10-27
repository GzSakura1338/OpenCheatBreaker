package net.minecraft.tileentity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TileEntity {
   public static Logger logger = LogManager.getLogger();
   public static Map<String, Class<? extends TileEntity>> nameToClassMap = Maps.newHashMap();
   public static Map<Class<? extends TileEntity>, String> classToNameMap = Maps.newHashMap();
   public World worldObj;
   public BlockPos pos = BlockPos.ORIGIN;
   public boolean tileEntityInvalid;
   public int blockMetadata = -1;
   public Block blockType;

   public static void addMapping(Class<? extends TileEntity> cl, String id) {
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
      this.pos = new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z"));
   }

   public void writeToNBT(NBTTagCompound compound) {
      String str = classToNameMap.get(this.getClass());
      if (str == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      }

      compound.setString("id", str);
      compound.setInteger("x", this.pos.getX());
      compound.setInteger("y", this.pos.getY());
      compound.setInteger("z", this.pos.getZ());
   }

   public static TileEntity createAndLoadEntity(NBTTagCompound nbt) {
      TileEntity tileEntity = null;

      try {
         Class clazz = nameToClassMap.get(nbt.getString("id"));
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
         IBlockState blockState = this.worldObj.getBlockState(this.pos);
         this.blockMetadata = blockState.getBlock().getMetaFromState(blockState);
      }

      return this.blockMetadata;
   }

   public void markDirty() {
      if (this.worldObj != null) {
         IBlockState blockState = this.worldObj.getBlockState(this.pos);
         this.blockMetadata = blockState.getBlock().getMetaFromState(blockState);
         this.worldObj.markChunkDirty(this.pos, this);
         if (this.getBlockType() != Blocks.air) {
            this.worldObj.updateComparatorOutputLevel(this.pos, this.getBlockType());
         }
      }
   }

   public double getDistanceSq(double x, double y, double z) {
      double d = this.pos.getX() + 0.5 - x;
      double d2 = this.pos.getY() + 0.5 - y;
      double d3 = this.pos.getZ() + 0.5 - z;
      return d * d + d2 * d2 + d3 * d3;
   }

   public double getMaxRenderDistanceSquared() {
      return 4096.0;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Block getBlockType() {
      if (this.blockType == null) {
         this.blockType = this.worldObj.getBlockState(this.pos).getBlock();
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
      reportCategory.addCrashSectionCallable("Name", new Callable<String>() {
         public String call() throws Exception {
            return TileEntity.classToNameMap.get(TileEntity.this.getClass()) + " // " + TileEntity.this.getClass().getCanonicalName();
         }
      });
      if (this.worldObj != null) {
         CrashReportCategory.addBlockInfo(reportCategory, this.pos, this.getBlockType(), this.getBlockMetadata());
         reportCategory.addCrashSectionCallable(
            "Actual block type",
            new Callable<String>() {
               public String call() throws Exception {
                  int idFromBlock = Block.getIdFromBlock(TileEntity.this.worldObj.getBlockState(TileEntity.this.pos).getBlock());

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
         reportCategory.addCrashSectionCallable("Actual block data value", new Callable<String>() {
            public String call() throws Exception {
               IBlockState blockState = TileEntity.this.worldObj.getBlockState(TileEntity.this.pos);
               int metaFromState = blockState.getBlock().getMetaFromState(blockState);
               if (metaFromState < 0) {
                  return "Unknown? (Got " + metaFromState + ")";
               }

               String replace = String.format("%4s", Integer.toBinaryString(metaFromState)).replace(" ", "0");
               return String.format("%1$d / 0x%1$X / 0b%2$s", metaFromState, replace);
            }
         });
      }
   }

   public void setPos(BlockPos posIn) {
      this.pos = posIn;
   }

   public boolean alwaysFalse() {
      return false;
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
      addMapping(TileEntityBanner.class, "Banner");
   }
}
