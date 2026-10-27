package net.minecraft.client.resources.data;

public class FontMetadataSection implements IMetadataSection {
   public float[] charWidths;
   public float[] charLefts;
   public float[] charSpacings;

   public FontMetadataSection(float[] floats, float[] floats2, float[] floats3) {
      this.charWidths = floats;
      this.charLefts = floats2;
      this.charSpacings = floats3;
   }
}
