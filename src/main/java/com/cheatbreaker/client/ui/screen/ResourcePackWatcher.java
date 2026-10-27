package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;

public class ResourcePackWatcher {
   public WatchService watchService;

   public ResourcePackWatcher(Path path) {
      try {
         this.watchService = FileSystems.getDefault().newWatchService();
         Files.walkFileTree(
            path,
            new SimpleFileVisitor<Path>() {
               public FileVisitResult preVisitDirectory(Path path2, BasicFileAttributes basicFileAttributes) throws IOException {
                  File toFile = path2.toFile();
                  if (ResourcePackScanner.checkFile(toFile)) {
                     path2.register(
                        ResourcePackWatcher.this.watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE
                     );
                     return FileVisitResult.CONTINUE;
                  } else {
                     return ResourcePackScanner.checkFile2(toFile) ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
                  }
               }
            }
         );
      } catch (IOException e) {
         CheatBreaker.getInstance().getLogger().error("Failed to create watch service", e);
      }
   }

   public boolean check() {
      boolean flag = false;
      WatchKey watchKey;
      if (this.watchService != null && (watchKey = this.watchService.poll()) != null) {
         for (WatchEvent watchEvent : watchKey.pollEvents()) {
            File toFile = ((Path)watchKey.watchable()).resolve(watchEvent.context().toString()).toFile();
            if (ResourcePackScanner.checkFile2(toFile) || ResourcePackScanner.checkFile(toFile)) {
               flag = true;
               break;
            }
         }

         watchKey.reset();
      }

      return flag;
   }
}
