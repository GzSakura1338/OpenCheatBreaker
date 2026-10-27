package net.optifine.config;

public class GlVersion {
   public int major;
   public int minor;
   public int release;
   public String suffix;

   public GlVersion(int n, int n2) {
      this(n, n2, 0);
   }

   public GlVersion(int n, int n2, int n3) {
      this(n, n2, n3, (String)null);
   }

   public GlVersion(int n, int n2, int n3, String str) {
      this.major = n;
      this.minor = n2;
      this.release = n3;
      this.suffix = str;
   }

   public int getMajor() {
      return this.major;
   }

   public int getMinor() {
      return this.minor;
   }

   public int getRelease() {
      return this.release;
   }

   public int toInt() {
      return this.minor > 9
         ? this.major * 100 + this.minor
         : (this.release > 9 ? this.major * 100 + this.minor * 10 + 9 : this.major * 100 + this.minor * 10 + this.release);
   }

   @Override
   public String toString() {
      return this.suffix == null
         ? "" + this.major + "." + this.minor + "." + this.release
         : "" + this.major + "." + this.minor + "." + this.release + this.suffix;
   }
}
