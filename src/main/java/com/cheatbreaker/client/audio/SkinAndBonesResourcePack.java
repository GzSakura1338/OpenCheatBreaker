package com.cheatbreaker.client.audio;

import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Set;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class SkinAndBonesResourcePack implements IResourcePack {
   public static Set<String> stringSet = ImmutableSet.of("s&b");
   public File file;
   public InputStream inputStream;

   @Override
   public Set<String> getResourceDomains() {
      return stringSet;
   }

   @Override
   public String getPackName() {
      return "Skin&Bones animation pack";
   }

   @Override
   public IMetadataSection getPackMetadata(IMetadataSerializer metadataSerializer, String str) {
      return null;
   }

   @Override
   public boolean resourceExists(ResourceLocation resourceLocation) {
      if (new File(this.file, resourceLocation.getResourcePath()).exists()) {
         return true;
      }

      this.inputStream = SkinAndBonesResourcePack.class.getResourceAsStream("/assets/skin_n_bones/models/" + resourceLocation.getResourcePath());
      return this.inputStream != null;
   }

   @Override
   public InputStream getInputStream(ResourceLocation resourceLocation) throws java.io.FileNotFoundException {
      if (this.inputStream != null) {
         InputStream inputStream = this.inputStream;
         this.inputStream = null;
         return inputStream;
      } else {
         return new FileInputStream(new File(this.file, resourceLocation.getResourcePath()));
      }
   }

   public SkinAndBonesResourcePack(File file) {
      this.file = file;
      this.file.mkdirs();
   }

   @Override
   public BufferedImage getPackImage() {
      return null;
   }
}
