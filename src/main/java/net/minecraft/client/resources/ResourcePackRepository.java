package net.minecraft.client.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipFile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackRepository {
   public static Logger logger = LogManager.getLogger();
   public static FileFilter resourcePackFilter = new FileFilter() {
      @Override
      public boolean accept(File file) {
         boolean flag = file.isFile() && file.getName().endsWith(".zip");
         boolean flag2 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
         return flag || flag2;
      }
   };
   public ReentrantLock lock = new ReentrantLock();
   public List<ResourcePackRepository.Entry> repositoryEntriesAll = Lists.newArrayList();
   public List<ResourcePackRepository.Entry> repositoryEntries = Lists.newArrayList();
   public File dirResourcepacks;
   public IResourcePack resourcePackInstance;
   public File dirServerResourcepacks;
   public ListenableFuture<Object> downloadingPacks;
   public IResourcePack rprDefaultResourcePack;
   public IMetadataSerializer rprMetadataSerializer;
   public long longValue;
   public String text;
   public String text2;
   public static ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(MoreExecutors.newDirectExecutorService());

   public ResourcePackRepository(
      File dirResourcepacksIn,
      File dirServerResourcepacksIn,
      IResourcePack rprDefaultResourcePackIn,
      IMetadataSerializer rprMetadataSerializerIn,
      GameSettings settings
   ) {
      this.dirResourcepacks = dirResourcepacksIn;
      this.dirServerResourcepacks = dirServerResourcepacksIn;
      this.rprDefaultResourcePack = rprDefaultResourcePackIn;
      this.rprMetadataSerializer = rprMetadataSerializerIn;
      this.fixDirResourcepacks();
      this.updateRepositoryEntriesAll();
      Iterator iterator = settings.resourcePacks.iterator();

      while (iterator.hasNext()) {
         String str = (String)iterator.next();

         for (ResourcePackRepository.Entry entry : this.repositoryEntriesAll) {
            if (entry.getResourcePackName().equals(str)) {
               if (entry.getFormat() == 1 || settings.incompatibleResourcePacks.contains(entry.getResourcePackName())) {
                  this.repositoryEntries.add(entry);
                  break;
               }

               iterator.remove();
               logger.warn("Removed selected resource pack {} because it's no longer compatible", new Object[]{entry.getResourcePackName()});
            }
         }
      }
   }

   public void fixDirResourcepacks() {
      if (this.dirResourcepacks.exists()) {
         if (!this.dirResourcepacks.isDirectory() && (!this.dirResourcepacks.delete() || !this.dirResourcepacks.mkdirs())) {
            logger.warn("Unable to recreate resourcepack folder, it exists but is not a directory: " + this.dirResourcepacks);
         }
      } else if (!this.dirResourcepacks.mkdirs()) {
         logger.warn("Unable to create resourcepack folder: " + this.dirResourcepacks);
      }
   }

   public List<File> getResourcePackFiles() {
      return this.dirResourcepacks.isDirectory() ? Arrays.asList(this.dirResourcepacks.listFiles(resourcePackFilter)) : Collections.emptyList();
   }

   public void updateRepositoryEntriesAll() {
      ArrayList newArrayList = Lists.newArrayList();

      for (File file : this.getResourcePackFiles()) {
         ResourcePackRepository.Entry entry = new ResourcePackRepository.Entry(file);
         if (!this.repositoryEntriesAll.contains(entry)) {
            try {
               entry.updateResourcePack();
               newArrayList.add(entry);
            } catch (Exception e) {
               newArrayList.remove(entry);
            }
         } else {
            int indexOf = this.repositoryEntriesAll.indexOf(entry);
            if (indexOf > -1 && indexOf < this.repositoryEntriesAll.size()) {
               newArrayList.add(this.repositoryEntriesAll.get(indexOf));
            }
         }
      }

      this.repositoryEntriesAll.removeAll(newArrayList);

      for (ResourcePackRepository.Entry entry : this.repositoryEntriesAll) {
         entry.closeResourcePack();
      }

      this.repositoryEntriesAll = newArrayList;
   }

   public List<ResourcePackRepository.Entry> getRepositoryEntriesAll() {
      return ImmutableList.copyOf(this.repositoryEntriesAll);
   }

   public List<ResourcePackRepository.Entry> getRepositoryEntries() {
      return ImmutableList.copyOf(this.repositoryEntries);
   }

   public void setRepositories(List<ResourcePackRepository.Entry> repositories) {
      this.repositoryEntries.clear();
      this.repositoryEntries.addAll(repositories);
   }

   public File getDirResourcepacks() {
      return this.dirResourcepacks;
   }

   public ListenableFuture<Object> downloadResourcePack(String url, String hash) {
      String str;
      if (hash.matches("^[a-f0-9]{40}$")) {
         str = hash;
      } else {
         str = "legacy";
      }

      File file = new File(this.dirServerResourcepacks, str);
      this.lock.lock();

      try {
         if (url.equals(this.text) && hash.equals(this.text2) && this.downloadingPacks != null && !this.downloadingPacks.isCancelled()) {
            return this.downloadingPacks;
         }

         this.clearResourcePack();
         this.text = url;
         this.text2 = hash;
         long longValue = this.longValue;
         if (file.exists() && hash.length() == 40) {
            try {
               String str2 = Hashing.sha1().hashBytes(Files.toByteArray(file)).toString();
               if (str2.equals(hash)) {
                  this.downloadingPacks = this.setResourcePackInstance(file);
                  return this.downloadingPacks;
               }

               logger.warn("File " + file + " had wrong hash (expected " + hash + ", found " + str2 + "). Deleting it.");
               FileUtils.deleteQuietly(file);
            } catch (IOException e) {
               logger.warn("File " + file + " couldn't be hashed. Deleting it.", e);
               FileUtils.deleteQuietly(file);
            }
         }

         this.deleteOldServerResourcesPacks();
         final GuiScreenWorking guiScreenWorking = new GuiScreenWorking();
         Map sessionInfo = Minecraft.getSessionInfo();
         final Minecraft minecraft = Minecraft.getMinecraft();
         Futures.getUnchecked(minecraft.addScheduledTask(new Runnable() {
            @Override
            public void run() {
               minecraft.displayGuiScreen(guiScreenWorking);
            }
         }));
         ListenableFuture downloadResourcePack = HttpUtil.downloadResourcePack(file, url, sessionInfo, 52428800, guiScreenWorking, minecraft.getProxy());
         this.downloadingPacks = Futures.transformAsync(downloadResourcePack, obj -> {
            this.lock.lock();

            try {
               return longValue != this.longValue ? Futures.immediateCancelledFuture() : this.setResourcePackInstance(file);
            } finally {
               this.lock.unlock();
            }
         }, listeningExecutorService);
         return this.downloadingPacks;
      } finally {
         this.lock.unlock();
      }
   }

   public void deleteOldServerResourcesPacks() {
      if (this.dirServerResourcepacks.exists()) {
         ArrayList newArrayList = Lists.newArrayList(FileUtils.listFiles(this.dirServerResourcepacks, TrueFileFilter.TRUE, (IOFileFilter)null));
         Collections.sort(newArrayList, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
         int n = 0;

         for (File file : (Iterable<File>)newArrayList) {
            if (n++ >= 10) {
               logger.info("Deleting old server resource pack " + file.getName());
               FileUtils.deleteQuietly(file);
            }
         }
      }
   }

   public ListenableFuture<Object> setResourcePackInstance(File resourceFile) {
      try (ZipFile zipFile = new ZipFile(resourceFile)) {
         if (zipFile.getEntry("pack.mcmeta") == null) {
            return Futures.immediateFailedFuture(new IOException("Resource pack is missing pack.mcmeta: " + resourceFile));
         }
      } catch (IOException e) {
         return Futures.immediateFailedFuture(e);
      }

      this.resourcePackInstance = new FileResourcePack(resourceFile);
      return Minecraft.getMinecraft().scheduleResourcesRefresh();
   }

   public IResourcePack getResourcePackInstance() {
      return this.resourcePackInstance;
   }

   public void clearResourcePack() {
      this.lock.lock();

      try {
         if (this.downloadingPacks != null) {
            this.downloadingPacks.cancel(true);
         }

         this.downloadingPacks = null;
         this.text = null;
         this.text2 = null;
         this.longValue++;
         if (this.resourcePackInstance != null) {
            this.resourcePackInstance = null;
            Minecraft.getMinecraft().scheduleResourcesRefresh();
         }
      } finally {
         this.lock.unlock();
      }
   }

   public class Entry {
      public File resourcePackFile;
      public IResourcePack reResourcePack;
      public PackMetadataSection rePackMetadataSection;
      public BufferedImage texturePackIcon;
      public ResourceLocation locationTexturePackIcon;

      public Entry(File resourcePackFileIn) {
         this.resourcePackFile = resourcePackFileIn;
      }

      public void updateResourcePack() throws IOException {
         this.reResourcePack = this.resourcePackFile.isDirectory()
            ? new FolderResourcePack(this.resourcePackFile)
            : new FileResourcePack(this.resourcePackFile);
         this.rePackMetadataSection = this.reResourcePack.getPackMetadata(ResourcePackRepository.this.rprMetadataSerializer, "pack");

         try {
            this.texturePackIcon = this.reResourcePack.getPackImage();
         } catch (IOException e) {
         }

         if (this.texturePackIcon == null) {
            this.texturePackIcon = ResourcePackRepository.this.rprDefaultResourcePack.getPackImage();
         }

         this.closeResourcePack();
      }

      public void bindTexturePackIcon(TextureManager textureManagerIn) {
         if (this.locationTexturePackIcon == null) {
            this.locationTexturePackIcon = textureManagerIn.getDynamicTextureLocation("texturepackicon", new DynamicTexture(this.texturePackIcon));
         }

         textureManagerIn.bindTexture(this.locationTexturePackIcon);
      }

      public void closeResourcePack() {
         if (this.reResourcePack instanceof Closeable) {
            IOUtils.closeQuietly((Closeable)this.reResourcePack);
         }
      }

      public IResourcePack getResourcePack() {
         return this.reResourcePack;
      }

      public String getResourcePackName() {
         return this.reResourcePack.getPackName();
      }

      public String getTexturePackDescription() {
         return this.rePackMetadataSection == null
            ? EnumChatFormatting.RED + "Invalid pack.mcmeta (or missing 'pack' section)"
            : this.rePackMetadataSection.getPackDescription().getFormattedText();
      }

      public int getFormat() {
         return this.rePackMetadataSection.getPackFormat();
      }

      @Override
      public boolean equals(Object obj) {
         return this == obj ? true : (obj instanceof ResourcePackRepository.Entry ? this.toString().equals(obj.toString()) : false);
      }

      @Override
      public int hashCode() {
         return this.toString().hashCode();
      }

      @Override
      public String toString() {
         return String.format(
            "%s:%s:%d", this.resourcePackFile.getName(), this.resourcePackFile.isDirectory() ? "folder" : "zip", this.resourcePackFile.lastModified()
         );
      }
   }
}
