package com.cheatbreaker.client.minimap;

import java.util.Observable;
import net.minecraft.world.chunk.Chunk;

public class ChangeNotifier extends Observable implements IChangeNotifier {
   @Override
   public void chunkChanged(Chunk chunk) {
      this.setChanged();
      this.notifyObservers(chunk);
   }
}
