package net.minecraft.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.state.BlockWorldState;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class FactoryBlockPattern {
   public static Joiner COMMA_JOIN = Joiner.on(",");
   public List<String[]> depth = Lists.newArrayList();
   public Map<Character, Predicate<BlockWorldState>> symbolMap = Maps.newHashMap();
   public int aisleHeight;
   public int rowWidth;

   public FactoryBlockPattern() {
      this.symbolMap.put(' ', Predicates.alwaysTrue());
   }

   public FactoryBlockPattern aisle(String... aisle) {
      if (!ArrayUtils.isEmpty(aisle) && !StringUtils.isEmpty(aisle[0])) {
         if (this.depth.isEmpty()) {
            this.aisleHeight = aisle.length;
            this.rowWidth = aisle[0].length();
         }

         if (aisle.length != this.aisleHeight) {
            throw new IllegalArgumentException(
               "Expected aisle with height of " + this.aisleHeight + ", but was given one with a height of " + aisle.length + ")"
            );
         }

         for (String str : aisle) {
            if (str.length() != this.rowWidth) {
               throw new IllegalArgumentException(
                  "Not all rows in the given aisle are the correct width (expected " + this.rowWidth + ", found one with " + str.length() + ")"
               );
            }

            for (char c : str.toCharArray()) {
               if (!this.symbolMap.containsKey(c)) {
                  this.symbolMap.put(c, (Predicate<BlockWorldState>)null);
               }
            }
         }

         this.depth.add(aisle);
         return this;
      } else {
         throw new IllegalArgumentException("Empty pattern for aisle");
      }
   }

   public static FactoryBlockPattern start() {
      return new FactoryBlockPattern();
   }

   public FactoryBlockPattern where(char symbol, Predicate<BlockWorldState> blockMatcher) {
      this.symbolMap.put(symbol, blockMatcher);
      return this;
   }

   public BlockPattern build() {
      return new BlockPattern(this.makePredicateArray());
   }

   public Predicate<BlockWorldState>[][][] makePredicateArray() {
      this.checkMissingPredicates();
      Predicate[][][] newInstance = (Predicate[][][])Array.newInstance(Predicate.class, this.depth.size(), this.aisleHeight, this.rowWidth);

      for (int i = 0; i < this.depth.size(); i++) {
         for (int j = 0; j < this.aisleHeight; j++) {
            for (int k = 0; k < this.rowWidth; k++) {
               newInstance[i][j][k] = this.symbolMap.get(this.depth.get(i)[j].charAt(k));
            }
         }
      }

      return newInstance;
   }

   public void checkMissingPredicates() {
      ArrayList newArrayList = Lists.newArrayList();

      for (Entry entry : this.symbolMap.entrySet()) {
         if (entry.getValue() == null) {
            newArrayList.add(entry.getKey());
         }
      }

      if (!newArrayList.isEmpty()) {
         throw new IllegalStateException("Predicates for character(s) " + COMMA_JOIN.join(newArrayList) + " are missing");
      }
   }
}
