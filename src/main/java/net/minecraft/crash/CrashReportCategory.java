package net.minecraft.crash;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;

public class CrashReportCategory {
    public List elements = new ArrayList();
    public StackTraceElement[] stackTrace = new StackTraceElement[0];
    public CrashReport theCrashReport;
    public String title;

    public CrashReportCategory(CrashReport crashReport, String str) {
        this.theCrashReport = crashReport;
        this.title = str;
    }

    public static String createPositionString(double d, double d2, double d3) {
        return String.format(
            "%.2f,%.2f,%.2f - %s", d, d2, d3, getLocationInfo(MathHelper.floor_double(d), MathHelper.floor_double(d2), MathHelper.floor_double(d3))
        );
    }

    public static String getLocationInfo(int n, int n2, int n3) {
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

    public void addCrashSectionCallable(String str, Callable callable) {
        try {
            this.addCrashSection(str, callable.call());
        } catch (Throwable e) {
            this.addCrashSectionThrowable(str, e);
        }
    }

    public void addCrashSection(String str, Object obj) {
        this.elements.add(new CrashReportCategory.Entry(str, obj));
    }

    public void addCrashSectionThrowable(String str, Throwable throwable) {
        this.addCrashSection(str, throwable);
    }

    public int getPrunedStackTrace(int n) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length <= 0) {
            return 0;
        }

        this.stackTrace = new StackTraceElement[stackTrace.length - 3 - n];
        System.arraycopy(stackTrace, 3 + n, this.stackTrace, 0, this.stackTrace.length);
        return this.stackTrace.length;
    }

    public boolean firstTwoElementsOfStackTraceMatch(StackTraceElement stackTraceElement, StackTraceElement stackTraceElement2) {
        if (this.stackTrace.length != 0 && stackTraceElement != null) {
            StackTraceElement stackTraceElement3 = this.stackTrace[0];
            if (stackTraceElement3.isNativeMethod() == stackTraceElement.isNativeMethod()
                && stackTraceElement3.getClassName().equals(stackTraceElement.getClassName())
                && Objects.equals(stackTraceElement3.getFileName(), stackTraceElement.getFileName())
                && stackTraceElement3.getMethodName().equals(stackTraceElement.getMethodName())) {
                if (stackTraceElement2 != null != this.stackTrace.length > 1) {
                    return false;
                }

                if (stackTraceElement2 != null && !this.stackTrace[1].equals(stackTraceElement2)) {
                    return false;
                }

                this.stackTrace[0] = stackTraceElement;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void trimStackTraceEntriesFromBottom(int n) {
        StackTraceElement[] stackTraceElements = new StackTraceElement[this.stackTrace.length - n];
        System.arraycopy(this.stackTrace, 0, stackTraceElements, 0, stackTraceElements.length);
        this.stackTrace = stackTraceElements;
    }

    public void appendToStringBuilder(StringBuilder sb) {
        sb.append("-- ").append(this.title).append(" --\n");
        sb.append("Details:");

        for (CrashReportCategory.Entry entry : (Iterable<CrashReportCategory.Entry>)this.elements) {
            sb.append("\n\t");
            sb.append(entry.getText());
            sb.append(": ");
            sb.append(entry.getText2());
        }

        if (this.stackTrace != null && this.stackTrace.length > 0) {
            sb.append("\nStacktrace:");

            for (StackTraceElement stackTraceElement : this.stackTrace) {
                sb.append("\n\tat ");
                sb.append(stackTraceElement.toString());
            }
        }
    }

    public StackTraceElement[] getStackTrace() {
        return this.stackTrace;
    }

    public static void addBlockInfo(CrashReportCategory category, final int x, final int y, final int z, final Block blockIn, final int meta) {
        final int idFromBlock = Block.getIdFromBlock(blockIn);
        category.addCrashSectionCallable("Block type", new Callable() {
            public String call() {
                try {
                    return String.format("ID #%d (%s // %s)", idFromBlock, blockIn.getUnlocalizedName(), blockIn.getClass().getCanonicalName());
                } catch (Throwable e) {
                    return "ID #" + idFromBlock;
                }
            }
        });
        category.addCrashSectionCallable("Block data value", new Callable() {
            public String call() {
                if (meta < 0) {
                    return "Unknown? (Got " + meta + ")";
                }

                String replace = String.format("%4s", Integer.toBinaryString(meta)).replace(" ", "0");
                return String.format("%1$d / 0x%1$X / 0b%2$s", meta, replace);
            }
        });
        category.addCrashSectionCallable("Block location", new Callable() {
            public String call() {
                return CrashReportCategory.getLocationInfo(x, y, z);
            }
        });
    }

    public static class Entry {
        public String text;
        public String text2;

        public Entry(String str, Object obj) {
            this.text = str;
            if (obj == null) {
                this.text2 = "~~NULL~~";
            } else if (obj instanceof Throwable) {
                Throwable throwable = (Throwable)obj;
                this.text2 = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
            } else {
                this.text2 = obj.toString();
            }
        }

        public String getText() {
            return this.text;
        }

        public String getText2() {
            return this.text2;
        }
    }
}
