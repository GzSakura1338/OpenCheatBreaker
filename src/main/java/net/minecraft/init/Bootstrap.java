package net.minecraft.init;

import com.mojang.authlib.GameProfile;
import java.io.PrintStream;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LoggingPrintStream;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
   public static PrintStream SYSOUT = System.out;
   public static boolean alreadyRegistered = false;
   public static Logger LOGGER = LogManager.getLogger();

   public static boolean isRegistered() {
      return alreadyRegistered;
   }

   public static void registerDispenserBehaviors() {
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.arrow, new BehaviorProjectileDispense() {
         @Override
         public IProjectile getProjectileEntity(World worldIn, IPosition position) {
            EntityArrow entityArrow = new EntityArrow(worldIn, position.getX(), position.getY(), position.getZ());
            entityArrow.canBePickedUp = 1;
            return entityArrow;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.egg, new BehaviorProjectileDispense() {
         @Override
         public IProjectile getProjectileEntity(World worldIn, IPosition position) {
            return new EntityEgg(worldIn, position.getX(), position.getY(), position.getZ());
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.snowball, new BehaviorProjectileDispense() {
         @Override
         public IProjectile getProjectileEntity(World worldIn, IPosition position) {
            return new EntitySnowball(worldIn, position.getX(), position.getY(), position.getZ());
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.experience_bottle, new BehaviorProjectileDispense() {
         @Override
         public IProjectile getProjectileEntity(World worldIn, IPosition position) {
            return new EntityExpBottle(worldIn, position.getX(), position.getY(), position.getZ());
         }

         @Override
         public float getFloatValue() {
            return super.getFloatValue() * 0.5F;
         }

         @Override
         public float getFloatValue2() {
            return super.getFloatValue2() * 1.25F;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.potionitem, new IBehaviorDispenseItem() {
         public BehaviorDefaultDispenseItem dispenserBehavior = new BehaviorDefaultDispenseItem();

         @Override
         public ItemStack dispense(IBlockSource source, final ItemStack stack) {
            return ItemPotion.isSplash(stack.getMetadata()) ? (new BehaviorProjectileDispense() {
               @Override
               public IProjectile getProjectileEntity(World worldIn, IPosition position) {
                  return new EntityPotion(worldIn, position.getX(), position.getY(), position.getZ(), stack.copy());
               }

               @Override
               public float getFloatValue() {
                  return super.getFloatValue() * 0.5F;
               }

               @Override
               public float getFloatValue2() {
                  return super.getFloatValue2() * 1.25F;
               }
            }).dispense(source, stack) : this.dispenserBehavior.dispense(source, stack);
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.spawn_egg, new BehaviorDefaultDispenseItem() {
         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing facing = BlockDispenser.getFacing(source.getBlockMetadata());
            double d = source.getX() + facing.getFrontOffsetX();
            double d2 = source.getBlockPos().getY() + 0.2F;
            double d3 = source.getZ() + facing.getFrontOffsetZ();
            Entity spawnCreature = ItemMonsterPlacer.spawnCreature(source.getWorld(), stack.getMetadata(), d, d2, d3);
            if (spawnCreature instanceof EntityLivingBase && stack.hasDisplayName()) {
               ((EntityLiving)spawnCreature).setCustomNameTag(stack.getDisplayName());
            }

            stack.splitStack(1);
            return stack;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fireworks, new BehaviorDefaultDispenseItem() {
         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing facing = BlockDispenser.getFacing(source.getBlockMetadata());
            double d = source.getX() + facing.getFrontOffsetX();
            double d2 = source.getBlockPos().getY() + 0.2F;
            double d3 = source.getZ() + facing.getFrontOffsetZ();
            EntityFireworkRocket entityFireworkRocket = new EntityFireworkRocket(source.getWorld(), d, d2, d3, stack);
            source.getWorld().spawnEntityInWorld(entityFireworkRocket);
            stack.splitStack(1);
            return stack;
         }

         @Override
         public void playDispenseSound(IBlockSource source) {
            source.getWorld().playAuxSFX(1002, source.getBlockPos(), 0);
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fire_charge, new BehaviorDefaultDispenseItem() {
         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing facing = BlockDispenser.getFacing(source.getBlockMetadata());
            IPosition dispensePosition = BlockDispenser.getDispensePosition(source);
            double d = dispensePosition.getX() + facing.getFrontOffsetX() * 0.3F;
            double d2 = dispensePosition.getY() + facing.getFrontOffsetY() * 0.3F;
            double d3 = dispensePosition.getZ() + facing.getFrontOffsetZ() * 0.3F;
            World world = source.getWorld();
            Random rand = world.rand;
            double d4 = rand.nextGaussian() * 0.05 + facing.getFrontOffsetX();
            double d5 = rand.nextGaussian() * 0.05 + facing.getFrontOffsetY();
            double d6 = rand.nextGaussian() * 0.05 + facing.getFrontOffsetZ();
            world.spawnEntityInWorld(new EntitySmallFireball(world, d, d2, d3, d4, d5, d6));
            stack.splitStack(1);
            return stack;
         }

         @Override
         public void playDispenseSound(IBlockSource source) {
            source.getWorld().playAuxSFX(1009, source.getBlockPos(), 0);
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.boat, new BehaviorDefaultDispenseItem() {
         public BehaviorDefaultDispenseItem behavior = new BehaviorDefaultDispenseItem();

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing facing = BlockDispenser.getFacing(source.getBlockMetadata());
            World world = source.getWorld();
            double d = source.getX() + facing.getFrontOffsetX() * 1.125F;
            double d2 = source.getY() + facing.getFrontOffsetY() * 1.125F;
            double d3 = source.getZ() + facing.getFrontOffsetZ() * 1.125F;
            BlockPos offset = source.getBlockPos().offset(facing);
            Material material = world.getBlockState(offset).getBlock().getMaterial();
            double d4;
            if (Material.water.equals(material)) {
               d4 = 1.0;
            } else {
               if (!Material.air.equals(material) || !Material.water.equals(world.getBlockState(offset.down()).getBlock().getMaterial())) {
                  return this.behavior.dispense(source, stack);
               }

               d4 = 0.0;
            }

            EntityBoat entityBoat = new EntityBoat(world, d, d2 + d4, d3);
            world.spawnEntityInWorld(entityBoat);
            stack.splitStack(1);
            return stack;
         }

         @Override
         public void playDispenseSound(IBlockSource source) {
            source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
         }
      });
      BehaviorDefaultDispenseItem behaviorDefaultDispenseItem = new BehaviorDefaultDispenseItem() {
         public BehaviorDefaultDispenseItem behavior = new BehaviorDefaultDispenseItem();

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            ItemBucket item = (ItemBucket)stack.getItem();
            BlockPos offset = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            if (item.tryPlaceContainedLiquid(source.getWorld(), offset)) {
               stack.setItem(Items.bucket);
               stack.stackSize = 1;
               return stack;
            } else {
               return this.behavior.dispense(source, stack);
            }
         }
      };
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.lava_bucket, behaviorDefaultDispenseItem);
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.water_bucket, behaviorDefaultDispenseItem);
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.bucket, new BehaviorDefaultDispenseItem() {
         public BehaviorDefaultDispenseItem behavior = new BehaviorDefaultDispenseItem();

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            BlockPos offset = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            IBlockState blockState = world.getBlockState(offset);
            Block block = blockState.getBlock();
            Material material = block.getMaterial();
            Item item;
            if (Material.water.equals(material) && block instanceof BlockLiquid && blockState.getValue(BlockLiquid.LEVEL) == 0) {
               item = Items.water_bucket;
            } else {
               if (!Material.lava.equals(material) || !(block instanceof BlockLiquid) || blockState.getValue(BlockLiquid.LEVEL) != 0) {
                  return super.dispenseStack(source, stack);
               }

               item = Items.lava_bucket;
            }

            world.setBlockToAir(offset);
            if (--stack.stackSize == 0) {
               stack.setItem(item);
               stack.stackSize = 1;
            } else if (source.<TileEntityDispenser>getBlockTileEntity().addItemStack(new ItemStack(item)) < 0) {
               this.behavior.dispense(source, new ItemStack(item));
            }

            return stack;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.flint_and_steel, new BehaviorDefaultDispenseItem() {
         public boolean success = true;

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            BlockPos offset = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            if (world.isAirBlock(offset)) {
               world.setBlockState(offset, Blocks.fire.getDefaultState());
               if (stack.attemptDamageItem(1, world.rand)) {
                  stack.stackSize = 0;
               }
            } else if (world.getBlockState(offset).getBlock() == Blocks.tnt) {
               Blocks.tnt.onBlockDestroyedByPlayer(world, offset, Blocks.tnt.getDefaultState().withProperty(BlockTNT.EXPLODE, true));
               world.setBlockToAir(offset);
            } else {
               this.success = false;
            }

            return stack;
         }

         @Override
         public void playDispenseSound(IBlockSource source) {
            if (this.success) {
               source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
            } else {
               source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
            }
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.dye, new BehaviorDefaultDispenseItem() {
         public boolean success = true;

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            if (EnumDyeColor.WHITE == EnumDyeColor.byDyeDamage(stack.getMetadata())) {
               World world = source.getWorld();
               BlockPos offset = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
               if (ItemDye.applyBonemeal(stack, world, offset)) {
                  if (!world.isRemote) {
                     world.playAuxSFX(2005, offset, 0);
                  }
               } else {
                  this.success = false;
               }

               return stack;
            } else {
               return super.dispenseStack(source, stack);
            }
         }

         @Override
         public void playDispenseSound(IBlockSource source) {
            if (this.success) {
               source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
            } else {
               source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
            }
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.tnt), new BehaviorDefaultDispenseItem() {
         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            BlockPos offset = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            EntityTNTPrimed entityTNTPrimed = new EntityTNTPrimed(world, offset.getX() + 0.5, offset.getY(), offset.getZ() + 0.5, (EntityLivingBase)null);
            world.spawnEntityInWorld(entityTNTPrimed);
            world.playSoundAtEntity(entityTNTPrimed, "game.tnt.primed", 1.0F, 1.0F);
            stack.stackSize--;
            return stack;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.skull, new BehaviorDefaultDispenseItem() {
         public boolean success = true;

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            EnumFacing facing = BlockDispenser.getFacing(source.getBlockMetadata());
            BlockPos offset = source.getBlockPos().offset(facing);
            BlockSkull skull = Blocks.skull;
            if (!world.isAirBlock(offset) || !skull.canDispenserPlace(world, offset, stack)) {
               this.success = false;
            } else if (!world.isRemote) {
               world.setBlockState(offset, skull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.UP), 3);
               TileEntity tileEntity = world.getTileEntity(offset);
               if (tileEntity instanceof TileEntitySkull) {
                  if (stack.getMetadata() == 3) {
                     GameProfile gameProfile = null;
                     if (stack.hasTagCompound()) {
                        NBTTagCompound tagCompound = stack.getTagCompound();
                        if (tagCompound.hasKey("SkullOwner", 10)) {
                           gameProfile = NBTUtil.readGameProfileFromNBT(tagCompound.getCompoundTag("SkullOwner"));
                        } else if (tagCompound.hasKey("SkullOwner", 8)) {
                           String string = tagCompound.getString("SkullOwner");
                           if (!StringUtils.isNullOrEmpty(string)) {
                              gameProfile = new GameProfile((UUID)null, string);
                           }
                        }
                     }

                     ((TileEntitySkull)tileEntity).setPlayerProfile(gameProfile);
                  } else {
                     ((TileEntitySkull)tileEntity).setType(stack.getMetadata());
                  }

                  ((TileEntitySkull)tileEntity).setSkullRotation(facing.getOpposite().getHorizontalIndex() * 4);
                  Blocks.skull.checkWitherSpawn(world, offset, (TileEntitySkull)tileEntity);
               }

               stack.stackSize--;
            }

            return stack;
         }

         @Override
         public void playDispenseSound(IBlockSource source) {
            if (this.success) {
               source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
            } else {
               source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
            }
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.pumpkin), new BehaviorDefaultDispenseItem() {
         public boolean success = true;

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            BlockPos offset = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            BlockPumpkin pumpkin = (BlockPumpkin)Blocks.pumpkin;
            if (world.isAirBlock(offset) && pumpkin.canDispenserPlace(world, offset)) {
               if (!world.isRemote) {
                  world.setBlockState(offset, pumpkin.getDefaultState(), 3);
               }

               stack.stackSize--;
            } else {
               this.success = false;
            }

            return stack;
         }

         @Override
         public void playDispenseSound(IBlockSource source) {
            if (this.success) {
               source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
            } else {
               source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
            }
         }
      });
   }

   public static void register() {
      if (!alreadyRegistered) {
         alreadyRegistered = true;
         if (LOGGER.isDebugEnabled()) {
            redirectOutputToLog();
         }

         Block.registerBlocks();
         BlockFire.init();
         Item.registerItems();
         StatList.init();
         registerDispenserBehaviors();
      }
   }

   public static void redirectOutputToLog() {
      System.setErr(new LoggingPrintStream("STDERR", System.err));
      System.setOut(new LoggingPrintStream("STDOUT", SYSOUT));
   }

   public static void printToSYSOUT(String str) {
      SYSOUT.println(str);
   }
}
