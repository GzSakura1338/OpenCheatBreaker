package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class CrashReportCategory {
   public List<CrashReportCategory.Entry> children = Lists.newArrayList();
   public StackTraceElement[] stackTrace = new StackTraceElement[0];
   public CrashReport crashReport;
   public String name;

   public CrashReportCategory(CrashReport report, String name) {
      this.crashReport = report;
      this.name = name;
   }

   public static String getCoordinateInfo(double x, double y, double z) {
      return String.format("%.2f,%.2f,%.2f - %s", x, y, z, getCoordinateInfo(new BlockPos(x, y, z)));
   }

   public static String getCoordinateInfo(BlockPos pos) {
      int n = pos.getX();
      int n2 = pos.getY();
      int n3 = pos.getZ();
      StringBuilder sb = new StringBuilder();

      try {
         sb.append(String.format("World: (%d,%d,%d)", n, n2, n3));
      } catch (Throwable e) {
         sb.append("(Error finding world loc)");
      }

      sb.append(", ");

      try {
         int n4 = n >> 4;
         int n5 = n3 >> 4;
         int n6 = n & 15;
         int n7 = n2 >> 4;
         int n8 = n3 & 15;
         int n9 = n4 << 4;
         int n10 = n5 << 4;
         int n11 = (n4 + 1 << 4) - 1;
         int n12 = (n5 + 1 << 4) - 1;
         sb.append(
            String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", n6, n7, n8, n4, n5, n9, n10, n11, n12)
         );
      } catch (Throwable e) {
         sb.append("(Error finding chunk loc)");
      }

      sb.append(", ");

      try {
         int n4 = n >> 9;
         int n5 = n3 >> 9;
         int n6 = n4 << 5;
         int n7 = n5 << 5;
         int n8 = (n4 + 1 << 5) - 1;
         int n9 = (n5 + 1 << 5) - 1;
         int n10 = n4 << 9;
         int n11 = n5 << 9;
         int n12 = (n4 + 1 << 9) - 1;
         int n13 = (n5 + 1 << 9) - 1;
         sb.append(
            String.format(
               "Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)",
               n4,
               n5,
               n6,
               n7,
               n8,
               n9,
               n10,
               n11,
               n12,
               n13
            )
         );
      } catch (Throwable e) {
         sb.append("(Error finding world loc)");
      }

      return sb.toString();
   }

   public void addCrashSectionCallable(String sectionName, Callable<String> callable) {
      try {
         this.addCrashSection(sectionName, callable.call());
      } catch (Throwable e) {
         this.addCrashSectionThrowable(sectionName, e);
      }
   }

   public void addCrashSection(String sectionName, Object value) {
      this.children.add(new CrashReportCategory.Entry(sectionName, value));
   }

   public void addCrashSectionThrowable(String sectionName, Throwable throwable) {
      this.addCrashSection(sectionName, throwable);
   }

   public int getPrunedStackTrace(int size) {
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      if (stackTrace.length <= 0) {
         return 0;
      }

      this.stackTrace = new StackTraceElement[stackTrace.length - 3 - size];
      System.arraycopy(stackTrace, 3 + size, this.stackTrace, 0, this.stackTrace.length);
      return this.stackTrace.length;
   }

   public boolean firstTwoElementsOfStackTraceMatch(StackTraceElement s1, StackTraceElement s2) {
      if (this.stackTrace.length != 0 && s1 != null) {
         StackTraceElement stackTraceElement = this.stackTrace[0];
         if (stackTraceElement.isNativeMethod() == s1.isNativeMethod()
            && stackTraceElement.getClassName().equals(s1.getClassName())
            && Objects.equals(stackTraceElement.getFileName(), s1.getFileName())
            && stackTraceElement.getMethodName().equals(s1.getMethodName())) {
            if (s2 != null != this.stackTrace.length > 1) {
               return false;
            }

            if (s2 != null && !this.stackTrace[1].equals(s2)) {
               return false;
            }

            this.stackTrace[0] = s1;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void trimStackTraceEntriesFromBottom(int amount) {
      StackTraceElement[] stackTraceElements = new StackTraceElement[this.stackTrace.length - amount];
      System.arraycopy(this.stackTrace, 0, stackTraceElements, 0, stackTraceElements.length);
      this.stackTrace = stackTraceElements;
   }

   public void appendToStringBuilder(StringBuilder builder) {
      builder.append("-- ").append(this.name).append(" --\n");
      builder.append("Details:");

      for (CrashReportCategory.Entry entry : this.children) {
         builder.append("\n\t");
         builder.append(entry.getKey());
         builder.append(": ");
         builder.append(entry.getValue());
      }

      if (this.stackTrace != null && this.stackTrace.length > 0) {
         builder.append("\nStacktrace:");

         for (StackTraceElement stackTraceElement : this.stackTrace) {
            builder.append("\n\tat ");
            builder.append(stackTraceElement.toString());
         }
      }
   }

   public StackTraceElement[] getStackTrace() {
      return this.stackTrace;
   }

   public static void addBlockInfo(CrashReportCategory category, final BlockPos pos, final Block blockIn, final int blockData) {
      final int idFromBlock = Block.getIdFromBlock(blockIn);
      category.addCrashSectionCallable("Block type", new Callable<String>() {
         public String call() throws Exception {
            try {
               return String.format("ID #%d (%s // %s)", idFromBlock, blockIn.getUnlocalizedName(), blockIn.getClass().getCanonicalName());
            } catch (Throwable e) {
               return "ID #" + idFromBlock;
            }
         }
      });
      category.addCrashSectionCallable("Block data value", new Callable<String>() {
         public String call() throws Exception {
            if (blockData < 0) {
               return "Unknown? (Got " + blockData + ")";
            }

            String replace = String.format("%4s", Integer.toBinaryString(blockData)).replace(" ", "0");
            return String.format("%1$d / 0x%1$X / 0b%2$s", blockData, replace);
         }
      });
      category.addCrashSectionCallable("Block location", new Callable<String>() {
         public String call() throws Exception {
            return CrashReportCategory.getCoordinateInfo(pos);
         }
      });
   }

   public static void addBlockInfo(CrashReportCategory category, final BlockPos pos, final IBlockState state) {
      category.addCrashSectionCallable("Block", new Callable<String>() {
         public String call() throws Exception {
            return state.toString();
         }
      });
      category.addCrashSectionCallable("Block location", new Callable<String>() {
         public String call() throws Exception {
            return CrashReportCategory.getCoordinateInfo(pos);
         }
      });
   }

   static class Entry {
      public String key;
      public String value;

      public Entry(String key, Object value) {
         this.key = key;
         if (value == null) {
            this.value = "~~NULL~~";
         } else if (value instanceof Throwable) {
            Throwable value2 = (Throwable)value;
            this.value = "~~ERROR~~ " + value2.getClass().getSimpleName() + ": " + value2.getMessage();
         } else {
            this.value = value.toString();
         }
      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }
   }
}
