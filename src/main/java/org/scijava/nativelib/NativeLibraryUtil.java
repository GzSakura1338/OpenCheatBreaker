package org.scijava.nativelib;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeLibraryUtil {
   public static NativeLibraryUtilArchitecture nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.UNKNOWN;
   public static final String DEFAULT_SEARCH_PATH = "natives/";
   public static final String DELIM = "/";
   public static String archStr = null;
   public static Logger logger = LoggerFactory.getLogger("org.scijava.nativelib.NativeLibraryUtil");

   public static boolean loadNativeLibraryFromClass(Class<?> clazz, String str) {
      try {
         return loadNativeLibrary(new DefaultJniExtractor(clazz), str);
      } catch (IOException e) {
         logger.debug("IOException creating DefaultJniExtractor", e);
         return false;
      }
   }

   public static String getPlatformLibraryPath(String str) {
      if (archStr == null) {
         archStr = getArchitecture().name().toLowerCase(Locale.ENGLISH);
      }

      String str2 = (!str.equals("") && !str.endsWith("/") ? str + "/" : str) + archStr + "/";
      logger.debug("platform specific path is " + str2);
      return str2;
   }

   public static String getPlatformLibraryName(String str) {
      String str2 = null;
      switch (getArchitecture()) {
         case AIX_32:
         case AIX_64:
         case LINUX_32:
         case LINUX_64:
         case LINUX_ARM:
         case LINUX_ARM64:
         case LINUX_RISCV32:
         case LINUX_RISCV64:
            str2 = "lib" + str + ".so";
            break;
         case WINDOWS_32:
         case WINDOWS_64:
         case WINDOWS_ARM64:
            str2 = str + ".dll";
            break;
         case OSX_32:
         case OSX_64:
         case OSX_ARM64:
            str2 = "lib" + str + ".dylib";
      }

      logger.debug("native library name " + str2);
      return str2;
   }

   public static boolean loadNativeLibrary(JniExtractor jniExtractor, String str, String... strs) {
      if (NativeLibraryUtilArchitecture.UNKNOWN == getArchitecture()) {
         logger.warn("No native library available for this platform.");
      } else {
         try {
            LinkedList linkedList = strs == null ? new LinkedList() : new LinkedList<>(Arrays.asList(strs));
            linkedList.add(0, "natives/");
            linkedList.add(1, "");
            linkedList.add(2, "META-INF/lib");

            for (String str2 : (Iterable<String>)linkedList) {
               File extractJni = jniExtractor.extractJni(getPlatformLibraryPath(str2), str);
               if (extractJni != null) {
                  System.load(extractJni.getAbsolutePath());
                  return true;
               }
            }
         } catch (UnsatisfiedLinkError e) {
            logger.debug("Problem with library", e);
         } catch (IOException e) {
            logger.debug("Problem with extracting the library", e);
         }
      }

      return false;
   }

   public static boolean loadVersionedNativeLibrary(Class<?> clazz, String str) {
      str = getVersionedLibraryName(clazz, str);
      return loadNativeLibraryFromClass(clazz, str);
   }

   public static String getVersionedLibraryName(Class<?> clazz, String str) {
      String implementationVersion = clazz.getPackage().getImplementationVersion();
      if (null != implementationVersion && implementationVersion.length() > 0) {
         str = str + "-" + implementationVersion;
      }

      return str;
   }

   public static NativeLibraryUtilProcessor getProcessor() {
      NativeLibraryUtilProcessor nativeLibraryUtilProcessor = NativeLibraryUtilProcessor.UNKNOWN;
      String toLowerCase = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH);
      if (toLowerCase.contains("arm")) {
         nativeLibraryUtilProcessor = NativeLibraryUtilProcessor.ARM;
      } else if (toLowerCase.contains("aarch64")) {
         nativeLibraryUtilProcessor = NativeLibraryUtilProcessor.AARCH_64;
      } else if (toLowerCase.contains("ppc")) {
         byte b = 32;
         if (toLowerCase.contains("64")) {
            b = 64;
         }

         nativeLibraryUtilProcessor = 32 == b ? NativeLibraryUtilProcessor.PPC : NativeLibraryUtilProcessor.PPC_64;
      } else if (toLowerCase.contains("86") || toLowerCase.contains("amd")) {
         byte b = 32;
         if (toLowerCase.contains("64")) {
            b = 64;
         }

         nativeLibraryUtilProcessor = 32 == b ? NativeLibraryUtilProcessor.INTEL_32 : NativeLibraryUtilProcessor.INTEL_64;
      } else if (toLowerCase.contains("riscv")) {
         byte b = 32;
         if (toLowerCase.contains("64")) {
            b = 64;
         }

         nativeLibraryUtilProcessor = 32 == b ? NativeLibraryUtilProcessor.RISCV_32 : NativeLibraryUtilProcessor.RISCV_64;
      }

      logger.debug("processor is " + nativeLibraryUtilProcessor + " os.arch is " + System.getProperty("os.arch").toLowerCase(Locale.ENGLISH));
      return nativeLibraryUtilProcessor;
   }

   public static NativeLibraryUtilArchitecture getArchitecture() {
      if (NativeLibraryUtilArchitecture.UNKNOWN == nativeLibraryUtilArchitecture) {
         NativeLibraryUtilProcessor processor = getProcessor();
         if (NativeLibraryUtilProcessor.UNKNOWN != processor) {
            String toLowerCase = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
            if (!toLowerCase.contains("nix") && !toLowerCase.contains("nux")) {
               if (toLowerCase.contains("aix")) {
                  if (NativeLibraryUtilProcessor.PPC == processor) {
                     nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.AIX_32;
                  } else if (NativeLibraryUtilProcessor.PPC_64 == processor) {
                     nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.AIX_64;
                  }
               } else if (toLowerCase.contains("win")) {
                  if (NativeLibraryUtilProcessor.INTEL_32 == processor) {
                     nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.WINDOWS_32;
                  } else if (NativeLibraryUtilProcessor.INTEL_64 == processor) {
                     nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.WINDOWS_64;
                  } else if (NativeLibraryUtilProcessor.AARCH_64 == processor) {
                     nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.WINDOWS_ARM64;
                  }
               } else if (toLowerCase.contains("mac")) {
                  if (NativeLibraryUtilProcessor.INTEL_32 == processor) {
                     nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.OSX_32;
                  } else if (NativeLibraryUtilProcessor.INTEL_64 == processor) {
                     nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.OSX_64;
                  } else if (NativeLibraryUtilProcessor.AARCH_64 == processor) {
                     nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.OSX_ARM64;
                  } else if (NativeLibraryUtilProcessor.PPC == processor) {
                     nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.OSX_PPC;
                  }
               }
            } else if (NativeLibraryUtilProcessor.INTEL_32 == processor) {
               nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.LINUX_32;
            } else if (NativeLibraryUtilProcessor.INTEL_64 == processor) {
               nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.LINUX_64;
            } else if (NativeLibraryUtilProcessor.ARM == processor) {
               nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.LINUX_ARM;
            } else if (NativeLibraryUtilProcessor.AARCH_64 == processor) {
               nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.LINUX_ARM64;
            } else if (NativeLibraryUtilProcessor.RISCV_32 == processor) {
               nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.LINUX_RISCV32;
            } else if (NativeLibraryUtilProcessor.RISCV_64 == processor) {
               nativeLibraryUtilArchitecture = NativeLibraryUtilArchitecture.LINUX_RISCV64;
            }
         }
      }

      logger.debug("architecture is " + nativeLibraryUtilArchitecture + " os.name is " + System.getProperty("os.name").toLowerCase(Locale.ENGLISH));
      return nativeLibraryUtilArchitecture;
   }
}
