package com.cheatbreaker.client.util.mumble;

import com.cheatbreaker.client.util.Logging;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.win32.StdCallLibrary;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MumbleLink {
   public int fileDescriptor = -1;
   public Pointer sharedMemory;
   public static final int CONSTANT_2 = 2;
   public MumbleMemory layout;
   public MumbleLink.Kernel32 kernel32;
   public static final int FILE_MAP_ALL_ACCESS = 983071;
   public String lastDescription;
   public static final long RELINK_INTERVAL_NANOS = 30000000000L;
   public Pointer fileMappingHandle;
   public String lastName;
   public String lastIdentity;
   public static final int CONSTANT_2_2 = 2;
   public static final int CONSTANT_1 = 1;
   public static final int CONSTANT_2_3 = 2;
   public long mappingExpiresAt;
   public MumbleLink.PosixLibC posixLibC;
   public String lastContext;
   public static final int CONSTANT_1_2 = 1;

   public boolean update(
      float[] floats, float[] floats2, float[] floats3, float[] floats4, float[] floats5, float[] floats6, String str, String str2, String str3, String str4
   ) {
      if (!this.ensureMapped()) {
         return false;
      }

      boolean flag = this.sharedMemory.getInt(0L) != 2;
      if (flag) {
         this.lastName = null;
         this.lastIdentity = null;
         this.lastContext = null;
         this.lastDescription = null;
      }

      if (!Objects.equals(str, this.lastName)) {
         this.writeWideString(44L, 256, str);
         this.lastName = str;
      }

      if (!Objects.equals(str4, this.lastDescription)) {
         this.writeWideString(this.layout.descriptionOffset, 2048, str4);
         this.lastDescription = str4;
      }

      if (!Objects.equals(str2, this.lastIdentity)) {
         this.writeWideString(this.layout.identityOffset, 256, str2);
         this.lastIdentity = str2;
      }

      if (!Objects.equals(str3, this.lastContext)) {
         this.writeContext(str3);
         this.lastContext = str3;
      }

      this.writeVector(8L, floats);
      this.writeVector(20L, floats2);
      this.writeVector(32L, floats3);
      this.writeVector(this.layout.cameraPositionOffset, floats4);
      this.writeVector(this.layout.cameraFrontOffset, floats5);
      this.writeVector(this.layout.cameraTopOffset, floats6);
      if (flag) {
         this.sharedMemory.setInt(0L, 2);
      }

      this.sharedMemory.setInt(4L, this.sharedMemory.getInt(4L) + 1);
      return true;
   }

   public void writeWideString(long l, int n, String str) {
      String str2 = str == null ? "" : str.substring(0, Math.min(str.length(), n - 1));
      this.sharedMemory.setWideString(l, str2);
   }

   public MumbleLink(MumbleMemory mumbleMemory, MumbleLink.Kernel32 kernel32, MumbleLink.PosixLibC posixLibC) {
      this.layout = mumbleMemory;
      this.kernel32 = kernel32;
      this.posixLibC = posixLibC;
   }

   public void writeContext(String str) {
      byte[] bytes = str == null ? new byte[0] : str.getBytes(StandardCharsets.UTF_8);
      int min = Math.min(bytes.length, 256);
      if (min > 0) {
         this.sharedMemory.write(this.layout.contextOffset, bytes, 0, min);
      }

      this.sharedMemory.setInt(this.layout.contextLengthOffset, min);
   }

   public void unlink() {
      if (this.sharedMemory != null) {
         this.sharedMemory.setMemory(0L, this.layout.size, (byte)0);
      }

      this.closeMapping();
   }

   public void writeVector(long l, float[] floats) {
      this.sharedMemory.setFloat(l, floats[0]);
      this.sharedMemory.setFloat(l + 4L, floats[1]);
      this.sharedMemory.setFloat(l + 8L, floats[2]);
   }

   public boolean ensureMapped() {
      if (this.sharedMemory != null) {
         if (System.nanoTime() < this.mappingExpiresAt) {
            return true;
         }

         this.closeMapping();
      }

      if (this.kernel32 != null) {
         this.fileMappingHandle = this.kernel32.OpenFileMappingW(983071, false, new WString("MumbleLink"));
         if (this.fileMappingHandle != null && Pointer.nativeValue(this.fileMappingHandle) != 0L) {
            this.sharedMemory = this.kernel32.MapViewOfFile(this.fileMappingHandle, 983071, 0, 0, new NativeLong(this.layout.size));
            if (this.sharedMemory != null && Pointer.nativeValue(this.sharedMemory) != 0L) {
               this.mappingExpiresAt = System.nanoTime() + 30000000000L;
               return true;
            } else {
               this.kernel32.CloseHandle(this.fileMappingHandle);
               this.fileMappingHandle = null;
               this.sharedMemory = null;
               return false;
            }
         } else {
            this.fileMappingHandle = null;
            return false;
         }
      } else {
         this.fileDescriptor = this.posixLibC.shm_open("/MumbleLink." + this.posixLibC.getuid(), 2, 0);
         if (this.fileDescriptor < 0) {
            return false;
         } else {
            this.sharedMemory = this.posixLibC.mmap(null, new NativeLong(this.layout.size), 3, 1, this.fileDescriptor, new NativeLong(0L));
            if (this.sharedMemory != null && Pointer.nativeValue(this.sharedMemory) != Pointer.nativeValue(Pointer.createConstant(-1))) {
               this.mappingExpiresAt = System.nanoTime() + 30000000000L;
               return true;
            } else {
               this.posixLibC.close(this.fileDescriptor);
               this.fileDescriptor = -1;
               this.sharedMemory = null;
               return false;
            }
         }
      }
   }

   public static MumbleLink create() {
      try {
         if (Platform.isWindows()) {
            return new MumbleLink(new MumbleMemory(2), (MumbleLink.Kernel32)Native.load("kernel32", MumbleLink.Kernel32.class), null);
         }

         if (Platform.isLinux() || Platform.isMac()) {
            return new MumbleLink(new MumbleMemory(4), null, (MumbleLink.PosixLibC)Native.load(Platform.C_LIBRARY_NAME, MumbleLink.PosixLibC.class));
         }
      } catch (LinkageError | RuntimeException e) {
         Logging.warnTagged("Mumble Link", "Native access is unavailable: %s", e.toString());
      }

      return null;
   }

   public void closeMapping() {
      if (this.sharedMemory != null) {
         if (this.kernel32 != null) {
            this.kernel32.UnmapViewOfFile(this.sharedMemory);
         } else {
            this.posixLibC.munmap(this.sharedMemory, new NativeLong(this.layout.size));
         }

         this.sharedMemory = null;
      }

      if (this.fileMappingHandle != null) {
         this.kernel32.CloseHandle(this.fileMappingHandle);
         this.fileMappingHandle = null;
      }

      if (this.fileDescriptor >= 0) {
         this.posixLibC.close(this.fileDescriptor);
         this.fileDescriptor = -1;
      }

      this.lastName = null;
      this.lastIdentity = null;
      this.lastContext = null;
      this.lastDescription = null;
   }

   public interface Kernel32 extends StdCallLibrary {
      Pointer MapViewOfFile(Pointer pointer, int n, int n2, int n3, NativeLong nativeLong);

      boolean UnmapViewOfFile(Pointer pointer);

      boolean CloseHandle(Pointer pointer);

      Pointer OpenFileMappingW(int n, boolean flag, WString wstring);
   }

   public interface PosixLibC extends Library {
      int getuid();

      Pointer mmap(Pointer pointer, NativeLong nativeLong, int n, int n2, int n3, NativeLong nativeLong2);

      int close(int n);

      int shm_open(String str, int n, int n2);

      int munmap(Pointer pointer, NativeLong nativeLong);
   }
}
