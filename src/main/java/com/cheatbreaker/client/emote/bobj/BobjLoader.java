package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.animation.Keyframe;
import com.cheatbreaker.client.emote.Bone;
import com.cheatbreaker.client.emote.LookAtConstraint;
import com.cheatbreaker.client.emote.ModelData;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import org.apache.commons.lang3.ArrayUtils;

public class BobjLoader {
   public static ModelData load(InputStream inputStream) {
      List readLines2 = readLines(inputStream);
      ArrayList list = new ArrayList();
      ArrayList list2 = new ArrayList();
      ArrayList list3 = new ArrayList();
      ArrayList list4 = new ArrayList();
      HashMap map = new HashMap();
      HashMap map2 = new HashMap();
      BobjMesh bobjMesh = null;
      BobjAction bobjAction = null;
      BobjGroup bobjGroup = null;
      BobjChannel bobjChannel = null;
      BobjArmature bobjArmature = null;
      Bone bone = null;
      BobjVertex bobjVertex = null;
      int n = 0;
      Iterator iterator = readLines2.iterator();

      while (iterator.hasNext()) {
         String[] split = ((String)iterator.next()).split("\\s");
         String str = split[0];
         if (str.equals("o")) {
            list4.add(bobjMesh = new BobjMesh(split[1]));
            bobjArmature = null;
            bobjVertex = null;
         } else if (str.equals("o_arm")) {
            bobjMesh.armatureName = split[1];
         } else if (str.equals("v")) {
            if (bobjVertex != null) {
               bobjVertex.normalizeWeights();
            }

            list.add(bobjVertex = new BobjVertex(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
         } else if (str.equals("vw")) {
            float parseFloat = Float.parseFloat(split[2]);
            if (parseFloat != 0.0F) {
               bobjVertex.weights.add(new BobjWeight(split[1], parseFloat));
            }
         } else if (str.equals("vt")) {
            list2.add(new Vector2f(Float.parseFloat(split[1]), Float.parseFloat(split[2])));
         } else if (str.equals("vn")) {
            list3.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
         } else if (str.equals("f")) {
            bobjMesh.faces.add(new BobjFace(split[1], split[2], split[3]));
         } else if (str.equals("arm_name")) {
            n = 0;
            bone = null;
            bobjArmature = new BobjArmature(split[1]);
            map2.put(bobjArmature.name, bobjArmature);
         } else if (str.equals("arm_action")) {
            bobjArmature.defaultActionName = split[1];
         } else if (str.equals("arm_bone")) {
            Vector3f vector3f = new Vector3f(Float.parseFloat(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
            Matrix4f matrix4f = new Matrix4f();
            float[] floats = new float[16];

            for (int i = 6; i < 22; i++) {
               floats[i - 6] = Float.parseFloat(split[i]);
            }

            matrix4f.set(floats);
            bone = new Bone(n++, split[1], split[2], vector3f, matrix4f);
            bobjArmature.addBone(bone);
         } else if (str.equals("arm_ik") && split.length >= 2) {
            Bone bone2 = bobjArmature.bonesByName.get(split[1]);
            if (bone == null) {
               System.out.println("Found IK modifier in BOBJ, but bone " + split[1] + " doesn't exist...");
            } else {
               bone.addConstraint(
                  new LookAtConstraint(bone2, split.length >= 3 ? Integer.parseInt(split[2]) : 1, split.length >= 4 && split[3].equals("true"))
               );
            }
         } else if (str.equals("an")) {
            map.put(split[1], bobjAction = new BobjAction(split[1]));
         } else if (str.equals("ao")) {
            bobjAction.groups.put(split[1], bobjGroup = new BobjGroup(split[1]));
         } else if (str.equals("ag")) {
            bobjGroup.channels.add(bobjChannel = new BobjChannel(split[1], Integer.parseInt(split[2])));
         } else if (str.equals("kf")) {
            bobjChannel.keyframes.add(Keyframe.fromTokens(split));
         }
      }

      if (bobjVertex != null) {
         bobjVertex.normalizeWeights();
      }

      return new ModelData(list, list2, list3, list4, map, map2);
   }

   public static BobjCompiledData compileStatic(ModelData modelData) {
      ArrayList list = new ArrayList();
      ArrayList list2 = new ArrayList();

      for (BobjMesh bobjMesh : modelData.meshes) {
         list2.addAll(bobjMesh.faces);
      }

      float[] floats = new float[list2.size() * 3 * 4];
      float[] floats2 = new float[list2.size() * 3 * 2];
      float[] floats3 = new float[list2.size() * 3 * 3];
      int n = 0;

      for (BobjFace bobjFace : (Iterable<BobjFace>)list2) {
         BobjFacePoint[] points = bobjFace.points;
         int length = points.length;

         for (int i = 0; i < length; i++) {
            writeVertex(n, points[i], null, modelData, list, floats, floats2, floats3, null, null);
            n++;
         }
      }

      return new BobjCompiledData(floats, floats2, floats3, null, null, (int[])ArrayUtils.toPrimitive(list.toArray(new Integer[0])), null);
   }

   public static Map<String, BobjCompiledData> compileMeshes(ModelData modelData) {
      HashMap map = new HashMap();

      for (BobjMesh bobjMesh : modelData.meshes) {
         ArrayList list = new ArrayList();
         List faces = bobjMesh.faces;
         int[] ints = new int[faces.size() * 3 * 4];
         float[] floats = new float[faces.size() * 3 * 4];
         float[] floats2 = new float[faces.size() * 3 * 4];
         float[] floats3 = new float[faces.size() * 3 * 2];
         float[] floats4 = new float[faces.size() * 3 * 3];
         Arrays.fill(ints, -1);
         Arrays.fill(floats, -1.0F);
         int n = 0;

         for (BobjFace bobjFace : (Iterable<BobjFace>)faces) {
            BobjFacePoint[] points = bobjFace.points;
            int length = points.length;

            for (int i = 0; i < length; i++) {
               writeVertex(n, points[i], bobjMesh, modelData, list, floats2, floats3, floats4, floats, ints);
               n++;
            }
         }

         map.put(bobjMesh.name, new BobjCompiledData(floats2, floats3, floats4, floats, ints, (int[])ArrayUtils.toPrimitive(list.toArray(new Integer[0])), bobjMesh));
      }

      return map;
   }

   public static void handleModelData(ModelData modelData, ModelData modelData2) {
      int size = modelData.vertices.size();
      int size2 = modelData.normals.size();
      int size3 = modelData.texCoords.size();
      modelData.vertices.addAll(modelData2.vertices);
      modelData.normals.addAll(modelData2.normals);
      modelData.texCoords.addAll(modelData2.texCoords);
      Iterator iterator = modelData2.meshes.iterator();

      while (iterator.hasNext()) {
         BobjMesh copyWithOffsets = ((BobjMesh)iterator.next()).copyWithOffsets(size, size2, size3);
         copyWithOffsets.armature = modelData.armatures.get(copyWithOffsets.armatureName);
         modelData.meshes.add(copyWithOffsets);
      }
   }

   public static void writeVertex(
      int n, BobjFacePoint bobjFacePoint, BobjMesh bobjMesh, ModelData modelData, List<Integer> list, float[] floats, float[] floats2, float[] floats3, float[] floats4, int[] ints
   ) {
      list.add(n);
      if (bobjFacePoint.vertexIndex >= 0) {
         BobjVertex bobjVertex = modelData.vertices.get(bobjFacePoint.vertexIndex);
         floats[n * 4] = bobjVertex.x;
         floats[n * 4 + 1] = bobjVertex.y;
         floats[n * 4 + 2] = bobjVertex.z;
         floats[n * 4 + 3] = 1.0F;
         if (bobjMesh != null) {
            for (int i = 0; i < Math.min(bobjVertex.weights.size(), 4); i++) {
               BobjWeight bobjWeight = bobjVertex.weights.get(i);
               Bone bone = bobjMesh.armature.bonesByName.get(bobjWeight.boneName);
               floats4[n * 4 + i] = bone == null ? 0.0F : bobjWeight.weight;
               ints[n * 4 + i] = bone == null ? -1 : bone.index;
            }
         }
      }

      if (bobjFacePoint.texCoordIndex >= 0) {
         Vector2f vector2f = modelData.texCoords.get(bobjFacePoint.texCoordIndex);
         floats2[n * 2] = vector2f.x;
         floats2[n * 2 + 1] = 1.0F - vector2f.y;
      }

      if (bobjFacePoint.normalIndex >= 0) {
         Vector3f vector3f = modelData.normals.get(bobjFacePoint.normalIndex);
         floats3[n * 3] = vector3f.x;
         floats3[n * 3 + 1] = vector3f.y;
         floats3[n * 3 + 2] = vector3f.z;
      }
   }

   public static List<String> readLines(InputStream inputStream) {
      ArrayList list = new ArrayList();

      try {
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

         String str;
         while ((str = bufferedReader.readLine()) != null) {
            list.add(str);
         }

         bufferedReader.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

      return list;
   }
}
