package net.minecraft.init;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class Bootstrap {
    public static boolean alreadyRegistered = false;

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
            public BehaviorDefaultDispenseItem itemDispenserBehavior = new BehaviorDefaultDispenseItem();

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
                }).dispense(source, stack) : this.itemDispenserBehavior.dispense(source, stack);
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.spawn_egg, new BehaviorDefaultDispenseItem() {
            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                EnumFacing facingDirection = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                double d = source.getX() + facingDirection.getFrontOffsetX();
                double d2 = source.getYInt() + 0.2F;
                double d3 = source.getZ() + facingDirection.getFrontOffsetZ();
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
                EnumFacing facingDirection = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                double d = source.getX() + facingDirection.getFrontOffsetX();
                double d2 = source.getYInt() + 0.2F;
                double d3 = source.getZ() + facingDirection.getFrontOffsetZ();
                EntityFireworkRocket entityFireworkRocket = new EntityFireworkRocket(source.getWorld(), d, d2, d3, stack);
                source.getWorld().spawnEntityInWorld(entityFireworkRocket);
                stack.splitStack(1);
                return stack;
            }

            @Override
            public void playDispenseSound(IBlockSource source) {
                source.getWorld().playAuxSFX(1002, source.getXInt(), source.getYInt(), source.getZInt(), 0);
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fire_charge, new BehaviorDefaultDispenseItem() {
            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                EnumFacing facingDirection = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                IPosition iPositionFromBlockSource = BlockDispenser.getIPositionFromBlockSource(source);
                double d = iPositionFromBlockSource.getX() + facingDirection.getFrontOffsetX() * 0.3F;
                double d2 = iPositionFromBlockSource.getY() + facingDirection.getFrontOffsetX() * 0.3F;
                double d3 = iPositionFromBlockSource.getZ() + facingDirection.getFrontOffsetZ() * 0.3F;
                World world = source.getWorld();
                Random rand = world.rand;
                double d4 = rand.nextGaussian() * 0.05 + facingDirection.getFrontOffsetX();
                double d5 = rand.nextGaussian() * 0.05 + facingDirection.getFrontOffsetY();
                double d6 = rand.nextGaussian() * 0.05 + facingDirection.getFrontOffsetZ();
                world.spawnEntityInWorld(new EntitySmallFireball(world, d, d2, d3, d4, d5, d6));
                stack.splitStack(1);
                return stack;
            }

            @Override
            public void playDispenseSound(IBlockSource source) {
                source.getWorld().playAuxSFX(1009, source.getXInt(), source.getYInt(), source.getZInt(), 0);
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.boat, new BehaviorDefaultDispenseItem() {
            public BehaviorDefaultDispenseItem itemDispenserBehavior = new BehaviorDefaultDispenseItem();

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                EnumFacing facingDirection = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                World world = source.getWorld();
                double d = source.getX() + facingDirection.getFrontOffsetX() * 1.125F;
                double d2 = source.getY() + facingDirection.getFrontOffsetY() * 1.125F;
                double d3 = source.getZ() + facingDirection.getFrontOffsetZ() * 1.125F;
                int n = source.getXInt() + facingDirection.getFrontOffsetX();
                int n2 = source.getYInt() + facingDirection.getFrontOffsetY();
                int n3 = source.getZInt() + facingDirection.getFrontOffsetZ();
                Material material = world.getBlock(n, n2, n3).getMaterial();
                double d4;
                if (Material.water.equals(material)) {
                    d4 = 1.0;
                } else {
                    if (!Material.air.equals(material) || !Material.water.equals(world.getBlock(n, n2 - 1, n3).getMaterial())) {
                        return this.itemDispenserBehavior.dispense(source, stack);
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
                source.getWorld().playAuxSFX(1000, source.getXInt(), source.getYInt(), source.getZInt(), 0);
            }
        });
        BehaviorDefaultDispenseItem behaviorDefaultDispenseItem = new BehaviorDefaultDispenseItem() {
            public BehaviorDefaultDispenseItem itemDispenserBehavior = new BehaviorDefaultDispenseItem();

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                ItemBucket item = (ItemBucket)stack.getItem();
                int xInt = source.getXInt();
                int yInt = source.getYInt();
                int zInt = source.getZInt();
                EnumFacing facingDirection = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                if (item.tryPlaceContainedLiquid(source.getWorld(), xInt + facingDirection.getFrontOffsetX(), yInt + facingDirection.getFrontOffsetY(), zInt + facingDirection.getFrontOffsetZ())) {
                    stack.setItem(Items.bucket);
                    stack.stackSize = 1;
                    return stack;
                } else {
                    return this.itemDispenserBehavior.dispense(source, stack);
                }
            }
        };
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.lava_bucket, behaviorDefaultDispenseItem);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.water_bucket, behaviorDefaultDispenseItem);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.bucket, new BehaviorDefaultDispenseItem() {
            public BehaviorDefaultDispenseItem itemDispenserBehavior = new BehaviorDefaultDispenseItem();

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                EnumFacing facingDirection = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                World world = source.getWorld();
                int n = source.getXInt() + facingDirection.getFrontOffsetX();
                int n2 = source.getYInt() + facingDirection.getFrontOffsetY();
                int n3 = source.getZInt() + facingDirection.getFrontOffsetZ();
                Material material = world.getBlock(n, n2, n3).getMaterial();
                int blockMetadata = world.getBlockMetadata(n, n2, n3);
                Item item;
                if (Material.water.equals(material) && blockMetadata == 0) {
                    item = Items.water_bucket;
                } else {
                    if (!Material.lava.equals(material) || blockMetadata != 0) {
                        return super.dispenseStack(source, stack);
                    }

                    item = Items.lava_bucket;
                }

                world.setBlockToAir(n, n2, n3);
                if (--stack.stackSize == 0) {
                    stack.setItem(item);
                    stack.stackSize = 1;
                } else if (((TileEntityDispenser)source.getBlockTileEntity()).addToFirstFreeSlot(new ItemStack(item)) < 0) {
                    this.itemDispenserBehavior.dispense(source, new ItemStack(item));
                }

                return stack;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.flint_and_steel, new BehaviorDefaultDispenseItem() {
            public boolean flag = true;

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                EnumFacing facingDirection = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                World world = source.getWorld();
                int n = source.getXInt() + facingDirection.getFrontOffsetX();
                int n2 = source.getYInt() + facingDirection.getFrontOffsetY();
                int n3 = source.getZInt() + facingDirection.getFrontOffsetZ();
                if (world.isAirBlock(n, n2, n3)) {
                    world.setBlock(n, n2, n3, Blocks.fire);
                    if (stack.attemptDamageItem(1, world.rand)) {
                        stack.stackSize = 0;
                    }
                } else if (world.getBlock(n, n2, n3) == Blocks.tnt) {
                    Blocks.tnt.onBlockDestroyedByPlayer(world, n, n2, n3, 1);
                    world.setBlockToAir(n, n2, n3);
                } else {
                    this.flag = false;
                }

                return stack;
            }

            @Override
            public void playDispenseSound(IBlockSource source) {
                if (this.flag) {
                    source.getWorld().playAuxSFX(1000, source.getXInt(), source.getYInt(), source.getZInt(), 0);
                } else {
                    source.getWorld().playAuxSFX(1001, source.getXInt(), source.getYInt(), source.getZInt(), 0);
                }
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.dye, new BehaviorDefaultDispenseItem() {
            public boolean flag = true;

            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                if (stack.getMetadata() == 15) {
                    EnumFacing facingDirection = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                    World world = source.getWorld();
                    int n = source.getXInt() + facingDirection.getFrontOffsetX();
                    int n2 = source.getYInt() + facingDirection.getFrontOffsetY();
                    int n3 = source.getZInt() + facingDirection.getFrontOffsetZ();
                    if (ItemDye.checkItemStack(stack, world, n, n2, n3)) {
                        if (!world.isRemote) {
                            world.playAuxSFX(2005, n, n2, n3, 0);
                        }
                    } else {
                        this.flag = false;
                    }

                    return stack;
                } else {
                    return super.dispenseStack(source, stack);
                }
            }

            @Override
            public void playDispenseSound(IBlockSource source) {
                if (this.flag) {
                    source.getWorld().playAuxSFX(1000, source.getXInt(), source.getYInt(), source.getZInt(), 0);
                } else {
                    source.getWorld().playAuxSFX(1001, source.getXInt(), source.getYInt(), source.getZInt(), 0);
                }
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.tnt), new BehaviorDefaultDispenseItem() {
            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                EnumFacing facingDirection = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                World world = source.getWorld();
                int n = source.getXInt() + facingDirection.getFrontOffsetX();
                int n2 = source.getYInt() + facingDirection.getFrontOffsetY();
                int n3 = source.getZInt() + facingDirection.getFrontOffsetZ();
                EntityTNTPrimed entityTNTPrimed = new EntityTNTPrimed(world, n + 0.5F, n2 + 0.5F, n3 + 0.5F, null);
                world.spawnEntityInWorld(entityTNTPrimed);
                stack.stackSize--;
                return stack;
            }
        });
    }

    public static void register() {
        if (!alreadyRegistered) {
            alreadyRegistered = true;
            Block.registerBlocks();
            BlockFire.registerDefaultFlammables();
            Item.registerItems();
            StatList.setup();
            registerDispenserBehaviors();
        }
    }
}
