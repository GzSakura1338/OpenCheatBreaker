package net.minecraft.block.material;

public class MapColor {
   public static MapColor[] mapColorArray = new MapColor[64];
   public static MapColor airColor = new MapColor(0, 0);
   public static MapColor grassColor = new MapColor(1, 8368696);
   public static MapColor sandColor = new MapColor(2, 16247203);
   public static MapColor clothColor = new MapColor(3, 13092807);
   public static MapColor tntColor = new MapColor(4, 16711680);
   public static MapColor iceColor = new MapColor(5, 10526975);
   public static MapColor ironColor = new MapColor(6, 10987431);
   public static MapColor foliageColor = new MapColor(7, 31744);
   public static MapColor snowColor = new MapColor(8, 16777215);
   public static MapColor clayColor = new MapColor(9, 10791096);
   public static MapColor dirtColor = new MapColor(10, 9923917);
   public static MapColor stoneColor = new MapColor(11, 7368816);
   public static MapColor waterColor = new MapColor(12, 4210943);
   public static MapColor woodColor = new MapColor(13, 9402184);
   public static MapColor quartzColor = new MapColor(14, 16776437);
   public static MapColor adobeColor = new MapColor(15, 14188339);
   public static MapColor magentaColor = new MapColor(16, 11685080);
   public static MapColor lightBlueColor = new MapColor(17, 6724056);
   public static MapColor yellowColor = new MapColor(18, 15066419);
   public static MapColor limeColor = new MapColor(19, 8375321);
   public static MapColor pinkColor = new MapColor(20, 15892389);
   public static MapColor grayColor = new MapColor(21, 5000268);
   public static MapColor silverColor = new MapColor(22, 10066329);
   public static MapColor cyanColor = new MapColor(23, 5013401);
   public static MapColor purpleColor = new MapColor(24, 8339378);
   public static MapColor blueColor = new MapColor(25, 3361970);
   public static MapColor brownColor = new MapColor(26, 6704179);
   public static MapColor greenColor = new MapColor(27, 6717235);
   public static MapColor redColor = new MapColor(28, 10040115);
   public static MapColor blackColor = new MapColor(29, 1644825);
   public static MapColor goldColor = new MapColor(30, 16445005);
   public static MapColor diamondColor = new MapColor(31, 6085589);
   public static MapColor lapisColor = new MapColor(32, 4882687);
   public static MapColor emeraldColor = new MapColor(33, 55610);
   public static MapColor obsidianColor = new MapColor(34, 8476209);
   public static MapColor netherrackColor = new MapColor(35, 7340544);
   public int colorIndex;
   public int colorValue;

   public MapColor(int index, int color) {
      if (index >= 0 && index <= 63) {
         this.colorIndex = index;
         this.colorValue = color;
         mapColorArray[index] = this;
      } else {
         throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
      }
   }

   public int getMapColor(int n) {
      short s = 220;
      if (n == 3) {
         s = 135;
      }

      if (n == 2) {
         s = 255;
      }

      if (n == 1) {
         s = 220;
      }

      if (n == 0) {
         s = 180;
      }

      int n2 = (this.colorValue >> 16 & 0xFF) * s / 255;
      int n3 = (this.colorValue >> 8 & 0xFF) * s / 255;
      int n4 = (this.colorValue & 0xFF) * s / 255;
      return 0xFF000000 | n2 << 16 | n3 << 8 | n4;
   }
}
