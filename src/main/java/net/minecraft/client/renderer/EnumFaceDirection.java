package net.minecraft.client.renderer;

import net.minecraft.util.EnumFacing;

public enum EnumFaceDirection {
   DOWN(
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
      )
   ),
   UP(
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
      )
   ),
   NORTH(
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
      )
   ),
   SOUTH(
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
      )
   ),
   WEST(
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.WEST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
      )
   ),
   EAST(
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.SOUTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.DOWN_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
      ),
      new EnumFaceDirection.VertexInformation(
         EnumFaceDirection.Constants.EAST_INDEX, EnumFaceDirection.Constants.UP_INDEX, EnumFaceDirection.Constants.NORTH_INDEX
      )
   );

   public EnumFaceDirection.VertexInformation[] vertexInfos;
   public static EnumFaceDirection[] facings = new EnumFaceDirection[6];

   public static EnumFaceDirection getFacing(EnumFacing facing) {
      return facings[facing.getIndex()];
   }

   EnumFaceDirection(EnumFaceDirection.VertexInformation... vertexInfosIn) {
      this.vertexInfos = vertexInfosIn;
   }

   public EnumFaceDirection.VertexInformation getVertexInformation(int index) {
      return this.vertexInfos[index];
   }

   static {
      facings[EnumFaceDirection.Constants.DOWN_INDEX] = DOWN;
      facings[EnumFaceDirection.Constants.UP_INDEX] = UP;
      facings[EnumFaceDirection.Constants.NORTH_INDEX] = NORTH;
      facings[EnumFaceDirection.Constants.SOUTH_INDEX] = SOUTH;
      facings[EnumFaceDirection.Constants.WEST_INDEX] = WEST;
      facings[EnumFaceDirection.Constants.EAST_INDEX] = EAST;
   }

   public static final class Constants {
      public static int SOUTH_INDEX = EnumFacing.SOUTH.getIndex();
      public static int UP_INDEX = EnumFacing.UP.getIndex();
      public static int EAST_INDEX = EnumFacing.EAST.getIndex();
      public static int NORTH_INDEX = EnumFacing.NORTH.getIndex();
      public static int DOWN_INDEX = EnumFacing.DOWN.getIndex();
      public static int WEST_INDEX = EnumFacing.WEST.getIndex();
   }

   public static class VertexInformation {
      public int xIndex;
      public int yIndex;
      public int zIndex;

      public VertexInformation(int xIndexIn, int yIndexIn, int zIndexIn) {
         this.xIndex = xIndexIn;
         this.yIndex = yIndexIn;
         this.zIndex = zIndexIn;
      }
   }
}
