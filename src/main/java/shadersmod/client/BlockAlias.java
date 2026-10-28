package shadersmod.client;

import java.util.ArrayList;
import java.util.HashSet;
import net.optifine.Config;
import net.optifine.MatchBlock;

public class BlockAlias {
    public int blockId;
    public MatchBlock[] matchBlocks;

    public BlockAlias(int n, MatchBlock[] matchBlocks) {
        this.blockId = n;
        this.matchBlocks = matchBlocks;
    }

    public int getBlockId() {
        return this.blockId;
    }

    public boolean matches(int n, int n2) {
        for (int i = 0; i < this.matchBlocks.length; i++) {
            MatchBlock matchBlock = this.matchBlocks[i];
            if (matchBlock.matches(n, n2)) {
                return true;
            }
        }

        return false;
    }

    public int[] getMatchBlockIds() {
        HashSet set = new HashSet();

        for (int i = 0; i < this.matchBlocks.length; i++) {
            MatchBlock matchBlock = this.matchBlocks[i];
            int blockId = matchBlock.getBlockId();
            set.add(blockId);
        }

        Integer[] toArray = (Integer[])set.toArray(new Integer[set.size()]);
        return Config.toPrimitive(toArray);
    }

    public MatchBlock[] getMatchBlocks(int n) {
        ArrayList list = new ArrayList();

        for (int i = 0; i < this.matchBlocks.length; i++) {
            MatchBlock matchBlock = this.matchBlocks[i];
            if (matchBlock.getBlockId() == n) {
                list.add(matchBlock);
            }
        }

        return (MatchBlock[])list.toArray(new MatchBlock[list.size()]);
    }

    @Override
    public String toString() {
        return "block." + this.blockId + "=" + Config.arrayToString(this.matchBlocks);
    }
}
