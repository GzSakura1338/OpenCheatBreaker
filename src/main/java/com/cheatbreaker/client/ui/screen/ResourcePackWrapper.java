package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.util.SneakyThrow;
import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class ResourcePackWrapper implements IResourcePack {
   public String text;
   public CustomDefaultResourcePack customDefaultResourcePack;
   public static Set set = null;
   public FileResourcePack fileResourcePack;
   public FolderResourcePack folderResourcePack;

   @Override
   public BufferedImage getPackImage() {
      return null;
   }

   @Override
   public String getPackName() {
      return this.text + "ResourcePack";
   }

   @Override
   public IMetadataSection getPackMetadata(IMetadataSerializer metadataSerializer, String str) {
      return null;
   }

   public ResourcePackWrapper(String str) {
      this.text = str;
      set = ImmutableSet.of(str);
      File fileAssets = Minecraft.getMinecraft().getFileAssets();
      this.customDefaultResourcePack = new CustomDefaultResourcePack(new ResourceIndex(fileAssets, str).getResourceMap(), str);
      this.fileResourcePack = new FileResourcePack(Minecraft.getMinecraft().mcDataDir);
      this.folderResourcePack = new FolderResourcePack(Minecraft.getMinecraft().mcDataDir);
   }

   @Override
   public Set getResourceDomains() {
      return set;
   }

   @Override
   public boolean resourceExists(ResourceLocation resourceLocation) {
      return this.customDefaultResourcePack.resourceExists(resourceLocation) || this.fileResourcePack.resourceExists(resourceLocation) || this.folderResourcePack.resourceExists(resourceLocation);
   }

   @Override
   public InputStream getInputStream(ResourceLocation resourceLocation) {
      try {
         return this.customDefaultResourcePack.getInputStream(resourceLocation);
      } catch (IOException e) {
         try {
            return this.fileResourcePack.getInputStream(resourceLocation);
         } catch (IOException e2) {
            try {
               return this.folderResourcePack.getInputStream(resourceLocation);
            } catch (IOException e3) {
               throw SneakyThrow.sneakyThrow(e3);
            }
         }
      }
   }
}
