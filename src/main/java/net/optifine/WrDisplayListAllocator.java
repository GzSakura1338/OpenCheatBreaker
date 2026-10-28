package net.optifine;

import java.util.ArrayList;
import java.util.List;

public class WrDisplayListAllocator {
    public List<WrDisplayListBlock> listBlocks = new ArrayList<>();
    public WrDisplayListBlock currentBlock = null;
    public int blockIndex = -1;

    public int allocateDisplayLists(int n) {
        if (n > 0 && n <= 16384) {
            if (this.currentBlock == null || !this.currentBlock.canAllocate(n)) {
                if (this.blockIndex + 1 < this.listBlocks.size()) {
                    this.blockIndex++;
                    this.currentBlock = this.listBlocks.get(this.blockIndex);
                } else {
                    this.currentBlock = new WrDisplayListBlock();
                    this.blockIndex = this.listBlocks.size();
                    this.listBlocks.add(this.currentBlock);
                }

                if (!this.currentBlock.canAllocate(n)) {
                    throw new IllegalArgumentException("Can not allocate: " + n);
                }
            }

            return this.currentBlock.allocate(n);
        } else {
            throw new IllegalArgumentException("Invalid display list length: " + n);
        }
    }

    public void resetAllocatedLists() {
        this.currentBlock = null;
        this.blockIndex = -1;

        for (int i = 0; i < this.listBlocks.size(); i++) {
            WrDisplayListBlock wrDisplayListBlock = this.listBlocks.get(i);
            wrDisplayListBlock.reset();
        }
    }

    public void deleteDisplayLists() {
        for (int i = 0; i < this.listBlocks.size(); i++) {
            WrDisplayListBlock wrDisplayListBlock = this.listBlocks.get(i);
            wrDisplayListBlock.deleteDisplayLists();
        }

        this.listBlocks.clear();
        this.currentBlock = null;
        this.blockIndex = -1;
    }
}
