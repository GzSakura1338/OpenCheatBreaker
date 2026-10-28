package com.cheatbreaker.client.minimap;

import java.util.Observer;
import net.minecraft.world.chunk.Chunk;

public interface IChangeNotifier {
    void addObserver(Observer observer);

    void chunkChanged(Chunk chunk);
}
