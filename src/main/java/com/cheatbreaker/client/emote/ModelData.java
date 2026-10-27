package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.emote.bobj.BobjAction;
import com.cheatbreaker.client.emote.bobj.BobjArmature;
import com.cheatbreaker.client.emote.bobj.BobjMesh;
import com.cheatbreaker.client.emote.bobj.BobjVertex;
import java.util.List;
import java.util.Map;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class ModelData {
   public List<BobjVertex> vertices;
   public List<Vector2f> texCoords;
   public List<Vector3f> normals;
   public List<BobjMesh> meshes;
   public Map<String, BobjAction> actions;
   public Map<String, BobjArmature> armatures;

   public void clearGeometry() {
      this.vertices.clear();
      this.texCoords.clear();
      this.normals.clear();
      this.meshes.clear();
   }

   public ModelData(
      List<BobjVertex> list, List<Vector2f> list2, List<Vector3f> list3, List<BobjMesh> list4, Map<String, BobjAction> map, Map<String, BobjArmature> map2
   ) {
      this.vertices = list;
      this.texCoords = list2;
      this.normals = list3;
      this.meshes = list4;
      this.actions = map;
      this.armatures = map2;

      for (BobjMesh bobjMesh : list4) {
         bobjMesh.armature = (BobjArmature)map2.get(bobjMesh.armatureName);
      }
   }

   public boolean hasMeshes() {
      return !this.meshes.isEmpty();
   }
}
