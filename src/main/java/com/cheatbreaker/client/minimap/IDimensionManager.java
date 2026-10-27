package com.cheatbreaker.client.minimap;

import java.util.ArrayList;

public interface IDimensionManager {
   void populateDimensions();

   ArrayList<DimensionContainer> getDimensions();

   DimensionContainer getDimensionById(int n);

   void ensureDimension(int n);
}
