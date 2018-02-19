package edu.nyu.crypto.csci3033.miners;

import edu.nyu.crypto.csci3033.blockchain.NetworkStatistics;
import edu.nyu.crypto.csci3033.blockchain.Block;

public class SelfishMiner extends BaseMiner implements Miner {
    private Block currentHead;
    private Block officialHead;
    private double totalHashRate;
    private int advance = -1;
    private int oldAdvance;

    public SelfishMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);
    }

    @Override
    public Block currentlyMiningAt() {
        return currentHead;
    }

    @Override
    public Block currentHead() {
        return officialHead;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        // If the total hash rate is below 0.25 we usually do not want to attack
        if (getHashRate() / totalHashRate > 0.25) {
            if (isMinerMe) {
                advance = currentHead.getHeight() - officialHead.getHeight();
                if (block.getHeight() > currentHead.getHeight()) {
                    this.currentHead = block;
                }
            } else {
                if (block.getHeight() > officialHead.getHeight()) {
                    officialHead = block;
                    advance = currentHead.getHeight() - officialHead.getHeight();
                    if (advance < 0) {
                        currentHead = officialHead;
                    }
                    if (advance == 1 || advance == 0) {
                        officialHead = currentHead;
                    }
                }
            }
        }

        // Else just default to compliant miner behaviour
        else {
            if(isMinerMe) {
                if (block.getHeight() > currentHead.getHeight()) {
                    currentHead = block;
                    officialHead = block;
                }
            }

            else{
                 if (block.getHeight() > currentHead.getHeight()) {
                    currentHead = block;
                    officialHead = block;
                }
            }

        }
    }

    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.currentHead = genesis;
        this.officialHead = genesis;
        this.advance = -1;
        totalHashRate = 1; // Initialize low to not change strategy...
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
        totalHashRate = statistics.getTotalHashRate();
    }
}
