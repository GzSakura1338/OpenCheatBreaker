package com.cheatbreaker.client.emote.bobj;

public class BobjCompiledData {
   public float[] vertices;
   public float[] texCoords;
   public float[] normals;
   public float[] boneWeights;
   public int[] boneIndices;
   public int[] indices;
   public BobjMesh mesh;

   public BobjCompiledData(float[] floats, float[] floats2, float[] floats3, float[] floats4, int[] ints, int[] ints2, BobjMesh bobjMesh) {
      this.vertices = floats;
      this.texCoords = floats2;
      this.normals = floats3;
      this.boneWeights = floats4;
      this.boneIndices = ints;
      this.indices = ints2;
      this.mesh = bobjMesh;
   }
}
