package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemBlock extends Item {
    public Block blockInstance;
    public IIcon iIcon;

    public ItemBlock(Block block) {
        this.blockInstance = block;
    }

    public ItemBlock setUnlocalizedName(String str) {
        super.setUnlocalizedName(str);
        return this;
    }

    @Override
    public int getSpriteNumber() {
        return this.blockInstance.getItemIconName() != null ? 1 : 0;
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        return this.iIcon != null ? this.iIcon : this.blockInstance.getBlockTextureFromSide(1);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        Block block = world.getBlock(n, n2, n3);
        if (block == Blocks.snow_layer && (world.getBlockMetadata(n, n2, n3) & 7) < 1) {
            n4 = 1;
        } else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush) {
            if (n4 == 0) {
                n2--;
            }

            if (n4 == 1) {
                n2++;
            }

            if (n4 == 2) {
                n3--;
            }

            if (n4 == 3) {
                n3++;
            }

            if (n4 == 4) {
                n--;
            }

            if (n4 == 5) {
                n++;
            }
        }

        if (itemStack.stackSize == 0) {
            return false;
        }

        if (!entityPlayer.canPlayerEdit(n, n2, n3, n4, itemStack)) {
            return false;
        }

        if (n2 == 255 && this.blockInstance.getMaterial().isSolid()) {
            return false;
        }

        if (world.canPlaceEntityOnSide(this.blockInstance, n, n2, n3, false, n4, entityPlayer, itemStack)) {
            int metadata = this.getMetadata(itemStack.getMetadata());
            int onBlockPlaced = this.blockInstance.onBlockPlaced(world, n, n2, n3, n4, f, f2, f3, metadata);
            if (world.setBlock(n, n2, n3, this.blockInstance, onBlockPlaced, 3)) {
                if (world.getBlock(n, n2, n3) == this.blockInstance) {
                    this.blockInstance.onBlockPlacedBy(world, n, n2, n3, entityPlayer, itemStack);
                    this.blockInstance.onPostBlockPlaced(world, n, n2, n3, onBlockPlaced);
                }

                world.playSoundEffect(
                    n + 0.5F,
                    n2 + 0.5F,
                    n3 + 0.5F,
                    this.blockInstance.stepSound.getPlaceSound(),
                    (this.blockInstance.stepSound.getVolume() + 1.0F) / 2.0F,
                    this.blockInstance.stepSound.getFrequency() * 0.8F
                );
                itemStack.stackSize--;
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean checkWorld(World world, int n, int n2, int n3, int n4, EntityPlayer entityPlayer, ItemStack itemStack) {
        Block block = world.getBlock(n, n2, n3);
        if (block == Blocks.snow_layer) {
            n4 = 1;
        } else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush) {
            if (n4 == 0) {
                n2--;
            }

            if (n4 == 1) {
                n2++;
            }

            if (n4 == 2) {
                n3--;
            }

            if (n4 == 3) {
                n3++;
            }

            if (n4 == 4) {
                n--;
            }

            if (n4 == 5) {
                n++;
            }
        }

        return world.canPlaceEntityOnSide(this.blockInstance, n, n2, n3, false, n4, null, itemStack);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.blockInstance.getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName() {
        return this.blockInstance.getUnlocalizedName();
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return this.blockInstance.getCreativeTabToDisplayOn();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        this.blockInstance.getSubBlocks(item, creativeTabs, list);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        String itemIconName = this.blockInstance.getItemIconName();
        if (itemIconName != null) {
            this.iIcon = register.registerIcon(itemIconName);
        }
    }
}
