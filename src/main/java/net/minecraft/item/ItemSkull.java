package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.block.BlockSkull;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemSkull extends Item {
    public static String[] skullTypes = new String[]{"skeleton", "wither", "zombie", "char", "creeper"};
    public static String[] stringArray = new String[]{"skeleton", "wither", "zombie", "steve", "creeper"};
    public IIcon[] iIconArray;

    public ItemSkull() {
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setMaxDurability(0);
        this.setHasSubtypes(true);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (n4 == 0) {
            return false;
        }

        if (!world.getBlock(n, n2, n3).getMaterial().isSolid()) {
            return false;
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

        if (!world.isRemote) {
            world.setBlock(n, n2, n3, Blocks.skull, n4, 2);
            int n5 = 0;
            if (n4 == 1) {
                n5 = MathHelper.floor_double(entityPlayer.rotationYaw * 16.0F / 360.0F + 0.5) & 15;
            }

            TileEntity tileEntity = world.getTileEntity(n, n2, n3);
            if (tileEntity != null && tileEntity instanceof TileEntitySkull) {
                if (itemStack.getMetadata() == 3) {
                    GameProfile gameProfile = null;
                    if (itemStack.hasTagCompound()) {
                        NBTTagCompound tagCompound = itemStack.getTagCompound();
                        if (tagCompound.hasKey("SkullOwner", 10)) {
                            gameProfile = NBTUtil.readGameProfileFromNBT(tagCompound.getCompoundTag("SkullOwner"));
                        } else if (tagCompound.hasKey("SkullOwner", 8) && tagCompound.getString("SkullOwner").length() > 0) {
                            gameProfile = new GameProfile(null, tagCompound.getString("SkullOwner"));
                        }
                    }

                    ((TileEntitySkull)tileEntity).setOwnerAndType(gameProfile);
                } else {
                    ((TileEntitySkull)tileEntity).setSkullType(itemStack.getMetadata());
                }

                ((TileEntitySkull)tileEntity).setSkullRotation(n5);
                ((BlockSkull)Blocks.skull).makeWither(world, n, n2, n3, (TileEntitySkull)tileEntity);
            }

            itemStack.stackSize--;
        }

        return true;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        for (int i = 0; i < skullTypes.length; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        if (n < 0 || n >= skullTypes.length) {
            n = 0;
        }

        return this.iIconArray[n];
    }

    @Override
    public int getMetadata(int n) {
        return n;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int metadata = stack.getMetadata();
        if (metadata < 0 || metadata >= skullTypes.length) {
            metadata = 0;
        }

        return super.getUnlocalizedName() + "." + skullTypes[metadata];
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemStack) {
        if (itemStack.getMetadata() == 3 && itemStack.hasTagCompound()) {
            if (itemStack.getTagCompound().hasKey("SkullOwner", 10)) {
                return StatCollector.translateToLocalFormatted(
                    "item.skull.player.name", NBTUtil.readGameProfileFromNBT(itemStack.getTagCompound().getCompoundTag("SkullOwner")).getName()
                );
            }

            if (itemStack.getTagCompound().hasKey("SkullOwner", 8)) {
                return StatCollector.translateToLocalFormatted("item.skull.player.name", itemStack.getTagCompound().getString("SkullOwner"));
            }
        }

        return super.getItemStackDisplayName(itemStack);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.iIconArray = new IIcon[stringArray.length];

        for (int i = 0; i < stringArray.length; i++) {
            this.iIconArray[i] = register.registerIcon(this.getIconString() + "_" + stringArray[i]);
        }
    }
}
