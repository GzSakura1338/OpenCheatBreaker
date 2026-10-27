package net.minecraftforge.client.model;

import javax.vecmath.Matrix4f;
import net.minecraft.util.EnumFacing;

public interface ITransformation {
   EnumFacing rotate(EnumFacing enumFacing);

   int rotate(EnumFacing enumFacing, int n);

   Matrix4f createMatrix4f();
}
