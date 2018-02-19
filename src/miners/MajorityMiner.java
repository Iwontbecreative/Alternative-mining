package edu.nyu.crypto.csci3033.miners;

import com.sun.org.apache.bcel.internal.generic.NEW;
import edu.nyu.crypto.csci3033.blockchain.NetworkStatistics;
import edu.nyu.crypto.csci3033.blockchain.Block;

public class MajorityMiner extends BaseMiner implements Miner{

    private Block currentHead;
    private int totalHashRate;

    public MajorityMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);

    }

    @Override
    public Block currentlyMiningAt() {
        return currentHead;
    }

    @Override
    public Block currentHead() {
        return currentHead;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        // Either it is us and we are ok mining.
        // If it is not us but we lost majority, accept the block.
        if(isMinerMe || totalHashRate/2 > getHashRate()) {
                this.currentHead = block;
        }
    }


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.currentHead = genesis;
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
        this.totalHashRate = statistics.getTotalHashRate();
    }
}
