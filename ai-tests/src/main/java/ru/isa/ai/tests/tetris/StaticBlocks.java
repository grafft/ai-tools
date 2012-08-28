package ru.isa.ai.tests.tetris;

import java.util.*;

/**
 * Author: Aleksandr Panov
 * Date: 27.08.12
 * Time: 11:09
 */
public class StaticBlocks {
    private Map<Integer, List<Block>> blockMap = new TreeMap<>();
    private Set<Block> blockSet = new HashSet<>();

    public void addBlock(Block block) {
        if (blockMap.get(block.getyCoord()) == null) {
            blockMap.put(block.getyCoord(), new ArrayList<Block>());
        }
        blockMap.get(block.getyCoord()).add(block);
        blockSet.add(block);
    }

    public Set<Block> getBlockSet() {
        return blockSet;
    }

    public int collapseLines(int maxLength) {
        List<Integer> keys = new ArrayList<>();
        for (Integer key : blockMap.keySet()) {
            if (blockMap.get(key).size() == maxLength) {
                keys.add(key);
            }
        }
        for (Integer key : keys) {
            blockMap.remove(key);
            for (int j = key - 1; j >= 0; j--) {
                if (blockMap.get(j) != null) {
                    for (Block toBottom : blockMap.get(j)) {
                        toBottom.setyCoord(j + 1);
                    }
                    blockMap.put(j + 1, blockMap.get(j));
                    blockMap.remove(j);
                }
            }
        }
        blockSet.clear();
        for (Integer key : blockMap.keySet()) {
            blockSet.addAll(blockMap.get(key));
        }
        return keys.size();
    }
}
