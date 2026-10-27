package com.cheatbreaker.client.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;

public class ImageTransferable implements Transferable {
   public List list;

   @Override
   public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
      return DataFlavor.javaFileListFlavor.equals(dataFlavor);
   }

   public ImageTransferable(List list) {
      this.list = list;
   }

   @Override
   public Object getTransferData(DataFlavor dataFlavor) {
      return this.list;
   }

   @Override
   public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[]{DataFlavor.javaFileListFlavor};
   }
}
