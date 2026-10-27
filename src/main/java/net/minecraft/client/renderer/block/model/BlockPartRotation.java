package net.minecraft.client.renderer.block.model;

import net.minecraft.util.EnumFacing.Axis;
import org.lwjgl.util.vector.Vector3f;

public class BlockPartRotation {
   public Vector3f origin;
   public Axis axis;
   public float angle;
   public boolean rescale;

   public BlockPartRotation(Vector3f originIn, Axis axisIn, float angleIn, boolean rescaleIn) {
      this.origin = originIn;
      this.axis = axisIn;
      this.angle = angleIn;
      this.rescale = rescaleIn;
   }
}
